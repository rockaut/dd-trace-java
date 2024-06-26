import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import static datadog.trace.agent.test.base.HttpServerTest.controller
import static datadog.trace.agent.test.base.HttpServerTest.ServerEndpoint.ERROR

class ErrorServlet extends HttpServlet {
  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    controller(ERROR) {
      // can not use sendError since it will force the contentType to text/html
      resp.setStatus(ERROR.status)
      resp.writer.print(ERROR.body)
    }
  }
}
