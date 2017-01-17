<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>

<title>Insert title here</title>
</head>
<body>
	
	<div class="container">

      <form class="form-signin" action="test.jsp">
        <h2 class="form-signin-heading">Please sign in</h2>
        <label for="inputEmail" class="sr-only">Email address</label>
        <input type="email" id="inputEmail" class="form-control" placeholder="Email address" required autofocus>
        <label for="inputPassword" class="sr-only">Password</label>
        <input type="password" id="inputPassword" class="form-control" placeholder="Password" required>
        <div class="checkbox">
          <label>
            <input type="checkbox" value="remember-me"> Remember me
          </label>
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
      </form>

    </div> <!-- /container -->
 	<br><br><br>
 	<%
 		String client_id="9b645fb27f29a85824e0d54b6153cb335f344ceed90566f85901aefe2bf0ef05";
        String redirect_uri="http://localhost:8080/testWeb2/test.jsp/callback";
        String response_type="code";
        String state="123";
 		String oauth2_url="http://140.134.26.71:20080/oauth/authorize?client_id="+
        	client_id+"&redirect_uri="+redirect_uri+"&response_type="+response_type+"&state="+state;
 	%>
 	<form method="post" action="<%=oauth2_url%>">
 		<input type="hidden" name="grant_type" value="password">
 		<strong>username</strong>
 		<input type="text" name="username">
 		<br>
 		<strong>password</strong>
 		<input type="text" name="password">
 		<br>
 		<button type="submit">Sign in</button>
 	
 	</form>
 	<br>
 	<button type="button"><a href="<%=oauth2_url%>">abc</button>
 
</body>
</html>