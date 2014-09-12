
<%@ page language="java" contentType="text/html; charset=GBK" %>

<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template" prefix="template" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ include file="/include/globe.inc"%>
<%@ page import="com.dhcc.webnms.om.task.Task"%>
<%@ page import="com.dhcc.webnms.struts.common.StrutsConstants"%>


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
 <script language="JavaScript" src="../include/list_s.js"></script>
  </head>
  
   <script language="JavaScript" src="../include/print.js"></script>
   <OBJECT classid=CLSID:8856F961-340A-11D0-A96B-00C04FD705A2 height=0 id=WB width=0>
</OBJECT>
  <body class="WorkWin_Body">
  
 <table align="center" cellSpacing="0" cellPadding="0" id="tblListTitle" class="WorkPage_ListTable" width="100%" border="0">
  <html:form action="/TaskMgr"> 
  <html:hidden property="taskname"/>
  <html:hidden property="operate"/>
  <html:hidden property="currentPageNumber" />
  <html:hidden property="jumpState"/>
  <tr align="center" valign="middle" class="noprint"> 
 	     <td valign="middle"  class="WorkPage_ListTitle_Left" align=left> 
            <table  height="25" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                
                <td width="20%" align=left>&nbsp;</td>
                <td width="80%">&nbsp;<bean:message key="task.list.title"/></td>
                </tr>
            </table>
			<td ></td>
			<td ></td>
			<td align=right>
				<bean:message key="prompt.search.bykeyword"/>
				<html:select property="searchfield">
				<html:option value="taskname"><bean:message key="task.taskname"/></html:option>
				</html:select>
				<html:text name="TaskForm" property="searchkeyword" size="20" maxlength="32"/>

				<html:submit styleClass="button" onclick="submitCheck(operate,'search')">
        <bean:message key="button.search" />
      </html:submit>
			</td>
  </tr>
  <tr><td colspan=5>&nbsp;</td></tr>
  <tr><td colspan=5>
  <table class="tab3"  cellSpacing="0"  cellPadding="3" border=0>
  <tr align="right" > 
  <td colspan=5 align=left height=18>
  <table width="100%" cellSpacing="0"  cellPadding="0" border=0  class="noprint">
  <tr >
  <td align="right" height=18>
      <html:submit property="addbutton" onclick="submitCheck(operate,'ready_add')"  styleClass="button" >
        <bean:message key="button.add"/>
      </html:submit>
      <html:submit property="editbutton"  onclick="submitCheck(operate,'ready_edit')" styleClass="button" disabled="true">
        <bean:message key="button.edit"/>
      </html:submit>
      
       <input type="submit" value="<bean:message key='button.delete'/>" name="delbutton" class=button onclick="return submitCheck(operate,'delete')" disabled=true>

<INPUT type="button" value="<bean:message key="button.print"/>" id=button1 name=button1 onclick="print1()" class="button"> 
           
    </td> </tr> </table>
    </td>
  </tr>
  
  <tr  class="firsttr">
    <td width="13%"><input type=checkbox name=checkall onclick="submitCheck(operate,'checkall')"  class="noborder"> <strong><bean:message key="common.plsselect"/></strong><br> </td>
    <td width="12%">&nbsp;</td>
    <td width="18%"><strong><bean:message key="task.taskname"/></strong></td>
    <td width="18%"><strong><bean:message key="task.startsign"/></strong></td>
    <td width="18%"><strong><bean:message key="task.polltime"/></strong></td>
    </tr>
  <%
  	int index = 0;
  	String modifystr="";
  	String startstr="";
  	String polltimeunitstr="";
  %>
  <logic:iterate id="task" name="list" offset='<%=StrutsConstants.LIST_CURRENTPOSITION%>'  length="<%=StrutsConstants.LIST_PAGESIZE%>" scope="request"> 
  <%
  	index ++;	
  	if(((Task)task).getModify().equals("1")) 	modifystr="已修改";
   	else modifystr="未修改";
  	if(((Task)task).getStartsign().equals("1"))startstr="已启动";
  	else startstr="未启动";
  	if(((Task)task).getPolltimeunit().equals("m"))polltimeunitstr="分钟";
  	else if(((Task)task).getPolltimeunit().equals("h"))polltimeunitstr="小时";
  	else if(((Task)task).getPolltimeunit().equals("d"))polltimeunitstr="天";
  	else if(((Task)task).getPolltimeunit().equals("s"))polltimeunitstr="秒";
  %>
  <tr  class="othertr" <%=onmouseoverstyle%> ondblclick="submitCheck('<bean:write name="task" property="taskname"/>','dbclicksubmit')">
    <td height="20"> 
     <input type=checkbox name=stringMultibox value='<bean:write name="task" property="taskname"/>' class=noborder onclick="submitCheck('<bean:write name="task" property="taskname"/>','checkthisbox')">
    </td>
    <td onclick="submitCheck('<bean:write name="task" property="taskname"/>','checkthis')">&nbsp;<%=index%></td>
    <td onclick="submitCheck('<bean:write name="task" property="taskname"/>','checkthis')">
    <html:link action="/TaskMgr?operate=ready_edit" paramId="taskname" paramName="task" paramProperty="taskname" > 
      <bean:write name="task" property="taskname"/></html:link></td>
    <td onclick="submitCheck('<bean:write name="task" property="taskname"/>','checkthis')"><%=startstr%></td>
    <td onclick="submitCheck('<bean:write name="task" property="taskname"/>','checkthis')"><bean:write name="task" property="polltime"/><%=polltimeunitstr%></td>  
   </tr>
  </logic:iterate> 
 <tr align="right"> 
  <td colspan=5 align=left>
  <table width="100%" ><td align="left">
   共<bean:write name="TaskForm" property="total"/>条数据&nbsp;&nbsp;第<bean:write name="TaskForm" property="currentPageNumber"/>页&nbsp;&nbsp;共<bean:write name="TaskForm" property="totalPage"/>页
  
	</td>
	<td align="center">
  &nbsp;
	</td>
	<td align="right">
   转到第<input type=text name=searchPage size=4> 页
   <html:submit onclick="selectPage('5')" styleClass="button">
        <bean:message key="button.search"/>
      </html:submit>
	</td>
	<td align="right">
      <input type="button" class=button name="<bean:message key="button.firstpage"/>" value ="<bean:message key="button.firstpage"/>" onclick="selectPage(1)">
	<logic:equal name="TaskForm" property="hasPrevious" value="true">
	<input type="button" class=button name="<bean:message key="button.previous"/>" value ="<bean:message key="button.previous"/>" onclick="selectPage(2)">
	</logic:equal>
	<logic:equal name="TaskForm" property="hasPrevious" value="false">
	<input type="button" class=button name="<bean:message key="button.previous"/>" value ="<bean:message key="button.previous"/>" Disabled ="false">
	</logic:equal>
	<logic:equal name="TaskForm" property="hasNext" value="true">
	<input type="button" class=button name="<bean:message key="button.next"/>" value ="<bean:message key="button.next"/>" onclick="selectPage(3)">
	</logic:equal>
	<logic:equal name="TaskForm" property="hasNext" value="false">
	<input type="button" class=button name="<bean:message key="button.next"/>" value ="<bean:message key="button.next"/>" onclick="selectPage(3)"  Disabled ="false">
	</logic:equal>
	<input type="button" class=button name="<bean:message key="button.endpage"/>" value ="<bean:message key="button.endpage"/>" onclick="selectPage(4)">
    </td> </tr> </table>
    </td>
  </tr>
</table>
</td></tr></table>
</html:form> 
  </body>
</html:html>
