<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.automation.model.NetCfgFileNode"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.automation.model.ConfiguringDevice"%>
<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List configlist = (List)request.getAttribute("configlist");
  List nodeList = (List)request.getAttribute("list");
  JspPage jp = (JspPage) request.getAttribute("page");

  Hashtable<String,Integer> map = new Hashtable<String,Integer>();
  if(nodeList != null && nodeList.size()>0){
	  NetCfgFileNode voo = null;
	     for(int i=0;i<nodeList.size();i++){
	        voo = (NetCfgFileNode)nodeList.get(i);
	        map.put(voo.getIpaddress(),voo.getConnecttype());
	     }
	  }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link href="<%=rootPath%>/automation/css/contextmenu.css" rel="stylesheet">
		<script src="<%=rootPath%>/automation/js/jquery-1.8.2.min.js" type="text/javascript"></script>
		<script src="<%=rootPath%>/automation/js/contextmenu.js" type="text/javascript"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/menu.js"></script>
		<link rel="stylesheet" href="<%=rootPath%>/automation/css/style.css" type="text/css">
		<script type="text/javascript">
			var listAction = "<%=rootPath%>/netCfgFile.do?action=list";
	  		var delAction = "<%=rootPath%>/netCfgFile.do?action=delete";
			var curpage= <%=jp.getCurrentPage()%>;
  			var totalpages = <%=jp.getPageTotal()%>;
		</script>
		
		<script language="JavaScript">

			
			function edit()
			{
				mainForm.action="<%=rootPath%>/netCfgFile.do?action=ready_edit&id="+node;
				mainForm.submit();
			}
  			
			function toAdd(){
				mainForm.action = "<%=rootPath%>/netCfgFile.do?action=ready_add";
			    mainForm.submit();
			}
		
			function synchronizedData()
			{
				mainForm.action = "<%=rootPath%>/netCfgFile.do?action=synchronizeData";
				mainForm.submit();
			}
	  		
	  		function toDelete(){  
	  		     if(confirm('是否确定删除这条记录?')) {
     				mainForm.action = "<%=rootPath%>/netCfgFile.do?action=delete";
     				mainForm.submit();
     				}
	  		}
  			
  			function CreateWindow(url){
				msgWindow=window.open(url,"protypeWindow","toolbar=no,width=850,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
			}
			//暂时未加功能
		    function uploadCfgFile()
			{
				window.open("<%=rootPath%>/vpntelnetconf.do?action=readyuploadCfgFile&&id="+node+"&ipaddress="+ipaddress,"oneping", "height=400, width= 800, top=300, left=100,scrollbars=yes");
			}
			//暂时未加功能
			function addForBatch()
			{
				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=ready_addForBatch";
				mainForm.submit();
			}
		</script>
	</head>
	<body>
	
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
									                	<td class="content-title"> 自动化 >> 远程登录设置列表</td>
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
																		<!--  ip地址：<input type="text" size="30" name="ipfindaddress">
																		<input type="button" value="查询" onclick="toFindIp()">-->
																	</td>
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
															<table width="100%" cellpadding="0" cellspacing="1">
																<tr>
																	<td bgcolor="#ECECEC" width="80%" align="center"><jsp:include page="../../common/page.jsp"><jsp:param name="curpage" value="<%=jp.getCurrentPage()%>" />
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
		        													<td align="center" width="20%">IP地址</td>
		        													<td align="center" width="15%">设备别名</td>
		        													<td align="center">设备类型</td>
		        													<!--  <td align="center">最后一次备份时间</td>-->
		        													<!-- <td align="center">b</td>
		        													<td align="center" width="15%">VPN采集状态</td>
		        													<td align="center" width="10%">同步状态</td> -->
		        													<td align="center" width="10%">登录方式</td>
		        													<td align="center" width="10%">操作</td>
		        												</tr>
		        												<%
		        													ConfiguringDevice vo=null;
		        													int startRow = jp.getStartRow();
			        												for (int i = 0; i < configlist.size(); i++) 
			        												{
			        													vo = (ConfiguringDevice)configlist.get(i);
																		vo = (ConfiguringDevice) configlist.get(i);
																		String lastUpdateTime = "没有备份文件";
																		if(vo.getLastUpdateTime() != null)
																		{
																			lastUpdateTime = vo.getLastUpdateTime().toString().substring(0,vo.getLastUpdateTime().toString().length()-5);
																		}
																		
			        											%>
			        													<tr>
											        						<td align='center'>
																			<INPUT type="checkbox" class=noborder name="checkbox"
																					value="<%=vo.getId() %>">
																			</td>
																			<td align='center'>
																				<font color='blue'><%=startRow + i%></font>
			        														</td>
			        														<td align='center'><%=vo.getIpaddress()%></td>
			        														<td align='center'><%=vo.getAlias() %></td>
			        														<td align='center'><%=vo.getDeviceRender() %></td>
			        														<!--<td align='center'><%=lastUpdateTime %></td>-->
			        														<!-- <td align='center'>b</td> -->
			        													
			        														<td align='center'><%if(map.containsKey(vo.getIpaddress())){if(map.get(vo.getIpaddress()) == 0){out.println("Telnet");}else{out.println("SSH");}}%></td>
																			<!-- <td align='center'><a href="<%=rootPath%>/vpntelnetconf.do?action=ready_edit&id=<%=vo.getId()%>"><img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td> -->
																			<td align="center" align='center'>
								        										<input type="hidden" id="id" name="id" value="<%=vo.getId()%>">
																	            <img class="img" src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 alt="右键操作"></td>
			        													</tr>
			        													<%
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
	<script language="JavaScript">
		$('.img').contextmenu({
			height:115,
			width:100,
			items : [{
				text :'修改信息',
				icon :'<%=rootPath%>/automation/images/edit.gif',
				action: function(target){
					var id=$($(target).parent()).find('#id').val();
					edit(id);
				}
             }]
		},{
			height:115,
			width:100,
			items : [{
				text :'PING',
				icon :'<%=rootPath%>/automation/images/ping.gif',
				action: function(target){
					var id=$($(target).parent()).find('#id').val();
					edit(id);
				}
             }]
		},{
			height:115,
			width:100,
			items : [{
				text :'traceroute',
				icon :'<%=rootPath%>/automation/images/traceroute.gif',
				action: function(target){
					var id=$($(target).parent()).find('#id').val();
					edit(id);
				}
             }]
		});
		function edit(node)
		{
			   mainForm.action="<%=rootPath%>/netCfgFile.do?action=ready_edit&id="+node;
				mainForm.submit();
		}
	</script>
</html>
