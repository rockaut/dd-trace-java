[ {
  "type" : "test_session_end",
  "version" : 1,
  "content" : {
    "test_session_id" : ${content_test_session_id},
    "service" : "test-maven-service",
    "name" : "maven.test_session",
    "resource" : "Maven Smoke Tests Project",
    "start" : ${content_start},
    "duration" : ${content_duration},
    "error" : 0,
    "metrics" : {
      "process_id" : ${content_metrics_process_id},
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0
    },
    "meta" : {
      "_dd.p.tid" : ${content_meta__dd_p_tid},
      "os.architecture" : ${content_meta_os_architecture},
      "test.status" : "pass",
      "ci.workspace_path" : ${content_meta_ci_workspace_path},
      "language" : "jvm",
      "runtime.name" : ${content_meta_runtime_name},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "library_version" : ${content_meta_library_version},
      "span.kind" : "test_session_end",
      "runtime.version" : ${content_meta_runtime_version},
      "runtime-id" : ${content_meta_runtime_id},
      "test.type" : "test",
      "env" : "integration-test",
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "component" : "maven",
      "test.code_coverage.enabled" : "true",
      "test.toolchain" : ${content_meta_test_toolchain},
      "test.command" : "mvn -B test",
      "test.framework_version" : "4.13.2",
      "test.framework" : "junit4"
    }
  }
}, {
  "type" : "test_module_end",
  "version" : 1,
  "content" : {
    "test_session_id" : ${content_test_session_id},
    "test_module_id" : ${content_test_module_id},
    "service" : "test-maven-service",
    "name" : "maven.test_module",
    "resource" : "Maven Smoke Tests Project maven-surefire-plugin default-test",
    "start" : ${content_start_2},
    "duration" : ${content_duration_2},
    "error" : 0,
    "metrics" : {
    },
    "meta" : {
      "_dd.p.tid" : ${content_meta__dd_p_tid_2},
      "test.type" : "test",
      "os.architecture" : ${content_meta_os_architecture},
      "test.module" : "Maven Smoke Tests Project maven-surefire-plugin default-test",
      "test.status" : "pass",
      "ci.workspace_path" : ${content_meta_ci_workspace_path},
      "runtime.name" : ${content_meta_runtime_name},
      "env" : "integration-test",
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "library_version" : ${content_meta_library_version},
      "component" : "maven",
      "test.code_coverage.enabled" : "true",
      "span.kind" : "test_module_end",
      "test.execution" : "maven-surefire-plugin:test:default-test",
      "runtime.version" : ${content_meta_runtime_version},
      "test.command" : "mvn -B test",
      "test.framework_version" : "4.13.2",
      "test.framework" : "junit4"
    }
  }
}, {
  "type" : "test_suite_end",
  "version" : 1,
  "content" : {
    "test_session_id" : ${content_test_session_id},
    "test_module_id" : ${content_test_module_id},
    "test_suite_id" : ${content_test_suite_id},
    "service" : "test-maven-service",
    "name" : "junit.test_suite",
    "resource" : "datadog.smoke.TestSucceedPropertyAssertion",
    "start" : ${content_start_3},
    "duration" : ${content_duration_3},
    "error" : 0,
    "metrics" : {
      "process_id" : ${content_metrics_process_id_2},
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0
    },
    "meta" : {
      "_dd.p.tid" : ${content_meta__dd_p_tid_3},
      "os.architecture" : ${content_meta_os_architecture},
      "test.source.file" : "src/test/java/datadog/smoke/TestSucceedPropertyAssertion.java",
      "test.module" : "Maven Smoke Tests Project maven-surefire-plugin default-test",
      "test.status" : "pass",
      "ci.workspace_path" : ${content_meta_ci_workspace_path},
      "language" : "jvm",
      "runtime.name" : ${content_meta_runtime_name},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "library_version" : ${content_meta_library_version},
      "span.kind" : "test_suite_end",
      "test.suite" : "datadog.smoke.TestSucceedPropertyAssertion",
      "runtime.version" : ${content_meta_runtime_version},
      "runtime-id" : ${content_meta_runtime_id_2},
      "test.type" : "test",
      "env" : "integration-test",
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "component" : "junit",
      "test.framework_version" : "4.13.2",
      "test.framework" : "junit4"
    }
  }
}, {
  "type" : "test",
  "version" : 2,
  "content" : {
    "trace_id" : ${content_trace_id},
    "span_id" : ${content_span_id},
    "parent_id" : ${content_parent_id},
    "test_session_id" : ${content_test_session_id},
    "test_module_id" : ${content_test_module_id},
    "test_suite_id" : ${content_test_suite_id},
    "service" : "test-maven-service",
    "name" : "junit.test",
    "resource" : "datadog.smoke.TestSucceedPropertyAssertion.test_succeed",
    "start" : ${content_start_4},
    "duration" : ${content_duration_4},
    "error" : 0,
    "metrics" : {
      "process_id" : ${content_metrics_process_id_2},
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0,
      "test.source.end" : 12,
      "test.source.start" : 9
    },
    "meta" : {
      "_dd.p.tid" : ${content_meta__dd_p_tid_4},
      "os.architecture" : ${content_meta_os_architecture},
      "test.source.file" : "src/test/java/datadog/smoke/TestSucceedPropertyAssertion.java",
      "test.source.method" : "test_succeed()V",
      "test.module" : "Maven Smoke Tests Project maven-surefire-plugin default-test",
      "test.status" : "pass",
      "ci.workspace_path" : ${content_meta_ci_workspace_path},
      "language" : "jvm",
      "runtime.name" : ${content_meta_runtime_name},
      "os.platform" : ${content_meta_os_platform},
      "os.version" : ${content_meta_os_version},
      "library_version" : ${content_meta_library_version},
      "test.name" : "test_succeed",
      "span.kind" : "test",
      "test.suite" : "datadog.smoke.TestSucceedPropertyAssertion",
      "runtime.version" : ${content_meta_runtime_version},
      "runtime-id" : ${content_meta_runtime_id_2},
      "test.type" : "test",
      "env" : "integration-test",
      "runtime.vendor" : ${content_meta_runtime_vendor},
      "component" : "junit",
      "test.framework_version" : "4.13.2",
      "test.framework" : "junit4"
    }
  }
} ]
