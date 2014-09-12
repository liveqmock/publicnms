var dataobj;
/**
 * 初始化添加一级菜单窗体
 */
function intialDialog() {
	$('#dd').dialog({
		toolbar:[{
			text:'保存',
			iconCls:'icon-save',
			handler:function(){
				$("#dataform").submit();
			}
		}],modal:true
	});
	
}

function addOneLevel() {
	 $("#dd").css({padding:"5px",width:"400px",height:"200px"});
	 $("#data").css("display","inline");
	 intialDialog();

};

function checkdata(){
	if(fucCheckLength($("input[name='menu_name']").val())==-1){
		$.messager.alert("提示信息","请输入菜单名");
		return false;
	}
	return true;
}

function delform() {
	if(del()) {
		$("#mainform").attr("action","controller/delmenu.action");
		$("#mainform").submit();
	}
	
}

/**
 * 根据单选按钮，展示数据。
 */
function fillingdata() {
	var menu_id = $('input[@name="edit_menu_radio"][checked]').val();
	var tempobj = dataobj;
	$.each(tempobj,function(i,o){
		if(menu_id==o.menu_id) {
			$("input[name='edit_menu_name']").val(o.menu_name);
			$("input[name='edit_menu_url']").val(o.menu_url);
			$("input[name='edit_menu_id']").val(o.menu_id);
			$("#edittable").show();
			return false;
		}
	});
}
/**
 * 清理edittable的数据。
 */
function emptydata() {
	$("input[name='edit_menu_name']").val("");
	$("input[name='edit_menu_url']").val("#");
	$("input[name='edit_menu_id']").val("");
	$("#edittable").show();
}
/**
 * 表单提交时验证表单
 * @returns {Boolean}
 */
function checkeditdata(){
	if(fucCheckLength($("input[name='edit_menu_name']").val())==-1){
		$.messager.alert("提示信息","请输入菜单名");
		return false;
	}
	return true;
}
/**
 * 删除二级菜单
 */
function checkdeldata() {
	var flag = "e";
	$('input[name="edit_menu_radio"]:checked').each(function(){
        flag = "s";
	});
  if("e"==flag) {
	  $.messager.alert("提示信息","请选择需要删除的记录");
	  return ;
  }else {
	  
	  $.messager.confirm('提示信息','确定删除吗?',function(r){   
		    if (r){   
		    	$("input[name='del_menu_id']").val($('input[name="edit_menu_radio"]:checked').val());
		  	   $("input[name='edit_menu_name']").val('删除');
		    	 $("#editform").submit();
		    }   
		});  

	 
  }
	
}
/**
 * 初始化编辑二级菜单窗体。
 * @param parent_id
 */
function editmenudialog(parent_id) {
	 var table = "<table id='maintable' border='1' class='table_box'><tr><td><table id='datatable'>";
	 $.ajax({
		   type: "POST",
		   url: "querySecondMenu.action",
		   data: "parent_id="+parent_id,
		   async:false,
		   success: function(obj){
			 dataobj = obj;  
			 $.each(obj,function(i,o){
				 table = table + "<tr><td><input type='radio' value='"+o.menu_id+"' name='edit_menu_radio' /></td><td><font>"+o.menu_name+"</font></td></tr>";
			 });
		   }
		});
	table = table + "</table></td><td>";
	table = table + "<form id='editform' action='controller/editMenu.action' onsubmit='return checkeditdata();' method='get'>" +
			"<table id='edittable' >" +
			"<tr><td><font class='red'>*</font>菜单名：</td><td><input type='text' name='edit_menu_name' value=''/></td></tr>" +
			"<tr><td>响应地址：</td><td><input type='text' name='edit_menu_url' value=''/></td></tr>" +
			"<tr><td colspan='2' align='center'><input type='hidden' name='edit_parent_id' value='"+parent_id+"'/> " +
					"<input type='hidden' name='edit_menu_id' value=''/><input type='hidden' name='del_menu_id' value=''/></td></tr>" +
					"</table></form><td></tr></table>";
	$('#sec').empty();
	$('#sec').append(table);
	$("#edittable").hide();
	$('#sec').dialog({
		toolbar:[{
			text:'添加',
			iconCls:'icon-save',
			handler:function(){
				emptydata();
			}
		},
		         {
			text:'修改',
			iconCls:'icon-save',
			handler:function(){
				fillingdata();
			}
		},
		    {
			text:'保存',
			iconCls:'icon-save',
			handler:function(){
				$("#editform").submit();
			}
		},
		{
			text:'删除',
			iconCls:'icon-save',
			handler:function(){
				checkdeldata();
			}
		}
		],modal:true
	});
	
}

/**
 * 修改一级菜单
 */
function modifyoneform() {
	if(del()) {
			var temp = $("input[name='checkbox']:checked").parent("td");
			$("input[name='one_menu_name']").val(temp.next().html());
			$("input[name='one_menu_url']").val(temp.next().next().next().html());
			$("input[name='one_menu_id']").val($("input[name='checkbox']:checked").val());
		$("#onedatatable").show();
		$('#one').dialog({
		toolbar:[{
			text:'保存',
			iconCls:'icon-save',
			handler:function(){
				$("#onedataform").submit();
			}
		}],modal:true
	  });
	}
}
/**
* 校验修改一级菜单
*/
function checkonedata(){
if(fucCheckLength($("input[name='one_menu_name']").val())==-1){
		$.messager.alert("提示信息","请输入菜单名");
		return false;
	}
	return true;
}
