package datadog.trace.instrumentation.jetty9;

import datadog.trace.bootstrap.instrumentation.api.URIRawDataAdapter;
import org.eclipse.jetty.server.Request;

public final class RequestURIDataAdapter extends URIRawDataAdapter {

  private final Request request;

  public RequestURIDataAdapter(Request request) {
    this.request = request;
  }

  @Override
  public String scheme() {
    return request.getScheme();
  }

  @Override
  public String host() {
    return request.getServerName();
  }

  @Override
  public int port() {
    return request.getServerPort();
  }

  @Override
  protected String innerRawPath() {
    return request.getRequestURI();
  }

  @Override
  public String fragment() {
    return null;
  }

  @Override
  protected String innerRawQuery() {
    return request.getQueryString();
  }
}
