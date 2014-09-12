<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.automation.model.CompCheckResultModel"%>
<%@ include file="/automation/common/globe.inc"%>
<%@page import="java.util.List"%>

<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List list = (List)request.getAttribute("list");
  
  String id=(String)request.getAttribute("id");
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/automation/js/navbar.js"></script>
		<LINK href="<%=rootPath%>/automation/css/style.css" type="text/css"
			rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/automation/js/page.js"></script>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/automation/css/detail.css" rel="stylesheet"
			type="text/css">
		<link href="<%=rootPath%>/automation/css/mainstyle.css" rel="stylesheet"
			type="text/css">
		<script type="text/javascript">
			var listAction = "<%=rootPath%>/configRule.do?action=ready_addip";
	  		var delAction = "<%=rootPath%>/configRule.do?action=delete";
			
		</script>
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
		<script type="text/javascript">
			function chkall(){
				var checkall = document.getElementById("checkall");
				var checkboxes = document.getElementsByName("checkbox");
				for(var i = 0 ; i < checkboxes.length; i++){
					var checkbox = checkboxes[i];
					checkbox.checked = checkall.checked;
				}
			}
		</script>
		<script type="text/javascript">
			function radiochk(){
				var obj= document.getElementsByName("ipaddress");
				var Rev="";
			   for(var i=0;i<obj.length;i++)
			   {
			      if(obj[i].checked)
				     {
					     Rev=obj[i].value;
					     break;
				     }
			   }
			   return Rev;
			} 
				
			
	function submitip(){
			mainForm.action = "<%=rootPath%>/configRule.do?action=saveip";
            mainForm.submit();
	 			window.close();
	}
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container" width="100%">
				<tr>
					<td class="td-container-main">
					<input type="hidden" name="id" value="<%=id%>">
						<table id="container-main" class="container-main" width="100%">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content" width="100%">
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body" width="100%">
		        									<tr>
													<td>
														<table id="add-content-header" class="add-content-header" width="100%">
										                	<tr>
										      
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">运行策略详情</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        							</tr>
				        							
		        									<tr>
		        										<td>
		        											<table cellspacing="0" border="1" bordercolor="#ababab">
		        												<tr height=28 style="background:#ECECEC" align="center" class="content-title">
		        												
		        													<td align="center">是否违反</td>
		        													<td align="center">违反严重度</td>
		        													<td align="center">违反规则名称</td>
		        													<td align="center">所属组</td>
		        													<td align="center">描述</td>
		        												</tr>
		        												<%if(list!=null&&list.size()>0){
		        												for(int i=0;i<list.size();i++){
		        												CompCheckResultModel model=(CompCheckResultModel)list.get(i);
		        												String isViolation="是";
		        												if(model.getIsViolation()==1)
		        												  isViolation="否";
		        												  String level="";
		        												  if(model.getViolationSeverity()==0)
		        												  {
		        												 level="普通";
		        												  }else if(model.getViolationSeverity()==1){
		        												  level="重要";
		        												  }else if(model.getViolationSeverity()==2){
		        												  level="严重";
		        												  }
		        												%>
		        												
		        												<tr <%=onmouseoverstyle%>>
		        												
		        													<td align="center"><%=isViolation %></td>
		        													<td align="center"><%=level %></td>
		        													<td align="center"><%=model.getRuleName() %></td>
		        													<td align="center"><%=model.getGroupName() %></td>
		        													<td align="center"><%=model.getDescription() %></td>
		        												</tr>
		        												<% 
		        												}
		        												 }%>
		        												<tr style="background-color: #ECECEC;">
																<TD nowrap colspan="5" align=center>
																<br>
																	<input type="reset" style="width: 50" value="关 闭" onclick="window.close()">
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
