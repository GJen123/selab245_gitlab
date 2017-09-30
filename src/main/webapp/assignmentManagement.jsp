<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn,fcu.selab.progedu.conn.HttpConnect,fcu.selab.progedu.conn.Language"%>
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
	session.putValue("page", "assignmentManagement");
%>

<%@ include file="language.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
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
<body style="background-color:#F5F5F5;">
	
	<%@ include file="header.jsp" %>
	
	<script>
	$(document).ready(function() {
		$("form").submit(function(evt) {
			evt.preventDefault();
			var formData = new FormData($(this)[0]);
			$.ajax({
				url : 'webapi/project2/create',
				type : 'POST',
				data : formData,
				async : true,
				cache : false,
				contentType : false,
				enctype : 'multipart/form-data',
				processData : false,
				success : function(response) {
					alert("uploaded!");
					top.location.href = "../ProgEdu/assignmentManagement.jsp";
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
		<div class="container" style="margin-top: 20px; width: 1140px;">
			<br>
			<form class="form-signin">
				<div class="card">
					<h4 class="card-header"><strong><fmt:message key="teacherManageHW_h3_distributeHW"/></strong></h4>
	
					<div class="card-block" style="padding: 20px 20px 20px 20px;">
						<div class="col-md-4">
							<div class="form-group">
								<label for="Hw_Name"><h4><i class="fa fa-minus" aria-hidden="true"></i>&nbsp; <fmt:message key="teacherManageHW_label_hwName"/></h4></label>
								<input id="Hw_Name" type="text" class="form-control" name="Hw_Name" required="required" placeholder="eg. OOP-HW1"/>
							</div>
							<div class="form-group">
								<label for="Hw_Deadline"><h4><i class="fa fa-minus" aria-hidden="true"></i>&nbsp; <fmt:message key="teacherManageHW_label_hwDeadline"/></h4></label>
								<input id="Hw_Deadline" type="text" class="form-control" name="Hw_Deadline" required="required" placeholder="eg. 2017/01/01 23:59"/>
							</div>				
							
							<!-- ------------------------checkbox display------------------------------- -->
							<div class="form-group">
								<label for="checkbox">
									<input type="checkbox" id="checkbox" onclick='handleClick(this, "example")'><fmt:message key="teacherManageHW_input_ifHasExample"/>
								</label>
							</div>
							<div style="display:none" id="example">
								<div class = "form-group">
									<a href="MvnQuickStart.zip" class="btn btn-secondary" style="background-color:#F5F5F5;" id="mvn_download"><fmt:message key="teacherManageHW_a_downloadMaven"/></a>
									<a href="JavacQuickStart.zip" class="btn btn-secondary" style="background-color:#F5F5F5;" id="java_download"><fmt:message key="teacherManageHW_a_downloadJavac"/></a>
								</div>
								<div class="form-group">
									<label for="fileRadio"><fmt:message key="teacherManageHW_label_zipradio"/></label>
									<label class="radio-inline"><input type="radio" name="fileRadio" value="Maven">Maven</label>
									<label class="radio-inline"><input type="radio" name="fileRadio" value="Javac">Javac</label>
								</div>
								<div class="form-group">
									
									<label for="file"><fmt:message key="teacherManageHW_label_uploadZip"/></label>
									<input type="file" accept=".zip" name="file" size="50" width="48"/>
								</div>
							</div>
							<!-- ------------------------------------------------------- -->
					
							
							<div class="form-group">
								<label for="Hw_README"><h4><i class="fa fa-minus" aria-hidden="true"></i>&nbsp; <fmt:message key="teacherManageHW_label_hwReadme"/></h4></label>
								<textarea id="Hw_README" cols="100" rows="20" name="Hw_README"></textarea>
							</div>
							
							<div class="form-group">
								<button type="submit" class="btn btn-default" style="background-color:#F5F5F5; color: #292b2c; border-color: #ccc" onclick="load();nicEditors.findEditor('Hw_README').saveContent();"><fmt:message key="teacherManageHW_button_send"/></button>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
	
</body>
</html>