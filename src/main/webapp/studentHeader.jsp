<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
 	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	 
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
	<script src="https://code.jquery.com/jquery-3.2.1.js" integrity="sha256-DZAnKJ/6XZ9si04Hgrsxu/8s717jcIzLy3oi35EouyE=" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js" integrity="sha384-b/U6ypiBEHpOf/4+1nzFpr53nxSS+GLCkfwBdFNTxtclqqenISfwAzpKaMNFNmj4" crossorigin="anonymous"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/js/bootstrap.min.js" integrity="sha384-h0AbiXch4ZDo7tp9hKZ4TsHbi047NrKGLO3SEJAg45jXxnGIfYzk4Si90RDIqNm1" crossorigin="anonymous"></script>
	<link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
	<link href="font-awesome/progedu.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet"
	  href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
	 
	<style>
		body{
			padding-top: 50px;
			font-family: Microsoft JhengHei;
		}
		.container{
			padding-bottom: 30px;
		}
		button{
			font-family: -apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif;
		}
	</style>
	
	<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
    <a class="navbar-brand" href="studentDashboard.jsp">ProgEdu</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse"
      data-target="#navbarsExampleDefault" aria-controls="navbarsExampleDefault"
      aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarsExampleDefault">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item active"><a class="nav-link" href="studentDashboard.jsp"><fmt:message key="top_navbar_dashboard"/> <span
            class="sr-only">(current)</span></a>
        </li>
      </ul>
      <ul class="navbar-nav navbar-toggler-right">
        <li class="nav-item dropdown active"><a class="nav-link dropdown-toggle" href=""
          id="language" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fa fa-language" aria-hidden="true"></i>&nbsp;<fmt:message key="top_navbar_language"/></a>
          <div class="dropdown-menu" aria-labelledby="language">
            <a class="dropdown-item" href="studentDashboard.jsp?lang=zh"><i class="fa fa-globe" aria-hidden="true"></i>&nbsp;<fmt:message key="top_navbar_lanChinese"/></a>
          	<a class="dropdown-item" href="studentDashboard.jsp?lang=en"><i class="fa fa-globe" aria-hidden="true"></i>&nbsp;<fmt:message key="top_navbar_lanEnglish"/></a>
          </div></li>
        <li class="nav-item dropdown active"><a class="nav-link dropdown-toggle" href=""
          id="setting" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <fmt:message key="top_navbar_Setting"/></a>
          <div class="dropdown-menu" aria-labelledby="setting">
            <a class="dropdown-item" href="#"> 修改密碼</a>
          	<a class="dropdown-item" href="#"> 登出</a>
          </div></li>
        <li class="nav-item active"><a class="nav-link" href="memberLogOut.jsp"> <fmt:message key="top_navbar_signOut"/> <i
            class="fa fa-sign-out" aria-hidden="true"></i></a></li>
      </ul>
    </div>
  </nav>