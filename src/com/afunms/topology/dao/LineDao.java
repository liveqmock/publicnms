/**
 * <p>Description:operate table nms_hint_line</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2010-01-04
 */

package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.common.base.*;
import com.afunms.common.util.SysLogger;
import com.afunms.topology.model.HintLine;

public class LineDao extends BaseDao implements DaoInterface
{
   public LineDao()
   {
	   super("nms_hint_line");	   	  
   }
   
   public boolean deleteByidXml(String id,String xml)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from nms_hint_line where xmlfile='"+xml+"' and father_id='"+id+"' or child_id='"+id+"'");
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.deleteByidXml()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   public List<HintLine> findByFid(String fid,String xml){
	   List list = new ArrayList();
	   String sql = "select * from nms_hint_line where father_id='" + fid + "' and xmlfile='"+xml+"'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
			   list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.findById()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   public void deleteByXml(String xml)
   {
	   try
	   {
		   conn.executeUpdate("delete from nms_hint_line where xmlfile='"+xml+"'");
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.deleteByXml()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
   }
   public HintLine getChildNode(String xml,String cid){
	   HintLine vo = null;
	   String sql = "select * from nms_hint_line where xmlfile='"+xml+"' and child_id='"+cid+"'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
			   vo = (HintLine) loadFromRS(rs);
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.getChildNode(String xml,String cid)",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return vo;
   }
   public List<HintLine> getChildList(String xml,String fid){
	   List list = new ArrayList();
	   String sql = "select * from nms_hint_line where xmlfile='"+xml+"' and father_id='"+fid+"'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
			   list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.getChildList()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   
   public HintLine findById(String id,String xml){
	   HintLine hintLine = null;
	   String sql = "select * from nms_hint_line where line_id='" + id + "' and xmlfile='"+xml+"'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			   hintLine = (HintLine)loadFromRS(rs);
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.findById()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return hintLine;
   }
   
   public List<HintLine> findByXml(String xml){
	   List list = new ArrayList();
	   String sql = "select * from nms_hint_line where xmlfile='"+xml+"'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
			   list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.findByXml()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   
   public List<HintLine> findByid(String id){
	   List list = new ArrayList();
	   String sql = "select * from nms_hint_line where father_id='" + id + "' or child_id='" + id + "'";
	   try
	   {
		   rs = conn.executeQuery(sql);
		   while(rs.next())
		     list.add(loadFromRS(rs));
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.findByid()",e); 
	   }
	   finally
	   {
		   conn.close();
	   }
	   return list;
   }
   
   public boolean lineExist(String id)
   {	   
	   String sql = null;
	   try
	   {
		   sql = "select * from nms_hint_line where line_id='" + id + "'";
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			 return true;	   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.lineExist()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return false;	      
   }

   public boolean lineExist(String startId,String endId)
   {	   
	   String sql = null;
	   try
	   {
		   sql = "select * from nms_hint_line where father_id='" + startId + "' and child_id='" + endId + "'";		   
		   rs = conn.executeQuery(sql);
		   if(rs.next())
			 return true;          		   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.lineExist()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return false;	      
   }
   
   public boolean save(BaseVo baseVo)
   {   
	   HintLine vo = (HintLine)baseVo;	
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("insert into nms_hint_line(line_id,father_id,child_id,father_xy,child_xy,xmlfile,line_name,width)values('");
	   sql.append(vo.getLineId());
	   sql.append("','");
	   sql.append(vo.getFatherId());
	   sql.append("','");
	   sql.append(vo.getChildId());
	   sql.append("','");
	   sql.append(vo.getFatherXy());
	   sql.append("','");
	   sql.append(vo.getChildXy());
	   sql.append("','");
	   sql.append(vo.getXmlfile());
	   sql.append("','");
	   sql.append(vo.getLineName());
	   sql.append("',");
	   sql.append(vo.getWidth());
	   sql.append(")");
	   return saveOrUpdate(sql.toString());
   }
   
   public boolean update(BaseVo baseVo)
   {   
	   HintLine vo = (HintLine)baseVo;	
	   StringBuffer sql = new StringBuffer(200);
	   sql.append("update nms_hint_line set ");
	   sql.append(" line_name = '"+vo.getLineName()+"'");
	   sql.append(", father_id = '"+vo.getFatherId()+"'");
	   sql.append(", line_id = '"+vo.getLineId()+"'");
	   sql.append(", child_id = '"+vo.getChildId()+"'");
	   sql.append(", xmlfile = '"+vo.getXmlfile()+"'");
	   sql.append(", father_xy = '"+vo.getFatherXy()+"'");
	   sql.append(", child_xy = '"+vo.getChildXy()+"'");
	   sql.append(", width="+vo.getWidth());
	   sql.append(" where id = "+vo.getId());
	   
	   return saveOrUpdate(sql.toString()); 
   }
   
   public boolean delete(String id)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from nms_hint_line where id=" + id);
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public boolean delete(String id,String xml)
   {
	   boolean result = false;
	   try
	   {
		   conn.executeUpdate("delete from nms_hint_line where line_id='" + id +"' and xmlfile='"+xml+"'");
		   result = true;
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("LineDao.delete()",e); 
	   }
	   finally
	   {
		   conn.close();   
	   }
	   return result;
   }
   
   public BaseVo loadFromRS(ResultSet rs)
   {
	   HintLine vo = new HintLine();
	   try
	   {
		   vo.setId(rs.getInt("id"));
		   vo.setLineId(rs.getString("line_id"));
		   vo.setChildId(rs.getString("child_id"));
		   vo.setFatherId(rs.getString("father_id"));
		   vo.setChildXy(rs.getString("child_xy"));
		   vo.setFatherXy(rs.getString("father_xy"));
		   vo.setXmlfile(rs.getString("xmlfile"));
		   vo.setLineName(rs.getString("line_name"));
		   vo.setWidth(rs.getInt("width"));
	   }
       catch(Exception e)
       {
    	   e.printStackTrace();
 	       SysLogger.error("LineDao.loadFromRS()",e); 
       }	   
       return vo;
   }
}
