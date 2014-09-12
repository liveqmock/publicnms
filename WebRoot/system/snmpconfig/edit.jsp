<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>

<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.system.model.*"%>

<%
  String rootPath = request.getContextPath();
  SnmpConfig vo = (SnmpConfig)request.getAttribute("vo");
  String v1value ="";
  String v2value="";
  if(vo.getSnmpversion() ==0)v1value="selected";
  if(vo.getSnmpversion() ==1)v2value="selected";
  String menuTable = (String)request.getAttribute("menuTable");%>
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

<!--nielin add for timeShareConfig at 2010-01-04 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<!--nielin add for timeShareConfig at 2010-01-04 end-->


<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
  
    var chk = checkinput("name","string","名称",30,false);
     var chk1 = checkinput("readcommunity","string","读团体",30,false);
     var chk2 = checkinput("writecommunity","string","写团体",30,false);
     var chk3 = checkinput("timeout","integer","超时时间",30,false);
     var chk4 = checkinput("trytime","integer","重试次数",30,false);
     
     if(chk && chk1 && chk2 && chk3 && chk4 )
     {
     	    
        	mainForm.action = "<%=rootPath%>/snmp.do?action=update";
        	mainForm.submit();
        
     }
       // mainForm.submit();
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
	timeShareConfiginit(); // nielin add for time-sharing at 2010-01-04
}

</script>


</head>
<body id="body" class="body" onload="initmenu();">

    <form method="post" name="mainForm">
          <input type=hidden name="id" value="<%=vo.getId()%>">
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
											                	<td class="add-content-title">系统管理 >> 资源管理  >> SNMP设置编辑</td>
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
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1"
																	width="100%">
																
																	<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">名称&nbsp;</TD>
                  					<TD nowrap width="20%">&nbsp;&nbsp;<input type="text" name="name" maxlength="50" size="20" class="formStyle" value="<%=vo.getName()%>"></TD>
                				</tr> 
                  				<tr>
                  					<TD nowrap align="right" height="24" width="10%">SNMP版本&nbsp;</TD>
                  					<TD nowrap width="20%">&nbsp;
                  					<select name="snmpversion">
                  					<option value="0" <%=v1value%>>SNMP V1</option>
                  					<option value="1" <%=v2value%>>SNMP V2c</option>
                  					</select>
                  					</TD>
                				</tr> 
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">读团体&nbsp;</TD>
                  					<TD nowrap width="20%">&nbsp;
                  					<input type="text" name="readcommunity" maxlength="50" size="20" class="formStyle" value="<%=vo.getReadcommunity()%>">
                  					</TD>
                				</tr> 
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%">写团体&nbsp;</TD>
                  					<TD nowrap width="20%">&nbsp;
                  					<input type="text" name="writecommunity" maxlength="50" size="20" class="formStyle" value="<%=vo.getWritecommunity()%>">
                  					</TD>
                				</tr>
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">超时时间(秒)&nbsp;</TD>
                  					<TD nowrap width="20%">&nbsp;
                  					<input type="text" name="timeout" maxlength="50" size="20" class="formStyle" value="<%=vo.getTimeout()%>">
                  					</TD>
                				</tr>
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%">重试次数&nbsp;</TD>
                  					<TD nowrap width="20%">&nbsp;
                  					<input type="text" name="trytime" maxlength="50" size="20" class="formStyle" value="<%=vo.getTrytime()%>">
                  					</TD>
                				</tr>             									                 										                      								            							
															<tr>
																<TD nowrap colspan="4" align=center>
																<br><input type="button" value="修改" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																	<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
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
</body>
</HTML>