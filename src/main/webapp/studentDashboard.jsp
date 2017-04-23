<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="BIG5"%>
<%@ page import="conn.Conn,conn.StudentConn,conn.HttpConnect,fcu.selab.progedu.config.GitlabConfig,data.CourseData"%>
<%@ page import="java.util.ArrayList"
	import="org.gitlab.api.GitlabAPI" import="org.gitlab.api.models.*"%>
<%@ page import="java.util.*" %>
<%@ include file="language.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">

<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
	
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>

	<%
		GitlabConfig gitData = GitlabConfig.getInstance();
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
					<div class="table-responsive">
						<table class="table">
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
					</div>
				<%
			}
		%>
	</div>
</body>
</html>