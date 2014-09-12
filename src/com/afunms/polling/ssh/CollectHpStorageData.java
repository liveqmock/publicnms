package com.afunms.polling.ssh;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Sensor;
import com.afunms.polling.node.SshController;
import com.afunms.polling.node.SshDisk;
import com.afunms.polling.node.SshEnclosure;
import com.afunms.polling.node.SshPort;
import com.afunms.polling.node.SshVdisk;
import com.afunms.polling.node.SystemInfo;
import com.afunms.polling.node.Volume;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.telnet.SSHWrapper;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.model.RemotePingHost;

public class CollectHpStorageData extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returnHash = new Hashtable();
		Host node = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeGatherIndicators.getNodeid()));
		if (node == null)
			return returnHash;
		// 判断是否在采集时间段内
		if (ShareData.getTimegatherhash() != null) {
			if (ShareData.getTimegatherhash().containsKey(node.getId() + ":equipment")) {
				TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
				int _result = 0;
				_result = timeconfig.isBetween((List) ShareData.getTimegatherhash().get(node.getId() + ":equipment"));
				if (_result == 1) {
				} else if (_result == 2) {
				} else {
					// 清除之前内存中产生的告警信息
					try {
						NodeDTO nodedto = null;
						NodeUtil nodeUtil = new NodeUtil();
						nodedto = nodeUtil.creatNodeDTOByHost(node);
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.deleteEvent(node.getId() + "", nodedto.getType(), nodedto.getSubtype(), "storage", null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return returnHash;
				}

			}
		}

//		SSHWrapper ssh = new SSHWrapper();
		RemotePingHostDao remotePingHostDao = null;
		String result = "";
		StringBuffer cmd = new StringBuffer();
		SystemInfo info=null;
		List<SshController> controllerList=null;
		List<SshDisk> diskList =null;
		List<SshPort>  portList=null;
		List<SshVdisk>  vdiskList=null;
		List<SshEnclosure> enclosurelist=null;
		List<Sensor> sensorList=null;
		List<Volume> volumeList=null;
		Map map=null;
		SSHUtil sshutil=null;
		try {
			cmd.append("show system").append("\n");
			cmd.append("show controllers").append("\n");
			cmd.append("show disks").append("\n");
			cmd.append("show ports").append("\n");
			cmd.append("show vdisks").append("\n");
			cmd.append("show enclosure-status").append("\n");
			cmd.append("show sensor-status").append("\n");
			cmd.append("show volumes").append("\n");
			//String[] cmds = { "", "show system"," ","show controllers"," "," show disks","show ports"," ","show vdisks","show enclosure-status"," ","show sensor-status"," ","show volumes"};
			//String[] cmds = { "set cli-parameter timeout 90 pager off", "show system"," ","show controllers"," "," show disks","show ports"," ","show vdisks","show enclosure-status"," ","show sensor-status"," ","show volumes"};

			String[] cmds = { "set cli-parameter timeout 90 pager off", "show system","show controllers","show disks","show ports","show vdisks","show enclosure-status","show sensor-status","show volumes"};


			remotePingHostDao = new RemotePingHostDao();
			RemotePingHost remotePingHost = (RemotePingHost) remotePingHostDao.findByNodeId(node.getId() + "");
			  sshutil=new SSHUtil(node.getIpAddress(), 22, remotePingHost.getUsername(), remotePingHost.getPassword());
			  
			  result=sshutil.executeCmds(cmds);
//			ssh.connect(node.getIpAddress(), 22, remotePingHost.getUsername(), remotePingHost.getPassword());
//			result = ssh.send(cmd.toString());
//			test te = new test();
//			result=te.name();
			ParseData data = new ParseData();
			 info=data.parseHpStorageData(result);
			 controllerList =data.parseControllers(result);
			 diskList =data.parseDisks(result);
			 portList=data.parsePorts(result);
			  vdiskList=data.parseVdisk(result);
			 map=data.parseEnclosure(result);
			 sensorList=data.parseSensor(result);
			 volumeList=data.parseVolume(result);
			returnHash.put("systeminfo", info);
			returnHash.put("controllers", controllerList);
			returnHash.put("disks", diskList);
			returnHash.put("ports", portList);
			returnHash.put("vdisk", vdiskList);
			returnHash.put("enclosures", map);
			returnHash.put("sensor", sensorList);
			returnHash.put("volume", volumeList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			try {
//				ssh.disconnect();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			if(sshutil!=null)
			sshutil.disconnect();
			remotePingHostDao.close();
		}
		ShareData.getSharedata().put("hpstorage:" + node.getIpAddress(),returnHash);
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		
			
				//连通率进行判断               						
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()),"storage", "");
				for(int m = 0 ; m < list.size() ; m ++){
					AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(m);
					if(info!=null&&"1".equals(_alarmIndicatorsNode.getEnabled())){
						if(_alarmIndicatorsNode.getName().equalsIgnoreCase("health")){
							CheckEventUtil checkeventutil = new CheckEventUtil();
							checkeventutil.checkEvent(node, _alarmIndicatorsNode, info.getHealth());
						}
					}  
					if("1".equals(_alarmIndicatorsNode.getEnabled())){
						if(_alarmIndicatorsNode.getName().equalsIgnoreCase("vdisk")){
							CheckEventUtil checkutil = new CheckEventUtil();
							if(vdiskList!=null&&vdiskList.size()>0){
								for(int i=0;i<vdiskList.size();i++){
									SshVdisk disk=vdiskList.get(i);
									String tmp = disk.getStatusJobs();
									if(disk.getStatusJobs().contains("FTOL")){
										tmp = "FTOL";
									}
							       checkutil.checkEvent(node, _alarmIndicatorsNode,  tmp, disk.getName().trim());
							}
							}
						}
					} 
					if("1".equals(_alarmIndicatorsNode.getEnabled())){
						if(_alarmIndicatorsNode.getName().equalsIgnoreCase("port")){
							CheckEventUtil checkutil = new CheckEventUtil();
							if(portList!=null&&portList.size()>0){
								for(int i=0;i<portList.size();i++){
									SshPort port=portList.get(i);
									checkutil.checkEvent(node, _alarmIndicatorsNode,  port.getStatus(), port.getPort().trim());
							}
						}
						}
					} 
					if("1".equals(_alarmIndicatorsNode.getEnabled())){
						if(_alarmIndicatorsNode.getName().equalsIgnoreCase("enclosure")){
							CheckEventUtil checkutil = new CheckEventUtil();
							if(enclosurelist!=null&&enclosurelist.size()>0){
								for(int i=0;i<enclosurelist.size();i++){
									SshEnclosure enclosure=enclosurelist.get(i);
									checkutil.checkEvent(node, _alarmIndicatorsNode,  enclosure.getStatus(), enclosure.getType()+":"+enclosure.getNumber());
							}
						}
						}
					} 
					if("1".equals(_alarmIndicatorsNode.getEnabled())){
						if(_alarmIndicatorsNode.getName().equalsIgnoreCase("controller")){
							CheckEventUtil checkutil = new CheckEventUtil();
							if(controllerList!=null&&controllerList.size()>0){
								for(int i=0;i<controllerList.size();i++){
									SshController controller=controllerList.get(i);
									checkutil.checkEvent(node, _alarmIndicatorsNode,  controller.getStatus(), "ID:"+controller.getId());
							}
						}
						}
					} 
					if("1".equals(_alarmIndicatorsNode.getEnabled())){
						if(_alarmIndicatorsNode.getName().equalsIgnoreCase("disk")){
							CheckEventUtil checkutil = new CheckEventUtil();
							if(diskList!=null&&diskList.size()>0){
								for(int i=0;i<diskList.size();i++){
									SshDisk disk=diskList.get(i);
									String status = disk.getStatus();
									String howused = disk.getHowUsed();
									String tmp = "OK";
									if((!status.equalsIgnoreCase("OK") && !status.equalsIgnoreCase("UP")) || (!howused.contains("VDISK") && !howused.contains("GLOBAL SP"))){
										tmp = status;
									}
									
									checkutil.checkEvent(node, _alarmIndicatorsNode,  tmp, disk.getLocation()+":"+disk.getSerialNumber());
							}
						}
						}
					}
					if("1".equals(_alarmIndicatorsNode.getEnabled())){
						if(_alarmIndicatorsNode.getName().equalsIgnoreCase("sensor")){
							CheckEventUtil checkutil = new CheckEventUtil();
							if(sensorList!=null&&sensorList.size()>0){
								for(int i=0;i<sensorList.size();i++){
									Sensor sensor=sensorList.get(i);
									checkutil.checkEvent(node, _alarmIndicatorsNode,  sensor.getStatus(), sensor.getName());
							}
						}
						}
					}
			
		}
		return returnHash;
	}

}
