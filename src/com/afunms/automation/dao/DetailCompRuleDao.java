package com.afunms.automation.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.afunms.automation.model.CompRule;
import com.afunms.automation.model.DetailCompRule;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class DetailCompRuleDao extends BaseDao implements DaoInterface{
   public  DetailCompRuleDao() {
	super("nms_comp_detail_rule");
   }
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		DetailCompRule vo=new DetailCompRule();
		try {
			vo.setId(rs.getInt("ID"));
			vo.setRuleId(rs.getInt("RULEID"));
			vo.setRelation(rs.getInt("RELATION"));
			vo.setIsContain(rs.getInt("ISCONTAIN"));
			vo.setExpression(rs.getString("EXPRESSION"));
			vo.setBeginBlock(rs.getString("BEGINBLOCK"));
			vo.setEndBlock(rs.getString("ENDBLOCK"));
			vo.setIsExtraContain(rs.getInt("ISEXTRACONTAIN"));
			vo.setExtraBlock(rs.getString("EXTRABLOCK"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vo;
	}

	public boolean save(BaseVo vo) {
		StringBuffer sql = new StringBuffer();
		DetailCompRule rule=(DetailCompRule)vo;
		sql.append("insert into nms_comp_detail_rule(RULEID,RELATION,ISCONTAIN,EXPRESSION,BEGINBLOCK,ENDBLOCK,ISEXTRACONTAIN,EXTRABLOCK)values(");
		sql.append(rule.getRuleId());
		sql.append(",");
		sql.append(rule.getRelation());
		sql.append(",");
		sql.append(rule.getIsContain());
		sql.append(",'");
		sql.append(rule.getExpression());
		sql.append("','");
		sql.append(rule.getBeginBlock());
		sql.append("','");
		sql.append(rule.getEndBlock());
		sql.append("',");
		sql.append(rule.getIsExtraContain());
		sql.append(",'");
		sql.append(rule.getExtraBlock());
		sql.append("')");
		
		return saveOrUpdate(sql.toString());
	}

	public boolean deleteDetailRule(BaseVo vo) {
		 boolean result = false;
		 CompRule rule=(CompRule)vo;
		 
		   try
		   {
		       conn.executeUpdate("delete from nms_comp_detail_rule where RULEID=" + rule.getId());
		      
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("BaseDao.delete()",ex);
		       result = false;
		   }finally{
			   if (conn!=null) {
				conn.close();
			}
		   }
		   return result;
	}
	 public boolean delete(String[] id)
	   {
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<id.length;i++)
		           conn.addBatch("delete from nms_comp_detail_rule where RULEID=" + id[i]);
		       conn.executeBatch();
		       result = true;
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("BaseDao.delete()",ex);
		       result = false;
		   }
		   return result;
	   }
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	  
	  public Hashtable<Integer,List<DetailCompRule>> findByIds(String ids){ 
		   List list = new ArrayList();
		   Hashtable<Integer, List<DetailCompRule>> retHashtable = new Hashtable<Integer, List<DetailCompRule>>();
		   try 
		   {
			   rs = conn.executeQuery("select * from nms_comp_detail_rule where RULEID in("  + ids+")");
			   //SysLogger.info("select * from " + table + condition);
			   if(rs == null)return null;
			   while(rs.next()){
				  list.add(loadFromRS(rs));
			   }
			   //set
			   HashSet<Integer> idSet = new HashSet<Integer>(); 
			   if(list != null){
				   for(int i=0; i<list.size(); i++){
					   DetailCompRule detailCompRule = (DetailCompRule)list.get(i);
					   idSet.add(detailCompRule.getId());
				   }
			   }
			   //得到无重复的ID组成的Set
			   Iterator<Integer> idIterator = idSet.iterator();
			   while(idIterator.hasNext()){
				   Integer id = idIterator.next();//每个ID都不一样
				   List<DetailCompRule> detailCompRuleList = new ArrayList<DetailCompRule>();
				   for(int i=0; i<list.size(); i++){
					   DetailCompRule detailCompRule = (DetailCompRule)list.get(i);
					   if(id.intValue() == detailCompRule.getId()){
						   detailCompRuleList.add(detailCompRule);
					   }
				   }
				   retHashtable.put(id, detailCompRuleList);
			   }
		   } 
		   catch(Exception e) 
		   {
	           list = null;
	           e.printStackTrace();
		   }
		   finally
		   {
			   if(rs != null){
				   try{
					   rs.close();
				   }catch(Exception e){
				   }
			   }
		   }
		   return retHashtable;	
	   }
	   public List findByCondition(String condition){
		   List list = new ArrayList();
		   try 
		   {
			 //  SysLogger.info("select * from nms_comp_detail_rule "  + condition);
			   rs = conn.executeQuery("select * from nms_comp_detail_rule "  + condition);
			   
			   if(rs == null)return null;
			   while(rs.next())
				  list.add(loadFromRS(rs));				
		   } 
		   catch(Exception e) 
		   {
	           list = null;
	           e.printStackTrace();
			   SysLogger.error("BaseDao.findByCondition()",e);
		   }
		   finally
		   {
			   if(rs != null){
				   try{
					   rs.close();
				   }catch(Exception e){
				   }
			   }
			  
		   }
		   return list;	
	   }
}
