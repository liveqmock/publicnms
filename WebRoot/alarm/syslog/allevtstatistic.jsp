<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.event.model.NetSyslogAllEvent"%>
<%@page import="java.util.List"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List<NetSyslogAllEvent> allEvtList =(List<NetSyslogAllEvent>)request.getAttribute("allEvtList");
	String ipaddress = (String)request.getAttribute("ipaddress");
	String startdate = (String) request.getAttribute("startdate");
	String todate = (String) request.getAttribute("todate");
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="utf-8" />
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>



		<script language="JavaScript" type="text/JavaScript">
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
});

function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=400,height=400,directories=no,status=no,scrollbars=no,menubar=no")
}   

function setBid(eventTextId , eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/business.do?action=setBidbyuser&event='+event.id+'&value='+event.value + '&eventText=' + eventTextId);
}

function query()
  {  
     mainForm.action = "<%=rootPath%>/netreport.do?action=find&netflag=1";
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
	setClass();
}

function setClass(){
	document.getElementById('netSyslogTitle-1').className='detail-data-title';
	document.getElementById('netSyslogTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('netSyslogTitle-1').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>


	</head>
	<body id="body" class="body" onload="initmenu();">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form id="mainForm" method="post" name="mainForm">
			<input name="ipaddress" value="<%=ipaddress%>" type="hidden"/>
			<input name="startdate" value="<%=startdate%>" type="hidden"/>
			<input name="todate" value="<%=todate%>" type="hidden"/>
			<div id="loading">
				<div class="loading-indicator">
					<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif"
						width="32" height="32" style="margin-right: 8px;" align="middle" />
					Loading...
				</div>
			</div>
			<div id="loading-mask" style=""></div>
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-report">
									<table id="container-main-report" class="container-main-report">
										<tr>
											<td>
												<table id="report-content" class="report-content">
													<tr>
														<td	background="<%=rootPath%>/common/images/right_t_02.jpg"
															width="100%">
															<table width="100%" cellspacing="0" cellpadding="0">
																<tr>
																	<td align="left">
																		<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																			width="5" height="29" />
																	</td>
																	<td class="layout_title">
																		<b>告警 >> SYSLOG管理 >> STSLOG详细 >> 全部事件</b>
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
															<table id="report-content-header"
																class="report-content-header">
																<tr>
																	<td>
																		<%=netSyslogTitleTable%>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table id="report-content-body"
																class="report-content-body">
																<tr>
																	<td>

																		<table id="report-data-header"
																			class="report-data-header">
																			<tr>
																				<td>

																					<table id="report-data-header-title"
																						class="report-data-header-title">
																						<tr>
																							<td>
																								设备IP地址：<%=ipaddress %>
																							</td>
																						</tr>
																					</table>
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td>

																		<table id="report-data-body" class="report-data-body">
																			<tr height=28 class="microsoftLook0">
																				<td width="40%" class="report-data-body-title">进程名</td>
																				<td class="report-data-body-title">错误</td>
																				<td class="report-data-body-title">警告</td>
																				<td class="report-data-body-title">失败</td>
																				<td class="report-data-body-title">其他</td>
																				<td class="report-data-body-title">全部</td>
																			</tr>
																			<%
																			if(null == allEvtList || allEvtList.size() == 0 ){
																			%>
																			<tr>
																				<td class="report-data-body-title" colspan="6">没有Syslog日志信息</td>
																			</tr>
																			<%}else{ 
																				for(NetSyslogAllEvent event : allEvtList){
																			%>
																			<tr  align="center" class="microsoftLook0" bgcolor="#ffffff" <%=onmouseoverstyle%> height=25>
																				<td width="40%" class="report-data-body-list"><%=event.getProcessname() %></td>
																				<td class="report-data-body-list"><a <%if(event.getErrors()>0){%> href="<%=rootPath %>/netsyslog.do?action=syslogallevtcatelist&ipaddress=<%=ipaddress %>&priority=error&processname=<%=event.getProcessname() %>&startdate=<%=startdate %>&todate=<%=todate %>&jp=1" <%} %>><%=event.getErrors() %></a></td>
																				<td class="report-data-body-list"><a <%if(event.getWarnings()>0){%> href="<%=rootPath %>/netsyslog.do?action=syslogallevtcatelist&ipaddress=<%=ipaddress %>&priority=warning&processname=<%=event.getProcessname() %>&startdate=<%=startdate %>&todate=<%=todate %>&jp=1" <%} %>><%=event.getWarnings() %></a></td>
																				<td class="report-data-body-list"><a <%if(event.getFailures()>0){%> href="<%=rootPath %>/netsyslog.do?action=syslogallevtcatelist&ipaddress=<%=ipaddress %>&priority=fail&processname=<%=event.getProcessname() %>&startdate=<%=startdate %>&todate=<%=todate %>&jp=1" <%} %>><%=event.getFailures() %></a></td>
																				<td class="report-data-body-list"><a <%if(event.getOthers()>0){%> href="<%=rootPath %>/netsyslog.do?action=syslogallevtcatelist&ipaddress=<%=ipaddress %>&priority=others&processname=<%=event.getProcessname() %>&startdate=<%=startdate %>&todate=<%=todate %>&jp=1" <%} %>><%=event.getOthers() %></a></td>
																				<td class="report-data-body-list"><a <%if(event.getAll()>0){%> href="<%=rootPath %>/netsyslog.do?action=syslogallevtcatelist&ipaddress=<%=ipaddress %>&processname=<%=event.getProcessname() %>&startdate=<%=startdate %>&todate=<%=todate %>&jp=1" <%} %>><%=event.getAll() %></a></td>
																			</tr>
																			<%}} %>
																		</table>
																	</td>
																</tr>

															</table>
														</td>
													</tr>

												</table>
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