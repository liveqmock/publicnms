<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.afunms.bpm.util.MenuConstance"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.bpm.design.model.DesignTempModel"%>
<%@page import="com.afunms.system.util.CodeUtil"%>
<%@page import="com.bpm.system.utils.StringUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>流程设计列表</title>
<%
String rootPath=request.getContextPath();
String menuTable = MenuConstance.getMenuTable();
JspPage jp = (JspPage)request.getAttribute("jsppage");
%>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<link href="<%=rootPath %>/js/jquery/themes/default/easyui.css" rel="stylesheet" type="text/css"/>
<link href="<%=rootPath %>/js/jquery/themes/icon.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
<script type="text/javascript" 	src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all-debug.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/system/design.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/global/systemutil.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/global/check.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<script language="javascript">	
var curpage= <%=jp.getCurrentPage()%>;
var totalpages = <%=jp.getPageTotal()%>;
var listAction = "<%=rootPath%>/controller/queryDesign.action";
</script>
<script language="javascript">	
  
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/controller/queryDesign.action";
     mainForm.submit();
  }
  
  function delpoyProcess()
  {  
     mainForm.action = "<%=rootPath%>/controller/designProcessDeploy.action";
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
<script type="text/javascript">
function showtype(modelid,key) {
var returnvalue = window.showModalDialog('<%=rootPath%>/system/bpmtype/radiobpmtype.jsp?modelid='+modelid+'&key='+key,window,'dialogWidth=400px;dialogHeight=1000px;center=yes')
if("refresh"==returnvalue) {
 window.location.reload();
}
}

function shownewtype() {
var returnvalue = window.showModalDialog('<%=rootPath%>/system/bpmtype/radiobpmtypenew.jsp?',window,'dialogWidth=400px;dialogHeight=1000px;center=yes')
if("nodefined"!=returnvalue&&null!=returnvalue) {
var attr = returnvalue.split("|");
$("#temptext").val(attr[1]);
$("#keytext").val(attr[0]);
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
									                	<td class="content-title"> 流程>> 流程管理 >> 流程设计 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<% if(jp!=null){ %>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
		        									<tr >
														<td >
															<table width="100%" >
																<tr>
									    							<td class="body-data-title" width="80%" align="center">
																		<jsp:include page="../../common/page.jsp">
																			<jsp:param name="curpage" value="<%=jp.getCurrentPage()%>" />
																			<jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>" />
																		</jsp:include>
														    		</td>
			        											</tr>
															</table>
														</td>
													</tr> 
													<tr class="body-data-title">
														<td>
														<div align="right">
															<input type="button" value="部署流程" onclick="delpoyProcess()"/> &nbsp;&nbsp;
       														<input type="button" value="新建流程模型" onclick="intialDesignDialog()"/> &nbsp;&nbsp;
       														<input type="button" value="编辑流程模型" onclick="editDesign()"/> &nbsp;&nbsp;
       														<input type="button" value="删除流程模型" onclick="delDesign()"/> 
    													</div>
    													<div id="dd" title="新建流程模型"  style="width: 400px;height: 300px;display: none">
   															 <table>
   																 <tr>
    																 <td><label><font class="red">*</font> 模型名：</label></td>
   																	 <td><input type="text" name="modelname" value="" size="23"/></td>
   																 </tr>
   																 <tr>
   																	 <td>&nbsp;&nbsp;<label>描述：</label></td>
    																 <td><textarea rows="3" cols="20"  name="modeldesc" id="desc"></textarea></td>
   																 </tr>
   																  <tr>
    																 <td><label><font class="red">*</font> 类型：</label></td>
   																	 <td>
   																	<input type="text" size="23" id="temptext" value="" readonly="readonly" onclick="shownewtype()"/>
   																	<input type=hidden id="keytext" name="keytext" value=""/>
   																	 </td>
   																 </tr>
    														</table>
   														 </div>
   														
														</td>
													</tr>
													<tr>
														<td>
															<table>
					        									<tr>
          																 <td align="center" class="body-data-title"><input name="checkall" type="checkbox" value=""/></td>
          																 <td align="center" class="body-data-title">模型名称</td>
          																 <td align="center" class="body-data-title">模型类型</td>
         																 <td align="center" class="body-data-title">描述信息</td>
					        									</tr>
		<%
	 	DesignTempModel model=null;
	 	if (jp.getList() != null)
	 	{
	 		for (int i = 0; i < jp.getList().size(); i++) 
	 		{
	 		model=(DesignTempModel)jp.getList().get(i);
	 			%><tr>
				<td align="center" class="body-data-list"><input type="checkbox" name="checkbox" value="<%=model.getModelid() %>"/></td>
				<td align="center" class="body-data-list"><%=model.getName()%></td>
				<td align="center" class="body-data-list" nowrap="nowrap" style="cursor: pointer;" onclick="showtype('<%=model.getModelid()%>','<%=model.getKeytext()%>')">
				<%
					if(StringUtil.isNotBlank(model.getTypename())) {
						out.print(model.getTypename());
					}else {
						out.println("");
					}
				%>
				
				</td>
				<td align="center" class="body-data-list"><%=model.getModeldesc() %></td>
				
			</tr><%
	 		}
	 	}
	 	%>
		        											</table>
														</td>
													</tr> 
		        								</table>
		        							</td>
		        						</tr>
		        						<% } %>
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
