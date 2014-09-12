
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.common.util.*" %>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@ page import="com.afunms.polling.api.I_Portconfig"%>
<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.event.model.NetSyslog"%>

<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%
String rootPath = request.getContextPath();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html locale="true">
  <head>
    
    <title>Syslog详细信息</title>
    
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
 
 <script language="JavaScript" fptype="dynamicoutline">
		 
</script>

<%
	NetSyslog syslog = (NetSyslog)request.getAttribute("syslog");
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
	java.text.SimpleDateFormat sdftime = new java.text.SimpleDateFormat("HH:mm:ss");
	Date cc = syslog.getRecordtime().getTime();
	String date1 = sdf.format(cc);
	String time1 = sdftime.format(cc);
%> 
  </head>
  
   <script language="JavaScript" src="<%=rootPath%>/include/print.js"></script>
   <OBJECT classid=CLSID:8856F961-340A-11D0-A96B-00C04FD705A2 height=0 id=WB width=0>
</OBJECT>
  <body class="WorkWin_Body">
  
 <table align="center" cellSpacing="0" cellPadding="0" id="tblListTitle" class="WorkPage_ListTable" width="60%" border="0">
  <form action=""> 
  <tr align="center" valign="middle" class="noprint"> 
 	     <td valign="middle"  class="WorkPage_ListTitle_Left" align=left> 

            </td>
  </tr>
  <tr><td colspan=10>&nbsp;</td></tr>
  <tr><td colspan=10>
  <table class="tab3"  cellSpacing="0"  cellPadding="3" border=0>
  <tr align="right" > 
  <td colspan=10 align=left height=18>
  <table width="100%" cellSpacing="0"  cellPadding="0" border=0  class="noprint">
  <tr bgcolor=#ECECEC>
  <td align="left" height=18 class="layout_title">
<b>日志详细信息</b>
    </td>   	
  <td align="right" height=18>
<INPUT type="button" value="关闭窗口" id=button1 name=button1 onclick="window.close()">            
    </td> 
    </tr>
     </table>
    </td>
  </tr>
  
  <tr bgcolor=#cedefa height=25>
    <td width=20% bgcolor="#F1F1F1">日期(A)</td>
    <td width=30% bgcolor="#F1F1F1"><%=date1%></td>
    <td width=20% bgcolor="#F1F1F1">时间(M)</td>
    <td width=30% bgcolor="#F1F1F1"><%=time1%></td>
   </tr>
	 <tr >
    <td>类型(M)</td>
    <td><%=syslog.getPriorityName()%></td>
  </tr>
  
	 <tr bgcolor="#F1F1F1">
    <td width=20% >IP地址</td>
    <td width=30% ><%=syslog.getIpaddress()%></td>	 
    <td>计算机(O)</td>
    <td align=left><%=syslog.getHostname()%></td>
  </tr>
	 <tr height=25 >
    <td colspan=4>描述(D):</td>
  </tr>

	 <tr >
    <td colspan=4><textarea  rows="10" cols="50"><%=syslog.getMessage()%>
    	</textarea></td>
  </tr>
    
</table>
</td></tr></table>
</form> 
  </body>
</html>
