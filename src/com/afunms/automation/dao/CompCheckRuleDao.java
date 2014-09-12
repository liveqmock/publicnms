package com.afunms.automation.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.automation.model.CompCheckRule;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class CompCheckRuleDao extends BaseDao implements DaoInterface{
	public CompCheckRuleDao() {
		super("nms_comp_check_rule");
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {

		CompCheckRule vo=new CompCheckRule();
		try {
			vo.setId(rs.getInt("ID"));
			vo.setStrategyId(rs.getInt("STRATEGY_ID"));
			vo.setGroupId(rs.getInt("GROUP_ID"));
			vo.setRuleId(rs.getInt("RULE_ID"));
			vo.setIp(rs.getString("IP"));
			vo.setIsViolation(rs.getInt("ISVIOLATION"));
			vo.setRelation(rs.getInt("RELATION"));
			vo.setIsContain(rs.getInt("ISCONTAIN"));
			vo.setContent(rs.getString("CONTENT"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
   public List loadByCondition(int strategyId,int groupId,int ruleId,String ip) {
	   String condition=" where STRATEGY_ID="+strategyId+" and GROUP_ID="+groupId+" and RULE_ID="+ruleId+" and IP='"+ip+"'";
	   return findByCondition(condition);
}
}
