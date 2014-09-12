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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import com.afunms.polling.node.Ggsci;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Ggscicollectdata;
import com.afunms.polling.om.Hpdpcollectdata;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.gatherResulttosql.HostnetPingResultTosql;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LoadGgsciFile {
	/**
	 * @param hostname
	 */
	private String ipaddress;

	 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public LoadGgsciFile(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	
	public LoadGgsciFile() {
		
	}
	
	public Hashtable collect_Data(NodeGatherIndicators gatherIndicatorsNode)
    {
		Hashtable returnHash = new Hashtable();
		Ggsci ggsci = (Ggsci)PollingEngine.getInstance().getGgsciByID(Integer.parseInt(gatherIndicatorsNode.getNodeid()+""));
		
		if(ggsci == null)return returnHash;
		
		if(ShareData.getTimegatherhash() != null){
    		if(ShareData.getTimegatherhash().containsKey(ggsci.getId()+":ggsciservice")){
    			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
    			int _result = 0;
    			_result = timeconfig.isBetween((List)ShareData.getTimegatherhash().get(ggsci.getId()+":ggsciservice"));
    			if(_result ==1 ){
    				//SysLogger.info("########时间段内: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else if(_result == 2){
    				//SysLogger.info("########全天: 开始采集 "+node.getIpAddress()+" PING数据信息##########");
    			}else {
    				SysLogger.info("######## "+ggsci.getIpAddress()+" 不在采集GGSCI时间段内,退出##########");
    				//清除之前内存中产生的告警信息
    			    try{
    			    	//清除之前内存中产生的内存告警信息
						CheckEventUtil checkutil = new CheckEventUtil();
						NodeDTO nodedto = null;
    					NodeUtil nodeUtil = new NodeUtil();
    					nodedto = nodeUtil.creatNodeDTOByNode(ggsci);
						checkutil.deleteEvent(ggsci.getId()+"", nodedto.getType(), nodedto.getSubtype(), "ggscistatus", null);
    			    }catch(Exception e){
    			    	e.printStackTrace();
    			    }
    				return returnHash;
    			}
    			
    		}
    	}
		SysLogger.info("######## "+ggsci.getIpAddress()+" 采集ggsci##########");
		//ping采集
		Vector vector=null;
		Hashtable pinghash = new Hashtable();
		PingUtil pingU=new PingUtil(ggsci.getIpAddress());
		Integer[] packet=pingU.ping();
		try{
				vector=pingU.addhis(packet); 
		}catch(Exception e){
			SysLogger.info(ggsci.getIpAddress()+"===ping数据为空");
			e.printStackTrace();
		}
		if(vector!=null){				
			//hostdataManager.createHostData(vector,alarmIndicatorsNode);					
			ShareData.setPingdata(ggsci.getIpAddress(),vector);
			pinghash.put("ping", vector);
		}
		HostnetPingResultTosql tosql=new HostnetPingResultTosql();
        tosql.CreateResultTosql(pinghash, ggsci.getId() + "");
		if(pinghash != null){
			//对PING值进行告警检测
			if(pinghash != null && pinghash.size()>0){
				Vector pingvector = (Vector)pinghash.get("ping");
				if(pingvector != null){
					String ping = "0";
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					for (int i = 0; i < pingvector.size(); i++) {
	    				Pingcollectdata pingdata = (Pingcollectdata) pingvector.elementAt(i);
	    				ping = pingdata.getThevalue();
	    				if(pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")){
							//连通率进行判断               						
							List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(ggsci.getId()), gatherIndicatorsNode.getType(), "");
							for(int m = 0 ; m < list.size() ; m ++){
								AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(m);
								if("1".equals(_alarmIndicatorsNode.getEnabled())){
									if(_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")){
										CheckEventUtil checkeventutil = new CheckEventUtil();
										//SysLogger.info(_alarmIndicatorsNode.getName()+"=====_alarmIndicatorsNode.getName()=========");
										checkeventutil.checkEvent(ggsci, _alarmIndicatorsNode, pingdata.getThevalue());
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
		
		ipaddress=ggsci.getIpAddress();
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
		if(ipAllData == null)ipAllData = new Hashtable();
		//
		
		StringBuffer fileContent = new StringBuffer();
		Nodeconfig nodeconfig = new Nodeconfig();
		String collecttime = "";
    	try 
		{
    		String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+".ggsci.log";		
			//String filename = "D:/10.41.1.111.ggsci.log";		
			
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
    	
	     //----------------解析数据采集时间内容--创建监控项---------------------        	
		tmpPt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:collecttimeend)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			collecttime = mr.group(2);
		}
		if (collecttime != null && collecttime.length()>0 ){
			collecttime = collecttime.trim();
		}
		
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String lasttime = "";
		try {
			String sql = "select * from nms_ggsci where ipaddress = '" + ipaddress + "'";
			rs = dbmanager.executeQuery(sql);
			while(rs.next()){
				lasttime = rs.getString("collecttime");
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
						.conversionToNodeDTO(ggsci);
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
				return returnHash;
			}
			
			tmpPt = Pattern.compile("(\\s+\\([a-zA-Z0-9.]+\\)\\s+[0-9]+\\>)(.*)([A-Z]{5}\\s+\\([a-zA-Z0-9.]+\\)\\s+[0-9]+\\>)",Pattern.DOTALL);
	    	mr = tmpPt.matcher(fileContent.toString());
	    	String ggsciTmp = "";
	    	if(mr.find()){
	    		ggsciTmp = mr.group(2);
	    	}
	    	Hashtable hashtable = new Hashtable();
	    	Vector v = new Vector();
	    	String[] ggsciStr = ggsciTmp.toString().split("\\n");
	    	for(int i = 0; i < ggsciStr.length; i++){
	    		String value = ggsciStr[i];
	    		if(value.startsWith("Program") || value.trim().equals("") || value.length() == 0){
	    			continue;
	    		}
				String[] re=value.split("\\s++");
				if(re.length < 5){
					String[] res = new String[5];
					if(re.length == 1){
						res[0] = re[0]; res[1] = ""	; res[2] = ""; res[3] = ""; res[4] = "";
					}else if(re.length == 2){
						res[0] = re[0]; res[1] = re[1]	; res[2] = ""; res[3] = ""; res[4] = "";
					}else if(re.length == 3){
						res[0] = re[0]; res[1] = re[1]	; res[2] = re[2]; res[3] = ""; res[4] = "";
					}else if(re.length == 4){
						res[0] = re[0]; res[1] = re[1]	; res[2] = re[2]; res[3] = re[3]; res[4] = "";
					}
					v.add(res);
				}else{
					v.add(re);
				}
	    	}
		
		if(v.size() > 0 && v != null){
			// ---------------------------------判断告警 start
			try {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil
						.conversionToNodeDTO(ggsci);
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
					if ("ggscistatus"
							.equalsIgnoreCase(alarmIndicatorsNode
									.getName())) {
						for(int k = 0; k < v.size(); k++){
							String[] alarmhash = (String[])v.get(k);
							String states = (String)alarmhash[1];
							String ggscistatus = "1";
							String sIndex = "";
							sIndex = alarmhash[2];
							if(!states.equalsIgnoreCase("RUNNING")){
								ggscistatus = "-1";
								sIndex = alarmhash[2];
							}
							checkEventUtil.checkEvent(nodeDTO,
									alarmIndicatorsNode, ggscistatus, sIndex);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(v.size() > 0 && v != null){
			hashtable.put("ggsci", v);
			hashtable.put("collecttime", collecttime);
			hashtable.put("ipaddress", ipaddress);
			try {
				new LoadGgsciFile().addGgsci(hashtable);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return returnHash;
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
	
    public void deleteFile(String ipAddress){

			try
			{
				File delFile = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipAddress + ".log");
			//System.out.println("###开始删除文件："+delFile);
			//delFile.delete();
			System.out.println("###成功删除文件："+delFile);
			}
			catch(Exception e)		
			{}
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
    
    public static void main(String args[]){
    	StringBuffer fileContent = new StringBuffer();
		Nodeconfig nodeconfig = new Nodeconfig();
		String collecttime = "";
		
//		if(host == null)return null;
//		nodeconfig.setNodeid(host.getId());
//		nodeconfig.setHostname(host.getAlias());
    	try 
		{
    		//String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+".log";		
			String filename = "D:/10.41.1.111.ggsci.log";		
			
			System.out.println("====解析文件=="+filename);
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
//    		try{
//    			copyFile(ipaddress,getMaxNum(ipaddress));
//    		}catch(Exception e){
//    			e.printStackTrace();
//    		}
		} 
    	catch (Exception e)
		{
			e.printStackTrace();
		}

	    
	    String[] re1 = fileContent.toString().trim().split("\\n");
	    String result = fileContent.toString().trim().replaceAll(" " , "") ;
	    String[] re2 = result.split("\\n");
	//    System.out.println(result);
	    String timeStr=re1[0];
	    int psrBegin = 0;
	    int pscBegin = 0;
	    int pqBegin = 0;
	    int pcltBegin = 0;
	    for(int i =0;i<re2.length;i++)	{
	    	String test = re2[i].trim();
	    	if(test.equalsIgnoreCase("GGSCI(dazhanghcr1)1>"))
	    		psrBegin=i;
	    	if(test.equalsIgnoreCase("GGSCI(dazhanghcr1)2>"))
	    		pscBegin=i;
	    }
	
		Hashtable hashtable = new Hashtable();
		Vector v = new Vector();
	    Ggscicollectdata ggsciVo = new Ggscicollectdata();   
	    StringBuffer sb = new StringBuffer();
		for(int i = psrBegin + 2;i<pscBegin;i++){
			String value = re1[i];
			
			if(value == null || value.length() == 0){
				continue;
			}
			//String[] re=value.split("(" + (char)32 + "|" + (char)9 + ")+");
			String[] re=value.split("\\s++");
			if(re.length < 5){
				String[] res = new String[5];
				if(re.length == 1){
					res[0] = re[0]; res[1] = ""	; res[2] = ""; res[3] = ""; res[4] = "";
				}else if(re.length == 2){
					res[0] = re[0]; res[1] = re[1]	; res[2] = ""; res[3] = ""; res[4] = "";
				}else if(re.length == 3){
					res[0] = re[0]; res[1] = re[1]	; res[2] = re[2]; res[3] = ""; res[4] = "";
				}else if(re.length == 4){
					res[0] = re[0]; res[1] = re[1]	; res[2] = re[2]; res[3] = re[3]; res[4] = "";
				}
				v.add(res);
			}else{
				v.add(re);
			}
		}
		if(v.size() > 0 && v != null){
			hashtable.put("ggsci", v);
			hashtable.put("collecttime", collecttime);
			hashtable.put("ipaddress", "10.41.1.111");
			try {
				new LoadGgsciFile().addGgsci(hashtable);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
    }
    
    public boolean addGgsci(Hashtable returnhash) throws Exception {
		 
		String ipaddress = "";
		ipaddress = (String)returnhash.get("ipaddress");
		
		if(returnhash.containsKey("ggsci")){
			DBManager dbmanager = new DBManager();
			Vector ggsciVector = null;
			ggsciVector = (Vector)returnhash.get("ggsci");
			//Racstatuscollectdata vo = null;
			Hpdpcollectdata vo = null;
			if(ggsciVector != null && ggsciVector.size() > 0){
				try{
					String sql1 = "delete from nms_ggsci where ipaddress='" + ipaddress + "'";
					dbmanager.addBatch(sql1);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar tempCal = Calendar.getInstance();
					Date cc = tempCal.getTime();
					String montime = sdf.format(cc);
					String collecttime = (String)returnhash.get("collecttime");
					if(collecttime == null || collecttime.equals("")){
						collecttime = montime;
					}
					
					for(int i = 0; i < ggsciVector.size(); i++){
						String[] ggscihash = (String[])ggsciVector.get(i);
						String sql = "";
						try {
							 sql = "insert into nms_ggsci(ipaddress,programName,status1,group1,lagAtChkpt,timeSinceChkpt,collecttime) values('"
							 	+ ipaddress
								+ "','"
								+ ggscihash[0]
								+ "','"
								+ ggscihash[1]
								+ "','"
								+ ggscihash[2]
								+ "','"
								+ ggscihash[3]
								+ "','"
								+  ggscihash[4]
								+ "','"
								+ collecttime + "')";
							dbmanager.addBatch(sql);
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						} 
					}
					dbmanager.executeBatch();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					dbmanager.close();
				}
			}
		}
		return true;
	}
    public static void main1(String args[]){
    	String fileContent = "";
    	String[] re1 = fileContent.toString().trim().split("\\n");
	    String result = fileContent.toString().trim().replaceAll(" " , "") ;
	    String[] re2 = result.split("\\n");
	//    System.out.println(result);
	    int psrBegin = 0;
	    int pscBegin = 0;
	    for(int i =0;i<re2.length;i++)	{
	    	String test = re2[i].trim();
	    	if(test.equalsIgnoreCase("GGSCI(dazhanghcr1)1>"))
	    		psrBegin=i;
	    	if(test.equalsIgnoreCase("GGSCI(dazhanghcr1)2>"))
	    		pscBegin=i;
	    }
	
		Hashtable hashtable = new Hashtable();
		Vector v = new Vector();
	    Ggscicollectdata ggsciVo = new Ggscicollectdata();   
		for(int i = psrBegin + 2;i<pscBegin;i++){
			String value = re1[i];
			
			if(value == null || value.length() == 0){
				continue;
			}
			//String[] re=value.split("(" + (char)32 + "|" + (char)9 + ")+");
			String[] re=value.split("\\s++");
			if(re.length < 5){
				String[] res = new String[5];
				if(re.length == 1){
					res[0] = re[0]; res[1] = ""	; res[2] = ""; res[3] = ""; res[4] = "";
				}else if(re.length == 2){
					res[0] = re[0]; res[1] = re[1]	; res[2] = ""; res[3] = ""; res[4] = "";
				}else if(re.length == 3){
					res[0] = re[0]; res[1] = re[1]	; res[2] = re[2]; res[3] = ""; res[4] = "";
				}else if(re.length == 4){
					res[0] = re[0]; res[1] = re[1]	; res[2] = re[2]; res[3] = re[3]; res[4] = "";
				}
				v.add(res);
			}else{
				v.add(re);
			}
		}
    }
}
