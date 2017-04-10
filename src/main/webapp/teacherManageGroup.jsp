<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@ page import="conn.Conn,conn.Language"%>
<%@ page import="service.UserService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.*" %>
<%@ page import="org.gitlab.api.GitlabAPI" %>
<%@ page import="org.gitlab.api.models.*" %>
<%@ page import="java.util.Locale" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "teacherManageGroup");
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
	
	<title>ProgEdu</title>
</head>
<body>
	<!-- 設定語言 -->
	<fmt:setBundle basename = "<%=basename %>"/>
	
	<%@ include file="header.jsp" %>

	<div class="container">
		<div>
			<div class="login-panel panel panel-default">
				<div class="panel-heading">
					<h3><fmt:message key="teacherManageGroup_h3_newGroup"/></h3>
				</div>

				<div class="panel-body">
					<div class="col-md-2">
						<a href="webapi/group/export" class="btn btn-default"><fmt:message key="teacherManageGroup_a_exportStudent"/></a>
					</div>

					<div class="col-md-10">
						<form method="post" action="webapi/group/upload"
							enctype="multipart/form-data">
							<button type="button" class="btn btn-default" data-toggle="modal"
								data-target="#exampleModal" data-whatever="@mdo"><fmt:message key="teacherManageGroup_button_importStudent"/></button>
							<div class="modal fade" id="exampleModal" tabindex="-1"
								role="dialog" aria-labelledby="exampleModalLabel"
								aria-hidden="true">
								<div class="modal-dialog">
									<div class="modal-content">
										<div class="modal-header">
											<button type="button" class="close" data-dismiss="modal">
												<span aria-hidden="true">&times;</span> <span
													class="sr-only">Close</span>
											</button>
											<h4 class="modal-title" id="exampleModalLabel"><fmt:message key="teacherManageGroup_h4_importStudent"/></h4>
										</div>

										<div class="modal-body">
											<div class="form-group">
												<h4><fmt:message key="teacherManageGroup_h4_uploadFile"/></h4>
												<input type="file" name="file" size="50" />
											</div>
										</div>
										<div class="modal-footer">
											<button type="button" class="btn btn-default"
												data-dismiss="modal"><fmt:message key="teacherManageGroup_button_close"/></button>
											<button type="submit" class="btn btn-primary"><fmt:message key="teacherManageGroup_button_send"/></button>
										</div>
									</div>
								</div>
							</div>
						</form>
					</div>
					<div>
					<br><br>
					<p>id, name</p>
						<%
							UserService userService = new UserService();
							List<GitlabUser> lsUsers = userService.getUsers();
							Collections.reverse(lsUsers);
							for (GitlabUser user : lsUsers) {
								if (user.getId() == 1) {
									continue;
								}
						%>
							<p><%=user.getId() %>, <%=user.getName()%></p>
						<%
							}
						%>
					</div>
				</div>
				<!-- panel-body -->
			</div>
			<!-- panel -->
		</div>
	</div>

</body>
</html>