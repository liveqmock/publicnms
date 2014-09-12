<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.alarm.model.AlarmIndicators"%>
<%@page import="java.util.List"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.common.util.*" %>
<%@page import="com.afunms.alarm.model.AlarmIndicatorsNode"%>
<%@ include file="/include/globe.inc"%>

<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List list = (List)request.getAttribute("list");
  String category = (String)request.getAttribute("category");
  String nodeid = (String)request.getAttribute("nodeid");
  String type = (String)request.getAttribute("type");
  String subtype = (String)request.getAttribute("subtype");
  String ipaddress=(String)request.getAttribute("ipaddress");
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
				mainForm.action = "<%=rootPath%>/alarmIndicatorsNode.do?action=edit&id=" + node+"&ipaddress="+ipaddress+"&flag=vmware";
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
		
			  function toAdd()
  			{
      				mainForm.action = "<%=rootPath%>/alarmIndicatorsNode.do?action=showtoaddlist&category=lun";
      				mainForm.submit();
  			}
  			  function toMultiAdd()
  			{
      				mainForm.action = "<%=rootPath%>/alarmIndicatorsNode.do?action=showtomultiaddlist";
      				mainForm.submit();
  			}
			
		</script>
			<script type="text/javascript">
			
  			var delAction = "<%=rootPath%>/alarmIndicatorsNode.do?action=listDelete";
  		    		      function doDelete()
  {  
   if(confirm('是否确定删除这条记录?')) {
     mainForm.action = "<%=rootPath%>/alarmIndicatorsNode.do?action=listDelete";
     mainForm.submit();
     }
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
					onclick="parent.cancelmanage()">取消监视</td>
			</tr>
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.addmanage()">启用监视</td>
			</tr>
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.toDelete()">删除</td>
			</tr>
		</table>
		</div>
		<!-- 右键菜单结束-->
		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" name="nodeid" id="nodeid" value="<%=nodeid%>">
			<input type="hidden" name="type" id="type" value="<%=type%>">
			<input type="hidden" name="subtype" id="subtype" value="<%=subtype%>">
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
									                	<td class="content-title"> 告警 >> 告警配置 >> 告警阀值列表 </td>
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
		        												<a href="#" onclick="toAdd()">添加</a>&nbsp;&nbsp;	<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;<a href="#" onclick="toMultiAdd()">批量应用</a>&nbsp;&nbsp;
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
													    			<td class="body-data-title" width=10%>名称</td>
													    			<td class="body-data-title" width=8%>描述</td>
													    			<td class="body-data-title">类型</td>
													    			<td class="body-data-title">子类型</td>
													    			<td class="body-data-title">目录</td>
													    			<td class="body-data-title">子目录</td>
													    			<td class="body-data-title" width=7%>数据类型</td>
													    			<td class="body-data-title">单位</td>
													    			<td class="body-data-title">启用</td>
													    			<td class="body-data-title">比较方式</td>
													    			<td class="body-data-title">一级阀值</td>
													    			<td class="body-data-title">次数</td>
													    			<td class="body-data-title" width=3%>告警</td>
													    			<td class="body-data-title">二级阀值</td>
													    			<td class="body-data-title">次数</td>
													    			<td class="body-data-title" width=3%>告警</td>
													    			<td class="body-data-title">三级阀值</td>
													    			<td class="body-data-title">次数</td>
													    			<td class="body-data-title" width=3%>告警</td>
													    			<td class="body-data-title" width=5%>详情</td>
							        							</tr>
							        							<%
							        							
							        								if(list != null && list.size() > 0) {
							        								     session.setAttribute("flag","lun");
							        								      int h=0;
							        									for(int i = 0; i < list.size(); i++){
							        										AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
							        										if(!alarmIndicatorsNode.getCategory().equalsIgnoreCase("lun"))continue;
							        										h=h+1;
							        										String isMonitor = "否";
							        										String bcolor="gray";
							        										if("1".equals(alarmIndicatorsNode.getEnabled())){
							        											isMonitor = "是";
							        											bcolor ="#3399CC";
							        										}
							        										
							        										//System.out.println(alarmIndicatorsNode.getDatatype()+ "====================");
							        										
							        										String datatype = "字符串";
							        										if("Number".equals(alarmIndicatorsNode.getDatatype())){
							        											datatype = "数字";
							        										}
							        										
							        										String sms0 = "否";
							        										if("1".equals(alarmIndicatorsNode.getSms0())){
							        											sms0 = "是";
							        										}
							        										
							        										String sms1 = "否";
							        										if("1".equals(alarmIndicatorsNode.getSms1())){
							        											sms1 = "是";
							        										}
							        										
							        										String sms2 = "否";
							        										if("1".equals(alarmIndicatorsNode.getSms2())){
							        											sms2 = "是";
							        										}
							        										String compare = "升序";
							        										String bgcol = "#ffffff";
							        										if(alarmIndicatorsNode.getCompare() == 0){
							        											compare = "降序";
							        											bgcol = "gray";
							        										}if(alarmIndicatorsNode.getCompare() == 2){
							        											compare = "相等";
							        										}
							        										%>
							        										
							        										<tr <%=onmouseoverstyle%>>
							        											<td class="body-data-list">
							        												<INPUT type="checkbox" class=noborder name=checkbox value="<%=alarmIndicatorsNode.getId()%>" class=noborder><%=h %>
							        											</td>
																    			<td class="body-data-list" ><%=alarmIndicatorsNode.getName()%></td>
																    			<td class="body-data-list" bgcolor="#33CC33"><%=alarmIndicatorsNode.getDescr()%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getType()%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getSubtype()%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getCategory()%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getSubentity()%></td>
																    			<td class="body-data-list"><%=datatype%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getThreshlod_unit()%></td>
																    			<td class="body-data-list" bgcolor="<%=bcolor%>"><%=isMonitor%></td>
																    			<td class="body-data-list" bgcolor="<%=bgcol%>"><%=compare%></td>
																    			<td class="body-data-list" bgcolor="yellow"><%=alarmIndicatorsNode.getLimenvalue0()%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getTime0()%></td>
																    			<td class="body-data-list"><%=sms0%></td>
																    			<td class="body-data-list" bgcolor="orange"><%=alarmIndicatorsNode.getLimenvalue1()%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getTime1()%></td>
																    			<td class="body-data-list"><%=sms1%></td>
																    			<td class="body-data-list" bgcolor="red"><%=alarmIndicatorsNode.getLimenvalue2()%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getTime2()%></td>
																    			<td class="body-data-list"><%=sms2%></td>
										        								<td align="center" class="body-data-list">
																					<img src="<%=rootPath%>/resource/image/status.gif"
																						border="0" width=15 oncontextmenu=showMenu('2','<%=alarmIndicatorsNode.getId()%>','<%=ipaddress %>','1111') title="右键菜单">
						       													</td>
										        							</tr>
							        										<%
							        									}
							        								}%>
							        								<tr>
																			<TD nowrap colspan="20" align=center><br>
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
