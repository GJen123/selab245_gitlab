<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "studentManagement");
%>

<%@ include file="language.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<style>
		/* Center the loader */
		#loader {
 			position: absolute;
  			left: 50%;
  			top: 50%;
  			z-index: 1;
 			width: 150px;
  			height: 150px;
  			margin: -75px 0 0 -75px;
  			border: 16px solid #f3f3f3;
  			border-radius: 50%;
  			border-top: 16px solid #3498db;
  			width: 120px;
  			height: 120px;
  			-webkit-animation: spin 2s linear infinite;
  			animation: spin 2s linear infinite;
			}

			@-webkit-keyframes spin {
  				0% { -webkit-transform: rotate(0deg); }
  				100% { -webkit-transform: rotate(360deg); }
			}

			@keyframes spin {
			  	0% { transform: rotate(0deg); }
  				100% { transform: rotate(360deg); }
			}
			
			/* Add animation to "page content" */
			.animate-bottom {
 				position: relative;
 		 		-webkit-animation-name: animatebottom;
  				-webkit-animation-duration: 1s;
  				animation-name: animatebottom;
  				animation-duration: 1s
			}

			@-webkit-keyframes animatebottom {
  					from { bottom:-100px; opacity:0 } 
  				to { bottom:0px; opacity:1 }
			}

			@keyframes animatebottom { 
  				from{ bottom:-100px; opacity:0 } 
  				to{ bottom:0; opacity:1 }
			}
	</style>
	<title>ProgEdu</title>
</head>

<body  style="background-color:#F5F5F5;">
	
	<%@ include file="header.jsp" %>
	
<script>
	$(document).ready(function() {
		$("form").submit(function(evt) {
			evt.preventDefault();
			var formData = new FormData($(this)[0]);
			$.ajax({
				url : 'webapi/user/upload',
				type : 'POST',
				data : formData,
				async : true,
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
	
	<script type="text/javascript">
		function load() {
			document.getElementById("loader").style.display = "block";
		}
	</script>
	<div id="loader" style="display: none"></div>
	
	<div>
		<div class="container-fluid" style="margin-top: 20px; width: 1140px;">
			<br>
			<div>
				<div class="card">
					<h4 class="card-header"><strong><fmt:message key="teacherManageStudent_h3_newAllStudent"/></strong></h4>
	
					<div class="card-block" style="padding: 20px 20px 20px 20px;">
						<div class="form-group">
							<form>
								<h5><i class="fa fa-file-excel-o" aria-hidden="true"></i>&nbsp; <fmt:message key="teacherManageStudent_h4_uploadStudent"/></h5>
								Select File to Upload:<input type="file" name="file" style="margin-left: 10px;">
								<br> <input type="submit" value="Upload" onclick="load();">
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>