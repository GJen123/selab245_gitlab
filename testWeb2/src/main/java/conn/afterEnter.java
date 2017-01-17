package conn;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gitlab.api.models.GitlabSession;

/**
 * Servlet implementation class afterEnter
 */
public class afterEnter extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public afterEnter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String gitlabURL = "http://140.134.26.71:20080";
		conn conn = new conn();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		enterCheck check = new enterCheck();
		if(check.httpPost(username, password)!=null){
			String json = check.httpPost(username, password);
			if(json.equals("Unauthorized")){
				response.sendRedirect("memberEnter.jsp");
			}else{
				System.out.println("json : "+json);
				String access_token = check.analysisJSON(json);
				if(access_token!=null){
					HttpSession session = request.getSession();
					if (username.equals("root")){
						response.sendRedirect("teacherDashboard.jsp");
						session.setAttribute("username", username);
						session.setAttribute("password", password);
						session.setAttribute("private_token", null);
					}
					else {
						GitlabSession s = conn.getSession(gitlabURL, username, password);
						String private_token = s.getPrivateToken();
						session.setAttribute("username", username);
						session.setAttribute("password", password);
						session.setAttribute("private_token", private_token);
						response.sendRedirect("studentEnter.jsp");
					}
				}else{
					System.out.println("abc");
					response.sendRedirect("memberEnter.jsp");
				}
			}
		}
	}

}
