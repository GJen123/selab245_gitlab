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

	<div class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="studentDashboard.jsp">ProgEdu</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li><a href="studentDashboard.jsp">儀表版</a></li>
                    <li><a href="#">作業</a></li>
                    <li class="active"><a href="#">專題</a></li>
                    <li><a href="#">作業管理</a></li>
                    <li><a href="#">專題管理</a></li>
                </ul>
                    <ul class="nav navbar-nav navbar-right">
        <li><a href="memberLogOut.jsp" id="loginLink">登出</a></li>
    </ul>

            </div>
        </div>
    </div>
	<br><br><br>

	<%
		GitlabData gitData = new GitlabData();
		CourseData courseData = new CourseData();
	
		String private_token = session.getAttribute("private_token").toString();
		StudentConn sConn = new StudentConn(private_token); 
		List<GitlabProject> projects = new ArrayList<GitlabProject>();
		
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
								pro_total_commits = sConn.getAllCommits(project.getId());
								%>
									<th><%=pro_total_commits %></th>
								<%
							}
							
						}
					%>
					
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>