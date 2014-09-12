/**
 * <p>Description:get mysql information</p>
 * <p>Company: afunms</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-6
 */

package com.afunms.application.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.afunms.application.model.Urlmonitor_realtime;
import com.afunms.application.model.WebConfig;
import com.afunms.common.util.SysLogger;

public class UrlDataCollector {

	private static Properties props;

	static FileInputStream fis;

	public UrlDataCollector() {
	}

	public static Urlmonitor_realtime getUrlmonitor_realtime(WebConfig urlconf,
			boolean old, String s) throws Exception {
		return getUrlmonitor_realtime(urlconf, true, old, s);
	}

	public static Urlmonitor_realtime getUrlmonitor_realtime(WebConfig urlconf,
			boolean flag, boolean old, String str) throws Exception {
		String s = urlconf.getStr();

		int i = urlconf.getId();
		String s1 = urlconf.getMethod();
		String s2 = urlconf.getQuery_string();
		int j = urlconf.getTimeout();
		int k = 1;
		String s3 = urlconf.getAvailability_string();
		String s4 = urlconf.getUnavailability_string();
		int v = urlconf.getVerify();
		boolean flag1 = false;
		if (v == 1) {
			flag1 = true;
		}
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(s);

		Object obj = null;
		String s5 = null;
		long starttime = 0;
		long endtime = 0;
		long condelay = 0;
		int conflag = 1;
		try {

			URL url = new URL(urlconf.getStr());
			starttime = System.currentTimeMillis();
			URLConnection con = url.openConnection();
			
			con.setConnectTimeout(urlconf.getTimeout());
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			con.setAllowUserInteraction(true);
			String s7 = "";
			
			try{
				//SysLogger.info(con.getContent().toString());
//				con.getInputStream();
				InputStreamReader _read = new InputStreamReader(con
						.getInputStream(), "GBK");
				BufferedReader _breader = new BufferedReader(_read);
				String _oneRow = "";
				
				while ((_oneRow = _breader.readLine()) != null) {// 读取当前日期的日志文件，要根据读取行数定位。
					s7 += _oneRow + "\n";
				}
				_breader.close();
				_read.close();
			}catch(Exception e){
				conflag = 0;
				//e.printStackTrace();
			}

			/*
			 * UsernamePasswordCredentials upc = new
			 * UsernamePasswordCredentials("foo", "bar");
			 * 
			 * client.getState().setCredentials(null, null, upc);
			 * get.setDoAuthentication(false); client.setConnectionTimeout(j);
			 * //long l = System.currentTimeMillis(); int
			 * j1=client.executeMethod(get); //long l1 =
			 * System.currentTimeMillis(); System.out.println("Time =="+(l1-l));
			 * System.out.println("j1==="+j1); InputStreamReader read = new
			 * InputStreamReader (get.getResponseBodyAsStream(),"GBK");
			 * 
			 * BufferedReader breader=new BufferedReader(read);
			 * 
			 * String oneRow=""; String s7=""; while((oneRow =
			 * breader.readLine())!=null){//读取当前日期的日志文件，要根据读取行数定位。
			 * s7+=oneRow+"\n"; } //System.out.println(s+" s7 "+s7.length()+"
			 * str "+str.length()); breader.close(); read.close();
			 */
			if (k != 0 && s3 != null && s3.length() > 0) {
				if (!doAvailabilityCheck(s7, s3, true)) {
					k = 0;
					s5 = "The String \"" + s3
							+ "\" did not appear in the response";
				}
			}
			if (k != 0 && s4 != null && s4.length() > 0) {
				if (!doAvailabilityCheck(s7, s4, false)) {
					k = 0;
					s5 = "The String \"" + s4 + "\" appeared in the response";
				}
			}
			if (flag && flag1 && k == 0)
				return getUrlmonitor_realtime(urlconf, false, old, str);

			// k=0表明页面无效

			Urlmonitor_realtime ur = new Urlmonitor_realtime();

			//ur.setIs_canconnected(new Integer(1));
			ur.setIs_canconnected(conflag);
			ur.setIs_valid(new Integer(k));
			ur.setCondelay(new Integer(condelay + ""));
			if (old == true) {

				// System.out.println(s+" s7.equals(str)
				// "+s7.toLowerCase().trim().equals(str.toLowerCase().trim()));
				if (s7.equals(str)) {
					ur.setIs_refresh(new Integer(0));
					ur.setPage_context(s7);
				} else {
					ur.setIs_refresh(new Integer(1));
					ur.setPage_context(str);
				}
			} else {
				ur.setIs_refresh(new Integer(0));
				ur.setPage_context(s7);

			}
			ur.setReason("返回：");
			ur.setMon_time(Calendar.getInstance());
			
//			httpClient.getHostConfiguration().setProxy("172.17.17.100",80);//代理地址
			//创建GET方法的实例   
//			GetMethod getMethod = new GetMethod(urlconf.getStr());   +
			//使用系统提供的默认的恢复策略   
			get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());   
		    //执行getMethod   
	        int statusCode = client.executeMethod(get);   
		    if (statusCode != HttpStatus.SC_OK) {   
		        System.err.println("Method failed: " + get.getStatusLine());   
		    }   
		    //读取内容    
		    byte[] responseBody = get.getResponseBody();   
		    //处理内容   
		    String charset_str = get.getResponseCharSet();//获得编码信息
//			System.out.println(charset_str);
		    String newStr = new String(responseBody,charset_str);  
//		    SysLogger.info("-----------------------------");
//			SysLogger.info(new String(newStr));   
//			SysLogger.info("------"+newStr.length()/1024);  
		    ur.setPagesize((newStr.length()/1024)+"");
		    //ur.setKey_exist(newStr);
		    ur.setPage_context(newStr);
		    ur.setKey_exist("");
			return ur;
		} catch (HttpException httpException) {
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;

			httpException.printStackTrace();
			Urlmonitor_realtime ur3 = new Urlmonitor_realtime();
			ur3.setIs_canconnected(conflag);
			ur3.setIs_valid(new Integer(0));
			ur3.setIs_refresh(new Integer(0));
			ur3.setPage_context(str);
			ur3.setReason(props.getProperty("600"));
			ur3.setMon_time(Calendar.getInstance());
			ur3.setCondelay(new Integer("" + condelay));
			ur3.setPagesize("0");
		    ur3.setKey_exist("");
			return ur3;
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;

			if (flag && flag1) {
				return getUrlmonitor_realtime(urlconf, false, old, str);
			} else {
				Urlmonitor_realtime ur1 = new Urlmonitor_realtime();
				ur1.setIs_canconnected(conflag);
				ur1.setIs_valid(new Integer(0));
				ur1.setIs_refresh(new Integer(0));
				ur1.setPage_context(str);

				ur1.setReason("页面不能连接");
				ur1.setMon_time(Calendar.getInstance());
				ur1.setCondelay(new Integer("" + condelay));
				ur1.setPagesize("0");
			    ur1.setKey_exist("");
				return ur1;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;

			if (flag && flag1) {
				return getUrlmonitor_realtime(urlconf, false, old, str);
			} else {
				exception.printStackTrace();
				Urlmonitor_realtime ur6 = new Urlmonitor_realtime();

				ur6.setIs_canconnected(conflag);
				ur6.setIs_valid(new Integer(0));
				ur6.setIs_refresh(new Integer(0));
				ur6.setPage_context(str);
				ur6.setReason(" Exception while trying to acces the url "
						+ exception.toString());
				ur6.setMon_time(Calendar.getInstance());
				ur6.setCondelay(new Integer("" + condelay));
				ur6.setPagesize("0");
			    ur6.setKey_exist("");
				return ur6;
			}
		} finally {
			get.releaseConnection();
		}
	}
	
	public static String[] getUrlmonitor_realtime(String urlstr,
			boolean flag, boolean old, String str) throws Exception {
		String[] retValue = new String[2];
		//String s = url;

		//int i = urlconf.getId();
		//String s1 = urlconf.getMethod();
		//String s2 = urlconf.getQuery_string();
		//int j = urlconf.getTimeout();
		int k = 1;
		//String s3 = urlconf.getAvailability_string();
		//String s4 = urlconf.getUnavailability_string();
		// long l = System.currentTimeMillis();
		//int v = urlconf.getVerify();
		boolean flag1 = false;
//		if (v == 1) {
//			flag1 = true;
//		}
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(urlstr);

		Object obj = null;
		String s5 = null;
		long starttime = 0;
		long endtime = 0;
		long condelay = 0;
		try {

			URL url = new URL(urlstr);
			SysLogger.info(urlstr+"---------");
			starttime = System.currentTimeMillis();
			URLConnection con = url.openConnection();
			//con.setConnectTimeout(urlconf.getTimeout());
			con.setConnectTimeout(10000);
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			con.setAllowUserInteraction(true);
			//con.getInputStream();
			InputStreamReader _read = new InputStreamReader(con
					.getInputStream(), "GBK");
			BufferedReader _breader = new BufferedReader(_read);
			String _oneRow = "";
			String s7 = "";
			while ((_oneRow = _breader.readLine()) != null) {// 读取当前日期的日志文件，要根据读取行数定位。
				s7 += _oneRow + "\n";
			}
			_breader.close();
			_read.close();
			retValue[0] = "1";
			retValue[1] = condelay+""; 
			//SysLogger.info(s7);

			/*
			 * UsernamePasswordCredentials upc = new
			 * UsernamePasswordCredentials("foo", "bar");
			 * 
			 * client.getState().setCredentials(null, null, upc);
			 * get.setDoAuthentication(false); client.setConnectionTimeout(j);
			 * //long l = System.currentTimeMillis(); int
			 * j1=client.executeMethod(get); //long l1 =
			 * System.currentTimeMillis(); System.out.println("Time =="+(l1-l));
			 * System.out.println("j1==="+j1); InputStreamReader read = new
			 * InputStreamReader (get.getResponseBodyAsStream(),"GBK");
			 * 
			 * BufferedReader breader=new BufferedReader(read);
			 * 
			 * String oneRow=""; String s7=""; while((oneRow =
			 * breader.readLine())!=null){//读取当前日期的日志文件，要根据读取行数定位。
			 * s7+=oneRow+"\n"; } //System.out.println(s+" s7 "+s7.length()+"
			 * str "+str.length()); breader.close(); read.close();
			 */
			
//			if (k != 0 && s3 != null && s3.length() > 0) {
//				if (!doAvailabilityCheck(s7, s3, true)) {
//					k = 0;
//					s5 = "The String \"" + s3
//							+ "\" did not appear in the response";
//				}
//			}
//			if (k != 0 && s4 != null && s4.length() > 0) {
//				if (!doAvailabilityCheck(s7, s4, false)) {
//					k = 0;
//					s5 = "The String \"" + s4 + "\" appeared in the response";
//				}
//			}
//			
//			if (flag && flag1 && k == 0)
//				return getUrlmonitor_realtime(urlconf, false, old, str);

			// k=0表明页面无效

//			Urlmonitor_realtime ur = new Urlmonitor_realtime();
//
//			ur.setIs_canconnected(new Integer(1));
//			ur.setIs_valid(new Integer(k));
//			ur.setCondelay(new Integer(condelay + ""));
//			if (old == true) {
//
//				// System.out.println(s+" s7.equals(str)
//				// "+s7.toLowerCase().trim().equals(str.toLowerCase().trim()));
//				if (s7.equals(str)) {
//					ur.setIs_refresh(new Integer(0));
//					ur.setPage_context(s7);
//				} else {
//					ur.setIs_refresh(new Integer(1));
//					ur.setPage_context(str);
//				}
//			} else {
//				ur.setIs_refresh(new Integer(0));
//				ur.setPage_context(s7);
//
//			}
//			ur.setReason("返回：");
//			ur.setMon_time(Calendar.getInstance());
//			return ur;
		} catch (HttpException httpException) {
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			retValue[0] = "0";
			retValue[1] = condelay+"";
			httpException.printStackTrace();

			 httpException.printStackTrace();
//			Urlmonitor_realtime ur3 = new Urlmonitor_realtime();
//			ur3.setIs_canconnected(new Integer(1));
//			ur3.setIs_valid(new Integer(0));
//			ur3.setIs_refresh(new Integer(0));
//			ur3.setPage_context(str);
//			ur3.setReason(props.getProperty("600"));
//			ur3.setMon_time(Calendar.getInstance());
//			ur3.setCondelay(new Integer("" + condelay));
//			return ur3;
		} catch (IOException ioexception) {
			// ioexception.printStackTrace();
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			retValue[0] = "0";
			retValue[1] = condelay+"";
			ioexception.printStackTrace();

//			if (flag && flag1) {
//				return getUrlmonitor_realtime(urlconf, false, old, str);
//			} else {
//				Urlmonitor_realtime ur1 = new Urlmonitor_realtime();
//				ur1.setIs_canconnected(new Integer(0));
//				ur1.setIs_valid(new Integer(0));
//				ur1.setIs_refresh(new Integer(0));
//				ur1.setPage_context(str);
//
//				ur1.setReason("页面不能连接");
//				ur1.setMon_time(Calendar.getInstance());
//				ur1.setCondelay(new Integer("" + condelay));
//				return ur1;
//			}
		} catch (Exception exception) {
			// exception.printStackTrace();
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			retValue[0] = "0";
			retValue[1] = condelay+"";
			exception.printStackTrace();

//			if (flag && flag1) {
//				return getUrlmonitor_realtime(urlconf, false, old, str);
//			} else {
//				exception.printStackTrace();
//				Urlmonitor_realtime ur6 = new Urlmonitor_realtime();
//
//				ur6.setIs_canconnected(new Integer(1));
//				ur6.setIs_valid(new Integer(0));
//				ur6.setIs_refresh(new Integer(0));
//				ur6.setPage_context(str);
//				ur6.setReason(" Exception while trying to acces the url "
//						+ exception.toString());
//				ur6.setMon_time(Calendar.getInstance());
//				ur6.setCondelay(new Integer("" + condelay));
//				return ur6;
//			}
		} finally {
			get.releaseConnection();
		}
		return retValue;
	}

	private static String getHostPort(String s) {
		StringTokenizer stringtokenizer = new StringTokenizer(s, "/");
		String s1 = stringtokenizer.nextToken();
		String s2 = stringtokenizer.nextToken();
		return s1 + "//" + s2 + "/";
	}

	private static String getURI(String s) {
		StringTokenizer stringtokenizer = new StringTokenizer(s, "/");
		StringBuffer stringbuffer = new StringBuffer();
		int i = 0;
		for (i = 0; stringtokenizer.hasMoreTokens(); i++)
			if (i < 2) {
				stringtokenizer.nextToken();
			} else {
				stringbuffer.append("/");
				stringbuffer.append(stringtokenizer.nextToken());
			}

		if (i < 2)
			stringbuffer = new StringBuffer("/");
		return stringbuffer.toString();
	}

	private static boolean doAvailabilityCheck(String s, String s1, boolean flag) {
		ArrayList arraylist = getStrings(s1);
		for (int i = 0; i < arraylist.size(); i++) {
			String s2 = (String) arraylist.get(0);
			if (s.indexOf(s2) == -1) {
				if (flag)
					return false;
			} else if (!flag)
				return false;
		}

		return true;
	}

	private static ArrayList getStrings(String s) {
		char ac[] = s.toCharArray();
		boolean flag = true;
		String s1 = "";
		ArrayList arraylist = new ArrayList();
		for (int i = 0; i < ac.length; i++) {
			char c = ac[i];
			if (c != '"' && c != ' ')
				s1 = s1 + String.valueOf(c);
			if (c == '"') {
				if (flag) {
					flag = false;
					if (!s1.trim().equals("")) {
						arraylist.add(s1.trim());
						s1 = "";
					}
				} else {
					flag = true;
				}
			} else if (c == ' ') {
				if (flag && !s1.trim().equals("")) {
					arraylist.add(s1.trim());
					s1 = "";
				}
				s1 = s1 + String.valueOf(c);
			}
		}

		if (!s1.trim().equals("")) {
			arraylist.add(s1.trim());
			s1 = "";
		}
		return arraylist;
	}

}