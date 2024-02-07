package datadog.trace.instrumentation.pekkohttp.iast;

import static datadog.trace.agent.tooling.bytebuddy.matcher.NameMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.google.auto.service.AutoService;
import datadog.trace.agent.tooling.Instrumenter;
import datadog.trace.agent.tooling.InstrumenterGroup;
import datadog.trace.api.iast.IastContext;
import datadog.trace.api.iast.InstrumentationBridge;
import datadog.trace.api.iast.Propagation;
import datadog.trace.api.iast.Source;
import datadog.trace.api.iast.SourceTypes;
import datadog.trace.api.iast.propagation.PropagationModule;
import net.bytebuddy.asm.Advice;
import org.apache.pekko.http.scaladsl.model.Uri;
import scala.Tuple2;
import scala.collection.Iterator;

/** Propagates taint from a {@link Uri} to query strings fetched from it. */
@AutoService(Instrumenter.class)
public class UriInstrumentation extends InstrumenterGroup.Iast
    implements Instrumenter.ForSingleType {
  public UriInstrumentation() {
    super("pekko-http");
  }

  @Override
  public String instrumentedType() {
    return "org.apache.pekko.http.scaladsl.model.Uri";
  }

  @Override
  public void methodAdvice(MethodTransformer transformer) {
    transformer.applyAdvice(
        isMethod()
            .and(not(isStatic()))
            .and(named("queryString"))
            .and(returns(named("scala.Option")))
            .and(takesArguments(1))
            .and(takesArgument(0, named("java.nio.charset.Charset"))),
        UriInstrumentation.class.getName() + "$TaintQueryStringAdvice");
    transformer.applyAdvice(
        isMethod()
            .and(not(isStatic()))
            .and(named("rawQueryString"))
            .and(returns(named("scala.Option")))
            .and(takesArguments(0)),
        UriInstrumentation.class.getName() + "$TaintQueryStringAdvice");
    transformer.applyAdvice(
        isMethod()
            .and(not(isStatic()))
            .and(named("query"))
            .and(returns(named("org.apache.pekko.http.scaladsl.model.Uri$Query")))
            .and(takesArguments(2))
            .and(takesArgument(0, named("java.nio.charset.Charset")))
            .and(takesArgument(1, named("org.apache.pekko.http.scaladsl.model.Uri$ParsingMode"))),
        UriInstrumentation.class.getName() + "$TaintQueryAdvice");
  }

  static class TaintQueryStringAdvice {
    @Advice.OnMethodExit(suppress = Throwable.class)
    @Propagation
    static void after(@Advice.This Uri uri, @Advice.Return scala.Option<String> ret) {
      PropagationModule mod = InstrumentationBridge.PROPAGATION;
      if (mod == null || ret.isEmpty()) {
        return;
      }
      mod.taintIfTainted(ret.get(), uri);
    }
  }

  public static class TaintQueryAdvice {
    // bind uri to a variable of type Object so that this advice can also
    // be used from FromDataInstrumentaton
    @Advice.OnMethodExit(suppress = Throwable.class)
    @Source(SourceTypes.REQUEST_PARAMETER_VALUE)
    static void after(@Advice.This /*Uri*/ Object uri, @Advice.Return Uri.Query ret) {
      PropagationModule prop = InstrumentationBridge.PROPAGATION;
      if (prop == null || ret.isEmpty()) {
        return;
      }

      final IastContext ctx = IastContext.Provider.get();
      if (!prop.isTainted(ctx, uri)) {
        return;
      }

      Iterator<Tuple2<String, String>> iterator = ret.iterator();
      while (iterator.hasNext()) {
        Tuple2<String, String> pair = iterator.next();
        final String name = pair._1(), value = pair._2();
        prop.taint(ctx, name, SourceTypes.REQUEST_PARAMETER_NAME, name);
        prop.taint(ctx, value, SourceTypes.REQUEST_PARAMETER_VALUE, name);
      }
    }
  }
}
