/*
 * Created on 2005-4-7
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.snmp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.config.model.Procs;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.ProcessNetData;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Servicecollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.temp.model.ServiceNodeTemp;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LoadWindowsWMIFile {
	/**
	 * @param hostname
	 */
	private String ipaddress;

	//private ProcsDao procsManager = new ProcsDao();
	private Hashtable sendeddata = ShareData.getProcsendeddata();

	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public LoadWindowsWMIFile(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Hashtable getTelnetMonitorDetail() {
		Host node = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
		if(node == null)return null;
		if(!node.isManaged())return null;
		//得到前一次采集的数据
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
		if(ipAllData == null)ipAllData = new Hashtable();
		Hashtable returnHash = new Hashtable();
		StringBuffer fileContent = new StringBuffer();
		Vector cpuVector = new Vector();
		Vector memoryVector=new Vector();
		Vector systemVector = new Vector();
		Vector userVector = new Vector();
		Vector diskVector = new Vector();
		Vector processVector = new Vector();
		Vector interfaceVector = new Vector();
		Vector utilhdxVector = new Vector();
		
		Hashtable hostinfohash = new Hashtable();
		Hashtable diskconfighash = new Hashtable();
		Hashtable networkstatushash = new Hashtable();
		Hashtable alldiskperformancehash = new Hashtable();
		
		//存放逻辑磁盘性能数据
		List logicdiskperformancelist = new ArrayList();
		Hashtable oldalldiskperformancehash = new Hashtable();
		Hashtable newalldiskperformancehash = new Hashtable();
		List cpuconfiglist = new ArrayList();
		List physicalDisklist = new ArrayList();
		List networkconfiglist = new ArrayList();
		List networkstatuslist = new ArrayList();
		Hashtable memoryhash = new Hashtable();
		List diskperformancelist = new ArrayList();
		Hashtable pefmemoryhash = new Hashtable();
		Hashtable pefcpuhash = new Hashtable();
		List servicelist = new ArrayList();
		List userlist = new ArrayList();
		List iflist = new ArrayList();
		List diskpef=new ArrayList();//磁盘读取性能

		CPUcollectdata cpudata = null;
		Systemcollectdata systemdata = null;
		Usercollectdata userdata = null;
		Processcollectdata processdata = null;
		
		FileInputStream fi = null;
		FileOutputStream fo = null;
		Document doc;
		Element root = null;	
		List elements = null;
		String cpupercValue = "0";

		Calendar date = Calendar.getInstance();
		
		try {
			
			String filename = ResourceCenter.getInstance().getSysPath()+ "linuxserver/" + ipaddress + ".xml";
			File file=new File(filename);
			if(!file.exists()){
				//文件不存在
				return null;
			}
			file = null;
			fi = new FileInputStream(filename);
			SAXBuilder sb = new SAXBuilder();
			doc = sb.build(fi);
			root = doc.getRootElement();
			fi.close();
			elements = root.getChildren();
			
			if(elements == null) elements = new ArrayList();
			
			//获取网卡配置信息
			for(int j=0;j<elements.size();j++){
				//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+((Element)elements.get(j)).getName());
				
				
				/**
				 * 网卡配置信息
				 * 
				 * 
				 */
				
				if("Networkconfigstart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//网络接口信息开始
					//System.out.println("===============Networkconfigstart============");
					List subelements = ((Element)(elements.get(j))).getChildren();
					//System.out.println(subelements.size());
					
					
				  	if(subelements != null && subelements.size()>0){
				  		Hashtable rValue = new Hashtable();
				  		for(int k=0;k<subelements.size();k++){
				  			
				  			//System.out.println("==========1=====================");
				  			if("Networkconfiginfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//网络配置信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					
					  					rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  				}
					  			}
					  			networkconfiglist.add(rValue);
				  			}
				  		}
				  	}
				  	
				}
				
				
				
				/**
				 * 主机配置信息
				 * 
				 * 
				 * 
				 */
				
				
				if("hostinfostart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//主机配置信息开始
					
					//System.out.println("===============hostinfostart============");
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			//SysLogger.info(((Element)(subelements.get(k))).getName()+"==========");
				  			if("hostinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//主机配置信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
				  				List cpuList = new ArrayList();
					  			if(endelements != null && endelements.size()>0){
					  				for(int m=0;m<endelements.size();m++){
					  					if("TotalPhysicalMemory".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						List rvalue = SysUtil.checkSize(((Element)(endelements.get(m))).getValue());
					  						hostinfohash.put(((Element)(endelements.get(m))).getName(), (String)rvalue.get(0)+(String)rvalue.get(1)+"");
					  					}else if ("CPUname".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						cpuList.add(((Element)(endelements.get(m))).getValue());
					  					}else
					  						hostinfohash.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  						//SysLogger.info(SnmpMibConstants.HostInfoName.get(((Element)(endelements.get(m))).getName())+"=========="+((Element)(endelements.get(m))).getValue());
					  					
					  				}
					  			}
					  			if(cpuList != null && cpuList.size()>0){
					  				hostinfohash.put("CPUname", cpuList);
					  			}
				  			}
				  		}
				  	}
				} 
				
				
				/**
				 * 
				 * 服务信息
				 * 
				 * 
				 * 
				 */
				
				if("serviceinfostar".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//系统启动的服务信息开始
					
					//System.out.println("===============serviceinfostar============");
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			Hashtable subValue = new Hashtable();
				  			Hashtable rValue = new Hashtable();
				  			if("serviceinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//系统启动的服务信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					if("StartMode".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						rValue.put(((Element)(endelements.get(m))).getName(), SnmpMibConstants.ServiceStartModeConfig.get(((Element)(endelements.get(m))).getValue()));
					  					}else if("State".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						rValue.put(((Element)(endelements.get(m))).getName(), SnmpMibConstants.ServiceStateConfig.get(((Element)(endelements.get(m))).getValue()));
					  					}else if("ServiceType".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						rValue.put(((Element)(endelements.get(m))).getName(), SnmpMibConstants.ServiceTypeConfig.get(((Element)(endelements.get(m))).getValue()));
					  					}else
					  						rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  				}
					  			}
					  			servicelist.add(rValue);
				  			}
				  		}
				  	}
				}
				
				
				/**
				 * 解析系统用户信息
				 * 用户名
				 * 
				 */
				
				if("UserAccountinfostart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//系统用户信息开始
					
					//System.out.println("===============UserAccountinfostart============");
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			Hashtable rValue = new Hashtable();
				  			if("UserAccountinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//系统用户信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					if("LocalAccount".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						rValue.put(((Element)(endelements.get(m))).getName(), SnmpMibConstants.UserLocalAccountConfig.get(((Element)(endelements.get(m))).getValue()));
					  					}else if("Status".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						rValue.put(((Element)(endelements.get(m))).getName(), SnmpMibConstants.UserStatusConfig.get(((Element)(endelements.get(m))).getValue()));
					  					}else
					  						rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  				}
					  			}
					  			userlist.add(rValue);
				  			}
				  		}
				  	}
				}
				
				
				
				/**
				 * 进程信息
				 * 
				 * 
				 * 
				 */
				
				
				
				if("processinfostart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//进程信息开始
					
					//System.out.println("===============processinfostart============");
					List procslist = new ArrayList();
					ProcsDao procsdaor=new ProcsDao();
					try{
						procslist = procsdaor.loadByIp(ipaddress);
					}catch(Exception ex){
						ex.printStackTrace();
					}finally{
						procsdaor.close();
					}
					List procs_list = new ArrayList();
					Hashtable procshash = new Hashtable();
					Vector procsV = new Vector();
					if (procslist != null && procslist.size() > 0) {
						for (int i = 0; i < procslist.size(); i++) {
							Procs procs = (Procs) procslist.get(i);
							procshash.put(procs.getProcname(), procs);
							procsV.add(procs.getProcname());
						}
					}
					
					
					int totalPhySize = 0;
					if (memoryhash != null && memoryhash.size() > 0){
						String TotalVisibleMemorySize = (String)memoryhash.get("TotalVisibleMemorySize");
						totalPhySize = Integer.parseInt(Math.round(Float.parseFloat(TotalVisibleMemorySize.substring(0,TotalVisibleMemorySize.length()-1)))+"");
					}
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			Hashtable subValue = new Hashtable();
				  			Hashtable rValue = new Hashtable();
				  			//SysLogger.info(((Element)(subelements.get(k))).getName()+"==========");
				  			if("processinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//进程信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  				}
					  				//进程ID
					  				String pid = (String)rValue.get("ProcessId");
					  				//线程数量
					  				String threadCount = (String)rValue.get("ThreadCount");
					  				String percmem = "0";
					  				
					  				String memsize = Math.round(Float.parseFloat((String)rValue.get("WorkingSetSize"))* 1.0f / 1024)+"";
					  				if(totalPhySize > 0){
					  					percmem = Math.round(Float.parseFloat(memsize)/(totalPhySize*1024))+"";
					  				}
					  				
					  				
					  				
					  				//进程名称
					  				String name = (String)rValue.get("Name");
					  				//cputime
					  				String cputime = Math.round((Float.parseFloat((String)rValue.get("KernelModeTime"))+Float.parseFloat((String)rValue.get("UserModeTime")))/10000000)+"";
					  				String processstatus = "正在运行";
					  				
					  				String processtype="";
					  				
					  				//句柄数量
					  				String handleCount = (String)rValue.get("HandleCount");
//					  				String vbstring0 = pro[0];// pid
//									String vbstring1 = pro[1];// command
//									String vbstring2 = "应用程序";
//									String vbstring3 = "正在运行";
//									String vbstring4 = "";// memsize
//									String vbstring5 = pro[2];// cputime
//									String vbstring6 = pro[3];// %mem

									//进程内存使用率
									processdata = new Processcollectdata();
									processdata.setIpaddress(ipaddress);
									processdata.setCollecttime(date);
									processdata.setCategory("Process");
									processdata.setEntity("MemoryUtilization");
									processdata.setSubentity(pid);
									processdata.setRestype("dynamic");
									processdata.setUnit("%");
									processdata.setThevalue(percmem);
									processVector.addElement(processdata);

									//进程内存大小
									processdata = new Processcollectdata();
									processdata.setIpaddress(ipaddress);
									processdata.setCollecttime(date);
									processdata.setCategory("Process");
									processdata.setEntity("Memory");
									processdata.setSubentity(pid);
									processdata.setRestype("static");
									processdata.setUnit("K");
									processdata.setThevalue(memsize);
									processVector.addElement(processdata);
									
									//进程类型
									processdata = new Processcollectdata();
									processdata.setIpaddress(ipaddress);
									processdata.setCollecttime(date);
									processdata.setCategory("Process");
									processdata.setEntity("Type");
									processdata.setSubentity(pid);
									processdata.setRestype("static");
									processdata.setUnit(" ");
									processdata.setThevalue(processtype);
									processVector.addElement(processdata);
									
									//进程状态
									processdata = new Processcollectdata();
									processdata.setIpaddress(ipaddress);
									processdata.setCollecttime(date);
									processdata.setCategory("Process");
									processdata.setEntity("Status");
									processdata.setSubentity(pid);
									processdata.setRestype("static");
									processdata.setUnit(" ");
									processdata.setThevalue(processstatus);
									processVector.addElement(processdata);

									//进程名称
									processdata = new Processcollectdata();
									processdata.setIpaddress(ipaddress);
									processdata.setCollecttime(date);
									processdata.setCategory("Process");
									processdata.setEntity("Name");
									processdata.setSubentity(pid);
									processdata.setRestype("static");
									processdata.setUnit(" ");
									processdata.setThevalue(name);
									processVector.addElement(processdata);

									//进程占用的CPU时间
									processdata = new Processcollectdata();
									processdata.setIpaddress(ipaddress);
									processdata.setCollecttime(date);
									processdata.setCategory("Process");
									processdata.setEntity("CpuTime");
									processdata.setSubentity(pid);
									processdata.setRestype("static");
									processdata.setUnit("秒");
									processdata.setThevalue(cputime);
									processVector.addElement(processdata);
									
									//线程数量
									processdata = new Processcollectdata();
									processdata.setIpaddress(ipaddress);
									processdata.setCollecttime(date);
									processdata.setCategory("Process");
									processdata.setEntity("ThreadCount");
									processdata.setSubentity(pid);
									processdata.setRestype("static");
									processdata.setUnit("");
									processdata.setThevalue(threadCount);
									processVector.addElement(processdata);
									
									//句柄数量
									processdata = new Processcollectdata();
									processdata.setIpaddress(ipaddress);
									processdata.setCollecttime(date);
									processdata.setCategory("Process");
									processdata.setEntity("HandleCount");
									processdata.setSubentity(pid);
									processdata.setRestype("static");
									processdata.setUnit("");
									processdata.setThevalue(handleCount);
									processVector.addElement(processdata);
									// 判断是否有需要监视的进程，若取得的列表里包含监视进程，则从Vector里去掉
									if (procsV != null && procsV.size() > 0) {
										if (procsV.contains(name)) {
											// procshash.remove(vbstring1);
											procsV.remove(name);
											// 判断已经发送的进程短信列表里是否有该进程,若有,则从已发送列表里去掉该短信信息
											if (sendeddata.containsKey(ipaddress + ":"
													+ name)) {
												sendeddata.remove(ipaddress + ":" + name);
											}
											// 判断进程丢失列表里是否有该进程,若有,则从该列表里去掉该信息
											Hashtable iplostprocdata = (Hashtable) ShareData
													.getLostprocdata(ipaddress);
											if (iplostprocdata == null)
												iplostprocdata = new Hashtable();
											if (iplostprocdata.containsKey(name)) {
												iplostprocdata.remove(name);
												ShareData.setLostprocdata(ipaddress,
														iplostprocdata);
											}

										}
									}
					  				
					  			}
					  			//判断ProcsV里还有没有需要监视的进程，若有，则说明当前没有启动该进程，则用命令重新启动该进程，同时写入事件
								Vector eventtmpV = new Vector();
								if (procsV != null && procsV.size() > 0) {
									for (int i = 0; i < procsV.size(); i++) {
										Procs procs = (Procs) procshash.get((String) procsV
												.get(i));
										Host host = (Host) PollingEngine.getInstance()
												.getNodeByIP(ipaddress);
										try {
											Hashtable iplostprocdata = (Hashtable) ShareData
													.getLostprocdata(ipaddress);
											if (iplostprocdata == null)
												iplostprocdata = new Hashtable();
											iplostprocdata.put(procs.getProcname(), procs);
											ShareData.setLostprocdata(ipaddress, iplostprocdata);
											EventList eventlist = new EventList();
											eventlist.setEventtype("poll");
											eventlist.setEventlocation(host.getSysLocation());
											eventlist.setContent(procs.getProcname() + "进程丢失");
											eventlist.setLevel1(1);
											eventlist.setManagesign(0);
											eventlist.setBak("");
											eventlist.setRecordtime(Calendar.getInstance());
											eventlist.setReportman("系统轮询");
											String bids = ","+host.getBid();
											eventlist.setBusinessid(bids);
											eventlist.setNodeid(host.getId());
											eventlist.setOid(0);
											eventlist.setSubtype("host");
											eventlist.setSubentity("proc");
											EventListDao eventlistdao = new EventListDao();
											eventlistdao.save(eventlist);
											eventtmpV.add(eventlist);
											// 发送手机短信并写事件和声音告警
											createSMS(procs);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
				  			}
				  		}
				  	}
				}
				
				
				/**
				 * 磁盘配置
				 * 
				 * 
				 * 
				 */
				
				
				if("diskconfigstart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//磁盘信息开始
					//System.out.println("===============diskconfigstart============");
					
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			Hashtable rValue = new Hashtable();
				  			if("diskconfiginfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//磁盘信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					if("Size".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						List rvalue = SysUtil.checkSize(((Element)(endelements.get(m))).getValue());
					  						rValue.put(((Element)(endelements.get(m))).getName(), (String)rvalue.get(0)+(String)rvalue.get(1)+"");
					  					}else{
					  						rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  					}
					  				}
					  			}
					  			physicalDisklist.add(rValue);
				  			}
				  		}
				  		
				  	}
				}
				
				/**
				 *cpu 配置信息
				 * 位数、二级缓存大小、cpu使用率、cpu的主频
				 * 需要技术cpu的 负载
				 * 统计有几个cpu，每个cpu的使用率是多少，做平均值
				 */
				
				
				if("cpuconfigstart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//CPU配置信息开始
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			Hashtable subValue = new Hashtable();
				  			Hashtable rValue = new Hashtable();
				  			if("cpuconfiginfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//CPU配置信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					//SysLogger.info(SnmpMibConstants.CPUConfigName.get(((Element)(endelements.get(m))).getName())+"=========="+((Element)(endelements.get(m))).getValue());
					  					rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  				}
					  			}
					  			cpuconfiglist.add(rValue);
				  			}
				  		}
				  	}
				}
				
				
				
				/**
				 * 系统内存性能
				 * 
				 * 
				 */
				
				if("PerfOSMemorystart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//内存性能信息开始
					
					//System.out.println("===============PerfOSMemorystart============");
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			if("OSMemorystartinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//内存性能信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				for(int m=0;m<endelements.size();m++){
					  					if("AvailableBytes".equalsIgnoreCase(((Element)(endelements.get(m))).getName())
					  							|| "CommittedBytes".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						float allblocksize = Float.parseFloat(((Element)(endelements.get(m))).getValue());
											float allsize = allblocksize * 1.0f / 1024;
											pefmemoryhash.put(((Element)(endelements.get(m))).getName(), Math.round(allsize)+"");
					  					}else
					  						pefmemoryhash.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  				}
					  			}
				  			}
				  		}
				  	}
				}
				
				
				
				
				
				
				
				if("OSSystemstart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//CPU性能信息开始
					
					//System.out.println("===============OSSystemstart============");
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			if("OSSysteminfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//CPU性能信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				for(int m=0;m<endelements.size();m++){
					  						pefcpuhash.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  				}
					  			}
				  			}
				  		}
				  	}
				}
				
				
				
				/**
				 * 网卡流速信息
				 * 接收数据包、发送的数据包、接收的字节、发送的字节、网卡带宽、发送的错包数
				 * 
				 */
				
				
				if("NetworkInterfacestart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//网络接口性能信息开始
					
					
					List oldiflist = new ArrayList();
					if(ipAllData != null){
						oldiflist = (List)ipAllData.get("iflist");
					}
					
					
					
					
					//System.out.println("===============NetworkInterfacestart============");
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			
				  			Hashtable ifhash = new Hashtable();
				  			Hashtable oldifhash = new Hashtable();//用来取出老记录
							if(oldiflist != null && oldiflist.size()>0){
								oldifhash = (Hashtable)oldiflist.get(k);
							}
							
				  			
				  			Hashtable rValue = new Hashtable();
				  			if("NetworkInterfaceinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//网络接口性能信息
				  				Interfacecollectdata interfacedata = null;
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					//System.out.println(SnmpMibConstants.NetworkInterfaceinfoName.get(((Element)(endelements.get(m))).getName())+"=========="+((Element)(endelements.get(m))).getValue());
					  					rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  				
					  				}
					  				
					  				
					  				
					  				if(rValue != null && rValue.size()>0){
					  					//端口索引
					  					interfacedata=new Interfacecollectdata();
										interfacedata.setIpaddress(ipaddress);
										interfacedata.setCollecttime(date);
										interfacedata.setCategory("Interface");
										interfacedata.setEntity("index");
										interfacedata.setSubentity(k+"");
										//端口状态不保存，只作为静态数据放到临时表里
										interfacedata.setRestype("static");
										interfacedata.setUnit("");
										interfacedata.setThevalue(k+"");
										interfacedata.setChname("端口索引");
										interfaceVector.addElement(interfacedata);
					  					//端口描述
					  					interfacedata=new Interfacecollectdata();
										interfacedata.setIpaddress(ipaddress);
										interfacedata.setCollecttime(date);
										interfacedata.setCategory("Interface");
										interfacedata.setEntity("ifDescr");
										interfacedata.setSubentity(k+"");
										//端口状态不保存，只作为静态数据放到临时表里
										interfacedata.setRestype("static");
										interfacedata.setUnit("");
										interfacedata.setThevalue((String)rValue.get("Name"));
										interfacedata.setChname("端口描述2");
										interfaceVector.addElement(interfacedata);
										//端口带宽
										interfacedata=new Interfacecollectdata();
										interfacedata.setIpaddress(ipaddress);
										interfacedata.setCollecttime(date);
										interfacedata.setCategory("Interface");
										interfacedata.setEntity("ifSpeed");
										interfacedata.setSubentity(k+"");
										interfacedata.setRestype("static");
										interfacedata.setUnit("");
										interfacedata.setThevalue((String)rValue.get("CurrentBandwidth"));
										interfacedata.setChname("每秒字节数");
										interfaceVector.addElement(interfacedata);
										//当前状态
//										interfacedata=new Interfacecollectdata();
//										interfacedata.setIpaddress(ipaddress);
//										interfacedata.setCollecttime(date);
//										interfacedata.setCategory("Interface");
//										interfacedata.setEntity("ifOperStatus");
//										interfacedata.setSubentity(k+"");
//										interfacedata.setRestype("static");
//										interfacedata.setUnit("");
//										interfacedata.setThevalue("up");
//										interfacedata.setChname("当前状态");
//										interfaceVector.addElement(interfacedata);
										//当前状态
										interfacedata=new Interfacecollectdata();
										interfacedata.setIpaddress(ipaddress);
										interfacedata.setCollecttime(date);
										interfacedata.setCategory("Interface");
										interfacedata.setEntity("ifOperStatus");
										interfacedata.setSubentity(k+"");
										interfacedata.setRestype("static");
										interfacedata.setUnit("");
										//
										interfacedata.setThevalue(1+"");
										interfacedata.setChname("当前状态");
										interfaceVector.addElement(interfacedata);
										
											
										String outBytes ="0";
										String inBytes ="0";
										String oldOutBytes = "0";
										String oldInBytes = "0";
										
										
										String endOutBytes = "0";
										String endInBytes = "0";
										
										
										outBytes=(String)rValue.get("BytesSentPersec");//当前发送的字节数
										inBytes=(String)rValue.get("BytesReceivedPersec");//当前接受的字节数
										//计算数据流量
										
										if(oldifhash != null && oldifhash.size()>0){
											//发送的数据字节数
											if(oldifhash.containsKey("outBytes")){
												oldOutBytes = (String)oldifhash.get("outBytes");
												try{
													endOutBytes = (Long.parseLong(outBytes)-Long.parseLong(oldOutBytes))+"";
												}catch(Exception e){
													e.printStackTrace();
											}
											
											}
											//接受的数据字节数
											if(oldifhash.containsKey("inBytes")){
												oldInBytes = (String)oldifhash.get("inBytes");
												try{
													endInBytes = (Long.parseLong(inBytes)-Long.parseLong(oldInBytes))+"";
												}catch(Exception e){
													e.printStackTrace();
											}
											
											}
											
											
										}
						  				
										ifhash.put("inBytes", inBytes);
										ifhash.put("outBytes", outBytes);
										
										
										 iflist.add(ifhash);
										
										
										//端口入口流速
										UtilHdx utilhdx=new UtilHdx();
										utilhdx.setIpaddress(ipaddress);
										utilhdx.setCollecttime(date);
										utilhdx.setCategory("Interface");
										String chnameBand="";
										utilhdx.setEntity("InBandwidthUtilHdx");
										utilhdx.setThevalue(Long.toString(Long.parseLong((String)endInBytes)*8/1024/300));
										utilhdx.setSubentity(k+"");
										utilhdx.setRestype("dynamic");
										utilhdx.setUnit("Kb/秒");	
										utilhdx.setChname(k+"端口入口"+"流速");
										utilhdxVector.addElement(utilhdx);
										//端口出口流速
										utilhdx=new UtilHdx();
										utilhdx.setIpaddress(ipaddress);
										utilhdx.setCollecttime(date);
										utilhdx.setCategory("Interface");
										utilhdx.setEntity("OutBandwidthUtilHdx");
										utilhdx.setThevalue(Long.toString(Long.parseLong((String)endOutBytes)*8/1024/300));
										utilhdx.setSubentity(k+"");
										utilhdx.setRestype("dynamic");
										utilhdx.setUnit("Kb/秒");	
										utilhdx.setChname(k+"端口出口"+"流速");
										utilhdxVector.addElement(utilhdx);
										//丢弃的数据包
										utilhdx=new UtilHdx();
										utilhdx.setIpaddress(ipaddress);
										utilhdx.setCollecttime(date);
										utilhdx.setCategory("Interface");
										utilhdx.setChname("入站被丢弃的数据包");
										utilhdx.setEntity("ifInDiscards");
										utilhdx.setThevalue((String)rValue.get("PacketsReceivedDiscarded"));
										utilhdx.setSubentity(k+"");
										utilhdx.setRestype("dynamic");
										utilhdx.setUnit("个");
										utilhdxVector.addElement(utilhdx);
										//入站错误数据包
										utilhdx=new UtilHdx();
										utilhdx.setIpaddress(ipaddress);
										utilhdx.setCollecttime(date);
										utilhdx.setCategory("Interface");
										utilhdx.setChname("入站错误数据包");
										utilhdx.setEntity("ifInErrors");
										utilhdx.setThevalue((String)rValue.get("PacketsReceivedErrors"));
										utilhdx.setSubentity(k+"");
										utilhdx.setRestype("dynamic");
										utilhdx.setUnit("个");
										utilhdxVector.addElement(utilhdx);
										//入口非单向传输数据包
										utilhdx=new UtilHdx();
										utilhdx.setIpaddress(ipaddress);
										utilhdx.setCollecttime(date);
										utilhdx.setCategory("Interface");
										utilhdx.setChname("非单向传输数据包");
										utilhdx.setEntity("ifInNUcastPkts");
										utilhdx.setThevalue((String)rValue.get("PacketsReceivedNonUnicastPersec"));
										utilhdx.setSubentity(k+"");
										utilhdx.setRestype("dynamic");
										utilhdx.setUnit("个");
										utilhdxVector.addElement(utilhdx);
										//入口单向传输数据包
										utilhdx=new UtilHdx();
										utilhdx.setIpaddress(ipaddress);
										utilhdx.setCollecttime(date);
										utilhdx.setCategory("Interface");
										utilhdx.setChname("单向传输数据包");
										utilhdx.setEntity("ifInUcastPkts");
										utilhdx.setThevalue((String)rValue.get("PacketsReceivedUnicastPersec"));
										utilhdx.setSubentity(k+"");
										utilhdx.setRestype("dynamic");
										utilhdx.setUnit("个");
										utilhdxVector.addElement(utilhdx);
										//出口非单向传输数据包
										utilhdx=new UtilHdx();
										utilhdx.setIpaddress(ipaddress);
										utilhdx.setCollecttime(date);
										utilhdx.setCategory("Interface");
										utilhdx.setChname("非单向传输数据包");
										utilhdx.setEntity("ifOutNUcastPkts");
										utilhdx.setThevalue((String)rValue.get("PacketsSentNonUnicastPersec"));
										utilhdx.setSubentity(k+"");
										utilhdx.setRestype("dynamic");
										utilhdx.setUnit("个");
										utilhdxVector.addElement(utilhdx);
										//出口单向传输数据包
										utilhdx=new UtilHdx();
										utilhdx.setIpaddress(ipaddress);
										utilhdx.setCollecttime(date);
										utilhdx.setCategory("Interface");
										utilhdx.setChname("单向传输数据包");
										utilhdx.setEntity("ifOutUcastPkts");
										utilhdx.setThevalue((String)rValue.get("PacketsSentUnicastPersec"));
										utilhdx.setSubentity(k+"");
										utilhdx.setRestype("dynamic");
										utilhdx.setUnit("个");
										utilhdxVector.addElement(utilhdx);
										
					  				}
					  			}
					  			//subValue.put(((Element)(subelements.get(k))).getName(), rValue);
				  			}
				  		}
				  	}
				}
				
				
				/**
				 * 
				 * 网卡状体信息
				 * 网卡名称、mac地址、连接状态
				 * 
				 */
				
				
				if("NetworkStatusstart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//网卡信息开始
					
					//System.out.println("===============NetworkStatusstart============");
					
					List subelements = ((Element)(elements.get(j))).getChildren();
					
					
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			
				  			//System.out.println("--------NetworkStatusinfo-----1--------------------");
				  			if("NetworkStatusinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//网卡信息
				  				//System.out.println("--------NetworkStatusinfo-----2--------------------");
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  			//	System.out.println("--------NetworkStatusinfo-----3--------------------");
					  				Hashtable rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					if("NetConnectionStatus".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  					//	System.out.println("--------NetworkStatusinfo-----4--------------------"+((Element)(endelements.get(m))).getName());
					  						
					  						
					  						//System.out.println(((Element)(endelements.get(m))).getName()+"==="+((Element)(endelements.get(m))).getValue());
					  						
					  						//rValue.put(((Element)(endelements.get(m))).getName(), SnmpMibConstants.NetworkStatusConfig.get(((Element)(endelements.get(m))).getValue()));
					  						rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  					
					  					}else{
					  						
					  						
					  						rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  					}
					  				}
					  				networkstatuslist.add(rValue);
					  			}
				  			}
				  		}
				  	}
				}
				
				
				
				/**
				 * 
				 * 内存信息
				 * 
				 */
				
				
				if("SystemMemorystart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//内存信息开始
					
					//System.out.println("===============SystemMemorystart============");
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			if("SystemMemoryinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//内存信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				Hashtable rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					//以M单位表示
					  					List rvalue = SysUtil.checkSize(Float.parseFloat(((Element)(endelements.get(m))).getValue())*1024+"");
					  					memoryhash.put(((Element)(endelements.get(m))).getName(), Float.parseFloat(((Element)(endelements.get(m))).getValue())/1024+"M");
					  					//memoryhash.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  				}
					  				
					  			}
				  			}
				  		}
				  	}
				}
				
				
				/**
				 * 硬盘性能
				 * 
				 * 
				 */
				
				if("PhysicalDiskstart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//总磁盘性能信息开始
					
					
					//System.out.println("===============PhysicalDiskstart============");
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			if("PhysicalDiskinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//总磁盘性能信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				Hashtable rValue = new Hashtable();
					  				for(int m=0;m<endelements.size();m++){
					  					newalldiskperformancehash.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  					alldiskperformancehash.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());
					  					
					  				}
					  			}
				  			}
				  		}
				  	}
				}
				
				
				/**
				 * 
				 * 磁盘分区读写新能
				 * 
				 * 
				 * 
				 */
				
				if("LogicalDiskstart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//逻辑磁盘性能信息开始
					
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			if("LogicalDiskinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//逻辑磁盘性能信息
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
				  				Hashtable rValue = new Hashtable();
					  			if(endelements != null && endelements.size()>0){
					  				for(int m=0;m<endelements.size();m++){
					  					rValue.put(((Element)(endelements.get(m))).getName(), ((Element)(endelements.get(m))).getValue());					  					
					  				}
					  			}
					  			logicdiskperformancelist.add(rValue);
				  			}
				  		}
				  	}
				}
				
				
				/**
				 * 磁盘分区使用信息
				 * 大小、可用空间、盘符、分区格式
				 * 
				 */
				if("diskinfostart".equalsIgnoreCase(((Element)(elements.get(j))).getName())){
					//磁盘信息开始
					List subelements = ((Element)(elements.get(j))).getChildren();
				  	if(subelements != null && subelements.size()>0){
				  		for(int k=0;k<subelements.size();k++){
				  			
				  			
				  			
				  			if("diskinfo".equalsIgnoreCase(((Element)(subelements.get(k))).getName())){
				  				//磁盘信息
				  				
				  				String sizeunit = "G";
				  				//String freesizeunit = "";
				  				
				  				
				  				
				  				List endelements = ((Element)(subelements.get(k))).getChildren();
					  			if(endelements != null && endelements.size()>0){
					  				String caption = "";
					  				String FreeSpace = "";
					  				String Size = "";
					  				
					  				long allsize = 0;
					  				long freesize = 0;
					  				int utilper = 0;
					  				
					  				for(int m=0;m<endelements.size();m++){
					  					
					  					
					  					if("Size".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
	                                        //把磁盘空间大小获取出来
					  						long allblocksize = Long.parseLong(((Element)(endelements.get(m))).getValue());
					  						
											allsize = allblocksize  / 1024;
											
											if(sizeunit.equals("")&& allsize>=1024*1024)
											{
												sizeunit="G";
											}else if(sizeunit.equals("")&& allsize>=1024)
											 {
												sizeunit="M";
											 }else  if(sizeunit.equals("")&& allsize<1024)
											 {
												 sizeunit="K";
											 }
											
											
											if (sizeunit.equals("G")) {
												allsize = allsize / 1024/1024;
												//sizeunit = "G";
											} else if(sizeunit.equals("M")){
												//sizeunit = "M";
												allsize = allsize / 1024;
											}else if(sizeunit.equals("k"))
											 {
												//sizeunit = "K";
											 }
											
					  					}else if("FreeSpace".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						
					  						
					  						
					  						freesize =Long.parseLong(((Element)(endelements.get(m))).getValue());
											freesize = freesize / 1024;
											
											if(sizeunit.equals("")&& freesize>=1024*1024)
											{
												sizeunit="G";
											}else if(sizeunit.equals("")&& freesize>=1024)
											 {
												//System.out.println("====MMM=====");
												sizeunit="M";
											 }else  if(sizeunit.equals("")&& freesize<1024)
											 {
												 sizeunit="K";
											 }
											
											
											if(sizeunit.equals("G"))
											 {
												freesize = freesize /1024/1024;
												//freesizeunit = "G";
											 }else if (sizeunit.equals("M")) {
												freesize = freesize / 1024;
												//freesizeunit = "M";
											} else if (sizeunit.equals("K"))
											  {
												
											  }
											
											
					  					}else if("Caption".equalsIgnoreCase(((Element)(endelements.get(m))).getName())){
					  						caption = ((Element)(endelements.get(m))).getValue();
					  					}
					  				}
					  			
					  				if(allsize-freesize>0){
					  					utilper = Math.round((allsize-freesize)*100/allsize);
					  				}
					  				
					  				Diskcollectdata diskdata = null;
					  				diskdata = new Diskcollectdata();
									diskdata.setIpaddress(ipaddress);
									diskdata.setCollecttime(date);
									diskdata.setCategory("Disk");
									diskdata.setEntity("Utilization");// 利用百分比
									diskdata.setSubentity(caption+"/");
									diskdata.setRestype("static");
									diskdata.setUnit("%");
									diskdata.setThevalue(utilper+"");
									diskVector.addElement(diskdata);

									diskdata = new Diskcollectdata();
									diskdata.setIpaddress(ipaddress);
									diskdata.setCollecttime(date);
									diskdata.setCategory("Disk");
									diskdata.setEntity("AllSize");// 总空间
									diskdata.setSubentity(caption+"/");
									diskdata.setRestype("static");
									diskdata.setThevalue(Math.round(allsize)+"");
									diskdata.setUnit(sizeunit);
									diskVector.addElement(diskdata);

									diskdata = new Diskcollectdata();
									diskdata.setIpaddress(ipaddress);
									diskdata.setCollecttime(date);
									diskdata.setCategory("Disk");
									diskdata.setEntity("UsedSize");// 使用大小
									diskdata.setSubentity(caption+"/");
									diskdata.setRestype("static");
									diskdata.setUnit(sizeunit);
									diskdata.setThevalue(Math.round((allsize-freesize))+"");
									diskVector.addElement(diskdata);
									
									//yangjun 
									String diskinc = "0.0";
									int pastutil = 0;
									Vector disk_v = (Vector)ipAllData.get("disk");
									if (disk_v != null && disk_v.size() > 0) {
										for (int si = 0; si < disk_v.size(); si++) {
											Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
											if((caption+"/").equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
												pastutil = Integer.parseInt(disk_data.getThevalue());
											}
										}
									} else {
										pastutil = utilper;
									}
									if (pastutil == 0) {
										pastutil = utilper;
									}
									if(utilper-pastutil>0){
					  					diskinc = (utilper-pastutil)+"";
					  				}
					  				diskdata = new Diskcollectdata();
									diskdata.setIpaddress(ipaddress);
									diskdata.setCollecttime(date);
									diskdata.setCategory("Disk");
									diskdata.setEntity("UtilizationInc");// 利用增长率百分比
									diskdata.setSubentity(caption+"/");
									diskdata.setRestype("dynamic");
									diskdata.setUnit("%");
									diskdata.setThevalue(diskinc);
									diskVector.addElement(diskdata);
									//
					  			}
				  			}
				  		}
				  	}
				}
				
				
				
				
				
				
			   }
		} catch (Exception e) {
			e.printStackTrace();
		}
		//--------------完成解析文件
		
		//System.out.println("====================================解析文件完成==========================================");
		//主机信息添
		if (hostinfohash != null && hostinfohash.size() > 0)
			returnHash.put("hostinfo", hostinfohash);
		
		//网卡连接信息
		//System.out.println("==========网卡===================");
		
		
		if (networkconfiglist != null && networkconfiglist.size() > 0){
			
//			for(int i=0;i<networkconfiglist.size();i++){
//				Hashtable rValue = (Hashtable)networkconfiglist.get(i);
//				String index = (String)rValue.get("Index");
//				System.out.println("---------------------网卡状态信息-----2222222222222222------------------");
//				if(networkstatuslist != null && networkstatuslist.size()>0){
//					for(int j=0;j<networkstatuslist.size();j++){
//						Hashtable statusvalue = (Hashtable)networkstatuslist.get(j);
//						if(index.equalsIgnoreCase((String)statusvalue.get("Index"))){
//							//得到该网卡的MAC地址和状态
//							rValue.put("NetConnectionStatus", (String)statusvalue.get("NetConnectionStatus"));
//							break;
//						}
//					}
//				}
//				networklist.add(rValue);
//			}
			returnHash.put("networkconfig", networkconfiglist);
		}
		
		//网卡状态信息
		if(networkstatuslist != null && networkstatuslist.size()>0){
			returnHash.put("networkstatus", networkstatuslist);
		}
		
		
	     //把硬盘配置信息放到内存里面
		if (diskconfighash != null && diskconfighash.size() > 0)
			returnHash.put("diskconfig", diskconfighash);
		
		
		//cpu 平均负载
		if (cpuconfiglist != null && cpuconfiglist.size() > 0){
			int cpuperc = 0;
			int cpuvalue = 0;
			int cpuflag = 0;
			for(int i=0;i<cpuconfiglist.size();i++){
				Hashtable cpuhash = (Hashtable)cpuconfiglist.get(i);
				if(cpuhash.containsKey("LoadPercentage")){
					int cpu = Integer.parseInt((String)cpuhash.get("LoadPercentage"));
					cpuvalue = cpuvalue +cpu;
					cpuflag = cpuflag+1;
				}
			}
			//if(cpuflag >0 && cpuconfiglist.size()>0){
			//	cpuperc = cpuvalue/cpuflag;
			//}
			cpuperc = cpuvalue/cpuflag;
			
			
			  cpudata=new CPUcollectdata();
			  cpudata.setIpaddress(ipaddress);
			  cpudata.setCollecttime(date);
			  cpudata.setCategory("CPU");
			  cpudata.setEntity("Utilization");
			  cpudata.setSubentity("Utilization");
			  cpudata.setRestype("dynamic");
			  cpudata.setUnit("%");		
			  cpudata.setThevalue(cpuperc+"");
			  cpuVector.add(0, cpudata);
			returnHash.put("cpuconfig", cpuconfiglist);
			cpupercValue = cpuperc+"";
		}
		
		//物理硬盘列表
		if (physicalDisklist != null && physicalDisklist.size() > 0)
			returnHash.put("physicaldisklist", physicalDisklist);
		
		//内存
		if (memoryhash != null && memoryhash.size() > 0){
			String TotalVisibleMemorySize = (String)memoryhash.get("TotalVisibleMemorySize");
			String FreePhysicalMemory = (String)memoryhash.get("FreePhysicalMemory");
			String TotalVirtualMemorySize = (String)memoryhash.get("TotalVirtualMemorySize");
			String FreeVirtualMemory = (String)memoryhash.get("FreeVirtualMemory");
			
			int totalPhySize = Integer.parseInt(Math.round(Float.parseFloat(TotalVisibleMemorySize.substring(0,TotalVisibleMemorySize.length()-1)))+"");
			int freePhySize = Integer.parseInt(Math.round(Float.parseFloat(FreePhysicalMemory.substring(0,FreePhysicalMemory.length()-1)))+"");
			int totalVirSize = Integer.parseInt(Math.round(Float.parseFloat(TotalVirtualMemorySize.substring(0,TotalVirtualMemorySize.length()-1)))+"");
			int freeVirSize = Integer.parseInt(Math.round(Float.parseFloat(FreeVirtualMemory.substring(0,FreeVirtualMemory.length()-1)))+"");
			int phyperc = 0;
			int virperc = 0;
			if(totalPhySize-freePhySize>0){
				phyperc = (totalPhySize-freePhySize)*100/totalPhySize;
			}
			if(totalVirSize-freeVirSize>0){
				virperc = (totalVirSize-freeVirSize)*100/totalVirSize;
			}
			
			
			Memorycollectdata memorydata = null;
			memorydata=new Memorycollectdata();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Utilization");
			memorydata.setSubentity("PhysicalMemory");
			memorydata.setRestype("dynamic");
			memorydata.setUnit("%");
			memorydata.setThevalue(phyperc+"");
			memoryVector.addElement(memorydata);
			
			memorydata=new Memorycollectdata();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Capability");
			memorydata.setRestype("static");
			memorydata.setSubentity("PhysicalMemory");
			memorydata.setUnit("M");
			memorydata.setThevalue(totalPhySize+"");
			memoryVector.addElement(memorydata);
			
			memorydata=new Memorycollectdata();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("UsedSize");
			memorydata.setRestype("static");
			memorydata.setSubentity("PhysicalMemory");	
			memorydata.setUnit("M");
			memorydata.setThevalue((totalPhySize-freePhySize)+"");
			memoryVector.addElement(memorydata);
			
			memorydata=new Memorycollectdata();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Capability");
			memorydata.setRestype("static");
			memorydata.setSubentity("VirtualMemory");
			memorydata.setUnit("M");
			memorydata.setThevalue(totalVirSize+"");
			memoryVector.addElement(memorydata);
			
			memorydata=new Memorycollectdata();
			memorydata.setIpaddress(ipaddress);
			memorydata.setCollecttime(date);
			memorydata.setCategory("Memory");
			memorydata.setEntity("Utilization");
			memorydata.setSubentity("VirtualMemory");
			memorydata.setRestype("dynamic");
			memorydata.setUnit("%");
			memorydata.setThevalue(virperc+"");
			memoryVector.addElement(memorydata);
			
			returnHash.put("memoryconfig", memoryhash);
		}

		if (memoryVector != null && memoryVector.size() > 0)
			returnHash.put("memory", memoryVector);
		
		
		
		
		//磁盘分区性能
		if (logicdiskperformancelist != null && logicdiskperformancelist.size() > 0){
			List returnList = new ArrayList();
			Hashtable olddisk = new Hashtable();
			//从历史中把结果拿出来
			List oldlogicdiskperformancelist = (List)ipAllData.get("logicdiskperformancelist");
			//判断是否从历史中获取到数据
			if(oldlogicdiskperformancelist != null && oldlogicdiskperformancelist.size()>0){
				
				for(int k=0;k<oldlogicdiskperformancelist.size();k++){
					Hashtable prelogicdisk = (Hashtable)oldlogicdiskperformancelist.get(k);
					//用名字做为k 把数据从内存中取出来
					olddisk.put((String)prelogicdisk.get("Name"), prelogicdisk);
				}
			}
			
			for(int i=0;i<logicdiskperformancelist.size();i++){
				Hashtable newlogicdisk = (Hashtable)logicdiskperformancelist.get(i);
				double result = 0;// 为连个数据的时间差
				double newCountValue = Double.parseDouble((String)newlogicdisk.get("PercentDiskTime"));
				double newCountBase = Double.parseDouble((String)newlogicdisk.get("PercentDiskTime_Base"));
				
				
				if(olddisk.containsKey((String)newlogicdisk.get("Name"))){
					// 获取对应的历史记录
					Hashtable prelogicdisk = (Hashtable)olddisk.get((String)newlogicdisk.get("Name"));
					double prevCountValue = Double.parseDouble((String)prelogicdisk.get("PercentDiskTime"));
					double prevCountBase  = Double.parseDouble((String)prelogicdisk.get("PercentDiskTime_Base"));
					result = ((prevCountValue - newCountValue) / 
			                (prevCountBase - newCountBase)) * 100;	
					
					
					
					//计算出连个时间段内磁盘读取的差值
					long AvgDiskQueueLength=0;
					long AvgDiskReadQueueLength=0;
					long AvgDiskWriteQueueLength=0;
					long DiskWriteBytesPersec=0;
					long AvgDisksecPerWrite=0;
					long AvgDisksecPerRead=0;
					long DiskReadBytesPersec=0;
					long PercentDiskTime_Base=0;
			      
					PercentDiskTime_Base=(Long.parseLong((String)newlogicdisk.get("PercentDiskTime_Base"))-Long.parseLong((String)prelogicdisk.get("PercentDiskTime_Base")));
					
					AvgDiskQueueLength=(Long.parseLong((String)newlogicdisk.get("AvgDiskQueueLength"))-Long.parseLong((String)prelogicdisk.get("AvgDiskQueueLength")));
					
					AvgDiskReadQueueLength=(Long.parseLong((String)newlogicdisk.get("AvgDiskReadQueueLength"))-Long.parseLong((String)prelogicdisk.get("AvgDiskReadQueueLength")));
					
					AvgDiskWriteQueueLength=(Long.parseLong((String)newlogicdisk.get("AvgDiskWriteQueueLength"))-Long.parseLong((String)prelogicdisk.get("AvgDiskWriteQueueLength")));
					
					DiskWriteBytesPersec=(Long.parseLong((String)newlogicdisk.get("DiskWriteBytesPersec"))-Long.parseLong((String)prelogicdisk.get("DiskWriteBytesPersec")));
					
					AvgDisksecPerWrite=(Long.parseLong((String)newlogicdisk.get("AvgDisksecPerWrite"))-Long.parseLong((String)prelogicdisk.get("AvgDisksecPerWrite")));
					
					AvgDisksecPerRead=(Long.parseLong((String)newlogicdisk.get("AvgDisksecPerRead"))-Long.parseLong((String)prelogicdisk.get("AvgDisksecPerRead")));
					
					DiskReadBytesPersec=(Long.parseLong((String)newlogicdisk.get("DiskReadBytesPersec"))-Long.parseLong((String)prelogicdisk.get("DiskReadBytesPersec")));
					
					
					
					
					//System.out.println("===============================PercentDiskTime_Base="+PercentDiskTime_Base);
					Hashtable disklist=new Hashtable ();
					
					if(PercentDiskTime_Base==0)
					{
						disklist.put("AvgDiskQueueLength", 0);
						disklist.put("AvgDiskReadQueueLength", 0);
						disklist.put("AvgDiskWriteQueueLength", 0);
						disklist.put("DiskWriteBytesPersec", 0);
						disklist.put("AvgDisksecPerWrite",0);
						disklist.put("AvgDisksecPerRead", 0);
						disklist.put("DiskReadBytesPersec", 0);
					}
					else{
					disklist.put("AvgDiskQueueLength", AvgDiskQueueLength*100/PercentDiskTime_Base );
					disklist.put("AvgDiskReadQueueLength", AvgDiskReadQueueLength*100/PercentDiskTime_Base);
					disklist.put("AvgDiskWriteQueueLength", AvgDiskWriteQueueLength*100/PercentDiskTime_Base);
					disklist.put("DiskWriteBytesPersec", DiskWriteBytesPersec/300/1024*8);
					disklist.put("AvgDisksecPerWrite", AvgDisksecPerWrite/300/1024*8);
					disklist.put("AvgDisksecPerRead", AvgDisksecPerRead/300/1024*8);
					disklist.put("DiskReadBytesPersec", DiskReadBytesPersec/300/1024*8);
					}
					//disklist.put("PercentDiskTime_Base", PercentDiskTime_Base);
					disklist.put("Name", (String)newlogicdisk.get("Name"));
					diskpef.add(disklist);
					//returnHash.put("diskperforlist", disklist);
					
				}
				
				
				newlogicdisk.put("DiskBusy", Math.round(result)+"");
				double ReadBytesPersec = Double.parseDouble((String)newlogicdisk.get("DiskReadBytesPersec"))/1024;
				double WriteBytesPersec = Double.parseDouble((String)newlogicdisk.get("DiskWriteBytesPersec"))/1024;
				
				
				Diskcollectdata diskdata = null;
  				diskdata = new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("Busy");// 利用百分比
				diskdata.setSubentity((String)newlogicdisk.get("Name")+"/");
				diskdata.setRestype("static");
				diskdata.setUnit("%");
				diskdata.setThevalue(Math.round(result)+"");
				diskVector.addElement(diskdata);
				
				//读KBytes/s
				diskdata = new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("ReadBytesPersec");//读KBytes/s
				diskdata.setSubentity((String)newlogicdisk.get("Name")+"/");
				diskdata.setRestype("static");
				diskdata.setUnit("");
				diskdata.setThevalue(Math.round(ReadBytesPersec)+"");
				diskVector.addElement(diskdata);
				//写KBytes/s
				diskdata = new Diskcollectdata();
				diskdata.setIpaddress(ipaddress);
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("WriteBytesPersec");//写KBytes/s
				diskdata.setSubentity((String)newlogicdisk.get("Name")+"/");
				diskdata.setRestype("static");
				diskdata.setUnit("");
				diskdata.setThevalue(Math.round(WriteBytesPersec)+"");
				diskVector.addElement(diskdata);
				
				
				//returnList.add(newlogicdisk);
			}
			returnHash.put("diskperforlist", diskpef);
			returnHash.put("logicdiskperformancelist", logicdiskperformancelist);
			//returnHash.put("logicdiskperformancelist", returnList);
		}
		
		
		
		
		
		
		
		//磁盘分区读写性能
		if (alldiskperformancehash != null && alldiskperformancehash.size() > 0){
			oldalldiskperformancehash = (Hashtable)ipAllData.get("alldiskperformancehash");
			if(oldalldiskperformancehash != null && oldalldiskperformancehash.size()>0){
				double prevCountValue = Double.parseDouble((String)oldalldiskperformancehash.get("PercentDiskTime"));
				double prevCountBase  = Double.parseDouble((String)oldalldiskperformancehash.get("PercentDiskTime_Base"));
				double newCountValue = Double.parseDouble((String)alldiskperformancehash.get("PercentDiskTime"));
				double newCountBase = Double.parseDouble((String)alldiskperformancehash.get("PercentDiskTime_Base"));
				double result = ((prevCountValue - newCountValue) / 
		                (prevCountBase - newCountBase)) * 100;
				alldiskperformancehash.put("DiskBusy", Math.round(result)+"");
			}else{
				alldiskperformancehash.put("DiskBusy", "0");
			}
			
			returnHash.put("alldiskperformance", alldiskperformancehash);
			
		}
		
		//告警检测
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "windows");
			CheckEventUtil checkutil = new CheckEventUtil();
			for(int i = 0 ; i < list.size() ; i ++){
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				String alarmIndicatorsnodename = alarmIndicatorsnode.getName();
				//cpu告警
				if(("cpu").equalsIgnoreCase(alarmIndicatorsnodename)){
					checkutil.checkEvent(node, alarmIndicatorsnode, cpupercValue);
				}
				//磁盘告警
				if(("diskperc").equalsIgnoreCase(alarmIndicatorsnodename)){
					if(diskVector != null && diskVector.size() > 0){
					    checkutil.checkDisk(node,diskVector,alarmIndicatorsnode);
					}
				}
				//服务告警 
				if("service".equalsIgnoreCase(alarmIndicatorsnodename)){
					if(servicelist != null && servicelist.size() > 0){
						Vector serviceVector = new Vector();
						for(int j=0; j<servicelist.size(); j++){
							Object obj = servicelist.get(i);
			   	   			if(obj instanceof Hashtable){
								Hashtable serviceItemHash = (Hashtable)obj;
			   	   				ProcessNetData processNetData = new ProcessNetData();
			   	   				ServiceNodeTemp serviceNodeTemp = processNetData.getServiceNodeTempByHashtable(serviceItemHash);
			   	   				Servicecollectdata servicecollectdata = new Servicecollectdata();
			   	   				try {
			   						BeanUtils.copyProperties(servicecollectdata,serviceNodeTemp);
			   					} catch (Exception e) {
			   						e.printStackTrace();
			   					}
			   					serviceVector.add(servicecollectdata);
			   	   			}
						}
						checkutil.createHostServiceGroupEventList(node.getIpAddress() , serviceVector, alarmIndicatorsnode);
					}
				}
				//进程告警
				if("process".equalsIgnoreCase(alarmIndicatorsnodename)){
					if(processVector != null && processVector.size() > 0){
						checkutil.createProcessGroupEventList(node.getIpAddress() , processVector , alarmIndicatorsnode);
					}
				}
				//物理内存
				if("physicalmemory".equalsIgnoreCase(alarmIndicatorsnodename)){
					Hashtable collectHash = new Hashtable();
					collectHash.put("physicalmem", memoryVector);//物理内存
					checkutil.updateData(node,collectHash,"host","windows",alarmIndicatorsnode);
				}
				//虚拟内存告警
				if("virtualmemory".equalsIgnoreCase(alarmIndicatorsnodename)){
					Hashtable collectHash = new Hashtable();
					collectHash.put("virtalmem", memoryVector);//虚拟内存
					checkutil.updateData(node,collectHash,"host","windows",alarmIndicatorsnode);
				}
				//入口流速  出口流速  网卡流速信息
				if("AllInBandwidthUtilHdx".equalsIgnoreCase(alarmIndicatorsnodename) || "AllOutBandwidthUtilHdx".equalsIgnoreCase(alarmIndicatorsnodename)
						|| "utilhdx".equalsIgnoreCase(alarmIndicatorsnodename)){
					Hashtable collectHash = new Hashtable();
					//由于元素类型不一致，得转换一下
					Vector utilhdxVectorTemp = new Vector();
					for(int j=0; j<utilhdxVector.size(); j++){
						UtilHdx utilHdx = (UtilHdx)utilhdxVector.get(j);
						AllUtilHdx allUtilHdx = new AllUtilHdx();
						BeanUtils.copyProperties(allUtilHdx, utilHdx);
						utilhdxVectorTemp.add(allUtilHdx);
					}
					collectHash.put("allutilhdx", utilhdxVectorTemp);
					checkutil.updateData(node,collectHash,"host","windows",alarmIndicatorsnode);
				}
				//硬件信息告警   【暂无告警】
				//存储的告警      【暂无告警】storage
				//arp的告警       【暂无告警】ipmac
				//安装的软件信息【暂无告警】 software
				//接口信息          【暂无告警】interface
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		
		
		
		if (processVector != null && processVector.size() > 0)
			returnHash.put("process", processVector);
		if (diskVector != null && diskVector.size() > 0){
			returnHash.put("disk", diskVector);
		}
		if (cpuVector != null && cpuVector.size() > 0){
			returnHash.put("cpu", cpuVector);
		}
		if (pefmemoryhash != null && pefmemoryhash.size() > 0)
			returnHash.put("pefmemory", pefmemoryhash);
		if (pefcpuhash != null && pefcpuhash.size() > 0)
			returnHash.put("pefcpu", pefcpuhash);
		if (servicelist != null && servicelist.size() > 0)
			returnHash.put("service", servicelist);
		if (utilhdxVector != null && utilhdxVector.size()>0){
			returnHash.put("utilhdx",utilhdxVector);
		}
		if (interfaceVector != null && interfaceVector.size()>0)
			returnHash.put("interface",interfaceVector);
		
		if (userlist != null && userlist.size()>0)
			returnHash.put("user",userlist);
		if (iflist != null && iflist.size()>0)returnHash.put("iflist",iflist);
		
		return returnHash;
	}

	public String getMaxNum(String ipAddress) {
		String maxStr = null;
		File logFolder = new File(ResourceCenter.getInstance().getSysPath()
				+ "/linuxserver/");
		String[] fileList = logFolder.list();

		for (int i = 0; i < fileList.length; i++) // 找一个最新的文件
		{
			if (!fileList[i].startsWith(ipAddress))
				continue;

			return ipAddress;
		}
		return maxStr;
	}

	public void deleteFile(String ipAddress) {

		try {
			File delFile = new File(ResourceCenter.getInstance().getSysPath()
					+ "/linuxserver/" + ipAddress + ".log");
			// System.out.println("删除的文件为："+delFile);
			// delFile.delete();
		} catch (Exception e) {
		}
	}

	public void copyFile(String ipAddress, String max) {
		try {
			String cmd = "cmd   /c   copy   "
					+ ResourceCenter.getInstance().getSysPath()
					+ "\\linuxserver\\" + ipAddress + ".log" + " "
					+ ResourceCenter.getInstance().getSysPath()
					+ "\\linuxserver_bak\\" + ipAddress + ".log";
			// System.out.println(cmd);
			// String cmd = "E:\\Program Files\\Internet
			// Explorer\\IEXPLORE.EXE";
			Process child = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			System.err.println(e);
			// e.printStackTrace();
		}

	}

	public void createSMS(Procs procs) {
		Procs lastprocs = null;
		// 建立短信
		procs.setCollecttime(Calendar.getInstance());
		// 从已经发送的短信列表里获得当前该PROC已经发送的短信
		lastprocs = (Procs) sendeddata.get(procs.getIpaddress() + ":"
				+ procs.getProcname());
		// try{
		// if (lastprocs==null){
		// //内存中不存在 ,表明没发过短信,则发短信
		// Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
		// Smscontent smscontent = new Smscontent();
		// String time = sdf.format(procs.getCollecttime().getTime());
		// smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
		// //发送短信
		// Vector tosend = new Vector();
		// tosend.add(smscontent);
		// smsmanager.sendSmscontent(tosend);
		// //把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
		// sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);
		// }else{
		// //若已经发送的短信列表存在这个IP的PROC进程
		// //若在，则从已发送短信列表里判断是否已经发送当天的短信
		// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		// Date last = null;
		// Date current = null;
		// Calendar sendcalen = (Calendar)lastprocs.getCollecttime();
		// Date cc = sendcalen.getTime();
		// String tempsenddate = formatter.format(cc);
		//		 			
		// Calendar currentcalen = (Calendar)procs.getCollecttime();
		// cc = currentcalen.getTime();
		// last = formatter.parse(tempsenddate);
		// String currentsenddate = formatter.format(cc);
		// current = formatter.parse(currentsenddate);
		//		 			
		// long subvalue = current.getTime()-last.getTime();
		//		 			
		// if (subvalue/(1000*60*60*24)>=1){
		// //超过一天，则再发信息
		// Smscontent smscontent = new Smscontent();
		// String time = sdf.format(procs.getCollecttime().getTime());
		// Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
		// if (equipment == null){
		// return;
		// }else
		// smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
		//			 			
		// //发送短信
		// Vector tosend = new Vector();
		// tosend.add(smscontent);
		// smsmanager.sendSmscontent(tosend);
		// //把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
		// sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);
		// }else{
		// //没超过一天,则只写事件
		// Vector eventtmpV = new Vector();
		// Eventlist event = new Eventlist();
		// Monitoriplist monitoriplist =
		// (Monitoriplist)monitormanager.getByIpaddress(procs.getIpaddress());
		// event.setEventtype("host");
		// event.setEventlocation(procs.getIpaddress());
		// event.setManagesign(new Integer(0));
		// event.setReportman("monitorpc");
		// event.setRecordtime(Calendar.getInstance());
		// event.setLevel1(new Integer(1));
		// event.setEquipment(equipmentManager.getByip(monitoriplist.getIpaddress()));
		// event.setNetlocation(equipmentManager.getByip(monitoriplist.getIpaddress()).getNetlocation());
		// String time = sdf.format(Calendar.getInstance().getTime());
		// event.setContent(monitoriplist.getEquipname()+"&"+monitoriplist.getIpaddress()+"&"+time+"进程"+procs.getProcname()+"丢失&level=1");
		// eventtmpV.add(event);
		// try{
		// eventmanager.createEventlist(eventtmpV);
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		// }
		// }
		// }catch(Exception e){
		// e.printStackTrace();
		// }
	}

}
