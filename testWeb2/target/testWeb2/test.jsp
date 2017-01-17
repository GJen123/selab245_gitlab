<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ page import="url.URLconn,conn.conn" %>
<%@ page import="java.util.List"
		 import="java.util.ArrayList"
		 import="org.gitlab.api.GitlabAPI"
		 import="org.gitlab.api.models.*"
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!-- out.write("<h3><a href=\"http://140.134.26.71:20080/u/"+name[i]+"/projects\">"+name[i]+"</a></h3>"); -->

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>

<title>Insert title here</title>

<style>
	.container{
		background: #eee;
		margin-bottom: 10px;
	}

</style>

</head>
<body>
<%

conn conn = new conn();
List<GitlabUser> Users = new ArrayList<GitlabUser>();
Users = conn.getUsers();
List<GitlabProject> projects = new ArrayList<GitlabProject>();

%>

<%
	for (GitlabUser user : Users) {
		String name = user.getName();
		String userName = user.getUsername();
		String personal_pro_url = "http://140.134.26.71:20080/u/"+userName+"/projects";
		projects = conn.getProject(user);
		%>
		<div class="container" >
		<h1><a href="#" onclick="window.open('<%=personal_pro_url%>')"><%=name %></a></h1>
		
		<%
		int i=1;
		for (GitlabProject project : projects){
			String each_pro_url = "http://140.134.26.71:20080/"+userName+"/"+project.getName();
			%>
			<h2><a href="#" onclick="window.open('<%=each_pro_url%>')"><%=i%> : <%=project.getName() %></a></h2>
			
			<% 
			i++;
		}
		%>
		</div>
		
		<% 
	}
%>

</body>
</html>