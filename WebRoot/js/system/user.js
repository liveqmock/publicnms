/**
 * 初始化添加一级菜单窗体
 */
function add() {
    $("#addtable").show();
	$('#adddiv').dialog({
		toolbar:[{
			text:'保存',
			iconCls:'icon-save',
			handler:function(){
				$("#addform").submit();
			}
		}],modal:true
	});
	
}


function modify() {
if(del()){
var temp = $("input[name='checkbox']:checked").parent("td");
var val = $("input[name='checkbox']:checked").val();
$.ajax({
	   type: "POST",
	   url: "userGroupQuery.action",
	   data: "userId="+val,
	   async:false,
	   success: function(obj){
		    $.each(obj,function(i,o){
		    	$("input[name='modifygroups']").each(function() {
					if($(this).val()==o.id) {
						 $(this).attr("checked", true);
					}
					
				});
		    });
	   }
	});
			$("#modifyid").val(val);
var array = temp.next().next().html().split("-"); 			
			$("#modifyfirstName").val(array[0]);
			$("#modifylastName").val(array[1]);
			$("#modifyemail").val(temp.next().next().next().html());
    $("#modifytable").show();
	$('#modifydiv').dialog({
		toolbar:[{
			text:'保存',
			iconCls:'icon-save',
			handler:function(){
				$("#modifyform").submit();
			}
		}],modal:true
	});
	}
}
/**
 * 添加用户时校验
 * @returns {Boolean}
 */
function checkadddata(){
	if(fucCheckLength($("#addid").val())==-1){
		$.messager.alert("提示信息","请输入账号");
		return false;
	}
	if(""!=$("#mess").html()) {
		$.messager.alert("提示信息","账号已存在");
		return false;
	}
	if(fucCheckLength($("#addfirstName").val())==-1){
		$.messager.alert("提示信息","请输入姓");
		return false;
	}
	if(fucCheckLength($("#addlastName").val())==-1){
		$.messager.alert("提示信息","请输入名");
		return false;
	}
	if(fucCheckLength($("#addpassword1").val())==-1){
		$.messager.alert("提示信息","请输入密码");
		return false;
	}
	if(!compare($("#addpassword1").val(),$("#addpassword2").val())){
		$.messager.alert("提示信息","两次密码不一致");
		return false;
	}
	if(fucCheckLength($("#addemail").val())==-1){
		$.messager.alert("提示信息","请输入邮箱地址");
		return false;
	}
	if(!chkemail($("#addemail").val())){
		$.messager.alert("提示信息","邮箱地址格式不正确");
		return false;
	}
	var flag = "e";
	$("input[name='addgroups']:checked").each(function() {
		flag = "s";
		return false;
	});
	if("e"==flag){
		$.messager.alert("提示信息","请选择用户组");
		return false;
	}
	
	return true;
}
/**
 * 校验修改用户信息
 * @returns {Boolean}
 */
function checkmodifydata(){
	if(fucCheckLength($("#modifyfirstName").val())==-1){
		$.messager.alert("提示信息","请输入姓");
		return false;
	}
	if(fucCheckLength($("#modifylastName").val())==-1){
		$.messager.alert("提示信息","请输入名");
		return false;
	}
	
	if(!compare($("#modifypassword1").val(),$("#modifypassword2").val())){
		$.messager.alert("提示信息","两次密码不一致");
		return false;
	}
	if(fucCheckLength($("#modifyemail").val())==-1){
		$.messager.alert("提示信息","请输入邮箱地址");
		return false;
	}
	if(!chkemail($("#modifyemail").val())){
		$.messager.alert("提示信息","邮箱地址格式不正确");
		return false;
	}
	var flag = "e";
	$("input[name='modifygroups']:checked").each(function() {
		flag = "s";
		return false;
	});
	if("e"==flag){
		$.messager.alert("提示信息","请选择用户组");
		return false;
	}
	
	return true;
}


function delform() {
	if(del()) {
	 $.messager.confirm('提示信息','确定删除吗?',function(r){   
		    if (r){   
		    	$("#mainform").attr("action","controller/userDel.action");
		       $("#mainform").submit();
		    }   
		});  
	}
}


/**
 * 检查用户名是否存在
 * @param val
 */
function checkuserid(val) {
	$.ajax({
		   type: "POST",
		   url: "checkUserId.action",
		   data: "userId="+val,
		   async:false,
		   success: function(obj){
			 if("error"==obj) {
				 $("#mess").html("账号已存在");
			 }else {
				 $("#mess").html("");
			 }
		   }
		});
}
