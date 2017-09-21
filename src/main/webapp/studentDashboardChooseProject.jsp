<%@ page language="java" contentType="text/html; charset=BIG5" pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.StudentConn, fcu.selab.progedu.conn.HttpConnect"%>
<%@ page import="fcu.selab.progedu.config.GitlabConfig,fcu.selab.progedu.config.CourseConfig"%>
<%@ page import="fcu.selab.progedu.config.GitlabConfig,fcu.selab.progedu.config.JenkinsConfig"%>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.gitlab.api.GitlabAPI"%>
<%@ page import="org.gitlab.api.models.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.json.JSONArray, org.json.JSONException, org.json.JSONObject" %>
<%@ page import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.db.ProjectDbManager" %>
<%@ page import="fcu.selab.progedu.data.User, fcu.selab.progedu.data.Project" %>
<%@ page import="fcu.selab.progedu.jenkins.JobStatus, java.text.SimpleDateFormat" %>

<%
	String private_token = null;
	if(null != session.getAttribute("private_token") && !"".equals(session.getAttribute("private_token")) ){
	  private_token = session.getAttribute("private_token").toString();
	}else{
	  response.sendRedirect("index.jsp");
	}
	session.putValue("page", "sutdentDashboardChooseProject");
%>

<%@ include file="language.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
	<style type="text/css">
		#inline p {
			display: inline;
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
		.bigcircle {
			border-radius: 10px;
			height: 60px;
            font-weight: bold;
            width: 60px;
            color: white;
            text-align: center;
		    position: absolute;
    		top: 100%;
    		left: 45%;
    		margin-right: -50%;
    		transform: translate(-50%, -50%)
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
		#goToJenkins{
			float: right;
			background-color: white;
			color: #1079c9;
			border: 1px solid #1079c9;
			margin-bottom: 10px;
		}
		.center-justified {
    		text-align: justify;
    		-moz-text-align-last: center;
    		text-align-last: center;
		}
	</style>
<script type="text/javascript">
		function handleClick(cb, divId){
			var o=document.getElementById(divId);
			if(cb.checked){
				o.style.display='';
			}else{
				o.style.display='none';
			}
		}
</script>

<title>ProgEdu</title>
</head>
<body>
	<%@ include file="studentHeader.jsp"%>

	<%
		String projectIdSession = request.getParameter("projectId");
		int projectId = -1;
		if(null == projectIdSession || "".equals(projectIdSession)){
			  
		}else {
			projectId = Integer.parseInt(projectIdSession);
		}
		GitlabConfig gitData = GitlabConfig.getInstance();
		CourseConfig courseData = CourseConfig.getInstance();
		JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
		Conn conn = Conn.getInstance();
		JenkinsApi jenkins = JenkinsApi.getInstance();
		
		StudentConn sConn = new StudentConn(private_token);
		GitlabUser user = sConn.getUser();
		List<GitlabProject> projects = sConn.getProject();
		Collections.reverse(projects);
		List<GitlabCommit> commits;

		int pro_total_commits = 0;
	%>
	<div class="row">
		<nav class="hidden-xs-down bg-faded sidebar" id="navHeight">
		<ul class="nav nav-pills flex-column" style="margin-top: 20px;">
			<li class="nav-item"><font size="4"><a href="javascript:;" data-toggle="collapse" data-target="#student" class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp;
						<fmt:message key="stuDashboard_a_projects" /> <i class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
				<ul id="student" class="collapse" style="list-style: none;">
					<%
						for (GitlabProject project : projects) {
							//String projectName = project.getName();
							String projectName = project.getName();
							String href = "\"studentDashboardChooseProject.jsp?projectId=" + project.getId() + "\"";
					%>
					<li class="nav-item">
						<font size="3">
							<a class="nav-link" href=<%=href %>>
								<i class="fa fa-angle-right" aria-hidden="true"></i>&nbsp; <%=projectName %>
							</a>
						</font>
					</li>
					<%
						}
					%>
				</ul>
			</li>
		</ul>
		</nav>

		<!-- ------------------------ main -------------------------------------- -->
		<%
		String projectName = "";
		String projectUrl = "";
		if(projectId != -1){
			GitlabProject project = sConn.getProjectById(projectId);
			projectName = project.getName();
			projectUrl = project.getHttpUrl();
			projectUrl = projectUrl.replace("0912fe2b3e43", "140.134.26.71:20080");
		}
		GitlabProject choosedProject = new GitlabProject();
		for(GitlabProject project : projects){
		  if(projectName.equals(project.getName())){
		    choosedProject = project;
		  }
		}
		int commit_count = conn.getAllCommitsCounts(choosedProject.getId());
		commits = conn.getAllCommits(choosedProject.getId());
		Collections.reverse(commits);
		%>
		<main class="col-md-9 col-xs-11 p-l-2 p-t-2">
		<div class="container" style="margin-top: 30px;">
			<h2><i class="fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp; <%=projectName%></h2>
			<p>Git Repository</p>
			<p style="border: 1px solid gray; background-color: white; border-radius: 5px; padding: 5px 0px 5px 10px; color: gray"><%=projectUrl %></p>
			<p>Please clone this repository to your local workspace.</p>
			<!-- ---------------------------- Project ------------------------------- -->
		      <div class="row">  
		      <div class="col-3">
		        <div class="card" style="background: none; border: none">
		        	<h4 id="Student Projects" class="card-header" style="font-weight: bold; background:none">Code Analysis Results</h4>
		       		<div class="card-block center-justified">
		        	<%
						String projectJenkinsUrl = null;
		        		String jobName = user.getUsername() + "_" + projectName;
		        		String jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
						List<Integer> buildNumbers = jenkins.getJenkinsJobAllBuildNumber(jenkinsData.getJenkinsRootUsername(), jenkinsData.getJenkinsRootPassword(), jobUrl);
						String buildUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + commit_count + "/api/json";
						String buildApiJson = jenkins.getJobBuildApiJson(jenkinsData.getJenkinsRootUsername() ,jenkinsData.getJenkinsRootPassword(), buildUrl);
						String result = jenkins.getJobBuildResult(buildApiJson);
						String circleColor = null;
					
						if(result.equals("SUCCESS")){
							circleColor = "bigcircle blue";
							projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName;
						}else{
							circleColor = "bigcircle red";
							projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + commit_count +"/consoleText";
									    
							// check if is checkstyle error
							String consoleText = jenkins.getConsoleText(projectJenkinsUrl);
							boolean isCheckstyleError = jenkins.checkIsCheckstyleError(consoleText);
							if(isCheckstyleError == true){
								circleColor = "bigcircle orange";
								projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + commit_count +"/violations";
							}
						}
						if(commit_count == 1){
						 	circleColor = "bigcircle gray";
						}
						%>
						<p class="<%=circleColor%>"><a style="font-size: 40px"><%=commit_count %></a></p>
					</div>
		        </div>
		        </div>
		        <!-- ------------------------------------------------------------------------------------------------------------------- -->
		        <div class="col-9">
		        <div class="card" style="border:none">
		        	<h4 id="Student Projects" class="card-header" style="font-weight: bold">Programming History</h4>
		        	<div class="card-block">
						<div id="inline">
							<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
							<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
							<p class="ovol orange" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_checkstyleFail"/></p>
							<!-- 
							<p class="ovol green" style="padding: 5px 10px;"><fmt:message key="dashboard_p_plagiarism"/></p>
							<p class="ovol gold" style="padding: 5px 10px;"><fmt:message key="dashboard_p_unitTestFail"/></p>
							 -->
							<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
						</div>
						<table class="table table-striped" style="margin-top: 20px; width: 100%">
							<thead>
								<tr>
									<th width="10%" style="font-weight: bold">#</th>
									<th width="10%" style="font-weight: bold">Status</th>
									<th width="20%" style="font-weight: bold">Date</th>
									<th style="font-weight: bold">Comment</th>
								</tr>
							</thead>
							<tbody>
								<%
									circleColor = null;
									projectJenkinsUrl = null;
									for(int num=1; num<=commit_count; num++){
									  jobName = user.getUsername() + "_" + projectName;
									  jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
									  buildNumbers = jenkins.getJenkinsJobAllBuildNumber(jenkinsData.getJenkinsRootUsername(), jenkinsData.getJenkinsRootPassword(), jobUrl);
									  buildUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + num + "/api/json";
									  buildApiJson = jenkins.getJobBuildApiJson(jenkinsData.getJenkinsRootUsername() ,jenkinsData.getJenkinsRootPassword(), buildUrl);
									  result = jenkins.getJobBuildResult(buildApiJson);
									  
									  // Get commit date
									  Date date = commits.get(num-1).getCreatedAt();
									  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
									    if(isCheckstyleError == true){
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
									  		<td width="10%"><p class="<%=circleColor%>" id="pProject"></p></td>
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
		        </div>
		    </div>
		    
		    <!-- iFrame -->
			<%
				jobName = sConn.getUsername() + "_" + projectName;
				String lastBuildUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/lastBuild/consoleText";
			%>
			<div class="card" style="margin-top: 30px">
				<h4 id="Student Projects" class="card-header">
		        	<i class="fa fa-table" aria-hidden="true"></i>&nbsp; 
		        		輸出
		        </h4>
		        <div class="card-block">
			        <iframe src="<%=lastBuildUrl %>" width="100%" height="500px">
		  				<p>Your browser does not support iframes.</p>
					</iframe>
		        </div>
			</div>
			<!-- iFrame -->
		</div>
		</main>
	</div>
</body>
</html>