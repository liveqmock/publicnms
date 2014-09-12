<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.automation.model.NetCfgFileNode"%>
<%@ include file="/automation/common/globe.inc"%>

<%
  String rootPath = request.getContextPath();
  NetCfgFileNode mo=(NetCfgFileNode)request.getAttribute("vpntelnetconf");
  String deviceType = request.getParameter("deviceType");
  %>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/automation/css/ext/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-lang-zh_CN.js" charset="utf-8"></script>
<script language="JavaScript" type="text/JavaScript">
  Ext.onReady(function()
{  

	
 Ext.get("vpn").on("click",function(){
  	Ext.MessageBox.wait('数据加载中，请稍后.. '); 
	mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=updatepassword";

 });	
function tijiao(){
	mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=updatepassword";
	window.close();
}
});

</script>


	</head>
	<body id="body" class="body">



		<form id="mainForm" method="post" name="mainForm" action="<%=rootPath%>/vpntelnetconf.do?action=updatepassword">
			<input type=hidden name="id" value="<%=mo.getId()%>">
			<input type=hidden name="deviceType" value="<%=deviceType %>">
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
																	<td class="add-content-title">
																		&nbsp;>> 修改密码
																	</td>
																	<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table id="detail-content-body"
																class="detail-content-body">
																<tr>
																	<td>

																		<table border="0" id="table1" cellpadding="0"
																			cellspacing="1" width="100%">

																			<tr>
																				<TD nowrap align="right" height="24" width="10%">
																					旧用户&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<input type="text" name="user" size="40"
																						class="formStyle" value="<%=mo.getUser() %>"
																						readonly="readonly">
																					<font color="red">&nbsp;*</font>
																				</TD>
																				<TD nowrap align="right" height="24">
																					旧密码&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<input type="password" name="password" size="40"
																						class="formStyle" value="<%=mo.getPassword() %>"
																						readonly="readonly">
																					<font color="red">&nbsp;*
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24">
																					特权密码&nbsp;
																				</TD>
																				<TD nowrap colspan=3>
																					&nbsp;
																					<input type="password" name="supassword" size="35"
																						class="formStyle" value="<%=mo.getSupassword() %>"
																						readonly="readonly">
																				</TD>
																			</tr>

																			<tr>
																				<TD nowrap align="right" height="24">
																					ip&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<input name="ipaddress" type="text" size="40"
																						class="formStyle" value="<%=mo.getIpaddress() %>"
																						readonly="readonly">
																					<font color="red" readonly="readonly">&nbsp;*
																					</font>
																				</TD>
																				<td align="right" height="20">
																					端口&nbsp;
																				</td>
																				<td colspan="3" align="left">
																					&nbsp;
																					<input name="port" type="text" size="16"
																						class="formStyle" value="<%=mo.getPort() %>"
																						readonly="readonly">
																					<font color="red">&nbsp;* </font>
																				</td>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24">
																					新用户&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<input name="modifyuser" type="text" size="40"
																						class="formStyle" value="<%=mo.getUser() %>">
																					<font color="red">&nbsp;* </font>
																				</TD>
																				<td align="right" height="20">
																					&nbsp;新密码
																				</td>
																				<td colspan="3" align="left">
																					&nbsp;
																					<input name="newpassword" type="password" size="40"
																						class="formStyle">
																				</td>
																			</tr>

																			<tr>
																				<TD nowrap colspan="4" align=center>
																					<br>
																					<input type="submit" value="修 改" style="width: 50" id="vpn" onclick="#">&nbsp;&nbsp;
																					<input type="button" value="关 闭" style="width:50" id="vpn1" onclick="window.close()" >
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
															<table id="detail-content-footer"
																class="detail-content-footer">
																<tr>
																	<td>
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0">
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
										<tr>
											<td align="right">

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