<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.topology.model.ConnectTypeConfig"%>
<%@page import="java.util.*" %>
<%@page import="com.afunms.automation.model.PasswdTimingConfig"%>
<%@page import="com.afunms.automation.util.AutomationUtil"%>
<%@ include file="/automation/common/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.system.util.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List<PasswdTimingConfig> PasswdTimingBackupTelnetConfigList = (ArrayList<PasswdTimingConfig>)request.getAttribute("list");
  //List list = (List)request.getAttribute("passwdTimingBackupTelnetConfigList");
  JspPage jp = (JspPage) request.getAttribute("page");
 Hashtable<String,String> alarmWayHashtable=(Hashtable<String,String>)request.getAttribute("alarmWayHashtable");
  
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<LINK href="<%=rootPath%>/automation/css/style.css" type="text/css" rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>
			
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
		<script language="JavaScript">

			//公共变量
			var node="";
			var ipaddress="";
			var operate="";
			/**
			*根据传入的id显示右键菜单
			*/
			function showMenu(id,nodeid,ip)
			{	
				ipaddress=ip;
				node=nodeid;
				//operate=oper;
			    if("" == id)
			    {
			        popMenu(itemMenu,100,"100");
			    }
			    else
			    {
			        popMenu(itemMenu,100,"1111");
			    }
			    event.returnValue=false;
			    event.cancelBubble=true;
			    return false;
			}
			/**
			*显示弹出菜单
			*menuDiv:右键菜单的内容
			*width:行显示的宽度
			*rowControlString:行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示
			*/
			function popMenu(menuDiv,width,rowControlString)
			{
			    //创建弹出菜单
			    var pop=window.createPopup();
			    //设置弹出菜单的内容
			    pop.document.body.innerHTML=menuDiv.innerHTML;
			    var rowObjs=pop.document.body.all[0].rows;
			    //获得弹出菜单的行数
			    var rowCount=rowObjs.length;
			    //alert("rowCount==>"+rowCount+",rowControlString==>"+rowControlString);
			    //循环设置每行的属性
			    for(var i=0;i<rowObjs.length;i++)
			    {
			        //如果设置该行不显示，则行数减一
			        var hide=rowControlString.charAt(i)!='1';
			        if(hide){
			            rowCount--;
			        }
			        //设置是否显示该行
			        rowObjs[i].style.display=(hide)?"none":"";
			        //设置鼠标滑入该行时的效果
			        rowObjs[i].cells[0].onmouseover=function()
			        {
			            this.style.background="#397DBD";
			            this.style.color="white";
			        }
			        //设置鼠标滑出该行时的效果
			        rowObjs[i].cells[0].onmouseout=function(){
			            this.style.background="#F1F1F1";
			            this.style.color="black";
			        }
			    }
			    //屏蔽菜单的菜单
			    pop.document.oncontextmenu=function()
			    {
			            return false; 
			    }
			    //选择右键菜单的一项后，菜单隐藏
			    pop.document.onclick=function()
			    {
			        pop.hide();
			    }
			    //显示菜单
			    pop.show(event.clientX-1,event.clientY,width,rowCount*25,document.body);
			    return true;
			}
			
		</script>
		<script type="text/javascript">
		
			function edit()
			{
				mainForm.action="<%=rootPath%>/remoteDevice.do?action=ready_editPasswdTimingCfg&id="+node;
				mainForm.submit();
			}
	  		function addBackup()
			{
				mainForm.action="<%=rootPath%>/remoteDevice.do?action=addPasswdBackup&id="+node;
				mainForm.submit();
			}
	  		function disBackup()
			{
				mainForm.action="<%=rootPath%>/remoteDevice.do?action=disPasswdBackup&id="+node;
				mainForm.submit();
			}
			  			
			function toAdd(){
				mainForm.action = "<%=rootPath%>/remoteDevice.do?action=ready_addPasswd";
			    mainForm.submit();
			}
	  		function toDelete(){  
     				mainForm.action = "<%=rootPath%>/remoteDevice.do?action=deletePasswdTimingCfg";
     				mainForm.submit();
	  		}
			  
			
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
	<div id="itemMenu" style="display: none";>
		<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
			style="border: thin;font-size: 12px" cellspacing="0">
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.edit()">编辑</td>
			</tr>
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.addBackup()">启动</td>
			</tr>
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.disBackup()">取消启动</td>
			</tr>
		</table>
	</div>
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title"> 自动化 >> 远程设备维护 >> 密码定时变更 </td>
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
															<table width="100%" cellpadding="0" cellspacing="1">
																<tr>
																	<td bgcolor="#ECECEC" width="10%" align='right'>
																		<a href="#" onclick="toAdd()">添加</a>
																		<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
		        									<tr>
		        										<td colspan="2">
		        											<table cellspacing="0" border="1" bordercolor="#ababab">
		        												<tr height=28 style="background:#ECECEC" align="center" class="content-title">
		        													<td align="center"><INPUT type="checkbox" id="checkall" name="checkall" onclick="javascript:chkall()" class=noborder></td>
		        													<td align="center">序号</td>
		        													<td align="center">IP地址</td>
		        												    <td align="center">任务创建时间</td>

		        													<td align="center">定时提醒时间</td>
		        													<td align="center">提醒方式</td>
		        													<td align="center">是否定时</td>
		        													<td align="center" width="20%">操作</td>
		        												</tr>
        												    <%					
															if(PasswdTimingBackupTelnetConfigList != null){
																//String[] frequencyName = { "每天", "每周", "每月", "每季", "每年" };
																String[] monthCh = { " 1月", " 2月", " 3月", " 4月", " 5月", " 6月", " 7月", " 8月",
																		" 9月", " 10月", " 11月", " 12月" };
																String[] weekCh = { " 星期日", " 星期一", " 星期二", " 星期三", " 星期四", " 星期五", " 星期六" };
																String[] dayCh = null;
																String[] hourCh =null;
																for(int i=0; i<PasswdTimingBackupTelnetConfigList.size(); i++){
																	PasswdTimingConfig passWDBackupTelnetConfig = (PasswdTimingConfig)PasswdTimingBackupTelnetConfigList.get(i);
																	StringBuffer sb = new StringBuffer();
																	String frequency = passWDBackupTelnetConfig.getBackup_sendfrequency();
																	String month = AutomationUtil.splitDate(passWDBackupTelnetConfig.getBackup_time_month(), monthCh, "month");
																	String week = AutomationUtil.splitDate(passWDBackupTelnetConfig.getBackup_time_week(), weekCh, "week"); 
																	String day = AutomationUtil.splitDate(passWDBackupTelnetConfig.getBackup_time_day(), dayCh, "day");
																	String hour = AutomationUtil.splitDate(passWDBackupTelnetConfig.getBackup_time_hou(), hourCh, "hour");
																	sb.append(frequency + " ");
														
																	if(frequency.equals("每天")){//每天 
																		sb.append(" 时间：(" + hour + ")");
																	}
																	if(frequency.equals("每周")){//每周
																		sb.append(" 星期:(" + week + ")");
																		sb.append(" 时间：(" + hour + ")");
																	}
																	if(frequency.equals("每月")){//每月
																		sb.append(" 日期：(" + day + ")");
																		sb.append(" 时间：(" + hour + ")");
																	}
																	if(frequency.equals("每季")){//每季
																		sb.append(" 月份：(" + month + ")");
																		sb.append(" 日期：(" + day + ")");
																		sb.append(" 时间：(" + hour + ")");
																	}
																	if(frequency.equals("每年")){//每年
																		sb.append(" 月份：(" + month + ")");
																		sb.append(" 日期：(" + day + ")");
																		sb.append(" 时间：(" + hour + ")");
																	}
																	
																	String status = passWDBackupTelnetConfig.getStatus();
																	String wayId=passWDBackupTelnetConfig.getWarntype();
																	String wayName=alarmWayHashtable.get(wayId);
																 %>
		        												  <tr <%=onmouseoverstyle%>>
										        						<td align='center'>
																		<INPUT type="checkbox" class=noborder name="checkbox"
																				value="<%=passWDBackupTelnetConfig.getId()%>">
																		</td>
																		<td align='center'>
																			<font color='blue'><%=i+1 %></font>
		        														<br></td> 
		        														<td align='center'><%=passWDBackupTelnetConfig.getTelnetconfigips()%><br></td>
		        														<td align='center'><%=passWDBackupTelnetConfig.getBackup_date() %></td>
		        														<td align='center'><%=sb.toString()+"" %></td>
		        														<td align='center'><%=wayName %></td>
		        														<td align='center'><%="是".equals(status)?"启动":"未启动" %></td>
		        														<td align='center'>
																			<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('2','<%=passWDBackupTelnetConfig.getId() %>') alt="右键操作">
																		</td>
		        													</tr>
		        													<%
		        												}}
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
		        										<td>
		        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
									                  			<tr>
									                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
									                    			<td></td>
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
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
