package com.gatherResulttosql;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Interfacecollectdata;
import com.gatherdb.GathersqlListManager;

public class NetTemperatureResultTosql {
	
	
	/**
	 * 
	 * 把采集数据生成sql放入的内存列表中
	 */
	public void CreateResultTosql(Hashtable ipdata,String ip)
	{
	
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String allipstr = SysUtil.doip(ip);
		
	if(ipdata.containsKey("temperature")){
		//温度
		Vector temperatureVector = (Vector) ipdata.get("temperature");
		if (temperatureVector != null && temperatureVector.size() > 0) {
			//得到温度
			Interfacecollectdata temperdata = (Interfacecollectdata) temperatureVector.elementAt(0);
			if (temperdata.getRestype().equals("dynamic")) {
				Calendar tempCal = (Calendar) temperdata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "temper" + allipstr;
				long count = 0;
				if(temperdata.getCount() != null){
					count = temperdata.getCount();
				}
				StringBuffer sBuffer = new StringBuffer();
				sBuffer.append("insert into ");
				sBuffer.append(tablename);
				sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
				sBuffer.append("values('");
				sBuffer.append(ip);
				sBuffer.append("','");
				sBuffer.append(temperdata.getRestype());
				sBuffer.append("','");
				sBuffer.append(temperdata.getCategory());
				sBuffer.append("','");
				sBuffer.append(temperdata.getEntity());
				sBuffer.append("','");
				sBuffer.append(temperdata.getSubentity());
				sBuffer.append("','");
				sBuffer.append(temperdata.getUnit());
				sBuffer.append("','");
				sBuffer.append(temperdata.getChname());
				sBuffer.append("','");
				sBuffer.append(temperdata.getBak());
				sBuffer.append("','");
				sBuffer.append(count);
				sBuffer.append("','");
				sBuffer.append(temperdata.getThevalue());
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					sBuffer.append("','");
					sBuffer.append(time);
					sBuffer.append("')");
			    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			    	sBuffer.append("',");
			    	sBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
			    	sBuffer.append(")");
			    }
				//System.out.println(sql);
				GathersqlListManager.Addsql(sBuffer.toString());
				
				
				sBuffer = null;
			}
			temperdata = null;
		}
		temperatureVector = null;
	}
	
	
	}
	

}
