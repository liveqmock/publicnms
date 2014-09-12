  var REQUIRED="required";//title为此值时，验证必填。
  var DIGITAL="digital";//title为此值时，验证必须为数字。
  var REQUIRED_DIGITAL="required_digital";//title为此值时，验证必须为数字。
  var ZERO_DIGITAL="zerodigital";//title为此值时，验证必须大于等于0的数字。
  var REQUIRED_ZERO_DIGITAL="required_zerodigital";//title为此值时，验证必须大于等于0的数字。
  var NEGATIVE_DIGITAL="negativedigital";//title为此值时，验证必须负数字。
  var REQUIRED_NEGATIVE_DIGITAL="negativedigital";//title为此值时，验证必须负数字。
  var POSITIVE="positive";//title为此值时，验证必须为正整数。
  var REQUIRED_POSITIVE="required_positive";//title为此值时，验证必须为正整数。
  var NATURAL_NUMBER="naturalnumber";//title为此值时，验证必须为自然数。
  var REQUIRED_NATURAL_NUMBER="required_naturalnumber";//title为此值时，验证必须为自然数。
  var EMAIL="required_email";//title为此值时，验证必须为自然数。
  var REQUIRED_EMAIL="required_email";//title为此值时，验证必须为自然数。
  
  
  
  
  var REQUIRED_MESSAGE="不能为空";//必填项为空时的提示信息。
  var DIGITAL_MESSAGE="必须为数字";//数字验证提示信息。
  var ZERO_DIGITAL_MESSAGE="必须为大于或等于0的数字";//数字验证提示信息。
  var NEGATIVE_DIGITAL_MESSAGE="必须为小于0的数字";//数字验证提示信息。
  var POSITIVE_MESSAGE="必须为正整数";//数字验证提示信息。
  var NATURAL_NUMBER_MESSAGE="必须为自然数";//数字验证提示信息。
  var EMAIL_MESSAGE="邮箱地址格式不正确";//数字验证提示信息。
  
  
  
  
  var VALIDATA_FLAG = true;//验证结果
  var WARN_NAME = "warntitle";//提示信息span的name属性
  function checkdata() {
  //校验开始，先清除所有SPAN提示框
  $("#mainForm span").each(function(){
             //取消SPAN提示框
             if(WARN_NAME==this.title) {
                      $(this).remove();
             }
      
 
  });
  //校验开始清除VALIDATA_FLAG标志
  VALIDATA_FLAG = true;
  
  $("#mainForm input").each(function(){
             //验证必填INPUT  
             if(REQUIRED==this.title) {
                       if(!checkNull(this)){
                          VALIDATA_FLAG = false;
                       } 
             }
             //验证数字格式
            else if(DIGITAL==this.title) {
                       if(!checkDigital(this)){
                          VALIDATA_FLAG = false;
                       } 
             }
             //验证大于等于0数字格式
            else if(ZERO_DIGITAL==this.title) {
                       if(!checkZeroDigital(this)){
                          VALIDATA_FLAG = false;
                       } 
             }
             //验证负数字格式
            else if(NEGATIVE_DIGITAL==this.title) {
                       if(!checkNegativeDigital(this)){
                          VALIDATA_FLAG = false;
                       } 
             }
              //验证正整数
            else if(POSITIVE==this.title) {
                       if(!checkPositive(this)){
                          VALIDATA_FLAG = false;
                       } 
             }
              //验证大于等于0的整数
            else if(NATURAL_NUMBER==this.title) {
                       if(!checkNaturalNumber(this)){
                          VALIDATA_FLAG = false;
                       } 
             }
             
             //验证大于等于0的整数
            else if(EMAIL==this.title) {
                       if(!checkEmail(this)){
                          VALIDATA_FLAG = false;
                       } 
             }
             
             
             
             
             
             
             
              //验证数字格式
            else if(REQUIRED_DIGITAL==this.title) {
                     if(checkNull(this)) {
                          if(!checkDigital(this)){
                          VALIDATA_FLAG = false;
                       } 
                     }else {
                         VALIDATA_FLAG = false;
                     }
                       
             }
             //验证大于等于0数字格式
            else if(REQUIRED_ZERO_DIGITAL==this.title) {
                      if(checkNull(this)) {
                              if(!checkZeroDigital(this)){
                          VALIDATA_FLAG = false;
                       }  
                     }else {
                        VALIDATA_FLAG = false;
                     }
                      
             }
             //验证负数字格式
            else if(REQUIRED_NEGATIVE_DIGITAL==this.title) {
                     if(checkNull(this)) {
                           if(!checkNegativeDigital(this)){
                          VALIDATA_FLAG = false;
                       } 
                     }else {
                         VALIDATA_FLAG = false;
                     }
                      
             }
              //验证正整数
            else if(REQUIRED_POSITIVE==this.title) {
                      if(checkNull(this)) {
                          if(!checkPositive(this)){
                          VALIDATA_FLAG = false;
                       } 
                     }else {
                         VALIDATA_FLAG = false;
                     }
                       
             }
              //验证大于等于0的整数
            else if(REQUIRED_NATURAL_NUMBER==this.title) {
                     if(checkNull(this)) {
                        if(!checkNaturalNumber(this)){
                          VALIDATA_FLAG = false;
                       } 
                     }else {
                        VALIDATA_FLAG = false;
                     }
                       
             }
             
             //验证大于等于0的整数
            else if(REQUIRED_EMAIL==this.title) {
                     if(checkNull(this)) {
                        if(!checkEmail(this)){
                          VALIDATA_FLAG = false;
                       } 
                     }else {
                         VALIDATA_FLAG = false;
                     }
                       
                   }
      
 
  });
  
  $("#mainForm textarea").each(function(){
             //验证必填textarea 
             if(REQUIRED==this.title) {
                       if(!checkNull(this)){
                          VALIDATA_FLAG = false;
                       } 
             }
      
 
  });
  
  $("#mainForm select").each(function(){
             //验证必填textarea 
             if(REQUIRED==this.title) {
                       if(!checkNull(this)){
                          VALIDATA_FLAG = false;
                       } 
             }
      
 
  });
  return VALIDATA_FLAG;
  
  }
  /**
  *验证不能为空
  *为空返回flase
  **/
  function checkNull(obj) {
     var $obj = $(obj);
     if($.trim($obj.val())=="") {
        $obj.after("<span class='red' title='"+WARN_NAME+"'>"+REQUIRED_MESSAGE+"</span>");
        return false;
     }
     return true;
  }
  /**
  *验证是否为数字
  *不为数字时返回flase
  **/
  function checkDigital(obj) {
      var $obj = $(obj);
      if($.trim($obj.val())==""){return true;}
      var reg = /^(-)?(0|[1-9]\d*)$|^(-)?(0|[1-9]\d*)\.(\d+)$/;
      var flag =reg.test($.trim($obj.val()));
      if(!flag) {
          $obj.after("<span class='red' title='"+WARN_NAME+"'>"+DIGITAL_MESSAGE+"</span>");
          return false;
      }
      return true; 
  }
  
  /**
  *验证是否为大于等于0的数字
  *不为数字时返回flase
  **/
  function checkZeroDigital(obj) {
      var $obj = $(obj);
      if($.trim($obj.val())==""){return true;}
      if($.trim($obj.val())==""){return true;}
      var reg = /^(0|[1-9]\d*)$|^(0|[1-9]\d*)\.(\d+)$/;
      var flag =reg.test($.trim($obj.val()));
      if(!flag) {
          $obj.after("<span class='red' title='"+WARN_NAME+"'>"+ZERO_DIGITAL_MESSAGE+"</span>");
          return false;
      }
      return true; 
  }
  
   /**
  *验证是否为小于0的数字
  *不为数字时返回flase
  **/
  function checkNegativeDigital(obj) {
      var $obj = $(obj);
      if($.trim($obj.val())==""){return true;}
      var reg = /^(-)(0|[1-9]\d*)$|^(-)(0|[1-9]\d*)\.(\d+)$/;
      var flag =reg.test($.trim($obj.val()));
      if(!flag) {
          $obj.after("<span class='red' title='"+WARN_NAME+"'>"+NEGATIVE_DIGITAL_MESSAGE+"</span>");
          return false;
      }
      return true; 
  }
  /**
  *是否为自然数
  *不满足条件返回flase
  **/
  function checkNaturalNumber(obj) {
     var $obj = $(obj);
     if($.trim($obj.val())==""){return true;}
     var reg =/^\d+$/;
     var flag = reg.test($.trim($obj.val()));  
     if(!flag) {
        $obj.after("<span class='red' title='"+WARN_NAME+"'>"+NATURAL_NUMBER_MESSAGE+"</span>");
          return false;
     }
     return true;
  }
  
  
  /**
  *验证是否为正整数
  *不为正整数时返回flase
  **/
  function checkPositive(obj) {
     var $obj = $(obj);
     if($.trim($obj.val())==""){return true;}
     var reg = /^[1-9]+[0-9]*$/;
     var flag = reg.test($.trim($obj.val()));  
     if(!flag) {
        $obj.after("<span class='red' title='"+WARN_NAME+"'>"+POSITIVE_MESSAGE+"</span>");
          return false;
     }
     return true;
  }
  
   /**
  *验证是否为邮箱地址
  *不为满足时返回flase
  **/
  function checkEmail(obj) {
     var $obj = $(obj);
     if($.trim($obj.val())==""){return true;}
     var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
     var flag = reg.test($.trim($obj.val()));  
     if(!flag) {
        $obj.after("<span class='red' title='"+WARN_NAME+"'>"+EMAIL_MESSAGE+"</span>");
          return false;
     }
     return true;
  }
  
  function banjie() {
     //校验开始，先清除所有SPAN提示框
  $("#mainForm span").each(function(){
             //取消SPAN提示框
             if(WARN_NAME==this.title) {
                      $(this).remove();
             }
      
 
  });
  try {
     var $obj = $("#ordersolution");
     if($.trim($obj.val())=="") {
        $obj.after("<span class='red' title='"+WARN_NAME+"'>"+REQUIRED_MESSAGE+"</span>");
        return false;
     }
     
     return true;
  }catch(ex) {
  }
  }
  
  
  