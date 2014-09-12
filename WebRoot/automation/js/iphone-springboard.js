/*
* Author:      Marco Kuiper (http://www.marcofolio.net/)
* BelongsTo:   The iPhone springboard in xHTML, CSS and jQuery
*/

$(document).ready(function()
{
	// Fade in the docking bar
	$("#springboard-bar").fadeIn(1500);
	
	// Fade and Slide in the elements
	$("#springboard-items").fadeIn(1000); // Doesn't work in IE?
	$(".downright").animate({left:0, top:0}, 600);
	$(".downleft").animate({left:0, top:0}, 600);
	$(".upright").animate({left:0, top:0}, 600);
	$(".upleft").animate({left:0, top:0}, 600);
	
	// What will happen when an icon gets clicked
	$(".ico_btn").click(function(event) {
		var element = $(this);
		event.preventDefault();
		$("#springboard-bar").fadeOut("slow");
		$("#springboard-items").fadeOut("slow", function(){
			window.location = element.attr("href");
		});
	});

});