<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.automation.model.CheckResult"%>
<%@page import="com.afunms.automation.dao.CheckResultDao"%>
<%@page import="com.afunms.automation.model.CompCheckResultModel"%>
<%@page import="com.afunms.automation.model.StrategyIp"%>
<%@page import="com.afunms.automation.dao.StrategyIpDao"%>
<%@ include file="/automation/common/globe.inc"%>
<%
	String rootPath = request.getContextPath();
	List<CheckResult> checkList = (List<CheckResult>) request
			.getAttribute("list");
	String id = (String) request.getAttribute("id");
	List ipList = (List) request.getAttribute("ipList");
	String common = "<img src='/"+rootPath+"/img/common.gif'>";
	String serious = "<img src='/"+rootPath+"/img/serious.gif'>";
	String urgency = "<img src='/"+rootPath+"/img/urgency.gif'>";
	String correct = "<img src='/"+rootPath+"/img/correct.gif'>";
	String disable = "<img src='/"+rootPath+"/img/blue.gif'>";
	String error = "<img src='/"+rootPath+"/img/error.gif'>";
	String statusImg = "<img src='/"+rootPath+"/img/correct.gif'>";
%>
<html>
	<head>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/automation/js/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />

		<LINK href="<%=rootPath%>/automation/css/style.css" type="text/css"
			rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>
		<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
		<link href="<%=rootPath%>/automation/css/detail.css" rel="stylesheet"
			type="text/css">
		<script language="JavaScript" type="text/javascript">
  function toAddCluster(){
     mainForm.action = "<%=rootPath%>/serverUpAndDown.do?action=delOrAdd&type=1";
     mainForm.submit();
  }
  function toAdd()
  {
    var url="<%=rootPath%>/configRule.do?action=ready_addip&id=<%=id%>";
	window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
  }
  function exeRule()
  {
     mainForm.action = "<%=rootPath%>/configRule.do?action=exeRule&id=<%=id%>";
     mainForm.submit();
     
  } 
  function viewDetail(id,ip)
  {
    var url="<%=rootPath%>/configRule.do?action=viewDetail&id="+id+"&ip="+ip;
	window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
  }  
   function showDetail(id,divId){

document.getElementById(id).innerHTML="<span  onclick=\"hiddenDetail(\'"+id+"\',\'"+divId+"\')\" >-&nbsp;</span>";
document.getElementById(divId).style.display = "block";

}
function hiddenDetail(id,divId){
document.getElementById(id).innerHTML="<span  onclick=\"showDetail(\'"+id+"\',\'"+divId+"\')\">&nbsp;+&nbsp;&nbsp;</span>";
document.getElementById(divId).style.display = "none";

}
function showRule(strategyId,groupId,ruleId,ip,isVor){
 var url="<%=rootPath%>/configRule.do?action=showRule&strategyId="+strategyId+"&groupId="+groupId+"&ruleId="+ruleId+"&ip="+ip+"&isVor="+isVor;
	CreateWindow(url);
}
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow", "height=450, width=750, toolbar= no, menubar=no, scrollbars=no, resizable=no, location=no, status=no,top=100,left=300")

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
	function detail()
	{
	    location.href="<%=rootPath%>/FTP.do?action=detail&id="+node;
	}
	function edit()
	{
		location.href="<%=rootPath%>/serverUpAndDown.do?action=ready_edit&id="+node;
	}
	function cancelmanage()
	{
		location.href="<%=rootPath%>/FTP.do?action=changeMonflag&value=0&id="+node;
	}
	
	function openNewWindow(id) {
	
		window.open("<%=rootPath%>/serverUpAndDown.do?action=ready_editName&id="+id,"_blank",
			"toolbar=no,titlebar=no, location=no,directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=yes,top=300,left=300 width=370, height=150")
	
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
	<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa"
		onload="initmenu();">

		<!-- 这里用来定义需要显示的右键菜单 -->
		<div id="itemMenu" style="display: none";>
			<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
				style="border: thin; font-size: 12px" cellspacing="0">
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.edit()">
						修改信息
					</td>
				</tr>
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.reboot()">
						重新启动
					</td>
				</tr>
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.shutdown()">
						关闭服务器
					</td>
				</tr>
			</table>
		</div>
		<!-- 右键菜单结束-->
		<form method="post" name="mainForm">
			<table border="0" id="table1" cellpadding="0" cellspacing="0"
				width=100%>
				<tr>
					
					<td align="center" valign=top>
						<table width="98%" cellpadding="0" cellspacing="0" algin="center">
							<tr>
								<td background="<%=rootPath%>/common/images/right_t_02.jpg"
									width="100%">
									<table width="100%" cellspacing="0" cellpadding="0">
										<tr>
											<td align="left">
												<img src="<%=rootPath%>/common/images/right_t_01.jpg"
													width="5" height="29" />
											</td>
											<td class="layout_title">
												应用 >> 合规性 >> 策略
											</td>
											<td align="right">
												<img src="<%=rootPath%>/common/images/right_t_03.jpg"
													width="5" height="29" />
											</td>
										</tr>
									</table>
								</td>
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="1">
										<tr>
											<td bgcolor="#ECECEC" width="100%" align='right'>

												<a href="#" onclick="toAdd()">关联设备</a>
												<a href="#" onclick="exeRule()">运行策略</a>
												<a href="#" onclick="javascript:history.back(1)">返回</a>
											</td>
										</tr>
									</table>
								</td>
							</tr>

							<tr>
								<td colspan="2">
									<table cellspacing="1" cellpadding="0" width="100%">
										<tr class="microsoftLook0" height=28>

											<th width='5%'>
												序号
											</th>

											<th width='20%'>
												IP地址
											</th>
											<th width='15%'>
												合规性状态
											</th>
											<th width='20%'>
												违反规则数
											</th>
											<th width='15%'>
												合规性规则数
											</th>
											<th width='30%'>
												上次检查时间
											</th>

										</tr>
										<%
											CheckResult vo = null;
											int count = 0;
											if (checkList != null && checkList.size() > 0) {
												for (int i = 0; i < checkList.size(); i++) {
													CheckResult result = checkList.get(i);
													String status = "合规";

													if (result.getCount0() > 0 || result.getCount1() > 0
															|| result.getCount2() > 0) {
														status = "违反";
														statusImg = "<img src='/"+rootPath+"/automation/images/error.gif'>";
													}
													CheckResultDao dao = new CheckResultDao();
													vo = dao.getExtraCount1(id, result.getIp());
													if (vo != null)
														count = vo.getExactCount();
										%>
										<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
											class="microsoftLook">

											<td height=25 align=center>
												<font color='blue'><%=1 + i%></font>
											</td>
											<td height=25 align=center>
											&nbsp;<span id="show<%=i%>"><span onclick="showDetail('show<%=i%>','group<%=i%>')">&nbsp;&nbsp;+&nbsp;&nbsp;</span> </span><%=result.getIp()%></td>
											<td height=25 align=center>
												&nbsp;<%=statusImg%>
												<%=status%></td>
											<td height=25 align=center>
												<%=common%>
												<%=result.getCount0()%>&nbsp;&nbsp;<%=serious%>
												<%=result.getCount1()%>&nbsp;&nbsp;<%=urgency%>
												<%=result.getCount2()%></td>

											<td height=25 align=center><%=correct%>
												<%=count%></td>
											<td height=25 align=center><%=result.getCheckTime()%></td>

										</tr>
										<tr bgcolor="#FFFFFF">
											<td colspan=6>
												<div id="group<%=i%>" style="display: none;">
													<table>
														<tr>
															<td nowrap align="center" height="25" width="20%">
																&nbsp;<%=error%>
																&nbsp;违反的规则
															</td>
															<td width="80%" align="left">
																&nbsp;&nbsp;
															</td>

														</tr>

														<%
															CheckResultDao resultDao2 = new CheckResultDao();
																	List list = resultDao2.getReslutByIdAndIp(id, result
																			.getIp());
																	if (list != null && list.size() > 0) {
																		boolean flag1 = true;
																		for (int k = 0; k < list.size(); k++) {
																			String level = "";
																			String levelStr = "";
																			CompCheckResultModel model = (CompCheckResultModel) list
																					.get(k);

																			if (model.getViolationSeverity() == 2) {
																				level = "<img src='/"+rootPath+"/automation/images/urgency.gif'>";
																				levelStr = "严重的";
																			} else if (model.getViolationSeverity() == 1) {
																				level = "<img src='/"+rootPath+"/automation/images/serious.gif'>";
																				levelStr = "重要的";
																			} else if (model.getViolationSeverity() == 0) {
																				level = "<img src='/"+rootPath+"/automation/images/common.gif'>";
																				levelStr = "普通的";
																			}
																			if (model.getIsViolation() == 0) {
														%>
														<tr>
															<td nowrap align="center" height="32" width="10%">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=level%><%=levelStr%></td>
															<td width="90%" height="32" align="left">
																&nbsp;&nbsp;
																<a href="#"
																	onclick="showRule('<%=model.getStrategyId()%>','<%=model.getGroupId()%>','<%=model.getRuleId()%>','<%=model.getIp()%>','0')"><%=model.getRuleName()%></a>
																[<%=model.getGroupName()%>]-
																<%=model.getDescription()%></td>

														</tr>
														<%
															} else if (model.getIsViolation() == 1) {
																				if (flag1) {
																					flag1 = false;
														%>
														<tr>
															<td nowrap align="center" height="32" width="20%">
																&nbsp;<%=correct%>&nbsp; 合规的规则
															</td>
															<td height="32" width="80%" align="left">
																&nbsp;&nbsp;
															</td>

														</tr>
														<%
															}
														%>
														<tr>
															<td nowrap align="center" height="32" width="10%">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=level%><%=levelStr%></td>
															<td height="32" width="90%" align="left">
																&nbsp;&nbsp;
																<a href="#"
																	onclick="showRule('<%=model.getStrategyId()%>','<%=model.getGroupId()%>','<%=model.getRuleId()%>','<%=model.getIp()%>','1')"><%=model.getRuleName()%></a>
																[<%=model.getGroupName()%>]-
																<%=model.getDescription()%></td>

														</tr>
														<%
															}
																		}
																	}
														%>
													</table>

												</div>
											</td>
										</tr>
										<%
											}

											}
											int j = 0;
											if (ipList != null && ipList.size() > 0) {
												int k = 0;
												if (checkList != null)
													k = checkList.size();
												for (int i = 0; i < ipList.size(); i++) {
													StrategyIp strategyIp = (StrategyIp) ipList.get(i);
													CheckResultDao dao = new CheckResultDao();
													vo = dao.getExtraCount(id, strategyIp.getIp());
													if (vo != null)
														count = vo.getExactCount();
													if (count > 0) {

														j = 1 + i + k;
										%>
										<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
											class="microsoftLook">

											<td height=25 align=center>
												&nbsp;
												<font color='blue'><%=j%></font>
											</td>
											<td height=25 align=center>&nbsp;<span id="show<%=j%>"><span onclick="showDetail('show<%=j%>','group<%=j%>')">&nbsp;&nbsp;+&nbsp;&nbsp;</span> </span><%=strategyIp.getIp()%></td>
											<td height=25 align=center>
												&nbsp;合规
											</td>
											<td height=25 align=center>
												<%=common %>0&nbsp;&nbsp;<%=serious %>0&nbsp;&nbsp;<%=urgency %>0
											</td>

											<td height=25 align=center><%=correct%>
												<%=count%></td>
											<td height=25 align=center><%=vo.getCheckTime()%></td>

										</tr>
										<tr bgcolor="#FFFFFF">
											<td colspan=6>
												<div id="group<%=j%>" style="display: none;">
													<table>

														<tr>
															<td nowrap align="center" height="32" width="20%">
																&nbsp;<%=correct%>&nbsp; 合规的规则
															</td>
															<td height="32" width="80%" align="left">
																&nbsp;&nbsp;
															</td>

														</tr>
														<%
															CheckResultDao resultDao2 = new CheckResultDao();
																		List list = resultDao2.getReslutByIdAndIp(id + "",
																				strategyIp.getIp());
																		if (list != null && list.size() > 0) {
																			boolean flag1 = true;
																			for (int k1 = 0; k1 < list.size(); k1++) {
																				String level = "";
																				String levelStr = "";
																				CompCheckResultModel model = (CompCheckResultModel) list
																						.get(k1);

																				if (model.getViolationSeverity() == 2) {
																					level = "<img src='/"+rootPath+"/automation/images/urgency.gif'>";
																					levelStr = "严重的";
																				} else if (model.getViolationSeverity() == 1) {
																					level = "<img src='/"+rootPath+"/automation/images/serious.gif'>";
																					levelStr = "重要的";
																				} else if (model.getViolationSeverity() == 0) {
																					level = "<img src='/"+rootPath+"/automation/images/common.gif'>";
																					levelStr = "普通的";
																				}
														%>
														<tr>
															<td nowrap align="center" height="32" width="10%">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=level%><%=levelStr%></td>
															<td nowrap height="32" width="90%" align="left">
																&nbsp;&nbsp;
																<a href="#" onclick="showRule('<%=model.getStrategyId()%>','<%=model.getGroupId()%>','<%=model.getRuleId()%>','<%=model.getIp()%>','1')"><%=model.getRuleName()%></a> [<%=model.getGroupName()%>]- <%=model.getDescription()%></td>

														</tr>
														<%
															}
																		}
														%>
													</table>

												</div>
											</td>
										</tr>
										<%
											}
												}

											}
											StrategyIpDao ipDao = new StrategyIpDao();
											List deviceList = ipDao
													.findByCondition(" where AVAILABILITY=0 and STRATEGY_ID="
															+ id);
											if (deviceList != null && deviceList.size() > 0) {

												int k = 0;
												for (int i = 0; i < deviceList.size(); i++) {
													StrategyIp vo1 = (StrategyIp) deviceList.get(i);
													if (checkList != null && j == 0)
														k = checkList.size();
										%>
										<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
											class="microsoftLook">

											<td height=25 align=center>

												<font color='blue'><%=1 + i + k + j%></font>
											</td>
											<td height=25 align=center><%=vo1.getIp()%></td>
											<td height=25 align=center>
												数据不可用
											</td>
											<td height=25 align=center>
												[无数据]
											</td>
											<td height=25 align=center>
												[无数据]
											</td>
											<td height=25 align=center>
												--NA--
											</td>

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
			</table>
	</BODY>
</HTML>
