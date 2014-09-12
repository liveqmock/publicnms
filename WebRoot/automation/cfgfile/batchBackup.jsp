<%@page language="java" contentType="text/html;charset=gb2312"%>

<%
	String rootPath = request.getContextPath();
	String fileName = (String) request.getAttribute("fileName");
	String filena = fileName.substring(fileName.lastIndexOf("\\") + 1);
	
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/swfobject.js"></script>
		<script  type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/wfm.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
        <link rel="stylesheet" type="text/css" href="<%=rootPath%>/automation/css/ext/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-lang-zh_CN.js" charset="utf-8"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/timeShareConfigdiv.js" charset="gb2312"></script>

		<script language="JavaScript" type="text/javascript">

 Ext.onReady(function()
{

        Ext.get("backup").on("click",function(){
     	Ext.MessageBox.wait('数据加载中，请稍后.. ');
        mainForm.action = "<%=rootPath%>/netCfgFile.do?action=bkpCfg_forBatch";
        mainForm.submit();
 });
});


			function showup(){
				var url="<%=rootPath%>/autoControl.do?action=multi_telnet_netip";
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
		</script>

	</head>
	<body id="body" class="body" >



		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" name="fileName" value="<%=fileName%>">
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
																		&nbsp;批量备份
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
																			<!-- 
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="10%">
																					文件名&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<input type="text" name="filena" maxlength="50"
																						size="50" class="formStyle" value="<%=filena%>"
																						readonly="readonly">
																					<input type="hidden" name="fileName"
																						value="<%=fileName%>">
																				</TD>
																			</tr>
																			 -->
																			<tr>
																				<TD nowrap align="right" height="24" width="10%">
																					文件备注&nbsp;
																				</TD>
																				<TD nowrap width="40%" colspan=3>
																					&nbsp;
																					<input type="text" id="fileDesc" name="fileDesc"
																						maxlength="32" size="50" value="" onclick="#">
																					&nbsp;&nbsp;
																				</td>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="10%">
																					网络设备&nbsp;
																				</TD>
																				<TD nowrap width="40%" colspan=3>
																					&nbsp;
																					<input name="ipaddress" type="text" size="40" class="formStyle" id="ipaddress" readonly="readonly" >
																					<input type="button" value="选择网络设备" onclick="showup()"><font color="red">&nbsp;* </font>
																				</TD>
																			</tr>
																			<tr>
																				<TD nowrap colspan="4" align=center>
																				<br>
																				<!--<input type="button" value="备份运行文件" style="width:80" id="process_run" onclick="#">&nbsp;&nbsp;
																				<input type="button" value="备份启动文件" style="width:80" id="process_startup" onclick="#">&nbsp;&nbsp;
																				-->
																				    <select name="bkptype">
																						<option value="run" label="备份运行文件">备份运行文件</option>
																						<option value="startup" label="备份启动文件">备份启动文件</option>
																						<option value="all" label="全部备份">全部备份</option>
																					</select>
																					<input type="button"  id="backup" style="width:50" value="确  定">&nbsp;&nbsp;
																					<input type="reset" style="width:50" value="重  置">&nbsp;&nbsp;
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