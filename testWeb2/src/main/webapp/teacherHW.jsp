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
                    <li><a href="teacherDashboard.jsp">學生Projects</a></li>
                    <li class="active"><a href="teacherHW.jsp">作業</a></li>
                    <li><a href="teacherGroup.jsp">專題</a></li>
                    <li><a href="teacherManageStudent.jsp">學生管理</a></li>
                    <li><a href="teacherManageHW.jsp">作業管理</a></li>
                    <li><a href="teacherManageGroup.jsp">專題管理</a></li>
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
		String gitlabURL = "http://140.134.26.71:20080";
		List<GitlabUser> users = conn.getUsers();
		List<GitlabProject> projects = new ArrayList<GitlabProject>();	
	%>
	<div class="container">
		<table class="table table-striped">
			<thead>
				<tr>
					<th>學生</th>
					<th>Projects</th>
					<th>Commit Times</th>
				</tr>
			</thead>
			<tbody>
				<%
					for(GitlabUser user : users){
						String userName = user.getUsername();
			    		String personal_url = "http://140.134.26.71:20080/u/" + userName;
						GitlabSession user_session = conn.getSession(gitlabURL, user.getUsername(), user.getUsername());
						String private_token = conn.getToken(user_session);
						String lastName = null;
						List<String> projects_Name = httpConn.httpGetStudentOwnedProjectName(private_token);
						List<String> projects_Url = httpConn.httpGetStudentOwnedProjectUrl(private_token);
						List<Integer> projects_Id = httpConn.httpGetStudentOwnedProjectId(private_token);
						for(int i=0;i<projects_Name.size();i++){
							String project_event_url = conn.getProjectEvent(projects_Id.get(i), private_token);
							int total_commit_count = httpConn.httpGetProjectEvent(project_event_url);
							if(lastName != user.getName()){
								lastName = user.getName();
								%>
									<tr>
										<td><a href="#" onclick="window.open('<%=personal_url %>')"><%=user.getName() %></a></td>
										<td><a href="#" onclick="window.open('<%=projects_Url.get(i) %>')"><%=projects_Name.get(i) %></a></td>
										<td><%=total_commit_count %></td>
									</tr>
								<%
							}else{
								%>
									<tr>
										<td></td>
										<td><a href="#" onclick="window.open('<%=projects_Url.get(i) %>')"><%=projects_Name.get(i) %></a></td>
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