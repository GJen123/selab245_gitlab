<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn,fcu.selab.progedu.conn.HttpConnect" %>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi, fcu.selab.progedu.conn.Language" %>
<%@ page import="fcu.selab.progedu.config.GitlabConfig" %>
<%@ page import="fcu.selab.progedu.config.JenkinsConfig" %>
<%@ page import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.db.ProjectDbManager" %>
<%@ page import="fcu.selab.progedu.data.User, fcu.selab.progedu.data.Project" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.*, fcu.selab.progedu.conn.Dash" %>
<%@ page import="fcu.selab.progedu.jenkins.JobStatus" %>
<%@ page import="org.json.JSONArray, org.json.JSONException, org.json.JSONObject" %>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "dashboard");
%>

<%@ include file="language.jsp" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<style type="text/css">
		body, html{
			height: 100%;
			overflow-x: hidden;
		}
		#allProject {
		 margin: 10px 0px 0px 0px;
		}
		
		.sidebar {
			height: 100%;
			background-color: #444;
			color: white; 
			margin: -1px;
			position: fixed; /* Set the navbar to fixed position */
   			top: 0;
   			margin-top: 50px;
   		 	overflow-y: scroll;
   		 	z-index: 100;
		}
		.sidebar a{
			color: white;
		}
		.sidebar a:hover{
			color: orange;
		}
		.sidebar button{
			color: white;
			background: none;
		}
		
		#main {
			height: 100%;
			margin-left: 200px;
			overflow-x: scroll;
			padding-top: 20px;
			width: auto;
		}
		
		#inline {
		    margin: 20px;
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
			background: gold;
		}
		.green {
			background: #32CD32;
		}
		.gold{
			background: #FFD700;
		}
		.circle a {
			color: #fff;
			line-height: 30px;
		}
	</style>

	<link rel="shortcut icon" href="img/favicon.ico"/>
	<link rel="bookmark" href="img/favicon.ico"/>
	<title>ProgEdu</title>
	<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="PUBLIC">
</head>
<body>
	<%
		Conn conn = Conn.getInstance();
		
		HttpConnect httpConn = HttpConnect.getInstance();
	
		UserDbManager db = UserDbManager.getInstance();
		ProjectDbManager Pdb = ProjectDbManager.getInstance();
		
		// db users
		List<User> users = db.listAllUsers();
		
		// gitlab projects
		List<GitlabProject> gitProjects = new ArrayList<GitlabProject>();
		
		// db projects
		List<Project> dbProjects = Pdb.listAllProjects();
		
		// gitlab jenkins courseData
		GitlabConfig gitData = GitlabConfig.getInstance();
		JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
		
		JenkinsApi jenkins = JenkinsApi.getInstance();
	%>
	<%@ include file="header.jsp" %>
	<!-- -----sidebar----- -->
	<div class="sidebar" style="width:200px;">
		<ul class="nav flex-column" style="padding-top: 20px;">
   			<li class="nav-item">
   				<font size="4"><a href="javascript:;" data-toggle="collapse" data-target="#overview" class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_a_overview"/> <i class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
   				<ul id="overview" class="collapse" style="list-style: none;">
   					<li class="nav-item"><font size="3"><a class="nav-link" href="#Student Projects"><i class="fa fa-table" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_li_studentProjects"/></a></font></li>
			   			<li class="nav-item"><font size="3">
			   				<a class="nav-link" href="dashboardChart.jsp">
			   					<i class="fa fa-bar-chart" aria-hidden="true"></i>&nbsp; 
			   					<fmt:message key="dashboard_li_chart"/>
			   				</a>
			   			</font></li>
   				</ul>
    		</li>
    		<li class="nav-item">
    		<font size="4"><a href="javascript:;" data-toggle="collapse" data-target="#student" class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_a_student"/> <i class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
  			<ul id="student" class="collapse" style="list-style: none;">
   			            <%
	           	for(User user : users){
            	  String userName = user.getUserName();
            	  String href = "\"dashStuChoosed.jsp?studentId=" + user.getGitLabId() + "\"";
            %>
	           	  	<li class="nav-item"><font size="3"><a class="nav-link" href=<%=href %>><i class="fa fa-angle-right" aria-hidden="true"></i>&nbsp; <%=userName %></a></font></li>
	           	<%
		          	}
		          %>
    		</ul>
     		</li>
    	</ul>
    </div>
	<!-- -----sidebar----- -->
      <div class="container-fluid" id="main" style="width: auto;">
      <h1 style="margin-bottom: 20px;"><fmt:message key="dashboard_a_overview"/></h1>
       <!-- ---------------------------- Student Project ------------------------------- -->
       <div class="card" style="width: fit-content;">
       	<h4 id="Student Projects" class="card-header"><i class="fa fa-table" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_li_studentProjects"/></h4>
       	<div class="card-block">
			<!-- <div id="inline">
				<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
				<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
				<p class="ovol orange" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_checkstyleFail"/></p>
				<p class="ovol green" style="padding: 5px 10px;"><fmt:message key="dashboard_p_plagiarism"/></p>
				<p class="ovol gold" style="padding: 5px 10px;"><fmt:message key="dashboard_p_unitTestFail"/></p>
				<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
			</div> -->
			<%@ include file="projectLight.jsp" %>
			<table class="table table-striped" style="margin-top: 20px; width: 100%">
				<thead>
					<tr>
						<th style="font-weight: 900; font-size: 18px"><fmt:message key="dashboard_th_studentId"/></th>
						<%
							for(Project project : dbProjects){
								%>
									<th style="font-weight: 900; font-size: 18px"><%=project.getName() %></th>
								<%
							}
						%>
					</tr>
				</thead>
				<tbody>
					<%
						for(User user : users){
							String userName = user.getUserName();
							String dashStuChoosedHref = "\"dashStuChoosed.jsp?studentId=" + user.getGitLabId() + "\"";
							%>
								<tr id="allProject">
									<td width="10%" id="allProject"><a href=<%=dashStuChoosedHref %>><%=user.getUserName() %></a></td>
									<%
										gitProjects = conn.getProject(user);
										Collections.reverse(gitProjects);
										for(Project dbProject : dbProjects){
											String proName = null;
											String circleColor = "circle gray";
											for(GitlabProject gitProject : gitProjects){
												if(dbProject.getName().equals(gitProject.getName())){
													proName = dbProject.getName(); 
													break;
												}else{
													proName = "N/A";
												}
											}
											
											if("N/A".equals(proName) || "".equals(proName) || null == proName){
												if(proName == null) {
													proName = "N/A";
											}
												%>
													<td style="margin: 10px 0px 0px 10px;"><%=proName %></td>
												<%
											}else{
											  String dashProjectChoosedHref="dashProjectChoosed.jsp?userId=" + user.getGitLabId() + "&proName=" + dbProject.getName();
												%>
													<td style="padding: 10px 0px 0px 30px;">
														<p id=<%= user.getUserName() + "_" + dbProject.getName()%> class="">
															<a id=<%= user.getUserName() + "_" + dbProject.getName() + "_commit"%> href="<%=dashProjectChoosedHref%>"> </a>
														</p>
													</td>
													
													<script type="text/javascript">
														var userName = <%="'" + user.getUserName() + "'"%>
														var proName = <%="'" + dbProject.getName() + "'"%>
														$.ajax({
															url : 'webapi/commits/result',
															type : 'GET',
															data: {
																"proName" : proName,
																"userName" : userName
															}, 
															async : true,
															cache : true,
															contentType: 'application/json; charset=UTF-8',
															success : function(responseText) {
																var result = responseText.split(",");
																if(result.length >= 3) {
																	var d = document.getElementById(result[0]);
																	d.className = result[1];
																	var a = document.getElementById(result[0] + "_commit");
																	a.textContent = result[2];
																}
															}, 
															error : function(responseText) {
																console.log("False!");
															}
														});
													</script>
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
       </div>
      </div>
</body>
</html>