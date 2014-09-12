<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.model.*"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.util.UserView"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
	List list = (List) request.getAttribute("list");
	JspPage jp = (JspPage) request.getAttribute("page");

	UserView view = new UserView();

	//从session 中获取用户

	User operator = (User) session
			.getAttribute(SessionConstant.CURRENT_USER);
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

		<script language="JavaScript" type="text/javascript">
  var listAction = "<%=rootPath%>/equip.do?action=list";
  var delAction = "<%=rootPath%>/equip.do?action=delete";
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;

  var alertInfo ="是否删除！";
  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/equip.do?action=ready_add";
     mainForm.submit();
  }
   function toType()
  {
     mainForm.action = "<%=rootPath%>/equip.do?action=type";
     mainForm.submit();
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
									                	<td class="content-title"> 系统管理 >> 系统配置 >> Equip列表 </td>
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
																		<a href="#" onclick="toAdd()">添加</a>
																		<a href="#" onclick="toDelete()">删除</a>
																		&nbsp;&nbsp;&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													
													<tr>
														<td>
															<table>
																<tr>
																	<td class="body-data-title">
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
															<table>
																<tr>
																	<td align="center" class="body-data-title">
																		<INPUT type="checkbox" class=noborder name="checkall"
																			onclick="javascript:chkall()">
																	</td>
																	<td align="center" class="body-data-title">
																		序号
																	</td>
																	<td align="center" class="body-data-title">
																		类别
																	</td>
																	<td align="center" class="body-data-title">
																		中文名称
																	</th>
																	<td align="center" class="body-data-title">
																		英文名称
																	</th>
																	<td align="center" class="body-data-title">
																		topo_image
																	</th>
																	<td align="center" class="body-data-title">
																		lost_image
																	</td>
																	<td align="center" class="body-data-title">
																		alarm_image
																	</td>
																	<td align="center" class="body-data-title">
																		path
																	</td>
																	<td align="center" class="body-data-title">
																		编辑
																	</td>
																</tr>
																<%
																	equip vo = null;
																	int startRow = jp.getStartRow();
																	if(list.size()!= 0 && list != null){
																	for (int i = 0; i < list.size(); i++) {
																		vo = (equip) list.get(i);
																%>
																<tr <%=onmouseoverstyle%>>
																	<td align="center" class="body-data-list">
																		<INPUT type="checkbox" class=noborder name=checkbox
																			value="<%=vo.getId()%>">
																	</td>
																	<td align="center" class="body-data-list">
																		<font color='blue'><%=startRow + i%></font>
																	</td>
																	<td align="center" class="body-data-list"><%=vo.getCategory()%></td>
																	<td align="center" class="body-data-list">
																		<a href="#" style="cursor: hand"
																			onclick="window.showModalDialog('<%=rootPath%>/equip.do?action=read&id=<%=vo.getId()%>',window,',dialogHeight:400px;dialogWidth:600px')"><%=vo.getCn_name()%>
																	    </a>
																	</td>
																	<td align="center" class="body-data-list" style="background-color: #ECECEC;"><%=vo.getEn_name()%></td>
																	<td align="center" class="body-data-list"><%=vo.getTopo_image()%></td>
																	<td align="center" class="body-data-list" style="background-color: #ECECEC;"><%=vo.getLost_image()%></td>
																	<td align="center" class="body-data-list"><%=vo.getAlarm_image()%></td>
																	<td align="center" class="body-data-list" style="background-color: #ECECEC;"><%=vo.getPath()%></td>
																	<td align="center" class="body-data-list">
																		<a href="<%=rootPath%>/equip.do?action=ready_edit&id=<%=vo.getId()%>" >
																			<img src="<%=rootPath%>/resource/image/editicon.gif" border="0" />
																	    </a>
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
	</BODY>
</HTML>
