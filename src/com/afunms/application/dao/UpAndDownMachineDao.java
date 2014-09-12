package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.afunms.application.model.Cluster;
import com.afunms.application.model.UpAndDownMachine;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;


//create table nms_remote_up_down_machine(id int,name varchar(50),ipaddress varchar(30),serverType varchar(10),lasttime datetime,username varchar(30),passwd varchar(30))
public class UpAndDownMachineDao extends BaseDao implements DaoInterface 
{
	public UpAndDownMachineDao()
	{
		super("nms_remote_up_down_machine");
	}    
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		UpAndDownMachine machine = new UpAndDownMachine();
		try {
			machine.setId(rs.getInt("id"));
			machine.setClusterId(rs.getInt("clusterId"));
			machine.setName(rs.getString("name"));
			machine.setIpaddress(rs.getString("ipaddress"));
			machine.setServerType(rs.getString("serverType"));
			machine.setLasttime(rs.getTimestamp("lasttime"));
			machine.setUsername(rs.getString("username"));
			machine.setPasswd(rs.getString("passwd"));
			machine.setMonitorStatus(rs.getInt("monitorStatus"));
			machine.setIsMonitor(rs.getInt("isMonitor"));
			machine.setIsJoin(rs.getInt("isJoin"));
			machine.setSequence(rs.getInt("sequence"));
			machine.setPowerOffBefore(rs.getString("powerOffBefore"));
			machine.setPowerOnAfter(rs.getString("powerOnAfter"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return machine;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		UpAndDownMachine machine = (UpAndDownMachine)vo;
		StringBuffer sql=new StringBuffer();
		int id=this.getNextID();
		sql.append("insert into nms_remote_up_down_machine(id,clusterId,name,ipaddress,serverType,lasttime," +
				"username,passwd,monitorStatus,isMonitor,isJoin,powerOnAfter,powerOffBefore,sequence) values(");
		sql.append(id);
		sql.append(",");
		sql.append(machine.getClusterId());
		sql.append(",'");
		sql.append(machine.getName());
		sql.append("','");
		sql.append(machine.getIpaddress());
		sql.append("','");
		sql.append(machine.getServerType());
		
		if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
			sql.append("','");
			sql.append(machine.getLasttime());
			sql.append("','");
		}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
			Date cc = machine.getLasttime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String recordtime = sdf.format(cc);
			sql.append("',");
			sql.append("to_date('"+recordtime+"','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",'");
		}
		
		
		sql.append(machine.getUsername());
		sql.append("','");
		sql.append(machine.getPasswd());
		sql.append("',");
		sql.append(machine.getMonitorStatus());
		sql.append(",");
		sql.append(machine.getIsMonitor());
		sql.append(",");
		sql.append(machine.getIsJoin());
		sql.append(",'");
		sql.append(machine.getPowerOnAfter());
		sql.append("','");
		sql.append(machine.getPowerOffBefore());
		sql.append("',");
		sql.append(id);
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		UpAndDownMachine machine = (UpAndDownMachine)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_remote_up_down_machine set name='");
		sql.append(machine.getName());
		sql.append("',ipaddress='");
		sql.append(machine.getIpaddress());
		sql.append("',serverType='");
		sql.append(machine.getServerType());
		sql.append("',username='");
		sql.append(machine.getUsername());
		sql.append("',passwd='");
		sql.append(machine.getPasswd());
		sql.append("',monitorStatus=");
		sql.append(machine.getMonitorStatus());
		sql.append(",isMonitor=");
		sql.append(machine.getIsMonitor());
		sql.append(",clusterId=");
		sql.append(machine.getClusterId());
		sql.append(",powerOnAfter='");
		sql.append(machine.getPowerOnAfter());
		sql.append("',powerOffBefore='");
		sql.append(machine.getPowerOffBefore());
		
		sql.append("' where id=");
		sql.append(machine.getId());
		return this.saveOrUpdate(sql.toString());
	}
	public boolean updateWithTime(BaseVo vo) {
		// TODO Auto-generated method stub
		UpAndDownMachine machine = (UpAndDownMachine)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_remote_up_down_machine set name='");
		sql.append(machine.getName());
		sql.append("',ipaddress='");
		sql.append(machine.getIpaddress());
		sql.append("',serverType='");
		sql.append(machine.getServerType());
		sql.append("',username='");
		sql.append(machine.getUsername());
		sql.append("',passwd='");
		sql.append(machine.getPasswd());
		sql.append("',monitorStatus=");
		sql.append(machine.getMonitorStatus());
		sql.append(",isMonitor=");
		sql.append(machine.getIsMonitor());
		sql.append(",isJoin=");
		sql.append(machine.getIsJoin());
		sql.append(",lasttime='");
		sql.append(machine.getLasttime());
		sql.append("',powerOnAfter='");
		sql.append(machine.getPowerOnAfter());
		sql.append("',powerOffBefore='");
		sql.append(machine.getPowerOffBefore());
		sql.append("' where id=");
		sql.append(machine.getId());
		return this.saveOrUpdate(sql.toString());
	}
	public void addBatchUpdateAllTime(BaseVo vo) {
		// TODO Auto-generated method stub
		UpAndDownMachine machine = (UpAndDownMachine)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_remote_up_down_machine set name='");
		sql.append(machine.getName());
		sql.append("',ipaddress='");
		sql.append(machine.getIpaddress());
		sql.append("',serverType='");
		sql.append(machine.getServerType());
		sql.append("',username='");
		sql.append(machine.getUsername());
		sql.append("',passwd='");
		sql.append(machine.getPasswd());
		sql.append("',monitorStatus=");
		sql.append(machine.getMonitorStatus());
		sql.append(",isMonitor=");
		sql.append(machine.getIsMonitor());
		sql.append(",isJoin=");
		sql.append(machine.getIsJoin());
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lasttime = df.format(machine.getLasttime());
		
		sql.append(",lasttime=to_date('");
		sql.append(lasttime);
		sql.append("','yyyy-mm-dd hh24:mi:ss'),powerOnAfter='");
		sql.append(machine.getPowerOnAfter());
		sql.append("',powerOffBefore='");
		sql.append(machine.getPowerOffBefore());
		sql.append("' where id=");
		sql.append(machine.getId());
		conn.addBatch(sql.toString());
	}
	public void executeBatch() {
		conn.executeBatch();
	}
	/**
	 * 修改服务器组ID
	 * @param ids
	 * @return
	 */
	public boolean updateClusterId(String[] ids) {
		  boolean result = false;
		   try
		   {
		       for(int i=0;i<ids.length;i++)
		        conn.addBatch("update nms_remote_up_down_machine set clusterId=0 where clusterId=" + ids[i]);
		       conn.executeBatch();
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("UpAndDownMahineDao.updateClusterId",ex);
		       result = false;
		   }
		return result;
	}
	
	public List loadAll(){
	String sql="select m.id,m.clusterId,m.name,m.ipaddress,m.serverType,m.username,m.lasttime,m.passwd,m.monitorStatus,m.isMonitor,m.isJoin,m.powerOnAfter,m.powerOffBefore,m.sequence,c.name name1  from nms_remote_up_down_machine m,nms_remote_up_down_cluster c where m.clusterId=c.id  union select  m.id,m.clusterId,m.name,m.ipaddress,m.serverType,m.username,m.lasttime,m.passwd,m.monitorStatus,m.isMonitor,m.isJoin,m.powerOnAfter,m.powerOffBefore,m.sequence,' ' from nms_remote_up_down_machine m  where  m.clusterId=0";
      rs=conn.executeQuery(sql);
      List<UpAndDownMachine> list=new ArrayList<UpAndDownMachine>();
   
	try {
		
		while(rs.next()){
		UpAndDownMachine machine = new UpAndDownMachine();
		
		machine.setId(rs.getInt("id"));
		machine.setClusterId(rs.getInt("clusterId"));
		machine.setName(rs.getString("name"));
		machine.setIpaddress(rs.getString("ipaddress"));
		machine.setServerType(rs.getString("serverType"));
		machine.setLasttime(rs.getTimestamp("lasttime"));
		machine.setUsername(rs.getString("username"));
		machine.setPasswd(rs.getString("passwd"));
		machine.setMonitorStatus(rs.getInt("monitorStatus"));
		machine.setIsMonitor(rs.getInt("isMonitor"));
		machine.setIsJoin(rs.getInt("isJoin"));
		machine.setSequence(rs.getInt("sequence"));
		machine.setClusterName(rs.getString("name1"));
		machine.setPowerOffBefore(rs.getString("powerOffBefore"));
		machine.setPowerOnAfter(rs.getString("powerOnAfter"));
		list.add(machine);
		 }
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return list;

	}
	
	public UpAndDownMachine findByIP(String ip)
    {
		UpAndDownMachine machine = null;
        try
	    {
		   rs = conn.executeQuery("select * from nms_remote_up_down_machine where ipaddress='" + ip + "'"); 
		   if(rs.next()){
			   machine = new UpAndDownMachine();
			   machine.setId(rs.getInt("id"));
			   machine.setClusterId(rs.getInt("clusterId"));
			   machine.setName(rs.getString("name"));
			   machine.setIpaddress(rs.getString("ipaddress"));
			   machine.setServerType(rs.getString("serverType"));
			   machine.setLasttime(rs.getTimestamp("lasttime"));
			   machine.setUsername(rs.getString("username"));
			   machine.setPasswd(rs.getString("passwd"));
			   machine.setMonitorStatus(rs.getInt("monitorStatus"));
			   machine.setIsMonitor(rs.getInt("isMonitor"));
			   machine.setIsJoin(rs.getInt("isJoin"));
			   machine.setSequence(rs.getInt("sequence"));
			   machine.setPowerOffBefore(rs.getString("powerOffBefore"));
			   machine.setPowerOnAfter(rs.getString("powerOnAfter"));
		   }
	    }    
	    catch(Exception ex)
	    {
		   //ex.printStackTrace();
		    SysLogger.error("BaseDao.findByID()",ex);
	    }finally{
		    if(rs != null){
			    try{
			 	   rs.close();
			    }catch(Exception e){
			    }
		   }
	   }
       return machine;
    }
	public Cluster findDataById(int id){
		String sql="select c.id as id,c.name as name,c.serverType as serverType,c.createtime as createtime from nms_remote_up_down_cluster c,nms_remote_up_down_machine m where m.clusterId=c.id and m.id="+id+" order by m.clusterId,m.id";
		rs=conn.executeQuery(sql);
		Cluster cluster = new Cluster();
		try {
			
			while(rs.next()){
				if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
					cluster.setId(rs.getInt("id"));
					cluster.setName(rs.getString("name"));
					cluster.setServerType(rs.getString("serverType"));
					cluster.setCreatetime(rs.getTimestamp("createtime"));
				}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
					cluster.setId(rs.getInt("id"));
					cluster.setName(rs.getString("name"));
					cluster.setServerType(rs.getString("serverType"));
					cluster.setCreatetime(rs.getTimestamp("createtime"));
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.close();
			
		}
		return cluster;
		
	}
	public boolean updateClusterIdById(int clusterId,int id) {
		String sql="update nms_remote_up_down_machine set clusterId="+clusterId+" where id="+id;
	  boolean isSucess=true;
		try {
		  conn.executeUpdate(sql);
	} catch (Exception e) {
		isSucess=false;
	}finally{
		if (conn!=null) {
			conn.close();
		}
	}
		
	  return isSucess;
	}
	public List loadClusterList(int clusterId) {
		List list=findByCondition(" where clusterId="+clusterId+" order by sequence,id");
		return list;
	}
	
	public HashMap<Integer, Integer> countById(){
		HashMap<Integer, Integer> totalMap=new HashMap<Integer, Integer>();
		String sql="select m.clusterId id,count( m.clusterId) value  from nms_remote_up_down_machine m where  m.clusterId!=0 group by m.clusterId";
		rs=conn.executeQuery(sql);
		try {
			while (rs.next()) {
			totalMap.put(rs.getInt("id"), rs.getInt("value"));
			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalMap;
	}
	
	public static void main(String[] args) {
		Date date=new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
		Timestamp now = new Timestamp(date.getTime());//获取系统当前时间
		String str = df.format(now);
		System.out.println(str);
	}
	
}
