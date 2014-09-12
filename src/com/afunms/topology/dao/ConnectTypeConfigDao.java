

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.ConnectTypeConfig;


public class ConnectTypeConfigDao extends BaseDao implements DaoInterface
{
   public ConnectTypeConfigDao()
   {
	   super("nms_connecttypeconfig");	   	  
   }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		ConnectTypeConfig vo = new ConnectTypeConfig();
		try
		{
			vo.setId(rs.getInt("id"));
			vo.setNode_id(rs.getString("node_id"));
			vo.setConnecttype(rs.getString("connecttype"));
			vo.setUsername(rs.getString("username"));
			vo.setPassword(rs.getString("password"));
			vo.setLoginPrompt(rs.getString("login_prompt"));
			vo.setPasswordPrompt(rs.getString("password_prompt"));
			vo.setShellPrompt(rs.getString("shell_prompt"));
		}
		catch(Exception e)
		{
			SysLogger.error("ConnectTypeConfigDao.loadFromRS()",e); 
		}	   
		return vo;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		
		ConnectTypeConfig connectTypeConfig = (ConnectTypeConfig)vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_connecttypeconfig(node_id,connecttype,username,password,login_prompt,password_prompt,shell_prompt)" +
				"values('");
		sql.append(connectTypeConfig.getNode_id());
		sql.append("','");
		sql.append(connectTypeConfig.getConnecttype());
		sql.append("','");
		sql.append(connectTypeConfig.getUsername());
		sql.append("','");
		sql.append(connectTypeConfig.getPassword());
		sql.append("','");
		sql.append(connectTypeConfig.getLoginPrompt());
		sql.append("','");
		sql.append(connectTypeConfig.getPasswordPrompt());
		sql.append("','");
		sql.append(connectTypeConfig.getShellPrompt());
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}
	
	
	public ConnectTypeConfig findByNodeId(String nodeId){
		List list = findByCriteria("select * from nms_connecttypeconfig where node_id='" + nodeId + "'");
		if(list !=null && list.size() > 0){
			return (ConnectTypeConfig)list.get(0);
		}
		return null;
	}
	
	public boolean deleteByNodeId(String nodeId){
		String sql = "delete from nms_connecttypeconfig where node_id='" + nodeId +"'";
		return saveOrUpdate(sql);
	}
	
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		ConnectTypeConfig config = (ConnectTypeConfig)vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_connecttypeconfig set connecttype ='");
		sql.append(config.getConnecttype());
		sql.append("', username ='");
		sql.append(config.getUsername());
		sql.append("', password ='");
		sql.append(config.getPassword());
		sql.append("', login_prompt ='");
		sql.append(config.getLoginPrompt());
		sql.append("', password_prompt ='");
		sql.append(config.getPasswordPrompt());
		sql.append("', shell_prompt ='");
		sql.append(config.getShellPrompt());
		sql.append("'");
		//SysLogger.info(sql.toString());
		return saveOrUpdate(sql.toString());
	}
   
   
}
