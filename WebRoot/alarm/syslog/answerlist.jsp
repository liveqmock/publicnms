<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@ page import="com.afunms.event.model.*"%>
<%@ page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");//左侧菜单栏
	String startdate = (String) request.getAttribute("startdate");
	String todate = (String) request.getAttribute("todate");
	String questionid = (String) request.getAttribute("questionid");
	String title = (String)request.getAttribute("title");
	String priority = (String)request.getAttribute("priority");
	String warningByTimeQuestion = (String)request.getAttribute("warningByTimeQuestion");
	String streventname = (String)request.getAttribute("streventname");
%>

<html>
	<head>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css"
			rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
		<script src="<%=rootPath%>/resource/js/dtree.js"
			type="text/javascript"></script>
		<script src="<%=rootPath%>/resource/js/prototype.js"
			type="text/javascript"></script>
		<link href="<%=rootPath%>/include/dtree.css" type="text/css"
			rel="stylesheet" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="gb2312" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>

		<script language="javascript">	
	var delAction = "<%=rootPath%>/netsyslog.do?action=delete";
	var listAction = "<%=rootPath%>/netsyslog.do?action=statistic";

  function query(){
   	mainForm.action = "<%=rootPath%>/netsyslog.do?action=questionlist";
  	mainForm.submit();
  }  
  
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
  
	Ext.onReady(function(){
		var strClass = Ext.get('strclass');
		var ipaddress = Ext.get('ipaddress');
		strClass.on('change', function(){
			ipaddress.dom.select();
		});
	});
  
function CreateWindow(urlstr)
{
msgWindow=window.open(urlstr,"protypeWindow","toolbar=no,width=500,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}
</script>
	</head>
	<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form method="post" name="mainForm">
			<input type=hidden name="questionid" value="<%=questionid %>">
			<!-- <table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%> -->
			<table id="body-container" class="body-container">
				<tr>
					<td width="200" valign=top align=center>
						<%=menuTable%>
					</td>
					<td align="center" valign=top>
						<table cellpadding="0" cellspacing="0" algin="center" style="width:98%"><!-- width="98%" -->
							<tr>
								<td background="<%=rootPath%>/common/images/right_t_02.jpg"
									width="100%">
									<table width="100%" cellspacing="0" cellpadding="0">
										<tr>
											<td align="left">
												<img src="<%=rootPath%>/common/images/right_t_01.jpg"
													width="5" height="29" />
											</td>
											<td class="layout_title">
												<b>告警 >> SYSLOG管理 >> <%=title %> </b>
											</td>
											<td align="right">
												<img src="<%=rootPath%>/common/images/right_t_03.jpg"
													width="5" height="29" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<%
								if(null == warningByTimeQuestion){
							%>
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="1">
										<tr>
											<td bgcolor="#ECECEC" width="100%" align='left'>
												&nbsp;&nbsp;开始日期
												<input type="text" name="startdate" value="<%=startdate%>"
													size="10">
												<a onclick="event.cancelBubble=true;"
													href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
													<img id=imageCalendar1 align=absmiddle width=34 height=21
														src="<%=rootPath%>/include/calendar/button.gif" border=0>
												</a>&nbsp;&nbsp;&nbsp;&nbsp; 截止日期
												<input type="text" name="todate" value="<%=todate%>"
													size="10" />
												<a onclick="event.cancelBubble=true;"
													href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
													<img id=imageCalendar2 align=absmiddle width=34 height=21
														src="<%=rootPath%>/include/calendar/button.gif" border=0>
												</a>
												<input type="button" name="submitss" value="查询"
													onclick="query()">
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<%}else{ 
								String hour = (String)request.getAttribute("dateType");
								if(null == hour){
									startdate += " 00:00:00";
									todate += " 23:59:59";
								}
							%>
							<input type="hidden" name="dateType" value="<%=hour %>">
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="1">
										<tr>
											<td bgcolor="#ECECEC" width="100%">
												&nbsp;&nbsp;<strong>从<%=startdate %></strong><br>
												&nbsp;&nbsp;<strong>到<%=todate %></strong>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<%} %>
							
							<tr>
								<td colspan="2">
									<table cellspacing="1" cellpadding="0" width="100%">
										<tr align="center" height=28 class="microsoftLook0">
											<td width="50%">
												<strong>IP地址</strong>
											</td>
											<td>
												<strong>计数</strong>
											</td>
										</tr>

										<%
											Map map = (HashMap)request.getAttribute("map");
											if (null == map || map.size() == 0) {
										%>
										<tr height=25  class="microsoftLook0">
											<td colspan="2" align="center">
												没有信息
											</td>
										</tr>
										<%
											} else {
												Set set = map.entrySet();
												Iterator iter = set.iterator();
												while (iter.hasNext()) {
													Map.Entry entry = (Map.Entry)iter.next();
													String ipaddress = (String)entry.getKey();
													int count = (Integer)entry.getValue();
										%>

										<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height=25 align="center">
											<td><%=ipaddress %></td>
											<td>
												<a href="<%=rootPath%>/netsyslog.do?action=catelist&ipaddress=<%=ipaddress%>&streventname=<%=streventname %>&priority=<%=priority %>&startdate=<%=startdate %>&todate=<%=todate %>&jp=1"><%=count%></a>
												<!-- <a href="<%=rootPath%>/netsyslog.do?action=catelist&ipaddress=<%=ipaddress%>&streventname=<%=streventname %>&startdate=<%=startdate %>&todate=<%=todate %>&jp=1"><%=count%></a> -->
											</td>
										</tr>
										<%
												}
											}
										%>
									</table>
								</td>
							</tr>
							<tr>
								<td background="<%=rootPath%>/common/images/right_b_02.jpg">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td align="left" valign="bottom">
												<img src="<%=rootPath%>/common/images/right_b_01.jpg"
													width="5" height="11" />
											</td>
											<td></td>
											<td align="right" valign="bottom">
												<img src="<%=rootPath%>/common/images/right_b_03.jpg"
													width="5" height="11" />
											</td>
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
</HTML>
