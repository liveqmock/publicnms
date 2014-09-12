package com.afunms.home.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.home.user.model.HomeUserModel;

public class HomeUserDao extends BaseDao implements DaoInterface {

    public HomeUserDao() {
	super("nms_home_module_user");
	// TODO Auto-generated constructor stub
    }

    @Override
    public BaseVo loadFromRS(ResultSet rs) {
	// TODO Auto-generated method stub
	HomeUserModel model = new HomeUserModel();
	try {
	    model.setId(rs.getInt("id"));
	    model.setName(rs.getString("name"));
	    model.setChName(rs.getString("chName"));
	    model.setEnName(rs.getString("enName"));
	    model.setUser_id(rs.getString("user_id"));
	    model.setRole_id(rs.getInt("role_id"));
	    model.setDept_id(rs.getInt("dept_id"));
	    model.setVisible(rs.getInt("visible"));
	    model.setBusinessids(rs.getString("businessids"));
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
	String sql = saveSql(vo);
	// System.out.println(sql.toString());
	return saveOrUpdate(sql);

    }

    public String saveSql(BaseVo vo) {
	HomeUserModel model = (HomeUserModel) vo;
	StringBuffer sql = new StringBuffer(200);
	sql.append("insert into nms_home_module_user(name,chName,enName, user_id, role_id, dept_id, visible, businessids,note,type)values('");
	sql.append(model.getName());
	sql.append("','");
	sql.append(model.getChName());
	sql.append("','");
	sql.append(model.getEnName());
	sql.append("','");
	sql.append(model.getUser_id());
	sql.append("','");
	sql.append(model.getRole_id());
	sql.append("','");
	sql.append(model.getDept_id());
	sql.append("','");
	sql.append(model.getVisible());
	sql.append("','");
	sql.append(model.getBusinessids());
	sql.append("','");
	sql.append(model.getNote());
	sql.append("','");
	sql.append(model.getType());
	sql.append("')");
	return sql.toString();
    }

    public boolean update(BaseVo vo) {
	return saveOrUpdate(updateSql(vo));
    }

    public String updateSql(BaseVo vo) {
	// TODO Auto-generated method stub
	HomeUserModel model = (HomeUserModel) vo;
	StringBuffer sql = new StringBuffer(200);
	sql.append("update nms_home_module_user set name='");
	sql.append(model.getName());
	sql.append("',chName='");
	sql.append(model.getChName());
	sql.append("',enName='");
	sql.append(model.getEnName());
	sql.append("',user_id='");
	sql.append(model.getUser_id());
	sql.append("',role_id='");
	sql.append(model.getRole_id());
	sql.append("',dept_id='");
	sql.append(model.getDept_id());
	sql.append("',visible='");
	sql.append(model.getVisible());
	sql.append("',businessids='");
	sql.append(model.getBusinessids());
	sql.append("',note='");
	sql.append(model.getNote());
	sql.append("',type='");
	sql.append(model.getType());
	sql.append("' where id=");
	sql.append(model.getId());
	return sql.toString();
    }
    public String updateSql(BaseVo vo,String whereSql) {
	// TODO Auto-generated method stub
	HomeUserModel model = (HomeUserModel) vo;
	StringBuffer sql = new StringBuffer(200);
	sql.append("update nms_home_module_user set name='");
	sql.append(model.getName());
	sql.append("',chName='");
	sql.append(model.getChName());
	sql.append("',enName='");
	sql.append(model.getEnName());
	sql.append("',user_id='");
	sql.append(model.getUser_id());
	sql.append("',role_id='");
	sql.append(model.getRole_id());
	sql.append("',dept_id='");
	sql.append(model.getDept_id());
	sql.append("',visible='");
	sql.append(model.getVisible());
	sql.append("',businessids='");
	sql.append(model.getBusinessids());
	sql.append("',note='");
	sql.append(model.getNote());
	sql.append("',type='");
	sql.append(model.getType());
	sql.append("' "); 
	sql.append(whereSql);
	
	return sql.toString();
    }
     

    public Hashtable findHashtable(String condition) {

	Hashtable result = new Hashtable();
	List list = findByCondition(condition);
	if (list != null) {
	    for (int i = 0; i < list.size(); i++) {
		HomeUserModel model = (HomeUserModel) list.get(i);
		result.put("" + model.getChName(), model.getVisible());
	    }
	}

	return result;

    }


}
