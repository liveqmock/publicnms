<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.config.model.Business"%>
<%@page import="com.afunms.event.model.EventList"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String)request.getAttribute("menuTable");
	List list = (List)request.getAttribute("list");
	List businessList = (List)request.getAttribute("businessList");
	String subtype = (String)request.getAttribute("subtype");
	System.out.println("=========1111111111111==="+subtype);
	String ostype = (String)request.getAttribute("ostype");
	String subentity = (String)request.getAttribute("subentity");
	String level1 = (String)request.getAttribute("level1");
	String managesign = (String)request.getAttribute("managesign");
	String businessid = (String)request.getAttribute("businessid");
	Hashtable hashtable = (Hashtable)request.getAttribute("hashtable");
	String startdate = (String)request.getAttribute("startdate");
	String todate = (String)request.getAttribute("todate");
	JspPage jp = (JspPage)request.getAttribute("page");
	String ipaddress = (String)request.getAttribute("ipaddress");
%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<LINK href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
	<script type="text/javascript" src="<%=rootPath%>/js/dwr/engine.js"></script>
        <script type="text/javascript" src="<%=rootPath%>/js/dwr/util.js"></script>
        <script type="text/javascript" src="<%=rootPath%>/dwr/interface/CommonHelper.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 

<script language="javascript">	
	var curpage= <%=jp.getCurrentPage()%>;
  	var totalpages = <%=jp.getPageTotal()%>;
  	var delAction = "<%=rootPath%>/event.do?action=delete";
  	var listAction = "<%=rootPath%>/event.do?action=summary"; 
 

function accEvent(eventid){
	mainForm.eventid.value=eventid;
	mainForm.action="<%=rootPath%>/event.do?action=accit";	
	mainForm.submit();
}

  function query()
  {  
     mainForm.action = "<%=rootPath%>/event.do?action=summary&jp=1";
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
  	document.getElementById("level1").value=level1;
  	document.getElementById("managesign").value="<%=managesign%>";
  	document.getElementById("subentity").value=subentity;
  	 	
  	mainForm.action = "<%=rootPath%>/event.do?action=showDetails&jp=1&nodeId="+nodeId;
     mainForm.submit();
  }
   function deleteDetails(nodeId , subentity , subtype , level1){
   if(confirm('确定是否删除告警信息？')) {
  	document.getElementById("subtype").value=subtype;
  	
  	document.getElementById("level1").value=level1;
  	document.getElementById("businessid").value="<%=businessid%>";
  	document.getElementById("level1").value=level1;
  	document.getElementById("managesign").value="<%=managesign%>";
  	document.getElementById("subentity").value=subentity;
  	
  	mainForm.action = "<%=rootPath%>/event.do?action=deleteDetails&jp=1&nodeId="+nodeId;
     mainForm.submit();
     }
  }
 
  function showNodeDetails(nodeId){
  	mainForm.action = "<%=rootPath%>/detail/dispatcher.jsp?id=" + nodeId+"&flag=1&fromtopo=true";
     mainForm.submit();
  }
  
  
  
  
 

 
  
    function doDelete()
  {  
   if(confirm('是否确定删除这条记录?')) {
     mainForm.action = "<%=rootPath%>/event.do?action=dodelete";
     mainForm.submit();
     }
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
function clears(){
  
 if(confirm('确定是否清空告警信息？')) {
     mainForm.action = "<%=rootPath%>/event.do?action=clear";
     mainForm.submit();
     }
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
  	document.getElementById("ostype").value="<%=ostype%>";
  	document.getElementById("subentity").value="<%=subentity%>";
  	
  }
    //wxy add begin
function selectedEntity(){
var objSelect=document.getElementById("ostype");
for(var i=0;i<objSelect.options.length;i++)
		    {
			     if(objSelect.options[i].value =="<%=ostype%>")
		         {
			      
			           objSelect.options[i].selected = true;
			             break;
			        }
			   }  
}
function getsubEntityList(){
			       dwr.util.removeAllOptions("ostype");
			        dwr.util.removeAllOptions("subentity");
			         
                   var subtype=document.getElementById("subtype").value;
				    DWREngine.setAsync(false);//设置同步
				    CommonHelper.getTypeList(subtype,{
						      						callback:callback,//回调函数
						      						timeout:5000,//超时时间
						      						errorHandler:function(message) { 
						      						alert("ERROR: " + message);
						      						}
					      						}); 

			}
			function callback(object){
			 //  var arrayObj = new Array();
			 //  arrayObj[0]=['不限']; 
			//   for(var i=0;i<object.length;i++){
			 //   arrayObj[i+1]=[object[i]];
			 //    }
			 dwr.util.addOptions("subentity", ["不限"],{escapeHtml:false});
			 dwr.util.addOptions("ostype", object,{escapeHtml:false}); 
			selectedEntity();
			}
			////////////////////////////////////
			function selectedOs(){
        var objSelect=document.getElementById("subentity");
        for(var i=0;i<objSelect.options.length;i++)
		    {
			     if(objSelect.options[i].value =="<%=subentity%>")
		         {
			      
			           objSelect.options[i].selected = true;
			             break;
			        }
			   }  
}
function getOsList(){
			       dwr.util.removeAllOptions("subentity");
			        var type=document.getElementById("subtype").value;
                   var ostype=document.getElementById("ostype").value;
				    DWREngine.setAsync(false);//设置同步
				    CommonHelper.getsubTypeIndex(type,ostype,{
						      						callback:callbackOs,//回调函数
						      						timeout:5000,//超时时间
						      						errorHandler:function(message) { 
						      						alert("ERROR: " + message);
						      						}
					      						}); 

			}
			function callbackOs(object){
			  dwr.util.addOptions("subentity", ["不限"],{escapeHtml:false});
			 dwr.util.addOptions("subentity", object,{escapeHtml:false}); 
			 selectedOs();
			}
			 //wxy add end
</script>

<script>
	
</script>


</head>
<body id="body" class="body" onload="initmenu();getsubEntityList();getOsList()">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
	<form id="mainForm" method="post" name="mainForm">
		
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
								                	<td class="content-title">&nbsp;告警 >> 告警浏览 >> 告警统计 </td>
								                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
								       			</tr>
								        	</table>
										</td>
									</tr>
									<tr>
										<td>
											<table id="content-body" class="content-body">
												<tr>
													<td >
														<table>
															<tr >
																<td  class="detail-data-body-title">
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
																<td class="detail-data-body-title" style="text-align: left;">
																	&nbsp;开始日期
																	<input type="text" name="startdate" value="<%=startdate%>" size="10">
																	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																	<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
																
																	截止日期
																	<input type="text" name="todate" value="<%=todate%>" size="10"/>
																	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
																	<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
																</td>
															</tr>
														</table>
							  						</td>                       
					        					</tr>
					        					<tr>
					        						<td>
														<table>
															<tr>
																<td  class="detail-data-body-title" style="text-align: left;">	
																	&nbsp;设备类型
																	<select name="subtype" onchange="getsubEntityList()">
																	<option value="value">不限</option>
																	<option value="net" >网络设备</option>
																	<option value="host">服务器</option>
																	<option value="db" >数据库</option>
																	<option value="firewall" >防火墙</option>
																	<option value="middleware">中间件</option>
																	
																	<option value="storage" >存储</option>
																	<!--  
																	<option value="service" >服务</option>
																	<option value="virtual" >虚拟机</option>
																	<option value="vpn" >VPN</option>
																	<option value="ups" >UPS</option>
																	-->
																	<option value="link" >链路</option>
																	</select>
																	&nbsp;子类型
																	<select name="ostype" onchange="getOsList()">
																	<option value="不限">不限</option>
																	
																	</select>
																	&nbsp;指标类型
																	<select name="subentity">
																	<option value="不限">不限</option>
																	
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
																	<select name="businessid">
																	<option value="-1">不限</option>
																	<%
																		for(int i = 0 ; i < businessList.size(); i++){
																			Business business = (Business)businessList.get(i);
																			
																			%>
																				<option value="<%=business.getId()%>"><%=business.getName()%></option>
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
																	IP地址：<input type="text" name="ipaddress" id="ipaddress" value="<%=ipaddress%>">
																	<input type="button" name="submitss" value="查询" onclick="query()">
																	<INPUT type="button" class="formStyle" value="实时" onclick="window.open('<%=rootPath %>/event.do?action=todaylist&jsp=1','','toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no')">
																   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <INPUT type="button" name="clearList" value="清空告警" onclick="clears()"/>
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
																<td class="detail-data-body-title"><!-- <INPUT type="checkbox" name="checkall" onclick="javascript:chkall()"> -->序号</td>
        														<td class="detail-data-body-title">等级</td>
        														<td class="detail-data-body-title">类型</td>
        														<td class="detail-data-body-title">告警来源</td>
														    	<td class="detail-data-body-title">告警描述</td>
														    	<td class="detail-data-body-title">告警次数</td>
														    	<td class="detail-data-body-title">开始时间</td>
														    	<td class="detail-data-body-title">最新时间</td>
														    	<td class="detail-data-body-title">查看告警详情</td>
														    	<td class="detail-data-body-title">操作</td>
															</tr>
															<%
																if(list!=null && list.size()>0){
																	for(int i = 0 ; i < list.size(); i++){
																			List tempList = (List)list.get(i);
																			EventList eventList = (EventList)tempList.get(0);
																			
																			String subtype_tmp = eventList.getSubtype();
																			
																			int nodeid_tmp = eventList.getNodeid();
																			// 用于 跳转到 dispatcher.jsp 的id显示详细信息
																			
																			// see  db --> nms_manage_nodetype
																			
																			String dispatcher_id = "";
																			String type="";
																			if("host".equals(subtype_tmp)){
																				dispatcher_id = "net" + nodeid_tmp;
																				type="服务器";
																			}else if("network".equals(subtype_tmp) || "net".equals(subtype_tmp) || "storage".equals(subtype_tmp)){
																				dispatcher_id = "net" + nodeid_tmp;
																				type="网络设备";
																			}else if("storage".equals(subtype_tmp)){
																				dispatcher_id = "net" + nodeid_tmp;
																				type="存储";
																			}else if("db".equals(subtype_tmp)){
																				dispatcher_id = "dbs" + nodeid_tmp;
																				type="数据库";
																			}else if (subtype_tmp.equalsIgnoreCase("bus")) {
																				dispatcher_id = "bus" + nodeid_tmp;
																				
																			} else if (subtype_tmp.equalsIgnoreCase("service")) {
																				if("webping".equals(eventList.getSubentity())){
																					dispatcher_id = "wes" + nodeid_tmp;
																				}
																			} else if (subtype_tmp.equalsIgnoreCase("mail")) {
																				dispatcher_id = "mai" + nodeid_tmp;
																			} else if (subtype_tmp.equalsIgnoreCase("wasserver")) {
																				dispatcher_id = "was" + nodeid_tmp;
																			} else if (subtype_tmp.equalsIgnoreCase("grapes")) {
																				
																			} else if (subtype_tmp.equalsIgnoreCase("radar")) {
																				
																			} else if (subtype_tmp.equalsIgnoreCase("plot")) {
																				
																			} else if (subtype_tmp.equalsIgnoreCase("tomcat")) {
																				dispatcher_id = "tom" + nodeid_tmp;
																			} else if (subtype_tmp.equalsIgnoreCase("ftp")) {
																				dispatcher_id = "ftp" + nodeid_tmp;
																			} else if (subtype_tmp.equalsIgnoreCase("mq")) {
																				dispatcher_id = "mqs" + nodeid_tmp;
																				type="中间件";
																			} else if (subtype_tmp.equalsIgnoreCase("domino")) {
																				dispatcher_id = "dom" + nodeid_tmp;
																				type="中间件";
																			} else if (subtype_tmp.equalsIgnoreCase("iis")) {
																				dispatcher_id = "iis" + nodeid_tmp;
																				type="中间件";
																			} else if (subtype_tmp.equalsIgnoreCase("socket")) {
																				dispatcher_id = "soc" + nodeid_tmp;
																				type="中间件";
																			} else if (subtype_tmp.equalsIgnoreCase("cics")) {
																				dispatcher_id = "cic" + nodeid_tmp;
																				type="中间件";
																			} else if (subtype_tmp.equalsIgnoreCase("weblogic")) {
																				dispatcher_id = "web" + nodeid_tmp;
																				type="中间件";
																			}else if (subtype_tmp.equalsIgnoreCase("url")) {
																				dispatcher_id = "wes" + nodeid_tmp;
																			}else if (subtype_tmp.equalsIgnoreCase("apache")) {
																				dispatcher_id = "apa" + nodeid_tmp;
																			}else if (subtype_tmp.equalsIgnoreCase("resin")) {
																				dispatcher_id = "res" + nodeid_tmp;
																			}else if (subtype_tmp.equalsIgnoreCase("weblogin")) {
																				dispatcher_id = "wln" + nodeid_tmp;
																			}else if(subtype_tmp.equalsIgnoreCase("air"))
																			{
																			    dispatcher_id = "air" + nodeid_tmp;
                                                                            }else if(subtype_tmp.equalsIgnoreCase("ups"))
																			{
																			    dispatcher_id = "ups" + nodeid_tmp;
                                                                            }																			
																			
																			String level = String.valueOf(eventList.getLevel1());
																			String bgcolor = "";
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
																		%>
																		<tr <%=onmouseoverstyle%>>
																			<td class="detail-data-body-list" height="28px"><!--<INPUT type="checkbox" name="checkbox">--><%=i+1%></td>
			        														<td class="detail-data-body-list" <%=bgcolor%>><%=level%></td>
			        														<td class="detail-data-body-list" ><%=type%></td>
			        														<td class="detail-data-body-list"><a href="#"  onclick='showNodeDetails("<%=dispatcher_id%>")'><%=hashtable.get(eventList.getId()+"")%></a></td>
																	        <td class="detail-data-body-list"><%=eventList.getContent()%></td>
																	    	<td class="detail-data-body-list"><%=tempList.get(1)%></td>
																	    	<td class="detail-data-body-list"><%=tempList.get(2)%></td>
																	    	<td class="detail-data-body-list"><%=tempList.get(3)%></td>
																	    	<td class="detail-data-body-list"><a href="#"  onclick='showDetails("<%=eventList.getNodeid()%>","<%=eventList.getSubentity()%>","<%=eventList.getSubtype()%>","<%=eventList.getLevel1()%>" )'>查看告警详情</a></td>
																		<td class="detail-data-body-list"><a href="#"  onclick='deleteDetails("<%=eventList.getNodeid()%>","<%=eventList.getSubentity()%>","<%=eventList.getSubtype()%>","<%=eventList.getLevel1()%>" )'>删除</a></td>
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
