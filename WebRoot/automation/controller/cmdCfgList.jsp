<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.Map"%>
<%@ include file="/automation/common/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.automation.model.CmdCfgFile"%>


<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
  Map map = (Map)request.getAttribute("map");
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script  type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<LINK href="<%=rootPath%>/automation/css/style.css" type="text/css" rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>

		<script type="text/javascript">
			var listAction = "<%=rootPath%>/autoControl.do?action=configlist";
	  		var delAction = "<%=rootPath%>/autoControl.do?action=delete";
		
		</script>
		<script language="JavaScript" type="text/JavaScript">
	  		
	  		function toDelete(){  
     				mainForm.action = "<%=rootPath%>/autoControl.do?action=deleteFile";
     				mainForm.submit();
	  		}
	  		
	  		function configFileList(ip)
			{
			   location.href="<%=rootPath%>/autoControl.do?action=cmdCfgList&ip="+ip;
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
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title">  自动化控制>> 定时巡检列表</td>
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
		        											<table cellspacing="0" border="1" bordercolor="#ababab">
		        												<tr height=28 style="background:#ECECEC" align="center" class="content-title">
		        													<td align="center"><INPUT type="checkbox" id="checkall" name="checkall" onclick="javascript:chkall()" class=noborder></td>
		        													<td align="center">序号</td>
		        													<td align="center" width="20%">设备名称</td>
		        													<td align="center" width="20%">IP地址</td>
		        													<td align="center">设备类型</td>
		        													<td align="center">扫描时间</td>
		        													
		        													
		        													
		        												</tr>
		        												<%
		        												CmdCfgFile vo=null;
		        													if(list!=null){
			        												for (int i = 0; i < list.size(); i++) 
			        												{
			        												
			        													vo = (CmdCfgFile)list.get(i);
																		String time="";
																		String alias="";
																		if(vo.getBackupTime()!=null){
																		time=vo.getBackupTime().toString().substring(0,vo.getBackupTime().toString().length()-5);
																		}
																		if(map!=null&&map.containsKey(vo.getIpaddress())){
																			alias=(String)map.get(vo.getIpaddress());
																		}
			        											%>
			        													<tr <%=onmouseoverstyle%>>
											        						<td align='center'>
																			<INPUT type="checkbox" class=noborder name="checkbox"
																					value="<%=vo.getId() %>">
																			</td>
																			<td align='center'>
																				<font color='blue'><%= i+1%></font>
			        														</td>
			        														<td align='center'><a href='#' onclick="configFileList('<%=vo.getIpaddress()%>')"><font color='blue'><%=alias%></font></a></td>
			        														<td align='center'><a href='#' onclick="configFileList('<%=vo.getIpaddress()%>')"><font color='blue'><%=vo.getIpaddress()%></font></a></td>
			        														<td align='center'><%=vo.getBkpType()%></td>
			        														<td align='center'><%=time%></td>
			        														
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
