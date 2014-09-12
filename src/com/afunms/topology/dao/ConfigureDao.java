package com.afunms.topology.dao;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.ConfigureNode;


public class ConfigureDao extends BaseDao implements DaoInterface {
	
	public ConfigureDao() {
		super("nms_manage_nodeconfigure");
	}
	public void delete(String id) {
		try {
			conn.executeUpdate("delete from nms_manage_nodeconfigure where id="+ id );
		} catch (Exception ex) {
			SysLogger.error("Error in ConfigureDao.delete()", ex);
		} finally {
			conn.close();
		}
	}

	public boolean deletenode(int id) {
		StringBuffer sql = new StringBuffer(200);
		sql.append("delete from nms_manage_nodeconfigure where id ="+id );
		return saveOrUpdate(sql.toString());
		
	}
	
	public boolean save(BaseVo baseVo) {
		ConfigureNode vo = (ConfigureNode) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_manage_nodeconfigure(id,text,father_id,descn)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getText());
		sql.append("',");
		sql.append(vo.getFatherId());
		sql.append(",'");
		sql.append(vo.getDescn());
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}
	
	public boolean insertNode(String text,String descn){
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_manage_nodeconfigure(text,father_id,descn)values('"+text+"',0,'"+descn+"')");
		return saveOrUpdate(sql.toString());
	}
	
	public boolean insertChildNode(String text,String descn,int father_id){
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_manage_nodeconfigure(text,father_id,descn)values('"+text+"',"+father_id+",'"+descn+"')");
		return saveOrUpdate(sql.toString());
	}
 
	public boolean saveFather(BaseVo baseVo) {
		ConfigureNode vo = (ConfigureNode) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_manage_nodeconfigure(id,text,father_id,descn)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getText());
		sql.append("',0");
		sql.append(",'");
		sql.append(vo.getDescn());
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}
	
	public boolean update(BaseVo baseVo) {
		ConfigureNode vo = (ConfigureNode) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_manage_nodeconfigure set name='");
		sql.append(vo.getName());
		sql.append("',text='");
		sql.append(vo.getText());
		sql.append("',father_id=");
		sql.append(vo.getFatherId());
		sql.append(",descn='");
		sql.append(vo.getDescn());
		sql.append("' where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}
	
	public boolean modifynode(String text,String descn,int nodeid){
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_manage_nodeconfigure set text='"+text+"',descn='"+descn+"' where id="+nodeid);
		return saveOrUpdate(sql.toString());
	}
	
	public List getAllString(){
		List list = new ArrayList();
		try{
			 rs=conn.executeQuery("select * from nms_manage_nodeconfigure where 1=1");
			 while(rs.next())
				  list.add(loadFromRS(rs)); 
		}catch(Exception ex){
			list=null;
			SysLogger.error("Error in ConfigureDao.delete()", ex);
		}
		return list;
	}
	
	public List getNodeString(int pid){
		List list=new ArrayList();
		try{
			rs=conn.executeQuery("select * from nms_manage_nodeconfigure where id="+pid);
			while(rs.next())
				  list.add(loadFromRS(rs)); 
		}catch(Exception ex){
			list=null;
			SysLogger.error("Error in ConfigureDao.delete()",ex);
		}
		return list;
	}
	
	public void close()
	   {
	       conn.close();
	   }
	
	public List findByFatherId(int fatherId) {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from nms_manage_nodeconfigure where father_id=" + fatherId);
			while(rs.next())
				  list.add(loadFromRS(rs));	
		} catch (Exception ex) {
			list = null;
			SysLogger.error("ConfigureDao.findByFatherId()", ex);
		}
		return list;
	}
	
	public BaseVo loadFromRS(ResultSet rs) {
		ConfigureNode vo = new ConfigureNode();
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setText(rs.getString("text"));
			vo.setFatherId(rs.getInt("father_id"));
			vo.setDescn(rs.getString("descn"));
		} catch (Exception e) {
			SysLogger.error("ConfigureDao.loadFromRS()", e);
		}
		return vo;
	}
}