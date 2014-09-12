<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.event.model.EventReport"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@ page import="com.afunms.config.model.*"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%
  String rootPath = request.getContextPath();
  
  User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);
	String username = vo.getName();	
  //System.out.println(rootPath);
  List list = (List)request.getAttribute("list");
  
  int rc = list.size();

  JspPage jp = (JspPage)request.getAttribute("page");
  
String startdate = (String)request.getAttribute("startdate");
String todate = (String)request.getAttribute("todate");

int level1 = Integer.parseInt(request.getAttribute("level1")+"");
//int businessid = Integer.parseInt(request.getAttribute("businessid")+"");
String subtype = (String)request.getAttribute("subtype");

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
//System.out.println("========================");
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">

<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 

<link rel="stylesheet" type="text/css" href="<%=rootPath%>/system/usertasklog/resource/ext2/resources/css/ext-all.css">
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/system/usertasklog/resource/ext2/resources/css/core.css">
<script type="text/javascript" src="<%=rootPath %>/system/usertasklog/resource/ext2/ext-base.js"></script>
<script type="text/javascript" src="<%=rootPath %>/system/usertasklog/resource/ext2/ext-all-debug.js"></script>

<script language="javascript">	
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/event.do?action=delete";
  var listAction = "<%=rootPath%>/event.do?action=list";

function accEvent(eventid){
	mainForm.eventid.value=eventid;
	mainForm.action="<%=rootPath%>/event.do?action=accit";	
	mainForm.submit();
}

	function batchAccfiEvent(){
		var eventids = ''; 
		var formItem=document.forms["mainForm"];
		var formElms=formItem.elements;
		var l=formElms.length;
		while(l--){
			if(formElms[l].type=="checkbox"){
				var checkbox=formElms[l];
				if(checkbox.name == "checkbox" && checkbox.checked==true){
	 				if (eventids==""){
	 					eventids=checkbox.value;
	 				}else{
	 					eventids=eventids+","+checkbox.value;
	 				}
 				}
			}
		}
        if(eventids == ""){
        	alert("未选中");
        	return ;
        }
		window.open("<%=rootPath%>/alarm/event/batch_accitevent.jsp?eventids="+eventids,"accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0");
	}
	
	//batchDoReport();
	function batchDoReport(){
		 var eventids = ''; 
		var formItem=document.forms["mainForm"];
		var formElms=formItem.elements;
		var l=formElms.length;
		while(l--){
			if(formElms[l].type=="checkbox"){
				var checkbox=formElms[l];
				if(checkbox.name == "checkbox" && checkbox.checked==true){
	 				if (eventids==""){
	 					eventids=checkbox.value;
	 				}else{
	 					eventids=eventids+","+checkbox.value;
	 				}
 				}
			}
		}
        if(eventids == ""){
        	alert("未选中");
        	return ;
        }
		window.open("<%=rootPath%>/alarm/event/batch_doreport.jsp?eventids="+eventids,"accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0");
	}
	
	function batchEditAlarmLevel(){
		var eventids = ''; 
		var formItem=document.forms["mainForm"];
		var formElms=formItem.elements;
		var l=formElms.length;
		while(l--){
			if(formElms[l].type=="checkbox"){
				var checkbox=formElms[l];
				if(checkbox.name == "checkbox" && checkbox.checked==true){
	 				if (eventids==""){
	 					eventids=checkbox.value;
	 				}else{
	 					eventids=eventids+","+checkbox.value;
	 				}
 				}
			}
		}
        if(eventids == ""){
        	alert("未选中");
        	return ;
        }
		window.open("<%=rootPath%>/alarm/event/batch_editAlarmLevel.jsp?eventids="+eventids,"accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0");
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
   if(confirm('是否确定删除这条记录?')) {
     mainForm.action = "<%=rootPath%>/event.do?action=dodelete";
     mainForm.submit();
    }
  }  
  
	function summary(){
  		mainForm.action = "<%=rootPath%>/event.do?action=summary";
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

}

</script>

<script>
	var store = new Array();
	
	Ext.onReady(function(){
		<%
		
		java.text.SimpleDateFormat _sdf1 = new java.text.SimpleDateFormat("MM-dd HH:mm");
		java.text.SimpleDateFormat sdf0 = new java.text.SimpleDateFormat("yyyy-MM-dd"); 
  		String nowtime = sdf0.format(new Date());
	
		for(int i = 0 ; i < list.size() ; i ++){
		
			EventList eventList = (EventList)list.get(i);
			
  			Date cc = eventList.getRecordtime().getTime();
  			String recordtime = _sdf1.format(cc);
			%>
			
			store.push({
				id			:	"<%=eventList.getId()%>",
			    eventtype	:	"<%=eventList.getEventtype()%>",
			    eventlocation:	"<%=eventList.getEventlocation()%>",
			    content		:	"<%=eventList.getContent()%>",
			    level1		:	"<%=eventList.getLevel1()%>",
			    managesign	:	"<%=eventList.getManagesign()%>",
			    bak			:	"<%=eventList.getBak()%>",
			    recordtime	:	"<%=recordtime%>",
			    reportman	:	"<%=eventList.getReportman()%>",
			    nodeid		:	"<%=eventList.getNodeid()%>",
			    businessid	:	"<%=eventList.getBusinessid()%>",
			    oid			:	"<%=eventList.getOid()%>",
			    subtype		:	"<%=eventList.getSubtype()%>",
			    managetime	:	"<%=eventList.getManagetime()%>",
			    subentity	:	"<%=eventList.getSubentity()%>"
			    
	            });
			
			<%
		}
		
		EventReport eventreport= (EventReport)request.getAttribute("eventreport");
		if(eventreport != null){
			String deal_time = sdf0.format(eventreport.getDeal_time().getTime());
			String report_time = sdf0.format(eventreport.getReport_time().getTime());
			%>
				var eventreport = [	'<%=eventreport.getId()%>',
									'<%=eventreport.getEventid()%>',
									'<%=eventreport.getReport_content()%>',
									'<%=deal_time%>',
									'<%=report_time%>',
									'<%=eventreport.getReport_man()%>'
								];
				showReport(eventreport);
			<%
		}
		
		%>
	
	});
	
	function accEvent(eventId){
		var event = "";
		for(var i = 0 ; i < store.length ; i++){
			if(eventId == store[i].id){
				event = store[i];
			}
		}
		
		var window =new Ext.Window({
			title:"接收事件",
			width:400,
			height:300,
			padding:20,
			items:[{
				xtype:"label",
				html:'<div>该事件尚未被任何管理员受理，点击确定表明您已经了解该情况并且准备受理</div><br>'
					+'<div>事件等级:'+event.level1+'</div><br>'
					+'<div>事件描述:'+event.content+'</div><br>'
					+'<div>登记日期:'+event.recordtime+'</div><br>'
					+'<div>登 记 人:'+event.reportman+'</div><br>'
			
			
			}],
			buttons:[{
				text:"接受处理",
				handler:function(){
					document.getElementById("eventid").value = eventId;
					mainForm.action = "<%=rootPath%>/event.do?action=accfi";
        			mainForm.submit();
				}
			}]
			
		});
		window.show();
	}
	
	
	function doReport(eventId){
		var event = "";
		for(var i = 0 ; i < store.length ; i++){
			if(eventId == store[i].id){
				event = store[i];
			}
		}
		
		var win2 =new Ext.Window({
			title:"接收事件",
			width:400,
			padding:20,
			items:[{
				xtype:"label",
				html:'<div>如果您已经解决了该问题，现在您可以填写事件处理报告了</div><br>'
					//+'<div>解决时间:<input type="text" id="deal_time4" name="deal_time4" size="10" value="<%=nowtime%>">'
						//			+'<a onclick="event.cancelBubble=true;" href="javascript:showData()">'
						//			+'<img id=imageCalendar5 align=middle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0></a>'
					//+'</div>'
			},{
				xtype:"label",
				text:"解决时间:"
			},
			{
				id:"deal_time4", 
				name:"deal_time4",
				xtype:"datefield",
				value:"<%=nowtime%>",
				format:"Y-m-d"
			},{
				xtype:"label",
				html:'<div><br>报告内容</div>'
			},{
				id:"report_content1",
				name:"report_content1",
				xtype:"textarea",
				width:280,
				height:180,
				value:event.content
			},{
				id:"report_man1",
				name:"report_man1",
				xtype:"label",
				html:"<div><br>填 写 人:<%=username%></div>"
			}],
			buttons:[{
				text:"确 定",
				handler:function(){
					document.getElementById("eventid").value=eventId;
					document.getElementById("deal_time5").value=document.getElementById("deal_time4").value;
					document.getElementById("report_content").value=document.getElementById("report_content1").value;
					document.getElementById("report_man").value="<%=username%>";
					mainForm.action="<%=rootPath%>/event.do?action=doreport";
        			mainForm.submit();
				}},{
				text:"关闭窗口",
				handler:function(){
					win2.close();
				}
				}]
			
		});
		win2.show();
	}
	
	function showReport(eventreport){
		var win3 =new Ext.Window({
			title:"接收事件",
			width:400,
			padding:20,
			items:[{
				xtype:"label",
				html:'<div>该问题已经解决，完成报告如下</div><br>'
			},{
				xtype:"label",
				text:"解决时间:"+eventreport[3]
			},{
				xtype:"label",
				html:'<div><br>报告内容</div>'
			},{
				id:"report_content1",
				name:"report_content1",
				xtype:"label",
				width:250,
				height:150,
				text:eventreport[2]
			},{
				id:"report_man1",
				name:"report_man1",
				xtype:"label",
				html:'<div><br>填 写 人:'+ eventreport[5] +'</div>'
			}],
			buttons:[
				{
				text:"关闭窗口",
				handler:function(){
					win3.close();
				}
				}]
			
		});
		win3.show();
	}
	
	function viewReport(eventid){
		document.getElementById("eventid").value=eventid;
		mainForm.action="<%=rootPath%>/event.do?action=showReport";
        mainForm.submit();
	}
	
	function showData(){
		ShowCalendar(document.getElementById("imageCalendar5"),document.getElementById("deal_time5"),null,0,330);
	}
</script>


</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="initmenu();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm">
<input type=hidden name="eventid">
<input type=hidden name="deal_time5">
<input type=hidden name="report_content">
<input type=hidden name="report_man">

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
                    <td class="layout_title"><b>告警 >> 告警浏览 >> 告警列表</b></td>
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
					&nbsp;&nbsp;<INPUT type="button" class="formStyle" value="删 除" onclick=" return doDelete()">
					<INPUT type="button" class="formStyle" value="刷 新" onclick="window.location.reload()">
					<INPUT type="button" class="formStyle" value="实 时" onclick="window.open('<%=rootPath %>/event.do?action=todaylist&jsp=1')">
					<input type="button" class="formStyle" value="统 计" onclick="summary()">
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
					&nbsp;&nbsp;
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
								<td>
								<table width="100%" cellpadding="0" cellspacing="1" >
								<tr>
								<td>
								<table width="100%" cellpadding="0" cellspacing="0" >
								<tr>
								<td bgcolor="#ECECEC" width="50%" align='left'>
		&nbsp;&nbsp;&nbsp;&nbsp;开始日期
		<input type="text" name="startdate" value="<%=startdate%>" size="10">
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
	<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
	
		截止日期
		<input type="text" name="todate" value="<%=todate%>" size="10"/>
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
	<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
		事件类型
		<select name="subtype">
		<option value="value" seclected>不限</option>
		<option value="db" >db</option>
		<option value="host" >host</option>
		<option value="network" >network</option>
		<option value="firewall" >firewall</option>
		</select>
		事件等级
		<select name="level1">
		<option value="99">不限</option>
		<option value="1" <%=level1str%>>普通事件</option>
		<option value="2" <%=level2str%>>严重事件</option>
		<option value="3" <%=level3str%>>紧急事件</option>
		</select>
		业务类型
		<select name="businessid">
		<option value="0">不限</option>
		<%
			int businessid = Integer.parseInt(request.getAttribute("businessid")+"");
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
		处理状态
		<select name="status">
		<option value="99">不限</option>
		<option value="0" <%=status0str%>>未处理</option>
		<option value="1" <%=status1str%>>正在处理</option>
		<option value="2" <%=status2str%>>已处理</option>
		</select>	
	<input type="button" name="submitss" value="查询" onclick="query()">
						</td>
										</tr>
								</table>
		  						</td>
									</tr>
								</table>
		  						</td>                       
        						</tr>
        						<tr align="right" bgcolor="#ECECEC">
							<td><table><tr>
							<td width="75%">&nbsp;</td>
							<td width="15" height=15 >&nbsp;&nbsp;</td>
							<td  height=15>&nbsp;&nbsp;<input type="button" name="" value="接受处理" onclick='batchAccfiEvent();'/></td>
							<td width="15" height=15>&nbsp;&nbsp;</td>
							<td  height=15>&nbsp;&nbsp;<input type="button" name="" value="填写报告" onclick='batchDoReport();'/></td>
							<td width="15" height=15>&nbsp;&nbsp;</td>
							<td  height=15>&nbsp;&nbsp;<input type="button" name="submitss" value="修改等级" onclick="batchEditAlarmLevel();">&nbsp;&nbsp;</td>
							<td width="15" height=15>&nbsp;&nbsp;</td>
							</tr></table></td>
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
								<td colspan="5">
  <table  cellSpacing="1"  cellPadding="0" border=0 width=100%>
	
  <tr  class="microsoftLook0" height=28>
    	<td>&nbsp;<b><INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()" align="center">序号</b></td>
        <td width="10%" align="center"><b>等级</b></td>
        <td width="15%" align="center"><b>告警来源</b></td>
    	<td width="40%" align="center"><b>告警描述</b></td>
	<td align="center"><b>登记日期</b></td>
    	<td align="center"><b>登记人</b></td>
    	<td align="center"><b>处理状态</b></td>
    	<td align="center"><b>操作</b></td>
   </tr>
<%

    	EventList eventlist = null;
    	int startRow = jp.getStartRow();
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
  	
  	if("1".equals(level)){
  		level="普通告警";
  	}
  	if("2".equals(level)){
  		level="严重告警";
  	}
  	if("3".equals(level)){
  		level="紧急告警";
  	} 
  	if("0".equals(level)){
  		level="提示信息";
  	}  
  	String bgcolorstr="";	
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

    <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;">&nbsp;&nbsp;<INPUT type="checkbox" class=noborder name=checkbox value="<%=eventlist.getId()%>"><%=startRow + i%></td>
    <%
    	if("3".equals(showlevel)){
    %>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;" bgcolor=red align=center><%=level%>&nbsp;</td>
       <%
       }else if("2".equals(showlevel)){
       %>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;" bgcolor=orange align=center><%=level%>&nbsp;</td>
              <%
       }else if("0".equals(showlevel)){
       %>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;" bgcolor=blue align=center><%=level%>&nbsp;</td>
       <%
       }else{
       %>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;" bgcolor=yellow align=center><%=level%>&nbsp;</td>
       <%
       }
       %>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;"><%=eventsrc%>&nbsp;</td>
      <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;"><%=content%></td>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;">
      <%=rtime1%></td>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;">
      <%=rptman%></td>
       <td class="detail-data-body-list" bgcolor="<%=bgcolorstr%>"> <font color=#ffffff><%=status%></font></td>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;"  align="center">
         <%
		if ("0".equals(s)) {
		%>
		<input type="button" value="接受处理" class="button"
			onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
		<!--<input type ="button" value="接受处理" class="button" onclick="accEvent('<%=eventid%>')">-->
		<%
			}
				if ("1".equals(s)) {
		%>
		<input type="button" value="填写报告" class="button"
			onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
		<!--<input type ="button" value="填写报告" class="button" onclick="fiReport('<%=eventid%>')">-->
		<%
			}
				if ("2".equals(s)) {
		%>
		<input type="button" value="查看报告" class="button"
			onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
		<!--<input type ="button" value="查看报告" class="button" onclick="viewReport('<%=eventid%>')">-->
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
				<tr>
              <td background="<%=rootPath%>/common/images/right_b_02.jpg" ><table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
                    <td></td>
                    <td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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
