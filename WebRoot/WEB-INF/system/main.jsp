<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>东华BPM</title>
</head>
<%
	String rootPath = request.getContextPath();
	
%>
<link href="<%=rootPath%>/css/main.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/main.js"></script>
<body class="content">
<%@ include file="/top.jsp" %>
<%@ include file="/menu.jsp" %>
<iframe id="iframe_content" name ="list" src="<%=rootPath%>/controller/taskClaimList.action" height="350px" width="100%" frameborder="0"></iframe>
<%@ include file="/footer.jsp" %> 
</body>
</html>