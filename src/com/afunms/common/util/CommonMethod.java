package com.afunms.common.util;

import java.io.File;
import java.io.IOException;

import com.afunms.initialize.ResourceCenter;

public class CommonMethod {

	/**
	 * @author lgw
	 * @date Feb 28, 2011 1:31:36 PM
	 * @param args void
	 * @Description: TODO(描述这个方法的作用) 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	protected static String keycode = "SXZVFNRN9MZ9L8LGA0E2B1BB";
	public static int[] chartColors = {0x3292ce,0xe9714a,0x3bc93b,0xddde46,0xd75fb7,0x56b4c2,
		   0xa6cc22,0x897795,0xfba53f,0xba9f5d,0xac8fd4,0xd5eaef };//最后2个颜色值需要修改
	/**
	 * 
	 * @author lgw
	 * @date Nov 17, 2010 2:00:15 PM
	 * @return String
	 * @Description: TODO(生成图片名字)
	 */
	public  static String getPicName()
	{
		java.util.UUID uuid=java.util.UUID.randomUUID();
		return uuid.toString();
	}
	/**
	 * 
	 * @author lgw
	 * @date Nov 17, 2010 2:00:26 PM
	 * @return String
	 * @Description: TODO(图片存放路径)
	 */
	public static String checkFile()
	{
		  PathMethod pm = new PathMethod();
		File   directory =   new   File(pm.getProjectPath());   
		String files = null;
		String path=ResourceCenter.getInstance().getSysPath()+"resource/image/jfreechart/";
		try {
			//files = directory.getCanonicalPath()+"/reportimg".toString();
			files = path+"reportimg";
		} catch (Exception e) {
			e.printStackTrace();
		}
		File myfile = new File(files);
		if(!myfile.isDirectory())
		{
			myfile.mkdir();
		}
		return files;
	}
}
