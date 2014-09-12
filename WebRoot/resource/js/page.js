  var curpage=1;
  var totalpages = 1;
  var alertInfo = "";
  var delAction = "";
  var listAction = "";
  var gotoAction = "";
  if(listAction=="") listAction = "list.jsp";
 if(gotoAction=="") gotoAction = "list.jsp";
  if(alertInfo=="") alertInfo = "\u786e\u5b9e\u8981\u5220\u9664\u8fd9\u4e9b\u8bb0\u5f55\u5417?";

  function setPage()
  {
    if(curpage==1)
    {
       mainForm.fp.disabled = true;
       mainForm.pp.disabled = true;
    }
    if(curpage==totalpages)
    {
       mainForm.np.disabled = true;
       mainForm.lp.disabled = true;
    }
  }
  
  function firstPage()
  {
    var a = curpage;
    if(a == 1)
      return;

    mainForm.jp.value = 1;
    mainForm.action = listAction;
    mainForm.submit();
  }


  function precedePage()
  {
    var a = curpage;
    a = a - 1;
    if(a < 1)
      return;

    mainForm.jp.value = a;
    mainForm.action = listAction;
    mainForm.submit();
  }

  function nextPage()
  {
    var a = curpage;
    var b = totalpages;
    a = a - 1 + 2;
    if(a > b)
      return;
    mainForm.jp.value = a;
    mainForm.action = listAction;
    mainForm.submit();
  }

  function lastPage()
  {
    var a = curpage;
    var b = totalpages;

    if(a >= b)
      return;

    mainForm.jp.value = b;
    mainForm.action = listAction;
    mainForm.submit();
  }

  function goto_page()
  {
    if ( mainForm.jp.value == "" )
       return;
    if ( isNaN(mainForm.jp.value) )
    {
       mainForm.jp.value = ""
       return;
    }

    var a = totalpages;
    var b = mainForm.jp.value;
    if( b == "" || b < 1 || b > a )
    {
      mainForm.jp.value = "";
      mainForm.jp.focus();
      return;
    }
    else
    {
      mainForm.action = listAction;
      mainForm.submit();
    }
  }

  function toDelete()
  {
     var bExist = false;

     if ( mainForm.checkbox.length == null )
     {
        if( mainForm.checkbox.checked )
           bExist = true;
     }
     else
     {
        for( var i=0; i < mainForm.checkbox.length; i++ )
        {
           if(mainForm.checkbox[i].checked)
           {
              bExist = true;
              break;
           }
        }
     }

     if(bExist)
     {
       if (window.confirm(alertInfo))
       {
          mainForm.action = delAction;
          mainForm.submit();
       }
     }
     else
     {
        alert("\u8bf7\u9009\u62e9\u8981\u5220\u9664\u7684\u8bb0\u5f55");
        return false;
     }
  }

  function toDelete2()
  {
    var bExist = false;

    if ( mainForm.radio.length == null ) 
    {
      if( mainForm.radio.checked )  
         bExist = true;
    }
    else  
    {
       for( var i=0; i < mainForm.radio.length; i++ )
       {
         if(mainForm.radio[i].checked)
            bExist = true;
       }
    }

    if(bExist)
    {
      if (window.confirm(alertInfo))
      {
         mainForm.action = delAction;
         mainForm.submit();
      }
    }
    else
    {
       alert("\u8bf7\u9009\u62e9\u8981\u5220\u9664\u7684\u8bb0\u5f55");
       return false;
    }
  }

  function chkall()
  {
     if ( mainForm.checkbox.length == null )
     {
        if( mainForm.checkall.checked )
           mainForm.checkbox.checked = true;
        else
           mainForm.checkbox.checked = false;
     }
     else
     {
        if(mainForm.checkall.checked)
        {
           for( var i=0; i < mainForm.checkbox.length; i++ )
              mainForm.checkbox[i].checked = true;
        }
        else
        {
           for( var i=0; i < mainForm.checkbox.length; i++ )
              mainForm.checkbox[i].checked = false;
        }
     }
  }


function gotoPerPage(perpagenum)
  {
		var iperpage = perpagenum;
		if(iperpage==50){
			mainForm.jp.value = 1;
			mainForm.perpagenum.value = 50;
			mainForm.action = listAction;
			mainForm.submit();
		}else if(iperpage==100){
			mainForm.jp.value = 1;
			mainForm.perpagenum.value = 100;
			mainForm.action = listAction;
			mainForm.submit();
		}else if(iperpage==200){
			mainForm.jp.value = 1;
			mainForm.perpagenum.value = 200;
			mainForm.action = listAction;
			mainForm.submit();
		}else if(iperpage==30){
			mainForm.jp.value = 1;
			mainForm.perpagenum.value = 30;
			mainForm.action = listAction;
			mainForm.submit();
		}else if(iperpage==80){
			mainForm.jp.value = 1;
			mainForm.perpagenum.value = 80;
			mainForm.action = listAction;
			mainForm.submit();
		}
  }
  
  	/***********************************************************************************************************
	回显下拉选框
	参数：
		objId    下拉框的ID
		strValue 要选中的option的值,即option与此值相等时选中
	***********************************************************************************************************/
	function setSelectItem(objId,strValue){
		var options = document.getElementById(objId);
	    var option=options.getElementsByTagName("option");  
	    var str = "" ;  
	    for(var i=0;i<option.length;++i){ 
	    	if(option[i].value == strValue){  
	    		option[i].selected = true; 
	    		//alert('aaa'); 
	    	}  
	    }
	}