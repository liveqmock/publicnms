<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@ include file="/include/globe.inc"%>
<%@ page import="com.afunms.event.model.*"%>
<%@ page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="java.util.*"%>

<%
   String rootPath = request.getContextPath();
  //System.out.println(rootPath);
  List list = (List)request.getAttribute("list");
   HostNodeDao hostdao= new HostNodeDao();
   List hostnodelist = hostdao.loadNode();
  int rc = list.size();

  JspPage jp = (JspPage)request.getAttribute("page");
  
String startdate = (String)request.getAttribute("startdate");
String todate = (String)request.getAttribute("todate");

String _priority = "8,1,2,3";
if(request.getAttribute("priority") != null && !"".equals((String)request.getAttribute("priority"))){
	_priority = (String)request.getAttribute("priority");
}

String logcontent = (String)request.getAttribute("content");
if(logcontent == null)logcontent = "";

String _strclass = (String)request.getParameter("strclass");
String str1 = "";
String str2 = "";
String str3 = "";
if("1".equals(_strclass)){
	str2 = "selected";
}else if("2".equals(_strclass)){
	str3 = "selected";
}else{
	str1 = "selected";
}
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>

<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script src="<%=rootPath%>/resource/js/dtree.js" type="text/javascript"></script>
<script src="<%=rootPath%>/resource/js/prototype.js" type="text/javascript"></script>
<link href="<%=rootPath%>/include/dtree.css" type="text/css" rel="stylesheet" />

<script language="javascript">	
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/netsyslog.do?action=delete";
  var listAction = "<%=rootPath%>/netsyslog.do?action=list";

  function query()
  {
  hideTree('canMutiCheckTree');
   var value=document.getElementById("canMutiCheckTree_hidden").value
   mainForm.action = "<%=rootPath%>/netsyslog.do?action=list&jp=1&priority="+value;
   mainForm.submit();
  }  
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/network.do?action=find";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  }
  
function CreateWindow(urlstr)
{
msgWindow=window.open(urlstr,"protypeWindow","toolbar=no,width=500,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}
</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm">
<input type=hidden name="eventid">
<table id="body-container" class="body-container">
	<tr>
		<td width="200" valign=top align=center>
			<%=menuTable%>
		</td>
		<td align="center" valign=top>
			<table style="width:98%"  cellpadding="0" cellspacing="0" algin="center">
			<tr>
				<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%"><table width="100%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    <td class="layout_title"><b>告警 >> SYSLOG管理 >> 浏览STSLOG</b></td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>
			  </tr>
  						<tr>
		
					<tr>           
								<td>
								<table width="100%" cellpadding="0" cellspacing="1" >
        			<tr>
					            <td bgcolor="#ECECEC" width="100%" align='left'>开始日期
		<input type="text" name="startdate" value="<%=startdate%>" size="10">
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
	<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
	
		截止日期
		<input type="text" name="todate" value="<%=todate%>" size="10"/>
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
	<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
		级别
		<!-- 用于canMutiCheckTree的显示的层 --> 
		<div id="canMutiCheckTree_treeScript_Title" class="drop-title"><a href="javascript:void(0);" onclick="hideTree('canMutiCheckTree')">×&nbsp;</a></div>
		<div id="canMutiCheckTree_treeScript_Container" class="drop-body">
		  <div id="canMutiCheckTree_treeScript" class="tree"></div>
		</div>
		
		<!-- 存放canMutiCheckTree原始数据的层 -->
			<div id="canMutiCheckTree_Div" class="tree" style="display:none;">
				<script type="text/javascript">
						canMutiCheckTree = new dTree('canMutiCheckTree','','2');
						canMutiCheckTree.add(0,-1,'全部','','','mainFrame','','');
						canMutiCheckTree.add(8,0,'紧急','','','mainFrame','','');
						canMutiCheckTree.add(1,0,'报警','','','mainFrame','','');
						canMutiCheckTree.add(2,0,'关键','','','mainFrame','','');
						canMutiCheckTree.add(3,0,'错误','','','mainFrame','','');
						canMutiCheckTree.add(4,0,'警告','','','mainFrame','','');
						canMutiCheckTree.add(5,0,'通知','','','mainFrame','','');
						canMutiCheckTree.add(6,0,'提示','','','mainFrame','','');
						canMutiCheckTree.add(7,0,'调试','','','mainFrame','','');
						document.write(canMutiCheckTree);
				</script>
			</div>
		<!-- 用于canMutiCheckTree存放数据的控件 --> 
		<input id="canMutiCheckTree_text" value="" type="hidden"/>
		<input id="canMutiCheckTree_hidden" type="hidden" value="<%=_priority%>"/>
		<button id="dd" class="button" onclick="treeDropDown(this,'canMutiCheckTree');">告警级别选择<span style="font-family: Webdings; font-size:9px ">6</span></button>
		业务
		<select name="businessid">
		<option value="-1">不限</option>
		<%
			
			int businessid = Integer.parseInt(request.getAttribute("businessid")+"");
			//System.out.println("businessid==="+businessid);
			List businesslist = (List)request.getAttribute("businesslist");
			if(businesslist != null && businesslist.size()>0){
				for(int i=0;i<businesslist.size();i++){
					Business bu = (Business)businesslist.get(i);
					//if(businessid>=-1){
						if(bu.getId().equals(businessid+"")){
		%>
							<option value="<%=bu.getId()%>" selected><%=bu.getName()%></option>
		<%
						}else{
		%>
							<option value="<%=bu.getId()%>"><%=bu.getName()%></option>
		<%
						}
					//}
				}
			}
		%>
		</select>
		类别
		<select name="strclass">
		<option value="-1" <%=str1 %>>不限</option>
		<option value="1" <%=str2 %>>主机</option>
		<option value="2" <%=str3 %>>网络</option>
		</select>
		IP
		 <select id="ipaddress" name="ipaddress" >
		 <option value="-1" <%=str1 %>>不限</option>
				    			<%
				    				if (hostnodelist != null && hostnodelist.size()>0){
				    					for(int i=0;i<hostnodelist.size();i++){
				    						HostNode hostnode = (HostNode)hostnodelist.get(i);
				    			%>
				    				<option value='<%=hostnode.getIpAddress()%>'><%=hostnode.getIpAddress()%></option>
								<%		    				
				    					}
				    				}
				    			%>
				    					</select>
		描述<input type="text" name="content" size=20 value="<%=logcontent%>">&nbsp;			
	<input type="button" name="submitss" value="查询" onclick="query()">
						</td>
									</tr>
        								</table>
										</td>
        						</tr>		
		        		<tr>
							<td>
							<table width="100%" cellpadding="0" cellspacing="1" >
							<tr>
						    <td bgcolor="#ECECEC" width="80%" align="center">
    <jsp:include page="../../common/page.jsp">
     <jsp:param name="curpage" value="<%=jp.getCurrentPage()%>"/>
     <jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>"/>
   </jsp:include>
        </td>
        </tr>
		</table>
		</td>
		</tr>    						
        <tr>
		<td align=right bgcolor="#ECECEC">
		<a href="<%=rootPath%>/netsyslog.do?action=downloadsyslogreport&startdate=<%=startdate%>&todate=<%=todate%>" target="_blank"><img name="selDay1" alt='导出EXCEL' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0">导出EXCEL</a>&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="<%=rootPath%>/netsyslog.do?action=downloadsyslogreportall&startdate=<%=startdate%>&todate=<%=todate%>" target="_blank"><img name="selDay1" alt='导出EXCEL' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0">导出全部EXCEL</a>&nbsp;&nbsp;&nbsp;&nbsp;
		
		</td>
		</tr>  
        						
							<tr>
								<td colspan="2">
									<table cellspacing="1" cellpadding="0" width="100%">
  						<tr align="center" height=28 class="microsoftLook0"> 
    	<td><strong>序号</strong></td>
        <td width="10%"><strong>等级</strong></td>
        <td width="18%"><strong>来源</strong></td>
    	<td width="42%"><strong>描述</strong></td>
	<td><strong>接受时间</strong></td>
	<td><strong>详细信息</strong></td>
	
   </tr>
<%
    	NetSyslog syslog = null;
    	int startRow = jp.getStartRow();
		session.setAttribute("startRow",startRow);
		session.setAttribute("list",list);
  	java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
  	for(int i=0;i<list.size();i++){
	  	syslog = (NetSyslog)list.get(i);	
	  	System.out.println();
	  	Date cc = syslog.getRecordtime().getTime();
	  	String message = syslog.getMessage();
	  	int priority = syslog.getPriority();
	  	String priorityname = syslog.getPriorityName();
	  	String rtime1 = _sdf.format(cc);
	  	if("error".equals(priorityname)){
	  		priorityname = "<span style=\"color:#CC0000\">"+priorityname+"</span>";
	  	}else if("warning".equals(priorityname)){
	  		priorityname = "<span style=\"color:#FFCC00\">"+priorityname+"</span>";
	  	}

%>

 <tr  bgcolor="#FFFFFF" <%=onmouseoverstyle%> height=25  align="center"> 

    <td><%=startRow + i%></td>
       <td ><%=priorityname%>&nbsp;</td>
       <td><%=syslog.getHostname()%>(<%=syslog.getIpaddress()%>)&nbsp;</td>
       <td><%=message%></td>
       <td><%=rtime1%></td>
       <td><input type ="button" value="详细信息" class="button" onclick="javascript:return CreateWindow('<%=rootPath%>/netsyslog.do?action=netsyslogdetail&id=<%=syslog.getId()%>&ipaddress=<%=syslog.getIpaddress()%>')">
       </td>
 </tr>
 <%}

 %>  
   
 </table>
								</td>
							</tr>	
				<tr>
              <td background="<%=rootPath%>/common/images/right_b_02.jpg" ><table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="11" /></td>
                    <td></td>
                    <td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="11" /></td>
                  </tr>
              </table></td>
            </tr>	
			</table>
		</td>
	</tr>
</table>
</form>
</BODY>
</HTML>
