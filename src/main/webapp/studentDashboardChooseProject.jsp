<%@ page language="java" contentType="text/html; charset=BIG5" pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.StudentConn, fcu.selab.progedu.conn.HttpConnect"%>
<%@ page import="fcu.selab.progedu.config.GitlabConfig,fcu.selab.progedu.config.CourseConfig"%>
<%@ page import="fcu.selab.progedu.config.GitlabConfig,fcu.selab.progedu.config.JenkinsConfig"%>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.gitlab.api.GitlabAPI"%>
<%@ page import="org.gitlab.api.models.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.json.JSONArray, org.json.JSONException, org.json.JSONObject" %>
<%@ page import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.db.ProjectDbManager" %>
<%@ page import="fcu.selab.progedu.data.User, fcu.selab.progedu.data.Project" %>
<%@ page import="fcu.selab.progedu.jenkins.JobStatus, java.text.SimpleDateFormat" %>
<%@ page import="fcu.selab.progedu.conn.StudentDash" %>
<%@ page import="fcu.selab.progedu.conn.StudentDashChoosePro" %> 

<%
	String private_token = null;
	if(null != session.getAttribute("private_token") && !"".equals(session.getAttribute("private_token")) ){
	  private_token = session.getAttribute("private_token").toString();
	}else{
	  response.sendRedirect("index.jsp");
	}
	session.putValue("page", "studentDashboardChooseProject");
	
	// Set the student private_token
	
	Cookie[] cookies = request.getCookies();
	Cookie cookie = null;
	if(cookies != null){
	  for(Cookie c : cookies){
	    if(c.getName().equals("private_token")){
	      cookie = c;
	      break;
	    }
	  }
	}else {
		response.sendRedirect("index.jsp");
	}
	if(cookie != null){
	  private_token = cookie.getValue();
	}else {
		response.sendRedirect("index.jsp");
	}
	
%>

<%@ include file="language.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<script src="https://code.highcharts.com/highcharts.js"></script>
		<script src="https://code.highcharts.com/modules/exporting.js"></script>
		<style type="text/css">
			body{
				overflow-x: hidden;
			}
			p {
				white-space: nowrap;
			}
			#mainTable {
				width: 100%;
				height: 100%;
			}
			#sidebar {
				height: 100%;
				background-color: #444;
				color: white; 
				margin: -1px;
				position: fixed; /* Set the navbar to fixed position */
    			top: 0;
    			z-index: 100;
			}
			.nav-link {
				color: white; 
			}
			.nav-link:hover{
				color: #33CCFF;
			}
			#overview {
			color: white;
			}
			#overview:hover{
				color: #33CCFF;
			}
			#copyTarget {
				border: 1px solid gray; 
				background-color: white; 
				border-radius: 5px; 
				padding: 5px 0px 5px 10px; 
				width: 300px;
				color: gray;
			}
			#inline {
				width: 100%;
				display: inline;
			}
			.bigcircle2 {
				border-radius: 10px;
				height: 80px;
	            width: 80px;
	            color: white;
	            text-align: center;
	            margin: 0 auto;
			}
			.bigcircle2 a{
				line-height: 40px;
			}
			
			#inline p {
				display: inline;
			}
			#main {
				background-color: #f5f5f5;
				height: 100%;
				margin-left: 200px;
				overflow-x: scroll;
				padding-top: 20px;
				width: auto;
			}
			.ovol {
				border-radius: 50px;
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
			.bigcircle {
				border-radius: 10px;
				height: 80px;
	            font-weight: bold;
	            width: 80px;
	            color: white;
	            text-align: center;
			    position: absolute;
	    		top: 100%;
	    		left: 45%;
	    		margin-right: -50%;
	    		transform: translate(-50%, -50%)
			}
			.CPF {
				background: #e52424;
			}
			.S {
				background: #5fa7e8;
			}
			.NB {
				background: #878787;
			}
			.CSF {
				background: gold;
			}
			.CTF {
				background: #32CD32;
			}
			#goToJenkins{
				float: right;
				background-color: white;
				color: #1079c9;
				border: 1px solid #1079c9;
				margin-bottom: 10px;
			}
			.center-justified {
	    		text-align: justify;
	    		-moz-text-align-last: center;
	    		text-align-last: center;
			}
			.tableActive {
				background-color: #ddd;
			}
		</style>
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
	
	<link rel="shortcut icon" href="img/favicon.ico"/>
	<link rel="bookmark" href="img/favicon.ico"/>
	<title>ProgEdu</title>
	</head>
	<body>
		<%@ include file="studentHeader.jsp"%>
	
		<%
			//To display the under html code (about some if-else)
			StudentDash stuDash = new StudentDash(private_token);
			StudentDashChoosePro stuDashChoPro = new StudentDashChoosePro();
			
			// Get the user's Gitlab project
			List<GitlabProject> stuProjects = stuDash.getStuProject();
		
			String strProjectId = request.getParameter("projectId");
			
			int projectId = -1;
			if(null == strProjectId || "".equals(strProjectId)){
				  
			}else {
				projectId = Integer.parseInt(strProjectId);
			}
			
			GitlabProject choosedProject = stuDashChoPro.getChoosedProject(stuProjects, projectId);
			
			JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
			Conn conn = Conn.getInstance();
			
			StudentConn sConn = new StudentConn(private_token);
			GitlabUser user = sConn.getUser();
			List<GitlabProject> projects = sConn.getProject();
			Collections.reverse(projects);
		%>
		
		<!-- -----sidebar----- -->
		<div id="sidebar">
			<ul class="nav flex-column" style="padding-top: 20px;">
			  <li class="nav-item" style="margin: 10px 0px 0px 15px; color: burlywood;">
			    <font size="4"><a href="studentDashboard.jsp" id="overview"><i class="fa fa-bar-chart" aria-hidden="true"></i>&nbsp; <fmt:message key="stuDashboard_li_overview"/></a></font>
			  </li>
			  <li class="nav-item" style="margin: 10px 0px 0px 15px;">
			    <font size="4"><a><i class="fa fa-minus-square-o" aria-hidden="true"> &nbsp;<fmt:message key="stuDashboard_li_assignments"/></i></a></font>
			  </li>
			  <%
				  	for(GitlabProject stuProject : stuProjects){
				  	  String href = "\"studentDashboardChooseProject.jsp?projectId=" + stuProject.getId() + "\"";
				  	  %>
				  	  	<li class="nav-item" style="margin:0px 0px 0px 30px">
						  <font size="3"><a class="nav-link" href=<%=href %>><i class="fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp; <%=stuProject.getName() %></a></font>
						</li>
				  	  <%
				  	}
			  %>
			</ul>
		</div>
		<!-- -----sidebar----- -->
		<!-- -----main----- -->
		<div class="container-fluid" id="main">
			<%
				String projectName = choosedProject.getName();
				String projectUrl = stuDashChoPro.getChoosedProjectUrl(choosedProject);
				List<String> jobColors = stuDash.getMainTableJobColor(stuProjects);
				List<String> jobCommitCounts = stuDash.getMainTableJobCommitCount(stuProjects);
				ProjectDbManager pDb = ProjectDbManager.getInstance();
				Project project = pDb.getProjectByName(projectName);
			%>
			<div style="margin: 10px 10px 10px 10px;">
				<h2 style="white-space: nowrap"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp; <%=projectName%></h2>
				<br>
				<h5 style="font-weight: 700"><fmt:message key="stuDashChooseProject_p_gitRepo"/></h5>
				<div id="inline" style="white-space: nowrap">
					<p id="copyTarget" style="padding-right: 10px;"><%=projectUrl %></p>
					<button id="copyButton" class="btn btn-dark" onclick = "copyToClipboard(document.getElementById('copyTarget'))"><i class="fa fa-clipboard" aria-hidden="true"></i></button>
				</div>
				<p><fmt:message key="stuDashChooseProject_p_cloneUrl"/></p>
				<hr>
				<h5 style="font-weight: 700"><fmt:message key="stuDashChooseProject_p_assignmentContent"/></h5>
				<p><%=project.getDescription() %></p>
				<hr>
				<h5 style="font-weight: 700"><fmt:message key="stuDashChooseProject_p_deadline"/></h5>
				<p><%=project.getDeadline() %></p>
			</div>
			
			<hr>
			
			<div class="container" style="margin: 25px 0px;">
				<div class="row">
					<div class="col-3">
						<h4 style="text-align: center;"><fmt:message key="stuDashChooseProject_h4_codeAnalysisResult"/></h4>
						<%
							String lastBuildColor = stuDashChoPro.getLastColor(user.getUsername(), projectName);
							lastBuildColor = "bigcircle2 " + lastBuildColor;
							List<Integer> buildNum = stuDashChoPro.getScmBuildCounts(user.getUsername(), projectName);
							int lastBuildNum = buildNum.size();
							if(lastBuildNum==1){
							  lastBuildColor = "bigcircle2 gray";
							}
						%>
                 				<div style="text-align: center">
		                	<div style="padding: 5px;">
		                		<h3 class="<%=lastBuildColor%>" style="width: 90px; margin: 0 auto; padding: 20px; color: white;"><a><%=lastBuildNum %></a></h3>
		                    </div>
		                </div>
					</div>
					
					<div class="col-9">
						<h4><fmt:message key="stuDashChooseProject_h4_programHistory"/></h4>
						<div style="margin: 15px 0px;">
							<%@ include file="projectLight.jsp" %>
						</div>
						
						<table class="table table-hover" style="background-color: white" id="projectList">
							<thead>
								<tr>
			                    	<th>#</th>
			                    	<th><fmt:message key="stuDashChooseProject_th_status"/></th>
			                    	<th><fmt:message key="stuDashChooseProject_th_date"/></th>
			                    	<th><fmt:message key="stuDashChooseProject_th_comment"/></th>
			                    </tr>
							</thead>
							<tbody id="projectTbody">
							<%
								int commit_count = buildNum.size();
								int i=1;
								int lastBuildMessageNum = 0;
								for(Integer num : buildNum){
								  	%>
								  	<script type="text/javascript">
										var userName = <%="'" + user.getUsername() + "'"%>
										var proName = <%="'" + projectName + "'"%>
										var date, color, message, num
										$.ajax({
											url : 'webapi/jenkins/buildDetail',
											type : 'GET',
											data: {
												"num": <%=num%>,
												"proName" : proName,
												"userName" : userName
											}, 
											async : true,
											cache : true,
											contentType: 'application/json; charset=UTF-8',
											success : function(responseText) {
												var str = JSON.stringify(responseText);
												var obj = JSON.parse(str);
												date = obj.date;
												color = obj.color;
												message = obj.message;
												num = obj.num;
												
												td_color = document.getElementById("color" + num);
												td_date = document.getElementById("date" + num);
												td_message = document.getElementById("message" + num);
												
												td_color.className=color;
												td_date.textContent = date;
												td_message.innerHTML = message;
											}, 
											error : function(responseText) {
												console.log("False!");
											}
										});
									</script>
									<%
										String tableActive = "";
										if(num == buildNum.size()){
										  tableActive = "tableActive";
										}
									%>
								  	<tr id="<%=num %>" onClick="changeIframe(this)" class="<%=tableActive%>">
								  		<td><%=i %></td>
								  		<td><p class="" id=<%="color" + num %>></p></td>
								  		<td id=<%="date" + num %>>></td>
								  		<td id=<%="message" + num %>></td>
								  	</tr>
								  	<%
								  	i++;
								  	lastBuildMessageNum = num;
								}
							%>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			
			<hr>
         				
       		<!-- iFrame -->
			<%
				int num = lastBuildMessageNum;
				String jobName = user.getUsername() + "_" + projectName;
				String jenkinsBuildNumUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName;
				String lastBuildUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/" +  num + "/consoleText";
				String url = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/";
			%>
			<h4><a id="iFrameTitle" href="<%=jenkinsBuildNumUrl%>">Feedback Information (#<%=buildNum.size() %>)</a></h4>
			<div style="margin:10px;">
				<iframe src="<%=lastBuildUrl %>" width="100%" height="500px" style="background: #fff3cd;" id="jenkinsOutput">
			  		<p>Your browser does not support iframes.</p>
				</iframe>
			</div>
			<!-- iFrame -->
		</div>
		<!-- -----main----- -->
	</body>
	<script type="text/javascript">
		function copyToClipboard(elem) {
			  // create hidden text element, if it doesn't already exist
		    var targetId = "_hiddenCopyText_";
		    var isInput = elem.tagName === "INPUT" || elem.tagName === "TEXTAREA";
		    var origSelectionStart, origSelectionEnd;
		    if (isInput) {
		        // can just use the original source element for the selection and copy
		        target = elem;
		        origSelectionStart = elem.selectionStart;
		        origSelectionEnd = elem.selectionEnd;
		    } else {
		        // must use a temporary form element for the selection and copy
		        target = document.getElementById(targetId);
		        if (!target) {
		            var target = document.createElement("textarea");
		            target.style.position = "absolute";
		            target.style.left = "-9999px";
		            target.style.top = "0";
		            target.id = targetId;
		            document.body.appendChild(target);
		        }
		        target.textContent = elem.textContent;
		    }
		    // select the content
		    var currentFocus = document.activeElement;
		    target.focus();
		    target.setSelectionRange(0, target.value.length);
		    
		    // copy the selection
		    var succeed;
		    try {
		    	  succeed = document.execCommand("copy");
		    } catch(e) {
		        succeed = false;
		    }
		    // restore original focus
		    if (currentFocus && typeof currentFocus.focus === "function") {
		        currentFocus.focus();
		    }
		    
		    if (isInput) {
		        // restore prior selection
		        elem.setSelectionRange(origSelectionStart, origSelectionEnd);
		    } else {
		        // clear temporary content
		        target.textContent = "";
		    }
		    return succeed;
		}
	</script>
	<script type="text/javascript">
		function changeIframe(tr){
			var u = '<%=url%>' + tr.id + '/consoleText';
			$('#jenkinsOutput').attr('src',u);
			document.getElementById("iFrameTitle").innerHTML = "Feedback Information (#" + tr.id + ")";
			$('#projectTbody tr').removeClass("tableActive");
			document.getElementById(tr.id).className = "tableActive";
		}
	</script>
</html>