<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.alarm.model.AlarmPort"%>

<%@page import="java.util.List"%>
<%@page import="com.afunms.config.model.Business"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.alarm.model.AlarmWay"%>
<%
  	String rootPath = request.getContextPath();
  
  	AlarmPort alarmPortNode = (AlarmPort)request.getAttribute("alarmPortNode");
 
  //	String pollinterstr = alarmPortNode.getPoll_interval()+"-"+alarmPortNode.getInterval_unit();
  	String onesel = "";
  	String twosel = "";
  	if(alarmPortNode.getCompare()==1)onesel = "selected";
  	if(alarmPortNode.getCompare()==0)twosel = "selected";
  	String nodeid = (String)request.getParameter("nodeid");
  	String ipaddress = (String)request.getParameter("ipaddress");
	Hashtable alarmWayHashtable = (Hashtable)request.getAttribute("alarmWayHashtable");
 
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

		setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
	 	Ext.get("process").on("click",function(){
	        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
	        //msg.style.display="block";
	        mainForm.action = "<%=rootPath%>/alarmport.do?action=update";
	        mainForm.submit();
	       
	 	});	
	
	});
//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	msgWindow=window.open(url,"protypeWindow","toolbar=no,width=1000,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
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
	initAttribute();
}

</script>
<script>
	function initAttribute(){
		document.getElementById("enabled").value = "<%=alarmPortNode.getEnabled()%>";
		document.getElementById("smsin1").value = "<%=alarmPortNode.getSmsin1()%>";
		document.getElementById("smsin2").value = "<%=alarmPortNode.getSmsin2()%>";
		document.getElementById("smsin3").value = "<%=alarmPortNode.getSmsin3()%>";
		document.getElementById("smsout1").value = "<%=alarmPortNode.getSmsout1()%>";
		document.getElementById("smsout2").value = "<%=alarmPortNode.getSmsout2()%>";
		document.getElementById("smsout3").value = "<%=alarmPortNode.getSmsout3()%>";
		
		<%
			if(alarmWayHashtable != null){
				AlarmWay alarmWayin1 = (AlarmWay)alarmWayHashtable.get("wayin1");
				if(alarmWayin1!=null){
					%>
					document.getElementById("wayin0-id").value = "<%=alarmPortNode.getWayin1()%>";
					document.getElementById("wayin0-name").value = "<%=alarmWayin1.getName()%>";
					<%
				}
				
				AlarmWay alarmWayin2 = (AlarmWay)alarmWayHashtable.get("wayin2");
				if(alarmWayin2!=null){
					%>
					document.getElementById("wayin1-id").value = "<%=alarmPortNode.getWayin1()%>";
					document.getElementById("wayin1-name").value = "<%=alarmWayin2.getName()%>";
					<%
				}
				
				AlarmWay alarmWayin3 = (AlarmWay)alarmWayHashtable.get("wayin3");
				if(alarmWayin3!=null){
					%>
					document.getElementById("wayin2-id").value = "<%=alarmPortNode.getWayin3()%>";
					document.getElementById("wayin2-name").value = "<%=alarmWayin3.getName()%>";
					<%
				}
			}
			if(alarmWayHashtable != null){
				AlarmWay alarmWayout1 = (AlarmWay)alarmWayHashtable.get("wayout1");
				if(alarmWayout1!=null){
					%>
					document.getElementById("wayout0-id").value = "<%=alarmPortNode.getWayout1()%>";
					document.getElementById("wayout0-name").value = "<%=alarmWayout1.getName()%>";
					<%
				}
				
				AlarmWay alarmWayout2 = (AlarmWay)alarmWayHashtable.get("wayout2");
				if(alarmWayout2!=null){
					%>
					document.getElementById("wayout1-id").value = "<%=alarmPortNode.getWayout1()%>";
					document.getElementById("wayout1-name").value = "<%=alarmWayout2.getName()%>";
					<%
				}
				
				AlarmWay alarmWayout3 = (AlarmWay)alarmWayHashtable.get("wayout3");
				if(alarmWayout3!=null){
					%>
					document.getElementById("wayout2-id").value = "<%=alarmPortNode.getWayout3()%>";
					document.getElementById("wayout2-name").value = "<%=alarmWayout3.getName()%>";
					<%
				}
			}
		%>
	}
	function initValue(){
		document.getElementById("enabled").value = "<%=alarmPortNode.getEnabled()%>";
		document.getElementById("smsin1").value = "<%=alarmPortNode.getSmsin1()%>";
		document.getElementById("smsin2").value = "<%=alarmPortNode.getSmsin2()%>";
		document.getElementById("smsin3").value = "<%=alarmPortNode.getSmsin3()%>";
	
		document.getElementById("smsout1").value = "<%=alarmPortNode.getSmsout1()%>";
		document.getElementById("smsout2").value = "<%=alarmPortNode.getSmsout2()%>";
		document.getElementById("smsout3").value = "<%=alarmPortNode.getSmsout3()%>";
	}
	
	function chooseAlarmWay(value,alarmWayIdEvent,alarmWayNameEvent){
		CreateWindow("<%=rootPath%>/alarmWay.do?action=chooselist&jp=1&alarmWayNameEvent=" + alarmWayNameEvent + "&alarmWayIdEvent=" + alarmWayIdEvent);
	}
</script>

</head>
<body id="body" class="body" onload="initmenu();initValue();">
	<form id="mainForm" method="post" name="mainForm">
		<input type="hidden" id="id" name="id" value="<%=alarmPortNode.getId()%>">
		<input type="hidden" name="nodeid" id="nodeid" value="<%=nodeid%>">
		<input type="hidden" name="ipaddress" id="ipaddress" value="<%=ipaddress%>">
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
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title"> 告警 >> 告警配置 >> 告警指标配置 >> 告警指标修改 </td>
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
																		<tr>						
																			<td align="right" height="24" width="10%">名称:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" id="name" name="name" readonly="readonly" value="<%=alarmPortNode.getName()%>">
																			</td>
																			<td align="right" height="24" width="10%">类型:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" id="type" name="type" readonly="readonly" value="<%=alarmPortNode.getType()%>">
																			</td>
																		</tr>	
																		
												    						<tr> 
												  							<td align="right" height="24" width="10%">是否监视:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<select id="enabled" name="enabled">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
																			<td align="right" height="24" width="10%">告警描述:&nbsp;</td>	
																			<td width="40%">&nbsp;
																				<input type="text" name="alarm_info" id="alarm_info" value="<%=alarmPortNode.getAlarm_info()%>">
																			</td>					
												 						</tr> 
												 						<tr style="background-color: #ECECEC;"> 
												  							<td align="right" height="24" width="10%">阀值比较方式:&nbsp;</td>				
																			<td width="90%" colspan=3>&nbsp;
																				<select id="compare" name="compare">
																					<option value="1" <%=onesel%>>升序</option>
																					<option value="0" <%=twosel%>>降序</option>
																				</select>
																			</td>				
												 						</tr>
												 						
												 						<tr style="background-color: #ECECEC;"> 
												  							<td align="right" height="24" width="10%">一级入口阀值:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="invalue1" id="invalue1" value="<%=alarmPortNode.getLevelinvalue1()%>">
																			</td>
																			<td align="right" height="24" width="10%">一级出口阀值:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="outvalue1" id="outvalue1" value="<%=alarmPortNode.getLeveloutvalue1()%>">
																			</td>
												 						</tr> 
												 						<tr> 
												  							<td align="right" height="24" width="10%">连续次数:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="intime1" id="intime1" value="<%=alarmPortNode.getLevelintimes1()%>">
																			</td>
																			<td align="right" height="24" width="10%">连续次数:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="outtime1" id="outtime1" value="<%=alarmPortNode.getLevelouttimes1()%>">
																			</td>
												 						</tr> 
												 						<tr style="background-color: #ECECEC;"> 
												  							<td align="right" height="24" width="10%">告警:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<select name="smsin1" id="smsin1">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
																			<td align="right" height="24" width="10%">告警:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<select name="smsout1" id="smsout1">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
																			
												 						</tr>
												 						<tr style="background-color: #ECECEC;"> 
												  							<td align="right" height="24" width="10%">告警方式:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" disabled="disabled" value="" name="wayin1-name" id="wayin1-name">
																				<input type="hidden" value="" name="wayin1-id" id="wayin1-id">
																				<a href="#" onclick="chooseAlarmWay('','wayin1-id','wayin1-name')">浏览</a>
																			</td>
																			<td align="right" height="24" width="10%">告警方式:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" disabled="disabled" value="" name="wayout1-name" id="wayout1-name">
																				<input type="hidden" value="" name="wayout1-id" id="wayout1-id">
																				<a href="#" onclick="chooseAlarmWay('','wayout1-id','wayout1-name')">浏览</a>
																			</td>
																			
												 						</tr>
												 						<tr> 
												  							<td align="right" height="24" width="10%">二级入口阀值:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="invalue2" id="invalue2" value="<%=alarmPortNode.getLevelinvalue2()%>">
																			</td>
																			<td align="right" height="24" width="10%">二级出口阀值:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="outvalue2" id="outvalue2" value="<%=alarmPortNode.getLeveloutvalue2()%>">
																			</td>
												 						</tr>
												 						<tr style="background-color: #ECECEC;"> 
												  							<td align="right" height="24" width="10%">连续次数:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="intime2" id="intime2" value="<%=alarmPortNode.getLevelintimes2()%>">
																			</td>
																			<td align="right" height="24" width="10%">连续次数:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="outtime2" id="outtime2" value="<%=alarmPortNode.getLevelouttimes2()%>">
																			</td>
												 						</tr>
												 						<tr> 
												  							<td align="right" height="24" width="10%">告警:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<select name="smsin2" id="smsin2">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
																			<td align="right" height="24" width="10%">告警:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<select name="smsout2" id="smsout2">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
																			
												 						</tr>
												 						<tr> 
												  							<td align="right" height="24" width="10%">告警方式:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" disabled="disabled" value="" name="wayin2-name" id="wayin2-name">
																				<input type="hidden" value="" name="wayin2-id" id="wayin2-id">
																				<a href="#" onclick="chooseAlarmWay('','wayin2-id','wayin2-name')">浏览</a>
																			</td>
																			<td align="right" height="24" width="10%">告警方式:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" disabled="disabled" value="" name="wayout2-name" id="wayout2-name">
																				<input type="hidden" value="" name="wayout2-id" id="wayout2-id">
																				<a href="#" onclick="chooseAlarmWay('','wayout2-id','wayout2-name')">浏览</a>
																			</td>
												 						</tr>
												 						<tr style="background-color: #ECECEC;"> 
												  							<td align="right" height="24" width="10%">三级入口阀值:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="invalue3" id="invalue3" value="<%=alarmPortNode.getLevelinvalue3()%>">
																			</td>
																			<td align="right" height="24" width="10%">三级出口阀值:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="outvalue3" id="outvalue3" value="<%=alarmPortNode.getLeveloutvalue3()%>">
																			</td>
												 						</tr>
												 						<tr> 
												  							<td align="right" height="24" width="10%">连续次数:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="intime3" id="intime3" value="<%=alarmPortNode.getLevelintimes3()%>">
																			</td>
																			<td align="right" height="24" width="10%">连续次数:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" name="outtime3" id="outtime3" value="<%=alarmPortNode.getLevelouttimes3()%>">
																			</td>
												 						</tr>
												 						<tr style="background-color: #ECECEC;"> 
												  							<td align="right" height="24" width="10%">告警:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<select name="smsin3" id="smsin3">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
																			<td align="right" height="24" width="10%">告警:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<select name="smsout3" id="smsout3">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
																			
												 						</tr> 
												 						<tr>
												 						<td align="right" height="24" width="10%">告警方式:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" disabled="disabled" value="" name="wayin3-name" id="wayin3-name">
																				<input type="hidden" value="" name="wayin3-id" id="wayin3-id">
																				<a href="#" onclick="chooseAlarmWay('','wayin3-id','wayin3-name')">浏览</a>
																			</td>
																			<td align="right" height="24" width="10%">告警方式:&nbsp;</td>				
																			<td width="40%" >&nbsp;
																				<input type="text" disabled="disabled" value="" name="wayout3-name" id="wayout3-name">
																				<input type="hidden" value="" name="wayout3-id" id="wayout3-id">
																				<a href="#" onclick="chooseAlarmWay('','wayout3-id','wayout3-name')">浏览</a>
																			</td>
																			</tr>
																			<tr>
																			<TD nowrap colspan="4" align=center>
																			<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																				<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">&nbsp;&nbsp;
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