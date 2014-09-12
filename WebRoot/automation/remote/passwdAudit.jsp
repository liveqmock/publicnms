<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
  String rootPath = request.getContextPath();
  JspPage jp = (JspPage) request.getAttribute("page");
%>

<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
	
		<LINK href="<%=rootPath%>/automation/css/style.css" type="text/css" rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>

		
		<script type="text/javascript">
			var listAction = "<%=rootPath%>/remoteDevice.do?action=ready_multi_audit";
	  		var delAction = "<%=rootPath%>/remoteDevice.do?action=delete";
			var curpage= <%=jp.getCurrentPage()%>;
  			var totalpages = <%=jp.getPageTotal()%>;
			
  			function doFind(){  
			     if(mainForm.value.value=="")
			     {
			     	alert("请输入查询条件");
			     	return false;
			     }
			     mainForm.action = "<%=rootPath%>/remoteDevice.do?action=queryByCondition";
			     mainForm.submit();
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
								<td class="td-container-main-add">
									<table id="container-main-add" class="container-main-add">
										<tr>
											<td>
												<table id="add-content" class="add-content">
													<tr>
														<td>
															<table id="add-content-header"
																class="add-content-header">
																<tr>
																	<td align="left" width="5">
																		<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																			width="5" height="29" />
																	</td>
																	<td class="add-content-title">
																		密码审计信息
																	</td>
																	<td align="right">
																		<img
																			src="<%=rootPath%>/common/images/right_t_03.jpg"
																			width="5" height="29" />
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
																			<jsp:param name="curpage"
																				value="<%=jp.getCurrentPage()%>" />
																			<jsp:param name="pagetotal"
																				value="<%=jp.getPageTotal()%>" />
																		</jsp:include>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td >
															<table cellspacing="0" border="1" bordercolor="#ababab">
																<tr>
																	<td bgcolor="#ECECEC" width="60%">
																		&nbsp;
																		<B>查&nbsp;&nbsp;询:</B>
							        									<SELECT name="key" style="width=100">
																			<OPTION value="username" selected>
																				用户
																			</OPTION>
																			<OPTION value="ip">
																				IP地址
																			</OPTION>
																		</SELECT>&nbsp;<b>=</b>&nbsp; 
								          								<INPUT type="text" name="value" width="15" class="formStyle">
								          								<INPUT type="button" class="formStyle" value="查询" onclick=" return doFind()">
								          							</td>            
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
																		<table cellspacing="0" border="1" bordercolor="#ababab">
																			<tr height=28 style="background:#ECECEC" align="center" class="content-title">
																				<TD nowrap align="center">
																					序号&nbsp;
																				</TD>
																				<TD nowrap align="center">
																					用户&nbsp;
																				</TD>
																				<TD nowrap align="center">
																					IP&nbsp;
																				</TD>
																				<TD nowrap align="center">
																					旧密码&nbsp;
																				</TD>
																				<td align="center">
																					新密码&nbsp;
																				</td>
																				<td align="center">
																					修改时间&nbsp;
																				</td>
																			</tr>
																			<c:forEach items="${list}" var="had" varStatus="status">
																				<tr>
																					<td align='center'>
																						${status.index+1}
					        														</td>
																					<td align='center'>
																						${had.username}
																					</td>
																					<td align='center'>
																						${had.ip}
																					</td>
																					<td align='center'>
																						*
																					</td>
																					<td align='center'>
																						*
																					</td>
																					<td align='center'>
																						${had.dotime}
																					</td>
																				</tr>
																			</c:forEach>
																		</table>
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
																				<td align="left" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_01.jpg"
																						width="5" height="12" />
																				</td>
																				<td align="right" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_03.jpg"
																						width="5" height="12" />
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
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</form>
	</body>
</HTML>