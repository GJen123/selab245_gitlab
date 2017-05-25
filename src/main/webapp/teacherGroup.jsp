<%@ page language="java" contentType="text/html; charset=BIG5"
<<<<<<< HEAD
	pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn, fcu.selab.progedu.conn.HttpConnect, fcu.selab.progedu.db.GroupDbManager"%>
<%@ page import="fcu.selab.progedu.conn.Language,fcu.selab.progedu.config.GitlabConfig, fcu.selab.progedu.data.Group" %>
<%@ page import="java.util.List" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.ArrayList" %>
=======
    pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="fcu.selab.progedu.conn.Conn,fcu.selab.progedu.conn.HttpConnect" %>
<%@ page import="fcu.selab.progedu.jenkins.JenkinsApi, fcu.selab.progedu.conn.Language" %>
<%@ page import="fcu.selab.progedu.config.GitlabConfig" %>
<%@ page import="fcu.selab.progedu.config.JenkinsConfig" %>
<%@ page import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.db.ProjectDbManager" %>
<%@ page import="fcu.selab.progedu.data.User, fcu.selab.progedu.data.Project" %>   
>>>>>>> 58809bb32d0ddd25c32cac5cb911230d33e3c14d
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.*" %>

<%@ include file="language.jsp" %>

<%
	Conn conn = Conn.getInstance();
	GitlabConfig gitData = GitlabConfig.getInstance();
	
	List<GitlabGroup> groups = conn.getGroups();
	Collections.reverse(groups);

	String groupId = request.getParameter("id"); // Get group id
	if(null == groupId){
	  groupId = String.valueOf(groups.get(0).getId());
	}
%>

<html>
<head>
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
	
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>
<<<<<<< HEAD
	 
	<%
		//Conn conn = Conn.getInstance();
		GitlabConfig gitData = GitlabConfig.getInstance();
		//List<GitlabGroup> groups = conn.getGroups();
		GroupDbManager gdb = GroupDbManager.getInstance();
		List<Group> groups = gdb.listGroups();
		String groupUrl = "/groups/";
		
		%>
			<div class="container">
				<table class="table table-condensed">
					<thead>
						<tr>
					      <th><fmt:message key="teacherGroup_th_group"/></th>
					      <th><fmt:message key="teacherGroup_th_project"/></th>
					      <th><fmt:message key="teacherGroup_th_student"/></th>
					    </tr>
					</thead>
					<tbody>
						<%
							for(Group group : groups){
								//List<GitlabGroupMember> groupMembers = conn.getGroupMembers(group);
								//Collections.reverse(groupMembers);
								//List<GitlabProject> projects = conn.getGroupProject(group);
								//String groupUrl = conn.getGroupUrl(group);
										%>
											<tr>
												<td>
													<table class="table table-condensed">
														<tr>
															<td><a href="<%=groupUrl + group.getGroupName() %>" onclick="window.open('<%=groupUrl + group.getGroupName() %>')"><%=group.getGroupName() %></a></td>
														</tr>
													</table>
												</td>
												<td>
													<table class="table table-condensed">
														<%
															/*for(GitlabProject project : projects){
																String projectUrl = project.getWebUrl();
																String oldStr = projectUrl.substring(0, 19);
																projectUrl = projectUrl.replace(oldStr, gitData.getGitlabHostUrl());
																projectUrl += "/commits/master";*/
																%>
																	<tr>
																		
																	</tr>
																<%
															//}
														%>
														
													</table>
												</td>
												<td>
													<table class="table table-condensed">
													<%
														String master = group.getMaster();
														String memberUrl = gitData.getGitlabHostUrl() + "/u/" + master;
													%>
														<tr>
															<td><a href="<%=memberUrl %>" onclick="window.open('<%=memberUrl %>')">組長：<%=master %></a></td>
														</tr>
														<%
															for(String member : group.getContributor()){
																memberUrl = gitData.getGitlabHostUrl() + "/u/" + member;
																if(member.equals("Administrator")) {
																	continue;
																}
																%>
																																		<tr>
																		<td><a href="<%=memberUrl %>" onclick="window.open('<%=memberUrl %>')">組員：<%=member %></a></td>
																	</tr>
																<% 
															}
														
														%>
													</table>
												</td>
											</tr>
										<%
							}
						%>
					</tbody>
				</table>
			</div>
		<%
	%>
=======
>>>>>>> 58809bb32d0ddd25c32cac5cb911230d33e3c14d
	
	<div class="row">
        <nav class="col-sm-3 col-md-2 hidden-xs-down bg-faded sidebar">
          <ul class="nav nav-pills flex-column">
            <%
            	for(GitlabGroup group : groups){
            	  String href = "\"testGroup.jsp?id=" + group.getId() + "\"";
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
        	<h1><%=groupChoosed.getName() %></h1>
        	<br><br>
        	<div class="container">
	        	<table class="table">
	        		<thead>
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
	        					<!-- 
	        					<ul style="list-style-type:none">
	        						<%
	        							for(GitlabGroupMember member : groupMembers){
	        							  %>
	        							  	<li><%=member.getName() %></li>
	        							  <%
	        							}
	        						%>
	        					</ul>
	        					 -->
	        					 
	        					<table id="noborder">
	        						<%
	        						for(GitlabGroupMember member : groupMembers){
	        						  %>
	        						  	<tr><th><%=member.getName() %></th></tr>
	        						  <%
	        						}
	        						%>
	        					</table>
	        				</td>
						</tr>
	        		</tbody>
	        	</table>
        	</div>
        	
        </main>
    </div>
</body>
</html>