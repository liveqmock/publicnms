<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.common.util.SysUtil" %>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.HostLastCollectDataManager"%>
<%@page import="com.afunms.polling.api.I_HostLastCollectData"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>

<%@page import="java.util.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.config.model.Portconfig"%>
<%@ page import="com.afunms.event.model.*"%>
<%@ page import="com.afunms.event.dao.*"%>
<%@ page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.*" %>
<% 

	String rootPath = request.getContextPath(); 
	EventList eventdetail = (EventList)request.getAttribute("eventList");
	String eventids = request.getParameter("eventids");
	String[] ids = eventids.split(",");
	//String eventid = request.getParameter("eventid");
	String eventid = ids[0];
  	//System.out.println("---------ids------>"+eventid);
  	if(eventdetail==null){
  		//eventid = request.getParameter("eventid");
  			//if(eventid == null){
			//	eventid = (String)request.getAttribute("eventid");
			//	if(eventid == null){
			//		eventid = (String)session.getAttribute("idforknowledge");
			//	}
			//}
  		EventListDao dao = new EventListDao();
  		try{
  			eventdetail = (EventList)dao.findByID(eventid);
  		}catch(Exception e){
  		}finally{
  			dao.close();
  		}
  		//eventdetail= new EventList();
  	}
	
	//System.out.println("eventid========================="+eventid);

	//EventReport eventreport = (EventReport)request.getAttribute("eventreport");
	java.text.SimpleDateFormat sdf0 = new java.text.SimpleDateFormat("yyyy-MM-dd"); 
 	String nowtime = sdf0.format(new Date());
  	User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);
	String username = vo.getName();	
	  

  	
  	
%>
<%
	session.setAttribute("idforknowledge", eventid);
%>
<html>
<head>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script language="javascript">  
  		function batchAccfiEvent(){
        	mainForm.action = "<%=rootPath%>/event.do?action=batchAccfi&eventids=<%=eventids%>";
        	mainForm.submit();
        	
  		}
  		
   		function doReport(){
			mainForm.action="<%=rootPath%>/event.do?action=doreport";
        	mainForm.submit();
   	}  

  function myKnowledge(){
  //alert("进入方法");
  	mainForm.action = "<%=rootPath%>/knowledge.do?action=hostfind";
    mainForm.submit();
  }
  
  function myKnowledgebase(){
  //alert("进入方法");
  	mainForm.action = "<%=rootPath%>/knowledgebase.do?action=hostfind";
    mainForm.submit();
  }
  
  function myEvent(){
  //alert("返回event");
	  mainForm.action = "<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>";
	  mainForm.submit();
  }
  
</script>
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<title>批量接受处理</title>

<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
		<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet"
			type="text/css">
		<title>批量接受事件</title>

		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css"
			type="text/css">
		<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet"
			type="text/css">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
	<script type="text/javascript">
function setClass(){
	document.getElementById('knowledgeDetailTitle-0').className='detail-data-title';
	document.getElementById('knowledgeDetailTitle-0').onmouseover="this.className='detail-data-title'";
	document.getElementById('knowledgeDetailTitle-0').onmouseout="this.className='detail-data-title'";
}
</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#ececec" onload="setClass()">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm">
<input type="hidden" id="eventid"  name="eventid" value="<%=eventdetail.getId() %>">
<input type=hidden name="id" value="<%=eventdetail.getId()%>">
<br><center>
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=98%>
								<tr>
									<td class="detail-data-header">
										<%=knowledgeDetailTitleTable%>
									</td>
								</tr>
	<tr>
		<td bgcolor="#FFFFFF" align="center">
 			<table width=95% class="tab3"  cellSpacing="0"  cellPadding="0">
			<%
			  	java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
			  	Date cc = eventdetail.getRecordtime().getTime();
			  	//Integer eventid = eventdetail.getId();
			  	String eventlocation = eventdetail.getEventlocation();
			  	String content = eventdetail.getContent();
			  	String level = String.valueOf(eventdetail.getLevel1());
			  	String status = String.valueOf(eventdetail.getManagesign());
			  	String s = status;
			  	String act="处理报告";
			  	if("1".equals(level)){
			  		level="普通事件";
			  	}
			  	if("2".equals(level)){
			  		level="紧急事件";
			  	}
			   	  	if("0".equals(status)){
			  		status = "未处理";
			  	}
			  	if("1".equals(status)){
			  		status = "处理中";  	
			  	}
			  	if("2".equals(status)){
			  	  	status = "处理完成";
			  	}
			  	String rptman = eventdetail.getReportman();
			  	String rtime1 = _sdf.format(cc);
			%>
          		<tr algin="left" valign="center">                      														
            		<td height="28" colspan=2 align="left" bordercolor="#ececec" bgcolor="#ececec">&nbsp;接受事件</td>
          		</tr>
  				<tr>
  					<td colspan=2><br>&nbsp;&nbsp;该事件尚未被任何管理员受理，点击确定表明您已经了解该情况并且准备受理<br><br></td>
			  	</tr>	
			  	<tr>
			        <td width="20%" align=right>事件等级</td>
			        <td width="80%">&nbsp;&nbsp;<%=level%></td>
			  	</tr>
			  	<tr>     
			    	<td width="20%" align=right>事件描述</td>
			    	<td width="80%">&nbsp;&nbsp;<textarea name="content" cols=50 rows=5 readonly><%=content%></textarea></td>
			  	</tr>
			  	<tr>
					<td align=right>登记日期</td>
					<td>&nbsp;&nbsp;<%=rtime1%></td>
			  	</tr>
			  	<tr>
			    	<td align=right>登记人</td>
			    	<td>&nbsp;&nbsp;<%=rptman%></td>
			   </tr>


 				<tr>
			    	<td colspan=2 align=center>
			       		<input type ="button" value="批量接受处理" class="formStylebutton" onclick="batchAccfiEvent()">
			       			&nbsp;&nbsp;<input type=button class="formStylebutton" value="关闭窗口" onclick="javascript:window.close();">
			       		<br><br>
			       </td>
			 	</tr> 
			</table>		
		</td>
	</tr>
</table>                	              
</form>
</BODY>
</HTML>
 