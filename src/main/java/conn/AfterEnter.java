package conn;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gitlab.api.models.GitlabSession;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.exception.LoadConfigFailureException;

/**
 * Servlet implementation class AfterEnter
 */
public class AfterEnter extends HttpServlet {
  private static final long serialVersionUID = 1L;

  GitlabConfig gitData = GitlabConfig.getInstance();

  private Conn conn = Conn.getInstance();
  
  private String gitlabUrl = null;

  private String username = null;

  private String password = null;

  private String language = null;

  private EnterCheck check;

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
    try {
      gitlabUrl = gitData.getGitlabHostUrl();
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    username = request.getParameter("username");
    password = request.getParameter("password");
    language = request.getParameter("language");
    check = new EnterCheck();
    try {
      if (check.httpPost(username, password) != null) {
        String json = check.httpPost(username, password);
        if (json.equals("Unauthorized")) {
          response.sendRedirect("index.jsp");
        } else {
          String accessToken = check.analysisJson(json);
          if (accessToken != null) {
            HttpSession session = request.getSession();
            if (username.equals("root")) {
              response.sendRedirect("dashboard.jsp");
              session.setAttribute("username", username);
              session.setAttribute("password", password);
              session.setAttribute("private_token", null);
              session.setAttribute("language", language);
              session.setAttribute("page", "dashboard.jsp");
            } else {
              GitlabSession gitSession = conn.getSession(username, password);
              String privateToken = gitSession.getPrivateToken();
              session.setAttribute("username", username);
              session.setAttribute("password", password);
              session.setAttribute("private_token", privateToken);
              session.setAttribute("language", language);
              session.setAttribute("page", "teacherHW");
              response.sendRedirect("studentDashboard.jsp");
            }
          } else {
            response.sendRedirect("index.jsp");
          }
        }
      }
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
