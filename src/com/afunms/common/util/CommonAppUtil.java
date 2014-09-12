/*
 * Created on 2005-4-27
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.common.util;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CommonAppUtil {
	private static String appName=null;
	public final static int batch_size=35;
	private static String skin = null;
	/**
	 * 
	 */
	public CommonAppUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

/**
 * @return
 */
public static String getAppName() {
	String osname=System.getProperty("os.name");
		String s=System.getProperty("user.dir"); 
				if(osname.toLowerCase().indexOf("windows")>=0){
					s = s.substring(0, s.lastIndexOf("\\"));
					appName=s+"\\webapps\\afunms";
				}
				else{
					s = s.substring(0, s.lastIndexOf("/"));
					appName=s+"/webapps/afunms";
				}
	//appName = s + "/webapps/netflow";
	//System.out.println("appname is "+appName);
	//appName=s+"/webapps/dhwebnms";
	//appName=s+"/webapps/nms";				
	
	//appName=s+"\\webapps\\nms";
				//appName=s+"/webapps/nms";
	
	//System.out.println("appname is "+appName);
	return appName;
	}

	public static String getSkin() {
		return skin;
	}
	
	public static void setSkin(String skin) {
		CommonAppUtil.skin = skin;
	}

	public static String getSkinPath(){
		if (skin == null || "".equals(skin) || "null".equals(skin)) {
			return "";
		}else
		return skin + "/";
	}

}
