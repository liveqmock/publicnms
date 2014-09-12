function checkall() {
	$("input[name='checkall']").toggle(
			  function () {
			    $("input[name='checkbox']").attr("checked",true);
			  },
			  function () {
			    $("input[name='checkbox']").attr("checked",false);
			  }
			);
	
}

function addItemClass() {
	$("input[type='button']").addClass("botton");
}

$(document).ready(function(){
		checkall();
		addItemClass();
	
});


function del() {
	var flag = "e";
	$('input[name="checkbox"]:checked').each(
            function() {
                 flag = "s";
            }
        );
	
	if("e"==flag) {
		$.messager.alert("提示信息","请选择需要操作的记录");
		return false;
	}
	return true;
}

