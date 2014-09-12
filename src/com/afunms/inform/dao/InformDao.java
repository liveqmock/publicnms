package com.afunms.inform.dao;

import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;
import com.afunms.inform.model.*;

public class InformDao extends BaseDao
{
	public InformDao()
	{
		super("");
	}
	
	public List queryServerPerformance(String day,String field)
	{
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer(200);
		sql.append("select topo_tbl.ip_long ip_long,topo_tbl.ip_address ip_address,topo_tbl.alias alias,cpu_tbl.id node_id,cpu_value,mem_value,disk_value from");
		sql.append(" (select * from topo_host_node where category=4) topo_tbl left join");
		/*cpu*/
		sql.append(" (select a.node_id id,ROUND(avg(a.percentage),1) cpu_value from topo_node_multi_data a");
		sql.append(" where a.moid in ('001001','004001') or a.moid='001001' and substring(a.log_time,1,10)='");
		sql.append(day);
		sql.append("' group by a.node_id) cpu_tbl ");
		sql.append(" on (topo_tbl.id=cpu_tbl.id) left join");
		/*mem*/
		sql.append(" (select b.node_id id,ROUND(avg(b.value),1) mem_value from topo_node_single_data b");
		sql.append(" where b.moid in('001002','004002') and substring(b.log_time,1,10)='");
		sql.append(day);
		sql.append("' group by b.node_id ) mem_tbl ");
		sql.append(" on (topo_tbl.id=mem_tbl.id) left join");
		/*disk*/ 
		sql.append(" (select c.node_id id,ROUND(avg(c.percentage),1) disk_value from topo_node_multi_data c");
		sql.append(" where c.moid in ('001003','004003') and substring(c.log_time,1,10)='");
		sql.append(day);
		sql.append("' group by c.node_id ) disk_tbl ");
		sql.append(" on (topo_tbl.id=disk_tbl.id)");
		if(field.equals("ip_long"))
	       sql.append(" order by " + field);
		else
		   sql.append(" order by " + field + " desc");

		try
		{	
			rs = conn.executeQuery(sql.toString());			
			while(rs.next())
			{
				ServerPerformance vo = new ServerPerformance();
				vo.setNodeId(rs.getInt("node_id"));
				vo.setIpAddress(rs.getString("ip_address"));
				vo.setAlias(rs.getString("alias"));
				vo.setCpuValue(rs.getFloat("cpu_value"));
				vo.setDiskValue(rs.getFloat("disk_value"));
				vo.setMemValue(rs.getFloat("mem_value"));
				list.add(vo);
			}						
		}
		catch(Exception ex)
		{
			SysLogger.error("Error in InformDao.queryServerPerformance()");
		}
		finally
		{
			conn.close();
		}		
		return list;
	}
	
	public List queryNetworkPerformance(String day,String field)
	{
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer(200);
		sql.append("select topo_tbl.ip_long ip_long,topo_tbl.ip_address ip_address,topo_tbl.alias alias,");
		sql.append(" cpu_tbl.id node_id,cpu_value,mem_value,if_util from ");
		sql.append(" topo_host_node topo_tbl left join" );
		sql.append(" (select c.node_id id,ROUND(avg(c.percentage),1) if_util"); 
		sql.append(" from topo_interface_data c,topo_host_node d where (c.moid='003003' or c.moid='003002')"); 
		sql.append(" and d.category<4 and c.node_id=d.id and substring(c.log_time,1,10)='" + day + "'" );
		sql.append(" group by c.node_id )  if_tbl on topo_tbl.id = if_tbl.id left JOIN");
		sql.append(" (select b.node_id id,ROUND(avg(b.value),1) mem_value" );
		sql.append(" from topo_node_single_data b where b.moid='002002' and substring(b.log_time,1,10)='" + day + "'"); 
		sql.append(" group by b.node_id )  mem_tbl on mem_tbl.id = topo_tbl.id left join");
		sql.append("(select a.node_id id,ROUND(avg(a.value),1) cpu_value" );
		sql.append(" from topo_node_single_data a where a.moid='002001' and substring(a.log_time,1,10)='" + day + "'"); 
		sql.append(" group by a.node_id)  cpu_tbl on cpu_tbl.id = topo_tbl.id ");
		sql.append(" where topo_tbl.category<4 ");
		if(field.equals("ip_long"))
		   sql.append(" order by " + field);
		else
		   sql.append(" order by " + field + " desc");

		try
		{
			rs = conn.executeQuery(sql.toString());			
			while(rs.next())
			{
				NetworkPerformance vo = new NetworkPerformance();
				vo.setNodeId(rs.getInt("node_id"));
				vo.setIpAddress(rs.getString("ip_address"));
				vo.setAlias(rs.getString("alias"));
				vo.setCpuValue(rs.getFloat("cpu_value"));
				vo.setMemValue(rs.getFloat("mem_value"));
				vo.setIfUtil(rs.getFloat("if_util"));				
				list.add(vo);
			}						
		}
		catch(Exception ex)
		{
			SysLogger.error("Error in InformDao.queryNetworkPerformance()");
		}
		finally
		{
			conn.close();
		}		
		return list;
	}
	
	public List queryVirusInfo(String beginDate,String field)
	{
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer(200);
		sql.append("select tbl1.ip ip,tbl1.num num_of_times,tbl3.num num_of_virus,tbl3.virus_file virus_file,tbl3.virus virus from"); 
	    sql.append(" (select machine_ip ip,COUNT(*) num from nms_symantec WHERE substring(begintime,1,10)='");
	    sql.append(beginDate);
	    sql.append("' group by ip) tbl1");
	    sql.append(" left join(");
	    sql.append(" select COUNT(*) num, max(num_of_file) num1,tbl2.virus_file virus_file,tbl2.ip,tbl2.virus from"); 
	    sql.append("(select count(*) num_of_file,machine_ip ip,virus_file,virus from nms_symantec ");
	    sql.append(" WHERE substring(begintime,1,10)='");
	    sql.append(beginDate);
	    sql.append("' group by virus_file,ip) tbl2");
	    sql.append(" group by tbl2.ip ) tbl3");
	    sql.append(" on tbl1.ip = tbl3.ip");
	    sql.append(" order by " + field + " desc");
	    
		try
		{		    
			rs = conn.executeQuery(sql.toString());			
			while(rs.next())
			{
				VirusInfo vo = new VirusInfo();
				vo.setIp(rs.getString("ip"));
				vo.setNumOfTimes(rs.getInt("num_of_times"));
				vo.setNumOfVirus(rs.getInt("num_of_virus"));
				vo.setVirusFile(rs.getString("virus_file"));
				vo.setVirusName(rs.getString("virus"));
				list.add(vo);
			}						
		}
		catch(Exception ex)
		{
			SysLogger.error("Error in InformDao.queryVirusInfo()");
		}
		finally
		{
			conn.close();
		}		
		return list;
	}
	
	public BaseVo loadFromRS(ResultSet rs)
	{
		return null;
	}		
}
