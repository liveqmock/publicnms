package com.afunms.alarm.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmPort;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;

import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class AlarmPortDao extends BaseDao implements DaoInterface{

	public AlarmPortDao() {
		super("nms_alarm_port_node");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		AlarmPort vo=new AlarmPort();
	      try
	      {
	          vo.setId(rs.getInt("id"));
	          vo.setIpaddress(rs.getString("ipaddress"));  
	          vo.setPortindex(rs.getInt("portindex"));
	          vo.setName(rs.getString("name"));
	          vo.setType(rs.getString("type"));
	          vo.setSubtype(("subtype"));
	          vo.setEnabled(rs.getString("enabled"));
	          vo.setCompare(rs.getInt("compare"));
	          vo.setLevelinvalue1(rs.getInt("levelinvalue1"));
	          vo.setLevelinvalue2(rs.getInt("levelinvalue2"));
	          vo.setLevelinvalue3(rs.getInt("levelinvalue3"));
	          vo.setLeveloutvalue1(rs.getInt("leveloutvalue1"));
	          vo.setLeveloutvalue2(rs.getInt("leveloutvalue2"));
	          vo.setLeveloutvalue3(rs.getInt("leveloutvalue3"));
	          vo.setLevelintimes1(rs.getInt("levelintimes1"));
	          vo.setLevelintimes2(rs.getInt("levelintimes2"));
	          vo.setLevelintimes3(rs.getInt("levelintimes3"));
	          vo.setLevelouttimes1(rs.getInt("levelouttimes1"));
	          vo.setLevelouttimes2(rs.getInt("levelouttimes2"));
	          vo.setLevelouttimes3(rs.getInt("levelouttimes3"));
	          vo.setSmsin1(rs.getInt("smsin1"));
	          vo.setSmsin2(rs.getInt("smsin2"));
	          vo.setSmsin3(rs.getInt("smsin3"));
	          vo.setSmsout1(rs.getInt("smsout1"));
	          vo.setSmsout2(rs.getInt("smsout2"));
	          vo.setSmsout3(rs.getInt("smsout3"));
	          vo.setWayin1(rs.getString("wayin1"));
	          vo.setWayin2(rs.getString("wayin2"));
	          vo.setWayin3(rs.getString("wayin3"));
	          vo.setWayout1(rs.getString("wayout1"));
	          vo.setWayout2(rs.getString("wayout2"));
	          vo.setWayout3(rs.getString("wayout3"));
	          vo.setAlarm_info(rs.getString("alarm_info"));
	        //  vo.setAlarm_level(rs.getInt("alarm_level"));
	      }
	      catch(Exception e)
	      {
	          //SysLogger.error("PortconfigDao.loadFromRS()",e);
	    	  e.printStackTrace();
	          vo = null;
	      }
	      return vo;
	}

	public boolean save(BaseVo baseVo) {
		AlarmPort vo=(AlarmPort)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_portconfig(ipaddress,portindex,name,type,subtype,enabled,levelinvalue1,levelinvalue2,levelinvalue3,leveloutvalue1,leveloutvalue2,leveloutvalue3,smsin1,smsin2,smsin3,smsout1,smsout2,smsout3,alarm_info)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("',");
		sql.append(vo.getPortindex());
		sql.append(",'");
		sql.append(vo.getName());	
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("','");
		sql.append(vo.getEnabled());
		sql.append("',");
		sql.append(vo.getLevelinvalue1());
		sql.append(",");
		sql.append(vo.getLevelinvalue2());
		sql.append(",");
		sql.append(vo.getLevelinvalue3());
		sql.append(",");
		sql.append(vo.getLeveloutvalue1());
		sql.append(",");
		sql.append(vo.getLeveloutvalue2());
		sql.append(",");
		sql.append(vo.getLeveloutvalue3());
		sql.append(",");
		sql.append(vo.getSmsin1());
		sql.append(",");
		sql.append(vo.getSmsin2());
		sql.append(",");
		sql.append(vo.getSmsin3());
		sql.append(",");
		sql.append(vo.getSmsout1());
		sql.append(",");
		sql.append(vo.getSmsout2());
		sql.append(",");
		sql.append(vo.getSmsout3());
		sql.append(",'");
		sql.append(vo.getAlarm_info());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
     AlarmPort vo = (AlarmPort)baseVo;
		
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_alarm_port_node set ");
	
		//sql.append(vo.getIpaddress());
		//sql.append("',portindex=");
		//sql.append(vo.getPortindex());
		//sql.append(",name='");
		//sql.append(vo.getName());	
		//sql.append("',type='");
		//sql.append(vo.getType());
		//sql.append("',subtype='");
		//sql.append(vo.getSubtype());
		sql.append("enabled='");
		sql.append(vo.getEnabled());
		sql.append("',compare=");
		sql.append(vo.getCompare());
		sql.append(",levelinvalue1=");
		sql.append(vo.getLevelinvalue1());
		sql.append(",levelinvalue2=");
		sql.append(vo.getLevelinvalue2());
		sql.append(",levelinvalue3=");
		sql.append(vo.getLevelinvalue3());
		sql.append(",leveloutvalue1=");
		sql.append(vo.getLeveloutvalue1());
		sql.append(",leveloutvalue2=");
		sql.append(vo.getLeveloutvalue2());
		sql.append(",leveloutvalue3=");
		sql.append(vo.getLeveloutvalue3());
		sql.append(",levelintimes1=");
		sql.append(vo.getLevelintimes1());
		sql.append(",levelintimes2=");
		sql.append(vo.getLevelintimes2());
		sql.append(",levelintimes3=");
		sql.append(vo.getLevelintimes3());
		sql.append(",levelouttimes1=");
		sql.append(vo.getLevelouttimes1());
		sql.append(",levelouttimes2=");
		sql.append(vo.getLevelouttimes2());
		sql.append(",levelouttimes3=");
		sql.append(vo.getLevelouttimes3());
		sql.append(",smsin1=");
		sql.append(vo.getSmsin1());
		sql.append(",smsin2=");
		sql.append(vo.getSmsin2());
		sql.append(",smsin3=");
		sql.append(vo.getSmsin3());
		sql.append(",smsout1=");
		sql.append(vo.getSmsout1());
		sql.append(",smsout2=");
		sql.append(vo.getSmsout2());
		sql.append(",smsout3=");
		sql.append(vo.getSmsout3());
		sql.append(",alarm_info='");
		sql.append(vo.getAlarm_info());
		sql.append("',wayin1='");
		sql.append(vo.getWayin1());
		sql.append("',wayin2='");
		sql.append(vo.getWayin2());
		sql.append("',wayin3='");
		sql.append(vo.getWayin3());
		sql.append("',wayout1='");
		sql.append(vo.getWayout1());
		sql.append("',wayout2='");
		sql.append(vo.getWayout2());
		sql.append("',wayout3='");
		sql.append(vo.getWayout3());
		sql.append("' where id=");
		sql.append(vo.getId());
		
		return saveOrUpdate(sql.toString());
	}
	/*
	 * 
	 * 从内存和数据库表里获取每个IP的端口信息，存入端口告警配置表里
	 */
	public void fromLastToPortAlarm() {
		List list=new ArrayList();
		List list1=new ArrayList();
		List shareList = new ArrayList();
		Hashtable porthash= new Hashtable();
		
		AlarmPort alarmPort = null;
		Vector configV = new Vector();
		try{
			//从内存中得到所有端口的采集信息
			Hashtable sharedata = ShareData.getSharedata();
			//从数据库得到监视IP列表
			HostNodeDao hostnodedao = new HostNodeDao();
			shareList = hostnodedao.loadMonitorNet();	
			if (shareList != null && shareList.size()>0){
				for(int i=0;i<shareList.size();i++){
					HostNode monitornode = (HostNode)shareList.get(i);				
					Hashtable ipdata = (Hashtable)sharedata.get(monitornode.getIpAddress());
					if (ipdata == null )continue;
					Vector vector = (Vector)ipdata.get("interface");
					if (vector !=null && vector.size()>0){
						for(int k=0;k<vector.size();k++){
							Interfacecollectdata inter = (Interfacecollectdata)vector.get(k);
							if (inter.getEntity().equalsIgnoreCase("ifname")){
								list.add(inter);
							}
						}
					}				
				}
			}
		//从端口配置表里获取列表
		//Query query1=session.createQuery("from Portconfig portconfig order by portconfig.ipaddress,portconfig.portindex");
		list1=loadAll();		
		if (list1 != null && list1.size()>0){
			for(int i=0;i<list1.size();i++){
				alarmPort = (AlarmPort)list1.get(i);				
				porthash.put(alarmPort.getIpaddress()+":"+alarmPort.getPortindex(),alarmPort);
			}
		}
		//判断采集到的端口信息是否已经在端口配置表里已经存在，若不存在则加入
		if (list != null && list.size()>0){
			for(int i=0;i<list.size();i++){
				Interfacecollectdata hostlastcollectdata = (Interfacecollectdata)list.get(i);
				if (!porthash.containsKey(hostlastcollectdata.getIpaddress()+":"+hostlastcollectdata.getSubentity())){
					alarmPort = new AlarmPort();
					
					alarmPort.setIpaddress(hostlastcollectdata.getIpaddress());
					
					alarmPort.setName(hostlastcollectdata.getThevalue());
					alarmPort.setPortindex(new Integer(hostlastcollectdata.getSubentity()));
					
					AlarmPortDao dao = new AlarmPortDao();
					try{
						dao.save(alarmPort);
					}catch(Exception ex){
						ex.printStackTrace();
					}finally{
						dao.close();
					}
				}
			}
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	// TODO Auto-generated method stub
	}
	
	//通过IP查询数据
	public List loadByIpaddress(String ip)
  	{
  		List list = new ArrayList();
  		try
  		{
  			rs = conn.executeQuery("select * from nms_alarm_port_node where ipaddress='"+ip+"' order by portindex");
  			while(rs.next())
  				list.add(loadFromRS(rs)); 
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("PortconfigDao:loadAll()",e);
  			list = null;
  		}
  		finally
  		{
  			try {
  			if (rs!=null)
				rs.close();
			
  			if (conn!=null)
  				conn.close();
			
  			} catch (SQLException e) {
				e.printStackTrace();
			}
  		}
  		return list;
  	}
	
	//删除IP相关的数据
	public List deleteByIpaddress(String ip)
  	{
  		List list = new ArrayList();
  		try
  		{
  			conn.executeUpdate("delete from nms_alarm_port_node where ipaddress='"+ip+"' ");
  		}
  		catch(Exception e)
  		{
  			SysLogger.error("AlarmPortDao:deleteByIpaddress()",e);
  			list = null;
  		}
  		finally
  		{
  			try {
			
  			if (conn!=null)
  				conn.close();
			
  			} catch (Exception e) {
				e.printStackTrace();
			}
  		}
  		return list;
  	}
	public List getAllByEnabledAndIp(){
		List list = new ArrayList();
		try{
			String sql="select * from nms_alarm_port_node h where h.enabled = '1' ";
			rs = conn.executeQuery(sql);
	        while(rs.next())
	        	list.add(loadFromRS(rs));
	        
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
  		{
			try {
	  			if (rs!=null)
					rs.close();
				
	  			if (conn!=null)
	  				conn.close();
				
	  			} catch (SQLException e) {
					e.printStackTrace();
				}
  		}
		// TODO Auto-generated method stub
		return list;
	}
	//查询所有要监控的端口
	public List getAllByEnabledAndIp(String ip){
		List list = new ArrayList();
		try{
			String sql="select * from nms_alarm_port_node h where h.ipaddress='"+ip+"' and h.enabled = '1' ";
			rs = conn.executeQuery(sql);
	        while(rs.next())
	        	list.add(loadFromRS(rs));
	        
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally
  		{
			try {
	  			if (rs!=null)
					rs.close();
				
	  			if (conn!=null)
	  				conn.close();
				
	  			} catch (SQLException e) {
					e.printStackTrace();
				}
  		}
		// TODO Auto-generated method stub
		return list;
	}
}
