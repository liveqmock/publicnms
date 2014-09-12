
$(function() {
				/**
				* the list of posts
				*/
				var $list 		= $('#rp_list ul');
				/**
				* number of related posts
				*/
				var elems_cnt 		= $list.children().length;
				/**
				* show the first set of posts.
				* 200 is the initial left margin for the list elements
				*/
				load(200);   
				
				function load(initial){
					$list.find('li').hide().andSelf().find('div').css('margin-left',-initial+'px');
					var loaded	= 0;
					//show 5 random posts from all the ones in the list. 
					//Make sure not to repeat
					var $elem	= $list.find('li:nth-child(1)').show();
					//animate them
					var d = 150;
					//$list.find('li:visible div').each(function(){
					//	$(this).stop().animate({
					//		'marginLeft':'50px'
					//	},d += 100);
					//});
				}
					
				/**
				* hovering over the list elements makes them slide out
					
				$list.find('li:visible').live('mouseenter',function () {
					$(this).find('div').stop().animate({
						'marginLeft':'90px'
					},150);
				}).live('mouseleave',function () {
					$(this).find('div').stop().animate({
						'marginLeft':'65px'
					},150);
				});*/
			//top
			
				var $listbp 		= $('#bp_list ul');
				
				var elems_cnt_bp 		= $listbp.children().length;
				
				loadbp(200);
				
				function loadbp(initialbp){
					$listbp.find('li').hide().andSelf().find('div').css('margin-top',-initialbp+'px');
					var loadedbp	= 0;
					
					var $elembp	= $listbp.find('li:nth-child(1)').show();
					//animate them
					var dbp = 150;
					$listbp.find('li:visible div').each(function(){
						$(this).stop().animate({
							'marginTop':'50px'
						},dbp += 100);
					});
				}
					
				
				//$listbp.find('li:visible').live('mouseenter',function () {
				//	$(this).find('div').stop().animate({
				//		'marginTop':'90px'
				//	},150);
				//}).live('mouseleave',function () {
				//	$(this).find('div').stop().animate({
				//		'marginTop':'50px'
				//	},150);
				//});
			
            });
 
function showMenu1(e){
	//e.style.marginLeft = "0px";
	//e.onclick = function() {
	//	hideMenu1(this);
	//}
}

function hideMenu1(e) {
	//e.style.marginLeft = "-100px";
	//e.onclick = function() {
	//	showMenu1(this);
	//}
}