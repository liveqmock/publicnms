<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.system.model.Role"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.Admin"%>
<%@page import="com.afunms.system.model.User"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="java.util.List"%>
<%
	String rootPath = request.getContextPath();
	List<Admin> admin_list = (List<Admin>)session.getAttribute(SessionConstant.CURRENT_ADMIN);
	User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
	System.out.println("==========================================================");
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/javascript">

  function toUpdate()
  {
     mainForm.action = "<%=rootPath%>/admin.do?action=admin_update";
     mainForm.submit();
  }
</script>
<script language="javascript">	
  
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/network.do?action=find";
     mainForm.submit();
  }
  
    function doCancelManage()
  {  
     mainForm.action = "<%=rootPath%>/network.do?action=cancelmanage";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  }

</script>
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
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="200" valign=top align=center rowspan="2">
		<%=menuTable%>
		</td>
		
		<td valign="top">
		<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding= rules=none align=center border=1>
			<tr>
			<td height="28" align="left" valign="middle" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan="3">&nbsp;&nbsp;系统管理 >> 角色管理 >> 角色列表</td>
			</tr>
			<tr>
			<td height="30" bgcolor="#ffffff" align="right" valign="middle" colspan="3">
				<a href="#" onclick="toUpdate()">修改</a>&nbsp;&nbsp;&nbsp;
			</td>
			</tr>
			<tr>
				<td colspan="1">
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr class="microsoftLook0" height=28>
						<% 
						List<Role> Rolelist = (List<Role>)request.getAttribute("Rolelist");
						for(int i = 0; i< Rolelist.size(); i++){
						
						if(user.getRole() == 0){
						%>
						<td height="28" bgcolor="#DEEBF7" align="center" valign="middle">
						<input type="radio" id="role" name="RoleId" value=<%=Rolelist.get(i).getId() %>>&nbsp;<%=(String)Rolelist.get(i).getRole()%></td>
						<%	
						}else if(user.getRole() == Rolelist.get(i).getId()) {
						%>
						
						<td height="28" bgcolor="#DEEBF7" align="center" valign="middle">
						<input type="radio" id="role" name="RoleId" value=<%=Rolelist.get(i).getId() %>>&nbsp;<%=(String)Rolelist.get(i).getRole()%></td>
						
						
						<%}}	%>
						
						
						</tr>
					</table>
				</td>
			</tr>
		</table>
			</td>

			</tr>
</table>
</form>

</BODY>


<script>
window.onload=function(){
	var tables = document.getElementsByTagName("table");
	var lies = document.getElementsByTagName("li");
	var idpattern=new RegExp("^"+"0");
	
	for(var i=0,j=tables.length;i<j;i++){
		var flag = true;
		var table = tables[i];
		<%for(int k = 0 ; k<admin_list.size();k++){
			String func_desc = admin_list.get(k).getFunc_desc();
		
			%>
			
			if(table.id == "<%=func_desc%>"){
				
				flag = false;
				
			}
			<%
		}%>
		
		if(flag&&idpattern.test(table.id)){
	
			table.style.display = "none";
		}
	}
	
	for(var i=0,j=lies.length;i<j;i++){
		var flag = true;
		var li = lies[i];
		<%for(int k = 0 ; k<admin_list.size();k++){
			String func_desc = admin_list.get(k).getFunc_desc();
			
			%>
			if(li.className == "<%=func_desc%>"){
				
				flag = false;
			}
			<%
		}%>
		if(flag){
			li.style.display = "none";
		}
	}
	
	initmenu();
}
</script>

</HTML>
