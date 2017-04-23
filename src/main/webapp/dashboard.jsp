<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ page import="conn.Conn,conn.HttpConnect,jenkins.JenkinsApi" %>
<%@ page import="fcu.selab.progedu.config.GitlabConfig" %>
<%@ page import="fcu.selab.progedu.config.JenkinsConfig" %>
<%@ page import="db.UserDbManager, db.ProjectDbManager" %>
<%@ page import="data.User, data.Project" %>   
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.*" %>
    
<%
	//if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
	//	response.sendRedirect("index.jsp");
	//}
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
		HttpConnect httpConn = HttpConnect.getInstance();
	
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
		
		JenkinsApi jenkins = new JenkinsApi();
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
						String personal_url = gitData.getGitlabHostUrl() + "/u/" + userName;
						%>
							<tr>
								<td><%=user.getUserName() %></td>
								<td><strong><a href="#" onclick="window.open('<%=personal_url %>')"><%=user.getName() %></a></strong></td>
								<%
									gitProjects = conn.getProject(user);
									Collections.reverse(gitProjects);
									for(Project dbProject : dbProjects){
										String proName = null;
										String proUrl = null;
										int commit_count = 0;
										String colorPic = null;
										for(GitlabProject gitProject : gitProjects){
											if(dbProject.getName().equals(gitProject.getName())){
												proName = dbProject.getName();
												proUrl = gitProject.getWebUrl();
												proUrl = conn.getReplaceUrl(proUrl);
												proUrl += "/commits/master"; 
												commit_count = conn.getAllCommitsCounts(gitProject.getId());
												//---Jenkins---
												String jobName = user.getUserName() + "_" + gitProject.getName();
												String jobUrl = "http://" + jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
												String color = jenkins.getJobJsonColor(jenkinsData.getJenkinsRootUsername() ,jenkinsData.getJenkinsRootPassword(), jobUrl);
												
												if(color!=null){
													colorPic = jenkins.getColorPic(color);
												}else{
													colorPic = "jenkins_pic/jenkins_gray.PNG";
												}
												//-------------
												break;
											}else{
												proName = "N/A";
											}
										}
										
										if("N/A".equals(proName)){
											%>
												<td><%=proName %></td>
											<%
										}else{
											%>
												<td><a href="#" onclick="window.open('<%=proUrl %>')"><%=commit_count %></a>
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