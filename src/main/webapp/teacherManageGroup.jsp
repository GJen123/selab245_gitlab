<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="utf-8"%>
<%@ page import="conn.conn"%>
<%@ page import="service.UserService" %>
<%@ page import="java.util.List" import="java.util.ArrayList" import="java.util.*"
	import="org.gitlab.api.GitlabAPI" import="org.gitlab.api.models.*"%>
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
	
	<title>GitlabEdu</title>
</head>
<body>

	<%
		if(session.getAttribute("username") == null || session.getAttribute("username").toString().equals("")){
			response.sendRedirect("index.jsp");
		}
	%>
	<div class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand">GitlabEdu</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li><a href="teacherHW.jsp">作業</a></li>
                    <li><a href="teacherGroup.jsp">專題</a></li>
                    <li class="dropdown">
                    	<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">設定 <span class="caret"></span></a>
                    	<ul class="dropdown-menu">
	                    	<li><a href="teacherManageStudent.jsp">學生管理</a></li>
	                    	<li><a href="teacherManageHW.jsp">作業管理</a></li>
	                    	<li class="active"><a href="teacherManageGroup.jsp">專題管理</a></li>
                    	</ul>
                    </li>
                </ul>
                    <ul class="nav navbar-nav navbar-right">
        <li><a href="memberLogOut.jsp" id="loginLink">登出</a></li>
    </ul>

            </div>
        </div>
    </div>
	<br><br><br>

	<div class="container">
		<div>
			<div class="login-panel panel panel-default">
				<div class="panel-heading">
					<h3>新增組別</h3>
				</div>

				<div class="panel-body">
					<div class="col-md-2">
						<a href="webapi/group/export" class="btn btn-default">匯出學生名單</a>
					</div>

					<div class="col-md-10">
						<form method="post" action="webapi/group/upload"
							enctype="multipart/form-data">
							<button type="button" class="btn btn-default" data-toggle="modal"
								data-target="#exampleModal" data-whatever="@mdo">匯入學生名單</button>
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
											<h4 class="modal-title" id="exampleModalLabel">匯入學生名單</h4>
										</div>

										<div class="modal-body">
											<div class="form-group">
												<h4>上傳檔案</h4>
												<input type="file" name="file" size="50" />
											</div>
										</div>
										<div class="modal-footer">
											<button type="button" class="btn btn-default"
												data-dismiss="modal">關閉</button>
											<button type="submit" class="btn btn-primary">送出</button>
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