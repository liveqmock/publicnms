package com.afunms.inform.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.*;
import com.afunms.inform.model.*;

public class PerformanceDao extends BaseDao
{
   private final static int topn = 5;
   
   public PerformanceDao()
   {
	   super("");
   }
   
   private List getTopN(String sql)
   {
	   List list = new ArrayList();
	   try
	   {
		   rs = conn.executeQuery(sql);
		   int i = 1;
		   while(rs.next())
		   {
			   Performance vo = new Performance();
			   vo.setIpAddress(rs.getString("ip_address"));
			   vo.setValue(rs.getDouble("value"));
			   vo.setId(rs.getString("id"));
			   list.add(vo);
			   i++;
			   if(i>topn) break; 
		   }
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("PerformanceDao.getAvgTopN()",e); 
	   }
	   return list;
   }
   
   /**
    * cpu利用率最高的N台服务器
    */
   public List getServerCpuTopN(String day)
   {
	   String sql = "select a.ip_address,a.id,b.node_id,ROUND(avg(b.percentage),1) value from " + 
	   "topo_host_node a,topo_node_multi_data b where substring(b.log_time,1,10)='" +
	   day + "' and b.moid='001001' and a.id=b.node_id group by b.node_id order by value desc";
	   
       return getTopN(sql);
   }

   /**
    * 内存利用率最高的N台服务器
    */
   public List getServerMemoryTopN(String day)
   {
	   String sql = "select a.ip_address,a.id,b.node_id,ROUND(avg(b.value),1) value from " + 
	   "topo_host_node a,topo_node_single_data b where substring(b.log_time,1,10)='" +
	   day + "' and b.moid='001002' and a.id=b.node_id group by b.node_id order by value desc";
	   return getTopN(sql);
   }

   /**
    * 硬盘利用率最高的N台服务器
    */
   public List getServerDiskTopN(String day)
   {
	   String sql = "select a.ip_address,a.id,b.node_id,ROUND(avg(b.percentage),1) value from " + 
	   "topo_host_node a,topo_node_multi_data b where substring(b.log_time,1,10)='" +
	   day + "' and b.moid='001003' and a.id=b.node_id group by b.node_id order by value desc";
	   return getTopN(sql);
   }
   
   /**
    * 接口利用率最高的N台网络设备
    */
   public List getNetworkTrafficTopN(String day)
   {
	   String sql = "select a.ip_address,a.id,b.node_id,ROUND(avg(b.percentage),1) value " +
	   "from topo_host_node a,topo_interface_data b where substring(b.log_time,1,10)='" +
	   day + "' and (b.moid='003002' or b.moid='003003') and a.id=b.node_id " + 
	   " and a.category <4 group by b.node_id order by value desc";
	   return getTopN(sql);
   }
   
   /**
    * CPU利用率最高的N台网络设备
    */
   public List getNetworkCpuTopN(String beginDate)
   {
	   String sql = "select a.ip_address,a.id,b.node_id,ROUND(avg(b.value),1) value from " + 
	   "topo_host_node a,topo_node_single_data b where substring(b.log_time,1,10)='" +
	   beginDate + "' and b.moid='002001' and a.id=b.node_id group by b.node_id order by value desc";
	   return getTopN(sql);
   }

   /**
    * 内存利用率最高的N台网络设备
    */
   public List getNetworkMemoryTopN(String beginDate)
   {
	   String sql = "select a.ip_address,a.id,b.node_id,ROUND(avg(b.value),1) value from " + 
	   "topo_host_node a,topo_node_single_data b where substring(b.log_time,1,10)='" +
	   beginDate + "' and b.moid='002002' and a.id=b.node_id group by b.node_id order by value desc";
	   return getTopN(sql);
   }
   
   /**
    * 删除某天的数据
    */
   public void deleteData()
   {
	   conn.addBatch("delete from topo_interface_data where substring(log_time,1,10)='" + SysUtil.getDate(-20) + "'");	   	   
	   conn.addBatch("delete from topo_node_multi_data where substring(log_time,1,10)='" + SysUtil.getDate(-60) + "'");
	   conn.addBatch("delete from topo_node_single_data where substring(log_time,1,10)='" + SysUtil.getDate(-60) + "'");	   
	   conn.addBatch("delete from nms_alarm_message where substring(log_time,1,10)='" + SysUtil.getDate(-30) + "'");
	   conn.addBatch("delete from nms_ip_change where substring(log_time,1,10)='" + SysUtil.getDate(-5) + "'");
	   conn.addBatch("delete from nms_symantec where SUBSTRING(begintime,1,10)='" + SysUtil.getDate(-2) + "'");
	   conn.executeBatch();
	   conn.close();
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {	   
	   return null;
   }
}