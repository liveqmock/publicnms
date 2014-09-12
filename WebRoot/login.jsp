<%@page language="java" contentType="text/html;charset=GB2312"%>
<%
  String rootPath = request.getContextPath();
%>
<HTML>
<head>
<TITLE>IT运维监控系统</TITLE>
<META http-equiv=X-UA-Compatible content=IE=EmulateIE7>
<META http-equiv=Content-Type content="text/html; charset=GBK"/>

<LINK href="resource/css/login.css" type="text/css" rel=stylesheet>
<META content="MSHTML 6.00.2800.1106" name=GENERATOR/>
<style type="text/css">
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
	background-image: url(<%=rootPath%>/resource/image/bg.jpg);
	font-size:12px;
	color:#000000;
}
.botton_login {	
	background-image: url(<%=rootPath%>/resource/image/login_button.gif);
	width: 53px;
	height:20px;
	border: none;
	padding-top: 2px;
	background-repeat: no-repeat;
}
.input_text {	background-color: #f2f1f1;
	height: 16px;
	width: 120px;
	padding-left: 2px;
	border: 1px solid #828485;
}
-->
</style>
<script language="javascript">
function doLogin()
{
    if(mainForm.userid.value=="")
    {
       alert("请输入用户名!");
       mainForm.userid.focus();
       return false;
    }
    else if(mainForm.password.value=="")
    {
       alert("请输入密码!");
       mainForm.password.focus();
       return false;
    } 
}
</script>




</head>

<body>
<form method="post" name="mainForm" action="<%=rootPath%>/user.do?action=login" onsubmit="return doLogin();">
<table width="1021" height="590" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" background="<%=rootPath%>/resource/image/loginbg.jpg" style="background-repeat:no-repeat"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td height="360" colspan="2">&nbsp;</td>
        </tr>
      <tr>
        <td width="595">&nbsp;</td>
        <td width="43%"><table width="240" height="100"  border="0" align="left" cellpadding="0" cellspacing="0">
          <tr valign="middle">
            <td width="37%" align="right"><span class="style1">用户名&nbsp;</span></td>
            <td width="63%" height="30" align="left"><input type="text" name="userid" id="userid" class="input_text" /></td>
          </tr>
          <tr valign="middle">
            <td align="right"><span class="style1">密&nbsp;&nbsp;码&nbsp;</span></td>
            <td height="30" align="left"><table width="100%"  border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="86%"><input type="password" name="password" id="password" class="input_text" />
                  </td>
                  <td width="14%"><img src="<%=rootPath%>/resource/image/button_modify.gif" alt="修改密码" width="16" height="16" /> </td>
                </tr>
            </table></td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td height="30" align="left"><input type="submit" name="Submit" value="登 录" class="botton_login" />
              &nbsp;
              <input type="reset" name="Submit2" value="重 置" class="botton_login" /></td>
          </tr>
        </table></td>
      </tr>
    </table></td>
  </tr>
</table>
</form>
</body>
</html>
