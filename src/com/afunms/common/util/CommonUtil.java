/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CommonUtil 
{		
    private CommonUtil()
    {
    }
    
	/**
	 * 将一个字符串形式的ip地址转换成一个长整数，如果是非法数据，则返回0
	 * 
	 * @param ip
	 * @return
	 */
	static public long ip2long(String ip) {
		long result = 0;
		try {
			StringTokenizer st = new StringTokenizer(ip, ".");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				int part = Integer.parseInt(token);
				result = result * 256 + part;
			}
		} catch (Exception e) {
			result = 0;
		}
		return result;
	}
	
	//0a:0a:98:a0:00:a1 -> 10.10.152.160   161
	static public String[] IPPort2String(String ip, int len){
		String[] s = ip.split(":");
		if( len>0 && s.length>len ){
		
			String retip = ""+Long.parseLong(s[0],16);
			for (int i = 1; i < len; ++i) {
				retip = retip+"."+Long.parseLong(s[i],16);
			}
			String tmp=s[len];
			for( int i=len+1; i<s.length; ++i){
				tmp = tmp+s[i];
			}
			
			String retport = ""+Long.parseLong(tmp,16);
			
			String[] ret = new String[2];
			ret[0] = retip;
			ret[1] = retport;
			return ret;
		}
		
		return new String[0];
		
	}
	
	
	public static String demoChangeStringToHex(final String ip) {
		String returnString = "";
		try {
			StringTokenizer st = new StringTokenizer(ip, ".");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				int part = Integer.parseInt(token);
				System.out.println(part);
				//String tmpHex = Integer.toHexString(part & 0xFF);
				String tmpHex = Integer.toHexString(part);
				if(tmpHex.length()==1){
					tmpHex= "0"+tmpHex;
				}
				returnString = returnString+" "+tmpHex;
				System.out.println(part+"--->"+tmpHex);
				//result = result * 256 + part;
			}
		} catch (Exception e) {
			//result = 0;
		}
		System.out.println(returnString.trim());
		return returnString.trim();
		/*
		
	    int changeLine = 1;
	    String s = "Convert a string to HEX/こんにちは/你好";
	    if (ip != null) {
	        s = ip;
	    }
	    System.out.println(s);
	    for (int i = 0; i < s.length(); i++) {
	        byte[] ba = s.substring(i, i + 1).getBytes();
	        // & 0xFF for preventing minus
	        String tmpHex = Integer.toHexString(ba[0] & 0xFF);
	        System.out.print("0x" + tmpHex.toUpperCase());
	        System.out.print(" ");
	        if (changeLine++ % 8 == 0) {
	            System.out.println("");
	        }
	        // Multiply byte according
	        if (ba.length == 2) {
	            tmpHex = Integer.toHexString(ba[1] & 0xff);
	            System.out.print("0x" + tmpHex.toUpperCase());
	            System.out.print(" ");
	            if (changeLine++ % 8 == 0) {
	                System.out.println("");
	            }
	        }
	    }

	    System.out.println(""); // change line
	    System.out.println(""); // change line
	    */
	}
    public static String getDateAndTime()
    {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		String currentTime = formatter.format(date);
		return currentTime;
    }
    //返回2008-01-18 16:00:00中的，20080118这个格式的日期
    public static String getCurrentDate(){  	 
    	String [] strArray = getDateAndTime().split(" ");
    	String dateFormat = strArray[0];
		String [] dateArray = dateFormat.split("-");
		String dateStr = dateArray[0]+dateArray[1]+dateArray[2];
    	return dateStr;
    }
    
	/**
	 * getCurrentTime操作
	 * 取得 12:22:22 这样格式的时间
	 * @param
	 * @return
	 */
	public static String getCurrentTime()
	{
	    Date today;
	    SimpleDateFormat formatter
    		= new SimpleDateFormat("HH:mm:ss");
	    
	    today = new Date();
	    String returnStr = formatter.format(today);
	    today = null;
	    formatter = null;
	    return returnStr;
	}
	
	  //往后延时10秒钟，用于检测最新10秒钟内的报警信息
    public static String getLaterTenSecondTime(){		
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date1;
		String timeFormat = null;
		try {
			date1 = format1.parse(getDateAndTime());
			long Time=(date1.getTime()/1000)-60;
			date1.setTime(Time*1000);
			String mydate1 = format1.format(date1);
			//System.out.println("mydate1:"+mydate1);
			
			String [] strArray = mydate1.split(" ");
			timeFormat = strArray[1];
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		return timeFormat;
    }
    
    /**
     * 字符串转换成十六进制值
     * @param bin String 我们看到的要转换成十六进制的字符串
     * @return 
     */
    public static String bin2hex(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }

    /**
     * 十六进制转换字符串
     * @param hex String 十六进制
     * @return String 转换后的字符串
     */
    public static String hex2bin(String hex) {
        String digital = "0123456789ABCDEF";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return new String(bytes);
    }

    /** 
     * java字节码转字符串 
     * @param b 
     * @return 
     */
    public static String byte2hex(byte[] b) { //一个字节的数，

        // 转成16进制字符串

        String hs = "";
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            //整数转成十六进制表示

            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                hs = hs + "0" + tmp;
            } else {
                hs = hs + tmp;
            }
        }
        tmp = null;
        return hs.toUpperCase(); //转成大写

    }

    /**
     * 字符串转java字节码
     * @param b
     * @return
     */
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节

            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        b = null;
        return b2;
    }

    public static void main(String[] args) {
//        String content = "技术性问题EDF%&^%#_|~";
//        System.out.println(bin2hex(content));
//        System.out.println(hex2bin(bin2hex(content)));
    	String ipaddress = "192.168.0.1";
    	System.out.println(doip(ipaddress));
    }
    
	public String getDate(String swdate){
		String[] num = swdate.split(":");
		String num1 = Integer.valueOf(num[0],16).toString();
		String num2 = Integer.valueOf(num[1],16).toString();
		String num3 = Integer.valueOf(num[2],16).toString();
		String num4 = Integer.valueOf(num[3],16).toString();
		String num5 = Integer.valueOf(num[4],16).toString();
		String num6 = Integer.valueOf(num[5],16).toString();
		String num7 = Integer.valueOf(num[6],16).toString();
		String num8 = Integer.valueOf(num[7],16).toString();
		String swyear = Integer.parseInt(num1)*256+Integer.parseInt(num2)+"";
		String swnewdate = swyear+"-"+num3+"-"+num4+" "+num5+":"+num6+":"+num7+":"+num8;
		return swnewdate;
		
	}
   
	/**
	 * 根据key来得到hash中的value  若key不存在，则返回defaultValue
	 * @param hash 
	 * @param key 
	 * @param defaultValue
	 * @return
	 */
	public static String getValue(Hashtable hash,String key,String defaultValue){
		if(hash == null){
			return defaultValue;
		}
		if(hash.containsKey(key)){
			return String.valueOf(hash.get(key));
		}else{
			return defaultValue;
		}
	}
	
	/**
	 * 对端口流速信息进行排序
	 * <p>例如： 索引 描述    状态 出口 入口</p>
	 * <p>     [23, eth0, , 1, 2144, 20256]</P>
	 * @param ifvector  
	 * @param orderflag  要排序的字段
	 * @param sorttype   排序的类型  升序或者降序
	 * @param netInterfaceItem  key
	 * @author HONGLI
	 * @return
	 */ 
	public static Vector<String[]> sortInterface(Vector<String[]> ifvector, String orderflag, String sorttype, String[] netInterfaceItem){

		for(int i=0; i < ifvector.size(); i++){
			for(int j=i+1;j<ifvector.size();j++){
				String[] strs_1 = (String[])ifvector.get(i);
				String[] strs_2 = (String[])ifvector.get(j);
				for(int m=0; m<netInterfaceItem.length; m++){
					if(orderflag.equalsIgnoreCase(netInterfaceItem[m]) && strs_1[m] != null && strs_2[m] != null){
						String str1 = getNumStrFromString(strs_1[m]);
						String str2 = getNumStrFromString(strs_2[m]);
						double d1 = Double.parseDouble(str1);
						double d2 = Double.parseDouble(str2);
						if(sorttype.equalsIgnoreCase("asc")){//正序 从小到大
							if(d1 > d2){
								ifvector.add(j, strs_1);
								ifvector.remove(j+1);
								ifvector.add(i, strs_2);
								ifvector.remove(i+1);
							}
						}else{//"desc" 反序 从大到小
							if(d1 < d2){
								ifvector.add(j, strs_1);
								ifvector.remove(j+1);
								ifvector.add(i, strs_2);
								ifvector.remove(i+1);
							}
						}
					}
				}
			}
		}
		return ifvector;
	}
	
	/**
	 * 对端口流速信息进行排序
	 * <p>例如： 索引 描述    状态 出口 入口</p>
	 * <p>     [23, eth0, , 1, 2144, 20256]</P>
	 * @param ifvector  
	 * @param orderflag  要排序的字段
	 * @param sorttype   排序的类型  升序或者降序
	 * @param netInterfaceItem  key
	 * @author HONGLI
	 * @return
	 */
	public static List<Hashtable> sortInterfaceList(List<Hashtable> netflowList, String orderflag, String sorttype, String[] netInterfaceItem){
		System.out.println(netflowList+orderflag+sorttype);
		for(int i=0; i < netflowList.size(); i++){
			for(int j=i+1;j<netflowList.size();j++){
				Hashtable hash_1 = (Hashtable)netflowList.get(i);
				Hashtable hash_2 = (Hashtable)netflowList.get(j);
				for(int m=0; m<netInterfaceItem.length; m++){
					if(orderflag.equalsIgnoreCase(netInterfaceItem[m])){
						String str1 = getNumStrFromString((String)hash_1.get(orderflag));
						String str2 = getNumStrFromString((String)hash_2.get(orderflag));
						double d1 = Double.parseDouble(str1);
						double d2 = Double.parseDouble(str2);
						if(sorttype.equalsIgnoreCase("asc")){//正序 从小到大
							if(d1 > d2){
								netflowList.add(j, hash_1);
								netflowList.remove(j+1);
								netflowList.add(i, hash_2);
								netflowList.remove(i+1);
							}
						}else{//"desc" 反序 从大到小
							if(d1 < d2){
								netflowList.add(j, hash_1);
								netflowList.remove(j+1);
								netflowList.add(i, hash_2);
								netflowList.remove(i+1);
							}
						}
					}
				}
			}
		}
		return netflowList;
	}
	
	/**
	 * <p>将Hashtable转换成HashMap 类型</p>
	 * @param data 
	 * @return 
	 */
	public static HashMap converHashTableToHashMap(Hashtable data){
		if(data == null){
			return null;
		}
		HashMap retHashMap = new HashMap();
		Iterator iter = data.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String key = String.valueOf(entry.getKey()); 
		    String value = String.valueOf(entry.getValue()); 
		    retHashMap.put(key, value);
		}
		return retHashMap;
	}
	
	public static String doip(String ip){
//		  String newip="";
//		  for(int i=0;i<3;i++){
//			int p=ip.indexOf(".");
//			newip+=ip.substring(0,p);
//			ip=ip.substring(p+1);
//		  }
//		 newip+=ip;
		ip = ip.replaceAll("\\.", "_").trim();
		 //System.out.println("newip="+newip);
		 return ip;
	}
	
	/**
	 * 获取字符串中的数字 如：“1012eKb/秒aaa”    则获取到“1012”
	 * @return
	 */
	public static String getNumStrFromString(String str){
//		 String str = "1012eKb/秒aaa";
		 Pattern pattern = Pattern.compile("\\d");
		 Matcher matcher = pattern.matcher(str);
		 StringBuffer numStr = new StringBuffer();
		 while(matcher.find()){
			numStr.append(matcher.group(0));
		 }
//		 System.out.println(numStr);
		return numStr.toString();
	}
	
	/**
	 * 正则表达式过滤掉乱码(中文字符)
	 * @author HONGLI
	 * @param str  需要过滤的字符串
	 * @return
	 */
	public static String removeIllegalStr(String str){
		if(str == null){
			return "";
		}
		Pattern pattern = Pattern.compile("\\p{ASCII}");
		Matcher matcher = pattern.matcher(str);
		String numStr = "";
		while(matcher.find()){
			numStr = numStr + matcher.group(0);
		}
		return numStr;
	}
	
	/**
	 * 使用字符集过滤掉乱码(包括中文繁体字符)   将字符串中的乱码替换为“-”
	 * @author HONGLI
	 * @param charsetName 字符集 可设置为“GB2312”  
	 * @param str         需要转换的乱码字符等
	 * @return
	 */
	public static String removeIllegalStr(String charsetName, String str){
		if(str == null || str.equals("")){
			return "";
		}
		StringBuffer sBuffer = new StringBuffer();
		for(int i=0; i<str.length(); i++){
			char c = str.charAt(i);
			boolean b = Charset.forName(charsetName).newEncoder().canEncode(c);
			if(b){
				sBuffer.append(c);
			}else{
				sBuffer.append("-");
			}
		}
		return sBuffer.toString();
	}
	
	/**
	 * 去除arraylist的重复元素
	 * @param arlList
	 */
	 public static synchronized List removeDuplicate(ArrayList arlList){   
		 if(arlList == null){
			 return null;
		 }
		 HashSet h = new HashSet(arlList);   
		 arlList.clear();   
		 arlList.addAll(h);   
		 return arlList;
	 }  
	 
	 /**
	  * 数据截取
	  * @param data    被截取的数
	  * @param length  截取的小数点后的位数
	  * @return
	  */
	 public synchronized static String format(double data, int length){
		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(length);
		numberFormat.setGroupingUsed(false);//设置数字不进行分组，去掉逗号
		String retStr = numberFormat.format(data);
		return retStr;
	 }
	 
}
