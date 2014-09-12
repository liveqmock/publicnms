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
<script type="text/javascript" src="<%=rootPath%>/js/system/user.js"></script>
<html>
<head>
</head>
<body>
  
<div class="iframe_content">
<div>
   <input type="button" name="add"  value="添加" onclick="add()"/> 
   <input type="button" name="del"  value="修改" onclick="modify()" />
    <input type="button" name="del"  value="删除" onclick="delform()" />
   </div>
   <form id="mainform">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_box">
  		<tr>
    	<th><input name="checkall" type="checkbox" value=""/></th>
  		<th>账号</th>
  		<th>用户名</th>
  		<th>邮箱地址</th>
 	 </tr>
 	<c:forEach items="${list}" var="user">
 	<tr>
 	<td><input type="checkbox" name="checkbox" value="${user.id}"/> </td>
 	<td>${user.id} </td>
 	<td>${user.firstName}-${user.lastName}</td>
 	<td>${user.email} </td>
 	</tr>
 	</c:forEach>
  </table>
  </form>
</div>

<div id="adddiv" title="添加用户" style="width: 400px;height: 300px" >
   <form action="userModify.action" id="addform" onsubmit="return checkadddata();">
		<table id="addtable" style="display: none" >
		<tr>
		<td ><font class="red">*</font> 账号：</td>
		<td><input type="text" name="id" id="addid" value="" onblur="checkuserid(this.value)"/><span class="red" id="mess"></span></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 姓：</td>
		<td><input type="text" name="firstName" id="addfirstName" value=""/></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 名：</td>
		<td><input type="text" name="lastName" id="addlastName" value=""/></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 密码：</td>
		<td><input type="password" name="password" id="addpassword1" value=""/></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 确认密码：</td>
		<td><input type="password" id="addpassword2" value=""/></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 邮箱地址：</td>
		<td><input type="text" name="email" id="addemail" value=""/></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 所属用户组：</td>
		<td>
		<table>
		
		<tr>
		<td>
		<c:forEach items="${group}" var="g" varStatus="status" >
		     <input type="checkbox" value="${g.id}" name="addgroups"/>&nbsp;${g.name }
		     <c:if test="${(status.index+1)%3==0 }">
		     <br/>
		     </c:if>
		 </c:forEach>
		</td>
		</tr>
		</table>
		<input name="flag" type="hidden" value="add"/>
		</td>
		</tr>
		</table>
		 </form>
	</div>
	
	
	<div id="modifydiv" title="修改用户" style="width: 400px;height: 300px">
   <form action="userModify.action" id="modifyform" onsubmit="return checkmodifydata();">
		<table id="modifytable" style="display: none" >
		<tr style="display: none">
		<td ><font class="red">*</font> 账号：</td>
		<td><input type="text" name="id" id="modifyid" value=""/></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 姓：</td>
		<td><input type="text" name="firstName" id="modifyfirstName" value=""/></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 名：</td>
		<td><input type="text" name="lastName" id="modifylastName" value=""/></td>
		</tr>
		<tr>
		<td >密码：</td>
		<td><input type="password" name="password" id="modifypassword1" value=""/></td>
		</tr>
		<tr>
		<td > 确认密码：</td>
		<td><input type="password" id="modifypassword2" value=""/></td>
		</tr>
		<tr>
		<tr>
		<td ><font class="red">*</font> 邮箱地址：</td>
		<td><input type="text" name="email" id="modifyemail" value=""/></td>
		</tr>
		<tr>
		<td ><font class="red">*</font> 所属用户组：</td>
		<td>
		
		<table>
		<tr>
		<td>
		<c:forEach items="${group}" var="g" varStatus="status" >
		     <input type="checkbox" value="${g.id}" name="modifygroups"/>&nbsp;${g.name }
		     <c:if test="${(status.index+1)%3==0 }">
		     <br/>
		     </c:if>
		 </c:forEach>
		</td>
		</tr>
		</table>
		</td>
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
