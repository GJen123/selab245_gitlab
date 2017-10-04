<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.HttpConnect, fcu.selab.progedu.conn.Language"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="java.util.ArrayList" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ include file="language.jsp" %>

<c:url value="index.jsp" var="displayLan">
<c:param name="Language" value="tw" />
</c:url>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
		<script src="https://code.jquery.com/jquery-3.2.1.js" integrity="sha256-DZAnKJ/6XZ9si04Hgrsxu/8s717jcIzLy3oi35EouyE=" crossorigin="anonymous"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js" integrity="sha384-b/U6ypiBEHpOf/4+1nzFpr53nxSS+GLCkfwBdFNTxtclqqenISfwAzpKaMNFNmj4" crossorigin="anonymous"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/js/bootstrap.min.js" integrity="sha384-h0AbiXch4ZDo7tp9hKZ4TsHbi047NrKGLO3SEJAg45jXxnGIfYzk4Si90RDIqNm1" crossorigin="anonymous"></script>
		
		<link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
		<link href="font-awesome/progedu.css" rel="stylesheet" type="text/css">
		<link rel="stylesheet"
		  href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
	
	
		<title>ProgEdu Login</title>
		
		 <script type="text/javascript">
			var Msg ='<%=session.getAttribute("enterError")%>';
			    if (Msg != "null") {
				 function alertName(){
				 	alert("Enter Error! Your username or password is incorrect");
				 } 
			 }
		 </script> 
		 <script type="text/javascript"> window.onload = alertName; </script>
		 <link rel="shortcut icon" href="img/favicon.ico"/>
		 <link rel="bookmark" href="img/favicon.ico"/>
		 
		 <style>
		 	body {
		 		font-family: Microsoft JhengHei;
		 	}
		 	.container {
		 		width: 400px;
		 		margin-top: 10px;
		 	}
		 	hr {
		 		width: 60%;
		 		color: #fff;
		 	}
		 	label{
				font-size:20px;
				font-weight: bold;
			}
		 	#form{
		 		background-color: white;
		 		padding: 20px;
		 		box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
		 		margin: 0 auto;
		 	}
		 	.form-group {
		 		margin: 10px;
		 	}
		 	input {
		 		font-size: 11px;
		 		padding-top: 3px;
		 	}
		 	#submit{
		 		font-weight: bold;
		 	}
		 </style>
		 
	</head>
	<body style="background-color:#d3d3d3;">
		
		<div class="container">
			<h2 class="form-signin-heading" style="text-align:center; font-size: 60px; font-weight: 400;">ProgEdu</h2>
			<hr>
	        <div id="form">
				<form class="form-signin" method="post" action="AfterEnter">
					<input type="hidden" name="grant_type" value="password">
					
					<div class="form-group row">
						<label for="inputUsername"><fmt:message key="index_label_userName"/></label> 
						<div class="input-group">
							<span class="input-group-addon"><i class="fa fa-user" aria-hidden="true"></i></span>
							<input type="text" id="inputUsername" name="username" class="form-control" placeholder="Username" required autofocus> 
						</div>
					</div>
							
					<div class="form-group row">
						<label for="inputPassword"><fmt:message key="index_label_password"/></label> 
						<br>
						<div class="input-group">
							<span class="input-group-addon"><i class="fa fa-lock fa-lg" aria-hidden="true"></i></span>
							<input type="password" id="inputPassword" name="password" class="form-control" placeholder="Password" required>
						</div>
					</div>
					
					<input type="hidden" name="language" value="<%=finalLan%>">
						
					<div class="form-group row" style="margin-top: 30px;">
						<button class="btn btn-lg btn-primary btn-block" type="submit" id="submit"><fmt:message key = "index_btn_signIn"/></button>
					</div>
				</form>
			</div>
		</div>
		<!-- /container -->
	
	</body>
</html>
