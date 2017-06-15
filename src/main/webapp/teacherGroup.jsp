<%@ page language="java" contentType="text/html; charset=BIG5"	pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.HttpConnect, fcu.selab.progedu.db.GroupDbManager"%>
<%@ page import="fcu.selab.progedu.conn.Language,fcu.selab.progedu.config.GitlabConfig, fcu.selab.progedu.data.Group" %>
<%@ page import="java.util.List" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="fcu.selab.progedu.conn.Conn,fcu.selab.progedu.conn.HttpConnect" %>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi, fcu.selab.progedu.conn.Language" %>
<%@ page import="fcu.selab.progedu.config.GitlabConfig" %>
<%@ page import="fcu.selab.progedu.config.JenkinsConfig" %>
<%@ page import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.db.ProjectDbManager" %>
<%@ page import="fcu.selab.progedu.data.User, fcu.selab.progedu.data.Project" %>   
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.*" %>

<%@ include file="language.jsp" %>

<%
	Conn conn = Conn.getInstance();
	GitlabConfig gitData = GitlabConfig.getInstance();
	
	List<GitlabGroup> groups = conn.getGroups();
	Collections.reverse(groups);
	
	GroupDbManager gdb = GroupDbManager.getInstance();
	List<Group> dbGroups = gdb.listGroups();
	String groupUrl = "/groups/";
		

	String groupId = request.getParameter("id"); // Get group id
	if(null == groupId){
	  groupId = String.valueOf(groups.get(0).getId());
	}
%>

<html>
<head>
	<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
	<style type="text/css">
		#inline li {
		    display: inline;
		}
	</style>
	<style>
	    .table th, .table td { 
	        border-top: none !important;
	        border-left: none !important;
	    }
	</style>
	
	<style>
		body, html{
			height:100%;
		}
	</style>
	
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>

	<div class="row">
        <nav class="bg-faded sidebar col-sm-3 col-md-2 hidden-xs-down">
        	<!-- <div class="w3-sidebar w3-light-grey w3-bar-block" style="width:15%">
			  <%
            	for(GitlabGroup group : groups){
            	  String href = "\"teacherGroup.jsp?id=" + group.getId() + "\"";
            	  %>
            	  	<a class="nav-link" href=<%=href %>><%=group.getName() %></a>
            	  <%
            	}
           	%>
			</div> -->
          <ul class="nav nav-pills flex-column">
            <%
            	for(GitlabGroup group : groups){
            	  String href = "\"teacherGroup.jsp?id=" + group.getId() + "\"";
            	  %>
            	  	<li class="nav-item"><a class="nav-link" href=<%=href %>><%=group.getName() %></a></li>
            	  <%
            	}
            %>
          </ul>
        </nav>
        <main class="col-md-9 col-xs-11 p-l-2 p-t-2">
        	<%
        		GitlabGroup groupChoosed = new GitlabGroup();
        		for(GitlabGroup group : groups){
        		  if(Integer.valueOf(groupId) == group.getId()){
        		    groupChoosed = group;
        		    break;
        		  }
        		}
        	%>
        	
        	<br><br>
        	<div class="container">
        		<div class="card">
	        		<h1 class="card-header"><%=groupChoosed.getName() %></h1>
	        		<div class="card-block">
	        			<table class="table">
			        		<thead class="thead-default">
			        			<tr>
			        				<th width="20%"><fmt:message key="teacherGroup_th_project"/></th>
								    <th width="20%"><fmt:message key="teacherGroup_th_student"/></th>
			        			</tr>
			        		</thead>
			        		<tbody>
			        			<%
			        				List<GitlabProject> projects = conn.getGroupProject(groupChoosed);
			        				Collections.reverse(projects);
			        				List<GitlabGroupMember> groupMembers = conn.getGroupMembers(groupChoosed);
			        				Collections.reverse(groupMembers);
			        			%>
			        			<tr>
			        				<td>
			        					<table>
			        						<tr>
			        							<%
			        								for(GitlabProject project : projects){
			        								  	%>
			        								  		<td width="20%"><%=project.getName() %></td>
			        								  	<%
			        								}
			        							%>
			        						</tr>
			        					</table>
			        				</td>
			        				<td>
			        					<table id="noborder">
			        						<%
			        						for(GitlabGroupMember member : groupMembers){
			        						  String role = "";
			        							if(member.getName().equals("Administrator")) {
		        									continue;
		        								}
			        							if(member.getAccessLevel().toString().equals("Master")) {
			        								role = "組長";
			        								%>
			        									<tr><th><font size="4"><%=role %>：  <%=member.getName() %></font></th></tr>
			        								<%
			        							}else{
			        							  	continue;
			        							}
			        						}
			        						for(GitlabGroupMember member : groupMembers){
			        							String role = "";
			        							if(member.getName().equals("Administrator")) {
		        									continue;
		        								}
			        							if(member.getAccessLevel().toString().equals("Developer")) {
			        								role = "組員";
			        								%>
					        						  	<tr><th><font size="3"><%=role %>：  <%=member.getName() %></font></th></tr>
					        						<%
			        							}
			        						}
			        						%>
			        					</table>
			        				</td>
								</tr>
			        		</tbody>
			        	</table>
	        		</div>
        		</div>
	        	
        	</div>
        </main>
    </div>
</body>
</html>