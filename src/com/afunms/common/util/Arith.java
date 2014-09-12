/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

/**
 * Title:        电子商务平台
 * Description:
 * 字符串组件： 替换/格式判断
 * Copyright:    Copyright (c) 2001
 * Company:      中泰信能
 * @author
 * @version 1.0
 */


import java.math.BigDecimal;
import java.math.BigInteger;

/**

* 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精

* 确的浮点数运算，包括加减乘除和四舍五入。

*/

public class Arith{
    //默认除法运算精度

    private static final int DEF_DIV_SCALE = 2;
    //这个类不能实例化

    private Arith(){

    }
    /**

    * 提供精确的加法运算。

    * @param v1 被加数

    * @param v2 加数

    * @return 两个参数的和

    */

    public static double add(double v1,double v2){

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.add(b2).doubleValue();

    }



    /**

    * 提供精确的减法运算。

    * @param v1 被减数

    * @param v2 减数

    * @return 两个参数的差

    */

    public static double sub(double v1,double v2){

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.subtract(b2).doubleValue();

    }



    /**

    * 提供精确的乘法运算。

    * @param v1 被乘数

    * @param v2 乘数

    * @return 两个参数的积

    */

    public static double mul(double v1,double v2){

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.multiply(b2).doubleValue();

    }



    /**

    * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到

    * 小数点以后10位，以后的数字四舍五入。

    * @param v1 被除数

    * @param v2 除数

    * @return 两个参数的商

    */

    public static double div(double v1,double v2){

        return div(v1,v2,DEF_DIV_SCALE);

    }
    
    /**

     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到

     * 小数点以后10位，以后的数字四舍五入。

     * @param v1 被除数

     * @param v2 除数

     * @return 两个参数的商

     */

     public static double divByInteger(BigInteger v1,BigInteger v2){
         return divAsBigDecimal(v1,v2,DEF_DIV_SCALE);

     }



    /**

    * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指

    * 定精度，以后的数字四舍五入。

    * @param v1 被除数

    * @param v2 除数

    * @param scale 表示表示需要精确到小数点以后几位。

    * @return 两个参数的商

    */

    public static double div(double v1,double v2,int scale){

        if(scale<0){

            throw new IllegalArgumentException(

                "The scale must be a positive integer or zero");

        }

        BigDecimal b1 = new BigDecimal(Double.toString(v1));

        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();

    }
    
    /**

     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指

     * 定精度，以后的数字四舍五入。

     * @param v1 被除数

     * @param v2 除数

     * @param scale 表示表示需要精确到小数点以后几位。

     * @return 两个参数的商

     */

     public static double divAsBigDecimal(BigInteger v1,BigInteger v2,int scale){

         if(scale<0){

             throw new IllegalArgumentException(

                 "The scale must be a positive integer or zero");

         }

         BigDecimal b1 = new BigDecimal(v1.toString());

         BigDecimal b2 = new BigDecimal(v2.toString());

         return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();

     }



    /**

    * 提供精确的小数位四舍五入处理。

    * @param v 需要四舍五入的数字

    * @param scale 小数点后保留几位

    * @return 四舍五入后的结果

    */

    public static double round(double v,int scale){

        if(scale<0){

            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }

        BigDecimal b = new BigDecimal(Double.toString(v));

        BigDecimal one = new BigDecimal("1");

        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();

    }
    
    /**
	 * @author zhubinhua
	 * @param str     需要格式化的字符串
	 * @param n       保留小数个数
	 * @param flag    是否进行四舍五入
	 * 
	 */
	public static String floatToStr(String str, int n, int flag) {
		String result = null;
		
		float f = Float.parseFloat(str);
		float temp = new Double(Math.pow(10, n)).floatValue();
		
		if(flag == 1) {  //四舍五入
			f = (float)(Math.round(f * temp) / temp);
		} else if(flag == 0) {
			f = (float)(Math.floor(f * temp) / temp);
		}
		
		result = String.valueOf(f);
		
		//用0来补充小数位数
		int count = result.length() - result.indexOf(".") - 1;
		if(count < n) {
			for(int i=0; i<n-count; i++) {
				result = result + "0";
			}
		}
		
		return result;
	}

};
