package com.afunms.polling.telnet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class AS400Telnet {
	
	private String ip;
	
	private String username;
	
	private String password;
	
	private String loginPrompt;
	
	private String passwordPrompt;
	
	private String shellPrompt;
	
	private TelnetWrapperForAS400 telnetWrapperForAS400;
	
	
	

	/**
	 * 
	 */
	public AS400Telnet() {
		// TODO Auto-generated constructor stub
	}
	
	

	


	/**
	 * @param ip
	 * @param username
	 * @param password
	 * @param loginPrompt
	 * @param passwordPrompt
	 * @param shellPrompt
	 */
	public AS400Telnet(String ip, String username, String password,
			String loginPrompt, String passwordPrompt, String shellPrompt) {
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.loginPrompt = loginPrompt;
		this.passwordPrompt = passwordPrompt;
		this.shellPrompt = shellPrompt;
	}






	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	
	/**
	 * @return the loginPrompt
	 */
	public String getLoginPrompt() {
		return loginPrompt;
	}






	/**
	 * @param loginPrompt the loginPrompt to set
	 */
	public void setLoginPrompt(String loginPrompt) {
		this.loginPrompt = loginPrompt;
	}






	/**
	 * @return the passwordPrompt
	 */
	public String getPasswordPrompt() {
		return passwordPrompt;
	}






	/**
	 * @param passwordPrompt the passwordPrompt to set
	 */
	public void setPasswordPrompt(String passwordPrompt) {
		this.passwordPrompt = passwordPrompt;
	}






	/**
	 * @return the shellPrompt
	 */
	public String getShellPrompt() {
		return shellPrompt;
	}






	/**
	 * @param shellPrompt the shellPrompt to set
	 */
	public void setShellPrompt(String shellPrompt) {
		this.shellPrompt = shellPrompt;
	}






	public boolean init(String ip, String username, String password,
			String loginPrompt, String passwordPrompt, String shellPrompt){
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.loginPrompt = loginPrompt;
		this.passwordPrompt = passwordPrompt;
		this.shellPrompt = shellPrompt;
		return false;
	}
	
	public boolean connect(){
		boolean result = false;
		telnetWrapperForAS400 = new TelnetWrapperForAS400();
		try {
			telnetWrapperForAS400.connect(ip, 23);
			result = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	public boolean login(){
		boolean result = false;
		try {
			telnetWrapperForAS400.login(username , password , loginPrompt  , null , shellPrompt);
			telnetWrapperForAS400.setPrompt(shellPrompt);
			telnetWrapperForAS400.send("");
			result = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	
	public Hashtable execute(Hashtable gatherhash){
		Hashtable hashtable = new Hashtable(); 
		if(gatherhash.containsKey("disk")){
			Vector diskVector = getWrkdsksts();
			hashtable.put("AS400disk", diskVector);
		}
		
		if(gatherhash.containsKey("system")){
			Hashtable hashtable2 = getWrksyssts();
			hashtable.put("SystemStatus", hashtable2);
		}
		
		if(gatherhash.containsKey("service")){
			Vector serviceVector = getService();
			hashtable.put("service", serviceVector);
		}
		
		if(gatherhash.containsKey("network")){
			Vector networkVector = getNetwork();
			hashtable.put("network", networkVector);
		}
		
		if(gatherhash.containsKey("hardware")){
			Vector hardwareVector = getHardware();
			hashtable.put("hardware", hardwareVector);
		}
		
		
		try {
			
			String result1 = new String(telnetWrapperForAS400.write(""));
			
			System.out.println(ip+"---------AS400:result1========" + result1);
			
            String result2 = new String(telnetWrapperForAS400.write(""));
			
			System.out.println(ip+"---------AS400:result2========" + result2);
			
			String result3 = new String(telnetWrapperForAS400.write(57,48,this.shellPrompt));
			   
			System.out.println(ip+"---------AS400:result3========" + result3);
			telnetWrapperForAS400.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hashtable;
	}
	
	
	public Vector getWrkdsksts(){
		Vector vector = new Vector();
		
		try {
            String result0 = new String(telnetWrapperForAS400.write(""));
			System.out.println(ip+"disk---------AS400:result0========" + result0);
			String result = new String(telnetWrapperForAS400.write("wrkdsksts"));
			
			System.out.println(ip+"disk---------AS400:result========" + result);
			
			if(result.indexOf("90. Sign off")!=-1){
				result = new String(telnetWrapperForAS400.write(""));
			}
			System.out.println(ip+"disk---------AS400:result1========" + result);
			String value = StringUtilForAS400.filterCode(result);
			
			System.out.println(ip+"disk---------AS400:value========" + value);
			vector = parseWrkdsksts(value);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vector;
	}
	
	public Vector parseHardware(String result){
		Vector vector = new Vector();
		
		if(result == null || result.length() == 0){
			return vector;
		}
		
//		if(result.indexOf("Resource")<0 || result.indexOf("Status")<0 || result.indexOf("Text")<0 || result.indexOf("Type-model")<0){
//			return vector;
//		}
//		System.out.println(result.trim().replace("   ", "*"));
//		System.out.println("(" + (char)32 + "|" + (char)9 + ")+");
		String[] values = result.trim().split("\\s{2,12}");
		
		if(values== null || values.length ==0 ){
			return vector;
		}
		
		Hashtable valuehashtable = new Hashtable();
		try {
			for(int i = 0 ; i < values.length ; i++){
				String value = values[i].trim();
				
				int j = i%4;
				
//				System.out.println(value+"=====i========" + i + "========j======" + j); 
				if( j == 0 ){
					valuehashtable.put("Resource", value);
					continue;
				}
				if( j == 1 ){
					valuehashtable.put("Type-model", value);
					continue;
				}
				if( j == 2 ){
					valuehashtable.put("Status", value);
					continue;
				}
				if( j == 3 ){
					valuehashtable.put("Text", value);
				}
				vector.add(valuehashtable);
				valuehashtable = new Hashtable();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error!");
		}
		
		System.out.println(vector.size()+"===========Hardwarevector.size=================");
		return vector;
	}
	
	public Vector parseService(String result){
		Vector vector = new Vector();
		
		if(result == null || result.length() == 0){
			return vector;
		}
		
		if(result.indexOf("Option")<0 || result.indexOf("Feature")<0 || result.indexOf("Description")<0){
			return vector;
		}
		String data = "";
		if(result.indexOf("More...")!=-1){
			data = result.substring(result.indexOf("Description")+11, result.indexOf("More..."));
		}
		if(result.indexOf("Bottom")!=-1){
			data = result.substring(result.indexOf("Description")+11, result.indexOf("Bottom"));
		}
		
		String[] values = data.trim().split("\\s{2,}");
		
		if(values== null || values.length ==0 ){
			return vector;
		}
		
		Hashtable valuehashtable = new Hashtable();
		try {
			for(int i = 0 ; i < values.length ; i++){
				String value = values[i].trim();
				
				int j = i%4;
				
//				System.out.println(value+"=====i========" + i + "========j======" + j); 
				if( j == 0 ){
					valuehashtable.put("ID", value);
					continue;
				}
				if( j == 1 ){
					valuehashtable.put("Option", value);
					continue;
				}
				if( j == 2 ){
					valuehashtable.put("Feature", value);
					continue;
				}
				if( j == 3 ){
					valuehashtable.put("Description", value);
				}
				vector.add(valuehashtable);
				valuehashtable = new Hashtable();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error!");
		}
		
		System.out.println(vector.size()+"===========Servicevector.size=================");
		return vector;
	}
	public Vector parseNetwork(String result){
		Vector vector = new Vector();
		
		if(result == null || result.length() == 0){
			return vector;
		}
		
		if(result.indexOf("Resource")<0 || result.indexOf("Status")<0 || result.indexOf("Text")<0){
			return vector;
		}
		String data = "";
		if(result.indexOf("More...")!=-1){
			data = result.substring(result.indexOf("Text")+4, result.indexOf("More..."));
		}
		if(result.indexOf("Bottom")!=-1){
			data = result.substring(result.indexOf("Text")+4, result.indexOf("Bottom"));
		}
		
		String[] values = data.trim().split("\\s{2,}");
		
		if(values== null || values.length ==0 ){
			return vector;
		}
		
		Hashtable valuehashtable = new Hashtable();
		try {
			for(int i = 0 ; i < values.length ; i++){
				String value = values[i].trim();
				
				int j = i%4;
				
//				System.out.println(value+"=====i========" + i + "========j======" + j); 
				if( j == 0 ){
					valuehashtable.put("Resource", value);
					continue;
				}
				if( j == 1 ){
					valuehashtable.put("Type", value);
					continue;
				}
				if( j == 2 ){
					valuehashtable.put("Status", value);
					continue;
				}
				if( j == 3 ){
					valuehashtable.put("Text", value);
				}
				vector.add(valuehashtable);
				valuehashtable = new Hashtable();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error!");
		}
		
		System.out.println(vector.size()+"===========Networkvector.size=================");
		return vector;
	}
	
	
	public Vector parseWrkdsksts(String result){
		System.out.println("===========result================="+result);
		
		Vector vector = new Vector();
		
		if(result == null || result.length() == 0){
			return vector;
		}
		if(result.indexOf("Size")<0 || result.indexOf("Busy")<0 || result.indexOf("Command")<0){
			return vector;
		}
		System.out.println("22222222222222222222222");
		String data = "";
		if(result.indexOf("More...")!=-1){
			data = result.substring(result.indexOf("Busy")+4, result.indexOf("More..."));
		}
		if(result.indexOf("Bottom")!=-1){
			data = result.substring(result.indexOf("Busy")+4, result.indexOf("Bottom"));
		}
		
		String[] values = data.trim().split("(" + (char)32 + "|" + (char)9 + ")+");
		
		if(values== null || values.length ==0 ){
			return vector;
		}
		System.out.println("333333333333333333333333333");
		Hashtable valuehashtable = new Hashtable();
		
		try {
			for(int i = 0 ; i < values.length ; i++){
				String value = values[i].trim();
				
				int j = i%11;
				
//				System.out.println(value+"=====i========" + i + "========j======" + j); 
				if( j == 0 ){
					valuehashtable.put("Unit", value);
					continue;
				}
				if( j == 1 ){
					valuehashtable.put("Type", value);
					continue;
				}
				if( j == 2 ){
					valuehashtable.put("Size(M)", value);
					continue;
				}
				if( j == 3 ){
					valuehashtable.put("%Used", value);
					continue;
				}
				if( j == 4 ){
					valuehashtable.put("I/O Rqs", value);
					continue;
				}
				if( j == 5 ){
					valuehashtable.put("Request Size(K)", value);
					continue;
				}
				if( j == 6 ){
					valuehashtable.put("Read Rqs", value);
					continue;
				}
				if( j == 7 ){
					valuehashtable.put("Write Rqs", value);
					continue;
				}
				if( j == 8 ){
					valuehashtable.put("Read(K)", value);
					continue;
				}
				if( j == 9 ){
					valuehashtable.put("Write(K)", value);
					continue;
				}
				if( j == 10 ){
					valuehashtable.put("%Busy", value);
				}
				vector.add(valuehashtable);
				valuehashtable = new Hashtable();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error!");
		}
		
		System.out.println(vector.size()+"===========vector.size(=================");
		return vector;
	}
	
	/**
	 * 此方法只获取其 cpu 和 db 的数据 其他数据被丢弃
	 * @return
	 */
	public Hashtable getWrksyssts(){
		Hashtable hashtable = new Hashtable();
		
		try {
			
            String result0 = new String(telnetWrapperForAS400.write(""));
			
			System.out.println(ip+"cpu---------AS400:result0========" + result0);
			
			String result = new String(telnetWrapperForAS400.write("wrksyssts"));
			
			System.out.println(ip+"cpu---------AS400:result========" + result);
			
			if(result.indexOf("90. Sign off")!=-1){
				result = new String(telnetWrapperForAS400.write(""));
			}
			
			System.out.println(ip+"cpu---------AS400:result1========" + result);
			
			String value = StringUtilForAS400.filterCode(result);
			
			System.out.println(ip+"cpu---------AS400:value========" + value);
			    
			hashtable = parseWrksyssts(value);    
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hashtable;
	}
	
	/**
	 * 此方法获取服务信息的数据
	 * @return
	 */
	public Vector getService(){
		Vector vector = new Vector();
		
		try {
			
            String result0 = new String(telnetWrapperForAS400.write(""));
			
			if(result0.indexOf("90. Sign off")!=-1){
				result0 = new String(telnetWrapperForAS400.write(""));
			}
			
			System.out.println(ip+"service---------AS400:result0========" + result0);
			
			String result = new String(telnetWrapperForAS400.write("dspsfwrsc"));
			
			System.out.println(ip+"service---------AS400:result========" + result);
			
			if(result.indexOf("90. Sign off")!=-1){
				result = new String(telnetWrapperForAS400.write(""));
			}
			
			System.out.println(ip+"service---------AS400:result1========" + result);
			
			String value = StringUtilForAS400.filterCode(result);
			
			System.out.println(ip+"service---------AS400:value========" + value);
			    
			vector = parseService(value);    
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vector;
	}
	/**
	 * 此方法获取网卡 的数据
	 * @return
	 */
	public Vector getNetwork(){
		Vector vector = new Vector();
		
		try {
			
            String result0 = new String(telnetWrapperForAS400.write(""));
			
			if(result0.indexOf("90. Sign off")!=-1){
				result0 = new String(telnetWrapperForAS400.write(""));
			}
			
			System.out.println(ip+"network---------AS400:result0========" + result0);
			
			String result = new String(telnetWrapperForAS400.write("wrkhdwrsc",32,42,"cmn","Bottom"));
			
			System.out.println(ip+"network---------AS400:result========" + result);
			
//			if(result.indexOf("90. Sign off")!=-1){
//				result = new String(telnetWrapperForAS400.write(""));
//			}
			
			System.out.println(ip+"network---------AS400:result1========" + result);
			
			String value = StringUtilForAS400.filterCode(result);
			
			System.out.println(ip+"network---------AS400:value========" + value);
			    
			vector = parseNetwork(value);    
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vector;
	}
	
	/**
	 * 此方法获取硬件信息
	 * @return
	 */
	public Vector getHardware(){
		Vector vector = new Vector();
		
		try {
			
            String result0 = new String(telnetWrapperForAS400.write(""));
			
			System.out.println(ip+"Hardware---------AS400:result0========" + result0);
			
			String result = new String(telnetWrapperForAS400.write("dsphdwrsc",32,42,"ahw","More..."));
			
			System.out.println(ip+"Hardware---------AS400:result========" + result);
			
//			if(result.indexOf("90. Sign off")!=-1){
//				result = new String(telnetWrapperForAS400.write(""));
//			}
			String str = StringUtilForAS400.filterCode(result);
			String data = "";
			if(result.indexOf("More...")!=-1){
				data = str.substring(str.indexOf("Text")+4, str.indexOf("More..."));
			}
			if(result.indexOf("Bottom")!=-1){
				data = str.substring(str.indexOf("Text")+4, str.indexOf("Bottom"));
			}
//			while(result.indexOf("Bottom")<0){
//				System.out.println(result.indexOf("Bottom")<0);
//				result = new String(telnetWrapperForAS400.write(34,"More..."));
//				str = StringUtilForAS400.filterCode(result);
//				System.out.println("str============"+str);
//				if(result.indexOf("More...")!=-1){
//					data = data + str.substring(str.indexOf("Text")+4, str.indexOf("More..."));
//				}
//				if(result.indexOf("Bottom")!=-1){
//					data = data + str.substring(str.indexOf("Text")+4, str.indexOf("Bottom"));
//				}
//			}
			
			System.out.println(ip+"Hardware---------AS400:result1========" + data);
			
			String value = StringUtilForAS400.filterCode(data);
			
			System.out.println(ip+"Hardware---------AS400:value========" + value);
			
			    
			vector = parseHardware(value);    
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vector;
	}
	
	public Hashtable parseWrksyssts(String result){
		Hashtable hashtable = new Hashtable();
		if(result == null || result.length() == 0){
			return hashtable;
		}
		
		String cpuValue = "0";
		try {
			if(result.indexOf("Auxiliary storage:")>0){
				if(result.indexOf("% CPU used . . . . . . . :")>0 || result.indexOf("Auxiliary storage:")>0){
					int cpuIndex = result.indexOf("% CPU used . . . . . . . :");
					int cpulength = "% CPU used . . . . . . . :".length();
					cpuValue = result.substring(cpuIndex + cpulength, result.indexOf("Auxiliary storage:")).trim();
					if(cpuValue != null && cpuValue.indexOf(".") == 0){
						cpuValue = "0" + cpuValue;
					}
				}
			}
			if(result.indexOf("System ASP . . . . . . . :")>0){
				if(result.indexOf("% CPU used . . . . . . . :")>0 || result.indexOf("System ASP . . . . . . . :")>0){
					int cpuIndex = result.indexOf("% CPU used . . . . . . . :");
					int cpulength = "% CPU used . . . . . . . :".length();
					cpuValue = result.substring(cpuIndex + cpulength, result.indexOf("System ASP . . . . . . . :")).trim();
					if(cpuValue != null && cpuValue.indexOf(".") == 0){
						cpuValue = "0" + cpuValue;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		hashtable.put("cpu", cpuValue);
		
		
		String DBCapabilityValue = "0";
		
		try {
			if(result.indexOf("% system ASP used  . . . :")>0){
				if(result.indexOf("% DB capability  . . . . :")>0 || result.indexOf("% system ASP used  . . . :")>0){
					int DBCapabilityIndex = result.indexOf("% DB capability  . . . . :");
					int DBCapabilitylength = "% DB capability  . . . . :".length();
					DBCapabilityValue = result.substring(DBCapabilityIndex + DBCapabilitylength, result.indexOf("% system ASP used  . . . :")).trim();
					if(DBCapabilityValue != null && DBCapabilityValue.indexOf(".") == 0){
						DBCapabilityValue = "0" + DBCapabilityValue;
					}
				}
			}
			if(result.indexOf("System ASP . . . . . . :")>0){
				if(result.indexOf("% DB capability  . . . . :")>0 || result.indexOf("System ASP . . . . . . :")>0){
					int DBCapabilityIndex = result.indexOf("% DB capability  . . . . :");
					int DBCapabilitylength = "% DB capability  . . . . :".length();
					DBCapabilityValue = result.substring(DBCapabilityIndex + DBCapabilitylength, result.indexOf("System ASP . . . . . . :")).trim();
					if(DBCapabilityValue != null && DBCapabilityValue.indexOf(".") == 0){
						DBCapabilityValue = "0" + DBCapabilityValue;
					}  
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		hashtable.put("DBCapability", DBCapabilityValue);
		
		Vector vector = new Vector();
		String data = "";
		if(result.indexOf("Bottom")!=-1){
			data = result.substring(result.indexOf("Pages")+19, result.indexOf("Bottom"));
		}
		double mem = 0.0;
        String[] values = data.trim().split("\\s{2,}");
		
		Hashtable valuehashtable = new Hashtable();
		try {
			for(int i = 0 ; i < values.length ; i++){
				String value = values[i].trim();
				
				int j = i%8;
				
//				System.out.println(value+"=====i========" + i + "========j======" + j); 
				
				if( j == 0 ){
					valuehashtable.put("Pool", value);
					continue;
				}
				if( j == 1 ){
					valuehashtable.put("Pool_Size(M)", value);
					mem = mem + Double.parseDouble(value);
					continue;
				}
				if( j == 2 ){
					valuehashtable.put("Reserved_Size(M)", value);
					continue;
				}
				if( j == 3 ){
					valuehashtable.put("Max_Active", value);
					continue;
				}
				if( j == 4 ){
					valuehashtable.put("DB_Fault", value);
					continue;
				}
				if( j == 5 ){
					valuehashtable.put("DB_Pages", value);
					continue;
				}
				if( j == 6 ){
					valuehashtable.put("Non-DB_Fault", value);
					continue;
				}
				if( j == 7 ){
					valuehashtable.put("Non-DB_Pages", value);
				}
				vector.add(valuehashtable);
				valuehashtable = new Hashtable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		hashtable.put("db_pool", vector);
		
		hashtable.put("memory", mem);
		
		return hashtable;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar tempCal = Calendar.getInstance();
//		Date cc = tempCal.getTime();
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		String time = sdf.format(cc);
//		System.out.println(time);
		// TODO Auto-generated method stub
//		TelnetWrapperForAS400 telnetWrapperForAS400 = new TelnetWrapperForAS400();
//		String user = "WUSER";
//		String pwd = "DEMO2PWD";
//		String ip = "iSeriesD.DFW.IBM.COM";
//		try {
//			telnetWrapperForAS400.connect(ip, 23);
//			telnetWrapperForAS400.login(user , pwd , "2005." , null , "2005.");
//			telnetWrapperForAS400.setPrompt("===>");
//			telnetWrapperForAS400.send("");
//			String result3 = new String(telnetWrapperForAS400.write("wrkactjob"));
//			String result4 = telnetWrapperForAS400.send(telnetWrapperForAS400.write((char)12));
//			System.out.println(StringUtilForAS400.filterCode(result3));
//			System.out.println(StringUtilForAS400.filterCode(result4));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		String str="你  好,             Fuck   You 1";
		System.out.println(str);
		String[] values = str.trim().split("\\s{1,}");
		for(int i=0;i<values.length;i++){
			System.out.println(values[i]);
		}
		  System.out.println(str.replaceAll("\\s{2,12}","&")); 
	}
	
	

}
