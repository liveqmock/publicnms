var currentStep=0; //c
var max_line_num=0;//


//иорф
function upLine(flextb){ 
		//alert(currentStep)
	  if(currentStep==0){
		alert('the first item');
		return false;
	  }
	  if(currentStep<=1){
		 alert('the last item');
	  return false;
	  }
	  var upStep=currentStep-1;
	  
	  $('#' + flextb).find('#line'+upStep+" td:first-child").html(currentStep);
	  $('#' + flextb).find('#line'+currentStep+" td:first-child").html(upStep);
	 
	  var upContent=$('#' + flextb).find('#line'+upStep).html();
	  var currentContent=$('#' + flextb).find('#line'+currentStep).html();
	  $('#' + flextb).find('#line'+upStep).html(currentContent);
	
	  $('#' + flextb).find('#line'+currentStep).html(upContent);  
	  $('#' + flextb + ' tr').each(function(){$(this).css("background-color","");});
	  $('#' + flextb).find('#line'+upStep).css("background-color","yellow"); 
	  currentStep=upStep;
	  syncDateId(flextb)
}

function downLine(flextb){
	 max_line_num = $('#' + flextb + ' tr').length - 1;
	 if(currentStep==0){
		alert('the first item');
		return false;
	  }
	 if(currentStep>=max_line_num){
		 alert('the last item');
		return false;
	  }
	  var nextStep=parseInt(currentStep)+1;
	  
	  $('#' + flextb).find('#line'+nextStep+" td:first-child").html(currentStep);
	  $('#' + flextb).find('#line'+currentStep+" td:first-child").html(nextStep);
	  
	  var nextContent=$('#' + flextb).find('#line'+nextStep).html();
	  var currentContent=$('#' + flextb).find('#line'+currentStep).html();
	  $('#' + flextb).find('#line'+nextStep).html(currentContent);
	  
	  $('#' + flextb).find('#line'+currentStep).html(nextContent);  
	  $('#' + flextb + ' tr').each(function(){$(this).css("background-color","");});
	  $('#' + flextb).find('#line'+nextStep).css("background-color","#D5EFF4"); 
	  currentStep=nextStep;
	  syncDateId(flextb)
}

function lineclick(flextb, line){
   $("#" + flextb + " tr").each(function(){$(this).css("background-color","");});
   var seq=$(line).children("td").html();
   $(line).css("background-color","#D5EFF4");
   currentStep=seq;
}
function syncDateId(flextb){
	$("#" + flextb + " tr").each(
    function(index,domEl){
		$(this).children("td").each(function(index){
			if(index==0){
				curLineNo = $(this).html();
			}
			tdHtml = $(this).html();
			
			var r = new RegExp("\\[[0-9]+\\]");
			if(r.test(tdHtml)){
				var arr = tdHtml.match(r);
		  		var r = new RegExp("\\[" + arr[0] + "\\]", "g");
				tdHtml = tdHtml.replace(r, "[" + (curLineNo - 1) + "]");
				//alert(tdHtml);
				$(this).html(tdHtml);
		  	}
		});
		
 	});
}