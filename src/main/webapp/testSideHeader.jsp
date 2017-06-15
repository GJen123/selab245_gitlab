<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="language.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">
	<script src="https://code.jquery.com/jquery-3.1.1.slim.min.js" integrity="sha384-A7FZj7v+d/sdmMqp/nOQwliLvUsJfDHW+k9Omg/a/EheAdgtzNs3hpfag6Ed950n" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js" integrity="sha384-DztdAPBWPRXSA/3eYEEUWrWCy7G5KFbe8fFjk5JAIxUYHKkDx6Qin1DkWx51bBrb" crossorigin="anonymous"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js" integrity="sha384-vBWWzlZJ8ea9aCX4pEW3rVHjgjt7zpkNpZk+02D9phzyeVkE+jo0ieGizqPLForn" crossorigin="anonymous"></script>
	<title>Insert title here</title>
	
	<style type="text/css">
		#white a {
			color: white;
		}
	</style>
	
</head>
<body>
	<div id="wrapper">
		<nav class="navbar navbar-toggleable-md navbar-inverse bg-inverse" role="navigation">
			<button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
	    		<span class="navbar-toggler-icon"></span>
	  		</button>
	  		<a class="navbar-brand" href="#">ProgEdu</a>
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
	    		<li class="nav-item"><a class="nav-link" href="memberLogOut.jsp" id="loginLink"><fmt:message key="top_navbar_signOut"/></a></li>
	    	</ul>
		</nav>
		<nav class="col-sm-3 col-md-2 hidden-xs-down bg-inverse sidebar">
		  <!-- Navbar content -->
		  <ul class="navbar-nav" id="white">
            <li class="nav-item"><a class="nav-link" href="dashboard.jsp"><fmt:message key="top_navbar_dashboard"/></a></li>
            <li class="nav-item"><a class="nav-link" href="teacherGroup.jsp"><fmt:message key="top_navbar_groupProject"/></a></li>
      		<li class="nav-item dropdown">
        		<a class="nav-link dropdown-toggle" href="http://example.com" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          			<fmt:message key="top_navbar_manage"/>
        		</a>
        		<div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
          			<a class="dropdown-item" href="studentManagement.jsp"><fmt:message key="top_navbar_manageStudent"/></a>
          			<a class="dropdown-item" href="assignmentManagement.jsp"><fmt:message key="top_navbar_manageHW"/></a>
          			<a class="dropdown-item" href="groupManagement.jsp"><fmt:message key="top_navbar_manageGroup"/></a>
        		</div>
      		</li>
      		<li>
                <a href="javascript:;" data-toggle="collapse" data-target="#demo"><i class="fa fa-fw fa-arrows-v"></i> <fmt:message key="top_navbar_manage"/>↓ <i class="fa fa-fw fa-caret-down"></i></a>
                <ul id="demo" class="collapse">
                    <li><a class="dropdown-item" href="studentManagement.jsp"><fmt:message key="top_navbar_manageStudent"/></a></li>
                    <li><a class="dropdown-item" href="assignmentManagement.jsp"><fmt:message key="top_navbar_manageHW"/></a></li>
                    <li><a class="dropdown-item" href="groupManagement.jsp"><fmt:message key="top_navbar_manageGroup"/></a></li>
                </ul>
            </li>
          </ul>
		</nav>
	</div>
</body>
</html>