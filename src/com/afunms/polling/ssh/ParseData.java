package com.afunms.polling.ssh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.polling.node.Sensor;
import com.afunms.polling.node.SshController;
import com.afunms.polling.node.SshDisk;
import com.afunms.polling.node.SshEnclosure;
import com.afunms.polling.node.SshPort;
import com.afunms.polling.node.SshVdisk;
import com.afunms.polling.node.SystemInfo;
import com.afunms.polling.node.Volume;

/**
 * 
 * @descrition TODO
 * @author wangxiangyong
 * @date Jun 15, 2013 11:52:22 AM
 */
public class ParseData {
	public static void main(String[] args) {
		/*test te = new test();
		ParseData data = new ParseData();
		 data.parseEnclosure(te.name());*/
		// data.parsePorts(te.name3());
//		data.parseEnclosure(te.name());
		// data.parseEnclosure(te.name5());
		// data.parseSensor(te.name6());
	}

	public SystemInfo parseHpStorageData(String data) {
		SystemInfo info = new SystemInfo();
		Pattern tmpPt = null;
		Matcher mr = null;
		tmpPt = Pattern.compile("(System Information)(.*)(show controllers)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			data = mr.group(2);
		}
		if (data != null && data.length() > 0) {
			data = data.trim();
		}
		this.parseSysName(data, info);// ÏµÍ³Ãû³Æ
		this.parseHealth(data, info);
		this.parseSupportedLocal(data, info);
		this.parseSysContact(data, info);
		this.parseProductId(data, info);
		this.parseProductBand(data, info);
		this.parseScsiVendor(data, info);
		this.parseSysInfo(data, info);
		this.parseSysLocation(data, info);
		this.parseVendorName(data, info);

		return info;
		// System.out.println("contact:"+info.getSysName()+"==setSysContact:"+info.getSysContact());
		// System.out.println("parseSysLocation:"+info.getSupportLocal());
	}

	public List<SshController> parseControllers(String data) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String controllers = "";
		tmpPt = Pattern.compile("(Controllers)(.*)(show disks)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			controllers = mr.group(2);
		}
		if (controllers != null && controllers.length() > 0) {
			controllers = controllers.trim();
		}
		List<SshController> controllerList = new ArrayList<SshController>();
		try {

			String[] controllerArray = controllers.split("\n");

			if (controllerArray != null) {
				SshController controller = null;
				for (int i = 0; i < controllerArray.length; i++) {

					if (controllerArray[i].indexOf("Controller ID:") > -1) {
						controller = new SshController();
						String id = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setId(id);
					}
					if (controllerArray[i].indexOf("Serial Number:") > -1) {
						String serialNum = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setSerialNum(serialNum);
					}
					if (controllerArray[i].indexOf("Hardware Version:") > -1) {
						String hardwareVersion = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setHardwareVersion(hardwareVersion);
					}
					if (controllerArray[i].indexOf("CPLD Version:") > -1) {
						String cpldVersion = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setCpldVersion(cpldVersion);
					}
					if (controllerArray[i].indexOf("MAC Address:") > -1) {
						String mac = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setMac(mac);
					}
					if (controllerArray[i].indexOf("WWNN:") > -1) {
						String wwnn = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setWwnn(wwnn);
					}
					if (controllerArray[i].indexOf("IP Address:") > -1) {
						String ip = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setIp(ip);
					}
					if (controllerArray[i].indexOf("IP Subnet Mask:") > -1) {
						String mask = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setMask(mask);
					}
					if (controllerArray[i].indexOf("IP Gateway:") > -1) {
						String gateway = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setGateway(gateway);
					}
					if (controllerArray[i].indexOf("Disks:") > -1) {
						String disks = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setDisks(disks);
					}
					if (controllerArray[i].indexOf("Vdisks:") > -1) {
						String vdisks = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setVdisks(vdisks);
					}
					if (controllerArray[i].indexOf("Cache Memory Size (MB):") > -1) {
						String cache = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setCache(cache);
					}
					if (controllerArray[i].indexOf("Host Ports:") > -1) {
						String hostPorts = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setHostPorts(hostPorts);
					}
					if (controllerArray[i].indexOf("Disk Channels:") > -1) {
						String diskChannels = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setDiskChannels(diskChannels);
					}
					if (controllerArray[i].indexOf("Disk Bus Type:") > -1) {
						String diskBusType = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setDiskBusType(diskBusType);
					}
					if (controllerArray[i].indexOf("Status:") > -1) {
						String status = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setStatus(status);
					}
					if (controllerArray[i].indexOf("Failed Over:") > -1) {
						String failedOver = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setFailedOver(failedOver);
					}
					if (controllerArray[i].indexOf("Fail Over Reason:") > -1) {
						String failOverReason = controllerArray[i].substring(controllerArray[i].indexOf(":") + 1, controllerArray[i].length()).trim();
						controller.setFailOverReason(failOverReason);
						controllerList.add(controller);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return controllerList;

	}

	public List<SshDisk> parseDisks(String data) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String disks = "";
		tmpPt = Pattern.compile("(show disks)(.*)(show ports)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			disks = mr.group(2);
		}

		if (disks != null && disks.length() > 0) {
			disks = disks.trim();
		}
		List<SshDisk> diskList = new ArrayList<SshDisk>();
		try {

			String[] diskArray = disks.split("\n");
			int num = 7;
			if(disks.contains("Health")){
				num = 6;
			}
			if (diskArray != null) {
				SshDisk disk = null;
				for (int i = 0; i < diskArray.length; i++) {
					if(diskArray[i].trim().equals("") || diskArray[i].contains("Number") || diskArray[i].contains("Status") || diskArray[i].contains("#") || diskArray[i].contains("-----") || diskArray[i].contains("Health")){
						 continue;
					 }
					
					String[] tmpData = diskArray[i].split("\\s++");
					if ((tmpData != null) && (tmpData.length >= num)) {
						disk = new SshDisk();
						disk.setLocation(tmpData[0]);
						disk.setSerialNumber(tmpData[1]);
						disk.setVendor(tmpData[2]);
						disk.setRev(tmpData[3]);
						if(tmpData.length >= num && num == 6){
							 StringBuffer sb = new StringBuffer();
								for(int k = 4; k < tmpData.length-1; k++){
									sb.append(tmpData[k]);
									sb.append(" ");
								}
								disk.setHowUsed(sb.toString());
								disk.setType(tmpData[tmpData.length-1]);
								String[] tmp = diskArray[i+1].trim().split("\\s++");
								disk.setSize(tmp[0]);
								disk.setRate(tmp[1]);
								disk.setStatus(tmp[tmp.length-1]);
						}else if(tmpData.length >= num && num == 7){
							 StringBuffer sb = new StringBuffer();
								for(int k = 4; k < tmpData.length-2; k++){
									sb.append(tmpData[k]);
									sb.append(" ");
								}
								disk.setHowUsed(sb.toString());
								disk.setType(tmpData[tmpData.length-2]);
								disk.setSize(tmpData[tmpData.length-1]);
								String[] tmp = diskArray[i+1].trim().split("\\s++");
								disk.setRate(tmp[0]);
								disk.setStatus(tmp[tmp.length-1]);
						}
						diskList.add(disk);

					}
				}
			}
//			if (disks != null) {
//				SshDisk disk = null;
//				boolean flag = false;
//				int count = 0;
//				String[] tmpData = disks.split("\\s++");
//				if ((tmpData != null)) {
//
//					for (int i = 0; i < tmpData.length; i++) {
//						if (tmpData[i].equals("Status")) {
//							count = i;
//							flag = true;
//							break;
//						}
//					}
//					int temp1 = count + 2;
//					int temp2 = count + 2;
//					if (flag) {
//
//						for (int i = 0; i <= (tmpData.length - temp1) / 9; i++) {
//							if(tmpData.length<temp2+9)break;
//							if(tmpData[temp2].equals("Press")&&tmpData[temp2+1].equals("any")){
//								temp2=temp2+8;
//							}
//							if(tmpData.length<temp2+9)break;
//							disk = new SshDisk();
//							disk.setLocation(tmpData[temp2]);
//							disk.setSerialNumber(tmpData[temp2+1]);
//							disk.setVendor(tmpData[temp2+2]);
//							disk.setRev(tmpData[temp2+3]);
//							if(tmpData[temp2+4].equals("GLOBAL")){
//								disk.setHowUsed(tmpData[temp2+4]+" "+tmpData[temp2+5]);
//								temp2++;
//							}else{
//								if(tmpData.length > temp2 + 9){
//									disk.setHowUsed(tmpData[temp2+4]+" "+tmpData[temp2+5]);
//									temp2++;
//								}else{
//									disk.setHowUsed(tmpData[temp2+4]);
//								}
//							}
//							disk.setType(tmpData[temp2+5]);
//							disk.setSize(tmpData[temp2+6]);
//							disk.setRate(tmpData[temp2+7]);
//							disk.setStatus(tmpData[temp2+8]);
//							diskList.add(disk);
//							temp2 = temp2 + 9;
//							
//						}
//					}
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return diskList;

	}

	public List<SshPort> parsePorts(String data) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String ports = "";
		tmpPt = Pattern.compile("(Port Media)(.*)(show vdisks)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			ports = mr.group(2);
		}

		if (ports != null && ports.length() > 0) {
			ports = ports.trim();
		}
		List<SshPort> portList = new ArrayList<SshPort>();
		try {

			String[] portArray = ports.split("\n");
			if (portArray != null) {
				SshPort port = null;
				for (int i = 0; i < portArray.length; i++) {

					String[] tmpData = portArray[i].split("\\s++");
					if ((tmpData != null)) {
						if (tmpData.length == 8) {
							if(tmpData[0].equals("Press")&&tmpData[1].equals("any")){
								continue;
							}
							port = new SshPort();
							port.setPort(tmpData[0]);
							port.setMedia(tmpData[1]);
							port.setTargetID(tmpData[2]);
							port.setStatus(tmpData[3]);
							port.setSpeedA(tmpData[4]);
							port.setSpeedC(tmpData[5]);
							port.setTopoC(tmpData[6]);
							port.setPid(tmpData[7]);
							portList.add(port);

						} else if (tmpData.length == 6) {
							port = new SshPort();
							port.setPort(tmpData[0]);
							port.setMedia(tmpData[1]);
							port.setTargetID(tmpData[2]);
							port.setStatus(tmpData[3]);
							port.setSpeedA("");
							port.setSpeedC(tmpData[4]);
							port.setTopoC(tmpData[5]);
							port.setPid("");
							portList.add(port);
						}

					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return portList;

	}

	public List<SshVdisk> parseVdisk(String data) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String vdisks = "";
		tmpPt = Pattern.compile("(show vdisks)(.*)(show enclosure-status)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			vdisks = mr.group(2);
		}

		if (vdisks != null && vdisks.length() > 0) {
			vdisks = vdisks.trim();
		}
		List<SshVdisk> vdiskList = new ArrayList<SshVdisk>();
		try {

			 String[] vdisksArray = vdisks.split("\n");
			 if (vdisksArray != null) {
				 SshVdisk vdisk = null;
				 for (int i = 0; i < vdisksArray.length; i++) {
					 if(vdisksArray[i].contains("Name") || vdisksArray[i].contains("#") || vdisksArray[i].contains("-----") || vdisksArray[i].contains("Serial Number")){
						 continue;
					 }
				
				 String[] tmpData = vdisksArray[i].split("\\s++");
				 if ((tmpData != null) && tmpData.length >=10) {
						 vdisk = new SshVdisk();
						 vdisk.setName(tmpData[0]);
						 vdisk.setSize(tmpData[1]);
						 vdisk.setFree(tmpData[2]);
						 vdisk.setOwn(tmpData[3]);
						 vdisk.setPref(tmpData[4]);
						 vdisk.setRaid(tmpData[5]);
						 vdisk.setDisks(tmpData[6]);
						 vdisk.setSpr(tmpData[7]);
						 vdisk.setChk(tmpData[8]);
						 if(tmpData.length > 10){
							 StringBuffer sb = new StringBuffer();
								for(int k = 9; k <= tmpData.length-1; k++){
									sb.append(tmpData[k]);
									sb.append(" ");
								}
								vdisk.setStatusJobs(sb.toString());
						 }else {
							 vdisk.setStatusJobs(tmpData[9]);
						 }
						 vdisk.setSerialNumber(vdisksArray[i+1]);
						 vdiskList.add(vdisk);
				   }
				 }
			 }
//				if ((tmpData != null)) {
//
//					for (int i = 0; i < tmpData.length; i++) {
//						if (tmpData[i].equals("Number")) {
//							count = i;
//							flag = true;
//							break;
//						}
//					}
//					int temp1 = count + 2;
//					int temp2 = count + 2;
//		System.out.println(temp1 + "-----------tmp---" + temp2);
//					if (flag) {
//
//						for (int i = 0; i <= (tmpData.length - temp1) / 11; i++) {
//							if(tmpData.length<temp2+11)break;
//							if(tmpData[temp2].equals("Press")&&tmpData[temp2+1].equals("any")){
//								temp2=temp2+8;
//							}
//							if(tmpData.length<temp2+11)break;
//							vdisk = new SshVdisk();
//							vdisk.setName(tmpData[temp2]);
//							vdisk.setSize(tmpData[temp2 + 1]);
//							vdisk.setFree(tmpData[temp2 + 2]);
//							vdisk.setOwn(tmpData[temp2 + 3]);
//							vdisk.setPref(tmpData[temp2 + 4]);
//							vdisk.setRaid(tmpData[temp2 + 5]);
//							vdisk.setDisks(tmpData[temp2 + 6]);
//							vdisk.setSpr(tmpData[temp2 + 7]);
//							vdisk.setChk(tmpData[temp2 + 8]);
//							if(tmpData.length>=12){
//								StringBuffer sb = new StringBuffer();
//								for(int k = temp2+9; k < tmpData[i].length()-11; k++){
//									sb.append(tmpData[k]);
//									sb.append(" ");
//									temp2++;
//								}
//								vdisk.setStatusJobs(sb.toString());
//							}else{
//								vdisk.setStatusJobs(tmpData[temp2 + 9]);
//							}
//							vdisk.setSerialNumber(tmpData[tmpData.length-1]);
//							temp2 = temp2 + 11;
//							vdiskList.add(vdisk);
//						}
//					}
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vdiskList;

	}

	public Map parseEnclosure(String data) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String enclosure = "";
		Map map=new HashMap();
		tmpPt = Pattern.compile("(Chassis)(.*)(show sensor-status)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			enclosure = mr.group(2);
		}

		if (enclosure != null && enclosure.length() > 0) {
			enclosure = enclosure.trim();
		}
	
		List<SshEnclosure> enclosureList = null;
		try {

			String[] enclosureArray = enclosure.split("\n");
			if (enclosureArray != null) {
				boolean flag = false;
				String id = "";
				StringBuffer tempBuffer=null;
				SshEnclosure sshEnclosure = null;
				StringBuffer keyBuffer=null;
				for (int i = 0; i < enclosureArray.length; i++) {

					String[] tmpData = enclosureArray[i].split("\\s++");
					if ((tmpData != null)&&tmpData.length>0){
                        int ii=i;
						int size = tmpData.length;
						if (flag) {
							if(tmpData[0].indexOf("------------")>-1)continue;
							
							id=tmpData[0];
							int v=0;
							tempBuffer=new StringBuffer();
							while(v<5&&tmpData[0].indexOf("------------")<0){
								
								for(int k=0;k<tmpData.length;k++){
									tempBuffer.append(tmpData[k]).append(" ");
								}
								if(enclosureArray.length-1>++ii)
								 tmpData = enclosureArray[ii].split("\\s++");
							}
							String tempData="";
							Pattern tmpPt1 = Pattern.compile("(\\w+)(\\s+)(\\w+)(\\s+)", Pattern.DOTALL);
							mr = tmpPt1.matcher(tempBuffer.toString().trim());
							keyBuffer=new StringBuffer();
							String begin="",end="";
							if (mr.find()) {
								tempData = mr.group(3);
								keyBuffer.append("Chassis:").append( mr.group(1)).append("   ");
								//keyBuffer.append("Vendor:").append( mr.group(3)).append("   ");
								begin=mr.group(3);
							}
							tmpPt = Pattern.compile("(\\s+)(\\d+)(\\s+)(\\d+\\:\\d+\\s\\d+)(\\s+)(\\d+\\:\\d+\\s\\d+)(\\s+)(\\w++)(\\s+)(\\w++)(\\s+)(\\w++)", Pattern.DOTALL);
							mr = tmpPt.matcher(tempBuffer.toString().trim());
							if (mr.find()) {
								tempData = mr.group(8);
								//keyBuffer.append("CPLD:").append( mr.group(2)).append("  ");
								//keyBuffer.append("(EMP A BUS:ID Rev):").append( mr.group(4)).append("  ");
								//keyBuffer.append("(EMP B BUS:ID Rev):").append( mr.group(6)).append("  ");
								//keyBuffer.append("WWPN:").append( mr.group(8)).append("  ");
								keyBuffer.append("|Status:").append( mr.group(10)).append("  ");
								keyBuffer.append("|Health:").append( mr.group(12)).append("  ");
								end= mr.group(2);
//								System.out.println(mr.group(1)+"=================="+tempData);
							}
							id=keyBuffer.toString();
							flag = false;
							enclosureList = new ArrayList<SshEnclosure>();
						}else if (tmpData[size - 1].equals("Health")) {
							flag = true;
							if(enclosureList!=null)map.put(id, enclosureList);
						}else if (tmpData.length == 6) {
							sshEnclosure = new SshEnclosure();
							sshEnclosure.setType(tmpData[0]);
							sshEnclosure.setNumber(tmpData[1]);
							sshEnclosure.setStatus(tmpData[2]);
							sshEnclosure.setFruPN(tmpData[3]);
							sshEnclosure.setFruSN(tmpData[4]);
							sshEnclosure.setAddData(tmpData[5]);
							enclosureList.add(sshEnclosure);

						}
					}
				}
				if(enclosureList!=null)map.put(id, enclosureList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	public List<Sensor> parseSensor(String data) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String sensorData = "";
		tmpPt = Pattern.compile("(Sensor Name)(.*)(show volumes)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			sensorData = mr.group(2);
		}

		if (sensorData != null && sensorData.length() > 0) {
			sensorData = sensorData.trim();
		}

		List<Sensor> sensorList = new ArrayList<Sensor>();
		try {

			String[] enclosureArray = sensorData.split("\n");
			if (enclosureArray != null) {
				boolean flag=false;
				Sensor sensor = null;
				for (int i = 0; i < enclosureArray.length; i++) {

					String[] tmpData = enclosureArray[i].split("\\s++");
					
					if ((tmpData != null)) {
						int size=tmpData.length;
						if(size>2&&tmpData[0].equals("Press")&&tmpData[1].equals("any"))continue;
						StringBuffer name=null;
						if(size>=1)
						if(tmpData[size-1].equals("Status"))flag=true;
						
						if (flag&&tmpData.length>=3) {
							
							sensor = new Sensor();
							 name=new StringBuffer();
							for(int j=0;j<size-2;j++){
								name.append(tmpData[j]).append(" ");
							}
							sensor.setName(name.toString());
							sensor.setValue(tmpData[size-2]);
							sensor.setStatus(tmpData[size-1]);
							sensorList.add(sensor);

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sensorList;

	}

	public List<Volume> parseVolume(String data) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String volumeData = "";
		tmpPt = Pattern.compile("(show volumes)(.*)(---------------)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		List<Volume> volumeList = new ArrayList<Volume>();
		if (mr.find()) {
			volumeData = mr.group(2);
		}

		if (volumeData != null && volumeData.length() > 0) {
			volumeData = volumeData.trim();
		}
		try {

			// String[] enclosureArray = sensorData.split("\n");
			if (volumeData != null) {
				Volume volume = null;
				boolean flag = false;
				int count = 0;
				String[] tmpData = volumeData.split("\\s++");
				if ((tmpData != null)) {

					for (int i = 0; i < tmpData.length; i++) {
						if (tmpData[i].equals("Description")) {
							count = i;
							flag = true;
							break;
						}
					}
					int temp1 = count + 2;
					int temp2 = count + 2;
					if (flag) {

						for (int i = 0; i <= (tmpData.length - temp1) / 9; i++) {
							if(tmpData.length<temp2+9)break;
							if(tmpData[temp2].equals("Press")&&tmpData[temp2+1].equals("any")){
								temp2=temp2+8;
							}
							if(tmpData.length<temp2+9)break;
							volume = new Volume();
							volume.setVdiskName(tmpData[temp2]);
							volume.setSize(tmpData[temp2 + 1]);
							volume.setSerialNumber(tmpData[temp2 + 2]);
							volume.setWrPolicy(tmpData[temp2 + 3]);
							volume.setCacheOpt(tmpData[temp2 + 4]);
							volume.setReadAheadSize(tmpData[temp2 + 5]);
							volume.setType(tmpData[temp2 + 6]);
							volume.setClasses(tmpData[temp2 + 7]);
							volume.setDescription(tmpData[temp2 + 8]);
							
							temp2 = temp2 + 9;
							volumeList.add(volume);

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return volumeList;

	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseSysName(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String sysName = "";
		tmpPt = Pattern.compile("(System Name:)(.*)(System Contact:)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			sysName = mr.group(2);
		}
		if (sysName != null && sysName.length() > 0) {
			sysName = sysName.trim();
		}
		info.setSysName(sysName);
	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseSysContact(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String sysContact = "";
		tmpPt = Pattern.compile("(System Contact:)(.*)(System Location:)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			sysContact = mr.group(2);
		}
		if (sysContact != null && sysContact.length() > 0) {
			sysContact = sysContact.trim();
		}
		info.setSysContact(sysContact);
	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseSysLocation(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String sysLocation = "";
		tmpPt = Pattern.compile("(System Location:)(.*)(System Information:)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			sysLocation = mr.group(2);
		}
		if (sysLocation != null && sysLocation.length() > 0) {
			sysLocation = sysLocation.trim();
		}

		info.setSysLocation(sysLocation);
	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseSysInfo(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String sysInfo = "";
		tmpPt = Pattern.compile("(System Information:)(.*)(Vendor Name:)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			sysInfo = mr.group(2);
		}
		if (sysInfo != null && sysInfo.length() > 0) {
			sysInfo = sysInfo.trim();
		}
		info.setSysInfo(sysInfo);
	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseVendorName(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String vendorName = "";
		tmpPt = Pattern.compile("(Vendor Name:)(.*)(Product ID:)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			vendorName = mr.group(2);
		}
		if (vendorName != null && vendorName.length() > 0) {
			vendorName = vendorName.trim();
		}
		info.setVerdorID(vendorName);
	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseProductId(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String productID = "";
		tmpPt = Pattern.compile("(Product ID:)(.*)(Product Brand:)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			productID = mr.group(2);
		}
		if (productID != null && productID.length() > 0) {
			productID = productID.trim();
		}
		info.setProductID(productID);
	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseProductBand(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String brand = "";
		tmpPt = Pattern.compile("(Product Brand:)(.*)(SCSI Vendor ID:)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			brand = mr.group(2);
		}
		if (brand != null && brand.length() > 0) {
			brand = brand.trim();
		}
		info.setProBrand(brand);
	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseScsiVendor(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String scsi = "";
		tmpPt = Pattern.compile("(SCSI Vendor ID:)(.*)(Enclosure Count:)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			scsi = mr.group(2);
		}
		if (scsi != null && scsi.length() > 0) {
			scsi = scsi.trim();
		}
		info.setScsiVendorId(scsi);
	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseHealth(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String health = "";
		tmpPt = Pattern.compile("(Health:)(.*)(Supported Locales:)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			health = mr.group(2);
		}
		if (health != null && health.length() > 0) {
			health = health.trim();
		}
		info.setHealth(health);
	}

	/**
	 * 
	 * @description TODO
	 * @author wangxiangyong
	 * @date Jun 15, 2013 11:57:00 AM
	 * @return void
	 * @param data
	 * @param info
	 */
	public void parseSupportedLocal(String data, SystemInfo info) {
		Pattern tmpPt = null;
		Matcher mr = null;
		String supportLocal = "";
		tmpPt = Pattern.compile("(Supported Locales:)(.*)(# show controllers)", Pattern.DOTALL);
		mr = tmpPt.matcher(data);
		if (mr.find()) {
			supportLocal = mr.group(2);
		}
		if (supportLocal != null && supportLocal.length() > 0) {
			supportLocal = supportLocal.trim();
		}
		info.setSupportLocal(supportLocal);
	}

}
