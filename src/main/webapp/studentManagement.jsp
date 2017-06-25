<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "teacherManageStudent");
%>

<%@ include file="language.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>ProgEdu</title>
</head>

<body  style="background-color:#F5F5F5;">
	<!-- 設定語言 -->
	<fmt:setBundle basename = "<%=basename %>"/>
	
	<%@ include file="header.jsp" %>
	<div>
		<div class="container" style="margin-top: 20px; width: 1140px;">
			<br>
			<div>
				<div class="card">
					<h4 class="card-header"><strong><fmt:message key="teacherManageStudent_h3_newAllStudent"/></strong></h4>
	
					<div class="card-block">
						<div class="form-group">
							<form>
								<h5><i class="fa fa-file-excel-o" aria-hidden="true"></i>&nbsp; <fmt:message key="teacherManageStudent_h4_uploadStudent"/></h5>
								Select File to Upload:<input type="file" name="file" style="margin-left: 10px;">
								<br> <input type="submit" value="Upload">
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
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
</html>