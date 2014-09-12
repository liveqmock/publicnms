package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.gatherdb.GathersqlListManager;



public class NetinterfaceResultTosql{
	
	
	
	/**
	 * 
	 * 把采集数据生成sql放入的内存列表中
	 */
	public void CreateResultTosql(Hashtable ipdata,String ip)
	{
	
	
	if(ipdata.containsKey("interface")){
		
		//Hashtable interfacehash = (Hashtable)ipdata.get("interface");
		//将当前采集到的接口数据入库
//		ProcessNetData processnetdata = new ProcessNetData();
//		try{
//			processnetdata.processInterfaceData(host.getId()+"", ip, type, nodeDTO.getSubtype(), interfacehash);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		processnetdata = null;
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String allipstr = SysUtil.doip(ip);
		
		//端口状态信息入库
		Vector interfaceVector = (Vector) ipdata.get("interface");
		if (interfaceVector.size() == 1 && ((Interfacecollectdata)interfaceVector.get(0)).getSubentity().equals("unknown")) {
			System.out.println("------没有采集到ip为 " + ip + " 的interface数据");
			return;//没有采集到数据
		}
		Calendar tempCal = null;
		Date cc = null;
		//StringBuffer sBuffer = null;
		String time = null;
		if (interfaceVector != null && interfaceVector.size() > 0) {	    							
			String tablename = "portstatus"+allipstr;
			//PortconfigDao dao=new PortconfigDao();
			//List portlist = null; 
			//if(ShareData.getPortConfigHash().containsKey(ip)) portlist = (List)ShareData.getPortConfigHash().get(ip);
			//Vector vector=dao.getSmsByIp(ip);//接口索引
			if (ShareData.getPortConfigHash().get(ip)!=null&&((List)ShareData.getPortConfigHash().get(ip)).size()>0) {								
			try{
				Hashtable indexhash = new Hashtable();
				for(int k=0;k<((List)ShareData.getPortConfigHash().get(ip)).size();k++){
					com.afunms.config.model.Portconfig portconfig = (com.afunms.config.model.Portconfig)((List)ShareData.getPortConfigHash().get(ip)).get(k);
					indexhash.put(portconfig.getPortindex()+"", portconfig.getPortindex());
				}
				
				Interfacecollectdata interfacedata = null;
				
				for (int i = 0; i < interfaceVector.size(); i++) {
					interfacedata = (Interfacecollectdata) interfaceVector.elementAt(i);
					if (indexhash.containsKey(interfacedata.getSubentity())&&interfacedata.getEntity().equals("ifOperStatus")) {
						tempCal = (Calendar) interfacedata.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						tempCal = null;
						cc = null;
							long count = 0;
							if(interfacedata.getCount() != null){
								count = interfacedata.getCount();
							}
							StringBuffer sBuffer = new StringBuffer(150);
						    sBuffer.append("insert into ");
						    sBuffer.append(tablename);
						    sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
						    sBuffer.append("values('");
						    sBuffer.append(ip);
						    sBuffer.append("','");
						    sBuffer.append(interfacedata.getRestype());
						    sBuffer.append("','");
						    sBuffer.append(interfacedata.getCategory());
						    sBuffer.append("','");
						    sBuffer.append(interfacedata.getEntity());
						    sBuffer.append("','");
						    sBuffer.append(interfacedata.getSubentity());
						    sBuffer.append("','");
						    sBuffer.append(interfacedata.getUnit());
						    sBuffer.append("','");
						    sBuffer.append(interfacedata.getChname());
						    sBuffer.append("','");
						    sBuffer.append(interfacedata.getBak());
						    sBuffer.append("','");
						    sBuffer.append(count);
						    sBuffer.append("','");
						    sBuffer.append(interfacedata.getThevalue());
						    
						    if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						    	sBuffer.append("','");
							    sBuffer.append(time);
							    sBuffer.append("')");
						    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						    	sBuffer.append("',");
							    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
							    sBuffer.append(")");
						    }else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
						    	sBuffer.append("',");
							    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
							    sBuffer.append(")");
						    }
						    
						    
						    //SysLogger.info(sBuffer.toString());
						    GathersqlListManager.Addsql(sBuffer.toString());
						    sBuffer = null;
						    
					}
					interfacedata = null;	
				}
			}catch(Exception e){	
				e.printStackTrace();
			}
			}
		}
		interfaceVector = null;
		
		//
		Vector allutilhdxVector = (Vector) ipdata.get("allutilhdx");
		if (allutilhdxVector != null && allutilhdxVector.size() > 0) {
			AllUtilHdx allutilhdx = null;
			String tablename = "allutilhdx" + allipstr;
			for (int si = 0; si < allutilhdxVector.size(); si++) {
				
					allutilhdx = (AllUtilHdx) allutilhdxVector.elementAt(si);
					if (allutilhdx.getRestype().equals("dynamic")) {
						if (allutilhdx.getThevalue().equals("0"))
							continue;
						tempCal = (Calendar) allutilhdx.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						
						long count = 0;
						if(allutilhdx.getCount() != null){
							count = allutilhdx.getCount();
						}
						StringBuffer sBuffer = new StringBuffer(200);
						sBuffer.append("insert into ");
						sBuffer.append(tablename);
						sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(allutilhdx.getRestype());
						sBuffer.append("','");
						sBuffer.append(allutilhdx.getCategory());
						sBuffer.append("','");
						sBuffer.append(allutilhdx.getEntity());
						sBuffer.append("','");
						sBuffer.append(allutilhdx.getSubentity());
						sBuffer.append("','");
						sBuffer.append(allutilhdx.getUnit());
						sBuffer.append("','");
						sBuffer.append(allutilhdx.getChname());
						sBuffer.append("','");
						sBuffer.append(allutilhdx.getBak());
						sBuffer.append("','");
						sBuffer.append(count);
						sBuffer.append("','");
						sBuffer.append(allutilhdx.getThevalue());
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("','");
						    sBuffer.append(time);
						    sBuffer.append("')");
					    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }
						 GathersqlListManager.Addsql(sBuffer.toString());
						 sBuffer = null;
						 allutilhdx=null;
					   
				
			}
			
		
		}
		//allutilhdxVector = null;
	}
		//UtilHdxPerc
		Vector utilhdxpercVector = (Vector) ipdata.get("utilhdxperc");
		if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
			String tablename = "utilhdxperc" + allipstr;
			UtilHdxPerc utilhdxperc = null;
			for (int si = 0; si < utilhdxpercVector.size(); si++) {
				
				utilhdxperc = (UtilHdxPerc) utilhdxpercVector.elementAt(si);
				if (utilhdxperc.getRestype().equals("dynamic")) {
					if (utilhdxperc.getThevalue().equals("0")|| utilhdxperc.getThevalue().equals("0.0"))
						continue;
					tempCal = (Calendar) utilhdxperc.getCollecttime();
					cc = tempCal.getTime();
					time = sdf.format(cc);
					long count = 0;
					if(utilhdxperc.getCount() != null){
						count = utilhdxperc.getCount();
					}
					StringBuffer sBuffer = new StringBuffer();
					sBuffer.append("insert into ");
					sBuffer.append(tablename);
					sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(utilhdxperc.getRestype());
					sBuffer.append("','");
					sBuffer.append(utilhdxperc.getCategory());
					sBuffer.append("','");
					sBuffer.append(utilhdxperc.getEntity());
					sBuffer.append("','");
					sBuffer.append(utilhdxperc.getSubentity());
					sBuffer.append("','");
					sBuffer.append(utilhdxperc.getUnit());
					sBuffer.append("','");
					sBuffer.append(utilhdxperc.getChname());
					sBuffer.append("','");
					sBuffer.append(utilhdxperc.getBak());
					sBuffer.append("','");
					sBuffer.append(count);
					sBuffer.append("','");
					sBuffer.append(utilhdxperc.getThevalue());
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
				    	sBuffer.append("','");
					    sBuffer.append(time);
					    sBuffer.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sBuffer.append("',");
					    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
					    sBuffer.append(")");
				    }else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
				    	sBuffer.append("',");
					    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
					    sBuffer.append(")");
				    }
					//System.out.println(sql);						
					GathersqlListManager.Addsql(sBuffer.toString());
					    
				utilhdxperc = null;
				sBuffer=null;							
			}
			
		}
		//utilhdxpercVector = null;
		}
		
		//UtilHdx
		Vector utilhdxVector = (Vector) ipdata.get("utilhdx");
		if (utilhdxVector != null && utilhdxVector.size() > 0) {
			String tablename = "utilhdx" + allipstr;
			UtilHdx utilhdx = null;
			for (int si = 0; si < utilhdxVector.size(); si++) {
				
					utilhdx = (UtilHdx) utilhdxVector.elementAt(si);
					if (utilhdx.getRestype().equals("dynamic")) {
						if (utilhdx.getThevalue().equals("0"))
							continue;
						tempCal = (Calendar) utilhdx.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						long count = 0;
						if(utilhdx.getCount() != null){
							count = utilhdx.getCount();
						}
						StringBuffer sBuffer = new StringBuffer();
						sBuffer.append("insert into ");
						sBuffer.append(tablename);
						sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(utilhdx.getRestype());
						sBuffer.append("','");
						sBuffer.append(utilhdx.getCategory());
						sBuffer.append("','");
						sBuffer.append(utilhdx.getEntity());
						sBuffer.append("','");
						sBuffer.append(utilhdx.getSubentity());
						sBuffer.append("','");
						sBuffer.append(utilhdx.getUnit());
						sBuffer.append("','");
						sBuffer.append(utilhdx.getChname());
						sBuffer.append("','");
						sBuffer.append(utilhdx.getBak());
						sBuffer.append("','");
						sBuffer.append(count);
						sBuffer.append("','");
						sBuffer.append(utilhdx.getThevalue());
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("','");
						    sBuffer.append(time);
						    sBuffer.append("')");
					    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }
						GathersqlListManager.Addsql(sBuffer.toString());
				sBuffer = null;
				
				//utilhdx = null;    								
			}
			
		}
		//utilhdxVector = null;
		}
		//discardsperc
		Vector discardspercVector = (Vector) ipdata.get("discardsperc");
		if (discardspercVector != null && discardspercVector.size() > 0) 
		{
			DiscardsPerc discardsperc = null;
			 String tablename = "discardsperc" + allipstr;
			for (int si = 0; si < discardspercVector.size(); si++) {
				
					discardsperc = (DiscardsPerc) discardspercVector.elementAt(si);
					if (discardsperc.getRestype().equals("dynamic")) {
						if (discardsperc.getThevalue().equals("0.0"))
							continue;
						tempCal = (Calendar) discardsperc.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						long count = 0;
						if(discardsperc.getCount() != null){
							count = discardsperc.getCount();
						}
						StringBuffer sBuffer = new StringBuffer();
						sBuffer.append("insert into ");
						sBuffer.append(tablename);
						sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(discardsperc.getRestype());
						sBuffer.append("','");
						sBuffer.append(discardsperc.getCategory());
						sBuffer.append("','");
						sBuffer.append(discardsperc.getEntity());
						sBuffer.append("','");
						sBuffer.append(discardsperc.getSubentity());
						sBuffer.append("','");
						sBuffer.append(discardsperc.getUnit());
						sBuffer.append("','");
						sBuffer.append(discardsperc.getChname());
						sBuffer.append("','");
						sBuffer.append(discardsperc.getBak());
						sBuffer.append("','");
						sBuffer.append(count);
						sBuffer.append("','");
						sBuffer.append(discardsperc.getThevalue());
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("','");
						    sBuffer.append(time);
						    sBuffer.append("')");
					    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }
						GathersqlListManager.Addsql(sBuffer.toString());
				sBuffer = null;
				
				//discardsperc = null;
			}
			
		}
		//discardspercVector = null;
	   }

		//errorsperc
		Vector errorspercVector = (Vector) ipdata.get("errorsperc");
		if (errorspercVector != null && errorspercVector.size() > 0) {
			ErrorsPerc errorsperc = null;
			String tablename = "errorsperc" + allipstr;
			for (int si = 0; si < errorspercVector.size(); si++) {
				
					errorsperc = (ErrorsPerc) errorspercVector.elementAt(si);
					if (errorsperc.getRestype().equals("dynamic")) {
						if (errorsperc.getThevalue().equals("0.0"))
							continue;
						tempCal = (Calendar) errorsperc.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						long count = 0;
						if(errorsperc.getCount() != null){
							count = errorsperc.getCount();
						}
						StringBuffer sBuffer = new StringBuffer();
						sBuffer.append("insert into ");
						sBuffer.append(tablename);
						sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(errorsperc.getRestype());
						sBuffer.append("','");
						sBuffer.append(errorsperc.getCategory());
						sBuffer.append("','");
						sBuffer.append(errorsperc.getEntity());
						sBuffer.append("','");
						sBuffer.append(errorsperc.getSubentity());
						sBuffer.append("','");
						sBuffer.append(errorsperc.getUnit());
						sBuffer.append("','");
						sBuffer.append(errorsperc.getChname());
						sBuffer.append("','");
						sBuffer.append(errorsperc.getBak());
						sBuffer.append("','");
						sBuffer.append(count);
						sBuffer.append("','");
						sBuffer.append(errorsperc.getThevalue());
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("','");
						    sBuffer.append(time);
						    sBuffer.append("')");
					    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }
						GathersqlListManager.Addsql(sBuffer.toString());		
						sBuffer = null;
					}
				
				
				time = null;
				//errorsperc = null;
			}
			
		
		//errorspercVector = null;
	  }
		//packs
		Vector packsVector = (Vector) ipdata.get("packs");
		if (packsVector != null && packsVector.size() > 0) {
			String tablename = "packs" + allipstr;
			Packs packs = null;
			for (int si = 0; si < packsVector.size(); si++) {
				
					packs = (Packs) packsVector.elementAt(si);
					if (packs.getRestype().equals("dynamic")) {
						if (packs.getThevalue().equals("0"))
							continue;
						tempCal = (Calendar) packs.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						long count = 0;
						if(packs.getCount() != null){
							count = packs.getCount();
						}
						StringBuffer sBuffer = new StringBuffer();
						sBuffer.append("insert into ");
						sBuffer.append(tablename);
						sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(packs.getRestype());
						sBuffer.append("','");
						sBuffer.append(packs.getCategory());
						sBuffer.append("','");
						sBuffer.append(packs.getEntity());
						sBuffer.append("','");
						sBuffer.append(packs.getSubentity());
						sBuffer.append("','");
						sBuffer.append(packs.getUnit());
						sBuffer.append("','");
						sBuffer.append(packs.getChname());
						sBuffer.append("','");
						sBuffer.append(packs.getBak());
						sBuffer.append("','");
						sBuffer.append(count);
						sBuffer.append("','");
						sBuffer.append(packs.getThevalue());
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("','");
						    sBuffer.append(time);
						    sBuffer.append("')");
					    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }
														
						GathersqlListManager.Addsql(sBuffer.toString());
				
				       sBuffer = null;
				
				       //packs = null;
			}
			
		}
		//packsVector = null;
		}
		//inpacks
		Vector inpacksVector = (Vector) ipdata.get("inpacks");
		if (inpacksVector != null && inpacksVector.size() > 0) {
			String tablename = "inpacks" + allipstr;
			InPkts packs = null;
			for (int si = 0; si < inpacksVector.size(); si++) {
				
					packs = (InPkts) inpacksVector.elementAt(si);
					if (packs.getRestype().equals("dynamic")) {
						if (packs.getThevalue().equals("0"))
							continue;
						tempCal = (Calendar) packs.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						
						long count = 0;
						if(packs.getCount() != null){
							count = packs.getCount();
						}
						StringBuffer sBuffer = new StringBuffer();
						sBuffer.append("insert into ");
						sBuffer.append(tablename);
						sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(packs.getRestype());
						sBuffer.append("','");
						sBuffer.append(packs.getCategory());
						sBuffer.append("','");
						sBuffer.append(packs.getEntity());
						sBuffer.append("','");
						sBuffer.append(packs.getSubentity());
						sBuffer.append("','");
						sBuffer.append(packs.getUnit());
						sBuffer.append("','");
						sBuffer.append(packs.getChname());
						sBuffer.append("','");
						sBuffer.append(packs.getBak());
						sBuffer.append("','");
						sBuffer.append(count);
						sBuffer.append("','");
						sBuffer.append(packs.getThevalue());
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("','");
						    sBuffer.append(time);
						    sBuffer.append("')");
					    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }
						GathersqlListManager.Addsql(sBuffer.toString());
				        sBuffer = null;
				        //packs = null;
			}
			
		}
		//inpacksVector = null;
		}
		//outpacks
		Vector outpacksVector = (Vector) ipdata.get("outpacks");
		if (outpacksVector != null && outpacksVector.size() > 0) {
			String tablename = "outpacks" + allipstr;
			OutPkts packs = null;
			for (int si = 0; si < outpacksVector.size(); si++) {
				
					packs = (OutPkts) outpacksVector.elementAt(si);
					if (packs.getRestype().equals("dynamic")) {
						if (packs.getThevalue().equals("0"))
							continue;
						tempCal = (Calendar) packs.getCollecttime();
						cc = tempCal.getTime();
						time = sdf.format(cc);
						long count = 0;
						if(packs.getCount() != null){
							count = packs.getCount();
						}
						StringBuffer sBuffer = new StringBuffer();
						sBuffer.append("insert into ");
						sBuffer.append(tablename);
						sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(packs.getRestype());
						sBuffer.append("','");
						sBuffer.append(packs.getCategory());
						sBuffer.append("','");
						sBuffer.append(packs.getEntity());
						sBuffer.append("','");
						sBuffer.append(packs.getSubentity());
						sBuffer.append("','");
						sBuffer.append(packs.getUnit());
						sBuffer.append("','");
						sBuffer.append(packs.getChname());
						sBuffer.append("','");
						sBuffer.append(packs.getBak());
						sBuffer.append("','");
						sBuffer.append(count);
						sBuffer.append("','");
						sBuffer.append(packs.getThevalue());
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("','");
						    sBuffer.append(time);
						    sBuffer.append("')");
					    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					    	sBuffer.append("',");
						    sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
						    sBuffer.append(")");
					    }

					    GathersqlListManager.Addsql(sBuffer.toString());
				       sBuffer = null;
				       //packs = null;
			}
		
		}
		//outpacksVector = null;
		}
		
		
	
	}
	
	
	}
	

}
