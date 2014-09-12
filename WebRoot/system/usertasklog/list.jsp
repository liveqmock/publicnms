<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.model.UserTaskLog"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%@include file="/include/globe.inc"%>
<%
  	String rootPath = request.getContextPath(); 
  	List<UserTaskLog> userTaskLogList = (List<UserTaskLog>)request.getAttribute("userTaskLogList");
  	String menuTable = (String)request.getAttribute("menuTable");
  	User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
%>

<html>
<head>
<title>E2CS 0.0.14 - Basic Sample</title>
<link rel="shortcut icon" href="<%=rootPath%>/system/usertasklog/resource/images/e2cs_16x16.png">
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/system/usertasklog/resource/ext2/resources/css/ext-all.css">
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/system/usertasklog/resource/ext2/resources/css/core.css">
<script type="text/javascript" src="<%=rootPath %>/system/usertasklog/resource/ext2/ext-base.js"></script>
<script type="text/javascript" src="<%=rootPath %>/system/usertasklog/resource/ext2/ext-all-debug.js"></script>
<link id="css" rel="stylesheet" type="text/css" href="<%=rootPath %>/system/usertasklog/resource/calendar.css">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">


<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">

<style type="text/css">
<!--
/*Custom CSS for the sview.blankHTML porperty */
.custom_image_addNewEvent_scheduler{cursor:pointer;	padding-top: 15px;	padding-right: 5px;	padding-bottom: 5px;	padding-left: 5px;}
.custom_text_addNewEvent_scheduler{font-family: Verdana, Arial, Helvetica, sans-serif;	font-size: 14px; font-weight: bold;	color: #FFFFFF;	text-decoration: none;	text-align: center;	padding: 3px;}
.test_taskovercss{ background-image:url(<%=rootPath%>/system/usertasklog/resource/images/task_over_001.png); background-repeat:repeat-y; color:#FFFFFF;} 
.test_taskovercss_b{ background-color:#999999;color:#FFFFFF;background-image: url(<%=rootPath%>/system/usertasklog/resource/images/__header_background_001.png);	background-repeat: repeat-y;} 
.test_taskovercss_sched{ background-image:url(<%=rootPath%>/system/usertasklog/resource/images/header_background_002.png); background-repeat:repeat-x; color:#FFFFFF;}
.test_taskovercss_sched_b{ background-image:url(<%=rootPath%>/system/usertasklog/resource/images/header_background_001.png); background-repeat:repeat-x; color:#FFFFFF;}
.holiday{height:16px; background-image: url(<%=rootPath%>/system/usertasklog/resource/imgs_test/cake.png);	background-repeat: no-repeat;background-position: left 2px;	text-indent: 18px;}
-->
</style>
<script type="text/javascript" src="<%=rootPath %>/system/usertasklog/resource/locale/e2cs_zh_CN.js"></script>
<script type="text/javascript" src="<%=rootPath %>/system/usertasklog/resource/e2cs_pack.js"></script>


<script type="text/javascript">
Ext.onReady(function() { 
	
	/*
	hideshow('monthsample');
	hideshow('daysample');	
	hideshow('weeksample');	
	hideshow('sxsample');
	Ext.get('calendar_hdr_monthx').addClassOnOver('settings_overview');
	Ext.get('calendar_hdr_monthx').addListener('click',	function(){ 	hideshow('monthsample');} );
	Ext.get('calendar_hdr_dayx').addClassOnOver('settings_overview');
	Ext.get('calendar_hdr_dayx').addListener('click',	function(){	hideshow('daysample');	} 	);
	Ext.get('calendar_hdr_weekx').addClassOnOver('settings_overview');
	Ext.get('calendar_hdr_weekx').addListener('click',	function(){	hideshow('weeksample');	} 	);
	Ext.get('calendar_hdr_scx').addClassOnOver('settings_overview');
	Ext.get('calendar_hdr_scx').addListener('click',	function(){	hideshow('sxsample');	} 	);	
	*/
});
	
function test_calendar(obj){
	Ext.QuickTips.init();  
	buttonx1= new Ext.menu.Item({ id: 'buttonx1_task', iconCls:'x-calendar-month-btnmv_task',	text: "Custom menu test 1" });
	buttonx2= new Ext.menu.Item({ id: 'buttonx2_task',iconCls:'x-calendar-month-btnmv_task',	text: "Custom menu test 2" });
	buttonz1= new Ext.menu.Item({ id: 'buttonz1_task', iconCls:'x-calendar-month-btnmv_task',	text: "Custom action 1" });
	buttonz2= new Ext.menu.Item({ id: 'buttonz2_task',iconCls:'x-calendar-month-btnmv_task',	text: "Custom action 2" });
	boton_daytimertask  = new Ext.menu.Item({ id: 'btnTimerTask', iconCls:'task_time', text: "Set Task Alarm...."  });
	boton_daytimertaskb = new Ext.menu.Item({ id: 'btnTimerOff' , iconCls:'task_time_off', text: "Delete Task's Alarm...."  });	
	button_sched_1= new Ext.menu.Item({ id: 'buttonx1_task',iconCls:'x-calendar-month-btnmv_task',text: "Custom menu  on sched test 1" });
	button_sched_2= new Ext.menu.Item({ id: 'buttonx2_task',iconCls:'x-calendar-month-btnmv_task',text: "Custom menu  on sched test 2" });
	var reader = new Ext.data.ArrayReader({id:0}, [	
	   {name: 'id' 		, type: 'int'},
       {name: 'userid'		, type: 'string'},  
       {name:'username'	, type: 'string'},
       {name:'content'  	, type: 'string'},
       {name:'date'		, type: 'string'}, 
       {name: 'color'		, type: 'string'}
    ]);
    var dummyData = new Array();
    <%for(int i = 0; i < userTaskLogList.size(); i++){ %>
    dummyData[<%=i%>] = ['<%=userTaskLogList.get(i).getId()%>',
    		'<%=userTaskLogList.get(i).getUserId()%>',
    		'<%=user.getName()%>',
    		'<%=userTaskLogList.get(i).getContent()%>',
    		'<%=userTaskLogList.get(i).getTime()%>',
    		'greenyellow'
    		];
    	<%
    }%>
   	
    var calendarstoretest= new Ext.data.Store({reader:reader,data:dummyData });
	prueba = new Ext.ECalendar({
		id: 'test_calx', 
		name: 'test_calx',
		header: true,
		title: 'Calendar Sample',
		mytitle: ' 001',	
		height:400, 
		fieldsRefer:{ //0.0.11 
			id:'id',
			userid:'userid',
			username:'username', 
			content:'content',
			date:'date',
			color:'color'
		},
		storeOrderby:'username', 	//0.0.11 
		storeOrder:'DESC',		//0.0.11 
		showCal_tbar: true, 
		showRefreshbtn:true,
		currentView: 'month',
		currentdate: new Date(),
		dateSelector: true,
		dateSelectorIcon: '<%=rootPath%>/system/usertasklog/resource/images/date.png',
		dateSelectorIconCls: 'x-cmscalendar-icon-date-selector',
		dateformat :'Y-m-d',
		iconCls: 'x-cmscalendar-icon-main',
		dateSelector:true,
		store:calendarstoretest, 
		monitorBrowserResize:true, 
		widgetsBind: {bindMonth:null,bindDay:null,binWeek:null},
		tplTaskZoom: new Ext.XTemplate( 
		'<tpl for=".">',
			'<div class="ecal-show-basetasktpl-div"><b>Title:</b>{title}<br>',
			'<br><b>Username:</b>{username}',
			'<br><b>Date:</b>{date}',
			'<br><b>Content:</b><div><hr><div>{content}<div><hr>',
		'</tpl>'
		),
		iconToday:'<%=rootPath%>/system/usertasklog/resource/images/cms_calendar.png',
		iconMonthView:'<%=rootPath%>/system/usertasklog/resource/images/calendar_view_month.png',
		iconListView:'<%=rootPath%>/system/usertasklog/resource/images/calendar_view_week.png',
		//iconWeekView:'<%=rootPath%>/system/usertasklog/resource/images/calendar_view_week.png',
		//iconDayView:'<%=rootPath%>/system/usertasklog/resource/images/calendar_view_day.png',
		//iconSchedView:'<%=rootPath%>/system/usertasklog/resource/images/calendar_view_schedule.png', //0.0.10  // NEW :) 
		loadMask:false, //0.0.12  we dont need this saple here cause all the data is loaded its suits better  for Serverside loading 
		customMaskText:'E2CS DEMO<br>Wait a moment please...!<br>Processing Information for calendar', //0.0.12 		
		//-------- NEW on 0.0.10 -------------------
		/*
		sview:{
				header: true, 
				headerFormat:'Y-M', 
				headerButtons: true,
				headerAction:'event',  //gotoview
				periodselector:false,
				blankzonebg:'#6C90B4',
				//sched_addevent_id
				blankHTML:'<div id="{calx}-test-img" class="custom_image_addNewEvent_scheduler" style=" width:100%; background-color:#6C90B4"><div align="center" id="{sched_addevent_id}"><img src="<%=rootPath%>/system/usertasklog/resource/images/no_events_default.jpg" width="174" height="143"></div><div class="custom_text_addNewEvent_scheduler">Click on Image to add a new Task</div></div>',
				listItems: { 
					headerTitle:"Events for Test", 		
					periodFormats:{ 
						Day:		'l - d - F  - Y', 	
						DayScheduler_format: 'd', 	
						hourFormat: 'h:i a', 				 
						startTime:  '7:00:00 am',		
						endTime:    '10:00:00 pm',
						WeekTPL:  	'<tpl for=".">Week No.{numweek} Starting on {datestart} Ending on {dateend}</tpl>',  
						WeekFormat:	'W',	
						DatesFormat:'d/m/Y', 
						Month:'M-Y', 
						TwoMonthsTPL:'<tpl for=".">Period No.{numperiod} Starting on {datestart} Ending on {dateend}</tpl>',
						QuarterTPL:  '<tpl for=".">Period No.{numperiod} Starting on {datestart} Ending on {dateend}</tpl>'
					},	
					useStoreColor:false, 	
					descriptionWidth:246,
					parentLists:false, //to expand collapse Parent Items if false all tasks shown as parent
					launchEventOn:'click',
					editableEvents:true, // If true a context menu will appear 
					ShowMenuItems:[1,1,1,1,1,1,1,1], // ADD, EDIT, DELETE, GO NEXT PERIOD , GO PREV PERIOD, Chg Month, Chg Week, CHG Day
					taskdd_ShowMenuItems:[1,1,1],    // ADD, EDIT, DELETE
					moreMenuItems:[button_sched_1,button_sched_2],
					taskdd_BaseColor:'#6C90B4',
					taskdd_clsOver:'test_taskovercss_sched',
					taskdd_showqtip:true,
					taskdd_shownames:true
				},
				listbody:{
					//e2cs.schedviews.subView ={ Day:  0,  Week: 1,  Month: 2, TwoMonths: 3,  Quarter: 4};
					//e2cs.schedviews.Units   ={ Hours:0,  Days: 1,  Weeks: 2};
					periodType:e2cs.schedviews.subView.TwoMonths,
					headerUnit:e2cs.schedviews.Units.Days,
					headerUnitWidth:25
				}
		},
		*/ 
		
		//-------------------------------------------
		mview:{
			header: true,
			headerFormat:'Y-F',
			headerButtons: true,
			dayAction:'viewday',    //dayAction: //viewday , event, window
			//moreMenuItems:[buttonx1,buttonx2],
			showTaskcount: false,
			startDay:0,
			taskStyle:'margin-top:2px;', //Css style for text in day(if it has tasks and showtaskcount:true)
			showTaskList: true,
			showNumTasks:10,
			TaskList_launchEventOn:'click', //0.0.11 
			TaskList_tplqTip: new Ext.XTemplate( 
			'<tpl for=".">{usernamexl}{usernameval}<br>{datexl}{dateval}<hr color=\'#003366\' noshade>{content}</tpl>' ), //0.0.11 
			ShowMenuItems:[1,1,1,1,1,1],  //0.0.11  - ADD, nextmonth, prevmonth, chg Week , CHG Day, chg Sched,	
			//TaskList_moreMenuItems:[buttonz1,buttonz2], 	  //0.0.11
			TaskList_ShowMenuItems:[1,1,1]//0.0.11 	- Add, DELETE, EDIT 	
		}
		/*
		wview:{
			headerlabel:'Week #',
			headerButtons: true,
			dayformatLabel:'D j', 
			moreMenuItems:[buttonx1,buttonx2],
			style: 'google',
			alldayTaksMore:'window', 
			alldayTasksMaxView:6,
			store: null, 
			task_width:25, 
			tasksOffset:40,
			headerDayClick:'viewday',
			ShowMenuItems:[1,1,1,1,1,1],	//0.0.11  add, go next w , go prev w , chg month , chg day, chg sched 
			task_ShowMenuItems:[1,1,1,1,1], //0.0.11  add, delete, edit, go next w , go prev w
			task_eventLaunch:'dblclick',		//0.0.11
			startDay:0, //sundays   0.0.14 
			task_clsOver:'test_taskovercss',
			forceTaskFit:true // 0.0.14 
		},
		dview:{
			header:true,
			headerFormat:'l - d - F  - Y',
			headerButtons: true,
			moreMenuItems:[],
			// day specific 
			hourFormat: 'h',
			startTime: '00:00:00 am',
			endTime:   '11:59:59 pm',
			// task settings 
			store: null,
			task_increment:1,
			taskBaseColor: '#ffffff', 
			task_width:60,
			taskAdd_dblclick: true,				//added on 0.0.7
			taskAdd_timer_dblclick:true,		//0.0.11
			useMultiColorTasks: false, 
			multiColorTasks:[], 
			tasks:[],
			moreMenuItems:[	boton_daytimertask,	boton_daytimertaskb	],
			task_clsOver:'test_taskovercss',	
			ShowMenuItems:[1,1,1,1,1,1],		//0.0.11 ADD, next day, prev day , chg Month , CHG Week, chg Sched, (for daybody menu) 
			task_DDeditable:true, 			    //0.0.11   
			task_eventLaunch:'dblclick',  		//0.0.11 'click, dblclick, if set to '' then no action is taken
			task_ShowMenuItems:[1,1,1,1,1],		//0.0.11 ADD, delete, edit, Next day , Prev Day  (for Taks/events) 
			customHTMLinpos:'before',			//0.0.13  Feature request 
			forceTaskFit: true					//0.0.14
		}*/
	});	
	//scheduler only event on this object
	/*  
	prueba.viewscheduler.on({
		'headerClick':{
				fn: function(refunit,datex, mviewx, calx) { 
					var msgstring = "Header Value clicked -(" + refunit +')<br>';
					if (refunit=='day' || refunit=='hour'){ 
						msgstring += 'on date -(' + datex.format('d/m/Y h:i:s') +')<br>';	
					} else { 
						msgstring += 'on Week# ' + datex[0].format('W');
						msgstring += '-(From ' + datex[0].format('d/m/Y h:i:s') +'  to '; 
						msgstring +=datex[1].format('d/m/Y h:i:s') + ')';	
					} 	
					Ext.Msg.alert('Information', msgstring);	
				},
				scope:this
		},
		'beforePeriodChange':{
				fn:function(refperiod,datexold,datexnew){
					if (refperiod==1){ //week 
						//do your stuff here 
					} else { 
						//do your stuff here 
					}
					return false; 
				},
				scope:this
		}, 
		'afterPeriodChange':{
				fn:function(refperiod,datexnew){
					if (refperiod==1){ 
						alert ("Changed date from " + datexnew[0] + " to " + datexnew[1]);					
					} else { 
						alert ("Changed date to " + datexnew);					
					} 
				}
		}
	});
	*/
	//dayClick only event on this object  
	prueba.viewmonth.on({		
		'dayClick':{
				fn: function(datex, mviewx, calx) { 
					alert ("dayclick event for " + datex);
				},
				scope:this
		},
		'beforeMonthChange':{
				fn: function(currentdate,newdate) { 
					//alert ("gonna change month to " + newdate.format('m/Y') + ' Actual date=' + currentdate.format('m/Y') );
					return false; 
				},
				scope:this
		},
		'afterMonthChange':{
				fn: function(newdate) { 
					//alert ("Month changed to " + newdate.format('m/Y') ) ;
				},
				scope:this
		}
	});
	/* 
	prueba.viewweek.on({
		'dblClickTaskAllDay':{
				fn: function(task,dxview,calendar) { 
					var datatest='Task all day <br>record No:' + task[0] + '<br>';
					datatest+='id-task:'  + task[1] + ' ' + task[2] + '<br>';
					datatest+='starts:'    + task[3] + '<br>';
 					datatest+='ends:'   + task[4] + '<br>';
 					datatest+='contents'  + task[5] + '<br>';	
					datatest+='index'     + task[6] + '<br>';	
					Ext.Msg.alert('Information', datatest);	
				},
				scope:this
		},
		'beforeWeekChange':{
			fn: function (currentDate, newDate){
					return false;	
			}
		}, 
		'afterWeekChange':{
			fn: function(newdate){
				return false; 
			}
		}
	});  
	//'beforeDayChange' and  'afterDayChange' unique events on day view 	
	prueba.viewday.on({
		'beforeDayChange':{
				fn: function(currentdate, newdate) { 
					alert ("gonna change to " + newdate.format('m/d/Y') + ' Actual date=' + currentdate.format('m/d/Y') );
					return false; 
				},
				scope:this
		},		
		'afterDayChange':{
				fn: function(newdate) { 
					alert ("changed to " + newdate.format('m/d/Y'));
				},
				scope:this
		}	
	}); 
	*/
	prueba.on({
		'beforeContextMenuTask': {
				fn: function(refview,datatask,showItems,myactions) { 
						 return false; 
				}
		},	
		'beforeChangeDate': {
			fn: function( newdate , calobj){
				//return true; 
			} 		
		},
		'afterChangeDate':{
			fn: function( newdate , calobj){
				alert ("Date changed to:" + newdate.format('d-m-Y'));
			}
		},
		
		'onChangeView':{
			fn: function(newView, oldView, calobj){ 
				Ext.get("samplebox_cview").update("<b>Current View:</b> " + newView); 
			},scope: this
		},
		
		'beforeChangeView':{
				fn: function (newView,OldView,calendarOBJ){
					if (newView==OldView){ 
						return true; 
					} 
					var r=confirm("Change from " + OldView + " to " + newView);
					if(r){
						var mainForm = document.getElementById("mainForm");
						mainForm.action = "<%=rootPath%>/userTaskLog.do?action=listType&jp=1";
						mainForm.submit();
					}
					return r;
				},scope:this
		},
		'beforeTaskAdd':{
				fn: function( datex, msg ) { 
					var date = new Date(datex);
					var massage = date.format('Y-m-d');
					massage += '  ';
					massage += msg;
					alert(massage);
					return false;
				}
		},
		'taskAdd':{
				fn: function( datex ) { 
					//alert ("Adding Task for " + datex);
					
					
					var form=new Ext.form.FormPanel({
					url:"<%=rootPath%>/userTaskLog.do?action=add&date="+datex.format('Y-m-d'),
					labelWidth:60,
					labelAlign:"right",
					frame:true,
					defaults:{xtype:"textfield",width:250,height:150},
					items:[
					{xtype:"textarea",name:"contentxl",fieldLabel:"日志内容",id:"contentxl"}
					],
					html:'<font color="red">字数小于1000字<font>',
					buttons:[{text:"提交",
					handler:function(){
						var mainForm = document.getElementById("mainForm");	
						var content = document.getElementById("content");
						var date = document.getElementById("date");
						content.value = document.getElementById("contentxl").value;
						if(content.value.length>1000){
							alert("字数: " + content.value.length + "! 请不要超过 1000 字");
							return false;
						}
						date.value = datex.format('Y-m-d');
						mainForm.action = "<%=rootPath%>/userTaskLog.do?action=add";
						mainForm.submit({
							waitTitle:"请稍候",
							waitMsg:"正在提交表单数据，请稍候。。。。。。"
							});
						
						}},{text:"重置",
							handler:function(){			
								form.form.reset();
							}
							},
							{text:"关闭",
							handler:function(){
								win.destroy();		
								win.hide();
							}
							}]
									
					});
					var win=new Ext.Window({title:"添加新日志",
 							width:400,
 							height:300,
 							modal:true,
 							items:form,
 							maximizable:true}); 
 					win.show();
 					
				}
		},
		// 20091204 start---------------------------------------------------------
		/*
		'taskDblClick':{
				fn: function (task,dxview,calendar,refviewname){
					var datatest='Record No:' + task[0] + '<br>';
					datatest+='id-task:'  + task[1] + ' ' + task[2] + '<br>';
					datatest+='starts:'    + task[3] + '<br>';
 					datatest+='ends:'   + task[4] + '<br>';
 					datatest+='contents'  + task[5] + '<br>';	
					datatest+='index'     + task[6] + '<br>';	
					Ext.Msg.alert('Information on Object - ' + refviewname, datatest);		
				},
				scope:this 
		},
		*/
		// 20091204 end---------------------------------------------------------
		'taskDblClick':{
				fn: function (task,dxview,calendar,refviewname){
					var datatest='<div style="word-wrap:break-word;word-break:break-all;">';
					datatest+= '日   志 : ' + task[0] + '<br>';
					datatest+= '日志-id : '  + task[1]+ '<br>';
					datatest+= '用户名 : '    + task[2] + '<br>';
 					datatest+= '日  期 : '   + task[3] + '<br>';
 					datatest+= '内  容 :'  +  '<br>';
 					datatest+= '       ' + task[4] + '<br>';
 					datatest+='</div>';
 					Ext.Msg.alert('日志信息 ' , datatest);	
					
				},
				scope:this 
		},
		'beforeTaskDelete': {
				fn: function (datatask,dxview) { 
					return false; 
					// do your stuff to check if the event/task could be deleted 
				}, scope:this
		},
		'onTaskDelete':{
			fn:function(datatask){
				// 20091204 edit
				// var r=confirm("Delete event " + datatask[1] + " " + datatask[2] + "...? YES/NO" );
				var r=confirm("删除 日志 : " + datatask[1] + "  日期 : " + datatask[3] );
				return r; 
				// do your stuf for deletion and return the value 
			},scope:this
		},
	   'afterTaskDelete':{
	   		fn: function(datatask,action){
	   			// 20091204 edit
				// action ? alert("Event: " + datatask[1] + " " + datatask[2] + " Deleted"): alert("Event Delete was canceled..!"); 
				// perform any action after deleting the event/task
				if(action){
				    var mainForm = document.getElementById("mainForm");
				    var id = document.getElementById("id");
				    id.value = datatask[1];
					mainForm.action = "<%=rootPath%>/userTaskLog.do?action=delete";
					mainForm.submit();
				}
			},scope:this
 	    },
		'beforeTaskEdit': {
				fn: function (datatask,dxview) { 
					return false; 							
				}, scope:this
		},	
	   'onTaskEdit':{
			fn:function(datatask){
				var r=confirm("编辑 日志 :" + datatask[1] + "  日期 :　" + datatask[3] );
				return r; 
				// do your stuf for editing and return the value
			},scope:this
	    },
	    'afterTaskEdit':{
	   		fn: function(datatask,action){
				// perform any action after deleting the event/task
				if (action){ 
					var form=new Ext.form.FormPanel({
					labelWidth:60,
					labelAlign:"right",
					frame:true,
					defaults:{xtype:"textfield",width:250,height:150},
					items:[
					{xtype:"textarea",name:"contentxl",fieldLabel:"日志内容",id:"contentxl",value:datatask[4]}
					],
					html:'<font color="red" >字数小于1000字<font>',
					buttons:[{text:"提交",
					handler:function(){
						var mainForm = document.getElementById("mainForm");	
						var content = document.getElementById("content");
						var date = document.getElementById("date");
						var id = document.getElementById("id");
						content.value = document.getElementById("contentxl").value;
						if(content.value.length>1000){
							alert("字数: " + content.value.length + "! 请不要超过 1000 字");
							return false;
						}
						date.value = datatask[3];
						id.value = datatask[1];
						mainForm.action = "<%=rootPath%>/userTaskLog.do?action=edit";
						mainForm.submit({
							waitTitle:"请稍候",
							waitMsg:"正在提交表单数据，请稍候。。。。。。"
							});
						
						}},{text:"重置",
							handler:function(){			
								form.form.reset();
							}
							}]
									
					});
					var win=new Ext.Window({title:"编辑日志",
 							width:400,
 							height:300,
 							modal:true,
 							items:form,
 							maximizable:true}); 
 					win.show();
				} 
				return false; 
			},scope:this
	    },
		'beforeTaskMove':{
				fn: function (datatask,Taskobj,dxview,TaskEl) { // return "true" to cancel or "false" to go on 
					return false; 	
				}, scope:this
		},
		'TaskMoved':{
				fn: function (newDataTask,Taskobj,dxview,TaskEl) {   // do some stuff 
					var test=21;  // use breakpoint in firefox here 
					task = newDataTask; 
					datatest ='Task id:'  + task[0] + ' ' + task[2] + '<br>';
					datatest+='recid:'    + task[1] + '<br>';
					datatest+='starts:'    + task[3] + '<br>';
					datatest+='ends:'   + task[4] + '<br>';
					datatest+='contents:' + task[5] + '<br>';	
					datatest+='index:'    + task[6] + '<br>';	
					Ext.Msg.alert('Information Modified task', datatest);	
//					var myrecordtask = prueba.store.getAt( (parseInt(task[6])-1) ); //Alert...!   base number of index are 1  not 0 
//					myrecordtask.set('startdate',task[3] );
//					myrecordtask.set('enddate',task[4] );
//					prueba.store.commitChanges(); 			
					
				}, scope:this
		},
		'customMenuAction':{
				fn: function (MenuId, Currentview,datatask,objEl,dxview){
					var datatest = ''; 
					if (Currentview=='month'){ 
						task = datatask; 
						datatest ='Element ID :'  + task[0] + '<br>';
						datatest+='Task ID :'  + task[1] + '<br>';
						datatest+='Menu ID :'  + MenuId + '<br>';
						Ext.Msg.alert('(Month) Information- ' + Currentview, datatest);	
					} else if (Currentview=='day'){ 
						task = datatask; 
						datatest ='Task id:'  + task[0] + ' ' + task[1] + '<br>';
						datatest+='starts:'    + task[2] + '<br>';
 						datatest+='Ends:'   + task[3] + '<br>';
 						datatest+='contents:' + task[4] + '<br>';	
						datatest+='index:'    + task[5] + '<br>';	
						datatest+='Test Menu:'  + MenuId + '<br>';	
						Ext.Msg.alert('(Day) Task information' + Currentview, datatest);			
					} else if (Currentview=='week'){
						task = datatask; 
						datatest ='Task el-id:'  + task[0] + '  task id:' + task[1] + '<br>';
						datatest+='Subject:'     + task[2] + '<br>';
 						datatest+='Starts:'   + task[3] + '<br>';
 						datatest+='Ends:' + task[4] + '<br>';	
						datatest+='index:'    + task[6] + '<br>';	
						datatest+='Test Menu:'  + MenuId + '<br>';	
						Ext.Msg.alert('(Week) Task information' + Currentview, datatest);
					} else if (Currentview=='scheduler'){
						task = datatask; 
						datatest ='Task id:'  + task[0] + ':' + task[2] + '<br>';
						datatest+='starts:'    + task[3] + '<br>';
 						datatest+='Ends:'   + task[4] + '<br>';
 						datatest+='contents:' + task[5] + '<br>';	
						datatest+='index:'    + task[6] + '<br>';	
						datatest+='Test Menu:'  + MenuId + '<br>';	
						Ext.Msg.alert('(Scheduler) Task information' + Currentview, datatest);			
					} 
				},scope:this		
		}

	});
	prueba.render('calendar');
	
	Ext.EventManager.onWindowResize( function(){  prueba.refreshCalendarView();  });
	
}
/*
function testfechas(data) { 

	var pruebax = Ext.getCmp('test_calx');
	
	if (data==1) { 			alert( pruebax.getCalendarMonth() );
	} else if (data==2){	alert( pruebax.getCalendarYear() );  
	} else if (data==3){ 	alert( pruebax.getCalendarDay() );
	} else if (data==4){	alert( pruebax.getCalendarWeekDay('') );
	} else if (data==5){	
		var test=pruebax.getCalendarWeekDay('str'); 
		alert(test);
	} else { 				
		alert( pruebax.getCurrentDate() );
	} 
} 
function hideshow(datatochoose){
		var mysamples = Ext.select('tr.' + datatochoose,true); 
		mysamples.each(function(el, thisobj, index){
					var xtest = el;
					if (xtest.getStyle('display')=='none'){ 
						xtest.setStyle({display:''});
					} else { 
						xtest.setStyle({display:'none'} );
					} 
				 }
		);
}
function samplemonth(data){
	switch(data){
		case 1: 
			Ext.get("testsvc1").dom.checked ? prueba.viewmonth.showTaskcount=true : prueba.viewmonth.showTaskcount=false; 
			prueba.changeView('month',prueba.currentdate); 
			break; 
		case 2: 
			var valtochange = Ext.get("selectmonthaction").getValue(); 
			prueba.viewmonth.dayAction=valtochange; 
			break;
		case 3: 
			if (prueba.currentView!='month'){ alert ("first Change to month view..!"); return false; 	} 
			if (prueba.viewmonth.ZoomDay(prueba.currentdate.format('m/d/Y'))==false){ alert ("No events for this day...!"); }  
			break; 
		case 4:  // new feature on 0.0.9 
		 	var testnum = Ext.get("numlistingtasks").getValue(); 
			var pattern = /^[0-9]+$/;
			var testnumeric = pattern.test(testnum); 
			if (!testnumeric){ alert("Only numbers are allowed..!"); return false;}			
			if (testnum>20){   alert("20 its the limit to set the number of event list items"); return false; } 
			Ext.get("testlist_tasks1").dom.checked ? prueba.viewmonth.showTaskList=true : prueba.viewmonth.showTaskList=false; 
			prueba.viewmonth.showNumTasks = testnum; 
			prueba.changeView('month',prueba.currentdate);	
			break; 				
		case 5: // new feature on 0.0.11 - TaskList_launchEventOn
			var teststr = Ext.get("selectmonth_tasktimeaction").getValue(); 
			if (teststr=='none'){ 	teststr =''; } 
			prueba.viewmonth.TaskList_launchEventOn=teststr; 
			prueba.changeView('month',prueba.currentdate);
			break; 
		case 6: // new feature on 0.0.11 - TaskList_launchEventOn
			var myShowMenuItems=[0,0,0,0,0,0]; 
			Ext.get("m_item_1_checkbox").dom.checked ? myShowMenuItems[0]=true : myShowMenuItems[0]=false;
			Ext.get("m_item_2_checkbox").dom.checked ? myShowMenuItems[1]=true : myShowMenuItems[1]=false;
			Ext.get("m_item_3_checkbox").dom.checked ? myShowMenuItems[2]=true : myShowMenuItems[2]=false;
			Ext.get("m_item_4_checkbox").dom.checked ? myShowMenuItems[3]=true : myShowMenuItems[3]=false;
			Ext.get("m_item_5_checkbox").dom.checked ? myShowMenuItems[4]=true : myShowMenuItems[4]=false;
			Ext.get("m_item_6_checkbox").dom.checked ? myShowMenuItems[5]=true : myShowMenuItems[5]=false;
			prueba.viewmonth.ShowMenuItems = myShowMenuItems; 
			prueba.changeView('month',prueba.currentdate);
			break; 
		case 7: 
			var myTaskList_ShowMenuItems=[0,0,0]; 
			Ext.get("m2_item_1_checkbox").dom.checked ? myTaskList_ShowMenuItems[0]=true : myTaskList_ShowMenuItems[0]=false;
			Ext.get("m2_item_2_checkbox").dom.checked ? myTaskList_ShowMenuItems[1]=true : myTaskList_ShowMenuItems[1]=false;
			Ext.get("m2_item_3_checkbox").dom.checked ? myTaskList_ShowMenuItems[2]=true : myTaskList_ShowMenuItems[2]=false;
			prueba.viewmonth.TaskList_ShowMenuItems = myTaskList_ShowMenuItems; 
			prueba.changeView('month',prueba.currentdate);
			break; 		
		default: 
			return false; 
			break; 
	} 
} 
function sampleday(data){
	switch(data){
		case 1: 
			var newval = Ext.get("numtskwidthday").getValue();
			var regxpstr = newval;
			var pattern = /^[0-9]*$/
			if ( pattern.test(regxpstr) == false) {
				alert("new width must be an integer..!");
			} else{ 
				(newval<20)?  prueba.viewday.task_width=20  : prueba.viewday.task_width = newval; 
				(newval>120)? prueba.viewday.task_width=120 : prueba.viewday.task_width = newval; 
				prueba.refreshCalendarView(true); 				
			}
			break; 
		case 2: 
			var newvalcss = Ext.get("select_day_clasover_sample").getValue();
			prueba.viewday.task_clsOver = newvalcss; 
			prueba.refreshCalendarView(true); 						
			break;
		case 3: 
			var newval = parseInt(Ext.get("select_day_allowdd_sample").getValue());
			prueba.viewday.task_DDeditable = newval; 
			prueba.refreshCalendarView(true); 						
			break;
		case 4: 
			var myShowMenuItems=[0,0,0,0,0,0]; 
			Ext.get("d_item_1_checkbox").dom.checked ? myShowMenuItems[0]=true : myShowMenuItems[0]=false;
			Ext.get("d_item_2_checkbox").dom.checked ? myShowMenuItems[1]=true : myShowMenuItems[1]=false;
			Ext.get("d_item_3_checkbox").dom.checked ? myShowMenuItems[2]=true : myShowMenuItems[2]=false;
			Ext.get("d_item_4_checkbox").dom.checked ? myShowMenuItems[3]=true : myShowMenuItems[3]=false;
			Ext.get("d_item_5_checkbox").dom.checked ? myShowMenuItems[4]=true : myShowMenuItems[4]=false;
			Ext.get("d_item_6_checkbox").dom.checked ? myShowMenuItems[5]=true : myShowMenuItems[5]=false;
			prueba.viewday.ShowMenuItems = myShowMenuItems; 
			prueba.refreshCalendarView(true);
			break; 
		case 5: 
			var myTaskList_ShowMenuItems=[0,0,0,0,0]; 
			Ext.get("d2_item_1_checkbox").dom.checked ? myTaskList_ShowMenuItems[0]=true : myTaskList_ShowMenuItems[0]=false;
			Ext.get("d2_item_2_checkbox").dom.checked ? myTaskList_ShowMenuItems[1]=true : myTaskList_ShowMenuItems[1]=false;
			Ext.get("d2_item_3_checkbox").dom.checked ? myTaskList_ShowMenuItems[2]=true : myTaskList_ShowMenuItems[2]=false;
			Ext.get("d2_item_4_checkbox").dom.checked ? myTaskList_ShowMenuItems[3]=true : myTaskList_ShowMenuItems[3]=false;
			Ext.get("d2_item_5_checkbox").dom.checked ? myTaskList_ShowMenuItems[4]=true : myTaskList_ShowMenuItems[4]=false;
			prueba.viewday.task_ShowMenuItems = myTaskList_ShowMenuItems; 
			prueba.refreshCalendarView(true);
			break; 
		case 6: 
			var teststr = Ext.get("selectday_tasktimeaction").getValue(); 
			if (teststr=='none'){ 	teststr =''; } 
			prueba.viewday.task_eventLaunch=teststr; 
			prueba.refreshCalendarView(true);
			break; 
		case 7: 
			var newval = parseInt(Ext.get("select_day_ftw_sample").getValue());
			prueba.viewday.forceTaskFit = newval;
			prueba.refreshCalendarView(true);
			break;	
		case 8:
			var newvalhtmct = Ext.get("select_day_htmlct_sample").getValue();
			if (newvalhtmct==""){ newvalhtmct='before'; } 
			prueba.viewday.customHTMLinpos = newvalhtmct; 
			prueba.refreshCalendarView(true); 						
			break;
		default: 
			return false; 
			break; 
	} 
}
function sampleweek(data){
	switch(data){
		case 1: 
			//alert ("hola"); 
			var newval = Ext.get("numtskwidthweek").getValue();
			var regxpstr = newval;
			var pattern = /^[0-9]*$/
			if ( pattern.test(regxpstr) == false) {
				alert("new width must be an integer..!");
			} else{ 
				(newval<20)?  prueba.viewweek.task_width=20  : prueba.viewweek.task_width = newval; 
				(newval>120)? prueba.viewweek.task_width=120 : prueba.viewweek.task_width = newval; 
				prueba.refreshCalendarView(true); 				
			}
			//alert ("hola2"); 			
			break;  
		case 2: 
			var newvalcss = Ext.get("select_week_clasover_sample").getValue();
			prueba.viewweek.task_clsOver = newvalcss; 
			prueba.refreshCalendarView(true); 						
			break;	
		case 3: 
			var teststr = Ext.get("selectweek_tasktimeaction").getValue(); 
			if (teststr=='none'){ 	teststr =''; } 
			prueba.viewweek.task_eventLaunch=teststr; 
			prueba.refreshCalendarView(true);
			break;	
		case 4:
			var myShowMenuItems=[0,0,0,0,0,0]; 
			Ext.get("w_item_1_checkbox").dom.checked ? myShowMenuItems[0]=true : myShowMenuItems[0]=false;
			Ext.get("w_item_2_checkbox").dom.checked ? myShowMenuItems[1]=true : myShowMenuItems[1]=false;
			Ext.get("w_item_3_checkbox").dom.checked ? myShowMenuItems[2]=true : myShowMenuItems[2]=false;
			Ext.get("w_item_4_checkbox").dom.checked ? myShowMenuItems[3]=true : myShowMenuItems[3]=false;
			Ext.get("w_item_5_checkbox").dom.checked ? myShowMenuItems[4]=true : myShowMenuItems[4]=false;
			Ext.get("w_item_6_checkbox").dom.checked ? myShowMenuItems[5]=true : myShowMenuItems[5]=false;
			prueba.viewweek.ShowMenuItems = myShowMenuItems; 
			prueba.refreshCalendarView(true);
			break; 	
		case 5: 
			var myTaskList_ShowMenuItems=[0,0,0,0,0]; 
			Ext.get("w2_item_1_checkbox").dom.checked ? myTaskList_ShowMenuItems[0]=true : myTaskList_ShowMenuItems[0]=false;
			Ext.get("w2_item_2_checkbox").dom.checked ? myTaskList_ShowMenuItems[1]=true : myTaskList_ShowMenuItems[1]=false;
			Ext.get("w2_item_3_checkbox").dom.checked ? myTaskList_ShowMenuItems[2]=true : myTaskList_ShowMenuItems[2]=false;
			Ext.get("w2_item_4_checkbox").dom.checked ? myTaskList_ShowMenuItems[3]=true : myTaskList_ShowMenuItems[3]=false;
			Ext.get("w2_item_5_checkbox").dom.checked ? myTaskList_ShowMenuItems[4]=true : myTaskList_ShowMenuItems[4]=false;
			prueba.viewweek.task_ShowMenuItems = myTaskList_ShowMenuItems; 
			prueba.refreshCalendarView(true);
			break; 	
		case 6: 				
			var teststr = Ext.get("select_week_actionclick").getValue(); 
			if (teststr=='none'){ 	teststr ='none'; } 
			prueba.viewweek.headerDayClick=teststr; 
			prueba.refreshCalendarView(true);
			break;
		case 7:
			var teststr = Ext.get("select_week_styleview").getValue(); 
			prueba.viewweek.style=teststr; 
			prueba.refreshCalendarView(true);
			break;
		case 8: 
			var newval = parseInt(Ext.get("select_w_ftw_sample").getValue());
			prueba.viewweek.forceTaskFit = newval;
			prueba.refreshCalendarView(true);
			break;
		case 9: 
			var newvalhtmct = Ext.get("select_w_htmlct_sample").getValue();
			if (newvalhtmct==""){ newvalhtmct='before'; } 
			prueba.viewweek.customHTMLinpos = newvalhtmct; 
			prueba.refreshCalendarView(true); 
			break;
		case 10:
			var newval = parseInt(Ext.get("select_week_sd").getValue());
			prueba.viewweek.startDay = newval;
			prueba.refreshCalendarView(true);
			break;
		default: 
			return false; 
			break; 
	} 
}
function samplesched(data){
	switch(data){
		case 1: 
			var teststr = Ext.get("select_sched_actionclick").getValue(); 
			if (teststr=='none'){ 	teststr ='none'; } 
			prueba.viewscheduler.headerAction=teststr; 
			prueba.refreshCalendarView(true);		
			break;
		case 2: 
			var teststr = parseInt(Ext.get("select_sched_colorusebg").getValue()); 
			prueba.viewscheduler.listItems.useStoreColor=teststr; 
			prueba.refreshCalendarView(true);
			break;
		case 3: 
			var newval = parseInt(Ext.get("numlistItemwidth").getValue());
			var regxpstr = newval;
			var pattern = /^[0-9]*$/
			if ( pattern.test(regxpstr) == false) {
				alert("new width must be an integer..!");
			} else{ 
				(newval<100)?  prueba.viewscheduler.listItems.descriptionWidth=100 : prueba.viewscheduler.listItems.descriptionWidth = newval; 
				(newval>400)?  prueba.viewscheduler.listItems.descriptionWidth=400 : prueba.viewscheduler.listItems.descriptionWidth = newval; 
				prueba.refreshCalendarView(true); 				
			}		
			break;
		case 4: 
			var teststr = Ext.get("select_schedmouseaction").getValue(); 
			if (teststr=='none' || teststr==''){ teststr =''; } 
			prueba.viewscheduler.listItems.launchEventOn=teststr; 
			prueba.refreshCalendarView(true);	
			break;
		case 5:  // units 
			var teststr = parseInt(Ext.get("select_sched_units").getValue());
			prueba.viewscheduler.listbody.headerUnit = teststr; 
			// Validate possible combinations For displaying  on the scheduler header body 
			if (prueba.viewscheduler.listbody.periodType>=1 || prueba.viewscheduler.listbody.periodType<=4){ 
				if (prueba.viewscheduler.listbody.headerUnit==0) { //For everything else cant' apply hours 
					prueba.viewscheduler.listbody.headerUnit=1; 
				} 
			} 
			if (prueba.viewscheduler.listbody.periodType==1 || prueba.viewscheduler.listbody.periodType==2){  // Month and week cant apply Week units 
				if (prueba.viewscheduler.listbody.headerUnit!=1) { //For everything else can weeks 
					prueba.viewscheduler.listbody.headerUnit=1; 
				} 
			} 
			if (prueba.viewscheduler.listbody.periodType==0 && prueba.viewscheduler.listbody.headerUnit!=0){ 
				prueba.viewscheduler.listbody.headerUnit=0; 
			} //For days cann appply only hours 
			prueba.refreshCalendarView(true);	
			break; 
		case 6:  // period type 
			var teststr = parseInt(Ext.get("select_shced_period").getValue());
			prueba.viewscheduler.listbody.periodType = teststr; 
			// Validate possible combinations For displaying  on the scheduler header body 
			if (prueba.viewscheduler.listbody.periodType>=1 || prueba.viewscheduler.listbody.periodType<=4){ 
				if (prueba.viewscheduler.listbody.headerUnit==0) { //For everything else cant' apply hours 
					prueba.viewscheduler.listbody.headerUnit=1; 
				} 
			} 
			if (prueba.viewscheduler.listbody.periodType==1 || prueba.viewscheduler.listbody.periodType==2){  // Month and week cant apply Week units 
				if (prueba.viewscheduler.listbody.headerUnit!=1) { //For everything else can weeks 
					prueba.viewscheduler.listbody.headerUnit=1; 
				} 
			} 
			if (prueba.viewscheduler.listbody.periodType==0 && prueba.viewscheduler.listbody.headerUnit!=0){ 
				prueba.viewscheduler.listbody.headerUnit=0; 
			} //For days cann appply only hours 
			prueba.refreshCalendarView(true);	
			break; 
		case 7: 
			var newval = parseInt(Ext.get("numlistUnitwidth").getValue());
			var regxpstr = newval;
			var pattern = /^[0-9]*$/
			if ( pattern.test(regxpstr) == false) {
				alert("new width must be an integer..!");
			} else{ 
				(newval<25)?  prueba.viewscheduler.listbody.headerUnitWidth=25 : prueba.viewscheduler.listbody.headerUnitWidth = newval; 
				(newval>80)?  prueba.viewscheduler.listbody.headerUnitWidth=80 : prueba.viewscheduler.listbody.headerUnitWidth = newval; 
				prueba.refreshCalendarView(true); 				
			}	
			break; 
		case 8: //clsover 
			var teststr = Ext.get("select_sched_clasover_sample").getValue(); 
			prueba.viewscheduler.listItems.taskdd_clsOver= teststr; 
			prueba.refreshCalendarView(true); 
			break; 
		case 9:  // use names on the timeline ? 
			var teststr = parseInt(Ext.get("select_sched_usenames").getValue()); 
			prueba.viewscheduler.listItems.taskdd_shownames= teststr; 
			prueba.refreshCalendarView(true);
			break; 
		case 10: // menu Items ListItems 
			var myShowMenuItems=[0,0,0,0,0,0,0,0]; 
			Ext.get("s_item_1_checkbox").dom.checked ? myShowMenuItems[0]=true : myShowMenuItems[0]=false;
			Ext.get("s_item_2_checkbox").dom.checked ? myShowMenuItems[1]=true : myShowMenuItems[1]=false;
			Ext.get("s_item_3_checkbox").dom.checked ? myShowMenuItems[2]=true : myShowMenuItems[2]=false;
			Ext.get("s_item_4_checkbox").dom.checked ? myShowMenuItems[3]=true : myShowMenuItems[3]=false;
			Ext.get("s_item_5_checkbox").dom.checked ? myShowMenuItems[4]=true : myShowMenuItems[4]=false;
			Ext.get("s_item_6_checkbox").dom.checked ? myShowMenuItems[5]=true : myShowMenuItems[5]=false;
			Ext.get("s_item_7_checkbox").dom.checked ? myShowMenuItems[5]=true : myShowMenuItems[5]=false;
			Ext.get("s_item_8_checkbox").dom.checked ? myShowMenuItems[5]=true : myShowMenuItems[5]=false;
			prueba.viewscheduler.listItems.ShowMenuItems = myShowMenuItems; 
			prueba.refreshCalendarView(true);
			break; 
		case 11: // menuItems for task 
			var myTaskList_ShowMenuItems=[0,0,0]; 
			Ext.get("s2_item_1_checkbox").dom.checked ? myTaskList_ShowMenuItems[0]=true : myTaskList_ShowMenuItems[0]=false;
			Ext.get("s2_item_2_checkbox").dom.checked ? myTaskList_ShowMenuItems[1]=true : myTaskList_ShowMenuItems[1]=false;
			Ext.get("s2_item_3_checkbox").dom.checked ? myTaskList_ShowMenuItems[2]=true : myTaskList_ShowMenuItems[2]=false;
			prueba.viewscheduler.listItems.taskdd_ShowMenuItems = myTaskList_ShowMenuItems; 
			prueba.refreshCalendarView(true);
			break;		
		default: 
			return false; 
			break; 
	} 
} 
*/
</script>
<style type="text/css">
body{margin-left: 10px;	margin-top: 10px;	margin-right: 10px;	margin-bottom: 10px;}
.maintitle {font-family: Verdana, Arial, Helvetica, sans-serif;	font-size: 18px;color: #003366;}
.mainsubtitle {font-family: Verdana, Arial, Helvetica, sans-serif;	font-size: 14px;color: #003366;}
.main_notes{	font-family: Verdana, Arial, Helvetica, sans-serif;	font-size: 9px;}
.task_time {background-image: url(<%=rootPath %>/system/usertasklog/resource/imgs/clock.png);}
.task_time_off {background-image:url(<%=rootPath %>/system/usertasklog/resource/imgs/clock_red.png);}
#samplebox #sampleboxtitle {font-family: Verdana, Arial, Helvetica, sans-serif;	font-size: 14px;color: #FFFFFF;	background-color: #003366;padding: 3px;}
#samplebox {font-family: Verdana, Arial, Helvetica, sans-serif;	font-size: 11px;}
#samplebox .textsample{	font-family: Verdana, Arial, Helvetica, sans-serif;	font-size: 10px;}
.settings_monthview {font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 14px;color: #FFFFFF;	background-color: #006633;padding:4px;font-weight: bold; cursor:pointer;}
.settings_overview  {font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 14px;color: #FFFFFF; background-color: #006699;padding:4px;font-weight: bold;cursor:pointer;}
.style1 {font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 9px; font-weight: bold; }
</style>
</head>
<body onLoad="test_calendar();">
	<form method="post" name="mainForm" id="mainForm">
		<input type="hidden" name="content" id="content">
		<input type="hidden" name="date" id="date">
		<input type="hidden" name="id" id="id">
		<table width="100%" border="0" align="center">
			<tr>
				<td width="200px" valign=top align=center>
					<%=menuTable%>
				</td>
				<td width="900" height= "" valign="top">
					<div id="calendar"></div>
					<div id="pruebapanel"></div>
					<hr color="#003366" noshade>
					<!-- 
					<p>&nbsp;</p>
					<div id="samplebox">
					    <div id="sampleboxtitle">Sample BOX</div>
						<div id="newdateselectorsample"></div> 
					    <hr color="#003366" noshade>
					    <div id="samplebox_cview"><b>Current View</b>: Month</div>
					  	<p>&nbsp;</p>
					    <div>	
					<div >
					
					                <table width="77%" border="0" align="center">
					                  <tr class="textsample">
					                    <td colspan="2" ><div id="calendar_hdr_samples" class="settings_monthview">Calendar</div></td>
					                  </tr>
					                  <tr class="textsample">
					                    <td colspan="2" >
					                      <div align="center">
					                        <input type="button" name="buttondates1" id="buttondates1" value="Year" 		onClick="testfechas(2);" />
					                        <input type="button" name="buttondates2" id="buttondates2" value="Month"      	onClick="testfechas(1);" />
					                        <input type="button" name="buttondates3" id="buttondates3" value="Day" 			onClick="testfechas(3);" />
					                        <input type="button" name="buttondates4" id="buttondates4" value="Week Day"   	onClick="testfechas(4);" />
					                        <input type="button" name="buttondates5" id="buttondates5" value="Week Day 2" 	onClick="testfechas(5);" />  
					                        <input type="button" name="buttondates6" id="buttondates6" value="Working Date" onClick="testfechas(6);" />                   
					                      </div></td>
					                  </tr>
					                  <tr class="textsample">
					                    <td colspan="2"><div id="calendar_hdr_monthx" class="settings_monthview">Month view</div></td>
					                  </tr>
					                  <tr class="textsample monthsample">
					                <td width="47%" ><b>Month - showtask count on each day : </b></td>
					                <td width="53%">
					                <form name="formshowtaskcount" id="formshowtaskcount">
					                <label><input name="showtaskcountval" type="radio"  value="1" id="testsvc1" >Yes</label>
					                <label><input name="showtaskcountval" type="radio"  value="0" id="testsvc2" checked>No</label>
					                &nbsp;&nbsp;<input type="button" name="button4" id="button4" value="Refresh Calendar" onClick="samplemonth(1);">
					                </form></td></tr>
					                  <tr class="textsample monthsample">
					                    <td ><b>Month - show task List on each day : </b>&nbsp;</td>
					                    <td>
					                    <form name="formshowtasklist" id="formshowtasklist"># of tasks
					                    <input name="numlistingtasks" type="text" id="numlistingtasks" value="5" size="6" maxlength="3">
					                    <label><input name="showtaskcountval" type="radio"  value="1" id="testlist_tasks1" checked>Yes</label>
					                    <label><input name="showtaskcountval" type="radio"  value="0" id="testlist_tasks2" >No</label>
					                    &nbsp;&nbsp;<input type="button" name="button4" id="button4" value="Refresh Calendar" 
					                    onClick="samplemonth(4);">
					                    </form></td>
					                  </tr>
					                  <tr class="textsample monthsample">
					                    <td ><b>Month  - Select action to Perfom on day click</b> </td>
					                    <td><form name="form1" method="post" action="">
					                      <select name="selectmonthaction" id="selectmonthaction">
					                        <option value="viewday">Change to  Day view</option>
					                        <option value="window">Show Window With tasks</option>
					                        <option value="event">Launch Event</option>
					                      </select>
					                            <input type="button" name="button5" id="button5" value="change" onClick="samplemonth(2);">
					                    </form>                    </td>
					                  </tr>
					                  <tr class="textsample monthsample">
					                    <td ><b>Month - Manually Zoom Day tasks</b></td>
					                    <td><input type="button" name="button6" id="button6" value="Zoom" onClick="samplemonth(3);"></td>
					                  </tr>
					                  <tr class="textsample monthsample">
					                    <td ><b>Month - Launch Event for Task Item (task list)</b></td>
					                    <td><form name="formeventonmonth" method="post" action="">
					                      <select name="selectmonth_tasktimeaction" id="selectmonth_tasktimeaction">
					                        <option value="none">none</option>
					                        <option value="click">Click</option>
					                        <option value="dblclick" selected>Double Click</option>
					                      </select>
					                      <input type="button" name="button2" id="button2" value="change" onClick="samplemonth(5);">
					                    </form></td>
					                  </tr>
					                  <tr class="textsample monthsample">
					                    <td valign="top" ><b>Month - Menu ITEMS (day element)</b></td>
					                    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
					                      <tr class="textsample">
					                        <td width="49%"><div align="left">ADD</div></td>
					                        <td width="51%"><div align="center">
					                          <input name="m_item_1_checkbox" type="checkbox" id="m_item_1_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Next Month</div></td>
					                        <td><div align="center">
					                          <input name="m_item_2_checkbox" type="checkbox" id="m_item_2_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Previous Month</div></td>
					                        <td><div align="center">
					                          <input name="m_item_3_checkbox" type="checkbox" id="m_item_3_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Change to Week view</div></td>
					                        <td><div align="center">
					                          <input name="m_item_4_checkbox" type="checkbox" id="m_item_4_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Change to Day View</div></td>
					                        <td><div align="center">
					                          <input name="m_item_5_checkbox" type="checkbox" id="m_item_5_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Change to Scheduler View</div></td>
					                        <td><div align="center">
					                          <input name="m_item_6_checkbox" type="checkbox" id="m_item_6_checkbox" checked>
					                        </div></td>
					                      </tr>
					                    </table>
					                    <p align="center"><input type="button" name="button4" id="button4" value="Refresh Calendar" 
					                    onClick="samplemonth(6);"></p>                    </td>
					                  </tr>
					                  <tr class="textsample monthsample">
					                    <td valign="top" ><b>Month - Menu ITEMS For TASK (task list)</b></td>
					                    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
					                      <tr class="textsample">
					                        <td width="49%">ADD</td>
					                        <td width="51%"><div align="center">
					                          <input name="m2_item_1_checkbox" type="checkbox" id="m2_item_1_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Delete</td>
					                        <td><div align="center">
					                          <input name="m2_item_2_checkbox" type="checkbox" id="m2_item_2_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Edit</td>
					                        <td><div align="center">
					                          <input name="m2_item_3_checkbox" type="checkbox" id="m2_item_3_checkbox" checked>
					                        </div></td>
					                      </tr>
					                    </table>
					                     <p align="center"><input type="button" name="button4" id="button4" value="Refresh Calendar" 
					                     onClick="samplemonth(7);"></p></td>
					                  </tr>                  
					                  <tr class="textsample">
					                    <td colspan="2" ><div id="calendar_hdr_dayx" class="settings_monthview">Day view</div></td>
					                  </tr>
					                  <tr class="textsample daysample">
					                    <td ><b>Day - Change Event(task) Width </b></td>
					                    <td><input name="numtskwidthday" type="text" id="numtskwidthday" value="50" size="6" maxlength="3">
					                    <input type="submit" name="button" id="button" value="change" onClick="sampleday(1);"></td>
					                  </tr>
					                  <tr class="textsample daysample">
					                    <td ><b>Day - Force Task Width (0.0.14)</b></td>
					                    <td><select name="select_day_ftw_sample" id="select_day_ftw_sample">
					                       <option value="1">Yes</option>
					                       <option value="0" selected>No</option> 
					                       </select>
					                     <input type="submit" name="button9" id="button9" value="change" onClick="sampleday(7);"></td>
					                  </tr>
					                   <tr class="textsample daysample">
					                     <td ><b>Day - Custom HTML pos (0.0.14)</b> check 12-Dic-2008 for custom HTML</td>
					                     <td ><select name="select_day_htmlct_sample" id="select_day_htmlct_sample">
					                       <option value="before" selected>Before</option>
					                       <option value="after">After</option>
					                     </select>
					                     <input type="submit" name="button8" id="button8" value="change" onClick="sampleday(8);"></td>
					                   </tr>                  
					                   <tr class="textsample daysample">
					                     <td ><b>Day - task_clsOver Sample</b></td>
					                     <td ><select name="select_day_clasover_sample" id="select_day_clasover_sample">
					                       <option value="test_taskovercss" selected>test_taskovercss</option>
					                       <option value="test_taskovercss_b">test_taskovercss_b</option>
					                     </select>
					                       <input type="submit" name="button8" id="button8" value="change" onClick="sampleday(2);"></td>
					                   </tr>
					                   <tr class="textsample daysample">
					                     <td ><b>Day -</b> <b>Launch Event for Task Item</b></td>
					                     <td ><form name="formeventonday" method="post" action="">
					                      <select name="selectday_tasktimeaction" id="selectday_tasktimeaction">
					                        <option value="none">none</option>
					                        <option value="click">Click</option>
					                        <option value="dblclick" selected>Double Click</option>
					                      </select>
					                      <input type="button" name="button2" id="button2" value="change" onClick="sampleday(6);">
					                    </form></td>
					                   </tr>
					                   <tr class="textsample daysample">
					                     <td ><b>Day - task - Allow DD and resizable</b></td>
					                     <td ><select name="select_day_allowdd_sample" id="select_day_allowdd_sample">
					                       <option value="1" selected>Yes</option>
					                       <option value="0">No</option>
					                                                               </select>
					                     <input type="submit" name="button9" id="button9" value="change" onClick="sampleday(3);"></td>
					                   </tr>
					                   <tr class="textsample daysample">
					                     <td valign="top" ><b>Day - Menu Items on Day Body element</b></td>
					                     <td ><table width="100%" border="0" cellspacing="0" cellpadding="0">
					                       <tr class="textsample">
					                         <td width="49%"><div align="left">ADD</div></td>
					                         <td width="51%"><div align="center">
					                           <input name="d_item_1_checkbox2" type="checkbox" id="d_item_1_checkbox2" checked>
					                         </div></td>
					                       </tr>
					                       <tr class="textsample">
					                         <td><div align="left">Next Day</div></td>
					                         <td><div align="center">
					                           <input name="d_item_2_checkbox2" type="checkbox" id="d_item_2_checkbox2" checked>
					                         </div></td>
					                       </tr>
					                       <tr class="textsample">
					                         <td><div align="left">Previous Day</div></td>
					                         <td><div align="center">
					                           <input name="d_item_3_checkbox2" type="checkbox" id="d_item_3_checkbox2" checked>
					                         </div></td>
					                       </tr>
					                       <tr class="textsample">
					                         <td><div align="left">Change to Month view</div></td>
					                         <td><div align="center">
					                           <input name="d_item_4_checkbox2" type="checkbox" id="d_item_4_checkbox2" checked>
					                         </div></td>
					                       </tr>
					                       <tr class="textsample">
					                         <td><div align="left">Change to Week View</div></td>
					                         <td><div align="center">
					                           <input name="d_item_5_checkbox2" type="checkbox" id="d_item_5_checkbox2" checked>
					                         </div></td>
					                       </tr>
					                       <tr class="textsample">
					                         <td><div align="left">Change to Scheduler View</div></td>
					                         <td><div align="center">
					                           <input name="d_item_6_checkbox2" type="checkbox" id="d_item_6_checkbox2" checked>
					                         </div></td>
					                       </tr>
					                     </table>
					                      <p align="center"><input type="button" name="button4" id="button4" value="Refresh Calendar" 
					                    onClick="sampleday(4);"></p>                     </td>
					                   </tr>
					                   <tr class="textsample daysample">
					                     <td valign="top" ><b>Day - Menu Items on each Task</b></td>
					                     <td ><table width="100%" border="0" cellspacing="0" cellpadding="0">
					                      <tr class="textsample">
					                        <td width="49%">ADD</td>
					                        <td width="51%"><div align="center">
					                          <input name="d2_item_1_checkbox" type="checkbox" id="d2_item_1_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Delete</td>
					                        <td><div align="center">
					                          <input name="d2_item_2_checkbox" type="checkbox" id="d2_item_2_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Edit</td>
					                        <td><div align="center">
					                          <input name="d2_item_3_checkbox" type="checkbox" id="d2_item_3_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Next Day</td>
					                        <td><div align="center">
					                          <input name="d2_item_4_checkbox" type="checkbox" id="d2_item_4_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Previous day</td>
					                        <td><div align="center">
					                          <input name="d2_item_5_checkbox" type="checkbox" id="d2_item_5_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      
					                    </table>
					                     <p align="center"><input type="button" name="button4" id="button4" value="Refresh Calendar" 
					                     onClick="sampleday(5);"></p></td>
					                   </tr>
					                   <tr class="textsample">
					                    <td colspan="2" ><div id="calendar_hdr_weekx" class="settings_monthview">Week view</div></td>
					                  </tr>
					                   <tr class="textsample weeksample">
					                     <td ><b>Week - Start Day (0.0.14)</b></td>
					                     <td><select name="select_week_sd" id="select_week_sd">
					                       <option value="0" selected>Sunday</option>
					                       <option value="1">Monday</option>
					                       <option value="6">Saturday</option>
					                     </select>
					                       <input type="submit" name="button8" id="button8" value="change" onClick="sampleweek(10);"></td>
					                   </tr>                  
					                   <tr class="textsample weeksample">
					                     <td ><b>Week - Style view</b></td>
					                     <td><select name="select_week_styleview" id="select_week_styleview">
					                       <option value="plain">Plain</option>
					                       <option value="google" selected>Google</option>
					                     </select>
					                       <input type="submit" name="button8" id="button8" value="change" onClick="sampleweek(7);"></td>
					                   </tr>
					                  <tr class="textsample weeksample">
					                    <td ><b>Week - Change Event(task) Width</b></td>
					                    <td><input name="numtskwidthweek" type="text" id="numtskwidthweek" value="50" size="6" maxlength="3">
					                    <input type="submit" name="buttonweek" id="buttonweek" value="change" onClick="sampleweek(1);"></td>
					                  </tr>
					                  <tr class="textsample weeksample">
					                    <td ><b>Week - Force Task Width (0.0.14)</b></td>
					                    <td><select name="select_w_ftw_sample" id="select_w_ftw_sample">
					                       <option value="1">Yes</option>
					                       <option value="0" selected>No</option> 
					                       </select>
					                     <input type="submit" name="button9" id="button9" value="change" onClick="sampleweek(8);"></td>
					                  </tr> 
										<tr class="textsample weeksample">
					                     <td ><b>Week - Custom HTML pos (0.0.14)</b> check 12-Dic-2008 for custom HTML</td>
					                     <td ><select name="select_w_htmlct_sample" id="select_w_htmlct_sample">
					                       <option value="before" selected>Before</option>
					                       <option value="after">After</option>
					                     </select>
					                     <input type="submit" name="button8" id="button8" value="change" onClick="sampleweek(9);"></td>
					                   </tr>                                    
					                  <tr class="textsample weeksample">
					                    <td ><b>Week- Day's Header click action</b></td>
					                    <td ><select name="select_week_actionclick" id="select_week_actionclick">
					                      <option value="none">none</option>
					                      <option value="viewday" selected>Go to Day view</option></select>
										   <input type="submit" name="buttonweekaction" id="buttonweekaction" value="change" onClick="sampleweek(6);">                                                                                </td>
					                  </tr>
					                  <tr class="textsample weeksample">
					                     <td ><b>Week - task_clsOver Sample</b></td>
					                     <td ><select name="select_week_clasover_sample" id="select_week_clasover_sample">
					                       <option value="test_taskovercss" selected>test_taskovercss</option>
					                       <option value="test_taskovercss_b">test_taskovercss_b</option>
					                     </select>
					                       <input type="submit" name="button8" id="button8" value="change" onClick="sampleweek(2);"></td>
					                   </tr>
										<tr class="textsample weeksample">
					                     <td ><b>Week -</b> <b>Launch Event for Task Item</b></td>
					                     <td ><form name="formeventonweek" method="post" action="">
					                      <select name="selectweek_tasktimeaction" id="selectweek_tasktimeaction">
					                        <option value="none">none</option>
					                        <option value="click">Click</option>
					                        <option value="dblclick" selected>Double Click</option>
					                      </select>
					                      <input type="button" name="button2" id="button2" value="Change" onClick="sampleweek(3);">
					                    </form></td>
					                   </tr>  
									  <tr class="textsample weeksample">
					                    <td valign="top" ><b>Week - Menu ITEMS (day body)</b></td>
					                    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
					                      <tr class="textsample">
					                        <td width="49%"><div align="left">ADD</div></td>
					                        <td width="51%"><div align="center">
					                          <input name="w_item_1_checkbox" type="checkbox" id="w_item_1_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Next Week</div></td>
					                        <td><div align="center">
					                          <input name="w_item_2_checkbox" type="checkbox" id="w_item_2_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Previous Week</div></td>
					                        <td><div align="center">
					                          <input name="w_item_3_checkbox" type="checkbox" id="w_item_3_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Change to Month view</div></td>
					                        <td><div align="center">
					                          <input name="w_item_4_checkbox" type="checkbox" id="w_item_4_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Change to Day View</div></td>
					                        <td><div align="center">
					                          <input name="w_item_5_checkbox" type="checkbox" id="w_item_5_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Change to Scheduler View</div></td>
					                        <td><div align="center">
					                          <input name="w_item_6_checkbox" type="checkbox" id="w_item_6_checkbox" checked>
					                        </div></td>
					                      </tr>
					                    </table>
					                    <p align="center"><input type="button" name="buttonw4" id="buttonw4" value="Refresh Calendar" 
					                    onClick="sampleweek(4);"></p>                    </td>
					                  </tr>                   
					<tr class="textsample weeksample">
					                     <td valign="top" ><b>Week - Menu Items on each Task</b> <b>(normal tasks)</b></td>
					                     <td ><table width="100%" border="0" cellspacing="0" cellpadding="0">
					                      <tr class="textsample">
					                        <td width="49%">ADD</td>
					                        <td width="51%"><div align="center">
					                          <input name="w2_item_1_checkbox" type="checkbox" id="w2_item_1_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Delete</td>
					                        <td><div align="center">
					                          <input name="w2_item_2_checkbox" type="checkbox" id="w2_item_2_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Edit</td>
					                        <td><div align="center">
					                          <input name="w2_item_3_checkbox" type="checkbox" id="w2_item_3_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Next Week</td>
					                        <td><div align="center">
					                          <input name="w2_item_4_checkbox" type="checkbox" id="w2_item_4_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Previous Week</td>
					                        <td><div align="center">
					                          <input name="w2_item_5_checkbox" type="checkbox" id="w2_item_5_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      
					                    </table>
					                     <p align="center"><input type="button" name="buttonww4" id="buttonww4" value="Refresh Calendar" 
					                     onClick="sampleweek(5);"></p></td>
					                  </tr> 
					                  <tr class="textsample">
					                    <td colspan="2" ><div id="calendar_hdr_scx" class="settings_monthview">Schedule view</div></td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td ><b>Schedule - Period Type</b></td>
					                    <td ><select name="select_shced_period" id="select_shced_period">
					                      <option value="0" selected>Day</option>
					                      <option value="1">Week</option>
					                      <option value="2">Month</option>
					                      <option value="3">Two months</option>
					                      <option value="4">Quarter</option>
					                    </select>
					                    <input type="button" name="button7" id="button7" value="change" onClick="samplesched(6);"></td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td ><b>Schedule-</b> <b>Unit Header TYPE</b></td>
					                    <td ><select name="select_sched_units" id="select_sched_units">
					                      <option value="0" selected>Hours</option>
					                      <option value="1">Days</option>
					                      <option value="2">Weeks</option>
					                    </select>
										   <input type="submit" name="buttonweekaction" id="buttonweekaction" value="change" onClick="samplesched(5);">                    </td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td ><b>Schedule- Unit Header click Action</b></td>
					                    <td ><select name="select_sched_actionclick" id="select_sched_actionclick">
					                      <option value="none">none</option>
					                      <option value="gotoview">Go to View</option>
					                      <option value="event">Event</option>
					                    </select>
										   <input type="submit" name="buttonweekaction" id="buttonweekaction" value="change" onClick="samplesched(1);">                    </td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td ><b>Schedule - Use Store Color for List Items BG</b></td>
					                    <td >
					                      <select name="select_sched_colorusebg" id="select_sched_colorusebg">
					                        <option value="0" selected>NO</option>
					                        <option value="1">Yes</option>
					                      </select>
					                      <input type="button" name="button3" id="button3" value="change" onClick="samplesched(2);">                   </td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td ><b>Schedule - List Item Width</b></td>
					                    <td ><input name="numlistItemwidth" type="text" id="numlistItemwidth" value="250" size="6" maxlength="3">
					                    <input type="submit" name="buttonweek" id="buttonweek" value="change" onClick="samplesched(3);"></td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td ><b>Schedule - List Item / Time line - Action</b></td>
					                    <td > <select name="select_schedmouseaction" id="select_schedmouseaction">
					                        <option value="">none</option>
					                        <option value="click" selected>Click</option>
					                        <option value="dblclick">Double Click</option>
					                      </select>
					                      <input type="button" name="button2" id="button2" value="Change" onClick="samplesched(4);"></td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td ><b>Schedule - Unit Header Width</b></td>
					                    <td ><input name="numlistUnitwidth" type="text" id="numlistUnitwidth" value="25" size="6" maxlength="3">
					                    <input type="submit" name="buttonweek" id="buttonweek" value="change" onClick="samplesched(7);"></td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td ><b>Schedule - Time Line class Over</b></td>
					                   <td ><select name="select_sched_clasover_sample" id="select_sched_clasover_sample">
					                       <option value="test_taskovercss_sched" selected>test_taskovercss_sched</option>
					                       <option value="test_taskovercss_sched_b">test_taskovercss_sched_b</option>
					                     </select>
					                       <input type="submit" name="button8" id="button8" value="change" onClick="samplesched(8);"></td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td ><b>Schedule - Display Subject on the Time-Line body</b></td>
					                    <td ><select name="select_sched_usenames" id="select_sched_usenames">
					                        <option value="0" selected>NO</option>
					                        <option value="1">Yes</option>
					                      </select>
					                    <input type="button" name="button3" id="button3" value="change" onClick="samplesched(9);"></td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td valign="top" ><b>Schedule - Menu ITEMS (ListItems)</b></td>
					                    <td ><table width="100%" border="0" cellspacing="0" cellpadding="0">
					                      <tr class="textsample">
					                        <td width="49%"><div align="left">ADD</div></td>
					                        <td width="51%"><div align="center">
					                            <input name="s_item_1_checkbox2" type="checkbox" id="s_item_1_checkbox2" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">EDIT</div></td>
					                        <td><div align="center">
					                            <input name="s_item_2_checkbox2" type="checkbox" id="s_item_2_checkbox2" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">DELETE</div></td>
					                        <td><div align="center">
					                            <input name="s_item_3_checkbox2" type="checkbox" id="s_item_3_checkbox2" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">GO Next Period</div></td>
					                        <td><div align="center">
					                            <input name="s_item_4_checkbox2" type="checkbox" id="s_item_4_checkbox2" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Go Prev period</div></td>
					                        <td><div align="center">
					                            <input name="s_item_5_checkbox2" type="checkbox" id="s_item_5_checkbox2" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td><div align="left">Change to Month View</div></td>
					                        <td><div align="center">
					                            <input name="s_item_6_checkbox2" type="checkbox" id="s_item_6_checkbox2" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Change to Week View</td>
					                        <td><div align="center">
					                            <input name="s_item_7_checkbox2" type="checkbox" id="s_item_7_checkbox2" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>Change to Day view</td>
					                        <td><div align="center">
					                            <input name="s_item_8_checkbox2" type="checkbox" id="s_item_8_checkbox2" checked>
					                        </div></td>
					                      </tr>
					                    </table>
										<p align="center"><input type="button" name="buttonw4" id="buttonw4" value="Refresh Calendar" 
					                    onClick="samplesched(10);"></p>                    </td>
					                  </tr>
					                  <tr class="textsample sxsample">
					                    <td valign="top" ><b>Schedule - Menu ITEMS (Time Lines)</b></td>
					                    <td ><table width="100%" border="0" cellspacing="0" cellpadding="0">
					                      <tr class="textsample">
					                        <td width="49%">ADD</td>
					                        <td width="51%"><div align="center">
					                          <input name="s2_item_1_checkbox" type="checkbox" id="s2_item_1_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>EDIT</td>
					                        <td><div align="center">
					                          <input name="s2_item_2_checkbox" type="checkbox" id="s2_item_2_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      <tr class="textsample">
					                        <td>DELETE</td>
					                        <td><div align="center">
					                          <input name="s2_item_3_checkbox" type="checkbox" id="s2_item_3_checkbox" checked>
					                        </div></td>
					                      </tr>
					                      
					
					                    </table>
					                    <p align="center"><input type="button" name="buttonww4" id="buttonww4" value="Refresh Calendar" 
					                     onClick="samplesched(11);"></p></td>
					                  </tr>
					            </table>
					            </div>
					    	</div>
						<div>	</div>
					</div>
					             --> 
					   	  
				</td>
			</tr>
		</table>
	</form>
</body>
</html>