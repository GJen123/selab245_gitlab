<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="BIG5"%>
<%@ page import="conn.Conn,conn.StudentConn,conn.HttpConnect,data.GitlabData,data.CourseData"%>
<%@ page import="java.util.ArrayList"
	import="org.gitlab.api.GitlabAPI" import="org.gitlab.api.models.*"%>
<%@ page import="java.util.*" %>
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
		GitlabData gitData = new GitlabData();
		CourseData courseData = new CourseData();
	
		String private_token = session.getAttribute("private_token").toString();
		StudentConn sConn = new StudentConn(private_token); 
		List<GitlabProject> projects;
		List<GitlabCommit> commits;
		int commits_counts = 0;
		
		int pro_total_commits = 0;
	%>
	<div class="container">
		<h2>Hello <%=sConn.getUsername() %></h2>
		<table class="table table-striped">
			<thead>
				<tr>
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
		<%
			for(GitlabProject project : projects){
				if(!courseData.getCourseName().equals(project.getName().substring(0,3))){
					continue;
				}
				commits = sConn.getAllCommits(project.getId());
				Collections.reverse(commits);
				commits_counts = commits.size();
				%>
					<table class="table table-bordered">
						<thead>
							<tr>
								<th><%=project.getName() %></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<%
									for(int i=1;i<commits_counts+1;i++){
										%>
											<th><%=i %></th>
										<%
									}
								%>
							</tr>
							<tr>
								<%
									for(GitlabCommit commit : commits){
										%>
											<th><%=commit.getMessage() %></th>
										<%
									}
								%>
							</tr>
						</tbody>
					</table>
				<%
			}
		%>
	</div>
</body>
</html>