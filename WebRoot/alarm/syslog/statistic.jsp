<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@ include file="/include/globe.inc"%>
<%@ page import="com.afunms.event.model.*"%>

<%
	String rootPath = request.getContextPath();
	JspPage jp = (JspPage) request.getAttribute("page");
	String startdate = (String) request.getAttribute("startdate");
	String todate = (String) request.getAttribute("todate");
	String _strclass = (String) request.getParameter("strclass");
	String ipaddress = (String) request.getAttribute("ipaddress");
	if (null == ipaddress)
		ipaddress = "";
	String str1 = "";
	String str2 = "";
	String str3 = "";
	if ("1".equals(_strclass)) {
		str2 = "selected";
	} else if ("2".equals(_strclass)) {
		str3 = "selected";
	} else {
		str1 = "selected";
	}
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");//左侧菜单栏
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
	var curpage= <%=jp.getCurrentPage()%>;
	var totalpages = <%=jp.getPageTotal()%>;
	var delAction = "<%=rootPath%>/netsyslog.do?action=delete";
	var listAction = "<%=rootPath%>/netsyslog.do?action=statistic";
	function ltrim(s){
		return s.replace( /^\s*/, "");
	}
	function rtrim(s){
		return s.replace( /\s*$/, "");
	}
	function trim(s){
		return rtrim(ltrim(s));
	}
	function ipFuzzyMatch(ipaddress){
		if (null == ipaddress || ipaddress == 'undefined' || trim(ipaddress) == '') {
			return true;//ipaddress为空时，按设备类型查询
		}
		ipaddress = trim(ipaddress);
		var arr = ipaddress.split('.');
		if (arr.length > 4) {
			alert("IP地址过长，请检查！");
			return false;
		}else{
			for (var index = 0; index < arr.length; index++) {
				if(arr[index] == ""){
					if (index != 0 || index != arr.length-1) {
						alert("IP地址中间不能为空值！");
						return false;
					}
					continue;
				}else if (isNaN(arr[index])) {
					alert("IP必须是数字！");
					return false;
				}else{
					if (parseInt(arr[index]) > 255) {
						alert("IP只能在255范围内！");
						return false;
					}
				}
			}
			return true;
		}
	}
	
	function ipPrecMatch(ipaddress){
		var ipReg = /^(((\d{1,2})|(1\d{2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d{1,2})|(1\d{2})|(2[0-4]\d)|(25[0-5]))$/;
		if(!ipReg.test(ipaddress)){
  			alert("IP地址为空或格式不正确，请重新输入！");
   			return false;
		}
		return true;
	}

  function query(){
  	if(mainForm.strclass.value != '-1'){
		if(!ipFuzzyMatch(mainForm.ipaddress.value))return false;
	}
   	mainForm.action = "<%=rootPath%>/netsyslog.do?action=statistic&jp=1";
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
			<input type=hidden name="eventid">
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
												<b>告警 >> SYSLOG管理 >> STSLOG详细 </b>
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
												</a>&nbsp;&nbsp;&nbsp;&nbsp; 类别
												<select name="strclass">
													<option value="-1" <%=str1%>>
														不限
													</option>
													<option value="1" <%=str2%>>
														主机
													</option>
													<option value="2" <%=str3%>>
														网络
													</option>
												</select>
												&nbsp;&nbsp;&nbsp;&nbsp; IP
												<input type="text" name="ipaddress" value="<%=ipaddress%>">
												<input type="button" name="submitss" value="查询"
													onclick="query()">
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
									<a href="<%=rootPath%>/netsyslog.do?action=exportStatistic"
										target="_blank"><img name="selDay1" alt='导出EXCEL'
											style="CURSOR: hand"
											src="<%=rootPath%>/resource/image/export_excel.gif" width=18
											border="0">导出EXCEL</a>&nbsp;&nbsp;&nbsp;&nbsp;
									<a
										href="<%=rootPath%>/netsyslog.do?action=exportStatisticall&ipaddress=<%=ipaddress%>&startdate=<%=startdate%>&todate=<%=todate%>&strclass=<%=_strclass%>"
										target="_blank"><img name="selDay1" alt='导出EXCEL'
											style="CURSOR: hand"
											src="<%=rootPath%>/resource/image/export_excel.gif" width=18
											border="0">导出全部EXCEL</a>
								</td>
							</tr>

							<tr>
								<td colspan="2">
									<table cellspacing="1" cellpadding="0" width="100%">
										<tr align="center" height=28 class="microsoftLook0">
											<td>
												<font style="font-weight: bold">序号</font>
											</td>
											<td width="15%">
												<font style="font-weight: bold">名称</font>
											</td>
											<td>
												<strong>IP地址</strong>
											</td>
											<td>
												<strong>设备类型</strong>
											</td>
											<td width="10%">
												<strong>状态</strong>
											</td>
											<td>
												<strong>错误</strong>
											</td>
											<td>
												<strong>警告</strong>
											</td>
											<td>
												<strong>失败</strong>
											</td>
											<td>
												<strong>其他</strong>
											</td>
											<td>
												<strong>全部</strong>
											</td>
										</tr>

										<%
											List viewersList = (List) request.getAttribute("viewersList");
											session.setAttribute("list", viewersList);
											int startRow = jp.getStartRow();
											session.setAttribute("startRow", startRow);
											if (null == viewersList || viewersList.size() == 0) {
										%>
										<tr>
											<td colspan="2" align="center">
												没有Syslog日志信息
											</td>
										</tr>
										<%
											} else {
												NetSyslogViewer viewer = null;
												for (int i = 0; i < viewersList.size(); i++) {
													viewer = (NetSyslogViewer) viewersList.get(i);
													String status = viewer.getStatus();
										%>

										<!-- <tr align="center" height=28 class="microsoftLook0"> -->
										<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height=25>
											<td>
												&nbsp;<%=startRow + i%></td>
											<td width="15%">
												<a
													href="<%=rootPath%>/netsyslog.do?action=syslogimpevt&ipaddress=<%=viewer.getIpaddress()%>&startdate=<%=startdate%>&todate=<%=todate%>"><%=viewer.getHostName()%></a>
											</td>
											<td><%=viewer.getIpaddress()%></td>
											<td><%=com.afunms.common.util.SyslogFinals.devCategoryMap.get(viewer.getCategory())	%></td>
											<%
												if ("0".equals(status)) {
											%>
											<td width="10%">
												<img title="停止采集日志"
													src="<%=rootPath%>/resource/image/statusCancelled.gif" />
											</td>
											<%
												} else {
											%>
											<td width="10%">
												<img title="正在采集日志"
													src="<%=rootPath%>/resource/image/statusOK.gif" />
											</td>
											<%
												}
											%>
											<td align="center">
												<a <%if(viewer.getErrors()>0){%>
													href="<%=rootPath%>/netsyslog.do?action=catelist&priority=error&ipaddress=<%=viewer.getIpaddress()%>&startdate=<%=startdate%>&todate=<%=todate%>" <%} %>><%=viewer.getErrors()%></a>
											</td>
											<td align="center">
												<a <%if(viewer.getWarnings()>0){%>
													href="<%=rootPath%>/netsyslog.do?action=catelist&priority=warning&ipaddress=<%=viewer.getIpaddress()%>&startdate=<%=startdate%>&todate=<%=todate%>" <%} %>><%=viewer.getWarnings()%></a>
											</td>
											<td align="center">
												<a <%if(viewer.getFailures()>0){%>
													href="<%=rootPath%>/netsyslog.do?action=catelist&priority=failure&ipaddress=<%=viewer.getIpaddress()%>&startdate=<%=startdate%>&todate=<%=todate%>" <%} %>><%=viewer.getFailures()%></a>
											</td>
											<td align="center">
												<a <%if(viewer.getOthers()>0){%>
													href="<%=rootPath%>/netsyslog.do?action=catelist&priority=others&ipaddress=<%=viewer.getIpaddress()%>&startdate=<%=startdate%>&todate=<%=todate%>" <%} %>><%=viewer.getOthers()%></a>
											</td>
											<td align="center">
												<a <%if(viewer.getAll()>0){%>
													href="<%=rootPath%>/netsyslog.do?action=catelist&ipaddress=<%=viewer.getIpaddress()%>&startdate=<%=startdate%>&todate=<%=todate%>" <%} %>><%=viewer.getAll()%></a>
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
