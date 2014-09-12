<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/automation/common/globe.inc"%>

<% 
	String rootPath = request.getContextPath();    
	String commands = (String)request.getAttribute("commands");
	String fileName = (String)request.getAttribute("fileName");
	//String fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/automation/js/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/automation/js/wfm.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/automation/css/ext/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" 	src="<%=rootPath%>/automation/js/timeShareConfigdiv.js" charset="gb2312"></script>

<script language="JavaScript" type="text/javascript">


Ext.onReady(function(){

 Ext.get("backup").on("click",function(){
     	Ext.MessageBox.wait('数据加载中，请稍后.. ');
        mainForm.action = "<%=rootPath%>/autoControl.do?action=saveCmdCfg";
        mainForm.submit();
        
        
 });
});

	


    


</script>


<script>
//-- nielin add for timeShareConfig 2010-01-03 end-------------------------------------------------------------
	/*
	* 此方法用于短信分时详细信息
	* 需引入 /application/resource/js/timeShareConfigdiv.js 
	*/
	//接受用户的列表
	var action = "<%=rootPath%>/user.do?action=setReceiver";
	// 获取短信分时详细信息的div
	function timeShareConfiginit(){
		var rowNum = document.getElementById("rowNum");
		rowNum.value = "0";
		// 获取设备或服务的分时数据列表,
		var timeShareConfigs = new Array();
		var smsConfigs = new Array();
		var phoneConfigs = new Array();
	    for(var i = 0; i< timeShareConfigs.length; i++){
	    	var item = timeShareConfigs[i];
	    	if(item.timeShareType=="sms"){
	    		smsConfigs.push({
	                timeShareConfigId:item.timeShareConfigId,
	                timeShareType:item.timeShareType,
	                beginTime:item.beginTime,
	                endTime:item.endTime,
	                userIds:item.userIds
	            });
	    	}
	    	if(item.timeShareType=="phone"){
	    		phoneConfigs.push({
	                timeShareConfigId:item.timeShareConfigId,
	                timeShareType:item.timeShareType,
	                beginTime:item.beginTime,
	                endTime:item.endTime,
	                userIds:item.userIds
	            });
	    	}
	    }
		timeShareConfig("smsConfigTable",smsConfigs);
		timeShareConfig("phoneConfigTable",phoneConfigs);
	}
//---- nielin add for timeShareConfig 2010-01-03 end-------------------------------------------------------------------------
</script>


</head>
<body id="body" >


	<!-- 右键菜单结束-->
   <form name="mainForm" method="post">
      <input type=hidden name="commands" value="<%=commands %>">
		<table id="body-container" class="body-container">
			<tr style="background-color: #FFFFFF;">
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
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">&nbsp;保存命令文件</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
																			<tr style="background-color: #ECECEC;">
													                  					<TD nowrap align="right" height="24" width="10%">文件名&nbsp;</TD>
													                  					<TD nowrap width="40%">&nbsp;
													                  					<input type="text" name="fileName" maxlength="50" size="50"  value="<%=fileName %>" >
													                  					
													                  					</TD>
													                		</tr>
																			<tr > 
													  							<TD nowrap align="right" height="24" width="10%">文件备注&nbsp;</TD>       
																				<TD nowrap width="40%" colspan=3>&nbsp;
																					<input type="text" id="fileDesc" name="fileDesc" maxlength="32" size="50" value="" onclick="#">&nbsp;&nbsp; 
																				</td>
													 						</tr>
													    						
																			<tr>
																				<TD nowrap colspan="4" align=center>
																				<br>
																				
																					<input type="button"  id="backup" style="width:50" value="确  定">&nbsp;&nbsp;
																					<input type="button" style="width:50" value="关 闭" onclick="window.close()">&nbsp;&nbsp;
																				</TD>	
																			</tr>	
																			
																		</TABLE>
										 							
										 							
				        										</td>
				        									</tr>
				        								</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-footer" class="detail-content-footer">
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
				</td>
			</tr>
		</table>
		
	</form>
</BODY>


</HTML>