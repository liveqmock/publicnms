package com.afunms.common.base;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 基础工具类
 * @author  
 */
public class BaseUtil {
	private static final DecimalFormat num_df = new DecimalFormat("#.##");
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat YMD_CN = new SimpleDateFormat("yyyy年MM月dd日");
	public static final SimpleDateFormat HH = new SimpleDateFormat("HH");
    public static SimpleDateFormat Times = new SimpleDateFormat("HH:mm:ss");

    public static SimpleDateFormat Y2DTIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat Y2CH_FORMAT = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

    public static SimpleDateFormat Y2NO_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static SimpleDateFormat Y2D_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static SimpleDateFormat Y2T_FORMAT = new SimpleDateFormat("HHmmss");

    public static SimpleDateFormat YMD_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat Y2NO_Mill_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    
    public static final SimpleDateFormat Y2SPECIAL_FORMAT = new SimpleDateFormat("yyyy.MM.dd");
    
    public static void main(String[] args) {
    	try {
    		System.out.println(BaseUtil.diffMinute(BaseUtil.sdf.parse("2012-11-20 16:1:00"),BaseUtil.sdf.parse("2012-11-20 15:11:00")));
			System.out.println(BaseUtil.sdf.format(new Date()));
			System.out.println(new Date());
			
			SimpleDateFormat asdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			asdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			System.out.println(asdf.format(new Date()));
			System.out.println(getNowHour());
			
			System.out.println(sdf.format(BaseUtil.sdf.parse("2012-1-20 16:1:00")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
    /**
     * 保留两位小数(银行家舍入法:向最接近数字方向舍入的舍入模式，如果与两个相邻数字的距离相等，则向相邻的偶数舍入)
     */
    public static String getNumDf2(Double double1){
    	return num_df.format(double1);
    }
   /**
    * 取得当前时间的小时（北京时间）
    */
    public static Integer getNowHour(){
    	HH.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    	return Integer.parseInt(HH.format(new Date()));
    }
	public static String getSystemDateOfString(){
		Calendar calendar = Calendar.getInstance();		
		return sdf.format(calendar.getTime());
	}
	public static Date getSystemDate()
	{
		Calendar calendar = Calendar.getInstance();	
		return calendar.getTime();
	}
	public static Date getDate(String date)
	{
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			System.out.print("日期格式不正确!");
		}
		return getSystemDate();
	}
	/**
	 * 日期增加天数获得新的日期
	 * @param oldDate
	 * @param intDay
	 * @return
	 */
    public final static Date getDateAdd(Date oldDate, int intDay) {
        Calendar calendar = Calendar.getInstance();// 实例化calendar对象
        calendar.setTime(oldDate);// 设置calendar对象的时间属性

        calendar.add(Calendar.DATE, intDay);// 对天数进行增加

        return calendar.getTime();// 得到calendar对象的时间

    }
    /**
    * md5 加密字符串

    * @param String
    * @return String
     * @throws NoSuchAlgorithmException 
     * @throws NoSuchAlgorithmException 
    * @throws NoSuchAlgorithmException 
    */
    public static final String md5(String input){
       byte[] inputByte=input.getBytes();
       MessageDigest md = null;
	   try {
			md = MessageDigest.getInstance("md5");
	   } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   }
       md.update(inputByte);
       byte[] digest=md.digest();
       StringBuffer buf=new StringBuffer();
       for(int i=0;i<digest.length;i++){
        int val=((int)digest[i]) & 0xff;
        if(val < 16){
         buf.append("0");
        }
        buf.append(Integer.toHexString(val));
       }
       return buf.toString().toLowerCase();
    }
    /**
     * 将hql语句解析为sql语句
     * @param originalHql
     * @param sessionFactory
     * @return
     * @throws Exception
     */
    /*public static String getCountSql(String originalHql, org.hibernate.SessionFactory sessionFactory) throws Exception { 
        QueryTranslatorImpl queryTranslator = new QueryTranslatorImpl(originalHql, originalHql, 
           Collections.EMPTY_MAP, (org.hibernate.engine.SessionFactoryImplementor)sessionFactory); 

        queryTranslator.compile(Collections.EMPTY_MAP, false); 
        return queryTranslator.getSQLString(); 
    }*/

    /**
     * 创建正则表达式的方法，采用常用的形式
     * 
     * @author
     * @param strRegex
     *            通常习惯书写的正则表达式，如"^\\d+\\w."等，注意要用两条斜杠。

     * @param flagCase
     *            是否区分大小写。0 不区分，1 区分， 默认区分大小写。另外，此方法默认了多行匹配。

     */
    public final static Pattern createPattern(String strRegex, int flagCase) {
        Pattern patternRegex = null;
        if (flagCase == 0) {
            patternRegex = Pattern.compile(strRegex, Pattern.MULTILINE
                    | Pattern.CASE_INSENSITIVE);
        } else {
            patternRegex = Pattern.compile(strRegex, Pattern.MULTILINE);
        }
        return patternRegex;
    }

    /**
     * 使用正则表达式检查某字串是否匹配表达式的方法
     * 
     * @author 
     * @return 返回是否匹配正则表达式。

     * @param strBeChecked
     *            被检查的字符串。

     * @param strRegex
     *            传入的正则表达式字符串，如："\\(\\d{3}\\)\\s\\d{3}-\\d{4}";
     * @param flagCase
     *            是否区分大小写。0 不区分，1 区分， 默认区分大小写。 flags Match flags, a bit mask
     *            that may include {@link #CASE_INSENSITIVE},
     *            {@link #MULTILINE}, {@link #DOTALL}, {@link #UNICODE_CASE},
     *            and {@link #CANON_EQ}
     */
    public final static boolean checkMatchRegex(String strBeChecked,
            String strRegex, int flagCase) {
        Pattern patternRegex = createPattern(strRegex, flagCase);
        Matcher m = patternRegex.matcher(strBeChecked);
        return m.find();
    }

    /**
     * 使用正则表达式替换某字串中匹配表达式的部分的方法
     * 
     * @author
     * @return 返回替换后的字串。

     * @param strBeReplaced
     *            被检查的字符串。

     * @param strRegex
     *            传入的正则表达式字符串，如："\\(\\d{3}\\)\\s\\d{3}-\\d{4}";
     * @param flagCase
     *            是否区分大小写。0 不区分，1 区分， 默认区分大小写。

     * @param strChange
     *            替换的字串。

     */
    public final static String replaceMatchRegex(String strBeChecked,
            String strRegex, int flagCase, String strChange) {
        Pattern patternRegex = createPattern(strRegex, flagCase);
        Matcher m = patternRegex.matcher(strBeChecked);
        return m.replaceAll(strChange);
    }
    public final static String subMatchRegex(String strBeMatch,String strRegex,int falgCase)
    {
    	Pattern pattern = BaseUtil.createPattern(strRegex,falgCase);
		Matcher matcher = pattern.matcher(strBeMatch);
		StringBuffer buffer = new StringBuffer();
		while(matcher.find())
		{
			buffer.append(matcher.group());
		}
		return buffer.toString();
    }
    /**
     * 截取字符串中的子串

     * @param str         需要截取的字符串

     * @param startNum    截取字符串的起始位置
     *                    当startNum = -1时，表示倒序截取该字符串的最后cutNum位

     *                    当startNum > 0时，表示顺序截取该字符串前cutNum位(索引值从1开始)
     * @param cutNum      截取字符串的个数 
     * @return
     */
    public final static String subString(String str,int startNum,int cutNum){
    	if(startNum > 0){
    		startNum = startNum - 1;
    		if(startNum <= str.length() && str.length() <= (startNum+cutNum)){
        		return str.substring(startNum, str.length());
        	}
        	else if(str.length() > (startNum+cutNum)){
        		return str.substring(startNum, (startNum+cutNum));
        	}
        	else{
        		return "";
        	}
    	}
    	else{
    		startNum = str.length() - cutNum ;
    		if(startNum > 0){
    			return str.substring(startNum, (startNum+cutNum));
    		}
    		else{
    			return str.substring(0, str.length());
    		}
    	}
    }
    /**
     * 日期相减
     * @param date1 日期
     * @param date2 日期
     * @return 返回(date1-date2)相减后的日期
     */
     public static int diffDate(Date date, Date date1) {
    	 return (int) ((date.getTime() - date1.getTime()) / (24 * 3600 * 1000));
     }
     /**
      * 日期相减
      * @param date1 日期
      * @param date2 日期
      * @return 返回(date1-date2)相减后的小时
      */
      public static int diffHour(Date date, Date date1) {
     	 return (int) ((date.getTime() - date1.getTime()) / (3600 * 1000));
      }
      /**
       * 日期相减
       * @param date1 日期
       * @param date2 日期
       * @return 返回(date1-date2)相减后的分钟
       */
       public static int diffMinute(Date date, Date date1) {
      	 return (int) ((date.getTime() - date1.getTime()) / (60 * 1000));
       }

     public static String GenerateRandomStr() {
         String randStr = "ABCDEFGHIabcdef0123456789"; // 写入你所希望的所有的字母A-Z,a-z,0-9
         StringBuffer generateRandStr = new StringBuffer();
         Random rand = new Random();
         int randStrLength = 6; // 你想生成的随机数的长度

         for (int i = 0; i < randStrLength; i++) {
             int randNum = rand.nextInt(25);
             generateRandStr.append(randStr.substring(randNum, randNum + 1));
         }
         System.out.println(generateRandStr); // 打印你的结果
         return generateRandStr.toString();
     }
     
     /**
      * 取得当前日期增加的月数后月份的第一天

      * 比如，当前日期为2009-06-25日，得到增加一个月后日期是7月份的1号

      * Date date = BaseUtil.getMonthAdd(date,1);
      * @param date 当前日期
      * @param month 月数
      * @return
      */
     public static Date getMonthAdd(String date, int month)
     {
    	Calendar calendar = Calendar.getInstance();   
 		try {
			calendar.setTime(BaseUtil.YMD.parse(date));
		} catch (ParseException e) {
		}
 		calendar.set(Calendar.DAY_OF_MONTH, 1);
 		calendar.add(Calendar.MONTH, month);
 		
 		return calendar.getTime();
     }
     
     /**
      * 日期增加年份获取新的日期
      * 比如，当前日期为2009-12-23日，增加一年后为2010-12-23日

      * Date date = BaseUtil.getYearAdd(date,1);
      * @param date 当前日期
      * @param year 年份
      * @return
      */
     public static Date getYearAdd(Date date, int year)
     {
    	 Calendar calendar = Calendar.getInstance();// 实例化calendar对象
         calendar.setTime(date);// 设置calendar对象的时间属性

         calendar.add(Calendar.YEAR, year);// 对天数进行增加

         return calendar.getTime();// 得到calendar对象的时间

     }
     
     /**
      * 取得当前月份属于第几季度和季度包含的开始月份和结束月份
      * @param month
      * @return
      */
     static public String[] getQuarterOfMonth(String month)
     {
     	String [] ret = new String[2];
     	
     	String firstQuarter = "01,02,03";
     	String secondQuarter = "04,05,06";
     	String thirdQuarter = "07,08,09";
     	String fourthQuarter = "10,11,12";
     	
     	if(firstQuarter.indexOf(month) >= 0)
     	{
     		ret = new String[]{"01","03","第一季度"};
     	}
     	if(secondQuarter.indexOf(month) >= 0)
     	{
     		ret = new String[]{"04","06","第二季度"};
     	}
     	if(thirdQuarter.indexOf(month) >= 0)
     	{
     		ret = new String[]{"07","09","第三季度"};
     	}
     	if(fourthQuarter.indexOf(month) >= 0)
     	{
     		ret = new String[]{"10","12","第四季度"};
     	}
     	
     	return ret;
     }
     /**
      * 获取指定日期是星期几
      * @param date
      * @return 
      */
     static public String getDayOfWeek(Date date)
     {
    	 String[] week = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
    	 Calendar calendar = Calendar.getInstance();   
    	 calendar.setTime(date);
    	 return week[calendar.get(Calendar.DAY_OF_WEEK)-1];
     }
     
     /**
      * 得到两个日期之间天数
      * @param startDate
      * @param endDate
      * @return
      * @throws ParseException 
      */
     public static int getDays(String startDate, String endDate) throws ParseException
     {
     	Calendar calendar1 = Calendar.getInstance();
     	calendar1.setTime(YMD_FORMAT.parse(startDate));
     	Calendar calendar2 = Calendar.getInstance();
     	calendar2.setTime(YMD_FORMAT.parse(endDate));
     	
     	int days = calendar1.get(Calendar.DAY_OF_YEAR) - calendar2.get(Calendar.DAY_OF_YEAR) + 1;
     	
     	return days;
     }
     
     /** 
      *添加计算两日期间的天数(包括头尾日期)
      * 要求输入格式为实例如: 2007-09-25
      * 如果格式输入有误将反回空字符串

      */
     public static String countDaysBetweenTwoData(String stratDataStr, String endDataStr) {
     	String countDays = "";
     	
     	String t1 = stratDataStr.replace('-','/'); 
         String t2 = endDataStr.replace('-','/');
         
         try{ 
             Date dt1= new Date(t1); 
             Date dt2= new Date(t2);  
             long l = dt1.getTime() - dt2.getTime(); 
       
             long countDay = l/60/60/1000/24; 
             countDays = String.valueOf(countDay+1);
         }catch(Exception e){ 
             return ""; 
         }
     	return countDays;
     }
     /**
      * 得到两个日期之间天数(此格式：yyyy-MM-dd)，不带时分秒
      * @param startDate
      * @param endDate
      * @return
      */
     public static int getDays2(String startDate, String endDate) throws Exception
     {
     	return (int) ((YMD_FORMAT.parse(endDate).getTime() - YMD_FORMAT.parse(startDate).getTime()) / (24 * 3600 * 1000));
     }
     
     /**
      * Md5 加密算法
      * @param source 明文
      * @return 密文
      */ 
     public static String getMD5(String source) {
    	byte[] sourceByte = source.getBytes() ; 
 		String s = null;
 		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符

 		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
 				'e', 'f' };
 		try {
 			java.security.MessageDigest md = java.security.MessageDigest
 					.getInstance("MD5");
 			md.update(sourceByte);
 			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
 			// 用字节表示就是 16 个字节

 			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
 			// 所以表示成 16 进制需要 32 个字符

 			int k = 0; // 表示转换结果中对应的字符位置
 			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节

 				// 转换成 16 进制字符的转换

 				byte byte0 = tmp[i]; // 取第 i 个字节

 				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, 
 				// >>> 为逻辑右移，将符号位一起右移

 				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
 			}
 			s = new String(str); // 换后的结果转换为字符串


 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return s.toUpperCase();
 	}

     /**
      * 得到两个日期之间的月份

      * @param startDate
      * @param endDate
      * @return
      * @throws ParseException 
      */
     public static int getMonths(String startDate, String endDate) throws ParseException
     {
     	Calendar calendar1 = Calendar.getInstance();
     	calendar1.setTime(YMD_FORMAT.parse(startDate));
     	Calendar calendar2 = Calendar.getInstance();
     	calendar2.setTime(YMD_FORMAT.parse(endDate));
     	
     	int months = calendar1.get(Calendar.MONTH) - calendar2.get(Calendar.MONTH) + 1;
     	return months;
     }
     
     /**
      * 得到两个日期之间的月份差 字符串格式为：yyyy-mm-dd
      * @param startDate 大日期

      * @param endDate  小日期

      * @return
      * @throws ParseException
      */
     public static int getMonths2(String startDate, String endDate) throws ParseException
     {
    	 Calendar calendar1 = Calendar.getInstance();
    	 calendar1.setTime(YMD_FORMAT.parse(startDate));
    	 calendar1.add(Calendar.DATE, 1);
    	 Calendar calendar2 = Calendar.getInstance();
    	 calendar2.setTime(YMD_FORMAT.parse(endDate));
    	 
    	 int years = calendar1.get(Calendar.YEAR) - calendar2.get(Calendar.YEAR);
    	 int months = calendar1.get(Calendar.MONTH) - calendar2.get(Calendar.MONTH);
    	 if(calendar1.get(Calendar.DAY_OF_YEAR) < calendar2.get(Calendar.DAY_OF_YEAR))
    	 {
    		 return years * 12 + months - 1;
    	 }
    	 else
    	 {
    		 return years * 12 + months;
    	 }
    	 
     }

     /**
      * 判断date1在date2之前
      * @param date1
      * @param date2
      * @return
      * @throws ParseException
      */
     public static boolean beforDate(String date1, String date2) throws ParseException
     {
     	Calendar calendar1 = Calendar.getInstance();
     	calendar1.setTime(sdf.parse(date1));
     	Calendar calendar2 = Calendar.getInstance();
     	calendar2.setTime(sdf.parse(date2));
     	
     	return calendar1.before(calendar2);
     }
     /**
      * 判断date1在date2之后
      * @param date1
      * @param date2
      * @return
      * @throws ParseException
      */
     public static boolean afterDate(String date1, String date2) throws ParseException
     {
     	Calendar calendar1 = Calendar.getInstance();
     	calendar1.setTime(sdf.parse(date1));
     	Calendar calendar2 = Calendar.getInstance();
     	calendar2.setTime(sdf.parse(date2));
     	
     	return calendar1.after(calendar2);
     }
     /**
      * 判断date1在date2之后
      * @param date1
      * @param date2
      * @return
      * @throws ParseException
      */
     public static boolean afterDate(Date date1, Date date2) throws ParseException
     {
     	Calendar calendar1 = Calendar.getInstance();
     	calendar1.setTime(date1);
     	Calendar calendar2 = Calendar.getInstance();
     	calendar2.setTime(date2);
     	
     	return calendar1.after(calendar2);
     }
     
     /**
      * 将String[]转为以某个分隔的字符，如“,”串起来的字符串。

      * 
      * @param strArrayIn
      *            传入的字符数组。

      * @param strSeparator
      *            分隔的字符。

      */
     public static String parseArrayToString(String[] strArrayIn,
             String strSeparator) {
     	if (strArrayIn == null) {
     		return "";
     	}
         StringBuffer sbArray = new StringBuffer();
         for (int i = 0; i < strArrayIn.length; i++) {
             sbArray.append(strArrayIn[i]);
             sbArray.append(strSeparator);
         }
         int iEnd = sbArray.length() - strSeparator.length();
         return sbArray.substring(0, iEnd);
     }

     private static final int DEF_DIV_SCALE = 10;

     /**
      * 
      * 提供精确的加法运算。

      * 
      * @param v1
      *            被加数

      * 
      * @param v2
      *            加数
      * 
      * @return 两个参数的和
      * 
      */

     public static double add(double v1, double v2) {

         BigDecimal b1 = new BigDecimal(Double.toString(v1));

         BigDecimal b2 = new BigDecimal(Double.toString(v2));

         return b1.add(b2).doubleValue();

     }
     
     public static String add(String v1, String v2) {

         BigDecimal b1 = new BigDecimal(v1);

         BigDecimal b2 = new BigDecimal(v2);

         return b1.add(b2).toString();

     }

     /**
      * 
      * 提供精确的减法运算。

      * 
      * @param v1
      *            被减数

      * 
      * @param v2
      *            减数
      * 
      * @return 两个参数的差
      * 
      */

     public static double sub(double v1, double v2) {

         BigDecimal b1 = new BigDecimal(Double.toString(v1));

         BigDecimal b2 = new BigDecimal(Double.toString(v2));

         return b1.subtract(b2).doubleValue();

     }
     
     public static String sub(String v1, String v2) {

         BigDecimal b1 = new BigDecimal(v1);

         BigDecimal b2 = new BigDecimal(v2);

         return b1.subtract(b2).toString();

     }

     /**
      * 
      * 提供精确的乘法运算。

      * 
      * @param v1
      *            被乘数

      * 
      * @param v2
      *            乘数
      * 
      * @return 两个参数的积
      * 
      */

     public static double mul(double v1, double v2) {

         BigDecimal b1 = new BigDecimal(Double.toString(v1));

         BigDecimal b2 = new BigDecimal(Double.toString(v2));

         return b1.multiply(b2).doubleValue();

     }
     
     public static String mul(String v1, String v2) {

         BigDecimal b1 = new BigDecimal(v1);

         BigDecimal b2 = new BigDecimal(v2);

         return b1.multiply(b2).toString();

     }

     /**
      * 
      * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
      * 
      * 小数点以后10位，以后的数字四舍五入。

      * 
      * @param v1
      *            被除数

      * 
      * @param v2
      *            除数
      * 
      * @return 两个参数的商
      * 
      */

     public static double div(double v1, double v2) {

         return div(v1, v2, DEF_DIV_SCALE);

     }

     /**
      * 
      * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指

      * 
      * 定精度，以后的数字四舍五入。

      * 
      * @param v1
      *            被除数

      * 
      * @param v2
      *            除数
      * 
      * @param scale
      *            表示表示需要精确到小数点以后几位。

      * 
      * @return 两个参数的商
      * 
      */

     public static double div(double v1, double v2, int scale) {

         if (scale < 0) {

             throw new IllegalArgumentException(
                     "The scale must be a positive integer or zero");

         }

         BigDecimal b1 = new BigDecimal(Double.toString(v1));

         BigDecimal b2 = new BigDecimal(Double.toString(v2));

         return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

     }
     
     public static String div(String v1, String v2, int scale) {

         if (scale < 0) {

             throw new IllegalArgumentException(
                     "The scale must be a positive integer or zero");

         }

         BigDecimal b1 = new BigDecimal(v1);

         BigDecimal b2 = new BigDecimal(v2);

         return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString();

     }

     /**
      * 
      * 提供精确的小数位四舍五入处理。

      * 
      * @param v
      *            需要四舍五入的数字
      * 
      * @param scale
      *            小数点后保留几位
      * 
      * @return 四舍五入后的结果
      * 
      */

     public static double round(double v, int scale) {

         if (scale < 0) {

             throw new IllegalArgumentException(
                     "The scale must be a positive integer or zero");

         }

         BigDecimal b = new BigDecimal(Double.toString(v));

         BigDecimal one = new BigDecimal("1");

         return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

     }
     /**
      * 将数字格式化输出字符串

      * @param formatStr 格式串

      * @param num
      * @return
      */
     public final static String formatString(String formatStr, int num) {
         DecimalFormat df = new DecimalFormat(formatStr);
         return df.format(num);
     }
     /**
      * 将double格式化输出字符串
      * @param formatStr 格式串 如1.234 格式化为"#0.00" 
      * @param num
      * @return
      */
     public final static String formatString(String formatStr, double decimal) {
    	 NumberFormat format = new DecimalFormat(formatStr);
         return format.format(decimal);
     }
     /**
      * 实现decode函数的功能

      * @param str(条件,值1,翻译值1,值2,翻译值2,...值n,翻译值n,缺省值)
      * @return
      */
     public final static String decode(String str){
    	 String[] strGroup = str.split(",");
    	 String strTerm = strGroup[0];
    	 if(strGroup.length % 2 != 0){
    		 return "str格式不正确，请验证";
    	 }
    	 for(int i=1;i<=strGroup.length-3;i+=2){
    		 if(strTerm.equals(strGroup[i])){
    			 return strGroup[i+1];
    		 }
    	 }
    	 return strGroup[strGroup.length-1];  //返回缺省值

     }
    /**
     * @author HANS
     * @param currentDate 指定日期
     * @return 返回指定日期月份的最后一天的日期
     */
    @SuppressWarnings("deprecation")
	public final static Date lastDayOfMonth(Date currentDate) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(currentDate);
    	final int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	Date lastDate = calendar.getTime();
    	lastDate.setDate(lastDay);
    	return lastDate;
    }
    /**
     * @author HANS
     * @param currentDate 指定日期
     * @return 返回指定日期年份的最后一天的日期
     */
    public final static Date lastDayOfYear(Date currentDate) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(currentDate);
    	calendar.set(Calendar.MONTH, 11);
    	calendar.set(Calendar.DAY_OF_MONTH, 31);
    	return calendar.getTime();
    }
    
    /**
     * 将一个字符串统一格式化

     * @author MJW
     * @param time
     * @return
     * @throws ParseException
     */
    public final static Date formatCSTDateString(String time) throws ParseException{
		Date dt = null;		
		SimpleDateFormat sdfCST = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.ENGLISH);
		SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy-MM-dd");
		if (time.toUpperCase().indexOf("CST") !=-1){
			Date date = sdfCST.parse(time);				 
			String ctime = sdfYYYY.format(date); 		
			dt = sdfYYYY.parse(ctime);
		}else {
			dt = sdfYYYY.parse(time);
		}
		return dt; 
	}
    /**
     * 日期加月
     * @param date
     * @param month
     * @return
     */
    public static Date getMonthAddInt(String date, int month)
    {
    	Calendar calendar = Calendar.getInstance();   
		try {
			calendar.setTime(BaseUtil.YMD.parse(date));
		} catch (ParseException e) {
		}
		calendar.add(Calendar.MONTH, month);
		
		return calendar.getTime();
    }
    
    /**
     * 根据出生日期计算年龄
     * @param birthDay
     * @return
     * @throws Exception
     */
    public static int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                "The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                //monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                } else {
                    //do nothing
                }
            } else {
                //monthNow>monthBirth
                age--;
            }
        } else {
            //monthNow<monthBirth
            //donothing
        }

        return age;
    }
    /**
	* 判断是否为空
	* @param str
	* @return
	*/
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 检查日期格式
	 * @param param
	 * @return
	 */
	public static boolean checkDateYYYYMMDD(String param) {
		boolean bool = true;
		if (param == null || "".equals(param.trim())) {
			return false;
		}
		try {
			YMD_FORMAT.parse(param);
		} catch (ParseException e) {
			bool = false;
		}
		return bool;
	}
}
