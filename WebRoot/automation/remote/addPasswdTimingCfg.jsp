<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.alarm.model.AlarmIndicatorsNode"%>
<%@page import="com.afunms.alarm.model.AlarmWay"%>
<%@page import="java.util.Hashtable"%>
<%@ include file="/automation/common/globe.inc"%>

<%
	String rootPath = request.getContextPath();
	AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)request.getAttribute("alarmIndicatorsNode");
	Hashtable alarmWayHashtable = (Hashtable)request.getAttribute("alarmWayHashtable");
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/swfobject.js"></script>
		<script  type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/wfm.js"></script>ce/js/wfm.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/automation/css/ext/ext-all.css"
			charset="gb2312" />
		<script type="text/javascript"
			src="<%=rootPath%>/automation/js/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/automation/js/ext/ext-lang-zh_CN.js"
			charset="utf-8"></script>
        <script type="text/javascript" src="<%=rootPath%>/automation/js/timeShareConfigdiv.js" charset="gb2312"></script>
        

		<script language="JavaScript" type="text/javascript">

Ext.onReady(function()
{
 Ext.get("process").on("click",function(){
  
    var chk1 = checkinput("ipaddress","string","网络设备",50,false);
    if(chk1)
    {
    	Ext.MessageBox.wait('数据加载中，请稍后.. ');
    	mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=passwdTimingBackup";
    	mainForm.submit();
    }
 });
});

//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
//-- nielin modify at 2010-01-04 end ----------------
function showup(){
	var url="<%=rootPath%>/vpntelnetconf.do?action=multi_netip";
	window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
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
	initAttribute();
	 // nielin add for time-sharing at 2010-01-04
}
function timeType(obj){
	var type = obj.value;
	document.getElementById('td_sendtimehou').style.display='none';
	document.getElementById('td_sendtimeday').style.display='none';
	document.getElementById('td_sendtimeweek').style.display='none';
	document.getElementById('td_sendtimemonth').style.display='none';
	if(type=='每天'){
		document.getElementById('td_sendtimehou').style.display='';
	}else if(type=='每周'){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeweek').style.display='';
	}else if(type=='每月'){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeday').style.display='';
	}else if(type=='每季'){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeday').style.display='';
		document.getElementById('td_sendtimemonth').style.display='';
	}else if(type=='每年'){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeday').style.display='';
		document.getElementById('td_sendtimemonth').style.display='';
	}
}
</script>
		<script type="text/javascript">
			function showTelnetNetList(){
				var url="<%=rootPath%>/vpntelnetconf.do?action=multi_telnet_netip";
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
			function showWarnList(){
				var url="<%=rootPath%>/vpntelnetconf.do?action=multi_warn_netip";
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
			
			
			function initAttribute(){
		
		<%
			if(alarmWayHashtable != null){
				AlarmWay alarmWay0 = (AlarmWay)alarmWayHashtable.get("way0");
				if(alarmWay0!=null){
					%>
					document.getElementById("way0-id").value = "<%=alarmIndicatorsNode.getWay0()%>";
					document.getElementById("way0-name").value = "<%=alarmWay0.getName()%>";
					<%
				}
			}
		%>
	}
			
			function chooseAlarmWay(value,alarmWayIdEvent,alarmWayNameEvent){
				CreateWindow("<%=rootPath%>/vpntelnetconf.do?action=chooselist&jp=1&alarmWayNameEvent=" + alarmWayNameEvent + "&alarmWayIdEvent=" + alarmWayIdEvent);
			}
		</script>

	</head>
	<body id="body" class="body" onload="initmenu();">
		<form name="mainForm" method="post">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-add">
								<table id="container-main-add" class="container-main-add">
									<tr>
										<td>
											<table id="add-content" class="add-content">
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">&nbsp;定时提醒</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
													 						<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="10%">
																					网络设备&nbsp;
																				</TD>
																				<TD nowrap width="40%" colspan=3>
																					&nbsp;
																					<input name="ipaddress" type="text" size="50" class="formStyle" id="ipaddress">
																					<input type="button" value="选择网络设备" onclick="showTelnetNetList()"><font color="red">&nbsp;* </font>
																				</TD>
																			</tr>
													 						<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="10%">
																					提醒方式&nbsp;
																				</TD>
																				<TD nowrap width="40%" colspan=3>
																					&nbsp;
																					<input name="way-name" type="text" size="50" class="formStyle" id="way-name">
																					<input type="hidden" value="" name="way-id" id="way-id">
																					<a href="#" onclick="chooseAlarmWay('','way-id','way-name')">浏览<font color="red">&nbsp;* </font>
																				</TD>
																			</tr>
													 						<tr style="background-color: #ECECEC;">
													 							
																			</tr>
													 						<tr style="background-color: #ECECEC;">
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24">定时提醒时间&nbsp;</TD>
																				<td nowrap  colspan="3">
																			        <div id="formDiv" style="">         
																		                <table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none border=1 align="center" >
																	                        <tr>
																	                            <td align="left">  
																		                            <br>
																	                                <table id="timeConfigTable" style="width:60%; padding:0;  background-color:#FFFFFF; position:relative; left:15px;" >
																                                        <TBODY>
																                                      
																										 <tr><TD style="WIDTH: 100px"><span>发送时间:</span></TD></tr>
																										 <tr><TD style="WIDTH: 100px">&nbsp;</TD></tr>
																										  <TR>
																										    <TD>
																										    <SELECT style="WIDTH: 250px" id=transmitfrequency 
																										      onchange="javascript:timeType(this)" 
																										      name=transmitfrequency> <OPTION value="每天" selected >每天</OPTION> <OPTION 
																										        value="每周">每周</OPTION> <OPTION value="每月">每月</OPTION> <OPTION 
																										        value="每季">每季</OPTION> <OPTION value="每年">每年</OPTION></SELECT>
																										  </TD>
																										  
																										  </TR>
																										   <tr><TD style="WIDTH: 100px">&nbsp;</TD></tr>
																										  <TR>
																										    <TD style="display: none;" id=td_sendtimemonth><SELECT 
																										      style="WIDTH: 250px" id=sendtimemonth multiple size=5 
																										      name=sendtimemonth> <OPTION selected value="01">01月</OPTION> 
																										        <OPTION value="02">02月</OPTION> <OPTION value="03">03月</OPTION> <OPTION 
																										        value="04">04月</OPTION> <OPTION value="05">05月</OPTION> <OPTION 
																										        value="06">06月</OPTION> <OPTION value="07">07月</OPTION> <OPTION 
																										        value="08">08月</OPTION> <OPTION value="09">09月</OPTION> <OPTION 
																										        value="10">10月</OPTION> <OPTION value="11">11月</OPTION> <OPTION 
																										        value="12">12月</OPTION></SELECT>
																											</TD>
																										    <TD style="display: none;" id=td_sendtimeweek><SELECT 
																										      style="WIDTH: 250px" id=sendtimeweek multiple size=5 
																										      name=sendtimeweek> <OPTION selected value="0">星期日</OPTION> <OPTION 
																										        value="1">星期一</OPTION> <OPTION value="2">星期二</OPTION> <OPTION 
																										        value="3">星期三</OPTION> <OPTION value="4">星期四</OPTION> <OPTION 
																										        value="5">星期五</OPTION> <OPTION value="6">星期六</OPTION></SELECT>
																											</TD>
																										    <TD style="display: none;" id=td_sendtimeday><SELECT style="WIDTH: 250px" 
																										      id=sendtimeday multiple size=5 name=sendtimeday> <OPTION 
																										        selected value="01">01日</OPTION> <OPTION value="02">02日</OPTION> <OPTION 
																										        value="03">03日</OPTION> <OPTION value="04">04日</OPTION> <OPTION 
																										        value="05">05日</OPTION> <OPTION value="06">06日</OPTION> <OPTION 
																										        value="07">07日</OPTION> <OPTION value="08">08日</OPTION> <OPTION 
																										        value="09">09日</OPTION> <OPTION value="10">10日</OPTION> <OPTION 
																										        value="11">11日</OPTION> <OPTION value="12">12日</OPTION> <OPTION 
																										        value="13">13日</OPTION> <OPTION value="14">14日</OPTION> <OPTION 
																										        value="15">15日</OPTION> <OPTION value="16">16日</OPTION> <OPTION 
																										        value="17">17日</OPTION> <OPTION value="18">18日</OPTION> <OPTION 
																										        value="19">19日</OPTION> <OPTION value="20">20日</OPTION> <OPTION 
																										        value="21">21日</OPTION> <OPTION value="22">22日</OPTION> <OPTION 
																										        value="23">23日</OPTION> <OPTION value="24">24日</OPTION> <OPTION 
																										        value="25">25日</OPTION> <OPTION value="26">26日</OPTION> <OPTION 
																										        value="27">27日</OPTION> <OPTION value="28">28日</OPTION> <OPTION 
																										        value="29">29日</OPTION> <OPTION value="30">30日</OPTION> <OPTION 
																										        value="31">31日</OPTION></SELECT>
																											</TD>
																										    <TD style="" id=td_sendtimehou><SELECT 
																										      style="WIDTH: 250px" id=sendtimehou multiple size=5 
																										      name=sendtimehou> <OPTION value="00AM">00AM</OPTION> <OPTION 
																										       value=01>01AM</OPTION> <OPTION value=02>02AM</OPTION> <OPTION selected 
																										        value=03>03AM</OPTION> <OPTION value=04>04AM</OPTION> <OPTION 
																										        value=05>05AM</OPTION> <OPTION value=06>06AM</OPTION> <OPTION 
																										        value=07>07AM</OPTION> <OPTION value=08>08AM</OPTION> <OPTION 
																										        value=09>09AM</OPTION> <OPTION value=10>10AM</OPTION> <OPTION 
																										        value=11>11AM</OPTION> <OPTION value=12>12AM</OPTION> <OPTION 
																										        value=13>01PM</OPTION> <OPTION value=14>02PM</OPTION> <OPTION 
																										        value=15>03PM</OPTION> <OPTION value=16>04PM</OPTION> <OPTION 
																										        value=17>05PM</OPTION> <OPTION value=18>06PM</OPTION> <OPTION 
																										        value=19>07PM</OPTION> <OPTION value=20>08PM</OPTION> <OPTION 
																										        value=21>09PM</OPTION> <OPTION value=22>10PM</OPTION> <OPTION 
																										        value=23>11PM</OPTION></SELECT>
																											</TD>
																										  </TR>
																									  </TBODY>
																	                                </table>
																	                            </td>
																	                        </tr>
																		                </table>
																		            </div> 
																				</td>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="10%">
																					启动定时&nbsp;
																				</TD>
																				<TD nowrap width="40%" colspan=3>
																					&nbsp;
																					<select name="status">
																						<option value="是" label="是">是</option>
																						<option value="否" label="否">否</option>
																					</select>
																				</TD>
																			</tr>
																			<tr>
																				<TD nowrap colspan="4" align=center>
																				<br>
																					<input type="button" id="process" style="width:50" value="确 定">&nbsp;&nbsp;  
																					<input type="reset" style="width: 50" value="返  回" onclick="javascript:history.back(1)">
																				</TD>	
																			</tr>	
																		</TABLE>
										 							
										 							
				        										</td>
				        									</tr>
				        								</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-footer" class="detail-content-footer">
				        									<tr>
				        										<td>
				        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
											                  			<tr>
											                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
											                    			<td></td>
											                    			<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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
</HTML>