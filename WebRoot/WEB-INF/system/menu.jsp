<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String rootPath = request.getContextPath();
%>
<link href="<%=rootPath%>/css/main.css" rel="stylesheet" type="text/css"/>
<link href="<%=rootPath %>/js/jquery/themes/default/easyui.css" rel="stylesheet" type="text/css"/>
<link href="<%=rootPath %>/js/jquery/themes/icon.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/global/systemutil.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/global/check.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/system/menu.js"></script>

<html>
<head>
</head>
<body>
<div class="iframe_content">
   <div>
   <input type="button" name="add"  value="添加一级菜单" onclick="addOneLevel()"/> 
   <input type="button" name="del"  value="修改一级菜单" onclick="modifyoneform()" />
    <input type="button" name="del"  value="删除一级菜单" onclick="delform()" />
   </div>
   <form id="mainform" >
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_box">
  		<tr>
    	<th ><input name="checkall"  type="checkbox" value=""/></th>
  		<th>菜单名</th>
		<th>序号</th>
		<th style="display: none">地址</th>
		<th>操作</th>
 	 </tr>
 	<c:forEach items="${list}" var="menu">
 	<tr>
 	<td><input type="checkbox" name="checkbox" value="${menu.menu_id}"/></td>
 	<td>${menu.menu_name}</td>
 	<td>${menu.menu_seq}</td>
 	<td style="display: none">${menu.menu_url}</td>
 	<td><img src="<%=rootPath%>/images/icons/page_white_edit.png" onclick="editmenudialog(${menu.menu_id})" alt="操作" border="0" class="td_icons" /></td>
 	</tr>
 	</c:forEach>
  </table>
  </form>
</div>
<div id="dd" title="添加一级菜单" >
   <form action="addOneLevelMenu.action" id="dataform" onsubmit="return checkdata();" >
		<table id="data" style="display: none" >
		<tr>
		<td ><font class="red">*</font> 菜单名：</td>
		<td><input type="text" name="menu_name" value=""/></td>
		<tr>
		<td>响应地址：</td>
		<td><input type="text" name="menu_url" value=""/></td>
		</tr>
		</table>
		 </form>
	</div>
	
	<div id="one" title="修改一级菜单"  style="width: 400px;height: 200px">
   <form action="modifyOneLevelMenu.action" id="onedataform" onsubmit="return checkonedata();">
		<table id="onedatatable" style="display: none" >
		<tr>
		<td ><font class="red">*</font> 菜单名：</td>
		<td><input type="text" name="one_menu_name" value=""/></td>
		<tr>
		<td>响应地址：</td>
		<td><input type="text" name="one_menu_url" value=""/></td>
		</tr>
		</table>
		<input type="hidden" name="one_menu_id" value=""/>
		 </form>
	</div>
	
	<div id="sec" title="编辑子菜单"  style="width: 600px;height: 300px">
		
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
