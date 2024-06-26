import datadog.trace.agent.test.base.HttpClientTest
import datadog.trace.agent.test.naming.TestingGenericHttpNamingConventions
import datadog.trace.instrumentation.apachehttpasyncclient.ApacheHttpAsyncClientDecorator
import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.message.BasicHeader
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Timeout

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Timeout(5)
class ApacheHttpAsyncClientCallbackTest extends HttpClientTest implements TestingGenericHttpNamingConventions.ClientV0 {

  @Shared
  RequestConfig requestConfig = RequestConfig.custom()
  .setConnectTimeout(CONNECT_TIMEOUT_MS)
  .setSocketTimeout(READ_TIMEOUT_MS)
  .build()

  @AutoCleanup
  @Shared
  def client = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig).build()

  def setupSpec() {
    client.start()
  }

  @Override
  int doRequest(String method, URI uri, Map<String, String> headers, String body, Closure callback) {
    def request = new HttpUriRequest(method, uri)
    headers.entrySet().each {
      request.addHeader(new BasicHeader(it.key, it.value))
    }

    def responseFuture = new CompletableFuture<>()

    client.execute(request, new FutureCallback<HttpResponse>() {

        @Override
        void completed(HttpResponse result) {
          try {
            callback?.call()
            responseFuture.complete(result.statusLine.statusCode)
          } catch (Exception e) {
            failed(e)
          }
        }

        @Override
        void failed(Exception ex) {
          responseFuture.completeExceptionally(ex)
        }

        @Override
        void cancelled() {
          responseFuture.cancel(true)
        }
      })

    return responseFuture.get(10, TimeUnit.SECONDS)
  }

  @Override
  CharSequence component() {
    return ApacheHttpAsyncClientDecorator.DECORATE.component()
  }

  @Override
  Integer statusOnRedirectError() {
    return 302
  }

  @Override
  boolean testRemoteConnection() {
    false // otherwise SocketTimeoutException for https requests
  }
}
