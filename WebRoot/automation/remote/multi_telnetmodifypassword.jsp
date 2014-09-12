<%@page language="java" contentType="text/html;charset=gb2312"%>

<%
	String rootPath = request.getContextPath();
	
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<LINK href="<%=rootPath%>/automation/css/style.css" type="text/css" rel="stylesheet">
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/automation/css/ext/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/jquery-1.8.2.min.js"></script>
		<script language="JavaScript" type="text/javascript">

 Ext.onReady(function()
{  
  setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("vpn").on("click",function(){
 	var chk1 = document.getElementById("ipaddress");
 	if(chk1.value != "")
 	{
 		Ext.MessageBox.wait('数据加载中，请稍后.. ');
		mainForm.action = "<%=rootPath%>/remoteDevice.do?action=multi_modify";
		mainForm.submit();
 	}
 	else
 	{
 		alert("网络设备不能为空！");
 	}
 });
});
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    


function selectType(){
var deviceType=document.getElementById("deviceType").value;
if(deviceType!="h3c"&&deviceType!="huawei"){
 $("#dev_tr").hide();
}else{
 $("#dev_tr").show();
}
}

			function showup(){
			 var deviceType=document.getElementById("deviceType").value;
				var url="<%=rootPath%>/vpntelnetconf.do?action=multi_telnet_netip&deviceType="+deviceType;
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
		</script>

	</head>
	<body id="body" class="body">
		<form id="mainForm" method="post" name="mainForm">
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
																		批量修改密码
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

																		<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
																			<tr >
																			   <TD nowrap align="right" height="24" >
																					设备类型&nbsp;
																				</TD>
																				<TD nowrap >
																					&nbsp;
																					<select name="deviceType" id="deviceType" onchange="selectType()">
																						<option value="h3c">H3C</option>
																						<option value="huawei">HUAWEI</option>
																						<option value="cisco">CISCO</option>
																						<option value="redgiant">RedGiant</option>
																						<option value="zte">ZTE</option>
																						
																					</select><font color="red">&nbsp;* </font>
																				</TD>
																				<TD nowrap align="right" height="24" >
																					网络设备&nbsp;
																				</TD>
																				<TD nowrap width="40%" >
																					&nbsp;
																					<input name="ipaddress" type="text" size="40" class="formStyle" id="ipaddress" readonly="readonly" >
																					<input type="button" value="选择网络设备" onclick="showup()"><font color="red">&nbsp;* </font>
																				</TD>
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24">
																					用户名&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<input name="modifyuser" type="text" size="40"
																						class="formStyle" value="">
																				</TD>
																				<td align="right" height="20">
																					&nbsp;新密码
																				</td>
																				<td colspan="1" align="left">
																					&nbsp;
																					<input name="newpassword" type="password" size="30"
																						class="formStyle">
																					<font color="red">*(必填)</font>
																				</td>
																			</tr>

																		
																			<tr  id="dev_tr">
																				<TD nowrap align="right" height="24">
																					认证(3a)&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<select name="threeA">
																						<option value=""></option>
																						<option value="aaa">
																							AAA
																						</option>
																					</select>
																				</TD>
																				<td align="right" height="20">
																					&nbsp;密码模式
																				</td>
																				<td colspan="1" align="left">
																					&nbsp;
																					<select name="encrypt">
																						<option value="1">
																							明文
																						</option>
																						<option value="0">
																							加密
																						</option>
																					</select>
																				</td>
																			</tr>
																			


																			<tr style="background-color: #ECECEC;">
																				<TD nowrap colspan="4" align=center>
																					<br>
																					<input type="button" value="修 改" style="width: 50"
																						id="vpn" onclick="#">
																					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																					<input type="reset" style="width: 50" value="返回"
																						onclick="javascript:history.back(1)">
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
											<td>

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