<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.model.MonitorNodeDTO"%>
<%@page import="com.afunms.common.util.HomeHelper"%>
<%@ include file="/include/globe.inc"%>


<%
String path = request.getContextPath();
String rootPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
HomeHelper helper=new HomeHelper();
 List monitorDBDTOList = (List)helper.getNetview();
  
	String cpuUsed = "0";
	String cpuUnUsed = "";
	if(cpuUsed.equals("0"))cpuUnUsed="100";
	String memeryUsed = "0";
	String memeryUnUsed = "";
	if(memeryUsed.equals("0"))memeryUnUsed="100";
	String pingValue = "0";
	
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
	<style type="text/css">
	body {
       margin:0;
       padding:0;
      }
	</style>
     <script type="text/javascript">
     
			function doInit()
	  {
		setInterval(autoRefresh,1000*30*1);
	  }
	
	function autoRefresh()
	{
	
	window.location.reload();
	}
	function openPreWin(id){
	window.parent.location.href="<%=rootPath%>/detail/dispatcher.jsp?flag=1&id=net"+id+"&fromtopo=true";
	}
   </script>
  </head>
  
  <body onload="doInit()">
       
				<table>
		        												<tr>
		        													<td align="center" class="body-data-title" width="10%">状态</td>
		        													<td align="center" class="body-data-title" width="30%">名称</td>
		        													<td align="center" class="body-data-title" width="15%">IP地址</td>
		        													<td align="center" class="body-data-title" width="15%">可用性</td>
		        													<td align="center" class="body-data-title" width="15%">CPU</td>
		        													<td align="center" class="body-data-title" width="15%">内存</td>
		        												</tr>
		        												<%
					        									    if(monitorDBDTOList!=null&& monitorDBDTOList.size()>0){
					        									        for(int i = 0 ; i < monitorDBDTOList.size() ; i++){
					        									        	MonitorNodeDTO monitorNodeDTO = (MonitorNodeDTO)monitorDBDTOList.get(i);
					        									        	pingValue = monitorNodeDTO.getPingValue();
					        									        	cpuUsed = monitorNodeDTO.getCpuValue();
					        									        	cpuUnUsed = String.valueOf(100 - Double.valueOf(cpuUsed));
					        									        	memeryUsed = monitorNodeDTO.getMemoryValue();
					        									        	memeryUnUsed = String.valueOf(100 - Double.valueOf(memeryUsed));
					        									        	String cpuValueColor = monitorNodeDTO.getCpuValueColor();
					        									        	String memeryValueColor = monitorNodeDTO.getMemoryValueColor();
					        									        	String status = monitorNodeDTO.getStatus();
					        									        	String statusImg = "";
					        									        	if("1".equals(status)){
					        									        		statusImg = "alarm_level_1.gif";
					        									        	}else if("2".equals(status)){
					        									        		statusImg = "alarm_level_2.gif";
					        									        	}else if("3".equals(status)){
					        									        		statusImg = "alert.gif";
					        									        	}else{
					        									        		statusImg = "status_ok.gif";
					        									        	}
					        									        	//Hashtable eventListSummary = monitorNodeDTO.getEventListSummary();
					        									        	//String generalAlarm = (String)eventListSummary.get("generalAlarm");
					        									        	//String urgentAlarm = (String)eventListSummary.get("urgentAlarm");
					        									        	//String seriousAlarm = (String)eventListSummary.get("seriousAlarm");
					        									        	//System.out.println("==============1441");
					        									            %>
					        									            <tr <%=onmouseoverstyle%>>
						        									            <td align="center" class="body-data-list"><img src="<%=rootPath%>/resource/image/topo/<%=statusImg%>"></td>
					        													<td align="left" class="body-data-list">
					        														&nbsp;
					        														<a href="#" onclick="openPreWin('<%=monitorNodeDTO.getId()%>')">
					        															<span id='detailType_net<%=monitorNodeDTO.getId()%>' onmouseover="showDetailInfo('net<%=monitorNodeDTO.getId()%>')" onmouseout="unShowDetailInfo();">
					        																<%=monitorNodeDTO.getAlias()%>
					        															</span>
					        														</a>
					        													</td>
					        													<td align="left" class="body-data-list">
					        														<table>
													                      				<tr>
													                      					<td><%=monitorNodeDTO.getIpAddress() %></td>
									       													<td>&nbsp;</td>
                    																	</tr>
												                      				</table> 
                    															</td>
					        													<td align="center" class="body-data-list">
					        														<table>
													                      				<tr>
													                      					<td align="center" width="50%"><%=pingValue%>%</td>
													                      				</tr>
												                      				</table>
					        													</td>
					        													<td align="center" class="body-data-list">
					        														<table>
													                      				<tr>
													                      					<td><%=cpuUsed%>%</td>
													                      					<td width=150>
													                      						<table border=1 height=15 width="100%" bgcolor=#ffffff>
																                      				<tr>
																                      					<td width="<%=cpuUsed%>%" bgcolor="<%=cpuValueColor%>"></td>
																                      					<td width="<%=cpuUnUsed%>%" bgcolor=#ffffff></td>
																                      				</tr>
															                      				</table>
													                      					</td>
													                      				</tr>
												                      				</table>
					        													</td>
					        													<td align="center" class="body-data-list" >
					        														<table>
													                      				<tr>
													                      					<td><%=memeryUsed%>%</td>
													                      					<td width=150>
													                      						<table border=1 height=15 width="100%" bgcolor=#ffffff>
																                      				<tr>
																                      					<td width="<%=memeryUsed%>%" bgcolor="<%=memeryValueColor%>"></td>
																                      					<td width="<%=memeryUnUsed%>%" bgcolor=#ffffff></td>
																                      				</tr>
															                      				</table>
													                      					</td>
													                      				</tr>
												                      				</table>
					        													</td>
					        													<%-- <td align="center" class="body-data-list">
					        														<table>
													                      				<tr>
													                      					<td align="center">
													                      						<table border=1 height=15 bgcolor=#ffffff>
																                      				<tr>
																                      					<td width="30%" bgcolor="#ffff00" align="center"><%=generalAlarm%></td>
																                      					<td width="30%" bgcolor="orange" align="center"><%=urgentAlarm%></td>
																                      					<td width="30%" bgcolor="#ff0000" align="center"><%=seriousAlarm%></td>
																                      				</tr>
															                      				</table>
													                      					</td>
													                      				</tr>
												                      				</table>
					        													</td>--%>
				        													</tr>
					        									            <% 
					        									            	}
					        									            }
					        									 %>
		        											</table>
		
  </body>
</html>
