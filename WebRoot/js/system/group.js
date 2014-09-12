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
			$("#modify").val(temp.next().next().html());
			$("#modifyid").val($("input[name='checkbox']:checked").val());
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

function checkadddata(){
	if(fucCheckLength($("#addid").val())==-1){
		$.messager.alert("提示信息","请输入编号");
		return false;
	}
	if(""!=$("#mess").html()) {
		$.messager.alert("提示信息","编号已存在");
		return false;
	}
	if(fucCheckLength($("#add").val())==-1){
		$.messager.alert("提示信息","请输入岗位名");
		return false;
	}
	return true;
}

function checkmodifydata(){
	if(fucCheckLength($("#modify").val())==-1){
		$.messager.alert("提示信息","请输入岗位名");
		return false;
	}
	return true;
}


function delform() {
	if(del()) {
	 $.messager.confirm('提示信息','确定删除吗?包含用户的岗位将不会被删除',function(r){   
		    if (r){   
		    	$("#mainform").attr("action","controller/groupDel.action");
		       $("#mainform").submit();
		    }   
		});  
	}
}
/**
 * 检查组号是否存在
 * @param val
 */
function checkgroupid(val) {
	$.ajax({
		   type: "POST",
		   url: "checkGroupId.action",
		   data: "groupId="+val,
		   async:false,
		   success: function(obj){
			 if("error"==obj) {
				 $("#mess").html("编号已存在");
			 }else {
				 $("#mess").html("");
			 }
		   }
		});
}
