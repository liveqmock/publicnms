package com.afunms.application.util;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleEntity;
import com.afunms.capreport.model.ReportValue;
import com.afunms.capreport.model.StatisNumer;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.report.amchart.AmChartTool;

public class ReportHelper {
	
public HashMap getAllValue(String ids,String startTime,String toTime) {
	HashMap allValueMap=new HashMap();
	List<StatisNumer> gridList=new ArrayList<StatisNumer>();
	List<ReportValue> valueList=new ArrayList<ReportValue>();
	String[] idValue=this.getIdValue(ids);
	
  	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
  	I_HostCollectData hostmanager = new HostCollectDataManager();
  	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
//  	if(startTime == null){
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		startTime = sdf.format(new Date())+" 00:00:00";
//	}else if(startTime != null && !startTime.contains("00:00:00")){
//		startTime=startTime+" 00:00:00";
//	}
//
//	if(toTime == null){
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		toTime = sdf.format(new Date())+ " 23:59:59";
//	}else if(toTime != null && !toTime.contains("23:59:59")){
//		toTime=toTime+" 23:59:59";
//	}
	List<List> pingList=new ArrayList<List>();
	List<List> list=new ArrayList<List>();
	List<List> memList=new ArrayList<List>();
	List<List> utilInList=new ArrayList<List>();
	List<List> utilOutList=new ArrayList<List>();
	List<List> diskList=new ArrayList<List>();
	
	List<String> pingipList=new ArrayList<String>();
	List<String> ipList=new ArrayList<String>();
	List<String> memipList=new ArrayList<String>();
	List<String> portipList=new ArrayList<String>();
	List<String> diskipList=new ArrayList<String>();
	if (idValue!=null&&idValue.length>0) {
		
		for (int i = 0; i < idValue.length; i++) {
			 if (idValue[i].indexOf("ping")>=0) {

				String pingvalue="0";
				String ip=idValue[i].replace("ping", "");
				
				try{
				Hashtable pinghash = hostmanager.getCategory(ip,"Ping","ConnectUtilization",startTime,toTime);
				if ("0".equals(runmodel)) {
					
					Vector pingData = (Vector) ShareData.getPingdata().get(ip);
					if (pingData != null && pingData.size() > 0) {
						Pingcollectdata ping = (Pingcollectdata) pingData.get(0);
						
						pingvalue = ping.getThevalue();
						
					}
				    }else {
				    	
						Hashtable curPinghash = hostmanager.getCurByCategory(ip, "Ping", "ConnectUtilization");
						pingvalue=(String)curPinghash.get("pingCur");
				    }
				if (pinghash!=null&&pinghash.size()>0) {
					 List pingDataList=(List)pinghash.get("list");
					 String pingAvg=(String) pinghash.get("avgpingcon");
					        
					 String pingMin=(String) pinghash.get("pingmax");
					 StatisNumer voNumer=new StatisNumer();
					 voNumer.setIp(ip);
					 voNumer.setType("gridPing");
					 voNumer.setCurrent(pingvalue);
					 voNumer.setMininum(pingMin);
					 voNumer.setAverage(pingAvg);
					 gridList.add(voNumer);
					if(pingDataList!=null&&pingDataList.size()>0)
					 pingList.add(pingDataList);
					pingipList.add(ip);
				}
				
				
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}else if(idValue[i].indexOf("cpu")>=0) {
				
				String ip=idValue[i].replace("cpu", "");
				
				String cpuvalue="0";
				if ("0".equals(runmodel)) {
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
							ip);
					
					if (ipAllData != null) {
					
						Vector cpuV = (Vector) ipAllData.get("cpu");
						if (cpuV != null && cpuV.size() > 0) {

							CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
							cpuvalue =cpu.getThevalue();
							
						}
					}
				}else {
					Hashtable curCpuhash=null;
					try {
						curCpuhash = hostmanager.getCurByCategory(ip, "CPU", "Utilization");
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					cpuvalue=(String)curCpuhash.get("pingCur");
					
					}
				try{
				Hashtable cpuhash = hostmanager.getCategory(ip,"CPU","Utilization",startTime,toTime);
				
				 List cpuList=(List)cpuhash.get("list");
				 String cpumax="0";
				 String cpuavg="0";
					if(cpuhash.get("max")!=null){
					  cpumax = (String)cpuhash.get("max");
					}
					if(cpuhash.get("avgcpucon")!=null){
						cpuavg = (String)cpuhash.get("avgcpucon");
				       }
					 StatisNumer voNumer=new StatisNumer();
					 voNumer.setIp(ip);
					 voNumer.setType("gridCpu");
					 voNumer.setCurrent(cpuvalue);
					 voNumer.setMaximum(cpumax);
					 voNumer.setAverage(cpuavg);
					 gridList.add(voNumer);
					 if (cpuList!=null&&cpuList.size()>0)
					  list.add(cpuList);
					ipList.add(ip);
				}catch(Exception e){
					e.printStackTrace();
				}
				}else if (idValue[i].indexOf("mem")>=0) {
			
			
			String ip=idValue[i].replace("mem", "");
			
			try{
				Hashtable memhash = hostmanager.getNetMemeory(ip,"Memory",startTime,toTime);
                if(memhash==null||memhash.size()==0) 
                	continue;
				List memDataList=(List)memhash.get("absList");
				String memMax=(String)memhash.get("max");
				String memAvg=(String)memhash.get("avg");
				String memCur=(String)memhash.get("cur");
				 StatisNumer voNumer=new StatisNumer();
				 voNumer.setIp(ip);
				 voNumer.setType("gridMem");
				 voNumer.setCurrent(memCur);
				 voNumer.setMaximum(memMax);
				 voNumer.setAverage(memAvg);
				 gridList.add(voNumer);
				if(memDataList!=null&&memDataList.size()>0) 
				memList.add(memDataList);
				memipList.add(ip);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else if (idValue[i].indexOf("port")>=0) {
			String realValue=idValue[i];
			String[] idRelValue=new String[realValue.split("\\*").length];
			idRelValue=realValue.split("\\*");
			if (idRelValue.length<4) {
				continue;
			}
			String ip=idRelValue[1].trim();
			String sindex=idRelValue[2].trim();
			String sname="";
			try {
				sname = new String(idRelValue[3].trim().getBytes("ISO8859-1"),"gb2312");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try{
				Hashtable utilhash = hostmanager.getUtilhdx(ip,sindex,startTime,toTime);
				
				List utilInDataList=(List)utilhash.get("inList");
				List utilOutDataList=(List)utilhash.get("outList");
				String curin="0";
				String curout="0";
				String maxin="0";
				String maxout="0";
				String minin="0";
				String minout="0";
				String avgin="0";
				String avgout="0";
				if (utilhash.get("curin")!=null)
					curin=(String)utilhash.get("curin");
				if (utilhash.get("curout")!=null)
					curout=(String)utilhash.get("curout");
				if (utilhash.get("maxin")!=null)
					maxin=(String)utilhash.get("maxin");
				if (utilhash.get("maxout")!=null)
					maxout=(String)utilhash.get("maxout");
				if (utilhash.get("minin")!=null)
					minin=(String)utilhash.get("minin");
				if (utilhash.get("minout")!=null)
					minout=(String)utilhash.get("minout");
				if (utilhash.get("avgin")!=null)
					avgin=(String)utilhash.get("avgin");
				if (utilhash.get("avgout")!=null)
					avgout=(String)utilhash.get("avgout");
				//进口
				 StatisNumer voNumer=new StatisNumer();
				 voNumer.setIp(ip);
				 voNumer.setType("gridPortIn");
				 voNumer.setName(sname);
				 voNumer.setCurrent(curin);
				 voNumer.setMaximum(maxin);
				 voNumer.setMininum(minin);
				 voNumer.setAverage(avgin);
				 gridList.add(voNumer);
				 //出口
				 StatisNumer voNumerout=new StatisNumer();
				 voNumerout.setIp(ip);
				 voNumerout.setType("gridPortOut");
				 voNumerout.setName(sname);
				 voNumerout.setCurrent(curout);
				 voNumerout.setMaximum(maxout);
				 voNumerout.setMininum(minout);
				 voNumerout.setAverage(avgout);
				 gridList.add(voNumerout);
				if(utilInDataList!=null&&utilInDataList.size()>0) utilInList.add(utilInDataList);
				if(utilOutDataList!=null&&utilOutDataList.size()>0) utilOutList.add(utilOutDataList);
				portipList.add(ip);
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}else if (idValue[i].indexOf("disk")>=0) {//磁盘
			
			String ip=idValue[i].replace("disk", "");
			
			Hashtable hostdiskhash=new Hashtable();
			//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
			
			if ("0".equals(runmodel)) {
				try{
					hostdiskhash = hostlastmanager.getDisk_share(ip,"Disk","","");
				}catch(Exception e){
					e.printStackTrace();
				}
			}else {
				try {
					hostdiskhash = hostlastmanager.getDisk(ip,
							"Disk", "", "");
				} catch (Exception e) {
					e.printStackTrace();
				}
				}
			try{
				Hashtable diskHash = hostmanager.getDiskHistroy(ip, "Disk", startTime, toTime);
				String countStr=(String)diskHash.get("count");
				double[] disk_data1 = new double[hostdiskhash.size()];
				
				int count=Integer.parseInt(countStr);
				List list2=new ArrayList();
				String diskType="";
				for (int j = 1; j <= count; j++) {
					List diskList1=(List)diskHash.get("list"+j);
					if(diskList1==null||diskList1.size()==0)
						break;
					String cur="0";
					String avg="0";
					String max="0";
				if (hostdiskhash.get(new Integer(j-1))!=null) {
					Hashtable dhash = (Hashtable) (hostdiskhash.get(new Integer(j-1)));

					cur= (String) dhash.get("Utilization");
					
				}
					
					if (diskHash.get("avg"+j)!=null)
						avg=(String)diskHash.get("avg"+j);
					if (diskHash.get("max"+j)!=null)
						max=(String)diskHash.get("max"+j);
					
						Vector vector=(Vector)diskList1.get(0);
						diskType=ip+"("+vector.get(1)+")";
						 //磁盘
						 StatisNumer voNumerout=new StatisNumer();
						 voNumerout.setIp(ip);
						 voNumerout.setType("gridDisk");
						 voNumerout.setName((String)vector.get(1));
						 voNumerout.setCurrent(cur);
						 voNumerout.setMaximum(max);
						 voNumerout.setAverage(avg);
						 gridList.add(voNumerout);
				if(diskList1!=null&&diskList1.size()>0)
				    list2.add(diskList1);
				diskipList.add(diskType);
				}
				
				diskList.add(list2);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
			
		}
		//连通率
		ReportValue pingValue=new ReportValue();
		pingValue.setIpList(pingipList);
		pingValue.setListValue(pingList);
		
		//CPU
		ReportValue cpuValue=new ReportValue();
		
		cpuValue.setIpList(ipList);
		cpuValue.setListValue(list);
		//内存
		ReportValue memValue=new ReportValue();
		memValue.setIpList(memipList);
		memValue.setListValue(memList);
		//端口
		ReportValue portValue=new ReportValue();
		portValue.setIpList(portipList);
		portValue.setListValue(utilInList);//入口
		portValue.setListTemp(utilOutList);//出口
		
		//磁盘
		ReportValue diskValue=new ReportValue();
		diskValue.setIpList(diskipList);
		diskValue.setListValue(diskList);
		
		allValueMap.put("ping", pingValue);
		allValueMap.put("cpu", cpuValue);
		allValueMap.put("mem", memValue);
		allValueMap.put("port", portValue);
		allValueMap.put("disk", diskValue);
		
		allValueMap.put("gridVlue", gridList);
	}
	return allValueMap;
}

public HashMap getDbValue(String ids,String startTime,String toTime){
	HashMap allValueMap=new HashMap();
	List<StatisNumer> gridList=new ArrayList<StatisNumer>();
	List<ReportValue> valueList=new ArrayList<ReportValue>();
	String[] idValue=this.getIdValue(ids);
	
  	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
  	I_HostCollectData hostmanager = new HostCollectDataManager();
  	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
  	
	Hashtable pinghash = new Hashtable();
	Hashtable curhash = new Hashtable();
	List<List> pingList = new ArrayList<List>();
	List<String> pingipList = new ArrayList<String>();
	List<List> tableList = new ArrayList<List>();
	List<String> tableipList = new ArrayList<String>();
	List<StatisNumer> valList=new ArrayList<StatisNumer>();
	boolean valFlag=true;
	boolean tableFlag=true;
	String tabledata="";
	StringBuffer val=new StringBuffer();
	StringBuffer tableHtml=new StringBuffer();
	if (idValue != null && idValue.length > 0) {
		
		for (int i = 0; i < idValue.length; i++) {

			String realValue = idValue[i];
			String[] idRelValue = new String[realValue.split("\\*").length];
			idRelValue = realValue.split("\\*");
			if(idRelValue.length<4)continue;
			String itemId = idRelValue[0].trim();
			String typeId = idRelValue[1].trim();
			String id = idRelValue[2].trim();
			String ip = idRelValue[3].trim();
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			if (itemId.equals("ping")) {
				try {
					typevo = (DBTypeVo) typedao.findByID(typeId);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					typedao.close();
				}
				try {
					if (typevo.getDbtype().equalsIgnoreCase("oracle")) {
						//String sid = "";
//						OraclePartsDao oracledao = new OraclePartsDao();
//						List sidlist = new ArrayList();
//						try {
//							sidlist = oracledao.findOracleParts(Integer
//									.parseInt(id));
//						} catch (Exception e) {
//							e.printStackTrace();
//						} finally {
//							oracledao.close();
//						}
//						if (sidlist != null) {
//							for (int j = 0; j < sidlist.size(); j++) {
//								OracleEntity ora = (OracleEntity) sidlist
//										.get(j);
//								sid = ora.getId() + "";
//								break;
//							}
//						}

						pinghash = hostmanager.getCategory(id,
								"ORAPing", "ConnectUtilization", startTime,
								toTime);
						curhash = hostmanager.getCurByCategory(id,
								"ORAPing", "ConnectUtilization");
					} else if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
						pinghash = hostmanager.getCategory(id, "SQLPing",
								"ConnectUtilization", startTime, toTime);
						curhash = hostmanager.getCurByCategory(id, "SQLPing",
								"ConnectUtilization");
					} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
						pinghash = hostmanager.getCategory(id, "DB2Ping",
								"ConnectUtilization", startTime, toTime);
						curhash = hostmanager.getCurByCategory(ip, "DB2Ping",
						"ConnectUtilization");
					} else if (typevo.getDbtype().equalsIgnoreCase("sybase")) {
						pinghash = hostmanager.getCategory(id, "SYSPing",
								"ConnectUtilization", startTime, toTime);
						curhash = hostmanager.getCurByCategory(id, "SYSPing",
						"ConnectUtilization");
					} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
						pinghash = hostmanager.getCategory(id, "INFORMIXPing",
								"ConnectUtilization", startTime, toTime);
						curhash = hostmanager.getCurByCategory(id, "INFORMIXPing",
						"ConnectUtilization");
					} else if (typevo.getDbtype().equalsIgnoreCase("mysql")) {// HONGLI
						pinghash = hostmanager.getCategory(id, "MYPing",
								"ConnectUtilization", startTime, toTime);
						curhash = hostmanager.getCurByCategory(id, "MYPing",
						"ConnectUtilization");
					}
					if (pinghash != null && pinghash.size() > 0) {
						List pingDataList = (List) pinghash.get("list");
						String pingCur=(String) curhash.get("pingCur");
						String pingAvg = (String) pinghash.get("avgpingcon");

						String pingMin = (String) pinghash.get("pingmax");
						 StatisNumer voNumer=new StatisNumer();
						 voNumer.setIp(ip);
						 voNumer.setType("gridPing");
						 voNumer.setCurrent(pingCur);
						 voNumer.setMininum(pingMin);
						 voNumer.setAverage(pingAvg);
						 gridList.add(voNumer);
						 if(pingDataList!=null&&pingDataList.size()>0)
						pingList.add(pingDataList);
					}
					pingipList.add(ip);
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}else if (itemId.equals("val")) {
				getVaList(valList, ip, id);
			
			}else if (itemId.equals("tablespace")) {
			
				try{
					Hashtable table = hostmanager.getOraSpaceHistroy(ip, "tablespace", startTime, toTime);
					String countStr=(String)table.get("count");
					
					int count=Integer.parseInt(countStr);
					List list2=new ArrayList();
					String diskType="";
					for (int j = 1; j <= count; j++) {
						List tableList1=(List)table.get("list"+j);
						if(tableList1==null||tableList1.size()==0)
							continue;
						String cur="0";
						String avg="0";
						String max="0";
					
						if (table.get("avg"+j)!=null)
							cur=(String)table.get("avg"+j);
						if (table.get("avg"+j)!=null)
							avg=(String)table.get("avg"+j);
						if (table.get("max"+j)!=null)
							max=(String)table.get("max"+j);
						
							Vector vector=(Vector)tableList1.get(0);
							diskType=ip+"("+vector.get(1)+")";
						
							 StatisNumer voNumer=new StatisNumer();
							 voNumer.setIp(ip);
							 voNumer.setType("gridTableSpace");
							 voNumer.setName((String)vector.get(1));//表空间名称
							 voNumer.setCurrent(cur);
							 voNumer.setMaximum(max);
							 voNumer.setAverage(avg);
							 gridList.add(voNumer);
							tableipList.add(diskType);
							 if(tableList1!=null&&tableList1.size()>0)
							list2.add(tableList1);
					}
					 if(list2!=null&&list2.size()>0)
					tableList.add(list2);
				}catch(Exception e){
					e.printStackTrace();
				}
			
		}
			
		}
		//连通率
		ReportValue pingValue=new ReportValue();
		pingValue.setIpList(pingipList);
		pingValue.setListValue(pingList);
		
		//表空间
		ReportValue tableValue=new ReportValue();
		tableValue.setIpList(tableipList);
		tableValue.setListValue(tableList);
		allValueMap.put("ping", pingValue);
		allValueMap.put("tablespace", tableValue);//oracle表空间
		allValueMap.put("val", valList);//mysql 性能信息
		allValueMap.put("gridVlue", gridList);
	}
	
	return allValueMap;
}
public HashMap getMidwareValue(String ids,String startTime,String toTime) {
	HashMap allValueMap=new HashMap();
	List<StatisNumer> gridList=new ArrayList<StatisNumer>();
	List<ReportValue> valueList=new ArrayList<ReportValue>();
	String[] idValue=this.getIdValue(ids);
	
  	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
  	I_HostCollectData hostmanager = new HostCollectDataManager();
  	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
  	if(startTime == null){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		startTime = sdf.format(new Date())+" 00:00:00";
	}else {
		startTime=startTime+" 00:00:00";
	}

	if(toTime == null){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		toTime = sdf.format(new Date())+ " 23:59:59";
	}else {
		toTime=toTime+" 23:59:59";
	}
	
	if (ids != null && !ids.equals("null") && !ids.equals("")) {
		idValue = new String[ids.split(",").length];
		idValue = ids.split(",");
	}

	Hashtable pinghash = new Hashtable();
	Hashtable jvmhash = new Hashtable();
	Hashtable curhash = new Hashtable();
	List<List> pingList = new ArrayList<List>();
	List<List> jvmList = new ArrayList<List>();
	List<String> pingipList = new ArrayList<String>();
	List<String> jvmipList = new ArrayList<String>();
	
	if (idValue != null && idValue.length > 0) {

		for (int i = 0; i < idValue.length; i++) {

			String realValue = idValue[i];
			String[] idRelValue = new String[realValue.split("\\*").length];
			idRelValue = realValue.split("\\*");
			if(idRelValue.length<4)continue;
			String type = idRelValue[0].trim();
			String item = idRelValue[1].trim();
			String ip = idRelValue[3].trim();
			try {
			 if (type.equalsIgnoreCase("tomcat")) {
				 if (item.equals("ping")) {
					 pinghash = getCategory(ip, "TomcatPing",
								"ConnectUtilization", startTime, toTime,"");
						curhash = hostmanager.getCurByCategory(ip, "TomcatPing",
								"ConnectUtilization");
					
					if (pinghash != null && pinghash.size() > 0) {
						List pingDataList = (List) pinghash.get("list");
						String pingCur=(String) curhash.get("pingCur");
						String pingAvg = (String) pinghash.get("avgpingcon");

						String pingMin = (String) pinghash.get("pingmax");
						 StatisNumer voNumer=new StatisNumer();
						 voNumer.setIp(ip);
						 voNumer.setType("gridPing");
						 voNumer.setName("Tomcat");
						 voNumer.setCurrent(pingCur);
						 voNumer.setMininum(pingMin);
						 voNumer.setAverage(pingAvg);
						 gridList.add(voNumer);
						 
						if(pingDataList!=null&&pingDataList.size()>0)
						pingList.add(pingDataList);
					}
					pingipList.add(ip);
				}else if(item.equals("jvm")){
					 jvmhash = getCategory(ip, "tomcat_jvm","jvm_utilization", startTime, toTime,"");
					Hashtable curJvm=hostmanager.getCurByCategory(ip, "tomcat_jvm","jvm_utilization");
					if (jvmhash != null && jvmhash.size() > 0) {
						List jvmDataList = (List) jvmhash.get("list");
						String jvmCur=(String) curJvm.get("pingCur");//当前虚拟利用率
						String jvmAvg = (String) jvmhash.get("avg_tomcat_jvm");

						String jvmMin = (String) jvmhash.get("max");
						 StatisNumer voNumer=new StatisNumer();
						 voNumer.setIp(ip);
						 voNumer.setType("tomcat_jvm");
						 voNumer.setName("Tomcat");
						 voNumer.setCurrent(jvmCur);
						 voNumer.setMaximum(jvmMin);
						 voNumer.setAverage(jvmAvg);
						 gridList.add(voNumer);
						if(jvmDataList!=null&&jvmDataList.size()>0)
						jvmList.add(jvmDataList);
						
					}
					jvmipList.add(ip);
				}
				 
			 }else if (type.equalsIgnoreCase("iis")) {
				 if (item.equals("ping")) {
					 pinghash = getCategory(ip, "IISPing",
								"ConnectUtilization", startTime, toTime,"");
					 curhash = hostmanager.getCurByCategory(ip, "IISPing",
						"ConnectUtilization");	
					
					
					if (pinghash != null && pinghash.size() > 0) {
						List pingDataList = (List) pinghash.get("list");
						String pingCur=(String) curhash.get("pingCur");
						String pingAvg = (String) pinghash.get("avgpingcon");

						String pingMin = (String) pinghash.get("pingmax");
						 StatisNumer voNumer=new StatisNumer();
						 voNumer.setIp(ip);
						 voNumer.setType("gridPing");
						 voNumer.setName("IIS");
						 voNumer.setCurrent(pingCur);
						 voNumer.setMininum(pingMin);
						 voNumer.setAverage(pingAvg);
						 gridList.add(voNumer);
						if (pingDataList!=null&&pingDataList.size()>0)
						pingList.add(pingDataList);
					}
					pingipList.add(ip);
				}
			} 
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		//连通率
		ReportValue pingValue=new ReportValue();
		pingValue.setIpList(pingipList);
		pingValue.setListValue(pingList);
		//jvm
		ReportValue jvmValue=new ReportValue();
		jvmValue.setIpList(jvmipList);
		jvmValue.setListValue(jvmList);
		
		allValueMap.put("ping", pingValue);
		allValueMap.put("jvm", jvmValue);//Tomcat 虚拟利用率
		allValueMap.put("gridVlue", gridList);
	}
	
	
	

return allValueMap;	
}
public Hashtable getCategory(String ip,String category,String subentity,String starttime,String endtime,
		String time)
		throws Exception {
		Hashtable hash = new Hashtable();
	 	
	 	DBManager dbmanager = new DBManager();
	 	ResultSet rs = null;
		try{
			if (!starttime.equals("") && !endtime.equals("")) {
				
				String allipstr = SysUtil.doip(ip);
				String sql = "";
				StringBuffer sb = new StringBuffer();
				 if (category.equals("TomcatPing")){
					sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcatping"+time+allipstr+" h where ");
				 }
				 if (category.equals("tomcat_jvm")){
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcat_jvm"+allipstr+" h where ");
				}
				 if (category.equals("IISPing")){
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from iisping"+allipstr+" h where ");
				}
				sb.append(" h.category='");
				sb.append(category);
				sb.append("' and h.subentity='");
				sb.append(subentity);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
				sql = sb.toString();
				rs = dbmanager.executeQuery(sql);
				List list1 =new ArrayList();
				String unit = "";
				double tempfloat=0;
				double pingcon = 0;
				double tomcat_jvm_con = 0;
				int downnum = 0;
				int i=0;
		        while (rs.next()) {
		        	i=i+1;
		        	Vector v =new Vector();		        	
		            String thevalue=rs.getString("thevalue");
		            String collecttime = rs.getString("collecttime");		            
		            v.add(0,emitStr(thevalue));
		            v.add(1,collecttime);
		            v.add(2,rs.getString("unit"));
		            if ((category.equals("TomcatPing")||category.equals("IISPing"))&&subentity.equalsIgnoreCase("ConnectUtilization")){
		            	pingcon=pingcon+getfloat(thevalue);
		            	if(thevalue.equals("0")){
		            		downnum = downnum + 1;
		            	}	
		            }
		          
		            if (subentity.equalsIgnoreCase("ConnectUtilization")) {
		            	if (i==1)tempfloat = getfloat(thevalue);
		            	if (tempfloat > getfloat(thevalue))tempfloat = getfloat(thevalue);
		            }else if (category.equalsIgnoreCase("tomcat_jvm")){
		            	tomcat_jvm_con=tomcat_jvm_con+getfloat(thevalue);
		            	if (tempfloat < getfloat(thevalue))tempfloat = getfloat(thevalue);
		            }else{
		            	if (tempfloat < getfloat(thevalue))tempfloat = getfloat(thevalue);
		            }	
		            list1.add(v);	
		    }	
		        rs.close();
		        //stmt.close();
		        
				Integer size = new Integer(0);
				hash.put("list", list1);
				if (list1.size() != 0) {
					size = new Integer(list1.size());
					if (list1.get(0) != null) {
						Vector tempV = (Vector)list1.get(0);
						unit = (String)tempV.get(2);
					}
				}
				if ((category.equals("TomcatPing")||category.equals("IISPing"))&&subentity.equalsIgnoreCase("ConnectUtilization")){
					if (list1 !=null && list1.size()>0){
						hash.put("avgpingcon", CEIString.round(pingcon/list1.size(),2)+unit);						
						hash.put("pingmax", tempfloat+"");
						hash.put("downnum", downnum+"");
					}else{ 
						hash.put("avgpingcon", "0.0");	
						hash.put("pingmax", "0.0");
						hash.put("downnum", "0");
					}
				}
				if (category.equals("tomcat_jvm")){
					if (list1 !=null && list1.size()>0){
						hash.put("avg_tomcat_jvm",CEIString.round(tomcat_jvm_con/list1.size(), 2)+unit);				
					}else{ 
						hash.put("avg_tomcat_jvm", "0.0%");							
					}
				}
				hash.put("size", size);			
				hash.put("max", CEIString.round(tempfloat,2) + unit);
				hash.put("unit", unit);
		        }
			} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (rs != null)
			rs.close();
			dbmanager.close();
		}
		
		return hash;
	}

private String emitStr(String num) {
	if (num != null) {
		if (num.indexOf(".")>=0){				
			if (num.substring(num.indexOf(".")+1).length()>7){
				String tempStr = num.substring(num.indexOf(".")+1);
				num = num.substring(0,num.indexOf(".")+1)+tempStr.substring(0,7);					
			}
		}
	}
	return num;
}

private double getfloat(String num) {
double snum = 0.0;
if (num != null) {
if (num.indexOf(".")>=0){				
	if (num.substring(num.indexOf(".")+1).length()>7){
		String tempStr = num.substring(num.indexOf(".")+1);
		num = num.substring(0,num.indexOf(".")+1)+tempStr.substring(0,7);					
	}
}
int inum = (int) (Float.parseFloat(num) * 100);
snum = new Double(inum/100.0).doubleValue();
}
return snum;
}
private List<StatisNumer> getVaList(List<StatisNumer> list,String ip,String id){

	
	DBVo vo = new DBVo();
	
	
	int doneFlag = 0;
	Vector val = new Vector();
	
	try{
		DBDao dao = new DBDao();
		try{
			vo = (DBVo)dao.findByID(id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(vo.getIpAddress());
		String serverip = hex+":"+vo.getId();
		DBDao dbDao = null;
		Hashtable ipData = null;
		try {
			dbDao = new DBDao();
			ipData = dbDao.getMysqlDataByServerip(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(dbDao != null){
				dbDao.close();
			}
		}
		if(ipData != null && ipData.size()>0){
			String dbnames = vo.getDbName();
			String[] dbs = dbnames.split(",");
			for(int k=0;k<dbs.length;k++){
				//判断是否已经获取了当前的配置信息
				//if(doneFlag == 1)break;
				String dbStr = dbs[k];
				if(ipData.containsKey(dbStr)){
					Hashtable returnValue = new Hashtable();
					returnValue = (Hashtable)ipData.get(dbStr);
					if(returnValue != null && returnValue.size()>0){
						if(doneFlag == 0){
							
							if(returnValue.containsKey("Val")){
								val = (Vector)returnValue.get("Val");
							}
						}
					}
				}
			}
		}
//		}
	
		
	}catch(Exception e){
		e.printStackTrace();
	}
	
	if(val != null && val.size()>0){
		
		for(int ii=0;ii<val.size();ii++){
			Hashtable return_value = (Hashtable)val.get(ii);
			if(return_value != null && return_value.size()>0){
				String name=return_value.get("variable_name").toString();
				String value=return_value.get("value").toString();
				if(name.equalsIgnoreCase("Max_used_connections"))
		{
		 name="服务器相应的最大连接数";
		}
		if(name.equalsIgnoreCase("Handler_read_first"))
		{
		 name="索引中第一条被读的次数";
		}
		if(name.equalsIgnoreCase("Handler_read_key"))
		{
		 name="根据键读一行的请求数";
		}
		if(name.equalsIgnoreCase("Handler_read_next"))
		{
		 name="按照键顺序读下一行的请求数";
		}
		if(name.equalsIgnoreCase("Handler_read_prev"))
		{
		 name="按照键顺序读前一行的请求数";
		}
		if(name.equalsIgnoreCase("Handler_read_rnd"))
		{
		 name="H根据固定位置读一行的请求数";
		}
		if(name.equalsIgnoreCase("Handler_read_rnd_next"))
		{
		 name="在数据文件中读下一行的请求数";
		}
		if(name.equalsIgnoreCase("Open_tables"))
		{
		 name="当前打开的表的数量";
		}
		if(name.equalsIgnoreCase("Opened_tables"))
		{
		 name="已经打开的表的数量";
		}
		if(name.equalsIgnoreCase("Threads_cached"))
		{
		 name="线程缓存内的线程的数量";
		}
		if(name.equalsIgnoreCase("Threads_connected"))
		{
		 name="当前打开的连接的数量";
		}
		if(name.equalsIgnoreCase("Threads_created"))
		{
		 name="创建用来处理连接的线程数";
		}
		if(name.equalsIgnoreCase("Threads_running"))
		{
		 name="激活的非睡眠状态的线程数";
		}
		if(name.equalsIgnoreCase("Table_locks_immediate"))
		{
		 name="立即获得的表的锁的次数";
		}
		if(name.equalsIgnoreCase("Table_locks_waited"))
		{
		 name="不能立即获得的表的锁的次数";
		}
		if(name.equalsIgnoreCase("Key_read_requests"))
		{
		 name="从缓存读键的数据块的请求数";
		}
		if(name.equalsIgnoreCase("Key_reads"))
		{
		 name="从硬盘读取键的数据块的次数";
		}
		if(name.equalsIgnoreCase("log_slow_queries"))
		{
		 name="是否记录慢查询";
		}
		if(name.equalsIgnoreCase("slow_launch_time"))
		{
		 name="创建线程的时间超过该秒数，服务器增加Slow_launch_threads状态变量";
		}
		
		StatisNumer numer=new StatisNumer();
		numer.setIp(ip);
		numer.setName(name);
		numer.setCurrent(value);
		numer.setType("valInfo");
		list.add(numer);
         }
		}
	
	}
	return list;


	
}

private String[] getIdValue(String ids){
	String[] idValue=null;
	if (ids!=null&&!ids.equals("null")&&!ids.equals("")) {
		 idValue=new String[ids.split(",").length];
    	idValue=ids.split(",");
	}
	return idValue;
}

	/**
	 * 获取报表打印的model
	 * @param orderList
	 * @return
	 */
	public static synchronized ReportValue getReportValue(List orderList,String indicatorName){
		ReportValue reportValue = new ReportValue();
		List valueList = new ArrayList();
		List ipList = new ArrayList();
		for(int i=0; i < orderList.size(); i++){
			Hashtable tempHash = (Hashtable)orderList.get(i);
			Hashtable valueHash = null;//连通率hash
			if(indicatorName.equalsIgnoreCase("ping")){
				if(tempHash.containsKey("pinghash")){
					valueHash = (Hashtable)tempHash.get("pinghash");
				}
			}else if(indicatorName.equalsIgnoreCase("responsetime")){
				if(tempHash.containsKey("responsehash")){
					valueHash = (Hashtable)tempHash.get("responsehash");
				}
			}
			if(valueHash == null){
				continue;
			}
			//取出连通率的数据值
			List<Vector> dataList = null;
			if(valueHash.containsKey("list")){
				dataList = (List<Vector>)valueHash.get("list");
			}
			valueList.add(dataList);
			ipList.add(tempHash.get("ipaddress"));
		}
		reportValue.setIpList(ipList);
		reportValue.setListValue(valueList);
		return reportValue;
	}

	/**
	 * 获取连通率图形chart的字符串
	 * @param orderList
	 * @return
	 */
	public static synchronized String getChartDivStr(List orderList, String indicatorName){
		List valueList = new ArrayList();
		List ipList = new ArrayList();
		for(int i=0; i < orderList.size(); i++){
			Hashtable tempHash = (Hashtable)orderList.get(i);
			Hashtable valueHash = null;//连通率hash
			if(indicatorName.equalsIgnoreCase("ping")){
				if(tempHash.containsKey("pinghash")){
					valueHash = (Hashtable)tempHash.get("pinghash");
				}
			}else if(indicatorName.equalsIgnoreCase("responsetime")){
				if(tempHash.containsKey("responsehash")){
					valueHash = (Hashtable)tempHash.get("responsehash");
				}
			}
			//取出连通率的数据值
			List<Vector> dataList = null;
			if(valueHash == null){
				continue;
			}
			if(valueHash.containsKey("list")){
				dataList = (List<Vector>)valueHash.get("list");
			}
			valueList.add(dataList);
			ipList.add(tempHash.get("ipaddress"));
		}
		AmChartTool amChartTool = new AmChartTool();
		String pingdata = amChartTool.makeAmChartData(valueList, ipList);
		return pingdata; 
	}
	
	/**
	 * 获取连通率图形chart的字符串(中间件)
	 * @param orderList
	 * @return
	 */
	public static synchronized String getMiddelChartDivStr(List orderList, String indicatorName){
		List valueList = new ArrayList();
		List ipList = new ArrayList();
		for(int i=0; i < orderList.size(); i++){
			Hashtable tempHash = (Hashtable)orderList.get(i);
			Hashtable valueHash = null;//连通率hash
			if(indicatorName.equalsIgnoreCase("ping")){
				if(tempHash.containsKey("pinghash")){
					valueHash = (Hashtable)tempHash.get("pinghash");
				}
				if(tempHash.containsKey("pinghashiis")){
					valueHash = (Hashtable)tempHash.get("pinghashiis");
				}
				if(tempHash.containsKey("pinghashweblogic")){
					valueHash = (Hashtable)tempHash.get("pinghashweblogic");
				}
			}else if(indicatorName.equalsIgnoreCase("responsetime")){
				if(tempHash.containsKey("responsehash")){
					valueHash = (Hashtable)tempHash.get("responsehash");
				}
			}
			//取出连通率的数据值
			List<Vector> dataList = null;
			if(valueHash == null){
				continue;
			}
			if(valueHash.containsKey("list")){
				dataList = (List<Vector>)valueHash.get("list");
			}
			valueList.add(dataList);
			ipList.add(tempHash.get("ipaddress"));
		}
		AmChartTool amChartTool = new AmChartTool();
		String pingdata = amChartTool.makeAmChartData(valueList, ipList);
		return pingdata; 
	}
	
	/**
	 * 获取报表打印的model(中间件)
	 * @param orderList
	 * @return
	 */
	public static synchronized ReportValue getMiddleReportValue(List orderList,String indicatorName){
		ReportValue reportValue = new ReportValue();
		List valueList = new ArrayList();
		List ipList = new ArrayList();
		for(int i=0; i < orderList.size(); i++){
			Hashtable tempHash = (Hashtable)orderList.get(i);
			Hashtable valueHash = null;//连通率hash
			if(indicatorName.equalsIgnoreCase("ping")){
				if(tempHash.containsKey("pinghash")){
					valueHash = (Hashtable)tempHash.get("pinghash");
				}
				if(tempHash.containsKey("pinghashiis")){
					valueHash = (Hashtable)tempHash.get("pinghashiis");
				}
				if(tempHash.containsKey("pinghashweblogic")){
					valueHash = (Hashtable)tempHash.get("pinghashweblogic");
				}
			}else if(indicatorName.equalsIgnoreCase("responsetime")){
				if(tempHash.containsKey("responsehash")){
					valueHash = (Hashtable)tempHash.get("responsehash");
				}
			}
			if(valueHash == null){
				continue;
			}
			//取出连通率的数据值
			List<Vector> dataList = null;
			if(valueHash.containsKey("list")){
				dataList = (List<Vector>)valueHash.get("list");
			}
			valueList.add(dataList);
			ipList.add(tempHash.get("ipaddress"));
		}
		reportValue.setIpList(ipList);
		reportValue.setListValue(valueList);
		return reportValue;
	}
	
}
