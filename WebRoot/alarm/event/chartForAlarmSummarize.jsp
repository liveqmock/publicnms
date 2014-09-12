<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.HashMap"%>
<%
	String rootPath = request.getContextPath();

	HashMap datamap = (HashMap) request.getAttribute("datamap");
	String[][] tableData = (String[][]) datamap.get("tabledata");
	String piedata = (String) datamap.get("piedata");
	String columndata = (String) datamap.get("columndata");
	String closedAlarmPicFile = (String) datamap.get("closedAlarmPicFile");
	String dayalarmData = (String) datamap.get("dayalarmData");
	String weekalarmData = (String) datamap.get("weekalarmData");
	String menuTable = (String) request.getAttribute("menuTable");
%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/xml/flush/amcolumn/swfobject.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
	</head>
	<body id="body" class="body">
		<span id="rootpath" value="<%=rootPath%>"></span>
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content" style="width: 99%" border="0">
										<tr>
											<td>
												<table id="content-body" class="content-body" style="border-left: #737272 0px solid; border-right: #737272 0px solid;">
													<tr>
														<td width="200" valign=top align=center>
															<%=menuTable%>
														</td>
														<td>
															<table style="margin-top: -2px;">
																<tr>
																	<td align='center' height="24" colspan="2">
																		<table id="content-header" class="content-header">
																			<tr>
																				<td align='center' height="29" class="content-title" style="text-align: center;">
																					<b>告警汇总信息</b>
																				</td>
																			<tr>
																		</table>
																	</td>
																</tr>
																<tr>
																	<td bgcolor="#FFFFFF">
																		<table>
																			<tr>
																				<td valign=top>
																					<table>
																						<tr>
																							<td align=center>
																								<div id="pie">
																								</div>

																								<script type="text/javascript">
																								<% if(!piedata.equals("0")){ %>	
																									// <![CDATA[	
																									var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","380", "280", "8", "#FFFFFF");
																									so.addVariable("path", "<%=rootPath%>/amchart/");
																									so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/alarmStatepie.xml"));
																									so.addVariable("chart_data","<%=piedata%>");
																									so.write("pie");
																									// ]]>
																									<%}else{%>
																									var _div=document.getElementById("pie");
																									var img=document.createElement("img");
																									img.setAttribute("src","<%=rootPath%>/resource/image/nodata.gif");
																									img.setAttribute("valign","center");
																									img.setAttribute("width","380");
																									img.setAttribute("height","280");
																									_div.appendChild(img);
																									<%}%>
																								</script>
																							</td>
																						</tr>
																						<tr>
																							<td>
																								<table>
																									<tr>
																										<td width=60>
																										</td>
																										<td>
																											<img src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=closedAlarmPicFile%>" id="closedAlarmPicFile">
																										</td>
																									</tr>
																								</table>
																							</td>
																						</tr>
																					</table>
																				</td>
																				<td valign=top>
																					<table>
																						<tr>
																							<td>
																								<table cellpadding=8 height=296 border="1" bordercolor="#7599d7" style="border: 1px solid #7599d7; border-collapse: collapse;">

																									<%
																										for (int i = 0; i < tableData.length; i++) {
																									%>
																									<tr>
																										<%
																											for (int j = 0; j < tableData[i].length; j++) {
																													if (j == 0) {
																										%>
																										<td width="80" height="25" align=center><%=tableData[i][j]%></td>
																										<%
																											} else if (i == 0 && j == 1) {//普通
																										%>
																										<td width="60" height="25" align=center bgcolor="blue"><%=tableData[i][j]%></td>
																										<%
																											} else if (i == 0 && j == 2) {//严重
																										%>
																										<td width="60" height="25" align=center bgcolor="yellow"><%=tableData[i][j]%></td>
																										<%
																											} else if (i == 0 && j == 3) {//紧急
																										%>
																										<td width="60" height="25" align=center bgcolor="orange"><%=tableData[i][j]%></td>
																										<%
																											} else if (i == 0 && j == 4) {//紧急
																										%>
																										<td width="60" height="25" align=center bgcolor="red"><%=tableData[i][j]%></td>
																										<%
																											} else {
																										%>
																										<td width="60" height="25" align=center><%=tableData[i][j]%></td>
																										<%
																											}
																												}
																										%>
																									</tr>
																									<%
																										}
																									%>
																								</table>
																							</td>
																						</tr>
																						<tr>
																							<td>
																								<div id="column">
																									<strong>You need to upgrade your Flash Player</strong>
																								</div>
																								<script type="text/javascript">
																									// <![CDATA[		
																									var so = new SWFObject("<%=rootPath%>/amchart/amcolumn.swf", "amcolumn","480", "320", "8", "#FFFFF");
																									so.addVariable("path", "<%=rootPath%>/amchart/");
																									so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/lastweekColumn.xml"));
																									so.addVariable("chart_data", "<%=columndata%>");
																									so.write("column");
																									// ]]>
																								</script>
																							</td>
																						<tr>
																					</table>
																				</td>
																				<td>
																					<table>
																						<tr>
																							<td>
																								<div id="lineOne">
																									<strong>You need to upgrade your Flash Player</strong>
																								</div>
																								<script type="text/javascript">
																								// <![CDATA[		
																								var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "amline","280", "300", "8", "#FFFFFF");
																								so.addVariable("path", "<%=rootPath%>/amchart/");
																								so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/dayalarm_setting.xml"));
																								so.addVariable("chart_data", "<%=dayalarmData%>");
																								so.write("lineOne");
																								// ]]>
																								</script>
																							</td>
																						</tr>
																						<tr>
																							<td>
																								<div id="lineTwo">
																									<strong>You need to upgrade your Flash Player</strong>
																								</div>
																								<script type="text/javascript">
																									// <![CDATA[		
																									var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "amline","280", "300", "8", "#FFFFFF");
																									so.addVariable("path", "<%=rootPath%>/amchart/");
																									so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/weekalarm_settings.xml"));
																									so.addVariable("chart_data", "<%=weekalarmData%>");
																									so.write("lineTwo");
																									// ]]>
																								</script>
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
													<tr style="height: 7px; background: #bec7ce;">
														<td>
															<div>
															</div>
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
	</body>
</html>
