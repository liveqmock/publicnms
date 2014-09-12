/*
 * Created on 2005-4-27
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.util;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CommonAppUtil {
	private static String appName=null;
	public final static int batch_size=35;
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
				}
				else{
					s = s.substring(0, s.lastIndexOf("/"));
				}
	//appName = s + "/webapps/netflow";
	//System.out.println("appname is "+appName);
	//appName=s+"/webapps/dhwebnms";
	//appName=s+"/webapps/nms";				
	appName=s+"\\webapps\\afunms";
	//System.out.println("appname is "+appName);
	return appName;
}

}
