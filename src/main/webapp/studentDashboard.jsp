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

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "dashboard");
%>

<%@ include file="language.jsp"%>

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
		body, html, .row, #navHeight{
					height:100%;
				}
	</style>

<title>ProgEdu</title>
</head>
<body>
	<%@ include file="studentHeader.jsp"%>

	<%
		GitlabConfig gitData = GitlabConfig.getInstance();
		CourseConfig courseData = CourseConfig.getInstance();
		JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
		
		JenkinsApi jenkins = JenkinsApi.getInstance();

		String private_token = null;
		if(!"".equals(session.getAttribute("private_token").toString()) && null != session.getAttribute("private_token").toString()){
		  private_token = session.getAttribute("private_token").toString();
		}else{
		  response.sendRedirect("index.jsp");
		}
		
		StudentConn sConn = new StudentConn(private_token);
		GitlabUser user = sConn.getUser();
		List<GitlabProject> projects = sConn.getProject();
		Collections.reverse(projects);
		
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
							String projectName = project.getNameWithNamespace();
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
		
		<main class="col-md-9 col-xs-11 p-l-2 p-t-2">
		<div class="container" style="margin-top: 30px;">
			<h2>Hello!&nbsp; <%=user.getName()%></h2>
			<div class="card">
		        <h4 id="Student Projects" class="card-header">
		        	<i class="fa fa-table" aria-hidden="true"></i>&nbsp; 
		        		<fmt:message key="stuDashboard_card_projects"/>
		        </h4>
		        <div class="card-block">
					<div id="inline">
						<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
						<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
						<p class="ovol orange" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_checkstyleFail"/></p>
						<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
					</div>
					<table class="table table-striped" style="margin-top: 20px; width: 100%">
						<thead>
							<tr>
								<th width="15%"></th>
								<%
									for(GitlabProject project : projects){
									  %>
									  	<th><%=project.getName() %></th>
									  <%
									}
								%>
							</tr>
						</thead>
						<tbody>
							<tr>
								<th width="15%">Commit</th>
								<%
									int commit_count = 0;
									List<JSONObject> jsons = new ArrayList<JSONObject>();
									for(GitlabProject project : projects){
										JSONObject json = new JSONObject();
										json.put("name", project.getName());
										int redCount = 0;
										int blueCount = 0;
										int grayCount = 0;
										int orangeCount = 0;
										int commitCount = 0;
										
									  commit_count = sConn.getAllCommitsCounts(project.getId());
									  JobStatus jobStatus = new JobStatus();
									  String jobName = user.getUsername() + "_" + project.getName();
									  jobStatus.setName(jobName);
									  String jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
									  jobStatus.setUrl(jobUrl);
									  jobStatus.setJobApiJson();
									  
									  if(null != jobStatus.getJobApiJson() && !"".equals(jobStatus.getJobApiJson())){
									    // has jenkins
									    // Get job status
										jobStatus.setJobApiJson();
										boolean isMaven = jenkins.checkProjectIsMvn(jobStatus.getJobApiJson());
										// --- Get job status End ---
										String color = null;
										String circleColor = null;
										int checkstyleErrorAmount = 0;
										String projectJenkinsUrl = null;
										
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
											  grayCount++;
											} else {
											  	if(color!=null){
											  	  circleColor = "circle " + color;
											  	if(color.equals("red")) {
											  		  redCount++;
											  	  }
											  	  if(color.equals("blue")) {
											  		  blueCount++;
											  	  }
											  	  if(color.equals("orange")) {
											  		  orangeCount++;
											  	  }
												}else{
												  circleColor = "circle gray";
												  grayCount++;
												}
											}
											json.put("blueCount", blueCount);
											json.put("redCount", redCount);
											json.put("orangeCount", orangeCount);
											json.put("grayCount", grayCount);
											json.put("commitCount", commitCount);
											jsons.add(json);
											//-------------
										}
										%>
											<td><p class="<%=circleColor%>"><a href="#" onclick="window.open('<%=projectJenkinsUrl  %>')"><%=commit_count %></a></p></td>
										<%
									  }else{
									    // no jenkins
									    %>
									    	<td><%=commit_count %></td>
									    <%
									  }
									}
								%>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<div class="card" style="margin-top: 30px">
		        <h4 id="Student Projects" class="card-header">
		        	<i class="fa fa-table" aria-hidden="true"></i>&nbsp; 
		        		<fmt:message key="stuDashboard_card_statisticChart"/>
		        </h4>
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
						</ul>
		        		<!-- Tab panes -->
						<div class="tab-content text-center" style="margin-top: 10px">
						  <div class="tab-pane active" id="chart1" role="tabpanel">
						  	<div id="chart1Demo" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
						  	</div>
						  <div class="tab-pane" id="chart2" role="tabpanel">
						  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
						  </div>
						  <div class="tab-pane" id="chart3" role="tabpanel">
						  	
						  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
						  </div>
						</div>
		        	</div>
			</div>
		</div>
		</main>
	</div>
	<div id="gotop"><i class="fa fa-chevron-up" aria-hidden="true"></i></div>
</body>
<!-- chart1 -->
<script type="text/javascript">
<%
	List<String> names = new ArrayList<String>();
	List<Integer> blues = new ArrayList<Integer>();
	List<Integer> reds = new ArrayList<Integer>();
	List<Integer> oranges = new ArrayList<Integer>();
	List<Integer> grays = new ArrayList<Integer>();
	
	for(JSONObject json : jsons) {
		if(!names.contains(json.get("name"))) {
			names.add(json.get("name").toString());
			blues.add(Integer.parseInt(json.get("blueCount").toString()));
			reds.add(Integer.parseInt(json.get("redCount").toString()));
			oranges.add(Integer.parseInt(json.get("orangeCount").toString()));
			grays.add(Integer.parseInt(json.get("grayCount").toString()));
		}else {
			int index = names.indexOf(json.get("name"));
			int blue = blues.get(index) + Integer.parseInt(json.get("blueCount").toString());
			int red = reds.get(index) + Integer.parseInt(json.get("redCount").toString());
			int orange = oranges.get(index) + Integer.parseInt(json.get("orangeCount").toString());
			int gray = grays.get(index) + Integer.parseInt(json.get("grayCount").toString());
			
			blues.set(index, blue);
			reds.set(index, red);
			oranges.set(index, orange);
			grays.set(index, gray);
		}
	}
	
	String x = "var x=[";
	int i = 0;
	for(String name : names) {
		x += "'" + name + "'";
		if(i != names.size()-1) {
			x += ",";
		}
		i++;
	}
	x += "];";
	out.println(x);
	
	int j=0;
	String s = "var s = [{ name: '建置成功', data:[";
	for(int blue : blues) {
		s += blue;
		if(j != blues.size()-1) {
			s += ", ";
		}
		j++;
	}
	s += "]}, { name: '編譯失敗', data:[";
	j = 0;
	for(int red : reds) {
		s += red;
		if(j != reds.size()-1) {
			s += ", ";
		}
		j++;
	}
	s += "]}, { name: '未通過程式規範', data:[";
	j = 0;
	for(int orange : oranges) {
		s += orange;
		if(j != oranges.size()-1) {
			s += ", ";
		}
		j++;
	}
	s += "]}, { name: '未建置', data:[";
	j = 0;
	for(int gray : grays) {
		s += gray;
		if(j != grays.size()-1) {
			s += ", ";
		}
		j++;
	}
	s += "]}]";
	out.println(s);
%>
Highcharts.chart('chart1Demo', {
    chart: {
        type: 'column'
    },
    title: {
        text: '各作業建置結果統計'
    },
    subtitle: {
        text: ''
    },
    xAxis: {
        categories: x,
        crosshair: true
    },
    yAxis: {
        min: 0,
        title: {
            text: '個數'
        }
    },
    tooltip: {
        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y}</b></td></tr>',
        footerFormat: '</table>',
        shared: true,
        useHTML: true
    },
    plotOptions: {
        column: {
            pointPadding: 0.2,
            borderWidth: 0
        }
    },
    series: s
});
</script>
<script type="text/javascript">
$(function(){
    $("#gotop").click(function(){
        jQuery("html,body").animate({
            scrollTop:0
        },1000);
    });
    $(window).scroll(function() {
        if ( $(this).scrollTop() > 300){
            $('#gotop').fadeIn("fast");
        } else {
            $('#gotop').stop().fadeOut("fast");
        }
    });
});
</script>
</html>