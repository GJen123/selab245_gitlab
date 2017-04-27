<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="utf-8"%>
<%@ page import=" fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.HttpConnect"%>
<%@ page import="fcu.selab.progedu.conn.Language,fcu.selab.progedu.config.GitlabConfig" %>
<%@ page import="java.util.List" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Collections" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}	
	session.putValue("page", "teacherGroup");
%>

<%@ include file="language.jsp"%>

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
		Conn conn = Conn.getInstance();
		GitlabConfig gitData = GitlabConfig.getInstance();
		System.out.println("groups : " + conn.getGroups().get(0).getName());
		List<GitlabGroup> groups = conn.getGroups();
		
		%>
			<div class="container">
				<table class="table table-condensed">
					<thead>
						<tr>
					      <th><fmt:message key="teacherGroup_th_group"/></th>
					      <th><fmt:message key="teacherGroup_th_project"/></th>
					      <th><fmt:message key="teacherGroup_th_student"/></th>
					    </tr>
					</thead>
					<tbody>
						<%
							for(GitlabGroup group : groups){
								List<GitlabGroupMember> groupMembers = conn.getGroupMembers(group);
								Collections.reverse(groupMembers);
								List<GitlabProject> projects = conn.getGroupProject(group);
								String groupUrl = conn.getGroupUrl(group);
										%>
											<tr>
												<td>
													<table class="table table-condensed">
														<tr>
															<td><a href="<%=groupUrl %>" onclick="window.open('<%=groupUrl %>')"><%=group.getName() %></a></td>
														</tr>
													</table>
												</td>
												<td>
													<table class="table table-condensed">
														<%
															for(GitlabProject project : projects){
																String projectUrl = project.getWebUrl();
																String oldStr = projectUrl.substring(0, 19);
																projectUrl = projectUrl.replace(oldStr, gitData.getGitlabHostUrl());
																projectUrl += "/commits/master";
																%>
																	<tr>
																		<td><a href="<%=projectUrl %>" onclick="window.open('<%=projectUrl %>')"><%=project.getName() %></a></td>
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
																String memberUrl = gitData.getGitlabHostUrl() + "/u/" + memberUsername;
																%>
																	<tr>
																		<td><a href="<%=memberUrl %>" onclick="window.open('<%=memberUrl %>')"><%=member.getName() %></a></td>
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