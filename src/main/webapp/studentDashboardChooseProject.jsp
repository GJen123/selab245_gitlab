<%@ page language="java" contentType="text/html; charset=BIG5" pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.StudentConn, fcu.selab.progedu.conn.HttpConnect"%>
<%@ page import="fcu.selab.progedu.config.GitlabConfig,fcu.selab.progedu.config.CourseConfig"%>
<%@ page import="fcu.selab.progedu.config.GitlabConfig,fcu.selab.progedu.config.JenkinsConfig"%>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.gitlab.api.GitlabAPI"%>
<%@ page import="org.gitlab.api.models.*"%>
<%@ page import="java.util.*"%>
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
#goToJenkins{
	float: right;
	background-color: white;
	color: #1079c9;
	border: 1px solid #1079c9;
	margin-bottom: 10px;
}

</style>

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

		String private_token = session.getAttribute("private_token").toString();
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
		<%
		String projectName = "";
		if(projectId != -1){
			GitlabProject project = sConn.getProjectById(projectId);
			projectName = project.getName();
		}
		%>
		<main class="col-md-9 col-xs-11 p-l-2 p-t-2">
		<div class="container" style="margin-top: 30px;">
			<h2>Hello!&nbsp; <%=user.getName()%></h2>
			<div class="card">
		        <h4 id="Student Projects" class="card-header">
		        	<i class="fa fa-table" aria-hidden="true"></i>&nbsp; 
		        		<%=projectName %><fmt:message key="stuDashboard_card_commitRecord"/>
		        </h4>
		        <div class="card-block">
					<div id="inline">
						<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
						<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
						<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
						<%
							String jenkinsHostUrl = jenkinsData.getJenkinsHostUrl();
							String jenkinsJobUrl = user.getUsername() + "_" + projectName;
							//http://140.134.26.71:38080/job/D0239866_OOP-HelloWorld/
						%>
						<a href="<%=jenkinsHostUrl + "/job/" + jenkinsJobUrl%>" id="goToJenkins" class="btn btn-default"><fmt:message key="stuDashboard_card_goToJenkins"/></a>
					</div>
					<table class="table table-striped">
						<thead style="background-color: #a3a3a3;">
							<tr>
							<%
							pro_total_commits = sConn.getAllCommitsCounts(projectId);
							//pro_total_commits = 5;
							for(int i=0; i<pro_total_commits; i++) {
								%>
									<td>
										<p style="margin-bottom: 0px; margin-left: 10px;"><strong><%=i+1%></strong></p>
									</td>
								<%
							}
							%>
							</tr>
						</thead>
						<tbody>
							<tr>
							<%
							GitlabProject project = sConn.getProjectById(projectId);
							commits = sConn.getAllCommits(projectId);
							Collections.reverse(commits);
							String proUrl = project.getWebUrl();
							String replaceProUrl = conn.getReplaceUrl(proUrl);
							for(int i=0; i<pro_total_commits; i++) {
								proUrl = replaceProUrl + "/commit/" + commits.get(i).getId();
								
								String jobName = sConn.getUsername() + "_" + project.getName();
								String jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
								List<Integer> numbers = jenkins.getJenkinsJobAllBuildNumber(jenkinsData.getJenkinsRootUsername() ,jenkinsData.getJenkinsRootPassword(), jobUrl);
								String buildNumber = numbers.get(i).toString();
								String buildUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" + buildNumber + "/api/json";
								String result = jenkins.getJobBuildResult(jenkinsData.getJenkinsRootUsername() ,jenkinsData.getJenkinsRootPassword(), buildUrl);
								String circleColor = "";
								if(i == 0){
									circleColor = "circle gray";
								}
								else {
									if(result.equals("SUCCESS")){
										circleColor = "circle blue";
									}
									if(result.equals("FAILURE")){
										circleColor = "circle red";
									}
								}
									%>
										<td style="background-color: white;">
											<a href="#" onclick="window.open('<%=proUrl %>')"><p class="<%=circleColor%>"></p></a>
										</td>
									<%
							}
							%>
							</tr>
						</tbody>
					</table>
					<iframe src="http://140.134.26.71:38080/job/D0239866_OOP-HelloWorld/" width=100% frameborder="1" scrolling="yes"></iframe>
				</div>
			</div>
			<div class="card" style="margin-top: 30px">
		        <h4 id="Student Projects" class="card-header">
		        	<i class="fa fa-table" aria-hidden="true"></i>&nbsp; 
		        		<fmt:message key="stuDashboard_card_statisticChart"/>
		        </h4>
		        <div class="card-block">
					<div id="hightChart" style="min-width: 310px; height: 350px; max-width: 525px; margin: 0 auto"></div>
				</div>
			</div>
		</div>
		</main>
	</div>
</body>
<script>
Highcharts.chart('hightChart', {
    chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
    },
    title: {
        text: ''
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            }
        }
    },
    series: [{
        name: 'Brands',
        colorByPoint: true,
        data: [{
            name: '未繳交',
            y: 77,
            sliced: true,
            selected: true
        }, {
            name: '已繳交',
            y: 23
        }]
    }]
});
</script>
</html>