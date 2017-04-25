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

<%
	//抓當地語言
	Locale locale = request.getLocale();
	String country = locale.getCountry();
	String localLan = locale.getLanguage();
	
	String finalLan = localLan;
	String reqLan = request.getParameter("lang");
	String sesLan = null;
	
	if(reqLan == null || reqLan.trim().equals("")) { 
		// 如果request裡沒有值
		System.out.println("no request");
		if(session.getAttribute("language") == null || session.getAttribute("language").toString().equals("")){
			// 如果session裡沒有值
			System.out.println("no session");
			finalLan = localLan;
		}else{
			// session裡有值
			System.out.println("has session");
			sesLan = session.getAttribute("language").toString();
			finalLan = sesLan;
		}
    } else{
    	// request裡有值  優先考慮request裡的值
    	System.out.println("has request");
    	finalLan = reqLan;
    }
	
	Language language = new Language();
	String basename = language.getBaseName(finalLan);
	System.out.println("finalLan : " + finalLan);
	System.out.println("basename : " + basename);
%>

<c:url value="index.jsp" var="displayLan">
<c:param name="Language" value="tw" />
</c:url>

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


<title>ProgEdu Login</title>
</head>
<body>
	<!-- 設定語言 -->
	<fmt:setBundle basename = "<%=basename %>"/>
	
	<div class="container" style="width:300px;height:60px">
        
		<form class="form-signin" method="post" action="AfterEnter">
		
			<h2 class="form-signin-heading"><fmt:message key="index_h2_plzSignIn"/></h2>
			<input type="hidden" name="grant_type" value="password">
			
			<div class="form-group row">
				<label for="inputUsername" class="col-2 col-form-label"><fmt:message key="index_label_userName"/></label> 
				<div class="col-sm-10">
					<input type="text" id="inputUsername" name="username" class="form-control" placeholder="Username" required autofocus> 
				</div>
			</div>
					
			<div class="form-group row">
				<label for="inputPassword" class="col-2 col-form-label"><fmt:message key="index_label_password"/></label> 
				<br>
				<div class="col-sm-10">
					<input type="password" id="inputPassword" name="password" class="form-control" placeholder="Password" required>
				</div>
			</div>
			<input type="hidden" name="language" value="<%=finalLan%>">
			<br>
				
			<button class="btn btn-lg btn-primary btn-block" type="submit"><fmt:message key = "index_btn_signIn"/></button>

		</form>
	</div>
	<!-- /container -->

</body>
</html>