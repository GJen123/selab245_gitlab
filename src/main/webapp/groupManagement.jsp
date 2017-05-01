<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@ page import="fcu.selab.progedu.conn.Conn,fcu.selab.progedu.conn.Language" %>
<%@ page import="fcu.selab.progedu.db.UserDbManager, fcu.selab.progedu.data.User" %>
<%@ page import="fcu.selab.progedu.service.UserService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Locale" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
		response.sendRedirect("index.jsp");
	}
	session.putValue("page", "teacherManageGroup");
	
	UserDbManager uDB = UserDbManager.getInstance();
	List<User> users = uDB.listAllUsers();
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
	
	<script>
	$(document).ready(function() {
		$('#upload').submit(function(evt) {
			evt.preventDefault();
			var formData = new FormData($(this)[0]);
			$.ajax({
				url : 'webapi/group/upload',
				type : 'POST',
				data : formData,
				async : false,
				cache : false,
				contentType : false,
				enctype : 'multipart/form-data',
				processData : false,
				success : function(response) {
					alert("Uploaded!");
					top.location.href = "../ProgEdu/groupManagement.jsp";
				}, 
				error : function(response) {
					alert("Failed! Check out whether there is the group with the same name on GitLab.");
				}
			});
			return false;
		});
	});
</script>
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
						<form id="upload" name="upload">
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
					<br><br><br><br>
					<div class="col-md-12">
						<form id="select" name="select" action="">
							<table>
								<tr>
									<td>
										<label for="groupName">隊伍名稱：</label>
										<input type="text" id="groupName" name="groupName">
								</tr>
								<tr>
									<td>
										<select style="width: 500px" name="select1" id="select1" multiple size="<%=users.size()%>">
											<%for(User user : users){
												%>
												<option value="<%=user.getUserName()%>"><%=user.getUserName() %>-<%=user.getName() %></option>
												<%
											}%>
										</select>
									</td>
									<td>
										<input type="button" id="gt" name="gt" value="&gt;&gt;">
										<br>
										<input type="button" id="lt" name="lt" value="&lt;&lt;">
									</td>
									<td>
										<select style="width: 500px" name="select2" id="select2" multiple>
										</select>
									</td>
								</tr>
							</table>
						</form>
					</div>
				</div>
				<!-- panel-body -->
			</div>
			<!-- panel -->
		</div>
	</div>
	<script>
		$('#gt').click(function(e){
			$('#select1 option:selected').each(function(index){
				$(this).remove();
				$('#select2').append("<option value="+$(this).val()+">"+$(this).text()+"</option>");
			})
	
		});
		$('#lt').click(function(e){
			$('#select2 option:selected').each(function(index){
				$(this).remove();
				$('#select1').append("<option value="+$(this).val()+">"+$(this).text()+"</option>");
			})
	
		});
</script>	
</body>
</html>