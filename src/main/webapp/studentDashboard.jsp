<%@ page language="java" contentType="text/html; charset=BIG5" pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.StudentConn, fcu.selab.progedu.conn.HttpConnect"%>
<%@ page import="fcu.selab.progedu.config.GitlabConfig,fcu.selab.progedu.config.CourseConfig"%>
<%@ page import="fcu.selab.progedu.config.JenkinsConfig" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.gitlab.api.GitlabAPI"%>
<%@ page import="org.gitlab.api.models.*"%>
<%@ page import="java.util.*"%>
<%@ page import="fcu.selab.progedu.jenkins.JobStatus" %>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi" %>
<%@ page import="org.json.JSONArray, org.json.JSONException, org.json.JSONObject" %>
<%@ page import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.db.ProjectDbManager" %>
<%@ page import="fcu.selab.progedu.data.User, fcu.selab.progedu.data.Project" %>  
<%@ page import="fcu.selab.progedu.conn.StudentDash" %> 
<%@ page import="fcu.selab.progedu.conn.Language" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "studentDashboard");
	
	// Set the student private_token
	String private_token = null;
	
	if(session.getAttribute("private_token").toString() != null && !session.getAttribute("private_token").toString().equals("")){
	  private_token = session.getAttribute("private_token").toString();
	}else{
	  response.sendRedirect("index.jsp");
	}
	
	Cookie[] cookies = request.getCookies();
	Cookie cookie = null;
	if(cookies != null){
	  for(Cookie c : cookies){
	    if(c.getName().equals("private_token")){
	      cookie = c;
	      break;
	    }
	  }
	}
	if(cookie != null){
	  private_token = cookie.getValue();
	}
	
	/*Language language = new Language();
	String lan = request.getParameter("language");
	String basename = null;
	if(null != lan && !"".equals(lan)){
	  basename = language.getBaseName(lan);
	}
	System.out.println("lan : " + lan);
	System.out.println("basename : " + basename);*/
%>

<%@ include file="language.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<style type="text/css">
		html, body {
			height: 100%;
		}
		#mainTable {
			width: 100%;
			height: 100%;
		}
		#sidebar {
			height: 100%;
			background-color: #444;
			color: white; 
			margin: -1px;
		}
		.nav-link {
			color: white; 
		}
		.nav-link:hover{
			color: #33CCFF;
		}
		#main {
			height: 100%;
		}
		
		#inline p {
		    display: inline;
		}
		.ovol {
			border-radius: 5px;
			height: 50px;
            font-weight: bold;
            width: 120px;
            color: white;
            text-align: center;
		}
		.circle {
			border-radius: 30px;
			height: 30px;
            font-weight: bold;
            width: 30px;
            color: white;
            text-align: center;
		}
		.red {
			background: #e52424;
		}
		.blue {
			background: #5fa7e8;
		}
		.gray {
			background: #878787;
		}
		.orange {
			background: #FF5809;
		}
		.green {
			background: #32CD32;
		}
		.gold{
			background: #FFD700;
		}
		.circle a {
			color: #fff;
		}
		#goToJenkins{
			float: right;
			background-color: white;
			color: #1079c9;
			border: 1px solid #1079c9;
		}
		#gotop {
		    display: none;
		    position: fixed;
		    right: 20px;
		    bottom: 20px;    
		    padding: 10px 15px;    
		    font-size: 20px;
		    background: #777;
		    color: white;
		    cursor: pointer;
		}
	</style>

<title>ProgEdu</title>
</head>
<body>
	<%
		// Get the user in Gitlab
		StudentConn sConn = new StudentConn(private_token);
		GitlabUser user = sConn.getUser();
		
		// To display the under html code (about some if-else)
		StudentDash stuDash = new StudentDash(private_token);
		
		// Get the user's Gitlab project
  		List<GitlabProject> stuProjects = stuDash.getStuProject();
  		Collections.reverse(stuProjects);
	%>
	
	<%@ include file="studentHeader.jsp"%>
	
	<table style="width: 100%; height: 100%;">
		<tr>
			<td style="width:250px;">
				<!-- -----sidebar----- -->
				<div id="sidebar">
					<ul class="nav flex-column" style="padding-top: 20px;">
					  <li class="nav-item" style="margin: 10px 0px 0px 15px; color: burlywood;">
					    <font size="4"><i class="fa fa-bar-chart" aria-hidden="true"></i>&nbsp; <fmt:message key="stuDashboard_li_overview"/></font>
					  </li>
					  <li class="nav-item" style="margin: 10px 0px 0px 15px;">
					    <font size="4"><a><i class="fa fa-minus-square-o" aria-hidden="true"> &nbsp;<fmt:message key="stuDashboard_li_assignments"/></i></a></font>
					  </li>
					  <%
						  	for(GitlabProject stuProject : stuProjects){
						  	  String href = "\"studentDashboardChooseProject.jsp?projectId=" + stuProject.getId() + "\"";
						  	  %>
						  	  	<li class="nav-item" style="margin:0px 0px 0px 30px">
								  <font size="3"><a class="nav-link" href=<%=href %>><i class="fa fa-pencil-square-o" aria-hidden="true"><%=stuProject.getName() %></i></a></font>
								</li>
						  	  <%
						  	}
					  %>
					</ul>
				</div>
				<!-- -----sidebar----- -->
			</td>
			<td style="background-color: #f5f5f5; padding-top: 20px;">
				<!-- -----main----- -->
				<div class="container-fluid" id="main">
	            	<h2>
	              		<i class="fa fa-bar-chart" aria-hidden="true"></i> <fmt:message key="stuDashboard_h2_overviewOfAssignments"/>
	            	</h2>
	            	<div id="inline" style="margin-top: 20px;">
						<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
						<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
						<p class="ovol orange" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_checkstyleFail"/></p>
						<!-- 
						<p class="ovol green" style="padding: 5px 10px;"><fmt:message key="dashboard_p_plagiarism"/></p>
						<p class="ovol gold" style="padding: 5px 10px;"><fmt:message key="dashboard_p_unitTestFail"/></p>
						 -->
						<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
					</div>
					<!-- -----table----- -->
					<table class="table table-striped" style="margin-top: 20px;">
						<thead>
							<tr>
								<th width="10%"><fmt:message key="stuDashboard_th_studentId"/></th>
								<%
									for(GitlabProject stuProject : stuProjects){
										%>
											<th><%=stuProject.getName() %></th>
										<%
									}
								%>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><%=user.getUsername() %></td>
								<%
									List<String> jobColors = stuDash.getMainTableJobColor(stuProjects);
									List<String> jobCommitCounts = stuDash.getMainTableJobCommitCount(stuProjects);
									for(GitlabProject stuProject : stuProjects){
									  	int i = 0;
									  	String color = "circle " + jobColors.get(i);
									  	String commitCount = jobCommitCounts.get(i);
									  	String href = "\"studentDashboardChooseProject.jsp?projectId=" + stuProject.getId() + "\"";
									  	%>
									  		<td><p class="<%=color%>"><a href=<%=href %>><%=commitCount %></a></p></td>
									  	<%
									  	i++;
									}
								%>
							</tr>
						</tbody>
					</table>
					<!-- -----table----- -->
				</div>
				<!-- -----main----- -->
			</td>
		</tr>
	</table>
	<div id="gotop"><i class="fa fa-chevron-up" aria-hidden="true"></i></div>
</body>
</html>