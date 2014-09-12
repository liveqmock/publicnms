(function(menu) {
    jQuery.fn.contextmenu = function(options) {
        var defaults = {
			height:100,
			width:100,
            offsetX : 2,        //鼠标在X轴偏移量
            offsetY : 2,        //鼠标在Y轴偏移量
            items   : [],       //菜单项
            action  : $.noop()  //自由菜单项回到事件
        };
        var opt = menu.extend(true, defaults, options);
        function create(e) {
            var m = menu('<ul class="simple-contextmenu"></ul>').appendTo(document.body);            
            menu.each(opt.items, function(i, item) {
                if (item) {
                    if(item.type == "split"){ 
                        menu("<div class='m-split'></div>").appendTo(m);
                        return;
                    }
                    var row   = menu('<li><div class="menuBarContainerDIV"><div class="menuBarIconDIV"><img src="' + item.icon + '" width="16" height="16" /> </div><div class="menuBarContentDIV"><a href="javascript:void(0)">'+item.text+'</a></div></div></li>').appendTo(m);                                        
                    if (item.action) {
                        row.find('a').click(function() {
                            item.action(e.target);
                        });
                    }
                }
            });
			m.css("width",opt.width+"px");
			$(".menuBarContainerDIV").css("width",opt.width-4+"px");
			$(".menuBarContentDIV").css("width",opt.width-35+"px");	
            return m;
        }
        
        this.live('contextmenu', function(e) {					
            var scrollTop = document.documentElement.scrollTop; //垂直滚动条距离顶部
		    var scrollLeft = document.documentElement.scrollLeft;//水平滚动条距离左边
			var clientX= e.clientX;//可视区域焦点X坐标值
			var clientY=e.clientY;//可视区域焦点Y坐标值					
            var m = create(e).show("fast");					
			var left = clientX + opt.offsetX+scrollLeft, top = clientY + opt.offsetY+scrollTop, p = {
                wh : menu(window).height(),//浏览器宽度
                ww : menu(window).width()//浏览器高度                
            }
			//当菜单超出窗口边界时处理			
			if((clientX+opt.width)>p.ww){
					left =p.ww- opt.width+scrollLeft-5;					
				}
			if((clientY+opt.height)>p.wh){
					top=p.wh- opt.height+scrollTop;
				}    
            m.css({
                zIndex : 10000,
                left : left,
                top : top
            });
            $(document.body).live('contextmenu click', function() {
                m.hide("fast",function(){
                    m.remove();
                });        
            });
            
            return false;
        });
        return this;
    }
})(jQuery);
