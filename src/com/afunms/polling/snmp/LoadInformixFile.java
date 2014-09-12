package com.afunms.polling.snmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.SysUtil;
import com.afunms.initialize.ResourceCenter;
import com.dhcc.pdm.contract;
import com.ibm.as400.util.commtrace.Data;
import com.informix.util.dateUtil;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class LoadInformixFile {

	private Element root;

	public LoadInformixFile(String path) {
		root = getRoot(path);
	}

	public LoadInformixFile(){
	}
	/**
	 * 
	 * @param path
	 *            要解析的sqlServer配置文件的路径
	 * @return xml文件的根元素
	 */
	private Element getRoot(String path) {
		Element root = null;
		SAXBuilder sb = new SAXBuilder();
		try {
			Document dc = sb.build(new FileInputStream(path));
			root = dc.getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
			System.out.println("初始化sqlServer文件出错");
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("初始化sqlServer文件出错");
			throw new RuntimeException(e);
		}
		return root;
	}
	
	
	public Hashtable getInformixConfig(){
		Hashtable table=new Hashtable();
		ArrayList databaselist = new ArrayList();//数据库信息
		ArrayList loglist = new ArrayList();//日志文件信息
		ArrayList spaceList = new ArrayList();//空间信息
		ArrayList configList = new ArrayList();//配置信息
		ArrayList sessionList = new ArrayList();//会话信息
		ArrayList lockList = new ArrayList();//锁信息信息
		ArrayList iolist = new ArrayList();//IO信息
		ArrayList aboutlist = new ArrayList();//概要信息
		
		try {
			List list=XPath.selectNodes(root, "//content/databaselist/column");
			Iterator it=list.iterator();
			while(it.hasNext()){
				Element ele=(Element)it.next();
				List<Element> children=ele.getChildren();
				Hashtable tb=new Hashtable();
				for(Element child:children){
					tb.put(child.getName(),child.getText());
				}
				databaselist.add(tb);
			}
			
			list=XPath.selectNodes(root, "//content/informixlog/column");
			 it=list.iterator();
			while(it.hasNext()){
				Element ele=(Element)it.next();
				List<Element> children=ele.getChildren();
				Hashtable tb=new Hashtable();
				for(Element child:children){
					tb.put(child.getName(),child.getText());
				}
				loglist.add(tb);
			}
			
			list=XPath.selectNodes(root, "//content/informixspaces/column");
			 it=list.iterator();
			 Map<String,Integer>names=new HashMap<String,Integer>();
			while(it.hasNext()){
				Element ele=(Element)it.next();
				List<Element> children=ele.getChildren();
				Hashtable tb=new Hashtable();
				for(Element child:children){
					tb.put(child.getName(),child.getText());
				}
				String name=(String)tb.get("dbspace");
				if(names.get(name)==null){
					spaceList.add(tb);
					String path=(String)tb.get("fname");
		  			int len=path.lastIndexOf("\\");
		  			if(len>0){
		  				tb.put("fname",path.substring(0, len));
		  			}
					names.put(name,1);
				}else{
				  	for(int i=0;i<spaceList.size();i++){
				  		Hashtable col=(Hashtable)spaceList.get(i);
				  		if(name.equals(col.get("dbspace"))){
				  			String v_page_size=(String)col.get("pages_size");
				  			String v_page_userd=(String)col.get("pages_used");
				  			String c_page_size=(String)tb.get("pages_size");
				  			String c_page_userd=(String)tb.get("pages_used");
				  			float f_v_size=0;
				  			if(v_page_size!=null&&!"".equals(v_page_size))
				  				f_v_size=Float.parseFloat(v_page_size);
				  			float f_v_userd=0;
				  			if(v_page_userd!=null&&!"".equals(v_page_userd))
				  				f_v_userd=Float.parseFloat(v_page_userd);
				  			float f_c_size=0;
				  			if(c_page_size!=null&&!"".equals(c_page_size))
				  				f_c_size=Float.parseFloat(c_page_size);
				  			float f_c_userd=0;
				  			if(c_page_userd!=null&&!"".equals(c_page_userd))
				  				f_c_userd=Float.parseFloat(c_page_userd);
				  			float total=f_v_size+f_c_size;
				  			float userd=f_v_userd+f_c_userd;
				  			col.put("pages_size",total);
				  			col.put("pages_used",userd);
				  			col.put("pages_free",total-userd);
				  			col.put("percent_free",(total-userd)*100/total);
				  			//col.put("");
				  			String path=(String)tb.get("fname");
				  			int len=path.lastIndexOf("\\");
				  			if(len>0){
				  				col.put("fname",path.substring(0, len));
				  			}
				  			break;
				  		}
				  	}
				}
			}
			
			list=XPath.selectNodes(root, "//content/configList/column");
			 it=list.iterator();
			while(it.hasNext()){
				Element ele=(Element)it.next();
				List<Element> children=ele.getChildren();
				Hashtable tb=new Hashtable();
				for(Element child:children){
					tb.put(child.getName(),child.getText());
				}
				configList.add(tb);
			}
			
			list=XPath.selectNodes(root, "//content/sessionList/column");
			 it=list.iterator();
			while(it.hasNext()){
				Element ele=(Element)it.next();
				List<Element> children=ele.getChildren();
				Hashtable tb=new Hashtable();
				for(Element child:children){
					tb.put(child.getName(),child.getText());
				}
				sessionList.add(tb);
			}
			
			list=XPath.selectNodes(root, "//content/lockList/column");
			 it=list.iterator();
			while(it.hasNext()){
				Element ele=(Element)it.next();
				List<Element> children=ele.getChildren();
				Hashtable tb=new Hashtable();
				for(Element child:children){
					tb.put(child.getName(),child.getText());
				}
				lockList.add(tb);
			}
			
			list=XPath.selectNodes(root, "//content/iolist/column");
			 it=list.iterator();
			while(it.hasNext()){
				Element ele=(Element)it.next();
				List<Element> children=ele.getChildren();
				Hashtable tb=new Hashtable();
				for(Element child:children){
					tb.put(child.getName(),child.getText());
				}
				iolist.add(tb);
			}
			
			list=XPath.selectNodes(root, "//content/aboutlist/column");
			 it=list.iterator();
			while(it.hasNext()){
				Element ele=(Element)it.next();
				List<Element> children=ele.getChildren();
				Hashtable tb=new Hashtable();
				for(Element child:children){
					tb.put(child.getName(),child.getText());
				}
				aboutlist.add(tb);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		table.put("informixspaces", spaceList);//空间信息
		table.put("informixlog", loglist);//日志信息
		table.put("databaselist", databaselist);//数据库信息
		table.put("configList", configList);//配置信息
		table.put("sessionList", sessionList);//会话信息
		table.put("lockList", lockList);//锁信息信息
		table.put("iolist", iolist);//IO信息
		table.put("aboutlist", aboutlist);//概要文件信息
		return table;
	}
	
	public String getStatus(){
		String str = null;
		try {
			Element element = (Element) XPath.selectSingleNode(root,
					"//content/status");
			str = element.getText();
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return str;
	}
	
	public Hashtable getInformixFile(){
		Hashtable table=new Hashtable();
		table.put("informix",getInformixConfig());
		table.put("status",getStatus());
		return table;
	}
	
	/**
	 * 解析Informix的log文件
	 * @author HONGLI	
	 * @param ipaddress
	 * @return
	 */
	public Hashtable loadInformixFile(String ipaddress){
		Hashtable retHash = new Hashtable();
		StringBuffer fileContent = new StringBuffer();
		String collecttime = "";
		try{
			String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+"_informix.log";				
			//D:\Tomcat5.0\webapps\afunms
//			String filename = "D:/Tomcat5.0/webapps/afunms" + "/linuxserver/"+ipaddress+"_informix.log";				
			File file=new File(filename);
			if(!file.exists()){
				//文件不存在,则产生告警
//				try{
////					createFileNotExistSMS(ipaddress);
//				}catch(Exception e){
//					e.printStackTrace();
//				}
				return retHash;
			}
			file = null;
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			try {
				String strLine = null;
				//读入文件内容
				while((strLine=br.readLine())!=null){
					fileContent.append(strLine + "\n");
					//SysLogger.info(strLine);
				}
			} catch (RuntimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally{
				if(isr != null){
					isr.close();
				}
				if(fis != null){
					fis.close();
				}
				if(br != null){
					br.close();
				}
			}
    		copyFile(ipaddress,getMaxNum(ipaddress));
		} catch (Exception e){
			e.printStackTrace();
		}
    	Pattern tmpPt = null;
    	Matcher mr = null;
    	
	     //----------------解析数据采集时间内容--创建监控项---------------------        	
		tmpPt = Pattern.compile("(cmdbegin:collectiontime)(.*)(cmdbegin:onstat-f)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find()){
			collecttime = mr.group(2);
		}
		if (collecttime != null && collecttime.length()>0 ){
			collecttime = collecttime.trim();
		}
		
		//----------------解析onstat -p--创建监控项---------------------  
		/**
		 *  等待锁的线程数量 lokwaits
		 *  缓冲池溢出数    ovbuff
		 *	数据库锁溢出    ovlock
		 *	死锁数         deadlks
		 *	数据库缓存容量Buffer等待情况  bufwaits
		 *	共享内存读命中率  bufreads_cached
	     *  共享内存写命中率  bufwrits_cached
		 */
		String lokwaits = "";//等待锁的线程数量
		String ovbuff = "";//缓冲池溢出数
		String ovlock = "";//数据库锁溢出
		String deadlks = "";//死锁数
		String bufwaits = "";//数据库缓存容量Buffer等待情况
		String bufreads_cached = "";//共享内存读命中率
		String bufwrits_cached = "";//共享内存写命中率
		String lockContent = "";
		tmpPt = Pattern.compile("(cmdbegin:onstat-p)(.*)(cmdbegin:onstat-d)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find()){
			lockContent = mr.group(2);
		}
		if (lockContent != null && lockContent.length()>0){
			String[] lockInforLineStrings = lockContent.split("\n");
			for(int i=0; i<lockInforLineStrings.length-1; i++){//遍历每一行
				String[] headStrings = lockInforLineStrings[i].split("\\s++");//标题行
				String[] detailStrings = lockInforLineStrings[i+1].split("\\s++");//数据行
				for(int j=0; j<headStrings.length; j++){
					String line = headStrings[j];
					if(line.equals("lokwaits")){
						lokwaits = detailStrings[j];
					}
					if(line.equals("ovbuff")){
						ovbuff = detailStrings[j];
					}
					if(line.equals("ovlock")){
						ovlock = detailStrings[j];
					}
					if(line.equals("deadlks")){
						deadlks = detailStrings[j];
					}
					if(line.equals("bufwaits")){
						bufwaits = detailStrings[j];
					}
					if(line.equals("bufreads")){//共享内存读  %cached
						bufreads_cached = detailStrings[j+1];//detailStrings[j]为读内存的大小
					}
					if(line.equals("bufwrits")){//共享内存读  %cached
						bufwrits_cached = detailStrings[j+1];
					}
				}
			}
		}
		//----------------解析onstat-f--创建监控项---------------------
		/**
		 * 数据库性能监控FOREGROUND WRITE情况
		 */
		String fgWrites = "";//数据库性能监控FOREGROUND WRITE情况
		String stateContent = "";
		tmpPt = Pattern.compile("(cmdbegin:onstat-f)(.*)(cmdbegin:onstat-g)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find()){
			stateContent = mr.group(2);
		}
		if (stateContent != null && stateContent.length()>0){
			String[] stateInforLineStrings = stateContent.split("\n");
			for(int i=0; i<stateInforLineStrings.length-1; i++){//遍历每一行
				String line = stateInforLineStrings[i];//标题行
				String[] detailStrings = stateInforLineStrings[i+1].split("\\s++");//数据行  //此处由于关键字中有空格 ，因此不好用通用算法
				if(line.indexOf("Fg Writes") != -1){
					fgWrites = detailStrings[0];
				}
			}
		}
		
		//----------------解析onstat-g--创建监控项---------------------
		/**
		 * 数据库等待队列
		 */
		String waitingThreadsContent = "";
		List<String> waitingThreads = new ArrayList<String>();//数据库等待队列
		tmpPt = Pattern.compile("(cmdbegin:onstat-g)(.*)(cmdbegin:onstat-p)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find()){
			waitingThreadsContent = mr.group(2);
		}
		if (waitingThreadsContent != null && waitingThreadsContent.length()>0){
			String[] waitingInforLineStrings = waitingThreadsContent.split("\n");
			for(int i=0; i<waitingInforLineStrings.length; i++){//遍历每一行
				String detailString = waitingInforLineStrings[i];//数据行
				String[] detailStrings = detailString.split("\\s++");
				if(detailStrings.length > 8 && detailStrings.length < 15 && !detailStrings[0].equals("tid")){
					waitingThreads.add(waitingInforLineStrings[i].trim());
				}
			}
		}
		retHash.put("waitingThreads", waitingThreads);//数据库等待队列
		retHash.put("fgWrites", fgWrites);//数据库性能监控FOREGROUND WRITE情况
		retHash.put("lokwaits", lokwaits);//等待锁的线程数量
		retHash.put("ovbuff", ovbuff);//缓冲池溢出数
		retHash.put("ovlock", ovlock);//数据库锁溢出 
		retHash.put("deadlks", deadlks);//死锁数 
		retHash.put("bufwaits", bufwaits);//数据库缓存容量Buffer等待情况
		retHash.put("bufreads_cached", bufreads_cached);//共享内存读命中率
		retHash.put("bufwrits_cached", bufwrits_cached);//共享内存写命中率
		retHash.put("collecttime", collecttime);//informix数据库启动时间
		
		return retHash;
	}
	
	
	/**
	 * 解析Informix的bar_act.log文件
	 * @author HONGLI	
	 * @param ipaddress
	 * @return
	 */
	public Hashtable loadInformixBarActLogFile(String ipaddress){
		Hashtable retHash = new Hashtable();
		StringBuffer fileContent = new StringBuffer();
		String collecttime = "";
		try{
//			String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+"_bar_act.log";				
			//D:\Tomcat5.0\webapps\afunms
			String filename = "D:/Tomcat5.0/webapps/afunms" + "/linuxserver/"+ipaddress+"_bar_act.log";				
			File file=new File(filename);
			if(!file.exists()){
				//文件不存在,则产生告警
//				try{
////					createFileNotExistSMS(ipaddress);
//				}catch(Exception e){
//					e.printStackTrace();
//				}
				return retHash;
			}
			file = null;
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			try {
				String strLine = null;
				//读入文件内容
				while((strLine=br.readLine())!=null){
					fileContent.append(strLine + "\n");
					//SysLogger.info(strLine);
				}
			} catch (RuntimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally{
				if(isr != null){
					isr.close();
				}
				if(fis != null){
					fis.close();
				}
				if(br != null){
					br.close();
				}
			}
//    		copyFile(ipaddress,getMaxNum(ipaddress));
		} catch (Exception e){
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//从数据库中读取上次备份的日期  2010-08-20 01:38:15
		String lastBackDate = "2011-05-06 04:05:55";
		//取出nms_informixother最后一条记录
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(ipaddress);
		DBDao dbDao = new DBDao();
		DBVo dbmonitorlist = null;
		try{
			dbmonitorlist = (DBVo)(((ArrayList)dbDao.findByCondition(" where ip_address = '"+ipaddress+"'")).get(0));
			lastBackDate = dbDao.getInformix_nmsbaractBackTime(hex+":"+dbmonitorlist.getDbName());
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally{
			dbDao.close();
		}
		String thisBackDate = "";//此次备份的日期
		List<String> actlogList = new ArrayList<String>();
		//数据库上次0级备份到现在的天数
		String[] lines = fileContent.toString().split("\\n");

		//得到最后一条log信息的日期
		for(int i=lines.length-1; i>0; i--){//反向读取
			if(lines[i] != null && !lines[i].trim().equals("")){
				if(lines[i].split("\\s")[1].matches("(\\d{4})-(\\d{1,2})-(\\d{1,2})") && lines[i].split("\\s")[2].matches("(\\d{2}):(\\d{2}):(\\d{2})")){
					thisBackDate = lines[i].split("\\s")[1] +" "+ lines[i].split("\\s")[2];
					break;
				}
			}
		}
//		int lastBackDateToThisBackDay = 0;
		if(lastBackDate == null || lastBackDate.equals("")){//数据库中未保存过bar_act.log的数据
			//备份全部
			lines = arrybaractlogs(lines);
			retHash.put("backdate", thisBackDate);//此次备份的日期
			retHash.put("baractlogs", lines);
//			retHash.put("lastBackDateToThisBackDay", lastBackDateToThisBackDay+"");//数据库上次0级备份到现在的天数
			return retHash;
		}
		//得到最后一次备份log之后，又新备份的第一个日期
		String newFirstBackDate = "";//新备份的第一个日期
		try {
			for(int i=0; i<lines.length; i++){//反向读取
				if(lines[i] != null && !lines[i].trim().equals("")){
					if(lines[i].split("\\s")[1].matches("(\\d{4})-(\\d{1,2})-(\\d{1,2})") && lines[i].split("\\s")[2].matches("(\\d{2}):(\\d{2}):(\\d{2})")){
						if(sdf.parse(lines[i].split("\\s")[1] +" "+ lines[i].split("\\s")[2]).getTime() > sdf.parse(lastBackDate).getTime()){//log中最后的日期 > 数据库中上次保存的日期
							newFirstBackDate = lines[i].split("\\s")[1] +" "+ lines[i].split("\\s")[2];
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("上一次备份的日期=="+lastBackDate);
//		System.out.println("得到最后一次备份log之后，又新备份的第一个日期=="+newFirstBackDate);
//		System.out.println("本次备份的结束日期=="+thisBackDate);
		//获取最后一次备份时 新添加的log内容
		String fgWrites = "";//数据库性能监控FOREGROUND WRITE情况
		if(newFirstBackDate != null && !newFirstBackDate.equals("")){
			String content = "";
			Pattern tmpPt = null;
			Matcher mr = null;
			tmpPt = Pattern.compile("("+newFirstBackDate+")(.*)",Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if(mr.find()){
				content = mr.group(2);
			}
			//拆分
			String line = null;
			if(content != null && content.length() != 0){
				lines = content.split("\\n");
				lines[0] = newFirstBackDate+lines[0];//补充上被过滤掉的第一行
			}
		}else{
			lines = new String[0];
		}
//		try {
//			long d = (new Date().getTime() - sdf.parse(lastBackDate).getTime())/(24*3600*1000);
//			lastBackDateToThisBackDay = (int)d;
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		lines = arrybaractlogs(lines);
		retHash.put("backdate", thisBackDate);//此次备份的日期
//		retHash.put("lastBackDateToThisBackDay", lastBackDateToThisBackDay+"");//数据库上次0级备份到现在的天数
		retHash.put("baractlogs", lines);//数据库ONBAR日志出现WARN或ERROR信息
		return retHash;
	}
	
	/**
	 * 将不以时间开头的字符串  添加到以时间开头的字符串的结尾
	 * @param lines
	 * @return
	 */
	public String[] arrybaractlogs(String[] lines){
		String[] strings = null;
		Vector<String> tempVector = new Vector<String>();
		for(int i=0; i<lines.length; i++){
			String line = lines[i].trim();
			if(line == null || line.equals("")){
				continue;
			}
			String string = line.split("\\s")[0];
			if(!string.matches("(\\d{4})-(\\d{1,2})-(\\d{1,2})")){
				line = tempVector.get(tempVector.size()-1)+line;
				tempVector.remove(tempVector.size()-1);
				tempVector.add(line);
			}else{
				tempVector.add(line);
			}
		}
		strings = new String[tempVector.size()];
		for(int i=0; i<tempVector.size(); i++){
			strings[i] = tempVector.get(i);
		}
		tempVector = null;
		return strings;
	}
	
	
	public void copyFile(String ipAddress,String max){
		try { 
			String currenttime = SysUtil.getCurrentTime();
			currenttime = currenttime.replaceAll("-", "");
			currenttime = currenttime.replaceAll(" ", "");
			currenttime = currenttime.replaceAll(":", "");
			String ipdir = ipAddress.replaceAll("\\.", "-");
			String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver_bak/"+ipdir;
			File file=new File(filename);
			if(!file.exists())file.mkdir();
	        String cmd   =   "cmd   /c   copy   "+ResourceCenter.getInstance().getSysPath() + "linuxserver\\" + ipAddress + ".log"+" "+ResourceCenter.getInstance().getSysPath() + "linuxserver_bak\\" +ipdir+"\\"+ ipAddress+"-" +currenttime+ ".log";             
	        //SysLogger.info(cmd);
	        Process   child   =   Runtime.getRuntime().exec(cmd);   
	    }catch (IOException e){    
	        e.printStackTrace();
	    }   
	}
	
    public String getMaxNum(String ipAddress){
    	String maxStr = null;
		File logFolder = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/");
   		String[] fileList = logFolder.list();
   		for(int i=0;i<fileList.length;i++) {//找一个最新的文件
   			if(!fileList[i].startsWith(ipAddress)) continue;
   			return ipAddress;
   		}
   		return maxStr;
    }
    
    public static void main(String[] args) {
    	LoadInformixFile loadInformixFile = new LoadInformixFile();
//    	Hashtable hash = loadInformixFile.loadInformixBarActLogFile("127.0.0.1");
//    	String[] strs = (String[])hash.get("baractlogs");
//    	for(String str:strs){
//    		System.out.println(str);
//    	}
    	String[] strString = new String[4];
    	strString[0] = "2011-05-20 13:16:51 1396842  4030516 Could not open XBSA library /usr/tivoli/tsm/client/informix/bin/bsashr10.o, so trying default path.";
    	strString[1] = "2011-05-20 13:16:51 1396842  4030516 An unexpected error occurred:  Could not load module /informix/lib/ibsad001_64.o.";
    	strString[2] = "System error: No such file or directory .";
    	strString[3] = "No such file or directory";
    	String[] str = loadInformixFile.arrybaractlogs(strString);
    	for(String s:str){
    		System.out.println(s);
    	}
    }
}
