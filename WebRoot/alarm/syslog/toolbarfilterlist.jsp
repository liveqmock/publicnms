<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.indicators.model.NodeGatherIndicators"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.config.model.Errptconfig"%>
<%@page import="com.afunms.topology.model.NetSyslogNodeAlarmKey"%>

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

String nodeid = (String)request.getAttribute("nodeid");
List flist = (List)request.getAttribute("facilitys");
//List plist = (List)request.getAttribute("prioritys");
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

 List netSyslogNodeAlarmList = (ArrayList)request.getAttribute("netSyslogNodeAlarmList");
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

			var delAction = "<%=rootPath%>/nodeGatherIndicators.do?action=delete";
			var listAction = "<%=rootPath%>/nodeGatherIndicators.do?action=list";
			var rowNum = 0;
  			
	var alarmType = [["1" , "普通告警"], ["2" , "严重告警"], ["3" , "紧急告警"]];
  			
	function toAdd()
  {
      mainForm.action = "<%=rootPath%>/nodesyslogrule.do?action=toolbarsave";
      mainForm.submit();
  }
			
	/**
	 * 增加一行
	 */
	function addRow(keywordDivId) {
		var tr = document.getElementById(keywordDivId).insertRow(0);
		rowNum = rowNum + 1;
		var str = "" + rowNum;
		while (str.length < 2) {
			str = "0" + str;
		}
		var types = "";
		for (var i = 0 ; i < alarmType.length; i++){
			types = types + '<OPTION value="' + alarmType[i][0] + '">' + alarmType[i][1] + '</OPTION>';
		}
		var selected = '<select id="level-' + str + '" name="level-' + str
				+ '" style="width:80px" onchange="changeDateSelect(' + str+ ',this.value)">' + types + '</select>';
		
		var td = tr.insertCell(tr.cells.length);
	
		var elemTable = document.createElement("table");
		var elemTBody = document.createElement("tBody");
		elemTable.style.marginTop = "10px";
		elemTable.id = "table" + str;
		elemTable.width = "100%";
		elemTable.align = "left";
		elemTable.cellPadding = "1";
		elemTable.cellSpacing = "1";
		elemTable.bgColor = "black";
		elemTBody.bgColor = "white";
		elemTable.appendChild(elemTBody);
		td.appendChild(elemTable);
	
		var tr0 = document.createElement("tr");
		elemTBody.appendChild(tr0);
		var td13 = tr0.insertCell(tr0.cells.length);
		td13.className = "lab";
		td13.innerHTML = '<span class="must">*</span>包含：';
		var td14 = tr0.insertCell(tr0.cells.length);
		td14.align = "left";
		td14.innerHTML = '<input class="input-text" id="keywords-'
				+ str
				+ '" name="keywords-'
				+ str
				+ '" type="text"  size="30"/>&nbsp;' + selected;
		var td12 = tr0.insertCell(tr0.cells.length);
		td12.className = "lab";
		td12.innerHTML = '<a href="javascript:delRow(' + keywordDivId + ','
				+ rowNum + ')">删除</a>';
		//changeAlarmLevelSelect(str,document.getElementById("level-" + str).value);
		document.getElementById("rowNum").value = rowNum;
	}
	
		
	function changeAlarmLevelSelect(num , value){
		var daysArray = "";
		var str = num + "";
		while (str.length < 2) {
			str = "0" + str;
		}
		var levelSelect = document.getElementById("level-" + str);
		levelSelect.length = 0;
		for (var i = 0 ; i < daysArray.length; i++){
			levelSelect.options[i] = new Option(daysArray[i][1] , daysArray[i][0]);
		}
	}


	function delRow(keywordDivId, rowNo) {
		var str = "" + rowNo;
		while (str.length < 2) {
			str = "0" + str;
		}
		var i = 0;
		while (keywordDivId.rows[i].firstChild.firstChild.id != "table" + str) {
			i++;
		}
		keywordDivId.deleteRow(i);
	}
	
	function initAlarmWayByArray(alarmConfigDivId, store) {
		for (var i = 0; i < store.length; i++) {
			var item = store[i];
			initAlarmWay(alarmConfigDivId , item);
		}
	}
		
	function initAlarmKeywords(alarmKeywordsDivId , item){
		addRow(alarmKeywordsDivId, item.category);
		var str = "" + rowNum;
		if (str.length < 2) {
			str = "0" + str;
		}
		//document.getElementById("id-" + str).value = item.id;
		document.getElementById("keywords-" + str).value = item.keywords;
		document.getElementById("level-" + str).value = item.level;
	}
	
	// 获取短信分时详细信息的div
	function initAlarmWayDetail(){
		var rowNum = document.getElementById("rowNum");
		rowNum.value = "0";
		var alarmWayDetail = new Array();
		// 获取设备或服务的分时数据列表,
		<%	
			if(netSyslogNodeAlarmList!=null&&netSyslogNodeAlarmList.size()>=0){
				for(int i = 0 ; i < netSyslogNodeAlarmList.size(); i++){	        
		            NetSyslogNodeAlarmKey vo = (NetSyslogNodeAlarmKey)netSyslogNodeAlarmList.get(i);
		%>
		            alarmWayDetail.push({
		                id:"<%=vo.getId()%>",
		                keywords:"<%=vo.getKeywords()%>",
		                level:"<%=vo.getLevel()%>"
		            });
		<%
		        }
	        }
	    %>   
	    for(var i = 0; i< alarmWayDetail.length; i++){
	    	var item = alarmWayDetail[i];
	    	var alarmConfigDivId = "keywordsTable";
	    	initAlarmKeywords(alarmConfigDivId , item);
	    }
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
	
	initAlarmWayDetail();
	//initData();
}
		</script>
	</head>
	<body id="body" class="body""   onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<input type="hidden" name="nodeid" id="nodeid" value="<%=nodeid%>">
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
									                	<td class="content-title"> <b>告警 >> SYSLOG管理 >> SYSLOG过滤规则</b></td>
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
															<table width="100%" cellPadding=0 cellspacing="1" algin="center">
									                    					<tr algin="left" valign="center">                      														
									                      						<td height="28" bgcolor="#ECECEC" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;<b>级别</b></td>
									                    					</tr>
									                    					<tr align="left" valign="center" bgcolor=#ffffff> 
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="0" <%=str0%> name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;紧急</td>
									                    						<td height="28" align="right" width=10%%><INPUT type="checkbox" class=noborder value="1" <%=str1%> name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;报警</td> 
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="2" <%=str2%> name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;关键</td> 
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="3" <%=str3%> name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;错误</td>
									                    					</tr> 
									                    					<tr align="left" valign="center" bgcolor=#ffffff> 
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="4" <%=str4%> name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;警告</td>
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="5" <%=str5%> name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;通知</td>
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="6" <%=str6%> name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;提示</td>
									                    						<td height="28" align="right" width=10%><INPUT type="checkbox" class=noborder value="7" <%=str7%> name="fcheckbox"></td>
									                    						<td height="28" align="left" width=15%>&nbsp;&nbsp;调试</td>
									                    					</tr>  
									                    					<tr algin="left" valign="center">                      														
									                      						<td height="28" bgcolor="#ECECEC" colspan="8">&nbsp;&nbsp;&nbsp;&nbsp;<b>告警条件</b></td>
									                    					</tr>   
									                    					<tr>
													                            <td style="padding:0px 0px 0px 5px;" colspan="8">
													                            	<div id="formDiv" style="width: 100%;">         
																		                <table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none border=1 algin="center" >
																	                        <tr>
																	                            <td style="padding:0px 0px 0px 5px">&nbsp;&nbsp;关键字：
																								<input type="hidden" name="rowNum" id="rowNum"></td>
																	                        </tr>
																	                        <tr>
																	                            <td align="center">           
																	                                <table id="keywordsTable"  style="width:80%; padding:0;  background-color:#FFFFFF;" >
																                                        <tr>
																                                            <td colspan="0" height="50" align="center"> 
																                                                <span  onClick='addRow("keywordsTable");' style="border: 1px solid black;margin:10px;padding:0px 3px 0px 3px;cursor: hand;line-height:15px">增加一行</span>
																                                            </td>
																                                        </tr>
																	                                </table>
																	                            </td>
																	                        </tr>
																		                </table>
																		            </div> 
													                            </td>
													                        </tr>
									            							</table>
																		</td>
																	</tr>
																	<tr>
																			<TD nowrap colspan="20" align=center><br>
																			<input type="button" value="确 定" style="width:50" class="formStylebutton" onclick="toAdd()">&nbsp;&nbsp;
																			<input type="button" value="关 闭" style="width:50" onclick="window.close()">
																			</TD>	
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
