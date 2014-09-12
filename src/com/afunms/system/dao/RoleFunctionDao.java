package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.RoleFunction;

public class RoleFunctionDao extends BaseDao implements DaoInterface{

	public RoleFunctionDao() {
		super("nms_role_func");
		// TODO Auto-generated constructor stub
	}


	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		
		return null;
	}
	
	public boolean deleteById(){
		return true ;
	}
	
	public boolean deleteByRoleId(String roleId){
		conn = new DBManager();
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
		   }finally{
			   conn.close();
		   }
		   return result;
		
	}
	
	public List<RoleFunction> findByRoleId(String roleId){
		conn = new DBManager();
		List<RoleFunction> roleFunctionlist = new ArrayList<RoleFunction>();
		String sql = "select * from nms_role_func where roleid="+roleId;
		ResultSet rs = null;
		try{
			rs = conn.executeQuery(sql);
			while(rs.next()){
				RoleFunction roleFunction = new RoleFunction();
				roleFunction = createRoleFunctionByRS(rs);
				roleFunctionlist.add(roleFunction);
				
			}
		}catch(Exception e){
			SysLogger.error("RoleFunction.findByRoleId()",e);
			e.printStackTrace();
		}finally{
			if(rs != null){
	    		try{
	    			rs.close();
	    		}catch(Exception e){
	    			
	    		}
	    	}
			conn.close();
		}
		return roleFunctionlist;
	}
	
	public boolean roleFunctionUpadte(List<RoleFunction> roleFunctionList){
		conn = new DBManager();
		boolean result = false;
		try{
			
			int firstId = getNextID();
			String sql = "insert into nms_role_func(id,roleid,funcid) values(";
			for(int i = 0 ; i<roleFunctionList.size(); i++){
				int id = firstId + i;
				String sql2 = sql+id+","+roleFunctionList.get(i).getRoleid()+","+roleFunctionList.get(i).getFuncid()+")";
				
				conn.addBatch(sql2);
			}
			conn.executeBatch();
			result = true;
		}catch(Exception ex){
			SysLogger.error("RoleFunctionDao.roleFunctionUpadte()",ex);
			result = false;
		}finally{
			conn.close();
		}
		return result;
	}
	
	private RoleFunction createRoleFunctionByRS(ResultSet rs){
		RoleFunction roleFunction = new RoleFunction();
		try{
			roleFunction.setId(Integer.valueOf(rs.getString("id")));
			roleFunction.setRoleid(rs.getString("roleid"));
			roleFunction.setFuncid(rs.getString("funcid"));
		}catch (Exception e)
	    {	    
	        SysLogger.error("Error in FuctionDao.createRoleFunctionByRS()",e);
	        e.printStackTrace();
	    }
		finally{
		}
		return roleFunction;
		
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
