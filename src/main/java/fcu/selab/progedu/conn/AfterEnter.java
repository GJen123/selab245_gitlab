package fcu.selab.progedu.conn;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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

  private String username = null;

  private String password = null;

  private String language = null;

  private String privateToken = null;

  boolean isEnter = true;

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
    final HttpSession session = request.getSession();
    username = request.getParameter("username");
    password = request.getParameter("password");
    language = request.getParameter("language");

    privateToken = checkEnter(username, password);

    sendRedirect(response, session, privateToken, username);
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
      isEnter = true;
      privateToken = gitSession.getPrivateToken();
      System.out.println("token : " + gitSession.getPrivateToken());
    } else {
      isEnter = false;
      privateToken = "Unauthorized";
      System.out.println("token : Unauthorized");
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
  public void sendRedirect(HttpServletResponse response, HttpSession session,
      String privateToken, String username) {
    try {
      if (isEnter == true) {
        if (privateToken.equals(gitData.getGitlabApiToken())) {
          session.setAttribute("page", "dashboard.jsp");
          session.setAttribute("username", username);
          response.sendRedirect("dashboard.jsp");
        } else {
          session.setAttribute("page", "studentDashboard.jsp");
          session.setAttribute("private_token", privateToken);
          Cookie cookie = new Cookie("private_token", privateToken);
          response.addCookie(cookie);
          session.setAttribute("username", username);
          response.sendRedirect("studentDashboard.jsp");
        }
      } else {
        session.setAttribute("enterError", "Enter Error");
        response.sendRedirect("index.jsp");
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
