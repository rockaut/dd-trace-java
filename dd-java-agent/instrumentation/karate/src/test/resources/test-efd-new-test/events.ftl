[ {
  "type" : "span",
  "version" : 1,
  "content" : {
    "trace_id" : ${content_trace_id},
    "span_id" : ${content_span_id},
    "parent_id" : ${content_parent_id},
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "name" : "karate.step",
    "resource" : "* print 'first'",
    "start" : ${content_start},
    "duration" : ${content_duration},
    "error" : 0,
    "metrics" : {
      "step.endLine" : 6,
      "step.startLine" : 6
    },
    "meta" : {
      "library_version" : ${content_meta_library_version},
      "component" : "karate",
      "step.name" : "* print 'first'",
      "env" : "none"
    }
  }
}, {
  "type" : "span",
  "version" : 1,
  "content" : {
    "trace_id" : ${content_trace_id_2},
    "span_id" : ${content_span_id_2},
    "parent_id" : ${content_parent_id_2},
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "name" : "karate.step",
    "resource" : "* print 'first'",
    "start" : ${content_start_2},
    "duration" : ${content_duration_2},
    "error" : 0,
    "metrics" : {
      "step.endLine" : 6,
      "step.startLine" : 6
    },
    "meta" : {
      "library_version" : ${content_meta_library_version},
      "component" : "karate",
      "step.name" : "* print 'first'",
      "env" : "none"
    }
  }
}, {
  "type" : "span",
  "version" : 1,
  "content" : {
    "trace_id" : ${content_trace_id_3},
    "span_id" : ${content_span_id_3},
    "parent_id" : ${content_parent_id_3},
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "name" : "karate.step",
    "resource" : "* print 'first'",
    "start" : ${content_start_3},
    "duration" : ${content_duration_3},
    "error" : 0,
    "metrics" : {
      "step.endLine" : 6,
      "step.startLine" : 6
    },
    "meta" : {
      "library_version" : ${content_meta_library_version},
      "component" : "karate",
      "step.name" : "* print 'first'",
      "env" : "none"
    }
  }
}, {
  "type" : "test_suite_end",
  "version" : 1,
  "content" : {
    "test_session_id" : ${content_test_session_id},
    "test_module_id" : ${content_test_module_id},
    "test_suite_id" : ${content_test_suite_id},
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "name" : "karate.test_suite",
    "resource" : "[org/example/test_succeed_one_case] test succeed",
    "start" : ${content_start_4},
    "duration" : ${content_duration_4},
    "error" : 0,
    "metrics" : { },
    "meta" : {
      "test.type" : "test",
      "test.module" : "karate",
      "test.status" : "pass",
      "test.traits" : "{\"category\":[\"foo\"]}",
      "test_session.name" : "session-name",
      "env" : "none",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "library_version" : ${content_meta_library_version},
      "component" : "karate",
      "span.kind" : "test_suite_end",
      "test.suite" : "[org/example/test_succeed_one_case] test succeed",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.framework" : "karate"
    }
  }
}, {
  "type" : "test",
  "version" : 2,
  "content" : {
    "trace_id" : ${content_trace_id},
    "span_id" : ${content_parent_id},
    "parent_id" : ${content_parent_id_4},
    "test_session_id" : ${content_test_session_id},
    "test_module_id" : ${content_test_module_id},
    "test_suite_id" : ${content_test_suite_id},
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "name" : "karate.test",
    "resource" : "[org/example/test_succeed_one_case] test succeed.first scenario",
    "start" : ${content_start_5},
    "duration" : ${content_duration_5},
    "error" : 0,
    "metrics" : {
      "process_id" : ${content_metrics_process_id},
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0
    },
    "meta" : {
      "_dd.tracer_host" : ${content_meta__dd_tracer_host},
      "test.module" : "karate",
      "test.status" : "pass",
      "language" : "jvm",
      "test.is_new" : "true",
      "library_version" : ${content_meta_library_version},
      "test.name" : "first scenario",
      "span.kind" : "test",
      "test.suite" : "[org/example/test_succeed_one_case] test succeed",
      "runtime-id" : ${content_meta_runtime_id},
      "test.type" : "test",
      "test.traits" : "{\"category\":[\"bar\",\"foo\"]}",
      "test_session.name" : "session-name",
      "env" : "none",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "component" : "karate",
      "_dd.profiling.ctx" : "test",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.framework" : "karate"
    }
  }
}, {
  "type" : "test",
  "version" : 2,
  "content" : {
    "trace_id" : ${content_trace_id_2},
    "span_id" : ${content_parent_id_2},
    "parent_id" : ${content_parent_id_4},
    "test_session_id" : ${content_test_session_id},
    "test_module_id" : ${content_test_module_id},
    "test_suite_id" : ${content_test_suite_id},
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "name" : "karate.test",
    "resource" : "[org/example/test_succeed_one_case] test succeed.first scenario",
    "start" : ${content_start_6},
    "duration" : ${content_duration_6},
    "error" : 0,
    "metrics" : {
      "process_id" : ${content_metrics_process_id},
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0
    },
    "meta" : {
      "_dd.tracer_host" : ${content_meta__dd_tracer_host},
      "test.module" : "karate",
      "test.status" : "pass",
      "language" : "jvm",
      "test.is_new" : "true",
      "library_version" : ${content_meta_library_version},
      "test.name" : "first scenario",
      "span.kind" : "test",
      "test.suite" : "[org/example/test_succeed_one_case] test succeed",
      "runtime-id" : ${content_meta_runtime_id},
      "test.type" : "test",
      "test.traits" : "{\"category\":[\"bar\",\"foo\"]}",
      "test_session.name" : "session-name",
      "env" : "none",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "test.is_retry" : "true",
      "component" : "karate",
      "_dd.profiling.ctx" : "test",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.framework" : "karate"
    }
  }
}, {
  "type" : "test",
  "version" : 2,
  "content" : {
    "trace_id" : ${content_trace_id_3},
    "span_id" : ${content_parent_id_3},
    "parent_id" : ${content_parent_id_4},
    "test_session_id" : ${content_test_session_id},
    "test_module_id" : ${content_test_module_id},
    "test_suite_id" : ${content_test_suite_id},
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "name" : "karate.test",
    "resource" : "[org/example/test_succeed_one_case] test succeed.first scenario",
    "start" : ${content_start_7},
    "duration" : ${content_duration_7},
    "error" : 0,
    "metrics" : {
      "process_id" : ${content_metrics_process_id},
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0
    },
    "meta" : {
      "_dd.tracer_host" : ${content_meta__dd_tracer_host},
      "test.module" : "karate",
      "test.status" : "pass",
      "language" : "jvm",
      "test.is_new" : "true",
      "library_version" : ${content_meta_library_version},
      "test.name" : "first scenario",
      "span.kind" : "test",
      "test.suite" : "[org/example/test_succeed_one_case] test succeed",
      "runtime-id" : ${content_meta_runtime_id},
      "test.type" : "test",
      "test.traits" : "{\"category\":[\"bar\",\"foo\"]}",
      "test_session.name" : "session-name",
      "env" : "none",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "test.is_retry" : "true",
      "component" : "karate",
      "_dd.profiling.ctx" : "test",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.framework" : "karate"
    }
  }
}, {
  "type" : "test_session_end",
  "version" : 1,
  "content" : {
    "test_session_id" : ${content_test_session_id},
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "name" : "karate.test_session",
    "resource" : "karate",
    "start" : ${content_start_8},
    "duration" : ${content_duration_8},
    "error" : 0,
    "metrics" : {
      "process_id" : ${content_metrics_process_id},
      "_dd.profiling.enabled" : 0,
      "_dd.trace_span_attribute_schema" : 0
    },
    "meta" : {
      "test.type" : "test",
      "_dd.tracer_host" : ${content_meta__dd_tracer_host},
      "test.status" : "pass",
      "test.early_flake.enabled" : "true",
      "test_session.name" : "session-name",
      "language" : "jvm",
      "env" : "none",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "library_version" : ${content_meta_library_version},
      "component" : "karate",
      "_dd.profiling.ctx" : "test",
      "span.kind" : "test_session_end",
      "runtime-id" : ${content_meta_runtime_id},
      "test.command" : "karate",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.framework" : "karate"
    }
  }
}, {
  "type" : "test_module_end",
  "version" : 1,
  "content" : {
    "test_session_id" : ${content_test_session_id},
    "test_module_id" : ${content_test_module_id},
    "service" : "worker.org.gradle.process.internal.worker.gradleworkermain",
    "name" : "karate.test_module",
    "resource" : "karate",
    "start" : ${content_start_9},
    "duration" : ${content_duration_9},
    "error" : 0,
    "metrics" : { },
    "meta" : {
      "test.type" : "test",
      "test.module" : "karate",
      "test.status" : "pass",
      "test.early_flake.enabled" : "true",
      "test_session.name" : "session-name",
      "env" : "none",
      "dummy_ci_tag" : "dummy_ci_tag_value",
      "library_version" : ${content_meta_library_version},
      "component" : "karate",
      "span.kind" : "test_module_end",
      "test.framework_version" : ${content_meta_test_framework_version},
      "test.framework" : "karate"
    }
  }
} ]