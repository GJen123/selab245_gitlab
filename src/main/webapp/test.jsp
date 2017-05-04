<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
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
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<style type="text/css">
		#inline li {
		    display: inline;
		}
	</style>
	
	<title>ProgEdu</title>
</head>
<body>
	<%@ include file="header.jsp" %>
	
	<%
		Conn conn = Conn.getInstance();
		HttpConnect httpConn = HttpConnect.getInstance();
	
		UserDbManager db = UserDbManager.getInstance();
		ProjectDbManager Pdb = ProjectDbManager.getInstance();
		
		// db的所有users
		List<User> users = db.listAllUsers();
		
		// 每個學生gitlab的projects
		List<GitlabProject> gitProjects = new ArrayList<GitlabProject>();
		
		// db的所有projects
		List<Project> dbProjects = Pdb.listAllProjects();
		
		// gitlab jenkins course的Data
		GitlabConfig gitData = GitlabConfig.getInstance();
		JenkinsConfig jenkinsData = JenkinsConfig.getInstance();
		
		JenkinsApi jenkins = JenkinsApi.getInstance();
	%>
      <div class="row">
        <nav class="col-sm-3 col-md-2 hidden-xs-down bg-faded sidebar">
          <ul class="nav nav-pills flex-column">
            <li class="nav-item">
              <a class="nav-link" href="#">Overview <span class="sr-only">(current)</span></a>
            </li>
          </ul>
        </nav>

        <main class="col-md-9 col-xs-11 p-l-2 p-t-2">
          <h1>Dashboard</h1>
			<div id="inline">
			<ul>
				<li><img src="jenkins_pic/jenkins_blue.PNG" width="36" height="31">Commit過且沒問題</li>
				<li><img src="jenkins_pic/jenkins_red.PNG" width="36" height="31">Commit過但有Error</li>
				<li><img src="jenkins_pic/jenkins_gray.PNG" width="36" height="31">未Commit</li>
			</ul>
		</div>
		<table class="table table-striped">
			<thead class="bg-primary text-white">
				<tr>
					<th><fmt:message key="teacherHW_th_studentId"/></th>
					<th><fmt:message key="teacherHW_th_studentName"/></th>
					<%
						for(Project project : dbProjects){
							%>
								<th><%=project.getName() %></th>
							<%
						}
					%>
				</tr>
			</thead>
			<tbody>
				<%
					for(User user : users){
						String userName = user.getUserName();
						String personal_url = gitData.getGitlabHostUrl() + "/u/" + userName;
						%>
							<tr>
								<td width="15%"><%=user.getUserName() %></td>
								<td width="10%"><strong><a href="#" onclick="window.open('<%=personal_url %>')"><%=user.getName() %></a></strong></td>
								<%
									gitProjects = conn.getProject(user);
									Collections.reverse(gitProjects);
									for(Project dbProject : dbProjects){
										String proName = null;
										String proUrl = null;
										int commit_count = 0;
										String colorPic = null;
										for(GitlabProject gitProject : gitProjects){
											if(dbProject.getName().equals(gitProject.getName())){
												proName = dbProject.getName();
												proUrl = gitProject.getWebUrl();
												proUrl = conn.getReplaceUrl(proUrl);
												proUrl += "/commits/master"; 
												commit_count = conn.getAllCommitsCounts(gitProject.getId());
												//---Jenkins---
												String jobName = user.getUserName() + "_" + gitProject.getName();
												String jobUrl = jenkinsData.getJenkinsHostUrl() + "/job/" + jobName + "/api/json";
												String color = jenkins.getJobJsonColor(jenkinsData.getJenkinsRootUsername() ,jenkinsData.getJenkinsRootPassword(), jobUrl);
												if(commit_count == 1){
												  colorPic = "jenkins_pic/jenkins_gray.PNG";
												} else {
												  	if(color!=null){
														colorPic = jenkins.getColorPic(color);
													}else{
														colorPic = "jenkins_pic/jenkins_gray.PNG";
													}
												}
												//-------------
												break;
											}else{
												proName = "N/A";
											}
										}
										
										if("N/A".equals(proName)){
											%>
												<td><%=proName %></td>
											<%
										}else{
											%>
												<td><a href="#" onclick="window.open('<%=proUrl %>')"><%=commit_count %></a>
												<img src="<%=colorPic %>" width="36" height="31"></td>
											<%
										}
									}
								%>
							</tr>
						<%
					}
				%>
			</tbody>
		</table>
        </main>
      </div>

</body>
</html>