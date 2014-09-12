/**
 * <p>Description:polling data pool</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-28
 */

package com.afunms.polling;

import java.util.*;

import com.afunms.common.util.*;
import com.afunms.monitor.item.base.*;
import com.afunms.inform.model.Alarm;

public class PollingDao
{        
    private static int bufferDataNum = 0;
    private static DBManager conn;
	private static PollingDao instance = new PollingDao();
    public static PollingDao getInstance()
    {       
        return instance;       
    }

    private PollingDao()
    {
    	conn = new DBManager();
    }    
    
    public synchronized void addAlarmInfo(List alarmInfoList)
    {
    	for(int i=0;i<alarmInfoList.size();i++)
    	{
    		Alarm vo = (Alarm)alarmInfoList.get(i);    		    	   
		    StringBuffer sql = new StringBuffer(100);
		    sql.append("insert into nms_alarm_message(id,ip_address,message,alarm_level,category,log_time)values('");
		    sql.append((new java.util.Date()).getTime());
		    sql.append("','");
			sql.append(vo.getIpAddress());
			sql.append("','");
			sql.append(vo.getMessage());
			sql.append("',");
			sql.append(vo.getLevel());
			sql.append(",");
			sql.append(vo.getCategory());
			sql.append(",'");
			sql.append(vo.getLogTime());
			sql.append("')");
			add2Buffer(sql.toString());			
			sql = null;
    	}
    }
    
    public synchronized void addItemInfo(int nodeId,MonitoredItem item)
    {
       if(item.getResultType()==1)
       {
    	   if(item.getSingleResult()< 0) return; //不正常的值不入库
    	   
    	   StringBuffer sql = new StringBuffer(200);
    	   sql.append("insert into topo_node_single_data(node_id,moid,value,log_time)values(");
    	   sql.append(nodeId);
    	   sql.append(",'");
    	   sql.append(item.getMoid());
    	   sql.append("',");
    	   sql.append(item.getSingleResult());
    	   sql.append(",'");
    	   sql.append(SysUtil.longToTime(item.getLastTime()));
    	   sql.append("')");
    	   add2Buffer(sql.toString());
    	   sql = null;
       }
       else
       {
    	   List resultList = item.getMultiResults();       
    	   if(resultList==null||resultList.size()==0) return;
    	       	   
       	   for(int j=0;j<resultList.size();j++)
       	   {	   
       		  MonitorResult mr = (MonitorResult)resultList.get(j);
       		  if(mr.getPercentage() < 0 || mr.getPercentage() > 100 || mr.getValue() < 0) continue; //不正常的值不入库
       		
       		  StringBuffer sql = new StringBuffer(200);
       		  if(item.getMoid().startsWith("003")) //是traffic数据,插入nms_interface_data表
       		  {
     	         sql.append("insert into topo_interface_data(node_id,entity,moid,value,percentage,log_time)values(");
    	         sql.append(nodeId);
    	    	 sql.append(",'");
    	         sql.append(mr.getEntity());
    	         sql.append("','");
			     sql.append(item.getMoid());
			     sql.append("',");
			     sql.append(mr.getValue());
			     sql.append(",");
			     sql.append(mr.getPercentage());
			     sql.append(",'");
			     sql.append(SysUtil.longToTime(item.getLastTime()));
			     sql.append("')"); 
       		  }		
       		  else
       		  {		         		     
    	         sql.append("insert into topo_node_multi_data(node_id,entity,moid,value,percentage,log_time)values(");
    	         sql.append(nodeId);
    	    	 sql.append(",'");
    	         sql.append(mr.getEntity());
    	         sql.append("','");
			     sql.append(item.getMoid());
			     sql.append("',");
			     sql.append(mr.getValue());
			     sql.append(",");
			     sql.append(mr.getPercentage());
			     sql.append(",'");
			     sql.append(SysUtil.longToTime(item.getLastTime()));
			     sql.append("')");			     
       		  }   
       		  add2Buffer(sql.toString());
       		  sql = null;
       	   }//end_for_j  
       }//end_if        	      
    }
    
    private synchronized void add2Buffer(String sql)
    {    	
	    conn.addBatch(sql); 	    
	    bufferDataNum ++;
	    if(bufferDataNum>50)  //每50条数据写一次
	    {	    		
		    bufferDataNum = 0;   		
	        conn.executeBatch();
	    }
    }     
}