<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.common.base.BaseVo"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@ page import="com.afunms.event.model.*"%>
<%@ page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.event.manage.NetSyslogManager" %>
<%
	String rootPath = request.getContextPath();
	List list = (List) request.getAttribute("list");

	JspPage jp = (JspPage) request.getAttribute("page");

	String startdate = (String) request.getAttribute("startdate");
	String todate = (String) request.getAttribute("todate");
	String ipaddress = (String) request.getAttribute("ipaddress");
	if (ipaddress == null)ipaddress = "";
//	String eventid = (String) request.getAttribute("eventid");
//	if (eventid == null)eventid = "";
	String priority = (String) request.getAttribute("priority");
	String processname = (String) request.getAttribute("processname");
	if (processname == null)processname = "";
	String message = (String) request.getAttribute("message");
	if (message == null)message = "";
	String busi = (String) request.getAttribute("busi");//区分业务类型：如果是用户账户问题 则busi=userAccout，否则busi为null
	if (busi == null)busi = "";
	String tabTitle = "";
	String streventname = (String)request.getAttribute("streventname");
	if (streventname == null)streventname = "";
	else if("userLoginSuccess".equals(streventname)){
		tabTitle = "登录成功的事件";
	}else if("userLoginFailure".equals(streventname)){
		tabTitle = "登录失败的事件";
	}else if("userLogoutSuccess".equals(streventname)){
		tabTitle = "登出成功的事件";
	}else if("nwDevUserLoginSuccess".equals(streventname)){
		tabTitle = "网络设备登录成功的事件";
	}else if("nwDevUserLoginFailure".equals(streventname)){
		tabTitle = "网络设备登录失败的事件";
	}else if("nwDevUserLogoutSuccess".equals(streventname)){
		tabTitle = "网络设备登出成功的事件";
	}else if("qsUserChangePassSuccess".equals(streventname)){
		tabTitle = "密码修改成功的用户";
	}else if("qsUserChangePassFailure".equals(streventname)){
		tabTitle = "密码修改失败的用户";
	}else if("qsUserAccountDisabled".equals(streventname)){
		tabTitle = "帐号被删除和禁用的详细信息";
	}else if("qsUserModifyLog".equals(streventname)){
		tabTitle = "清除了审计日志的用户列表信息";
	}
	String username = (String)request.getAttribute("username");
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
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

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css"
			rel="stylesheet">
		<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet"
			type="text/css">
		<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css"
			type="text/css">
		<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet"
			type="text/css">
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
		<script src="<%=rootPath%>/resource/js/dtree.js"
			type="text/javascript"></script>
		<script src="<%=rootPath%>/resource/js/prototype.js"
			type="text/javascript"></script>
		<link href="<%=rootPath%>/include/dtree.css" type="text/css"
			rel="stylesheet" />

		<script language="javascript">	
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/netsyslog.do?action=delete";
  var listAction = "<%=rootPath%>/netsyslog.do?action=syslogimpevtcatelist";
  
	function CreateWindow(urlstr){
		msgWindow=window.open(urlstr,"protypeWindow","toolbar=no,width=500,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
	}
	function query(){
   		mainForm.action = "<%=rootPath%>/netsyslog.do?action=syslogimpevtcatelist&jp=1";
  		mainForm.submit();
  	}
</script>
	</head>
	<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form method="post" name="mainForm">
			<!-- <input type=hidden name="eventid" value="<%//=eventid%>"> -->
			<input type=hidden name="streventname" value="<%=streventname%>">
			<input type=hidden name="username" value="<%=username%>">
			<table border="0" id="table1" cellpadding="0" cellspacing="0"
				width=100%>
				<tr>
					<td width="200" valign=top align=center>
						<%=menuTable%>
					</td>
					<td align="center" valign=top>
						<table width="98%" cellpadding="0" cellspacing="0" algin="center">
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
												<b>告警 >> SYSLOG管理 >> 浏览STSLOG >> <%
													if (null == tabTitle || "null".equalsIgnoreCase(tabTitle)) {
												%>全部<%
													} else
														out.print(tabTitle);
												%>
												</b>
											</td>
											<td align="right">
												<img src="<%=rootPath%>/common/images/right_t_03.jpg"
													width="5" height="29" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="1">
										<tr>
											<td bgcolor="#ECECEC" width="100%" align='left'>
												开始日期
												<input type="text" name="startdate" value="<%=startdate%>"
													size="10">
												<a onclick="event.cancelBubble=true;"
													href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
													<img id=imageCalendar1 align=absmiddle width=34 height=21
														src="<%=rootPath%>/include/calendar/button.gif" border=0>
												</a>
												截止日期
												<input type="text" name="todate" value="<%=todate%>"
													size="10" />
												<a onclick="event.cancelBubble=true;"
													href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
													<img id=imageCalendar2 align=absmiddle width=34 height=21
														src="<%=rootPath%>/include/calendar/button.gif" border=0>
												</a>
												来源<input type="text" name="processname" value="<%=processname%>">
												消息<input type="text" name="message" value="<%=message%>">
												IP<input type="text" name="ipaddress" value="<%=ipaddress%>">
												<input type="button" name="submitss" value="查询" onclick="query()">
											</td>
										</tr>
									</table>
								</td>
							</tr>
							
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="1">
										<tr>
											<td bgcolor="#ECECEC" width="100%" align='left'>
												<!-- <input name="ipaddress" value="<%=ipaddress%>" type="hidden" /> -->
												<input name="priority" value="<%=priority%>" type="hidden" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="1">
										<tr>
											<td bgcolor="#ECECEC" width="80%" align="center">
												<jsp:include page="../../common/page.jsp">
													<jsp:param name="curpage" value="<%=jp.getCurrentPage()%>" />
													<jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>" />
												</jsp:include>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td align=right bgcolor="#ECECEC">
									<a
										href="<%=rootPath%>/netsyslog.do?action=exportEvtCatelist&ipaddress=<%=ipaddress%>"
										target="_blank"><img name="selDay1" alt='导出EXCEL'
											style="CURSOR: hand"
											src="<%=rootPath%>/resource/image/export_excel.gif" width=18
											border="0">导出EXCEL</a>&nbsp;&nbsp;&nbsp;&nbsp;
									<a
										href="<%=rootPath%>/netsyslog.do?action=exportImpevtCatelistall&ipaddress=<%=ipaddress%>&username=<%=username%>&priority=<%=priority%>&startdate=<%=startdate %>&todate=<%=todate %>&processname=<%=processname %>&message=<%=message %>&streventname=<%=streventname %>"
										target="_blank"><img name="selDay1" alt='导出EXCEL'
											style="CURSOR: hand"
											src="<%=rootPath%>/resource/image/export_excel.gif" width=18
											border="0">导出全部EXCEL</a>&nbsp;&nbsp;&nbsp;&nbsp;
								</td>
							</tr>

							<tr>
								<td colspan="2">
									<table cellspacing="1" cellpadding="0" width="100%">
										<tr align="center" height=28 class="microsoftLook0">
											<td>
												<strong>序号</strong>
											</td>
											<td width="10%">
												<strong>等级</strong>
											</td>
											<td width="15%">
												<strong>设备名</strong>
											</td>
											<td width="40%">
												<strong>描述</strong>
											</td>
											<td>
												<strong>接受时间</strong>
											</td>
											<td>
												<strong>详细信息</strong>
											</td>

										</tr>
										<%
											NetSyslog syslog = null;
											int startRow = jp.getStartRow();
											session.setAttribute("startRow", startRow);
											session.setAttribute("list", list);
											java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat(
													"MM-dd HH:mm");
											String priorityName = "", hostname = "", strIpaddress = "", strMessage = "";
											Long id = 0L;
											Date cc = null;
											for (int i = 0; i < list.size(); i++) {
												syslog = (NetSyslog) list.get(i);
												if(null != syslog){
													priorityName = syslog.getPriorityName(); 
													hostname = syslog.getHostname();
													strIpaddress = syslog.getIpaddress();
													strMessage = syslog.getMessage();
													id = syslog.getId();
													cc = syslog.getRecordtime().getTime();
												}
												String rtime1 = _sdf.format(cc);
										%>

										<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height=25>

											<td>
												&nbsp;<%=startRow + i%></td>
											<td><%=priorityName %>&nbsp;
											</td>
											<td><%=hostname %>(<%=strIpaddress%>)&nbsp;
											</td>
											<td><%=strMessage %></td>
											<td><%=rtime1%></td>
											<td>
												<input type="button" value="详细信息" class="button"
													onclick="javascript:return CreateWindow('<%=rootPath%>/netsyslog.do?action=netsyslogdetail&id=<%=id %>&ipaddress=<%=strIpaddress %>&busi=<%=busi %>')">
											</td>
										</tr>
										<%
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
