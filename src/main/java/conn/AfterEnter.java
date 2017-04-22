package conn;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gitlab.api.models.GitlabSession;

import data.GitlabData;

/**
 * Servlet implementation class AfterEnter
 */
public class AfterEnter extends HttpServlet {
  private static final long serialVersionUID = 1L;

  GitlabData gitData = new GitlabData();

  private Conn conn = Conn.getInstance();

  /**
   * @see HttpServlet#HttpServlet()
   */
  public AfterEnter() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    doPost(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub

    String gitlabURL = gitData.getHostUrl();
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String language = request.getParameter("language");
    EnterCheck check = new EnterCheck();
    if (check.httpPost(username, password) != null) {
      String json = check.httpPost(username, password);
      if (json.equals("Unauthorized")) {
        response.sendRedirect("index.jsp");
      } else {
        String access_token = check.analysisJSON(json);
        if (access_token != null) {
          HttpSession session = request.getSession();
          if (username.equals("root")) {
            response.sendRedirect("dashboard.jsp");
            session.setAttribute("username", username);
            session.setAttribute("password", password);
            session.setAttribute("private_token", null);
            session.setAttribute("language", language);
            session.setAttribute("page", "dashboard.jsp");
          } else {
            GitlabSession s = conn.getSession(gitlabURL, username, password);
            String private_token = s.getPrivateToken();
            session.setAttribute("username", username);
            session.setAttribute("password", password);
            session.setAttribute("private_token", private_token);
            session.setAttribute("language", language);
            session.setAttribute("page", "teacherHW");
            response.sendRedirect("studentDashboard.jsp");
          }
        } else {
          response.sendRedirect("index.jsp");
        }
      }
    }
  }

}
