//翻页定位到某一页时调用的方法.
function upturnCheck(theform,radix)
{

    if(theform.tmpindex.value == "")
    {
        alert("请输入指定的页面");
        theform.tmpindex.focus();
        return;
    }
    if ( !fucNaturalNum(theform.tmpindex.value) )
    {
        alert("请输入整数型数字");
        theform.tmpindex.focus();
        return;
    }

    theform.index.value = eval(eval(theform.tmpindex.value-1)*radix);

    theform.submit();
}
//删除时对用户进行确认,询问是否删除
function Deleteconfirm(url)
{
   if(confirm("你确定要删除该记录吗?")!="0")
   {
      window.location.href=url
   }
}
//重启设备时对用户进行确认,询问是否删除
function Restartconfirm(url)
{
   if(confirm("你确定要重启设备吗?")!="0")
   {
      window.location.href=url
   }
}
//关闭设备时对用户进行确认,询问是否删除
function Shutdownconfirm(url)
{
   if(confirm("你确定要关闭设备吗?")!="0")
   {
      window.location.href=url
   }
}

//配置server.xml删除配置信息提示
function DELConfigserverxml(url)
{
   if(confirm("注意:删除信息后,必须点击应用按钮\n此功能才会生效.是否删除?")!="0")
   {
      window.location.href=url
   }
}

//配置server.xml添加配置信息提示
function ADDConfigserverxml()
{
   window.alert("注意:添加信息后,必须点击应用按钮\n此功能才会生效.");
}

//配置server.xml修改配置信息提示
function MODIFYConfigserverxml(url)
{
   window.alert("注意:修改信息后,必须点击应用按钮\n此功能才会生效.");
}

//配置server.xml应用配置信息提示
function APPLYConfigserverxml(url)
{
   if(confirm("注意:应用上面的配置信息后服务器将重启服务\n,用户必须重新登录系统.是否应用?")!="0")
   {
      window.location.href=url
   }
}

//IP校验：输入值必须在“0123456789./”范围内，否则返回false，如果用户不输入任何值，返回true
function fucCheckIP(NUM)
{
  var i,j,strTemp;
  strTemp="0123456789./";
  if ( NUM.length == 0) return true;
  for (i=0;i<NUM.length;i++)
  {
      j = strTemp.indexOf(NUM.charAt(i));
      if (j==-1)
      {
          return false;
      }

  }
  return true;
}

/*---------------------
模块名称：chekipnetmask
功能说明：ip地址的审核(附带子网掩码的判断)如:192.168.0.123/24
作者：yangc
创建日期：20051101
---------------------*/
function chekipnetmask(ipstr)
{
var ip_array1 = new Array();
var ip_array2 = new Array();
var i,j,strTemp;
var strerror=0;
var errornum=0;
strTemp="0123456789./";
if (ipstr.length == 0)
{
//ip不能为空
//errornum = errornum + 1;

}
else
{
for (i=0;i<ipstr.length;i++)
{
    j=strTemp.indexOf(ipstr.charAt(i));
    //说明有错误字符
    if (j==-1)
    {
        strerror=strerror+1;
    }
}
 //说明是数字
if(strerror == 0)
{

    //把传来的参数按"/"划分为数组
    ip_array1 = ipstr.split("/");
    //按"/"划分的数组长度等于2则有掩码
    if(ip_array1.length==2)
    {
        //端口号大于零小于等于32
        if(ip_array1[1]<0 || ip_array1[1]>32 || ip_array1[1]=="" || ip_array1[1]==null)
        {
            //返回错误"厌码号错误"
			errornum = errornum + 1;
        }
        else
        {
            //把ipstr[0]按"."划分数组
            ip_array2 = ip_array1[0].split(".");
            //按"."划分的数组长度等于4则有效
            if(ip_array2.length!=4)
            {
                //返回错误"ip格式错误"
				errornum = errornum + 1;
            }
            else
            {
                var iperror;
                iperror = 0;
                //ip的每个单元数大于等于0小于256
                for (i = 0; i < 4; i++)
                {
                    if( ip_array2[i]<0 || ip_array2[i]>255 || ip_array2[i]==null || ip_array2[i]=="" || (ip_array2[i].length==3 && ip_array2[i]< 100) || (ip_array2[i].length==2 && ip_array2[i]< 10))
                    iperror=iperror+1;
                }
                //返回错误"ip格式错误"
                if(iperror>0 || ip_array2[0]==0)
				{
					errornum = errornum + 1;
				}
            }

        }

    }
    else
    {
        //把ipstr按"."划分数组
        ip_array2 = ipstr.split(".");
        //按"."划分的数组长度等于4则有效
        if(ip_array2.length!=4)
        {
            //返回错误"ip格式错误"
			errornum = errornum + 1;
        }
        else
        {
            var iperror;
            iperror = 0;
            //ip的每个单元数大于等于0小于256
            for (i = 0; i < 4; i++)
            {
                if( ip_array2[i]<0 || ip_array2[i]>255 || ip_array2[i]==null || ip_array2[i]=="" || (ip_array2[i].length==3 && ip_array2[i]< 100) || (ip_array2[i].length==2 && ip_array2[i]< 10))
                iperror=iperror+1;
            }
            //返回错误"ip格式错误"
            if(iperror>0 || ip_array2[0]==0 )
			{
				errornum = errornum + 1;
			}
         }
    }
}
else
{
//返回错误"输入字符错误"
errornum = errornum + 1;
}
}
if(errornum==0)
{
	return true;
}
else
{
	return false;
}
}

/*---------------------
模块名称：chekip
功能说明：ip地址的审核(不带子网掩码)
作者：yangc
创建日期：20060303
---------------------*/

function chekip(ipstr)
{
var ip_array1 = new Array();
var ip_array2 = new Array();
var i,j,strTemp;
var strerror=0;
var errornum=0;
strTemp="0123456789.";
if (ipstr.length == 0)
{
//ip不能为空
//errornum = errornum + 1;

}
else
{
for (i=0;i<ipstr.length;i++)
{
    j=strTemp.indexOf(ipstr.charAt(i));
    //说明有错误字符
    if (j==-1)
    {
        strerror=strerror+1;
    }
}
 //说明是数字
if(strerror == 0)
{

        //把ipstr按"."划分数组
        ip_array2 = ipstr.split(".");
        //按"."划分的数组长度等于4则有效
        if(ip_array2.length!=4)
        {
            //返回错误"ip格式错误"
			errornum = errornum + 1;
        }
        else
        {
            var iperror;
            iperror = 0;
            //ip的每个单元数大于等于0小于256
            for (i = 0; i < 4; i++)
            {
                if( ip_array2[i]<0 || ip_array2[i]>255 || ip_array2[i]==null || ip_array2[i]=="" || (ip_array2[i].length==3 &&               ip_array2[i]< 100) || (ip_array2[i].length==2 && ip_array2[i]< 10))
                iperror=iperror+1;
            }
            //返回错误"ip格式错误"
            if(iperror>0 || ip_array2[0]==0 )
			{
				errornum = errornum + 1;
			}
         }

}
else
{
//返回错误"输入字符错误"
errornum = errornum + 1;
}
}
if(errornum==0)
{
	return true;
}
else
{
	return false;
}
}

//数字校验：输入值必须在“0123456789.”范围内，否则返回false，如果用户不输入任何值，返回true
function fucCheckNUM(NUM)
{
  var i,j,strTemp;
  strTemp="0123456789.";
  if ( NUM.length == 0) return true;
  for (i=0;i<NUM.length;i++)
  {
      j = strTemp.indexOf(NUM.charAt(i));
      if (j==-1)
      {
          return false;
      }

  }
  return true;
}
//自然数校验：输入值必须在“0123456789”范围内，否则返回false，如果用户不输入任何值，返回true
function fucNaturalNum(NUM)
{
  var i,j,strTemp;
  strTemp="0123456789";
  if ( NUM.length == 0) return true;
  if (NUM==0) return false;
  for (i=0;i<NUM.length;i++)
  {
    j = strTemp.indexOf(NUM.charAt(i));
    if (j==-1)
    {
      return false;
    }
  }
  return true;
}
//返回输入值的长度（没有过滤空格），用户不输入任何值返回-1
function fucCheckLength(strTemp)
{
  var i,sum;
  sum=0;
  strTemp = Trim(strTemp);
  if (strTemp.length<1) return -1;  for(i=0;i<strTemp.length;i++)
  {
    if ((strTemp.charCodeAt(i)>=0) && (strTemp.charCodeAt(i)<=255))
      sum=sum+1;
    else
      sum=sum+2;
  }
  return sum;
}
//email校验：输入值必须包括“@”"."，否则返回false，如果用户不输入任何值，返回true
function chkemail(a)
{
  var i=a.length;
  if ( a.length == 0) return true;
  var temp = a.indexOf('@');
  var tempd = a.indexOf('.');
  if(!(temp > 0 && tempd > 1)) return false;
  return true;
}
//电话号码校验：输入值必须在“0123456789-()#”范围内，否则返回false，如果用户不输入任何值，返回true
function fucCheckTEL(TEL)
{
  var i,j,strTemp;
  strTemp="0123456789-()#";
  if ((TEL.length > 0)&&(TEL.length<7)) return false;
  if (TEL.length = 0) return true;
  for (i=0;i<TEL.length;i++)
  {
    j=strTemp.indexOf(TEL.charAt(i));
    if (j==-1)
    {
      return false;
    }
  }
  return true;
}

function fucCheckPhone(Phone) {
var reg0 = /^13d{5,9}$/;  //13开头的手机号
var reg1 = /^153d{4,8}$/;  //153的手机号
var reg2 = /^159d{4,8}$/;  //159的手机号[separator]
var reg3 = /^0d{10,11}$/; //固定电话不带“-”
var p1 = /^(([0+]d{2,3}-)?(0d{2,3})-)?(d{7,8})(-(d{3,}))?$/;//固定电话带“-”
if((reg0.test(Phone)||reg1.test(Phone)||reg2.test(Phone)||reg3.test(Phone)||p1.test(Phone))==false){
   return false;
}
return true;
}

/* ======================================================================
FUNCTION:    Trim

INPUT:          str (string): the string to be altered

RETURN:         A string with no leading or trailing spaces;
                returns null if invalid arguments were passed

DESC:            This function removes all leading and tralining spaces from a string.

CALLS:        This function depends on the functions TrimLeft & TrimRight
                They must be included in order for this function to work properly.
====================================================================== */
function Trim( str ) {
    var resultStr = "";

    resultStr = TrimLeft(str);
    resultStr = TrimRight(resultStr);

    return resultStr;
} // end Trim

/* ======================================================================
FUNCTION:    TrimLeft

INPUT:          str (string): the string to be altered

RETURN:         A string with no leading spaces;
                returns null if invalid arguments were passed

DESC:            This function removes all leading spaces from a string.
====================================================================== */
function TrimLeft( str ) {
    var resultStr = "";
    var i = len = 0;

    // Return immediately if an invalid value was passed in
    if (str+"" == "undefined" || str == null)
        return null;

    // Make sure the argument is a string
    str += "";

    if (str.length == 0)
        resultStr = "";
    else {
          // Loop through string starting at the beginning as long as there
          // are spaces.
//          len = str.length - 1;
        len = str.length;

          while ((i <= len) && (str.charAt(i) == " "))
            i++;

       // When the loop is done, we're sitting at the first non-space char,
         // so return that char plus the remaining chars of the string.
          resultStr = str.substring(i, len);
      }

      return resultStr;
} // end TrimLeft

/* ======================================================================
FUNCTION:    TrimRight

INPUT:          str (string): the string to be altered

RETURN:         A string with no trailing spaces;
                returns null if invalid arguments were passed

DESC:            This function removes all trailing spaces from a string.
====================================================================== */
function TrimRight( str ) {
    var resultStr = "";
    var i = 0;

    // Return immediately if an invalid value was passed in
    if (str+"" == "undefined" || str == null)
        return null;

    // Make sure the argument is a string
    str += "";

    if (str.length == 0)
        resultStr = "";
    else {
          // Loop through string starting at the end as long as there
          // are spaces.
          i = str.length - 1;
          while ((i >= 0) && (str.charAt(i) == " "))
             i--;

         // When the loop is done, we're sitting at the last non-space char,
         // so return that char plus all previous chars of the string.
          resultStr = str.substring(0, i + 1);
      }

      return resultStr;
} // end TrimRight

/* ======================================================================
FUNCTION:    IsEnglishFileName

INPUT:        str (string) - the string to be tested

RETURN:      true, 如果str是字符,数字,下划线,或者点号
                                false, otherwise.

PLATFORMS:    Netscape Navigator 3.01 and higher,
                                  Microsoft Internet Explorer 3.02 and higher,
                                  Netscape Enterprise Server 3.0,
                                  Microsoft IIS/ASP 3.0.
====================================================================== */
function IsEnglishFileName( str )
{
    // Return immediately if an invalid value was passed in
    if (str+"" == "undefined" || str+"" == "null")
        return false;

    var isValid = true;

    str += "";

    //去掉路径,仅保留文件名
    while(str.length > 0 && str.indexOf("/") > -1)
        str = str.substring(str.indexOf("/") + 1,str.length);

    while(str.length > 0 && str.indexOf("\\") > -1)
        str = str.substring(str.indexOf("\\") + 1,str.length)

        for (i = 0; i < str.length; i++)
        {
            // Alphanumeric must be between "0"-"9", "A"-"Z", or "a"-"z", or "_", or ".",or" "
            if ( !( ((str.charAt(i) >= "0") && (str.charAt(i) <= "9")) ||
                    ((str.charAt(i) >= "a") && (str.charAt(i) <= "z")) ||
                    ((str.charAt(i) >= "A") && (str.charAt(i) <= "Z")) ||
                    (str.charAt(i) == "_") || (str.charAt(i) == ".") ) ||
                    (str.charAt(i) == " "))
            {
                isValid = false;
                break;
            }

        } // END for

        return isValid;

}  // end IsEnglishFileName

//检查输入值是否为>=0且<1
function CheckDecimalFraction(val)
{
    if(!CheckNUM(val))
    {
        return false;
    }
    var vall = new Number(val);
    if(vall>=0 && vall < 1 )
        return true;
    else
      	return false;
}

//检查输入值是否为>=0
function CheckPlus(val)
{
    if(!CheckNUM(val))
    {
        return false;
    }
    var vall = new Number(val);
    if(vall>=0 )
        return true;
    else
      	return false;
}

//检查输入值是否为<=0
function CheckNegative(val)
{
    if(!CheckNUM(val))
    {
        return false;
    }
    var vall = new Number(val);
    if(vall<=0 )
        return true;
    else
      	return false;
}

//检查输入值是否为>0
function CheckPositive(val)
{   val = Trim(val);
    if(!CheckNUM(val))
    {
        return false;
    }
    var vall = new Number(val);
    if(vall>0 )
        return true;
    else
      	return false;
}



//数字校验：输入值必须在“0123456789.,-”范围内，否则返回false，如果用户不输入任何值，返回true
function CheckNUM(NUM)
{
  var i,j,strTemp;
  strTemp="0123456789.,";
  if ( NUM.length == 0) return true;
  for (i=0;i<NUM.length;i++)
  {
      if(i==0 && NUM.length>1 && NUM.charAt(i)=="-")
      {

      }
      else
      {
          j = strTemp.indexOf(NUM.charAt(i));
          if (j==-1)
          {
              return false;
          }
      }
  }
  return true;
}

//指定页面区域内容导入Word
 function AllAreaWord(tableID)
 {
  var oWD = new ActiveXObject("Word.Application");
  var oDC = oWD.Documents.Add("",0,1);
  var oRange =oDC.Range(0,1);
  var sel = document.body.createTextRange();
  sel.moveToElementText(tableID);
  sel.select();
  sel.execCommand("Copy");
  oRange.Paste();
  oWD.Application.Visible = true;
  //window.close();
 }

 //指定页面区域内容导入Excel
 function AllAreaExcel(tableID)
 {
  var oXL = new ActiveXObject("Excel.Application");
  var oWB = oXL.Workbooks.Add();
  var oSheet = oWB.ActiveSheet;
  var sel=document.body.createTextRange();
  sel.moveToElementText(tableID);
  sel.select();
  sel.execCommand("Copy");
  oSheet.Paste();
  oXL.Visible = true;
 }

/* ======================================================================
 * <p>Title： 密码字符规则校验</p>
 * <p>Description: 密码必须包含a-z、A-Z、0-9和规定使用的特殊字符的任意组合</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: 北京中智网安科技发展有限公司</p>
 * @author 李振兴
 * @version 1.0
 * 修改人：李振兴
 * 修改日期:20060828
 * 修改目的:由于密码强度过强造成用户输入密码不方便的问题，现目的更改方法降低密码校验强度。
====================================================================== */

 /**
 * 辅助函数,配合checkRule(val)使用，检查非法字符。
 * @boolean 如果存在非法字符，那么返回true,否则返回false
 */
function isHaveBadChar(val)
{
   //非法字符库
    var BadChar = "&<>\"'" ;
	for(var i=0;i<val.length;i++)
	{
		if(BadChar.indexOf(val.substring(i,(i+1)))!=-1)
		{
			return true ;
		}
    }
    return false ;
}
 /**
 * 检查密码是否是a-z、A-Z、0-9和特殊字符的组合
 * @boolean 如果符合密码规则，那么返回true,否则返回false
 */
 /*
 function checkRule(val)
 {
   //所有英文大写字母
    var Capital = /[a-z]/ ;
   //所有英文小写字母
    var LowerCase = /[A-Z]/ ;
   //非A-Z、a-z、0-9的字符
    var SpecialCharacter = /[^A-Za-z0-9_]/ ;
	//检查是否有大写字母存在
	if(val.search(Capital)==-1)
	{
		return false ;
	}
	//检查是否有小写字母存在
	else if(val.search(LowerCase)==-1)
	{
		return false ;
	}
	//检查是否有特殊字符存在
	else if(val.search(SpecialCharacter)==-1)
	{
		return false ;
	}
	//检查是否有非法字符存在
	else if(isHaveBadChar(val))
	{
		return false ;
	}
	return true ;
 }
 */
 function checkRule(val)
 {
   //所有英文大小写字母
    var en = /[a-zA-Z]/ ;
   //数字0-9
    var num = /[0-9]/ ;
	//检查是否有大小写字母存在
	if(val.search(en)==-1)
	{
		return false ;
	}
	//检查是否有数字存在
	else if(val.search(num)==-1)
	{
		return false ;
	}
	//检查是否有非法字符存在
	else if(isHaveBadChar(val))
	{
		return false ;
	}
	return true ;
 }
 /**
 * 检查密码是否是a-z、A-Z、0-9的字符
 * @boolean 如果符合密码规则，那么返回true,否则返回false
 * 作者:李振兴
 * 创建日期:20060828
 */
 /*
  function checkRule(val)
  {
	var rulestr = /^[0-9a-z]*[0-9a-z]$/i ;
    if(rulestr.test(val))
    {
		return true ;
    }
	else
	{
		return false ;
	}
  }
 */
 /**
 * 比较字符串是否相同
 * @boolean 如果字符串相同，那么返回true,否则返回false
 */
function compare(val1,val2)
{
	if(val1==val2)
	{
		return true ;
	}
	return false ;
}


/**
* 检查MAC地址的正确性
* @param boolean 如果正确则返回true，否则返回false
* @作者 李振兴
* 创建日期:20060730
*/

function checkMAC(value)
{
var mac = /^([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}$/;
if(mac.test(value))
{
return true ;
}
else
{
return false;
}
}
/**
* 检查MAC地址
*/
function CheckNETMASK(value)
{
    var end = value.indexOf(".") ;
    var tmp = value.substring(0,end) ;
    if(tmp!=255)
    {
        return false ;
    }
    return true ;
 }
/**
*比较字符串替换
*作者:李振兴
*创建日期:20060913
*/
function replaceAll(str,value1,value2)
{
        while(str.indexOf(value1)!=-1)
        {
                str = str.replace(value1,value2) ;
        }
        return str ;
}
/**
*比较日期大小
*作者:李振兴
*创建日期:20060913
*/
function compareDate(date1,date2)
{
	var d1 = new Date(replaceAll(date1,"-","/")) ;
	var d2 = new Date(replaceAll(date2,"-","/")) ;
        if(d1<d2)
        {
                return true ;
        }
        return false ;
}
//删除时对用户进行确认,询问是否删除，调用ajax刷新内容显示区
function AjaxDeleteconfirm(url,parms,request)
{
   if(confirm("你确定要删除该记录吗?")!="0")
   {
	ajaxRequestHtml(url,parms,request) ;
   }
}