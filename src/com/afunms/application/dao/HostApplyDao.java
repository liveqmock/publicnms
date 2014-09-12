package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.HostApplyModel;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Oct 9, 2011 3:33:44 PM
 * 类说明
 */
public class HostApplyDao extends BaseDao implements DaoInterface {
	public HostApplyDao() {
		super("system_host_apply");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		HostApplyModel vo = new HostApplyModel();
		try {
			vo.setId(rs.getInt("id"));
			vo.setUserId(rs.getInt("user_id"));
			vo.setNodeid(rs.getInt("nodeid"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setIpaddres(rs.getString("ipaddress"));
			vo.setShow(Boolean.parseBoolean(rs.getString("isShow")));
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
	
	public boolean save(BaseVo vo) {
		HostApplyModel model = (HostApplyModel)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into system_host_apply(user_id,nodeid,type,subtype,ipaddress,isShow)values('");
		sql.append(model.getUserId());
        sql.append("','");
		sql.append(model.getNodeid());
		sql.append("','");
		sql.append(model.getType());
		sql.append("','");
		sql.append(model.getSubtype());
		sql.append("','");
		sql.append(model.getIpaddres());
		sql.append("','");
		sql.append(model.isShow());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean save(List<HostApplyModel> list) {
	    boolean result = false;
	    for (HostApplyModel hostApplyModel : list) {
	        StringBuffer sql=new StringBuffer();
	        sql.append("insert into system_host_apply(user_id,nodeid,type,subtype,ipaddress,isShow)values('");
	        sql.append(hostApplyModel.getUserId());
	        sql.append("','");
	        sql.append(hostApplyModel.getNodeid());
	        sql.append("','");
	        sql.append(hostApplyModel.getType());
	        sql.append("','");
	        sql.append(hostApplyModel.getSubtype());
	        sql.append("','");
	        sql.append(hostApplyModel.getIpaddres());
	        sql.append("','");
	        sql.append(hostApplyModel.isShow());
	        sql.append("')");
	        conn.addBatch(sql.toString());
        }
	    conn.executeBatch();
        return result;
    }

	public boolean update(BaseVo vo) {
		HostApplyModel model = (HostApplyModel)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("update system_host_apply set nodeid = '");
		sql.append(model.getNodeid());
		sql.append("', type = '");
		sql.append(model.getType());
		sql.append("', user_id = '");
        sql.append(model.getUserId());
		sql.append("',subtype = '");
		sql.append(model.getSubtype());
		sql.append("',ipaddress = '");
		sql.append(model.getIpaddres());
		sql.append("',isShow = '");
		sql.append(model.isShow());
		sql.append("' where id = '");
		sql.append(model.getId());
		sql.append("'");
		return saveOrUpdate(sql.toString());
	}
	
	/**
	 * 根据条件删除
	 * @param condition
	 * @return
	 */
	public boolean delete(String condition){
		boolean flag = false;
	    try{
		   conn.executeUpdate("delete from system_host_apply " + condition);
	    }catch(Exception e){
		   e.printStackTrace();
	    }finally{
	    	conn.close();
	    }
		return flag;
	}
	
	/**
	 * 批量修改多个服务器的应用是否展示标志位
	 * @param ipAndSubTypes   [ip:subtype]
	 * @param isShow          true/false
	 */
	public void batchUpdateMultilIsShow(String[] ipAndSubTypes, boolean isShow) {
		try {
			conn.addBatch("update system_host_apply set isShow = 'false' ");
			if(ipAndSubTypes != null){
				for(int i=0; i < ipAndSubTypes.length; i++){
					String ipAndSubType = ipAndSubTypes[i];
					if(!ipAndSubType.contains(":")){
						continue;
					}
					String[] tempArry = ipAndSubType.split(":");
					String ip = tempArry[0];
					String subtype = tempArry[1];
					String sql = "update system_host_apply set isShow = '" + isShow +"' where ipaddress = '" + ip + "' and subtype = '"+subtype+"'"; 
					conn.addBatch(sql);
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			conn.close();
		}
	}

	/**
	 * 批量修改单个服务器的应用是否展示标志位
	 * @param ipAndSubTypes   [ip:subtype]
	 * @param isShow          true/false
	 * @param ipaddress       服务器的IP地址
	 */
	public void batchUpdateSingleIsShow(String[] ipAndSubTypes, boolean isShow, String ipaddress) {
		try {
			conn.addBatch("update system_host_apply set isShow = 'false' where ipaddress = '"+ipaddress+"'");
			if(ipAndSubTypes != null){
				for(int i=0; i < ipAndSubTypes.length; i++){
					String ipAndSubType = ipAndSubTypes[i];
					if(!ipAndSubType.contains(":")){
						continue;
					}
					String[] tempArry = ipAndSubType.split(":");
					String ip = tempArry[0];
					String subtype = tempArry[1];
					String sql = "update system_host_apply set isShow = '" + isShow +"' where ipaddress = '" + ip + "' and subtype = '"+subtype+"'"; 
					conn.addBatch(sql);
				}
			}
			conn.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			conn.close();
		}
	}

    /**
     * findByUserId:
     * <p>根据用户获取应用
     *
     * @param   userId
     *          - 用户 id
     * @return  {@link List<HostApplyModel>}
     *          - 应用列表
     *
     * @since   v1.01
     */
    public List<HostApplyModel> findByUserId(int userId) {
        return findByCondition(" where user_id='" + userId + "'");
    }

    public List<HostApplyModel> findByNodeid(int nodeid) {
        return findByCondition(" where nodeid='" + nodeid + "'");
    }
    
    public boolean deleteByUserId(int userId) {
        return saveOrUpdate("delete from system_host_apply where user_id='" + userId + "'");
    }
}
