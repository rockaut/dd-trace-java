package springdata

import org.elasticsearch.common.io.FileSystemUtils
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.springframework.beans.factory.DisposableBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories(basePackages = "springdata")
@ComponentScan(basePackages = "springdata")
class Config {

  @Bean
  NodeBuilder nodeBuilder() {
    return new NodeBuilder()
  }

  @Bean
  NodeBean elasticsearchNode() {

    def tmpDir = File.createTempFile("test-es-working-dir-", "")
    tmpDir.delete()
    tmpDir.mkdir()
    tmpDir.deleteOnExit()

    System.addShutdownHook {
      if (tmpDir != null) {
        FileSystemUtils.deleteSubDirectories(tmpDir.toPath())
        tmpDir.delete()
      }
    }

    final Settings.Builder elasticsearchSettings =
      Settings.settingsBuilder()
      .put("http.enabled", "false")
      .put("path.data", tmpDir.toString())
      .put("path.home", tmpDir.toString())

    println "ES work dir: $tmpDir"

    return new NodeBean(nodeBuilder().local(true).settings(elasticsearchSettings.build()).node())
  }

  @Bean
  ElasticsearchOperations elasticsearchTemplate(NodeBean bean) {
    return new ElasticsearchTemplate(bean.node.client())
  }

  static final class NodeBean implements DisposableBean {
    Node node

    NodeBean(Node node) {
      this.node = node
    }

    Node getNode() {
      return node
    }

    @Override
    void destroy() throws Exception {
      node.close()
    }
  }
}
