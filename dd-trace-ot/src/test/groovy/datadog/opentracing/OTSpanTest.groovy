package datadog.opentracing

import datadog.trace.api.interceptor.MutableSpan
import datadog.trace.bootstrap.instrumentation.api.ResourceNamePriorities
import datadog.trace.test.util.DDSpecification
import io.opentracing.Scope
import io.opentracing.Span
import spock.lang.Shared

class OTSpanTest extends DDSpecification {
  @Shared
  DDTracer tracer = DDTracer.builder().build()

  def cleanup() {
    tracer?.close()
  }

  def "test resource name assignment through MutableSpan casting"() {
    given:
    OTSpan testSpan = tracer.buildSpan("parent").withResourceName("test-resource").start() as OTSpan
    OTScopeManager.OTScope testScope = tracer.activateSpan(testSpan) as OTScopeManager.OTScope

    when:
    Span active = tracer.activeSpan()
    Span child = tracer.buildSpan("child").asChildOf(active).start()
    Scope scope = tracer.activateSpan(child)

    MutableSpan localRootSpan = ((MutableSpan) child).getLocalRootSpan()
    localRootSpan.setResourceName("correct-resource")

    then:
    testSpan.getResourceName() == "correct-resource"

    when:
    testSpan.delegate.setResourceName("should-be-ignored", ResourceNamePriorities.HTTP_FRAMEWORK_ROUTE)

    then:
    testSpan.getResourceName() == "correct-resource"

    cleanup:
    scope.close()
    child.finish()
    testScope.close()
    testSpan.finish()
  }
}
