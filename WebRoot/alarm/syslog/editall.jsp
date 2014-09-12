<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="java.util.*"%>
<%@ include file="/include/globe.inc"%>

<%
 String rootPath = request.getContextPath(); 
  BusinessDao bussdao = new BusinessDao();
   List allbuss = null;
   try{
  allbuss =  bussdao.loadAll();  
   }catch(Exception e){
   }finally{
   bussdao.close();
   }
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
</head>
<script language="javascript">
 
  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/netsyslogalarm.do?action=toolbarsaveall";
      mainForm.submit();
	  window.close();
		
		window.opener.location.href=window.opener.location.href;
		self.opener.location.reload();
		window.opener.location.reload(); 
  }
</script>


</script>
<body id="body" class="body"">
		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" name="ids" value="<%=(String)request.getAttribute("hostid")%>">
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
									                	<td class="content-title"> <b>告警 >> SYSLOG管理 >> SYSLOG过滤规则</b></td>
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
															<table width="100%" cellPadding=0 cellspacing="1" algin="center">
									                    					<tr algin="left" valign="center">                      														
									                      						<td height="28" bgcolor="#ECECEC" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;<b>级别</b></td>
									                    					</tr>
									                    					<tr align="left" valign="center" bgcolor=#ffffff> 
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="0" name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;紧急</td>
									                    						<td height="28" align="right" width=10%%><INPUT type="checkbox" class=noborder value="1" name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;报警</td> 
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="2"  name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;关键</td> 
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="3" name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;错误</td>
									                    					</tr> 
									                    					<tr align="left" valign="center" bgcolor=#ffffff> 
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="4" name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;警告</td>
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="5"  name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;通知</td>
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="6" name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;提示</td>
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="7"  name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;调试</td>
									                    					</tr>                     					                    					                    					                     		                   										                 										                      								
									            					</table>
														</td>
													</tr>
													<tr>
																			<TD nowrap colspan="20" align=center><br>
																			<input type="button" value="确 定" style="width:50" class="formStylebutton" onclick="toAdd()">&nbsp;&nbsp;
																			<input type="button" value="关 闭" style="width:50" onclick="window.close()">
																			</TD>	
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