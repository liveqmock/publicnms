<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.afunms.bpm.util.MenuConstance"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.bpm.system.model.FormModel"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<c:set var="path" value="${request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>表单列表</title>
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
<script type="text/javascript" src="<%=rootPath %>/js/main.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/global/systemutil.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/system/form.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<%if(null!=jp) { %>
<script language="javascript">	
var curpage= <%=jp.getCurrentPage()%>;
var totalpages = <%=jp.getPageTotal()%>;
var listAction = "<%=rootPath%>/controller/formList.action";
</script>
<%} %>

<script type="text/javascript">
function showPic(e,id){   
	var srcv="<%=rootPath%>/controller/formImg.action?id="+id;
	var imgv=document.getElementById("formimg"); 
	imgv.src=srcv;  
	var div = document.getElementById("formdiv");   
	div.style.display="block"; 
	var x = e.clientX + document.body.scrollLeft;
	var y = e.clientY + document.body.scrollTop;
	if(x>700)
	{
	 div.style.left=x-200+"px"; 
	}
	else
	{
	div.style.left=x+10+"px"; 
	}
	div.style.top=y+5+"px";   
	div.style.position="absolute"; //必须指定这个属性，否则div3层无法跟着鼠标动   
}   

function hiddenPic(){   
	var imgv=document.getElementById("formimg"); 
	imgv.src="";  
	var div = document.getElementById("formdiv"); //将要弹出的层   
	div.style.display="none"; 
} 

</script>

<script language="javascript">	
function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/controller/formList.action";
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
									                	<td class="content-title"> 流程>> 流程管理 >> 表单列表 </td>
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
       														<input type="button" value="删除表单" onclick="delform()"/> 
    													</div>
														</td>
													</tr>
													<tr>
														<td>
															<table>
					        									<tr>
          																 <td align="center" class="body-data-title"><input name="checkall" type="checkbox" value=""/></td>
          																 <td align="center" class="body-data-title">ID</td>
         																 <td align="center" class="body-data-title">名称</td>
					        									</tr>
		<%
	 	FormModel model=null;
	 	if (jp.getList() != null)
	 	{
	 		for (int i = 0; i < jp.getList().size(); i++) 
	 		{
	 		model=(FormModel)jp.getList().get(i);
	 			%><tr>
				<td onmouseover='showPic(event,"<%=model.getId() %>");' onmouseout="hiddenPic();" align="center" class="body-data-list"><input type="checkbox" name="checkbox" value="<%=model.getId() %>"/></td>
				<td align="center" class="body-data-list"><%=model.getId() %></td>
				<td align="center" class="body-data-list"><%=model.getName() %></td>
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
		<div id="formdiv" style="border:1px solid #ffffff;position:absolute;display: none;">
			<img id="formimg" src="">
		</div>
		
	</body>
</html>
