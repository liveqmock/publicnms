package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.QueueInfo;

public class QueueInfoDao extends BaseDao implements DaoInterface{
	public QueueInfoDao(String allip){
		super("queueinfo"+allip);
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		QueueInfo vo=new QueueInfo();
		try {
			vo.setId(rs.getInt("id"));
			vo.setEntity(rs.getString("entity"));
			vo.setInputSize(rs.getInt("inputSize"));
			vo.setInputMax(rs.getInt("inputMax"));
			vo.setInputDrops(rs.getInt("inputDrops"));
			vo.setInputFlushes(rs.getInt("inputFlushes"));
			vo.setOutputSize(rs.getInt("outputSize"));
			vo.setOutputMax(rs.getInt("outputMax"));
			vo.setOutputDrops(rs.getInt("outputDrops"));
			vo.setOutputThreshold(rs.getInt("outputThreshold"));
			vo.setAvailBandwidth(rs.getInt("availBandwidth"));
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
