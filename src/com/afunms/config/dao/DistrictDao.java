package com.afunms.config.dao;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.DistrictConfig;


public class DistrictDao extends BaseDao implements DaoInterface {

	public DistrictDao() {
		super("nms_district");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
	
		DistrictConfig vo = new DistrictConfig();
		 try
	      {
			 vo.setId(rs.getInt("id"));
	          vo.setName(rs.getString("name"));
	          vo.setDesc(rs.getString("dis_desc"));
	          vo.setDescolor(rs.getString("descolor"));
	      }
	      catch(Exception e)
	      {
	          SysLogger.error("DistrictDao.loadFromRS()",e);
	          vo = null;
	      }
	      return vo;
	}

	 /** 
	    * 列出所有方法
	    */
	  public List loadAll()
	  {
	     List list = new ArrayList(5);
	     try
	     {
	         rs = conn.executeQuery("select * from nms_district order by id");
	         while(rs.next())
	        	list.add(loadFromRS(rs)); 
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("DistrictDao:loadAll()",e);
	         list = null;
	     }
	     finally
	     {
	         conn.close();
	     }
	     return list;
	  }
	    /**
	    * 修改方法
	    */
	  public boolean update(BaseVo baseVo) {
		// TODO Auto-generated method stub
		  DistrictConfig vo = (DistrictConfig)baseVo;	 
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_district set name ='");
		sql.append(vo.getName());
		sql.append("',dis_desc='");
		sql.append(vo.getDesc());
		sql.append("',descolor='");
		sql.append(vo.getDescolor());
		sql.append("' where id="+vo.getId());
		return saveOrUpdate(sql.toString());
	}
	   /**
	    * 添加方法
	    */
	   public boolean save(BaseVo basevo) {
		// TODO Auto-generated method stub
		   DistrictConfig vo = (DistrictConfig)basevo;	  
		   StringBuffer sql = new StringBuffer();
		   sql.append("insert into nms_district(name,dis_desc,descolor)values(");
		   sql.append("'");
		   sql.append(vo.getName());
		   sql.append("','");
		   sql.append(vo.getDesc());
		   sql.append("','");
		   sql.append(vo.getDescolor());
		   sql.append("'");
		   sql.append(")");
		  
		   return saveOrUpdate(sql.toString());
	}
	   

		 /**
		  * 根据id删除这条记录
		  * @param id
		  * @return
		  */
		  public boolean delete(String id)
		   {
			   boolean result = false;
			   try
			   {
				   conn.addBatch("delete from nms_district where id=" + id);
				   //System.out.println("delete from nms_contract where id=" + id);
				   conn.executeBatch();
				   result = true;
			   }
			   catch(Exception e)
			   {
				   SysLogger.error("DistrictDao.delete()",e); 
			   }
			   finally
			   {
				   conn.close();
			   }
			   return result;
		   }

}
