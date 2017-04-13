<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="utf-8"%>
<%@ page import="conn.Conn,conn.HttpConnect,teacher.teacherGetUserHw,jenkins.JenkinsApi,conn.Language,data.GitlabData,data.JenkinsData,data.CourseData"%>
<%@ page import="java.util.List" %>
<%@ page import="com.offbytwo.jenkins.client.JenkinsHttpClient" %>
<%@ page import="com.offbytwo.jenkins.JenkinsServer" %>
<%@ page import="com.offbytwo.jenkins.model.JobWithDetails" %>
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.ArrayList"  %>
<%@ page import="java.util.Locale" %>
<%@ page import="db.UserDBManager, db.ProjectDBManager" %>
<%@ page import="data.User, data.Project" %>

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
		
		GitlabData gitData = new GitlabData();
		JenkinsData jenkinsData = new JenkinsData();
		CourseData courseData = new CourseData();
		
		HttpConnect httpConn = new HttpConnect();
		teacherGetUserHw getUserHw = new teacherGetUserHw();
		List<User> users = db.listAllUsers();
		List<GitlabProject> projects = new ArrayList<GitlabProject>();	
		
		GitlabUser root = conn.getRoot();
		
		GitlabSession rootSession = conn.getRootSession();
		String private_token = conn.getPrivate_token(rootSession);
		
		JenkinsApi jenkins = new JenkinsApi();
		
	%>
	
	<div class="container">
		<table class="table table-striped">
			<thead>
				<tr>
					<th><fmt:message key="teacherHW_th_studentId"/></th>
					<th><fmt:message key="teacherHW_th_studentName"/></th>
					<%
						List<Project> Projects = Pdb.listAllProjects();
						for(Project project : Projects){
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
						projects = conn.getProject(user);
						Collections.reverse(projects);
						%>
							<tr>
								<td><%=user.getUserName() %></td>
								<td><strong><a href="<%=personal_url %>" onclick="window.open('<%=personal_url %>')"><%=user.getName() %></a></strong></td>
								<%
									int i=0;
									for(GitlabProject project : projects){
										String project_WebURL = project.getWebUrl();
										String oldStr = project_WebURL.substring(0, 19);
										project_WebURL = project_WebURL.replace(oldStr, gitData.getHostUrl());
										project_WebURL += "/commits/master"; 
										
										//---Jenkins---
										String jobName = user.getUserName() + "_" + project.getName();
										String jobUrl = "http://" + jenkinsData.getUrl() + "/job/" + jobName + "/api/json";
										String color = jenkins.getJobJsonColor(jenkinsData.getUserName() ,jenkinsData.getPassWord(), jobUrl);
										
										String colorPic = null;
										if(color!=null){
											colorPic = jenkins.getColorPic(color);
										}else{
											colorPic = "jenkins_pic/jenkins_gray.PNG";
										}
										//-------------
										
										int count = 0;
										
										if(project.getName().substring(0,courseData.getCourseName().length()).equals(courseData.getCourseName())){
											//String project_event_url = conn.getProjectEvent(project.getId(), private_token);
											//int total_commit_count = getUserHw.httpGetProjectEvent(project_event_url);
											count = httpConn.httpGetCommitCount(project.getId());
											%>
												<td><a href="#" onclick="window.open('<%=project_WebURL%>')"><%=count %></a>
												<img src="<%=colorPic %>" width="36" height="31"></td>
											<%
										}
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