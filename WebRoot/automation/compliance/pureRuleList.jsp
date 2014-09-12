<%@page language="java" contentType="text/html;charset=gb2312" %>

<%@page import="java.util.*"%>
<%@page import="com.afunms.automation.model.CompRule"%>
<%@page import="com.afunms.automation.dao.DetailCompRuleDao"%>
<%@page import="com.afunms.automation.model.DetailCompRule"%>


<% 

	String rootPath = request.getContextPath(); 
    List list=(List)request.getAttribute("list");
  
%>

<html>
<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script> 
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/automation/js/navbar.js"></script>
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/automation/css/ext/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-lang-zh_CN.js" charset="utf-8"></script>

        <script type="text/javascript" src="<%=rootPath%>/automation/js/jquery-1.8.2.min.js"></script>


<script language="JavaScript" type="text/javascript">

function updateGroupRule(){

 Ext.MessageBox.wait('数据加载中，请稍后.. ');
 mainForm.action = "<%=rootPath%>/configRule.do?action=updateGroupRule";
 mainForm.submit();
 parent.opener.location.href="<%=rootPath%>/configRule.do?action=groupRuleList";
 window.close();
}

function CreateWindow(url)
{
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no");
}    

function showDetail(id,divId){

document.getElementById(id).innerHTML="<span  onclick=\"hiddenDetail(\'"+id+"\',\'"+divId+"\')\" >&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;</span>";
document.getElementById(divId).style.display = "block";

}
function hiddenDetail(id,divId){
document.getElementById(id).innerHTML="<span  onclick=\"showDetail(\'"+id+"\',\'"+divId+"\')\">&nbsp;&nbsp;+&nbsp;&nbsp;&nbsp;</span>";
document.getElementById(divId).style.display = "none";

}
</script>


</head>
<body id="body" >


	<!-- 右键菜单结束-->
   <form name="mainForm" method="post">
		<table id="body-container" class="body-container">
			<tr>
				<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-add">
								<table id="container-main-add" class="container-main-add">
									<tr>
										<td bgcolor="#FFFFFF">
											<table id="add-content" class="add-content" border=1>
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header" >
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">&nbsp; 顺应性规则组明细</td>
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
				        										
				        												<table border="0" id="table1" cellpadding="0"  width="100%">
																			<tr>
																		        <td colspan=2>
																		            <div id="groupRuleTitle">
																		                 <table>
																		                        <tr>
																		                            <td class="report-data-body-title" width="10%">序号</td>
																		                            <td class="report-data-body-title" width="45%">规则名称</td>
																		                            <td class="report-data-body-title" width="45%">描述</td>
																		                        </tr>
																		                 </table>     
																			        </div>
																			        <div id="groupRule" style="height:200px;overFlow:auto;">
																			             <table >
																			             <%if(list!=null&&list.size()>0){
																			                for(int i=0;i<list.size();i++){
																			                 CompRule compRule=(CompRule)list.get(i);
																	                         String severity="";
																	                         String type="";
																	                         DetailCompRuleDao dao=new DetailCompRuleDao();
																	                          List detailList=dao.findByCondition(" where RULEID="+compRule.getId());
																	                          DetailCompRule detailCompRule=null;
																	                          String content="";
																	                          String express="";
																	                          String relation="";
																	                          String contain="";
																	                          String expression=""; 

																	                          
																	                       if(compRule.getViolation_severity()==0){
																	                          severity="普通";
																	                         }else if(compRule.getViolation_severity()==1){
																	                          severity="重要";
																	                         }else if(compRule.getViolation_severity()==2){
																	                          severity="严重";
																	                          }
																	                          if(compRule.getSelect_type()==0){
																	                            type="简单标准";
																	                            if(detailList!=null&&detailList.size()>0){
																	                            detailCompRule=(DetailCompRule)detailList.get(0);
																	                            if(detailCompRule.getIsContain()==0){
																	                            	content="配置文件 包含所有行";
																	                            }else if(detailCompRule.getIsContain()==1){
																	                            	content="配置文件 不包含任何行";
																	                            }else if(detailCompRule.getIsContain()==2){
																	                            	content="配置文件 应该包含集合";
																	                            }else if(detailCompRule.getIsContain()==3){
																	                            	content="配置文件 不应该包含集合";
																	                            }
																	                            express=detailCompRule.getExpression();
																	                            express=express.replaceAll(";;;;", "\r\n");
																	                            }
																	                          }else if(compRule.getSelect_type()==2){
																	                          
																	                            type="高级自定义标准";
																	                            
																	                          }
                                                                                              if(compRule.getSelect_type()==1||compRule.getSelect_type()==2){
																	                          
																	                          if(compRule.getSelect_type()==1)
																	                            type="高级标准";
																	                      
																	                          }
																	                        
																			              %>
																			                    <tr >
																			                        <td align="center"  width="10%" height=30><%=i+1 %></td>
																			                        <td  align="left"  width="45%" height=30><span id="show<%=i %>"><span  onclick="showDetail('show<%=i %>','group<%=i %>')" >&nbsp;&nbsp;+&nbsp;&nbsp;</span></span><%=compRule.getComprule_name() %></td>
																			                        <td align="left"  width="45%" height=30><%=compRule.getDescription() %></td>
																			                    </tr>
																			                     <tr>
																			                         <td colspan=3>
																			                             <div id="group<%=i %>" style="display:none;background:#8470FF"> 
																			                                 <table>
																			                                        <tr>
																			                                            <td nowrap align="left" height="24" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;违反严重度</td>
																			                                            <td width="80%" align="left">：&nbsp;&nbsp;<%=severity %></td>
																			                                        </tr>
																			                                        <tr>
																			                                            <td nowrap align="left" height="24" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;标准类型</td>
																			                                            <td width="80%" align="left">：&nbsp;&nbsp;<%=type %></td>
																			                                        </tr>
																			                                        <tr>
																			                                            <td nowrap align="left" height="24" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;补充描述</td>
																			                                            <td width="80%" align="left">：&nbsp;&nbsp;<%=compRule.getRemediation_descr() %></td>
																			                                        </tr>
																			                                        <% if(compRule.getSelect_type()==2){
																			                                         if(detailList!=null&&detailList.size()>0){
																			                                         detailCompRule=(DetailCompRule)detailList.get(0);
																			                                         String beginBlock=detailCompRule.getBeginBlock();
																			                                         String endBlock=detailCompRule.getEndBlock();
																			                                         String extraBlock=detailCompRule.getExtraBlock();
																			                                         int isExtra=detailCompRule.getIsExtraContain();
																			                                         String isExtraContain="";
																			                                         if(isExtra==0){
																			                                         isExtraContain="附加块 不包含：";
																			                                          }else if(isExtra==1){
																			                                          isExtraContain="附加块 包含：";
																			                                          }
																			                                         if(beginBlock!=null&&!beginBlock.equals("null")){
																			                                          %>
																			                                            <tr>
																			                                            <td nowrap align="left" height="24" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;起始块</td>
																			                                            <td width="80%" align="left">：&nbsp;&nbsp;<%=beginBlock %></td>
																			                                        </tr>
																			                                          <%
																			                                          }
																			                                           if(endBlock!=null&&!endBlock.equals("null")){
																			                                          %>
																			                                            <tr>
																			                                            <td nowrap align="left" height="24" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;起始块</td>
																			                                            <td width="80%" align="left">：&nbsp;&nbsp;<%=beginBlock %></td>
																			                                        </tr>
																			                                          <%
																			                                          }
																			                                           if(isExtra!=-1){
																			                                          %>
																			                                            <tr>
																			                                            <td nowrap align="left" height="24" width="20%"><%=isExtraContain %></td>
																			                                            <td width="80%" align="left">：&nbsp;&nbsp;<%=extraBlock %></td>
																			                                        </tr>
																			                                          <%
																			                                            }
																			                                           }
                                                                                                                     }
																			                                        if(compRule.getSelect_type()==0){ %>
																			                                        <tr>
																			                                            <td nowrap align="left" height="24" width="20%">&nbsp;&nbsp;&nbsp;&nbsp;规则标准</td>
																			                                            <td width="80%" align="left">：&nbsp;&nbsp;<%=content %></td>
																			                                        </tr>
																			                                        <tr>
																			                                            <td nowrap align="left" height="24" width="100%" colspan=2>
																			                                            <textarea name="content" id="content" rows="8" cols="93"><%=express %></textarea>
																			                                            </td>
																			                                            
																			                                        </tr>
																			                                        <%}else if(compRule.getSelect_type()==1||compRule.getSelect_type()==2){ %>
																			                                        
																			                                           <tr>
																			                                            <td colspan=2 width="100%">
																			                                                <table>
																			                                                <br/>
																			                                                        <tr>
																			                                                             <td width="10%" align="center">关系</td>
																			                                                             <td width="25%" align="center">条件</td>
																			                                                             <td width="65%" align="center">模式</td>
																			                                                        </tr>
																			                                                        <%    
																			                                                           if(detailList!=null&&detailList.size()>0){
																	                                                                      for(int j=0;j<detailList.size();j++){
																	                                                                     detailCompRule=(DetailCompRule)detailList.get(j);
																	                                                                   if(detailCompRule.getRelation()==-1){
																	                                                                        relation="";
																	                                                                     }else if(detailCompRule.getRelation()==0){
																	                                                                        relation="或";
																	                                                                     }else if(detailCompRule.getRelation()==1){
																	                                                                        relation="与";
																	                                                                     }
																	                                                                     
																	                                                                 if(detailCompRule.getIsContain()==0){
																	                            	                                       contain="不包含";
																	                                                                   }else if(detailCompRule.getIsContain()==1){
																	                            	                                       contain="包含";
																	                                                                   }
																	                                                                       expression=detailCompRule.getExpression();
																	                                                                   %>
																			                                                        <tr>
																			                                                             <td width="10%" align="center"><%=relation %></td>
																			                                                             <td width="25%" align="center"><%=contain %></td>
																			                                                             <td width="65%" align="center"><%=expression %></td>
																			                                                        </tr>
																			                                                        <%
																			                                                        }
																	                                                             } 
																	                                                             %>
																			                                                </table>
																			                                            </td>
																			                                        </tr> 
																			                                        <%} %>
																			                                 </table>
																			                             </div>
																			                         </td>
																			                    </tr>
																			                    <%
																			                      } 
																			                    }    
																			                    %>
																			             </table>
																			        </div>
																		        </td>
																			</tr>
																			<tr>
																				<TD nowrap  align=center colspan=2>
																				<br><input type="reset" style="width:50" value="关 闭" onclick="window.close();">
																				</TD>	
																			</tr>	
																		</TABLE>
										 							
										 							
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
</BODY>


</HTML>