

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.RemotePingHost;
import com.afunms.topology.model.RemotePingNode;


public class RemotePingHostDao extends BaseDao implements DaoInterface
{
   public RemotePingHostDao()
   {
	   super("nms_remote_ping_host");	   	  
   }

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		RemotePingHost vo = new RemotePingHost();
		try
		{
			vo.setId(rs.getInt("id"));
			vo.setNode_id(rs.getString("node_id"));
			vo.setUsername(rs.getString("username"));
			vo.setPassword(rs.getString("password"));
			vo.setLoginPrompt(rs.getString("login_prompt"));
			vo.setPasswordPrompt(rs.getString("password_prompt"));
			vo.setShellPrompt(rs.getString("shell_prompt"));
		}
		catch(Exception e)
		{
			SysLogger.error("RemotePingHostDao.loadFromRS()",e); 
		}	   
		return vo;
	}
	
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		
		RemotePingHost remotePingHost = (RemotePingHost)vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_remote_ping_host(node_id,username,password,login_prompt,password_prompt,shell_prompt)" +
				"values('");
		sql.append(remotePingHost.getNode_id());
		sql.append("','");
		sql.append(remotePingHost.getUsername());
		sql.append("','");
		sql.append(remotePingHost.getPassword());
		sql.append("','");
		sql.append(remotePingHost.getLoginPrompt());
		sql.append("','");
		sql.append(remotePingHost.getPasswordPrompt());
		sql.append("','");
		sql.append(remotePingHost.getShellPrompt());
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}
	
	public RemotePingHost findByNodeId(String nodeId){
		List list = findByCriteria("select * from nms_remote_ping_host where node_id='" + nodeId + "'");
		if(list !=null && list.size() > 0){
			return (RemotePingHost)list.get(0);
		}
		return null;
	}
	
	public boolean deleteByNodeId(String nodeId){
		String sql = "delete from nms_remote_ping_host where node_id='" + nodeId +"'";
		return saveOrUpdate(sql);
	}
	
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
   
   
}
