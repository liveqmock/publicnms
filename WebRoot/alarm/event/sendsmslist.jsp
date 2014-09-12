<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@ include file="/include/globe.inc"%>

<%@page import="com.afunms.event.model.*"%>
<%@page import="java.util.*"%>

<%
  String rootPath = request.getContextPath();
  //System.out.println(rootPath);
  List list = (List)request.getAttribute("list");
  
  int rc = list.size();

  JspPage jp = (JspPage)request.getAttribute("page");
  
String startdate = (String)request.getAttribute("startdate");
String todate = (String)request.getAttribute("todate");

//System.out.println("========================");
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script language="javascript">	
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/smsevent.do?action=delete";
  var listAction = "<%=rootPath%>/smsevent.do?action=list";

function accEvent(eventid){
	mainForm.eventid.value=eventid;
	mainForm.action="<%=rootPath%>/smsevent.do?action=accit";	
	mainForm.submit();
}

  function query()
  {  
     mainForm.action = "<%=rootPath%>/smsevent.do?action=list&jp=1";
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
     mainForm.action = "<%=rootPath%>/smsevent.do?action=dodelete";
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
                    <td class="layout_title"><b>告警 >> 短信告警 >> 短信告警浏览</b></td>
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
					            <td bgcolor="#ECECEC" width="100%" align='left'>&nbsp;&nbsp;&nbsp;开始日期
		<input type="text" name="startdate" value="<%=startdate%>" size="10">
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
	<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
	
		截止日期
		<input type="text" name="todate" value="<%=todate%>" size="10"/>
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
	<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
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
								<td colspan="2">
									<table cellspacing="1" cellpadding="0" width="100%">
  						<tr align="center" height=28 class="microsoftLook0"> 
    	<td><strong>&nbsp;序号</strong></td>
        <td width="10%"><strong>名字</strong></td>
        <td width="15%"><strong>手机号码</strong></td>
    	<td width="40%"><strong>告警描述</strong></td>
	<td><strong>告警日期</strong></td>
   </tr>
<%
int startRow = jp.getStartRow();
SendSmsConfig vo=null;
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
for(int i=0;i<list.size();i++)
{ 
     vo=(SendSmsConfig)list.get(i);
     Date cc = vo.getEventtime().getTime();
     String rtime1 = sdf.format(cc);
    
%>

 <tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height=25>

    <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;">&nbsp; <%=startRow + i%></td>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;"><%=vo.getName()%>&nbsp;</td>
      <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;"><%=vo.getMobilenum()%></td>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;">
      <%=vo.getEventlist()%></td>
       <td style="border-left:1px solid #EEEEEE;border-bottom:1px solid #EEEEEE;">
      <%=rtime1%></td>
 </tr>
 <%
}
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
