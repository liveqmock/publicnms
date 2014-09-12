<%@page language="java" contentType="text/html;charset=gb2312" %>

<%@page import="java.util.*"%>
<%@page import="com.afunms.automation.dao.CompRuleDao"%>
<%@page import="com.afunms.automation.model.CompRule"%>
<%@page import="com.afunms.automation.model.CompCheckRule"%>


<% 

	String rootPath = request.getContextPath(); 
	
    String id=(String)request.getAttribute("ruleId");
    List list=(List)request.getAttribute("list");
    String isVor=(String)request.getAttribute("isVor");
    CompRuleDao ruleDao=new CompRuleDao();
   CompRule compRule= (CompRule)ruleDao.findByID(id);
   StringBuffer sb=new StringBuffer();
   CompCheckRule vo=null;
  if(list!=null&&list.size()>0){
  for(int i=0;i<list.size();i++){
   vo=(CompCheckRule)list.get(i);
   sb.append(vo.getContent()+"\r\n");
  }
 
  }
  String status="";//是否违反规则
  String statusImg="";
  if(isVor.equals("0")){
  status="违反";
  statusImg="<img src='/"+rootPath+"/img/error.gif'>";
  }else if(isVor.equals("1")){
  status="合规";
  statusImg="<img src='/"+rootPath+"/img/correct.gif'>";
  }
  String level="";
  String levelImg="";
  if(compRule.getViolation_severity()==0){
  level="普通";
  levelImg="<img src='/"+rootPath+"/img/common.gif'>";
  }else if(compRule.getViolation_severity()==1){
  level="重要";
  levelImg="<img src='/"+rootPath+"/img/serious.gif'>";
  }else if(compRule.getViolation_severity()==2){
  level="严重";
  levelImg="<img src='/"+rootPath+"/img/urgency.gif'>";
  }
  
  String contain="";
  String type="";
  if(compRule.getSelect_type()==0){
  type="简单标准";
  if(vo.getIsContain()==0){
  contain="包含所有行";
  }else if(vo.getIsContain()==1){
   contain="不包含任何行";
  }else if(vo.getIsContain()==2){
   contain="应该包含集合";
  }else if(vo.getIsContain()==3){
   contain="不应该包含集合";
  }
  }else if(compRule.getSelect_type()==1){
  type="高级标准";
  }else if(compRule.getSelect_type()==2){
  type="高级自定义标准";
  }
 
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



function CreateWindow(url)
{
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no");
}    


</script>


</head>
<body id="body" >


	<!-- 右键菜单结束-->
   <form name="mainForm" method="post">
		<table id="body-container" class="body-container">
			<tr>
				<td class="td-container-main" bgcolor="#FFFFFF">
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
											                	<td class="add-content-title">&nbsp; 自动化>> 配置文件管理>> 合规性管理>>规则明细</td>
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
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
																			<tr>
																			    <td nowrap align="left" height="30" width="20%">规则名称&nbsp;&nbsp;：</td>
																			    <td width="80%" height="30" align="left"><%=compRule.getComprule_name() %></td>
													                  		</tr>
																			<tr>
																			    <td nowrap align="left" height="30" width="20%">规则结果&nbsp;&nbsp;：</td>
																			    <td width="80%" height="30" align="left"><%=statusImg%> <%=status %></td>
													                  		</tr>
																			<tr>
																			    <td nowrap align="left" height="30" width="20%">违反严重度：&nbsp;&nbsp;</td>
																			    <td width="80%"  height="30" align="left" valign=middle><%=levelImg %> <%=level %></td>
													                  		</tr>
																			<tr>
																			    <td nowrap align="left" height="30" width="20%">标准类型&nbsp;&nbsp;：</td>
																			    <td width="80%"  height="30" align="left" valign=middle><%=type %></td>
													                  		</tr>
																			<tr>
																			     <td nowrap align="left" height="30" width="20%" valign=top> 描&nbsp;&nbsp;述&nbsp;&nbsp;&nbsp;&nbsp;：</td>
																			     <td width="80%" height="30"><%=compRule.getDescription() %></td>
																		   </tr>
																		   <% if(compRule.getSelect_type()==0){
																		   %>
																		   <tr>
																			     <td nowrap align="left" height="30" width="20%" valign=top> 规则标准&nbsp;&nbsp;：</td>
																			     <td width="80%" height="30"><%=contain %></td>
																		    </tr>
																		    <tr>
																			     <td nowrap align="left"  width="20%" valign=top> 规则内容&nbsp;&nbsp;：</td>
																			     <td width="80%" >
																			     
																			          <div >
																			              <table bgcolor="#F8F8FF" >
																			              <%
																			             
																			               if(list!=null&&list.size()>0){
                                                                                                for(int i=0;i<list.size();i++){
                                                                                                   CompCheckRule checkRule=(CompCheckRule)list.get(i);
                                                                                                   // sb.append(vo.getContent()+"\r\n"+"sss");
                                                                                                    String content=checkRule.getContent();
                                                                                                    
                                                                                                    if(checkRule.getIsViolation()==0){
                                                                                                    %>
                                                                                                    <tr>
																			                        <td><font color="red"><%=content%></font></td>
																			                         </tr>
                                                                                                    <%
                                                                                                    }else if(checkRule.getIsViolation()==1){
                                                                                                     %>
                                                                                                    <tr>
																			                        <td><font color="black"><%=content %></font></td>
																			                         </tr>
                                                                                                    <%
                                                                                                    }
                                                                                             }
 
                                                                                           } 
                                                                                           
																			               %>
																			                     
																			              </table>
																			          </div>
                                                                                 </td>
																		   </tr>
																		   <%
																		   }else if(compRule.getSelect_type()==1||compRule.getSelect_type()==2){
																		    CompCheckRule checkRule=null;
																		    int isExist=0;
																		   if(compRule.getSelect_type()==1){
																		    %>
																		   <tr>
																			     <td nowrap align="left" height="30"  colspan=2> 高级标准&nbsp;&nbsp;：</td>
																			     
																		    </tr>
																		    <%}else if(compRule.getSelect_type()==2){
																		   
																		    String extra="";
																		    String title="";
																		    String content="";
																		    
																		         for(int i=0;i<list.size();i++){
                                                                                                 checkRule=(CompCheckRule)list.get(i);
                                                                                                 if(checkRule.getRelation()==-2){
                                                                                               isExist=checkRule.getIsViolation();
                                                                                                   content=checkRule.getContent();
                                                                                                   title="起始块";
                                                                                                  }else if(checkRule.getRelation()==-3){
                                                                                                   content=checkRule.getContent();
                                                                                                   title="结束块";
                                                                                                  }else if(checkRule.getRelation()==-4){
                                                                                                  extra=checkRule.getContent();
                                                                                                   title="附加块";
                                                                                                  if(checkRule.getIsContain()==-1){
                                                                                                  content="无";
                                                                                                  }else if(checkRule.getIsContain()==0){
                                                                                                  content="不包含"+"  "+extra;
                                                                                                  }else if(checkRule.getIsContain()==1){
                                                                                                   content="包含"+"  "+extra;
                                                                                                  }
                                                                                                  }else{
                                                                                                  continue;
                                                                                                  }
                                                                                                  %>
                                                                            <tr>
																			     <td nowrap align="left" height="30" width="20%" valign=top> <%=title %>&nbsp;&nbsp;：</td>
																			     <td width="80%" height="30"><%=content %></td>
																		    </tr> 
                                                                                                  <% 
                                                                                                  }
                                                                                               
																		     %>
																		     <tr>
																			     <td nowrap align="left" height="30"  colspan=2> 高级自定义标准</td>
																			     
																		    </tr>
																		    <%} 
																		    if(isExist==0&&compRule.getSelect_type()==2)
																		    {
																		    %>
																		    <tr>
																			     <td nowrap align="center" height="30"  colspan=2>
																			      <font color=red>未找到符合配置的标准块&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>
																			     </td>
																			     <% 
																		    }else{
																		    %>
																		   <tr>
																			     <td nowrap align="left" height="30"  colspan=2>
																			         <div>
																			              <table bgcolor="#F8F8FF" >
																			                    <tr>
																			                        <td height="30" align=center>序号</td>
																			                        <td height="30" align=center>关系</td>
																			                        <td height="30" align=center>条件</td>
																			                        <td height="30" align=center>模式</td>
																			                        <td height="30" align=center>结果</td>
																			                    </tr>
																			                    <%
																			                    if(list!=null&&list.size()>0){
																			                    String relation="";
																			                    String isContain="";
																			                    String isCompare="";
																			                    String isCompareImg="";
																			                    int temp=0;
                                                                                                for(int i=0;i<list.size();i++){
                                                                                                  checkRule=(CompCheckRule)list.get(i);
                                                                                                  int re=checkRule.getRelation();
                                                                                                   temp++;
                                                                                                 if(re==0){
                                                                                                   relation="or";
                                                                                                  }else if(re==1){
                                                                                                   relation="and";
                                                                                                  }else if(re==-2||re==-3||re==-4){
                                                                                                  temp=temp-1;
                                                                                                 continue;
                                                                                                  }else{
                                                                                                  relation="";
                                                                                                  }
                                                                                                  if(checkRule.getIsContain()==0){
                                                                                                  isContain="不包含";
                                                                                                  }else if(checkRule.getIsContain()==1){
                                                                                                   isContain="包含";
                                                                                                  }
                                                                                                  if(checkRule.getIsViolation()==0){
                                                                                                  isCompare="不匹配";
                                                                                                  isCompareImg="<img src='/afunms/img/failed.gif'>";
                                                                                                  }else if(checkRule.getIsViolation()==1){
                                                                                                  isCompare="匹配";
                                                                                                  isCompareImg="<img src='/afunms/img/success.gif'>";
                                                                                                  }
                                                                                                 
                                                                                                 %>
                                                                                                 <tr>
                                                                                                     <td  height="28" align=center><%=temp%></td>
                                                                                                     <td height="28" align=center><%=relation %></td>
                                                                                                     <td height="28" align=center><%=isContain %></td>
                                                                                                     <td height="28" align=center><%=checkRule.getContent() %></td>
                                                                                                     <td height="28" align=center><%=isCompareImg%> <%=isCompare %></td>
                                                                                                 </tr>
                                                                                                 <%
                                                                                                  }
                                                                                                 } 
                                                                                                 %>
																			              </table>
																			         </div>
                                                                                 </td>
																			     
																		    </tr>
																		   <%
																		   }
																		   } 
																		   %>
																			
																	
																		   
																		        
																			
																			<tr>
																				<TD nowrap colspan="2" align=center colspan=2>
																				<br>
																					<input type="reset" style="width:50" value="关闭" onclick="window.close();">
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