package com.afunms.polling.snmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.AclBaseDao;
import com.afunms.config.model.AclBase;
import com.afunms.config.model.AclDetail;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;

public class LoadTelnetAclFile {
	public Hashtable<String, List<?>> dealCfgData(String result,String filename,String ip,String type){
		Hashtable<String, List<?>> alldata=new Hashtable<String, List<?>>();
		
//		 List<AclBase> baseList=new ArrayList<AclBase>();
//		 List<AclDetail> detailList=new ArrayList<AclDetail>();
//		 List<AclMatchVal> valList=new ArrayList<AclMatchVal>();
		 result=loadFile(filename);
		this.parsePolicy(ip,type,result,alldata);
		 
		
		 try{
			 Host node = (Host)PollingEngine.getInstance().getNodeByIP(ip);
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET,type,"matches");
				for(int i = 0 ; i < list.size(); i++){
					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.checkData(node,alldata,"net",type,alarmIndicatorsnode);
				}
			
		    }catch(Exception e)
					{
		    	e.printStackTrace();
		    }
		 return alldata;
	}
	
	 public synchronized void  parsePolicy(String ip,String type,String content,Hashtable<String,List<?>> alldata) {
			String[] totalLines = content.split("\n");
			if (totalLines!=null) {
				int nextId=1;
				AclBaseDao baseDao=null;
				Vector<String> nameVec=null;
				HashMap<String,Integer> keyVal=null;
				try {
					baseDao=new AclBaseDao();
					nextId=baseDao.getNextID();
					nameVec=baseDao.getNames(ip);
					keyVal=baseDao.getKeyVal(ip);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					baseDao.close();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String collecttime = sdf.format(new Date());
				List<AclBase> baseList=new ArrayList<AclBase>();
				List<AclDetail> detailList=new ArrayList<AclDetail>();
				Hashtable hash=ShareData.getAclHash(ip);
				if (hash==null)hash=new Hashtable();
				String baseName="";
				int baseId=1;
				boolean flag=false;
				
				for (int i = 0; i < totalLines.length; i++) {
					if(type.equals("cisco")){
					flag=false;
					if (totalLines[i].indexOf("Standard IP access list")>-1||totalLines[i].indexOf("Extended IP access list")>-1) {
						baseName=totalLines[i].trim();
						if (!nameVec.contains(totalLines[i].trim())) {
							baseId=nextId;
							AclBase base=new AclBase();
							base.setId(baseId);
							base.setIpaddres(ip);
							base.setName(baseName);
							baseList.add(base);
							nextId++;
							flag=true;
						}else {
							if(keyVal!=null&&keyVal.containsKey(baseName))
							baseId=keyVal.get(baseName);
						}
						
					}else if (totalLines[i].indexOf("permit")>-1||totalLines[i].indexOf("deny")>-1) {
						
						int index=0;
						if(totalLines[i].indexOf("permit")>-1){
							index=totalLines[i].indexOf("permit");
						}else if(totalLines[i].indexOf("deny")>-1){
							index=totalLines[i].indexOf("deny");
						}
						int value=0;
						int matches=0;
						int status=0;
						String name=totalLines[i].substring(0,index).trim();
						String desc="";
						if(totalLines[i].indexOf("(")>-1){
						 desc=totalLines[i].substring(index,totalLines[i].indexOf("(")).trim();
						}else {
							desc=totalLines[i].trim();
						}
						if(totalLines[i].indexOf("(")>-1&& totalLines[i].indexOf("match")>-1){
						 value=Integer.parseInt(totalLines[i].substring(totalLines[i].indexOf("(")+1,totalLines[i].indexOf("match")).trim());
						
						}
						if (hash.containsKey(ip+":"+baseName+":"+name)) {
							 matches=(Integer) hash.get(ip+":"+baseName+":"+name);
							matches=value-matches;
							status=1;
							
						}
						AclDetail detail=new AclDetail();
						detail.setBaseId(baseId);
						detail.setName(name);
						detail.setDesc(desc);
						detail.setValue(value);
						detail.setMatches(matches);
						detail.setStatus(status);
						detail.setCollecttime(collecttime);
						detailList.add(detail);
						hash.put(ip+":"+baseName+":"+name, value);
						
					}
				}else if (type.equals("h3c")) {

					flag=false;
					if (totalLines[i].indexOf("Basic ACL")>-1) {
						int tempindex=totalLines[i].indexOf(",");
						if (tempindex>-1) {
							baseName=totalLines[i].substring(0,tempindex);
						}else {
							baseName=totalLines[i].trim();
						}
						
						if (!nameVec.contains(baseName)) {
							baseId=nextId;
							
							AclBase base=new AclBase();
							base.setId(baseId);
							base.setIpaddres(ip);
							base.setName(baseName);
							baseList.add(base);
							nextId++;
							flag=true;
						}else {
							if(keyVal!=null&&keyVal.containsKey(baseName))
							baseId=keyVal.get(baseName);
						}
						
					}else if (totalLines[i].indexOf("permit")>-1||totalLines[i].indexOf("deny")>-1) {
						
						int index=0;
						if(totalLines[i].indexOf("permit")>-1){
							index=totalLines[i].indexOf("permit");
						}else if(totalLines[i].indexOf("deny")>-1){
							index=totalLines[i].indexOf("deny");
						}
						int value=0;
						int matches=0;
						int status=0;
						String name=totalLines[i].substring(0,index).trim();
						String desc="";
						if(totalLines[i].indexOf("(")>-1){
						 desc=totalLines[i].substring(index,totalLines[i].indexOf("(")).trim();
						}else {
							desc=totalLines[i].trim();
						}
						if(totalLines[i].indexOf("(")>-1&& totalLines[i].indexOf("match")>-1){
						 value=Integer.parseInt(totalLines[i].substring(totalLines[i].indexOf("(")+1,totalLines[i].indexOf("match")).trim());
						
						}
						if (hash.containsKey(ip+":"+baseName+":"+name)) {
							 matches=(Integer) hash.get(ip+":"+baseName+":"+name);
							matches=value-matches;
							status=1;
							
						}
						AclDetail detail=new AclDetail();
						detail.setBaseId(baseId);
						detail.setName(name);
						detail.setDesc(desc);
						detail.setValue(value);
						detail.setMatches(matches);
						detail.setStatus(status);
						detail.setCollecttime(collecttime);
						detailList.add(detail);
						hash.put(ip+":"+baseName+":"+name, value);
						
					}

					
				
				}
			}
				alldata.put("base", baseList);
				alldata.put("detail", detailList);
				ShareData.setAclHash(ip, hash);
			}
	}
	 
	public String loadFile(String filename) {
		//File f = new File("D:\\2010-03-17\\Tomcat5.0\\webapps\\afunms\\script\\ACL.log");
		File f = new File(filename);
		StringBuffer content = new StringBuffer();
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			while ((s = br.readLine()) != null) {
				content.append(s + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}
}
