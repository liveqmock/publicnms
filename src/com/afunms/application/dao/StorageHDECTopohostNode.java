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
public class StorageHDECTopohostNode extends BaseDao implements DaoInterface {

	public StorageHDECTopohostNode() {
		super("topo_host_node");
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
			storage.setCommunity(rs.getString("community"));
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
		return false;
	}

//	public boolean topohostsave(BaseVo vo) {
//		// TODO Auto-generated method stub
//		Storage storage = (Storage)vo;
//		StringBuffer sql = new StringBuffer();
//		System.out.println("sql()====------qwe"+sql);
//		sql.append("insert into topo_host_node(id,ip_address,ip_long,sys_name," +
//				   "alias,net_mask,sys_descr,sys_oid,community,write_community," +
//				   "category,managed,type,super_node,local_net,layer," +
//				   "bridge_address,status,discoverstatus,sys_location,sys_contact,snmpversion,collecttype," +
//				   "ostype,sendmobiles,sendemail,bid,endpoint,sendphone,supperid,transfer,asset_id,location)values('");
//		sql.append(storage.getId());
//		sql.append("','");
//		sql.append(storage.getIpaddress());	
//		sql.append("','");
//		sql.append("104");
//		sql.append("','");
//		sql.append(storage.getSys_name());
//		sql.append("','");
//		sql.append(storage.getName());
//		sql.append("','");
//		sql.append(storage.getNet_mask());
//		sql.append("','");
//		sql.append(storage.getSys_descr());
//		sql.append("','");
//		sql.append(storage.getSys_oid());
//		sql.append("','");
//		sql.append(storage.getCommunity());
//		sql.append("','");
//		sql.append(storage.getWrite_community());
//		sql.append("','");
//		sql.append("109");
//		sql.append("','");
//		sql.append(storage.getMon_flag());
//		sql.append("','");
//		sql.append(storage.getType());
//		sql.append("','");
//		sql.append("0");
//		sql.append("','");
//		sql.append("0");
//		sql.append("','");
//		sql.append("0");
//		sql.append("','");
//		sql.append(storage.getBridge_address());	
//		sql.append("','");
//		sql.append("0");
//		sql.append("','");
//		sql.append("0");
//		sql.append("','");
//		sql.append(storage.getSys_location());
//		sql.append("','");
//		sql.append(storage.getSys_contact());
//		sql.append("','");
//		sql.append(storage.getSnmpversion());
//		sql.append("','");
//		sql.append(storage.getCollecttype());
//		sql.append("','");
//		sql.append("0");
//		sql.append("','");
//		sql.append(storage.getSendmobiles());
//		sql.append("','");
//		sql.append(storage.getSendemail());
//		sql.append("','");
//		sql.append(storage.getBid());
//		sql.append("','");
//		sql.append("0");
//		sql.append("','");
//		sql.append(storage.getSendphone());
//		sql.append("','");
//		sql.append("0");
//		sql.append("','");
//		sql.append("0");
//		sql.append("','");
//		sql.append(storage.getAsset_id());
//		sql.append("','");
//		sql.append(storage.getLocation());
//		sql.append("')");
//		return saveOrUpdate(sql.toString());
//	}
	
	
	

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
	public List findByMon_flagtopo(String managed){
		String sql = "select * from topo_host_node where managed='" + managed + "'";
		return findByCriteria(sql);
	}
	
	public boolean updateMon_flagtopo(String managed , String id){
		String sql = "update topo_host_node set managed='" + managed + "' where id ='" + id + "'";
		return saveOrUpdate(sql.toString());
	}

	/**
	    * 删除一批记录
	    */
	   public String topodelete(String id)
	   {
		  String sql="delete  from  topo_host_node where id='" + id+"'";
		  System.out.print("-------------sql===========--------------123"+ sql);    
		   return sql;
	   }
	
	   /**
	    * 删除一批记录
	    */
	   public String nmsdelete(String nodeid)
	   {
		  String sql="delete  from  nms_gather_indicators_node where nodeid='" + nodeid+"'";
		  System.out.print("-------------sql===========--------------123"+ sql);    
		   return sql;
	   }
	   /**
	    * 删除一批记录
	    */
	   public String nmsalarmindicatorsnodedelete(String nodeid)
	   {
		  String sql="delete  from  nms_alarm_indicators_node where nodeid='" + nodeid+"'";
		  System.out.print("-------------sql===========--------------123"+ sql);    
		   return sql;
	   }
	
}