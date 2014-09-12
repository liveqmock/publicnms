<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%
  String rootPath = request.getContextPath();
  //System.out.println(rootPath);
  List list = (List)request.getAttribute("list");
  
  int rc = list.size();

  
  
String startdate = (String)request.getAttribute("startdate");
String todate = (String)request.getAttribute("todate");

int level1 = Integer.parseInt(request.getAttribute("level1")+"");
int _status = Integer.parseInt(request.getAttribute("status")+"");

String level1str="";
String level2str="";
String level3str="";
if(level1 == 1){
	level1str = "selected";
}else if(level1 == 2){
	level2str = "selected";
}else if(level1 == 3){
	level3str = "selected";	
}

String status0str="";
String status1str="";
String status2str="";
if(_status == 0){
	status0str = "selected";
}else if(_status == 1){
	status1str = "selected";
}else if(_status == 2){
	status2str = "selected";	
}  
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script language="javascript">	
 
  var delAction = "<%=rootPath%>/event.do?action=delete";
  var listAction = "<%=rootPath%>/event.do?action=list";

function accEvent(eventid){
	mainForm.eventid.value=eventid;
	mainForm.action="<%=rootPath%>/event.do?action=accit";	
	mainForm.submit();
}

  function query()
  {  
     mainForm.action = "<%=rootPath%>/event.do?action=list&jp=1";
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
  
    function doDelete()
  {  
     mainForm.action = "<%=rootPath%>/event.do?action=dodelete";
     mainForm.submit();
  }  
  
// 全屏观看
function gotoFullScreen() {
	parent.mainFrame.resetProcDlg();
	var status = "toolbar=no,height="+ window.screen.height + ",";
	status += "width=" + (window.screen.width-8) + ",scrollbars=no";
	status += "screenX=0,screenY=0";
	window.open("topology/network/index.jsp", "fullScreenWindow", status);
	parent.mainFrame.zoomProcDlg("out");
}

</script>
<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;
//修改菜单的上下箭头符号
function my_on(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="on";
}
function my_off(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="off";
}
//添加菜单	
function initmenu()
{
	var idpattern=new RegExp("^menu");
	var menupattern=new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for(var i=0,j=tds.length;i<j;i++){
		var td = tds[i];
		if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}
	abc();

}

</script>
</head>
<BODY leftmargin="0" class="body" topmargin="0" onload="initmenu();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm">
<input type=hidden name="eventid">
<table border="0"  cellpadding="0" cellspacing="0" width=100% id="body-container" class="body-container">
	<tr>
		
		<td align="center" valign=top>
			<table style="width:98%"  cellpadding="0" cellspacing="0" algin="center">
			<tr>
				<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%"><table width="100%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    <td class="layout_title"><b>告警 >> 告警浏览 >> 实时告警列表</b></td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>
			  </tr>
				
				
							
  						<tr>           
								<td>
								<table width="100%" cellpadding="0" cellspacing="1" >
								<tr>
								<td>
								<table width="100%" cellpadding="0" cellspacing="0" >
								<tr>
								<td bgcolor="#ECECEC" width="50%" align='left'>
					&nbsp;&nbsp;开始日期
		<input type="text" name="startdate" value="<%=startdate%>" size="10">
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
	<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
	
		截止日期
		<input type="text" name="todate" value="<%=todate%>" size="10"/>
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
	<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
		
	
	
						</td>
					<td bgcolor="#ECECEC" width="50%" align='right'>
					<table height=15>
					<tr height=15>
					<td bgcolor=red height=15>
					&nbsp;&nbsp;
					</td>
					<td height=15>
					紧急告警
					</td>
					<td bgcolor=orange height=15>
					&nbsp;&nbsp;
					</td>
					<td height=15>
					严重告警
					</td>
					<td bgcolor=yellow height=15>
					&nbsp;&nbsp;
					</td>
					<td height=15>
					普通告警
					</td>
					<td bgcolor=blue height=15>
					&nbsp;&nbsp;
					</td>
					<td height=15>
					提示信息
					</td>
										</tr>
								</table>
		  						</td>
										</tr>
								</table>
		  						</td>
									</tr>
								</table>
		  						</td>                       
        						</tr>							

        						
							<tr>
								<td colspan="2">
  <table cellSpacing="1"  cellPadding="0" border=0 width=100%>
	
  <tr class="microsoftLook0" height=28>
    	<td>&nbsp;<INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()" align="center"><b>序号</b></td>
        <td width="8%" align="center"><b>等级</b></td>
        <td width="15%" align="center"><b>告警来源</b></td>
    	<td width="40%" align="center"><b>告警描述</b></td>
		<td align="center"><b>登记日期</b></td>
    	<td align="center"><b>登记人</b></td>
    	<td align="center"><b>处理状态</b></td>
    	<td align="center"><b>操作</b></td>
   </tr>
<%
    	EventList eventlist = null;
    	
  	java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
  	//List list = (List)request.getAttribute("list");
  	for(int i=0;i<list.size();i++){
  		String eventsrc = "";
  		eventlist = (EventList)list.get(i);
  		if(eventlist.getSubtype().equalsIgnoreCase("network") || eventlist.getSubtype().equalsIgnoreCase("host")){
  			HostNode node = new HostNode();
			HostNodeDao dao = new HostNodeDao();
			node = dao.loadHost(eventlist.getNodeid()); 
			eventsrc = node.getAlias()+"("+node.getIpAddress()+")";
  		}else{
  			eventsrc = eventlist.getEventlocation();
  		}
  	/*
     	HostNode node = new HostNode();
	HostNodeDao dao = new HostNodeDao();
	if(eventlist.getSubtype().equalsIgnoreCase("db")){
		//数据库
	}	
	node = dao.loadHost(eventlist.getNodeid());  
	*/	
  	Date cc = eventlist.getRecordtime().getTime();
  	Integer eventid = eventlist.getId();
  	String eventlocation = eventlist.getEventlocation();
  	String content = eventlist.getContent();
  	String level = String.valueOf(eventlist.getLevel1());
  	String status = String.valueOf(eventlist.getManagesign());
  	String s = status;
  	String showlevel = level;
  	String act="处理报告";
  	String bgcolorstr="";
  	if("0".equals(level)){
  		level="提示信息";
  	}
  	if("1".equals(level)){
  		level="普通告警";
  	}
  	if("2".equals(level)){
  		level="严重告警";
  	}
  	if("3".equals(level)){
  		level="紧急告警";
  	}  	
   	if("0".equals(status)){
  		status = "未处理";
  		bgcolorstr="#9966FF";
  	}
  	if("1".equals(status)){
  		status = "处理中";  
  		bgcolorstr="#3399CC";	
  	}
  	if("2".equals(status)){
  	  	status = "处理完成";
  	  	bgcolorstr="#33CC33";
  	}
  	String rptman = eventlist.getReportman();
  	String rtime1 = _sdf.format(cc);
%>

 <tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height=25>

    <td>&nbsp;&nbsp;<INPUT type="checkbox" class=noborder name=checkbox value="<%=eventlist.getId()%>"></td>
    <%
    	if("3".equals(showlevel)){
    %>
       <td bgcolor=red align=center><%=level%>&nbsp;</td>
       <%
       }else if("2".equals(showlevel)){
       %>
       <td bgcolor=orange align=center><%=level%>&nbsp;</td>
       <%
       }else if("1".equals(showlevel)){
       %>
       <td bgcolor=yellow align=center><%=level%>&nbsp;</td>
       <%
       }else if("0".equals(showlevel)){
        %>
           <td bgcolor=blue align=center><%=level%>&nbsp;</td>
        <%
       }
       %>
       <td><%=eventsrc%>&nbsp;</td>
      <td><%=content%></td>
       <td><%=rtime1%></td>
       <td><%=rptman%></td>
       <td class="detail-data-body-list" bgcolor="<%=bgcolorstr%>"> <font color=#ffffff><%=status%></font></td>
       <td align="center">
       <%
       		if("0".equals(s)){
       		%>
       			<input type ="button" value="接受处理" class="button" onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 500, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
       		<%
       		}
       		if("1".equals(s)){
       		%>
       			<input type ="button" value="填写报告" class="button" onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 700, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
       		<%
       		}
       		if("2".equals(s)){
       		%>
       			<input type ="button" value="查看报告" class="button" onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 700, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")'>
       		<%
       		}
       %>
       </td>
       
 </tr>
 <%}

 %>  
   
 </table>
								</td>
							</tr>	
					
			</table>
		</td>
	</tr>
</table>
</form>
</BODY>
<script>
	
	function abc()
	{
	
		
		setInterval("fresh()",5000);
	} 
	function fresh(){
		window.location.reload(true);
	}
</script>
</HTML>
