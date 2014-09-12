<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.alarm.model.AlarmIndicators"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.alarm.model.AlarmIndicatorsNode"%>
<%@page import="com.afunms.topology.model.*"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>

<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List hostnodelist = (List)request.getAttribute("hostnodelist");
  String ids = (String)request.getAttribute("ids");
  
  String nodeid = (String)request.getAttribute("nodeid");
  String type = (String)request.getAttribute("type");
  String subtype = (String)request.getAttribute("subtype");
%>


<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
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
			
			
			function add(){
				mainForm.action = "<%=rootPath%>/alarmIndicators.do?action=add";
				mainForm.submit();
			}
			
			function edit(){
				mainForm.action = "<%=rootPath%>/alarmIndicatorsNode.do?action=edit&id=" + node;
				mainForm.submit();
			}
			
			function viewAlarmNode(){
				mainForm.action = "<%=rootPath%>/alarmIndicators.do?action=add";
				mainForm.submit();
			}
			
			
			function addmanage()
			{
				mainForm.action ="<%=rootPath%>/alarmIndicatorsNode.do?action=changeManage&value=1&id="+ node + "&nodeid=" + "<%=nodeid%>"; 
				mainForm.submit();
			}
			
			function cancelmanage()
			{
				mainForm.action ="<%=rootPath%>/alarmIndicatorsNode.do?action=changeManage&value=0&id="+ node + "&nodeid=" + "<%=nodeid%>";
				mainForm.submit();
				
			}
			function todelete()
			{
			if(confirm('是否确定删除这条记录?')) {
				mainForm.action ="<%=rootPath%>/alarmIndicatorsNode.do?action=delete&value=0&id="+ node + "&nodeid=<%=nodeid%>";
				mainForm.submit();
			 }
			}
			
			function toAdd()
  			{
      				mainForm.action = "<%=rootPath%>/alarmIndicatorsNode.do?action=changeadd";
      				mainForm.submit();
  			}
  			
  			function toAdd2()
  			{
      				mainForm.action = "<%=rootPath%>/alarmIndicatorsNode.do?action=replenish";
      				mainForm.submit();
  			}
  			  function toMultiAdd()
  			{
      				mainForm.action = "<%=rootPath%>/alarmIndicatorsNode.do?action=showtomultiaddlist";
      				mainForm.submit();
  			}
			
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<!-- 这里用来定义需要显示的右键菜单 -->
		<div id="itemMenu" style="display: none";>
		<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
			style="border: thin;font-size: 12px" cellspacing="0">
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.edit()">编辑</td>
			</tr>
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.cancelmanage()">取消采集</td>
			</tr>
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.addmanage()">添加采集</td>
			</tr>
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.todelete()">删除</td>
			</tr>
		</table>
		</div>
		<!-- 右键菜单结束-->
		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" name="nodeid" id="nodeid" value="<%=nodeid%>">
			<input type="hidden" name="type" id="type" value="<%=type%>">
			<input type="hidden" name="subtype" id="subtype" value="<%=subtype%>">
			<input type="hidden" name="ids" id="ids" value="<%=ids%>">
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
									                	<td class="content-title"> 告警 >> 告警配置 >> 告警阀值批量应用列表 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
													<tr>
														<td class="body-data-title" style="text-align:right">
		        												&nbsp;&nbsp;
		        											</td>
		        										</tr>
		        								</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
													<tr>
														<td>
															<table>
																<tr>
																<td class="body-data-title" width=5%>
																	<INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()" class=noborder>序号</td>
													    			<td class="body-data-title" width=20%>名称</td>
													    			<td class="body-data-title">IP</td>
													    			<td class="body-data-title">所属业务</td>
							        							</tr>
							        							<%
							        							
							        								if(hostnodelist != null && hostnodelist.size() > 0) {
							        									for(int i = 0; i < hostnodelist.size(); i++){
							        										HostNode hostnode = (HostNode)hostnodelist.get(i);
							        										
							        										       BusinessDao bussdao = new BusinessDao();
																		       List allbuss = bussdao.loadAll();  
																		   	String bid = hostnode.getBid();
																		   	if(bid == null)bid="";
																		   	String id[] = bid.split(",");
																		   	List bidlist = new ArrayList();
																		   	if(id != null &&id.length>0){
																				for(int j=0;j<id.length;j++){
																			    		bidlist.add(id[j]);
																				}
																			}
																			String bussName = "";
																			if( allbuss.size()>0){
																				for(int k=0;k<allbuss.size();k++){
																					Business buss = (Business)allbuss.get(k);
																					
																					if(bidlist.contains(buss.getId()+"")){
																						bussName = bussName + ',' + buss.getName();
																					}
																					
																 				}
																			}
							        										%>
							        										
							        										<tr <%=onmouseoverstyle%>>
							        											<td class="body-data-list">
							        												<INPUT type="checkbox" class=noborder name=checkbox value="<%=hostnode.getId()%>" class=noborder>
							        											</td>
																    			<td class="body-data-list" ><%=hostnode.getAlias()%></td>
																    			<td class="body-data-list" bgcolor="#33CC33"><%=hostnode.getIpAddress()%></td>
																    			<td class="body-data-list"><%=bussName%></td>
										        							</tr>
							        										<%
							        									}
							        								}%>
							        								<tr>
																			<TD nowrap colspan="20" align=center><br>
																			<input type="button" value="补充设置" style="width:80" onclick="toAdd2()">&nbsp;&nbsp;
																			<input type="button" value="当前设置" style="width:80" onclick="toAdd()">&nbsp;&nbsp;
																			<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">&nbsp;&nbsp;
																			<input type="button" value="关 闭" style="width:50" onclick="window.close()">
																			</TD>	
																		</tr>
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
