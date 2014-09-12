<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.model.*"%>
<%
	String rootPath = request.getContextPath();
	List list = (List) request.getAttribute("list");
%>

<%
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script language="javascript">	
  
  function doQuery()
  {  
	window.location.reload();
  }
  
    function doAdd()
  {  
     mainForm.action = "<%=rootPath%>/snmp.do?action=ready_add";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  }
  
// 全屏观看
function gotoFullScreen() {
	parent.mainFrame.resetProcDlg();
	var status = "toolbar=no,height="+ window.screen.height + ",";
	status += "width=" + (window.screen.width-8) + ",scrollbars=no";
	status += "screenX=0,screenY=0";
	window.open("topology/network/index.jsp", "fullScreenWindow", status);
	parent.mainFrame.zoomProcDlg("out");
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

}

</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>	
							</tr>
						</table>
					</td>
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
									                	<td class="content-title"> 系统管理 >> 资源管理 >> SNMP设置 </td>
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
															<table>
																<tr>
																	<td class="body-data-title" style="text-align: right;">
																		&nbsp;&nbsp;
																		<INPUT type="button" class="formStyle" value="添 加"
																			onclick=" return doAdd()">
																		&nbsp;&nbsp;
																		<INPUT type="button" class="formStyle" value="刷 新"
																			onclick=" return doQuery()">
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
																	<td align="center" class="body-data-title">
																		名称
																		</a>
																	</td>
																	<td align="center" class="body-data-title">
																		版本
																	</td>
																	<td align="center" class="body-data-title">
																		读团体
																	</td>
																	<td align="center" class="body-data-title">
																		写团体
																	</td>
																	<td align="center" class="body-data-title">
																		超时时间(秒)
																	</td>
																	<td align="center" class="body-data-title">
																		重试次数
																	</td>
																	<td align="center" class="body-data-title">
																		修改
																	</td>
						
																	<td align="center" class="body-data-title">
																		删除
																	</td>
																</tr>
																<%
																	SnmpConfig vo = null;
																	for (int i = 0; i < list.size(); i++) {
																		vo = (SnmpConfig) list.get(i);
																		String version = "";
																		if (vo.getSnmpversion() == 0) {
																			version = "SNMP V1";
																		} else {
																			version = "SNMP V2c";
																		}
																		//Host host = (Host)PollingEngine.getInstance().getNodeByID(vo.getId());
																%>
																<tr <%=onmouseoverstyle%> >
						
																	<td align="center" class="body-data-list"><%=vo.getName()%></td>
																	<td align="center" class="body-data-list"><%=version%></a></td>
																	<td align="center" class="body-data-list"><%=vo.getReadcommunity()%></td>
																	<td align="center" class="body-data-list"><%=vo.getWritecommunity()%></td>
																	<td align="center" class="body-data-list"><%=vo.getTimeout()%></td>
																	<td align="center" class="body-data-list"><%=vo.getTrytime()%></td>
																	<td align="center" class="body-data-list">
																		<a
																			href="<%=rootPath%>/snmp.do?action=ready_edit&id=<%=vo.getId()%>">修改</a>
																	</td>
																	<td align="center" class="body-data-list">
																		<a
																			href="<%=rootPath%>/snmp.do?action=delete&id=<%=vo.getId()%>">删除</a>
																	</td>
																	</td>
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
	</BODY>
</HTML>
