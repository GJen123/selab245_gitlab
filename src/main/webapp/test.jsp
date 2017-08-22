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
<%@ page import="org.json.JSONArray, org.json.JSONException, org.json.JSONObject" %>

<%@ include file="language.jsp" %>
<fmt:setBundle basename = "form_en"/>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	 <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js" integrity="sha384-DztdAPBWPRXSA/3eYEEUWrWCy7G5KFbe8fFjk5JAIxUYHKkDx6Qin1DkWx51bBrb" crossorigin="anonymous"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js" integrity="sha384-vBWWzlZJ8ea9aCX4pEW3rVHjgjt7zpkNpZk+02D9phzyeVkE+jo0ieGizqPLForn" crossorigin="anonymous"></script>
	<link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	<link href="font-awesome/progedu.css" rel="stylesheet" type="text/css">
	<script src="https://code.highcharts.com/highcharts.js"></script>
	<script src="https://code.highcharts.com/modules/data.js"></script>
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
	</style>
	<style>
		body{
			padding-top: 50px;
			overflow-x: hidden;
		}
		.container{
			padding-bottom: 30px;
		}
		button{
			font-family: -apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif;
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
	
	<nav class="navbar fixed-top navbar-toggleable-md navbar-inverse bg-inverse">
	  		<button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
	    		<span class="navbar-toggler-icon"></span>
	  		</button>
	  		<a class="navbar-brand" href="dashboard.jsp">ProgEdu</a>
	  		<div class="collapse navbar-collapse" id="navbarNavDropdown">
		    	<ul class="navbar-nav">
		      		<li class="nav-item"><a class="nav-link" href="dashboard.jsp"><fmt:message key="top_navbar_dashboard"/></a></li>
		      		<li class="nav-item"><a class="nav-link" href="teacherGroup.jsp"><fmt:message key="top_navbar_groupProject"/></a></li>
		      		<li class="nav-item dropdown">
		        		<a class="nav-link dropdown-toggle" href="http://example.com" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		          			<fmt:message key="top_navbar_manage"/>
		        		</a>
		        		<div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
		          			<a class="dropdown-item" href="studentManagement.jsp"><i class="fa fa-user-plus" aria-hidden="true"></i> <fmt:message key="top_navbar_manageStudent"/></a>
		          			<a class="dropdown-item" href="assignmentManagement.jsp"><i class="fa fa-pencil-square" aria-hidden="true" style="margin-right: 5px;"></i> <fmt:message key="top_navbar_manageHW"/></a>
		          			<a class="dropdown-item" href="groupManagement.jsp"><i class="fa fa-users" aria-hidden="true" style="margin-right: 2px;"></i> <fmt:message key="top_navbar_manageGroup"/></a>
		        		</div>
		      		</li>
		    	</ul>
		    	<ul class="navbar-nav navbar-toggler-right">
		    		<li class="nav-item dropdown">
		        		<a class="nav-link dropdown-toggle" href="http://example.com" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		          			<fmt:message key="top_navbar_language"/>
		        		</a>
		        		<div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
		        			<a class="dropdown-item" href="ChooseLanguage?language=zh"><fmt:message key="top_navbar_lanChinese"/></a>
		          			<a class="dropdown-item" href="ChooseLanguage?language=en"><fmt:message key="top_navbar_lanEnglish"/></a>
		        		</div>
		      		</li>
		    		<li class="nav-item"><a class="nav-link" href="memberLogOut.jsp" id="loginLink"><fmt:message key="top_navbar_signOut"/> <i class="fa fa-sign-out" aria-hidden="true"></i></a></li>
		    	</ul>
	  		</div>
	  	<div id="gotop"><i class="fa fa-chevron-up" aria-hidden="true"></i></div>
	</nav>
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
							<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
							<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
							<p class="ovol orange" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_checkstyleFail"/></p>
							<p class="ovol green" style="padding: 5px 10px;"><fmt:message key="dashboard_p_plagiarism"/></p>
							<p class="ovol gold" style="padding: 5px 10px;"><fmt:message key="dashboard_p_unitTestFail"/></p>
							<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
						</div>
						<table class="table table-striped" style="margin-top: 20px; width: 100%">
							<thead>
								<tr>
									<th><fmt:message key="dashboard_th_studentId"/></th>
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
								List<JSONObject> jsons = new ArrayList<JSONObject>();
								
									for(User user : users){
										String userName = user.getUserName();
										String personal_url = gitData.getGitlabHostUrl() + "/u/" + userName;
										%>
											<tr>
												<td width="15%"><a href="#" onclick="window.open('<%=personal_url %>')"><%=user.getUserName() %></a></td>
												<%
													gitProjects = conn.getProject(user);
													Collections.reverse(gitProjects);
													for(Project dbProject : dbProjects){
														String proName = null;
														String proUrl = null;
														JobStatus jobStatus = new JobStatus();
														String checkStyleResultUrl = null;
														String projectJenkinsUrl = null;
														int commit_count = 0;
														String circleColor = "circle gray";
														for(GitlabProject gitProject : gitProjects){
															if(dbProject.getName().equals(gitProject.getName())){
																
																JSONObject json = new JSONObject();
																json.put("name", dbProject.getName());
																int redCount = 0;
																int blueCount = 0;
																int grayCount = 0;
																int orangeCount = 0;
																int commitCount = 0;
																		
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
																boolean isMaven = jenkins.checkProjectIsMvn(jobStatus.getJobApiJson());
																
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
																}
																
																if(commit_count == 1){
																  circleColor = "circle gray";
																  projectJenkinsUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName;
																  grayCount++;
																  
																  Random ran = new Random();
																  int num = ran.nextInt(7)+2; // 2~8
																  commit_count = num;
																  int colorNum = ran.nextInt(10)+1;
																  if(colorNum == 1){
																    circleColor = "circle red";
																  }else if (colorNum == 2){
																    circleColor = "circle gray";
																    commit_count = 1;
																  }else if (colorNum == 3){
																    circleColor = "circle orange";
																  }else if (colorNum == 4){
																    circleColor = "circle blue";
																  }else if (colorNum == 5){
																    circleColor = "circle gold";
																  }
																  else{
																    circleColor = "circle blue";
																  }
																  
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
																  	commitCount += commit_count;
																}
																json.put("blueCount", blueCount);
																json.put("redCount", redCount);
																json.put("orangeCount", orangeCount);
																json.put("grayCount", grayCount);
																json.put("commitCount", commitCount);
																jsons.add(json);
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
																<td><p class="<%=circleColor%>"><a href="#" onclick="window.open('<%=projectJenkinsUrl  %>')"><%=commit_count %></a></p></td>

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
						</ul>
		        		<!-- Tab panes -->
						<div class="tab-content text-center" style="margin-top: 10px">
						  <div class="tab-pane active" id="chart1" role="tabpanel">
						  	<div id="chart1Demo" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
						  	</div>
						  <div class="tab-pane" id="chart2" role="tabpanel">
						  	<div id="chart2Demo" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
						  </div>
						  <div class="tab-pane" id="chart3" role="tabpanel">
						  	
						  	<img src="img/commitStiuation.png" alt="Smiley face" height="435" width="850">
						  </div>
						</div>
		        	</div>
		        </div>	
	        </div>
        </main>
<!-- ------------------------ main -------------------------------------- -->
      </div>
</body>
<!-- set Highchart colors -->
<script>
Highcharts.setOptions({
	 colors: ['#5fa7e8', '#e52424', '#FF5809', '#878787']
	})
</script>
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

<!-- chart2 -->
<script type="text/javascript">
<%
List<Integer> commits = new ArrayList<Integer>();
names = new ArrayList<String>();
for(JSONObject json : jsons) {
	if(!names.contains(json.get("name"))) {
		names.add(json.get("name").toString());
		commits.add(Integer.parseInt(json.get("commitCount").toString()));
	}else {
		int index = names.indexOf(json.get("name"));
		int commit = commits.get(index) + Integer.parseInt(json.get("commitCount").toString());
		
		commits.set(index, commit);
	}
}
j=0;
s = "var s = [{ name: 'commit次數', type: 'column', data:[";
j = 0;
for(int commit : commits) {
	s += commit;
	if(j != commits.size()-1) {
		s += ", ";
	}
	j++;
}
s += "]}, { name: '建置成功', type: 'spline', data:[";
for(int blue : blues) {
	s += blue;
	if(j != blues.size()-1) {
		s += ", ";
	}
	j++;
}
s += "]}, { name: '編譯失敗', type: 'spline', data:[";
j = 0;
for(int red : reds) {
	s += red;
	if(j != reds.size()-1) {
		s += ", ";
	}
	j++;
}
s += "]}, { name: '未通過程式規範', type: 'spline', data:[";
j = 0;
for(int orange : oranges) {
	s += orange;
	if(j != oranges.size()-1) {
		s += ", ";
	}
	j++;
}
s += "]}]";
out.println(s);
%>
Highcharts.chart('chart2Demo', {
    chart: {
        zoomType: 'xy'
    },
    title: {
        text: '各作業上傳次數及建置結果統計'
    },
    subtitle: {
        text: ''
    },
    xAxis: [{
        categories: x,
        crosshair: true
    }],
    yAxis: [{ // Primary yAxis
        labels: {
            format: '',
            style: {
                color: Highcharts.getOptions().colors[1]
            }
        },
        title: {
            text: '次數',
            style: {
                color: Highcharts.getOptions().colors[1]
            }
        }
    }, { // Secondary yAxis blue
        title: {
            text: '個數',
            style: {
                color: Highcharts.getOptions().colors[0]
            }
        },
        labels: {
            format: '',
            style: {
                color: Highcharts.getOptions().colors[0]
            }
        },
        opposite: true
    }],
    tooltip: {
        shared: true
    },
    legend: {
        layout: 'vertical',
        align: 'left',
        x: 120,
        verticalAlign: 'top',
        y: 100,
        floating: true,
        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
    },
    series: s
});
</script>
</html>