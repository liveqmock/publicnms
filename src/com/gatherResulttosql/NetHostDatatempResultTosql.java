package com.gatherResulttosql;

import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;

import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;

import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Devicecollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Servicecollectdata;
import com.afunms.polling.om.Softwarecollectdata;
import com.afunms.polling.om.Storagecollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.gatherdb.GathersqlListManager;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 * 采集与数据分离模式采集结果转换成sql
 * 
 * @author konglq
 * 
 */
public class NetHostDatatempResultTosql {
	/**
	 * 更加内存的参数判断是否是分离模式 如果是分离模式则把内存的结果
	 */
	
	
	
	
	public void ResulttoSql()
	{
		// 数据分离模糊参数
		if("1".equals(PollingEngine.getCollectwebflag()))
		{
			
			// 从内存中获取采集的说有结果集
			
			Hashtable allpinghash = new Hashtable();
			Hashtable allcpuhash = new Hashtable();
			Hashtable allpmemoryhash = new Hashtable();
			Hashtable allvmemoryhash = new Hashtable();
			Hashtable alldiskhash = new Hashtable();
			Hashtable allstoragehash = new Hashtable();
			Hashtable allhardwarehash = new Hashtable();
			Hashtable allsoftwarehash = new Hashtable();
			Hashtable allservicehash = new Hashtable();
			// Hashtable allinpackshash = new Hashtable();
			// Hashtable alloutpackshash = new Hashtable();
			Hashtable allipmachash = new Hashtable();
			// Hashtable allinterfacehash = new Hashtable();
			Hashtable allsystemgrouphash = new Hashtable();
			Hashtable allprocesshash = new Hashtable();
			NodeUtil nodeUtil = new NodeUtil();
			
			Hashtable datahash=ShareData.getSharedata();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(datahash != null && datahash.size()>0){
				Enumeration iphash = datahash.keys();
			
				StringBuffer sbuffer=new StringBuffer();
				
			
					while(iphash.hasMoreElements()){
						String ip = (String)iphash.nextElement();   				  				
	    				Hashtable ipdata = (Hashtable)datahash.get(ip);
	    				if(ipdata != null){
	    					
	    					Enumeration ipdatahash = ipdata.keys();
	    					while(iphash.hasMoreElements()){
	    						String name = (String)iphash.nextElement(); 
	    					
	    					
	    					// 处理主机设备的数据
	    					// ping信息
	    					if(ipdata.containsKey("ping")){
	    						Hashtable pinghash = (Hashtable)ipdata.get("ping");
	    						Vector pingVector = (Vector) pinghash.get("ping");
	    						if (pingVector != null && pingVector.size() > 0) {
	    							allpinghash.put(ip, pingVector);
	    						}
	    					}
	    					
	    					// CPU信息
	    					if(ipdata.containsKey("cpu")){
	    						//Hashtable cpuhash = (Hashtable)ipdata.get("cpu");
	    						Vector cpuVector = (Vector) ipdata.get("cpu");
	    						if (cpuVector != null && cpuVector.size() > 0) {
	    							allcpuhash.put(ip, cpuVector);
	    						}
	    					}
	    					// physicalmemory
	    					if(ipdata.containsKey("physicalmemory")){
	    						Hashtable physicalmemoryhash = (Hashtable)ipdata.get("physicalmemory");
	    						Vector memoryVector = (Vector) physicalmemoryhash.get("memory");
	    						if (memoryVector != null && memoryVector.size() > 0) {
	    							allpmemoryhash.put(ip, memoryVector);
	    						}
	    					}
	    					
	    					// virtualmemory
	    					if(ipdata.containsKey("virtualmemory")){
	    						Hashtable virtualmemoryhash = (Hashtable)ipdata.get("virtualmemory");
	    						Vector memoryVector = (Vector) virtualmemoryhash.get("memory");
	    						if (memoryVector != null && memoryVector.size() > 0) {
	    							allvmemoryhash.put(ip, memoryVector);
	    						}
	    					}
	    					// ---------------------------
	    					// 进程
	    					if(ipdata.containsKey("process")){
	    						Hashtable processhash = (Hashtable)ipdata.get("process");
	    						Vector processVector = (Vector) processhash.get("process");
	    						if (processVector != null && processVector.size() > 0) {
	    							allprocesshash.put(ip, processVector);
	    						}
	    					}
	    					
	    					// disk
	    					if(ipdata.containsKey("disk")){
	    						Hashtable diskhash = (Hashtable)ipdata.get("disk");
	    						Vector diskVector = (Vector) diskhash.get("disk");
	    						if (diskVector != null && diskVector.size() > 0) {
	    							alldiskhash.put(ip, diskVector);
	    						}
	    					}
	    					
	    					// 存储
	    					if(ipdata.containsKey("storage")){
								Hashtable storhash = (Hashtable)ipdata.get("storage");
								Vector storVector = (Vector) storhash.get("storage");
								if (storVector != null && storVector.size() > 0) {
									allstoragehash.put(ip, storVector);
								}
	    					}
	    					
	    					// 硬件信息
	    					if(ipdata.containsKey("hardware")){
								Hashtable devhash = (Hashtable)ipdata.get("hardware");
								Vector devVector = (Vector) devhash.get("device");
								if (devVector != null && devVector.size() > 0) {
									allhardwarehash.put(ip, devVector);
								}
	    					}
	    					
	    					// 软件信息
	    					if(ipdata.containsKey("software")){
								Hashtable softhash = (Hashtable)ipdata.get("software");
								Vector sofVector = (Vector) softhash.get("software");
								if (sofVector != null && sofVector.size() > 0) {
									allsoftwarehash.put(ip, sofVector);
								}
	    					}
	    					// -------------
	    					// 服务信息
	    					if(ipdata.containsKey("service")){
								Hashtable servhash = (Hashtable)ipdata.get("service");
								Vector servVector = (Vector) servhash.get("winservice");
								if (servVector != null && servVector.size() > 0) {
									allservicehash.put(ip, servVector);
								}
	    					}
	    					

	    					
	    					// ARP信息
	    					if (ipdata.containsKey("ipmac")) {
	    						//Hashtable ipmachash = (Hashtable)ipdata.get("ipmac");
	    						Vector ipmacVector = (Vector) ipdata.get("ipmac");
	    						if (ipmacVector != null && ipmacVector.size() > 0) {
	    							allipmachash.put(ip, ipmacVector);
	    						}
	    					}
	    					

	    					
	    					// 系统属性信息
	    					if(ipdata.containsKey("systemgroup")){
	    						Hashtable systemhash = (Hashtable)ipdata.get("systemgroup");
	    						Vector systemVector = (Vector) systemhash.get("system");
	    						if (systemVector != null && systemVector.size() > 0) {
	    							allsystemgrouphash.put(ip, systemVector);
	    						}
	    					}
	    					
	    					
	    				}
	    				}
					}

					
					System.out.println("====allpinghash==="+allpinghash.size());
					// 处理PING入库
					if(allpinghash != null && allpinghash.size()>0){
						boolean flag=false;
						// XmlDataOperator xmlOpr=null;
						Enumeration pinghash = allpinghash.keys();

						while(pinghash.hasMoreElements()){
							String ip = (String)pinghash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector pingVector = (Vector)allpinghash.get(ip);						
							if(pingVector != null && pingVector.size()>0){
								String deleteSql = "delete from nms_ping_data_temp where nodeid='" +host.getId() + "'";
								String hendsql = "insert into nms_ping_data_temp"
									+ "(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak) "
									+ "values(";
								String endsql=")";
								
								sbuffer=new StringBuffer(hendsql);
								Vector list=new Vector();
								// dbmanager.addBatch(deleteSql);
								for(int i=0;i<pingVector.size();i++){
									Pingcollectdata vo = (Pingcollectdata) pingVector.elementAt(i);
									try {
										
										Calendar tempCal = (Calendar) vo.getCollecttime();
										Date cc = tempCal.getTime();
										String time = sdf.format(cc);
										
										sbuffer = new StringBuffer();
										sbuffer.append(hendsql);
										sbuffer.append("'").append(host.getId()).append("',");
										sbuffer.append("'").append(host.getIpAddress()).append("',");
										sbuffer.append("'").append(host.getType()).append("',");
										sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
										sbuffer.append("'").append(vo.getCategory()).append("',");
										sbuffer.append("'").append(vo.getEntity()).append("',");
										sbuffer.append("'").append(vo.getSubentity()).append("',");
										sbuffer.append("'").append(vo.getThevalue()).append("',");
										sbuffer.append("'").append(vo.getChname()).append("',");
										sbuffer.append("'").append(vo.getRestype()).append("',");
//										sbuffer.append("'").append(time).append("',");
										if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
											sbuffer.append("'").append(time).append("',");
										}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
											sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
										}
										sbuffer.append("'").append(vo.getUnit()).append("',");
										sbuffer.append("'").append(vo.getBak()).append("'");
										sbuffer.append(endsql);
										list.add(sbuffer.toString());
						    
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								
								GathersqlListManager.AdddateTempsql(deleteSql, list);
							}
							
							
						}
					}
					
					System.out.println("====allcpuhash==="+allcpuhash.size());
					// 处理CPU入库
					if(allcpuhash != null && allcpuhash.size()>0){
						
						
						Enumeration cpuhash = allcpuhash.keys();
						String hendsql="insert into nms_cpu_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('";
						String endsql=")";
						
						
						while(cpuhash.hasMoreElements()){
							String ip = (String)cpuhash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector cpuVector = (Vector)allcpuhash.get(ip);
							String deleteSql = "delete from nms_cpu_data_temp where nodeid='" +host.getId() + "'";
							// dbmanager.addBatch(deleteSql);
							// 得到CPU平均
							CPUcollectdata vo = (CPUcollectdata) cpuVector.elementAt(0);
							
							
								Calendar tempCal = (Calendar) vo.getCollecttime();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								Vector list=new Vector();
								sbuffer = new StringBuffer();
								sbuffer.append(hendsql);
								sbuffer.append("'").append(host.getId()).append("',");
								sbuffer.append("'").append(host.getIpAddress()).append("',");
								sbuffer.append("'").append(host.getType()).append("',");
								sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
								sbuffer.append("'").append(vo.getCategory()).append("',");
								sbuffer.append("'").append(vo.getEntity()).append("',");
								sbuffer.append("'").append(vo.getSubentity()).append("',");
								sbuffer.append("'").append(vo.getThevalue()).append("',");
								sbuffer.append("'").append(vo.getChname()).append("',");
								sbuffer.append("'").append(vo.getRestype()).append("',");
//								sbuffer.append("'").append(time).append("',");
								if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
									sbuffer.append("'").append(time).append("',");
								}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
									sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
								}
								sbuffer.append("'").append(vo.getUnit()).append("',");
								sbuffer.append("'").append(vo.getBak()).append("'");
								sbuffer.append(endsql);
								
                                System.out.println(sbuffer.toString());								
								list.add(sbuffer.toString());
							    GathersqlListManager.AdddateTempsql(deleteSql, list);
						}// 结束迭代
				
					}
					
					// 处理物理内存入库
					if(allpmemoryhash != null && allpmemoryhash.size()>0){
						
						Enumeration pmhash = allpmemoryhash.keys();
						String hendsql="insert into nms_memory_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('";
						String endsql=")";
						
						
						
						while(pmhash.hasMoreElements()){
							String ip = (String)pmhash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector memoryVector = (Vector)allpmemoryhash.get(ip);
							String deleteSql = "delete from nms_memory_data_temp where nodeid='" +host.getId() + "' and sindex='PhysicalMemory'";
							
							if(memoryVector != null && memoryVector.size()>0){
								for(int i=0;i<memoryVector.size();i++){
									Memorycollectdata vo = (Memorycollectdata) memoryVector.elementAt(i);
									if(!vo.getSubentity().equalsIgnoreCase("PhysicalMemory"))continue;
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);
									Vector list=new Vector();
									sbuffer = new StringBuffer();
									sbuffer.append(hendsql);
									sbuffer.append("'").append(host.getId()).append("',");
									sbuffer.append("'").append(host.getIpAddress()).append("',");
									sbuffer.append("'").append(host.getType()).append("',");
									sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
									sbuffer.append("'").append(vo.getCategory()).append("',");
									sbuffer.append("'").append(vo.getEntity()).append("',");
									sbuffer.append("'").append(vo.getSubentity()).append("',");
									sbuffer.append("'").append(vo.getThevalue()).append("',");
									sbuffer.append("'").append(vo.getChname()).append("',");
									sbuffer.append("'").append(vo.getRestype()).append("',");
//									sbuffer.append("'").append(time).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									sbuffer.append("'").append(vo.getUnit()).append("',");
									sbuffer.append("'").append(vo.getBak()).append("'");
									sbuffer.append(endsql);
									list.add(sbuffer.toString());
								    GathersqlListManager.AdddateTempsql(deleteSql, list);
										
								}
							}
						}
						
					}
					
					System.out.println("====allvmemoryhash==="+allvmemoryhash.size());
					// 处理虚拟内存入库
					if(allvmemoryhash != null && allvmemoryhash.size()>0){
						
						Enumeration vmhash = allvmemoryhash.keys();
						String hendsql="insert into nms_memory_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('";
						String endsql=")";
						
						
						while(vmhash.hasMoreElements()){
							String ip = (String)vmhash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector memoryVector = (Vector)allvmemoryhash.get(ip);
							String deleteSql = "delete from nms_memory_data_temp where nodeid='" +host.getId() + "' and sindex='VirtualMemory'";
							
							if(memoryVector != null && memoryVector.size()>0){
								for(int i=0;i<memoryVector.size();i++){
									Memorycollectdata vo = (Memorycollectdata) memoryVector.elementAt(i);
									if(!vo.getSubentity().equalsIgnoreCase("VirtualMemory"))continue;
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);								
									Vector list=new Vector();
									sbuffer = new StringBuffer();
									sbuffer.append(hendsql);
									sbuffer.append("'").append(host.getId()).append("',");
									sbuffer.append("'").append(host.getIpAddress()).append("',");
									sbuffer.append("'").append(host.getType()).append("',");
									sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
									sbuffer.append("'").append(vo.getCategory()).append("',");
									sbuffer.append("'").append(vo.getEntity()).append("',");
									sbuffer.append("'").append(vo.getSubentity()).append("',");
									sbuffer.append("'").append(vo.getThevalue()).append("',");
									sbuffer.append("'").append(vo.getChname()).append("',");
									sbuffer.append("'").append(vo.getRestype()).append("',");
//									sbuffer.append("'").append(time).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									sbuffer.append("'").append(vo.getUnit()).append("',");
									sbuffer.append("'").append(vo.getBak()).append("'");
									sbuffer.append(endsql);
									list.add(sbuffer.toString());
								    GathersqlListManager.AdddateTempsql(deleteSql, list);								 
									
								}
							}
						}
					}
				
					System.out.println("====allprocesshash==="+allprocesshash.size());
					// 处理进程信息入库
					if(allprocesshash != null && allprocesshash.size()>0){
						Enumeration processhash = allprocesshash.keys();
						String hendsql="insert into nms_process_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values(";
						String endsql=")";
						
						while(processhash.hasMoreElements()){
							String ip = (String)processhash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector processVector = (Vector)allprocesshash.get(ip);
							String deleteSql = "delete from nms_process_data_temp where nodeid='" +host.getId() + "'";
							if(processVector != null && processVector.size()>0){
								for(int i=0;i<processVector.size();i++){
									Processcollectdata vo = (Processcollectdata) processVector.elementAt(i);
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);	
									String thevalue = vo.getThevalue();
									if(thevalue != null){
										thevalue = thevalue.replaceAll("\\\\", "/");
										if(thevalue.length() > 50){
											thevalue = thevalue.substring(0, 50)+"...";
										}
									}
											
									Vector list=new Vector();
									sbuffer = new StringBuffer();
									sbuffer.append(hendsql);
									sbuffer.append("'").append(host.getId()).append("',");
									sbuffer.append("'").append(host.getIpAddress()).append("',");
									sbuffer.append("'").append(host.getType()).append("',");
									sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
									sbuffer.append("'").append(vo.getCategory()).append("',");
									sbuffer.append("'").append(vo.getEntity()).append("',");
									sbuffer.append("'").append(vo.getSubentity()).append("',");
									sbuffer.append("'").append(vo.getThevalue()).append("',");
									sbuffer.append("'").append(vo.getChname()).append("',");
									sbuffer.append("'").append(vo.getRestype()).append("',");
//									sbuffer.append("'").append(time).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									sbuffer.append("'").append(vo.getUnit()).append("',");
									sbuffer.append("'").append(vo.getBak()).append("'");
									sbuffer.append(endsql);
									list.add(sbuffer.toString());
								    GathersqlListManager.AdddateTempsql(deleteSql, list);

									
								}
							}
						}
					}
					
					System.out.println("====alldiskhash==="+alldiskhash.size());
					// 处理磁盘入库
					if(alldiskhash != null && alldiskhash.size()>0){
						
						Enumeration diskhash = alldiskhash.keys();
						String hendsql="insert into nms_disk_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('";
						String endsql=")";
						
						while(diskhash.hasMoreElements()){
							String ip = (String)diskhash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector diskVector = (Vector)alldiskhash.get(ip);
							String deleteSql = "delete from nms_disk_data_temp where nodeid='" +host.getId() + "'";
							if(diskVector != null && diskVector.size()>0){
								for(int i=0;i<diskVector.size();i++){
									Diskcollectdata vo = (Diskcollectdata) diskVector.elementAt(i);
									// if (vo.getSubentity().equals("Physical
									// Memory") ||
									// vo.getSubentity().equals("Virtual
									// Memory")||
									// vo.getSubentity().trim().length()==0)continue;
									Calendar tempCal = (Calendar) vo.getCollecttime();
									Date cc = tempCal.getTime();
									String time = sdf.format(cc);								
										
									Vector list=new Vector();
									sbuffer = new StringBuffer();
									sbuffer.append(hendsql);
									sbuffer.append("'").append(host.getId()).append("',");
									sbuffer.append("'").append(host.getIpAddress()).append("',");
									sbuffer.append("'").append(host.getType()).append("',");
									sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
									sbuffer.append("'").append(vo.getCategory()).append("',");
									sbuffer.append("'").append(vo.getEntity()).append("',");
									sbuffer.append("'").append(vo.getSubentity()).append("',");
									sbuffer.append("'").append(vo.getThevalue()).append("',");
									sbuffer.append("'").append(vo.getChname()).append("',");
									sbuffer.append("'").append(vo.getRestype()).append("',");
//									sbuffer.append("'").append(time).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									sbuffer.append("'").append(vo.getUnit()).append("',");
									sbuffer.append("'").append(vo.getBak()).append("'");
									sbuffer.append(endsql);
									list.add(sbuffer.toString());
								    GathersqlListManager.AdddateTempsql(deleteSql, list);

									    
								}
							}
						}
						
					}
					
					// 处理存储信息入库
					if(allstoragehash != null && allstoragehash.size()>0){
						Enumeration storagehash = allstoragehash.keys();
						String hendsql="insert into nms_storage_data_temp(nodeid,ip,type,subtype,name,stype,cap,storageindex,collecttime)values('";
						String endsql=")";
						
						while(storagehash.hasMoreElements()){
							String ip = (String)storagehash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector storageVector = (Vector)allstoragehash.get(ip);
							String deleteSql = "delete from nms_storage_data_temp where nodeid='" +host.getId() + "'";
							if(storageVector != null && storageVector.size()>0){
								Calendar tempCal=Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								
								
								Vector list=new Vector();
								for(int i=0;i<storageVector.size();i++){
									Storagecollectdata vo = (Storagecollectdata) storageVector.elementAt(i);
									sbuffer = new StringBuffer();
									sbuffer.append(hendsql);
									sbuffer.append("'").append(host.getId()).append("',");
									sbuffer.append("'").append(host.getIpAddress()).append("',");
									sbuffer.append("'").append(host.getType()).append("',");
									sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
									sbuffer.append("'").append(vo.getName().replace("\\", "/")).append("',");
									sbuffer.append("'").append(vo.getType()).append("',");
									sbuffer.append("'").append(vo.getCap()).append("',");
									sbuffer.append("'").append(vo.getStorageindex()).append("',");
//									sbuffer.append("'").append(time).append("'");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("'").append(time).append("'");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
									}
									sbuffer.append(endsql);
									list.add(sbuffer.toString());
									
								}
								
								GathersqlListManager.AdddateTempsql(deleteSql, list);

							}
						}
					}
					
					// 处理硬件信息入库
					if(allhardwarehash != null && allhardwarehash.size()>0){
						Enumeration hardwarehash = allhardwarehash.keys();
						String hendsql="insert into nms_device_data_temp(nodeid,ip,type,subtype,name,deviceindex,dtype,status,collecttime)values('";
						String endsql=")";
						
						while(hardwarehash.hasMoreElements()){
							String ip = (String)hardwarehash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector hardwareVector = (Vector)allhardwarehash.get(ip);
							String deleteSql = "delete from nms_device_data_temp where nodeid='" +host.getId() + "'";
							if(hardwareVector != null && hardwareVector.size()>0){
								Calendar tempCal=Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								Vector list=new Vector();
								for(int i=0;i<hardwareVector.size();i++){
									Devicecollectdata vo = (Devicecollectdata) hardwareVector.elementAt(i);	
									String name = vo.getName();
									if(name != null){
										name = name.replaceAll("\\\\", "/").replaceAll("'", "");
									}
									
									
									sbuffer = new StringBuffer();
									sbuffer.append(hendsql);
									sbuffer.append("'").append(host.getId()).append("',");
									sbuffer.append("'").append(host.getIpAddress()).append("',");
									sbuffer.append("'").append(host.getType()).append("',");
									sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
									sbuffer.append("'").append(name).append("',");
									sbuffer.append("'").append(vo.getDeviceindex()).append("',");
									sbuffer.append("'").append(vo.getType()).append("',");
									sbuffer.append("'").append(vo.getStatus()).append("',");
									//sbuffer.append("'").append(time).append("'");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("'").append(time).append("'");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
									}
									sbuffer.append(endsql);
									list.add(sbuffer.toString());
									
									    
								}
								GathersqlListManager.AdddateTempsql(deleteSql, list);
							}
						}
					}
					
					// 处理软件信息入库
					if(allsoftwarehash != null && allsoftwarehash.size()>0){
						
						Enumeration softwarehash = allsoftwarehash.keys();
						String hendsql="insert into nms_software_data_temp(nodeid,ip,type,subtype,insdate,name,stype,swid,collecttime) values('";
						String endsql=")";

						while(softwarehash.hasMoreElements()){
							String ip = (String)softwarehash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector softwareVector = (Vector)allsoftwarehash.get(ip);
							String deleteSql = "delete from nms_software_data_temp where nodeid='" +host.getId() + "'";
							Vector list=new Vector();
							
							if(softwareVector != null && softwareVector.size()>0){
								Calendar tempCal=Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for(int i=0;i<softwareVector.size();i++){
									Softwarecollectdata vo = (Softwarecollectdata) softwareVector.elementAt(i);								
									String name = vo.getName();
									if(name != null){
										name = name.replaceAll("'", "");
									}else{
										name = "";
									}
									name = CommonUtil.removeIllegalStr("GB2312", name);
									
									sbuffer = new StringBuffer();
									sbuffer.append(hendsql);
									sbuffer.append("'").append(host.getId()).append("',");
									sbuffer.append("'").append(host.getIpAddress()).append("',");
									sbuffer.append("'").append(host.getType()).append("',");
									sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
									sbuffer.append("'").append(name).append("',");
									sbuffer.append("'").append(vo.getType()).append("',");
									sbuffer.append("'").append(vo.getSwid()).append("',");
									//sbuffer.append("'").append(time).append("'");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("'").append(time).append("'");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
									}
									sbuffer.append(endsql);
									list.add(sbuffer.toString());						    
								
								}
							}
							
							GathersqlListManager.AdddateTempsql(deleteSql, list);
						}
						
					}

					
					// 处理服务信息入库
					if(allservicehash != null && allservicehash.size()>0){
						
						Enumeration servicehash = allservicehash.keys();
						
						String hendsql="insert into nms_sercice_data_temp(nodeid,ip,type,subtype,name,instate,opstate,paused,uninst,collecttime) values('";
						String endsql=")";
						
						
						while(servicehash.hasMoreElements()){
							String ip = (String)servicehash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector serviceVector = (Vector)allservicehash.get(ip);
							String deleteSql = "delete from nms_sercice_data_temp where nodeid='" +host.getId() + "'";

							
							if(serviceVector != null && serviceVector.size()>0){
								Calendar tempCal=Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								Vector list=new Vector();
								
								
								for(int i=0;i<serviceVector.size();i++){
									Servicecollectdata vo = (Servicecollectdata) serviceVector.elementAt(i);								
									
									try {
									
									sbuffer = new StringBuffer();
									sbuffer.append(hendsql);
									sbuffer.append("'").append(host.getId()).append("',");
									sbuffer.append("'").append(host.getIpAddress()).append("',");
									sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
									sbuffer.append("'").append(new String(vo.getName().getBytes(),"UTF-8")).append("',");
									sbuffer.append("'").append(vo.getInstate()).append("',");
									sbuffer.append("'").append(vo.getOpstate()).append("',");
									sbuffer.append("'").append(vo.getPaused()).append("',");
									sbuffer.append("'").append(vo.getUninst()).append("',");
//									sbuffer.append("'").append(time).append("'");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("'").append(time).append("'");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
									}
									sbuffer.append(endsql);
									list.add(sbuffer.toString());	
									
								}catch(Exception e)
								{
									
								}
								}
								
								GathersqlListManager.AdddateTempsql(deleteSql, list);
							}
						}
						
					}
									
					// 处理IPMAC信息入库
					if(allipmachash != null && allipmachash.size()>0){
						Enumeration ipmachash = allipmachash.keys();
						
						while(ipmachash.hasMoreElements()){
							String ip = (String)ipmachash.nextElement();
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector ipmacVector = (Vector)allipmachash.get(ip);
							String deleteSql = "delete from ipmac where relateipaddr='" + ip + "'";
							Vector list=new Vector();
							
							if(ipmacVector != null && ipmacVector.size()>0){
								for(int i=0;i<ipmacVector.size();i++){
									try{
									IpMac ipmac = (IpMac) ipmacVector.elementAt(i);	
									String mac = ipmac.getMac();
									if(mac == null){
										mac = "";
									}
									mac = CommonUtil.removeIllegalStr(mac);
								    String sqll = "";
									String time = sdf.format(ipmac.getCollecttime().getTime());
									sqll = "insert into ipmac(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms)values('";
									sqll = sqll + ipmac.getRelateipaddr() + "','" + ipmac.getIfindex() + "','" + ipmac.getIpaddress() + "','";
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sqll = sqll + new String(mac.getBytes(),"UTF-8") + "','" + time + "','" + ipmac.getIfband() + "','" + ipmac.getIfsms() + "')";
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sqll = sqll + new String(mac.getBytes(),"UTF-8") + "',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),'" + ipmac.getIfband() + "','" + ipmac.getIfsms() + "')";
									}
									  								
									  
									list.add(sqll);
								}catch(Exception e)
								{}
								}
								
							}
							
							GathersqlListManager.AdddateTempsql(deleteSql, list);
						}

					}
					
					
					
					// 处理系统组信息入库
					if(allsystemgrouphash != null && allsystemgrouphash.size()>0){
						Enumeration systemgrouphash = allsystemgrouphash.keys();
						
						String hendsql="insert into nms_system_data_temp(nodeid,ip,type,subtype,entity,subentity,sindex,thevalue,chname,restype,collecttime,unit,bak)values('";
						String endsql=")";
						
						
						while(systemgrouphash.hasMoreElements()){
							String ip = (String)systemgrouphash.nextElement();
							// SysLogger.info(ip+"==========system");
		    				Host host = (Host)PollingEngine.getInstance().getNodeByIP(ip);						
				 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(host);
							Vector systemgroupVector = (Vector)allsystemgrouphash.get(ip);
							String deleteSql = "delete from nms_system_data_temp where nodeid='" +host.getId() + "'";
							// 判断是否已经被删除过，若没被删除过，则添加到已被删除容器内且执行删除语句
							// dbmanager.addBatch(deleteSql);
							
							Vector list=new Vector();
							if(systemgroupVector != null && systemgroupVector.size()>0){
								Calendar tempCal=Calendar.getInstance();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								for(int i=0;i<systemgroupVector.size();i++){
									Systemcollectdata vo = (Systemcollectdata) systemgroupVector.elementAt(i);								
									
									sbuffer = new StringBuffer();
									sbuffer.append(hendsql);
									sbuffer.append("'").append(host.getId()).append("',");
									sbuffer.append("'").append(host.getIpAddress()).append("',");
									sbuffer.append("'").append(nodeDTO.getType()).append("',");
									sbuffer.append("'").append(nodeDTO.getSubtype()).append("',");
									sbuffer.append("'").append(vo.getCategory()).append("',");
									sbuffer.append("'").append(vo.getEntity()).append("',");
									sbuffer.append("'").append(vo.getSubentity()).append("',");
									sbuffer.append("'").append(vo.getThevalue()).append("',");
									sbuffer.append("'").append(vo.getChname()).append("',");
									sbuffer.append("'").append(vo.getRestype()).append("',");
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("'").append(time).append("',");
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										sbuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'),");
									}
									
									sbuffer.append("'").append(vo.getUnit()).append("',");
									sbuffer.append("'").append(vo.getBak()).append("'");
									sbuffer.append(endsql);
									list.add(sbuffer.toString());
								}
							}
							
							GathersqlListManager.AdddateTempsql(deleteSql, list);
						}
					}// 结束
					
										
	
        }
      }
     }
  }
		

	
					
					
					
		

