<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page
	import="org.gitlab.api.models.*, 
				  java.util.*"%>
<%@ page import="conn.conn,conn.httpConnect,jenkins.jenkinsApi"%>
<%
	conn conn = new conn();
	List<GitlabUser> users = new ArrayList<GitlabUser>();
	users = conn.getUsers();
	Collections.reverse(users);
	jenkinsApi jenkins = new jenkinsApi();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">


<title>CreateProject</title>
</head>
<body>
	<%!
		String name, rm;
	%>
	<%
		name = request.getParameter("Hw_Name"); 
		rm = request.getParameter("Hw_README");
		name = "OOP-"+name;
		conn.createPrivateProject(name, rm);
		
		String jenkinsUrl = "http://140.134.26.71:38080";
		String jenkinsCrumb = jenkins.getCrumb("GJen", "zxcv1234", jenkinsUrl);
		jenkins.createJenkinsJob(name, jenkinsCrumb);
		jenkins.buildJob(name, jenkinsCrumb);
		response.sendRedirect("teacherManageHW.jsp");
	%>
</body>
</html>