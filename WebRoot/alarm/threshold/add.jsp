<%@page language="java" contentType="text/html;charset=gb2312" %>

<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.config.model.Business"%>
<%@page import="com.afunms.system.model.TimeShareConfig"%>
<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>





<script language="JavaScript" type="text/javascript">


	Ext.onReady(function(){  

	 	Ext.get("process").on("click",function(){
	        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
	        //msg.style.display="block";
	        mainForm.action = "<%=rootPath%>/alarmIndicators.do?action=save";
	        mainForm.submit();
	 	});	
	});
//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
//-- nielin modify at 2010-01-04 end ----------------



//-- nielin add at 2010-07-27 start ----------------
function setBid(eventTextId , eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/business.do?action=setBid&event='+event.id+'&value='+event.value + '&eventText=' + eventTextId);
}
//-- nielin add at 2010-07-27 end ----------------





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
											                	<td class="add-content-title"> 告警 >> 告警配置 >> 告警指标配置 >> 告警指标添加 </td>
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
				        											<table cellspacing="1">
																		<tr style="background-color: #ECECEC;">						
																			<td align="right" height="24" width="10%">名称:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" id="name" name="name">
																			</td>
																			<td align="right" height="24" width="10%">类型:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" id="type" name="type">
																			</td>
																		</tr>	
																		<tr>
																			<td align="right" height="24" width="10%">子类型:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" id="subtype" name="subtype">
																			</td>
																			<td align="right" height="24" width="10%">描述:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" id="descr" name="descr">
																			</td>
																		</tr>
												    					<tr style="background-color: #ECECEC;"> 
												  							<td align="right" height="24" width="10%">是否监视:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<select id="enabled" name="enabled">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
																			<td align="right" height="24" width="10%">告警描述:&nbsp;</td>	
																			<td width="40%">&nbsp;
																				<input type="text" name="alarm_info" id="alarm_info">
																			</td>					
												 						</tr> 
												 						<tr> 
												  							<td align="right" height="24" width="10%">阀值单位:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" name="threshold_unit" id="threshold_unit">
																			</td>
																			<td align="right" height="24" width="10%">数据类型:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<select name="datatype" id="datatype">
																					<option value="String">字符串</option>
																					<option value="Number">数字</option>
																				</select>
																			</td>
												 						</tr> 
												 						<tr> 
												  							<td align="right" height="24" width="10%">一级阀值:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" name="limenvalue0" id="limenvalue0">
																			</td>
												 						</tr> 
												 						<tr> 
												  							<td align="right" height="24" width="10%">告警次数:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" name="time0" id="time0">
																			</td>
												 						</tr> 
												 						<tr > 
												  							<td align="right" height="24" width="10%">是否发送短信:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<select name="sms0" id="sms0">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
												 						</tr>
												 						<tr > 
												  							<td align="right" height="24" width="10%">二级阀值:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" name="limenvalue1" id="limenvalue1">
																			</td>
												 						</tr>
												 						<tr > 
												  							<td align="right" height="24" width="10%">告警次数:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" name="time1" id="time1">
																			</td>
												 						</tr>
												 						<tr> 
												  							<td align="right" height="24" width="10%">是否发送短信:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<select name="sms1" id="sms1">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
												 						</tr>
												 						<tr> 
												  							<td align="right" height="24" width="10%">三级阀值:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" name="limenvalue2" id="limenvalue2">
																			</td>
												 						</tr>
												 						<tr > 
												  							<td align="right" height="24" width="10%">告警次数:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" name="time2" id="time2">
																			</td>
												 						</tr>
												 						<tr> 
												  							<td align="right" height="24" width="10%">是否发送短信:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<select name="sms2" id="sms2">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
												 						</tr>
																		<tr>
																			<TD nowrap colspan="4" align=center>
																			<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																				<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
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
									<tr>
										<td>
											
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

<script>

function unionSelect(){
	var type = document.getElementById("type");
	var nameFont = document.getElementById("nameFont");
	var db_nameTD = document.getElementById("db_nameTD");
	var db_nameInput = document.getElementById("db_nameInput");
	var category = document.getElementById("category");
	var port  = document.getElementById("port");
	if(type.value == 2){
		nameFont.style.display="inline";
		db_nameTD.style.display="none";
		db_nameInput.style.display="none";
	}else{
		nameFont.style.display="none";
		db_nameTD.style.display="inline";
		db_nameInput.style.display="inline";
		
	}
	var categoryvalue = "";
	var portvalue = "";
	if(type.value == 1){
		categoryvalue = 53;
		portvalue = 1521;
	}else if(type.value == 2){
		categoryvalue = 54;
		portvalue = 1433;
	}else if(type.value == 4){
		categoryvalue = 52;
		portvalue = 3306;
	}else if(type.value == 5){
		categoryvalue = 59;
		portvalue = 50000;
	}else if(type.value == 6){
		categoryvalue = 55;
		portvalue = 2638;
	}else if(type.value == 7){
		categoryvalue = 60;
		portvalue = 9088;
	}
	port.value = portvalue;
	category.value = categoryvalue;
}

unionSelect();

</script>

</HTML>