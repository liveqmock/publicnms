<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@ include file="/automation/common/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.automation.model.ConfiguringDevice"%>


<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
  JspPage jp = (JspPage) request.getAttribute("page");
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<LINK href="<%=rootPath%>/automation/css/style.css" type="text/css" rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>

		<link rel="stylesheet" href="<%=rootPath%>/automation/css/style.css" type="text/css">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/menu.js"></script>
		<script type="text/javascript">
			var listAction = "<%=rootPath%>/netCfgFile.do?action=configlist";
	  		var delAction = "<%=rootPath%>/netCfgFile.do?action=delete";
			var curpage= <%=jp.getCurrentPage()%>;
  			var totalpages = <%=jp.getPageTotal()%>;
		</script>
		<script language="JavaScript" type="text/JavaScript">
		//备份
			function backup()
			{
				window.open("<%=rootPath%>/netCfgFile.do?action=readyBackupConfig&&id="+node+"&ipaddress="+ipaddress,"oneping", "height=400, width= 500, top=300, left=100,scrollbars=yes");
			}	
			//管理备份
			function setupcfg()
			{
				window.open("<%=rootPath%>/netCfgFile.do?action=readySetupConfig&&id="+node+"&ipaddress="+ipaddress,"oneping", "height=400, width= 800, top=300, left=100,scrollbars=yes");
			}
			
			
			//网络设备详细配置文件
			function showAllFile(ip,type)
			{
				location.href="<%=rootPath%>/netCfgFile.do?action=showAllFile&ip="+ip+"&type="+type;
				
			}
			//批量配置
			function deployCfgForBatch()
			{
				mainForm.action = "<%=rootPath%>/netCfgFile.do?action=ready_deployCfgForBatch";
				mainForm.submit();
			}
  			
  			function CreateWindow(url){
				msgWindow=window.open(url,"protypeWindow","toolbar=no,width=850,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
			}
			//批量备份
			function backupForBatch()
			{
				mainForm.action = "<%=rootPath%>/netCfgFile.do?action=ready_backupForBatch";
				mainForm.submit();
			}
			function uploadCfgFile()
			{
				window.open("<%=rootPath%>/netCfgFile.do?action=readyuploadCfgFile&&id="+node+"&ipaddress="+ipaddress,"oneping", "height=400, width= 800, top=300, left=100,scrollbars=yes");
			}
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
	<div id="itemMenu" style="display: none";>
		<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
			style="border: thin;font-size: 12px" cellspacing="0">
	
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.backup();">备份</td>
			</tr>
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.setupcfg()">管理备份</td>
			</tr>
		<!--  
			<tr>
				<td style="cursor: default; border: outset 1;" align="center" 
					onclick="parent.uploadCfgFile()">上传备份</td>
			</tr>
			
		-->
		</table>
	</div>
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
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title"> 自动化 >> 配置文件管理 >> 配置文件列表</td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
		        									<tr>
														<td>
															<table width="100%" cellpadding="0" cellspacing="1">
																<tr>
																	<td bgcolor="#ECECEC" width="60%">
																	&nbsp;&nbsp;&nbsp;
																		<!-- ip地址：<input type="text" size="30" name="ipfindaddress">
																		<input type="button" value="查询" onclick="toFindIp()">-->
																	</td>
																	<td bgcolor="#ECECEC" width="40%" align='right'>
																		
																		<a href="#" onclick="backupForBatch()">批量备份</a>&nbsp;
																		<a href="#" onclick="deployCfgForBatch()">批量配置</a>&nbsp;
																		
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
		        										<td>
		        											<table cellspacing="0" border="1" bordercolor="#ababab">
		        												<tr height=28 style="background:#ECECEC" align="center" class="content-title">
		        													<td align="center"><INPUT type="checkbox" id="checkall" name="checkall" onclick="javascript:chkall()" class=noborder></td>
		        													<td align="center">序号</td>
		        													<td align="center" width="20%">别名</td>
		        													<td align="center" width="20%">IP地址</td>
		        													<td align="center">设备类型</td>
		        													<td align="center">最后一次备份时间</td>
		        													<!-- <td align="center">b</td> -->
		        													<!-- <td align="center" width="15%">VPN采集状态</td>
		        													<td align="center" width="10%">同步状态</td>-->
		        													<td align="center" width="20%">操作</td>
		        												</tr>
		        												<%
		        													ConfiguringDevice vo=null;
		        													int startRow = jp.getStartRow();
		        													if(list!=null&&list.size()>0){
			        												for (int i = 0; i < list.size(); i++) 
			        												{
			        													vo = (ConfiguringDevice)list.get(i);
																		vo = (ConfiguringDevice) list.get(i);
																		String lastUpdateTime = "没有备份文件";
																		if(vo.getLastUpdateTime() != null)
																		{
																			lastUpdateTime = vo.getLastUpdateTime().toString().substring(0,vo.getLastUpdateTime().toString().length()-5);
																		}
																		
			        											%>
			        													<tr <%=onmouseoverstyle%>>
											        						<td align='center'>
																			<INPUT type="checkbox" class=noborder name="checkbox"
																					value="<%=vo.getId() %>">
																			</td>
																			<td align='center'>
																				<font color='blue'><%=startRow + i%></font>
			        														</td>
			        														<td align='center'><a href="#" onclick="showAllFile('<%=vo.getIpaddress()%>','<%=vo.getDeviceRender() %>')"><font color='blue'><%=vo.getAlias()%></font></a></td>
			        														<td align='center'><a href="#" onclick="showAllFile('<%=vo.getIpaddress()%>','<%=vo.getDeviceRender() %>')"><font color='blue'><%=vo.getIpaddress()%></font></a></td>
			        														<td align='center'><%=vo.getDeviceRender() %></td>
			        														<td align='center'><%=lastUpdateTime %></td>
			        														
																			<td align='center'>
																					<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('2','<%=vo.getId() %>','<%=vo.getIpaddress() %>') alt="右键操作">
																				</td>
			        													</tr>
			        													<%
			        												}
		        													}
		        												%>
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
