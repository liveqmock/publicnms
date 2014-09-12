<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@ include file="/include/globe.inc"%>
<%
  String rootPath = request.getContextPath();
  
String str0="";
String str1="";
String str2="";
String str3="";
String str4="";
String str5="";
String str6="";
String str7="";
String p0="";
String p1="";
String p2="";
String p3="";
String p4="";
String p5="";
String p6="";
String p7="";String p8="";String p9="";
String p16="";String p17="";String p18="";
String p19="";String p20="";String p21="";
String p22="";String p23="";
  
List flist = (List)request.getAttribute("facilitys");
List plist = (List)request.getAttribute("prioritys");
if(flist != null && flist.size()>0){
	for(int i=0;i<flist.size();i++){
		if("0".equals(flist.get(i))) {
			str0="checked";
		}else if("1".equals(flist.get(i))) {
			str1="checked";
		}else if("2".equals(flist.get(i))) {
			str2="checked";	
		}else if("3".equals(flist.get(i))) {
			str3="checked";
		}else if("4".equals(flist.get(i))) {
			str4="checked";	
		}else if("5".equals(flist.get(i))) {
			str5="checked";
		}else if("6".equals(flist.get(i))) {
			str6="checked";	
		}else if("7".equals(flist.get(i))) {
			str7="checked";								
		}
	}
}
if(plist != null && plist.size()>0){
	for(int i=0;i<plist.size();i++){
		if("0".equals(plist.get(i))) {
			p0="checked";
		}else if("1".equals(plist.get(i))) {
			p1="checked";
		}else if("2".equals(plist.get(i))) {
			p2="checked";	
		}else if("3".equals(plist.get(i))) {
			p3="checked";
		}else if("4".equals(plist.get(i))) {
			p4="checked";	
		}else if("5".equals(plist.get(i))) {
			p5="checked";
		}else if("6".equals(plist.get(i))) {
			p6="checked";	
		}else if("7".equals(plist.get(i))) {
			p7="checked";	
		}else if("8".equals(plist.get(i))) {
			p8="checked";
		}else if("9".equals(plist.get(i))) {
			p9="checked";	
		}else if("16".equals(plist.get(i))) {
			p16="checked";
		}else if("17".equals(plist.get(i))) {
			p17="checked";	
		}else if("18".equals(plist.get(i))) {
			p18="checked";
		}else if("19".equals(plist.get(i))) {
			p19="checked";	
		}else if("20".equals(plist.get(i))) {
			p20="checked";
		}else if("21".equals(plist.get(i))) {
			p21="checked";
		}else if("22".equals(plist.get(i))) {
			p22="checked";	
		}else if("23".equals(plist.get(i))) {
			p23="checked";													
		}
	}
}



String logcontent = (String)request.getAttribute("content");
if(logcontent == null)logcontent = "";
 
%>

<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<style>
.padding_td{
	padding:10px 10px 6px 10px;
	margin:10px 10px 10px 10px;
}

</style>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script language="javascript">	
  var delAction = "<%=rootPath%>/netsyslog.do?action=delete";
  var listAction = "<%=rootPath%>/netsyslog.do?action=list";

  function query()
  {  
     mainForm.action = "<%=rootPath%>/netsyslog.do?action=list&jp=1";
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
      mainForm.action = "<%=rootPath%>/netsyslog.do?action=save";
      mainForm.submit();
  }
  
function CreateWindow(urlstr)
{
msgWindow=window.open(urlstr,"protypeWindow","toolbar=no,width=500,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
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
<BODY leftmargin="0" topmargin="0" bgcolor="#ababab" onload="initmenu();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm">
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
                    <td class="layout_title"><b>告警 >> SYSLOG管理 >> SYSLOG过滤规则</b></td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>
			  </tr>
  				<tr>
  					<td valign=top>
						<table width="100%" cellPadding=0 cellspacing="1">
                    					<tr  align="center">                      														
                      						<td height="28" bgcolor="#ECECEC" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;<b>级别</b></td>
                    					</tr>
                    					<tr align="left" align="center" bgcolor=#ffffff> 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="0" <%=str0%> name="fcheckbox">&nbsp;&nbsp;紧急</div></td>
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="1" <%=str1%> name="fcheckbox">&nbsp;&nbsp;报警</div></td> 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="2" <%=str2%> name="fcheckbox">&nbsp;&nbsp;关键</div></td> 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="3" <%=str3%> name="fcheckbox">&nbsp;&nbsp;错误</div></td>
                    					</tr> 
                    					<tr align="left" align="center" bgcolor=#ffffff> 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="4" <%=str4%> name="fcheckbox">&nbsp;&nbsp;警告</div></td>
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="5" <%=str5%> name="fcheckbox">&nbsp;&nbsp;通知</div></td>
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="6" <%=str6%> name="fcheckbox">&nbsp;&nbsp;提示</div></td>
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="7" <%=str7%> name="fcheckbox">&nbsp;&nbsp;调试</div></td>
                    					</tr>                     					                    					                    					                     		                   										                 										                      								
            					</table>
            		</td>
            				</tr>
            				<tr>
            				<td>
					<table width="100%" cellPadding=0 cellspacing="1" >
                        			<tbody> 
									
                    					<tr  align="center" bgcolor="#ECECEC" >                      														
                      						<td height="28" bgcolor="#ECECEC" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;<b>分组</b></td>
                    					</tr>
								
                                    
                    					<tr  bgcolor=#ffffff > 
                    						<td height="28" width=10% ><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="0" <%=p0%> name="checkbox">&nbsp;&nbsp;kernel</div></td>
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="1" <%=p1%> name="checkbox">&nbsp;&nbsp;user</div></td> 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="2" <%=p2%> name="checkbox">&nbsp;&nbsp;mail</div></td> 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="3" <%=p3%> name="checkbox">&nbsp;&nbsp;daemon</div></td>
                    					</tr> 
                    					<tr  bgcolor=#ffffff > 
                    						<td height="28" width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="4" <%=p4%> name="checkbox">&nbsp;&nbsp;auth</div></td>
                    						<td height="28" width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="5" <%=p5%> name="checkbox">&nbsp;&nbsp;syslog</div></td>
                    						<td height="28" width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="6" <%=p6%> name="checkbox">&nbsp;&nbsp;lpr</div></td>
                    						<td height="28" width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="7" <%=p7%> name="checkbox">&nbsp;&nbsp;news</div></td>
                    					</tr>  
                    					<tr bgcolor=#ffffff > 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="8" <%=p8%> name="checkbox">&nbsp;&nbsp;uucp</div></td>
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="9" <%=p9%> name="checkbox">&nbsp;&nbsp;cron</div></td> 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="16" <%=p16%> name="checkbox">&nbsp;&nbsp;local0</div></td> 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="17" <%=p17%> name="checkbox">&nbsp;&nbsp;local1</div></td>
                    					</tr> 
                    					<tr bgcolor=#ffffff > 
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="18" <%=p18%> name="checkbox">&nbsp;&nbsp;local2</div></td>
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="19" <%=p19%> name="checkbox">&nbsp;&nbsp;local3</div></td>
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="20" <%=p20%> name="checkbox">&nbsp;&nbsp;local4</div></td>
                    						<td height="28"  width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="21" <%=p21%> name="checkbox">&nbsp;&nbsp;local5</div></td>
                    					</tr>  
                    					<tr bgcolor=#ffffff > 
                    						<td height="28" width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="22" <%=p22%> name="checkbox">&nbsp;&nbsp;local6</div></td>
                    						<td height="28" width=10%><div style="margin-left:100px"><INPUT type="checkbox" class=noborder value="23" <%=p23%> name="checkbox">&nbsp;&nbsp;local7</div></td>
                    						<td height="28" width=10%></td>
                    						<td height="28" align="center" width=10%></td>
                    					</tr>       					                   					                    					                    	
                    					 <tr  align="center" bgcolor=#ECECEC> 
				                            <td bgcolor="#ECECEC" align="center" colspan="8">
				                              <input type="button" value="修 改" style="width:50" class="formStylebutton" onclick="toAdd()">&nbsp;&nbsp;
            					            </td>
								        </tr>				                     		                   										                 										                      								
            					</tbody>
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
