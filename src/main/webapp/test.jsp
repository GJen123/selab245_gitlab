<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
    <%@ include file="language.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.1.1.slim.min.js" integrity="sha384-A7FZj7v+d/sdmMqp/nOQwliLvUsJfDHW+k9Omg/a/EheAdgtzNs3hpfag6Ed950n" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js" integrity="sha384-DztdAPBWPRXSA/3eYEEUWrWCy7G5KFbe8fFjk5JAIxUYHKkDx6Qin1DkWx51bBrb" crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js" integrity="sha384-vBWWzlZJ8ea9aCX4pEW3rVHjgjt7zpkNpZk+02D9phzyeVkE+jo0ieGizqPLForn" crossorigin="anonymous"></script>
<title>Insert title here</title>
</head>
<body>
  <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="dashboard.jsp">ProgEdu</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="dashboard.jsp"><fmt:message key="top_navbar_dashboard"/></a></li>
                <li><a href="teacherGroup.jsp"><fmt:message key="top_navbar_groupProject"/></a></li>
                <li class="dropdown">
                	<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                		<fmt:message key="top_navbar_manage"/> <span class="caret"></span></a>
                	<ul class="dropdown-menu">
                 	<li><a href="studentManagement.jsp"><fmt:message key="top_navbar_manageStudent"/></a></li>
                 	<li><a href="assignmentManagement.jsp"><fmt:message key="top_navbar_manageHW"/></a></li>
                 	<li><a href="groupManagement.jsp"><fmt:message key="top_navbar_manageGroup"/></a></li>
                	</ul>
                </li>
            </ul>
            
            <ul class="nav navbar-nav navbar-right">
            	<li class="dropdown">
                	<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                		<fmt:message key="top_navbar_language"/> <span class="caret"></span></a>
                	<ul class="dropdown-menu" >
                 	<li id="English" value="en"><a href="ChooseLanguage?language=en"><fmt:message key="top_navbar_lanEnglish"/></a></li>
                 	<li id="Chinese" value="zh"><a href="ChooseLanguage?language=zh"><fmt:message key="top_navbar_lanChinese"/></a></li>
                	</ul>
                </li>
    			<li><a href="memberLogOut.jsp" id="loginLink"><fmt:message key="top_navbar_signOut"/></a></li>
			</ul>
        </div>
    </div>
    
    <nav class="navbar navbar-inverse bg-inverse">
    	<div class="container">
    		<div class="navbar-header">
    			<button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
	                <span class="icon-bar"></span>
	                <span class="icon-bar"></span>
	                <span class="icon-bar"></span>
	                <span class="icon-bar"></span>
	            </button>
	            <a class="navbar-brand" href="dashboard.jsp">ProgEdu</a>
    		</div>
    		<div class="collapse navbar-collapse">
    			<ul class="nav navbar-nav">
    				<li class="nav-item active"><a href="dashboard.jsp"><fmt:message key="top_navbar_dashboard"/></a></li>
    			</ul>
    		</div>
    	</div>
    </nav>
    
		<nav class="navbar navbar-toggleable-md navbar-light bg-faded">
			<div class="container">
			
		  <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
		    <span class="navbar-toggler-icon"></span>
		  </button>
		  <a class="navbar-brand" href="#">Navbar</a>
		
		  <div class="collapse navbar-collapse" id="navbarSupportedContent">
		    <ul class="navbar-nav mr-auto">
		      <li class="nav-item active">
		        <a class="nav-link" href="#">Home <span class="sr-only">(current)</span></a>
		      </li>
		      <li class="nav-item">
		        <a class="nav-link" href="#">Link</a>
		      </li>
		      <li class="nav-item">
		        <a class="nav-link disabled" href="#">Disabled</a>
		      </li>
		    </ul>
		    <form class="form-inline my-2 my-lg-0">
		      <input class="form-control mr-sm-2" type="text" placeholder="Search">
		      <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
		    </form>
		  </div>
		  </div>
		</nav>

</body>
</html>