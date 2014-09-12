<!-- Hide
isIE = (document.all ? true : false);    
    
function getIEPosX(elt) { return getIEPos(elt,"Left"); }    
function getIEPosY(elt) { return getIEPos(elt,"Top"); }    
function getIEPos(elt,which) {    
 iPos = 0    
 while (elt!=null) {    
  iPos += elt["offset" + which]    
  elt = elt.offsetParent    
 }    
 return iPos    
}    
    
function getXBrowserRef(eltname) {    
 return (isIE ? document.all[eltname].style : document.layers[eltname]);    
}    
    
function hideElement(eltname) { getXBrowserRef(eltname).visibility = 'hidden'; }    
    
// 按不同的浏览器进行处理元件的位置    
function moveBy(elt,deltaX,deltaY) {    
 if (isIE) {    
  elt.left = elt.pixelLeft + deltaX;    
  elt.top = elt.pixelTop + deltaY;    
 } else {    
  elt.left += deltaX;    
  elt.top += deltaY;    
 }    
}    
    
function toggleVisible(eltname) {    
 elt = getXBrowserRef(eltname);    
 if (elt.visibility == 'visible' || elt.visibility == 'show') {    
   elt.visibility = 'hidden';    
 } else {    
   fixPosition(eltname);    
   elt.visibility = 'visible';    
 }    
}    
    
function setPosition(elt,positionername,isPlacedUnder) {    
 positioner = null;    
 if (isIE) {    
  positioner = document.all[positionername];    
  elt.left = getIEPosX(positioner);    
  elt.top = getIEPosY(positioner);    
 } else {    
  positioner = document.images[positionername];    
  elt.left = positioner.x;    
  elt.top = positioner.y;    
 }    
 if (isPlacedUnder) { moveBy(elt,0,positioner.height); }    
}    
    
    
    
//——————————————————————————————————————    
    
         // 判断浏览器    
         isIE = (document.all ? true : false);    
    
         // 初始月份及各月份天数数组    
         var months = new Array("一月", "二月", "三月", "四月", "五月", "六月", "七月",    
	 "八月", "九月", "十月", "十一月", "十二月");    
         var daysInMonth = new Array(31, 28, 31, 30, 31, 30, 31, 31,    
            30, 31, 30, 31);    
	 var displayMonth = new Date().getMonth();   
 	 var displayYear = new Date().getFullYear();  
	 var displayDay = 0;

	 var displayDivName;    
	 var displayElement;    
    
         function getDays(month, year) {    
            //测试选择的年份是否是润年？    
            if (1 == month)    
               return ((0 == year % 4) && (0 != (year % 100))) ||    
                  (0 == year % 400) ? 29 : 28;    
            else    
               return daysInMonth[month];    
         }    
    
         function getToday() {    
            // 得到今天的日期    
            this.now = new Date();    
            this.year = this.now.getFullYear();    
            this.month = this.now.getMonth();    
            this.day = this.now.getDate();    
         }    

         // 并显示今天这个月份的日历    
         today = new getToday();    
    
         function newCalendar(eltName,attachedElement) {    
	    if (attachedElement) {    
	       if (displayDivName && displayDivName != eltName) hideElement(displayDivName);    
	       displayElement = attachedElement;    
	    }    
	    displayDivName = eltName; 

            today = new getToday();    
            var parseYear = parseInt(displayYear + '');    
            var newCal = new Date(parseYear,displayMonth,1);    
            var day = -1;    
            var startDayOfWeek = newCal.getDay();    
            if ((today.year == newCal.getFullYear()) &&    
                  (today.month == newCal.getMonth()))    
	    {    
               day = today.day;    
            }    
            var intDaysInMonth =    
               getDays(newCal.getMonth(), newCal.getFullYear());    
            var daysGrid = makeDaysGrid(startDayOfWeek,day,intDaysInMonth,newCal,eltName)    
	    if (isIE) {    
	       var elt = document.all[eltName];    
	       elt.innerHTML = daysGrid;    
	    } else {    
	       var elt = document.layers[eltName].document;    
	       elt.open();    
	       elt.write(daysGrid);    
	       elt.close();    
	    }    
	 }    
    
	 function incMonth(delta,eltName) {    
	   displayMonth += delta;    
	   if (displayMonth >= 12) {    
	     displayMonth = 0;    
	     incYear(1,eltName);    
	   } else if (displayMonth <= -1) {    
	     displayMonth = 11;    
	     incYear(-1,eltName);    
	   } else {    
	     newCalendar(eltName);    
	   }    
	 }    
    
	 function incYear(delta,eltName) {    
	   displayYear = parseInt(displayYear + '') + delta;    
	   newCalendar(eltName);    
	 }    
    
	 function makeDaysGrid(startDay,day,intDaysInMonth,newCal,eltName) {   
	    var daysGrid;    
	    var month = newCal.getMonth();    
	    var year = newCal.getFullYear();    
	    var isThisYear = (year == new Date().getFullYear());    
	    var isThisMonth = (day > -1)    
	    daysGrid = '<table border=1 cellspacing=0 cellpadding=0><tr><td><table border=0 cellspacing=0 cellpadding=2 bgcolor=#ffffff><tr><td colspan=7 bgcolor=#ffffff nowrap>';    
	    daysGrid += '<a href="javascript:hideElement(\'' + eltName + '\')"><B style="color:black;background-color:blue"><font  color=white>╳</font></B></a>';    
	    daysGrid += '&nbsp;&nbsp;';    
	    daysGrid += '<a href="javascript:incMonth(-1,\'' + eltName + '\')">《</a>';    
    
	    daysGrid += '<b>';    
	    if (isThisMonth) { daysGrid += '<font color=red>' + months[month] + '</font>'; }    
	    else { daysGrid += months[month]; }    
	    daysGrid += '</b>';
 
	    daysGrid += '<a href="javascript:incMonth(1,\'' + eltName + '\')">》</a>';    
	    if (month < 10) { daysGrid += '&nbsp;&nbsp;&nbsp;&nbsp;'; }
		daysGrid += '&nbsp;&nbsp;';    
	    daysGrid += '<a href="javascript:incYear(-1,\'' + eltName + '\')">《</a>';    
    
	    daysGrid += '<b>';    
	    if (isThisYear) { daysGrid += '<font color=red>' + year + '</font>'; }    
	    else { daysGrid += ''+year; }    
	    daysGrid += '</b>';    
    
	    daysGrid += '<a href="javascript:incYear(1,\'' + eltName + '\')">》</a></td></tr>'; 
		daysGrid += '<tr><td bgcolor=gray colspan=7 ></td></tr>';  
	    daysGrid += '<tr><td align=right><font color=red>日</font></td><td align=right>一</td><td align=right>二</td><td align=right>三</td><td align=right>四</td><td align=right>五</td><td align=right><font color=red>六</font></td></tr>';  
	    daysGrid += '<tr><td bgcolor=gray colspan=7 ></td></tr>';  
		
	    var dayOfMonthOfFirstSunday = (7 - startDay + 1);    
	    for (var intWeek = 0; intWeek < 6; intWeek++) {    
	       var dayOfMonth;    
	       for (var intDay = 0; intDay < 7; intDay++) {    
	         dayOfMonth = (intWeek * 7) + intDay + dayOfMonthOfFirstSunday - 7;    
		 if (dayOfMonth <= 0) {    
	           daysGrid += "</td><td>";    
		 } else if (dayOfMonth <= intDaysInMonth) {    
		   var color = "blue";    
		   if (day > 0 && day == dayOfMonth) color="red";   
		   if (dayOfMonth == displayDay) color="green";
		   daysGrid += '<td align=right><a href="javascript:setDay(';    
		   daysGrid += dayOfMonth + ',\'' + eltName + '\')" ';
		   daysGrid += 'style="color:' + color + '">';    
		   var dayString = dayOfMonth + "</a></td>";    
		   if (dayString.length == 6) dayString = '0' + dayString;    
		   daysGrid += dayString;    
		 }    
	       }    
	       if (dayOfMonth < intDaysInMonth) daysGrid += "</tr>";    
	    }    
	    return daysGrid + "</table></td></tr></table>";    
	 }    
    
//月份从0开始记数
	 function setDay(day,eltName) {
	   if (displayMonth < 9)
	   {
		  strDisplayMonth = "0" + (displayMonth+1);
	   }
	   else
	   {
		  strDisplayMonth = (displayMonth+1);			
	   }
	   if (day < 10)
	   {
		  day = "0" + day;
	   }
	   displayElement.value = displayYear + "-" + strDisplayMonth + "-" + day;    
	   hideElement(eltName);    
	 }    
    
    
//——————————————————————————————————————    
    
// fixPosition() 这个函数和前面所讲的那个函数一样  
//  
function fixPosition(eltname) {  
 elt = getXBrowserRef(eltname);  
 positionerImgName = eltname + 'Pos';  
 // hint: try setting isPlacedUnder to false  
 isPlacedUnder = false;  
 if (isPlacedUnder) {  
  setPosition(elt,positionerImgName,true);  
 } else {  
  setPosition(elt,positionerImgName)  
 }  
}  
 
 

function toggleDatePicker(eltName,formElt) {  
  var x = formElt.indexOf('.');  
  var formName = formElt.substring(0,x);  
  var formEltName = formElt.substring(x+1);  

	//Added by Wangjianke(jianke@itechs.iscas.ac.cn)
 	var attachedElement = document.forms[formName].elements[formEltName]

	    if (attachedElement) {    
	       if (displayDivName && displayDivName != eltName) hideElement(displayDivName);    
	       displayElement = attachedElement;    
	    }    
	    displayDivName = eltName; 

	 var defaultDate = displayElement.value;
	if ((defaultDate != "") && (defaultDate != "0000-00-00"))
	{
	 x = defaultDate.indexOf('-');  
	 var defaultYear = defaultDate.substring(0,x);  
	 var y = defaultDate.indexOf('-',x+1);
 	 var defaultMonth = defaultDate.substring(x+1,y)-1;
 	 var defaultDay = defaultDate.substring(y+1);

	 displayYear = defaultYear;	 
  	 displayMonth = defaultMonth;
	 displayDay = defaultDay;
	}
  //End Added

  newCalendar(eltName,document.forms[formName].elements[formEltName]);  
  toggleVisible(eltName);  
}  
    
// -->
