package fcu.selab.progedu.conn;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

  private String username = null;

  private String password = null;

  private String language = null;

  private String privateToken = null;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public AfterEnter() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    doPost(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    username = request.getParameter("username");
    password = request.getParameter("password");
    language = request.getParameter("language");

    privateToken = checkEnter(username, password);

    sendRedirect(response, privateToken);
  }

  /**
   * Check if username and password is correct
   * 
   * @param username
   *          User name
   * @param password
   *          User password
   * @return private token
   */
  public String checkEnter(String username, String password) {
    String privateToken = null;
    GitlabSession gitSession = conn.getSession(username, password);
    if (gitSession != null) {
      privateToken = gitSession.getPrivateToken();
      System.out.println("token : " + gitSession.getPrivateToken());
    } else {
      privateToken = "Unauthorized";
      System.out.println("token : " + "Unauthorized");
    }
    return privateToken;
  }

  /**
   * After check, send redirect
   * 
   * @param response
   *          HttpServletResponse
   * @param privateToken
   *          Check return private token
   */
  public void sendRedirect(HttpServletResponse response, String privateToken) {
    try {
      if (privateToken.equals(gitData.getGitlabApiToken())) {
        System.out.println("Enter Teacher");
        response.sendRedirect("dashboard.jsp");
      } else if (privateToken.equals("Unauthorized")) {
        System.out.println("Enter Unauthorized");
        response.sendRedirect("index.jsp");
      } else {
        System.out.println("Enter Student");
        response.sendRedirect("studentDashboard.jsp");
      }
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
