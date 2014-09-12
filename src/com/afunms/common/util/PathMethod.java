package com.afunms.common.util;

import java.net.URL;

public class PathMethod {
	private static String classPath = "";// 系统类所在的路径
	private static String projectPath = "";// 系统工程所在的路径
	/**
	 * 返回系统中类所在的路径,如.../tomcat/webapps/ROOT/WEB-INF/classes
	 */
	public static String getClassPath() {
		if (classPath.equals("")) {
			URL url = Thread.currentThread().getContextClassLoader()
					.getResource("");
			classPath = url.toString().substring(getByteOmitted());
		}
		return classPath;
	}

	/**
	 * 返回工程路径,如.../tomcat/webapps/ROOT/
	 */
	public static String getProjectPath() {
		if (projectPath.equals("")) {
			String classPathTemp = getClassPath();
			for (int i = 0; i <= 2; i++) {
				int n = classPathTemp.lastIndexOf('/');
				classPathTemp = classPathTemp.substring(0, n);
			}
			projectPath = classPathTemp + "/";
		}
		return projectPath;
	}

	/**
	 * 返回tomcat配置文件server.xml所在的目录,如.../tomcat/conf/
	 */
	public static String getConfPath() {
		String confPath = "";
		String projectPathTemp = getProjectPath();
		for (int i = 0; i <= 2; i++) {
			int n = projectPathTemp.lastIndexOf('/');
			projectPathTemp = projectPathTemp.substring(0, n);
		}
		confPath = projectPathTemp + "/conf/";

		return confPath;
	}

	/**
	 * 根据操作系统的不同来得到字符截取位置
	 * 
	 * @return
	 */
	private static int getByteOmitted() {
		String os = System.getProperty("os.name");
		if (os.toUpperCase().indexOf("WINDOWS") != -1)
			return 6;
		if (os.toUpperCase().indexOf("LINUX") !=-1)
			return 5;
		
		return 5;
	}

	public static void main(String[] args) {
		System.out.println("classpath:======================"+PathMethod.getClassPath());
		System.out.println("projectpath:======================"+PathMethod.getProjectPath());
		System.out.println("confpath:========================="+PathMethod.getConfPath());
	}
}