package com.afunms.automation.dao;


import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.automation.model.CompStrategy;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
/**
 * 
 * @descrition 合规性策略
 * @author wangxiangyong
 * @date Sep 1, 2014 2:08:47 PM
 */
public class CompStrategyDao extends BaseDao implements DaoInterface{
    public  CompStrategyDao() {
		super("nms_comp_strategy");
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CompStrategy vo=new CompStrategy();
		try {
			vo.setId(rs.getInt("ID"));
			vo.setName(rs.getString("STRATEGY_NAME"));
            vo.setDescription(rs.getString("DESCRIPTION"));
            vo.setType(rs.getInt("COMP_TYPE"));
            vo.setViolateType(rs.getInt("STRATEGY_VIOLATE_TYPE"));
            vo.setGroupId(rs.getString("RULE_GROUP_ID"));
            vo.setCreateBy(rs.getString("CREATED_BY"));
            vo.setCreateTime(rs.getString("CREATED_TIME"));
            vo.setLastModifiedBy(rs.getString("LAST_MODIFIED_BY"));
            vo.setLastModifiedTime(rs.getString("LAST_MODIFIED_TIME"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		StringBuffer sql = new StringBuffer();
		CompStrategy stategry=(CompStrategy)vo;
		sql.append("insert into nms_comp_strategy(STRATEGY_NAME,DESCRIPTION,COMP_TYPE,STRATEGY_VIOLATE_TYPE,"+
				"RULE_GROUP_ID,CREATED_BY,CREATED_TIME,LAST_MODIFIED_BY,LAST_MODIFIED_TIME) values('");
		sql.append(stategry.getName());
		sql.append("','");
		sql.append(stategry.getDescription());
		sql.append("',");
		sql.append(stategry.getType());
		sql.append(",");
		sql.append(stategry.getViolateType());
		sql.append(",'");
		sql.append(stategry.getGroupId());
		sql.append("','");
		sql.append(stategry.getCreateBy());
		sql.append("','");
		sql.append(stategry.getCreateTime());
		sql.append("','");
		sql.append(stategry.getLastModifiedBy());
		sql.append("','");
		sql.append(stategry.getLastModifiedTime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		StringBuffer sql = new StringBuffer();
		CompStrategy stategry=(CompStrategy)vo;
		sql.append("update nms_comp_strategy set STRATEGY_NAME='");
		sql.append(stategry.getName());
		sql.append("',DESCRIPTION='");
		sql.append(stategry.getDescription());
		sql.append("',COMP_TYPE='");
		sql.append(stategry.getType());
		sql.append("',STRATEGY_VIOLATE_TYPE='");
		sql.append(stategry.getViolateType());
		sql.append("',RULE_GROUP_ID='");
		sql.append(stategry.getGroupId());
		sql.append("',LAST_MODIFIED_BY='");
		sql.append(stategry.getLastModifiedBy());
		sql.append("',LAST_MODIFIED_TIME='");
		sql.append(stategry.getLastModifiedTime());
		sql.append("' where ID="+stategry.getId());
		
		
		return saveOrUpdate(sql.toString());
	}
	 public boolean delete(String[] id)
	   {
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<id.length;i++){
		           conn.addBatch("delete from nms_comp_strategy where id=" + id[i]);
		           conn.addBatch("delete from nms_comp_strategy_device where STRATEGY_ID=" + id[i]);
		           conn.addBatch("delete from nms_comp_check_results where STRATEGY_ID=" + id[i]);
		       }
		       conn.executeBatch();
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("BaseDao.delete()",ex);
		       result = false;
		   }
		   return result;
	   }
}
