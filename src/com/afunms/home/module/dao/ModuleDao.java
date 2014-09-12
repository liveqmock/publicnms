package com.afunms.home.module.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.home.module.model.ModuleModel;

public class ModuleDao extends BaseDao implements DaoInterface {
    public ModuleDao() {
	super("nms_home_module");
	// TODO Auto-generated constructor stub
    }

    @Override
    public BaseVo loadFromRS(ResultSet rs) {
	// TODO Auto-generated method stub
	ModuleModel model = new ModuleModel();
	try {
	    model.setId(rs.getInt("id"));
	    model.setEnName(rs.getString("enName"));
	    model.setChName(rs.getString("chName"));
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
	ModuleModel model = (ModuleModel) vo;
	StringBuffer sql = new StringBuffer(200);
	sql.append("insert into nms_home_module(enName, chName, note,type )values('");
	sql.append(model.getEnName());
	sql.append("','");
	sql.append(model.getChName());
	sql.append("','");
	sql.append(model.getNote());
	sql.append("','");
	sql.append(model.getType());
	sql.append("')");
	return saveOrUpdate(sql.toString());

    }

    public boolean update(BaseVo vo) {
	return saveOrUpdate(updateSql(vo));
    }

    public String updateSql(BaseVo vo) {
	// TODO Auto-generated method stub
	ModuleModel model = (ModuleModel) vo;
	StringBuffer sql = new StringBuffer(200);
	sql.append("update nms_homevisible set enName='");
	sql.append(model.getEnName());
	sql.append("',chName='");
	sql.append(model.getChName());
	sql.append("',note='");
	sql.append(model.getNote());
	sql.append("',type='");
	sql.append(model.getType());
	sql.append("' where id=");
	sql.append(model.getId());
	return sql.toString();
    }

}
