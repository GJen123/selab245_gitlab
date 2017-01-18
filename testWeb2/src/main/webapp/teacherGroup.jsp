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
                    <li><a href="teacherHW.jsp">作業</a></li>
                    <li class="active"><a href="teacherGroup.jsp">專題</a></li>
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
		List<GitlabGroup> groups = conn.getGroups();
		
		%>
			<div class="container">
				<table class="table table-condensed">
					<thead>
						<tr>
					      <th>Group Name</th>
					      <th>Project</th>
					      <th>Students</th>
					    </tr>
					</thead>
					<tbody>
						<%
							for(GitlabGroup group : groups){
								List<GitlabGroupMember> groupMembers = conn.getGroupMembers(group);
								List<GitlabProject> projects = conn.getGroupProject(group);
								String groupUrl = conn.getGroupUrl(group);
										%>
											<tr>
												<td>
													<table class="table table-condensed">
														<tr>
															<td><a href="#" onclick="window.open('<%=groupUrl %>')"><%=group.getName() %></a></td>
														</tr>
													</table>
												</td>
												<td>
													<table class="table table-condensed">
														<%
															for(GitlabProject project : projects){
																String projectUrl = project.getWebUrl();
																projectUrl = projectUrl.replace("http://0912fe2b3e43", "http://140.134.26.71:20080");
																%>
																	<tr>
																		<td><a href="#" onclick="window.open('<%=projectUrl %>')"><%=project.getName() %></a></td>
																	</tr>
																<%
															}
														%>
														
													</table>
												</td>
												<td>
													<table class="table table-condensed">
														<%
															for(GitlabGroupMember member : groupMembers){
																String memberUsername = member.getUsername();
																String memberUrl = "http://140.134.26.71:20080/u/" + memberUsername;
																%>
																	<tr>
																		<td><a href="#" onclick="window.open('<%=memberUrl %>')"><%=member.getName() %></a></td>
																	</tr>
																<%
															}
														
														%>
													</table>
												</td>
											</tr>
										<%
							}
						%>
					</tbody>
				</table>
			</div>
		<%
	%>
	
</body>
</html>