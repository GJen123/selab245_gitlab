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
<%@ page import="java.util.*" %>
<%@ page import="fcu.selab.progedu.jenkins.JobStatus" %>

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
		.red {
			background: #e52424;
		}
		.blue {
			background: #258ce8;
		}
		.gray {
			background: #878787;
		}
		.circle a {
			color: #fff;
		}
	</style>
	
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>
	
	<%
		Conn conn = Conn.getInstance();
		HttpConnect httpConn = HttpConnect.getInstance();
	
		UserDbManager db = UserDbManager.getInstance();
		ProjectDbManager Pdb = ProjectDbManager.getInstance();
		
		// db������users
		List<User> users = db.listAllUsers();
		
		// 瘥���摮貊��gitlab��projects
		List<GitlabProject> gitProjects = new ArrayList<GitlabProject>();
		
		// db������projects
		List<Project> dbProjects = Pdb.listAllProjects();
		
		// gitlab jenkins course��Data
		GitlabConfig gitData = GitlabConfig.getInstance();
		JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
		
		JenkinsApi jenkins = JenkinsApi.getInstance();
	%>
      <div class="row">
        <nav class="hidden-xs-down bg-faded sidebar" id="navHeight">
          <ul class="nav nav-pills flex-column" style="margin-top: 20px;">
            <li class="nav-item">
            	<font size="4"><a href="javascript:;" data-toggle="collapse" data-target="#overview" class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_a_overview"/> <i class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
            	<ul id="overview" class="collapse" style="list-style: none;">
            		<li class="nav-item"><font size="3"><a class="nav-link" href="#Student Projects"><i class="fa fa-table" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_li_studentProjects"/></a></font></li>
            		<li class="nav-item"><font size="3"><a class="nav-link" href="#Statistics Chart"><i class="fa fa-bar-chart" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_li_chart"/></a></font></li>
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
        </nav>

<!-- ------------------------ main -------------------------------------- -->
        <main class="col-md-9 col-xs-11 p-l-2 p-t-2">
	        <div class="container" style="margin-top: 20px;">
	        <h1 style="margin-top: 30px; margin-bottom: 20px;"><fmt:message key="dashboard_a_overview"/></h1>
		        <!-- ---------------------------- Student Project ------------------------------- -->
		        <div class="card">
		        	<h4 id="Student Projects" class="card-header"><i class="fa fa-table" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_li_studentProjects"/></h4>
		        	<div class="card-block">
						<div id="inline">
							<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
							<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
							<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
						</div>
						<table class="table table-striped" style="margin-top: 20px; width: 100%">
							<thead>
								<tr>
									<th><fmt:message key="dashboard_th_studentId"/></th>
									<th><fmt:message key="dashboard_th_studentName"/></th>
									<%
										for(Project project : dbProjects){
											%>
												<th><%=project.getName() %></th>
											<%
										}
									%>
								</tr>
							</thead>
							<tbody>
								<%
									for(User user : users){
										String userName = user.getUserName();
										String personal_url = gitData.getGitlabHostUrl() + "/u/" + userName;
										%>
											<tr>
												<td width="10%"><%=user.getUserName() %></td>
												<td width="10%"><strong><a href="#" onclick="window.open('<%=personal_url %>')"><%=user.getName() %></a></strong></td>
												<%
													gitProjects = conn.getProject(user);
													Collections.reverse(gitProjects);
													for(Project dbProject : dbProjects){
														String proName = null;
														String proUrl = null;
														JobStatus jobStatus = new JobStatus();
														String checkStyleResultUrl = null;
														int commit_count = 0;
														String circleColor = "circle gray";
														for(GitlabProject gitProject : gitProjects){
															if(dbProject.getName().equals(gitProject.getName())){
																proName = dbProject.getName();
																proUrl = gitProject.getWebUrl();
																proUrl = conn.getReplaceUrl(proUrl);
																proUrl += "/commits/master"; 
																commit_count = conn.getAllCommitsCounts(gitProject.getId());
																//---Jenkins---
																String jobName = user.getUserName() + "_" + gitProject.getName();
																jobStatus.setName(jobName);
																String jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
																jobStatus.setUrl(jobUrl);
																// Get job status
																jobStatus.setJobApiJson();
																
																String color = jenkins.getJobJsonColor(jobStatus.getJobApiJson());
																checkStyleResultUrl = jenkins.getLastBuildUrl(jobStatus.getJobApiJson());
																checkStyleResultUrl += "checkstyleResult";
																if(commit_count == 1){
																  circleColor = "circle gray";
																} else {
																  	if(color!=null){
																  	  circleColor = "circle " + color;
																	}else{
																	  circleColor = "circle gray";
																	}
																}
																//-------------
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
																<td><%=proName %></td>
															<%
														}else{
															%>
																<td><p class="<%=circleColor%>"><a href="#" onclick="window.open('<%=checkStyleResultUrl  %>')"><%=commit_count %></a></p></td>

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
		        <!-- ---------------------------- Student Project ------------------------------- -->
		        
				<br><br>
		        
		        <!-- Nav tabs -->
		        <div class="card">
		        	<div class="card-header">
		        		<h4 id="Statistics Chart"><i class="fa fa-bar-chart" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_li_chart"/></h4>
		        	</div>
		        	
		        	<div class="card-block">
		        		<ul class="nav nav-tabs" role="tablist">
						  <li class="nav-item">
						    <a class="nav-link active" data-toggle="tab" href="#chart1" role="tab">Chart1</a>
						  </li>
						  <li class="nav-item">
						    <a class="nav-link" data-toggle="tab" href="#chart2" role="tab">Chart2</a>
						  </li>
						  <li class="nav-item">
						    <a class="nav-link" data-toggle="tab" href="#chart3" role="tab">Chart3</a>
						  </li>
						  <li class="nav-item">
						    <a class="nav-link" data-toggle="tab" href="#chart4" role="tab">Chart4</a>
						  </li>
						</ul>
		        		<!-- Tab panes -->
						<div class="tab-content text-center" style="margin-top: 10px">
						  <div class="tab-pane active" id="chart1" role="tabpanel">
						  	
						  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
						  	<h3 style="margin-top: 20px">Chart1</h3>
						  </div>
						  <div class="tab-pane" id="chart2" role="tabpanel">
						  	
						  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
						  	<h3 style="margin-top: 20px">Chart2</h3>
						  </div>
						  <div class="tab-pane" id="chart3" role="tabpanel">
						  	
						  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
						  	<h3 style="margin-top: 20px">Chart3</h3>
						  </div>
						  <div class="tab-pane" id="chart4" role="tabpanel">
						  	
						  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
						  	<h3 style="margin-top: 20px">Chart4</h3>
						  </div>
						</div>
		        	</div>
		        </div>	
	        </div>
        </main>
<!-- ------------------------ main -------------------------------------- -->
      </div>
</body>
</html>