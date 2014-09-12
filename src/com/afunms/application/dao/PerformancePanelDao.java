package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.application.model.PerformancePanelModel;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Aug 16, 2011 3:36:42 PM
 * 类说明
 */
public class PerformancePanelDao extends BaseDao implements DaoInterface {
	
	public PerformancePanelDao() {
		super("nms_performance_panel");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		PerformancePanelModel performancePanel = new PerformancePanelModel();
		try {
			performancePanel.setId(rs.getString("id"));
			performancePanel.setDeviceId(rs.getString("deviceId"));
			performancePanel.setDeviceType(rs.getString("deviceType"));
			performancePanel.setName(rs.getString("name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return performancePanel;
	}

	public boolean save(BaseVo vo) {
		PerformancePanelModel performancePanelModel = (PerformancePanelModel)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_performance_panel ('deviceId','deviceType','name') values ('");
		sql.append(performancePanelModel.getDeviceId());
		sql.append("','");
		sql.append(performancePanelModel.getDeviceType());
		sql.append("','");
		sql.append(performancePanelModel.getName());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		boolean result=false;
		PerformancePanelModel performancePanelModel = (PerformancePanelModel)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_performance_panel set deviceId = '");
		sql.append(performancePanelModel.getDeviceId());
		sql.append("', deviceType = '");
		sql.append(performancePanelModel.getDeviceType());
		sql.append("', name = '");
		sql.append(performancePanelModel.getName());
		sql.append("')");
		try {
			conn.executeUpdate(sql.toString());
			result=true;
			
		} catch (Exception e) {
			result=false;
			SysLogger.error("DominoConfigDao:update", e);
		}
		finally{
			conn.close();
		}
		return result;
	}

	public void savePreformance(List<PerformancePanelModel> performancePanelList) {
		if(performancePanelList == null){
			return ;
		}
		for(PerformancePanelModel p:performancePanelList){
			PerformancePanelModel performancePanelModel = (PerformancePanelModel)p;
			StringBuffer sql=new StringBuffer();
			sql.append("insert into nms_performance_panel (deviceId,deviceType,name) values ('");
			sql.append(performancePanelModel.getDeviceId());
			sql.append("','");
			sql.append(performancePanelModel.getDeviceType());
			sql.append("','");
			sql.append(performancePanelModel.getName());
			sql.append("')");
			try {
				conn.addBatch(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
				SysLogger.error("BaseDao.savePreformance()", e);
			}
		}
		conn.executeBatch();
	}
	
	public void deleteByName(String panelName){
		String sql = "delete from nms_performance_panel where name = '"+panelName+"'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			conn.close();
		}
	}
	
	/**
	 * 更细面板中的设备
	 * @param panelName
	 * @param deviceIds
	 * @param deviceType
	 * @return
	 */
	public boolean updatePanelDevices(String panelName, String deviceIds, String deviceType){
		boolean flag = false;
		if(deviceIds == null || deviceIds.trim().equals("")){
			return flag;
		}
		String sql = "delete from nms_performance_panel where name = '"+panelName+"'";
		try {
			//删除旧记录
			conn.executeUpdate(sql,true);
			//增加记录
			String[] deviceIdsArray = deviceIds.split(",");
			for(String deviceId:deviceIdsArray){
					StringBuffer sqlBuffer=new StringBuffer();
					sqlBuffer.append("insert into nms_performance_panel (deviceId,deviceType,name) values ('");
					sqlBuffer.append(deviceId);
					sqlBuffer.append("','");
					sqlBuffer.append(deviceType);
					sqlBuffer.append("','");
					sqlBuffer.append(panelName);
					sqlBuffer.append("')");
					try {
						conn.addBatch(sqlBuffer.toString());
					} catch (Exception e) {
						e.printStackTrace();
						SysLogger.error("BaseDao.updatePanelDevices()", e);
					}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return flag;
	}

	/**
	 * 面板的该名称是否被占用
	 * @param panelName
	 * @return
	 */
	public boolean checkPanelName(String panelName) {
		boolean flag = false;
		String sql = "select * from nms_performance_panel where name = '"+panelName+"'";
		try {
			//删除旧记录
			rs = conn.executeQuery(sql);
			if(rs != null && rs.next()){
				flag = true;//存在
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			conn.close();
		}
		return flag;
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
			sqlBuffer.append("delete from nms_performance_panel where deviceId='");
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
}
