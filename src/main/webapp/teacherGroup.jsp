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
	<!-- <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css"> -->
	<style type="text/css">
		#inline li {
		    display: inline;
		}
	    .table th, .table td { 
	        border-top: none !important;
	        border-left: none !important;
	    }
		body, html, .row, #navHeight{
			height:100%;
		}
		#groupMamber {
			padding: 5px !important;
		}
	</style>
	
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>

	<div class="row">
        <nav class="bg-faded sidebar hidden-xs-down" id="navHeight">
          <ul class="nav nav-pills flex-column" style="margin-top: 20px;">
            <%
            	for(GitlabGroup group : groups){
            	  String href = "\"teacherGroup.jsp?id=" + group.getId() + "\"";
            	  %>
            	  	<li class="nav-item"><font size="3"><a class="nav-link" href=<%=href %>><%=group.getName() %></a></font></li>
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
        	<div class="container" style="margin-top: 20px;">
	        	<h2><%=groupChoosed.getName() %></h2>
	        		<table class="table" style="margin-top: 20px;">
			        	<thead class="thead-default">
			        		<tr>
			        			<th width="20%"><font size="3"><fmt:message key="teacherGroup_th_project"/></font></th>
							    <th width="20%"><font size="3"><fmt:message key="teacherGroup_th_student"/></font></th>
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
			        							  		<td id="groupMamber" width="20%"><p><%=project.getName() %>專案</p></td>
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
			        								<tr><td id="groupMamber"><h5><%=role %>：  <%=member.getName() %></h5></td></tr>
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
					        					  	<tr><td id="groupMamber"><h6><%=role %>：  <%=member.getName() %></h6></td></tr>
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
        </main>
    </div>
</body>
</html>