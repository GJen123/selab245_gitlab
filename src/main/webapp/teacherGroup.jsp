<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.HttpConnect, fcu.selab.progedu.db.GroupDbManager"%>
<%@ page import="fcu.selab.progedu.conn.Language,fcu.selab.progedu.config.GitlabConfig, fcu.selab.progedu.data.Group" %>
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
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>
	 
	<%
		//Conn conn = Conn.getInstance();
		GitlabConfig gitData = GitlabConfig.getInstance();
		//List<GitlabGroup> groups = conn.getGroups();
		GroupDbManager gdb = GroupDbManager.getInstance();
		List<Group> groups = gdb.listGroups();
		String groupUrl = "/groups/";
		
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
							for(Group group : groups){
								//List<GitlabGroupMember> groupMembers = conn.getGroupMembers(group);
								//Collections.reverse(groupMembers);
								//List<GitlabProject> projects = conn.getGroupProject(group);
								//String groupUrl = conn.getGroupUrl(group);
										%>
											<tr>
												<td>
													<table class="table table-condensed">
														<tr>
															<td><a href="<%=groupUrl + group.getGroupName() %>" onclick="window.open('<%=groupUrl + group.getGroupName() %>')"><%=group.getGroupName() %></a></td>
														</tr>
													</table>
												</td>
												<td>
													<table class="table table-condensed">
														<%
															/*for(GitlabProject project : projects){
																String projectUrl = project.getWebUrl();
																String oldStr = projectUrl.substring(0, 19);
																projectUrl = projectUrl.replace(oldStr, gitData.getGitlabHostUrl());
																projectUrl += "/commits/master";*/
																%>
																	<tr>
																		
																	</tr>
																<%
															//}
														%>
														
													</table>
												</td>
												<td>
													<table class="table table-condensed">
													<%
														String master = group.getMaster();
														String memberUrl = gitData.getGitlabHostUrl() + "/u/" + master;
													%>
														<tr>
															<td><a href="<%=memberUrl %>" onclick="window.open('<%=memberUrl %>')">組長：<%=master %></a></td>
														</tr>
														<%
															for(String member : group.getContributor()){
																memberUrl = gitData.getGitlabHostUrl() + "/u/" + member;
																if(member.equals("Administrator")) {
																	continue;
																}
																%>
																																		<tr>
																		<td><a href="<%=memberUrl %>" onclick="window.open('<%=memberUrl %>')">組員：<%=member %></a></td>
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