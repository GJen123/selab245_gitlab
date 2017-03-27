<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="BIG5"%>
<%@ page import="conn.Conn,conn.StudentConn,conn.HttpConnect"%>
<%@ page import="java.util.List" import="java.util.ArrayList"
	import="org.gitlab.api.GitlabAPI" import="org.gitlab.api.models.*"%>
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
<title>Student Dashboard</title>
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
                <a class="navbar-brand">Teacher Dashboard</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li><a href="teacherDashboard.jsp">學生Projects</a></li>
                    <li><a href="#">作業</a></li>
                    <li class="active"><a href="teacherGroup.jsp">專題</a></li>
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
		String private_token = session.getAttribute("private_token").toString();
		StudentConn sConn = new StudentConn(private_token); 
		GitlabUser user = sConn.getUser();
		String Username = sConn.getUserName();
		List<GitlabProject> project = new ArrayList<GitlabProject>();
		project = sConn.getProject();
	%>
	<div class="container">
		<h1>Hello <%=Username %></h1>
		<h3>Your Projects</h3>
		<%
			for(GitlabProject pro : project){
				String proURL = pro.getWebUrl();
				proURL = proURL.replace("http://0912fe2b3e43", "http://140.134.26.71:20080");
				%>
				<p><a href="#" onclick="window.open('<%=proURL%>')"><%=pro.getName() %></a></p>
				<p><%=proURL %></p>
				
				<%
			}
		%>
		<br><br><br><br>
		<h4>Your private_token</h4>
		<p><%=private_token %></p>
	</div>
</body>
</html>