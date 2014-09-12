package com.afunms.automation.dao;


import java.sql.ResultSet;

import com.afunms.automation.model.CompRule;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class CompRuleDao extends BaseDao implements DaoInterface {

	public CompRuleDao() {
		super("nms_comp_rule");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CompRule vo = new CompRule();
		try {
			vo.setId(rs.getInt("ID"));
			vo.setDevice_type(rs.getString("DEVICE_TYPE"));
			vo.setComprule_name(rs.getString("COMPRULE_NAME"));
			vo.setDescription(rs.getString("DESCRIPTION"));
			vo.setViolation_severity(rs.getInt("VIOLATION_SEVERITY"));
			vo.setSelect_type(rs.getInt("SELECT_TYPE"));
			vo.setRemediation_descr(rs.getString("REMEDIATION_DESCR"));
			vo.setCreated_by(rs.getString("CREATED_BY"));
			vo.setCreate_time(rs.getString("CREATED_TIME"));
			vo.setLast_modified_by(rs.getString("LAST_MODIFIED_BY"));
			vo.setLast_modified_time(rs.getString("LAST_MODIFIED_TIME"));
			vo.setRule_content(rs.getString("RULE_CONTENT"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		StringBuffer sql = new StringBuffer();
		CompRule compRule = (CompRule) vo;
		sql.append("insert into nms_comp_rule(DEVICE_TYPE,COMPRULE_NAME,DESCRIPTION,VIOLATION_SEVERITY,SELECT_TYPE,"+
				"REMEDIATION_DESCR,CREATED_BY,CREATED_TIME,LAST_MODIFIED_BY,LAST_MODIFIED_TIME,RULE_CONTENT)values('");
		sql.append(compRule.getDevice_type());
		sql.append("','");
		sql.append(compRule.getComprule_name());
		sql.append("','");
		sql.append(compRule.getDescription());
		sql.append("',");
		sql.append(compRule.getViolation_severity());
		sql.append(",");
		sql.append(compRule.getSelect_type());
		sql.append(",'");
		sql.append(compRule.getRemediation_descr());
		sql.append("','");
		sql.append(compRule.getCreated_by());
		sql.append("','");
		sql.append(compRule.getCreate_time());
		sql.append("','");
		sql.append(compRule.getLast_modified_by());
		sql.append("','");
		sql.append(compRule.getLast_modified_time());
		sql.append("','");
		sql.append(compRule.getRule_content());
		sql.append("')");
		System.out.println(sql.toString());
		 return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {

		StringBuffer sql = new StringBuffer();
		CompRule compRule = (CompRule) vo;
		sql.append("update nms_comp_rule set DEVICE_TYPE='");
		
		sql.append(compRule.getDevice_type());
		sql.append("', COMPRULE_NAME='");
		sql.append(compRule.getComprule_name());
		sql.append("',DESCRIPTION='");
		sql.append(compRule.getDescription());
		sql.append("',VIOLATION_SEVERITY=");
		sql.append(compRule.getViolation_severity());
		sql.append(",SELECT_TYPE=");
		sql.append(compRule.getSelect_type());
		sql.append(",REMEDIATION_DESCR='");
		sql.append(compRule.getRemediation_descr());
		
		sql.append("',LAST_MODIFIED_BY='");
		sql.append(compRule.getLast_modified_by());
		sql.append("',LAST_MODIFIED_TIME='");
		sql.append(compRule.getLast_modified_time());
		sql.append("',RULE_CONTENT='");
		sql.append(compRule.getRule_content());
		sql.append("' where id="+compRule.getId());
		 return saveOrUpdate(sql.toString());
	
	}
  
}
