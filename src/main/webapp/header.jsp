<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js" integrity="sha384-DztdAPBWPRXSA/3eYEEUWrWCy7G5KFbe8fFjk5JAIxUYHKkDx6Qin1DkWx51bBrb" crossorigin="anonymous"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js" integrity="sha384-vBWWzlZJ8ea9aCX4pEW3rVHjgjt7zpkNpZk+02D9phzyeVkE+jo0ieGizqPLForn" crossorigin="anonymous"></script>
	<link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	<link href="font-awesome/progedu.css" rel="stylesheet" type="text/css">
	<style>
		body{
			padding-top: 50px;
		}
		.container{
			padding-bottom: 30px;
		}
		button{
			font-family: -apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif;
		}
	</style>
<body id="page-top">
	<nav class="navbar fixed-top navbar-toggleable-md navbar-inverse bg-inverse">
	  		<button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
	    		<span class="navbar-toggler-icon"></span>
	  		</button>
	  		<a class="navbar-brand" href="dashboard.jsp">ProgEdu</a>
	  		<div class="collapse navbar-collapse" id="navbarNavDropdown">
		    	<ul class="navbar-nav">
		    		<li class="nav-item"><a class="nav-link" href="#page-top"><fmt:message key="top_navbar_backToTop"/></a></li>
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
	</nav>
</body>