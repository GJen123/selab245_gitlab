<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="utf-8"%>
<%@ page import="conn.Conn,conn.HttpConnect,teacher.teacherGetUserHw,jenkins.JenkinsApi,conn.Language,data.GitlabData,data.JenkinsData,data.CourseData"%>
<%@ page import="java.util.List" %>
<%@ page import="com.offbytwo.jenkins.client.JenkinsHttpClient" %>
<%@ page import="com.offbytwo.jenkins.JenkinsServer" %>
<%@ page import="com.offbytwo.jenkins.model.JobWithDetails" %>
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.ArrayList"  %>
<%@ page import="java.util.Locale" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}

	//抓當地語言
	Locale locale = request.getLocale();
	String country = locale.getCountry();
	String localLan = locale.getLanguage();
	
	String finalLan = localLan;
	String reqLan = request.getParameter("lang");
	String sesLan = null;
	
	if(reqLan == null || reqLan.trim().equals("")) { 
		// 如果request裡沒有值
		System.out.println("no request");
		if(session.getAttribute("language") == null || session.getAttribute("language").toString().equals("")){
			// 如果session裡沒有值
			System.out.println("no session");
			finalLan = localLan;
		}else{
			// session裡有值
			System.out.println("has session");
			sesLan = session.getAttribute("language").toString();
			finalLan = sesLan;
		}
    } else{
    	// request裡有值  優先考慮request裡的值
    	System.out.println("has request");
    	session.putValue("language", reqLan);
    	finalLan = reqLan;
    }
	
	Language language = new Language();
	session.putValue("page", "teacherHW");
	String basename = language.getBaseName(finalLan);
	System.out.println("finalLan : " + finalLan);
	System.out.println("basename : " + basename);
%>
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
	                    	<li id="English" value="en"><a href="ChooseLanguage?language=en"><fmt:message key="top_navbar_lanEnglish"/></a></li>
	                    	<li id="Chinese" value="zh"><a href="ChooseLanguage?language=zh"><fmt:message key="top_navbar_lanChinese"/></a></li>
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
		Conn conn = Conn.getInstance();
	
		GitlabData gitData = new GitlabData();
		JenkinsData jenkinsData = new JenkinsData();
		CourseData courseData = new CourseData();
		
		HttpConnect httpConn = new HttpConnect();
		teacherGetUserHw getUserHw = new teacherGetUserHw();
		List<GitlabUser> users = conn.getUsers();
		List<GitlabProject> projects = new ArrayList<GitlabProject>();	
		
		GitlabUser root = conn.getRoot();
		
		GitlabSession rootSession = conn.getRootSession();
		String private_token = conn.getPrivate_token(rootSession);
		
		Collections.reverse(users);
		
		JenkinsApi jenkins = new JenkinsApi();
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
							if(project.getName().substring(0,courseData.getCourseName().length()).equals(courseData.getCourseName())){
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
					int num = 0;
					for(GitlabUser user : users){
						if(num<5){
							num++;
						}else{
							break;
						}
						String userName = user.getUsername();
			    		String personal_url = gitData.getHostUrl() + "/u/" + userName;
						projects = conn.getProject(user);
						Collections.reverse(projects);
						%>
							<tr>
								<td><%=user.getId() %></td>
								<td><strong><a href="#" onclick="window.open('<%=personal_url %>')"><%=user.getName() %></a></strong></td>
								<%
									for(GitlabProject project : projects){
										String project_WebURL = project.getWebUrl();
										String oldStr = project_WebURL.substring(0, 19);
										project_WebURL = project_WebURL.replace(oldStr, gitData.getHostUrl());
										project_WebURL += "/commits/master"; 
										
										//---Jenkins---
										String url = "http://" + jenkinsData.getUrl() + "/api/json";
										ArrayList<HashMap<String,String>> jobJson = jenkins.getJobJson(jenkinsData.getUserName() ,jenkinsData.getPassWord() , url, project.getName());
										String color = jenkins.getJobColor(jobJson, userName, project.getName());
										String colorPic = null;
										if(color!=null){
											colorPic = jenkins.getColorPic(color);
										}else{
											colorPic = "jenkins_pic/jenkins_gray.PNG";
										}
										//-------------
										
										int count = 0;
										if(project.getName().substring(0,courseData.getCourseName().length()).equals(courseData.getCourseName())){
											//String project_event_url = conn.getProjectEvent(project.getId(), private_token);
											//int total_commit_count = getUserHw.httpGetProjectEvent(project_event_url);
											count = httpConn.httpGetCommitCount(project.getId());
											%>
												<td><a href="#" onclick="window.open('<%=project_WebURL%>')"><%=count %></a>
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