<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.automation.model.TimingBackupCfgFile"%>
<%@page import="com.afunms.automation.util.AutomationUtil"%>
<%@ include file="/automation/common/globe.inc"%>
<%@page import="java.util.*"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List<TimingBackupCfgFile> timingBackupTelnetConfigList = (ArrayList<TimingBackupCfgFile>) request.getAttribute("timingBackupTelnetConfigList");
	String runImg = rootPath + "/img/Config-Running.gif";
	String startupImg = rootPath + "/img/Config-Startup.gif";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
		
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
        <script src="<%=rootPath%>/automation/js/jquery-1.8.2.min.js" type="text/javascript"></script>
		<script src="<%=rootPath%>/automation/js/contextmenu.js" type="text/javascript"></script>
		<link href="<%=rootPath%>/automation/css/contextmenu.css" rel="stylesheet">
		<LINK href="<%=rootPath%>/automation/css/style.css" type="text/css" rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/timeShareConfigdiv.js" charset="gb2312"></script>
        
		<script language="JavaScript" type="text/javascript">

 Ext.onReady(function()
{
 Ext.get("process").on("click",function(){
  
    var chk1 = checkinput("ipaddress","string","网络设备",50,false);
    if(chk1)
    {
    	Ext.MessageBox.wait('数据加载中，请稍后.. ');
    	mainForm.action = "<%=rootPath%>/autoControl.do?action=saveTimingCmdScan";
    	mainForm.submit();
    }
 });
});

//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
function showup(){
	var url="<%=rootPath%>/vpntelnetconf.do?action=multi_netip";
	window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
}

function timeType(obj){
	var type = obj.value;
	document.getElementById('td_sendtimehou').style.display='none';
	document.getElementById('td_sendtimeday').style.display='none';
	document.getElementById('td_sendtimeweek').style.display='none';
	document.getElementById('td_sendtimemonth').style.display='none';
	if(type==1){
		document.getElementById('td_sendtimehou').style.display='';
	}else if(type==2){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeweek').style.display='';
	}else if(type==3){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeday').style.display='';
	}else if(type==4){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeday').style.display='';
		document.getElementById('td_sendtimemonth').style.display='';
	}else if(type==5){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeday').style.display='';
		document.getElementById('td_sendtimemonth').style.display='';
	}
}

			
			function showTelnetNetList(){
				var url="<%=rootPath%>/vpntelnetconf.do?action=multi_telnet_netip";
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
			
			function toAdd(){
				mainForm.action = "<%=rootPath%>/netCfgFile.do?action=addTimingBackupTelnetConfig";
			    mainForm.submit();
			}
			
			function toDelete(){  
     				mainForm.action = "<%=rootPath%>/netCfgFile.do?action=deleteTimingBackupCfgFile";
     				mainForm.submit();
	  		}
	  		
	  		
		</script>

	</head>
	<body id="body" class="body" >
		

		<form name="mainForm" method="post">

			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-add">
									<table id="container-main-add" class="container-main-add">
										<tr>
											<td >
												<table id="add-content" class="add-content" >
												 <tr>
													<td>
														<table id="add-content-header" class="add-content-header"  >
										                	<tr>
											                	<td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title" width="100%">&nbsp;自动化 >> 配置文件管理 >> 定时备份列表</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						   </tr>
													<tr>
														<td>
															<table width="100%" cellpadding="0" cellspacing="1" >
																<tr>
																	
																	<td bgcolor="#ECECEC" width="40%" align='right'>
																		<a href="#" onclick="toAdd()">添加</a>&nbsp;
																		<a href="#" onclick="toDelete()">删除</a>&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table id="detail-content-body" class="detail-content-body">
																<tr>
																	<td>
																		<table border="0" id="table1" cellpadding="0"
																			cellspacing="0" width="100%">
																			<tr>
																				<td colspan="2">
																					<table cellspacing="0" border="0" bordercolor="#ababab">
																						<tr height=28 style="background: #ECECEC"
																							align="center" class="content-title">
																							<td align="center" class="body-data-title">
																								<INPUT type="checkbox" id="checkall"
																									name="checkall" onclick="javascript:chkall()"
																									class=noborder>
																							</td>
																							<td align="center" class="body-data-title">
																								序号
																							</td>
																							<td align="center" class="body-data-title" width="20%">
																								网络设备IP
																							</td>
																							<td align="center" class="body-data-title">
																								任务生成日期(yyyyMMddhh)
																							</td>
																							<td align="center" class="body-data-title">
																								定时备份日期
																							</td>
																							<td align="center" class="body-data-title">
																								是否定时
																							</td>
																							<td align="center" class="body-data-title">
																								备份类型
																							</td>
																							<td align="center" class="body-data-title">
																								是否有变动时备份
																							</td>
																							<td align="center" class="body-data-title" width="10%">
																								操作
																							</td>
																						</tr>
																						<%
																							if (timingBackupTelnetConfigList != null) {
																								String[] frequencyName = { "每天", "每周", "每月", "每季", "每年" };
																								String[] monthCh = { " 1月", " 2月", " 3月", " 4月", " 5月", " 6月", " 7月", " 8月", " 9月", " 10月", " 11月", " 12月" };
																								String[] weekCh = { " 星期日", " 星期一", " 星期二", " 星期三", " 星期四", " 星期五", " 星期六" };
																								String[] dayCh = null;
																								String[] hourCh = null;
																								for (int i = 0; i < timingBackupTelnetConfigList.size(); i++) {
																									TimingBackupCfgFile timingBackupTelnetConfig = (TimingBackupCfgFile) timingBackupTelnetConfigList.get(i);
																									StringBuffer sb = new StringBuffer();
																									int frequency = timingBackupTelnetConfig.getBackup_sendfrequency();
																									String month = AutomationUtil.splitDate(timingBackupTelnetConfig.getBackup_time_month(), monthCh, "month");
																									String week = AutomationUtil.splitDate(timingBackupTelnetConfig.getBackup_time_week(), weekCh, "week");

																									String day = AutomationUtil.splitDate(timingBackupTelnetConfig.getBackup_time_day(), dayCh, "day");
																									String hour = AutomationUtil.splitDate(timingBackupTelnetConfig.getBackup_time_hou(), hourCh, "hour");
																									sb.append(frequencyName[frequency - 1] + " ");
																									//if (month != null && !month.equals("")){
																									//sb.append(" 月份：(" + month + ")");
																									//}
																									//if (week != null && !week.equals("")){
																									//sb.append(" 星期:(" + week + ")");
																									//}
																									//if (day != null && !day.equals("")){
																									//sb.append(" 日期：(" + day + ")");
																									//}
																									//if (hour != null && !hour.equals("")){
																									//sb.append(" 时间：(" + hour + ")");
																									//}
																									if (frequency == 1) {//每天
																										sb.append(" 时间：(" + hour + ")");
																									}
																									if (frequency == 2) {//每周
																										sb.append(" 星期:(" + week + ")");
																										sb.append(" 时间：(" + hour + ")");
																									}
																									if (frequency == 3) {//每月
																										sb.append(" 日期：(" + day + ")");
																										sb.append(" 时间：(" + hour + ")");
																									}
																									if (frequency == 4) {//每季
																										sb.append(" 月份：(" + month + ")");
																										sb.append(" 日期：(" + day + ")");
																										sb.append(" 时间：(" + hour + ")");
																									}
																									if (frequency == 5) {//每年
																										sb.append(" 月份：(" + month + ")");
																										sb.append(" 日期：(" + day + ")");
																										sb.append(" 时间：(" + hour + ")");
																									}
																									String status = timingBackupTelnetConfig.getStatus();
																									String bkpType = timingBackupTelnetConfig.getBkpType();
																									String changeName = "";
																									String bkpTypeName = "";
																									String bkpImg = "";
																									if (bkpType.equals("run")) {
																										bkpTypeName = "备份运行文件";
																										bkpImg = "<img src='" + runImg + "'>";
																									}
																									if (bkpType.equals("startup")) {
																										bkpTypeName = "备份启动文件";
																										bkpImg = "<img src='" + startupImg + "'>";
																										;
																									}
																									if (bkpType.equals("all")) {
																										bkpTypeName = "全部备份";
																									}
																									if ("on".equals(timingBackupTelnetConfig.getCheckupdateflag())) {
																										changeName = "是";
																									} else {
																										changeName = "否";
																									}
																						%>
																						<tr <%=onmouseoverstyle%>>
																							<td align='center' class="body-data-list">
																								<INPUT type="checkbox" class=noborder
																									name="checkbox"
																									value="<%=timingBackupTelnetConfig.getId()%>">
																							</td>
																							<td align='center' class="body-data-list">
																								<font color='blue'><%=i + 1%></font>
																							</td>
																							<td align='center' class="body-data-list"><%=timingBackupTelnetConfig.getTelnetconfigips()%></td>
																							<td align='center' class="body-data-list"><%=timingBackupTelnetConfig.getBackup_date()%></td>
																							<td align='center' class="body-data-list"><%=sb.toString() + ""%></td>
																							<td align='center' class="body-data-list"><%="1".equals(status) ? "启动" : "未启动"%></td>
																							<td align='center' class="body-data-list"><%=bkpImg%><%=bkpTypeName%></td>
																							<td align='center' class="body-data-list"><%=changeName%></td>
																							<td align="center" class="body-data-list">
																		                        <input type="hidden" id="id" name="id" value="<%=timingBackupTelnetConfig.getId()%>">
																		                        <img class="img" src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 alt="右键操作">
																	                       </td>
																						</tr>
																						<%
																							}
																							}
																						%>
																					</table>
																				</td>
																			</tr>
																		</TABLE>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table id="detail-content-footer"
																class="detail-content-footer">
																<tr>
																	<td>
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0">
																			<tr>
																				<td align="left" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_01.jpg"
																						width="5" height="12" />
																				</td>
																				<td></td>
																				<td align="right" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_03.jpg"
																						width="5" height="12" />
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
					</td>
				</tr>
			</table>
		</form>
	</body>
	<script type="text/javascript">  
			$('.img').contextmenu({
				height:235,
				width:120,
				items : [{
					text :'编辑',
					icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/edit.gif',
					action: function(target){
						var id=$($(target).parent()).find('#id').val();
						edit(id);
					}
				},{
					text :'启动',
					icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/cancelMng.gif',
					action: function(target){
						var id=$($(target).parent()).find('#id').val();
						addBackup(id);
					}
				},{
					text :'取消启动',
					icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/cancelMng.gif',
					action: function(target){
						var id=$($(target).parent()).find('#id').val();
					}
                }]
			});	
			function edit(id)
			{
				mainForm.action="<%=rootPath%>/netCfgFile.do?action=ready_editTimingBackupTelnetConfig&id="+id;
				mainForm.submit();
			}
			function addBackup(id)
			{
				mainForm.action="<%=rootPath%>/vpntelnetconf.do?action=addBackup&id="+id;
				mainForm.submit();
			}
	  		function disBackup(id)
			{
				mainForm.action="<%=rootPath%>/vpntelnetconf.do?action=disBackup&id="+id;
				mainForm.submit();
			}
  </script>
</HTML>