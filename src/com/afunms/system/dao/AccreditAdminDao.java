package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.AccreditAdmin;

public class AccreditAdminDao extends BaseDao{

	public AccreditAdminDao() {
		super("nms_role_func");
		// TODO Auto-generated constructor stub
	}


	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		
		return null;
	}
	
	public synchronized boolean deleteById(){
		return true ;
	}
	
	public synchronized boolean deleteByRoleId(String roleId){
		boolean result = false;
		String sql = "delete from nms_role_func where roleid="+roleId;
		   try
		   {
		       conn.executeUpdate(sql);
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("AccreditAdmindDao.deleteByRoleId()",ex);
		       result = false;
		   }
		   return result;
		
	}
	
	public List<AccreditAdmin> findByRoleId(String roleId){
		List<AccreditAdmin> accreditAdminlist = new ArrayList<AccreditAdmin>();
		String sql = "select * from nms_role_func where roleid="+roleId;
		ResultSet rs = null;
		try{
			rs = conn.executeQuery(sql);
			while(rs.next()){
				AccreditAdmin accreditAdmin = new AccreditAdmin();
				accreditAdmin.setId(Integer.valueOf(rs.getString("id")));
				accreditAdmin.setRoleid(rs.getString("roleid"));
				accreditAdmin.setFuncid(rs.getString("funcid"));
				accreditAdminlist.add(accreditAdmin);
				
			}
		}catch(Exception e){
			SysLogger.error("AccreditAdmindDao.findByRoleId()",e);
		}finally{
			if(rs != null){
	    		try{
	    			rs.close();
	    		}catch(Exception e){
	    			
	    		}
	    	}
		}
		return accreditAdminlist;
	}
	
	public synchronized boolean AccreditAdminUpadte(List<AccreditAdmin> accreditAdminlist){
		boolean result = false;
		try{
			result = deleteByRoleId(accreditAdminlist.get(0).getRoleid());
			int firstId = getNextID();
			String sql = "insert into nms_role_func(id,roleid,funcid) values(";
			for(int i = 0 ; i<accreditAdminlist.size(); i++){
				int id = firstId + i;
				String sql2 = sql+id+","+accreditAdminlist.get(i).getRoleid()+","+accreditAdminlist.get(i).getFuncid()+")";
				
				conn.addBatch(sql2);
			}
			conn.executeBatch();
		}catch(Exception ex){
			SysLogger.error("AccreditAdmindDao.AccreditAdminUpadte()",ex);
			result = false;
		}
		return result;
	}



}
