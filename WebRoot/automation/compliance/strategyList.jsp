<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.automation.model.CompStrategy"%>
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
		<script  type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script> 
		<script language="JavaScript" src="<%=rootPath%>/automation/js/date.js"></script> 
		
        <script type="text/javascript" src="<%=rootPath%>/automation/js/jquery-1.8.2.min.js"></script>
<script language="JavaScript" type="text/JavaScript">
 

function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow", "height=450, width=750, toolbar= no, menubar=no, scrollbars=no, resizable=no, location=no, status=no,top=100,left=300")

}   
function createStrategy(){
return CreateWindow("<%=rootPath%>/configRule.do?action=createStrategy");
}
function deleteStrategy(){
mainForm.action="<%=rootPath%>/configRule.do?action=deleteStrategy";
mainForm.submit();
}

function editStrategy(id){
return CreateWindow("<%=rootPath%>/configRule.do?action=editStrategy&id="+id);
}
function showDetailStrategy(id){
return location.href="<%=rootPath%>/configRule.do?action=showDetailStrategy&id="+id;
}
function toAdd(id)
  {
    var url="<%=rootPath%>/configRule.do?action=ready_addip&id="+id;
	window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
  }
</script>



<script language="JavaScript" type="text/JavaScript">


function setClass(){
	document.getElementById('configRuleTitle-0').className='detail-data-title';
	document.getElementById('configRuleTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('configRuleTitle-0').onmouseout="this.className='detail-data-title'";
}

function refer(action){
		var mainForm = document.getElementById("mainForm");
		mainForm.action = '<%=rootPath%>' + action;
		mainForm.submit();
}
</script>


</head>
<body id="body" class="body" onload="setClass()">
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
																							 &nbsp;&nbsp;&nbsp;<input name="button"  onclick="createStrategy()" type="button"  value="新建策略" />
																							 <input name="button"  onclick="deleteStrategy()" type="button"  value="删除策略" />
																							
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
																	        <td class="report-data-body-title" width="10%">策略名称</td>
																	        <td class="report-data-body-title" width="40%">描述</td>
																	        <td class="report-data-body-title" width="10%">关联设备</td>
																	        <td class="report-data-body-title" width="10%">配置类型</td>
																	    	<td class="report-data-body-title" width="10%">创建人</td>
																	    	<td class="report-data-body-title" width="10%">编辑</td>
																	   </tr>
																	   <%
																	   if(list!=null){
																	   String typeImg="";
																	   for(int i=0;i<list.size();i++){
																	    CompStrategy vo=(CompStrategy)list.get(i);
																	    String type="启动";
																	    if(vo.getType()==0){
																	    type="正在运行";
																	    typeImg="<img src='"+rootPath+"/automation/images/Config-Running.gif'>";
																	    }else{
																	     type="启动";
																	      typeImg="<img src='"+rootPath+"/automation/images/Config-Startup.gif'>";
																	    }
																	    %>
																	    <tr <%=onmouseoverstyle%>>
																	    	<td align="center" class="body-data-list"><INPUT type="checkbox" class=noborder name="checkbox" value="<%=vo.getId()%>" ><%=i+1 %></td>
																	        <td align="center" class="body-data-list"><a href="#" onclick="showDetailStrategy(<%=vo.getId()%>)"><%=vo.getName() %></a></td>
																	        <td align="center" class="body-data-list"><%=vo.getDescription() %></td>
																	    	<td align="center" class="body-data-list"><a href="#" onclick="toAdd('<%=vo.getId() %>')">关联设备</a></td>
																	    	<td align="center" class="body-data-list"><%=typeImg %><%=type %></td>
																	    	<td align="center" class="body-data-list"><%=vo.getCreateBy() %></td>
																	    	<td align="center" class="body-data-list"><a href="#" onclick="editStrategy(<%=vo.getId()%>)"><img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td>
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