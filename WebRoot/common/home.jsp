<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.flex.networkTopology.NetworkMonitor"%>
<%@page import="org.apache.commons.collections.functors.WhileClosure"%>
<%@page import="org.apache.commons.lang.SystemUtils"%>
<%@page import="org.apache.commons.net.DefaultDatagramSocketFactory"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.inform.util.SystemSnap"%>
<%@page import="com.afunms.topology.dao.ManageXmlDao"%>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.system.vo.EventVo"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.initialize.ResourceCenter"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.afunms.system.dao.FunctionDao"%>
<%@page import="com.afunms.system.model.Function"%>
<%@page import="com.afunms.home.role.dao.HomeRoleDao"%>
<%@page import="com.afunms.home.user.dao.HomeUserDao"%>
<%
	Hashtable smallHashtable = (Hashtable) request.getAttribute("smallHashtable");
	User uservo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	Hashtable bigHashtable = (Hashtable) request.getAttribute("bigHashtable");
	System.out.println(bigHashtable);
	int lastOne = 0;//是否最后一个元素
	Enumeration RLKey = smallHashtable.elements();
	while (RLKey.hasMoreElements()) {
		String accRole = RLKey.nextElement().toString();
		if (accRole.equals("1")) {
			lastOne++;
		}
	}
	System.out.println(com.afunms.common.util.CommonAppUtil.getSkinPath());
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List list = null;
	Date c1 = new Date();
	String timeFormat = "MM-dd HH:mm:ss";
	java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
	List networklist = (List) session.getAttribute("networklist");
	List hostlist = (List) session.getAttribute("hostlist");
	String hostsize = "0";
	if (hostlist != null && hostlist.size() > 0)
		hostsize = hostlist.size() + "";
	String dbsize = (String) session.getAttribute("dbsize");
	if (dbsize == null)
		dbsize = "0";
	String securesize = (String) session.getAttribute("securesize");
	if (securesize == null)
		securesize = "0";
	String storagesize = (String) session.getAttribute("storagesize");
	if (storagesize == null)
		storagesize = "0";
	String servicesize = (String) session.getAttribute("servicesize");
	if (servicesize == null)
		servicesize = "0";
	String midsize = (String) session.getAttribute("midsize");
	if (midsize == null)
		midsize = "0";
	String routesize = (String) session.getAttribute("routesize");
	if (routesize == null)
		routesize = "0";
	String switchsize = (String) session.getAttribute("switchsize");
	if (switchsize == null)
		switchsize = "0";
	ManageXmlDao dao = new ManageXmlDao();
	ManageXml vo = (ManageXml) dao.findByView("1", uservo.getBusinessids());
	dao.close();
	String topo_name = "物理根图";
	String home_topo_view = "network.jsp";
	String zoom = "1";
	if (vo != null) {
		topo_name = vo.getTopoTitle();
		home_topo_view = vo.getXmlName();
		zoom = vo.getPercent() + "";
	}
	session.setAttribute(SessionConstant.HOME_TOPO_VIEW, home_topo_view);
	ManageXmlDao mxdao = new ManageXmlDao();
	ManageXml mxvo = (ManageXml) mxdao.findByBusView("1", uservo.getBusinessids());

	//得到业务告警信息
	NetworkMonitor networkMonitor = new NetworkMonitor();
	Hashtable bussinessviewHash = networkMonitor.getBussinessviewHash();
%>

<%
	//默认选择该用户第一个所属业务作为跳转的treeBid

	//该所属业务中没有路由器，且该用户没有下一个所属业务，不跳转！
	User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	String[] bids = user.getBusinessids().split(",");
	String defaultbid = "";
	for (String bid : bids) {
		if (bid != null && !bid.equals("")) {
			defaultbid = bid;
			break;
		}
	}
	//该所属业务中没有路由器，且该用户下一个所属业务中存在路由器，将该业务id作为treeBid,跳转！
	String treeBidRouter = defaultbid;
	String treeBidHost = defaultbid;
	String treeBidSwitch = defaultbid;
	String treeBidDb = defaultbid;
	String treeBidMid = defaultbid;
	String treeBidService = defaultbid;
	String treeBidSecu = defaultbid;
	Hashtable treeBidHash = (Hashtable) request.getAttribute("treeBidHash");
	if (treeBidHash != null) {
		if (treeBidHash.containsKey("treeBidRouter")) {
			treeBidRouter = (String) treeBidHash.get("treeBidRouter");
		}
		if (treeBidHash.containsKey("treeBidHost")) {
			treeBidHost = (String) treeBidHash.get("treeBidHost");
		}
		if (treeBidHash.containsKey("treeBidSwitch")) {
			treeBidSwitch = (String) treeBidHash.get("treeBidSwitch");
		}
		if (treeBidHash.containsKey("treeBidDb")) {
			treeBidDb = (String) treeBidHash.get("treeBidDb");
		}
		if (treeBidHash.containsKey("treeBidMid")) {
			treeBidMid = (String) treeBidHash.get("treeBidMid");
		}
		if (treeBidHash.containsKey("treeBidService")) {
			treeBidService = (String) treeBidHash.get("treeBidService");
		}
		if (treeBidHash.containsKey("treeBidSecu")) {
			treeBidSecu = (String) treeBidHash.get("treeBidSecu");
		}
	}

	String routepath = "/perform.do?action=monitornodelist&flag=1&category=net_router&treeBid=" + treeBidRouter;
	String switchpath = "/perform.do?action=monitornodelist&flag=1&category=net_switch&treeBid=" + treeBidSwitch;
	String hostpath = "/perform.do?action=monitornodelist&flag=1&category=net_server&treeBid=" + treeBidHost;
	String dbpath = "/db.do?action=list&flag=1&treeBid=" + treeBidDb;
	String midpath = "/middleware.do?action=list&flag=1&category=middleware&treeBid=" + treeBidMid;
	String servicepath = "/service.do?action=list&flag=1&treeBid=" + treeBidService;
	String securepath = "/perform.do?action=monitornodelist&flag=1&category=safeequip&treeBid=" + treeBidSecu;

	//存储链接路(暂时给定路由器的链接)   无测试环境<Task未完成>
	String storagepath = "/perform.do?action=monitornodelist&flag=1&category=net_storage&treeBid=15";
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />

		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/xml/flush/amcolumn/swfobject.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
		<!-- GC -->
		<!-- LIBS -->
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
		<!-- ENDLIBS -->
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/examples/ux/ProgressBarPager.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/examples/ux/PanelResizer.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/examples/ux/PagingMemoryProxy.js"></script>
		<!-- EXT做的重点资源标签页home.js  css样式修改 -->
		<style type="text/css">
body {
	background: url(images/bg.jpg)
}

.x-tab-strip-top .x-tab-right {
	background-image: url("<%=rootPath%>/img/ext/tabs/tabs-sprite.gif");
}

.x-tab-strip-top .x-tab-left {
	background-image: url("<%=rootPath%>/img/ext/tabs/tabs-sprite.gif");
}

.x-tab-strip-top .x-tab-strip-inner {
	background-image: url("<%=rootPath%>/img/ext/tabs/tabs-sprite.gif");
}

.x-tab-panel-body {
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

.x-tab-panel-header {
	background-color: #EAEAEA;
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

.x-tab-panel-header-plain .x-tab-strip-spacer {
	background-color: #EAEAEA;
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

.x-panel {
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

.x-panel-body {
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

UL.x-tab-strip-top {
	background-color: #EAEAEA;
}
</style>
		<script type="text/javascript" src="<%=rootPath%>/js/home.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/eventList.js"></script>
		<script type="text/javascript">window.setInterval(panelevent_var('<%=rootPath%>'),1000);</script>

		<script type="text/javascript">
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

		<script type="text/javascript">
		
		function hideMenu(){
			var element = document.getElementById("container-menu-bar").parentElement;
			var display = element.style.display;
			if(display == "inline"){
				hideMenuBar();
			}else{
				showMenuBar();
			}
			//刷新tabpanel的宽度
			var tr_width = Ext.get("keybusiness_tr").getWidth()-1;
			Ext.get('tab_list_tr').setWidth(tr_width);
			Ext.get('tab_list').setWidth(tr_width);
			Ext.get('devicexn').setWidth(tr_width);
		}
		
		function showMenuBar(){
			var element = document.getElementById("container-menu-bar").parentElement;
			element.style.display = "inline";
		}
		
		function hideMenuBar(){
			var element = document.getElementById("container-menu-bar").parentElement;
			element.style.display = "none";
		}
		var fatherXML = "<%=home_topo_view%>";
		function redirectUrl(){
			if(fatherXML=="network.jsp"){
			    window.open('<%=rootPath%>/topology/network/index.jsp','window', 'toolbar=no,height=screen.height,width=screen.width,scrollbars=no,center=yes,resizable=yes');
		} else {
		    window.open('<%=rootPath%>/topology/submap/index.jsp?submapXml='+fatherXML,'window', 'toolbar=no,height=screen.height,width=screen.width,scrollbars=no,center=yes,resizable=yes');
			}
		}
	</script>

	</head>
	<body id="body" class="body" onLoad="parent.topFrame.location.reload();initmenu();hideMenuBar();">

		<!-- 定义一个空div -->
		<span id="rootpath" value="<%=rootPath%>"></span>
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">

				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content" style="width: 99%" border="0">
										<tr>
											<td>
												<table id="content-body" class="content-body" style="border-left: #737272 0px solid; border-right: #737272 0px solid;">
													<tr>
														<td>
															<!-- 第一块start -->
															<table>
																<tr>
																	<%
																		int count = 0;
																		//第一块显示开始 
																		if (smallHashtable.get("设备快照").equals(1)) {
																			count++;
																	%>
																	<!-- 设备快照 -->
																	<td width=50% align='center' height="350">
																		<table width=100% height="100%" border="0" align='center' style="background-image:url('<%=rootPath %>/resource/image/global/bg6.jpg')">
																			<tr valign="top">
																				<td>
																					<table id="content-header" class="content-header">
																						<tr>
																							<td class="content-title" align='center' height="29" style="text-align: center;">
																								<b>设备快照</b>
																							</td>
																						<tr>
																					</table>
																				</td>
																			</tr>
																			<tr>
																				<td width="100%">
																					<table width="100%" >
																						<tr>
																							<td align="center">
																								<img src="<%=rootPath%>/resource/<%=NodeHelper.getSnapStatusImage(SystemSnap.getRouterStatus(user.getBusinessids()), 1)%>">
																								<br>
																								<a href="#" onClick="showTree('<%=routepath%>');">路由器(<%=routesize%>)</a>
																							</td>
																							<td align="center">
																								<img src="<%=rootPath%>/resource/<%=NodeHelper.getSnapStatusImage(SystemSnap.getSwitchStatus(user.getBusinessids()), 2)%>">
																								<br>
																								<a href="#" onClick="showTree('<%=switchpath%>');">交换机(<%=switchsize%>)</a>
																							</td>
																							<td align="center">
																								<img src="<%=rootPath%>/resource/<%=NodeHelper.getSnapStatusImage(SystemSnap.getServerStatus(user.getBusinessids()), 3)%>">
																								<br>
																								<a href="#" onClick="showTree('<%=hostpath%>');">服务器(<%=hostsize%>)</a>
																							</td>
																							<td align="center">
																								<img src="<%=rootPath%>/resource/<%=NodeHelper.getSnapStatusImage(SystemSnap.getDbStatus(user.getBusinessids()), 4)%>">
																								<br>
																								<a href="#" onClick="showTree('<%=dbpath%>');">数据库(<%=dbsize%>)</a>
																							</td>
																						</tr>
																						<tr>
																							<td>
																								<div style="height: 50px;"></div>
																							</td>
																						</tr>
																						<tr>
																							<td align="center">
																								<img src="<%=rootPath%>/resource/<%=NodeHelper.getSnapStatusImage(SystemSnap.getMiddleStatus(user.getBusinessids()), 5)%>">
																								<br>
																								<a href="#" onClick="showTree('<%=midpath%>');">中间件(<%=midsize%>)</a>
																							</td>
																							<td align="center">
																								<img src="<%=rootPath%>/resource/<%=NodeHelper.getSnapStatusImage(SystemSnap.getServiceStatus(user.getBusinessids()), 6)%>">
																								<br>
																								<!--<img src="<%=rootPath%>/resource/image/service.gif" ><br>-->
																								<a href="#" onClick="showTree('<%=servicepath%>');">服务(<%=servicesize%>)</a>
																							</td>
																							<td align="center">
																								<img src="<%=rootPath%>/resource/<%=NodeHelper.getSnapStatusImage(SystemSnap.getFirewallStatus(user.getBusinessids()), 7)%>">
																								<!--<img src="<%=rootPath%>/resource/image/topo/firewall/firewall.gif">-->
																								<br>
																								<a href="#" onClick="showTree('<%=securepath%>');">安全(<%=securesize%>)</a>
																							</td>
																							<td align="center">
																								<img src="<%=rootPath%>/resource/<%=NodeHelper.getSnapStatusImage(SystemSnap.getStorageStatus(user.getBusinessids()), 14)%>">
																								<br>
																								<a href="#" onClick="showTree('<%=storagepath%>');">存储(<%=storagesize%>)</a>
																							</td>
																						</tr>
																						<tr>
																							<td>
																								<div style="height: 20px;"></div>
																							</td>
																						</tr>
																					</table>
																				<td>
																			</tr>
																			<tr valign="bottom">
																				<td>

																				</td>
																			</tr>
																		</table>
																	</td>
																	<%
																		//是否添加列空白
																			if (count % 2 == 1) {
																	%>
																	<td style="background: #bec7ce;">
																		&nbsp;
																	</td>
																	<%
																		if (count == lastOne) {
																					System.out.println(count);
																	%>
																	<td width=50% align='center' height="270">
																	</td>
																	<%
																		}
																			}
																	%>
																	<%
																		//是否换新行开始 
																			if (count % 2 == 0 || count == lastOne) {
																	%>
																</tr>
															</table>
															<!-- 第一块end -->
														</td>
													</tr>
													<tr style="height: 7px; background: #bec7ce;">
														<td>
															<div></div>
														</td>
													</tr>
													<tr>
														<td>
															<!-- 第二块start -->
															<table>
																<tr>
																	<%
																		}//是否换新航结束
																		}
																	%>
																	<%
																		if (smallHashtable.get("网络拓扑图").equals(1)) {
																			count++;
																	%>
																	<!-- 网络拓扑图  -->
																	<td width=50% align='center' height="350">
																		<table width=100% height="100%" border="0" align='center'>
																			<tr>
																				<td align='center' height="24">
																					<table id="content-header" class="content-header">
																						<tr>
																							<td align='center' height="29" class="content-title" style="text-align: center;">
																								<b>网络拓扑图-<%=topo_name%></b>
																							</td>
																						<tr>
																					</table>
																				</td>
																			</tr>
																			<tr>
																				<td align='center' >
																					<iframe name="topo_Frame" src="<%=rootPath%>/topology/network/h_showMap.jsp?filename=networkData.jsp&zoom=<%=zoom%>" width="99%" height="99%" scrolling="No" frameborder="0"  noresize></iframe>
																				</td>
																			</tr>
																			<tr valign="bottom" style="height: 4px;">
																				<td>
																					<table id="content-footer" class="content-footer">
																						<tr>
																							<td>

																							</td>
																						</tr>
																					</table>
																				</td>
																			</tr>
																		</table>
																	</td>
																	<%
																		//是否添加列空白
																			if (count % 2 == 1) {
																	%>
																	<td style="background: #bec7ce;">
																		&nbsp;
																	</td>
																	<%
																		if (count == lastOne) {
																					System.out.println(count);
																	%>
																	<td width=50% align='center' height="270">
																	</td>
																	<%
																		}
																			}
																	%>
																	<%
																		//是否换新行开始 
																			if (count % 2 == 0 || count == lastOne) {
																	%>
																</tr>
															</table>
															<!-- 第二块end -->
														</td>
													</tr>
													<tr style="height: 7px; background: #bec7ce;">
														<td>
															<div></div>
														</td>
													</tr>
													<tr>
														<td>
															<!-- 第三块start -->
															<table>
																<tr>
																	<%
																		}//是否换新航结束
																		}
																	%>
																	<%
																		if (smallHashtable.get("设备性能").equals(1)) {
																			count++;
																	%>
																	<!-- 设备性能 -->
																	<td width=50% align='center' valign="top" height="460">
																		<table width=100% height="100%" border="0" align='center' style="background-image:url('/afunms/resource/image/global/bg6.jpg')">
																			<tr>
																				<td id='devicexn_title' align='center' height="24">
																					<table id="content-header" class="content-header">
																						<tr>
																							<td align='center' height="29" class="content-title" style="text-align: center;">
																								<b>性能</b>
																							</td>
																						<tr>
																					</table>
																				</td>
																			</tr>
																			<tr id="tab_list_tr" valign="top">
																				<td>
																					<!-- EXT的做的TAB 详见home.js文件 -->
																					<!-- 加重点资源Tab页 -->
																					<div id="tab_list" style="width: 100%;"></div>
																				</td>
																			</tr>
																			<tr valign="bottom">
																				<td>

																				</td>
																			</tr>
																		</table>
																	</td>

																	<%
																		//是否添加列空白
																			if (count % 2 == 1) {
																	%>
																	<td style="background: #bec7ce;" valign="top">
																		&nbsp;
																	</td>
																	<%
																		if (count == lastOne) {
																					System.out.println(count);
																	%>
																	<td width=50% align='center' height="270" valign="top">
																	</td>
																	<%
																		}
																			}
																	%>
																	<%
																		//是否换新行开始 
																			if (count % 2 == 0 || count == lastOne) {
																	%>
																</tr>
															</table>
															<!-- 第三块end -->
														</td>
													</tr>
													<tr style="height: 7px; background: #bec7ce;">
														<td>
															<div></div>
														</td>
													</tr>
													<tr>
														<td>

															<!-- 第四块start -->
															<table>
																<tr>
																	<%
																		}//是否换新航结束
																		}
																	%>
																	<%
																		//第二块显示开始
																		if (smallHashtable.get("关键业务").equals(1)) {
																			count++;
																	%>
																	<!-- 关键业务 -->
																	<td width=50% align='center' valign="top" height="460">
																		<table width=100% height="100%" border="0" align='center' style="background-image:url('/afunms/resource/image/global/bg6.jpg')">
																			<tr>
																				<td id='devicexn_title' align='center' height="24">
																					<table id="content-header" class="content-header">
																						<tr>
																							<td align='center' height="29" class="content-title" style="text-align: center;">
																								<b>最新告警</b>
																							</td>
																						<tr>
																					</table>
																				</td>
																			</tr>
																			<tr id="tab_list_tr" valign="top">
																				<td>

																					<div id="event_list" style="width: 100%;"></div>
																				</td>
																			</tr>
																			<tr valign="bottom">
																				     <td>

																				     </td>
																			</tr>

																		</table>
																	</td>
																	<%
																		//是否添加列空白
																			if (count % 2 == 1) {
																	%>
																	<td style="background: #bec7ce;" valign="top">
																		&nbsp;
																	</td>
																	<%
																		if (count == lastOne) {
																					System.out.println(count);
																	%>
																	<td width=50% align='center' height="270" valign="top">
																	</td>
																	<%
																		}
																			}
																	%>
																	<%
																		//是否换新行开始 
																			if (count % 2 == 0 || count == lastOne) {
																	%>
																</tr>
															</table>
															<!-- 第四块end -->

														</td>
													</tr>
													<tr style="height: 7px; background: #bec7ce;">
														<td>
															<div></div>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
																	<%
																		}//是否换新航结束
																		}
																	%>
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
		<script type="text/javascript">
			//Ext.onReady(tabpanel_var('<%=rootPath%>'));
			Ext.onReady(function(){panelevent_var('<%=rootPath%>');tabpanel_var('<%=rootPath%>')});
			
			
			//构建一个JavaScript的replaceAll方法  （该方法个别客户机浏览器不支持）
			//String.prototype.replaceAll  = function(s1,s2){    
			//  	return this.replace(new RegExp(s1,"gm"),s2);   
			//} 
			
			/**
			*跳转到性能页面
			*/
			function showTree(rightFramePath){
				//将等于和and转换一下  
				//rightFramePath = rightFramePath.replaceAll("&","-and-");
				//rightFramePath = rightFramePath.replaceAll("=","-equals-");
				//使用循环，将等于和and转换一下  
				while(rightFramePath.indexOf("&") != -1){
					rightFramePath = rightFramePath.replace("&","-and-");
				}
				while(rightFramePath.indexOf("=") != -1){
					rightFramePath = rightFramePath.replace("=","-equals-");
				}
				window.location.href =  "<%=rootPath%>/performance/index.jsp?flag=1&rightFramePath="+rightFramePath;
			}
		</script>
	</body>
</html>
