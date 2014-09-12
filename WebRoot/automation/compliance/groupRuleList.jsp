<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.automation.model.CompGroupRule"%>
<%@ include file="/automation/common/globe.inc"%>
<%@ include file="/automation/common/globeChinese.inc"%>

<%@page import="java.util.List"%>

<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
 
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/automation/js/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script> 
<script language="JavaScript" src="<%=rootPath%>/automation/date.js"></script> 

<script language="JavaScript" type="text/JavaScript">


function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow", "height=450, width=750, toolbar= no, menubar=no, scrollbars=no, resizable=no, location=no, status=no,top=100,left=300")

}   
function createGroupRule(){
return CreateWindow("<%=rootPath%>/configRule.do?action=createGroupRule");
}
function deleteGroupRule(){
mainForm.action="<%=rootPath%>/configRule.do?action=deleteGroupRule";
mainForm.submit();
}

function editGroupRule(id){
return CreateWindow("<%=rootPath%>/configRule.do?action=editGroupRule&id="+id);
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
	setClass();
}

function setClass(){
	document.getElementById('configRuleTitle-1').className='detail-data-title';
	document.getElementById('configRuleTitle-1').onmouseover="this.className='detail-data-title'";
	document.getElementById('configRuleTitle-1').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>


</head>
<body id="body" class="body" onload="initmenu();">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
	<form id="mainForm" method="post" name="mainForm">
		<table id="body-container" class="body-container">
			<tr>
				
				<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-report">
								<table id="container-main-report" class="container-main-report">
									<tr>
										<td>
											<table id="report-content" class="report-content">
												<tr>
													<td>
														<table id="report-content-header" class="report-content-header">
										                	<tr>
											    				<td>
														    		<%=configRuleTitleTable%>
														    	</td>
														  	</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="report-content-body" class="report-content-body">
				        									<tr>
				        										<td>
				        											
				        											<table id="report-data-header" class="report-data-header">
				        												<tr>
															  				<td>
																
																				<table id="report-data-header-title" class="report-data-header-title">
																					<tr>
																						<td >
																							 &nbsp;&nbsp;&nbsp;<input name="button"  onclick="createGroupRule()" type="button"  value="新建规则组" />
																							 <input name="button"  onclick="deleteGroupRule()" type="button"  value="删除规则组" />
																							 <input name="button"  onclick="" type="button"  value="Adhoc测试" />
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
				        											
				        											<table id="report-data-body" class="report-data-body">
				        												
				        												<tr height=28>
																	    	<td class="report-data-body-title" width="10%">&nbsp;<INPUT type="checkbox" name="checkall" onclick="javascript:chkall()">序号</td>
																	        <td class="report-data-body-title" width="30%">规则组名称</td>
																	        <td class="report-data-body-title" width="40%">描述</td>
																	    	<td class="report-data-body-title" width="20%">创建人</td>
																	   </tr>
																	   <%
																	   if(list!=null){
																	   for(int i=0;i<list.size();i++){
																	    CompGroupRule groupRule=(CompGroupRule)list.get(i);
																	    %>
																	    <tr <%=onmouseoverstyle%>>
																	    	<td align="center" class="body-data-list"><INPUT type="checkbox" class=noborder name="checkbox" value="<%=groupRule.getId()%>" ><%=i+1 %></td>
																	        <td align="center" class="body-data-list"><a href="#" onclick="editGroupRule(<%=groupRule.getId()%>)"><%=groupRule.getName() %></a></td>
																	        <td align="center" class="body-data-list"><%=groupRule.getDescription() %></td>
																	    	<td align="center" class="body-data-list"><%=groupRule.getCreatedBy()%></td>
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