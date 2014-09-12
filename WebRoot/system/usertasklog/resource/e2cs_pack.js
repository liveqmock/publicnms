
Ext.namespace('e2cs.dateParts');
e2cs.dateParts={ YEAR: 0,  MONTH: 1,  DAY: 2, HOUR: 3,  MINUTE: 4, SECOND: 5,    MILLISECOND: 6,	 QUARTER: 7, 	WEEK: 8, 	WEEKDAY: 9};

Ext.ECalendar = Ext.extend(Ext.Panel,{		   
	id:'e-calendar',
	title:'Calendar', 
	mytitle: '',
	html:null, 
	showCal_tbar:true,
	showRefreshbtn:true,  // ** added on alpha 0.0.2
	refreshAction:'view', // 0.0.11 'view','data' // 20091202 view ----- data
	// currentView  :  'month', 'week' or 'day'
	currentView: 'month',
	currentdate: new Date(), 
	dateSelector: true,   // 20091202 false ----- true
	dateSelectorIcon: '',
	// dateSelectorIconCls: 'selector', removed on alpha 0.0.4 
	dateformat :'Y-m-d',   // 20091202 d-m-Y ----- Y-m-d
	//binding controls 
	//same control type to refresh and keep  in the same frequence
	fieldsRefer:{	//0.0.11  // 20091202
		id:'id',
		userid:'userid',
		username:'username', 
		content:'content',
		date:'date',
		color:'color'
	},
	storeOrderby:'', 		//0.0.11 
	storeOrder:'ASC',		//0.0.11 
//	widgetsBind: { 
//		bindMonth:null,	
//		bindDay:null,
//		binWeek:null
//	},
	//added on 0.0.4 
	//-----------------------------------------------------------
	// 20091202
	//tplTaskTip : new Ext.XTemplate( 
	//	'<tpl for=".">{starxl}{startval}<br>{endxl}{endval}<hr color=\'#003366\' noshade>{details}</tpl>'
    //),
    tplTaskTip : new Ext.XTemplate( 
		'<tpl for=".">{usernamexl}{usernameval}<br>{datexl}{dateval}<hr color=\'#003366\' noshade><div style=\'word-wrap:break-word;word-break:break-all;\'>{content}</div></tpl>'
    ),
    // ----------------------------------------------------------
	tplTaskZoom: new Ext.XTemplate( 
		'<tpl for=".">',
		'<div class="ecal-show-basetasktpl-div">Tarea:{subject}<br>',
		'Inicia:{startdate}<br>Termina:{enddate}<br>Descripcion:<br>{description}<div><hr>',
		'</tpl>'
	),		
	monitorBrowserresize:false,  //0.0.7 beta 0.0.2 
	//Today button config--------------------------
	iconToday:'',
	// month view config--------------------------- 
	mview: null,
	iconMonthView:'',
	// week view config---------------------------- 
	wview: null,
	iconWeekView:'',
	// day view config ----------------------------
	dview: null, 
	iconDayView:'',
	// --------------------------------------------
	sview:null,
	iconSchedView:'',
	store: null, 	//store
	// --------------------------------------------
	pickerStartDay: 0, // 0.0.14  new feature to match according to Week View StartDay 	
	// private use --------------------------------
	viewmonth:null,
	viewday	 :null, 
	viewweek :null,
	viewscheduler:null, //0.0.10 
	viewready:false,  //0.0.7
	loadMask: true,
	customMaskText: '',
	calendarMask:null, // 0.0.12  internal use 
	//functions 
	initComponent:function() {
		this.addEvents(
		   'beforeRefresh',
		   'onRefresh',

		   'beforeChangeView',
		   'onChangeView',
		   
		   'beforeChangeDate',
		   'afterChangeDate',
		   
		   'beforeContextMenuTask', //0.0.14 Added  To provide logic on the contextmenus of the Tasks
		   		   
		   'customMenuAction',
		   'taskAdd',
		   'beforeTaskAdd',
		   'taskDblClick',

		   'beforeTaskMove',
		   'TaskMoved',

		   'beforeTaskDelete',
		   'onTaskDelete',
		   'afterTaskDelete',

		   'beforeTaskEdit',
		   'onTaskEdit',
		   'afterTaskEdit'
		);	
		
		//'taskDeleted', 0.0.4  removed  not use at all with the other events
		//'taskChanged'  0.0.4  removed not yet an implementation 
		if (this.html!=null) { this.html =null; } 
		toolspanel=[]; 
		this.btnrefresh	= {	id:'refresh',tooltip:'Actualizar contenido'	};
		if (this.showRefreshbtn){toolspanel.push(this.btnrefresh); 	} 
		
		this.btn_today ={
			id: this.id + '-btn_settoday', 
			cls: 'x-btn-text-icon', 
			text: e2cs.cal_locale.todayLabel,
			icon: this.iconToday, 
			tooltip: e2cs.cal_locale.todayToolTip
		};
		
		this.btn_monthviewchange = { 
			id: this.id + '-btn_monthview', 
			cls: 'x-btn-icon',	
			text: '', 
			icon: this.iconMonthView, 
			tooltip: e2cs.cal_locale.tooltipMonthView 
		};
		
		this.btn_weekviewchange =  { 
			id: this.id + '-btn_weekview',	 
			cls: 'x-btn-icon',	
			text: '', 
			icon: this.iconWeekView,	
			tooltip: e2cs.cal_locale.tooltipWeekView  
		};
		
		this.btn_dayviewchange =   { 
			id: this.id + '-btn_dayview',	 
			cls: 'x-btn-icon',	
			text: '', 
			icon: this.iconDayView, 	
			tooltip: e2cs.cal_locale.tooltipDayView   
		};		
		this.btn_schedviewchange = {
			id: this.id + '-btn_sched_view',	 
			cls: 'x-btn-icon',	
			text: '', 
			icon: this.iconSchedView, 	
			tooltip: e2cs.cal_locale.tooltipSchedView   
		};
		
		this.btn_listviewchange = { 
			id: this.id + '-btn_listview', 
			cls: 'x-btn-icon',	
			text: '', 
			icon: this.iconListView, 
			tooltip: e2cs.cal_locale.tooltipListView 
		};
		if (this.showCal_tbar){ 
			this.tbar_calendar= new Ext.Toolbar({	
				id:  this.id +'-cmscalendartoolbar', 
				autoWidth: true, 
				autoHeight:false,	
				//items:[ this.btn_today ,'-',this.btn_monthviewchange,this.btn_weekviewchange,this.btn_dayviewchange,this.btn_schedviewchange,this.btn_listviewchange,'-']  
				items:[ this.btn_today ,'-',this.btn_monthviewchange,this.btn_listviewchange,'-']  
			});	
		} else { 
			this.tbar_calendar=null; 
		} 
		this.selector_dateMenu  = new Ext.menu.DateMenu({ defaultAlign:'tr-br',subMenuAlign :''});
		if ( this.width){  var_autoWidth = false; } else { 	var_autoWidth = true;	} 
		if ( this.height){ var_autoHeight = false;} else {  var_autoHeight = true;	} 
		Ext.apply(this,{
			header: this.header,
			headerAsText: true, 
			title: this.title + this.mytitle,
			border:true,
			width:   this.width, 		//common width for all views 
			height:  this.height,  		//used on week view and day view
			monitorResize:true, 
			autoShow   :true, 
			autoWidth  :var_autoWidth,		    // default :( 
			autoHeight :var_autoHeight,			// used on month view
			autoScroll :false,  		// used on dayview
			html:this.html,    			// avoid other data displayed 
			tools: toolspanel,
			tbar:this.tbar_calendar,
			loadMask: this.loadMask
        });
		Ext.ECalendar.superclass.initComponent.call(this); 
		if (this.mview){ 
			this.viewmonth = this.getViewMonth(); 
			this.viewmonth.init(this,this.currentdate);		
		}
		if (this.dview ){ 
			//this.viewday = this.getViewDay();
			//this.viewday.init(this,this.currentdate);		
		} 
		if (this.wview){
			//this.viewweek = this.getViewWeek();
			//this.viewweek.init(this,this.currentdate);		
		} 
		if (this.sview){
			//this.viewscheduler = this.getViewShedule();
			//this.viewscheduler.init(this,this.currentdate);		
		} 
	}, 
	// end of function initComponent
	// Override other inherited methods 
	onResize: function(){
		Ext.ECalendar.superclass.onResize.apply(this, arguments);
		this.doLayout();
		if (this.viewready){  //0.0.7
			//if (this.currentView=='month'){     this.viewmonth.render(); 	} 
			//if (this.currentView=='week') {     this.viewweek.render();  	} 
			//if (this.currentView=='day')  {	    this.viewday.render(); 		} 
			//if (this.currentView=='schedule'){  this.viewscheduler.render();}	 //0.0.12
		} 
	}, 
    onRender: function(){
        Ext.ECalendar.superclass.onRender.apply(this,arguments);	
    },
	afterRender: function(){
		Ext.ECalendar.superclass.afterRender.call(this);
		if (this.loadMask){ 
			if (this.store==null && this.store==undefined){
			} else { 
				if (this.customMaskText==''){
					var TexttoshowonProgress =e2cs.cal_locale.loadmaskText; 
				} else { 
					var TexttoshowonProgress =this.customMaskText; 
				} 
				this.calendarMask = new Ext.LoadMask(Ext.get(this.id),{msg:TexttoshowonProgress});
//				this.store.on({'beforeload':{
//							fn:function(){ 
//								testtmp.show(); 
//							},scope:this
//						},
//						'load':{
//							fn:function(success,dataxx,purebax){
//								testtmp.hide();
//							},scope:this
//						} 
//				});	
			}	
		} 
		//0.0.11 Sort the order of the store if this.storeOrderby is specified 
		if (this.storeOrderby==''){ } else { this.store.sort(this.storeOrderby, this.storeOrder); 	} 		
		if (this.showCal_tbar){
			var btntoday = this.topToolbar.items.items[0]; 
			btntoday.setHandler( this.setCurrentDate, this );
			var btnmonth =  this.topToolbar.items.items[2]; 
			//var btnweek  =  this.topToolbar.items.items[3];
			//var btnday   =  this.topToolbar.items.items[4];
			//var btnsched =  this.topToolbar.items.items[5]; // 0.0.10 
			var btnlist = this.topToolbar.items.items[3];
			
			if (!this.mview){ btnmonth.setVisible(false);  } else { btnmonth.setVisible(true);	}
			//if (!this.dview){ btnday.setVisible(false);    } else { btnday.setVisible(true);	}  //20091203 note
			//if (!this.wview){ btnweek.setVisible(false);   } else { btnweek.setVisible(true);	}  //20091203 note
			//if (!this.sview){ btnsched.setVisible(false);  } else { btnsched.setVisible(true);	} //0.0.10 //20091203 note
			
			btnmonth.addListener('click',  function(){   this.changeView('month'); 		}  , this); 
			btnlist.addListener('click',   function(){   this.changeView('List');  		}  , this); 
			//btnday.addListener('click',    function(){   this.changeView('day');   		}  , this); 
			//btnweek.addListener('click',   function(){   this.changeView('week');  		}  , this); 
			//btnsched.addListener('click',  function(){   this.changeView('schedule');  	}  , this); 
			
		}
		if (this.header && this.tools.refresh){ // 0.0.10 Bug fix Thanks to Remy 
			if 	(this.refreshAction=='view'){   // 0.0.11    'view', 'data'
					this.tools.refresh.addListener('click', this.refreshCalendarView, this );
			} else { 
					this.tools.refresh.addListener('click', function(){this.store.reload();}, this );
			} 
		} 
		if (this.dateSelector && this.showCal_tbar){
			this.selector_dateMenu.picker.startDay  = this.pickerStartDay;
			this.selector_dateMenu.picker.todayText = e2cs.cal_locale.todayLabel;
			this.selector_dateMenu.picker.todayTip  = e2cs.cal_locale.todayToolTip;
			this.selector_dateMenu.picker.monthNames= e2cs.cal_locale.monthtitles;
			this.selector_dateMenu.picker.dayNames  = e2cs.cal_locale.daytitles;
			this.selector_dateMenu.picker.setValue(this.currentdate);
			this.selector_dateMenu.addListener('select', this.selectdatefromSelector , this); 
			//this.tbar_calendar.addFill() ;
			this.btn_selector  = {
					id: this.id + '-btn_dateselector',		
					cls: 'x-btn-text-icon',	
					text: e2cs.cal_locale.dateSelectorText,
					icon: this.dateSelectorIcon,
					tooltip: e2cs.cal_locale.dateSelectorTooltip,
					menu: this.selector_dateMenu
			};	
			this.tbar_calendar.addButton(this.btn_selector);
		} 
		if (this.ownerCt==undefined){
			if (this.currentView=='month'){  	this.viewmonth.render(); 	} 
			//if (this.currentView=='week') {  	this.viewweek.render(); 	} //20091203 note
			//if (this.currentView=='day')  {  	this.viewday.render(); 		} //20091203 note
			//if (this.currentView=='schedule'){  this.viewscheduler.render();}	 //0.0.10//20091203 note
		} else { 
			if (this.currentView=='month'){  	this.viewmonth.render(); 	} 
			//if (this.currentView=='week') {  	this.viewweek.render(); 	} //20091203 note
			//if (this.currentView=='day')  {  	this.viewday.render(); 		} //20091203 note
			//if (this.currentView=='schedule'){  this.viewscheduler.render();}	 //0.0.10  //20091203 note
		} 
		this.doLayout();
		this.viewready = true; //0.0.7 
		tmpobj = this;
		if (this.monitorBrowserResize){  //0.0.7 
			Ext.EventManager.onWindowResize( function(){ //console.log('change...inside calendar');
				tmpobj.refreshCalendarView(); 
			});
		} 
	},
	refreshCalendarView: function(btn){
		if (this.rendered){ 
			//0.0.11 Sort the order of the store if this.storeOrderby is specified 
			if (this.storeOrderby==''){ } else { this.store.sort(this.storeOrderby, this.storeOrder); 	} 	
			if (this.currentView=='month'){  	this.viewmonth.render(); 		} 
			//if (this.currentView=='week') {  	this.viewweek.render(); 		}  //20091203 note
			//if (this.currentView=='day')  {	 	this.viewday.render(); 			}  //20091203 note
			//if (this.currentView=='schedule'){  this.viewscheduler.render(); 	}	 //0.0.10    //20091203 note
			this.doLayout();
			this.fireEvent("onRefresh",this);
		}	
	}, 
	setNewDate:function(newdate){ 
		if ( this.fireEvent("beforeChangeDate", newdate , this)==false ) { return false; } 
		//0.0.11 Sort the order of the store if this.storeOrderby is specified 
		if (this.storeOrderby==''){ } else { this.store.sort(this.storeOrderby, this.storeOrder); 	} 
		this.currentdate = new Date(newdate); 	
		this.selector_dateMenu.picker.setValue(this.currentdate);
		if (this.currentView=='month'){ 	this.viewmonth.render(); 	} 
		if (this.currentView=='week') { 	this.viewweek.render(); 	} 
		if (this.currentView=='day')  {	 	this.viewday.render(); 	}  
		if (this.currentView =='schedule'){ this.viewscheduler.render(); 	} //0.0.10   
		this.doLayout();
		this.fireEvent("afterChangeDate", this.currentdate , this);
	} , 
	setCurrentDate: function(){ 
		this.setNewDate(Date());
	},
	changeView: function(datastr, opdate){
		newView  = datastr;
		oldView  = this.currentView; 
		if ( this.fireEvent("beforeChangeView", newView , this.currentView, this)==false ) { return false; } 
		//if (oldView=='schedule' && newView !='schedule'){ }//0.0.10  not implemented yet //remove period selector if any 
		if(datastr=='month'){//0.0.12 FIX 	
			if (this.viewmonth==null || this.viewmonth==undefined){ return false; 	
			} else { this.currentView='month'; this.viewmonth.render();
			}
		} else if (datastr=='week'){//0.0.12 FIX 
			if (this.viewweek==null || this.viewweek==undefined){ return false; 	
			} else {  this.currentView='week'; this.viewweek.render(); 	
			} 
		} else if (datastr=='day') {//0.0.12 FIX 
			if (this.viewday==null || this.viewday==undefined){ return false; 	
			} else {   this.currentView='day';  this.viewday.render(); 	
			} 
		} else if (datastr =='schedule'){ //0.0.12 FIX 
			if (this.viewscheduler==null || this.viewscheduler==undefined){ return false; 	
			} else { this.currentView='schedule';  this.viewscheduler.render(); 
			} 
		} else {
			return false; 
		}
		this.fireEvent("onChangeView", newView, oldView, this); 
	}, 
	//--------------------------------------------------------------
	//private ------------------------------------------------------
	//--------------------------------------------------------------	
	selectdatefromSelector: function(dp, dateval){ 
		this.setNewDate(dateval);
	},
	getCurrentDate: function(){ //0.0.11 
		return this.currentdate; 
	},	
	getViewMonth:function(){  
		if(!this.viewmonth){ this.viewmonth = new Ext.ECalendar.monthview(this.mview); }
        return this.viewmonth;
	}, 
	getViewDay: function(){
		if(!this.viewday){   this.viewday = new Ext.ECalendar.dayview(this.dview); }
        return this.viewday;
	},
	getViewWeek: function(){
		if(!this.viewweek){  this.viewweek = new Ext.ECalendar.weekview(this.wview);}
        return this.viewweek;
	},	
	getViewShedule: function(){ //0.0.10 new View (O.O) 
		if(!this.viewscheduler){  this.viewscheduler = new Ext.ECalendar.scheduleview(this.sview);}
        return this.viewscheduler;
	}, 
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	//------------------------------------------------------------------
	// Util functions  
	// -----------------------------------------------------------------
	//goNextMonth: function(){   alert("Mes siguiente"); 	}, removed for alpha 0.0.4
	//goPrevMonth: function(){   alert("Mes Anterior"); 	}, removed for alpha 0.0.4
	getCalendarMonth:	function(){			return ( this.currentdate.getMonth() + 1); 		}, 
	getCalendarYear: 	function(){ 		return ( this.currentdate.getFullYear() ) ;  	}, 
	getCalendarDay:		function(){  		return ( this.currentdate.getUTCDate() ) ; 		},
	getCalendarWeekDay: function(dataformat){ 
		if (dataformat=='str') { 
			return  Date.dayNames[this.currentdate.getDay()]; 
		} else { 
			return ( this.currentdate.getDay() ) ; 
		} 
	},
	//-------------------------------------------------------------------------------
	//getDateRangeOfWeek - Utility Date tool  new //0.0.10   also this give us the hope of week view to set the view with ChangebyWeek(weekno)  
	//-------------------------------------------------------------------------------
	getDateRangeOfWeek: function(weekNo){
		var d1 = new Date();
		numOfdaysPastSinceLastMonday = eval(d1.getDay()- 1);
		d1.setDate(d1.getDate() - numOfdaysPastSinceLastMonday);
		var weekNoToday = d1.getWeekOfYear();
		var weeksInTheFuture = eval( weekNo - weekNoToday );
		d1.setDate(d1.getDate() + eval( 7 * weeksInTheFuture ));
		var rangeIsFrom = new Date(    (d1.getMonth()+1) +"/" + d1.getDate() + "/" + d1.getFullYear() );
		d1.setDate(d1.getDate() + 6);
		var rangeIsTo = new Date (     (d1.getMonth()+1) +"/" + d1.getDate() + "/" + d1.getFullYear() );
		return [rangeIsFrom , rangeIsTo] ;
	},
	//-------------------------------------------------------------------------------
	// Dojo Date function -  altered  only the part of (fleegix.date.util.dateParts)
	//-------------------------------------------------------------------------------
	dateDiff: function (date1, date2, interv) {
	// date1  	// Date object or Number equivalent
	// date2	// Date object or Number equivalent
	// interval	// A constant representing the interval, e.g. YEAR, MONTH, DAY. See fleegix.date.util.dateParts.
	// Accept timestamp input
	  if (typeof date1 == 'number') { date1 = new Date(date1); }
	  if (typeof date2 == 'number') { date2 = new Date(date2); }
	  if  (date1.format('m/d/Y H:i:s') == date2.format('m/d/Y H:i:s') ) { return 0;  } 
	  var yeaDiff = date2.getFullYear() - date1.getFullYear();
	  var monDiff = (date2.getMonth() - date1.getMonth()) + (yeaDiff * 12);
	  var msDiff = date2.getTime() - date1.getTime(); // Millisecs
	  var secDiff = msDiff/1000;
	  var minDiff = secDiff/60;
	  var houDiff = minDiff/60;
	  var dayDiff = houDiff/24;
	  var weeDiff = dayDiff/7;
	  var delta = 0; // Integer return value
	  with (e2cs.dateParts) {
		switch (interv) {
		  case YEAR:
			delta = yeaDiff;
			break;
		  case QUARTER:
			var m1 = date1.getMonth();
			var m2 = date2.getMonth();
			// Figure out which quarter the months are in
			var q1 = Math.floor(m1/3) + 1;
			var q2 = Math.floor(m2/3) + 1;
			// Add quarters for any year difference between the dates
			q2 += (yeaDiff * 4);
			delta = q2 - q1;
			break;
		  case MONTH:
			delta = monDiff;
			break;
		  case WEEK:
			// Truncate instead of rounding
			// Don't use Math.floor -- value may be negative
			delta = parseInt(weeDiff);
			break;
		  case DAY:
			delta = dayDiff;
			break;
		  case WEEKDAY:
			var days = Math.round(dayDiff);
			var weeks = parseInt(days/7);
			var mod = days % 7;
	 
			// Even number of weeks
			if(mod == 0){
			  days = weeks*5;
			}else{
			  // Weeks plus spare change (< 7 days)
			  var adj = 0;
			  var aDay = date1.getDay();
			  var bDay = date2.getDay();
			  weeks = parseInt(days/7);
			  mod = days % 7;
			  // Mark the date advanced by the number of
			  // round weeks (may be zero)
			  var dtMark = new Date(date1);
			  dtMark.setDate(dtMark.getDate()+(weeks*7));
			  var dayMark = dtMark.getDay();
			  // Spare change days -- 6 or less
			  if(dayDiff > 0){
				switch(true){
				  // Range starts on Sat
				  case aDay == 6:
					adj = -1;
					break;
				  // Range starts on Sun
				  case aDay == 0:
					adj = 0;
					break;
				  // Range ends on Sat
				  case bDay == 6:
					adj = -1;
					break;
				  // Range ends on Sun
				  case bDay == 0:
					adj = -2;
					break;
				  // Range contains weekend
				  case (dayMark + mod) > 5:
					adj = -2;
					break;
				  default:
					// Do nothing
					break;
				}
			  }else if(dayDiff < 0){
				switch (true) {
				  // Range starts on Sat
				  case aDay == 6:
					adj = 0;
					break;
				  // Range starts on Sun
				  case aDay == 0:
					adj = 1;
					break;
				  // Range ends on Sat
				  case bDay == 6:
					adj = 2;
					break;
				  // Range ends on Sun
				  case bDay == 0:
					adj = 1;
					break;
				  // Range contains weekend
				  case (dayMark + mod) < 0:
					adj = 2;
					break;
				  default:// Do nothing
					break;
				}
			  }
			  days += adj;
			  days -= (weeks*2);
			}
			delta = days;
	 
			break;
		  case HOUR:
			delta = houDiff;
			break;
		  case MINUTE:
			delta = minDiff;
			break;
		  case SECOND:
			delta = secDiff;
	
			break;
		  case MILLISECOND:
			delta = msDiff;
			break;
		  default:// Do nothing
			break;
		}
	  }
	  return Math.round(delta); // Number (integer) // Round for fractional values and DST leaps
	}
});
// ** added on alpha 0.0.2
Ext.reg('e2cs_calendar',Ext.ECalendar); 
// --------------------------------------------------------------------------------------------------------------------------
// Ext.ECalendar end --------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------------

// ----------------------------------------------------------------------------------------------------------------------------------------------------------------
// Ext.ECalendar.monthview Start
// ----------------------------------------------------------------------------------------------------------------------------------------------------------------
Ext.ECalendar.monthview = function(config){
	Ext.apply(this,config);
	this.addEvents(	'dayClick',	'beforeMonthChange',	'afterMonthChange'	);	
	Ext.ECalendar.monthview.superclass.constructor.call(this);
};
Ext.extend(Ext.ECalendar.monthview, Ext.util.Observable, {
	referid:'monthview', //0.0.9
	header:true,
	headerFormat:'Y-M',  //  20091203  'M-Y'  ----------------'Y-M'
	headerButtons: true,
	moreMenuItems:[],  
	// useFit:false,// not used for the moment 
	// ------------------------------------------------------
	// dayAction:	// 'viewday' (set the view in the calendar for that day)   // 'event'	 (raise Event dayClick)  // 'window'	 (shows a window with a list of day's tasks if theres any...) //	none or null   (do nothing )
	dayAction:'viewday',
	// tasks properties for month view  ---------------------
	showTaskcount: false, 
	showTaskList: true, //0.0.9 ----------------------------
	showNumTasks: 5,
	isShowNumTasks:true, 	  // 20091203 add : false to ----- Not limited to the number of tasks(default : true)
	parse_format:'Y-m-d',     // 20091203 add : store's date parse format default: 'Y-m-d'
	task_format:'Y-m-d',// 20091203 'd-m-Y H:i:s a' -----------'Y-m-d'
//	0.0.14 	 deprecated TaskList_tplqTip because for consistency use in all views the calendar object property tplTaskTip
//	TaskList_tplqTip: new Ext.XTemplate( 
//		'<tpl for=".">{starxl}{startval}<br>{endxl}{endval}<hr color=\'#003366\' noshade>{details}</tpl>'
//	),	
	TaskList_launchEventOn:'click', //0.0.11 
	contextMenuLabels: e2cs.cal_locale.contextMenuLabelsDay,
	ShowMenuItems:[1,1,1,1,1,1],    //0.0.11  ADD, nextmonth, prevmonth, chg Week , CHG Day, chg Sched,
	TaskList_ShowMenuItems:[1,1,1], //0.0.11  ADD, delete, edit
	TaskList_moreMenuItems:[],		// Cutom Menu items to the contextmenu of the task 
	taskStyle:'margin-top: 10px;',  //0.0.9
	startDay:0, 					//0.0.8 added on similar to date picker and starts ont he selected day 
	thisview:null,
	init: function(calendar,dateval){;
		this.calx = calendar; this.datetohandle = dateval; 		
	},
	render: function (){ 
		if (this.calx.loadMask){
			//var myMask = new Ext.LoadMask( this.calx.id , {removeMask:true,msg:e2cs.cal_locale.loadmaskText});
			var myMask = new Ext.LoadMask( this.calx.id,{removeMask:true,msg:this.calx.customMaskText});			
			myMask.show();
		} 
		//0.0.9 small verification on showTaskcount vs showTaskList priority its always to showTaskcount and doesnt interfer on both sides 
		this.showTaskcount==true?this.showTaskList=false:this.showTaskList=this.showTaskList; 
		// header section 
		var dt= new Date(this.calx.currentdate); 
		Date.monthNames = e2cs.cal_locale.monthtitles; 
		Date.dayNames = e2cs.cal_locale.daytitles; 		
		if (this.header){	
			var tmpheader = this.genHeader(this.datetohandle); 	
		}
		var updateview = Ext.get(this.calx.body); 
		//0.0.12 Small test for removing child nodes 
		var testrender= updateview.dom.childNodes.length; 
		if (testrender){ 
			for (var i=testrender;i<testrender;i++){ 
				updateview.dom.removeChild(updateview.dom.childNodes[0]);
			} 
		} 		
		updateview.update(''); 
		var monthbase =   '<div id="' + this.calx.id + '-main-calendar-header"></div>'; 
		    monthbase +=  '<div id="' + this.calx.id + '-main-calendar-month-body"></div>';	
		updateview.update(monthbase);
		if (this.header){
			var tmpheader = Ext.get(this.calx.id + '-main-calendar-header');
			var prueba2 = tmpheader; 
			var myheaderwrap  = prueba2.wrap ({ tag:'div', cls: 'x-calendar-month-header',	html:''	}); 
			if (this.headerButtons){
				var prevclick = myheaderwrap.createChild({id:this.calx.id + '-btn-pm', tag: 'div', cls: 'x-calendar-month-previous', html:'' }); //0.0.6 added ID	// 0.0.10 Bug fix Thanks to PTG 
				var nextclick = myheaderwrap.createChild({id:this.calx.id + '-btn-nm', tag: 'div', cls: 'x-calendar-month-next',	 html:''	}); //0.0.6 added ID // 0.0.10 Bug fix Thanks to PTG 
				prevclick.dom['qtip']= e2cs.cal_locale.headerTooltipsMonth.prev;
				prevclick.addListener('click', this.onclickprev_month, this);
				prevclick.addClassOnOver('x-calendar-month-previous-over');
				nextclick.dom['qtip']= e2cs.cal_locale.headerTooltipsMonth.next;
				nextclick.addListener('click', this.onclicknext_month, this);
				nextclick.addClassOnOver('x-calendar-month-next-over');	
			} 
			var headerx =   myheaderwrap.createChild({  tag: 'div',	 id: 'header',  html:'' + dt.format(this.headerFormat) + ''	});
		} 
		// creates thebody of the month --------------------------------------------
		var tmpdays = Ext.get(this.calx.id + '-main-calendar-month-body');
		var day_hdrtext = this.genDaysHeader(); 
		var days_text   = this.genBody(dt,{styletasks:this.taskStyle});  
		var myheaderdayswrap = tmpdays.wrap({tag:'div',	cls:'x-calendar-month-days',	html:''}); 	
		var mydays = myheaderdayswrap.createChild({ 
			tag:'div',  id: this.calx.id + '-calendar-view-month', cls:'header-days' , 
			html:' <table id="' + this.calx.id + '-month_skel" width="100%" border="0" cellspacing="0" cellpadding="0"><tr class="skel_hdrdays" id="' + this.calx.id + '-skel_hdrdays">' + day_hdrtext + '</tr>' +  days_text + '</table>'
		}); 
		Ext.get(this.calx.id + '-skel_hdrdays').setHeight(17);
		if (Ext.isIE){ 
			var numrowsbodyskel = mydays.dom.childNodes[0].childNodes[0].rows.length-1 ; 
		} else { 
			var numrowsbodyskel = mydays.dom.childNodes[1].childNodes[0].rows.length-1 ; 
		} 	
		if (this.calx.ownerCt!=undefined){ //0.0.6 fix for ext.component containers such as tab and others and properly draw correctly
			if (this.calx.ownerCt.ctype && this.calx.ownerCt.ctype=="Ext.Component"){ //this.calx.height =  this.calx.ownerCt.height;  
				this.calx.height =  this.calx.ownerCt.getInnerHeight(); //0.0.7  - beta 0.0.2 
			} 
		}
		if (!this.calx.height || this.calx.height=='undefined'){ 
			if (this.calx.getEl().dom.offsetParent!=null) { 
				var tmpheight = this.calx.getEl().dom.offsetParent.clientHeight; //+ this.calx.getFrameHeight() ; //0.0.13 FIX for some containers 
			} else { 
				var tmpheight = 0; 
			} 
			//var tmpheight = this.calx.getEl().dom.offsetParent.clientHeight ; // + this.calx.getFrameHeight() ; //0.0.6
			if (tmpheight ==0) { tmpheight  = 300; }  // 0.0.13 Fix for some containers  or layouts  this is a temporal  height
			if (this.header){tmpheight+=-27;}
			if (this.calx.showCal_tbar){ tmpheight+=-27;  } 	
			if (this.calx.header){tmpheight+=-22;} 
			tmpheight+=-16;
			var morehoffst = 0; 
			//if (this.calx.showCal_tbar){ var morehoffst=76;  } else { var morehoffst=24; }
			Ext.get( this.calx.id + '-month_skel').setStyle({height:'' +  tmpheight-morehoffst + 'px' } );
			//valtosethtmp = Math.round(  (tmpheight-morehoffst-( Ext.get(this.calx.id + '-skel_hdrdays').getHeight(false) + (-1) ) )/numrowsbodyskel ) ; 			
			valtosethtmp = Math.round(  (tmpheight) / numrowsbodyskel ) ; 			
			valtosethtmp+=-3;
		} else{
			var tmpheight=0; 
			if (this.header){tmpheight+=-27;}
			if (this.calx.showCal_tbar){ tmpheight+=-27;  } 	
			if (this.calx.header){tmpheight+=-22;} 
			tmpheight+=-16;
			tmpheight+=this.calx.height; //-myheaderwrap.getHeight(true);
			if (this.calx.showCal_tbar){ var morehoffst=77;  } else { var morehoffst=24; }
			valtosethtmp = Math.round((tmpheight)/numrowsbodyskel ) ; 
			valtosethtmp+=-3;
		} 
		Ext.get(this.calx.id + '-month_skel').setHeight(tmpheight);		
		newsize  = tmpheight ; 
		valtosethtmp = Math.round( newsize/numrowsbodyskel ); 
		var mydays = Ext.select('td.daybody',true); 
		mydays.each(function(el, thisobj, index){
					var xtest = el; 
					if  (xtest.id == undefined || xtest==null){ var testdx = el.dom.id; } else { var testdx = el.id; } 					
					if ( testdx.indexOf('m-td-' + this.calx.id + '-')>=0){ 	//0.0.6 fix only those td's on the calendar (month view) 					 
							var myobjtowork = Ext.get(testdx);
							myobjtowork.setStyle({height:'' +  valtosethtmp + 'px' } );
							myobjtowork.addClassOnOver('daybody-over');						
							if (Ext.isOpera){ 
									myobjtowork.addListener('mousedown',this.operabuttons,this); 
							} else { 
									myobjtowork.addListener('click', this.onhandler_day, this);
									myobjtowork.addListener('contextmenu', 
										this.oncontextmenu, 
										this,{
										   stopPropagation:true,
										   normalized :true,
										   preventDefault:true
									});
							} 
					} 
				 },
			 this
		);	
		//0.0.9
		if (this.showTaskcount==false && this.showTaskList==true ) { //it has task lists on each day if applied 
			// fix the height of tasks_list elements if needed 
			var mydivlists = Ext.select('div.tasks_list',true);
			mydivlists.each(function(el,thisobj,index){
					if  ((el.id.indexOf(this.calx.id) + 1)>0){  // 0.0.10 bug reported by frank_ash
						element_id = el.id; 
						//testtdobjref = element_id.replace(/test_calendar-tasklist-/,'');  bug 
						testtdobjref = element_id.replace(this.calx.id + "-tasklist-",''); 
						objtd = Ext.get('m-td-' + this.calx.id + '-' + testtdobjref); 
						objtdtd ={top:objtd.getTop(), heigth:objtd.getHeight()}; 
						tmprefdata = 0; 
						tmprefdata = Ext.get(element_id).getTop()  - objtdtd.top ; 
						tmprefdata = tmprefdata+4 ; 
						var newHxx = 0; 
						newHxx =objtdtd.heigth - tmprefdata ; 
						Ext.get(element_id).setHeight(newHxx);
					} 
				 },this
			); 
			//assign  event handlers for each task-list-item 			
			var mylist_items = Ext.select('div.tasks_list_item',true);
			mylist_items.each(function(el,thisobj,index){
					if  ((el.id.indexOf(this.calx.id) + 1)>0){  // 0.0.10 bug reported by frank_ash
						test=11; 
						el.addClassOnOver('task_list_item_over');
						if (this.TaskList_launchEventOn!=''){  //0.0.11 if property not set then  no event attached to element 
							el.addListener(this.TaskList_launchEventOn,this.onDblClick_tasklistitem,this);
						} 
						if (Ext.isOpera){ 
								el.addListener('mousedown',this.operataskitembuttons,this); 
						} else { 
								el.addListener('contextmenu',this.oncontextmenuTaskitem, this, {stopPropagation:true,normalized :true,preventDefault:true	} );
						}
					} 
				 },this
			); 
		}
		if (this.calx.loadMask){
			myMask.hide();
		} 
	},
	// public function on month view 
	refreshView: function(){ this.render(); },
	ZoomDay: function (DateToZoom){ // Creates a window with a custom TPL for day view
		var dtx = new Date(DateToZoom); 
		var counttasks= this.calx.store.getCount();
		if ( counttasks>0 ){ 
			var count_in_day=0; taskstmp=[];
			for (var itask=0; itask<counttasks ; itask++){
				var testrec = this.calx.store.getAt(itask).data; 
				dateinit 	= this.calx.store.getAt(itask).data[this.calx.fieldsRefer.startdate];  //0.0.11dynamic fields
				dateend 	= this.calx.store.getAt(itask).data[this.calx.fieldsRefer.enddate];    //0.0.11dynamic fields
				checkdates 	= dtx.between( new Date(dateinit), new Date(dateend) ); 
				chkformat 	= dtx.format('m/d/Y'); 
				test = new Date(dateinit); if (test.format('m/d/Y')==chkformat){  checkdates =true; } 
				test = new Date(dateend);  if (test.format('m/d/Y')==chkformat){  checkdates =true; } 
				if (checkdates){taskstmp[count_in_day] = testrec; 	count_in_day+=1; } 
			} 
			if (count_in_day<=0){ return false;	} 
		} else { 
			return false; 
		}	
		showdata =[]; 
		for (var i=0;i<taskstmp.length;i++){ //0.0.11 dynamic fields 
			showdata[i]= [ 
					taskstmp[i][this.calx.fieldsRefer.id], 
					taskstmp[i][this.calx.fieldsRefer.subject],
					taskstmp[i][this.calx.fieldsRefer.description], 
					taskstmp[i][this.calx.fieldsRefer.startdate],	  
					taskstmp[i][this.calx.fieldsRefer.enddate],  
					taskstmp[i][this.calx.fieldsRefer.color] 
			]; 
		} 
		var reader = new Ext.data.ArrayReader({}, [
		   {name: this.calx.fieldsRefer.id 			, type: 'int'},     
		   {name: this.calx.fieldsRefer.subject 	, type: 'string'},  
		   {name: this.calx.fieldsRefer.description	, type: 'string'},
		   {name: this.calx.fieldsRefer.startdate	, type: 'string'},  
		   {name: this.calx.fieldsRefer.enddate		, type: 'string'},  
		   {name: this.calx.fieldsRefer.color		, type: 'string'}
		]);
		tmpstore= new Ext.data.Store({reader: reader, data:showdata});
		tmppanel = new Ext.Panel({
			id:'ecal-more-task-panel',header:false, autoDestroy:true,autoScroll:true,monitorResize:true, border:false,autoWidth:false, autoHeight:false,
			items: new Ext.DataView({
				loadingText: e2cs.cal_locale.win_tasks_loading, store:tmpstore, tpl: this.calx.tplTaskZoom,	autoWidth :true, autoHeight:true,
				overClass:'',	itemSelector:'', emptyText: e2cs.cal_locale.win_tasks_empty
			})
		});
		var ecalwinshowmore = new Ext.Window({
			id: 'ecal-win-moretasks', name: 'ecal-win-moretasks', title: e2cs.cal_locale.win_month_zoomlabel +  ' ' + dtx.format(e2cs.cal_locale.win_tasks_format),
			width:450, 	height:300, closeAction:'close', resizable:true,resizeHandles:'all', hideBorders:true, maximizable:true, plain:true,
			modal:true, layout:'fit',iconCls:'x-calendar-more-tasks-win',items:[tmppanel] 											 
		});
		ecalwinshowmore.show(); 
		return true; 
	},
	// ---------------------------------------------------------------------------------
	// private functions  --------------------------------------------------------------
	// ---------------------------------------------------------------------------------
	operabuttons: function (evx,elx,obx){//alert ("boton:" + evx.button); 
		if (Ext.isOpera){ 
			if (evx.button==0){ this.onhandler_day(evx,elx,obx); }  
			if (evx.button==2){ this.oncontextmenu(evx,elx,obx); }  
		}
	}, 
	oncontextmenu: function (evx,elx,obx){//alert (evx.browserEvent.type); 
		if (Ext.isOpera){ if (evx.button!=2){ return false; } }
		if (elx.className=="tasks_list_item" || elx.className=="tasks_list_item task_list_item_over" || elx.className.indexOf("task_list_item_over")>0 ){ return false;} 
		//0.0.11 Avoid to show menu cause no permission is set and also no custom Menus also 
		if (this.ShowMenuItems[0]!=true && this.ShowMenuItems[1]!=true && this.ShowMenuItems[2]!=true && this.ShowMenuItems[3]!=true && this.ShowMenuItems[4]!=true && this.ShowMenuItems[5]!=true && this.moreMenuItems.length<=0){return false;}
		evx.stopEvent();
		if (elx.className=='noday' || elx.className=='today' || elx.className=='monthday') { 
			//var refdate = new Date(elx.id);// 0.0.6 fix 
			var refdate = Date.parseDate(elx.id.substring(this.calx.id.length + 1),this.parse_format );
		} else if (elx.className=='tasks' ){ 
			//var refdate = new Date(elx.parentNode.firstChild.id); // 0.0.6
			var refdate = Date.parseDate(elx.parentNode.firstChild.id.substring(this.calx.id.length + 1) ,this.parse_format); 
		} else if (elx.id.indexOf('-tasklist-') >0 ) { 
			tmpdata =  elx.id.replace(this.calx.id + "-tasklist-",'');
			var refdate =  Date.parseDate(tmpdata + "" ,this.parse_format); 			  
		} else { 
			//var refdate = new Date(elx.firstChild.id); //0.0.6
			var refdate = Date.parseDate( elx.firstChild.id.substring(this.calx.id.length + 1),this.parse_format ); 
		}			
		if (this.menu){ 
			this.menu.removeAll() ;
		}
		this.menu = new Ext.menu.Menu({
			id: this.calx.id + '-contextmenu-month', //0.0.6 modified ID 
			shadow: true, 
			items:[{
				id: this.calx.id + '-month_ctxbtn_task', //0.0.6 modified ID
				iconCls:'x-calendar-month-btnmv_task',
				text: e2cs.cal_locale.contextMenuLabelsMonth.task + ' (' + refdate.format(this.calx.dateformat) + ')',scope:this
			},'-',{  // added on alpha 0.0.4
				id: this.calx.id + '-month_ctxbtn_nextmonth',  //0.0.6 modified ID
				iconCls: 'x-calendar-month-btnmv_nextmth',	 text: e2cs.cal_locale.contextMenuLabelsMonth.gonextmonth,scope:this
			},{ // added on alpha 0.0.4
				id: this.calx.id + '-month_ctxbtn_prevmonth', //0.0.6 modified ID
				iconCls: 'x-calendar-month-btnmv_prevmth',	 text: e2cs.cal_locale.contextMenuLabelsMonth.goprevmonth,scope:this
			},'-',{
				id: this.calx.id + '-month_ctxbtn_viewweek', //0.0.6 modified ID
				iconCls: 'x-calendar-month-btnmv_viewweek',	 text: e2cs.cal_locale.contextMenuLabelsMonth.chgwview,scope:this
			},{
				id: this.calx.id + '-month_ctxbtn_viewday',  //0.0.6 modified ID
				iconCls: 'x-calendar-month-btnmv_viewday',	 text: e2cs.cal_locale.contextMenuLabelsMonth.chgdview,scope:this
			},{
				id: this.calx.id + '-month_ctxbtn_viewsched', //0.0.11
				iconCls: 'x-calendar-month-btnmv_viewsched', text: e2cs.cal_locale.contextMenuLabelsMonth.chgsview,scope:this
			}]
		});
		if (this.moreMenuItems.length>0) { 
			this.menu.add('-'); 
			for (var i=0; i< this.moreMenuItems.length; i++){
				var idmenuitem = this.moreMenuItems[i].id; 
				this.moreMenuItems[i].rendered =false; 
				this.moreMenuItems[i].addListener('click', 
							function(parx){ 
							   //this.onCustomMenuAction(idmenuitem, Ext.get(elx),this); //0.0.11 - bug fix dfor passed ID 
   							   this.onCustomMenuAction(parx.id, Ext.get(elx),this); 
							}, this); 
				this.menu.add( this.moreMenuItems[i]);
			}
		}
		this.menu.items.items[0].addListener('click', function(){ this.onTaskAdd( Ext.get(elx),this) ; 	} , this); 
		this.menu.items.items[2].addListener('click', function(){ this.onclicknext_month(); }, this); // added on alpha 0.0.4                
		this.menu.items.items[3].addListener('click', function(){ this.onclickprev_month();    }, this); // added on alpha 0.0.4                
		this.menu.items.items[5].addListener('click', function(){ this.changeCalview(Ext.get(elx),this,1);     }, this); 
		this.menu.items.items[6].addListener('click', function(){ this.changeCalview(Ext.get(elx),this,2);     }, this);			
		this.menu.items.items[7].addListener('click', function(){ this.changeCalview(Ext.get(elx),this,3);     }, this);
		//0.0.11 - check visibility on the menu-items according to the new property 
		//ShowMenuItems:[1,1,1,1,1,1],//   //0.0.11  ADD, nextmonth, prevmonth, chg Week , CHG Day, chg Sched,
		if (this.ShowMenuItems[0]!=true){	
						this.menu.items.items[0].hidden=true; //ADD
						this.menu.items.items[1].hidden=true; //SEPARATOR 	
		} 				
		if (this.ShowMenuItems[1]!=true && this.ShowMenuItems[2]!=true && this.ShowMenuItems[3]!=true && this.ShowMenuItems[4]!=true && this.ShowMenuItems[5]!=true){
					this.menu.items.items[1].hidden=true; //SEPARATOR 	
		} 
		if (this.ShowMenuItems[1]!=true){	this.menu.items.items[2].hidden=true;		}  //NEXt month 
		if (this.ShowMenuItems[2]!=true){	this.menu.items.items[3].hidden=true;		}  //Prev Month 	
		if (this.ShowMenuItems[1]!=true && this.ShowMenuItems[2]!=true){
			this.menu.items.items[4].hidden=true;	
		}
		if (this.ShowMenuItems[3]!=true){	this.menu.items.items[5].hidden=true;		} 
		if (this.ShowMenuItems[4]!=true){	this.menu.items.items[6].hidden=true;		} 
		if (this.ShowMenuItems[5]!=true){	this.menu.items.items[7].hidden=true;		} 
		if (this.ShowMenuItems[3]==false && this.ShowMenuItems[4]==false && this.ShowMenuItems[5]==false){
				if (this.moreMenuItems.length>0) { this.menu.items.items[8].hidden=true; } 
		} 		
		// check the existence of view objects  0.0.11 
		if (!this.calx.dview){ this.menu.items.items[6].hidden=true; }
		if (!this.calx.wview){ this.menu.items.items[5].hidden=true; }
		if (!this.calx.sview){ this.menu.items.items[7].hidden=true; }
		if  (this.menu.items.items[5].hidden && this.menu.items.items[6].hidden && this.menu.items.items[7].hidden ){ 
			if (this.moreMenuItems.length>0) { this.menu.items.items[8].hidden=true; } 
		} 
		// ----------------------------
		this.menu.on('hide', this.onContextHide, this);
		this.menu.showAt( [ evx.getPageX(), evx.getPageY() ] );//this.menu.showAt( Ext.get(elx).getXY());	
	}, 
	onCustomMenuAction: function(MenuId,MonthEl,TaskObj){
		var datatask =[]; 		
		var tmpobj  = Ext.get(MonthEl); 
		if (MonthEl.dom.className=='noday' || MonthEl.dom.className=='today' || MonthEl.dom.className=='monthday') { 
			//datatask[0] = Ext.get(MonthEl).dom.id        //id  (date) of the selected day in month view
			datatask[0] = Ext.get(MonthEl).dom.id.substring(this.calx.id.length + 1);//0.0.6
			datatask[1] = Ext.get(MonthEl).getAttributeNS('tag','class'); // class name of selected date 
		} else if (MonthEl.dom.className=='tasks' ){ 
			//datatask[0] = Ext.get(MonthEl).dom.parentNode.firstChild.id //id  (date) of the selected day in month view		
			datatask[0] = Ext.get(MonthEl).dom.parentNode.firstChild.id.substring(this.calx.id.length + 1); //0.0.6
			datatask[1] = Ext.get( MonthEl.dom.parentNode.firstChild ).getAttributeNS('tag','class');
		} else { 
			//datatask[0] = Ext.get(MonthEl).dom.firstChild.id //id  (date) of the selected day in month view
			datatask[0] = Ext.get(MonthEl).dom.firstChild.id.substring(this.calx.id.length + 1);//0.0.6
			datatask[1] = Ext.get( MonthEl.dom.firstChild ).getAttributeNS('tag','class');    // class name of selected date 
		}  
		this.calx.fireEvent("customMenuAction", MenuId, 'month', datatask, MonthEl, this); 
	},	
	changeCalview: function(objx, mviewx, typeview){
		if (objx.dom.className=='noday' || objx.dom.className=='today' || objx.dom.className=='monthday') { 
			//var refdate= new Date(objx.id); //0.0.6
			var refdate= new Date(objx.id.substring(this.calx.id.length + 1) );
		} else if (objx.dom.className=='tasks' ){ 
			//var refdate = new Date(objx.dom.parentNode.firstChild.id); //0.0.6
			var refdate = new Date(objx.dom.parentNode.firstChild.id.substring(this.calx.id.length + 1) );
		} else { 
			//var refdate= new Date(objx.dom.firstChild.id); //0.0.6
			var refdate= new Date(objx.dom.firstChild.id.substring(this.calx.id.length + 1) );
		} 
		if (typeview==1){
			varview='week'; 
		} else if(typeview==2){   //0.0.11
			varview='day'; 
		} else { //0.0.11
			varview='schedule'; 
		}
		this.calx.changeView(varview);
	},
	onTaskAdd: function(objx, mviewx){
		if (objx.dom.className=='noday' || objx.dom.className=='today' || objx.dom.className=='monthday') { 
			//var refdate= new Date(objx.id); //0.0.6
			var refdate= Date.parseDate(objx.id.substring(this.calx.id.length + 1), this.parse_format);
		} else if (objx.dom.className=='tasks' ){ 
			//var refdate = new Date(objx.dom.parentNode.firstChild.id);  //0.0.6
			var refdate = new Date(objx.dom.parentNode.firstChild.id.substring(this.calx.id.length + 1) );
		} else if (objx.dom.className=="tasks_list_item" || objx.dom.className=="tasks_list_item task_list_item_over" || objx.dom.className.indexOf("task_list_item_over")>0 ){
			//tmpdata =  elx.id.replace(/test_calendar-tasklist-/,''); bug 
			tmpdata =  objx.id.replace(this.calx.id + "-tasklist-",'');
			refdate =  Date.parseDate(tmpdata + "" ,this.parse_format); 
		} else if (objx.dom.className=="tasks_list"){ // to check beheavior 
			tmpdata =  objx.id.replace(this.calx.id + "-tasklist-",'');
			var refdate =  Date.parseDate(tmpdata + "",this.parse_format ); 
		} else if (objx.dom.className=="daybody"){ // to check beheavior 
			//0.0.11 bug fix it was taking the current date 
			tmpdata =  objx.id.replace("m-td-" + this.calx.id + "-",'');//m-td-test_calx-11/11/2008	
			refdate =  Date.parseDate(tmpdata + "" ,this.parse_format); 
			//var refdate= new Date(this.calx.currentdate);//prev code			
		} else { 
			//var refdate= new Date(objx.dom.firstChild.id); // 0.0.6
			var refdate= Date.parseDate(objx.dom.firstChild.id.substring(this.calx.id.length + 1),this.parse_format );
		} 
		var count = this.getCountTask(refdate,this.calx.store);
		var beforeTaskAddFalg = true;
		if(count){
			var beforeTaskAddFalg =  this.calx.fireEvent("beforeTaskAdd", refdate,e2cs.cal_locale.addTaskMsg);
		}
		if(beforeTaskAddFalg){
			this.calx.fireEvent("taskAdd", refdate);	
		}
		
	},
	onContextHide: function(){ 		/*do nothing  */	},	
	operataskitembuttons: function (evx,elx,obx){// 0.0.9 
		if (Ext.isOpera){ 
				// if (evx.button==2){ this.oncontext_taskitem_menu(evx,elx,obx); }  
				// For the moment do nothing cause it raise an error i cant fix  :( cause "this" object its not recognized when launching a function  
				// So i repeat 	oncontextmenuTaskitem function code in her and it works (OO) its weird 																		   
				evx.stopEvent();
				var tmpdata= Ext.get(elx.id);
				//0.0.14 Fix to set logical conditions for context menu (apply to events only) 		
				var toshowOnCXmenu = this.TaskList_ShowMenuItems;  	// Reference TaskList_ShowMenuItems:[1,1,1],// //0.0.11  ADD, delete, edit
				var newmenuitems   = this.TaskList_moreMenuItems; 
				var actionsTaskCX=[]; // Custom Menu items to the contextmenu of the task 
				var dataTASKtmp = this.getTaskarray(tmpdata); 
				var testevent =  this.calx.fireEvent("beforeContextMenuTask", "monthview-task",dataTASKtmp,toshowOnCXmenu,actionsTaskCX); 
				if (testevent==true) { 
					// actionsTaskCX[0]   Will tellus if we abort the context menu
					// actionsTaskCX[1]   Will tell us if we Apply the logic 
					// actionsTaskCx[2]   Will tell us if we replace the Custom MenuItems 
					// actionsTaskCX[3]   Will contain the buttons or menu items to insert
					// actionsTaskCX[4]   Will contain the new logic to Show basic items on the task 
					if 	(actionsTaskCX[0]==false){  //If false we continue 
						if 	(actionsTaskCX[1]==true){  //If true we apply the new items  instead of the set in day view 
							if (actionsTaskCX[2]==true){ 
								var newmenuitems = actionsTaskCX[3];  // new more menu Items
							} 
							var toshowOnCXmenu = actionsTaskCX[4];		// the new showitems rule 
						} else {
							var newmenuitems   = this.TaskList_moreMenuItems; 					
							var toshowOnCXmenu = this.TaskList_ShowMenuItems; 
						} 
					} else {  //abort operation 
						return false; 
					} 
				} else { // do nothing follow as planned 
					var newmenuitems = this.TaskList_moreMenuItems; 
				} 
				//--------------------------------------------------------------------
				//var dateobjtoref = elx.id.replace(/test_calendar-tasklist-/,'');
				var dateobjtoref  =  elx.id.replace(this.calx.id + "-tasklist-",'');
				if (this.taskitem_menu){ this.taskitem_menu.removeAll() ; }
				this.taskitem_menu = new Ext.menu.Menu({
					shadow: true, 
					items:[{id:'month_tskitem_btn_task-add',	iconCls:'x-calendar-day-btnmv_add',		text: this.contextMenuLabels.taskAdd, 	scope:this},
						   {id:'month_tskitem_btn_task-delete', iconCls: 'x-calendar-day-btnmv_delete',	text: this.contextMenuLabels.taskDelete,scope:this},
						   '-',
						   {id:'month_tskitem_btn_task-edit',	iconCls: 'x-calendar-day-btnmv_task',	text: this.contextMenuLabels.taskEdit + tmpdata.getAttributeNS('tag','ec_subject'),scope:this	}
					]
				});
//				if (newmenuitems.length>0) { 
//					this.taskitem_menu.add('-'); 
//					for (var i=0; i<newmenuitems.length; i++){
//						newmenuitems[i].rendered =false; 
//						newmenuitems[i].addListener('click', 
//									function(parx , parz){ 
//										this.onCustomMenuAction_TaskItem(parx.id, Ext.get(elx), this );
//									}, this); 
//						this.taskitem_menu.add( newmenuitems[i]);
//					}
//				}
				if (newmenuitems.length>0) { 
					this.taskitem_menu.add('-'); 
					for (var i=0; i<newmenuitems.length; i++){
						// var idmenuitem = newmenuitems[i].id;  0.0.4 bug in custom action sending the id the problem was the last id was returned always
						newmenuitems[i].rendered =false;
						// 0.0.14 modification to let the user set buttons with menu attached on the context menu 
						// Sepearators could be used also. Note: only one level its allowed
						if (newmenuitems[i].menu==undefined) {  //just plain button 
							if (newmenuitems[i].ctype=="Ext.menu.Item"){ 
								newmenuitems[i].addListener('click', 
											function(parx , parz){ 
													this.onCustomMenuAction_TaskItem(parx.id, Ext.get(elx),this ); 
											}, this); 
							} else { 
								//Ext.menu.BaseItem // do nothing
							} 
							this.taskitem_menu.add( newmenuitems[i]);
						} else {	
							for (var xm=0;xm<newmenuitems[i].menu.items.length;xm++){
								newmenuitems[i].menu.items.items[xm].rendered=false;
								if (newmenuitems[i].menu.items.items[xm].ctype=="Ext.menu.Item"){ 
									newmenuitems[i].menu.items.items[xm].addListener('click', 
												function(parx,parz){ 
														this.onCustomMenuAction_TaskItem( parx.id, Ext.get(elx),this ); 
												}, this);
								} else { 
									//Ext.menu.BaseItem  // do nothing 
								}
							}
							this.taskitem_menu.add(newmenuitems[i]);
						} 
					}
				}				
				this.taskitem_menu.items.items[0].addListener('click', function(){ this.onActionTask( 1, Ext.get(elx), this ); 	}, this); 
				this.taskitem_menu.items.items[1].addListener('click', function(){ this.onActionTask( 2, Ext.get(elx), this ); 	}, this); 
				this.taskitem_menu.items.items[3].addListener('click', function(){ this.onActionTask( 3, Ext.get(elx), this ); 	}, this); 
				//0.0.11 Chek visibility according to new permision on
				//TaskList_ShowMenuItems:[1,1,1],// //0.0.11  ADD, delete, edit
				if (toshowOnCXmenu[0]!=true){ this.taskitem_menu.items.items[0].hidden=true; } 
				if (toshowOnCXmenu[1]!=true){ this.taskitem_menu.items.items[1].hidden=true; } 
				if (toshowOnCXmenu[0]!=true && toshowOnCXmenu[1]!=true){ 
					this.taskitem_menu.items.items[2].hidden=true;
				} 
				if (toshowOnCXmenu[2]!=true){ this.taskitem_menu.items.items[3].hidden=true; } 
				//---------------------------------------------------------------
				this.taskitem_menu.on('hide', this.onContextHide_Taskitem, this);
				this.taskitem_menu.showAt(evx.xy);																   																   
		}
	},
	onDblClick_tasklistitem: function(evx,elx,obx){ // 0.0.9 
		//test=elx.id.indexOf('test_calendar-mtask-item-')+1;  bug 
		test=elx.id.indexOf( this.calx.id + '-mtask-item-')+1;
		if (test){ 
			var datatask = this.getTaskarray(elx); 
			this.calx.fireEvent("taskDblClick",datatask,this,this.calx,'month'); //launch new event on the month object 
		} 
	},
	oncontextmenuTaskitem:function(evx,elx,obx){		// 0.0.9		
		if (Ext.isOpera){ if (evx.button!=2){ return false; } }
		evx.stopEvent();
		//0.0.11 Avoid display menu cause all menus are set to hidden on /TaskList_ShowMenuItems:[1,1,1],// //0.0.11  ADD, delete, edit
		if (this.TaskList_ShowMenuItems[0]!=true && this.TaskList_ShowMenuItems[1]!=true && this.TaskList_ShowMenuItems[2]!=true && this.TaskList_moreMenuItems.length<=0 ){ 
			return false;
		} 
		var tmpdata= Ext.get(elx.id);//0.0.14 Fix to set logical conditions for context menu (apply to events only) 		
		var toshowOnCXmenu = this.TaskList_ShowMenuItems;  	// Reference TaskList_ShowMenuItems:[1,1,1],// //0.0.11  ADD, delete, edit
		var newmenuitems   = this.TaskList_moreMenuItems; 
		var actionsTaskCX=[]; // Custom Menu items to the contextmenu of the task 
		var dataTASKtmp = this.getTaskarray(tmpdata); 
		var testevent =  this.calx.fireEvent("beforeContextMenuTask", "monthview-task",dataTASKtmp,toshowOnCXmenu,actionsTaskCX); 
		if (testevent==true) { 
			// actionsTaskCX[0]   Will tell us if we abort the context menu
			// actionsTaskCX[1]   Will tell us if we Apply the logic 
			// actionsTaskCx[2]   Will tell us if we replace the Custom MenuItems 
			// actionsTaskCX[3]   Will contain the buttons or menu items to insert
			// actionsTaskCX[4]   Will contain the new logic to Show basic items on the task 
			if 	(actionsTaskCX[0]==false){  //If false we continue 
				if 	(actionsTaskCX[1]==true){  //If true we apply the new items  instead of the set in day view 
					if (actionsTaskCX[2]==true){ 
						var newmenuitems = actionsTaskCX[3];  // new more menu Items
					} 
					var toshowOnCXmenu = actionsTaskCX[4];		// the new showitems rule 
				} else {
					var newmenuitems   = this.TaskList_moreMenuItems; 					
					var toshowOnCXmenu = this.TaskList_ShowMenuItems; 
				} 
			} else {  //abort operation 
				return false; 
			} 
		} else { // do nothing follow as planned 
			var newmenuitems = this.TaskList_moreMenuItems; 
		} 
		//--------------------------------------------------------------------
		//var dateobjtoref = elx.id.replace(/test_calendar-tasklist-/,''); bug 
		var dateobjtoref = elx.id.replace(this.calx.id + "-tasklist-",'');
		if (this.taskitem_menu){ this.taskitem_menu.removeAll() ; }
		this.taskitem_menu = new Ext.menu.Menu({
			shadow: true, 
			items:[{id:'month_tskitem_btn_task-add',	iconCls:'x-calendar-day-btnmv_add',		text: this.contextMenuLabels.taskAdd, 	scope:this},
				   {id:'month_tskitem_btn_task-delete', iconCls: 'x-calendar-day-btnmv_delete',	text: this.contextMenuLabels.taskDelete,scope:this},
				   '-',
				   {id:'month_tskitem_btn_task-edit',	iconCls: 'x-calendar-day-btnmv_task',	text: this.contextMenuLabels.taskEdit + tmpdata.getAttributeNS('tag','ec_date'),scope:this	}
			]
		});
//		if (newmenuitems.length>0) { 
//			this.taskitem_menu.add('-'); 
//			for (var i=0; i<newmenuitems.length; i++){
//				newmenuitems[i].rendered =false; 
//				newmenuitems[i].addListener('click', 
//							function(parx , parz){ 
//								this.onCustomMenuAction_TaskItem(parx.id, Ext.get(elx), this );
//							}, this); 
//				this.taskitem_menu.add( newmenuitems[i]);
//			}
//		}
		if (newmenuitems.length>0) { 
			this.taskitem_menu.add('-'); 
			for (var i=0; i<newmenuitems.length; i++){
				// var idmenuitem = newmenuitems[i].id;  0.0.4 bug in custom action sending the id the problem was the last id was returned always
				newmenuitems[i].rendered =false;
				// 0.0.14 modification to let the user set buttons with menu attached on the context menu,Sepearators could be used also. Note: only one level its allowed
				if (newmenuitems[i].menu==undefined) {  //just plain button 
					if (newmenuitems[i].ctype=="Ext.menu.Item"){ 
						newmenuitems[i].addListener('click', 
									function(parx , parz){ 
											this.onCustomMenuAction_TaskItem(parx.id, Ext.get(elx),this ); 
									}, this); 
					} else { //Ext.menu.BaseItem // do nothing
					} 
					this.taskitem_menu.add( newmenuitems[i]);
				} else {	
					for (var xm=0;xm<newmenuitems[i].menu.items.length;xm++){
						newmenuitems[i].menu.items.items[xm].rendered=false;
						if (newmenuitems[i].menu.items.items[xm].ctype=="Ext.menu.Item"){ 
							newmenuitems[i].menu.items.items[xm].addListener('click', 
										function(parx,parz){ 
												this.onCustomMenuAction_TaskItem( parx.id, Ext.get(elx),this ); 
										}, this);
						} else { //Ext.menu.BaseItem  // do nothing 
						}
					}
					this.taskitem_menu.add(newmenuitems[i]);
				} 
			}
		}		
		this.taskitem_menu.items.items[0].addListener('click', function(){ this.onActionTask( 1, Ext.get(elx), this ); 	}, this); 
		this.taskitem_menu.items.items[1].addListener('click', function(){ this.onActionTask( 2, Ext.get(elx), this ); 	}, this); 
		this.taskitem_menu.items.items[3].addListener('click', function(){ this.onActionTask( 3, Ext.get(elx), this ); 	}, this); 		
		//0.0.11 Chek visibility according to new permision on
		//TaskList_ShowMenuItems:[1,1,1],// //0.0.11  ADD, delete, edit
		if (toshowOnCXmenu[0]!=true){  	this.taskitem_menu.items.items[0].hidden=true; /*ADD*/ } 
		if (toshowOnCXmenu[1]!=true){ 	this.taskitem_menu.items.items[1].hidden=true; /*DELETE */ } 
		if (toshowOnCXmenu[0]!=true && toshowOnCXmenu[1]!=true){ this.taskitem_menu.items.items[2].hidden=true; /*SEPARATOR */	} 
		if (toshowOnCXmenu[2]!=true){ 	
			this.taskitem_menu.items.items[3].hidden=true;  //EDIT 
			if (newmenuitems.length>0){ 
				this.taskitem_menu.items.items[4].hidden=true; 		
			} 
		} 			
// 		Original CODE ------------------------------------------------------------------------------
//		if (this.TaskList_ShowMenuItems[0]!=true){ this.taskitem_menu.items.items[0].hidden=true; } 
//		if (this.TaskList_ShowMenuItems[1]!=true){ this.taskitem_menu.items.items[1].hidden=true; } 
//		if (this.TaskList_ShowMenuItems[0]!=true && this.TaskList_ShowMenuItems[1]!=true){ 
//			this.taskitem_menu.items.items[2].hidden=true;
//		} 
//		if (this.TaskList_ShowMenuItems[2]!=true){ 	
//			this.taskitem_menu.items.items[3].hidden=true; 
//			if (this.TaskList_moreMenuItems.length>0){ 
//				this.taskitem_menu.items.items[4].hidden=true; 		
//			} 
//		} 
// 		--------------------------------------------------------------------------------------------				
		this.taskitem_menu.on('hide', this.onContextHide_Taskitem, this);
		this.taskitem_menu.showAt(evx.xy);
	}, 
	onActionTask: function (action, taskEl, TaskObj ){ //0.0.9 
		this.taskitem_menu.hide();	
		var datatask = this.getTaskarray(taskEl); 
		switch(action){
			case 1:  //add	
				//var dateobjtoref = taskEl.id.replace(/test_calendar-mtask-item-/,'');
				if (taskEl.id.indexOf('-mtask-item-')>0){ 
					var dateobjtoref  =  taskEl.id.replace(this.calx.id + "-mtask-item-",'');	
				} else if (taskEl.id.indexOf('-tasklist-')>0){ 
					var dateobjtoref  =  taskEl.id.replace(this.calx.id + "-tasklist-",'');	
				}				
				dateobjtoref = dateobjtoref.substr(0,10);
				var eventdate = Date.parseDate(dateobjtoref,this.parse_format);
				var count = this.getCountTask(eventdate,this.calx.store);
				var beforeTaskAddFalg = true;
				if(count){
					var beforeTaskAddFalg =  this.calx.fireEvent("beforeTaskAdd", eventdate,e2cs.cal_locale.addTaskMsg);
				}
				if(beforeTaskAddFalg){
					this.calx.fireEvent("taskAdd", eventdate);	
				}
				break; 
			case 2: // delete
				var check = this.calx.fireEvent("beforeTaskDelete", datatask, this);  
				if (check!=true){ 
					
					if (this.calx.fireEvent("onTaskDelete",datatask)==true){ 
						this.calx.fireEvent("afterTaskDelete",datatask,true);
					} else { 
						this.calx.fireEvent("afterTaskDelete",null,false);
					} 
				} 
				break; 
			case 3: //edit
				var check = this.calx.fireEvent("beforeTaskEdit",datatask,this);  
				if (check!=true){ 
					if (this.calx.fireEvent("onTaskEdit",datatask)==true){ 
						this.calx.fireEvent("afterTaskEdit",datatask,true);
					} else { 
						this.calx.fireEvent("afterTaskEdit",null,false);
					}	
				}
				break; 			
			default: 
				break; 
		} 
	},	
	onCustomMenuAction_TaskItem:function(MenuId,taskEl,TaskObj){ 
		var datatask = this.getTaskarray(taskEl); 
		this.calx.fireEvent("customMenuAction", MenuId,'month',datatask,taskEl,this); 
		this.taskitem_menu.hide(); 
	},
	onContextHide_Taskitem: function(){ /*  0.0.9 	//do nothing */  },	
	onhandler_day: function (evx,elx,obx){
		if (elx.className=="noday" || elx.className=="today" || elx.className=="monthday") { 
			//var dateparam = new Date(elx.id);   // 0.0.6 fix 
			var dateparam = new Date( elx.id.substring(this.calx.id.length + 1) );
		} else if (elx.className=="tasks" ){ 
			//var dateparam = new Date(elx.parentNode.firstChild.id); 
			var dateparam = new Date( elx.parentNode.firstChild.id.substring(this.calx.id.length + 1) ); 
		} else if (elx.className=="tasks_list_item" || elx.className=="tasks_list" || elx.className=="tasks_list_item task_list_item_over" || elx.className.indexOf("task_list_item_over")>0 ){
			 var tmp=11; 		// do nothing  :( cause doesnt matter for this item 
		} else { 
			//var dateparam = new Date(elx.firstChild.id); 
			var dateparam = new Date( elx.firstChild.id.substring(this.calx.id.length + 1) ); 
		} 
		if (elx.className=="tasks_list_item" || elx.className=="tasks_list" || elx.className=="tasks_list_item task_list_item_over"  || elx.className.indexOf("task_list_item_over")>0 ){ 
			var tmp=11; // do nothing  :( cause doesnt matter for this item 
		}  else { 
			if (this.dayAction=="viewday"){
				//0.0.11  Bug fix because not receving the event 
				if ( this.calx.fireEvent("beforeChangeView", 'day' , this.calx.currentView, this.calx)==false ) { 
					return false; 
				} else { 
					this.calx.currentdate= dateparam; 
					this.calx.selector_dateMenu.picker.setValue(this.calx.currentdate);
					this.calx.currentView='day'; 	 
					this.calx.viewday.render();
					this.calx.fireEvent("onChangeView", 'day', 'month', this);
				} 
			} 
			if (this.dayAction=="event"){  this.fireEvent("dayClick", dateparam ,this, this.calx); }
			if (this.dayAction=='window'){ this.ZoomDay(dateparam); } 
		} 
	}, 
	onclickprev_month: function (){
		var changemonthdate = this.calx.currentdate.add(Date.MONTH,-1);
		var check = this.fireEvent("beforeMonthChange", this.calx.currentdate , changemonthdate); 
		if (!check){ 
			this.calx.currentdate = changemonthdate; 
			this.calx.selector_dateMenu.picker.setValue(this.calx.currentdate);
			this.render();
			this.fireEvent("afterMonthChange", changemonthdate);
		} 
	},
	onclicknext_month: function (){
		var changemonthdate = this.calx.currentdate.add(Date.MONTH,1);
		var check = this.fireEvent("beforeMonthChange", this.calx.currentdate , changemonthdate);
		if (!check){
			this.calx.currentdate = changemonthdate; 
			this.calx.selector_dateMenu.picker.setValue(this.calx.currentdate);
			this.render();
			this.fireEvent("afterMonthChange", changemonthdate);
		}
	}, 
	// ------------------------------------------------------------------------------------
	//construction functions  -------------------------------------------------------------
	// ------------------------------------------------------------------------------------	
	genHeader: function(dateval){
		var dt = new Date(dateval);
		Date.monthNames = e2cs.cal_locale.monthtitles; 
		var myheader = '<div class="x-calendar-month-header" style="width:' + (this.calx.width - 10) + 'px;">';
		myheader += '<div id="header">' + dt.format(this.headerFormat)  + '</div>'; 
		if (this.headerButtons){ //0.0.4 bug 
	    	myheader += '<div class="x-calendar-month-previous"></div>';
		    myheader += '<div class="x-calendar-month-next"></div>'; 
		} 
		myheader+= "</div>";
		return myheader; 
	},
	genDaysHeader: function() {
		var day_hdrtext = ""; 
		for (var i=0; i <e2cs.cal_locale.daytitles.length ; i++){ 
			//day_hdrtext+= '<td width="14%"><div class="dayheader">' + e2cs.cal_locale.daytitles[i] + '</div></td>';
			//0.0.8 fix for new property startDay :) 
			var d = this.startDay+i;
            if(d > 6){ d = d-7; }
			day_hdrtext+= '<td width="14%"><div class="dayheader">' + e2cs.cal_locale.daytitles[d] + '</div></td>';
		}
		return day_hdrtext;
	},
	genBody: function(dateval,configdata){ 
		var dt= new Date(dateval); 
		//var initday = dt.getFirstDayOfMonth();
		var initday = dt.getFirstDayOfMonth()-this.startDay;//0.0.8
		var daysgen = dt.getDaysInMonth(); 
		var irowtmp = 0; 
		// compensate for leap year
 	    if (dt.getMonth()==1) { // February only!
		  	if(( dt.getFullYear() % 4 == 0 && dt.getFullYear() % 100 != 0) || dt.getFullYear() % 400 == 0){
		  		daysgen = 29;
			}
	  	}		
		var mybody='<tr>';
		if (initday<0){ initday = 7	+ (initday)	;  } // fix BUG 05/11/2008 ---- 0.0.8 
		if (initday!=0){ 
			for(var iday=(initday); iday>0; iday--){
				// 20091203  (dateval.getMonth()+ 1)  + '/1/' + dateval.getFullYear()  ----------->  dateval.getFullYear()  +'-'+ (dateval.getMonth()+ 1) + '-1' 
				writedt= new Date( (dateval.getMonth()+ 1)  + '/1/' + dateval.getFullYear()  ).add(Date.DAY,(iday  * -1 )); 				
				// 20091203  writedt.format('m/d/Y')  ------------->   writedt.format('Y-m-d')
				mybody+='<td valign="top" id="m-td-' + this.calx.id + '-' + writedt.format('Y-m-d') + '" class="daybody">';
				// 20091203  writedt.format('j-M')  ------------->   writedt.format('M-j')
				mybody+='<div class="noday" id="' + this.calx.id + '-' + writedt.format('Y-m-d') + '">' +  writedt.format('M-j') + '</div>';
				// nice bug fix we missed the previous day of the previous month :( sorry  0.0.11 
				if (this.showTaskcount){ // get the count of task for each day
						tmptasks = this.getCountTask(writedt, this.calx.store); 
						if (tmptasks>0 ){ 
							mybody+='<div class="tasks" style="' + configdata.styletasks + '">' + e2cs.cal_locale.labelforTasksinMonth + ' (' + tmptasks + ')</div>'; 
						} 
				}  
				// 0.0.9 added 	--- task list items 			
				else { 
					if (this.showTaskList) { 
							var taskitems= this.getTaskList(writedt, this.calx.store); 
							if (taskitems!=''){	
								// 20091203  writedt.format('m/d/Y')  ------------->   writedt.format('Y-m-d')
								mybody+='<div class="tasks_list" id="' +  this.calx.id + '-tasklist-' + writedt.format('Y-m-d') + '" style="height:5px;' + configdata.styletasks + '">';
								mybody+=taskitems; 
								mybody+='</div>'; 	
							} 
					} 
				}
				mybody+='</td>'; 
			} 
		} 		
		icount = initday; 
		irowtmp   = 1; 
		startmonthdate = new Date( dt.format('m') + '/01/' + dt.format('Y') ); 
		for (var imonth=0; imonth<daysgen; imonth++){ 
			if (icount>=7){ 
				icount=0; 
				mybody+='</tr><tr>';
				irowtmp+=1;
			} 
			var datecreate= new Date(startmonthdate).add(Date.DAY, imonth);
			// 20091204   'm/d/Y'  ---------------->  'Y-m-d'
			if (datecreate.format('Y-m-d')==dt.format('Y-m-d') ){ 
				mybody+='<td valign="top" id="m-td-' + this.calx.id + '-' + datecreate.format('Y-m-d') + '" class="daybody" height="10px">'; 
				mybody+='<div class="today" id="'  + this.calx.id + '-' + datecreate.format('Y-m-d') + '">' +  datecreate.format('j') +   '</div>'; 
				if (this.showTaskcount){ // get the count of task for each day
						tmptasks = this.getCountTask(datecreate, this.calx.store); 
						if (tmptasks>0 ){ 
							mybody+='<div class="tasks" style="' + configdata.styletasks + '">' + e2cs.cal_locale.labelforTasksinMonth + ' (' + tmptasks + ')</div>'; 
						} 
				}  
				// 0.0.9 added 	--- task list items 			
				else { 
					if (this.showTaskList) { 
							var taskitems= this.getTaskList(datecreate, this.calx.store); 
							if (taskitems!=''){	
								// 20091204 'm/d/Y' ---------------> 'Y-m-d'
								mybody+='<div class="tasks_list" id="' +  this.calx.id + '-tasklist-' + datecreate.format('Y-m-d') + '" style="height:5px;' + configdata.styletasks + '">';
								mybody+=taskitems; 
								mybody+='</div>'; 	
							} 
					} 
				}
				mybody+='</td>'; 		
			} else { 
				// 20091204 'm/d/Y' ---------------> 'Y-m-d'
				mybody+='<td valign="top" id="m-td-' + this.calx.id + '-' + datecreate.format('Y-m-d') + '" class="daybody" height="10px">';
				// 20091208  0||6 is red else is null
				if(icount==0||icount==6){
					mybody+='<div class="monthday" id="' + this.calx.id + '-' + datecreate.format('Y-m-d') + '">' + '<font color = "red" > ' + datecreate.format('j') +  '</font>'+ '</div>';
				}else{
					mybody+='<div class="monthday" id="' + this.calx.id + '-' + datecreate.format('Y-m-d') + '">' +  datecreate.format('j') +   '</div>';
				} 					
				if (this.showTaskcount){ // get the count of task for each day
						tmptasks = this.getCountTask(datecreate, this.calx.store); 
						if (tmptasks>0 ){ 
							mybody+='<div class="tasks" style="' + configdata.styletasks + '">' + e2cs.cal_locale.labelforTasksinMonth + ' (' + tmptasks + ')</div>'; 
						} 
				} 
				// 0.0.9 added 	--- task list items 			
				else { 
					if (this.showTaskList) { 
							var taskitems= this.getTaskList(datecreate, this.calx.store); 
							if (taskitems!=''){	
								// 20091204 'm/d/Y' ---------------> 'Y-m-d'
								mybody+='<div class="tasks_list" id="' +  this.calx.id + '-tasklist-' + datecreate.format('Y-m-d') + '" style="height:5px;' + configdata.styletasks + '">';
								mybody+=taskitems; 
								mybody+='</div>'; 	
							} 
					} 
				}
				mybody+='</td>'; 		
			} 
			icount+=1; 
		}
		if (icount<7 ){ 
			var datatmp =0;
			for (var iday=icount; iday<7; iday++){
				datatmp=datatmp+1;  		
				var datenew = new Date(datecreate).add(Date.DAY,datatmp); 
				// 20091204 'm/d/Y' ---------------> 'Y-m-d'
				mybody+='<td valign="top"   id="m-td-' + this.calx.id + '-' + datenew.format('Y-m-d') + '" class="daybody" height="10px">';
				// 20091204 'j-M' ---------------> 'M-j'
				mybody+='<div class="noday" id="' + this.calx.id + '-' + datenew.format('Y-m-d') + '">' +  datenew.format('M-j') +   '</div>'; 					
				if (this.showTaskcount){ // get the count of task for each day
						//tmptasks = this.getCountTask(datecreate, this.calx.store); // bug 0.0.5
						tmptasks = this.getCountTask(datenew, this.calx.store); 
						if (tmptasks>0 ){ 
							mybody+='<div class="tasks" style="' + configdata.styletasks + '">' + e2cs.cal_locale.labelforTasksinMonth + ' (' + tmptasks + ')</div>'; 
						} 
				} // 0.0.9 added 	--- task list items 
				else { 
					if (this.showTaskList) { 
							var taskitems= this.getTaskList(datenew, this.calx.store); 
							if (taskitems!=''){	
								// 20091204 'm/d/Y' ---------------> 'Y-m-d'						
								mybody+='<div class="tasks_list" id="' +  this.calx.id + '-tasklist-' + datenew.format('Y-m-d') + '" style="height:5px;' + configdata.styletasks + '">';
								mybody+=taskitems; 
								mybody+='</div>'; 				
							} 
					} 
				} 			
				mybody+='</td>'; 
			} 
		} 
		mybody+='</tr>'; 
		return mybody; 
	},
	getCountTask:function(dtx,storex){
		var counttasks= storex.getCount();
		if ( counttasks>0 ){ 
			var count_in_day=0; 
			for (var itask=0; itask<counttasks ; itask++){
			// 20091203 start------------------------------------
				//dateinit = storex.getAt(itask).data[this.calx.fieldsRefer.startdate];//0.0.11dynamic fields  
				//dateend =  storex.getAt(itask).data[this.calx.fieldsRefer.enddate]; //0.0.11dynamic fields
				//checkdates = dtx.between( new Date(dateinit), new Date(dateend) ); 
				//chkformat =  dtx.format('m/d/Y'); 
				//test = new Date(dateinit); if (test.format('m/d/Y')==chkformat){  checkdates =true; } 
				//test = new Date(dateend);  if (test.format('m/d/Y')==chkformat){  checkdates =true; } 
				//var initxx = new Date(dateinit); var endxx = new Date(dateend); 
			    //if (initxx <dtx && endxx>dtx){ checkdates=true; } 
			    //if (checkdates){ 
			    checkdate = storex.getAt(itask).data[this.calx.fieldsRefer.date];
				test = Date.parseDate(checkdate, "Y-m-d");	
				if(test == null){
					test = new Date(checkdate);
				}			
				chkformat = dtx.format('Y-m-d');			
				if (chkformat == test.format('Y-m-d')){
			// 20091203 end ---------------------------------------->
					count_in_day+=1; 
				} 
			} 
			return count_in_day;
		} else { 
			return 0; 
		} 
	},
	getTaskList:function(dtx,storex){ //0.0.9  to create a task list on each day on month view :) 
		var daystoreturn = ""; 
		var counttasks= storex.getCount();
		if ( counttasks>0 ){ 
			var count_in_day=0; 
			for (var itask=0; itask<counttasks ; itask++){
			// 20091203 start----------------------------------------
				//dateinit = storex.getAt(itask).data[this.calx.fieldsRefer.startdate];  //0.0.11dynamic fields
				//dateend =  storex.getAt(itask).data[this.calx.fieldsRefer.enddate]; //0.0.11dynamic fields
				//checkdates = dtx.between( new Date(dateinit), new Date(dateend) ); 
				//chkformat =  dtx.format('m/d/Y'); 
				//test = new Date(dateinit); if (test.format('m/d/Y')==chkformat){  checkdates =true; } 
				//test = new Date(dateend);  if (test.format('m/d/Y')==chkformat){  checkdates =true; } 
				//var initxx = new Date(dateinit); var endxx = new Date(dateend); 
			    //if (initxx <dtx && endxx>dtx){ checkdates=true; } 			
				//if (checkdates){ 
				checkdate = storex.getAt(itask).data[this.calx.fieldsRefer.date];
				test = Date.parseDate(checkdate, this.parse_format);
				if(test == null){
					test = new Date(checkdate);
				}				
				chkformat = dtx.format(this.parse_format);
				if (chkformat == test.format(this.parse_format)){ 
			// 20091203 end------------------------------------------				
					if (count_in_day<this.showNumTasks){
					// 20091203 start------------------------------------------
						/*
						//'<tpl for=".">{starxl}{startval}<br>{endxl}{endval}<hr color="#003366" noshade>{details}</tpl>'
						var tmpdate = new Date(storex.getAt(itask).data[this.calx.fieldsRefer.startdate]);//0.0.11dynamic fields			
						var startlabel = tmpdate.format(this.task_format); 	
						tmpdateb = new Date(storex.getAt(itask).data[this.calx.fieldsRefer.enddate]);//0.0.11dynamic fields
						var endlabel = tmpdateb.format(this.task_format); 
						daystoreturn+='<div class="tasks_list_item"';
						daystoreturn+='style="background-color:' + storex.getAt(itask).data[this.calx.fieldsRefer.color] + '" ';  //0.0.11dynamic fields
						daystoreturn+='id="' + this.calx.id + '-mtask-item-' + chkformat + '-' + storex.getAt(itask).data[this.calx.fieldsRefer.id] + '" ';//0.0.11dynamic fields
						daystoreturn+='ec_id="' + storex.getAt(itask).data[this.calx.fieldsRefer.id] + '" ';//0.0.11dynamic fields
						
						daystoreturn+='ec_starts="' + startlabel + '" ';
						daystoreturn+='ec_ends="' + endlabel + '" ';
						//daystoreturn+='ec_subject="' + storex.getAt(itask).data.subject + '" ';
						daystoreturn+='ec_subject="' + storex.getAt(itask).data[this.calx.fieldsRefer.subject] + '" ';//0.0.11dynamic fields
						//daystoreturn+='ec_cnt="' + storex.getAt(itask).data.description + '" ';
						daystoreturn+='ec_cnt="' + storex.getAt(itask).data[this.calx.fieldsRefer.description] + '" ';//0.0.11dynamic fields
						daystoreturn+='ec_storeindex="' + itask + '" ';
						//daystoreturn+=' ext:qtitle="' + storex.getAt(itask).data.subject + '" ';
						daystoreturn+=' ext:qtitle="' + storex.getAt(itask).data[this.calx.fieldsRefer.subject] + '" ';//0.0.11dynamic fields
						var datatip = {
						   starxl:   e2cs.cal_locale.task_qtip_starts,
						   startval: startlabel,
						   endxl:    e2cs.cal_locale.task_qtip_ends, 
						   endval:   endlabel,
						   //details:  storex.getAt(itask).data.description,  color:storex.getAt(itask).data.color //0.0.11 dynamic fields 
						   details:  storex.getAt(itask).data[this.calx.fieldsRefer.description],  
						   color:storex.getAt(itask).data[this.calx.fieldsRefer.color]
						};
						
						//var mylist_itemTip = this.TaskList_tplqTip.apply(datatip); 
						//	0.0.14 	 deprecated TaskList_tplqTip because for consistency use in all views the calendar object property tplTaskTip
						var mylist_itemTip =  this.calx.tplTaskTip.apply(datatip); 						
						daystoreturn+='ext:qtip="' + mylist_itemTip + '">';
						daystoreturn+=storex.getAt(itask).data[this.calx.fieldsRefer.subject] + '</div>'; // 0.0.12 BUG fix
						count_in_day+=1;
						*/
						// 20091203 end-------------------------------------------------------
						var tmpdate = Date.parseDate(storex.getAt(itask).data[this.calx.fieldsRefer.date],this.parse_format);//0.0.11dynamic fields			
						var datelabel = tmpdate.format(this.task_format); 
						daystoreturn+='<div class="tasks_list_item"';
						daystoreturn+='style="background-color:' + storex.getAt(itask).data[this.calx.fieldsRefer.color] + '" ';;	
						daystoreturn+='id="' + this.calx.id + '-mtask-item-' + chkformat + '-' + storex.getAt(itask).data[this.calx.fieldsRefer.id] + '" ';
						daystoreturn+='ec_id="' + storex.getAt(itask).data[this.calx.fieldsRefer.id] + '" ';
						daystoreturn+='ec_userid="' + storex.getAt(itask).data[this.calx.fieldsRefer.userid] + '" ';
						daystoreturn+='ec_username="' + storex.getAt(itask).data[this.calx.fieldsRefer.username] + '" ';
						daystoreturn+='ec_date="' + storex.getAt(itask).data[this.calx.fieldsRefer.date] + '" ';
						daystoreturn+='ec_content="' + storex.getAt(itask).data[this.calx.fieldsRefer.content] + '" ';
						daystoreturn+='ec_storeindex="' + itask + '" ';
						daystoreturn+=' ext:qtitle="' + e2cs.cal_locale.labelforTasksinMonth + '" ';
						var datatip = {
						   usernamexl:  e2cs.cal_locale.task_qtip_username,
						   usernameval: storex.getAt(itask).data[this.calx.fieldsRefer.username],  
						   datexl:      e2cs.cal_locale.task_qtip_date,
						   dateval:     datelabel,
						   //details:  storex.getAt(itask).data.description,  color:storex.getAt(itask).data.color //0.0.11 dynamic fields 
						   content:     storex.getAt(itask).data[this.calx.fieldsRefer.content],  
						   color:       storex.getAt(itask).data[this.calx.fieldsRefer.color]
						};
						var mylist_itemTip =  this.calx.tplTaskTip.apply(datatip);
						daystoreturn+='ext:qtip="' + mylist_itemTip + '">';
						daystoreturn+= e2cs.cal_locale.labelforTasksinMonth + '</div>';
						if(this.isShowNumTasks){
							count_in_day+=1;
						}
					}
				} 
			} 
			return daystoreturn;
		} else { 
			return 0; 
		} 
	},
	getTaskarray: function(TaskElx){  //0.0.9 for gett the data on the task in task lit items 
		var tmpdata= Ext.get(TaskElx);
		var datatask =[];
		//20091204  start ---------------------------------------------------------- 
		/*
		datatask[0]=tmpdata.getAttributeNS('tag','id') ; 
		datatask[1]=tmpdata.getAttributeNS('tag','ec_id') ; 		
		datatask[2]=tmpdata.getAttributeNS('tag','ec_subject') ; 
		datatask[3]=tmpdata.getAttributeNS('tag','ec_starts') ; 
		datatask[4]=tmpdata.getAttributeNS('tag','ec_ends') ; 		
		datatask[5]=tmpdata.getAttributeNS('tag','ec_cnt') ; 
		datatask[6]=tmpdata.getAttributeNS('tag','ec_storeindex') ; 
		*/
		//20091204  end ---------------------------------------------------------- 
		datatask[0]=tmpdata.getAttributeNS('tag','id') ; 
		datatask[1]=tmpdata.getAttributeNS('tag','ec_id') ; 		
		datatask[2]=tmpdata.getAttributeNS('tag','ec_username') ; 
		datatask[3]=tmpdata.getAttributeNS('tag','ec_date') ; 		
		datatask[4]=tmpdata.getAttributeNS('tag','ec_content') ; 
		datatask[5]=tmpdata.getAttributeNS('tag','ec_storeindex') ; 
		return 	datatask; 	
	} 
});
