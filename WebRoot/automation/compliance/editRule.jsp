<%@page language="java" contentType="text/html;charset=gb2312" %>



<%@page import="java.util.*"%>
<%@page import="com.afunms.automation.model.CompRule"%>
<%@page import="com.afunms.automation.model.DetailCompRule"%>


<% 

	String rootPath = request.getContextPath(); 

    CompRule vo=(CompRule)request.getAttribute("vo");
    List detailRuleList=(List)request.getAttribute("detailRuleList");
    String content="";
    int id=vo.getId();
    String standard0="";
    String standard1="";
    String standard2="";
    String simpleConfig0="";
    String simpleConfig1="";
    String simpleConfig2="";
    String simpleConfig3="";
    String advanceConfig0="";
    String advanceConfig1="";
    String customConfig0="";
    String customConfig1="";
    String customValue="";
    String advanceValue="";
    String beginBlock="";
    String endBlock="";
    String extraConfig0="";
    String extraConfig1="";
    String extraConfig2="";
    String extra="";
     DetailCompRule rule=null;
    if(vo.getSelect_type()==0){
   
    if(detailRuleList!=null&&detailRuleList.size()>0){
     rule=(DetailCompRule)detailRuleList.get(0);
    content=rule.getExpression();
   // content=content.replaceAll(";;;;", "\r\n");
    System.out.println(content);
    if(rule.getIsContain()==0){
    simpleConfig0="selected";
    }else if(rule.getIsContain()==1){
     simpleConfig1="selected";
    }else if(rule.getIsContain()==2){
     simpleConfig2="selected";
    }else if(rule.getIsContain()==3){
     simpleConfig3="selected";
    }
    }
    standard0="checked";
    }else if(vo.getSelect_type()==1){
    
    standard1="checked";
    }else if(vo.getSelect_type()==2){
    standard2="checked";
    }
    String level0="";
    String level1="";
    String level2="";
    if(vo.getViolation_severity()==0){
    level0="checked";
    }else if(vo.getViolation_severity()==1){
    level1="checked";
    }else if(vo.getViolation_severity()==2){
    level2="checked";
    }
%>
<html>
<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="<%=rootPath%>/automation/js/jquery-1.8.2.min.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/automation/js/ext/ext-lang-zh_CN.js" charset="utf-8"></script>
<script type="text/javascript">


function deltr(index)
{
　　　　　$table=$("#advance_tab tr");
　　　　　{
　　　　　　$("tr[id='advance"+index+"']").remove();　

      　
　　　　　}　
}
//append向每个匹配的元素内追加内容

$(document).ready(function()
{
　　　$("#add_but").click(function()
        {
　　　　　　var $table=$("#advance_tab tr");
　　　　　　var len=$table.length;
　　　　　　$("#advance_tab").append("<tr id=advance"+(len)+"><td align='center'><select   id='advance_relation'"+(len)+"  class='formStyle'><option value='0'>或</option><option value='1'>与</option></select>&nbsp;&nbsp;&nbsp;<select   id='advance_config'"+(len)+"  class='formStyle'><option value='1'>包含</option><option value='0'>不包含</option></select>&nbsp;<input id='advance_value'"+(len)+" type='text' maxlength='200' size='50' class='formStyle'>&nbsp;<input id='advance_cut'"+(len)+"  type='button' maxlength='200' size='50' class='formStyle' value=' - 'onclick='deltr("+(len)+")'></td></tr>");　　
           
　　　　　})　
})


function customDeltr(index)
{
　　　　　$table=$("#custom_tab tr");
　　　　　{
　　　　　　$("tr[id='custom"+index+"']").remove();　

　　　　　}　
}
//append向每个匹配的元素内追加内容

$(document).ready(function()
{
　　　$("#custom_add").click(function()
        {
　　　　　　var $table=$("#custom_tab tr");
　　　　　　var len=$table.length;
　　　　　　$("#custom_tab").append("<tr id=custom"+(len)+"><td align='center'><select   id='custom_relation'"+(len)+"  class='formStyle'><option value='1'>或</option><option value='2'>与</option></select>&nbsp;&nbsp;&nbsp;<select   id='custom_config'"+(len)+"  class='formStyle'><option value='1'>包含</option><option value='0'>不包含</option></select>&nbsp;<input id='custom_value'"+(len)+" type='text' maxlength='200' size='50' class='formStyle'>&nbsp;<input id='custom_cut'"+(len)+"  type='button' maxlength='200' size='50' class='formStyle' value=' - 'onclick='customDeltr("+(len)+")'></td></tr>");　　
           
　　　　　})　
})
</script>


<script language="JavaScript" type="text/javascript">

 
function edit(){
  
   var arrayObj1= new Array(); 
   var arrayObj2 = new Array(); 
  
  var a=$("input[name='standard']:checked").val();
  if(a==1){
   $select=$("#advance_tab tr select");
　　$text=$("#advance_tab tr input:text");
   for(var i=0;i<$select.length;i++){
   arrayObj1[i]=$select.get(i).value;
       }
   for(var i=0;i<$text.length;i++){
   arrayObj2[i]=$text.get(i).value;
   }
  
  }else if(a==2){
      $selectc=$("#custom_tab tr select");
　　$textc=$("#custom_tab tr input:text");
  for(var i=0;i<$selectc.length;i++){
   arrayObj1[i]=$selectc.get(i).value;
       }
   for(var i=0;i<$textc.length;i++){
   arrayObj2[i]=$textc.get(i).value;
   }
  }
 $('#selVal').val(arrayObj1);
 $('#textVal').val(arrayObj2);
 mainForm.action = "<%=rootPath%>/configRule.do?action=edit";
 mainForm.submit();
 //window.close();
 //opener.location.href="<%=rootPath%>/configRule.do?action=ruleDetailList";

}

function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function simpleStandard(){
document.getElementById('simple').style.display = "block";
document.getElementById('advance').style.display = "none";
document.getElementById('custom').style.display = "none";
}
function advanceStandard(){
document.getElementById('simple').style.display = "none";
document.getElementById('advance').style.display = "block";
document.getElementById('custom').style.display = "none";
}
function customStandard(){
document.getElementById('simple').style.display = "none";
document.getElementById('advance').style.display = "none";
document.getElementById('custom').style.display = "block";
}
 Ext.onReady(function()
{  
  <%if(vo.getSelect_type()==0){
  
 %>
  simpleStandard();
  <%
  }else if(vo.getSelect_type()==1){
  if(detailRuleList!=null&&detailRuleList.size()>0){
  
    for(int i=0;i<detailRuleList.size();i++){
    rule=(DetailCompRule)detailRuleList.get(i);
    if(rule.getRelation()==-1){
      if(rule.getIsContain()==0){
            advanceConfig0="selected";
            }else if(rule.getIsContain()==1){
             advanceConfig1="selected";
            }
              advanceValue=rule.getExpression();
          }else{
       String relation0="";
       String relation1="";
       String contain0="";
       String contain1="";
       String express="";
       if(rule.getIsContain()==0){
       contain0="selected";
       }else if(rule.getIsContain()==1){
       contain1="selected";
       }
       if(rule.getRelation()==0){
       relation0="selected";
       }else if(rule.getRelation()==1){
       relation1="selected";
       }
       express=rule.getExpression();
       %> 
       　var $table=$("#advance_tab tr");
　　　　　　var len=$table.length;
　　　　　　$("#advance_tab").append("<tr id=advance"+(len)+"><td align='center'><select   id='advance_relation'"+(len)+"  class='formStyle'><option value='0'<%=relation0%>>或</option><option value='1' <%=relation1%>>与</option></select>&nbsp;&nbsp;&nbsp;<select   id='advance_config'"+(len)+"  class='formStyle'><option value='1' <%=contain1%>>包含</option><option value='0' <%=contain0%>>不包含</option></select>&nbsp;<input id='advance_value'"+(len)+" type='text' maxlength='200' size='50' class='formStyle' value='<%=express%>'/>&nbsp;<input id='advance_cut'"+(len)+"  type='button' maxlength='200' size='50' class='formStyle' value=' - 'onclick='deltr("+(len)+")'></td></tr>");　　
           
       <%
        }
       }
   }
   %>
  advanceStandard();
  <%
  }else if(vo.getSelect_type()==2){
    if(detailRuleList!=null&&detailRuleList.size()>0){
   
    for(int i=0;i<detailRuleList.size();i++){
    rule=(DetailCompRule)detailRuleList.get(i);
    if(!rule.getBeginBlock().equals("null"))
    beginBlock=rule.getBeginBlock();
    if(!rule.getEndBlock().equals("null"))
    endBlock=rule.getEndBlock();
    if(rule.getIsExtraContain()==-1){
    extraConfig0="selected";
    }else if(rule.getIsExtraContain()==0){
    extraConfig1="selected";
    }else if(rule.getIsExtraContain()==1){
    extraConfig2="selected";
    }
    if(!rule.getExtraBlock().equals("null"))
      extra=rule.getExtraBlock();
      
    if(rule.getRelation()==-1){
      if(rule.getIsContain()==0){
            customConfig0="selected";
            }else if(rule.getIsContain()==1){
             customConfig1="selected";
            }
              customValue=rule.getExpression();
          }else{
       String relation0="";
       String relation1="";
       String contain0="";
       String contain1="";
       String express="";
       if(rule.getIsContain()==0){
       contain0="selected";
       }else if(rule.getIsContain()==1){
       contain1="selected";
       }
       if(rule.getRelation()==0){
       relation0="selected";
       }else if(rule.getRelation()==1){
       relation1="selected";
       }
       express=rule.getExpression();
       %> 
       　var $table=$("#custom_tab tr");
　　　　　　var len=$table.length;
　　　　　　$("#custom_tab").append("<tr id=custom"+(len)+"><td align='center'><select   id='custom_relation'"+(len)+"  class='formStyle'><option value='0'<%=relation0%>>或</option><option value='1' <%=relation1%>>与</option></select>&nbsp;&nbsp;&nbsp;<select   id='custom_config'"+(len)+"  class='formStyle'><option value='1' <%=contain1%>>包含</option><option value='0' <%=contain0%>>不包含</option></select>&nbsp;<input id='custom_value'"+(len)+" type='text' maxlength='200' size='50' class='formStyle' value='<%=express%>'/>&nbsp;<input id='custom_cut'"+(len)+"  type='button' maxlength='200' size='50' class='formStyle' value=' - 'onclick='customDeltr("+(len)+")'></td></tr>");　　
           
       <%
        }
       }
   }
   %>
  
  customStandard();
  <%
  }
  %>

});

</script>

 


</head>
<body id="body"  >


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
										<td >
											<table id="add-content" class="add-content" border=1>
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header" >
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">&nbsp; 自动化>> 配置文件管理>> 策略管理>> 规则>> 修改规则</td>
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
				        										<input type="hidden" name="id" value="<%=id %>">
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
																			<tr>
																			    <td nowrap align="left" height="24" width="30%">规则名称</td>
																			    <td width="70%" align="left">：&nbsp;&nbsp;<input type="text" name="rule_name" maxlength="100" size="40" class="formStyle" value="<%=vo.getComprule_name()%>"></td>
													                  		</tr>
																			<tr> 
													  						    <td nowrap align="left" height="24" width="30%">描述：</td>
																			    <td width="70%" align="left">：&nbsp;&nbsp;<input type="text" name="des" maxlength="300" size="80" class="formStyle" value="<%=vo.getDescription() %>"></td>
																			</tr>
																			<tr> 
													  						    <td nowrap align="left" height="24" width="30%">选择标准：</td>
																			    <td width="70%" align="left">：&nbsp;&nbsp;
																			        <input type="radio" name="standard" onclick="simpleStandard()" value="0" <%=standard0 %>>简单标准&nbsp;&nbsp;
																			        <input type="radio" name="standard" onclick="advanceStandard()" value="1"<%=standard1 %>>高级标准&nbsp;&nbsp;
																			        <input type="radio" name="standard" onclick="customStandard()"  value="2" <%=standard2 %>>高级自定义标准&nbsp;&nbsp;
																			   </td>
																			</tr>
																			<tr>
																			    <td colspan="2">
																			     <hr/>
																			        <div id="simple" style="display:block;"> 
																			             <table>
																			            
																			                    <tr>
																			                         <td align=left ><font size="2" color="red">简单标准</font></td>
																			                         <td></td>
																			                    </tr>
																			                   <tr>
																			                   <td colspan=2>&nbsp;</td>
																			                   </tr>
																			                    <tr>
																			                         <td nowrap align="left" height="24" width="28%">配置文件</td>
																			                         <td width="72%" align="left">：&nbsp;&nbsp;
																			                             <select   name="simple_config"  class="formStyle">
																			                                     <option value="0" <%=simpleConfig0 %>>包含所有行</option>
																			                                     <option value="1" <%=simpleConfig1 %>>不包含任何行</option>
																			                                     <option value="2" <%=simpleConfig2 %>>应该包含集合</option>
																			                                     <option value="3" <%=simpleConfig3 %>>不应该包含集合</option>
																			                             </select>
                                                                                                     </td>
																			                        
																			                    </tr>
																			                    <tr>
																			                        <td nowrap align="left" height="24" width="28%">&nbsp;</td>
																			                        <td width="72%">&nbsp;&nbsp;&nbsp;&nbsp;<textarea name="content" id="content" rows="8" cols="77" ><%=content %></textarea></td>
																			                    </tr>
																			             </table>
																			        </div>
																			        <div id="advance" style="display:none;"> 
																			             <table>
																			                    <tr>
																			                         <td align=left ><font size="2" color="red">高级标准</font></td>
																			                         <td></td>
																			                    </tr>
																			                   <tr>
																			                         <td colspan=2>&nbsp;</td>
																			                   </tr>
																			                   <tr>
																			                         
																			                         <td width="100%" align="left" colspan=2>
																			                         <div style="height:100px;overFlow:auto;" align=center>配置文件：&nbsp;&nbsp;
                                                                                                         <select   name="advance_config"  class="formStyle">
																			                                     <option value="1" <%=advanceConfig1 %>>包含</option>
																			                                     <option value="0" <%=advanceConfig0 %>>不包含</option>
																			                             </select>
																			                             <input name="advance_value" type="text" maxlength="200" size="50" class="formStyle" value="<%=advanceValue %>">
																			                             <input name="add_but" id="add_but" type="button" maxlength="200" size="50" class="formStyle" value=" + ">&nbsp;&nbsp;&nbsp;&nbsp;
                                                                                                      <table id="advance_tab"></table>
                                                                                                     </div>
                                                                                                    
                                                                                                     </td>
																			                        
																			                    </tr>
																			                    
																			             </table>
																			        </div>
																			        <div id="custom" style="display:none;"> 
																			             <table>
																			                    <tr>
																			                         <td align=left ><font size="2" color="red">高级自定义标准</font></td>
																			                         <td>&nbsp;</td>
																			                    </tr>
																			                    <tr>
																			                         <td colspan=2>&nbsp;</td>
																			                    </tr>
																			                    <tr>
																			                        <td nowrap align="left" height="24" width="30%">配置起始块</td>
																			                        <td width="70%" align="left">：&nbsp;&nbsp;<input type="text" name="begin" maxlength="100" size="40" class="formStyle" value="<%=beginBlock %>"></td>
													                  		                    </tr>
																			                    <tr> 
													  						                        <td nowrap align="left" height="24" width="30%">配置结束块</td>
																			                        <td width="70%" align="left">：&nbsp;&nbsp;<input type="text" name="end" maxlength="100" size="40" class="formStyle" value="<%=endBlock %>"></td>
																			                    </tr>
																			                    <tr> 
													  						                        <td nowrap align="left" height="24" width="30%">附加的标准块</td>
																			                        <td width="70%" align="left">：&nbsp;&nbsp;
																			                        <select   name="isExtraContain"  class="formStyle">
																			                                     <option value="-1" <%=extraConfig0 %>>无</option>
																			                                     <option value="1" <%=extraConfig1 %>>包含</option>
																			                                     <option value="0" <%=extraConfig2 %>>不包含</option>
																			                         </select>
																			                        <input type="text" name="extra" maxlength="100" size="50" class="formStyle" value="<%=extra %>">
																			                        </td>
																			                    </tr>
																			                    <tr>
																			                         
																			                         <td width="100%" align="left" colspan=2>
																			                             <div style="height:100px;overFlow:auto;" align=center>
                                                                                                             配置文件：&nbsp;&nbsp;<select   name="custom_config"  class="formStyle">
																			                                     <option value="1" <%=customConfig1 %>>包含</option>
																			                                     <option value="0" <%=customConfig0 %>>不包含</option>
																			                                 </select>
																			                                     <input type="text" name="custom_value" id="custom_value" maxlength="100" size="50" class="formStyle" value="<%=customValue %>">
																			                                     <input name="custom_add" id="custom_add" type="button" maxlength="200" size="50" class="formStyle" value=" + ">&nbsp;&nbsp;
																			                                   <!--    <input name="custom_cut" id="custom_cut" type="button" maxlength="200" size="50" class="formStyle" value=" - ">--> 
                                                                                                                 <table id="custom_tab"></table>
                                                                                                        </div>
                                                                                                     </td>
																			                        
																			                    </tr>
																			                    
																			             </table>
																			        </div>
																			        <hr/>
																			    </td>
																			</tr>
																			<tr>
																			    <td nowrap align="left" height="24" width="30%">违反严重性</td>
																			    <td width="70%" align="left">：&nbsp;&nbsp;
																			        <input type="radio" name="level" value="0" <%=level0 %>>普通&nbsp;&nbsp;
																			        <input type="radio" name="level" value="1" <%=level1 %>>重要&nbsp;&nbsp;
																			        <input type="radio" name="level" value="2" <%=level2 %>>严重&nbsp;&nbsp;
																			   </td>
													                  		</tr>
																			<tr>
																			    <td nowrap align="left" height="24" width="30%">补充描述</td>
																			    <td width="70%" align="left">：&nbsp;&nbsp;<input type="text" name="add_des" maxlength="100" size="70" class="formStyle" value="<%=vo.getRemediation_descr() %>"></td>
													                  		</tr>
																			<tr>
																				<TD nowrap colspan="4" align=center colspan=2>
																				<br><input type="button" value="保 存" style="width:50" id="process2" onclick="edit()">&nbsp;&nbsp;
																					
																					<input type="reset" style="width:50" value="返 回" onclick="javascript:history.back(1)">
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