<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.automation.model.CheckResult"%>
<%@page import="com.afunms.automation.dao.CheckResultDao"%>
<%@page import="com.afunms.automation.model.CompCheckResultModel"%>
<%@page import="com.afunms.automation.model.StrategyIp"%>
<%@ include file="/automation/common/globe.inc"%>
<%@ include file="/automation/common/globeChinese.inc"%>
<%@page import="java.util.*"%>

<%

	String rootPath = request.getContextPath();
		List<CheckResult> checkList = (List<CheckResult>) request.getAttribute("list");
	List deviceList = (List) request.getAttribute("deviceList");
	String data = "";
	int wrongCount = 0;
	int exactCount = 0;
	int disabledCount = 0;
	if (deviceList != null && deviceList.size() > 0)
		disabledCount = deviceList.size();
	CheckResultDao resultDao = new CheckResultDao();
	List resultList = resultDao.getExtraCountList();
	if (resultList != null)
		exactCount = resultList.size();
	if (checkList != null && checkList.size() > 0) {

		for (int i = 0; i < checkList.size(); i++) {
			CheckResult result = checkList.get(i);
			if (result.getCount0() > 0 || result.getCount1() > 0
					|| result.getCount2() > 0) {
				wrongCount++;
			} else {
				exactCount++;
			}
		}
		
	}
	data = "<pie><slice title='违反'>" + wrongCount
				+ "</slice><slice title='顺从'>" + exactCount
				+ "</slice><slice title='不可用'>" + disabledCount
				+ "</slice></pie>";
	
String common="<img src='/afunms/img/common.gif'>";				
String correct="<img src='/afunms/img/correct.gif'>";				
String serious="<img src='/afunms/img/serious.gif'>";	
String urgency="<img src='/afunms/img/urgency.gif'>";	
String statusImg="<img src='/afunms/img/correct.gif'>";
String disable="<img src='/afunms/img/blue.gif'>";
String error="<img src='/afunms/img/error.gif'>";			
%>
<html>
	<head>


		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/swfobject.js"></script>
		<script  type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/automation/css/ext/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-lang-zh_CN.js" charset="utf-8"></script>

        <script type="text/javascript" src="<%=rootPath%>/automation/js/jquery-1.8.2.min.js"></script>
<script language="JavaScript" type="text/javascript">
 function viewDetail(id,ip)
  {
    var url="<%=rootPath%>/configRule.do?action=viewDetail&id="+id+"&ip="+ip;
	window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
  }  
  function setStrategy(){
  location.href="<%=rootPath%>/configRule.do?action=strategyList"
  } 
  function setGroup(){
  location.href="<%=rootPath%>//configRule.do?action=groupRuleList"
  } 
  function setRule(){
  location.href="<%=rootPath%>/configRule.do?action=ruleDetailList"
  } 
  function allDeviceReport(){
  location.href="<%=rootPath%>/configRule.do?action=allDeviceReport"
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
	</head>
	
<body id="body">
		<!-- 右键菜单结束-->
		<form id="mainForm" method="post" name="mainForm">
			<input type=hidden name="orderflag">
			<input type=hidden name="id">

			<table id="body-container" class="body-container">
				<tr>
					
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td >
									<table id="container-main-detail" class="container-main-detail">
										<tr>
								
								            <td >
									           <table id="detail-content" class="detail-content">
	<tr>
		<td> 
			 <table width="100%" background="<%=rootPath%>/common/images/right_t_02.jpg" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
					<td class="layout_title"><b>自动化>> 配置文件管理>> 合规性管理 >> 所有策略</b></td>
					<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table id="detail-content-body" class="detail-content-body">
				<tr>
					<td> 
						<table  cellpadding=0 width=100% align=center algin="center">
							<tr>
								<td>
								<table id="container-main" class="container-main">

							<tr>
								
								<td align="center" valign=top>
									<table width="98%" cellpadding="0" cellspacing="0"
										algin="center">
									
										<tr bgcolor="#FFFFFF">
											<td>
												<table width="100%" cellpadding="0" cellspacing="1">
													<tr>

														<td bgcolor="#FFFFFF" width="100%" align=center>
															<table align=center border=1 width="100%">
																<tr>
																	<td width="40%" align=center>
																		<table>
																			<tr>
																				<td height="36" align=center>
																					所有策略的设备：&nbsp;&nbsp;<%=exactCount + wrongCount + disabledCount%></td>
																			</tr>
																			<tr>
																				<td height="36" align=center>
																					合规策略的设备：&nbsp;&nbsp;<%=exactCount%></td>
																			</tr>
																			<tr>
																				<td height="36" align=center>
																					违反策略的设备：&nbsp;&nbsp;<%=wrongCount%></td>
																			</tr>
																			<tr>
																				<td height="36" align=center>
																					数据不可用设备：&nbsp;&nbsp;<%=disabledCount%></td>
																			</tr>
																		</table>

																	</td>

																	<td width="60%" align=center>
																		<div id="strategyPie" align=center>
																			
																		</div>
																		<script type="text/javascript"
																			src="<%=rootPath%>/include/swfobject.js"></script>
																		<script type="text/javascript">
													<% if(wrongCount==0&&exactCount==0&&disabledCount==0){%>
													 var _div=document.getElementById("strategyPie");
														  var img=document.createElement("img");
																img.setAttribute("src","<%=rootPath%>/resource/image/nodata.gif");
																_div.appendChild(img);
						                            
						                             <%}else{%>
						                                 var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf",  "ampie", "320", "250", "8", "#FFFFFF");
						                                 so.addVariable("path", "<%=rootPath%>/amchart/");
						                                 so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/strategyPie.xml"));
						                                 so.addVariable("chart_data","<%=data%>");
						                                 so.write("strategyPie"); 
														<%}%>
						                         </script>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
												<td valign=top >
												
												<table class="tool-bar-body-list" border=1>
												
												     <tr>
												         <td > 
												               
												 <ul>
													<li >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>快捷菜单</b></li>
													
													<li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=rootPath%>/resource/image/menu/celue.gif">&nbsp;<a href="#" onClick="setStrategy()">策略设置</a></li>
													<li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=rootPath%>/resource/image/menu/bat.gif">&nbsp;<a href="#" onClick="setGroup()">规则组设置</a></li>
													<li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=rootPath%>/resource/image/menu/sbmbbj.gif">&nbsp;<a href="#" onClick='setRule()'>规则设置</a></li>
	               									<li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=rootPath%>/resource/image/menu/ywfl.gif">&nbsp;<a href="#" onClick='allDeviceReport()'>导出报表</a></li>
	               								<!--  	<li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="<%=rootPath%>/resource/image/toolbar/sbmb.gif">&nbsp;<a href="#" onClick=''>运行策略</a></li>-->
               										
												 </ul>
												       </td>
												     </tr>
												       </table>
												 
											</td>
										</tr>
										<tr>
											<td colspan="2">
												<table cellspacing="1" cellpadding="0" width="100%">
													<tr class="microsoftLook0" height=28>

														<td width='5%'class="detail-data-body-title">
															序号
														</td>

														<td width='15%'class="detail-data-body-title">
															IP地址
														</td>
														<td width='10%'class="detail-data-body-title">
															合规性状态
														</td>
														<td width='15%'class="detail-data-body-title">
															违反规则数
														</td>
														<td width='10%'class="detail-data-body-title">
															合规性规则数
														</td>
														<td width='17%'class="detail-data-body-title">
															所属策略
														</td>
														<td width='18%'class="detail-data-body-title">
															上次检查时间
														</td>

													</tr>
													<%
														int count = 0;
														if (checkList != null && checkList.size() > 0) {
															for (int i = 0; i < checkList.size(); i++) {
																CheckResult result = checkList.get(i);
																String status = "合 规";
																if (result.getCount0() > 0 || result.getCount1() > 0
																		|| result.getCount2() > 0){
																	status = "违 反";
																	statusImg="<img src='/afunms/img/error.gif'>";	
																	}
																CheckResultDao dao = new CheckResultDao();

																CheckResult vo = dao.getExtraCount1(result.getId() + "",
																		result.getIp());
																if (vo != null)
																	count = vo.getExactCount();
																	
													%>
													<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
														class="microsoftLook">

														<td height=25 align=center class="detail-data-body-list" >
															<font color='blue'><%=1 + i%></font>
														</td>
														<td height=25 align=center class="detail-data-body-list" >
															<span id="show<%=i %>"><span  onclick="showDetail('show<%=i %>','group<%=i %>')" >&nbsp;&nbsp;+&nbsp;&nbsp;</span></span><%=result.getIp()%>
														</td>
														<td height=25 align=center class="detail-data-body-list" ><%=statusImg%><%=status%></td>
														<td height=25 align=center class="detail-data-body-list" ><%=common%> <%=result.getCount0()%>&nbsp;&nbsp;<%=serious%> <%=result.getCount1()%>&nbsp;&nbsp;<%=urgency%> <%=result.getCount2()%></td>

														<td height=25 align=center class="detail-data-body-list" ><%=correct %> <%=count%></td>
														<td height=25 align=center class="detail-data-body-list" ><%=result.getName()%></td>
														<td height=25 align=center class="detail-data-body-list" ><%=result.getCheckTime()%></td>

													</tr>
													<tr>
													     <td colspan=7>
															<div id="group<%=i %>" style="display:none;">
															     <table>
															             <tr>
																		    <td nowrap align="center" height="25" width="20%" >&nbsp;<%=error %> &nbsp;违反的规则</td>
																			<td width="80%" align="left">&nbsp;&nbsp;</td>
																		    
																		</tr>
																		
																		<%
																		CheckResultDao resultDao2=new CheckResultDao();
	                                                                     List list=resultDao2.getReslutByIdAndIp(result.getId()+"",result.getIp());
	                                                                     if(list!=null&&list.size()>0){
	                                                                     boolean flag1=true;
		        												           for(int k=0;k<list.size();k++){
		        												           String level="";
		        												           String levelStr="";
		        												           CompCheckResultModel model=(CompCheckResultModel)list.get(k);
		        												           
		        												           if(model.getViolationSeverity()==2){
		        												           level="<img src='/afunms/img/urgency.gif'>";
		        												           levelStr="严重的";
		        												           }else if(model.getViolationSeverity()==1){
		        												            level="<img src='/afunms/img/serious.gif'>";
		        												            levelStr="重要的";
		        												           }else if(model.getViolationSeverity()==0){
		        												            level="<img src='/afunms/img/common.gif'>";
		        												            levelStr="普通的";
		        												           }
		        												           if(model.getIsViolation()==0){
		        												          
																		 %>
																		 <tr>
																		    <td nowrap align="center" height="32" width="10%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=level %><%=levelStr %></td>
																			<td width="90%" height="32" align="left">&nbsp;&nbsp;<a href="#" onclick="showRule('<%=model.getStrategyId() %>','<%=model.getGroupId() %>','<%=model.getRuleId() %>','<%=model.getIp() %>','0')"><%=model.getRuleName() %></a>  [<%=model.getGroupName() %>]- <%=model.getDescription() %></td>
																		    
																		</tr>
																		<%
																		
																		}else if(model.getIsViolation()==1){
																		if(flag1){
																		flag1=false;
																		 %>
																		 <tr>
																		    <td nowrap align="center" height="32" width="20%">&nbsp;<%=correct %>&nbsp; 合规的规则</td>
																			<td height="32" width="80%" align="left">&nbsp;&nbsp;</td>
																		    
																		</tr>
																		<%
																		  }
																		 %>
																		 <tr>
																		    <td nowrap align="center" height="32" width="10%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=level %><%=levelStr %></td>
																			<td  height="32" width="90%" align="left">&nbsp;&nbsp;<a href="#" onclick="showRule('<%=model.getStrategyId() %>','<%=model.getGroupId() %>','<%=model.getRuleId() %>','<%=model.getIp() %>','1')"><%=model.getRuleName() %></a> [<%=model.getGroupName() %>]- <%=model.getDescription() %></td>
																		    
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
														int j=0;
														if(resultList!=null&&resultList.size()>0){
														int z=0;
														if(checkList!=null&&checkList.size()>0)
														z=checkList.size();
														j=z+resultList.size();
															for (int i = 0; i < resultList.size(); i++) {
																CheckResult result = (CheckResult)resultList.get(i);
																String status = "合 规";
												z++;
													%>
													<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
														class="microsoftLook">

														<td height=25 align=center class="detail-data-body-list">
															<font color='blue'><%=z%></font>
														</td>
														<td height=25 align=center class="detail-data-body-list">
															<span id="show<%=z %>"><span  onclick="showDetail('show<%=z %>','group<%=z %>')" >&nbsp;&nbsp;+&nbsp;&nbsp;</span></span><%=result.getIp()%>
														</td>
														<td height=25 align=center class="detail-data-body-list">
															<%=correct%><%=status%></td>
														<td height=25 align=center class="detail-data-body-list">
															<%=common%> 0&nbsp;&nbsp;<%=serious%> 0&nbsp;&nbsp;<%=urgency%> 0</td>

														<td height=25 align=center class="detail-data-body-list"><%=correct %> <%=result.getExactCount()%></td>
														<td height=25 align=center class="detail-data-body-list"><%=result.getName()%></td>
														<td height=25 align=center class="detail-data-body-list"><%=result.getCheckTime()%></td>

													</tr>
													<tr>
													     <td colspan=7>
															<div id="group<%=z %>" style="display:none;">
															     <table>
															             
																		 <tr>
																		    <td nowrap align="center" height="32" width="20%">&nbsp;<%=correct %>&nbsp; 合规的规则</td>
																			<td height="32" width="80%" align="left">&nbsp;&nbsp;</td>
																		    
																		</tr>
																		<%
																		CheckResultDao resultDao2=new CheckResultDao();
	                                                                     List list=resultDao2.getReslutByIdAndIp(result.getId()+"",result.getIp());
	                                                                     if(list!=null&&list.size()>0){
	                                                                     boolean flag1=true;
		        												           for(int k=0;k<list.size();k++){
		        												           String level="";
		        												           String levelStr="";
		        												           CompCheckResultModel model=(CompCheckResultModel)list.get(k);
		        												           
		        												           if(model.getViolationSeverity()==2){
		        												           level="<img src='/afunms/img/urgency.gif'>";
		        												           levelStr="严重的";
		        												           }else if(model.getViolationSeverity()==1){
		        												            level="<img src='/afunms/img/serious.gif'>";
		        												            levelStr="重要的";
		        												           }else if(model.getViolationSeverity()==0){
		        												            level="<img src='/afunms/img/common.gif'>";
		        												            levelStr="普通的";
		        												           }
																		
																		 %>
																		 <tr>
																		    <td nowrap align="center" height="32" width="10%">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=level %><%=levelStr %></td>
																			<td  height="32" width="90%" align="left">&nbsp;&nbsp;<a href="#" onclick="showRule('<%=model.getStrategyId() %>','<%=model.getGroupId() %>','<%=model.getRuleId() %>','<%=model.getIp() %>','1')"><%=model.getRuleName() %></a> [<%=model.getGroupName() %>]- <%=model.getDescription() %></td>
																		    
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
														

														if (deviceList != null && deviceList.size() > 0) {
															int k = 0;
															for (int i = 0; i < deviceList.size(); i++) {
																StrategyIp vo = (StrategyIp) deviceList.get(i);
																if (checkList != null&&j==0)
																	k = checkList.size();
													%>
													<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
														class="microsoftLook">

														<td height=25 align=center class="detail-data-body-list">
															<font color='blue'><%=1 + i + k+j%></font>
														</td>
														<td height=25 align=center class="detail-data-body-list">&nbsp;&nbsp;&nbsp;&nbsp;<%=vo.getIp()%></td>
														<td height=25 align=center class="detail-data-body-list">
															<%=disable %>不可用
														</td>
														<td height=25 align=center class="detail-data-body-list">
															[无数据]
														</td>
														<td height=25 align=center class="detail-data-body-list">[无数据]</td>
														<td height=25 align=center class="detail-data-body-list"><%=vo.getStrategyName()%></td>
														<td height=25 align=center class="detail-data-body-list">
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
								</td>
								
							</tr>
						</table>
					</td>
				</tr>
			</table>

		</form>

	</body>
</HTML>
