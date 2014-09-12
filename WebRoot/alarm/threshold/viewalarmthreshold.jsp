<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.alarm.model.AlarmIndicators"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.alarm.model.AlarmThreshold"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
  	String menuTable = (String)request.getAttribute("menuTable");
	List list = (List)request.getAttribute("list");
  
	String indicatorsId = (String)request.getAttribute("indicatorsId");
  	AlarmIndicators alarmIndicators = (AlarmIndicators)request.getAttribute("alarmIndicators");
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
				mainForm.action = "<%=rootPath%>/alarmIndicators.do?action=edit&id=" + node;
				mainForm.submit();
			}
			
			function save(){
				mainForm.action = "<%=rootPath%>/alarmIndicators.do?action=saveAlarmThreshold&indicatorsId=" + "<%=indicatorsId%>";
				mainForm.submit();
			}
			
			
			function setDefaultValue(){
				mainForm.action = "<%=rootPath%>/alarmIndicators.do?action=setDefaultValue&indicatorsId=" + "<%=indicatorsId%>";
				mainForm.submit();
			}
			
			
			  
			
		</script>
		
		<script type="text/javascript">
		
			var rowNum = 0;
		
			function initAlarmThresholdList(){
				var alarmThresholds = new Array();
				<%
					if(list !=null && list.size() != 0){
						System.out.println(list.size());
						for(int i = 0; i < list.size() ; i++){
							AlarmThreshold alarmThreshold = (AlarmThreshold)list.get(i);
				%>
							alarmThresholds.push({
								id:'<%=alarmThreshold.getId()%>',
								indicatorsId:'<%=alarmThreshold.getIndicatorsId()%>',
								datatype:'<%=alarmThreshold.getDatatype()%>',
								level:'<%=alarmThreshold.getLevel()%>',
								alarmTimes:'<%=alarmThreshold.getAlarmTimes()%>',
								value:'<%=alarmThreshold.getValue()%>',
								unit:'<%=alarmThreshold.getUnit()%>',
								isAlarm:'<%=alarmThreshold.getIsAlarm()%>',
								type:'<%=alarmThreshold.getType()%>',
								isSendSMS:'<%=alarmThreshold.getIsSendSMS()%>',
								bak:'<%=alarmThreshold.getBak()%>'
							});
				<%
						}
					}
				%>
				
				var alarmThresholdTable = document.getElementById("alarmThresholdTable");
				alarmThresholdTable.innerHTML = "";
				rowNum = 0;
				for(var i = 0;i < alarmThresholds.length;i++){
					addAlarmThresholdTable();
					var alarmThreshold = alarmThresholds[i];
					
					var str = "" + rowNum;
					if (str.length < 2) {
						str = "0" + str;
					}
					//document.getElementById("timeShareConfigId" + str).value = alarmThreshold.id;
					//document.getElementById("indicatorsId" + str).value = alarmThreshold.indicatorsId;
					document.getElementById("datatype" + str).value = alarmThreshold.datatype;
					document.getElementById("level" + str).value = alarmThreshold.level;
					document.getElementById("alarmTimes" + str).value = alarmThreshold.alarmTimes;
					document.getElementById("value" + str).value = alarmThreshold.value;
					document.getElementById("unit" + str).value = alarmThreshold.unit;
					document.getElementById("isAlarm" + str).value = alarmThreshold.isAlarm;
					document.getElementById("type" + str).value = alarmThreshold.type;
					document.getElementById("isSendSMS" + str).value = alarmThreshold.isSendSMS;
					document.getElementById("bak" + str).value = alarmThreshold.bak;
				}
				
			}
			
			
			
			function addAlarmThresholdTable(){
			
				rowNum = rowNum + 1;
				var str = "" + rowNum;
				while (str.length < 2) {
					str = "0" + str;
				}
				var table = '<table id="container-main-content-' + str + '"  class="container-main-content">'+
				'<tr><td><table class="content-header">'+
				'<tr>'+
				'<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>'+
				'<td class="content-title"> 告警指标阀值配置 ' + str  +' </td>'+
				'<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>'+
				'</tr>'+
				'</table>'+
		        '</td>'+
		        '</tr>'+
		        '<tr>'+
		        '<td>'+
		        '<table class="content-body">'+
				'<tr>'+
				'<td>'+
				'<table>'+
				'<tr>'+
				'<td class="body-data-title" >数据类型</td>'+
				'<td class="body-data-title">等级</td>'+
				'<td class="body-data-title">告警次数</td>'+
				'<td class="body-data-title">告警阀值</td>'+
				'<td class="body-data-title">阀值单位</td>'+
				'<td class="body-data-title">是否告警</td>'+
				'<td class="body-data-title">是否发送短信</td>'+
				'<td class="body-data-title">类型</td>'+
				'<td class="body-data-title">告警描述</td>'+
				'<td class="body-data-title">操作</td>'+
				'</tr>'+
				'<tr>'+
				'<td class="body-data-list"><input style="width:80px" type="text" id="datatype'+ str +'"  name="datatype'+ str +'"></td>'+
				'<td class="body-data-list"><input style="width:80px" type="text" id="level'+ str +'" name="level'+ str +'"></td>'+
				'<td class="body-data-list"><input style="width:80px" type="text" id="alarmTimes'+ str +'" name="alarmTimes'+ str +'"></td>'+
				'<td class="body-data-list"><input style="width:80px" type="text" id="value'+ str +'" name="value'+ str +'"></td>'+
				'<td class="body-data-list"><input style="width:80px" type="text" id="unit'+ str +'" name="unit'+ str +'"></td>'+
				'<td class="body-data-list"><input style="width:80px" type="text" id="isAlarm'+ str +'" name="isAlarm'+ str +'"></td>'+
				'<td class="body-data-list"><input style="width:80px" type="text" id="isSendSMS'+ str +'" name="isSendSMS'+ str +'"></td>'+
				'<td class="body-data-list"><input style="width:80px" type="text" id="type'+ str +'" name="type'+ str +'"></td>'+
				'<td class="body-data-list"><input type="text" id="bak'+ str +'" name="bak'+ str +'"></td>'+
				'<td class="body-data-list"><a href=javascript:delRow1('+ str +')>删除</a></td>'+
				'</tr>'+
				'</table>'+
				'</td>'+
				'</tr>'+
		        '</table>'+
		        '</td>'+
		        '</tr>'+
		        '<tr>'+
		        '<td>'+
		        '<table class="content-footer">'+
		        '<tr>'+
		        '<td>'+
		        '<table width="100%" border="0" cellspacing="0" cellpadding="0">'+
				'<tr>'+
				'<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>'+
				'<td></td>'+
				'<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>'+
				'</tr>'+
				'</table>'+
		        '</td>'+
		        '</tr>'+
		        '</table>'+
		        '</td>'+
		        '</tr>'+
		        '</table>';
		        var alarmThresholdTable = document.getElementById("alarmThresholdTable");
		        alarmThresholdTable.insertAdjacentHTML("beforeEnd" , table);
		        document.getElementById("rowNum").value = rowNum;
			}
			
			function delRow1(row) {
				var str = "" + row;
				while (str.length < 2) {
					str = "0" + str;
				}
				var alarmThresholdTable = document.getElementById("alarmThresholdTable");
				var alarmThresholdTable1 = document.getElementById("container-main-content-" + str);
		        alarmThresholdTable.removeChild(alarmThresholdTable1);
			}
			
			
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();initAlarmThresholdList();">
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
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title"> 告警 >> 告警配置 >> 告警指标阀值配置 </td>
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
															<table>
																<tr>
													    			<td  class="body-data-title" style="text-align: left;">
													    				所属指标
							        								</td>
							        								<td  class="body-data-title" style="text-align: left;">
													    				<a onclick="addAlarmThresholdTable();" href="#">增加阀值详细信息配置</a>&nbsp;&nbsp;&nbsp;
													    				<input type="hidden" id="rowNum" name="rowNum">
													    				<a onclick="save();" href="#">保存</a>&nbsp;&nbsp;&nbsp;
													    				<a onclick="addAlarmThresholdTable();" href="#">返回</a>&nbsp;&nbsp;&nbsp;
													    				<a onclick="initAlarmThresholdList();" href="#">重置</a>&nbsp;&nbsp;&nbsp;
																		<a onclick="initAlarmThresholdList();" href="#">重置</a>&nbsp;&nbsp;&nbsp;
																		<a onclick="setDefaultValue();" href="#">应用于设备默认值</a>&nbsp;&nbsp;&nbsp;
							        								</td>
							        							</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
													    			<td class="body-data-title">名称</td>
													    			<td class="body-data-title">类型</td>
													    			<td class="body-data-title">子类型</td>
													    			<td class="body-data-title">描述</td>
							        							</tr>
				        										<tr>
													    			<td class="body-data-list"><%=alarmIndicators.getName()%></td>
													    			<td class="body-data-list"><%=alarmIndicators.getType()%></td>
													    			<td class="body-data-list"><%=alarmIndicators.getSubtype()%></td>
													    			<td class="body-data-list"><%=alarmIndicators.getDescription()%></td>
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
							
							<tr>
								<td>
									<div id="alarmThresholdTable" class="td-container-main-content"></div>
								</td>
							</tr>
							
						</table>
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
