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
	        <h1 style="margin-top: 30px; margin-bottom: 20px;">D0240522_OOP-HW1</h1>
		        <!-- ---------------------------- Student Project ------------------------------- -->
		        <div class="card">
		        	<h4 id="Student Projects" class="card-header"><i class="fa fa-table" aria-hidden="true"></i>&nbsp; Project</h4>
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
									<th width="15%">Commit</th>
									<th width="15%">Light</th>
									<th width="15%">Date</th>
									<th>Commit Message</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>1</td>
									<td><p class="circle gray"></p></td>
									<td>9/25</td>
									<td>Teacher Commit</td>
								</tr>
								<tr>
									<td>2</td>
									<td><p class="circle red"></p></td>
									<td>9/25</td>
									<td>First Commit</td>
								</tr>
								<tr>
									<td>3</td>
									<td><p class="circle orange"></p></td>
									<td>9/27</td>
									<td>Add a table</td>
								</tr>
								<tr>
									<td>4</td>
									<td><p class="circle gold"></p></td>
									<td>9/28</td>
									<td>Write the JUnit test</td>
								</tr>
								<tr>
									<td>5</td>
									<td><p class="circle blue"></p></td>
									<td>9/30</td>
									<td>Finish</td>
								</tr>
							</tbody>
						</table>
		        	</div>
		        </div>
		        <!-- ---------------------------- Student Project ------------------------------- -->
		        
				<br><br>
	        </div>
        </main>
<!-- ------------------------ main -------------------------------------- -->
      </div>
</body>
</html>