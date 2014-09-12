<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.afunms.bpm.util.MenuConstance"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<%
	String rootPath = request.getContextPath();
String menuTable = MenuConstance.getMenuTable();
%>
<head></head>
<link href="<%=rootPath%>/css/main.css" rel="stylesheet" type="text/css"/>
<link href="<%=rootPath %>/js/jquery/themes/default/easyui.css" rel="stylesheet" type="text/css"/>
<link href="<%=rootPath %>/js/jquery/themes/icon.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/global/systemutil.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/global/check.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/system/group.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<script language="javascript">	
  
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/network.do?action=find";
     mainForm.submit();
  }
  
    function doCancelManage()
  {  
     mainForm.action = "<%=rootPath%>/network.do?action=cancelmanage";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
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
<body id="body" class="body" onload="initmenu();">


<form id="mainform" method="post" name="mainForm">
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
									                	<td class="content-title"> 系统管理 >> 用户管理 >> 岗位管理 </td>
									                    <td align="right"> <img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
										</tr>
										<tr>
											<td>
												<table id="content-body" class="content-body">
													<tr>
														<td>
															<table>
																<tr>
																	<td class="body-data-title" style="text-align: right;">
																		<a href="#" onclick="add()">添加</a>
																		<a href="#" onclick="modify()">修改</a>&nbsp;&nbsp;
																		<a href="#" onclick="delform()">删除</a>&nbsp;&nbsp;&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
																	<td align="center" class="body-data-title">
																		<INPUT type="checkbox" class=noborder name="checkall"
																			onclick="javascript:chkall()">
																	</td>
																	<td align="center" class="body-data-title">
																		编号
																	</td>
																	<td align="center" class="body-data-title">
																		岗位名
																	</td>
																	
																</tr>
																<c:forEach items="${list}" var="group">
 	<tr>
 	<td align="center" class="body-data-list"><input type="checkbox" name="checkbox" value="${group.id}"/> </td>
 	<td align="center" class="body-data-list">${group.id} </td>
 	<td align="center" class="body-data-list">${group.name} </td>
 	</tr>
 	</c:forEach>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
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
		<div id="adddiv" title="添加岗位" style="width: 400px;height: 0px" >
   <form action="groupModify.action" id="addform" onsubmit="return checkadddata();">
		<table id="addtable" style="display: none" >
		<tr>
		<td ><font class="red">*</font> 编号：</td>
		<td><input type="text" name="id" id="addid" value="" onblur="checkgroupid(this.value)"/><span class="red" id="mess"></span></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 岗位名：</td>
		<td><input type="text" name="name" id="add" value=""/></td>
		</tr>
		</table>
		 </form>
	</div>
	
	
	<div id="modifydiv" title="修改岗位" style="width: 400px;height: 0px">
   <form action="groupModify.action" id="modifyform" onsubmit="return checkmodifydata();">
		<table id="modifytable" style="display: none" >
		<tr>
		<td ><font class="red">*</font> 岗位名：</td>
		<td><input type="text" name="name" id="modify" value=""/>
		<input type="hidden" value="" name="id" id="modifyid"/>
		</td>
		<tr>
		<td colspan="2">
		&nbsp;<br>
		&nbsp;
		</tr>
		</table>
		 </form>
	</div>
</body>

<c:choose>
  <c:when test="${result=='error'}">
  <script type="text/javascript">
$.messager.alert("提示信息","执行出错");
</script>
  </c:when>
  <c:when test="${result=='success'}">
  <script type="text/javascript">
$.messager.alert("提示信息","执行成功");
</script>
  </c:when>
</c:choose>

</html>
