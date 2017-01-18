<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page
	import="org.gitlab.api.models.*, 
				  java.util.*"%>
<%@ page import="conn.conn,conn.httpConnect"%>
<%
	conn conn = new conn();
	List<GitlabUser> users = new ArrayList<GitlabUser>();
	users = conn.getUsers();
	Collections.reverse(users);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- 最新編譯和最佳化的 CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">

<!-- 選擇性佈景主題 -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">

<!-- 最新編譯和最佳化的 JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>

<title>CreateProject</title>
</head>
<body>
	<%!
		String name, des, rm;
	%>
	<%
		name = request.getParameter("Hw_Name"); 
		des = request.getParameter("Hw_Description"); 
		rm = request.getParameter("Hw_README");
		
		conn.createPrivateProject(name, rm);
		response.sendRedirect("teacherManageHW.jsp");
	%>
</body>
</html>