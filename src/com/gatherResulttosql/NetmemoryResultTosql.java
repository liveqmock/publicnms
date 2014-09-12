package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Memorycollectdata;
import com.gatherdb.GathersqlListManager;

public class NetmemoryResultTosql {
	
	
	/**
	 * 
	 * 把采集数据生成sql放入的内存列表中
	 */
	public void CreateResultTosql(Hashtable ipdata,String ip)
	{
	
	if(ipdata.containsKey("memory")){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String allipstr = SysUtil.doip(ip);
		
		Vector memoryVector = (Vector) ipdata.get("memory");
		if (memoryVector != null && memoryVector.size() > 0) {
			String tablename = "memory" + allipstr;
			Memorycollectdata memorydata=null;
			for (int si = 0; si < memoryVector.size(); si++) {
				memorydata = (Memorycollectdata) memoryVector.elementAt(si);
				if (memorydata.getSubentity().equals("unknown")) {
					//未采集到数据，不存储
					return;
					}
				if (memorydata.getRestype().equals("dynamic")) {
					Calendar tempCal = (Calendar) memorydata.getCollecttime();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
										
					long count = 0;
					if(memorydata.getCount() != null){
						count = memorydata.getCount();
					}
					StringBuffer sqlBuffer = new StringBuffer();
					sqlBuffer.append("insert into ");
					sqlBuffer.append(tablename);
					sqlBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
					sqlBuffer.append("values('");
					sqlBuffer.append(ip);
					sqlBuffer.append("','");
					sqlBuffer.append(memorydata.getRestype());
					sqlBuffer.append("','");
					sqlBuffer.append(memorydata.getCategory());
					sqlBuffer.append("','");
					sqlBuffer.append(memorydata.getEntity());
					sqlBuffer.append("','");
					sqlBuffer.append(memorydata.getSubentity());
					sqlBuffer.append("','");
					sqlBuffer.append(memorydata.getUnit());
					sqlBuffer.append("','");
					sqlBuffer.append(memorydata.getChname());
					sqlBuffer.append("','");
					sqlBuffer.append(memorydata.getBak());
					sqlBuffer.append("','");
					sqlBuffer.append(count);
					sqlBuffer.append("','");
					sqlBuffer.append(memorydata.getThevalue());
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sqlBuffer.append("','");
						sqlBuffer.append(time);
						sqlBuffer.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sqlBuffer.append("',");
				    	sqlBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
				    	sqlBuffer.append(")");
				    }else if("dm".equalsIgnoreCase(SystemConstant.DBType)){
				    	sqlBuffer.append("',");
				    	sqlBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
				    	sqlBuffer.append(")");
				    }
					GathersqlListManager.Addsql(sqlBuffer.toString());
					sqlBuffer = null;
				}
				memorydata = null;
			}
			tablename = null;
		}
		
		memoryVector = null;
	}
	
	}
	

}
