<!--
var g_MINY=1601;var g_MAXY=4500;var g_month=0;var g_day=0;var g_year=0;var g_yLow=1990;
function GetInputDate(t,f){
  var l=t.length;
  if(0==l)return false;
  var cSp='\0';var sSp1="";var sSp2="";
  for(var i=0;i<t.length;i++){
    var c=t.charAt(i);
	if(c==' '||isdigit(c))continue;
	else if(cSp=='\0'&&(c=='/'||c=='-'||c=='.')){ cSp=c;sSp1=t.substring(i+1,l);}
	else if(c==cSp)sSp2=t.substring(i+1,l);
	else if(c!=cSp)return false;
  }
  if(0==sSp1.length)return false;
  var m;var d;var y;
  if(g_dFmt=="mmddyy"){m=atoi(t);d=atoi(sSp1);}
  else{d=atoi(t);m=atoi(sSp1);}
  if(0!=sSp2.length)y=atoi(sSp2);
  else y=DefYr(m,d);
  if(y<100){
    y=1900+y;
	while(y<g_yLow)y=y+100;}
  if(y<g_MINY||y>g_MAXY||m<1||m>12)return false;
  if(d<1||d>GetMonthCount(m,y))return false;
  g_month=m;g_day=d;g_year=y;
  return true;
}

function DefYr(m,d){
  var dt=new Date();
  var yCur=(dt.getYear()<1000)?1900+dt.getYear():dt.getYear();
  if(m-1<dt.getMonth()||(m-1==dt.getMonth()&&d<dt.getDate()))return 1+yCur;else return yCur;
}

function atoi(s){
  var t=0;
  for(var i=0;i<s.length;i++){
    var c=s.charAt(i);
	if(!isdigit(c))return t;
	else t=t*10+(c-'0');
  }
  return t;
}

function isdigit(c){
  return(c>='0'&&c<='9');
}

function GetMonthCount(m,y){
  var c=rgMC[m-1];
  if((2==m)&&IsLeapYear(y))c++;
  return c;
}

function IsLeapYear(y){
  if(0==y%4&&((y%100!=0)||(y%400==0))) return true;else return false;
}

var rgMC=new Array(12);rgMC[0]=31;rgMC[1]=28;rgMC[2]=31;rgMC[3]=30;rgMC[4]=31;rgMC[5]=30;rgMC[6]=31;rgMC[7]=31;rgMC[8]=30;
  rgMC[9]=31;rgMC[10]=30;rgMC[11]=31;
var g_eC=null;var g_eCV="";var g_dFmt="mmddyy";var g_fnCB=null;

function ShowCalendar(eP,eD,eDP,dmin,dmax,fnCB){
  var dF=document.all.CalFrame;
  var wF=window.frames.CalFrame;
  if(null==wF.g_fCalLoaded||false==wF.g_fCalLoaded){
    alert("Unable to load popup calendar.\r\nPlease reload the page.");
	return;
  }
  dtMin=new Date();
  dtMin.setDate(dtMin.getDate()+dmin);
  dtMax=new Date();
  dtMax.setDate(dtMax.getDate()+dmax);
  wF.SetMinMax(new Date(dtMin),new Date(dtMax));
  g_fnCB=fnCB;
  wF.cityname(fnCB);
  if(eD==g_eC&&"block"==dF.style.display){
    if(g_eCV!=eD.value&&GetInputDate(eD.value,g_dFmt)){
	  wF.SetInputDate(g_day,g_month,g_year);
	  wF.SetDate(g_day,g_month,g_year);
	  g_eCV=eD.value;
    }
    else dF.style.display="none";
    }
  else{
    if(GetInputDate(eD.value,g_dFmt)){
	  wF.SetInputDate(g_day,g_month,g_year);
	  wF.SetDate(g_day,g_month,g_year);
	}
    else if(null!=eDP&&GetInputDate(eDP.value,g_dFmt)){
	    wF.SetInputDate(g_day,g_month,g_year);
	    wF.SetDate(g_day,g_month,g_year);
	  }
    else{
	  var dt=new Date(dtMin);
	  wF.SetInputDate(-1,-1,-1);
	  wF.SetDate(dt.getDate(),dt.getMonth()+1,dt.getFullYear());
	}
  var eL=0;var eT=0;var p=eP;
  while(p&&p.tagName!="BODY"){
    eT+=p.offsetTop;
	eL+=p.offsetLeft;
	p=p.offsetParent;
  }
  var eH=eP.offsetHeight;
  var dH=dF.style.pixelHeight;
  var sT=document.body.scrollTop;
  dF.style.left=eL;
  if(eT-dH>=sT&&eT+eH+dH>document.body.clientHeight+sT) dF.style.top=eT-dH;
  else dF.style.top=eT+eH;
  if("none"==dF.style.display) dF.style.display="block";g_eC=eD;g_eCV=eD.value;
  }
}

/*function SetDate(d,m,y){
  var ds="/";
  g_eC.focus();
  if(g_dFmt=="mmddyy") g_eC.value=m+ds+d+ds+y;
  else g_eC.value=d+ds+m+ds+y;
  g_eCV=g_eC.value;
  if(null!=g_fnCB&&""!=g_fnCB) eval(g_fnCB);
}*/
function SetDate(d,m,y){
  var ds="-";
  g_eC.focus();
  g_eC.value=y+ds+m+ds+d;
  g_eCV=g_eC.value;
  if(null!=g_fnCB&&""!=g_fnCB) eval(g_fnCB);
}

function GetDowStart(){return 0;}

function GetDOW2(d,m,y){
  var dt=new Date(y,m-1,d);
  
  return(dt.getDay()+(7-GetDowStart()))%7;
}

function LoadMonths(n){
  var dt=new Date();
  var m=dt.getMonth()+1;
 
  var y=dt.getFullYear();
   
  var rg=new Array(n);
  for(i=0;i<n;i++){
    rg[i]=document.createElement("IMG");
    rg[i].src="calendar/w"+GetDOW2(1,m,y)+"d"+GetMonthCount(m,y)+".gif";
    m++;
	if(12<m){ m=1;y++;}
  }
}

LoadMonths(12);

function chkBrowser(){
  this.ver=navigator.appVersion;
  this.dom=document.getElementById?1:0;
  this.ie5=(this.ver.indexOf("MSIE 5")>-1 && this.dom)?1:0;
  this.ie4=(document.all && !this.dom)?1:0;
  this.ns5=(this.dom && parseInt(this.ver) >= 5) ?1:0;
  this.ns4=(document.layers && !this.dom)?1:0;
  this.bVer=(this.ie5 || this.ie4 || this.ns4 || this.ns5);
  return this;
}

bVer=new chkBrowser();
ns4 = (document.layers)? true:false;
ie4 = (document.all)? true:false;
function AttB(f){if(bVer.ie4)f.style.display='block';}
function AttN(f){if(bVer.ie4)f.style.display='none';}

function show(idLayer,idParent){
  cLayer=bVer.dom?document.getElementById(idLayer).style:bVer.ie4?document.all[idLayer].style:bVer.ns4?idParent?document[idParent].document[idLayer]:document[idLayer]:0;
  cLayer.display='block';
  divLinksForm=(ns4)?document.divLinks.document.divLinks:document.divLinks;
  var d=document.Wiz;
  if (idLayer=='car'){
    AttB(d.CKind);AttB(d.Ctime1);AttB(d.Ctime2);d.srch[1].status='true';
  }
  else if (idLayer=='hot'){AttB(d.Hcadt);AttB(d.Hckid);d.srch[0].status='true';}
  else{AttB(d.FcAdu);d.srch[2].status='true';/*if (idParent!='flt' && d.opts[1].checked) hide('dts','flt');*/}
}

function hide(idLayer,idParent){
  cLayer=bVer.dom?document.getElementById(idLayer).style:bVer.ie4?document.all[idLayer].style:bVer.ns4?idParent?document[idParent].document[idLayer]:document[idLayer]:0;
  var d=document.Wiz;
  if(idLayer!='flt') AttN(d.FcAdu);
  AttN(d.Hcadt);
  AttN(d.Hckid);
  AttN(d.CKind);
  AttN(d.Ctime1);
  AttN(d.Ctime2);
  cLayer.display='none'
}
//-->