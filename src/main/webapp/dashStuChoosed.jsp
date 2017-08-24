<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn,fcu.selab.progedu.conn.HttpConnect, fcu.selab.progedu.conn.StudentConn" %>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi, fcu.selab.progedu.conn.Language" %>
<%@ page import="fcu.selab.progedu.config.CourseConfig" %>
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
	session.putValue("page", "dashStuChoosed");
%>

<%@ include file="language.jsp" %>

<%
	String studentId = request.getParameter("studentId");
	if(null == studentId){
	  
	}
%>
    
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
		.orange {
			background: #FF5809;
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
	
		UserDbManager db = UserDbManager.getInstance();
		ProjectDbManager Pdb = ProjectDbManager.getInstance();
		
		// Get all db users
		List<User> users = db.listAllUsers();
		
		// Get all db projects
		List<Project> dbProjects = Pdb.listAllProjects();
		
		// gitlab jenkins course data
		GitlabConfig gitData = GitlabConfig.getInstance();
		JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
		CourseConfig courseData = CourseConfig.getInstance();
		
		JenkinsApi jenkins = JenkinsApi.getInstance();
		
		// Get the choosed user
		User choosedUser = new User();
     	for(User user : users){
     		if(studentId.equals(String.valueOf(user.getGitLabId()))){
     			choosedUser = user;
     		    break;
     		}
     	}
	%>
      <div class="row">
        <nav class="hidden-xs-down bg-faded sidebar" id="navHeight">
          <ul class="nav nav-pills flex-column" style="margin-top: 20px;">
            <li class="nav-item">
            	<font size="4"><a href="javascript:;" data-toggle="collapse" data-target="#overview" class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp; <fmt:message key="dashboard_a_overview"/> <i class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
            	<ul id="overview" class="collapse" style="list-style: none;">
            		<li class="nav-item"><font size="3"><a class="nav-link" href="#Student Projects"><i class="fa fa-table" aria-hidden="true"></i> <fmt:message key="stuDashboard_li_projects"/></a></font></li>
            		<li class="nav-item"><font size="3"><a class="nav-link" href="#Statistics Chart"><i class="fa fa-bar-chart" aria-hidden="true"></i> <fmt:message key="dashboard_li_chart"/></a></font></li>
            	</ul>
            </li>
            <li class="nav-item">
            	<font size="4"><a href="javascript:;" data-toggle="collapse" data-target="#projects" class="nav-link"><i class="fa fa-bars" aria-hidden="true"></i>&nbsp; Projects <i class="fa fa-chevron-down" aria-hidden="true"></i></a></font>
            	<ul id="projects" class="collapse" style="list-style: none;">
	                    <%
	                		List<GitlabProject> projects = conn.getProject(choosedUser);
	                		Collections.reverse(projects);
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
                <ul id="student" class="collapse" style="list-style: none;">
                    <%
		            	for(User user : users){
		            	  String userName = user.getUserName();
		            	  String href = "\"dashStuChoosed.jsp?studentId=" + user.getGitLabId() + "\"";
		            	  %>
		            	  	<li class="nav-item"><font size="3"><a class="nav-link" href=<%=href %>><%=userName %></a></font></li>
		            	  <%
		            	}
		            %>
                </ul>
            </li>
          </ul>
        </nav>

        <main class="col-md-9 col-xs-11 p-l-2 p-t-2">
        	<%
        		String private_token = choosedUser.getPrivateToken();
            	StudentConn sConn = new StudentConn(private_token); 	
            	List<GitlabProject> gitProjects = sConn.getProject();
            	int pro_total_commits = 0;
        		
        	%>
        	<div class="container" style="margin-top: 20px;">
        	<h2 style="margin-top: 30px;"><%=choosedUser.getUserName() %></h2>
        		<br><br>
        		 <div class="card">
	        		 <table class="table table-striped" style="width: 100%">
	        		 	<div class="card-header">
		        			<h4 id="Statistics Chart"><i class="fa fa-table" aria-hidden="true"></i>&nbsp; 作業</h4>
		        		</div>
		        		<div class="card-block">
			        		<div id="inline">
								<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
								<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
								<p class="ovol orange" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_checkstyleFail"/></p>
								<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
							</div>
			        		<thead>
								<tr>
									<th width="15%">作業</th>
									<%
										for(Project dbProject : dbProjects){
										  %>
										  	<th><%=dbProject.getName() %></th>
										  <%
										}
									%>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th width="15%">Commits次數</th>
									<%
										for(Project dbProject : dbProjects){
										  
										  int commit_count = 0;
										  JobStatus jobStatus = new JobStatus();
										  String projectJenkinsUrl = null;
										  String circleColor = null;
										  
										  for(GitlabProject gitProject : gitProjects){
										    if(dbProject.getName().equals(gitProject.getName())){
										      commit_count = conn.getAllCommitsCounts(gitProject.getId());
										      //---Jenkins---
												String jobName = choosedUser.getUserName() + "_" + gitProject.getName();
												jobStatus.setName(jobName);
												String jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
												jobStatus.setUrl(jobUrl);
											  // Get job status
												jobStatus.setJobApiJson();
												boolean isMaven = jenkins.checkProjectIsMvn(jobStatus.getJobApiJson());
												// --- Get job status End ---
												String color = null;
												int checkstyleErrorAmount = 0;
												
												if(null != jobStatus.getJobApiJson()){
													color = jenkins.getJobJsonColor(jobStatus.getJobApiJson());
													if(!isMaven){
													  // Javac
													  if(color.equals("red")){
													    // color == red
													    projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/lastBuild/consoleText";
													  }else{
													    // color != red , gray or blue
													    projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName;
													  }
													}else{
													  // Maven
													  if(color.equals("red")){
													    projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/lastBuild/consoleText";
													 	String checkstyleDes = jenkins.getCheckstyleDes(jobStatus.getJobApiJson());
														if(null != checkstyleDes && !"".equals(checkstyleDes)){
														  checkstyleErrorAmount = jenkins.getCheckstyleErrorAmount(checkstyleDes);
														}
														if(checkstyleErrorAmount != 0){
														  color = "orange";
														  projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/violations";
														}
													  }else{
													    projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName;
													  }
													}
													if(commit_count == 1){
													  circleColor = "circle gray";
													  projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName;
													} else {
													  	if(color!=null){
													  	  circleColor = "circle " + color;
														}else{
														  circleColor = "circle gray";
														}
													}
													//-------------
												}
										    }else{
												continue;
											}
										    %>
										    	<td><p class="<%=circleColor%>"><a href="#" onclick="window.open('<%=projectJenkinsUrl  %>')"><%=commit_count %></a></p></td>
										    <%
										  }
										}
									%>
									
								</tr>
							</tbody>
		        		</div>
						
					</table>
        		 </div>
        		
				
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
				<!-- Nav tabs end -->
				
				
        	</div>
        </main>
      </div>

</body>
</html>