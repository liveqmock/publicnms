<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.util.CreateMenu"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.model.Function"%>
<%
	List<Function> menuRoot = (List<Function>)request.getAttribute("menuRoot");
  User user = (User)session.getAttribute(SessionConstant.CURRENT_USER); //当前用户
  if(user==null)
     response.sendRedirect("/common/error.jsp?errorcode=3003");

  String rootPath = request.getContextPath();
  CreateMenu menu = new CreateMenu(rootPath);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
<title>IT运维监控系统</title>
<meta http-equiv="X-UA-Compatible" content="IE=5" />
<LINK rel=stylesheet href="<%=rootPath%>/resource/css/login.css" type="text/css" >
<link rel="stylesheet" href="<%=rootPath%>/resource/css/xmenu.css" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/cssexpr.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/xmenu.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/custom_menu.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link href="css/css.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript">

  function toHome()
  {
     parent.mainFrame.location = "<%=rootPath%>/common/home.jsp";
  }

  function doQuit()
  {
     if (confirm("你真的要退出吗?"))
     {
         parent.location = "<%=rootPath%>/user.do?action=logout";
     }
  }

  function setPerson()
  {
     parent.mainFrame.location = "<%=rootPath%>/system/user/inputpwd.jsp";     
  }
</script>

<script>
   function menu(){
   menuFrame.location.reload();
   }

</script>


</HEAD>


<frameset rows="120,*" frameborder="no" border="0" framespacing="0">
  <frame src="<%=rootPath%>/common/top.jsp" marginwidth="0" marginheight="0" name="topFrame" scrolling="No" noresize="noresize" id="topFrame" title="topFrame" />
  <frame src="<%=rootPath%>/automation/common/home.jsp?lType=tree&rType=list" id="mainFrame" scrolling="auto" name="mainFrame" width="100%" marginwidth="0" marginheight="0" frameborder="0" />
</frameset>
<body leftmargin="0" topmargin="0" >
</BODY>
</html>
