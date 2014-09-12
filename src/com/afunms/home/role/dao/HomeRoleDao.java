package com.afunms.home.role.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.home.role.model.HomeRoleModel;

public class HomeRoleDao extends BaseDao implements DaoInterface {

    public HomeRoleDao() {
	super("nms_home_module_role");
	// TODO Auto-generated constructor stub
    }

    @Override
    public BaseVo loadFromRS(ResultSet rs) {
	// TODO Auto-generated method stub
	HomeRoleModel model = new HomeRoleModel();
	try {
	    model.setId(rs.getInt("id"));
	    model.setEnName(rs.getString("enName"));
	    model.setChName(rs.getString("chName"));
	    model.setRole_id(rs.getInt("role_id"));
	    model.setDept_id(rs.getInt("dept_id"));
	    model.setVisible(rs.getInt("visible"));
	    model.setNote(rs.getString("note"));
	    model.setType(rs.getInt("type"));
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return model;
    }

    public boolean save(BaseVo vo) {
	// TODO Auto-generated method stub
	HomeRoleModel model = (HomeRoleModel) vo;
	StringBuffer sql = new StringBuffer(200);
	sql.append("insert into nms_home_module_role(id,enName, chName, role_id, dept_id, visible, note,type)values('");
	sql.append(model.getId());
	sql.append("','");
	sql.append(model.getEnName());
	sql.append("','");
	sql.append(model.getChName());
	sql.append("','");
	sql.append(model.getRole_id());
	sql.append("','");
	sql.append(model.getDept_id());
	sql.append("','");
	sql.append(model.getVisible());
	sql.append("','");
	sql.append(model.getNote());
	sql.append("','");
	sql.append(model.getType());
	sql.append("')");
	// System.out.println(sql.toString());
	return saveOrUpdate(sql.toString());

    }

    public boolean update(BaseVo vo) {
	return saveOrUpdate(updateSql(vo));
    }
    public String saveSql(BaseVo vo){
	HomeRoleModel model = (HomeRoleModel) vo;
	StringBuffer sql = new StringBuffer(200);
	sql.append("insert into nms_home_module_role(id,enName, chName, role_id, dept_id, visible, note,type)values('");
	sql.append(model.getId());
	sql.append("','");
	sql.append(model.getEnName());
	sql.append("','");
	sql.append(model.getChName());
	sql.append("','");
	sql.append(model.getRole_id());
	sql.append("','");
	sql.append(model.getDept_id());
	sql.append("','");
	sql.append(model.getVisible());
	sql.append("','");
	sql.append(model.getNote());
	sql.append("','");
	sql.append(model.getType());
	sql.append("')");
	return sql.toString();
    }
    public String deleteSql(BaseVo vo){
	
	HomeRoleModel model = (HomeRoleModel) vo;
	StringBuffer sql=new StringBuffer(200); 
	sql.append("delete from nms_home_module_role ");
	sql.append(" where id='"+model.getId()+"'");
	sql.append(" and role_id='"+model.getRole_id()+"'"); 
	return sql.toString();
    }
    public String updateSql(BaseVo vo) {
	// TODO Auto-generated method stub
	HomeRoleModel model = (HomeRoleModel) vo;
	StringBuffer sql = new StringBuffer(200);
	sql.append("update nms_home_module_role set enName='");
	sql.append(model.getEnName());
	sql.append("',chName='");
	sql.append(model.getChName());
	sql.append("',role_id='");
	sql.append(model.getRole_id());
	sql.append("',dept_id='");
	sql.append(model.getDept_id());
	sql.append("',visible='");
	sql.append(model.getVisible());
	sql.append("',note='");
	sql.append(model.getNote());
	sql.append("',type='");
	sql.append(model.getType());
	sql.append("' where id=");
	sql.append(model.getId()); 
	return sql.toString();
    }

    public Hashtable findHashtable(String condition) {

	Hashtable result = new Hashtable();
	List list = findByCondition(condition);
	if (list != null) {
	    for (int i = 0; i < list.size(); i++) {
		HomeRoleModel model = (HomeRoleModel) list.get(i);
		result.put("" + model.getChName(), model.getVisible());
	    }
	}

	return result;

    }

}
