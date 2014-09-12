<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@ include file="/automation/common/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>

<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List list = (List)request.getAttribute("list");
  List iplist = (List)request.getAttribute("iplist");
  JspPage jp = (JspPage) request.getAttribute("page");
  list=iplist;
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

		<script type="text/javascript">
			var listAction = "<%=rootPath%>/vpntelnetconf.do?action=netip";
	  		var delAction = "<%=rootPath%>/vpntelnetconf.do?action=delete";
			var curpage= <%=jp.getCurrentPage()%>;
  			var totalpages = <%=jp.getPageTotal()%>;
		</script>
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
			function chkall(){
				var checkall = document.getElementById("checkall");
				var checkboxes = document.getElementsByName("checkbox");
				for(var i = 0 ; i < checkboxes.length; i++){
					var checkbox = checkboxes[i];
					checkbox.checked = checkall.checked;
				}
			}
		</script>
		<script type="text/javascript">
			function radiochk(){
				var obj= document.getElementsByName("ipaddress");
				var Rev="";
			   for(var i=0;i<obj.length;i++)
			   {
			      if(obj[i].checked)
				     {
					     Rev=obj[i].value;
					     break;
				     }
			   }
			   return Rev;
			} 
				
			
			function submitip(){
				window.opener.document.getElementById("ipaddress").value=radiochk();
	 			window.close();
			}
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
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
		        													<td align="center">IP地址</td>
		        													<td align="center">设备名称</td>
		        													<td align="center">设备类型</td>
		        													<td align="center">设备描述</td>
		        												</tr>
		        												<%
		        													HostNode vo=null;
			        												for (int i = 0; i < list.size(); i++) {
																		vo = (HostNode) list.get(i);
			        													%>
			        													<tr <%=onmouseoverstyle%>>
											        						<td align='center'>
											        							<input type="radio" name="ipaddress" class="noborder" value="<%=vo.getIpAddress()%>"><%=vo.getIpAddress()%>
																			</td>
			        														<td align='center'><%=vo.getAlias()%></td>
			        														<td align='center'><%=vo.getType()%></td>
			        														<td align='center'><%=vo.getSysDescr()%></td>
			        													</tr>
			        													<%
			        												}
		        												%>
		        												<tr style="background-color: #ECECEC;">
																<TD nowrap colspan="4" align=center>
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
