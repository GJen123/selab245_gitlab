<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="utf-8"%>
<%@ page import="conn.conn,conn.httpConnect,teacher.teacherGetUserHw,jenkins.jenkinsApi,conn.Language"%>
<%@ page import="java.util.List" import="java.util.ArrayList" import="java.util.*"
	import="org.gitlab.api.GitlabAPI" import="org.gitlab.api.models.*"
	import="com.offbytwo.jenkins.model.JobWithDetails"
	import="com.offbytwo.jenkins.JenkinsServer"
	import="com.offbytwo.jenkins.client.JenkinsHttpClient"
	%>
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
		session.putValue("page", "teacherHW");
		String lan = session.getAttribute("language").toString();
		String basename = language.getBaseName(lan);
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
                    <li class="active"><a href="teacherHW.jsp"><fmt:message key="top_navbar_dashboard"/></a></li>
                    <li><a href="teacherGroup.jsp"><fmt:message key="top_navbar_groupProject"/></a></li>
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
	                    	<li id="English" value="English"><a href="chooseLanguage?language=English"><fmt:message key="top_navbar_lanEnglish"/></a></li>
	                    	<li id="Chinese" value="Chinese"><a href="chooseLanguage?language=Chinese"><fmt:message key="top_navbar_lanChinese"/></a></li>
                    	</ul>
                    </li>
        			<li><a href="memberLogOut.jsp" id="loginLink"><fmt:message key="top_navbar_signOut"/></a></li>
    			</ul>
            </div>
        </div>
    </div>
    
    <script>
    	
    </script>
    
	<br><br><br>
	
	<%
		conn conn = new conn();
		httpConnect httpConn = new httpConnect();
		teacherGetUserHw getUserHw = new teacherGetUserHw();
		String gitlabURL = "http://140.134.26.71:20080";
		List<GitlabUser> users = conn.getUsers();
		List<GitlabProject> projects = new ArrayList<GitlabProject>();	
		
		GitlabUser root = conn.getRoot();
		
		GitlabSession rootSession = conn.getRootSession();
		String private_token = conn.getPrivate_token(rootSession);
		
		Collections.reverse(users);
		
		jenkinsApi jenkins = new jenkinsApi();
	%>
	
	<div class="container">
		<table class="table table-striped">
			<thead>
				<tr>
					<th><fmt:message key="teacherHW_th_studentId"/></th>
					<th><fmt:message key="teacherHW_th_studentName"/></th>
					<%
						List<GitlabProject> rootProjects = conn.getProject(root);
						Collections.reverse(rootProjects);
						for(GitlabProject project : rootProjects){
							if(project.getName().substring(0,3).equals("OOP")){
								%>
									<th><%=project.getName() %></th>
								<%
							}
						}
					%>
				</tr>
			</thead>
			<tbody>
				<%
					for(GitlabUser user : users){
						String userName = user.getUsername();
			    		String personal_url = "http://140.134.26.71:20080/u/" + userName;
						projects = conn.getProject(user);
						Collections.reverse(projects);
						%>
							<tr>
								<td><%=user.getId() %></td>
								<td><strong><a href="#" onclick="window.open('<%=personal_url %>')"><%=user.getName() %></a></strong></td>
								<%
									for(GitlabProject project : projects){
										String project_WebURL = project.getWebUrl();
										project_WebURL = project_WebURL.replace("http://0912fe2b3e43", "http://140.134.26.71:20080");
										project_WebURL += "/commits/master"; 
										
										//---Jenkins---
										String url = "http://140.134.26.71:38080/api/json";
										ArrayList<HashMap<String,String>> jobJson = jenkins.getJobJson("GJen","zxcv1234" , url, project.getName());
										String color = jenkins.getJobColor(jobJson, userName, project.getName());
										String colorPic = null;
										if(color!=null){
											colorPic = jenkins.getColorPic(color);
										}else{
											colorPic = "jenkins_pic/jenkins_gray.PNG";
										}
										//-------------
										
										if(project.getName().substring(0,3).equals("OOP")){
											String project_event_url = conn.getProjectEvent(project.getId(), private_token);
											int total_commit_count = getUserHw.httpGetProjectEvent(project_event_url);
											%>
												<td><a href="#" onclick="window.open('<%=project_WebURL%>')"><%=total_commit_count %></a>
												<img src="<%=colorPic %>" width="36" height="31"></td>
											<%
										}
									}
								%>
							</tr>
						<%
					}
				
				%>
			</tbody>
		</table>
	</div>
	
</body>
</html>