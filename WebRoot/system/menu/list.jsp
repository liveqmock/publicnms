<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.model.Function"%>
<%@page import="java.util.ArrayList"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List<Function> allFunction = (List<Function>) request
			.getAttribute("allFunction");
	List<Function> allMenuRoot = new ArrayList<Function>();
	List<Function> secondMenu = new ArrayList<Function>();
	List<Function> thirdMenu = new ArrayList<Function>();
	for (int i = 0; i < allFunction.size(); i++) {
		Function function = allFunction.get(i);
		if (1 == function.getLevel_desc()) {
			allMenuRoot.add(function);
		} else if (2 == function.getLevel_desc()) {
			secondMenu.add(function);
		} else {
			thirdMenu.add(function);
		}
	}
%>

<html>
	<head></head>
	<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<script language="JavaScript" type="text/javascript"
		src="<%=rootPath%>/include/navbar.js"></script>
	<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

	<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
		rel="stylesheet" type="text/css" />


	<script language="JavaScript" type="text/javascript">

	function toAdd(){
		mainForm.action = "<%=rootPath%>/menu.do?action=ready_add";
		mainForm.submit();
	}
	
	function toDelete(){
		var deleteMenu = "";
		if(window.event.srcElement.id=="rootMenuDelete"){
			var rootMenuSelect = document.getElementById("rootMenu");
			deleteMenu = rootMenuSelect.options[rootMenuSelect.selectedIndex].text;
			mainForm.action = "<%=rootPath%>/menu.do?action=delete&level_desc=1";
		}else if(window.event.srcElement.id=="secondMenuDelete"){
			var secondMenuSelect = document.getElementById("rootMenu&child");
			deleteMenu = secondMenuSelect.options[secondMenuSelect.selectedIndex].text;
			mainForm.action = "<%=rootPath%>/menu.do?action=delete&level_desc=2";
		}else if(window.event.srcElement.id=="thirdMenuDelete"){
			var thirdMenuSelect = document.getElementById("rootMenu&child&child");
			deleteMenu = thirdMenuSelect.options[thirdMenuSelect.selectedIndex].text;
			mainForm.action = "<%=rootPath%>/menu.do?action=delete&level_desc=3";
		}
		if(window.confirm("是否删除"+deleteMenu+"菜单及其所有子菜单!!!")){
			mainForm.submit();
		}
		
	}
	
	function toEdit(){
		var check = false;
		if(window.event.srcElement.id=="rootMenuEdit"){
			var rootMenu = document.getElementById("rootMenu");
			if(rootMenu.value == ""){
				check = true;
			}
			mainForm.action = "<%=rootPath%>/menu.do?action=ready_edit&level_desc=1";
		}else if(window.event.srcElement.id=="secondMenuEdit"){
			var rootMenu = document.getElementById("rootMenu&child");
			if(rootMenu.value == ""){
				check = true;
			}
			mainForm.action = "<%=rootPath%>/menu.do?action=ready_edit&level_desc=2";
		}else if(window.event.srcElement.id=="thirdMenuEdit"){
			var rootMenu = document.getElementById("rootMenu&child&child");
			if(rootMenu.value == ""){
				check = true;
			}
			mainForm.action = "<%=rootPath%>/menu.do?action=ready_edit&level_desc=3";
		}
		if(check){
			alert("菜单不能为空");
			return;
		}
		if(window.confirm("修改菜单的等级将删除此菜单下所有子菜单!!!")){
			mainForm.submit();
		}
		
	}
  

</script>




	<script language="JavaScript" type="text/JavaScript">
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

	<body id="body" class="body" onload="initmenu();">
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
									                	<td class="content-title"> 系统管理 >> 用户管理 >> 菜单设置 </td>
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
																	<td class="body-data-title" style="text-align: right;">
																		<input id=menuAdd " class="button" type="button" value="增加"
																			onclick="toAdd();">
																		&nbsp;&nbsp;&nbsp;
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
																	<td align="center" class="body-data-title">
																		一级主菜单
																	</td>
																	<td align="center" class="body-data-title">
																		<select style="width: 150px" id="rootMenu"
																			name="rootMenuSelect"
																			onChange="getChildMenu(this,this.options[this.selectedIndex].value)">
																		</select>
																	</td>
																	<td align="center" class="body-data-title">
																		<input id="rootMenuEdit" class="button" type="button"
																			value="编辑" onclick="toEdit();">
																		<input id="rootMenuDelete" class="button" type="button"
																			value="删除" onclick="toDelete();">
						
																	</td>
																</tr>
																<tr>
																	<td align="center" class="body-data-title">
																		二级子菜单
																	</td>
																	<td align="center" class="body-data-title">
																		<select style="width: 150px" id="rootMenu&child"
																			name="secondMenuSelect"
																			onChange="getChildMenu(this,this.options[this.selectedIndex].value)">
																		</select>
																	</td>
																	<td align="center" class="body-data-title">
																		<input id="secondMenuEdit" class="button" type="button"
																			value="编辑" onclick="toEdit();">
																		<input id="secondMenuDelete" class="button" type="button"
																			value="删除" onclick="toDelete();">
						
																	</td>
																</tr>
																<tr>
																	<td align="center" class="body-data-title">
																		三级子菜单
																	</td>
																	<td align="center" class="body-data-title">
																		<select style="width: 150px" id="rootMenu&child&child"
																			name="thirdMenuSelect">
																		</select>
																	</td>
																	<td align="center" class="body-data-title">
																		<input id="thirdMenuEdit" class="button" type="button"
																			value="编辑" onclick="toEdit();">
																		<input id="thirdMenuDelete" class="button" type="button"
																			value="删除" onclick="toDelete();">
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
	</BODY>


	<script>
	function initMenuRoot(){  
		document.all.rootMenu.length = 0 ;
		
		
		<%
			for(int i = 0 ; i < allMenuRoot.size(); i++){
		%>
				document.all.rootMenu.options[<%=i%>] = 
				new Option("<%=allMenuRoot.get(i).getCh_desc()%>","<%=allMenuRoot.get(i).getId()%>");   
		<%
			}
		%>
		getChildMenu(document.all.rootMenu,document.all.rootMenu.options[0].value);   
	}
	
	function getChildMenu(fatherMenu,currMenu){
		var fatherMenuSelectId = fatherMenu.id;
		var currMenuSelect = document.getElementById(fatherMenuSelectId+"&child");
		currMenuSelect.length = 0 ;
		var fatherMenuCurrMenu = currMenu;
		var functionFather = "";
		var childArray = new Array();
		var childflag = 0;
		
		<%
			for(int i = 0 ; i < allFunction.size(); i++){
				Function functionChild = allFunction.get(i);
				%>
				if(fatherMenuCurrMenu=="<%=functionChild.getFather_node()%>"){
					childArray[childflag] = new Array("<%=functionChild.getCh_desc()%>","<%=functionChild.getId()%>");
					childflag++;					
				}
				<%
				
			}
					
		%>
		
		for (i = 0 ;i <childArray.length;i++)   
      	{                   
			currMenuSelect.options[i] = new Option(childArray[i][0],childArray[i][1]);                     
		}	
		
		getChildMenu(currMenuSelect,currMenuSelect.options[0].value);
		
	
	}
	
	initMenuRoot(); 
</script>

</HTML>
