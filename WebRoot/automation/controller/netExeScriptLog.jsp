<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.automation.model.CmdResult"%>
<%@page import="java.util.Vector"%>
<%@page import="com.afunms.automation.model.NetCfgFileNode"%>
<%@ include file="/automation/common/globe.inc"%>
<%@ include file="/automation/common/globeChinese.inc"%>

<%@page import="java.util.List"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List list = (List) request.getAttribute("list");
	List resultList = (List) request.getAttribute("resultList");
	String content = (String) request.getAttribute("content");
	String isReturn = (String) request.getAttribute("isReturn");
	String[] ips = (String[]) request.getAttribute("ips");
	String commands = (String) request.getAttribute("commands");
	if (commands == null)
		commands = "";
	String selected0 = "";
	String selected1 = "";
	if (isReturn != null) {
		if (isReturn.equals("0")) {
			selected0 = "selected";
		} else if (isReturn.equals("1")) {
			selected1 = "selected";
		}
	}
	Vector vector = new Vector();
	if (ips != null) {
		for (int i = 0; i < ips.length; i++) {
			vector.add(ips[i]);
		}
	}
	String[] commStr = new String[commands.split("\r\n").length];
	commStr = commands.split("\r\n");
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">

		<script type="text/javascript" src="<%=rootPath%>/automation/js/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>
		<script language="JavaScript" src="<%=rootPath%>/automation/js/date.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/automation/css/ext/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-lang-zh_CN.js" charset="utf-8"></script>
        <script type="text/javascript" src="<%=rootPath%>/automation/js/jquery-1.8.2.min.js"></script>


		<script language="JavaScript" type="text/JavaScript">
  Ext.onReady(function()
{  
$('#fragment-0').hide();
$('#fragment-1').hide();

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
}

);

function CreateWindow(url)
{
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=400,height=400,directories=no,status=no,scrollbars=no,menubar=no")
}   

</script>

		<script language="JavaScript" type="text/JavaScript">


function setClass(){
	document.getElementById('scriptTitle-2').className='detail-data-title';
	
}
function firstFra(){
    document.getElementById('scriptTitle-0').className='detail-data-title';
	document.getElementById('scriptTitle-1').className='detail-data-title-out';
	document.getElementById('scriptTitle-2').className='detail-data-title-out';
	
	$('#fragment-0').show();
	$('#fragment-1').hide();
	$('#fragment-2').hide();
}

function secondFra(){
   var command=$('#commands').val();
   if(command==null||command==""){
    alert("命令不允许为空！");
    return;
   }
    document.getElementById('scriptTitle-0').className='detail-data-title-out';
	document.getElementById('scriptTitle-1').className='detail-data-title';
	document.getElementById('scriptTitle-2').className='detail-data-title-out';
	
	$('#fragment-0').hide();
	$('#fragment-1').show();
	$('#fragment-2').hide();
}

 function exeCmd(){
 var ip=$("[name='checkbox'][checked]").val();
 if(ip==undefined||ip==""){
 alert("请选择设备IP!!!");
 return;
 }
   mainForm.action="<%=rootPath%>/vpntelnetconf.do?action=exeCmd";
	mainForm.submit();
 }
function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
function downloadLog(){
 
   mainForm.action="<%=rootPath%>/autoControl.do?action=downloadLog";
	mainForm.submit();
}
function loadFile(){
window.open("<%=rootPath%>/autoControl.do?action=loadFile","oneping", "height=500, width= 800, top=100, left=100,scrollbars=yes");		
}
function saveFile(){
var commands=$("#commands").text();
reg = new RegExp("(\r)","g");
var value=commands.replace(reg,";;") 
window.open("<%=rootPath%>/autoControl.do?action=saveFile&&commands="+value,"oneping", "height=400, width= 500, top=300, left=100,scrollbars=yes");
				}
</script>


	</head>
	<body id="body" class="body" onload="setClass()">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form id="mainForm" method="post" name="mainForm">
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
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-report">
									<table id="container-main-report" class="container-main-report">
										<tr>
											<td>
												<table id="report-content" class="report-content">
													<tr>
														<td>
															<table width="100%"
																background="<%=rootPath%>/common/images/right_t_02.jpg"
																cellspacing="0" cellpadding="0">
																<tr>
																	<td align="left">
																		<div class="noPrint">
																			<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																				width="5" height="29" />
																		</div>
																	</td>
																	<td class="layout_title">
																		<b>自动化 >> 自动化控制 >> 执行脚本命令</b>
																	</td>
																	<td align="right">
																		<div class="noPrint">
																			<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																				width="5" height="29" />
																		</div>
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
																		<%=scriptTitleTable%>
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
																	<td align=center>

																		<table id="report-data-header"
																			class="report-data-header">
																			<tr>
																				<td>

																					<table id="report-data-header-title"
																						class="report-data-header-title">
																						<tr>
																							<td>
																								&nbsp;<font color="#DC143C"> 执行步骤： 1.命令输入 >> 2.选择设备 >> 3.执行结果</font>
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
																		<div id="fragment-0">
																			<table height="350">
																				<tr>
																					<td>
																						&nbsp;
																					</td>
																					<td>
																						&nbsp;
																					</td>
																				</tr>

																				<tr>
																					<td width="15%">
																						&nbsp;&nbsp;是否有返回结果：
																					</td>
																					<td width="85%">
																						<select name="isReturn" id="isReturn">
																							<option value="0" <%=selected0%>>
																								无
																							</option>
																							<option value="1" <%=selected1%>>
																								有
																							</option>
																						</select>
																					</td>
																				</tr>
																				<tr>
																    <td ></td>
																	<td align=center width="85%">

																		<table id="report-data-header"
																			class="report-data-header">
																			<tr>
																				<td>

																					<table id="report-data-header-title"
																						class="report-data-header-title">
																						<tr>
																						   
																							<td>
																								
																								<a href="#" onclick="loadFile()"><img src="<%=rootPath %>/resource/image/menu/fw.gif"/>加载文件</a>
																								&nbsp;<a href="#" onclick="saveFile()"><img src="<%=rootPath %>/resource/image/menu/sbxh.gif"/>保存文件</a>
																							</td>
																						</tr>
																					</table>
																				</td>
																			</tr>
																		</table>

																	</td>
																</tr>
																				<tr>
																					<td width="15%">
																						&nbsp;&nbsp;请输入脚本命令：
																					</td>
																					<td width="85%">
																						<textarea id="commands" name="commands" rows="10"
																							cols="85"><%=commands%></textarea>

																					</td>
																				</tr>

																				<tr>

																					<td colspan=2 align=center>
																						<br>
																						<input type="button" value="下一步"
																							onclick="secondFra()">
																					</td>
																				</tr>
																				<tr>

																					<td colspan=2 align=center>
																						<br>
																						&nbsp;
																						<br>
																						<br>
																						<br>
																						<br>
																					</td>
																				</tr>
																			</table>

																		</div>
																	</td>
																</tr>
																<tr>
																	<td>

																		<div id="fragment-1">
																			<table cellspacing="0" border="1"
																				bordercolor="#ababab">
																				<tr height=28 style="background: #ECECEC"
																					align="center" class="content-title">
																					<td align="center">
																						<INPUT type="checkbox" name="checkall"
																							onclick="javascript:chkall()">

																					</td>
																					<td align="center">
																						序号
																					</td>
																					<td align="center">
																						IP地址
																					</td>

																					<td align="center">
																						设备类型
																					</td>

																				</tr>
																				<%
																				NetCfgFileNode vo = null;
																					for (int i = 0; i < list.size(); i++) {
																						vo = (NetCfgFileNode) list.get(i);
																						String checked = "";
																						if (vector.contains(vo.getIpaddress()))
																							checked = "checked";
																				%>
																				<tr <%=onmouseoverstyle%>>

																					<td align='center'>
																						<input type="checkbox" name='checkbox'
																							id='checkbox' value="<%=vo.getIpaddress()%>"
																							<%=checked%>></input>
																					</td>
																					<td align='center'>
																						<%=i + 1%>
																					</td>
																					<td align='center'>
																						<%=vo.getIpaddress()%>
																					</td>

																					<td align='center'><%=vo.getDeviceRender()%></td>

																				</tr>
																				<%
																					}
																				%>
																				<tr style="background-color: #ECECEC;">
																					<TD nowrap colspan="7" align=center>
																						<br>
																						<input type="button" style="width: 50" value="上一步"
																							onclick="firstFra()">

																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																						<input type="button" value="执 行" style="width: 50"
																							onclick="exeCmd()">
																						<div id="emample6" class="example">
																					</TD>
																				</tr>

																			</table>
																		</div>

																		<div id="fragment-2">
																			<table>


																				<%
																					if (isReturn != null) {
																						if (isReturn.equals("1")) {
																				%>
																				<tr>
																					<td>
																						&nbsp;
																					</td>
																					<td align=right>
																						<a href="#" onclick="downloadLog()"><font
																							color="blue">导出Log</font>
																						</a>&nbsp;&nbsp;&nbsp;
																					</td>
																				</tr>
																				<tr>
																					<td>
																						执行结果：
																					</td>
																					<td>
																						&nbsp;
																						<textarea id="content" name="content" rows="31"
																							cols="140">
																						 <%=content%>
																						</textarea>
																					</td>
																				</tr>

																				<%
																					} else if (isReturn.equals("0")) {
																							if (resultList != null && resultList.size() > 0) {
																				%>
																				<tr>
																					<td colspan=2>
																						<table>
																							<tr height=28>

																								<td class="report-data-body-title">
																									IP
																								</td>
																								<td class="report-data-body-title">
																									命令
																								</td>
																								<td class="report-data-body-title">
																									执行结果
																								</td>

																							</tr>
																							<%
																								String ip = "";
																											for (int i = 0; i < resultList.size(); i++) {
																												CmdResult cmdResult = (CmdResult) resultList.get(i);
																												String temp = cmdResult.getIp();
																							%>
																							<tr bgcolor="#ffffff" height=25>

																								<%
																									if (!ip.equals(temp)) {
																								%>
																								<%
																									if (cmdResult.getResult().equals("登录失败!")) {
																								%>
																								<td class="report-data-body-list" align=center><%=cmdResult.getIp()%></td>
																								<%
																									} else {
																								%>
																								<td class="report-data-body-list" align=center
																									rowspan=<%=commStr.length%>><%=cmdResult.getIp()%></td>
																								<%
																									}
																													}
																													if (cmdResult.getResult().equals("执行成功!")) {
																								%>
																								<td class="report-data-body-list" align=center><%=cmdResult.getCommand()%></td>
																								<td class="report-data-body-list" align=center><%=cmdResult.getResult()%></td>
																								<%
																									} else {
																								%>
																								<td class="report-data-body-list" align=center>
																									<font color=red><%=cmdResult.getCommand()%></font>
																								</td>
																								<td class="report-data-body-list" align=center>
																									<font color=red><%=cmdResult.getResult()%></font>
																								</td>
																								<%
																									}
																								%>
																							</tr>
																							<%
																								ip = cmdResult.getIp();
																											}
																							%>
																						</table>
																					</td>
																				</tr>
																				<%
																					}
																						}
																					}
																				%>


																				<tr>
																					<td colspan=2 align=center>

																						<input type="button" value="上一步"
																							onclick="secondFra()">
																					</td>
																				</tr>
																			</table>

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
					</td>
				</tr>
			</table>

		</form>
	</BODY>
</HTML>