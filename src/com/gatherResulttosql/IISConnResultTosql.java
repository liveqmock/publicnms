package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.application.model.IISVo;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Memorycollectdata;
import com.gatherdb.GathersqlListManager;

public class IISConnResultTosql {
	
	
	/**
	 * 
	 * 把采集数据生成sql放入表中
	 */
	public void CreateResultTosql(List iisdata,String ip)
	{
	
	//if(ipdata.containsKey("memory")){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String allipstr = SysUtil.doip(ip);
		if (iisdata != null && iisdata.size() > 0) {
			Calendar date=Calendar.getInstance();
			Date cc = date.getTime();
			String time = sdf.format(cc);
			String tablename = "iisconn" + allipstr;
			IISVo vo=null;
			StringBuffer sqlBuffer = null;
			for (int si = 0; si < iisdata.size(); si++) {
				vo = (IISVo) iisdata.get(si);
				if(vo.getCurrentAnonymousUsers() != null && !"null".equalsIgnoreCase(vo.getCurrentAnonymousUsers())){
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("insert into ");
					sqlBuffer.append(tablename);
					sqlBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
					sqlBuffer.append("values('"+ip+"','','curanyusers','','curanyusers','','匿名连接的当前用户数','',0,'");
					sqlBuffer.append(vo.getCurrentAnonymousUsers());
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sqlBuffer.append("','");
						sqlBuffer.append(time);
						sqlBuffer.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sqlBuffer.append("',");
				    	sqlBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
				    	sqlBuffer.append(")");
				    }
					GathersqlListManager.Addsql(sqlBuffer.toString());
				}
				
				if(vo.getCurrentConnections() != null && !"null".equalsIgnoreCase(vo.getCurrentConnections())){
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("insert into ");
					sqlBuffer.append(tablename);
					sqlBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
					sqlBuffer.append("values('"+ip+"','','curconns','','curconns','','当前连接数','',0,'");
					sqlBuffer.append(vo.getCurrentConnections());
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sqlBuffer.append("','");
						sqlBuffer.append(time);
						sqlBuffer.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sqlBuffer.append("',");
				    	sqlBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
				    	sqlBuffer.append(")");
				    }
					GathersqlListManager.Addsql(sqlBuffer.toString());
				}
				
				if(vo.getConnectionAttempts() != null && !"null".equalsIgnoreCase(vo.getConnectionAttempts())){
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("insert into ");
					sqlBuffer.append(tablename);
					sqlBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
					sqlBuffer.append("values('"+ip+"','','connatmps','','connatmps','','尝试连接数','',0,'");
					sqlBuffer.append(vo.getConnectionAttempts());
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sqlBuffer.append("','");
						sqlBuffer.append(time);
						sqlBuffer.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sqlBuffer.append("',");
				    	sqlBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
				    	sqlBuffer.append(")");
				    }
					GathersqlListManager.Addsql(sqlBuffer.toString());
				}								
				
				if(vo.getLogonAttempts() != null && !"null".equalsIgnoreCase(vo.getLogonAttempts())){
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("insert into ");
					sqlBuffer.append(tablename);
					sqlBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
					sqlBuffer.append("values('"+ip+"','','logonatmps','','logonatmps','','尝试登陆数','',0,'");
					sqlBuffer.append(vo.getLogonAttempts());
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sqlBuffer.append("','");
						sqlBuffer.append(time);
						sqlBuffer.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sqlBuffer.append("',");
				    	sqlBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
				    	sqlBuffer.append(")");
				    }
					GathersqlListManager.Addsql(sqlBuffer.toString());
				}

				if(vo.getTotalNotFoundErrors() != null && !"null".equalsIgnoreCase(vo.getTotalNotFoundErrors())){
					tablename = "iiserr" + allipstr;
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("insert into ");
					sqlBuffer.append(tablename);
					sqlBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
					sqlBuffer.append("values('"+ip+"','','tnferrs','','tnferrs','','','',0,'");
					sqlBuffer.append(vo.getTotalNotFoundErrors());
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sqlBuffer.append("','");
						sqlBuffer.append(time);
						sqlBuffer.append("')");
				    }else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
				    	sqlBuffer.append("',");
				    	sqlBuffer.append("to_date('"+time+"','YYYY-MM-DD HH24:MI:SS')");
				    	sqlBuffer.append(")");
				    }
					GathersqlListManager.Addsql(sqlBuffer.toString());
				}

				
			}
			tablename = null;
		}
	//}
	
	}
	

}
