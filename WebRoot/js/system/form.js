$(document).ready(function(){
		checkall();
});

function delform() {
	 $.messager.confirm('提示信息','确定要删除此表单？',function(r){   
		    if (r){   
		    	$("#mainForm").attr("action","controller/formDelete.action");
		       $("#mainForm").submit();
		    }   
		});  
}

function checkall() {
	$("input[name='checkall']").toggle(
			  function () {
			    $("#mainForm input[type=checkbox]").attr("checked",true);
			  },
			  function () {
			  	$("#mainForm input[type=checkbox]").attr("checked",false);
			  }
			);
	
}
