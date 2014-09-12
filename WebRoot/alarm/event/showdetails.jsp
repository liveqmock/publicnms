<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.config.model.Business"%>
<%@page import="com.afunms.event.model.EventList"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.Date"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.event.model.EventReport"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String)request.getAttribute("menuTable");
	List list = (List)request.getAttribute("list");
	JspPage jp = (JspPage)request.getAttribute("page");
	User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);
	String username = vo.getName();	
	 
	String nodeId = (String)request.getAttribute("nodeId");
	List businessList = (List)request.getAttribute("businessList");
	Hashtable hashtable = (Hashtable)request.getAttribute("hashtable");
	String subtype = (String)request.getAttribute("subtype");
	String level1 = (String)request.getAttribute("level1");
	String businessid = (String)request.getAttribute("businessid");
	String startdate = (String)request.getAttribute("startdate");
	String todate = (String)request.getAttribute("todate");
	String managesign = (String)request.getAttribute("managesign");
	
	String[][] alermlevel = new String[][]{{"0","提示信息"},{"1","普通告警"},{"2","严重告警"},{"3","紧急告警"}};
%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<LINK href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
<script type="text/javascript" 	src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all-debug.js"></script>


<script language="javascript">	
	var curpage= <%=jp.getCurrentPage()%>;
  	var totalpages = <%=jp.getPageTotal()%>;
  	var delAction = "<%=rootPath%>/event.do?action=delete";
  	var listAction = "<%=rootPath%>/event.do?action=showDetails"; 

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
     mainForm.action = "<%=rootPath%>/event.do?action=showDetails&jp=1";
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
  
  function showDetails(nodeId , subentity , subtype , level1){
  	document.getElementById("subtype").value=subtype;
  	document.getElementById("level1").value=level1;
  	document.getElementById("businessid").value="<%=businessid%>";
  	document.getElementById("managesign").value="<%=managesign%>";
  	mainForm.action = "<%=rootPath%>/event.do?action=showDetails&nodeId=" + nodeId +"&subentity=" + subentity;
     mainForm.submit();
  }
  
  
  
 
    function showDelete()
  {  
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
     mainForm.action = "<%=rootPath%>/event.do?action=showdelete";
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
	
	initValue();

}

function initValue(){
  	document.getElementById("subtype").value="<%=subtype%>";
  	document.getElementById("level1").value="<%=level1%>";
  	document.getElementById("businessid").value="<%=businessid%>";
  	document.getElementById("managesign").value="<%=managesign%>";
  	
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
		String content = "";
		if(eventreport != null){
			String deal_time = sdf0.format(eventreport.getDeal_time().getTime());
			String report_time = sdf0.format(eventreport.getReport_time().getTime());
			
			if(eventreport.getReport_content()!=null && eventreport.getReport_content().length()>0){
				content = eventreport.getReport_content().replaceAll("\r\n" , "<br/>");
			}
			
			%>
				var eventreport = [	'<%=eventreport.getId()%>',
									'<%=eventreport.getEventid()%>',
									'<%=content%>',
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
				html:eventreport[2]
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
	
	function doEditAlermLevel(){
		var option = "";
		<%for(int i = 0 ; i < alermlevel.length ;i ++){
			%>
			option = option + "<option value='<%=alermlevel[i][0]%>'>" + "<%=alermlevel[i][1]%>" + "</option>"
			<%
		}%>
		var win4 =new Ext.Window({
			title:"修改事件等级",
			width:400,
			padding:20,
			items:[{
				xtype:"label",
				html:'<div>请选择事件等级：<select id="selectAlarmLevel">'+option+'</select></div><br>'
			}],
			buttons:[
				{
				text:"保存",
				handler:function(){
					document.getElementById("alermlevel").value=document.getElementById("selectAlarmLevel").value;
					mainForm.action="<%=rootPath%>/event.do?action=doEditAlarmLevel";
        			mainForm.submit();
					win4.close();
				}
				},
				{
				text:"关闭窗口",
				handler:function(){
					win4.close();
				}
				}]
			
		});
		win4.show();
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
<body id="body" class="body" onload="initmenu();">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
	<form id="mainForm" method="post" name="mainForm">
		<input type="hidden" name="nodeId" value="<%=nodeId%>">
		<input type="hidden" id="alermlevel" name="alermlevel">
		
		<input type=hidden name="eventid">
		<input type=hidden name="deal_time5">
		<input type=hidden name="report_content">
		<input type=hidden name="report_man">
		<table id="body-container" class="body-container">
			<tr>
				<td class="td-container-menu-bar">
					<table id="container-menu-bar" class="container-menu-bar">
						<tr>
							<td>
								<%=menuTable%>
							</td>	
						</tr>
					</table>
				</td>
				<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td id="td-container-main-content" class="td-container-main-content">
								<table id="container-main-content" class="container-main-content">
									<tr>
										<td>
											<table id="content-header" class="content-header">
							                	<tr>
								                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
								                	<td class="content-title"> 告警 >> 告警浏览 >> 告警统计 </td>
								                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
								       			</tr>
								        	</table>
										</td>
									</tr>
									<tr>
										<td>
											<table id="content-body" class="content-body">
												<tr>
													<td>
														<table >
															<tr>
																<td class="detail-data-body-title">
																	<table>
																		<tr>
																			<td width="75%">&nbsp;</td>
																			<td width="15" bgcolor=red height=15 >&nbsp;&nbsp;</td>
																			<td  height=15>&nbsp;&nbsp;紧急告警</td>
																			<td width="15" bgcolor=orange height=15>&nbsp;&nbsp;</td>
																			<td  height=15>&nbsp;&nbsp;严重告警</td>
																			<td width="15" bgcolor=yellow height=15>&nbsp;&nbsp;</td>
																			<td  height=15>&nbsp;&nbsp;普通告警&nbsp;&nbsp;</td>
																		</tr>
																	</table>
						  										</td>
															</tr>
														</table>
			  										</td>
												</tr>
												<tr>
													<td>
														<table>
															<tr>
																<td class="detail-data-body-title">
																	开始日期
																	<input type="text" name="startdate" value="<%=startdate%>" size="9">
																	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																	<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
																	截止日期
																	<input type="text" name="todate" value="<%=todate%>" size="9"/>
																	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
																	<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
																	事件类型
																	<select name="subtype">
																	<option value="value">不限</option>
																	<option value="db" >db</option>
																	<option value="host" >host</option>
																	<option value="network" >network</option>
																	<option value="firewall" >firewall</option>
																	</select>
																	事件等级
																	<select name="level1">
																	<option value="-1">不限</option>
																	<option value="0" >提示信息</option>
																	<option value="1" >普通事件</option>
																	<option value="2" >严重事件</option>
																	<option value="3" >紧急事件</option>
																	</select>
																	业务类型
																	<select name="businessid" style="width: 90px;">
																	<option value="-1">不限</option>
																	<%
																		for(int i = 0 ; i < businessList.size(); i++){
																			Business business = (Business)businessList.get(i);
																			%>
																				<option value="<%=business.getId()%>"><%=business.getDescr()%></option>
																			<%
																		} 
																	%>
																	</select>
																	处理状态
																	<select name="managesign">
																	<option value="-1">不限</option>
																	<option value="0" >未确认</option>
																	<option value="1" >已确认</option>
																	<option value="2" >已处理</option>
																	</select>
																	<input type="button" name="submitss" value="查询" onclick="query()">
																	<%-- <input type="button" name="submitss" value="修改等级" onclick="doEditAlermLevel()">--%>
																</td>
															</tr>
														</table>
							  						</td>                       
					        					</tr>
					        					<tr>
													<td class="detail-data-body-title">
														<table>
															<tr align="right">
																<td width="75%">&nbsp;</td>
																<td width="15" height=15 >&nbsp;&nbsp;</td>
																<td  height=15>&nbsp;&nbsp;<input type="button" name="" value="接受处理" onclick='batchAccfiEvent();'/></td>
																<td width="15" height=15>&nbsp;&nbsp;</td>
																<td  height=15>&nbsp;&nbsp;<input type="button" name="" value="填写报告" onclick='batchDoReport();'/></td>
																<td width="15" height=15>&nbsp;&nbsp;</td>
																<td  height=15>&nbsp;&nbsp;<input type="button" name="submitss" value="修改等级" onclick="batchEditAlarmLevel();"></td>
																<td width="15" height=15>&nbsp;&nbsp;</td>
																<td  height=15>&nbsp;&nbsp;<input type="button" name="" value="删除警告" onclick='showDelete();'/>&nbsp;&nbsp;</td>
																<td width="15" height=15>&nbsp;&nbsp;</td>
															</tr>
														</table>
													</td>
												</tr>
					        					<tr>
													<td>
														<table>
															<tr>
																<td class="detail-data-body-title">
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
													<td>
														<table>
															<tr>
																<td class="detail-data-body-title"><INPUT type="checkbox" name="checkall" onclick="javascript:chkall()">序号</td>
        														<td class="detail-data-body-title">等级</td>
        														<td class="detail-data-body-title">告警来源</td>
														    	<td class="detail-data-body-title">告警描述</td>
														    	<td class="detail-data-body-title">开始时间</td>
														    	<td class="detail-data-body-title">结束时间</td>
														    	<td class="detail-data-body-title">登记人</td>
														    	<td class="detail-data-body-title">处理状态</td>
														    	<td class="detail-data-body-title">操作</td>
															</tr>
															<%
																//int startRow = jp.getStartRow();
  																java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
																if(list!=null && list.size()>0){
																	for(int i = 0 ; i < list.size(); i++){
																			EventList eventList = (EventList)list.get(i);
																			
																			Date cc = eventList.getRecordtime().getTime();
																			String recordtime = _sdf.format(cc);
																			String level = String.valueOf(eventList.getLevel1());
																			String bgcolor = "";
																			String status = "";
																			int eventid = eventList.getId();
																			
																			managesign = String.valueOf(eventList.getManagesign());
																			if("0".equals(level)){
																		  		level="提示信息";
																		  		bgcolor = "bgcolor='blue'";
																		  	}
																			if("1".equals(level)){
																		  		level="普通告警";
																		  		bgcolor = "bgcolor='yellow'";
																		  	}
																		  	if("2".equals(level)){
																		  		level="严重告警";
																		  		bgcolor = "bgcolor='orange'";
																		  	}
																		  	if("3".equals(level)){
																		  		level="紧急告警";
																		  		bgcolor = "bgcolor='red'";
																		  	}
																		  	String bgcolorstr="";
																		  	if("0".equals(managesign)){
																		  		status = "未处理";
																		  		bgcolorstr="#9966FF";
																		  	}
																		  	if("1".equals(managesign)){
																		  		status = "处理中";  
																		  		bgcolorstr="#3399CC";	
																		  	}
																		  	if("2".equals(managesign)){
																		  	  	status = "处理完成";
																		  	  	bgcolorstr="#33CC33";
																		  	}
																		%>
																		<tr <%=onmouseoverstyle%>>
																			<td class="detail-data-body-list"><INPUT type="checkbox" name="checkbox" value="<%=eventList.getId()%>"><%=i+1%></td>
			        														<td class="detail-data-body-list" <%=bgcolor%>><%=level%></td>
			        														<td class="detail-data-body-list"><%=hashtable.get(eventList.getId()+"")%></td>
																	        <td class="detail-data-body-list"><%=eventList.getContent()%></td>
																	    	<td class="detail-data-body-list"><%=recordtime%></td>
																	    	<td class="detail-data-body-list"><%=eventList.getLasttime()%></td>
																	    	<td class="detail-data-body-list"><%=eventList.getReportman()%></td>
																	    	<td class="detail-data-body-list" bgcolor=<%=bgcolorstr%>><font color=#ffffff><%=status%></font></td>
																	    	<td class="detail-data-body-list">
																	    	  <%
																					  if ("0".equals(managesign)&&"提示信息".equals(level)) {
																	    	 
																					}else if ("0".equals(managesign)) {
																				%>
																				<input type="button" value="接受处理" class="button"
																					onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																				<!--<input type ="button" value="接受处理" class="button" onclick="accEvent('<%=eventid%>')">-->
																				<%
																					}
																						if ("1".equals(managesign)) {
																				%>
																				<input type="button" value="填写报告" class="button"
																					onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																				<!--<input type ="button" value="填写报告" class="button" onclick="fiReport('<%=eventid%>')">-->
																				<%
																					}
																						if ("2".equals(managesign)) {
																				%>
																				<input type="button" value="查看报告" class="button"
																					onclick='window.open("<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>","accEventWindow", "toolbar=no,height=400, width= 800, top=200, left= 200,resizable=yes,scrollbars=yes,screenX=0,screenY=0")'>
																				<!--<input type ="button" value="查看报告" class="button" onclick="viewReport('<%=eventid%>')">-->
																				<%
																					}
																				%></td>
																		</tr>
																		<%
																	}
																} 
															%>
														</table>
													</td>
												</tr>				
											</table>
										</td>
									</tr>
									<tr>
										<td>
											<table id="content-footer" class="content-footer">
												<tr>
													<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
                  									<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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
		</table>
		
	</form>
</BODY>
</HTML>
