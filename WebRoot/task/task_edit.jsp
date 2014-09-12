
<%@ page language="java" contentType="text/html; charset=GBK" %>

<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template" prefix="template" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ page import="com.dhcc.webnms.struts.task.form.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
  <head>
    <html:base />
    
    <title><bean:message key="webnms.title"/></title>
    
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <link href="../include/mainstyle.css" rel="stylesheet" type="text/css">
    <script language="JavaScript" src="../include/validation.js"></script>
<script language="JavaScript" fptype="dynamicoutline">
	function forward() {
	document.forms[0].operate.value="list";
	location.href="TaskMgr.do";
	}
	function valid(operate)
{			
	validate();
	if(returnVal) {
	  if(operate=='edit')alert("此次改动将在系统重启后生效！  ");
		TaskForm.operate.value=operate;
		return	true;
	}else{
	return	false;
	}
}	

function init() {
    if(TaskForm.flag.value=='0')alert("文件不存在");
		define('taskname','string','<bean:message key="task.taskname"/>', 2, 50);
		define('polltime','num','<bean:message key="task.polltime"/>', 1, 3);
	}
</script>
  </head>
  
  <body onload="init();"  class="WorkWin_Body">
    <html:form action="/TaskMgr" >
     <html:hidden property="operate"/>

       <html:hidden property="flag"/>
       <table align="center" cellSpacing="0" cellPadding="0" id="tblListTitle" class="WorkPage_ListTable" width="100%" border="0">
         <tr align="center" valign="middle"  bgcolor="#FFFFFF"> 
 	     <td valign="middle"  class="WorkPage_ListTitle_Left"  align=left> 
            <table  height="25" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                
                <td width="20%" valign="bottom">&nbsp; </td>
                <td width="80%">&nbsp;
        <logic:equal name="TaskForm" property="operate" value="add">
        <bean:message key="task.add.title"/>
        </logic:equal>
      <logic:equal name="TaskForm" property="operate" value="edit">
        <bean:message key="task.edit.title"/>
        </logic:equal></td>
                </tr>
            </table>
			<td ></td>
			<td ></td>
			<td align=right>
			 <logic:equal name="TaskForm" property="operate" value="add">
        <html:submit  styleClass="button" onclick="return valid('add')">
        <bean:message key="button.save"/>
      </html:submit>
      </logic:equal>
      <logic:equal name="TaskForm" property="operate" value="edit">
        <html:submit  styleClass="button" onclick="return valid('edit')">
        <bean:message key="button.edit"/>
      </html:submit>
      </logic:equal>
      <logic:equal name="TaskForm" property="operate" value="add">
       <html:submit  styleClass="button" onclick="return valid('saveadd')">
        <bean:message key="button.saveadd"/>
      </html:submit>
      </logic:equal>
        <input type=reset value="<bean:message key="button.reset"/>" class="button">
      <input type="button" value="<bean:message key="button.back"/>" name="btn" class=button onClick="forward()">
        
			</td>
  </tr>
    <tr>
  <td colspan=4>&nbsp;</td></tr>
  <tr>
  <td colspan=4>
		   <table class="tab3"  cellSpacing="2"  cellPadding="5" border=0>
		    <tr >
		    	<td class=firsttd>
		      <bean:message key="task.taskname"/>
		      </td>
		<td align="left" bgcolor=white>
		  <logic:equal name="TaskForm" property="operate" value="add">
        <html:text property="taskname" size="30" maxlength="30"/>
      </logic:equal>
      
		   <logic:equal name="TaskForm" property="operate" value="edit">
       <html:text property="taskname" size="30" maxlength="30" readonly="true"/>
      </logic:equal>    
		   
		  </td>
		    </tr>
		    
		     <tr >
		    	<td class=firsttd>
		      <bean:message key="task.startsign"/>
		      </td>
		    <td align="left" bgcolor=white>
				<html:select property="startsign">
				<html:option value="1">启动</html:option>
				<html:option value="0">未启动</html:option>																																			
				</html:select>
		    </td>
		    </tr>
		    
		    <tr >
		    	<td class=firsttd>
		      <bean:message key="task.polltime"/>
		      </td>
		    <td align="left" bgcolor=white>
		     <html:text property="polltime" size="30" maxlength="30"/>
		    </td>
		    </tr>	

		    <tr >
		    	<td class=firsttd>
		      <bean:message key="task.polltimeunit"/>
		      </td>
		    <td align="left" bgcolor=white>
      	<select name="polltimeunit" >
       	<logic:equal name="TaskForm" property="polltimeunit" value="m">
       	<option value="m" selected>分钟</option>
       	</logic:equal>
       	<logic:equal name="TaskForm" property="polltimeunit" value="h">
       	<option value="h" selected>小时</option>
       	</logic:equal>
       	<logic:equal name="TaskForm" property="polltimeunit" value="d">
       	<option value="d" selected>天</option>
       	</logic:equal>
       	<logic:equal name="TaskForm" property="polltimeunit" value="s">
       	<option value="s" selected>秒</option>
       	</logic:equal>
        <logic:notEqual name="TaskForm" property="polltimeunit" value="m">
       	<option value="m" >分钟</option>
       	</logic:notEqual>
       	<logic:notEqual name="TaskForm" property="polltimeunit" value="h">
       	<option value="h" >小时</option>
       	</logic:notEqual>
       	<logic:notEqual name="TaskForm" property="polltimeunit" value="d">
       	<option value="d" >天</option>
       	</logic:notEqual>
       	<logic:notEqual name="TaskForm" property="polltimeunit" value="s">
       	<option value="s" >秒</option>
       	</logic:notEqual>																															
 				</select>
 				</td>
 		    </tr>	
     
 		    </table>
     </td>
   </tr>
 
       </table>
     </html:form>
   </body>
 </html:html>