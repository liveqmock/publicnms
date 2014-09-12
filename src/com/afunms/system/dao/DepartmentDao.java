/**
 * <p>Description:operate table NMS_DEPARTMENT</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.dao;

import java.sql.ResultSet;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.Department;

public class DepartmentDao extends BaseDao implements DaoInterface
{
	public DepartmentDao()
	{
		super("system_department");		
	}
			
	public boolean save(BaseVo baseVo)
	{
		Department vo = (Department)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_department(id,dept,man,tel)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getDept());
		sql.append("','");
		sql.append(vo.getMan());		
		sql.append("','");
		sql.append(vo.getTel());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
		   
	public boolean update(BaseVo baseVo)
	{
		Department vo = (Department)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("update system_department set dept='");
		sql.append(vo.getDept());
		sql.append("',man='");
		sql.append(vo.getMan());
		sql.append("',tel='");
		sql.append(vo.getTel()); 
		sql.append("' where id=");
		sql.append(vo.getId());		
		return saveOrUpdate(sql.toString());			
	}

	public boolean delete(String[] id)
	{
		boolean result = false;
	    try
	    {	    
	        for(int i=0;i<id.length;i++)
	        {
	        	try{
	            conn.addBatch("delete from system_department where id=" + id[i]);
	        	}catch(Exception e){
	        		e.printStackTrace();
	        	}
	        	try{
	        		conn.addBatch("delete from system_user where dept_id=" + id[i]);
	        	}catch(Exception e){
	        		e.printStackTrace();
	        	}
	        }	
	        try{
	        	conn.executeBatch();
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("DepartmentDao.delete()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    return result;
	}
	
    public BaseVo loadFromRS(ResultSet rs)
    {
    	Department vo = new Department();
        try
        {
        	vo.setId(rs.getInt("id"));
        	vo.setDept(rs.getString("dept"));
        	vo.setMan(rs.getString("man"));
        	vo.setTel(rs.getString("tel"));
        }
        catch(Exception e)
        {
     	    SysLogger.error("DepartmentDao.loadFromRS()",e); 
        }	   
        return vo;
    }	
}
