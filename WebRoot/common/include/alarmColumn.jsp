<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>

<%@page import="com.afunms.event.service.AlarmSummarize"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
String rootPath = request.getContextPath();
	
	    AlarmSummarize alarmsum = new AlarmSummarize();
	    HashMap datamap = new HashMap();
 	    datamap = alarmsum.getDataColumn("");
  
        String columndata = (String) datamap.get("columndata");
   
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/xml/flush/amcolumn/swfobject.js"></script>
    <title>My JSP 'snapshot.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
   <script type="text/javascript">
     
			function doInit()
	  {
		setInterval(autoRefresh,1000*30*1);
	  }
	
	function autoRefresh()
	{
	
	window.location.reload();
	}
   </script>
  </head>
  
  <body onload="doInit()">
       
				  <div id="column">
						<strong>You need to upgrade your Flash
							Player</strong>
					</div>
					<script type="text/javascript">
						// <![CDATA[		
						var so = new SWFObject("<%=rootPath%>/amchart/amcolumn.swf", "amcolumn","360", "320", "8", "#FFFFF");
						so.addVariable("path", "<%=rootPath%>/amchart/");
						so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/lastweekColumn.xml"));
						so.addVariable("chart_data", "<%=columndata%>");
						so.write("column");
						// ]]>
					</script>
		
  </body>
</html>
