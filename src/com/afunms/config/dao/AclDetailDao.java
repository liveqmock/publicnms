package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.AclDetail;

public class AclDetailDao extends BaseDao implements DaoInterface{
    public AclDetailDao(){
    	super("sys_gather_acldetail");
    }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		AclDetail detail=new AclDetail();
		try {
			detail.setBaseId(rs.getInt("baseId"));
			detail.setName(rs.getString("name"));
			detail.setValue(rs.getInt("value"));
			detail.setMatches(rs.getInt("matches"));
			detail.setDesc(rs.getString("desciption"));
			detail.setStatus(rs.getInt("status"));
			detail.setCollecttime(rs.getString("collecttime"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return detail;
	}

	public boolean save(BaseVo vo) {
		AclDetail detail=(AclDetail)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into sys_gather_acldetail(baseId,name,value,matches,desciption,status,collecttime) values(");
		sql.append(detail.getBaseId());
		sql.append(",'");
		sql.append(detail.getName());
		sql.append("',");
		sql.append(detail.getValue());
		sql.append(",");
		sql.append(detail.getMatches());
		sql.append(",'");
		sql.append(detail.getDesc());
		sql.append("',");
		sql.append(detail.getStatus());
		sql.append(",'");
		sql.append(detail.getCollecttime());
		sql.append("')");
		
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
