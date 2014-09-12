package com.afunms.polling.snmp;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.initialize.ResourceCenter;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.ArrayInfo;
import com.afunms.polling.node.Battery;
import com.afunms.polling.node.Controller;
import com.afunms.polling.node.CtrlPort;
import com.afunms.polling.node.DIMM;
import com.afunms.polling.node.Disk;
import com.afunms.polling.node.Enclosure;
import com.afunms.polling.node.EnclosureFru;
import com.afunms.polling.node.HP;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Lun;
import com.afunms.polling.node.Port;
import com.afunms.polling.node.Processor;
import com.afunms.polling.node.SubSystemInfo;
import com.afunms.polling.node.SystemInfo;
import com.afunms.polling.node.VFP;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.HPStorageResultTosql;


public class HPParser {
	
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public HPParser()
	{}
	
	public void collectData(Node node,MonitoredItem item){
		   
	}
	   public void collectData(HostNode node){
		   
	 }
	   
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		SysLogger.info("########开始采集HP日志解析信息##########");
		Hashtable returnHash=new Hashtable();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)
			return returnHash;		
		//判断是否在采集时间段内
    	if(ShareData.getTimegatherhash() != null){
    		if(ShareData.getTimegatherhash().containsKey(node.getId()+":equipment")){
    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
    			int _result = 0;
    			_result = timeconfig.isBetween((List)ShareData.getTimegatherhash().get(node.getId()+":equipment"));
    			if(_result ==1 ){
    				//SysLogger.info("########时间段内: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else if(_result == 2){
    				//SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else {
    				SysLogger.info("######## "+node.getIpAddress()+" 不在采集HP日志解析时间段内,退出##########");//    				
    				return returnHash;
    			}    			
    		}
    	}
    	
    	try {    		
    		String fileName = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + node.getIpAddress() + ".hp.log";
    		//System.out.println("dddddddd      " + fileName);
    		File file = new File(fileName);
			if(!file.exists()){				
				try{
					//createFileNotExistSMS(ipaddress);
					SysLogger.info("############# "+node.getIpAddress()+"HP存储采集日志文件不存在退出采集##########");
				}catch(Exception e){
					e.printStackTrace();
				}
				return returnHash;
			}
    		FileReader fr = new FileReader(fileName);
    		BufferedReader br = new BufferedReader(fr);
    		String line = "";
    		StringBuffer sb = new StringBuffer();
    		while (null != (line = br.readLine())) {
    			sb.append(line).append("\r\n");
    		}
    		if (sb.length()>0) {
    			HP hp = parse(sb.toString());
    			//System.out.println(hp);    			
    			//System.out.println("解析完成");
    			//将采集的数据放入内存
    			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get("hpstorage:" + node.getIpAddress());
    			if(ipAllData == null)
    				ipAllData = new Hashtable();
    			ipAllData.put("systeminfo",hp.getSystemInfo());
    			ipAllData.put("arrayinfo",hp.getArrayInfo());
    			ipAllData.put("enclosures",hp.getEnclosures());
    			ipAllData.put("controllers",hp.getControllers());
    			ipAllData.put("ports",hp.getPorts());
    			ipAllData.put("disks",hp.getDisks());
    			ipAllData.put("luns",hp.getLuns());
    			ipAllData.put("vfps",hp.getVfps());
    			ipAllData.put("subSystemInfo",hp.getSubSystemInfo());
    		    ShareData.getSharedata().put("hpstorage:" + node.getIpAddress(), ipAllData);
    		    returnHash.put("hpstorage:" + node.getIpAddress(), hp);
    		    System.out.println("HP storage数据放入内存完成");
    			//将采集到的HP存储信息入库
    			//--------
    			HPStorageResultTosql resulttosql = new HPStorageResultTosql();
    			resulttosql.CreateResultTosql(hp, node.getIpAddress() + ":" + node.getId());
    			System.out.println("#################HP存储 入库完毕#####################");
    			//入库完毕
    			
    			//告警检测  			
    			
    		}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("################HP日志文件解析错误#########################");
			e.printStackTrace();
		}	
    	
		return returnHash;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String fileName = System.getProperty("user.dir")+File.separator+"log\\10.10.152.82.hp.log";
		System.out.println("dddddddd      " + fileName);
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		StringBuffer sb = new StringBuffer();
		while (null != (line = br.readLine())) {
			sb.append(line).append("\r\n");
		}
		if (sb.length()>0) {
			HP hp = parse(sb.toString());
			System.out.println(hp);			
			System.out.println("解析完成");
		}
	}

	private static HP parse(String str){
		HP hp = new HP();
		hp.setSystemInfo(parseSysInfo(str));
		hp.setArrayInfo(parseArrayInfo(str));
		hp.setEnclosures(parseEnclosures(str));
		hp.setControllers(parseController(str));
		hp.setPorts(parsePorts(str));
		hp.setDisks(parseDisks(str));
		hp.setLuns(parseLuns(str));
		hp.setVfps(parseVFP(str));
		hp.setSubSystemInfo(parseSubSysInfo(str));
		return hp;
	}

	private static SystemInfo parseSysInfo(String str){
		String regex = "Vendor ID:_+(\\w+)\\r\\n"
			+ "Product ID:_+(\\w+)\\r\\n"
			+ "Array World Wide Name:_+(\\w+)\\r\\n"
			+ "Array Serial Number:_+(\\w+)\\r\\n"
			+ "Alias:_+(\\w+)\\r\\n"
			+ "Software Revision:_+(.*)\\r\\n"
			+ "Command execution timestamp:_+(.*)\\r\\n";
		SystemInfo systemInfo = new SystemInfo();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		if (m.find()) {
			systemInfo.setVerdorID(m.group(1));
			systemInfo.setProductID(m.group(2));
			systemInfo.setArrayWorldWideName(m.group(3));
			systemInfo.setArraySerialNumber(m.group(4));
			systemInfo.setAlias(m.group(5));
			systemInfo.setSoftwareRevision(m.group(6));
			systemInfo.setCommandexecutioTimestamp(m.group(7));
		}
		return systemInfo;
	}
	
	private static ArrayInfo parseArrayInfo(String str){
		String regex = "ARRAY INFORMATION\\r\\n\\s*\\r\\n"
			+ "\\s*Array Status:_+(\\w+)\\r\\n"
			+ "\\s*Firmware Revision:_+(\\w+)\\r\\n"
			+ "\\s*Product Revision:_+(\\w+)\\r\\n"
			+ "\\s*Local Controller Product Revision:_+(\\w+)\\r\\n"
			+ "\\s*Remote Controller Product Revision:_+(\\w+)\\r\\n"
			+ "((\\s*Last Event Log Entry for Page \\d+:_+(\\w+)\\r\\n)*)";
		ArrayInfo arrayInfo = new ArrayInfo();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		if (m.find()) {
			arrayInfo.setArrayStatus(m.group(1));
			arrayInfo.setFirmwareRevision(m.group(2));
			arrayInfo.setProductRevision(m.group(3));
			arrayInfo.setLocalControllerProductRevision(m.group(4));
			arrayInfo.setRemoteControllerProductRevision(m.group(5));
			arrayInfo.setLastEventLogEntry(null);
			String lastEventLog = m.group(6);
			p = Pattern.compile("\\s*(Last Event Log Entry for Page \\d+):_+(\\w+)\\r\\n");
			m = p.matcher(lastEventLog);
			
			Map<String,String> lastEventLogMap = new HashMap<String,String>();
			while (m.find()) {
				lastEventLogMap.put(m.group(1), m.group(2));
			}
			if (lastEventLogMap.size() > 0) {
				arrayInfo.setLastEventLogEntry(lastEventLogMap);
			}
		}
		return arrayInfo;
	}

	private static List<Enclosure> parseEnclosures(String str){
		String regex = "Enclosure at (\\w+)\\r\\n"
			+ "\\s+Enclosure ID_+(\\w+)\\r\\n"
			+ "\\s+Enclosure Status_+(\\w+)\\r\\n"
			+ "\\s+Enclosure Type_+(.*)\\r\\n"
			+ "\\s+Node WWN_+(\\w+)\\r\\n\\s*\\r\\n"
			+ "\\s+FRU\\s*HW COMPONENT\\s*IDENTIFICATION\\s*ID STATUS\\r\\n"
			+ "\\s+=+\\r\\n"
			+ "((([ \\t\\x0B\\f]+[a-z0-9A-Z/.:<>]+)+\\r\\n)+)\\r\\n";
		
		List<Enclosure> Enclosures = new ArrayList<Enclosure>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			Enclosure enclosure = new Enclosure();
			enclosure.setName(m.group(1));
			enclosure.setEnclosureID(m.group(2));
			enclosure.setEnclosureStatus(m.group(3));
			enclosure.setEnclosureType(m.group(4));
			enclosure.setNodeWWN(m.group(5));
			
			List<EnclosureFru> enclosureFrus = new ArrayList<EnclosureFru>();
			String enclosureFruStr = m.group(6);
			Pattern p2 = Pattern.compile("([ \\t\\x0B\\f]+[a-z0-9A-Z/.:<>]+){4,}\\r\\n");
			Matcher m2 = p2.matcher(enclosureFruStr);
			while (m2.find()) {
				String[] enclosureFruFields = m2.group().trim().split("\\s+"); 
				EnclosureFru fru = new EnclosureFru();
				fru.setFru(enclosureFruFields[0]);
				fru.setHwComponent(enclosureFruFields[1]);
				fru.setIdentification(enclosureFruFields[enclosureFruFields.length-2]);
				fru.setIdStatus(enclosureFruFields[enclosureFruFields.length-1]);
				if (enclosureFruFields.length>4) {
					String temp = "";
					for (int i = 2; i < enclosureFruFields.length-1; i++) {
						temp += enclosureFruFields[i] + " ";
					}
					fru.setHwComponent(temp.trim());
				}
				enclosureFrus.add(fru);
			}
			if (enclosureFrus.size()>0) {
				enclosure.setFrus(enclosureFrus);
			}
			Enclosures.add(enclosure);
		}
		return Enclosures;
	}
	
	private static List<Controller> parseController1(String str){
		long stime = System.currentTimeMillis();
		String regex = "Controller At ([\\w/]+):\\r\\n"
				+ "[ \\t\\x0B\\f]+Status:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Serial Number:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Vendor ID:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Product ID:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Product Revision:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Firmware Revision:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Manufacturing Product Code:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Controller Type:_*(\\w+([ \\t\\x0B\\f]*\\w*)*)\\r\\n"
				+ "[ \\t\\x0B\\f]+Battery Charger Firmware Revision:_*([\\w\\.]+)\\r\\n"//11
				+ "(([ \\t\\x0B\\f]+Front Port At [\\w/\\.]+:\\r\\n"
				+ "(([ \\t\\x0B\\f]+\\w+)+:_*[\\w/\\.,]+([ \\t\\x0B\\f]+[\\w/\\.,]+)*\\r\\n){6,12})+)"//16
				+ "(([ \\t\\x0B\\f]+Back Port At [\\w/\\.]+:\\r\\n"
				+ "(([ \\t\\x0B\\f]+\\w+)+:_*[\\w/\\.,]+([ \\t\\x0B\\f]+[\\w/\\.,]+)*\\r\\n){9})+)"//21结束
				+ "[ \\t\\x0B\\f]+Battery at ([\\w/\\.]+):\\r\\n"
				+ "[ \\t\\x0B\\f]+Status:_*(\\w+)\\r\\n" 
				+ "[ \\t\\x0B\\f]+Identification:_*([\\w:/]+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Manufacturer Name:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Device Name:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Manufacturer Date:_*(.+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Remaining Capacity:_*(.+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Remaining Capacity:_*(.+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Voltage:_*(.+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Discharge Cycles:_*(.+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Processor at ([\\w/\\.]+):\\r\\n"
				+ "[ \\t\\x0B\\f]+Status:_*(\\w+)\\r\\n" 
				+ "[ \\t\\x0B\\f]+Identification:_*([\\w:/]+)\\r\\n"
				+ "(([ \\t\\x0B\\f]+DIMM at [\\w/\\.]+:\\r\\n"
				+ "[ \\t\\x0B\\f]+Status:_*\\w+\\r\\n" 
				+ "[ \\t\\x0B\\f]+Identification:_*[\\w:/]+\\r\\n"
				+ "[ \\t\\x0B\\f]+Capacity:_*.+\\r\\n)*)";
		int endIndex = 0;
		List<Controller> controllers = new ArrayList<Controller>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while(m.find()){
			endIndex = m.end();
			Controller controller = new Controller();
			controller.setName(m.group(1));
			controller.setStatus(m.group(2));
			controller.setSerialNumber(m.group(3));
			controller.setVendorID(m.group(4));
			controller.setProductID(m.group(5));
			controller.setProductRevision(m.group(6));
			controller.setFirmwareRevision(m.group(7));
			controller.setManufacturingProductCode(m.group(8));
			controller.setControllerType(m.group(9));
			controller.setBatteryChargerFirmwareRevision(m.group(11));
			//Front Port
			if (m.group(12).length()>0) {
				String frontPortStr = m.group(12);
				Pattern pattern = Pattern.compile("Front Port At ([\\w/\\.]+):\\r\\n");
				Matcher matcher = pattern.matcher(frontPortStr);
				List<Integer> indexs = new ArrayList<Integer>();
				List<CtrlPort> frontPorts = new ArrayList<CtrlPort>();
				
				while(matcher.find()){
					int start = matcher.start();
					indexs.add(start);
					CtrlPort ctrlPort = new CtrlPort();
					ctrlPort.setName(matcher.group(1));
					frontPorts.add(ctrlPort);
				}
				
				for (int i = 0; i < indexs.size(); i++) {
					CtrlPort ctrlPort = frontPorts.get(i);
					String subStr = "";
					if (i< indexs.size()-1) {
						subStr = frontPortStr.substring(indexs.get(i), indexs.get(i+1));
					}else{
						subStr = frontPortStr.substring(indexs.get(i));
					}
					toParse("Status:_*(\\w+)\\r\\n",subStr,ctrlPort,"status");
					toParse("Port Instance:_*(\\w+)\\r\\n",subStr,ctrlPort,"portInstance");
					toParse("Hard Address:_*(\\w+)\\r\\n",subStr,ctrlPort,"hardAddress");
					toParse("Link State:_*(\\.+)\\r\\n",subStr,ctrlPort,"linkState");
					toParse("Node WWN:_*(\\w+)\\r\\n",subStr,ctrlPort,"nodeWWN");
					toParse("Port WWN:_*(\\w+)\\r\\n",subStr,ctrlPort,"portWWN");
					toParse("Topology:_*(.+)\\r\\n",subStr,ctrlPort,"topology");
					toParse("Data Rate:_*(.+)\\r\\n",subStr,ctrlPort,"dataRate");
					toParse("Port ID:_*(\\w+)\\r\\n",subStr,ctrlPort,"portID");
					toParse("Device Host Name:_*(\\w+)\\r\\n",subStr,ctrlPort,"deviceHostName");
					toParse("Hardware Path:_*(.+)\\r\\n",subStr,ctrlPort,"hardwarePath");
					toParse("Device Path:_*(.+)\\r\\n",subStr,ctrlPort,"devicePath");
					
				}
				if (frontPorts.size()>0) {
					controller.setFrontPortList(frontPorts);
				}
			}
			
			if (m.group(17).length()>0) {
				String backPortStr = m.group(17);
				String regexStr = "Back Port At ([\\w/\\.]+):\\r\\n"
									+"[ \\t\\x0B\\f]+Status:_*(\\w+)\\r\\n"
									+"[ \\t\\x0B\\f]+Port Instance:_*(\\w+)\\r\\n"
									+"[ \\t\\x0B\\f]+Hard Address:_*(\\w+)\\r\\n"
									+"[ \\t\\x0B\\f]+Link State:_*(.+)\\r\\n"
									+"[ \\t\\x0B\\f]+Node WWN:_*(\\w+)\\r\\n"
									+"[ \\t\\x0B\\f]+Port WWN:_*(\\w+)\\r\\n"
									+"[ \\t\\x0B\\f]+Topology:_*(.+)\\r\\n"
									+"[ \\t\\x0B\\f]+Data Rate:_*(.+)\\r\\n"
									+"[ \\t\\x0B\\f]+Port ID:_*(\\w+)\\r\\n";
				Pattern pattern = Pattern.compile(regexStr);
				Matcher matcher = pattern.matcher(backPortStr);
				List<CtrlPort> backPorts = new ArrayList<CtrlPort>();
				while(matcher.find()){
					CtrlPort ctrlPort = new CtrlPort();
					ctrlPort.setName(matcher.group(1));
					ctrlPort.setStatus(matcher.group(2));
					ctrlPort.setPortInstance(matcher.group(3));
					ctrlPort.setHardAddress(matcher.group(4));
					ctrlPort.setLinkState(matcher.group(5));
					ctrlPort.setNodeWWN(matcher.group(6));
					ctrlPort.setPortWWN(matcher.group(7));
					ctrlPort.setTopology(matcher.group(8));
					ctrlPort.setDataRate(matcher.group(9));
					ctrlPort.setPortID(matcher.group(10));
					backPorts.add(ctrlPort);
				}
				if (backPorts.size()>0) {
					controller.setBackPortList(backPorts);
				}
			}
			
			Battery battery = new Battery();
			battery.setName(m.group(22));
			battery.setStatus(m.group(23));
			battery.setIdentification(m.group(24));
			battery.setManufacturerName(m.group(25));
			battery.setDeviceName(m.group(26));
			battery.setManufacturerDate(m.group(27));
			battery.setRemainingCapacity(m.group(28));
			battery.setPctRemainingCapacity(m.group(29));
			battery.setVoltage(m.group(30));
			battery.setDischargeCycles(m.group(31));
			controller.setBattery(battery);
			
			Processor processor = new Processor();
			processor.setName(m.group(32));
			processor.setStatus(m.group(33));
			processor.setIdentification(m.group(34));
			controller.setProcessor(processor);
			
//			System.out.println(m.group(35));
			
			if (m.group(35).length()>0) {
				String dimmsStr = m.group(35);
				String regexStr = "DIMM at ([\\w/\\.]+):\\r\\n"
									+ "[ \\t\\x0B\\f]+Status:_*(\\w+)\\r\\n" 
									+ "[ \\t\\x0B\\f]+Identification:_*([\\w:/]+)\\r\\n"
									+ "[ \\t\\x0B\\f]+Capacity:_*(.+)\\r\\n";
				Pattern pattern = Pattern.compile(regexStr);
				Matcher matcher = pattern.matcher(dimmsStr);
				List<DIMM> dimms = new ArrayList<DIMM>();
				while(matcher.find()){
					DIMM dimm = new DIMM();
					dimm.setName(matcher.group(1));
					dimm.setStatus(matcher.group(2));
					dimm.setIdentification(matcher.group(3));
					dimm.setCapacity(matcher.group(4));
					dimms.add(dimm);
				}
				if (dimms.size()>0) {
					controller.setDimmList(dimms);
				}
			}
			controllers.add(controller);
		}
		
		return controllers;
	}

	private static List<Controller> parseController2(String str){
		List<Controller> controllers = new ArrayList<Controller>();
		String regex = "Controller At ([\\w/]+):\\r\\n"
				+ "[ \\t\\x0B\\f]+Status:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Serial Number:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Vendor ID:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Product ID:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Product Revision:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Controller Type:_*(\\w+([ \\t\\x0B\\f]*\\w*)*)\\r\\n"
				+ "[ \\t\\x0B\\f]+Enclosure Switch Setting:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Drive Address Basis:_*(.+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Enclosure ID:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Loop Pair:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Loop ID:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Hard Address:_*(\\w+)\\r\\n";
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while(m.find()){
			Controller controller = new Controller();
			controller.setName(m.group(1));
			controller.setStatus(m.group(2));
			controller.setSerialNumber(m.group(3));
			controller.setVendorID(m.group(4));
			controller.setProductID(m.group(5));
			controller.setProductRevision(m.group(6));
			controller.setControllerType(m.group(7));
			controller.setEnclosureSwitchSetting(m.group(9));
			controller.setDriveAddressBasis(m.group(10));
			controller.setEnclosureID(m.group(11));
			controller.setLoopPair(m.group(12));
			controller.setLoopID(m.group(13));
			controller.setHardAddress(m.group(14));
			controllers.add(controller);
		}
		return controllers;
	}
	
	private static List<Controller> parseController(String str){
		String regex = "Controller At ([\\w/]+):\\r\\n";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		List<Integer> indexs = new ArrayList<Integer>();
		List<Controller> controllers = new ArrayList<Controller>();
		
		while(m.find()){
			int start = m.start();
			indexs.add(start);
		}
		
		for (int i = 0; i < indexs.size(); i++) {
			String subStr = "";
			if (i< indexs.size()-1) {
				subStr = str.substring(indexs.get(i), indexs.get(i+1));
			}else{
				subStr = str.substring(indexs.get(i));
			}
			
			regex = "[ \\t\\x0B\\f]+Enclosure Switch Setting:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Drive Address Basis:_*(.+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Enclosure ID:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Loop Pair:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Loop ID:_*(\\w+)\\r\\n"
				+ "[ \\t\\x0B\\f]+Hard Address:_*(\\w+)\\r\\n";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(subStr);
			if (matcher.find()) {
				controllers.addAll(parseController2(subStr));
			}else{
				controllers.addAll(parseController1(subStr));
			}
		}
		return controllers;
	}
	
	private static List<Port> parsePorts(String str){
		String regex = "Settings for port ([\\w/.]+):\\r\\n";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		List<Integer> indexs = new ArrayList<Integer>();
		List<Port> portsList = new ArrayList<Port>();
		
		while(m.find()){
			int start = m.start();
			indexs.add(start);
			Port port = new Port();
			port.setName(m.group(1).trim());
			portsList.add(port);
		}

		for (int i = 0; i < indexs.size(); i++) {
			Port port = portsList.get(i);
			String subStr = "";
			if (i< indexs.size()-1) {
				subStr = str.substring(indexs.get(i), indexs.get(i+1));
			}else{
				subStr = str.substring(indexs.get(i));
			}
			
			toParse("Port ID:_+(.*)\\r\\n",subStr,port,"portID");
			toParse("Behavior:_+(.*)\\r\\n",subStr,port,"behavior");
			toParse("Topology:_+(.*)\\r\\n",subStr,port,"topology");
			toParse("Queue Full Threshold:_+(.*)\\r\\n",subStr,port,"queueFullThreshold");
			toParse("Data Rate:_+(.*)\\r\\n",subStr,port,"dataRate");
		}
		return portsList;
	}	

	private static List<Disk> parseDisks(String str){
		String regex = "Disk at ([\\w/.]+):\\r\\n"
			+ "[ \\t\\x0B\\f]+Status:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Disk State:_*(\\w+)\\r\\n" 
			+ "[ \\t\\x0B\\f]+Vendor ID:_*(.*)\\r\\n" 
			+ "[ \\t\\x0B\\f]+Product ID:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Product Revision:_*(\\w+)\\r\\n" 
			+ "[ \\t\\x0B\\f]+Data Capacity:_*(.*)\\r\\n" 
			+ "[ \\t\\x0B\\f]+Block Length:_*(.*)\\r\\n"
			+ "[ \\t\\x0B\\f]+Address:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Node WWN:_*(.+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Initialize State:_*(.+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Redundancy Group:_*(\\w+)\\r\\n" 
			+ "[ \\t\\x0B\\f]+Volume Set Serial Number:_*(\\w+)\\r\\n" 
			+ "[ \\t\\x0B\\f]+Serial Number:_*(\\w+)\\r\\n" 
			+ "[ \\t\\x0B\\f]+Firmware Revision:_*(\\w+)\\r\\n";
		List<Disk> disks = new ArrayList<Disk>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			Disk disk = new Disk();
			disk.setName(m.group(1));
			disk.setStatus(m.group(2));
			disk.setDiskState(m.group(3));
			disk.setVendorID(m.group(4));
			disk.setProductID(m.group(5));
			disk.setProductRevision(m.group(6));
			disk.setDataCapacity(m.group(7));
			disk.setBlockLength(m.group(8));
			disk.setAddress(m.group(9));
			disk.setNodeWWN(m.group(10));
			disk.setInitializeState(m.group(11));
			disk.setRedundancyGroup(m.group(12));
			disk.setVolumeSetSerialNumber(m.group(13));
			disk.setSerialNumber(m.group(14));
			disk.setFirmwareRevision(m.group(15));
			disks.add(disk);
		}
		return disks;
	}

	private static List<Lun> parseLuns(String str){
		String regex = "LUN (\\d+):\\r\\n"
			+ "[ \\t\\x0B\\f]+Redundancy Group:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Active:_*(\\w+)\\r\\n" 
			+ "[ \\t\\x0B\\f]+Data Capacity:_*(.*)\\r\\n" 
			+ "[ \\t\\x0B\\f]+WWN:_*(.+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Number Of Business Copies:_*(.+)\\r\\n";
		List<Lun> luns = new ArrayList<Lun>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			Lun lun = new Lun();
			lun.setName(m.group(1));
			lun.setRedundancyGroup(m.group(2));
			lun.setActive(m.group(3));
			lun.setDataCapacity(m.group(4));
			lun.setWwn(m.group(5));
			lun.setNumberOfBusinessCopies(m.group(6));
			luns.add(lun);
		}
		return luns;
	}

	private static List<VFP> parseVFP(String str){
		String regex = "Settings for VFP Serial Port ([\\w/\\.]+):\\r\\n"
			+ "[ \\t\\x0B\\f]+VFP Baud Rate:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+VFP Paging Value:_*(\\w+)\\r\\n"; 
		List<VFP> vfps = new ArrayList<VFP>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			VFP vfp = new VFP();
			vfp.setName(m.group(1));
			vfp.setVFPBaudRate(m.group(2));
			vfp.setVFPPagingValue(m.group(3));
			vfps.add(vfp);
		}
		return vfps;
	}

	private static SubSystemInfo parseSubSysInfo(String str){
		String regex = "SUB-SYSTEM SETTINGS\\r\\n\\s*\\r\\n"
			+ "[ \\t\\x0B\\f]+RAID Level:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Auto Format Drive:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Hang Detection:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Capacity Depletion Threshold:_*([\\w%]+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Queue Full Threshold Maximum:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Enable Optimize Policy:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Enable Manual Override:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Manual Override Destination:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Read Cache Disable:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Rebuild Priority:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Security Enabled:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Shutdown Completion:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Subsystem Type ID:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Unit Attention:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Volume Set Partition \\(VSpart\\):_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Write Cache Enable:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Write Working Set Interval:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Enable Prefetch:_*(\\w+)\\r\\n"
			+ "[ \\t\\x0B\\f]+Disable Secondary Path Presentation:_*(\\w+)\\r\\n";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		if (m.find()) {
			SubSystemInfo subSystemInfo = new SubSystemInfo();
			subSystemInfo.setRaidLevel(m.group(1));
			subSystemInfo.setAutoFormatDrive(m.group(2));
			subSystemInfo.setHangDetection(m.group(3));
			subSystemInfo.setCapacityDepletionThreshold(m.group(4));
			subSystemInfo.setQueueFullThresholdMaximum(m.group(5));
			subSystemInfo.setEnableOptimizePolicy(m.group(6));
			subSystemInfo.setEnableManualOverride(m.group(7));
			subSystemInfo.setManualOverrideDestination(m.group(8));
			subSystemInfo.setReadCacheDisable(m.group(9));
			subSystemInfo.setRebuildPriority(m.group(10));
			subSystemInfo.setSecurityEnabled(m.group(11));
			subSystemInfo.setShutdownCompletion(m.group(12));
			subSystemInfo.setSubsystemTypeID(m.group(13));
			subSystemInfo.setUnitAttention(m.group(14));
			subSystemInfo.setVolumeSetPartition(m.group(15));
			subSystemInfo.setWriteCacheEnable(m.group(16));
			subSystemInfo.setWriteWorkingSetInterval(m.group(17));
			subSystemInfo.setEnablePrefetch(m.group(18));
			subSystemInfo.setDisableSecondaryPathPresentation(m.group(19));
			return subSystemInfo;
		}
		return null;
	}
	
	private static void toParse(String patternStr,String subStr,Object object,String field) {
		try{
			PropertyDescriptor pd = new PropertyDescriptor(field,object.getClass());
			Method setMethod = pd.getWriteMethod();
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(subStr);
			if (matcher.find()) {
				setMethod.invoke(object, matcher.group(1));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
