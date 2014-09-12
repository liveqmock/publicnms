<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@ page import="com.afunms.config.model.*"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%
  String rootPath = request.getContextPath();
  //System.out.println(rootPath);
  List list = (List)request.getAttribute("list");
  
String startdate = (String)request.getAttribute("startdate");
String todate = (String)request.getAttribute("todate");

int level1 = Integer.parseInt(request.getAttribute("level1")+"");
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

function accEvent(eventid){
	mainForm.eventid.value=eventid;
	mainForm.action="<%=rootPath%>/event.do?action=accit";	
	mainForm.submit();
}

  function query()
  {  
     mainForm.action = "<%=rootPath%>/event.do?action=equipmentlist&jp=1";
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

}

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="initmenu();">
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
                    <td class="layout_title"><b>告警 >> 告警统计 >> 按设备分布</b></td>
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
					            <td bgcolor="#ECECEC" width="100%" align='right'><INPUT type="button" class="formStyle" value="刷 新" onclick="window.location.href=window.location.href">
					&nbsp;&nbsp;&nbsp;
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

        						
							<tr>
								<td colspan="2">
  <table  cellSpacing="1"  cellPadding="0" border=0 width=100%>
  <tr  class="microsoftLook0" height=28>
    	<td align="center">&nbsp;<strong>序号</strong></td>
        <td width="15%" align="center"><strong>设备名称</strong></td>
        <td width="10%" align="center"><strong>设备类型</strong></td>
        <td width="20%" align="center"><strong>告警图表</strong></td>
        <td width="10%" align="center"><strong>告警总数</strong></td>
        <td width="10%" align="center"><strong>普通告警</strong></td>
    	<td width="10%" align="center"><strong>紧急告警</strong></td>
	<td align="center"><strong>严重告警</strong></td>
    	<td align="center"><strong>未处理</strong></td>
    	<td align="center"><strong>正在处理</strong></td>
    	<td align="center"><strong>已处理</strong></td>
   </tr>
<%
    	//EventList eventlist = null;
  	java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
  	//List list = (List)request.getAttribute("list");
  	for(int i=0;i<list.size();i++){
  		List rlist = (List)list.get(i);
  		HostNode node = (HostNode)rlist.get(0);
  		List levellist = (List)rlist.get(1);
  		List mlist = (List)rlist.get(2);
  		int one = 0;
  		int two = 0;
  		int three = 0;
  		int mone = 0;
  		int mtwo = 0;
  		int mthree = 0;
  		if(levellist != null && levellist.size()>0){
  			if(levellist.get(0)!= null){
  				one = (Integer)levellist.get(0);
  			}
  			if(levellist.get(1)!= null){
  				two = (Integer)levellist.get(1);
  			}
  			if(levellist.get(2)!= null){
  				three = (Integer)levellist.get(2);
  			}  			
  		}
  		if(mlist != null && mlist.size()>0){
  			if(mlist.get(0)!= null){
  				mone = (Integer)mlist.get(0);
  			}
  			if(mlist.get(1)!= null){
  				mtwo = (Integer)mlist.get(1);
  			}
  			if(mlist.get(2)!= null){
  				mthree = (Integer)mlist.get(2);
  			}  			
  		}
  		
  		String chart1 = null,chart2 = null,chart3 = null,responseTime = null;
  		DefaultPieDataset dpd = new DefaultPieDataset();
  		if((one+two+three)== 0){
  		
  		}else{
  			dpd.setValue("普通事件",100*one/(one+two+three));
  			dpd.setValue("紧急事件",100*two/(one+two+three));
  			dpd.setValue("严重事件",100 - 100*(one+two)/(one+two+three));
  			chart1 = ChartCreator.createPieChart(dpd,"",170,100); 
  		}  
  		int category = node.getCategory();
  		String catstr = "";
  		if(category < 4 || category == 7){
  			catstr = "网络设备";
  		}else if(category == 4){
  			catstr = "服务器";
  		}		
  		
%>

 <tr  bgcolor="#FFFFFF" <%=onmouseoverstyle%> height=25>

    <td>&nbsp;&nbsp;<%=i+1%></td>
       <td><%=node.getAlias()%>(<%=node.getIpAddress()%>)&nbsp;</td>
       <td><%=catstr%>&nbsp;</td>
       <%if (chart1 != null){%>
       <td><img src="<%=rootPath%>/artist?series_key=<%=chart1%>">&nbsp;</td>
       <%}else{%>
       <td>无事件&nbsp;</td>
       <%}%>
       <td><%=(one+two+three)%>&nbsp;</td>
       <td><%=one%>&nbsp;</td>
      <td><%=two%></td>
       <td>
      <%=three%></td>
       <td>
      <%=mone%></td>
       <td>
      <%=mtwo%></td>
       <td>
       <%=mthree%></td>
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
