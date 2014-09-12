<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/automation/common/globe.inc"%>
<%@page import="java.util.*" %>

<%
	String rootPath = request.getContextPath();
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		
		<script type="text/javascript" src="<%=rootPath%>/automation/js/swfobject.js"></script>
		<script  type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/wfm.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />

		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/automation/css/ext/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-lang-zh_CN.js" charset="utf-8"></script>
        <script type="text/javascript" src="<%=rootPath%>/automation/js/jquery-1.8.2.min.js"></script>
		<script language="JavaScript" type="text/javascript">

		 Ext.onReady(function()
		{
		 Ext.get("process").on("click",function(){
		      var arrayObj1= new Array(); 
		      var arrayObj2 = new Array();
		       $select=$("#advance_tab tr select");
		　　$text=$("#advance_tab tr input:text");
		   for(var i=0;i<$select.length;i++){
		   arrayObj1[i]=$select.get(i).value;
		       }
		   for(var i=0;i<$text.length;i++){
		   arrayObj2[i]=$text.get(i).value;
		   }
		   $('#selVal').val(arrayObj1);
		  $('#textVal').val(arrayObj2); 
		    var chk1 = checkinput("ipaddress","string","网络设备",50,false);
		    if(chk1)
		    {
		    	Ext.MessageBox.wait('数据加载中，请稍后.. ');
		    	mainForm.action = "<%=rootPath%>/autoControl.do?action=saveTimingCmdScan";
		    	mainForm.submit();
		    }
		 });
		});


		//添加条件
		$(document).ready(function()
		{
		　　　$("#add_but").click(function()
		        {
		　　　　　　var $table=$("#advance_tab tr");
		　　　　　　var len=$table.length;
		　　　　　　$("#advance_tab").append("<tr id=advance"+(len)+"><td ><select   id='advance_config'"+(len)+" ><option value='1'>包含</option><option value='0'>不包含</option></select>&nbsp;<input id='advance_value'"+(len)+" type='text' maxlength='200' size='50' class='formStyle'>&nbsp;<input id='advance_cut'"+(len)+"  type='button' maxlength='200' size='50' class='formStyle' value=' - 'onclick='deltr("+(len)+")'></td></tr>");　　
		           
		　　　　　})　
		})
		function deltr(index)
		{
		　　　　　$table=$("#advance_tab tr");
		　　　　　{
		　　　　　　$("tr[id='advance"+index+"']").remove();　
		
		      　
		　　　　　}　
		}

		function timeType(obj){
			var type = obj.value;
			document.getElementById('td_sendtimehou').style.display='none';
			document.getElementById('td_sendtimeday').style.display='none';
			document.getElementById('td_sendtimeweek').style.display='none';
			document.getElementById('td_sendtimemonth').style.display='none';
			if(type==1){
				document.getElementById('td_sendtimehou').style.display='';
			}else if(type==2){
				document.getElementById('td_sendtimehou').style.display='';
				document.getElementById('td_sendtimeweek').style.display='';
			}else if(type==3){
				document.getElementById('td_sendtimehou').style.display='';
				document.getElementById('td_sendtimeday').style.display='';
			}else if(type==4){
				document.getElementById('td_sendtimehou').style.display='';
				document.getElementById('td_sendtimeday').style.display='';
				document.getElementById('td_sendtimemonth').style.display='';
			}else if(type==5){
				document.getElementById('td_sendtimehou').style.display='';
				document.getElementById('td_sendtimeday').style.display='';
				document.getElementById('td_sendtimemonth').style.display='';
			}
		}

			function showTelnetNetList(){
				var url="<%=rootPath%>/autoControl.do?action=multi_telnet_netip";
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
		
		</script>

	</head>
	<body id="body" class="body" >


		<form name="mainForm" method="post">

			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-add">
								<table id="container-main-add" class="container-main-add">
									<tr>
										<td>
											<table id="add-content" class="add-content">
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">&nbsp;定时命令扫描</td>
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
				        										<input type="hidden" name="selVal" id='selVal' value=""/>
                                                                 <input type="hidden" name="textVal" id='textVal' value=""/>
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
													 						<tr style="background-color: #ECECEC;">
																				
																				<TD nowrap align="right" height="24" width="20%">
																					网络设备&nbsp;
																				</TD>
																				<TD nowrap   >
																					&nbsp;
																					<input name="ipaddress" type="text" size="40" class="formStyle" id="ipaddress" readonly="readonly" >
																					<input type="button" value="选择网络设备" onclick="showTelnetNetList()"><font color="red">&nbsp;* </font>
																				</TD>  
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24" width="20%">定时执行时间&nbsp;</TD>
																				<td nowrap>
																			        <div id="formDiv" style="">         
																		                <table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none border=1 align="center" >
																	                        <tr>
																	                            <td align="left">  
																		                            <br>
																	                                <table id="timeConfigTable" style="width:60%; padding:0;  background-color:#FFFFFF; position:relative; left:15px;" >
																                                        <TBODY>
																                                        <!-- 
																										  <TR>
																										    <TD style="WIDTH: 100px">报表类型</TD>
																										    <TD>
																										    <SELECT style="WIDTH: 250px" id="reporttype" 
																										      onchange="javascript:reportType(this)" 
																										      name="reporttype"> <OPTION selected value="day">日报</OPTION> <OPTION 
																										        value="week">周报</OPTION> <OPTION value="month">月报</OPTION> <OPTION 
																										        value="season">季季</OPTION> <OPTION value="year">年报</OPTION></SELECT>
																										  </TD>
																										  </TR>
																										 -->
																										 <tr><TD style="WIDTH: 100px"><span>执行时间:</span></TD></tr>
																										 <tr><TD style="WIDTH: 100px">&nbsp;</TD></tr>
																										  <TR>
																										    <TD>
																										    <SELECT style="WIDTH: 250px" id=transmitfrequency 
																										      onchange="javascript:timeType(this)" 
																										      name=transmitfrequency> <OPTION value=1 selected >每天</OPTION> <OPTION 
																										        value=2>每周</OPTION> <OPTION value=3>每月</OPTION> <OPTION 
																										        value=4>每季</OPTION> <OPTION value=5>每年</OPTION></SELECT>
																										  </TD>
																										  </TR>
																										   <tr><TD style="WIDTH: 100px">&nbsp;</TD></tr>
																										  <TR>
																										    <TD style="display: none;" id=td_sendtimemonth><SELECT 
																										      style="WIDTH: 250px" id=sendtimemonth multiple size=5 
																										      name=sendtimemonth> <OPTION selected value=01>01月</OPTION> 
																										        <OPTION value=02>02月</OPTION> <OPTION value=03>03月</OPTION> <OPTION 
																										        value=04>04月</OPTION> <OPTION value=05>05月</OPTION> <OPTION 
																										        value=06>06月</OPTION> <OPTION value=07>07月</OPTION> <OPTION 
																										        value=08>08月</OPTION> <OPTION value=09>09月</OPTION> <OPTION 
																										        value=10>10月</OPTION> <OPTION value=11>11月</OPTION> <OPTION 
																										        value=12>12月</OPTION></SELECT>
																											</TD>
																										    <TD style="display: none;" id=td_sendtimeweek><SELECT 
																										      style="WIDTH: 250px" id=sendtimeweek multiple size=5 
																										      name=sendtimeweek> <OPTION selected value=0>星期日</OPTION> <OPTION 
																										        value=1>星期一</OPTION> <OPTION value=2>星期二</OPTION> <OPTION 
																										        value=3>星期三</OPTION> <OPTION value=4>星期四</OPTION> <OPTION 
																										        value=5>星期五</OPTION> <OPTION value=6>星期六</OPTION></SELECT>
																											</TD>
																										    <TD style="display: none;" id=td_sendtimeday><SELECT style="WIDTH: 250px" 
																										      id=sendtimeday multiple size=5 name=sendtimeday> <OPTION 
																										        selected value=01>01日</OPTION> <OPTION value=02>02日</OPTION> <OPTION 
																										        value=03>03日</OPTION> <OPTION value=04>04日</OPTION> <OPTION 
																										        value=05>05日</OPTION> <OPTION value=06>06日</OPTION> <OPTION 
																										        value=07>07日</OPTION> <OPTION value=08>08日</OPTION> <OPTION 
																										        value=09>09日</OPTION> <OPTION value=10>10日</OPTION> <OPTION 
																										        value=11>11日</OPTION> <OPTION value=12>12日</OPTION> <OPTION 
																										        value=13>13日</OPTION> <OPTION value=14>14日</OPTION> <OPTION 
																										        value=15>15日</OPTION> <OPTION value=16>16日</OPTION> <OPTION 
																										        value=17>17日</OPTION> <OPTION value=18>18日</OPTION> <OPTION 
																										        value=19>19日</OPTION> <OPTION value=20>20日</OPTION> <OPTION 
																										        value=21>21日</OPTION> <OPTION value=22>22日</OPTION> <OPTION 
																										        value=23>23日</OPTION> <OPTION value=24>24日</OPTION> <OPTION 
																										        value=25>25日</OPTION> <OPTION value=26>26日</OPTION> <OPTION 
																										        value=27>27日</OPTION> <OPTION value=28>28日</OPTION> <OPTION 
																										        value=29>29日</OPTION> <OPTION value=30>30日</OPTION> <OPTION 
																										        value=31>31日</OPTION></SELECT>
																											</TD>
																										    <TD style="" id=td_sendtimehou><SELECT 
																										      style="WIDTH: 250px" id=sendtimehou multiple size=5 
																										      name=sendtimehou> <OPTION value=00>00AM</OPTION> <OPTION 
																										        value=01>01AM</OPTION> <OPTION value=02>02AM</OPTION> <OPTION selected 
																										        value=03>03AM</OPTION> <OPTION value=04>04AM</OPTION> <OPTION 
																										        value=05>05AM</OPTION> <OPTION value=06>06AM</OPTION> <OPTION 
																										        value=07>07AM</OPTION> <OPTION value=08>08AM</OPTION> <OPTION 
																										        value=09>09AM</OPTION> <OPTION value=10>10AM</OPTION> <OPTION 
																										        value=11>11AM</OPTION> <OPTION value=12>12AM</OPTION> <OPTION 
																										        value=13>01PM</OPTION> <OPTION value=14>02PM</OPTION> <OPTION 
																										        value=15>03PM</OPTION> <OPTION value=16>04PM</OPTION> <OPTION 
																										        value=17>05PM</OPTION> <OPTION value=18>06PM</OPTION> <OPTION 
																										        value=19>07PM</OPTION> <OPTION value=20>08PM</OPTION> <OPTION 
																										        value=21>09PM</OPTION> <OPTION value=22>10PM</OPTION> <OPTION 
																										        value=23>11PM</OPTION></SELECT>
																											</TD>
																										  </TR>
																									  </TBODY>
																	                                </table>
																	                            </td>
																	                        </tr>
																		                </table>
																		            </div> 
																				</td>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="20%">
																					启动定时&nbsp;
																				</TD>
																				<TD nowrap   >
																					&nbsp;
																					<select name="status">
																						<option value="1" label="是">是</option>
																						<option value="0" label="否">否</option>
																					</select>
																				</TD>
																			</tr>
																			<tr >
																				<TD nowrap align="right" height="24" width="20%">
																					命令&nbsp;
																				</TD>
																				<TD nowrap   >
																					<input name="bkpType" type=hidden value='0'/>
																					<textarea id="content" name="content" rows="7" cols="85"  ></textarea>
																					
																				</TD>
																			</tr>
																			<tr>
																			    <TD nowrap align="right" height="24" width="20%" valign=top>
																					过滤条件&nbsp;
																				</TD>
																			    <td   nowrap valign=top >
																			       <div style="height:100px;overFlow:auto;" >
																			                         
                                                                                            <table id="advance_tab">
                                                                                                <tr> 
                                                                                                      <td>
                                                                                                          <select   name="advance_config"  class="formStyle">
																			                                     <option value="1">包含</option>
																			                                     <option value="0">不包含</option>
																			                             </select>
																			                             <input name="advance_value" type="text" maxlength="200" size="50" class="formStyle">
																			                             <input name="add_but" id="add_but" type="button" maxlength="200" size="50" class="formStyle" value=" + ">&nbsp;&nbsp;&nbsp;&nbsp;
                                                                                                      </td>
                                                                                                </tr>   
                                                                                            </table>
                                                                                     </div>
                                                                               </td>
																			</tr>
																			<tr>
																				<TD nowrap colspan="4" align=center > 
																				<br>
																				
																					<input type="button" id="process" style="width:50" value="确 定">&nbsp;&nbsp;  
																					<input type="reset" style="width: 50" value="返  回" onclick="javascript:history.back(1)">
																				</TD>	
																			</tr>	
																		</TABLE>
										 							
										 							
				        										</td>
				        									</tr>
				        								</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-footer" class="detail-content-footer">
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
				</td>
				</tr>
			</table>
		</form>
	</body>
</HTML>