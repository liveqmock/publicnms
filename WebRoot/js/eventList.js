var panelevent_var = function(rootpath){ 

				var proxyevent = new Ext.data.HttpProxy({
					url:'getEventListAjaxManager.ajax?action=ajaxGetEventList'
				});
				
				var readerevent = new Ext.data.JsonReader({  
						root : 'EventList'
					},[
						{name : 'nodeid',type:'int'},
						{name : 'level1'},
						{name : 'eventlocation'},
						{name : 'content'},
						{name : 'recordtime'}
						
				]);
				
			    var dataStoreevent = new Ext.data.Store({
			        autoLoad: false,
					proxy : proxyevent,
					reader : readerevent
				});
				
			    var gridevent = new Ext.grid.GridPanel({
			        store: dataStoreevent,
			        title:'告警信息列表',
			        columns: [
			        	{
			                id       :'nodeid',
			                header   : '设备ID', 
			                width :30,
			                sortable : true, 
			                dataIndex: 'nodeid'
			            },
			            {
			                header   : '等级', 
			                width :50,
			                sortable : true, 
			                renderer : function(level1,_this,_data){ 
			                	var level = _data.get('level1');
			                	var levelname="";
			                	var bgcolor="";
			                	if("3"==level){
			                		levelname="紧急告警";	
			                		bgcolor="red";		                	
			                	}else if("2"==level){
			                		levelname="严重告警";	
			                		bgcolor="orange";		                	
			                	}else if("1"==level){
			                		levelname="普通告警";	
			                		bgcolor="yellow";			                	
			                	}else if("0"==level){
			                		levelname="提示信息";	
			                		bgcolor="blue";			                	
			                	}
			                	 var tabstr =  '<table width="100%"><tr><td bgcolor='+bgcolor+' height="18px" style="text-align:center;line-height:18px">'+levelname+'</td></tr></table>';
							     return tabstr;
							     
							     },
			                dataIndex: 'level1'
			            },
			            {
			                header   : '来源', 
			                width :100,
			                sortable : true, 
			                renderer : 'eventlocation',
			                dataIndex: 'eventlocation'
			            },
			            {
			                header   : '告警信息', 
			                sortable : true, 
			                width :170,
			                renderer : 'content', 
			                dataIndex: 'content'
			            },
			             {
			                header   : '记录时间', 
			                width :90,
			                sortable : true, 
			                renderer : 'recordtime', 
			                dataIndex: 'recordtime'
			            }
			       	],
			        stripeRows: true,
			        autoExpandColumn: 'nodeid',
			        autoHeight : true,
			        viewConfig:{   
			        	scrollOffset: -3 ,
						forceFit:true  
					}, 
			        stateful: false,
			        stateId: 'gridevent'
			    });  
			    gridevent.getStore().load();
			    
				var updateClock = function(){
    				dataStoreevent.reload();
				};
				
			    Ext.TaskMgr.start({
    				run: updateClock,
    				interval: 60000
				});
			    
			    
		    Ext.get('event_list').on('resize',function(){
		    	tabs.setWidth(Ext.get('event_list').getWidth());
		    	tabs.onLayout();
		    }); 
			    
		    var tabs = new Ext.TabPanel({
		    	id:'event',
		    	renderTo:"event_list",
                width:Ext.get("event_list").getWidth(),
		    	height:430,
		        activeTab: 0, 
			    autoScroll:true,
			    autoWidth:false,
		        plain:true,
		        defaults:{autoScroll: true},
		        items:[gridevent]
		    }); 
		    
		    
		    //增加Tab页面切换的事件
		    tabs.on("tabchange",function(currentTab, newTab ){
		    	newTab.getStore().load();
		    } );
		};