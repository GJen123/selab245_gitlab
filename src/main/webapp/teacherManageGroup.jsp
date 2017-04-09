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
	
	//抓當地語言
	Locale locale = request.getLocale();
	String country = locale.getCountry();
	String localLan = locale.getLanguage();
	
	String finalLan = localLan;
	String reqLan = request.getParameter("lang");
	String sesLan = null;
	
	if(reqLan == null || reqLan.trim().equals("")) { 
		// 如果request裡沒有值
		System.out.println("no request");
		if(session.getAttribute("language") == null || session.getAttribute("language").toString().equals("")){
			// 如果session裡沒有值
			System.out.println("no session");
			finalLan = localLan;
		}else{
			// session裡有值
			System.out.println("has session");
			sesLan = session.getAttribute("language").toString();
			finalLan = sesLan;
		}
	} else{
		// request裡有值  優先考慮request裡的值
		System.out.println("has request");
		session.putValue("language", reqLan);
		finalLan = reqLan;
	}
	
	Language language = new Language();
	session.putValue("page", "teacherManageGroup");
	String basename = language.getBaseName(finalLan);
	System.out.println("finalLan : " + finalLan);
	System.out.println("basename : " + basename);
%>

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
	
	<div class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand">ProgEdu</a>
            </div>
            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li><a href="teacherHW.jsp"><fmt:message key="top_navbar_dashboard"/></a></li>
                    <li><a href="teacherGroup.jsp"><fmt:message key="top_navbar_groupProject"/></a></li>
                    <li class="dropdown">
                    	<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                    		<fmt:message key="top_navbar_manage"/> <span class="caret"></span></a>
                    	<ul class="dropdown-menu">
	                    	<li><a href="teacherManageStudent.jsp"><fmt:message key="top_navbar_manageStudent"/></a></li>
	                    	<li><a href="teacherManageHW.jsp"><fmt:message key="top_navbar_manageHW"/></a></li>
	                    	<li class="active"><a href="teacherManageGroup.jsp"><fmt:message key="top_navbar_manageGroup"/></a></li>
                    	</ul>
                    </li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                	<li class="dropdown">
                    	<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                    		<fmt:message key="top_navbar_language"/> <span class="caret"></span></a>
                    	<ul class="dropdown-menu" >
	                    	<li id="English" value="en"><a href="ChooseLanguage?language=en"><fmt:message key="top_navbar_lanEnglish"/></a></li>
	                    	<li id="Chinese" value="zh"><a href="ChooseLanguage?language=zh"><fmt:message key="top_navbar_lanChinese"/></a></li>
                    	</ul>
                    </li>
        			<li><a href="memberLogOut.jsp" id="loginLink"><fmt:message key="top_navbar_signOut"/></a></li>
    			</ul>
            </div>
        </div>
    </div>
	<br><br><br>

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