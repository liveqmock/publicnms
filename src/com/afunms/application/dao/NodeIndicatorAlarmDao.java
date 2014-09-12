package com.afunms.application.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.afunms.application.model.NodeIndicatorAlarm;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 16, 2011 3:36:42 PM
 * 类说明 设备的告警信息持久层
 */
public class NodeIndicatorAlarmDao extends BaseDao implements DaoInterface {
	
	public NodeIndicatorAlarmDao() {
		super("node_indicator_alarm");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		NodeIndicatorAlarm performancePanel = new NodeIndicatorAlarm();
		try {
			performancePanel.setId(rs.getString("id"));
			performancePanel.setIndicatorName(rs.getString("indicatorName"));
			performancePanel.setAlarmLevel(rs.getString("alarmLevel"));
			performancePanel.setAlarmDesc(rs.getString("alarmDesc"));
			performancePanel.setDeviceId(rs.getString("deviceId"));
			performancePanel.setDeviceType(rs.getString("deviceType"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return performancePanel;
	}

	public boolean save(BaseVo vo) {
		NodeIndicatorAlarm performancePanelIndicatorsModel = (NodeIndicatorAlarm)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into node_indicator_alarm (deviceId,deviceType,indicatorName,alarmLevel,alarmDesc) values ('");
		sql.append(performancePanelIndicatorsModel.getDeviceId());
		sql.append("','");
		sql.append(performancePanelIndicatorsModel.getDeviceType());
		sql.append("','");
		sql.append(performancePanelIndicatorsModel.getIndicatorName());
		sql.append("','");
		sql.append(performancePanelIndicatorsModel.getAlarmLevel());
		sql.append("','");
		sql.append(performancePanelIndicatorsModel.getAlarmDesc());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		boolean result=false;
		NodeIndicatorAlarm nodeIndicatorAlarm = (NodeIndicatorAlarm)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update node_indicator_alarm set alarmLevel = '");
		sql.append(nodeIndicatorAlarm.getAlarmLevel());
		sql.append("', alarmDesc = '");
		sql.append(nodeIndicatorAlarm.getAlarmDesc());
		sql.append("' where deviceId = '");
		sql.append(nodeIndicatorAlarm.getDeviceId());
		sql.append("' and indicatorName = '");
		sql.append(nodeIndicatorAlarm.getIndicatorName());
		sql.append("'");
		try {
			conn.executeUpdate(sql.toString());
			result=true;
		} catch (Exception e) {
			result=false;
			SysLogger.error("NodeIndicatorAlarmDao:update", e);
		}
		finally{
			conn.close();
		}
		return result;
	}

	public void savePreformance(List<NodeIndicatorAlarm> performancePanelIndicatorsList) {
		for(NodeIndicatorAlarm p:performancePanelIndicatorsList){
			NodeIndicatorAlarm NodeIndicatorAlarm = (NodeIndicatorAlarm)p;
			StringBuffer sql=new StringBuffer();
			sql.append("insert into node_indicator_alarm (deviceId,deviceType,indicatorName,alarmLevel,alarmDesc) values ('");
			sql.append(NodeIndicatorAlarm.getDeviceId());
			sql.append("','");
			sql.append(NodeIndicatorAlarm.getDeviceType());
			sql.append("','");
			sql.append(NodeIndicatorAlarm.getIndicatorName());
			sql.append("','");
			sql.append(NodeIndicatorAlarm.getAlarmLevel());
			sql.append("','");
			sql.append(NodeIndicatorAlarm.getAlarmDesc());
			sql.append("')");
			try {
				conn.addBatch(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
				SysLogger.error("NodeIndicatorAlarmDao.savePreformance()", e);
			}
		}
		conn.executeBatch();
	}

	/**
	 * 判断该设备的告警指标的告警信息是否存在
	 * @param nodeIndicatorAlarm
	 * @return
	 */
	public boolean isExist(NodeIndicatorAlarm nodeIndicatorAlarm){
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from node_indicator_alarm where deviceId = '");
		sql.append(nodeIndicatorAlarm.getDeviceId());
		sql.append("' and deviceType = '");
		sql.append(nodeIndicatorAlarm.getDeviceType());
		sql.append("' and indicatorName = '");
		sql.append(nodeIndicatorAlarm.getIndicatorName());
		sql.append("'");
		try {
			rs = conn.executeQuery(sql.toString());
			if(rs != null && rs.next()){
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	/**
	 * @return
	 * <p>String为：deviceId:deviceType   List<NodeIndicatorAlarm>为：告警信息模型集合</p>
	 */
	public Hashtable<String, List<NodeIndicatorAlarm>> getNodeIndicaorAlarmHash(){
		Hashtable<String, List<NodeIndicatorAlarm>> retHash = new Hashtable<String, List<NodeIndicatorAlarm>>();
		 List<NodeIndicatorAlarm> list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from node_indicator_alarm order by id");
			if (rs == null)
				return null;
			while (rs.next())
				list.add((NodeIndicatorAlarm)loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
			SysLogger.error("BaseDao.loadAll()", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			conn.close();
		}
		//key：deviceId设备id
		Set<String> temSet = new HashSet<String>();
		for(int i=0; i<list.size(); i++){
			temSet.add(list.get(i).getDeviceId()+":"+list.get(i).getDeviceType());
		}
		
		Iterator<String> tempIterator = temSet.iterator();
		while(tempIterator.hasNext()){
			String deviceIdAndType = tempIterator.next();
			if(deviceIdAndType == null){
				continue;
			}
			List<NodeIndicatorAlarm> nodeIndicaorAlarList = new ArrayList<NodeIndicatorAlarm>();
			for(int i=0; i<list.size(); i++){
				NodeIndicatorAlarm tempAlarm = list.get(i);
				if(deviceIdAndType.equals(tempAlarm.getDeviceId()+":"+tempAlarm.getDeviceType())){
					nodeIndicaorAlarList.add(tempAlarm);
				}
			}
			retHash.put(deviceIdAndType, nodeIndicaorAlarList);
		}
		return retHash;
	}
	
	public void clearData(){
		conn.executeUpdate("update node_indicator_alarm set alarmLevel=0,alarmDesc = ''");
	}
	
	/**
	 * 根据类型和ID删除多条数据
	 * @param deviceId
	 * @param deviceType
	 * @return
	 */
	public boolean deleteByIdAndType(String deviceId, String deviceType){
		boolean flag = false;
		try {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("delete from node_indicator_alarm where deviceId='");
			sqlBuffer.append(deviceId);
			sqlBuffer.append("' and deviceType='");
			sqlBuffer.append(deviceType);
			sqlBuffer.append("'");
			conn.executeUpdate(sqlBuffer.toString());
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			conn.close();
		}
		return flag;
	}
	
	/**
	 * @param deviceId
	 * @param deviceType
	 * @param indicatorName
	 * @return
	 */
	public boolean deleteByIdAndTypeAndIndicatorName(String deviceId, String deviceType, String indicatorName){
		boolean flag = false;
		try {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("delete from node_indicator_alarm where deviceId='");
			sqlBuffer.append(deviceId);
			sqlBuffer.append("' and deviceType='");
			sqlBuffer.append(deviceType);
			sqlBuffer.append("' and indicatorName = '");
			sqlBuffer.append(indicatorName);
			sqlBuffer.append("'");
			conn.executeUpdate(sqlBuffer.toString());
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			conn.close();
		}
		return flag;
	}
}
