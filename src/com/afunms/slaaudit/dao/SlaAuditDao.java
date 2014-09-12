package com.afunms.slaaudit.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.slaaudit.model.SlaAudit;

public class SlaAuditDao extends BaseDao implements DaoInterface {
    public SlaAuditDao() {
	super("nms_sla_audit");
    }

    @Override
    public BaseVo loadFromRS(ResultSet rs) {
	// TODO Auto-generated method stub
    	SlaAudit model = new SlaAudit();
	try {
	    model.setId(rs.getInt("id"));
	    model.setUserid(rs.getInt("userid"));
	    model.setTelnetconfigid(rs.getInt("telnetconfigid"));
	    model.setSlatype(rs.getString("slatype"));
	    model.setOperation(rs.getString("operation"));
	    model.setCmdcontent(rs.getString("cmdcontent"));
	    model.setDotime(rs.getString("dotime"));
	    model.setDostatus(rs.getInt("dostatus"));
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return model;
    }

    public boolean save(BaseVo vo) {
	// 保存
    SlaAudit model = (SlaAudit) vo;
    int id=this.getNextID();
	StringBuffer sql = new StringBuffer(200);
	sql.append("insert into nms_sla_audit(id,userid, telnetconfigid, slatype,operation,cmdcontent,dotime,dostatus )values(");
	sql.append(id);
	sql.append(",");
	sql.append(model.getUserid());
	sql.append(",");
	sql.append(model.getTelnetconfigid());
	sql.append(",'");
	sql.append(model.getSlatype());
	sql.append("','");
	sql.append(model.getOperation());
	sql.append("','");
	sql.append(model.getCmdcontent());
	sql.append("','");
	sql.append(model.getDotime());
	sql.append("',");
	sql.append(model.getDostatus());
	sql.append(")");
	return saveOrUpdate(sql.toString());

    }

    public boolean update(BaseVo vo) {
	return saveOrUpdate(updateSql(vo));
    }

    public String updateSql(BaseVo vo) {
	//更新，修改
    SlaAudit model = (SlaAudit) vo;
	StringBuffer sql = new StringBuffer(200);
	sql.append("update nms_sla_audit set userid='");
	sql.append(model.getUserid());
	sql.append("',telnetconfigid='");
	sql.append(model.getTelnetconfigid());
	sql.append("',slatype='");
	sql.append(model.getSlatype());
	sql.append("',operation='");
	sql.append(model.getOperation());
	sql.append("',cmdcontent='");
	sql.append(model.getCmdcontent());
	sql.append("',dotime='");
	sql.append(model.getDotime());
	sql.append("' where id=");
	sql.append(model.getId());
	return sql.toString();
    }
	 /**
	  * 根据id删除这条记录
	  * @param id
	  * @return
	  */
	  public boolean delete(String id)
	   {
		   boolean result = false;
		   try
		   {
			   conn.addBatch("delete from nms_sla_audit where id=" + id);
			   conn.executeBatch();
			   result = true;
		   }
		   catch(Exception e)
		   {
			   SysLogger.error("SlaAuditDao.delete()",e); 
		   }
		   finally
		   {
			  // conn.close();
		   }
		   return result;
	   }
	public boolean deleteWhere(String where){
    //删除
		String sql = "delete from nms_sla_audit"+ where;
		return saveOrUpdate(sql.toString());
	}
	public List getSelectWhere(String where){
	//查询
		String sql = "select * from nms_sla_audit "
			+ where + " order by dotime asc"; 
		List list = findByCriteria(sql);
		return list;
	}
}