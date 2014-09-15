<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.automation.model.NetCfgFileNode"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.automation.model.ConfiguringDevice"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  String rootPath = request.getContextPath();
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
		<script src="<%=rootPath%>/automation/js/kkpager.js" type="text/javascript"></script>
		<script src="<%=rootPath%>/automation/js/kkpager.min.js" type="text/javascript"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/menu.js"></script>
		<link rel="stylesheet" href="<%=rootPath%>/automation/css/style.css" type="text/css">
		<link rel="stylesheet" href="<%=rootPath%>/automation/css/kkpager.css" type="text/css">
		<script type="text/javascript">
			var listAction = "<%=rootPath%>/remoteDevice.do?action=passwdList";
	  		var delAction = "<%=rootPath%>/netCfgFile.do?action=delete";
			var curpage= <%=jp.getCurrentPage()%>;
  			var totalpages = <%=jp.getPageTotal()%>;
		</script>
		<style type="text/css">
#customers
  {
  font-family:"Trebuchet MS", Arial, Helvetica, sans-serif;
  width:100%;
  border-collapse:collapse;
  }

#customers td, #customers th 
  {
  font-size:1em;
  border:1px solid #9aafe5;
  padding:3px 7px 2px 7px;
  }
#customers td{
background:#dcddc0 url('<%=rootPath%>/automation/images/cell-blue.jpg');
}
#customers th 
  {
  font-size:1.1em;
  text-align:left;
  padding-top:5px;
  padding-bottom:4px;
  background-color:#2e6ab1;
  color:#ffffff;
  }

#customers tr.alt td 
  {
  color:#000000;
  background-color:#EAF2D3;
  }
  
  
/*CSS sabrosus style pagination*/

DIV.sabrosus {
	PADDING-RIGHT: 3px; PADDING-LEFT: 3px; PADDING-BOTTOM: 3px; MARGIN: 3px 50px 3px 3px ; PADDING-TOP: 3px; TEXT-ALIGN: right
}
DIV.sabrosus A {
	BORDER-RIGHT: #9aafe5 1px solid; PADDING-RIGHT: 5px; BORDER-TOP: #9aafe5 1px solid; PADDING-LEFT: 5px; PADDING-BOTTOM: 2px; BORDER-LEFT: #9aafe5 1px solid; COLOR: #2e6ab1; MARGIN-RIGHT: 2px; PADDING-TOP: 2px; BORDER-BOTTOM: #9aafe5 1px solid; TEXT-DECORATION: none
}
DIV.sabrosus A:hover {
	BORDER-RIGHT: #2b66a5 1px solid; BORDER-TOP: #2b66a5 1px solid; BORDER-LEFT: #2b66a5 1px solid; COLOR: #000; BORDER-BOTTOM: #2b66a5 1px solid; BACKGROUND-COLOR: lightyellow
}
DIV.pagination A:active {
	BORDER-RIGHT: #2b66a5 1px solid; BORDER-TOP: #2b66a5 1px solid; BORDER-LEFT: #2b66a5 1px solid; COLOR: #000; BORDER-BOTTOM: #2b66a5 1px solid; BACKGROUND-COLOR: lightyellow
}
DIV.sabrosus SPAN.current {
	BORDER-RIGHT: navy 1px solid; PADDING-RIGHT: 5px; BORDER-TOP: navy 1px solid; PADDING-LEFT: 5px; FONT-WEIGHT: bold; PADDING-BOTTOM: 2px; BORDER-LEFT: navy 1px solid; COLOR: #fff; MARGIN-RIGHT: 2px; PADDING-TOP: 2px; BORDER-BOTTOM: navy 1px solid; BACKGROUND-COLOR: #2e6ab1
}
DIV.sabrosus SPAN.disabled {
	BORDER-RIGHT: #929292 1px solid; PADDING-RIGHT: 5px; BORDER-TOP: #929292 1px solid; PADDING-LEFT: 5px; PADDING-BOTTOM: 2px; BORDER-LEFT: #929292 1px solid; COLOR: #929292; MARGIN-RIGHT: 2px; PADDING-TOP: 2px; BORDER-BOTTOM: #929292 1px solid
}
</style>
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
									                	<td class="content-title"> 设备维护>> 远程登录设备密码修改列表</td>
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
																	</td>
																	
																</tr>
															</table>
														</td>
													</tr>
													
		        									<tr>
		        										<td>
		        										
		        											<table id="customers" cellspacing="0"> 
															 <tr> 
																<th><INPUT type="checkbox" id="checkall" name="checkall" onclick="javascript:chkall()" class=noborder></th> 
																<th>序号</th> 
																<th>名称</th> 
																<th>IP地址</th> 
																<th>设备类型</th> 
																<th>登录方式</th> 
																<th>操作</th> 
															</tr> 
		        												
		        												<%
		        													ConfiguringDevice vo=null;
		        													int startRow = jp.getStartRow();
			        												for (int i = 0; i < configlist.size(); i++) 
			        												{
			        													vo = (ConfiguringDevice)configlist.get(i);
																		vo = (ConfiguringDevice) configlist.get(i);
																
			        											%>
			        										
																<tr >
																		<td><INPUT type="checkbox" class=noborder name="checkbox"
																					value="<%=vo.getId() %>">  </td> 
																	<td><%=startRow + i%></td> 
																	<td><%=vo.getAlias()%></td> 
																	<td><%=vo.getIpaddress()%></td> 
																	<td><%=vo.getDeviceRender() %></td> 
																	<td><%if(map.containsKey(vo.getIpaddress())){if(map.get(vo.getIpaddress()) == 0){out.println("Telnet");}else{out.println("SSH");}}%></td> 
																	<td>
																				<input type="hidden" id="id" name="id" value="<%=vo.getId()%>">
								        										<input type="hidden" id="ipaddress" name="ipaddress" value="<%=vo.getIpaddress()%>">
																	            <img class="img" src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 alt="右键操作">
																	</td> 
																</tr> 
																
			        													<%
			        												}
		        												%>
		        											</table>
		        										</td>
		        									</tr>
		        									<tr>
														<td>
															<div class="sabrosus">
															<c:set var="cp" value="<%=jp.getCurrentPage()%>"></c:set>
															<c:set var="tp" value="<%=jp.getPageTotal()%>"></c:set>
															<c:choose>
																<c:when test="${cp==1 }">
																	<span class="disabled" title="首页">首页</span>
																</c:when>
																<c:otherwise>
																	<a href="javascript:void(0)" onclick="firstPage();" title="首页">首页</a>
																</c:otherwise>
															</c:choose>
															
															<c:choose>
																<c:when test="${cp <= 1 }">
																    <span class="disabled" title="上一页"> < </span>
																</c:when>
																<c:otherwise>
																	<a href="javascript:void(0)" onclick="precedePage()" title="上一页"> < </a>
																</c:otherwise>
															</c:choose>
															<c:if test="${(cp-2)>0 }">
																<a href="javascript:void(0)" onclick="toPage(this);"><c:out value="${cp-2 }"></c:out></a>
															</c:if>
															<c:if test="${(cp-1)>0 }">
																<a href="javascript:void(0)" onclick="toPage(this);"><c:out value="${cp-1 }"></c:out></a>
															</c:if>
															<span class="current"><c:out value="${cp }"></c:out></span>
															<c:if test="${(cp+1)<=tp }">
																<a href="javascript:void(0)" onclick="toPage(this);"><c:out value="${cp+1 }"></c:out></a>
															</c:if>
															<c:if test="${(cp+2)<=tp }">
																<a href="javascript:void(0)" onclick="toPage(this);"><c:out value="${cp+2 }"></c:out></a>
															</c:if>
															<c:choose>
																<c:when test="${cp>=tp }">
																	 <span class="disabled" title="下一页"> > </span>
																</c:when>
																<c:otherwise>
																	<a href="javascript:void(0)" onclick="nextPage();" title="下一页"> > </a>
																</c:otherwise>
															</c:choose>
															&nbsp;
															<c:choose>
																<c:when test="${cp==tp }">
																	<span class="disabled" title="尾页">尾页</span>
																</c:when>
																<c:otherwise>
																	<a href="javascript:void(0);" onclick="lastPage();" title="尾页">尾页</a>
																</c:otherwise>
															</c:choose>
															
															&nbsp;<span class="normalsize">共<c:out value="${tp }"></c:out> 页/8条数据</span>
															&nbsp;转到<span id="kkpager_gopage_wrap"><input type="button" id="kkpager_btn_go" onclick="jumpPage();" value="确定"><input type="text" id="kkpager_btn_go_input" onfocus="kkpager.focus_gopage()" onkeypress="return kkpager.keypress_gopage(event);" onblur="kkpager.blur_gopage()" value=""></span>页
															</div>
														</td>
														<input type="hidden" name="jp" type="text" maxLength="3">
														<input type="hidden" name="perpagenum"
				value="<%=request.getParameter("perpagenum") == null ? "" : request.getParameter("perpagenum")%>">

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
					text :'修改密码',
					icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/cancelMng.gif',
					action: function(target){
						var id=$($(target).parent()).find('#id').val();
						var ip=$($(target).parent()).find('#ipaddress').val();
						telnetModify(id,ip);
					}
				}]
		});
		function edit(node)
		{
		    mainForm.action="<%=rootPath%>/netCfgFile.do?action=ready_edit&id="+node;
			mainForm.submit();
		}
		function telnetModify(node,ipaddress)
			{
			      window.open("<%=rootPath%>/remoteDevice.do?action=readyTelnetModify&id="+node+"&ipaddress="+ipaddress,"oneping", "height=400, width= 800, top=300, left=100,scrollbars=yes");
			}
	</script>
</html>
