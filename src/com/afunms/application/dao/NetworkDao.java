package com.afunms.application.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.discovery.IpAddress;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.util.DataGate;

/**
 * 
 * @author HONGLI  Feb 12, 2011
 *
 */
public class NetworkDao {
	
	/**
	 * 删除多个设备的临时表中的数据
	 * @param tableName 表名称
	 * @param nodeid
	 * @return
	 */
	public Boolean clearNmsTempDatas(String[] tableNames,String[] ids){
		DBManager dbmanager = new DBManager();
		Boolean returnFlag = false;
		if(ids != null && ids.length > 0){
    		try {
				//进行修改
				for(int i=0;i<ids.length;i++){
					String id = ids[i];
				    PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));          
					for(String tableName : tableNames){
						String sql = "delete from "+tableName+" where nodeid='"+id+"'";
						System.out.println(sql);
						dbmanager.addBatch(sql);
					}
				}
				dbmanager.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				dbmanager.close();
			}
			returnFlag = true;
		}
		return returnFlag;
	}
	
	/**
	 * 刷新所有结点的数据信息 
	 * @param nodeList
	 * @return
	 */
	public void collectAllNetworkData(List nodeList){
		if(nodeList == null || nodeList.size() == 0){
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Connection conn = null;
		DBManager dbmanager = null;
		try {
			dbmanager = new DBManager();
			//conn = DataGate.getCon();
			//conn.setAutoCommit(true);
			ResultSet rs = null;
			Node node = null;
			Host host = null;
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = null;
			Vector memoryVector = null;
			Vector<Diskcollectdata> diskVector = null;
	        Vector cpuV = null;
	        Vector allutil = null;
	        Vector interfaceVector = null;
	        Vector ipPingData = null;
	        StringBuffer sqlBuffer = null;
	        Pingcollectdata pingcollectdata = null;
	        Interfacecollectdata interfacecollectdata = null;
	        AllUtilHdx allUtilHdx = null;
			for(int i=0; i<nodeList.size(); i++){
				node = (Node)nodeList.get(i);
				host = new Host();
				host = (Host)PollingEngine.getInstance().getNodeByID(node.getId());
		    	
		        nodedto = nodeUtil.creatNodeDTOByNode(host);
		        String nodeid = nodedto.getId()+"";
		        String type = nodedto.getType();
		        String subtype = nodedto.getSubtype();
				//取出连通率信息
//		        Hashtable ipAllData = new Hashtable();
		        memoryVector = new Vector();
				diskVector = new Vector<Diskcollectdata>();
		        cpuV = new Vector();
		        allutil = new Vector();
		        interfaceVector = new Vector();
		        ipPingData = new Vector();
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("select * from nms_ping_data_temp where nodeid = '");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and type = '");
				sqlBuffer.append(type);
				sqlBuffer.append("' and subtype = '"); 
				sqlBuffer.append(subtype);
				sqlBuffer.append("'");
				try {
					//dbmanager.executeQuery(sql)
					rs = dbmanager.executeQuery(sqlBuffer.toString());
					while (rs.next()) {
						pingcollectdata = new Pingcollectdata();
						String subentity = rs.getString("subentity");
						String sindex = rs.getString("sindex");
						String thevalue = rs.getString("thevalue");
						pingcollectdata.setCategory(rs.getString("entity"));
						pingcollectdata.setEntity(rs.getString("subentity"));
						pingcollectdata.setSubentity(rs.getString("sindex"));
						pingcollectdata.setThevalue(rs.getString("thevalue"));
						String collecttime = rs.getString("collecttime");
						Date date = sdf.parse(collecttime);
						Calendar calendar = Calendar.getInstance(); 
					    calendar.setTime(date); 
						pingcollectdata.setCollecttime(calendar);
						ipPingData.add(pingcollectdata);
						pingcollectdata = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					try {
						if(rs != null){
							rs.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				//取出内存信息
				StringBuffer sql = new StringBuffer();
				sql.append("select * from nms_memory_data_temp where nodeid = '");
				sql.append(nodeid);
				sql.append("' and type = '");
				sql.append(type);
				sql.append("' and subtype = '");
				sql.append(subtype);
				sql.append("'");
				try {
					//stmt = conn.createStatement();
					rs = dbmanager.executeQuery(sql.toString());
					while (rs.next()) {
						Memorycollectdata memorycollectdata = new Memorycollectdata();
						memorycollectdata.setEntity(rs.getString("subentity"));
						memorycollectdata.setSubentity(rs.getString("sindex"));
						memorycollectdata.setThevalue(rs.getString("thevalue"));
						memorycollectdata.setUnit(rs.getString("unit"));
						memoryVector.add(memorycollectdata);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally{
					try {
						if(rs != null){
							rs.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				//取出cpu信息
				StringBuffer cpuSqlBuffer = new StringBuffer();
				cpuSqlBuffer.append("select * from nms_cpu_data_temp where nodeid = '");
				cpuSqlBuffer.append(nodeid);
				cpuSqlBuffer.append("' and type = '");
				cpuSqlBuffer.append(type);
				cpuSqlBuffer.append("' and subtype = '");
				cpuSqlBuffer.append(subtype);
				cpuSqlBuffer.append("' and entity = 'CPU'");
				try {
					//stmt = conn.createStatement();
					rs = dbmanager.executeQuery(cpuSqlBuffer.toString());
					CPUcollectdata cpUcollectdata = new CPUcollectdata();
					while (rs.next()) {
						String subentity = rs.getString("subentity");
						String sindex = rs.getString("sindex");
						String thevalue = rs.getString("thevalue");
						cpUcollectdata.setThevalue(thevalue);
					}
					cpuV.add(cpUcollectdata);
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					try {
						if(rs != null){
							rs.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				//取出磁盘信息
				StringBuffer diskSqlBuffer = new StringBuffer();
				diskSqlBuffer.append(" select * from nms_disk_data_temp t where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
				//System.out.println(sql.toString());
				try {
					//stmt = conn.createStatement();
					rs = dbmanager.executeQuery(diskSqlBuffer.toString());
					while(rs.next()){
						Diskcollectdata diskcollectdata = new Diskcollectdata();
						diskcollectdata.setIpaddress(rs.getString("ip"));
						diskcollectdata.setCategory(rs.getString("entity"));
						diskcollectdata.setEntity(rs.getString("subentity"));
						diskcollectdata.setSubentity(rs.getString("sindex"));
						diskcollectdata.setThevalue(rs.getString("thevalue"));
						diskcollectdata.setChname(rs.getString("chname"));
						diskcollectdata.setRestype(rs.getString("restype"));
						diskcollectdata.setUnit(rs.getString("unit"));
						diskcollectdata.setBak(rs.getString("bak"));
						diskVector.add(diskcollectdata);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally{
					try {
						if(rs != null){
							rs.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				//取出端口流速信息  入口流速 出口流速 综合流速
				String utilhdxItems = "('AllInBandwidthUtilHdx','AllOutBandwidthUtilHdx','AllBandwidthUtilHdx')";
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("select distinct * from nms_interface_data_temp where nodeid = '");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and type = '");
				sqlBuffer.append(type);
				sqlBuffer.append("' and subtype = '");
				sqlBuffer.append(subtype);
				sqlBuffer.append("' and sindex in ");
				sqlBuffer.append(utilhdxItems);
				try {
					//stmt = conn.createStatement();
					rs = dbmanager.executeQuery(sqlBuffer.toString());
					while(rs.next()){
						String subentity = rs.getString("subentity");
						String thevalue = rs.getString("thevalue");
						String chname = rs.getString("chname");
						String restype = rs.getString("restype");
						String unit = rs.getString("unit");
						allUtilHdx = new AllUtilHdx();
						allUtilHdx.setSubentity(subentity);
						allUtilHdx.setChname(chname);
						allUtilHdx.setRestype(restype);
						allUtilHdx.setThevalue(thevalue);
						allUtilHdx.setUnit(unit);
						allutil.add(allUtilHdx);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally{
					try {
						if(rs != null){
							rs.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				//取出interface数据
				String netInterfaceItem = "('AllInBandwidthUtilHdx','AllOutBandwidthUtilHdx','AllBandwidthUtilHdx')";
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("select distinct * from nms_interface_data_temp where nodeid = '");
				sqlBuffer.append(nodeid);
				sqlBuffer.append("' and type = '");
				sqlBuffer.append(type);
				sqlBuffer.append("' and subtype = '");
				sqlBuffer.append(subtype);
				sqlBuffer.append("' and sindex in ");
				sqlBuffer.append(netInterfaceItem);
				try {
					//stmt = conn.createStatement();
					rs = dbmanager.executeQuery(sqlBuffer.toString());
					while(rs.next()){
						String subentity = rs.getString("subentity");
						String thevalue = rs.getString("thevalue");
						String chname = rs.getString("chname");
						String restype = rs.getString("restype");
						String unit = rs.getString("unit");
						String category = rs.getString("entity");
						interfacecollectdata = new Interfacecollectdata();
						interfacecollectdata.setSubentity(subentity);
						interfacecollectdata.setChname(chname);
						interfacecollectdata.setRestype(restype);
						interfacecollectdata.setThevalue(thevalue);
						interfacecollectdata.setUnit(unit);
						interfacecollectdata.setCategory(category);
						interfacecollectdata.setEntity(subentity);
						interfaceVector.add(interfacecollectdata);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally{
					try {
						if(rs != null){
							rs.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				//更新内存
				//加入ipPingData
				ShareData.getAllNetworkPingData().put(nodedto.getIpaddress(), ipPingData);
				Hashtable ipAllData = new Hashtable();
				//加入内存数据
				ipAllData.put("memory", memoryVector);
				//加入cpu数据
				ipAllData.put("cpu", cpuV);
				//加入磁盘数据
				ipAllData.put("disk", diskVector);
				//加入端口流速数据
				ipAllData.put("allutilhdx", allutil);
				//加入interface接口等数据
				ipAllData.put("interface", interfaceVector);
				ShareData.getAllNetworkData().put(nodedto.getIpaddress(), ipAllData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(dbmanager != null){
				try {
					dbmanager.close();
					dbmanager = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
     * 根据ids 批量修改 managesign 
     * @param managesignFrom 原始状态
     * @param managesignTo   批量更改后的状态 
     * @param ids 
     * @return
     */
    public boolean batchUpdataMoniterByIds(String managesignFrom, String managesignTo ,String[] ids){
    	DBManager conn = new DBManager();
    	if(ids == null){
    		return false;
    	}
    	try {
			for(String id : ids){
				if(id != null && !id.equals("")){
					String sql = "update topo_host_node set managed='"+ managesignTo + "' where id=" + id + " and managed = '" + managesignFrom +"'";
					conn.addBatch(sql);
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally{
			conn.close();
		}
    	return true;
    }
}
