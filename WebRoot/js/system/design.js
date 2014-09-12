 //url: "controller/createModel.action?modelname="+name+"&modeldesc="+desc,
 function intialDesignDialog() {
    $('#dd').show();
	$('#dd').dialog({
		toolbar:[{
			text:'保存',
			iconCls:'icon-save',
			handler:function(){
				if(checkNewDesignData()) {
					var name = $("input[name='modelname']").val();
					var desc = $("#desc").val();
					var keytext = $("#keytext").val();
					 $('#dd').dialog('close');
					$.ajax({
						   type: "POST",
						   url: "controller/createModel.action",
						   data: "modelname="+name+"&modeldesc="+desc+"&keytext="+keytext,
						   success: function(msg){
						   window.location.reload();
							   window.open(msg,"editdiskconfig", " fullscreen = yes,scrollbars=no");
							  
						   }
						});
				}else {
					return ;
				}
				
			}
		}],modal:true
	});
	
}
 
function checkNewDesignData() {
	
	if(fucCheckLength($("input[name='modelname']").val())==-1){
		$.messager.alert("提示信息","请输入名称");
		return false;
	}
	if(fucCheckLength($("#keytext").val())==-1){
		$.messager.alert("提示信息","请选择类型");
		return false;
	}
	return true;
	
} 


function editDesign() {
	if(del()) {
		window.open("../service/editor?id=" + $("input[name='checkbox']:checked").val(),"editdiskconfig", " fullscreen = yes,scrollbars=no");
	}
	
}


function delDesign() {
	if(del()) {
		 $.messager.confirm('提示信息','确定删除吗?',function(r){   
			    if (r){   
			    	$("#mainForm").attr("action","../controller/delModel.action");
			     //  $("#sub").click();
			      mainForm.submit();
			    }   
			});  
	}
}

function changecode() {
  var val = $("#codetype").val();
   var arr=new Array();
   arr = val.split("|");
   $.ajax({
   type: "POST",
   url: "../controller/codeDetailQuery.action",
   data: "typeId="+arr[1],
   success: function(msg){
     $("#codedetail").empty();
     $.each( msg, function(i, n){
     $("#codedetail").append("<option value='"+n.code+"'>"+n.name+"</option>");
    });
   }
});
}
$(document).ready(function() {
changecode();
});
