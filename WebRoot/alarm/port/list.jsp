<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.alarm.model.AlarmIndicators"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.alarm.model.AlarmPort"%>

<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List list = (List)request.getAttribute("list");
  
  String nodeid = (String)request.getAttribute("nodeid");
  String type = (String)request.getAttribute("type");
  String subtype = (String)request.getAttribute("subtype");
  String ipaddress = (String)request.getAttribute("ipaddress");
 
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link
			href="<%=rootPath%>/common/contextmenu/skins/default/contextmenu.css"
			rel="stylesheet">
		<script src="<%=rootPath%>/common/contextmenu/js/jquery-1.8.2.min.js"
			type="text/javascript"></script>
		<script src="<%=rootPath%>/common/contextmenu/js/contextmenu.js"
			type="text/javascript"></script>
	
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script type="text/javascript">
		 	
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
			
			}
		</script>
		<script type="text/javascript">
			$('.img').contextmenu({
				height:115,
				width:100,
				items : [{
					text :'修改信息',
					icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/edit.gif',
					action: function(target){
						var id=$($(target).parent()).find('#id').val();
						edit(id);
					}
				}]
			});
			function edit(node){
				mainForm.action = "<%=rootPath%>/alarmport.do?action=edit&id=" + node;
				mainForm.submit();
			}
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" name="nodeid" id="nodeid" value="<%=nodeid%>">
			<input type="hidden" name="ipaddress" id="ipaddress" value="<%=ipaddress%>">
			<input type="hidden" name="type" id="type" value="<%=type%>">
	
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
									                	<td class="content-title"> 告警 >> 告警配置 >> 告警阀值列表(<%=ipaddress %>) </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
													<tr>
														<td class="body-data-title" style="text-align:right">
		        												<a href="#" onclick="toAdd()"></a>&nbsp;&nbsp;<a href="#" onclick="toMultiAdd()"></a>&nbsp;&nbsp;
		        											</td>
		        										</tr>
		        								</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
													<tr>
														<td>
															<table>
																<tr>
																    <td class="body-data-title" width=5%>序号</td>
													    			<td class="body-data-title" width=10%>名称</td>
													    			<td class="body-data-title">启用</td>
													    			<td class="body-data-title">比较方式</td>
													    			<td class="body-data-title">一级入口阀值</td>
													    			<td class="body-data-title">次数</td>
													    			<td class="body-data-title" width=3%>告警</td>
													    			<td class="body-data-title">二级入口阀值</td>
													    			<td class="body-data-title">次数</td>
													    			<td class="body-data-title" width=3%>告警</td>
													    			<td class="body-data-title">三级入口阀值</td>
													    			<td class="body-data-title">次数</td>
													    			<td class="body-data-title" width=3%>告警</td>
													    			<td class="body-data-title">一级出口阀值</td>
													    			<td class="body-data-title">次数</td>
													    			<td class="body-data-title" width=3%>告警</td>
													    			<td class="body-data-title">二级出口阀值</td>
													    			<td class="body-data-title">次数</td>
													    			<td class="body-data-title" width=3%>告警</td>
													    			<td class="body-data-title">三级出口阀值</td>
													    			<td class="body-data-title">次数</td>
													    			<td class="body-data-title" width=3%>告警</td>
													    			<td class="body-data-title" width=5%>详情</td>
							        							</tr>
							        							<%
							        							
							        								if(list != null && list.size() > 0) {
							        									for(int i = 0; i < list.size(); i++){
							        										AlarmPort alarmPort = (AlarmPort)list.get(i);
							        										
							        										String isMonitor = "否";
							        										String bcolor="gray";
							        										if("1".equals(alarmPort.getEnabled())){
							        											isMonitor = "是";
							        											bcolor ="#3399CC";
							        										}
							        										
							        										String smsin1 = "否";
							        										if(alarmPort.getSmsin1()==1){
							        											smsin1 = "是";
							        										}
							        										
							        										String smsin2 = "否";
							        										if(alarmPort.getSmsin2()==1){
							        											smsin2 = "是";
							        										}
							        										
							        										String smsin3 = "否";
							        										if(alarmPort.getSmsin3()==1){
							        											smsin3 = "是";
							        										}
							        										
							        										String smsout1 = "否";
							        										if(alarmPort.getSmsout1()==1){
							        											smsout1 = "是";
							        										}
							        										
							        										String smsout2 = "否";
							        										if(alarmPort.getSmsout2()==1){
							        											smsout2 = "是";
							        										}
							        										
							        										String smsout3 = "否";
							        										if(alarmPort.getSmsout3()==1){
							        											smsout3 = "是";
							        										}
							        										String compare = "升序";
							        										String bgcol = "#ffffff";
							        										if(alarmPort.getCompare()== 0){
							        											compare = "降序";
							        											bgcol = "gray";
							        										}
							        										
							        										%>
							        										
							        										<tr>
							        											<td class="body-data-list"><font color='blue'>&nbsp;<%= 1+i%></font></td>
							        											
																    			<td class="body-data-list" ><%=alarmPort.getName()%></td>
																    			
																    			<td class="body-data-list" bgcolor="<%=bcolor%>"><%=isMonitor%></td>
																    			<td class="body-data-list" bgcolor="<%=bgcol%>"><%=compare%></td>
																    			<td class="body-data-list" bgcolor="yellow"><%=alarmPort.getLevelinvalue1()%>kb</td>
																    			<td class="body-data-list"><%=alarmPort.getLevelintimes1()%></td>
																    			<td class="body-data-list"><%=smsin1%></td>
																    			<td class="body-data-list" bgcolor="orange"><%=alarmPort.getLevelinvalue2()%>kb</td>
																    			<td class="body-data-list"><%=alarmPort.getLevelintimes2()%></td>
																    			<td class="body-data-list"><%=smsin2%></td>
																    			<td class="body-data-list" bgcolor="red"><%=alarmPort.getLevelinvalue3()%>kb</td>
																    			<td class="body-data-list"><%=alarmPort.getLevelintimes3()%></td>
																    			<td class="body-data-list"><%=smsin3%></td>
																    			<td class="body-data-list" bgcolor="yellow"><%=alarmPort.getLeveloutvalue1()%>kb</td>
																    			<td class="body-data-list"><%=alarmPort.getLevelouttimes1()%></td>
																    			<td class="body-data-list"><%=smsout1%></td>
																    			<td class="body-data-list" bgcolor="orange"><%=alarmPort.getLeveloutvalue2()%>kb</td>
																    			<td class="body-data-list"><%=alarmPort.getLevelouttimes2()%></td>
																    			<td class="body-data-list"><%=smsout2%></td>
																    			<td class="body-data-list" bgcolor="red"><%=alarmPort.getLeveloutvalue3()%>kb</td>
																    			<td class="body-data-list"><%=alarmPort.getLevelouttimes3()%></td>
																    			<td class="body-data-list"><%=smsout3%></td>
																				<td align="center" class="body-data-list">
																					<input type="hidden" id="id" name="id" value="<%=alarmPort.getId()%>">
																					<img class="img" src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 alt="右键操作">
																				</td>
																			</tr>
							        										<%
							        									}
							        								}%>
							        								<tr>
																			<TD nowrap colspan="20" align=center><br>
																			<input type="button" value="关 闭" style="width:50" onclick="window.close()">
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
