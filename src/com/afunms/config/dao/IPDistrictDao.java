package com.afunms.config.dao;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.IPDistrictConfig;


public class IPDistrictDao extends BaseDao implements DaoInterface {

	public IPDistrictDao() {
		super("nms_ipdistrict");
		// TODO Auto-generated constructor stub
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
	
		IPDistrictConfig vo = new IPDistrictConfig();
		 try
	      {
			  vo.setId(rs.getInt("id"));
			  vo.setDistrictid(rs.getInt("district_id"));
	          vo.setStartip(rs.getString("startip"));
	          vo.setEndip(rs.getString("endip"));}
	      catch(Exception e)
	      {
	          SysLogger.error("IPDistrictDao.loadFromRS()",e);
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
	         rs = conn.executeQuery("select * from nms_ipdistrict order by id");
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
		  IPDistrictConfig vo = (IPDistrictConfig)baseVo;	 
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_ipdistrict set district_id=");
		sql.append(vo.getDistrictid());
		sql.append(",startip='");
		sql.append(vo.getStartip());
		sql.append("',endip='");
		sql.append(vo.getEndip());
		sql.append("' where id="+vo.getId());
		return saveOrUpdate(sql.toString());
	}
	   /**
	    * 添加方法
	    */
	   public boolean save(BaseVo basevo) {
		// TODO Auto-generated method stub
		   IPDistrictConfig vo = (IPDistrictConfig)basevo;	  
		   StringBuffer sql = new StringBuffer();
		   sql.append("insert into nms_ipdistrict(district_id,startip,endip)values(");
		   sql.append(vo.getDistrictid());
		   sql.append(",'");
		   sql.append(vo.getStartip());
		   sql.append("','");
		   sql.append(vo.getEndip());
		   sql.append("'");
		   sql.append(")");
//		   System.out.println("-----------------"+sql);
		   return saveOrUpdate(sql.toString());
	}
	   
	   public List getDistrictById(int id){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
	
		   sql.append("select * from nms_district where id = "+id);
		   
		   return findByCriteria(sql.toString());
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
				   conn.addBatch("delete from nms_ipdistrict where id=" + id);
				   //System.out.println("delete from nms_contract where id=" + id);
				   conn.executeBatch();
				   result = true;
			   }
			   catch(Exception e)
			   {
				   SysLogger.error("IPDistrictDao.delete()",e); 
			   }
			   finally
			   {
				   conn.close();
			   }
			   return result;
		   }
		  
		  public List loadByDistrictId(String districtId){
			  String sql = "select * from nms_ipdistrict where district_id = '"+ districtId + "'";
			  return findByCriteria(sql);
		  }
		  
		  public boolean deleteByDistrictId(String districtId){
			  String sql = "delete from nms_ipdistrict where district_id = '"+ districtId + "'";
			  return saveOrUpdate(sql);
		  }

}
