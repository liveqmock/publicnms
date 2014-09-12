package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Knowledge;

public class KnowledgeDAO extends BaseDao implements DaoInterface {

	public KnowledgeDAO() {
		super("system_knowledge");
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Knowledge vo = new Knowledge();

		try {
			vo.setId(rs.getInt("id"));
			vo.setCategory(rs.getString("category"));
			vo.setEntity(rs.getString("entity"));
			vo.setSubentity(rs.getString("subentity"));
			vo.setAttachfiles(rs.getString("attachfiles"));
			vo.setBak(rs.getString("bak"));
		} catch (Exception e) {
			SysLogger.error("Error in KnowledgeDAO.loadFromRS()", e);
			vo = null;
		}
		return vo;
	}

	public List loadAll() {
		List list = new ArrayList(5);

		try {
			rs = conn
					.executeQuery("select * from system_knowledge order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			SysLogger.error("KnowledgeDAO:loadAll()", e);
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}
	//----------------事件处理方法----------------
	public String findforevent(String eventid) {
		String event_category = null;
		String event_entity = null;
		String event_subentity = null;
		String attachfiles=null;
		int ostype =0;
		int nodeid = 0 ;
		try {
			rs = conn.executeQuery("select * from system_eventlist where id='"
					+ eventid + "';");
			while (rs.next()) {
				event_category = rs.getString("subtype");
				event_subentity = rs.getString("subentity");
				nodeid = rs.getInt("nodeid");
			}
			rs = conn.executeQuery("select * from topo_host_node where id='"
					+ nodeid + "';");
			while (rs.next()) {
				ostype = rs.getInt("ostype");
			}
			rs=conn.executeQuery("select * from system_ostype where ostypeid='"+ostype+"';");
			while(rs.next()){
				event_entity=rs.getString("ostypename");
			}
		} catch (Exception e) {
			conn.close();
		}
		StringBuffer sql = new StringBuffer();
				sql.append("select attachfiles from system_knowledge where category='");
				sql.append(event_category);
				sql.append("' and entity='");
				sql.append(event_entity);
				sql.append("' and subentity='");
				sql.append(event_subentity);
				sql.append("';");
				SysLogger.info(sql.toString());
				try {
					rs = conn.executeQuery(sql.toString());
					while (rs.next()) {
						attachfiles = rs.getString("attachfiles");
					}
				} catch (Exception e) {
				}
				return attachfiles;
	}

	public String selectcontent(){
		ResultSet rs1=null,rs2=null,rs3=null;
		List selectlist1=new ArrayList();
		List selectlist2=new ArrayList();
		List selectlist3=new ArrayList();
		String subtype=null;
		String type=null;
		String name=null;
		String res=null;
		StringBuffer myselect=new StringBuffer();
		rs1=conn.executeQuery("select type from nms_alarm_indicators group by type;");
		try {
			while (rs1.next()) {
				selectlist1.add(rs1.getString(1));
			}
			for(int i=0;i<selectlist1.size();i++){
				type=(String)selectlist1.get(i);
				myselect.append(type+"$");
				rs2=conn.executeQuery("select subtype from nms_alarm_indicators where type='"+type+"' group by subtype");
				while(rs2.next()){
					selectlist2.add(rs2.getString(1));
				}
				for(int j=0;j<selectlist2.size();j++){
					subtype=(String) selectlist2.get(j);
					myselect.append(subtype+",");
				rs3=conn.executeQuery("select name from nms_alarm_indicators where subtype='"+subtype+"' group by name");
				while(rs3.next()){
					selectlist3.add(rs3.getString(1));
				}
				for(int n=0;n<selectlist3.size();n++){
					name=(String) selectlist3.get(n);
					myselect.append(name+",");
				}
				myselect.append("|");
				selectlist3.clear();
				}
				myselect.append("#");
				selectlist2.clear();
			}
			String n=myselect.toString();
			String m=n.replace(",|#", "#");
			String p=m.replace(",|", "|");
			int leng=p.length()-1;
			res=p.substring(0, leng);
		} catch (Exception e) {
		}finally{
			try{
				if(rs1 != null)rs1.close();
				if(rs2 != null)rs2.close();
				if(rs3 != null)rs3.close();
			}catch(Exception e){
				
			}
		}
		return res;
	}
	
	
	// ---------------save----------------
	public boolean save(BaseVo baseVo) {
		Knowledge vo = (Knowledge) baseVo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into system_knowledge(category,entity,subentity,attachfiles,bak)values(");
		sql.append("'");
		sql.append(vo.getCategory());
		sql.append("','");
		sql.append(vo.getEntity());
		sql.append("','");
		sql.append(vo.getSubentity());
		sql.append("','");
		sql.append(vo.getAttachfiles());
		sql.append("','");
		sql.append(vo.getBak());
		sql.append("');");

		return saveOrUpdate(sql.toString());
	}

	// ---------------update----------------
	public boolean update(BaseVo baseVo) {
		Knowledge vo = (Knowledge) baseVo;

		StringBuffer sql = new StringBuffer();
		sql.append("update system_knowledge set category='");
		sql.append(vo.getCategory());
		sql.append("',entity='");
		sql.append(vo.getEntity());
		sql.append("',subentity='");
		sql.append(vo.getSubentity());
		sql.append("',attachfiles='");
		sql.append(vo.getAttachfiles());
		sql.append("',bak='");
		sql.append(vo.getBak());
		sql.append("' where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}

	// ---------------delete----------------
	public boolean delete(String id) {
		boolean result = false;

		try {
			conn.addBatch("delete from system_knowledge where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			SysLogger.error("KnowledgeDAO.delete()", e);
		} finally {
			conn.close();
		}
		return result;
	}

}
