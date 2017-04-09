<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
<%@ page import="conn.Language" %>
<%@ page import="java.util.Locale" %>
<%
	//*****
	// ***¿ï¾Ü»y¨¥
	// *****
	Locale locale = request.getLocale();
	String localLan = locale.getLanguage();  // internet Language
	
	String finalLan = localLan;
	String reqLan = request.getParameter("lang");  // request Language
	String sesLan = null;  // session Language
	
	String non = "";
	if(null != reqLan && !non.equals(reqLan)){
		finalLan = reqLan;
		session.setAttribute("language", reqLan);
	}else{
		if(null != session.getAttribute("language") && !non.equals(session.getAttribute("language"))){
			sesLan = session.getAttribute("language").toString();
			finalLan = sesLan;
		}else{
			finalLan = localLan;
		}
	}
	
	Language language = new Language();
	String basename = language.getBaseName(finalLan);
%>