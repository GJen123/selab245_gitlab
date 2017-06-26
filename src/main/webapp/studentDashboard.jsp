<%@ page language="java" contentType="text/html; charset=BIG5" pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.StudentConn, fcu.selab.progedu.conn.HttpConnect"%>
<%@ page import="fcu.selab.progedu.config.GitlabConfig,fcu.selab.progedu.config.CourseConfig"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.gitlab.api.GitlabAPI"%>
<%@ page import="org.gitlab.api.models.*"%>
<%@ page import="java.util.*"%>
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
		session.putValue("page", "studentDashboard");
	
		GitlabConfig gitData = GitlabConfig.getInstance();
		CourseConfig courseData = CourseConfig.getInstance();

		String private_token = session.getAttribute("private_token").toString();
		StudentConn sConn = new StudentConn(private_token);
		GitlabUser user = sConn.getUser();
		List<GitlabProject> projects = sConn.getProject();
		Collections.reverse(projects);
		List<GitlabCommit> commits;
		int commits_counts = 0;

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
		
		<main class="col-md-9 col-xs-11 p-l-2 p-t-2">
		<div class="container" style="margin-top: 30px;">
			<h2>Hello!&nbsp; <%=user.getName()%></h2>
			<div class="card">
		        <h4 id="Student Projects" class="card-header">
		        	<i class="fa fa-table" aria-hidden="true"></i>&nbsp; 
		        		<fmt:message key="stuDashboard_card_commitRecord"/>
		        </h4>
		        <div class="card-block">
					<div id="inline">
						<p class="ovol blue" style="padding: 5px 10px;"><fmt:message key="dashboard_p_compileSuccess"/></p>
						<p class="ovol red" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileFail"/></p>
						<p class="ovol gray" style="padding: 5px 10px; margin-left: 5px;"><fmt:message key="dashboard_p_compileNotYet"/></p>
						<a href="#" id="goToJenkins" class="btn btn-default"><fmt:message key="stuDashboard_card_goToJenkins"/></a>
					</div>
					<table class="table table-striped">
						<thead>
						</thead>
					</table>
				</div>
			</div>
			<div class="card" style="margin-top: 30px">
		        <h4 id="Student Projects" class="card-header">
		        	<i class="fa fa-table" aria-hidden="true"></i>&nbsp; 
		        		<fmt:message key="stuDashboard_card_statisticChart"/>
		        </h4>
		        <div class="card-block">
					
				</div>
			</div>
		</div>
		</main>
	</div>
	<div id="gotop"><i class="fa fa-chevron-up" aria-hidden="true"></i></div>
</body>
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