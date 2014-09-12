/**
 * <p>Description: active_server_alarm</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project 衡水信用社
 * @date 2007-3-23
 */

package com.afunms.alarm.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List; 
import com.afunms.alarm.model.AlarmIndicatorsNode; 
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class AlarmIndicatorsNodeDao extends BaseDao implements DaoInterface{
    public AlarmIndicatorsNodeDao(){
    	super("nms_alarm_indicators_node");
    }

	AlarmIndicatorsUtil alarmIndicatorsUtil=new AlarmIndicatorsUtil();
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		AlarmIndicatorsNode alarmIndicatorsNode = new AlarmIndicatorsNode();
		try {
			alarmIndicatorsNode.setId(rs.getInt("id"));
			alarmIndicatorsNode.setNodeid(rs.getString("nodeid"));
			alarmIndicatorsNode.setSubentity(rs.getString("subentity"));
			alarmIndicatorsNode.setName(rs.getString("name"));
			alarmIndicatorsNode.setType(rs.getString("type"));
			alarmIndicatorsNode.setSubtype(rs.getString("subtype"));
			alarmIndicatorsNode.setDatatype(rs.getString("datatype"));
			alarmIndicatorsNode.setMoid(rs.getString("moid"));
			alarmIndicatorsNode.setThreshlod(rs.getInt("threshold"));
			alarmIndicatorsNode.setThreshlod_unit(rs.getString("threshold_unit"));
			alarmIndicatorsNode.setCompare(rs.getInt("compare"));
			alarmIndicatorsNode.setCompare_type(rs.getInt("compare_type"));
			alarmIndicatorsNode.setAlarm_times(rs.getString("alarm_times"));
			alarmIndicatorsNode.setAlarm_info(rs.getString("alarm_info"));
			alarmIndicatorsNode.setAlarm_level(rs.getString("alarm_level"));
			alarmIndicatorsNode.setEnabled(rs.getString("enabled"));
			alarmIndicatorsNode.setPoll_interval(rs.getString("poll_interval"));
			alarmIndicatorsNode.setInterval_unit(rs.getString("interval_unit"));
			alarmIndicatorsNode.setLimenvalue0(rs.getString("limenvalue0"));
			alarmIndicatorsNode.setLimenvalue1(rs.getString("limenvalue1"));
			alarmIndicatorsNode.setLimenvalue2(rs.getString("limenvalue2"));
			alarmIndicatorsNode.setTime0(rs.getString("time0"));
			alarmIndicatorsNode.setTime1(rs.getString("time1"));
			alarmIndicatorsNode.setTime2(rs.getString("time2"));
			alarmIndicatorsNode.setSms0(rs.getString("sms0"));
			alarmIndicatorsNode.setSms1(rs.getString("sms1"));
			alarmIndicatorsNode.setSms2(rs.getString("sms2"));
			alarmIndicatorsNode.setWay0(rs.getString("way0"));
			alarmIndicatorsNode.setWay1(rs.getString("way1"));
			alarmIndicatorsNode.setWay2(rs.getString("way2"));
			alarmIndicatorsNode.setCategory(rs.getString("category"));
			alarmIndicatorsNode.setDescr(rs.getString("descr"));
			alarmIndicatorsNode.setUnit(rs.getString("unit"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alarmIndicatorsNode;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		
		AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_alarm_indicators(nodeid , name, type, subtype, datatype, moid, threshold, " +
				"threshold_unit, compare, compare_type, alarm_times, alarm_info, alarm_level," +
				" enabled, poll_interval, interval_unit, subentity, limenvalue0, limenvalue1, " +
				"limenvalue2, time0, time1, time2, sms0, sms1, sms2, way0, way1, way2, category, descr, unit) values('");
		sql.append(alarmIndicatorsNode.getNodeid());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getName());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getType());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSubtype());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getDatatype());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getMoid());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getThreshlod());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getThreshlod_unit());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getCompare());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getCompare_type());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getAlarm_times());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getAlarm_info());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getAlarm_level());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getEnabled());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getPoll_interval());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getInterval_unit());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSubentity());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getLimenvalue0());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getLimenvalue1());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getLimenvalue2());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getTime0());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getTime1());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getTime2());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSms0());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSms1());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSms2());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getWay0());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getWay1());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getWay2());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getCategory());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getDescr());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getUnit());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean saveBatch(List list) {
		// TODO Auto-generated method stub
		try {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)iterator.next();
				StringBuffer sql = new StringBuffer();
				sql.append("insert into nms_alarm_indicators_node(nodeid , name, type, subtype, datatype, moid, threshold, " +
						"threshold_unit, compare, compare_type, alarm_times, alarm_info, alarm_level," +
						" enabled, poll_interval, interval_unit, subentity, limenvalue0, limenvalue1, " +
						"limenvalue2, time0, time1, time2, sms0, sms1, sms2, way0, way1, way2, category, descr, unit) values('");
				sql.append(alarmIndicatorsNode.getNodeid());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getName());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getType());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getSubtype());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getDatatype());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getMoid());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getThreshlod());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getThreshlod_unit());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getCompare());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getCompare_type());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getAlarm_times());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getAlarm_info());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getAlarm_level());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getEnabled());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getPoll_interval());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getInterval_unit());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getSubentity());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getLimenvalue0());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getLimenvalue1());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getLimenvalue2());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getTime0());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getTime1());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getTime2());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getSms0());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getSms1());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getSms2());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getWay0());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getWay1());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getWay2());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getCategory());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getDescr());
				sql.append("','");
				sql.append(alarmIndicatorsNode.getUnit());
				sql.append("')");
				
				try {
					//SysLogger.info(sql.toString());
					conn.addBatch(sql.toString()); 
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			conn.executeBatch();
			alarmIndicatorsUtil.loadAlarmIndicatorsNode();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)vo;
		
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_alarm_indicators_node set name ='");
		sql.append(alarmIndicatorsNode.getName());
		sql.append("',nodeid='");
		sql.append(alarmIndicatorsNode.getNodeid());
		sql.append("',type='");
		sql.append(alarmIndicatorsNode.getType());
		sql.append("',subtype='");
		sql.append(alarmIndicatorsNode.getSubtype());
		sql.append("',datatype='");
		sql.append(alarmIndicatorsNode.getDatatype());
		sql.append("',moid='");
		sql.append(alarmIndicatorsNode.getMoid());
		sql.append("',threshold='");
		sql.append(alarmIndicatorsNode.getThreshlod());
		sql.append("',threshold_unit='");
		sql.append(alarmIndicatorsNode.getThreshlod_unit());
		sql.append("',compare='");
		sql.append(alarmIndicatorsNode.getCompare());
		sql.append("',compare_type='");
		sql.append(alarmIndicatorsNode.getCompare_type());
		sql.append("',alarm_times='");
		sql.append(alarmIndicatorsNode.getAlarm_times());
		sql.append("',alarm_info='");
		sql.append(alarmIndicatorsNode.getAlarm_info());
		sql.append("',alarm_level='");
		sql.append(alarmIndicatorsNode.getAlarm_level());
		sql.append("',enabled='");
		sql.append(alarmIndicatorsNode.getEnabled());
		sql.append("',poll_interval='");
		sql.append(alarmIndicatorsNode.getPoll_interval());
		sql.append("',interval_unit='");
		sql.append(alarmIndicatorsNode.getInterval_unit());
		sql.append("',subentity='");
		sql.append(alarmIndicatorsNode.getSubentity());
		sql.append("',limenvalue0='");
		sql.append(alarmIndicatorsNode.getLimenvalue0());
		sql.append("',limenvalue1='");
		sql.append(alarmIndicatorsNode.getLimenvalue1());
		sql.append("',limenvalue2='");
		sql.append(alarmIndicatorsNode.getLimenvalue2());
		sql.append("',time0='");
		sql.append(alarmIndicatorsNode.getTime0());
		sql.append("',time1='");
		sql.append(alarmIndicatorsNode.getTime1());
		sql.append("',time2='");
		sql.append(alarmIndicatorsNode.getTime2());
		sql.append("',sms0='");
		sql.append(alarmIndicatorsNode.getSms0());
		sql.append("',sms1='");
		sql.append(alarmIndicatorsNode.getSms1());
		sql.append("',sms2='");
		sql.append(alarmIndicatorsNode.getSms2());
		sql.append("',way0='");
		sql.append(alarmIndicatorsNode.getWay0());
		sql.append("',way1='");
		sql.append(alarmIndicatorsNode.getWay1());
		sql.append("',way2='");
		sql.append(alarmIndicatorsNode.getWay2());
		sql.append("',category='");
		sql.append(alarmIndicatorsNode.getCategory());
		sql.append("',descr='");
		sql.append(alarmIndicatorsNode.getDescr());
		sql.append("',unit='");
		sql.append(alarmIndicatorsNode.getUnit());
		sql.append("' where id=" + alarmIndicatorsNode.getId());
		System.out.println(sql.toString()); 
		return saveOrUpdate(sql.toString());
	}
	
	public boolean update(List list) {
		// TODO Auto-generated method stub
		if(list != null && list.size()>0){
			for(int i=0;i<list.size();i++){
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("update nms_alarm_indicators_node set name ='");
				sql.append(alarmIndicatorsNode.getName());
				sql.append("',nodeid='");
				sql.append(alarmIndicatorsNode.getNodeid());
				sql.append("',type='");
				sql.append(alarmIndicatorsNode.getType());
				sql.append("',subtype='");
				sql.append(alarmIndicatorsNode.getSubtype());
				sql.append("',datatype='");
				sql.append(alarmIndicatorsNode.getDatatype());
				sql.append("',moid='");
				sql.append(alarmIndicatorsNode.getMoid());
				sql.append("',threshold='");
				sql.append(alarmIndicatorsNode.getThreshlod());
				sql.append("',threshold_unit='");
				sql.append(alarmIndicatorsNode.getThreshlod_unit());
				sql.append("',compare='");
				sql.append(alarmIndicatorsNode.getCompare());
				sql.append("',compare_type='");
				sql.append(alarmIndicatorsNode.getCompare_type());
				sql.append("',alarm_times='");
				sql.append(alarmIndicatorsNode.getAlarm_times());
				sql.append("',alarm_info='");
				sql.append(alarmIndicatorsNode.getAlarm_info());
				sql.append("',alarm_level='");
				sql.append(alarmIndicatorsNode.getAlarm_level());
				sql.append("',enabled='");
				sql.append(alarmIndicatorsNode.getEnabled());
				sql.append("',poll_interval='");
				sql.append(alarmIndicatorsNode.getPoll_interval());
				sql.append("',interval_unit='");
				sql.append(alarmIndicatorsNode.getInterval_unit());
				sql.append("',subentity='");
				sql.append(alarmIndicatorsNode.getSubentity());
				sql.append("',limenvalue0='");
				sql.append(alarmIndicatorsNode.getLimenvalue0());
				sql.append("',limenvalue1='");
				sql.append(alarmIndicatorsNode.getLimenvalue1());
				sql.append("',limenvalue2='");
				sql.append(alarmIndicatorsNode.getLimenvalue2());
				sql.append("',time0='");
				sql.append(alarmIndicatorsNode.getTime0());
				sql.append("',time1='");
				sql.append(alarmIndicatorsNode.getTime1());
				sql.append("',time2='");
				sql.append(alarmIndicatorsNode.getTime2());
				sql.append("',sms0='");
				sql.append(alarmIndicatorsNode.getSms0());
				sql.append("',sms1='");
				sql.append(alarmIndicatorsNode.getSms1());
				sql.append("',sms2='");
				sql.append(alarmIndicatorsNode.getSms2());
				sql.append("',way0='");
				sql.append(alarmIndicatorsNode.getWay0());
				sql.append("',way1='");
				sql.append(alarmIndicatorsNode.getWay1());
				sql.append("',way2='");
				sql.append(alarmIndicatorsNode.getWay2());
				sql.append("',category='");
				sql.append(alarmIndicatorsNode.getCategory());
				sql.append("',descr='");
				sql.append(alarmIndicatorsNode.getDescr());
				sql.append("',unit='");
				sql.append(alarmIndicatorsNode.getUnit());
				sql.append("' where id=" + alarmIndicatorsNode.getId());
				//SysLogger.info(sql.toString());
				conn.addBatch(sql.toString());
				//update(alarmIndicatorsNode);
				
			}
			try{
				conn.executeBatch();
				alarmIndicatorsUtil.loadAlarmIndicatorsNode();
			}catch(Exception e){
				
			}
		}
		return true;
		//return saveOrUpdate(sql.toString());
	}
	public List findByNodeIdAndTypeAndSubType(String nodeid , String type ,String subtype){
		String sql = "";
		List list = new ArrayList();
		if(subtype != null && !"null".equalsIgnoreCase(subtype) && subtype.trim().length()>0){
			sql = "select * from nms_alarm_indicators_node where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'";
		}else
			sql = "select * from nms_alarm_indicators_node where nodeid='" + nodeid + "' and type='" + type + "'";
		//SysLogger.info(sql);
	    try 
	    {
		   rs = conn.executeQuery(sql);
		   if(rs == null)return null;
		   while(rs.next())
			  list.add(loadFromRS(rs));				
	    } 
	    catch(Exception e) 
	    {
            list = null;
            e.printStackTrace();
		    SysLogger.error("AlarmIndicatorsNodeDao.findByNodeIdAndTypeAndSubType()",e);
	    }
	    return list;	
	}
	
   /**
    * 
    * 根据名称、类型、节点id来批量删除阀值指标
    * @param type 类型
    * @param name 名称
    * @param nodes 节点列表
    * @param list 阀值列表
    * @return 成功 true 失败false
    */
	public boolean deletenametypenodeid(String[] nodes,List list) {
	    String name="";
	    String type="";
	    for(int i=0;i<list.size();i++)
	    {
	    	AlarmIndicatorsNode alarmnode=(AlarmIndicatorsNode)list.get(i);
	    	name=alarmnode.getName();
	    	type=alarmnode.getType();
			StringBuffer sb=new StringBuffer("delete from nms_alarm_indicators_node");
	 	    sb.append(" where name='").append(name).append("'");
	 	    sb.append(" and ").append("type='").append(type).append("'");
	 	    sb.append(" and ");
		    for(int n=0;n<nodes.length;n++)
		    {
		    
		    	String sql=sb.toString();
		    	sql =sql+"nodeid='"+nodes[n]+"'";
		    	System.out.println("====="+sql);
		    	conn.addBatch(sql);	
		    }
	    	
		    sb=null;
	    }
		try{
			conn.executeBatch();
			alarmIndicatorsUtil.loadAlarmIndicatorsNode();
			conn.commit();
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	
	
	 /**
	    * 
	    * 根据节点的id删除节点的采集指标
	    * @param nodes 节点列表
	    * @return 成功 true 失败false
	    */
		public boolean deletenodeid(String[] nodes) {
			
		    for(int n=0;n<nodes.length;n++)
		    {
		         String sql="delete from nms_alarm_indicators_node where nodeid='";
		         sql=sql+nodes[n]+"'";
		    	System.out.println("*****sql==========="+sql);
		    	conn.addBatch(sql);
		    	
		    	
		    }
		    
				try{
					conn.executeBatch();
					alarmIndicatorsUtil.loadAlarmIndicatorsNode();
					conn.commit();
				}catch(Exception e){
					return false;
					
				}
			
			return true;
			
		}
	
	/**
	 * 
	 * 批量添加
	 * 
	 * 
	 * @param nodeids 
	 * @param list
	 * @return
	 */
	public boolean addBatch(String[]nodeids ,List list)
	{
		//List alllist=new ArrayList();
		for(int i=0;i<nodeids.length;i++)
		{
			String nodeid=nodeids[i];
			//String nodeid=nodeids[i];
			for(int n=0;n<list.size();n++)
			{
				AlarmIndicatorsNode alarmnode=new AlarmIndicatorsNode();
				alarmnode=(AlarmIndicatorsNode)list.get(n);
				alarmnode.setNodeid(nodeid);
                
				conn.addBatch(this.addsql(alarmnode));
				//alllist.add(alarmnode);
			}
		}
		try{
			conn.executeBatch();
			alarmIndicatorsUtil.loadAlarmIndicatorsNode();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			return false;
			
		}
	
		//this.saveBatch(alllist);
		
		
		return true;
	}
	
	
	
	/**
	 * 
	 * 根据阀值对象放回一个添加的sql
	 * @param alarmIndicatorsNode
	 * @return
	 */
	public String addsql(AlarmIndicatorsNode alarmIndicatorsNode)
	{
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_alarm_indicators_node(nodeid , name, type, subtype, datatype, moid, threshold, " +
				"threshold_unit, compare, compare_type, alarm_times, alarm_info, alarm_level," +
				" enabled, poll_interval, interval_unit, subentity, limenvalue0, limenvalue1, " +
				"limenvalue2, time0, time1, time2, sms0, sms1, sms2, way0, way1, way2, category, descr, unit) values('");
		sql.append(alarmIndicatorsNode.getNodeid());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getName());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getType());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSubtype());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getDatatype());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getMoid());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getThreshlod());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getThreshlod_unit());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getCompare());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getCompare_type());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getAlarm_times());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getAlarm_info());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getAlarm_level());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getEnabled());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getPoll_interval());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getInterval_unit());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSubentity());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getLimenvalue0());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getLimenvalue1());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getLimenvalue2());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getTime0());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getTime1());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getTime2());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSms0());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSms1());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getSms2());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getWay0());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getWay1());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getWay2());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getCategory());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getDescr());
		sql.append("','");
		sql.append(alarmIndicatorsNode.getUnit());
		sql.append("')");
		
		System.out.println("***==sql=="+sql.toString());
		return sql.toString();
		
		
	}
	
	
	public boolean save(List list) {
		// TODO Auto-generated method stub
		if(list != null && list.size()>0){
			for(int i=0;i<list.size();i++){
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
				StringBuffer sql = new StringBuffer();
				sql.append("update nms_alarm_indicators_node set name ='");
				sql.append(alarmIndicatorsNode.getName());
				sql.append("',nodeid='");
				sql.append(alarmIndicatorsNode.getNodeid());
				sql.append("',type='");
				sql.append(alarmIndicatorsNode.getType());
				sql.append("',subtype='");
				sql.append(alarmIndicatorsNode.getSubtype());
				sql.append("',datatype='");
				sql.append(alarmIndicatorsNode.getDatatype());
				sql.append("',moid='");
				sql.append(alarmIndicatorsNode.getMoid());
				sql.append("',threshold='");
				sql.append(alarmIndicatorsNode.getThreshlod());
				sql.append("',threshold_unit='");
				sql.append(alarmIndicatorsNode.getThreshlod_unit());
				sql.append("',compare='");
				sql.append(alarmIndicatorsNode.getCompare());
				sql.append("',compare_type='");
				sql.append(alarmIndicatorsNode.getCompare_type());
				sql.append("',alarm_times='");
				sql.append(alarmIndicatorsNode.getAlarm_times());
				sql.append("',alarm_info='");
				sql.append(alarmIndicatorsNode.getAlarm_info());
				sql.append("',alarm_level='");
				sql.append(alarmIndicatorsNode.getAlarm_level());
				sql.append("',enabled='");
				sql.append(alarmIndicatorsNode.getEnabled());
				sql.append("',poll_interval='");
				sql.append(alarmIndicatorsNode.getPoll_interval());
				sql.append("',interval_unit='");
				sql.append(alarmIndicatorsNode.getInterval_unit());
				sql.append("',subentity='");
				sql.append(alarmIndicatorsNode.getSubentity());
				sql.append("',limenvalue0='");
				sql.append(alarmIndicatorsNode.getLimenvalue0());
				sql.append("',limenvalue1='");
				sql.append(alarmIndicatorsNode.getLimenvalue1());
				sql.append("',limenvalue2='");
				sql.append(alarmIndicatorsNode.getLimenvalue2());
				sql.append("',time0='");
				sql.append(alarmIndicatorsNode.getTime0());
				sql.append("',time1='");
				sql.append(alarmIndicatorsNode.getTime1());
				sql.append("',time2='");
				sql.append(alarmIndicatorsNode.getTime2());
				sql.append("',sms0='");
				sql.append(alarmIndicatorsNode.getSms0());
				sql.append("',sms1='");
				sql.append(alarmIndicatorsNode.getSms1());
				sql.append("',sms2='");
				sql.append(alarmIndicatorsNode.getSms2());
				sql.append("',way0='");
				sql.append(alarmIndicatorsNode.getWay0());
				sql.append("',way1='");
				sql.append(alarmIndicatorsNode.getWay1());
				sql.append("',way2='");
				sql.append(alarmIndicatorsNode.getWay2());
				sql.append("',category='");
				sql.append(alarmIndicatorsNode.getCategory());
				sql.append("',descr='");
				sql.append(alarmIndicatorsNode.getDescr());
				sql.append("',unit='");
				sql.append(alarmIndicatorsNode.getUnit());
				sql.append("' where id=" + alarmIndicatorsNode.getId());
				//SysLogger.info(sql.toString());
				conn.addBatch(sql.toString());
				//update(alarmIndicatorsNode);
				
			}
			try{
				conn.executeBatch();
				alarmIndicatorsUtil.loadAlarmIndicatorsNode();
			}catch(Exception e){
				
			}
		}
		return true;
		//return saveOrUpdate(sql.toString());
	}
//	public AlarmIndicators getByTypeAndSubTypeAndName(String type ,String subtype ,String name){
//		
//		AlarmIndicators alarmIndicators = null;
//		
//		String sql = "select * from nms_alarm_indicators where type='" + type + "' and subtype='" + subtype
//			+ "' and name='" + name + "'";
//		List list = findByCriteria(sql);
//		
//		if(list!=null && list.size() ==1){
//			alarmIndicators = (AlarmIndicators)list.get(0);
//		}
//		
//		return alarmIndicators;
//	}
	
	public List getByTypeAndSubType(String type ,String subtype){
		String sql = "select * from nms_alarm_indicators_node where type='" + type + "' and subtype='" + subtype + "'";
		List list = findByCriteria(sql);
		return list;
	}
	
	public List getByNodeId(String nodeid){
		String sql = "select * from nms_alarm_indicators_node where nodeid='" + nodeid + "'";
		List list = findByCriteria(sql);
		return list;
	}
	
	public List getByInterval(String interval ,String unit){
		String sql = "select * from nms_alarm_indicators_node where poll_interval='" + interval + "' and interval_unit='" + unit + "'";
		List list = findByCriteria(sql);
		return list;
	}
	public List getByInterval(String interval ,String unit,int enabled){
		String sql = "select * from nms_alarm_indicators_node where poll_interval='" + interval + "' and interval_unit='" + unit + "' and enabled="+enabled;
		List list = findByCriteria(sql);
		return list;
	}
	
	public List getByNodeIdAndTypeAndSubType(String nodeid , String type ,String subtype){
		String sql = "";
		if(subtype != null && !"null".equalsIgnoreCase(subtype) && subtype.trim().length()>0){
			sql = "select * from nms_alarm_indicators_node where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'";
		}else
			sql = "select * from nms_alarm_indicators_node where nodeid='" + nodeid + "' and type='" + type + "'";
		//SysLogger.info(sql);
		List list = findByCriteria(sql);
		return list;
	}
	
	
	//虚拟机
	public List VMgetByNodeIdAndTypeAndSubType(String nodeid , String type ,String subtype,String category,String vid,String name){
		String sql = "";
		if(subtype != null && !"null".equalsIgnoreCase(subtype) && subtype.trim().length()>0){
			sql = "select * from nms_alarm_indicators_node where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' and category='"+category+"' and subentity='"+vid+"' and name ='"+name+"'";
		}else
			sql = "select * from nms_alarm_indicators_node where nodeid='" + nodeid + "' and type='" + type + "'";
		SysLogger.info(sql);
		List list = findByCriteria(sql);
		return list;
	}
	
	public List getByNodeIdAndTypeAndSubType(String nodeid , String type ,String subtype,String alarmname){
		String sql = "";
		if(subtype != null && !"null".equalsIgnoreCase(subtype) && subtype.trim().length()>0){
			sql = "select * from nms_alarm_indicators_node where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "' and name='"+alarmname+"'";
		}else
			sql = "select * from nms_alarm_indicators_node where nodeid='" + nodeid + "' and type='" + type + "'";
		//SysLogger.info(sql);
		List list = findByCriteria(sql);
		return list;
	}
	/**
	 * 查询所有的告警指标表数据
	 * @return
	 * @author makewen
	 * @date   Apr 1, 2011
	 */
	public List getAllAlarmInicatorsNodes(){
		String sql ="select * from nms_alarm_indicators_node";
//		SysLogger.info(sql);
		List list = findByCriteria(sql);
		return list;
	}
	
	public boolean deleteByNodeId(String nodeid){
		String sql = "delete from nms_alarm_indicators_node where nodeid='" + nodeid +  "'";
		return saveOrUpdate(sql);
	}
	
	
	
	public boolean deleteByNodeId(String nodeid,String type){
		String sql = "delete from nms_alarm_indicators_node where nodeid='" + nodeid +  "' and type='"+type+"'";
		return saveOrUpdate(sql);
	}
	
	public boolean deleteByNodeId(String nodeid , String type , String subtype){
		String sql = "delete from nms_alarm_indicators_node where nodeid='" + nodeid +  "' and type='" + type + "' and subtype='" + subtype + "'";
		return saveOrUpdate(sql);
	}
	
	/**
	 * 删除同一类设备的告警指标
	 * @param nodeids  设备ID
	 * @param type     类型
	 * @param subtype  子类型
	 * @return
	 */
	public boolean deleteByNodeIds(String[] nodeids, String type , String subtype){
		if(nodeids == null || nodeids.length == 0){
			return false;
		}
		StringBuffer nodeidsTempStrBuffer = new StringBuffer();
		for(int i=0; i<nodeids.length; i++){
			nodeidsTempStrBuffer.append("'");
			nodeidsTempStrBuffer.append(nodeids[i]);
			nodeidsTempStrBuffer.append("'");
			if(i != nodeids.length-1){
				nodeidsTempStrBuffer.append(",");
			}
		}//'1','2','3'
		String sql = "delete from nms_alarm_indicators_node where nodeid in (" + nodeidsTempStrBuffer +  ") and type='" + type + "' and subtype='" + subtype + "'";
		return saveOrUpdate(sql);
	}
	
	public boolean changeMonfalgById(String id , String monflag){
		String sql = "update nms_alarm_indicators_node set enabled='" + monflag +  "' where id='" + id + "'";
		return saveOrUpdate(sql);
	}
	public AlarmIndicatorsNode findByIdAndNode(String id,String nodeid)
    {
		AlarmIndicatorsNode vo = null;
       try
	   {
		   rs = conn.executeQuery("select * from nms_alarm_indicators_node where id=" + id + " and nodeid = '" + nodeid + "'"); 
		   if(rs.next())
		       vo = (AlarmIndicatorsNode) loadFromRS(rs);
	   }    
	   catch(Exception ex)
	   {
		   ex.printStackTrace();
		   SysLogger.error("BaseDao.findByIdAndNode(String id)",ex);
	   }  
       return vo;
    }
	//TODO  makewen-基类的方法重写实现功能
	@Override
	public boolean delete(String[] id){
	   boolean flag=super.delete(id);
	   if(flag){
		   alarmIndicatorsUtil.loadAlarmIndicatorsNode();
	   }
	   return flag; 
	} 
	@Override
	public boolean saveOrUpdate(String sql){ 
	   boolean flag=super.saveOrUpdate(sql);
	   if(flag){
		   alarmIndicatorsUtil.loadAlarmIndicatorsNode();
	   }
	   return flag;  
	}
	   
//	   public void close(){
//		   super.close();
//	   }
//
//	   public JspPage getPage(){
//		   return super.getPage();
//	   }  
//	   public List listByPage(int curpage,int perpage){	
//		   return super.listByPage(curpage,"",perpage);	   
//	   }
//	   public List listByPage(int curpage,String where,int perpage){
//		   return super.listByPage(curpage,where,perpage); 
//	   } 
//	   protected synchronized int getNextID(){
//		   return super.getNextID(); 
//	   }
//	 
//	   protected synchronized int getNextID(String otherTable) {
//		   return super.getNextID(otherTable); 
//	   }
//	    
//	   public BaseVo findByID(String id){
//		   return super.findByID(id); 
//	   }
//	 
//	   public List loadAll() {
//		   return super.loadAll(); 
//	   } 
//	   public List loadByPerAll(String bid){
//		   return super.loadByPerAll(bid); 
//	   } 
//	   public List loadOrderByIP(){
//		   return super.loadOrderByIP(); 
//	   }
//	   
//	   public List findByCriteria(String sql){
//		   return super.findByCriteria(sql); 
//	   } 
//	    
//	   public List findByCondition(String condition){ 
//		   return super.findByCondition(condition); 
//	   } 
//	   public String getCountByWhere(String where){ 
//		   return super.getCountByWhere(where); 
//	   }
}   