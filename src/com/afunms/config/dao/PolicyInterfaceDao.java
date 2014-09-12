package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.PolicyInterface;

public class PolicyInterfaceDao extends BaseDao implements DaoInterface{
public PolicyInterfaceDao(String allip){
	super("interfacepolicy"+allip);
}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
			PolicyInterface vo=new PolicyInterface();
			try {
				vo.setId(rs.getInt("id"));
				vo.setInterfaceName((rs.getString("interfaceName")));
				vo.setPolicyName(rs.getString("policyName"));
				vo.setClassName(rs.getString("className"));
				vo.setOfferedRate(rs.getInt("offeredRate"));
				vo.setDropRate(rs.getInt("dropRate"));
				vo.setMatchGroup(rs.getString("matchGroup"));
				vo.setMatchedPkts(rs.getInt("matchedPkts"));
				vo.setMatchedBytes(rs.getInt("matchedBytes"));
				vo.setDropsTotal(rs.getInt("dropsTotal"));
				vo.setDropsBytes(rs.getInt("dropsBytes"));
				vo.setDepth(rs.getInt("depth"));
				vo.setTotalQueued(rs.getInt("totalQueued"));
				vo.setNoBufferDrop(rs.getInt("noBufferDrop"));
				vo.setCollecttime(rs.getString("collecttime"));
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

}
