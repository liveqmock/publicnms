<%@page import="org.activiti.engine.repository.ProcessDefinition"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.afunms.bpm.util.MenuConstance"%>
<%@page import="com.afunms.system.util.CodeUtil"%>
<%@page import="java.util.Map"%>
<%@page import="com.bpm.system.utils.StringUtil"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<c:set var="path" value="${request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>流程定义列表</title>
<%
String rootPath=request.getContextPath();
String result = (String) request.getAttribute("result");
//if ("success".equals(result)) {
	//out.println("<script>window.alert('启动成功');</script>");
	//return ;
//}
String menuTable = MenuConstance.getMenuTable();
JspPage jp = (JspPage)request.getAttribute("jsppage");
Map<String,String> map = CodeUtil.getCodeModelType();
%>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		
		<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
		<script type="text/javascript" 	src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all-debug.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.7.2.min.js"></script>
<%if(null!=jp){ %>
<script language="javascript">	
var curpage= <%=jp.getCurrentPage()%>;
var totalpages = <%=jp.getPageTotal()%>;
var listAction = "<%=rootPath%>/controller/processDefineList.action";
var result="<%=result %>";
function loadresult()
{
	if(result=="error")
	{
		alert("你已经启动该流程，还未执行，请先执行！");
	}
	else if(result=="success")
	{
		alert("启动成功");
	}
 }
</script>
<%} %>

<script type="text/javascript">
function showPic(e,id,name){   
	var srcv="<%=rootPath%>/controller/processPicture.action?deploymentId="+id+"&resourceName="+name;
	var imgv=document.getElementById("imgid"); 
	imgv.src=srcv;  
	var div = document.getElementById("divid");   
	div.style.display="block"; 
	var x = e.clientX + document.body.scrollLeft;
	var y = e.clientY + document.body.scrollTop;
	if(x>800)
	{
	 div.style.left=x-400+"px"; 
	}
	else
	{
	div.style.left=x+10+"px"; 
	}
	div.style.top=y+5+"px";   
	div.style.position="absolute"; //必须指定这个属性，否则div3层无法跟着鼠标动   
}   

function hiddenPic(){   
	var imgv=document.getElementById("imgid"); 
	imgv.src="";  
	var div = document.getElementById("divid"); //将要弹出的层   
	div.style.display="none"; 
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

function startProcess(processId) {
    $.ajax({
						   type: "POST",
						   url: "controller/startProcessAuto.action",
						   data: "processId="+processId+"&manual=1",
						   success: function(msg){
						       if(msg=="noauth") {
						       //   alert("流程已启动，当前环节你没有继续操作的权限。");
						       	  alert("流程已启动");
						          return ;
						       }
						       window.location.href="controller/taskFormKey.action?taskId="+msg+"&flag=first";
						   }
						});
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
<body id="body" class="body" onload="initmenu();loadresult();">
<iframe name="myiframe" id="myiframe" width="0" height="0"></iframe>
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
									                	<td class="content-title"> 流程 >> 流程管理 >> 流程定义列表 </td>
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
													<tr>
														<td>
															<table>
															<tr>
          																 <td align="center" class="body-data-title">定义Id</td>
          																 <td align="center" class="body-data-title">部署Id</td>
         																 <td align="center" class="body-data-title">名称</td>
         															     <td align="center" class="body-data-title">KEY</td>
         															     <td align="center" class="body-data-title">类型</td>
         																 <td align="center" class="body-data-title">XML</td>
																		 <td align="center" class="body-data-title">图片</td>
																		 <td align="center" class="body-data-title">启动</td>
																		 <td align="center" class="body-data-title">删除</td>
					        									</tr>
		<%
	 	ProcessDefinition pd=null;
	 	if (jp.getList() != null)
	 	{
	 		for (int i = 0; i < jp.getList().size(); i++) 
	 		{
	 		pd=(ProcessDefinition)jp.getList().get(i);
	 			%><tr>
				<td onmouseover='showPic(event,"<%=pd.getDeploymentId() %>","<%=pd.getDiagramResourceName() %>");' onmouseout="hiddenPic();" align="center" class="body-data-list"><%=pd.getId() %></td>
				<td align="center" class="body-data-list"><%=pd.getDeploymentId() %></td>
				<td align="center" class="body-data-list"><%=pd.getName() %></td>
				<td align="center" class="body-data-list"><%=pd.getKey() %></td>
				<td align="center" class="body-data-list"><%=StringUtil.isBlank(map.get(pd.getResourceName()))?"":map.get(pd.getResourceName())%></td>
				<td align="center" class="body-data-list"><a target="_blank" href="../controller/processXML.action?
						deploymentId=<%=pd.getDeploymentId() %>&resourceName=<%=pd.getResourceName() %>">
						<%=pd.getResourceName()%>
						</a></td>
				<td align="center" class="body-data-list"><a target="_blank" href="../controller/processPicture.action?
						deploymentId=<%=pd.getDeploymentId() %>&resourceName=<%=pd.getDiagramResourceName() %>">
						<%=pd.getDiagramResourceName() %>
						</a></td>
				<td align="center" class="body-data-list"><a href="../controller/processDefineStart.action?processId=<%=pd.getId() %>">启动</a></td>
				<td align="center" class="body-data-list">
				<a href="../controller/processDefineDel.action?deploymentId=<%=pd.getDeploymentId() %>" onclick="return window.confirm('确定要删除?')">删除</a>
				</td>
				
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
		<div id="divid" style="background:#eeeeee;border:1px solid #ffffff;position:absolute;display: none;">
			<img id="imgid" src="">
		</div>
	</body>
</html>
