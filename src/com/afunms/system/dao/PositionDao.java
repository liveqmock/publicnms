/**
 * <p>Description:operate table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.dao;

import java.sql.ResultSet;

import com.afunms.system.model.Position;
import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;

public class PositionDao extends BaseDao implements DaoInterface
{
	public PositionDao()
	{
		super("system_position");		
	}
			
	public boolean save(BaseVo baseVo)
	{
		Position vo = (Position)baseVo;
		boolean result = false;
		try
		{
			conn.executeUpdate("insert into system_position(id,name)values(" + getNextID() + ",'" + vo.getName() + "')");
			result = true;
		}
		catch(Exception e)
		{			
			result = false;
			SysLogger.error("PositionDao.save()",e); 
		}
		finally
		{
			conn.close();
		}	
		return result;
	}
		   
	public boolean update(BaseVo baseVo)
	{
		Position vo = (Position)baseVo;
		boolean result = false;
		try
		{
		    conn.executeUpdate("update system_position set name='" + vo.getName() + "' where id=" + vo.getId());
		    result = true;
		}
		catch(Exception e)
		{
			result = false;
			SysLogger.error("PositionDao.update()",e); 
		}
		finally
		{
			conn.close();
		}			
		return result;			
	}

	public boolean delete(String[] id)
	{
		boolean result = false;
	    try
	    {	    
	        for(int i=0;i<id.length;i++)
	        {
	            conn.addBatch("delete from system_position where id=" + id[i]);
	            conn.addBatch("delete from nms_user where dept_id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	    	conn.rollback();
	        SysLogger.error("PositionDao.delete()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    return result;
	}

    public BaseVo loadFromRS(ResultSet rs)
    {
    	Position vo = new Position();
        try
        {
        	vo.setId(rs.getInt("id"));
        	vo.setName(rs.getString("name"));
        }
        catch(Exception e)
        {
     	    SysLogger.error("PositionDao.loadFromRS()",e); 
        }	   
        return vo;
    }	
}
