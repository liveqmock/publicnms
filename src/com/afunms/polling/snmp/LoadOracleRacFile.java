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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.config.model.Nodeconfig;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.om.oraclerac.Raccrstatuscollectdata;
import com.afunms.polling.om.oraclerac.Raccrstatusdata;
import com.afunms.polling.om.oraclerac.Raclistenerstatuscollectdata;
import com.afunms.polling.om.oraclerac.Racstatuscollectdata;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LoadOracleRacFile {
	/**
	 * @param hostname
	 */
	private String ipaddress;

	 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public LoadOracleRacFile(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	
	public LoadOracleRacFile() {
		
	}
	
	public Hashtable collect_Data(NodeGatherIndicators gatherIndicatorsNode)
    {

		Hashtable returnHash = new Hashtable();
		//Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(gatherIndicatorsNode.getNodeid()+""));
		
		List dbmonitorlists = new ArrayList();
		dbmonitorlists = ShareData.getDBList();
		DBVo dbmonitorlist = new DBVo();
		if (dbmonitorlists != null && dbmonitorlists.size() > 0) {
			for (int i = 0; i < dbmonitorlists.size(); i++) {
				DBVo vo = (DBVo) dbmonitorlists.get(i);
				if (vo.getId() == Integer.parseInt(gatherIndicatorsNode
						.getNodeid())) {
					dbmonitorlist = vo;
					break;
				}
			}
		}
		if(dbmonitorlist == null)return returnHash;
		
	
		//System.out.println("====解析文件==oracle rac++++++++++==");
		ipaddress=dbmonitorlist.getIpAddress();
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
		if(ipAllData == null)ipAllData = new Hashtable();
		//
		
		StringBuffer fileContent = new StringBuffer();
		Nodeconfig nodeconfig = new Nodeconfig();
		
		if(dbmonitorlist == null)return null;
    	try 
		{
    		String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+".rac.log";		
			//String filename = "D:/10.41.1.50.log";		
			
			System.out.println("====解析文件=="+filename);
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			String strLine = null;
    		//读入文件内容
    		while((strLine=br.readLine())!=null)
    		{
    			if (strLine.equals("")) {
					strLine = "|";// 这里
				}
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
    	String collecttime = "";
		tmpPt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:dbversion)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			collecttime = mr.group(2);
		}
		if (collecttime != null && collecttime.length()>0 && !collecttime.trim().equals("|")){
			collecttime = collecttime.trim();
		}else{
			collecttime = "";
		}
		/////log日志异常判断
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String lasttime = "";
		try {
			String sql = "select * from nms_oracle_racstatus where ipaddress = '" + ipaddress + "'";
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
//System.out.println(logstatus + "--logstatus");
			try {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil
						.conversionToNodeDTO(dbmonitorlist);
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
		String oracleVersion = "";
		tmpPt = Pattern.compile("(cmdbegin:dbversion)(.*)(cmdbegin:racstatus)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find()){
			oracleVersion = mr.group(2);
		}
		
		String racstatus = "";
		String racstatusC = "-1";
    	//Calendar date = Calendar.getInstance();
		tmpPt = Pattern.compile("(cmdbegin:racstatus)(.*)(cmdbegin:raclisterstatus)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			racstatus = mr.group(2);
		}
		String[] racstatusStr = racstatus.trim().split("\\|");
		String name = "";
		String type = "";
		String target = "";
		String state = "";
		Racstatuscollectdata vo = new Racstatuscollectdata();
		Vector racstatusVector = new Vector();
		if(racstatusStr != null && racstatusStr.length > 0){
			int b = 0;
			for(int i = 0; i < racstatusStr.length; i++){
				vo = new Racstatuscollectdata();
				String perData = racstatusStr[i];
				if(perData.equals("") || perData == null){
					continue;
				}
				String[] perDataArgs = perData.trim().split("\\n");
			
				name = perDataArgs[0].trim().substring(perDataArgs[0].indexOf("=") + 1, perDataArgs[0].length());
				type = perDataArgs[1].trim().substring(perDataArgs[1].indexOf("=") + 1, perDataArgs[1].length());
				target = perDataArgs[2].trim().substring(perDataArgs[2].indexOf("=") + 1, perDataArgs[2].length());
				state = perDataArgs[3].trim().substring(perDataArgs[3].indexOf("=") + 1, perDataArgs[3].length());
				
				vo.setIpaddress(ipaddress);
				vo.setName(name);
				vo.setType(type);
				vo.setTarget(target);
				vo.setState(state);
				vo.setCollecttime(collecttime);
				if(name.indexOf(".gsd") == -1 && !target.equalsIgnoreCase("ONLINE")){
					b++;
				}
				racstatusVector.add(vo);
			}
			if(b == 0){
				racstatusC = "1";
			}
		}
		
	//System.out.println(racstatusC + "--racstatusC");	
		String raccrstatus = "-1";
		String raclistenerstatus = "-1";
		
		if(oracleVersion.trim().equals("oracle version 11G")){
			
			//----------------解析raclisterstatus内容--创建监控项---------------------        	
			String raclisterstatusContent = "";
			tmpPt = Pattern.compile("(cmdbegin:raclisterstatus)(.*)(cmdbegin:raccrstatus\n)",Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if(mr.find())
			{
				raclisterstatusContent = mr.group(2);
			} 
			String[] raclistenerstatusLineArr = raclisterstatusContent.trim().split("\n");
			String listenerstatus = "";
			String listenerstatusrun = "";
			String listenerstatusnode = "";
			Raclistenerstatuscollectdata raclistenerstatusdata = new Raclistenerstatuscollectdata();
			
			if(raclistenerstatusLineArr != null && raclistenerstatusLineArr.length > 0){
				raclistenerstatusdata = new Raclistenerstatuscollectdata();
				listenerstatus = raclistenerstatusLineArr[0].trim().substring(raclistenerstatusLineArr[0].indexOf(" is") + 3,raclistenerstatusLineArr[0].length()).trim();
				listenerstatusrun = raclistenerstatusLineArr[1].trim().substring(raclistenerstatusLineArr[1].indexOf(" is") + 3,raclistenerstatusLineArr[1].indexOf("on")).trim();
				listenerstatusnode = raclistenerstatusLineArr[1].trim().substring(raclistenerstatusLineArr[1].indexOf(":") + 1,raclistenerstatusLineArr[1].length()).trim();
				raclistenerstatusdata.setIpaddress(ipaddress);
				raclistenerstatusdata.setListenerstatus(listenerstatus);
				raclistenerstatusdata.setListenerstatusrun(listenerstatusrun);
				raclistenerstatusdata.setListenerstatusnode(listenerstatusnode);
				raclistenerstatusdata.setCollecttime(collecttime);
				if(listenerstatusrun.trim().equalsIgnoreCase("running") && listenerstatus.trim().equalsIgnoreCase("enabled")){
					raclistenerstatus = "1";
				}
				returnHash.put("raclistenerstatus", raclistenerstatusdata);
			}
			//----------------解析raccrstatus内容--创建监控项---------------------        	
			String raccrstatusContent = "";
			tmpPt = Pattern.compile("(cmdbegin:raccrstatus)(.*)(cmdbegin:end)",Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if(mr.find())
			{
				raccrstatusContent = mr.group(2);
			} 
			Vector raccrstatusV = new Vector();
			if(raccrstatusContent != null && raccrstatusContent.length() > 0){
				Raccrstatuscollectdata raccrstatusTmp = new Raccrstatuscollectdata();
				Raccrstatusdata raccrstatusTemp = new Raccrstatusdata();
				
				String[] raccrstatusLineArr = raccrstatusContent.trim().split("\n");
				int b = 0;
				
				for(int i = 0; i < raccrstatusLineArr.length; i++){
					raccrstatusTemp = new Raccrstatusdata();
					raccrstatusTemp.setNumNo(raccrstatusLineArr[i].substring(0,raccrstatusLineArr[i].indexOf(":")));
					raccrstatusTemp.setIpaddress(ipaddress);
					raccrstatusTemp.setName(raccrstatusLineArr[i].substring(raccrstatusLineArr[i].indexOf(":") + 1,raccrstatusLineArr[i].indexOf("is")));
					raccrstatusTemp.setStatus(raccrstatusLineArr[i].substring(raccrstatusLineArr[i].indexOf("is") + 2,raccrstatusLineArr[i].length()));
					raccrstatusTemp.setCollecttime(collecttime);
					raccrstatusV.add(raccrstatusTemp);
					if(raccrstatusLineArr[i].indexOf("online") > -1){
						b++;
					}
				}
				if(b == raccrstatusLineArr.length){
					raccrstatus = "1";
				}
				raccrstatusTmp.setIpaddress(ipaddress);
				raccrstatusTmp.setRaccrstatus(raccrstatus);
				raccrstatusTmp.setCollecttime(collecttime);
				returnHash.put("raccrstatus", raccrstatusTmp);
				
			}
			
			if(raccrstatusV != null && raccrstatusV.size() > 0){
				returnHash.put("raccrstatusdata", raccrstatusV);
			}
			
		//	String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
			
			
			if(racstatusVector != null && racstatusVector.size() > 0){
				returnHash.put("racstatus",racstatusVector);
			}
			
		}else if(oracleVersion.trim().equals("oracle version 10G")){
			//----------------解析raclisterstatus内容--创建监控项---------------------        	
			String raclisterstatusContent = "";
			tmpPt = Pattern.compile("(cmdbegin:raclisterstatus)(.*)(cmdbegin:raccrstatus\n)",Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if(mr.find())
			{
				raclisterstatusContent = mr.group(2);
			} 
			String[] raclistenerstatusLineArr = raclisterstatusContent.trim().split("\n");
			if(raclistenerstatusLineArr != null && raclistenerstatusLineArr.length > 0){
				StringBuffer sb = new StringBuffer();
				String listenerstatus = "-1";
				String listenerstatusrun = "";
				String listenerstatusnode = "";
				int b = 0;
				for(int i = 0; i < raclistenerstatusLineArr.length; i++){
					String[] listener = (String[])raclistenerstatusLineArr[i].trim().split(",");
					String instance = listener[1];
					String status = listener[3];
					if(sb.indexOf(instance) > -1){
						continue;
					}
					if(!status.equalsIgnoreCase("READY")){
						b++;
					}
					String node = instance + ":" + status;
					if(sb.length() > 0){
						sb.append(" , ");
						sb.append(node);
						
					}else{
						sb.append(node);
					}
				}
				if(b == 0){
					raclistenerstatus = "1";
					listenerstatus = "1";
				}
				listenerstatusnode = sb.toString();
				Raclistenerstatuscollectdata raclistenerstatusdata = new Raclistenerstatuscollectdata();
				raclistenerstatusdata = new Raclistenerstatuscollectdata();
				raclistenerstatusdata.setIpaddress(ipaddress);
				raclistenerstatusdata.setListenerstatus(listenerstatus);
				raclistenerstatusdata.setListenerstatusrun(listenerstatusrun);
				raclistenerstatusdata.setListenerstatusnode(listenerstatusnode);
				raclistenerstatusdata.setCollecttime(collecttime);
				returnHash.put("raclistenerstatus", raclistenerstatusdata);
			}
			String raccrstatusContent = "";
			tmpPt = Pattern.compile("(cmdbegin:raccrstatus)(.*)(cmdbegin:end)",Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if(mr.find())
			{
				raccrstatusContent = mr.group(2);
			} 
			Vector raccrstatusV = new Vector();
			if(raccrstatusContent != null && raccrstatusContent.length() > 0){
				Raccrstatuscollectdata raccrstatusTmp = new Raccrstatuscollectdata();
				Raccrstatusdata raccrstatusTemp = new Raccrstatusdata();
				
				String[] raccrstatusLineArr = raccrstatusContent.trim().split("\n");
				int b = 0;
				
				for(int i = 0; i < raccrstatusLineArr.length; i++){
					String[] tmp = raccrstatusLineArr[i].trim().split("\\s++");
					raccrstatusTemp = new Raccrstatusdata();
					raccrstatusTemp.setNumNo("");
					raccrstatusTemp.setIpaddress(ipaddress);
					raccrstatusTemp.setName(tmp[0]);
					raccrstatusTemp.setStatus(tmp[1] + " " + tmp[2]);
					raccrstatusTemp.setCollecttime(collecttime);
					raccrstatusV.add(raccrstatusTemp);
					if(raccrstatusLineArr[i].indexOf("healthy") > -1){
						b++;
					}
				}
				if(b == raccrstatusLineArr.length){
					raccrstatus = "1";
				}
				raccrstatusTmp.setIpaddress(ipaddress);
				raccrstatusTmp.setRaccrstatus(raccrstatus);
				raccrstatusTmp.setCollecttime(collecttime);
				returnHash.put("raccrstatus", raccrstatusTmp);
				// ---------------------------------判断告警 start
			}
			
			if(raccrstatusV != null && raccrstatusV.size() > 0){
				returnHash.put("raccrstatusdata", raccrstatusV);
			}
			
		}
		// ---------------------------------判断告警 start
		try {
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil
					.conversionToNodeDTO(dbmonitorlist);
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
				if ("racstatus"
						.equalsIgnoreCase(alarmIndicatorsNode
								.getName())) {
						checkEventUtil.checkEvent(nodeDTO,
								alarmIndicatorsNode, raccrstatus, "raccrstatus");
						checkEventUtil.checkEvent(nodeDTO,
								alarmIndicatorsNode, raclistenerstatus, "raclistenerstatus");
						checkEventUtil.checkEvent(nodeDTO,
								alarmIndicatorsNode, racstatusC, "racstatus");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(racstatusVector != null && racstatusVector.size() > 0){
			returnHash.put("racstatus",racstatusVector);
		}
		if(returnHash.size() > 0 && returnHash != null){
			returnHash.put("collecttime",collecttime);
			returnHash.put("ipaddress", ipaddress);
			DBDao db = new DBDao();
			try {
				boolean b =db.addOracleRac(returnHash);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// ShareData.getSharedata().put(host.getIpAddress(), returnHash);
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
			//("###开始删除文件："+delFile);
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
    
}

