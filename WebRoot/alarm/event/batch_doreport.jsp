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
  	System.out.println("---------ids------>"+eventid);
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
			
	function batchDoReport(){
		mainForm.action="<%=rootPath%>/event.do?action=batchDoReport&eventids=<%=eventids%>";
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
<title>批量填写报告</title>

<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
		<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet"
			type="text/css">

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
           			<td height="28" colspan=2 align="left" bordercolor="#ececec" bgcolor="#ececec" class="txtGlobalBold">&nbsp;填写事件报告</td>
               	</tr>     	
     			<tr width ="100%">
     				<td colspan=2><br>如果您已经解决了该问题，现在您可以填写事件处理报告了<br><br></td>
     			</tr>
				<tr>
					<td align=right>解决时间</td>
					<td>&nbsp;&nbsp;
						<input type="text" name="deal_time5" size="10" value="<%=nowtime%>">
						<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].deal_time5,null,0,330)">
						<img id=imageCalendar1 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0></a>
					</td>
		        </tr>
				<tr>
					<td align=right>报告内容 </td>
					<td>&nbsp;&nbsp;<textarea name="report_content" rows="10" cols="100"><%=eventdetail.getContent()%></textarea></td>
				</tr>
				<tr>
					<td align=right>填写人</td>
					<td>&nbsp;&nbsp;<input type="text" name="report_man" value="<%=username%>"></td>
				</tr>
     			<tr>
     				<td align=center colspan=2>
			     		<br><input type="button" value="确 定" onclick="batchDoReport()">
			     		<input type="button" value="关闭窗口" onclick="window.close()"><br><br>
			     	</td>
     			</tr>
			</table>		
		</td>
	</tr>
</table>                	              
</form>
</BODY>
</HTML>
 