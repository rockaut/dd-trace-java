package datadog.trace.plugin.csi.impl.ext;

import static datadog.trace.plugin.csi.impl.CallSiteFactory.typeResolver;
import static datadog.trace.plugin.csi.util.CallSiteConstants.OPCODES_FQDN;
import static datadog.trace.plugin.csi.util.JavaParserUtils.*;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import datadog.trace.plugin.csi.AdviceGenerator.CallSiteResult;
import datadog.trace.plugin.csi.Extension;
import datadog.trace.plugin.csi.PluginApplication.Configuration;
import datadog.trace.plugin.csi.TypeResolver;
import datadog.trace.plugin.csi.impl.CallSiteSpecification;
import datadog.trace.plugin.csi.impl.CallSiteSpecification.AdviceSpecification;
import datadog.trace.plugin.csi.util.CallSiteUtils;
import datadog.trace.plugin.csi.util.MethodType;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class IastExtension implements Extension {

  private static final String IAST_CALL_SITES_CLASS = "IastCallSites";
  static final String IAST_CALL_SITES_FQCN = "datadog.trace.api.iast." + IAST_CALL_SITES_CLASS;
  private static final String HAS_TELEMETRY_INTERFACE = IAST_CALL_SITES_CLASS + ".HasTelemetry";
  private static final String IAST_METRIC_COLLECTOR_CLASS = "IastMetricCollector";
  private static final String IAST_METRIC_COLLECTOR_FQCN =
      "datadog.trace.api.iast.telemetry." + IAST_METRIC_COLLECTOR_CLASS;

  private static final String IAST_METRIC_COLLECTOR_INTERNAL_NAME =
      IAST_METRIC_COLLECTOR_FQCN.replaceAll("\\.", "/");

  private static final String VERBOSITY_CLASS = "Verbosity";
  private static final String VERBOSITY_FQCN =
      "datadog.trace.api.iast.telemetry." + VERBOSITY_CLASS;

  private static final String IAST_METRIC_CLASS = "IastMetric";
  private static final String IAST_METRIC_FQCN =
      "datadog.trace.api.iast.telemetry." + IAST_METRIC_CLASS;

  private static final String IAST_METRIC_INTERNAL_NAME = IAST_METRIC_FQCN.replaceAll("\\.", "/");

  @Override
  public boolean appliesTo(@Nonnull final CallSiteSpecification spec) {
    return IAST_CALL_SITES_FQCN.equals(spec.getSpi().getClassName());
  }

  @Override
  public void apply(
      @Nonnull final Configuration configuration, @Nonnull final CallSiteResult result)
      throws Exception {
    addTelemetry(configuration, result);
  }

  private void addTelemetry(final Configuration configuration, final CallSiteResult result)
      throws Exception {
    final TypeResolver resolver = getTypeResolver(configuration);

    // class with the addAdvices methods
    final CompilationUnit providerJavaFile = parseJavaFile(resolver, result.getFile());
    final ClassOrInterfaceDeclaration provider = getPrimaryType(providerJavaFile);
    if (implementsInterface(provider, HAS_TELEMETRY_INTERFACE)) {
      // already processed
      return;
    }

    // find all the advices in the provider
    final Map<AdviceSpecification, LambdaExpr> advices = findAdvices(result, provider);

    // parse current call site class and fetch all metadata regarding telemetry
    final CompilationUnit originalCallSiteFile =
        findOriginalCallSite(configuration, resolver, result);
    final ClassOrInterfaceDeclaration originalCallSite = getPrimaryType(originalCallSiteFile);
    final AdviceMetadata globalMetadata = AdviceMetadata.findAdviceMetadata(originalCallSite);

    // add telemetry to each of the advices
    boolean hasTelemetry = false;
    for (final MethodDeclaration callSiteMethod : originalCallSite.getMethods()) {
      if (isCallSite(callSiteMethod)) {
        final AdviceMetadata methodMetadata = AdviceMetadata.findAdviceMetadata(callSiteMethod);
        final AdviceMetadata metaData = methodMetadata != null ? methodMetadata : globalMetadata;
        // if the call site class or the method has been annotated then apply the extension
        if (metaData != null) {
          final List<LambdaExpr> adviceLambdas = filterAdviceLambdas(advices, callSiteMethod);
          for (final LambdaExpr advice : adviceLambdas) {
            addTelemetryToAdvice(advice, metaData);
            hasTelemetry = true;
          }
        }
      }
    }

    if (hasTelemetry) {
      // add telemetry support to the provider class
      addTelemetryInterface(providerJavaFile);

      // save the result
      providerJavaFile.getStorage().get().save();
    }
  }

  private void addTelemetryInterface(final CompilationUnit javaClass) {
    javaClass.addImport(IAST_METRIC_COLLECTOR_FQCN);
    javaClass.addImport(IAST_METRIC_FQCN);
    javaClass.addImport(VERBOSITY_FQCN);
    final ClassOrInterfaceDeclaration mainType = getPrimaryType(javaClass);
    mainType.addImplementedType(HAS_TELEMETRY_INTERFACE);
    final FieldDeclaration verbosityField =
        mainType.addField(VERBOSITY_CLASS, "verbosity", Modifier.Keyword.PRIVATE);
    verbosityField
        .getVariable(0)
        .setInitializer(
            new FieldAccessExpr().setScope(new NameExpr(VERBOSITY_CLASS)).setName("OFF"));
    final MethodDeclaration enableTelemetry =
        mainType
            .addMethod("setVerbosity", Modifier.Keyword.PUBLIC)
            .addParameter(VERBOSITY_CLASS, "verbosity")
            .addAnnotation(Override.class);
    final BlockStmt enableTelemetryBody = new BlockStmt();
    enableTelemetryBody.addStatement(
        new AssignExpr()
            .setTarget(accessLocalField("verbosity"))
            .setValue(new NameExpr("verbosity")));
    enableTelemetry.setBody(enableTelemetryBody);
  }

  private CompilationUnit findOriginalCallSite(
      final Configuration configuration, final TypeResolver resolver, final CallSiteResult result)
      throws FileNotFoundException {
    final String originalClass = result.getSpecification().getClazz().getClassName();
    final String separator = File.separator.equals("\\") ? "\\\\" : File.separator;
    final String javaFile = originalClass.replaceAll("\\.", separator);
    return parseSourceFile(configuration, resolver, javaFile);
  }

  private void addTelemetryToAdvice(final LambdaExpr adviceLambda, final AdviceMetadata metaData) {
    final BlockStmt lambdaBody = adviceLambda.getBody().asBlockStmt();
    final String metric = getMetricName(metaData);
    final String instrumentedMetric = "INSTRUMENTED_" + metric;
    final IfStmt instrumentedStatement =
        new IfStmt()
            .setCondition(isEnabledCondition(instrumentedMetric))
            .setThenStmt(
                new BlockStmt().addStatement(addTelemetryCollectorMethod(instrumentedMetric)));
    lambdaBody.addStatement(0, instrumentedStatement);
    final String executedMetric = "EXECUTED_" + metric;
    final IfStmt executedStatement =
        new IfStmt()
            .setCondition(isEnabledCondition(executedMetric))
            .setThenStmt(addTelemetryCollectorByteCode(executedMetric));
    lambdaBody.addStatement(1, executedStatement);
  }

  private static Expression isEnabledCondition(final String metric) {
    return new MethodCallExpr()
        .setScope(new FieldAccessExpr().setScope(new NameExpr(IAST_METRIC_CLASS)).setName(metric))
        .setName("isEnabled")
        .addArgument(accessLocalField("verbosity"));
  }

  private static MethodCallExpr addTelemetryCollectorMethod(final String metric) {
    return new MethodCallExpr()
        .setScope(new NameExpr(IAST_METRIC_COLLECTOR_CLASS))
        .setName("add")
        .addArgument(
            new FieldAccessExpr().setScope(new NameExpr(IAST_METRIC_CLASS)).setName(metric))
        .addArgument(intLiteral(1));
  }

  private static BlockStmt addTelemetryCollectorByteCode(final String metric) {
    final BlockStmt stmt = new BlockStmt();
    // this code generates the java source code needed to provide the bytecode for the statement
    // IastTelemetryCollector.add($"{metric}, 1);
    stmt.addStatement(
        new MethodCallExpr()
            .setScope(new NameExpr("handler"))
            .setName("field")
            .addArgument(
                new FieldAccessExpr().setScope(new NameExpr(OPCODES_FQDN)).setName("GETSTATIC"))
            .addArgument(new StringLiteralExpr(IAST_METRIC_INTERNAL_NAME))
            .addArgument(new StringLiteralExpr(metric))
            .addArgument(new StringLiteralExpr("L" + IAST_METRIC_INTERNAL_NAME + ";")));
    stmt.addStatement(
        new MethodCallExpr()
            .setScope(new NameExpr("handler"))
            .setName("instruction")
            .addArgument(
                new FieldAccessExpr().setScope(new NameExpr(OPCODES_FQDN)).setName("LCONST_1")));
    stmt.addStatement(
        new MethodCallExpr()
            .setScope(new NameExpr("handler"))
            .setName("method")
            .addArgument(
                new FieldAccessExpr().setScope(new NameExpr(OPCODES_FQDN)).setName("INVOKESTATIC"))
            .addArgument(new StringLiteralExpr(IAST_METRIC_COLLECTOR_INTERNAL_NAME))
            .addArgument(new StringLiteralExpr("add"))
            .addArgument(new StringLiteralExpr("(L" + IAST_METRIC_INTERNAL_NAME + ";J)V"))
            .addArgument(new BooleanLiteralExpr(false)));
    return stmt;
  }

  private static String getMetricName(final AdviceMetadata metaData) {
    final StringBuilder metric = new StringBuilder(metaData.getKind());
    if (metaData.getTag() != null) {
      // Uses the name of the field to compose the name of the metric
      final Expression tag = metaData.getTag();
      metric.append("_").append(tag.asFieldAccessExpr().getName());
    }
    return metric.toString();
  }

  /** Find all advice lambdas in the generated call site provider */
  private static Map<AdviceSpecification, LambdaExpr> findAdvices(
      final CallSiteResult result, final ClassOrInterfaceDeclaration callSiteProvider) {
    final MethodDeclaration acceptMethod =
        callSiteProvider.getMethodsBySignature("accept", "Container").get(0);
    final BlockStmt body = acceptMethod.getBody().get().asBlockStmt();
    final List<MethodCallExpr> addAdviceMethods =
        body.getStatements().stream()
            .filter(IastExtension::isAddAdviceMethodCall)
            .map(it -> it.asExpressionStmt().getExpression().asMethodCallExpr())
            .collect(Collectors.toList());
    return result.getSpecification().getAdvices().stream()
        .collect(
            Collectors.toMap(
                Function.identity(), spec -> findAdviceLambda(spec, addAdviceMethods)));
  }

  /**
   * Return only the lambdas that match the specified call site method by looking into the
   * signatures
   */
  private static List<LambdaExpr> filterAdviceLambdas(
      final Map<AdviceSpecification, LambdaExpr> advices, final MethodDeclaration callSiteMethod) {

    final List<LambdaExpr> result = new ArrayList<>();
    for (final Map.Entry<AdviceSpecification, LambdaExpr> entry : advices.entrySet()) {
      final AdviceSpecification spec = entry.getKey();
      final MethodType methodType = spec.getAdvice();
      if (!methodType.getMethodName().equals(callSiteMethod.getNameAsString())) {
        continue;
      }
      // java parser has issues with inner classes and descriptors, remove the dollar to do the
      // matching)
      final String descriptor = methodType.getMethodType().getDescriptor().replaceAll("\\$", "/");
      if (!descriptor.equals(callSiteMethod.toDescriptor())) {
        continue;
      }
      result.add(entry.getValue());
    }
    return result;
  }

  private static boolean isAddAdviceMethodCall(final Statement statement) {
    if (!statement.isExpressionStmt()) {
      return false;
    }
    final Expression expression = statement.asExpressionStmt().getExpression();
    if (!expression.isMethodCallExpr()) {
      return false;
    }
    final MethodCallExpr methodCall = expression.asMethodCallExpr();
    if (!methodCall.getScope().get().toString().equals("container")) {
      return false;
    }
    return methodCall.getNameAsString().equals("addAdvice");
  }

  private static LambdaExpr findAdviceLambda(
      final AdviceSpecification spec, final List<MethodCallExpr> addAdvices) {
    final MethodType pointcut = spec.getPointcut();
    for (final MethodCallExpr add : addAdvices) {
      final NodeList<Expression> arguments = add.getArguments();
      final String owner = arguments.get(0).asStringLiteralExpr().asString();
      if (!owner.equals(pointcut.getOwner().getInternalName())) {
        continue;
      }
      final String method = arguments.get(1).asStringLiteralExpr().asString();
      if (!method.equals(pointcut.getMethodName())) {
        continue;
      }
      final String description = arguments.get(2).asStringLiteralExpr().asString();
      if (!description.equals(pointcut.getMethodType().getDescriptor())) {
        continue;
      }
      return arguments.get(3).asLambdaExpr();
    }
    throw new IllegalArgumentException("Cannot find lambda expression for pointcut " + pointcut);
  }

  private static boolean isCallSite(final MethodDeclaration method) {
    return Stream.of("Before", "Around", "After")
        .map(method::getAnnotationByName)
        .anyMatch(Optional::isPresent);
  }

  private static TypeResolver getTypeResolver(final Configuration configuration) {
    final URL[] urls =
        configuration.getClassPath().stream().map(CallSiteUtils::toURL).toArray(URL[]::new);
    final ClassLoader loader = new URLClassLoader(urls);
    return typeResolver(loader, Thread.currentThread().getContextClassLoader());
  }

  private static CompilationUnit parseSourceFile(
      final Configuration configuration, final TypeResolver resolver, final String file)
      throws FileNotFoundException {
    final Path callSiteSource = configuration.getSrcFolder().resolve(file + ".java");
    if (!Files.exists(callSiteSource)) {
      throw new RuntimeException("Error finding original call site at " + callSiteSource);
    }
    return parseJavaFile(resolver, callSiteSource.toFile());
  }

  private static CompilationUnit parseJavaFile(final TypeResolver resolver, final File file)
      throws FileNotFoundException {
    final JavaSymbolSolver solver = new JavaSymbolSolver(resolver);
    final JavaParser parser = new JavaParser(new ParserConfiguration().setSymbolResolver(solver));
    return parser.parse(file).getResult().get();
  }

  private static Expression getAnnotationExpression(final AnnotationExpr expr) {
    if (expr.isMarkerAnnotationExpr()) {
      return null;
    } else if (expr.isSingleMemberAnnotationExpr()) {
      return expr.asSingleMemberAnnotationExpr().getMemberValue();
    } else {
      final List<MemberValuePair> pairs = expr.asNormalAnnotationExpr().getPairs();
      return pairs.stream()
          .filter(it -> it.getName().toString().equals("value"))
          .map(MemberValuePair::getValue)
          .findFirst()
          .orElse(null);
    }
  }

  private static class AdviceMetadata {
    private final String kind;
    private final Expression tag;

    private AdviceMetadata(final String kind, final Expression tag) {
      this.kind = kind;
      this.tag = tag;
    }

    private static AdviceMetadata findAdviceMetadata(final BodyDeclaration<?> target) {
      return target.getAnnotations().stream()
          .filter(AdviceMetadata::isAdviceAnnotation)
          .map(
              annotation -> {
                final Expression tag = getAnnotationExpression(annotation);
                final String typeName = annotation.getName().getId();
                return new AdviceMetadata(typeName.toUpperCase(), tag);
              })
          .findFirst()
          .orElse(null);
    }

    private static boolean isAdviceAnnotation(final AnnotationExpr expr) {
      final String identifier = expr.getName().getIdentifier();
      return identifier.equals("Source")
          || identifier.equals("Propagation")
          || identifier.equals("Sink");
    }

    public String getKind() {
      return kind;
    }

    public Expression getTag() {
      return tag;
    }
  }
}
