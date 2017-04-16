<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@ page import="conn.Conn,conn.HttpConnect,conn.Language"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.Locale" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "teacherManageHw");
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
	
	<script src="http://js.nicedit.com/nicEdit-latest.js" type="text/javascript"></script>
	<script type="text/javascript">bkLib.onDomLoaded(nicEditors.allTextAreas);</script>			
	
	<script type="text/javascript">
		function handleClick(cb, divId){
			var o=document.getElementById(divId);
			if(cb.checked){
				o.style.display='';
			}else{
				o.style.display='none';
			}
		}
	</script>
	<script>
	$(document).ready(function() {
		$("form").submit(function(evt) {
			evt.preventDefault();
			var formData = new FormData($(this)[0]);
			$.ajax({
				url : 'webapi/project/create',
				type : 'POST',
				data : formData,
				async : false,
				cache : false,
				contentType : false,
				enctype : 'multipart/form-data',
				processData : false,
				success : function(response) {
					alert("uploaded!");
					top.location.href = "../testWeb2/assignmentManagement.jsp";
				}, 
				error : function(response) {
					alert("failed!");
				}
			});
			return false;
		});
	});
	</script>
	<title>ProgEdu</title>
	
</head>
<body>
	<!-- 設定語言 -->
	<fmt:setBundle basename = "<%=basename %>"/>
	
	<%@ include file="header.jsp" %>
	
	<div class="container">
		<form class="form-signin">
			<div>
				<div class="login-panel panel panel-default">
					<div class="panel-heading">
						<h3><fmt:message key="teacherManageHW_h3_distributeHW"/></h3>
					</div>

					<div class="panel-body">
						<div class="col-md-4">
							<div class="form-group">
								<label for="Hw_Name"><fmt:message key="teacherManageHW_label_hwName"/></label>
								<input id="Hw_Name" type="text" class="form-control" name="Hw_Name" required="required" placeholder="ex. OOP-HW1"/>
							</div>				
							
							<!-- ------------------------checkbox display------------------------------- -->
							<div class="form-group">
								<label for="checkbox">
									<input type="checkbox" id="checkbox" onclick='handleClick(this, "example")'><fmt:message key="teacherManageHW_input_ifHasExample"/>
								</label>
							</div>
							<div style="display:none" id="example">
								<div class = "form-group">
									<a href="MvnQuickStart.zip" class="btn btn-default" id="mvn_download"><fmt:message key="teacherManageHW_a_downloadMaven"/></a>
									<a href="JavacQuickStart.zip" class="btn btn-default" id="java_download"><fmt:message key="teacherManageHW_a_downloadJavac"/></a>
								</div>
								<div class="form-group">
									<label for="fileRadio"><fmt:message key="teacherManageHW_label_zipradio"/></label>
									<label class="radio-inline"><input type="radio" name="fileRadio" value="Maven">Maven</label>
									<label class="radio-inline"><input type="radio" name="fileRadio" value="Javac">Javac</label>
								</div>
								<div class = "form-group">
									
									<label for="file"><fmt:message key="teacherManageHW_label_uploadZip"/></label>
									<input type="file" accept=".zip" name="file" size="50" width="48"/>
								</div>
							</div>
							<!-- ------------------------------------------------------- -->
					
							
							<div class="form-group">
								<label for="Hw_README"><fmt:message key="teacherManageHW_label_hwReadme"/></label>
								<textarea id="Hw_README" cols="100" rows="20" name="Hw_README"></textarea>
							</div>
							
							<div class="form-group">
								<button type="submit" class="btn btn-default"><fmt:message key="teacherManageHW_button_send"/></button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
		
	</div>
	
</body>
</html>