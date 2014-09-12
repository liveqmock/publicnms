<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.application.model.MonitorDBDTO"%>


<%@ include file="/include/globe.inc"%>
<%
String path = request.getContextPath();
String rootPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
 //DBManagerShow dbm = new DBManagerShow();
 //List list = (List) dbm.getDBMessage();
 List list = (List)request.getAttribute("listview");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
 
    <title>My JSP 'snapshot.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<style type="text/css">
	body {
       margin:0;
       padding:0;
      }
	</style>
   <script type="text/javascript">
     
	function doInit()
	{
		setInterval(autoRefresh,1000*100*1);
	}
	function autoRefresh()
	{
		window.location.href="<%=rootPath%>/db.do?action=listview";
	}
	function openPreWin(id){
	window.parent.location.href="<%=rootPath%>/detail/dispatcher.jsp?flag=1&id=dbs"+id+"&fromtopo=true";
	}
   </script>
  </head>
  
  <body onload="doInit()">
       
				<table align="center">
		        						<tr>
       													<td align="center" class="body-data-title" width="10%">状态</td>
       													<td align="center" class="body-data-title" width="30%">名称</td> 
       													<td align="center" class="body-data-title" width="10%">类型</td>
       													<td align="center" class="body-data-title" width="20%">IP地址</td>
       													<td align="center" class="body-data-title" width="20%">可用性</td>
		        									</tr>
		        									<%
		        										if (list != null && list.size() > 0) {

		        											for (int i = 0; i < list.size(); i++) {
		        												MonitorDBDTO monitorDBDTO = (MonitorDBDTO) list.get(i);
		        												Hashtable eventListSummary = monitorDBDTO
		        														.getEventListSummary();
		        												String generalAlarm = (String) eventListSummary
		        														.get("generalAlarm");
		        												String urgentAlarm = (String) eventListSummary
		        														.get("urgentAlarm");
		        												String seriousAlarm = (String) eventListSummary
		        														.get("seriousAlarm");

		        												String status = monitorDBDTO.getStatus();

		        												//System.out.println(status+"====");      	
		        												String statusImg = "";
		        												if ("1".equals(status)) {
		        													statusImg = "alarm_level_1.gif";
		        												} else if ("2".equals(status)) {
		        													statusImg = "alarm_level_2.gif";
		        												} else if ("3".equals(status)) {
		        													statusImg = "alert.gif";
		        												} else {
		        													statusImg = "status_ok.gif";
		        												}
		        									%>
		        									            <tr <%=onmouseoverstyle%>>
		        									            	<td align="center" class="body-data-list"><img src="<%=rootPath%>/resource/image/topo/<%=statusImg%>"></td>
			       													<td align="center" class="body-data-list"><a href="#" onclick="openPreWin('<%=monitorDBDTO.getId()%>')"><%=monitorDBDTO.getAlias()%></a></td>
			       													<td align="center" class="body-data-list"><%=monitorDBDTO.getDbtype()%></td>
			       													<td align="center" class="body-data-list"><%=monitorDBDTO.getIpAddress()%></td>
												                    <td align="center" class="body-data-list"><%=monitorDBDTO.getPingValue()%></td>
					        									</tr>
					        									            <%
					        									            	}
					        									            	}
					        									            %>
		        											</table>
		
  </body>
</html>
