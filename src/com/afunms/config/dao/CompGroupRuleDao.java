package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;


import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.CompGroupRule;

public class CompGroupRuleDao extends BaseDao implements DaoInterface{
  public  CompGroupRuleDao() {
	  super("nms_comp_rule_group");
}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CompGroupRule vo=new CompGroupRule();
		try {
			vo.setId(rs.getInt("ID"));
			vo.setName(rs.getString("NAME"));
			vo.setDescription(rs.getString("DESCRIPTION"));
			vo.setDeviceType(rs.getString("DEVICE_TYPE"));
			vo.setRuleId(rs.getString("RULE_ID"));
			vo.setCreatedBy(rs.getString("CREATED_BY"));
			vo.setCreatedTime(rs.getString("CREATED_TIME"));
			vo.setLastModifiedBy(rs.getString("LAST_MODIFIED_BY"));
			vo.setLastModifiedTime(rs.getString("LAST_MODIFIED_TIME"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vo;
	}

	public boolean save(BaseVo vo) {
		StringBuffer sql = new StringBuffer();
		CompGroupRule rule=(CompGroupRule)vo;
		sql.append("insert into nms_comp_rule_group(NAME,DESCRIPTION,DEVICE_TYPE,RULE_ID,CREATED_BY,CREATED_TIME,LAST_MODIFIED_BY,LAST_MODIFIED_TIME)values('");
		sql.append(rule.getName());
		sql.append("','");
		sql.append(rule.getDescription());
		sql.append("','");
		sql.append(rule.getDeviceType());
		sql.append("','");
		sql.append(rule.getRuleId());
		sql.append("','");
		sql.append(rule.getCreatedBy());
		sql.append("','");
		sql.append(rule.getCreatedTime());
		sql.append("','");
		sql.append(rule.getLastModifiedBy());
		sql.append("','");
		sql.append(rule.getLastModifiedTime());
		sql.append("')");
		
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		StringBuffer sql = new StringBuffer();
		CompGroupRule rule=(CompGroupRule)vo;
		sql.append("update nms_comp_rule_group set NAME='");
		sql.append(rule.getName());
		sql.append("',DESCRIPTION='");
		sql.append(rule.getDescription());
		sql.append("',DEVICE_TYPE='");
		sql.append(rule.getDeviceType());
		sql.append("',RULE_ID='");
		sql.append(rule.getRuleId());
		sql.append("',LAST_MODIFIED_BY='");
		sql.append(rule.getLastModifiedBy());
		sql.append("',LAST_MODIFIED_TIME='");
		sql.append(rule.getLastModifiedTime());
		sql.append("' where ID="+rule.getId());
		
		return saveOrUpdate(sql.toString());
	}

}
