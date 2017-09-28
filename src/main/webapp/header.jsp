<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
    
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js" integrity="sha384-b/U6ypiBEHpOf/4+1nzFpr53nxSS+GLCkfwBdFNTxtclqqenISfwAzpKaMNFNmj4" crossorigin="anonymous"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/js/bootstrap.min.js" integrity="sha384-h0AbiXch4ZDo7tp9hKZ4TsHbi047NrKGLO3SEJAg45jXxnGIfYzk4Si90RDIqNm1" crossorigin="anonymous"></script>
	
	<link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	<link href="font-awesome/progedu.css" rel="stylesheet" type="text/css">
	<script src="https://code.highcharts.com/highcharts.js"></script>
	<script src="https://code.highcharts.com/modules/data.js"></script>
	<script src="https://code.highcharts.com/modules/exporting.js"></script>
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