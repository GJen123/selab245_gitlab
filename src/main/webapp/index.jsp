<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="utf-8"%>
<%@ page import="conn.conn,conn.httpConnect"%>
<%@ page import="java.util.List" import="java.util.ArrayList"
	import="org.gitlab.api.GitlabAPI" import="org.gitlab.api.models.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<!-- jQuery library -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>


<title>GitlabEdu Login</title>
</head>
<body>

	<div class="container" style="width:300px;height:60px">

		<form class="form-signin" method="post" action="afterEnter">
			<h2 class="form-signin-heading">Please sign in</h2>
			<input type="hidden" name="grant_type" value="password">
			<label for="inputUsername" class="sr-only">Username</label> <input
				type="text" id="inputUsername" name="username" class="form-control"
				placeholder="Username" required autofocus> 
			<label for="inputPassword" class="sr-only">Password</label> <input
				type="password" id="inputPassword" name="password" class="form-control"
				placeholder="Password" required>
			<!-- <div class="checkbox">
				<label> <input type="checkbox" value="remember-me">
					Remember me
				</label>
			</div> -->
			<button class="btn btn-lg btn-primary btn-block" type="submit"
				onclick="">Sign
				in</button>

		</form>
	</div>
	<!-- /container -->
	


</body>
</html>