<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "dashboard");
%>

<%@ include file="language.jsp" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<style type="text/css">
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
		html, body{
			height: 100%;
		}
		#allProject {
		 margin: 10px 0px 0px 0px;
		}
		
		#sidebar {
			height: 100%;
			background-color: #444;
			color: white;
			margin: -1px;
		}
		#sidebar a{
			color: white;
		}
		#sidebar button{
			color: white;
			background: none;
		}
		
		#inline {
		    margin: 20px;
		}
		
		#inline p {
		    display: inline;
		}
		.ovol {
			border-radius: 5px;
			height: 50px;
            font-weight: bold;
            width: 120px;
            color: white;
            text-align: center;
		}
		.circle {
			border-radius: 30px;
			height: 30px;
            font-weight: bold;
            width: 30px;
            color: white;
            text-align: center;
		}
		.red {
			background: #e52424;
		}
		.blue {
			background: #5fa7e8;
		}
		.gray {
			background: #878787;
		}
		.orange {
			background: #FF5809;
		}
		.green {
			background: #32CD32;
		}
		.gold{
			background: #FFD700;
		}
		.circle a {
			color: #fff;
		}
	</style>

	<link rel="shortcut icon" href="img/favicon.ico"/>
	<link rel="bookmark" href="img/favicon.ico"/>
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>
	<script>
	$(document).ready(function() {
		$("form").submit(function(evt) {
			evt.preventDefault();
			var formData = new FormData($(this)[0]);
			$.ajax({
				url : 'webapi/project2/delete',
				type : 'POST',
				data : formData,
				async : true,
				cache : false,
				contentType : false,
				enctype : 'multipart/form-data',
				processData : false,
				success : function(response) {
					alert("uploaded!");
					top.location.href = "../ProgEdu/deleteAssignment.jsp";
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
	
	<div class="container" style="margin-top: 30px; width: 1140px;">
		<div class="card">
			<div class="card-header">
				<h4 ><strong><fmt:message key="deleteAssignment_h4"/></strong></h4>
			</div>
  			<div class="card-block" style="padding: 20px 20px 20px 20px;">
    			 <form id="upload" name="upload" style="margin-top: 10px;">
					<div class="form-group">
						<label for="Hw_Name"><h4><i class="fa fa-minus" aria-hidden="true"></i>&nbsp; <fmt:message key="deleteAssignment_assignmentName"/></h4></label>
						<input id="Hw_Name" type="text" class="form-control" name="Hw_Name" required="required" placeholder="eg. OOP-HW1"/>
					</div>
					<div class="form-group">
						<p><fmt:message key="deleteAssignment_attention"/></p>
						<button type="submit" class="btn btn-default" style="background-color:#F5F5F5; color: #292b2c; border-color: #ccc" onclick="load();"><fmt:message key="deleteAssignment_sendButton"/></button>
					</div>
				</form>
  			</div>
		</div>
	</div>
</body>
</html>