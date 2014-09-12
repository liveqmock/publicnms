package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.CfgCmdFile;

public class CfgCmdFileDao extends BaseDao implements DaoInterface{
    public CfgCmdFileDao(){
    	super("sys_config_command");
    }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CfgCmdFile vo=new CfgCmdFile();
		try {
			vo.setId(rs.getInt("id"));
			vo.setFilename(rs.getString("filename"));
			vo.setCreateBy(rs.getString("create_by"));
			vo.setCreateTime(rs.getString("create_time"));
			vo.setFileDesc(rs.getString("fileDesc"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vo;
	}

	public boolean save(BaseVo vo) {
		CfgCmdFile cmdFile=(CfgCmdFile)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into sys_config_command(filename,create_by,create_time,fileDesc) values('");
		sql.append(cmdFile.getFilename());
		sql.append("','");
		sql.append(cmdFile.getCreateBy());
		sql.append("','");
		sql.append(cmdFile.getCreateTime());
		sql.append("','");
		sql.append(cmdFile.getFileDesc());
		sql.append("')");
		
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
