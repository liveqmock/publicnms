
/* set up browser checks */

// check browsers
var ua		= navigator.userAgent;
var opera	= /opera [56789]|opera\/[56789]/i.test(ua);
var ie		= !opera && /msie [56789]/i.test(ua);		// preventing opera to be identified as ie
var moz		= !opera && /mozilla\/[56789]/i.test(ua);	// preventing opera to be identified as mz
/* end browser checks */

/* Do includes */

if (window.pathToRoot == null)
	window.pathToRoot = "/afunms/resource/";
/*
if (ie)
	document.write('<script type="text/javascript" src="menutest/cssexpr.js"><\/script>');
document.write('<link type="text/css" rel="StyleSheet" href="menutest/xmenu.css"><\/script>' +
	'<script type="text/javascript" src="menutest/xmenu.js"><\/script>');
	*/
	
// later
 webfxMenuImagePath = pathToRoot + "image/";

/* end includes */



webfxLayout = {
	writeTopMenuBar	: function ()
	{
		document.write(	"<div id='webfx-menu-bar'>");// div is closed in writeBottomMenuBar
	},
	writeBottomMenuBar : function ()
	{
		document.write("</div>");
	},
	writeMenu : function ()
	{
		if (ie || moz || opera)
		{
			if (ie)
				simplifyCSSExpression();
			this.writeTopMenuBar();
			document.write(menuBar);
			this.writeBottomMenuBar();
		}
		
	}

};

