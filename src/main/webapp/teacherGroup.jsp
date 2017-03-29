<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="utf-8"%>
<%@ page import="conn.Conn,conn.HttpConnect,conn.Language,data.GitlabData"%>
<%@ page import="java.util.List" import="java.util.ArrayList"
	import="org.gitlab.api.GitlabAPI" import="org.gitlab.api.models.*"%>
	
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
	<%
		if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
			response.sendRedirect("index.jsp");
		}
		Language language = new Language();
		session.putValue("page", "teacherGroup");
		String lan = null;
		String basename = null;
		if(session.getAttribute("language") == null || session.getAttribute("language").toString().equals("")){
			lan = "English";
			basename = language.getBaseName(lan);
		}else{
			lan = session.getAttribute("language").toString();
			basename = language.getBaseName(lan);
		}
		System.out.println("lan : " + lan);
		System.out.println("basename : " + basename);
	%>
	
	<!-- 設定語言 -->
	<fmt:setBundle basename = "<%=basename %>"/>
	
	<div class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand">ProgEdu</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li><a href="teacherHW.jsp"><fmt:message key="top_navbar_dashboard"/></a></li>
                    <li class="active"><a href="teacherGroup.jsp"><fmt:message key="top_navbar_groupProject"/></a></li>
                    <li class="dropdown">
                    	<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                    		<fmt:message key="top_navbar_manage"/> <span class="caret"></span></a>
                    	<ul class="dropdown-menu">
	                    	<li><a href="teacherManageStudent.jsp"><fmt:message key="top_navbar_manageStudent"/></a></li>
	                    	<li><a href="teacherManageHW.jsp"><fmt:message key="top_navbar_manageHW"/></a></li>
	                    	<li><a href="teacherManageGroup.jsp"><fmt:message key="top_navbar_manageGroup"/></a></li>
                    	</ul>
                    </li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                	<li class="dropdown">
                    	<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                    		<fmt:message key="top_navbar_language"/> <span class="caret"></span></a>
                    	<ul class="dropdown-menu" >
	                    	<li id="English" value="English"><a href="ChooseLanguage?language=English"><fmt:message key="top_navbar_lanEnglish"/></a></li>
	                    	<li id="Chinese" value="Chinese"><a href="ChooseLanguage?language=Chinese"><fmt:message key="top_navbar_lanChinese"/></a></li>
                    	</ul>
                    </li>
        			<li><a href="memberLogOut.jsp" id="loginLink"><fmt:message key="top_navbar_signOut"/></a></li>
    			</ul>
            </div>
        </div>
    </div>
	<br><br><br>
	
	<%
		Conn conn = Conn.getInstance();
		GitlabData gitData = new GitlabData();
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
																String oldStr = projectUrl.substring(0, 19);
																projectUrl = projectUrl.replace(oldStr, gitData.getHostUrl());
																projectUrl += "/commits/master";
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
																String memberUrl = gitData.getHostUrl() + "/u/" + memberUsername;
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