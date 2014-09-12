/**
 * 获取当期日期的格式
 */
function GetNowDate(){
	var myDate=new Date();  
	var month=myDate.getMonth()+1; 
	var date=myDate.getDate(); 
	var hours=myDate.getHours();
	var minnutes=myDate.getMinutes();
	var seconds=myDate.getSeconds();
	if(hours<=9)
	{
		hours="0"+hours;
	}
	if(minnutes<=9)
	{
		minnutes="0"+minnutes;
	}
	if(seconds<=9)
	{
		seconds="0"+seconds;
	}
	var nowTime=myDate.getFullYear()+"/"+month+"/"+date+" " +hours+":"+minnutes+":"+seconds;
	return nowTime;
}