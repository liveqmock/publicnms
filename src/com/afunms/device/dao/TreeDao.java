package com.afunms.device.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.device.model.AuditLog;
import com.afunms.device.model.Tree;

public class TreeDao extends BaseDao implements DaoInterface {
	public TreeDao() {
        super("its_device_tree");
    }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Tree vo=new Tree();
		try {
            vo.setId(rs.getInt("id"));
            vo.setCabinetId(rs.getInt("cabinetId"));
            vo.setExternalDeviceId(rs.getInt("externalDeviceId"));
            vo.setPid(rs.getString("pid"));
        } catch (Exception e) {
            SysLogger.error("TreeDao.loadFromRS()", e);
            vo = null;
        }
        return vo;
	}

	@Override
	public boolean save(BaseVo basevo) {
		Tree vo = (Tree) basevo;
	        StringBuffer sql = new StringBuffer();
	        sql.append("insert into its_device_tree(cabinetId,externalDeviceId,pid) values(");
	        sql.append(vo.getCabinetId());
	        sql.append(",");
	        sql.append(vo.getExternalDeviceId());
	        sql.append(",'");
	        sql.append(vo.getPid());
	        sql.append("')");
	        return saveOrUpdate(sql.toString());
	}

	@Override
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean deleteByExternalDeviceId(String externalDeviceId) {
		StringBuffer sql = new StringBuffer();
        sql.append("delete from its_device_tree where externalDeviceId=");
        sql.append(externalDeviceId);
        
        return saveOrUpdate(sql.toString());
	}
	public boolean deleteByCabinetId(String cabinetId) {
		StringBuffer sql = new StringBuffer();
        sql.append("delete from its_device_tree where cabinetId=");
        sql.append(cabinetId);
        return saveOrUpdate(sql.toString());
	}
}

