/**
 * <p>Description:operate table NMS_DEVICE_TYPE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-09
 */

package com.afunms.sysset.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.sysset.model.DeviceType;

public class DeviceTypeDao extends BaseDao implements DaoInterface
{
    public DeviceTypeDao()
    {
       super("nms_device_type");     
    }

    public List listByPage(int curpage)
    {
 	   List list = new ArrayList();	   
 	   try 
 	   {
 		   rs = conn.executeQuery("select count(*) from nms_device_type");
 		   if(rs.next())
 			   jspPage = new JspPage(6,curpage,rs.getInt(1));

 		   rs = conn.executeQuery("select * from nms_device_type order by id");
 		   int loop = 0;
 		   while(rs.next())
 		   {
 			  loop++;
 			  if(loop<jspPage.getMinNum()) continue;
 			  list.add(loadFromRS(rs));
 			  if(loop==jspPage.getMaxNum()) break;
 		   }
 	   } 
 	   catch (Exception e) 
 	   {
 		   SysLogger.error("DeviceTypeDao.listByPage()",e);
 		   list = null;
 	   }
 	   finally
 	   {
 		   conn.close();
 	   }
 	   return list;
    }

    public Map loadDeviceType()
    {
 	   Map map = new Hashtable(50);	   
 	   try 
 	   {
 		   rs = conn.executeQuery("select * from nms_device_type order by id");
 		   while(rs.next())
 			   map.put(rs.getString("sys_oid"),new Integer(rs.getInt("category")));
 	   } 
 	   catch (Exception e) 
 	   {
 		   SysLogger.error("DeviceTypeDao.loadDeviceType()",e);
 	   }
 	   finally
 	   {
 		   conn.close();
 	   }
 	   return map;
    }
    
    /**
     * 不能存在两个相同的sysOid,所以在增加和修改前都必须判断
     * id=0增加,id<>0时是修改
     */
    public boolean isSysOidExist(String sysOid,int id)
    {
	   String sql = "select * from nms_device_type where sys_oid='" + sysOid + "'";
	   boolean result = false;
	   try
	   {
		   rs = conn.executeQuery(sql);
		   if(rs.next())
		   {
			   if(rs.getInt("id")==id)
				 result = false;
			   else
			     result = true;
		   }	   
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("DeviceTypeDao.isSysOidExist()",e);    
	   }
	   return result;
    }

    public DeviceType findBySysOid(String sysOid)
    {
	   String sql = "select * from nms_device_type where sys_oid='" + sysOid + "'";	 
	   DeviceType vo = null;
	   try
	   {
		   rs = conn.executeQuery(sql);
		   if(rs.next())
              vo = (DeviceType)loadFromRS(rs);
	   }
	   catch(Exception e)
	   {
		   SysLogger.error("DeviceTypeDao.findBySysOid()",e);
		   vo = null;
	   }
	   return vo;
    }
    
	public boolean save(BaseVo baseVo)
	{
		DeviceType vo = (DeviceType)baseVo;
		
        StringBuffer sb = new StringBuffer(200);
        sb.append("insert into nms_device_type(id,producer,sys_oid,descr,image,category,locate,log_time)values(");
        sb.append(getNextID());
        sb.append(",'");
        sb.append(vo.getProducer());
        sb.append("','");
        sb.append(vo.getSysOid());
        sb.append("','");
        sb.append(vo.getDescr());
        sb.append("','");
        sb.append(vo.getImage());
        sb.append("',");
        sb.append(vo.getCategory());
        sb.append(",'");
        sb.append(vo.getLocate());
        sb.append("','");
        sb.append(SysUtil.getCurrentTime());        
        sb.append("')");	        
        return saveOrUpdate(sb.toString());
    }
		   
	public boolean update(BaseVo baseVo)
	{
		DeviceType vo = (DeviceType)baseVo;		
	    StringBuffer sb = new StringBuffer(200);
        sb.append("update nms_device_type set producer='");
        sb.append(vo.getProducer());
		sb.append("',sys_oid='");
		sb.append(vo.getSysOid());
		sb.append("',descr='");
        sb.append(vo.getDescr());
        sb.append("',image='");
        sb.append(vo.getImage());
        sb.append("',category=");
        sb.append(vo.getCategory());
        
        if(vo.getLocate()!=null)
        {	
           sb.append(",locate='");
           sb.append(vo.getLocate());
           sb.append("',log_time='");
           sb.append(SysUtil.getCurrentTime());           
           sb.append("' where id=");
        }
        else
           sb.append(" where id=");	
        sb.append(vo.getId());
        return saveOrUpdate(sb.toString());
    }
      
   public BaseVo loadFromRS(ResultSet rs)
   {
	   DeviceType vo = new DeviceType();
       try
       {
		   vo.setId(rs.getInt("id"));
		   vo.setProducer(rs.getInt("producer"));
		   vo.setSysOid(rs.getString("sys_oid"));
		   vo.setDescr(rs.getString("descr"));
		   vo.setImage(rs.getString("image"));
		   vo.setCategory(rs.getInt("category"));
		   vo.setLocate(rs.getString("locate"));
		   vo.setLogTime(rs.getString("log_time"));
       }
       catch(Exception e)
       {
   	       SysLogger.error("DeviceTypeDao.loadFromRS()",e); 
       }	   
       return vo;
   }	
}
