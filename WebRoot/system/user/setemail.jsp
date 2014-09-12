<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Vector"%>
<%@ page import="com.afunms.system.model.*"%>
<%
	String rootPath = request.getContextPath();
	Vector ids = (Vector)request.getAttribute("ids");
	List operatorlist = new ArrayList();
	operatorlist = (List)request.getAttribute("list");
%>
<html>
<head>
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<script language="JavaScript" type="text/javascript" fptype="dynamicoutline">

function valid(){
 		var oids ="";	
 		alert("=================");
 		for (var i=0;i<mainForm.checkbox.length;i++){
 			if(mainForm.checkbox[i].checked==true){
 				if (oids==""){
 					oids=mainForm.checkbox[i].value;
 				}else{
 					oids=oids+","+mainForm.checkbox[i].value;
 				}
 			}
 		}
		alert("=====");
 		parent.opener.document.all.sendmobiles.value=oids;
 		alert("##############");
                /*
                 * lijun modify begin;
                 */
                 alert("11111111111");
                if(typeof(parent.opener.setMobileValue)!="undefined"){
                	alert("222222222222");
                    parent.opener.setMobileValue(oids);
                    alert("33333");
                }
                /*
                 * lijun modify end;
                 */
		window.close();
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
	timeShareConfiginit(); // nielin add for time-sharing at 2010-01-04
}

</script>
</head>
<body id="body" class="body" onload="initmenu();">

	<!-- 右键菜单结束-->
   <form name="mainForm" method="post">
   <input type=hidden name="oid">
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
											                	<td class="add-content-title">设置接收的用户</td>
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
					<TBODY>
												<tr align="right" > 
							  <td colspan=10 align=left height=18>
							  <table width="100%" cellSpacing="0"  cellPadding="0" border=0  class="noprint">
							  <tr >
							  <td align="right" height=18>
							  <input type="submit" value="确 定" name="delbutton" class=button onclick="return valid()">
							
							<INPUT type="button" value="关闭窗口" id=button1 name=button1 onclick="window.close()" class="button"> 
							           
							    </td> </tr> </table>
							    </td>
							  </tr>
									<tr  class="firsttr">
							    <td class="report-data-body-title"><input type=checkbox name="checkall" onclick="javascript:chkall()"  class="noborder"> <strong></strong><br> </td>
							    <td class="report-data-body-title">&nbsp;</td>
							    <td class="report-data-body-title"><strong>ID</strong></td>
							    <td class="report-data-body-title"><strong>姓名</strong></td>
							    <td class="report-data-body-title"><strong>手机号码</strong></td>
							    	<td class="report-data-body-title"><strong>邮件</strong></td>
							    </tr>
							  <%
							  	int index = 0;
							  %>
							  <%
							  	if (operatorlist != null && operatorlist.size()>0){
							  		for(int i=0;i<operatorlist.size();i++){
							  			User user = (User)operatorlist.get(i);
							  			%>
							  			<tr  class="report-data-body-list" class="othertr" <%=onmouseoverstyle%> ondblclick="submitCheck1('<%=user.getId()%>','dbclicksubmit')">
							  			<%
							  			if (ids != null && ids.size()>0){
							  				if (ids.contains(user.getId())){
							  				%>
							    				<td class="report-data-body-list" height="20">
							    					<input type=checkbox name=checkbox value='<%=user.getId()%>' class=noborder onclick="submitCheck1('<%=user.getId()%>','checkthisbox')" checked>
							    				</td>  				
							  				<%
							  				}else{
							  				%>
							    				<td class="report-data-body-list" height="20">
							    					<input type=checkbox name=checkbox value='<%=user.getId()%>' class=noborder onclick="submitCheck1('<%=user.getId()%>','checkthisbox')">
							    				</td>  				  				
							  				<%
							  				}
							  			}else{
							  			%>
							    				<td class="report-data-body-list" height="20">
							    					<input type=checkbox name=checkbox value='<%=user.getId()%>' class=noborder onclick="submitCheck1('<%=user.getId()%>','checkthisbox')">
							    				</td>  				  			
							  			<%
							  			}%>
							    <td class="report-data-body-list" onclick="submitCheck1('<%=user.getId()%>','checkthis')">&nbsp;<%=i+1%></td>
							    <td class="report-data-body-list" onclick="submitCheck1('<%=user.getId()%>','checkthis')">       
							      <%=user.getUserid()%></td>
							      <td class="report-data-body-list" onclick="submitCheck1('<%=user.getId()%>','checkthis')">
							      <%=user.getName()%></td>
							      <td class="report-data-body-list" onclick="submitCheck1('<%=user.getId()%>','checkthis')">
							      <%=user.getMobile()%></td>  			
							      <td class="report-data-body-list" onclick="submitCheck1('<%=user.getId()%>','checkthis')">
							      <%=user.getEmail()%></td>  			
							  			
							  			<%	
							  		}
							  	}
							  %>
            					</tbody>
            					</table>
            					<br>
			</TD>																			
			</tr>			
															
															<tr>
																<!-- nielin add (for timeShareConfig) start 2010-01-03 -->
																<td><input type="hidden" id="rowNum" name="rowNum"></td>
																<!-- nielin add (for timeShareConfig) end 2010-01-03 -->
															
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