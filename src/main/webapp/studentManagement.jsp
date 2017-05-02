<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	//if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
	//	response.sendRedirect("index.jsp");
	//}
	session.putValue("page", "teacherManageStudent");
%>

<%@ include file="language.jsp"%>

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
	
	<title>ProgEdu</title>
	
	<script>
	$(document).ready(function() {
		$("form").submit(function(evt) {
			evt.preventDefault();
			var formData = new FormData($(this)[0]);
			$.ajax({
				url : 'webapi/user/upload',
				type : 'POST',
				data : formData,
				async : false,
				cache : false,
				contentType : false,
				enctype : 'multipart/form-data',
				processData : false,
				success : function(response) {
					alert("uploaded!");
					top.location.href = "../ProgEdu/studentManagement.jsp";
				}, 
				error : function(response) {
					alert("failed!");
				}
			});
			return false;
		});
	});
	</script>
</head>

<body>
	<!-- 設定語言 -->
	<fmt:setBundle basename = "<%=basename %>"/>
	
	<%@ include file="header.jsp" %>

	<div class="container">
		<div>
			<div class="login-panel panel panel-default">
				<div class="panel-heading">
					<h3><fmt:message key="teacherManageStudent_h3_newAllStudent"/></h3>
				</div>

				<div class="panel-body">
					<div class="form-group">
						<form>
							<h4><fmt:message key="teacherManageStudent_h4_uploadStudent"/></h4>
							Select File to Upload:<input type="file" name="file">
							<br> <input type="submit" value="Upload">
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>

</body>
</html>