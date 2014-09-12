package com.afunms.common.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.Flashcollectdata;
import com.afunms.polling.om.InPacks;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.OutPacks;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.polling.snmp.SnmpMibConstants;

public class ParserXmlUtil {
	/**@author wxy add
	 * 解析xml 四层
	 * @param fileName
	 */
	public Hashtable parserXml(String fileName) {
		Hashtable alldata=new Hashtable();
		  File inputXml=new File(fileName);   
		  SAXReader saxReader = new SAXReader(); 
		  Vector<Systemcollectdata> itemVec=null;
		  Systemcollectdata systemdata=null;
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  try { 
			  Document document = saxReader.read(inputXml);   
			  Element roots=document.getRootElement(); //root 节点 
			  for(Iterator i = roots.elementIterator(); i.hasNext();){ 
				  Element interfaces = (Element) i.next(); //interface节点
				  itemVec=new Vector(); 
				  for(Iterator j = interfaces.elementIterator(); j.hasNext();){
					  Element items=(Element) j.next(); //items节点
					  
					  systemdata=new Systemcollectdata();
					  String ip="";
					  for(Iterator k = items.elementIterator(); k.hasNext();){
						  Element node=(Element) k.next();
						 
						  if(node.getName().equals("ip")){
							 systemdata.setIpaddress(node.getText());
							 ip=node.getText();
							 if(alldata.containsKey(ip)){
								 itemVec=(Vector)alldata.get(ip);
							 }else {
								 itemVec=new Vector();
							}
						  }
//						  if(node.getName().equals("type"))
//								 systemdata.setRestype(node.getText());
						  
//						  if(node.getName().equals("subtype"))
//							  systemdata.set(node.getText());
						  
						  if(node.getName().equals("category"))
							  systemdata.setCategory(node.getText());
						  
						  if(node.getName().equals("entity"))
							 systemdata.setEntity(node.getText());
						  
						  if(node.getName().equals("subentity"))
							  systemdata.setSubentity(node.getText());
						  
						  if(node.getName().equals("thevalue"))
							  systemdata.setThevalue(node.getText());
						  
						  if(node.getName().equals("chname"))
							  systemdata.setChname(node.getText());
						  
						  if(node.getName().equals("restype"))
							  systemdata.setRestype(node.getText());
						  
						  if (node.getName().equals("time")) {
							  String time=node.getText();
							  Date date;
							  Calendar calendar = Calendar.getInstance();
							try {
								date = sdf.parse(time);
								 
							    calendar.setTime(date); 
							} catch (ParseException e) {
								e.printStackTrace();
							}
								
							systemdata.setCollecttime(calendar);
						     }
						  if (node.getName().equals("unit")) 
							  systemdata.setUnit(node.getText());
						  
						  if (node.getName().equals("bak")) 
							  systemdata.setBak(node.getText());
						  
						 
						  } 
					  itemVec.add(systemdata);
					  alldata.put(ip, itemVec);
					 
					  } 
				  }  
			  } catch (DocumentException e) { 
				 SysLogger.error(fileName+"未找到！") ;
				  } 
			  return alldata;
			  }  
	/**
	 * 解析接口Xml
	 * @param filePath
	 * @param ip
	 * @return
	 */
	public Hashtable parserInterfaceXml( String filePath,String ip){

		Vector interfaceVector=new Vector();
		Vector utilhdxVector = new Vector();
		Vector allutilhdxVector = new Vector();
		Vector packsVector = new Vector();
		Vector<InPacks> inpacksVector = new Vector<InPacks>();
		Vector<OutPacks> outpacksVector = new Vector<OutPacks>();
		Vector<InPkts> inpksVector = new Vector<InPkts>();
		Vector<OutPkts> outpksVector = new Vector<OutPkts>();
		Vector discardspercVector = new Vector();
		Vector errorspercVector = new Vector();
		Vector allerrorspercVector = new Vector();
		Vector alldiscardspercVector = new Vector();
		Vector allutilhdxpercVector=new Vector();
		Vector<UtilHdxPerc> utilhdxpercVector = new Vector<UtilHdxPerc>();
		Hashtable returnHash=new Hashtable();
		
		Vector<String> descVec=new Vector<String>();
		final String[] desc=SnmpMibConstants.NetWorkMibInterfaceDesc0;
		for (int i = 0; i < desc.length; i++) {
			descVec.add(desc[i]);
		}
		  File inputXml=new File(filePath);   
		  SAXReader saxReader = new SAXReader(); 
		  Vector itemVec=null;
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  try { 
			  Document document = saxReader.read(inputXml);   
			  Element roots=document.getRootElement(); //root 节点 
			  for(Iterator i = roots.elementIterator(); i.hasNext();){ 
				  Element interfaces = (Element) i.next(); //interface节点
				  for(Iterator j = interfaces.elementIterator(); j.hasNext();){
					  Element items=(Element) j.next(); //items节点
					
					 
					  String category="";
					  String entity="";
					  String subentity="";
					  String thevalue="";
					  String chname="";
					  String restype="";
					  Calendar cal=null;
					  String unit="";
					  String bak="";
					  for(Iterator k = items.elementIterator(); k.hasNext();){
						  Element node=(Element) k.next();
						  
						  if(node.getName().equals("category"))
							  category=node.getText();
						  
						  if(node.getName().equals("entity"))
							 entity=node.getText();
						  
						  if(node.getName().equals("subentity"))
							  subentity=node.getText();
						  
						  if(node.getName().equals("thevalue"))
							 thevalue=node.getText();
						  
						  if(node.getName().equals("chname"))
							 chname=node.getText();
						  
						  if(node.getName().equals("restype"))
							  restype=node.getText();
						  
						  if (node.getName().equals("time")) {
							  String tempTime=node.getText();
							  Date date;
							  Calendar calendar = Calendar.getInstance(); 
							try {
								date = sdf.parse(tempTime);
							    calendar.setTime(date); 
							} catch (ParseException e) {
								e.printStackTrace();
							}
								
							cal=calendar;
						     }
						  if (node.getName().equals("unit")) 
							unit=node.getText();
						  
						  if (node.getName().equals("bak")) 
							  bak=node.getText();
						  
						
						  } 
					  if (entity.equals("InBandwidthUtilHdxPerc")||entity.equals("OutBandwidthUtilHdxPerc")) {
						  UtilHdxPerc utilHdxPerc=new UtilHdxPerc();
						  utilHdxPerc.setIpaddress(ip);
						  utilHdxPerc.setCategory(category);
						  utilHdxPerc.setEntity(entity);
						  utilHdxPerc.setSubentity(subentity);
						  utilHdxPerc.setThevalue(thevalue);
						  utilHdxPerc.setCollecttime(cal);
						  utilHdxPerc.setRestype(restype);
						  utilHdxPerc.setChname(chname);
						  utilHdxPerc.setUnit(unit);
						  utilHdxPerc.setBak(bak);
//						  if (returnHash.containsKey(entity)) {
//							  itemVec=(Vector)returnHash.get(entity);
//								if (itemVec!=null) {
//									itemVec.add(utilHdxPerc);
//								}else {
//									itemVec=new Vector();
//									itemVec.add(utilHdxPerc);
//								}
//							}
						  utilhdxpercVector.add(utilHdxPerc);
					     }else if (descVec.contains(entity)||entity.equals("AllInCastPkts")||entity.equals("AllOutCastPkts")||entity.equals("AllInDiscards")||entity.equals("AllOutDiscards")) {
					    	 Interfacecollectdata interfacedata=new Interfacecollectdata();
					    	 interfacedata.setIpaddress(ip);
					    	 interfacedata.setCategory(category);
					    	 interfacedata.setEntity(entity);
					    	 interfacedata.setSubentity(subentity);
					    	 interfacedata.setThevalue(thevalue);
					    	 interfacedata.setCollecttime(cal);
					    	 interfacedata.setRestype(restype);
					    	 interfacedata.setChname(chname);
					    	 interfacedata.setUnit(unit);
					    	 interfacedata.setBak(bak);
					    	 interfaceVector.add(interfacedata);
						}else if (entity.equals("InCastPkts")||entity.equals("InCastPkts")) {
							Packs packs=new Packs();
							packs.setIpaddress(ip);
							packs.setCategory(category);
							packs.setEntity(entity);
							packs.setSubentity(subentity);
							packs.setThevalue(thevalue);
							packs.setCollecttime(cal);
							packs.setRestype(restype);
							packs.setChname(chname);
							packs.setUnit(unit);
							packs.setBak(bak);
							packsVector.add(packs);
						}else if (entity.equals("InDiscardsPerc")||entity.equals("OutDiscardsPerc")) {
							DiscardsPerc discardsPerc=new DiscardsPerc();
							discardsPerc.setIpaddress(ip);
							discardsPerc.setCategory(category);
							discardsPerc.setEntity(entity);
							discardsPerc.setSubentity(subentity);
							discardsPerc.setThevalue(thevalue);
							discardsPerc.setCollecttime(cal);
							discardsPerc.setRestype(restype);
							discardsPerc.setChname(chname);
							discardsPerc.setUnit(unit);
							discardsPerc.setBak(bak);
							discardspercVector.add(discardsPerc);
						}else if (entity.equals("InErrorsPerc")||entity.equals("OutErrorsPerc")) {
							ErrorsPerc errorsPerc=new ErrorsPerc();
							errorsPerc.setIpaddress(ip);
							errorsPerc.setCategory(category);
							errorsPerc.setEntity(entity);
							errorsPerc.setSubentity(subentity);
							errorsPerc.setThevalue(thevalue);
							errorsPerc.setCollecttime(cal);
							errorsPerc.setRestype(restype);
							errorsPerc.setChname(chname);
							errorsPerc.setUnit(unit);
							errorsPerc.setBak(bak);
							errorspercVector.add(errorsPerc);
						}else if (entity.equals("InBandwidthUtilHdx")||entity.equals("OutBandwidthUtilHdx")) {
							UtilHdx utilHdx=new UtilHdx();
							utilHdx.setIpaddress(ip);
							utilHdx.setCategory(category);
							utilHdx.setEntity(entity);
							utilHdx.setSubentity(subentity);
							utilHdx.setThevalue(thevalue);
							utilHdx.setCollecttime(cal);
							utilHdx.setRestype(restype);
							utilHdx.setChname(chname);
							utilHdx.setUnit(unit);
							utilHdx.setBak(bak);
							utilhdxVector.add(utilHdx);
						}else if (entity.equals("ifOutMulticastPkts")||entity.equals("ifOutBroadcastPkts")) {
							OutPkts outPkts=new OutPkts();
							outPkts.setIpaddress(ip);
							outPkts.setCategory(category);
							outPkts.setEntity(entity);
							outPkts.setSubentity(subentity);
							outPkts.setThevalue(thevalue);
							outPkts.setCollecttime(cal);
							outPkts.setRestype(restype);
							outPkts.setChname(chname);
							outPkts.setUnit(unit);
							outPkts.setBak(bak);
							outpksVector.add(outPkts);
						}else if (entity.equals("ifInMulticastPkts")||entity.equals("ifInBroadcastPkts")) {
							InPkts inPkts=new InPkts();
							inPkts.setIpaddress(ip);
							inPkts.setCategory(category);
							inPkts.setEntity(entity);
							inPkts.setSubentity(subentity);
							inPkts.setThevalue(thevalue);
							inPkts.setCollecttime(cal);
							inPkts.setRestype(restype);
							inPkts.setChname(chname);
							inPkts.setUnit(unit);
							inPkts.setBak(bak);
							inpksVector.add(inPkts);
						}else if (entity.equals("AllInBandwidthUtilHdx")||entity.equals("AllOutBandwidthUtilHdx")) {
							AllUtilHdx allUtilHdx=new AllUtilHdx();
							allUtilHdx.setIpaddress(ip);
							allUtilHdx.setCategory(category);
							allUtilHdx.setEntity(entity);
							allUtilHdx.setSubentity(subentity);
							allUtilHdx.setThevalue(thevalue);
							allUtilHdx.setCollecttime(cal);
							allUtilHdx.setRestype(restype);
							allUtilHdx.setChname(chname);
							allUtilHdx.setUnit(unit);
							allUtilHdx.setBak(bak);
							allutilhdxVector.add(allUtilHdx);
							
						}else if (entity.equals("ifInMulticastPkts")||entity.equals("ifInBroadcastPkts")) {
							InPkts inPkts=new InPkts();
							inPkts.setIpaddress(ip);
							inPkts.setCategory(category);
							inPkts.setEntity(entity);
							inPkts.setSubentity(subentity);
							inPkts.setThevalue(thevalue);
							inPkts.setCollecttime(cal);
							inPkts.setRestype(restype);
							inPkts.setChname(chname);
							inPkts.setUnit(unit);
							inPkts.setBak(bak);
							inpksVector.add(inPkts);
						}else if (entity.equals("ifOutMulticastPkts")||entity.equals("ifOutBroadcastPkts")) {
							OutPkts outPkts=new OutPkts();
							outPkts.setIpaddress(ip);
							outPkts.setCategory(category);
							outPkts.setEntity(entity);
							outPkts.setSubentity(subentity);
							outPkts.setThevalue(thevalue);
							outPkts.setCollecttime(cal);
							outPkts.setRestype(restype);
							outPkts.setChname(chname);
							outPkts.setUnit(unit);
							outPkts.setBak(bak);
							outpksVector.add(outPkts);
						}
					 
					  } 
				  }  
			    } catch (DocumentException e) { 
				 e.printStackTrace();
				  } 
			    Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ip);
				if(ipAllData == null)ipAllData = new Hashtable();
				if (interfaceVector != null && interfaceVector.size()>0)ipAllData.put("interface",interfaceVector);		
				if (allutilhdxpercVector != null && allutilhdxpercVector.size()>0)ipAllData.put("allutilhdxperc",allutilhdxpercVector);
				if (allutilhdxVector != null && allutilhdxVector.size()>0)ipAllData.put("allutilhdx",allutilhdxVector);
				if (utilhdxpercVector != null && utilhdxpercVector.size()>0)ipAllData.put("utilhdxperc",utilhdxpercVector);
				if (utilhdxVector != null && utilhdxVector.size()>0)ipAllData.put("utilhdx",utilhdxVector);		
				if (discardspercVector != null && discardspercVector.size()>0)ipAllData.put("discardsperc",discardspercVector);
				if (errorspercVector != null && errorspercVector.size()>0)ipAllData.put("errorsperc",errorspercVector);
				if (allerrorspercVector != null && allerrorspercVector.size()>0)ipAllData.put("allerrorsperc",allerrorspercVector);
				if (alldiscardspercVector != null && alldiscardspercVector.size()>0)ipAllData.put("alldiscardsperc",alldiscardspercVector);
				if (packsVector != null && packsVector.size()>0)ipAllData.put("packs",packsVector);
				if (inpacksVector != null && inpacksVector.size()>0)ipAllData.put("inpacks",inpacksVector);
				if (outpacksVector != null && outpacksVector.size()>0)ipAllData.put("outpacks",outpacksVector);
				 ShareData.getSharedata().put(ip, ipAllData);
				 
				returnHash.put("interface",interfaceVector);		
			    returnHash.put("allutilhdxperc",allutilhdxpercVector);
			    returnHash.put("allutilhdx",allutilhdxVector);
			    returnHash.put("utilhdxperc",utilhdxpercVector);
			    returnHash.put("utilhdx",utilhdxVector);		
			    returnHash.put("discardsperc",discardspercVector);
			    returnHash.put("errorsperc",errorspercVector);
			    returnHash.put("allerrorsperc",allerrorspercVector);
			    returnHash.put("alldiscardsperc",alldiscardspercVector);
			    returnHash.put("packs",packsVector);
			    returnHash.put("inpacks",inpacksVector);
			    returnHash.put("outpacks",outpacksVector);
				
			    //Hashtable allData=new Hashtable();
			    //allData.put(ip, returnHash);
			  //  System.out.println("==========================="+returnHash.toString());
			  return returnHash;
	}
	/**
	 * @anothor wxy add
	 * 解析网络设备CPU、内存、电源、风扇、闪存、温度、电压、连通性,（四层节点格式的XML）
	 * @param filePath
	 * @param ip
	 * @param type 八种类型（cpu,memory,PhysicalMemory,VirtualMemory,power,fan,flash,temperature,voltage,ping）
	 * @return
	 */
	public Hashtable parserNetWorkXml( String filePath,String type){
		
		
		Hashtable<String,Vector> returnHash=new Hashtable<String,Vector>();
		
		File inputXml=new File(filePath);   
		SAXReader saxReader = new SAXReader();
		Vector<CPUcollectdata> cpuVector=null;
		Vector<Memorycollectdata> memoryVector=null;
		Vector<Memorycollectdata> phyMemVec=null;
		Vector<Memorycollectdata> virMemVec=null;
		Vector<Diskcollectdata> diskVec=null;
		Vector<Interfacecollectdata> powerVector=null;
		Vector<Interfacecollectdata> fanVector=null;
		Vector<Flashcollectdata> flashVector=null;
		Vector<Pingcollectdata> pingVector=null;
		Vector<Interfacecollectdata> tempperatureVec=null;
		Vector<Interfacecollectdata> voltageVec=null;
		Vector tempVec=null;
		if (type.equals("cpu")) {
			 cpuVector=new Vector<CPUcollectdata>();
		}else if (type.equals("memory")) {
			
			 memoryVector=new Vector<Memorycollectdata>();
			 
		}else if (type.equals("PhysicalMemory")) {
			
			phyMemVec=new Vector<Memorycollectdata>();
			 
		}else if (type.equals("VirtualMemory")) {
			
			virMemVec=new Vector<Memorycollectdata>();
			 
		}else if (type.equals("disk")) {
			
			diskVec=new Vector<Diskcollectdata>();
			 	
		}else if (type.equals("power")) {
			
			powerVector=new Vector<Interfacecollectdata>();
			 	
		}else if (type.equals("fan")) {
			
			fanVector=new Vector<Interfacecollectdata>();
			 	
		}else if (type.equals("flash")) {
			flashVector=new Vector<Flashcollectdata>();
			 	
		}else if (type.equals("temperature")) {
			
			tempperatureVec=new Vector<Interfacecollectdata>();
			
		}else if (type.equals("voltage")) {
			
			voltageVec=new Vector<Interfacecollectdata>();
			
		}else if (type.equals("ping")) {
			
			pingVector=new Vector<Pingcollectdata>();
		}
		String ip="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try { 
			Document document = saxReader.read(inputXml);   
			Element roots=document.getRootElement();               //root 节点 
			for(Iterator i = roots.elementIterator(); i.hasNext();){ 
				Element interfaces = (Element) i.next();           //interface节点
				for(Iterator j = interfaces.elementIterator(); j.hasNext();){
					Element items=(Element) j.next();              //items节点
					
					String category="";
					String entity="";
					String subentity="";
					String thevalue="";
					String chname="";
					String restype="";
					Calendar cal=null;
					String unit="";
					String bak="";
					
					for(Iterator k = items.elementIterator(); k.hasNext();){
						Element node=(Element) k.next();
						 if(node.getName().equals("ip"))
							 ip=node.getText();
							
						if(node.getName().equals("category"))
							category=node.getText();
						
						if(node.getName().equals("entity"))
							entity=node.getText();
						
						if(node.getName().equals("subentity"))
							subentity=node.getText();
						
						if(node.getName().equals("thevalue"))
							thevalue=node.getText();
						
						if(node.getName().equals("chname"))
							chname=node.getText();
						
						if(node.getName().equals("restype"))
							restype=node.getText();
						
						if (node.getName().equals("time")) {
							String tempTime=node.getText();
							Date date;
							Calendar calendar = Calendar.getInstance(); 
							try {
								date = sdf.parse(tempTime);
								calendar.setTime(date); 
							} catch (ParseException e) {
								e.printStackTrace();
							}
							cal=calendar;
						}
						
						if (node.getName().equals("unit")){ 
							unit=node.getText();
						}
						
						if (node.getName().equals("bak")){ 
							bak=node.getText();
						}
			
					} 
					if (category.equals("CPU")) {
						CPUcollectdata cpu=new CPUcollectdata();
						cpu.setIpaddress(ip);
						cpu.setCategory(category);
						cpu.setEntity(entity);
						cpu.setSubentity(subentity);
						cpu.setThevalue(thevalue);
						cpu.setCollecttime(cal);
						cpu.setRestype(restype);
						cpu.setChname(chname);
						cpu.setUnit(unit);
						cpu.setBak(bak);
						if(returnHash.containsKey(ip)){
							 cpuVector=(Vector)returnHash.get(ip);
						 }else {
							 cpuVector=new Vector();
						}
						cpuVector.add(cpu);
						returnHash.put(ip, cpuVector);
					}else if (type.equals("memory")&&category.equals("Memory")) {
						Memorycollectdata memory=new Memorycollectdata();
						memory.setIpaddress(ip);
						memory.setCategory(category);
						memory.setEntity(entity);
						memory.setSubentity(subentity);
						memory.setThevalue(thevalue);
						memory.setCollecttime(cal);
						memory.setRestype(restype);
						memory.setChname(chname);
						memory.setUnit(unit);
						memory.setBak(bak);
						
						if(returnHash.containsKey(ip)){
							memoryVector=(Vector)returnHash.get(ip);
						 }else {
							 memoryVector=new Vector();
						}
						memoryVector.add(memory);
						returnHash.put(ip, memoryVector);
						
					}else if (type.equals("PhysicalMemory")&&subentity.equals("PhysicalMemory")&&category.equals("Memory")) {//为了与之前的兼容
						Memorycollectdata memory=new Memorycollectdata();
						memory.setIpaddress(ip);
						memory.setCategory(category);
						memory.setEntity(entity);
						memory.setSubentity(subentity);
						memory.setThevalue(thevalue);
						memory.setCollecttime(cal);
						memory.setRestype(restype);
						memory.setChname(chname);
						memory.setUnit(unit);
						memory.setBak(bak);
						
						if(returnHash.containsKey(ip)){
							phyMemVec=(Vector)returnHash.get(ip);
						 }else {
							 phyMemVec=new Vector();
						}
						phyMemVec.add(memory);
						returnHash.put(ip, phyMemVec);
						
					}else if (type.equals("VirtualMemory")&&subentity.equals("VirtualMemory")&&category.equals("Memory")) {//为了与之前的兼容
						Memorycollectdata memory=new Memorycollectdata();
						memory.setIpaddress(ip);
						memory.setCategory(category);
						memory.setEntity(entity);
						memory.setSubentity(subentity);
						memory.setThevalue(thevalue);
						memory.setCollecttime(cal);
						memory.setRestype(restype);
						memory.setChname(chname);
						memory.setUnit(unit);
						memory.setBak(bak);
						
						if(returnHash.containsKey(ip)){
							virMemVec=(Vector)returnHash.get(ip);
						 }else {
							 virMemVec=new Vector();
						}
						virMemVec.add(memory);
						returnHash.put(ip, virMemVec);
						
					}else if (type.equals("disk")&&category.equals("Disk")) {//为了与之前的兼容
						Diskcollectdata disk=new Diskcollectdata();
						disk.setIpaddress(ip);
						disk.setCategory(category);
						disk.setEntity(entity);
						disk.setSubentity(subentity);
						disk.setThevalue(thevalue);
						disk.setCollecttime(cal);
						disk.setRestype(restype);
						disk.setChname(chname);
						disk.setUnit(unit);
						disk.setBak(bak);
						
						if(returnHash.containsKey(ip)){
							diskVec=(Vector)returnHash.get(ip);
						 }else {
							 diskVec=new Vector();
						}
						diskVec.add(disk);
						returnHash.put(ip, diskVec);
						
					}else if (type.equals("power")&&category.equals("Power")) {
						Interfacecollectdata envir=new Interfacecollectdata();
						envir.setIpaddress(ip);
						envir.setCategory(category);
						envir.setEntity(entity);
						envir.setSubentity(subentity);
						envir.setThevalue(thevalue);
						envir.setCollecttime(cal);
						envir.setRestype(restype);
						envir.setChname(chname);
						envir.setUnit(unit);
						envir.setBak(bak);
						if(returnHash.containsKey(ip)){
							powerVector=(Vector)returnHash.get(ip);
						 }else {
							 powerVector=new Vector();
						}
						powerVector.add(envir);
						returnHash.put(ip, powerVector);
					}else if (type.equals("fan")&&category.equals("Fan")) {
						Interfacecollectdata envir=new Interfacecollectdata();
						envir.setIpaddress(ip);
						envir.setCategory(category);
						envir.setEntity(entity);
						envir.setSubentity(subentity);
						envir.setThevalue(thevalue);
						envir.setCollecttime(cal);
						envir.setRestype(restype);
						envir.setChname(chname);
						envir.setUnit(unit);
						envir.setBak(bak);
						
						if(returnHash.containsKey(ip)){
							fanVector=(Vector)returnHash.get(ip);
						 }else {
							 fanVector=new Vector();
						}
						fanVector.add(envir);
						returnHash.put(ip, fanVector);
						
					}else if (type.equals("flash")&&category.equals("Flash")) {
						Flashcollectdata envir=new Flashcollectdata();
						envir.setIpaddress(ip);
						envir.setCategory(category);
						envir.setEntity(entity);
						envir.setSubentity(subentity);
						envir.setThevalue(thevalue);
						envir.setCollecttime(cal);
						envir.setRestype(restype);
						envir.setChname(chname);
						envir.setUnit(unit);
						envir.setBak(bak);
						if(returnHash.containsKey(ip)){
							flashVector=(Vector)returnHash.get(ip);
						 }else {
							 flashVector=new Vector();
						}
						flashVector.add(envir);
						returnHash.put(ip, flashVector);
					}else if (type.equals("temperature")&&category.equals("Temperature")) {
						Interfacecollectdata envir=new Interfacecollectdata();
						envir.setIpaddress(ip);
						envir.setCategory(category);
						envir.setEntity(entity);
						envir.setSubentity(subentity);
						envir.setThevalue(thevalue);
						envir.setCollecttime(cal);
						envir.setRestype(restype);
						envir.setChname(chname);
						envir.setUnit(unit);
						envir.setBak(bak);
						if(returnHash.containsKey(ip)){
							tempperatureVec=(Vector)returnHash.get(ip);
						 }else {
							 tempperatureVec=new Vector();
						}
						tempperatureVec.add(envir);
						returnHash.put(ip, tempperatureVec);
					}else if (type.equals("voltage")&&category.equals("Voltage")) {
						Interfacecollectdata envir=new Interfacecollectdata();
						envir.setIpaddress(ip);
						envir.setCategory(category);
						envir.setEntity(entity);
						envir.setSubentity(subentity);
						envir.setThevalue(thevalue);
						envir.setCollecttime(cal);
						envir.setRestype(restype);
						envir.setChname(chname);
						envir.setUnit(unit);
						envir.setBak(bak);
						if(returnHash.containsKey(ip)){
							voltageVec=(Vector)returnHash.get(ip);
						 }else {
							 voltageVec=new Vector();
						}
						voltageVec.add(envir);
						returnHash.put(ip, voltageVec);
					}else if (category.equals("Ping")) {
						Pingcollectdata ping=new Pingcollectdata();
						ping.setIpaddress(ip);
						ping.setCategory(category);
						ping.setEntity(entity);
						ping.setSubentity(subentity);
						ping.setThevalue(thevalue);
						ping.setCollecttime(cal);
						ping.setRestype(restype);
						ping.setChname(chname);
						ping.setUnit(unit);
						ping.setBak(bak);
						if(returnHash.containsKey(ip)){
							pingVector=(Vector)returnHash.get(ip);
						 }else {
							 pingVector=new Vector();
						}
						if(entity.equals("Utilization")){
						pingVector.add(0,ping);
						}else if(entity.equals("ResponseTime")){
						pingVector.add(1,ping);
						}
						
						returnHash.put(ip, pingVector);
					}
					
				} 
				
			}  
		} catch (DocumentException e) { 
			SysLogger.error(filePath); 
		} 
		
	
		return returnHash;
	}
	/**
	 * @anothor wxy add
	 * 解析路由的XMl（四层节点格式的XML）
	 * @param filePath
	 * @param ip
	 * @param type 种类型
	 * @return
	 */
	public Hashtable parserIpRouterXml( String filePath){
		
		
		Hashtable<String,Vector> returnHash=new Hashtable<String,Vector>();
		
		File inputXml=new File(filePath);   
		SAXReader saxReader = new SAXReader();
		Vector<IpRouter> routerVector=null;
		
			routerVector=new Vector<IpRouter>();
		String ip="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try { 
			Document document = saxReader.read(inputXml);   
			Element roots=document.getRootElement();               //root 节点 
			for(Iterator i = roots.elementIterator(); i.hasNext();){ 
				Element interfaces = (Element) i.next();           //interface节点
				for(Iterator j = interfaces.elementIterator(); j.hasNext();){
					Element items=(Element) j.next();              //items节点
					
					String type="";
					String subtype="";
					String ifIndex="";
					String nexthop="";
					String proto="";
					String iproutertype="";
					String mask="";
					Calendar cal=null;
					String physaddress="";
					String dest="";
					
					for(Iterator k = items.elementIterator(); k.hasNext();){
						Element node=(Element) k.next();
						if(node.getName().equals("ip"))
							ip=node.getText();
						
						if(node.getName().equals("type"))
							type=node.getText();
						
//						if(node.getName().equals("subtype"))
//							subtype=node.getText();
						
						if(node.getName().equals("ifIndex"))
							ifIndex=node.getText();
						
						if(node.getName().equals("nexthop"))
							nexthop=node.getText();
						
						if(node.getName().equals("proto"))
							proto=node.getText();
						
						if(node.getName().equals("iproutertype"))
							iproutertype=node.getText();
						
						if(node.getName().equals("mask"))
							mask=node.getText();
						
						if (node.getName().equals("time")) {
							String tempTime=node.getText();
							Date date;
							Calendar calendar = Calendar.getInstance(); 
							try {
								date = sdf.parse(tempTime);
								calendar.setTime(date); 
							} catch (ParseException e) {
								e.printStackTrace();
							}
							cal=calendar;
						}
						
						if (node.getName().equals("physaddress")){ 
							physaddress=node.getText();
						}
						
						if (node.getName().equals("dest")){ 
							dest=node.getText();
						}
						
					} 
						IpRouter ipRouter=new IpRouter();
						ipRouter.setIfindex(ifIndex);
						ipRouter.setNexthop(nexthop);
						ipRouter.setProto(Long.parseLong(proto));
						ipRouter.setMask(mask);
						ipRouter.setType(Long.parseLong(iproutertype));
						ipRouter.setCollecttime(cal);
						ipRouter.setPhysaddress(physaddress);
						ipRouter.setDest(dest);
						
						routerVector.add(ipRouter);
					
				} 
				
			}  
		} catch (DocumentException e) { 
			SysLogger.error(filePath);
		} 
	returnHash.put("iprouter", routerVector);
		
		return returnHash;
	}
	/**
	 * @anothor wxy add
	 * 解析FDB的XMl（四层节点格式的XML）
	 * @param filePath
	 * @return
	 */
	public Hashtable parserFdbXml( String filePath){
		
		
		Hashtable<String,Vector> returnHash=new Hashtable<String,Vector>();
		
		File inputXml=new File(filePath);   
		SAXReader saxReader = new SAXReader();
		Vector<IpMac> ipmacVector=null;
		
		ipmacVector=new Vector<IpMac>();
		String ip="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try { 
			Document document = saxReader.read(inputXml);   
			Element roots=document.getRootElement();               //root 节点 
			for(Iterator i = roots.elementIterator(); i.hasNext();){ 
				Element interfaces = (Element) i.next();           //interface节点
				for(Iterator j = interfaces.elementIterator(); j.hasNext();){
					Element items=(Element) j.next();              //items节点
					
					String type="";
					String subtype="";
					String relateipaddr="";
					String ifindex="";
					String mac="";
					String time="";
					String ifband="";
					Calendar cal=null;
					String ifsms="";
					String bak="";
					
					for(Iterator k = items.elementIterator(); k.hasNext();){
						Element node=(Element) k.next();
						if(node.getName().equals("ip"))
							ip=node.getText();
						
						if(node.getName().equals("type"))
							type=node.getText();
						
//						if(node.getName().equals("subtype"))
//							subtype=node.getText();
						
						if(node.getName().equals("relateipaddr"))
							relateipaddr=node.getText();
						
						if(node.getName().equals("ifindex"))
							ifindex=node.getText();
						
						if(node.getName().equals("mac"))
							mac=node.getText();
						
						
						
						if(node.getName().equals("ifband"))
							ifband=node.getText();
						
						if (node.getName().equals("time")) {
							String tempTime=node.getText();
							Date date;
							Calendar calendar = Calendar.getInstance(); 
							try {
								date = sdf.parse(tempTime);
								calendar.setTime(date); 
							} catch (ParseException e) {
								e.printStackTrace();
							}
							cal=calendar;
						}
						
						if (node.getName().equals("ifsms")){ 
							ifsms=node.getText();
						}
						
						if (node.getName().equals("bak")){ 
							bak=node.getText();
						}
						
					} 
					IpMac ipMac=new IpMac();
					ipMac.setIpaddress(ip);
					ipMac.setIfband(ifband);
					ipMac.setMac(mac);
					ipMac.setIfindex(ifindex);
					ipMac.setCollecttime(cal);
					ipMac.setIfsms(ifsms);
					ipMac.setBak(bak);
					if(returnHash.containsKey(ip)){
						ipmacVector=(Vector)returnHash.get(ip);
					 }else {
						 ipmacVector=new Vector();
					}
					
					ipmacVector.add(ipMac);
					returnHash.put(ip, ipmacVector);
				} 
				
			}  
		} catch (DocumentException e) { 
			SysLogger.error(filePath);
		} 
		
		
		return returnHash;
	}
	/**
	 * @anothor wxy add
	 * 解析arp的XMl（四层节点格式的XML）
	 * @param filePath
	 * @param ip
	 * @param type 种类型
	 * @return
	 */
	public Hashtable parserArpXml( String filePath){
		
		
		Hashtable<String,Vector> returnHash=new Hashtable<String,Vector>();
		
		File inputXml=new File(filePath);   
		SAXReader saxReader = new SAXReader();
		Vector<IpMac> ipmacVector=null;
		
		ipmacVector=new Vector<IpMac>();
		String ip="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try { 
			Document document = saxReader.read(inputXml);   
			Element roots=document.getRootElement();               //root 节点 
			for(Iterator i = roots.elementIterator(); i.hasNext();){ 
				Element interfaces = (Element) i.next();           //interface节点
				for(Iterator j = interfaces.elementIterator(); j.hasNext();){
					Element items=(Element) j.next();              //items节点
					
					String type="";
					String subtype="";
					String relateipaddr="";
					String ifindex="";
					String mac="";
					String time="";
					String ifband="";
					Calendar cal=null;
					String ifsms="";
					String bak="";
					
					for(Iterator k = items.elementIterator(); k.hasNext();){
						Element node=(Element) k.next();
						if(node.getName().equals("ip"))
							ip=node.getText();
						
						if(node.getName().equals("type"))
							type=node.getText();
						
						
						if(node.getName().equals("relateipaddr"))
							relateipaddr=node.getText();
						
						if(node.getName().equals("ifindex"))
							ifindex=node.getText();
						
						if(node.getName().equals("mac"))
							mac=node.getText();
						
						
						
						if(node.getName().equals("ifband"))
							ifband=node.getText();
						
						if (node.getName().equals("time")) {
							String tempTime=node.getText();
							Date date;
							Calendar calendar = Calendar.getInstance(); 
							try {
								date = sdf.parse(tempTime);
								calendar.setTime(date); 
							} catch (ParseException e) {
								e.printStackTrace();
							}
							cal=calendar;
						}
						
						if (node.getName().equals("ifsms")){ 
							ifsms=node.getText();
						}
						
						if (node.getName().equals("bak")){ 
							bak=node.getText();
						}
						
					} 
					IpMac ipMac=new IpMac();
					ipMac.setIpaddress(ip);
					ipMac.setRelateipaddr(relateipaddr);
					ipMac.setIfband(ifband);
					ipMac.setMac(mac);
					ipMac.setIfindex(ifindex);
					ipMac.setCollecttime(cal);
					ipMac.setIfsms(ifsms);
					ipMac.setBak(bak);
					
					ipmacVector.add(ipMac);
					
				} 
				
			}  
		} catch (DocumentException e) { 
			SysLogger.error(filePath+e);
		} 
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ip);
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("ipmac",ipmacVector);
	    ShareData.getSharedata().put(ip, ipAllData);
		returnHash.put("ipmac", ipmacVector);
		return returnHash;
	}
	public static void main(String[] args) {
		ParserXmlUtil util=new ParserXmlUtil();
//		Hashtable aHashtable=util.parserNetWorkXml("D:\\host_mem.xml", "PhysicalMemory");
//		//Hashtable aHashtable2=(Hashtable)aHashtable.get("10.10.1.1");
//		Vector vector=(Vector)aHashtable.get("192.168.241.223");
//		for (int i = 0; i < vector.size(); i++) {
//			Memorycollectdata interfacedata=(Memorycollectdata)vector.get(i);
//			System.out.println(interfacedata.getSubentity()+":"+interfacedata.getThevalue());
//		}
//		Hashtable aHashtable=util.parserInterfaceXml("D:\\a.xml", "10.10.1.2");
//		Vector vector=(Vector)aHashtable.get("interface");
//		for (int i = 0; i < vector.size(); i++) {
//			Interfacecollectdata interfacedata=(Interfacecollectdata)vector.get(i);
//			System.out.println(interfacedata.getSubentity()+":"+interfacedata.getThevalue());
//		}
		
//		Hashtable aHashtable=util.parserXml("D:\\n.xml");
//		Vector vector=(Vector)aHashtable.get("10.10.1.2");
//		for (int i = 0; i < vector.size(); i++) {
//			Systemcollectdata interfacedata=(Systemcollectdata)vector.get(i);
//		System.out.println(interfacedata.getSubentity()+" : "+interfacedata.getThevalue());
//		}
//		Hashtable aHashtable=util.parserNetWorkXml("D:\\c.xml","cpu");
//		Vector vector=(Vector)aHashtable.get("cpu");
//		for (int i = 0; i < vector.size(); i++) {
//			CPUcollectdata interfacedata=(CPUcollectdata)vector.get(i);
//		System.out.println(interfacedata.getSubentity()+" : "+interfacedata.getThevalue());
//		}
		Hashtable aHashtable=util.parserNetWorkXml("D:\\net_fdb.xml","fdb");
		Vector vector=(Vector)aHashtable.get("192.168.250.1");
		for (int i = 0; i < vector.size(); i++) {
			Flashcollectdata interfacedata=(Flashcollectdata)vector.get(i);
			System.out.println(interfacedata.getSubentity()+" : "+interfacedata.getThevalue());
		}
	}
}
