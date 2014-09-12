package com.afunms.polling.snmp;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.model.Nodeconfig;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Nas;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Hpdpcollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.gatherResulttosql.HostnetPingResultTosql;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LoadHpNasFile {
	/**
	 * @param hostname
	 */
	private String ipaddress;

	 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public LoadHpNasFile(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	
	public LoadHpNasFile() {
		
	}
	
	public Hashtable collect_Data(NodeGatherIndicators gatherIndicatorsNode)
    {
		ipaddress = "";
		Hashtable returnHash = new Hashtable();
		Nas nas = (Nas)PollingEngine.getInstance().getNasByID(Integer.parseInt(gatherIndicatorsNode.getNodeid()+""));
		
		if(nas == null)return returnHash;
		
		if(ShareData.getTimegatherhash() != null){
    		if(ShareData.getTimegatherhash().containsKey(nas.getId()+":webservice")){
    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
    			int _result = 0;
    			_result = timeconfig.isBetween((List)ShareData.getTimegatherhash().get(nas.getId()+":webservice"));
    			if(_result ==1 ){
    				//SysLogger.info("########时间段内: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else if(_result == 2){
    				//SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else {
    				SysLogger.info("######## "+nas.getIpAddress()+" 不在采集NAS时间段内,退出##########");
    				//清除之前内存中产生的告警信息
    			    try{
    			    	//清除之前内存中产生的内存告警信息
						CheckEventUtil checkutil = new CheckEventUtil();
						NodeDTO nodedto = null;
    					NodeUtil nodeUtil = new NodeUtil();
    					nodedto = nodeUtil.creatNodeDTOByNode(nas);
						checkutil.deleteEvent(nas.getId()+"", nodedto.getType(), nodedto.getSubtype(), "nasstatus", null);
						checkutil.deleteEvent(nas.getId()+"", nodedto.getType(), nodedto.getSubtype(), "ping", null);
    			    }catch(Exception e){
    			    	e.printStackTrace();
    			    }
    				return returnHash;
    			}
    			
    		}
    	}
		
		SysLogger.info("######## "+nas.getIpAddress()+" 采集NAS##########");
		//ping采集
		Vector vector=null;
		Hashtable pinghash = new Hashtable();
		PingUtil pingU=new PingUtil(nas.getIpAddress());
		Integer[] packet=pingU.ping();
		try{
				vector=pingU.addhis(packet); 
		}catch(Exception e){
			SysLogger.info(nas.getIpAddress()+"===ping数据为空");
			e.printStackTrace();
		}
		if(vector!=null){				
			//hostdataManager.createHostData(vector,alarmIndicatorsNode);					
			ShareData.setPingdata(nas.getIpAddress(),vector);
			pinghash.put("ping", vector);
		}
		HostnetPingResultTosql tosql=new HostnetPingResultTosql();
        tosql.CreateResultTosql(pinghash, nas.getId() + "");
		if(pinghash != null){
			//对PING值进行告警检测
			if(pinghash != null && pinghash.size()>0){
				Vector pingvector = (Vector)pinghash.get("ping");
				if(pingvector != null){
					String ping = "0";
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					int b = 0;
					for (int i = 0; i < pingvector.size(); i++) {
	    				Pingcollectdata pingdata = (Pingcollectdata) pingvector.elementAt(i);
	    				ping = pingdata.getThevalue();
	    				if(pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")){
	    					b++;
							//连通率进行判断               						
							List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(nas.getId()), gatherIndicatorsNode.getType(), "");
							for(int m = 0 ; m < list.size() ; m ++){
								AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(m);
								if("1".equals(_alarmIndicatorsNode.getEnabled())){
									if(_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")){
										CheckEventUtil checkeventutil = new CheckEventUtil();
										//SysLogger.info(_alarmIndicatorsNode.getName()+"=====_alarmIndicatorsNode.getName()=========");
										checkeventutil.checkEvent(nas, _alarmIndicatorsNode, pingdata.getThevalue());
									}
								}  
							}
						}
	    			}
					pingvector=null;
					if(ping == "0"){
						return returnHash;
					}
				}
			}
		}
		vector=null;
		
		ipaddress=nas.getIpAddress();
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
		if(ipAllData == null)ipAllData = new Hashtable();
		//
		
		StringBuffer fileContent = new StringBuffer();
		Nodeconfig nodeconfig = new Nodeconfig();
		String collecttime = "";
		
    	try 
		{
    		String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+".nas.log";		
			//String filename = "D:/10.41.1.50.log";		
			
			//System.out.println("====解析文件=="+filename);
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			String strLine = null;
    		//读入文件内容
    		while((strLine=br.readLine())!=null)
    		{
    			fileContent.append(strLine + "\n");
    		}
    		isr.close();
    		fis.close();
    		br.close();
    		try{
    			copyFile(ipaddress,getMaxNum(ipaddress));
    		}catch(Exception e){
    			e.printStackTrace();
    		}
		} 
    	catch (Exception e)
		{
			e.printStackTrace();
		}
    	
    	Pattern tmpPt = null;
    	Matcher mr = null;
    	tmpPt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:collecttimeend)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			collecttime = mr.group(2);
		}
		if (collecttime != null && collecttime.length()>0 ){
			collecttime = collecttime.trim();
		}
		
		////log日志异常判断
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String lasttime = "";
		try {
			String sql = "select * from nms_hpnas where ipaddress = '" + ipaddress + "'";
			rs = dbmanager.executeQuery(sql);
			while(rs.next()){
				lasttime = rs.getString("mon_time");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally{
			dbmanager.close();
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		String logstatus = "1";
		if(lasttime.equals(collecttime) || collecttime.equals("")){
			logstatus = "-1";
		}
			try {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil
						.conversionToNodeDTO(nas);
				// 判断是否存在此告警指标
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil
						.getAlarmInicatorsThresholdForNode(nodeDTO
								.getId()
								+ "", nodeDTO.getType(), nodeDTO
								.getSubtype());
				CheckEventUtil checkEventUtil = new CheckEventUtil();
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list
							.get(i);
					if ("filestatus"
							.equalsIgnoreCase(alarmIndicatorsNode
									.getName())) {
							checkEventUtil.checkEvent(nodeDTO,
									alarmIndicatorsNode, logstatus);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		if(logstatus == "-1"){
			return null;
		}
		
		String nasdataTmp = "";
		tmpPt = Pattern.compile("(cmdbegin:ibrixserver)(.*)(cmdbegin:end)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			nasdataTmp = mr.group(2);
		}
    	String[] nasStr = nasdataTmp.toString().trim().split("\\n");
		Vector nasVector = new Vector();
		Hashtable nashash = new Hashtable();
		
		if(nasStr != null && nasStr.length > 0){
			int l = 0;
			for(int i = 0; i < nasStr.length; i++){
				if(nasStr[i].startsWith("SERVER_NAME") || nasStr[i].startsWith("-------")){
					continue;
				}
				l++;
				String[] nasTmp = nasStr[i].trim().split("\\s++");
				if(nasTmp != null && nasTmp.length > 0){
					nashash = new Hashtable();
					nashash.put("server_name", nasTmp[0]);
					if(nasTmp.length > 7){
						String stateTmp = "";
						for(int j = 1; j < nasTmp.length-5; j++){
							stateTmp +=nasTmp[j];
						}
						nashash.put("state", stateTmp);
						nashash.put("cpu", nasTmp[nasTmp.length-5]);
						nashash.put("net_io", nasTmp[nasTmp.length-4]);
						nashash.put("disk_io", nasTmp[nasTmp.length-3]);
						nashash.put("backup", nasTmp[nasTmp.length-2]);
						nashash.put("ha", nasTmp[nasTmp.length-1]);
						nashash.put("collecttime", collecttime);
					}else{
						nashash.put("state", nasTmp[1]);
						nashash.put("cpu", nasTmp[2]);
						nashash.put("net_io", nasTmp[3]);
						nashash.put("disk_io", nasTmp[4]);
						nashash.put("backup", nasTmp[5]);
						nashash.put("ha", nasTmp[6]);
						nashash.put("collecttime", collecttime);
					}
					nasVector.add(nashash);
				}
			}
			// ---------------------------------判断告警 start
			try {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil
						.conversionToNodeDTO(nas);
				// 判断是否存在此告警指标
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil
						.getAlarmInicatorsThresholdForNode(nodeDTO
								.getId()
								+ "", nodeDTO.getType(), nodeDTO
								.getSubtype());
				CheckEventUtil checkEventUtil = new CheckEventUtil();
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list
							.get(i);
					if ("nasstatus"
							.equalsIgnoreCase(alarmIndicatorsNode
									.getName())) {
						for(int k = 0; k < nasVector.size(); k++){
							Hashtable alarmhash = (Hashtable)nasVector.get(k);
							String states = "";
							String ha = "";
							states = (String)alarmhash.get("state");
							ha = (String)alarmhash.get("ha");
							String sIndex = "";
							sIndex = (String)alarmhash.get("server_name");
							String status = "1";
							if(!states.equalsIgnoreCase("UP") || !ha.equalsIgnoreCase("on")){
								status = "-1";
								if(!states.equalsIgnoreCase("UP")){
									sIndex += ":" + alarmhash.get("state");
								}
								if(!ha.equalsIgnoreCase("on")){
									sIndex += ":" + alarmhash.get("ha");
								}
							}
							checkEventUtil.checkEvent(nodeDTO,
									alarmIndicatorsNode, status, sIndex);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(nasVector.size() > 0 && nasVector != null){
			returnHash.put("ipaddress", ipaddress);
			returnHash.put("hpnas", nasVector);
		}
		try {
			boolean b = new LoadHpNasFile().addHpnas(returnHash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnHash;
	//	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
    }
	 public String getMaxNum(String ipAddress){
	    	String maxStr = null;
			File logFolder = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/");
	   		String[] fileList = logFolder.list();
	   		
	   		for(int i=0;i<fileList.length;i++) //找一个最新的文件
	   		{
	   			if(!fileList[i].startsWith(ipAddress)) continue;
	   			
	   			return ipAddress;
	   		}
	   		return maxStr;
	    }
	 
	 public void copyFile(String ipAddress,String max){
			try   { 
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
	 
	 public boolean addHpnas(Hashtable returnhash) throws Exception {
		 
			String ipaddress = "";
			ipaddress = (String)returnhash.get("ipaddress");
			
			if(returnhash.containsKey("hpnas")){
				DBManager dbmanager = new DBManager();
				try{
					
					Vector hpnasVector = null;
					hpnasVector = (Vector)returnhash.get("hpnas");
					Hpdpcollectdata vo = null;
					Hashtable nashash = new Hashtable();
					if(hpnasVector != null && hpnasVector.size() > 0){
						
						String sql1 = "delete from nms_hpnas where ipaddress='" + ipaddress + "'";
						dbmanager.addBatch(sql1);
						
						for(int i = 0; i < hpnasVector.size(); i++){
							nashash = (Hashtable)hpnasVector.get(i);
							
							String sql = "";
							try {
								 sql = "insert into nms_hpnas(ipaddress,server_name,state,cpu,net_io,disk_io,backup,ha,mon_time) "
								 	+ "values('"
									+ ipaddress
									+ "','"
									+ nashash.get("server_name").toString()
									+ "','"
									+ nashash.get("state").toString()
									+ "','"
									+ nashash.get("cpu").toString()
									+ "','"
									+ nashash.get("net_io").toString()
									+ "','"
									+ nashash.get("disk_io").toString() 
									+ "','" 
									+ nashash.get("backup").toString()
									+ "','"
									+ nashash.get("ha").toString()
									+ "','"
									+ nashash.get("collecttime").toString() + "')";
								 
								dbmanager.addBatch(sql);
							} catch (Exception e) {
								e.printStackTrace();
								return false;
							} 
						}
						dbmanager.executeBatch();
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dbmanager.close();
				}
			}
			return true;
		}
    
}

