<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.system.model.UserTaskLog"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%
  String rootPath = request.getContextPath();
  //System.out.println(rootPath);
  List<UserTaskLog> list = (List<UserTaskLog>)request.getAttribute("list");
  User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
  JspPage jp = (JspPage)request.getAttribute("page");
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
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script language="JavaScript" type="text/JavaScript">
	 //var delAction = "<%=rootPath%>/userTaskLog.do?action=delete";
	var curpage= <%=jp.getCurrentPage()%>;
    var totalpages = <%=jp.getPageTotal()%>;
  	var listAction = "<%=rootPath%>/userTaskLog.do?action=listType";
  	
  	function onchangeView(){
  		mainForm.action = "<%=rootPath%>/userTaskLog.do?action=list";
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
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm" id="mainForm">
<input type=hidden name="eventid">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="200" valign=top align=center>
			<%=menuTable%>
		</td>
		<td bgcolor="#cedefa" align="center" valign=top>
			<table width="98%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none align=center border=1 algin="center">
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=3>&nbsp;&nbsp;系统管理 >> 用户日志列表</td>
				</tr>
				<tr>
					<td height="28" align="right"><input type="button" value="切换至月视图" onclick="onchangeView();"></td>
				</tr>
				<tr>
		
					<td height=30 bgcolor="#FFFFFF" valign="top" align=center>
					<br>
						<table cellSpacing="1" cellPadding="0" width="100%" border="0">
						    <tr>
						    <td colspan="2" width="80%" align="center">
						    <jsp:include page="../../common/page.jsp">
						     <jsp:param name="curpage" value="<%=jp.getCurrentPage()%>"/>
						     <jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>"/>
						   </jsp:include>
						     </td>
						  </tr> 	
						</table>
					</td>
				<tr>
					<td>			
					
						  <table class="tab3"  cellSpacing="0"  cellPadding="0" border=0>
									
								  <tr  class="firsttr">
								    	<td width="10%">&nbsp;序号</td>
								        <td width="20%"><strong>用户名</strong></td>
								        <td width="50%"><strong>内容</strong></td>
								    	<td width="20%"><strong>日期</strong></td>
								 </tr>
								 <%for(int i = 0 ; i< list.size(); i++){
								 		String content = list.get(i).getContent();
								 		if(content.length()>=50){
								 			String contentNew = content.substring(0,50) + "...";
								 			content = contentNew;
								 		}
								 		%>
								 			<tr height="28">
								 				<td width="10%">&nbsp;
								 				<INPUT type="checkbox" class=noborder name=checkbox value="<%=list.get(i).getId()%>">
								 				<%=i+1 %></td>
								        		<td width="20%"><%=user.getName()%></td>
								        		<td width="50%"><%=content %></td>
								    			<td width="20%"><%=list.get(i).getTime()%></td>
								 			</tr>
								 		<%
								 } %>
						   
						 </table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
</BODY>
</HTML>
