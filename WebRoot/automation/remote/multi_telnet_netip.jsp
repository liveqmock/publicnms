<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.automation.model.NetCfgFileNode"%>
<%@ include file="/automation/common/globe.inc"%>
<%@page import="java.util.List"%>

<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/automation/js/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<LINK href="<%=rootPath%>/automation/css/style.css" type="text/css" rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>
		<link href="<%=rootPath%>/automation/css/detail.css" rel="stylesheet" type="text/css">
		<script type="text/javascript">
			var listAction = "<%=rootPath%>/vpntelnetconf.do?action=netip";
	  		var delAction = "<%=rootPath%>/vpntelnetconf.do?action=delete";
		</script>
		
		<script type="text/javascript">
			function chkall(){
				var checkall = document.getElementById("checkall");
				var checkboxes = document.getElementsByName("id");
				for(var i = 0 ; i < checkboxes.length; i++){
					var checkbox = checkboxes[i];
					checkbox.checked = checkall.checked;
				}
			}
		</script>
		<script type="text/javascript">
			function radiochk(){
				var obj= document.getElementsByName("id");
				var Rev="";
				var retVal="";
			   for(var i=0;i<obj.length;i++)
			   {
			      if(obj[i].checked)
				 {
					Rev=obj[i].value;
					retVal= retVal+","+Rev;
				  }
			   }
			   return retVal;
			} 
				
			
			function submitip(){
				window.opener.document.getElementById("ipaddress").value=radiochk();
	 			window.close();
			}
		</script>
	</head>
	<body id="body" class="body" >
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
		        									<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
										      
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">网络设备 IP列表</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        							</tr>
		        									<tr>
		        										<td>
		        											<table cellspacing="0" border="1" bordercolor="#ababab">
		        												<tr height=28 style="background:#ECECEC" align="center" class="content-title">
		        													<td align="center"><input type=checkbox name="checkall" onclick="javascript:chkall()"  class="noborder"></td>
		        													<td align="center">序号</td>
		        													<td align="center">IP地址</td>
		        													
		        													<td align="center">设备类型</td>
		        												</tr>
		        												<%
		        													NetCfgFileNode vo=null;
			        												for (int i = 0; i < list.size(); i++) {
																		vo = (NetCfgFileNode) list.get(i);
			        													%>
			        													<tr <%=onmouseoverstyle%>>
											        						 
											        						<td align='center'>
											        							<input type="checkbox"  class="noborder" name="id" value="<%=vo.getIpaddress()%>"/>
																			</td>
																			<td align='center'>
											        							<%=i+1%>
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
																<br><input type="button" value="提 交" style="width: 50" onclick="submitip()">
																					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																	<input type="reset" style="width: 50" value="关 闭" onclick="window.close()">
																</TD>	
															</tr>	
		        											</table>
		        										</td>
		        									</tr>
		        								</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-footer" class="content-footer">
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
		</form>
	</body>
</html>
