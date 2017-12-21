<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn,fcu.selab.progedu.conn.HttpConnect" %>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi, fcu.selab.progedu.conn.Language" %>
<%@ page import="fcu.selab.progedu.config.GitlabConfig" %>
<%@ page import="fcu.selab.progedu.config.JenkinsConfig" %>
<%@ page import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.db.ProjectDbManager" %>
<%@ page import="fcu.selab.progedu.data.User, fcu.selab.progedu.data.Project" %>   
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.*, fcu.selab.progedu.conn.Dash" %>
<%@ page import="fcu.selab.progedu.jenkins.JobStatus" %>
<%@ page import="org.json.JSONArray, org.json.JSONException, org.json.JSONObject" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "dashboard");
	
	int userId = Integer.parseInt(request.getParameter("userId"));
	String projectName = request.getParameter("proName");
%>

<%@ include file="language.jsp" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<style type="text/css">
		body, html {
			height: 100%;
		}
		#inline p {
		    display: inline;
		}
		#inline{
			margin: 20px;
		}
		
		.sidebar {
			height: 100%;
			background-color: #444;
			color: white; 
			margin: -1px;
			position: fixed; /* Set the navbar to fixed position */
   			top: 0;
   			padding-top: 50px;
   		 	overflow-x:hidden;
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
		.ovol {
			border-radius: 50px;
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
		html, body, .row, #navHeight {
			height: 100%;
		}
		#pProject a{
			width:1px;
			height:1px;
		}
	</style>
	
	<link rel="shortcut icon" href="img/favicon.ico"/>
	<link rel="bookmark" href="img/favicon.ico"/>
	<title>ProgEdu</title>
</head>
<body>
	
	<%
		Conn conn = Conn.getInstance();
	
		UserDbManager db = UserDbManager.getInstance();
		ProjectDbManager Pdb = ProjectDbManager.getInstance();
		
		List<User> users = db.listAllUsers();
		List<Project> dbProjects = Pdb.listAllProjects();
		
		// gitlab jenkins course��Data
		GitlabConfig gitData = GitlabConfig.getInstance();
		JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
		
		JenkinsApi jenkins = JenkinsApi.getInstance();
		
		GitlabUser choosedUser = conn.getUserById(userId);
		List<GitlabProject> projects = conn.getProject(choosedUser);
		Collections.reverse(projects);
	%>
	<%@ include file="header.jsp" %>
	<table style="width: 100%; height: 100%;">
		<tr>
			<td style="width:200px">
				<!-- -----sidebar----- -->
				<div class="sidebar" style="width:200px">
					<ul class="nav flex-column" style="padding-top: 20px;">
            			<li class="nav-item">
	        				<font size="4"><a href="javascript:;" data-toggle="collapse" data-target="#overview" class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp; <%=choosedUser.getUsername() %> <i class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
            				<ul id="overview" class="collapse" style="list-style: none;">
	          			          <%
			 			           	for(GitlabProject project : projects){
			    			        	  for(Project dbProject : dbProjects){
			            	   			  if(project.getName().equals(dbProject.getName())){
			            	      			String href = "dashProjectChoosed.jsp?userId=" + choosedUser.getId() + "&proName=" + project.getName();
			            	      %>
			            	      				<li class="nav-item"><font size="3"><a class="nav-link" href=<%=href %>><i class="fa fa-angle-right" aria-hidden="true"></i>&nbsp; <%=project.getName() %></a></font></li>
			            	      <%
			            	    			}
			            	  			}
			            			}
			            		%>
	            			</ul>
	        			</li>
            			<li class="nav-item">
                			<font size="4"><a href="javascript:;" data-toggle="collapse" data-target="#student" class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_a_student"/> <i class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
                			<ul id="student" class="collapse show" style="list-style: none;">
                    			<%
		            				for(User user : users){
		            					String style = "color: white;";
			            	  			String userName = user.getUserName();
			            	  			String href = "\"dashStuChoosed.jsp?studentId=" + user.getGitLabId() + "\"";
			            	  			if(choosedUser.getUsername().equals(user.getUserName())) {
			            	 				style = "color: burlywood;";
			            	 			}
		            	  		%>
		            	  			<li class="nav-item"><font size="3"><a style="<%=style%>" class="nav-link" href=<%=href %>><i class="fa fa-angle-right" aria-hidden="true"></i>&nbsp; <%=userName %></a></font></li>
		            	 		 <%
		            				}
		            			%>
                			</ul>
            			</li>
          			</ul>
				<!-- -----sidebar----- -->
			</div>
		</td>
		<td style="padding-top: 20px; padding-left: 0px; position: fixed; top: 0x;" class="col-md-10">
			<div class="container-fluid col-md-12" id="main" style="margin-top: 0px;">
	        	<h1 style="margin-bottom: 20px;"> <%=choosedUser.getUsername() %>_ <%=projectName %> </h1>
		        <!-- ---------------------------- Project ------------------------------- -->
		        <div class="card col-md-12" style="padding:0;">
		        	<h4 id="Student Projects" class="card-header"><i class="fa fa-table" aria-hidden="true"></i>&nbsp; Records</h4>
		        	<div class="card-block">
						<div id="inline">
							<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
							<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
							<p class="ovol orange" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_checkstyleFail"/></p>
							<!-- <p class="ovol green" style="padding: 5px 10px;"><fmt:message key="dashboard_p_plagiarism"/></p>
							<p class="ovol gold" style="padding: 5px 10px;"><fmt:message key="dashboard_p_unitTestFail"/></p> -->
							<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
						</div>
						<table class="table table-striped" style="margin-top: 20px; width: 100%">
							<thead>
								<tr>
									<th width="10%">Commit</th>
									<th width="10%">Light</th>
									<th width="15%">Date</th>
									<th>Commit Message</th>
								</tr>
							</thead>
							<tbody>
								<%
									GitlabProject choosedProject = new GitlabProject();
									for(GitlabProject project : projects){
									  if(projectName.equals(project.getName())){
									    choosedProject = project;
									  }
									}
									int commit_count = conn.getAllCommitsCounts(choosedProject.getId());
									List<GitlabCommit> commits = conn.getAllCommits(choosedProject.getId());
									Collections.reverse(commits);
									String circleColor = null;
									String projectJenkinsUrl = null;
									for(int num=1; num<=commit_count; num++){
									  String jobName = choosedUser.getUsername() + "_" + projectName;
									  String jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
									  List<Integer> buildNumbers = jenkins.getJenkinsJobAllBuildNumber(jenkinsData.getJenkinsRootUsername(), jenkinsData.getJenkinsRootPassword(), jobUrl);
									  String buildUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + num + "/api/json";
									  String buildApiJson = jenkins.getJobBuildApiJson(jenkinsData.getJenkinsRootUsername() ,jenkinsData.getJenkinsRootPassword(), buildUrl);
									  String result = jenkins.getJobBuildResult(buildApiJson);
									  
									  // Get commit date
									  Date date = commits.get(num-1).getCreatedAt();
									  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
									  sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
									  String strDate = sdf.format(date);
									  
									  if(result.equals("SUCCESS")){
									    circleColor = "circle blue";
									    projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName;
									  }else{
									    circleColor = "circle red";
									    projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + num +"/consoleText";
									    
									    // check if is checkstyle error
									    String consoleText = jenkins.getConsoleText(projectJenkinsUrl);
									    boolean isCheckstyleError = jenkins.checkIsCheckstyleError(consoleText);
									    if(isCheckstyleError){
									      circleColor = "circle orange";
									      projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + num +"/violations";
									    }
									  }
									  if(num == 1){
									    circleColor = "circle gray";
									  }
									  %>
									  	<tr>
									  		<th width="10%"><%=num %></th>
									  		<td width="10%"><p class="<%=circleColor%>" id="pProject"><a href="#" onclick="window.open('<%=projectJenkinsUrl  %>')">&nbsp;</a></p></td>
									  		<td width="15%"><%=strDate %></td>
									  		<td><%=commits.get(num-1).getMessage() %></td>
									  	</tr>
									  <%
									}
								%>
							</tbody>
						</table>
		        	</div>
		        </div>
		        <!-- ---------------------------- Student Project ------------------------------- -->
	        </div>
<!-- ------------------------ main -------------------------------------- -->
			</td>
		</tr>
		</table>
	</body>
</html>