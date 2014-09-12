
function LTrim(str)
{
   var rtnStr;
   rtnStr=""
   for (var i=0;i<str.length;i++)
   {
      if (str.charAt(i)!=" ")
      {
         rtnStr=str.substr(i);
         break;
      }
   }
   return rtnStr;
}

function RTrim(str)
{
   var rtnStr;
   rtnStr=""
   for (var i=str.length-1;i>=0;i--)
   {
      if (str.charAt(i)!=" ")
      {
	rtnStr=str.substring(0,i+1);
	break;
      }
   }
   return rtnStr;
}

function Trim(str)
{
  return(LTrim(RTrim(str)));
}

function formatString(str)
{
   var str2 = "";
   var onechar = "";
   var charcode = "";
   str = Trim(str);
   for(i=0;i<str.length;i++)
   {
     onechar = str.charAt(i);
     charcode = str.charCodeAt(i);
     if(charcode==39)
     {
       onechar = "_";
     }
     str2 = str2 + onechar;
   }
   return str2;
}

function getLength(str)
{
 var i,rt=0;
 for(i=0;i<str.length;i++)
 {
   rt++;
   if(str.charCodeAt(i)>256)
      rt++;
 }
 return rt;
}

  //2005-07-10????
  //htmlname:??????????
  //type:??????????????
  //note:????????????
  //maxlen:??????????????
  //isnull:????????????
  function checkinput(htmlname,type,note,maxlen,isnull)
  {
     var txtObject = eval("mainForm." + htmlname);
     var inputStr = Trim(formatString(txtObject.value)); 

     if(!isnull&&inputStr=="")  //????????????
     {
        alert("<" + note + ">\u4e0d\u80fd\u4e3a\u7a7a!"); 
        txtObject.focus();
        return false;
     }

     if(isnull&&inputStr=="") return true;

     var len = getLength(inputStr);
     if(type=="string")  //????????????
     {
        if(len>maxlen)      //????????
        {
	    alert("<" + note + ">\u8f93\u5165\u8fc7\u957f!");
	    txtObject.focus();
	    return false;
        }
     }
     else if(type=="number")  //??????????
     {
        if(isNaN(inputStr))
	    {
	       alert("<" + note + ">\u5fc5\u987b\u662f\u6570\u5b57!");
	       txtObject.focus();
	       return false;
	    }
        else if(len>maxlen)      //????????
        {
	       alert("<" + note + ">\u8f93\u5165\u8fc7\u957f!");
	       txtObject.focus();
	       return false;
        }
     }
     else if(type=="ip")   //????ip
     {
        var nn = 0;
        if(len>15||len<7)      //????????
        {
           alert("<" + note + ">\u8f93\u5165\u9519\u8bef,\u8bf7\u6b63\u786e\u8f93\u5165IP\u5730\u5740!");
           txtObject.focus();
           return false;
        }
        for(i=0;i<len;i++)
        {
           onechar = inputStr.charAt(i);
           if(isNaN(onechar)&&onechar!='.')
           {
              alert("<" + note + ">\u8f93\u5165\u9519\u8bef,\u8bf7\u6b63\u786e\u8f93\u5165IP\u5730\u5740!");
              txtObject.focus();
              return false;
	       }
           if(onechar=='.')
              nn++;
        }
        if(nn!=3)
        {
            alert("<" + note + ">\u8f93\u5165\u9519\u8bef,\u8bf7\u6b63\u786e\u8f93\u5165IP\u5730\u5740!");
            txtObject.focus();
            return false;
        }
     }
     else if(type=="email")  //??????email
     {
        if(inputStr.indexOf("@")==-1||inputStr.indexOf(".")==-1||inputStr.length<6)
	 {
            alert("<Email>????????,??????????Email!");
            txtObject.focus();
            return false;
	 }
     }
     else if(type=="mobile")  //??????????
     {
        if(isNaN(inputStr)||len!=11)
	 {
            alert("<??????>????????,????????????????!");
            txtObject.focus();
            return false;
	 }
     }
     txtObject.value = inputStr;
     return true;
  }
