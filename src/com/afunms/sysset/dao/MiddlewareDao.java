package com.afunms.sysset.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.sysset.model.DeviceType;
import com.afunms.sysset.model.Middleware;
import com.afunms.sysset.model.Producer;
import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;

public class MiddlewareDao extends BaseDao implements DaoInterface 
{
	public MiddlewareDao() {
		super("nms_manage_nodetype");
	}




	@Override
	public BaseVo loadFromRS(ResultSet rs) 
	{
		Middleware vo = new Middleware();
		try 
		{
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setText(rs.getString("text"));
			vo.setFather_id(rs.getInt("father_id"));
			vo.setTable_name(rs.getString("table_name"));
			vo.setCategory(rs.getString("category"));
			vo.setNode_tag(rs.getString("node_tag"));
		} 
		catch (Exception e) 
		{
			SysLogger.error("MiddlewareDao.loadFromRS()", e);
		}
		return vo;
	}
	
	/**
	 * 删除
	 */
	public boolean delete(String[] id)
    {
 	   boolean result = false;
 	   try
 	   {
 	       for(int i=0;i<id.length;i++)
 	       {	   
 	           conn.addBatch("delete from nms_manage_nodetype where id=" + id[i] + " or father_id=" + id[i]);
 	       }    
 	       conn.executeBatch();
 	       result = true;
 	   }
 	   catch(Exception ex)
 	   {
 	       SysLogger.error("MiddlewareDao.delete()",ex);
 	       conn.rollback();
 	       result = false;
 	   }
 	   finally
 	   {
 	       conn.close();
 	   }
 	   return result;
    }
	
	
	/**
	 * 判断名称是否已经存在,id==-1表示是添加数据前判断名称是否存在,id!=-1则表示是更新数据前判断名称是否存在
	 */
    public boolean isNameExist(String name,int id)
    {
    	String sql;
    	//判断前3个字符是否已经存在
    	String name_nod = name.substring(0,3);
    	if(id == -1)
    	{
    		sql = "SELECT * FROM afunms.nms_manage_nodetype where name like '" + name_nod + "%'";
    	}
    	else
    	{
    		sql = "SELECT * FROM afunms.nms_manage_nodetype where name like '" + name_nod + "%' and id!=" + id;
    	}
    	//返回结果，false表示该名称可以使用
    	boolean result = false;
    	try
  	   	{
    		rs = conn.executeQuery(sql);
  		   	if(rs.next())
  		   	{
  		   		result = true;
  		   	}
  	   	}
  	   	catch(Exception e)
  	   	{
  	   		SysLogger.error("MiddlewareDao.isNameExist()",e);    
  	   	}
    	return result;
    }
    
    public boolean isNameExist(String name)
    {
    	return isNameExist(name, -1);
    }



	public boolean save(BaseVo _vo) {
		Middleware vo = (Middleware)_vo;		
        StringBuffer sql = new StringBuffer(200);
        sql.append("insert into nms_manage_nodetype(id,name,text,father_id,table_name,category,node_tag) values(");
        sql.append(getNextID());
        sql.append(",'");
        sql.append(vo.getName());
        sql.append("','");
        sql.append(vo.getText());
        sql.append("',");
        sql.append(vo.getFather_id());
        sql.append(",'");
        sql.append(vo.getTable_name());
        sql.append("','");
        sql.append(vo.getCategory());
        sql.append("','");
        sql.append(vo.getNode_tag());
        sql.append("')");	                      
        return saveOrUpdate(sql.toString());
	}




	public boolean update(BaseVo _vo) {
		Middleware vo = (Middleware)_vo;		
	    StringBuffer sb = new StringBuffer(200);
        sb.append("update nms_manage_nodetype set name='");
        sb.append(vo.getName());
		sb.append("',text='");
		sb.append(vo.getText());
		sb.append("',father_id=");
        sb.append(vo.getFather_id());
        sb.append(",table_name='");
        sb.append(vo.getTable_name());
        sb.append("',category='");
        sb.append(vo.getCategory());
        sb.append("',node_tag='");
        sb.append(vo.getNode_tag());
        sb.append("' where id=");	
        sb.append(vo.getId());
        String str = sb.toString();
        return saveOrUpdate(str);
	}

}
