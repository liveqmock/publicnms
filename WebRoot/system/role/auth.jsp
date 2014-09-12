<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.util.CreateRoleTable"%>
<%
  String tmp = request.getParameter("role");
  int role = 1;
  if(tmp==null)
     role = 1;
  else
     role = Integer.parseInt(tmp);

  CreateRoleTable crt = new CreateRoleTable();
  String selRole = crt.getRoleBox(role);
  String roleTable = crt.getTable(role,false);
  String rootPath = request.getContextPath();
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />

<title>dhcnms</title>

<script language="JavaScript" type="text/javascript">

  function toRole()
  {
     mainForm.action = "<%=rootPath%>/system/role/auth.jsp";
     mainForm.submit();
  }

  function toList()
  {
     mainForm.action = "<%=rootPath%>/role.do?action=list";
     mainForm.submit();
  }

  function toUpdate()
  {
     mainForm.action = "<%=rootPath%>/admin.do?action=update";
     mainForm.submit();
  }
</script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;
//修改菜单的上下箭头符号
function my_on(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="on";
}
function my_off(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="off";
}
//添加菜单	
function initmenu()
{
	var idpattern=new RegExp("^menu");
	var menupattern=new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for(var i=0,j=tds.length;i<j;i++){
		var td = tds[i];
		if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}

}

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="initmenu();">

<form method="post" name="mainForm">
<input type=hidden name="rows" value="<%=crt.getRows()%>">
<br>
<table align="center" width="500">
  <tr class="othertr">
    <td width="100%" align="right">选择角色:<%=selRole%></td>
  </tr>
  <tr><td valign="top">
<!--table begin--><%=roleTable%><!--table end-->
 </td></tr>
 <tr>
  <td width="100%" align="center" height="30">
    <input id="write" type="button" class="button" value="确定" name="B1" onclick="toUpdate()">
    <input type="button" class="button" value="返回" name="B2" onclick="toList()">
  </td></tr>
</table>
</form>
</body>
</html>
