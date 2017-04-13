<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ page import="conn.Conn,data.GitlabData,data.JenkinsData,data.CourseData" %>
<%@ page import="db.UserDBManager, db.ProjectDBManager" %>
<%@ page import="data.User, data.Project" %>   
<%@ page import="java.util.List" %> 
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

<%@ include file="language.jsp"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet"
		href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
	<!-- jQuery library -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
	<!-- Latest compiled JavaScript -->
	<script
		src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
	
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>
	
	<%
		Conn conn = Conn.getInstance();
	
		UserDBManager db = UserDBManager.getInstance();
		ProjectDBManager Pdb = ProjectDBManager.getInstance();
		
		// db的所有users
		List<User> users = db.listAllUsers();
		
		// 每個學生gitlab的projects
		List<GitlabProject> gitProjects = new ArrayList<GitlabProject>();
		
		// db的所有projects
		List<Project> dbProjects = Pdb.listAllProjects();
		
		// gitlab jenkins course的Data
		GitlabData gitData = new GitlabData();
		JenkinsData jenkinsData = new JenkinsData();
		CourseData courseData = new CourseData();
	%>
	
	<div class="container">
		<table class="table table-striped">
			<thead>
				<tr>
					<th><fmt:message key="teacherHW_th_studentId"/></th>
					<th><fmt:message key="teacherHW_th_studentName"/></th>
					<%
						for(Project project : dbProjects){
							%>
								<th><%=project.getName() %></th>
							<%
						}
					%>
				</tr>
			</thead>
			<tbody>
				<%
					for(User user : users){
						String userName = user.getUserName();
						String personal_url = gitData.getHostUrl() + "/u/" + userName;
						%>
							<tr>
								<td><%=user.getUserName() %></td>
								<td><strong><a href="<%=personal_url %>" onclick="window.open('<%=personal_url %>')"><%=user.getName() %></a></strong></td>
								<%
									gitProjects = conn.getProject(user);
									Collections.reverse(gitProjects);
									for(Project dbProject : dbProjects){
										String proName = null;
										for(GitlabProject gitProject : gitProjects){
											if(dbProject.getName().equals(gitProject.getName())){
												proName = dbProject.getName();
											}else{
												proName = "N/A";
											}
										}
										%>
											<td><%=proName %></td>
										<%
									}
								%>
							</tr>
						<%
					}
				%>
			</tbody>
		</table>
	</div>
</body>
</html>