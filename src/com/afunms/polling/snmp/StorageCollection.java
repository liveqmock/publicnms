package com.afunms.polling.snmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class StorageCollection {
	
	/**
	 * ipaddress
	 */
	private String ipaddress;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 启用进程
	 */
	private Process process;
	
	/**
	 * 程序输入流
	 */
	private InputStream inputStream;
	
	/**
	 * 程序输出流
	 */
	private OutputStream outputStream;
	
	/**
	 * 执行后返回的数据
	 */
	private Hashtable data;
	
	/**
	 * 
	 */
	public StorageCollection() {
		// TODO Auto-generated constructor stub
		data = new Hashtable();
	}
	
	
	/**
	 * @param ipaddress
	 * @param name
	 * @param username
	 * @param password
	 */
	public StorageCollection(String ipaddress, String name, String username,
			String password) {
		this.ipaddress = ipaddress;
		this.name = name;
		this.username = username;
		this.password = password;
		data = new Hashtable();
	}



	/**
	 * @param ipaddress
	 * @param name
	 * @param username
	 * @param password
	 */
	public void setParameter(String ipaddress, String name, String username,
			String password) {
		this.ipaddress = ipaddress;
		this.name = name;
		this.username = username;
		this.password = password;
	}
	
	public boolean init(){
		//data = new Hashtable();
		return true;
	}
	
	public Hashtable execute(){
		return null;
	}
	
	public Hashtable execute(String command){
		Runtime runtime = Runtime.getRuntime();
		try {
			process = runtime.exec("cmd /c dscli -hmc1 "+ipaddress+ " -user " + username +" -passwd "+ password);
			inputStream = process.getInputStream();
			outputStream = process.getOutputStream();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			outputStreamWriter.write(command + "\r\n");
			outputStreamWriter.write("exit" + "\r\n");
			outputStreamWriter.flush();
			outputStreamWriter.close();
			String inStr = "";
			StringBuffer dataBuffer = new StringBuffer();
			while((inStr = bufferedReader.readLine()) != null){
				dataBuffer.append(inStr + "\r\n");
				System.out.println(inStr);
			}
			parseLog(command , dataBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public Hashtable execute(List commandList){
		for(int i = 0 ; i < commandList.size(); i++){
			String command = (String)commandList.get(i);
			execute(command);
		}
		System.out.println(data.size()+"=====data.size()===========");
		return data;
	}
	
	public boolean destroy(){
		try {
			if(inputStream != null){
				inputStream.close();
			}
			if(outputStream != null){
				outputStream.close();
			}
			if(data != null){
				data = null;
			}
			if(process != null){
				process.destroy();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void parseLog(String command , StringBuffer dataBuffer){
		if(command == null){
			return;
		}
		else if(command.contains("lsarraysite")){
			// 解析lsarraysite
			parseLsarraysite(command, dataBuffer);
		}else if(command.contains("lsarray")){
			// 解析lsarray
			parseLsarray(command, dataBuffer);
		}else if(command.contains("lsrank")){
			// 解析lsrank
			parseLsrank(command, dataBuffer);
		}else if(command.contains("lsextpool")){
			// 解析lsextpool
			parseLsextpool(command, dataBuffer);
		}else if(command.contains("lsfbvol")){
			// 解析lsfbvol
			parseLsfbvol(command, dataBuffer);
		}else if(command.contains("lsvolgrp")){
			// 解析lsvolgrp
			parseLsvolgrp(command, dataBuffer);
		}else if(command.contains("lsioport")){
			// 解析lsioport
			parseLsioport(command, dataBuffer);
		}else if(command.contains("lshostconnect")){
			// 解析lshostconnect
			parseLshostconnect(command, dataBuffer);
		}
	}
	
	public void parseLsarraysite(String command , StringBuffer dataBuffer){
		data.put("lsarraysite", getLsarraysiteData(dataBuffer));
	}
	
	public List getLsarraysiteData(StringBuffer dataBuffer){
		String[] data_str = dataBuffer.toString().split("\r\n");
		int beginRow = -1;
		int endRow = -1;
		for(int i = 0; i < data_str.length; i++){
			if("arsiteDAPairdkcap(10^9B)StateArray".equals(data_str[i].trim().replace(" ", ""))){
				// 从这一行为标题 , 标题行与数据行中间有一行(===================) 故数据开始行为 i + 2
				beginRow = i + 2;
				System.out.println(beginRow+"=======beginRow======");
			}
			
			if("dscli>".equals(data_str[i].trim().replace(" ", ""))){
				endRow = i;
				System.out.println(endRow+"=======endRow======");
			}
		}
		
		List list = new ArrayList();
		if(beginRow != -1 && endRow != -1){
			for(int i = beginRow ; i < endRow ; i++){
				String str = data_str[i];
				String[] str_split = str.split("(" + (char)32 + "|" + (char)9 + ")+");
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
				for(int j = 0; j < str_split.length ; j++){
					if(j==0)
						hashtable.put("arsite", str_split[j].trim());
					if(j==1)
						hashtable.put("DA Pair", str_split[j].trim());
					if(j==2)
						hashtable.put("dkcap (10^9B)", str_split[j].trim());
					if(j==3)
						hashtable.put("State", str_split[j].trim());
					if(j==4)
						hashtable.put("Array", str_split[j].trim());
				}
				list.add(hashtable);
			}
		}
		System.out.println(list.size()+"=========list.size()===========");
		
		return list;
	}
	
	public void parseLsarray(String command , StringBuffer dataBuffer){
		data.put("lsarray", getLsarrayData(dataBuffer));
	}
	
	public List getLsarrayData(StringBuffer dataBuffer){
		String[] data_str = dataBuffer.toString().split("\r\n");
		int beginRow = -1;
		int endRow = -1;
		
		int[] cols = new int[8];
		for(int i = 0; i < data_str.length; i++){
			if("ArrayStateDataRAIDtypearsiteRankDAPairDDMcap(10^9B)".equals(data_str[i].trim().replace(" ", ""))){
				// 从这一行为标题 , 标题行与数据行中间有一行(===================) 故数据开始行为 i + 2
				beginRow = i + 2;
				System.out.println(beginRow+"=======beginRow======");
				System.out.println(data_str[i].trim().indexOf("Array")+"========Array=========");
				cols[0] = data_str[i].trim().indexOf("Array");
				System.out.println(data_str[i].trim().indexOf("State")+"========State=========");
				cols[1] = data_str[i].trim().indexOf("State");
				System.out.println(data_str[i].trim().indexOf("Data")+"========Data=========");
				cols[2] = data_str[i].trim().indexOf("Data");
				System.out.println(data_str[i].trim().indexOf("RAIDtype")+"========RAIDtype=========");
				cols[3] = data_str[i].trim().indexOf("RAIDtype");
				System.out.println(data_str[i].trim().indexOf("arsite")+"========arsite=========");
				cols[4] = data_str[i].trim().indexOf("arsite");
				System.out.println(data_str[i].trim().indexOf("Rank")+"========Array=========");
				cols[5] = data_str[i].trim().indexOf("Rank");
				System.out.println(data_str[i].trim().indexOf("DA Pair")+"========Array=========");
				cols[6] = data_str[i].trim().indexOf("DA Pair");
				System.out.println(data_str[i].trim().indexOf("DDMcap (10^9B)")+"========Array=========");
				cols[7] = data_str[i].trim().indexOf("DDMcap (10^9B)");
				
			}
			
			if("dscli>".equals(data_str[i].trim().replace(" ", ""))){
				endRow = i;
				System.out.println(endRow+"=======endRow======");
			}
		}
		
		List list = new ArrayList();
		
		if(beginRow != -1 && endRow != -1){
			for(int i = beginRow ; i < endRow ; i++){
				String str = data_str[i];
				String[] str_split = str.split("(" + (char)32 + "|" + (char)9 + ")+");
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
//				for(int j = 0; j < str_split.length ; j++){
//					if(j==0)
//						hashtable.put("Array", str_split[j].trim());
//					if(j==1)
//						hashtable.put("State", str_split[j].trim());
//					if(j==2)
//						hashtable.put("Data", str_split[j].trim());
//					if(j==3)
//						hashtable.put("RAIDtype", str_split[j].trim());
//					if(j==4)
//						hashtable.put("arsite", str_split[j].trim());
//					if(j==5)
//						hashtable.put("Rank", str_split[j].trim());
//					if(j==6)
//						hashtable.put("DA Pair", str_split[j].trim());
//					if(j==7)
//						hashtable.put("DDMcap (10^9B)", str_split[j].trim());	
//				}
				hashtable.put("Array", str.substring(cols[0], cols[1]));
				hashtable.put("State", str.substring(cols[1], cols[2]));
				hashtable.put("Data", str.substring(cols[2], cols[3]));
				hashtable.put("RAIDtype", str.substring(cols[3], cols[4]));
				hashtable.put("arsite", str.substring(cols[4], cols[5]));
				hashtable.put("Rank", str.substring(cols[5], cols[6]));
				hashtable.put("DA Pair", str.substring(cols[6], cols[7]));
				hashtable.put("DDMcap (10^9B)", str.substring(cols[7]));	
				
				list.add(hashtable);
			}
		}
		
		return list;
	}
	
	public void parseLsrank(String command , StringBuffer dataBuffer){
		data.put("lsrank", getLsrankData(dataBuffer));
	}
	
	public List getLsrankData(StringBuffer dataBuffer){
		String[] data_str = dataBuffer.toString().split("\r\n");
		int beginRow = -1;
		int endRow = -1;
		for(int i = 0; i < data_str.length; i++){
			if("IDGroupStatedatastateArrayRAIDtypeextpoolIDstgtype".equals(data_str[i].trim().replace(" ", ""))){
				// 从这一行为标题 , 标题行与数据行中间有一行(===================) 故数据开始行为 i + 2
				beginRow = i + 2;
				System.out.println(beginRow+"=======beginRow======");
			}
			
			if("dscli>".equals(data_str[i].trim().replace(" ", ""))){
				endRow = i;
				System.out.println(endRow+"=======endRow======");
			}
		}
		
		List list = new ArrayList();
		
		if(beginRow != -1 && endRow != -1){
			for(int i = beginRow ; i < endRow ; i++){
				String str = data_str[i];
				String[] str_split = str.split("(" + (char)32 + "|" + (char)9 + ")+");
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
				for(int j = 0; j < str_split.length ; j++){
					if(j==0)
						hashtable.put("ID", str_split[j].trim());
					if(j==1)
						hashtable.put("Group", str_split[j].trim());
					if(j==2)
						hashtable.put("State", str_split[j].trim());
					if(j==3)
						hashtable.put("datastate", str_split[j].trim());
					if(j==4)
						hashtable.put("Array", str_split[j].trim());
					if(j==5)
						hashtable.put("RAIDtype", str_split[j].trim());
					if(j==6)
						hashtable.put("extpoolID", str_split[j].trim());	
					if(j==7)
						hashtable.put("stgtype", str_split[j].trim());
				}
				list.add(hashtable);
			}
		}
		return list;
	}
	
	public void parseLsextpool(String command , StringBuffer dataBuffer){
		data.put("lsextpool", getLsextpoolData(dataBuffer));
	}
	
	public List getLsextpoolData(StringBuffer dataBuffer){
		String[] data_str = dataBuffer.toString().split("\r\n");
		int beginRow = -1;
		int endRow = -1;
		for(int i = 0; i < data_str.length; i++){
			if("NameIDstgtyperankgrpstatusavailstor(2^30B)%allocatedavailablereservednumvols".equals(data_str[i].trim().replace(" ", ""))){
				// 从这一行为标题 , 标题行与数据行中间有一行(===================) 故数据开始行为 i + 2
				beginRow = i + 2;
				System.out.println(beginRow+"=======beginRow======");
			}
			
			if("dscli>".equals(data_str[i].trim().replace(" ", ""))){
				endRow = i;
				System.out.println(endRow+"=======endRow======");
			}
		}
		
		List list = new ArrayList();
		
		if(beginRow != -1 && endRow != -1){
			for(int i = beginRow ; i < endRow ; i++){
				String str = data_str[i];
				String[] str_split = str.split("(" + (char)32 + "|" + (char)9 + ")+");
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
				for(int j = 0; j < str_split.length ; j++){
					if(j==0)
						hashtable.put("Name", str_split[j].trim());
					if(j==1)
						hashtable.put("ID", str_split[j].trim());
					if(j==2)
						hashtable.put("stgtype", str_split[j].trim());
					if(j==3)
						hashtable.put("rankgrp", str_split[j].trim());
					if(j==4)
						hashtable.put("status", str_split[j].trim());
					if(j==5)
						hashtable.put("availstor (2^30B)", str_split[j].trim());
					if(j==6)
						hashtable.put("%allocated", str_split[j].trim());	
					if(j==7)
						hashtable.put("available", str_split[j].trim());
					if(j==8)
						hashtable.put("reserved", str_split[j].trim());	
					if(j==9)
						hashtable.put("numvols", str_split[j].trim());
				}
				list.add(hashtable);
			}
		}
		return list;
	}
	
	public void parseLsfbvol(String command , StringBuffer dataBuffer){
		data.put("lsfbvol", getLsfbvolData(dataBuffer));
	}
	
	public List getLsfbvolData(StringBuffer dataBuffer){
		String[] data_str = dataBuffer.toString().split("\r\n");
		int beginRow = -1;
		int endRow = -1;
		
		int[] cols = new int[11];
		for(int i = 0; i < data_str.length; i++){
			if("NameIDaccstatedatastateconfigstatedeviceMTMdatatypeextpoolcap(2^30B)cap(10^9B)cap(blocks)".equals(data_str[i].trim().replace(" ", ""))){
				// 从这一行为标题 , 标题行与数据行中间有一行(===================) 故数据开始行为 i + 2
				beginRow = i + 2;
				System.out.println(beginRow+"=======beginRow======");
				
				System.out.println(data_str[i].trim().indexOf("Name")+"========Array=========");
				cols[0] = data_str[i].trim().indexOf("Name");
				System.out.println(data_str[i].trim().indexOf("ID")+"========State=========");
				cols[1] = data_str[i].trim().indexOf("ID");
				System.out.println(data_str[i].trim().indexOf("accstate")+"========Data=========");
				cols[2] = data_str[i].trim().indexOf("accstate");
				System.out.println(data_str[i].trim().indexOf("datastate")+"========RAIDtype=========");
				cols[3] = data_str[i].trim().indexOf("datastate");
				System.out.println(data_str[i].trim().indexOf("configstate")+"========arsite=========");
				cols[4] = data_str[i].trim().indexOf("configstate");
				System.out.println(data_str[i].trim().indexOf("deviceMTM")+"========Array=========");
				cols[5] = data_str[i].trim().indexOf("deviceMTM");
				System.out.println(data_str[i].trim().indexOf("datatype")+"========Array=========");
				cols[6] = data_str[i].trim().indexOf("datatype");
				System.out.println(data_str[i].trim().indexOf("extpool")+"========Array=========");
				cols[7] = data_str[i].trim().indexOf("extpool");
				System.out.println(data_str[i].trim().indexOf("cap (2^30B)")+"========Array=========");
				cols[8] = data_str[i].trim().indexOf("cap (2^30B)");
				System.out.println(data_str[i].trim().indexOf("cap (10^9B)")+"========Array=========");
				cols[9] = data_str[i].trim().indexOf("cap (10^9B)");
				System.out.println(data_str[i].trim().indexOf("cap (blocks)")+"========Array=========");
				cols[10] = data_str[i].trim().indexOf("cap (blocks)");
				
			}
			
			if("dscli>".equals(data_str[i].trim().replace(" ", ""))){
				endRow = i;
				System.out.println(endRow+"=======endRow======");
			}
		}
		
		List list = new ArrayList();
		if(beginRow != -1 && endRow != -1){
			for(int i = beginRow ; i < endRow ; i++){
				String str = data_str[i];
				String[] str_split = str.split("(" + (char)32 + "|" + (char)9 + ")+");
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
//				for(int j = 0; j < str_split.length ; j++){
//					if(j==0)
//						hashtable.put("Name", str_split[j].trim());
//					if(j==1)
//						hashtable.put("ID", str_split[j].trim());
//					if(j==2)
//						hashtable.put("accstate", str_split[j].trim());
//					if(j==3)
//						hashtable.put("datastate", str_split[j].trim());
//					if(j==4)
//						hashtable.put("configstate", str_split[j].trim());
//					if(j==5)
//						hashtable.put("deviceMTM", str_split[j].trim());
//					if(j==6)
//						hashtable.put("datatype", str_split[j].trim());	
//					if(j==7)
//						hashtable.put("extpool", str_split[j].trim());
//					if(j==8)
//						hashtable.put("cap (2^30B)", str_split[j].trim());	
//					if(j==9)
//						hashtable.put("cap (10^9B)", str_split[j].trim());
//					if(j==10)
//						hashtable.put("cap (blocks)", str_split[j].trim());
//				}
				
				hashtable.put("Name", str.substring(cols[0], cols[1]));
				hashtable.put("ID", str.substring(cols[1], cols[2]));
				hashtable.put("accstate", str.substring(cols[2], cols[3]));
				hashtable.put("datastate", str.substring(cols[3], cols[4]));
				hashtable.put("configstate", str.substring(cols[4], cols[5]));
				hashtable.put("deviceMTM", str.substring(cols[5], cols[6]));
				hashtable.put("datatype", str.substring(cols[6], cols[7]));	
				hashtable.put("extpool", str.substring(cols[7], cols[8]));
				hashtable.put("cap (2^30B)", str.substring(cols[8], cols[9]));	
				hashtable.put("cap (10^9B)", str.substring(cols[9], cols[10]));
				hashtable.put("cap (blocks)", str.substring(cols[10]));
				list.add(hashtable);
			}
		}
		return list;
	}
	
	public void parseLsvolgrp(String command , StringBuffer dataBuffer){
		data.put("lsvolgrp", getLsvolgrpData(dataBuffer));
	}
	
	public List getLsvolgrpData(StringBuffer dataBuffer){
		String[] data_str = dataBuffer.toString().split("\r\n");
		int beginRow = -1;
		int endRow = -1;
		
		int[] cols = new int[3];
		for(int i = 0; i < data_str.length; i++){
			if("NameIDType".equals(data_str[i].trim().replace(" ", ""))){
				// 从这一行为标题 , 标题行与数据行中间有一行(===================) 故数据开始行为 i + 2
				beginRow = i + 2;
				System.out.println(beginRow+"=======beginRow======");
				
				System.out.println(data_str[i].trim().indexOf("Name")+"========Array=========");
				cols[0] = data_str[i].trim().indexOf("Name");
				System.out.println(data_str[i].trim().indexOf("ID")+"========State=========");
				cols[1] = data_str[i].trim().indexOf("ID");
				System.out.println(data_str[i].trim().indexOf("Type")+"========Data=========");
				cols[2] = data_str[i].trim().indexOf("Type");
			}
			
			if("dscli>".equals(data_str[i].trim().replace(" ", ""))){
				endRow = i;
				System.out.println(endRow+"=======endRow======");
			}
		}
		
		List list = new ArrayList();
		
		if(beginRow != -1 && endRow != -1){
			for(int i = beginRow ; i < endRow ; i++){
				String str = data_str[i];
				String[] str_split = str.split("(" + (char)32 + "|" + (char)9 + ")+");
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
//				for(int j = 0; j < str_split.length ; j++){
//					if(j==0)
//						hashtable.put("Name", str_split[j].trim());
//					if(j==1)
//						hashtable.put("ID", str_split[j].trim());
//					if(j==2)
//						hashtable.put("Type", str_split[j].trim());
//				}
				
				hashtable.put("Name", str.substring(cols[0], cols[1]));
				hashtable.put("ID", str.substring(cols[1], cols[2]));
				hashtable.put("Type", str.substring(cols[2]));
				list.add(hashtable);
			}
		}
		return list;
	}
	
	public void parseLsioport(String command , StringBuffer dataBuffer){
		data.put("lsioport", getLsioportData(dataBuffer));
	}
	
	public List getLsioportData(StringBuffer dataBuffer){
		String[] data_str = dataBuffer.toString().split("\r\n");
		int beginRow = -1;
		int endRow = -1;
		
		int[] cols = new int[6];
		for(int i = 0; i < data_str.length; i++){
			if("IDWWPNStateTypetopoportgrp".equals(data_str[i].trim().replace(" ", ""))){
				// 从这一行为标题 , 标题行与数据行中间有一行(===================) 故数据开始行为 i + 2
				beginRow = i + 2;
				System.out.println(beginRow+"=======beginRow======");
				
				System.out.println(data_str[i].trim().indexOf("ID")+"========Array=========");
				cols[0] = data_str[i].trim().indexOf("ID");
				System.out.println(data_str[i].trim().indexOf("WWPN")+"========State=========");
				cols[1] = data_str[i].trim().indexOf("WWPN");
				System.out.println(data_str[i].trim().indexOf("State")+"========Data=========");
				cols[2] = data_str[i].trim().indexOf("State");
				System.out.println(data_str[i].trim().indexOf("Type")+"========RAIDtype=========");
				cols[3] = data_str[i].trim().indexOf("Type");
				System.out.println(data_str[i].trim().indexOf("topo")+"========arsite=========");
				cols[4] = data_str[i].trim().indexOf("topo");
				System.out.println(data_str[i].trim().indexOf("portgrp")+"========Array=========");
				cols[5] = data_str[i].trim().indexOf("portgrp");
			}
			
			if("dscli>".equals(data_str[i].trim().replace(" ", ""))){
				endRow = i;
				System.out.println(endRow+"=======endRow======");
			}
		}
		
		List list = new ArrayList();
		
		if(beginRow != -1 && endRow != -1){
			for(int i = beginRow ; i < endRow ; i++){
				String str = data_str[i];
				String[] str_split = str.split("(" + (char)32 + "|" + (char)9 + ")+");
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
//				for(int j = 0; j < str_split.length ; j++){
//					if(j==0)
//						hashtable.put("ID", str_split[j].trim());
//					if(j==1)
//						hashtable.put("WWPN", str_split[j].trim());
//					if(j==2)
//						hashtable.put("State", str_split[j].trim());
//					if(j==3)
//						hashtable.put("Type", str_split[j].trim());
//					if(j==4)
//						hashtable.put("topo", str_split[j].trim());
//					if(j==5)
//						hashtable.put("portgrp", str_split[j].trim());
//				}
				hashtable.put("ID", str.substring(cols[0], cols[1]));
				hashtable.put("WWPN", str.substring(cols[1], cols[2]));
				hashtable.put("State", str.substring(cols[2], cols[3]));
				hashtable.put("Type", str.substring(cols[3], cols[4]));
				hashtable.put("topo", str.substring(cols[4], cols[5]));
				hashtable.put("portgrp", str.substring(cols[5]));
				list.add(hashtable);
			}
		}
		return list;
	}
	
	
	public void parseLshostconnect(String command , StringBuffer dataBuffer){
		data.put("lshostconnect", getLshostconnectData(dataBuffer));
	}
	
	public List getLshostconnectData(StringBuffer dataBuffer){
		String[] data_str = dataBuffer.toString().split("\r\n");
		int beginRow = -1;
		int endRow = -1;
		
		int[] cols = new int[8];
		for(int i = 0; i < data_str.length; i++){
			if("NameIDWWPNHostTypeProfileportgrpvolgrpIDESSIOport".equals(data_str[i].trim().replace(" ", ""))){
				// 从这一行为标题 , 标题行与数据行中间有一行(===================) 故数据开始行为 i + 2
				beginRow = i + 2;
				System.out.println(beginRow+"=======beginRow======");
				
				System.out.println(data_str[i].trim().indexOf("Name")+"========Array=========");
				cols[0] = data_str[i].trim().indexOf("Name");
				System.out.println(data_str[i].trim().indexOf("ID")+"========State=========");
				cols[1] = data_str[i].trim().indexOf("ID");
				System.out.println(data_str[i].trim().indexOf("WWPN")+"========Data=========");
				cols[2] = data_str[i].trim().indexOf("WWPN");
				System.out.println(data_str[i].trim().indexOf("HostType")+"========RAIDtype=========");
				cols[3] = data_str[i].trim().indexOf("HostType");
				System.out.println(data_str[i].trim().indexOf("Profile")+"========arsite=========");
				cols[4] = data_str[i].trim().indexOf("Profile");
				System.out.println(data_str[i].trim().indexOf("portgrp")+"========Array=========");
				cols[5] = data_str[i].trim().indexOf("portgrp");
				System.out.println(data_str[i].trim().indexOf("volgrpID")+"========arsite=========");
				cols[6] = data_str[i].trim().indexOf("volgrpID");
				System.out.println(data_str[i].trim().indexOf("ESSIOport")+"========Array=========");
				cols[7] = data_str[i].trim().indexOf("ESSIOport");
			}
			
			if("dscli>".equals(data_str[i].trim().replace(" ", ""))){
				endRow = i;
				System.out.println(endRow+"=======endRow======");
			}
		}
		
		List list = new ArrayList();
		
		if(beginRow != -1 && endRow != -1){
			for(int i = beginRow ; i < endRow ; i++){
				String str = data_str[i];
				String[] str_split = str.split("(" + (char)32 + "|" + (char)9 + ")+");
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
//				for(int j = 0; j < str_split.length ; j++){
//					if(j==0)
//						hashtable.put("Name", str_split[j].trim());
//					if(j==1)
//						hashtable.put("ID", str_split[j].trim());
//					if(j==2)
//						hashtable.put("WWPN", str_split[j].trim());
//					if(j==3)
//						hashtable.put("HostType", str_split[j].trim());
//					if(j==4)
//						hashtable.put("Profile", str_split[j].trim());
//					if(j==5)
//						hashtable.put("portgrp", str_split[j].trim());
//					if(j==6)
//						hashtable.put("volgrpID", str_split[j].trim());
//					if(j==7)
//						hashtable.put("ESSIOport", str_split[j].trim());
//				}
				
				hashtable.put("Name", str.substring(cols[0], cols[1]));
				hashtable.put("ID", str.substring(cols[1], cols[2]));
				hashtable.put("WWPN", str.substring(cols[2], cols[3]));
				hashtable.put("HostType", str.substring(cols[3], cols[4]));
				hashtable.put("Profile", str.substring(cols[4], cols[5]));
				hashtable.put("portgrp", str.substring(cols[5],cols[6]));
				hashtable.put("volgrpID", str.substring(cols[6], cols[7]));
				hashtable.put("ESSIOport", str.substring(cols[7]));
				list.add(hashtable);
			}
		}
		
		return list;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ipaddress = "10.204.109.23";
		String name = "10.204.109.23";
		String username = "admin";
		String password = "passw0rd";
		
		List commandList = new ArrayList();
		
//		commandList.add("lsarraysite -dev IBM.2107-75Z1171");
//		commandList.add("lsarray");
//		commandList.add("lsrank");
//		commandList.add("lsextpool");
//		commandList.add("lsfbvol");
//		commandList.add("lsvolgrp");
//		commandList.add("lsioport");
//		commandList.add("lshostconnect");
//		
		StorageCollection storageCollection = new StorageCollection();
		storageCollection.setParameter(ipaddress, name, username, password);
		storageCollection.init();
		storageCollection.execute(commandList);
		storageCollection.destroy();
	}
	
	
	
	
	
	
}
