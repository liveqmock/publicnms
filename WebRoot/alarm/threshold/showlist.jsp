<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.alarm.model.AlarmIndicatorsNode"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.indicators.model.NodeDTO"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>

<%
	String rootPath = request.getContextPath();
	
	String menuTable = (String)request.getAttribute("menuTable");
	
	String nodeid = (String)request.getAttribute("nodeid");
	String type = (String)request.getAttribute("type");
	String subtype = (String)request.getAttribute("subtype");
	
	List nodelist = (List)request.getAttribute("nodelist");
	
	List allNodeDTOlist = (List)request.getAttribute("allNodeDTOlist");
	List<AlarmIndicatorsNode> list = (List<AlarmIndicatorsNode>)request.getAttribute("list");
	Hashtable<String , NodeDTO> nodeDTOHashtable = (Hashtable<String , NodeDTO>)request.getAttribute("nodeDTOHashtable");
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
				
				
				var type = document.getElementById("type");
				var subtype = document.getElementById("subtype");
				if(type.value!="-1" && subtype.value !="-1" ){
					mainForm.action = "<%=rootPath%>/nodeGatherIndicators.do?action=showAdd&jspFlag=add";
				mainForm.submit();
				}
				if(type.value=="-1"){
					alert("请选择类型!");
				}
				if(subtype.value=="-1"){
					alert("请选择子类型!");
				}
			}
			
			function edit(){
				mainForm.action = "<%=rootPath%>/nodeGatherIndicators.do?action=showEdit&id=" + node;
				mainForm.submit();
			}
			
			function toChooseNode(){
				var nodeid = document.getElementById("nodeid");
				var type = document.getElementById("type");
				var subtype = document.getElementById("subtype");
				if(type.value!="-1" && subtype.value !="-1" && nodeid.value !="-1"){
					mainForm.action = "<%=rootPath%>/nodeGatherIndicators.do?action=showAdd&jspFlag=multi";
					mainForm.submit();
				}
				if(type.value=="-1"){
					alert("请选择类型!");
				}
				if(subtype.value=="-1"){
					alert("请选择子类型!");
				}
				if(nodeid.value=="-1"){
					alert("请选择设备!");
				}
				
			}
			
			function search(){
				mainForm.action = "<%=rootPath%>/nodeGatherIndicators.do?action=showlist";
				mainForm.submit();
			}
			
			
			  
			
		</script>
		<script type="text/javascript">
			
  			var delAction = "<%=rootPath%>/nodeGatherIndicators.do?action=showDelete";
  			var listAction = "<%=rootPath%>/nodeGatherIndicators.do?action=list";
  			function doDelete()
       {
           if(confirm('是否确定删除这条记录?')) {
                mainForm.action = "<%=rootPath%>/alarmIndicatorsNode.do?action=showDelete";
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
		</table>
		</div>
		<!-- 右键菜单结束-->
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
									                	<td class="content-title"> 资源 >> 性能监视 >> 指标阀值列表 </td>
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
																	<td class="body-data-title" style="text-align: left;">
																		&nbsp;&nbsp;&nbsp;<b>类型：</b>
																		<select id="type" name="type" style="width:150px" onchange="changeType()">
																			<option value="-1">不限</option>
																			<option value="host">服务器</option>
																			<option value="net">网络设备</option>
																			<option value="db">数据库</option>
																			<option value="middleware">中间件</option>
																			<option value="service">服务</option>
																		</select>
																		&nbsp;&nbsp;&nbsp;<b>子类型：</b>
																		<select id="subtype" name="subtype" style="width:150px" onchange="changeNode()">
																			<option value="-1">不限</option>
																		</select>
																		&nbsp;&nbsp;&nbsp;<b>设备：</b>
																		<select id="nodeid" name="nodeid" style="width:150px">
																		</select>
																		&nbsp;&nbsp;&nbsp;<input type="button" value="查  询" onclick="search()">
							        								</td>
													    			<td  class="body-data-title" style="text-align: right;">
							    										<a href="#" onclick="add()">添加</a>&nbsp;&nbsp;&nbsp;
							    										<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
							    										<a href="#" onclick="toChooseNode()">批量应用</a>&nbsp;&nbsp;&nbsp;
							        								</td>
							        							</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
															<td class="body-data-title" width=5%>
																	<INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()" class=noborder>序号</td>
													    			<td class="body-data-title" width=8%>设备名称</td>
													    			<td class="body-data-title" width=8%>IP</td>
													    			<td class="body-data-title" width=8%>阀值</td>
													    			<td class="body-data-title" width=8%>描述</td>
													    			<td class="body-data-title">类型</td>
													    			<td class="body-data-title">子类型</td>
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
							        							<%
							        								if(list != null && list.size() > 0) {
							        									for(int i = 0; i < list.size(); i++){
							        										AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
							        										
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
							        										NodeDTO nodeDTO = (NodeDTO)nodeDTOHashtable.get(String.valueOf(alarmIndicatorsNode.getNodeid()) + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype());
							        										if(nodeDTO == null){
							        											nodeDTO = new NodeDTO();
							        										}
							        										String ipaddress = nodeDTO.getIpaddress();
							        										if(ipaddress == null){
							        											ipaddress = "";
							        										}
							        										%>
							        											<tr <%=onmouseoverstyle%>>
																	    			<td class="body-data-list">
							        												<INPUT type="checkbox" class=noborder name=checkbox value="<%=alarmIndicatorsNode.getId()%>" class=noborder>
							        											</td>
							        											<td class="body-data-list"><%=nodeDTO.getName()%></td>
																	    			<td class="body-data-list"><%=ipaddress%></td>
																    			<td class="body-data-list" ><%=alarmIndicatorsNode.getName()%></td>
																    			<td class="body-data-list" bgcolor="#33CC33"><%=alarmIndicatorsNode.getDescr()%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getType()%></td>
																    			<td class="body-data-list"><%=alarmIndicatorsNode.getSubtype()%></td>
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
																						border="0" width=15 oncontextmenu=showMenu('2','<%=alarmIndicatorsNode.getId()%>','','1111') title="右键菜单">
						       													</td>
											        							</tr>
							        										<%
							        									}
							        								}%>
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
	
	<script>
	
		
	
		var nodeArray = new Array();
		
		<%
			if(allNodeDTOlist != null){
				for(int j = 0 ; j < allNodeDTOlist.size(); j++){
				 	NodeDTO  nodeDTO = (NodeDTO)allNodeDTOlist.get(j);
					%>
						nodeArray.push({
							id:"<%=nodeDTO.getId()%>",
							nodeid:"<%=nodeDTO.getNodeid()%>",
							name:"<%=nodeDTO.getName()%>",
							ipaddress:"<%=nodeDTO.getIpaddress()%>",
							type:"<%=nodeDTO.getType()%>",
							subtype:"<%=nodeDTO.getSubtype()%>",
							businessId:"<%=nodeDTO.getBusinessId()%>",
							businessName:"<%=nodeDTO.getBusinessName()%>"
					});
					<%
				}
			}
			
		%>
		
		function changeType(){
			var hostArray = [
						["windows" , "windows"],
						["linux" , "linux"],
						["aix" , "aix"],
						["hpunix" , "hpunix"],
						["solaris" , "solaris"],
						["as400" , "as400"]
						];
			var netArray = new Array();
			netArray = 	[
						["cisco" , "cisco"],
						["h3c" , "h3c"],
						["entrasys" , "entrasys"],
						["maipu" , "maipu"],
						["redgiant" , "redgiant"],
						["northtel" , "northtel"],
						["dlink" , "dlink"],
						["bdcom" , "bdcom"]
						];
			var dbArray = new Array();
			dbArray = 	[
						["oracle" , "oracle"],
						["sqlserver" , "sqlserver"],
						["mysql" , "mysql"],
						["db2" , "db2"],
						["sybase" , "sybase"],
						["Informix" , "Informix"]
						];
			var middlewareArray = new Array();
			middlewareArray = 	[
						["mq" , "mq"],
						["domino" , "domino"],
						["was" , "was"],
						["tomcat" , "tomcat"],
						["iis" , "iis"],
						["jboss" , "jboss"],
						["apache" , "apache"],
						["tuxedo" , "tuxedo"],
						["cics" , "cics"],
						["dns" , "dns"],
						["weblogic" , "weblogic"]
								];
			var serviceArray = new Array();
			serviceArray = 	[
						["ftp" , "ftp"],
						["email" , "email"],
						["hostprocess" , "hostprocess"],
						["web" , "web"],
						["port" , "port"]
								];
			var type = document.getElementById("type");
			var subtype = document.getElementById("subtype");
			subtype.length = 0;
			if(type.value == "-1"){
				subtype.options[0] = new Option("不限" , "-1");      
			}else if(type.value == "net"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <netArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(netArray[i][0],netArray[i][1]);                     
				}	
			}else if(type.value == "host"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <hostArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(hostArray[i][0],hostArray[i][1]);                     
				}	
			}else if(type.value == "db"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <dbArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(dbArray[i][0],dbArray[i][1]);                     
				}	
			}else if(type.value == "middleware"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <middlewareArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(middlewareArray[i][0],middlewareArray[i][1]);                     
				}	
			}else if(type.value == "service"){
				subtype.options[0] = new Option("不限" , "-1");
				for (var i = 0 ;i <serviceArray.length;i++)   
	      		{                   
					subtype.options[i+1] = new Option(serviceArray[i][0],serviceArray[i][1]);                     
				}	
			}
			
			changeNode();
		}
		
		
		function changeNode(){
			
			var type = document.getElementById("type");
			var subtype = document.getElementById("subtype");
			var nodeid = document.getElementById("nodeid");
			nodeid.length = 0;
			nodeid.options[0] = new Option("不限","-1");
			if(type.value == "-1"){
				var k = 1;
				
				for (var i = 0 ;i <nodeArray.length;i++)   
	      		{                   
					nodeid.options[k] = new Option(nodeArray[i].name,nodeArray[i].nodeid);
	      			k++;               
				}	
			}else if(type.value != "-1" && subtype.value == "-1"){
				var k = 1;
				for (var i = 0 ;i <nodeArray.length;i++)   
	      		{      
	      			if(nodeArray[i].type == type.value){
	      				nodeid.options[k] = new Option(nodeArray[i].name,nodeArray[i].nodeid);
	      				k++;
	      			}             
					                
				}	
			}else if(type.value != "-1" && subtype.value != "-1"){
				var k = 1;
				for (var i = 0 ;i <nodeArray.length;i++)
	      		{      
	      			if(nodeArray[i].type == type.value && nodeArray[i].subtype == subtype.value){
	      				nodeid.options[k] = new Option(nodeArray[i].name,nodeArray[i].nodeid);
	      				k++;
	      			}             
					                
				}	
			}
			
		}
		
		function initAttribute(){
			var type = document.getElementById("type");
			var subtype = document.getElementById("subtype");
			var nodeid = document.getElementById("nodeid");
			
			type.value = "<%=type%>";
			changeType();
			subtype.value = "<%=subtype%>";
			changeNode();
			nodeid.value = "<%=nodeid%>";
		}
		
		initAttribute();
	</script>
	
</html>
