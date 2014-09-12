package com.afunms.polling.snmp.db;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.application.model.DBVo;
import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;

public class LogParser {
	public static final String seperator = "=";
	public static final String lineseperator = "########################################";
	
	private static String getDataFromLogfile(Object obj, DBVo dbvo) {
		String className = obj.getClass().getSimpleName();
		String fileName = className.replace("Sybase", "").replace("Proxy", "");
		StringBuffer fileContent = new StringBuffer();
		try {
			String filename = ResourceCenter.getInstance().getSysPath()
					+ "linuxserver\\" + dbvo.getIpAddress() + ".sybase\\"+ fileName +".log";
			SysLogger.info("读取 " + filename);

			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String strLine = null;
			// 读入文件内容
			while ((strLine = br.readLine()) != null) {
				fileContent.append(strLine).append("\n");
			}
			isr.close();
			fis.close();
			br.close();
//			System.out.println(fileContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileContent.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	static Hashtable parse(Object obj, DBVo dbvo, String htKey, String[] args){
		Hashtable retHashtable = new Hashtable();
		String fileContent = getDataFromLogfile(obj, dbvo);
		if (fileContent.isEmpty()) {
			return retHashtable;
		}
		Vector returnVal7 = new Vector();
		StringBuffer patternStr = new StringBuffer();
		if (args.length > 0) {
			for (String key : args) {
				patternStr.append("(").append(key).append(seperator).append(".*)\\n");
			}
		}
		if (!patternStr.toString().isEmpty()) {
			Pattern p = Pattern.compile(patternStr.toString());
			Matcher m = p.matcher(fileContent);
			while (m.find()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= args.length; i++) {
					int index = m.group(i).indexOf(seperator);
					if (index != -1) {
						String key = m.group(i).substring(0, index);
						String value = m.group(i).substring(index+1);
						if (null != value && !value.isEmpty()) {
							return_value.put(key, value);
						}else{
							return_value.put(key, "");
						}
					}
				}
				returnVal7.add(return_value);
				return_value = null;
			}
			retHashtable.put(htKey, returnVal7);
		}

		return retHashtable;
	}
	
	@SuppressWarnings("unchecked")
	static Hashtable parse(Object obj, DBVo dbvo, String htKey, String prefix, String[] args, String subfix){
		Hashtable retHashtable = new Hashtable();
		String fileContent = getDataFromLogfile(obj, dbvo);
		if (fileContent.isEmpty()) {
			return retHashtable;
		}
		Vector returnVal7 = new Vector();
		StringBuffer patternStr = new StringBuffer();
		if (prefix != null && !"".equals(prefix)) {
			patternStr.append(prefix).append("\\n");
		}
		if (args.length > 0) {
			for (String key : args) {
				patternStr.append("((").append(key).append(seperator).append(".*\\n)*)");
			}
		}
		if (subfix != null && !"".equals(subfix)) {
			patternStr.append(subfix);
		}
		if (!patternStr.toString().isEmpty()) {
			Pattern p = Pattern.compile(patternStr.toString());
			Matcher m = p.matcher(fileContent);
			if (m.find()) {
//				System.out.println(m.group(1));
				String[] lines = m.group(1).split("\n");
				for (int i = 0; i < lines.length; i++) {
					Hashtable return_value = new Hashtable();
					String[] keyValue = lines[i].split(seperator);
					if (keyValue.length >= 2) {
						return_value.put(keyValue[0], keyValue[1]);
					}else{
						return_value.put(keyValue[0], "");
					}
					returnVal7.add(return_value);
					return_value = null;
				}
			}
			retHashtable.put(htKey, returnVal7);
		}
		return retHashtable;
	}
	
}
