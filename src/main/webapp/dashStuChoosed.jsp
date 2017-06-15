<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ page import="fcu.selab.progedu.conn.Conn,fcu.selab.progedu.conn.HttpConnect, fcu.selab.progedu.conn.StudentConn" %>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi, fcu.selab.progedu.conn.Language" %>
<%@ page import="fcu.selab.progedu.config.GitlabConfig,fcu.selab.progedu.config.CourseConfig" %>
<%@ page import="fcu.selab.progedu.config.GitlabConfig" %>
<%@ page import="fcu.selab.progedu.config.JenkinsConfig" %>
<%@ page import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.db.ProjectDbManager" %>
<%@ page import="fcu.selab.progedu.data.User, fcu.selab.progedu.data.Project" %>   
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.*" %>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "teacherHW");
	String pages = "teacherHW.jsp";
%>

<%@ include file="language.jsp" %>

<%
	String studentId = request.getParameter("studentId");
	if(null == studentId){
	  
	}
%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<style type="text/css">
		#inline li {
		    display: inline;
		}
		#circle{
            background: red;
            border-radius: 200px;
            color: white;
            height: 200px;
            font-weight: bold;
            width: 200px;
        }
        body, html, .row, #navHeight{
			height:100%;
		}
	</style>
	
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>
	
	<%
		Conn conn = Conn.getInstance();
	
		UserDbManager db = UserDbManager.getInstance();
		ProjectDbManager Pdb = ProjectDbManager.getInstance();
		
		// db的所有users
		List<User> users = db.listAllUsers();
		
		// 每個學生gitlab的projects
		List<GitlabProject> gitProjects = new ArrayList<GitlabProject>();
		
		// db的所有projects
		List<Project> dbProjects = Pdb.listAllProjects();
		
		// gitlab jenkins course的Data
		GitlabConfig gitData = GitlabConfig.getInstance();
		JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
		CourseConfig courseData = CourseConfig.getInstance();
		
		JenkinsApi jenkins = JenkinsApi.getInstance();
	%>
      <div class="row">
        <nav class="col-sm-3 col-md-2 hidden-xs-down bg-faded sidebar" id="navHeight">
          <ul class="nav nav-pills flex-column">
            <li class="nav-item"><font size="4"><a class="nav-link" href="dashboard.jsp">Overview <span class="sr-only">(current)</span></a></font></li>
            <li class="nav-item">
                <font size="4"><a href="javascript:;" data-toggle="collapse" data-target="#student" class="nav-link"><i class="fa fa-fw fa-arrows-v"></i> Student▼ <i class="fa fa-fw fa-caret-down"></i></a></font>
                <ul id="student" class="collapse" style="list-style: none;">
                    <%
		            	for(User user : users){
		            	  String userName = user.getUserName();
		            	  String href = "\"dashStuChoosed.jsp?studentId=" + user.getGitLabId() + "\"";
		            	  %>
		            	  	<li class="nav-item"><font size="4"><a class="nav-link" href=<%=href %>><%=userName %></a></font></li>
		            	  <%
		            	}
		            %>
                </ul>
            </li>
          </ul>
        </nav>

        <main class="col-md-9 col-xs-11 p-l-2 p-t-2">
        	<%
        		User choosedUser = new User();
        		for(User user : users){
        		  if(studentId.equals(String.valueOf(user.getGitLabId()))){
        		    choosedUser = user;
        		    break;
        		  }
        		}
        		
        		String private_token = choosedUser.getPrivateToken();
            	StudentConn sConn = new StudentConn(private_token); 	
            	List<GitlabProject> projects;
            	int pro_total_commits = 0;
        		
        	%>
        	
        	<h1><%=choosedUser.getName() %></h1>
        	
        	
        	<div class="container">
        		<br><br>
        		<table class="table">
					<thead>
						<tr class="table-info">
							<th width="15%">作業</th>
							<%
								projects = sConn.getProject();
								Collections.reverse(projects);
								for(GitlabProject project : projects){
									if(courseData.getCourseName().equals(project.getName().substring(0,3))){
										%>
										<th><%=project.getName() %></th>
										<%
									}
									
								}
							%>
						</tr>
					</thead>
					<tbody>
						<tr>
							<th width="15%">Commits次數</th>
							<%
								for(GitlabProject project : projects){
									if(courseData.getCourseName().equals(project.getName().substring(0,3))){
										pro_total_commits = sConn.getAllCommitsCounts(project.getId());
										%>
											<th><%=pro_total_commits %></th>
										<%
									}
									
								}
							%>
							
						</tr>
					</tbody>
				</table>
				
				<br><br>
        		
        		<!-- Nav tabs -->
				<ul class="nav nav-tabs" role="tablist">
				  <li class="nav-item">
				    <a class="nav-link active" data-toggle="tab" href="#chart1" role="tab">Chart1</a>
				  </li>
				  <li class="nav-item">
				    <a class="nav-link" data-toggle="tab" href="#chart2" role="tab">Chart2</a>
				  </li>
				  <li class="nav-item">
				    <a class="nav-link" data-toggle="tab" href="#chart3" role="tab">Chart3</a>
				  </li>
				  <li class="nav-item">
				    <a class="nav-link" data-toggle="tab" href="#chart4" role="tab">Chart4</a>
				  </li>
				</ul>
				
				<!-- Tab panes -->
				<div class="tab-content">
				  <div class="tab-pane active" id="chart1" role="tabpanel">
				  	<h3>Chart1</h3>
				  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
				  </div>
				  <div class="tab-pane" id="chart2" role="tabpanel">
				  	<h3>Chart2</h3>
				  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
				  </div>
				  <div class="tab-pane" id="chart3" role="tabpanel">
				  	<h3>Chart3</h3>
				  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
				  </div>
				  <div class="tab-pane" id="chart4" role="tabpanel">
				  	<h3>Chart4</h3>
				  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
				  </div>
				</div>
				<!-- Nav tabs end -->
				
				
        	</div>
        </main>
      </div>

</body>
</html>