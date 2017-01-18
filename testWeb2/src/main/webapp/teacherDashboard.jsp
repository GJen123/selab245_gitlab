<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ page import="conn.conn,conn.httpConnect"%>
<%@ page import="java.util.List" import="java.util.ArrayList"
	import="org.gitlab.api.GitlabAPI" import="org.gitlab.api.models.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
	
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet"
		href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
	<!-- jQuery library -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
	<!-- Latest compiled JavaScript -->
	<script
		src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
	
	<title>Teacher Dashboard</title>

</head>
<body>
	<%
		if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
			response.sendRedirect("memberEnter.jsp");
		}
	%>
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
                    <li class="active"><a href="teacherDashboard.jsp">學生Projects</a></li>
                    <li><a href="teacherHW.jsp">作業</a></li>
                    <li><a href="teacherGroup.jsp">專題</a></li>
                    <li><a href="teacherManageHW.jsp">作業管理</a></li>
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
		conn conn = new conn();
		httpConnect httpConn = new httpConnect();
		List<GitlabUser> Users = new ArrayList<GitlabUser>();
		Users = conn.getUsers();
		List<GitlabProject> projects = new ArrayList<GitlabProject>();
		GitlabUser root = conn.getRoot();
		List<GitlabProject> root_projects = new ArrayList<GitlabProject>();
		root_projects = conn.getProject(root);
		GitlabSession rootSession = conn.getRootSession();
		String private_token = conn.getPrivate_token(rootSession);
	%>
	<div class="container">
		<table class="table table-striped">
			<thead>
			    <tr>
			      <th>Student</th>
			      <th>Project Name</th>
			      <th>Commit Times</th>
			    </tr>
		  	</thead>
		   	<tbody>
			    <%
			    	for(GitlabUser user : Users){
			    		String userName = user.getUsername();
			    		String personal_url = "http://140.134.26.71:20080/u/" + userName;
			    		projects = conn.getProject(user);
			    		String lastName = null;
			    		for(GitlabProject project : projects){
			    			String project_WebURL = project.getWebUrl();
							project_WebURL = project_WebURL.replace("http://0912fe2b3e43", "http://140.134.26.71:20080");
			    			String project_event_url = conn.getProjectEvent(project.getId(), private_token);
							int total_commit_count = httpConn.httpGetProjectEvent(project_event_url);
				    		if(lastName != user.getName()){
				    			lastName = user.getName();
				    			%>
				    				<tr>
				    					<td><strong><a href="#" onclick="window.open('<%=personal_url %>')"><%=lastName %></a></strong></td>
				    					<td><a href="#" onclick="window.open('<%=project_WebURL %>')"><%=project.getName() %></a></td>
				    					<td><%=total_commit_count %></td>
				    				</tr>
				    			<%
				    		}else{
				    			%>
				    				<tr>
				    					<td></td>
				    					<td><a href="#" onclick="window.open('<%=project_WebURL %>')"><%=project.getName() %></a></td>
				    					<td><%=total_commit_count %></td>
				    				</tr>
				    			<%
				    		}
			    		}
			    	}
			    %>
			    
	  		</tbody>
		</table>
	</div>
</body>
</html>