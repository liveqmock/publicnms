<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.ErrorMessage"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
  String ec = request.getParameter("errorcode");
  int errorcode = 0;
  if(ec!=null)
     errorcode = Integer.parseInt(ec);

  String errorInfo = null;
  if(errorcode==-1) //直接取错误信息
     errorInfo = (String)request.getAttribute(SessionConstant.ERROR_INFO);
  else
     errorInfo = ErrorMessage.getErrorMessage(errorcode);
  String rootPath = request.getContextPath();     
%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<style type="text/css">
body{  font-family:Arial, Helvetica, sans-serif,"宋体"; font-size:24px;}
</style>
<script language="JavaScript" type="text/javascript">
  function toLogin()
  {
     parent.location="<%=rootPath%>/login.jsp";
  }
</script>
</head>
<body>
<br><br><br>
<table width="590" border=1 cellspacing=0 cellpadding=0 
align='center' style="background:#1e4765;">
  <tr>
    <td width="100%" align="center" height="30">
    <b> <font color="red">错误</font></b></td>
  </tr>
  <tr >
    <td width="100%" align="center" height="70" background="#FF47FF">
      <font color="red"><%=errorInfo%></font>
    </td>
  </tr>
  <tr>
    <td width="100%" align="center" height="30">
    <%
    	if("对不起,用户名或密码不正确!".equals(errorInfo)){
    %>
      <input type="button" value="返 回" name="B2" onclick="<%=rootPath%>/user.do?action=login">
      <%
      }else{
      %>
      <input type="button" class="button" value="返 回" name="B2" onclick="javascript:history.back(1)">
      <%
      }
      %>
    </td>
  </tr>
</table>
</body>
</html>
