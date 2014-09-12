<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script src="<%=rootPath%>/js/dynatree/jquery.js" type="text/javascript"></script>
	<script src="<%=rootPath%>/js/dynatree/jquery-ui.custom.js" type="text/javascript"></script>
	<script src="<%=rootPath%>/js/dynatree/jquery.cookie.js" type="text/javascript"></script>

	<link href="<%=rootPath%>/css/dynatree/ui.dynatree.css" rel="stylesheet" type="text/css">
	<script src="<%=rootPath%>/js/dynatree/jquery.dynatree.js" type="text/javascript"></script>

	<!-- jquery.contextmenu,  A Beautiful Site (http://abeautifulsite.net/) -->
	<script src="<%=rootPath%>/js/dynatree/contextmenu/jquery.contextMenu-custom.js" type="text/javascript"></script>
	<link href="<%=rootPath%>/js/dynatree/contextmenu/jquery.contextMenu.css" rel="stylesheet" type="text/css" >

	<!-- Start_Exclude: This block is not part of the sample code -->
	
	<script src="<%=rootPath%>/js/dynatree/prettify.js" type="text/javascript"></script>
	
	<script src="<%=rootPath%>/js/dynatree/sample.js" type="text/javascript"></script>	
			
		
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
<script type="text/javascript">
// --- Implement Cut/Copy/Paste --------------------------------------------
	var clipboardNode = null;
	var pasteMode = null;

	function copyPaste(action, node) {
		switch( action ) {
		case "cut":
		case "copy":
			clipboardNode = node;
			pasteMode = action;
			break;
		case "paste":
			if( !clipboardNode ) {
				alert("Clipoard is empty.");
				break;
			}
			if( pasteMode == "cut" ) {
				// Cut mode: check for recursion and remove source
				var isRecursive = false;
				var cb = clipboardNode.toDict(true, function(dict){
					// If one of the source nodes is the target, we must not move
					if( dict.key == node.data.key )
						isRecursive = true;
				});
				if( isRecursive ) {
					alert("Cannot move a node to a sub node.");
					return;
				}
				node.addChild(cb);
				clipboardNode.remove();
			} else {
				// Copy mode: prevent duplicate keys:
				var cb = clipboardNode.toDict(true, function(dict){
					dict.title = "Copy of " + dict.title;
					delete dict.key; // Remove key, so a new one will be created
				});
				node.addChild(cb);
			}
			clipboardNode = pasteMode = null;
			break;
		default:
			alert("Unhandled clipboard action '" + action + "'");
		}
	};
	function checkform() {
	if(""==$("#nametext").val()||null==$("#nametext").val()||"null"==$("#nametext").val()) {
	alert("请输入名称");
	return false;
	}
	return true;
	}
  function filltable(action,node) {
  switch( action ) {
		case "copy":
	$("#nametext").val("");
    $("#desctext").val("");
    $("#keytext").val(node.data.key);
    $("#myform").attr("action","<%=rootPath%>/bpmnodetype.do?action=save");
    $("#messtable").show();
	break;
		case "edit":
    $("#nametext").val(node.data.title);
    $("#desctext").val(node.data.tooltip);
    $("#keytext").val(node.data.key);
    $("#messtable").show();
	break;
		case "delete":
		 $("#keytext").val(node.data.key);
		 $("#nametext").val("1");
		 $("#myform").attr("action","<%=rootPath%>/bpmnodetype.do?action=delete");
		  $("#myform").submit();
		}
   
    
  }
	// --- Contextmenu helper --------------------------------------------------
	function bindContextMenu(span) {
		// Add context menu to this node:
		$(span).contextMenu({menu: "myMenu"}, function(action, el, pos) {
			// The event was bound to the <span> tag, but the node object
			// is stored in the parent <li> tag
			var node = $.ui.dynatree.getNode(el);
			switch( action ) {
			case "copy":
			   filltable(action,node);
               break;
			case "edit":
			   filltable(action,node);
               break;
            case "delete":
             filltable(action,node);
             break;
			default:
				alert("Todo: appply action '" + action + "' to node " + node);
			}
		});
	};

	// --- Init dynatree during startup ----------------------------------------

	$(function(){

		$("#tree").dynatree({
			persist: true,
            // checkbox:true,
            // selectMode:1,
           // classNames: {checkbox: "dynatree-radio"},
            fx: { height: "toggle", duration: 200 },
			initAjax:{
			     url:"<%=rootPath%>/bpmnode.ajax?action=list"
			},
			onActivate: function(node) {
			 $("#messtable").hide();
			},
			onClick: function(node, event) {
				// Close menu on click
				if( $(".contextMenu:visible").length > 0 ){
					$(".contextMenu").hide();
//					return false;
				}
			},
			
			/*Bind context menu for every node when it's DOM element is created.
			  We do it here, so we can also bind to lazy nodes, which do not
			  exist at load-time. (abeautifulsite.net menu control does not
			  support event delegation)*/
			onCreate: function(node, span){
				bindContextMenu(span);
			}
		});
	});
</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td >
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
									                	<td class="content-title"> 系统管理 >> 数据字典管理 >> 数据字典列表 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
										</tr>
										<tr>
										<td>
										<table border="0">
										<tr>
										<td>
										<!-- Definition of context menu -->
	<ul id="myMenu" class="contextMenu">
	    <li class="copy"><a href="#copy">增加</a></li>
		<li class="edit"><a href="#edit">编辑</a></li>
		<li class="delete"><a href="#delete">删除</a></li>
		
	</ul>
	<!-- Definition tree structure -->
	<div id="tree" >
		
	</div>
										
										
										</td>
										<td style="background-color: white;">
										<form method="POST" id="myform" action="<%=rootPath%>/bpmnodetype.do?action=update" onsubmit="return checkform();">
`										<table border="0" style="display: none;" id="messtable"  >
										<tr>
										<td>名称</td>
										<td><input type="text" value="" name="nametext" id="nametext"/> </td>
										</tr>
										<tr>
										<td>描述</td>
										<td><input type="text" value="" name="desctext" id="desctext"/> </td>
										</tr>
										<tr style="display: none">
										<td>KEY</td>
										<td><input type="text" value="" name="keytext" id="keytext"/> </td>
										</tr>
										<tr>
										<td colspan="2" align="left"><br>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="保存"/>
										</td>
										</tr>
										</table>
										</form>
										</td>
										</tr>
										
										
										</table>
										
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
	</BODY>
</HTML>
