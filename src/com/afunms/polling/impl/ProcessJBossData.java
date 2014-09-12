package com.afunms.polling.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


import com.afunms.application.model.IISConfig;
import com.afunms.application.model.IISVo;
import com.afunms.application.model.JBossConfig;
import com.afunms.common.util.ReflactUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.util.DataGate;
/**
 * <p>处理采集的JBoss数据信息</p>
 * @author HONGLI  Mar 7, 2011
 */
public class ProcessJBossData {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 保存JBoss的信息
	 * @param JBossconfigs   JBoss的集合
	 * @param JBossdatas  JBoss的数据集合
	 */
	public void saveJBossData(List<JBossConfig> jbossconfigs, Hashtable jbossdatas) {
		if(jbossconfigs == null || jbossconfigs.size() == 0 || jbossdatas == null || jbossdatas.isEmpty()){
			return ;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_jboss_temp(nodeid, entity, value, collecttime) values(?, ?, ?, ?)";
		String deleteSql = "delete from nms_jboss_temp where nodeid = ?";
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			deletePstmt = conn.prepareStatement(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			for (int i = 0; i < jbossconfigs.size(); i++) {
				JBossConfig JBossconfig = (JBossConfig)jbossconfigs.get(i);
				String JBossconfigid = JBossconfig.getId()+"";
				if(jbossdatas.containsKey("jboss:"+JBossconfigid)){
					Hashtable<String, String> jbossHash = (Hashtable<String, String>)jbossdatas.get("jboss:"+JBossconfigid);
					if(jbossHash == null){
						continue;
					}
					deletePstmt.setString(1, JBossconfigid);
					deletePstmt.execute();
					//遍历Hashtable
					Iterator<String> keyIterator = jbossHash.keySet().iterator();
					while(keyIterator.hasNext()){
						String key = keyIterator.next();
						String value = jbossHash.get(key);
						pstmt.setString(1, JBossconfigid);
						pstmt.setString(2, key);
						pstmt.setString(3, value);
						if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
							pstmt.setString(4, time);
						}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
							//java.util.Date  Dates = new java.util.Date(time);
							java.text.DateFormat date = java.text.DateFormat.getDateInstance();
							Date dat = date.parse(time);
						    java.sql.Timestamp time1 = new java.sql.Timestamp(dat.getYear(),dat.getMonth(),dat.getDate(),dat.getHours(),dat.getMinutes(),dat.getSeconds(),0);
							pstmt.setTimestamp(4, time1);
						}
						pstmt.addBatch();
					}
				}
			}
			pstmt.executeBatch();//批量插入
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally{
			if(deletePstmt != null){
				try {
					deletePstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
