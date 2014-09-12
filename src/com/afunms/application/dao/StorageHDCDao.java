/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;


import com.afunms.application.model.Storage;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.topology.util.KeyGenerator;

/**
 * 
 * @author hkmw
 * 
 * 此类为存储的Dao类 , 对表 nms_storage 进行操作
 *
 */
public class StorageHDCDao extends BaseDao implements DaoInterface {

	public StorageHDCDao() {
		super("nms_storage");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		Storage storage = new Storage();
		try {
			storage.setId(rs.getInt("id"));
			storage.setIpaddress(rs.getString("ipaddress"));
			storage.setName(rs.getString("name"));
			storage.setUsername(rs.getString("username"));
			storage.setSnmpversion(rs.getString("snmpversion"));
			storage.setMon_flag(rs.getString("mon_flag"));
			storage.setStatus(rs.getString("status"));
			storage.setCollecttype(rs.getString("collecttype"));
			storage.setCompany(rs.getString("company"));
			storage.setType(rs.getString("type"));
			storage.setSerialNumber(rs.getString("serial_number"));
			storage.setBid(rs.getString("bid"));
			storage.setCollectTime(rs.getString("collectTime"));
			storage.setSupperid(rs.getString("supperid"));
			storage.setSendemail(rs.getString("sendemail"));
			storage.setSendmobiles(rs.getString("sendmobiles"));
			storage.setSendphone(rs.getString("sendphone"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			SysLogger.error("StroageDao.loadFromRS()",e);
			e.printStackTrace();
		}
		return storage;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		Storage storage = (Storage)vo;
		StringBuffer sql = new StringBuffer();
		System.out.println("sql()====------qwe"+sql);
		sql.append("insert into nms_storagehd(id,ipaddress,name,username,snmpversion,status,mon_flag,community,collecttype,type,serial_number,bid,collecttime,supperid,sendemail,sendmobiles,sendphone)values('");
		sql.append(storage.getId());
		sql.append("','");
		sql.append(storage.getIpaddress());	
		sql.append("','");
		sql.append(storage.getName());
		sql.append("','");
		sql.append(storage.getUsername());
		sql.append("','");
		sql.append(storage.getSnmpversion());
		sql.append("','");
		sql.append(storage.getStatus());
		sql.append("','");
		sql.append(storage.getMon_flag());
		sql.append("','");
		sql.append(storage.getCommunity());
		sql.append("','");
		sql.append(storage.getCollecttype());
		sql.append("','");
		sql.append(storage.getType());
		sql.append("','");
		sql.append(storage.getSerialNumber());
		sql.append("','");
		sql.append(storage.getBid());
		sql.append("','");
		sql.append(storage.getCollectTime());
		sql.append("','");
		sql.append(storage.getSupperid());
		sql.append("','");
		sql.append(storage.getSendemail());
		sql.append("','");
		sql.append(storage.getSendmobiles());
		sql.append("','");
		sql.append(storage.getSendphone());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
	

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		Storage storage = (Storage)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_storagehd set ipaddress='");
		sql.append(storage.getId());
		sql.append("',ipaddress='");
		sql.append(storage.getIpaddress());	
		sql.append("',name='");
		sql.append(storage.getName());
		sql.append("',username='");
		sql.append(storage.getUsername());
		sql.append("',snmpversion='");
		sql.append(storage.getSnmpversion());
		sql.append("',status='");
		sql.append(storage.getStatus());
		sql.append("',mon_flag='");
		sql.append(storage.getMon_flag());
		sql.append("',collecttype='");
		sql.append(storage.getCollecttype());
		sql.append("',collecttype='");
		sql.append(storage.getCommunity());
		sql.append("',type='");
		sql.append(storage.getType());
		sql.append("',serial_number='");
		sql.append(storage.getSerialNumber());
		sql.append("',bid='");
		sql.append(storage.getBid());
		sql.append("',collecttime='");
		sql.append(storage.getCollectTime());
		sql.append("',supperid='");
		sql.append(storage.getSupperid());
		sql.append("',sendemail='");
		sql.append(storage.getSendemail());
		sql.append("',sendmobiles='");
		sql.append(storage.getSendmobiles());
		sql.append("',sendphone='");
		sql.append(storage.getSendphone());
		sql.append("' where id=");
		sql.append(storage.getId());
		//System.out.println(sql.toString()+"=======================================");
		return saveOrUpdate(sql.toString());
	}
	
	public List findByMon_flag(String mon_flag){
		String sql = "select * from nms_storagehd where mon_flag='" + mon_flag + "'";
		return findByCriteria(sql);
	}
	
	public boolean updateMon_flag(String mon_flag , String id){
		String sql = "update nms_storagehd set mon_flag='" + mon_flag + "' where id ='" + id + "'";
		return saveOrUpdate(sql.toString());
	}
	/**
	    * 删除一批记录
	    */
	   public boolean delete(String[] id)
	   {
		   
		   StorageHDECTopohostNode topostorageDao = new StorageHDECTopohostNode();
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<id.length;i++)
		       {
		    	   
		    	   System.out.println("==="+id[i]);
		           conn.addBatch("delete from nms_storagehd where id='"  + id[i]+"'");
		           conn.addBatch(topostorageDao.topodelete(id[i]));
		           conn.addBatch(topostorageDao.nmsdelete(id[i]));
		           conn.addBatch(topostorageDao.nmsalarmindicatorsnodedelete(id[i]));
		           
		       }
		       conn.executeBatch();
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("BaseDao.delete()",ex);
		       result = false;
		   }
		   return result;
	   }
	
	
}