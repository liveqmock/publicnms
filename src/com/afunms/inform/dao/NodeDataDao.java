/**
 * <p>Description:被监视节点数据</p>
 * <p>Company: 北京东华合创数码科技股份有限公司</p>
 * @author 王福民
 * @project 阿福网管
 * @date 2006-10-23
 */

package com.afunms.inform.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.*;

public class NodeDataDao extends BaseDao 
{
   public NodeDataDao()
   {
	   super("");
   }
   
   public float[] singleStat(String day,int nodeId,String moid)
   {
      String sql = "select substring(log_time,11,3) loghour,round(avg(value),1) avgvalue from"   
              + " topo_node_single_data where substring(log_time,1,10)='" + day + " 'and moid='"  
              + moid + "' and node_id=" + nodeId + " group by substring(log_time,1,13)";

	  float[] table = new float[24];
	  int total = 0; 
      try
      {
          rs = conn.executeQuery(sql);          
          for(int i=0;i<24;i++)
             table[i] = 0;                    
          while(rs.next())
          {
             table[rs.getInt("loghour")] = rs.getFloat("avgvalue");
             total ++;
          }          
      }
      catch (Exception ex)
      {
          SysLogger.error("Error in ReportDao.singleStat()",ex);
      }
      finally
      {
         conn.close();
      }
      return table;
   }

   public String[][] multiStat(String day,int nodeId,String moid)
   {
      String[][] result = null;
//--------------得到一个这样的数组:第一行为entity--------------
//      时间   | 00:00   | 01:00  | 02:00   | ............
//    entity1 | value1  | value2 | value3  | ............
//    entity2 | value4  | value5 | value6  | ............
//-------------------------------------------------------------
      try
      {       	 
        String sql1 = "select entity from topo_node_multi_data where moid='" + moid + "'"
               + " and node_id=" + nodeId + " and substring(log_time,1,10)='" + day 
               + "' group by entity "; 
        
        rs = conn.executeQuery("select count(*) from (" + sql1 + ") t");
        int entityTotal = 0;
        if(rs.next()) entityTotal = rs.getInt(1);     	   
        if(entityTotal==0) return null;

        result = new String[entityTotal][25];
        for(int i=0;i<result.length;i++)
           for(int j=0;j<result[0].length;j++)
             result[i][j] = "";  //初始化

        rs = conn.executeQuery(sql1);
        Hashtable tsList = new Hashtable();       
        int i = 0;
        while(rs.next())
        {
            result[i][24] = rs.getString("entity");
            tsList.put(rs.getString("entity"),new Integer(i));
            i++;
        } 
       
        String sql2 = "select entity,substring(log_time,11,3) loghour,round(avg(percentage),1) avgvalue"  
           + " from topo_node_multi_data where moid='" + moid + "' and node_id=" + nodeId + " and " 
           + " substring(log_time,1,10)='" + day + "' group by node_id,entity,substring(log_time,1,13)";
                
        rs = conn.executeQuery(sql2);
        int row = 0;
        while(rs.next())
        {
            row = ((Integer)tsList.get(rs.getString("entity"))).intValue();
            result[row][rs.getInt("loghour")] = rs.getString("avgvalue");
        }      
     }
     catch (Exception ex)
     {
         SysLogger.error("Error in ReportDao.multiStat()",ex);
         result = null;
     }
     finally
     {
        conn.close();
     }
     return result;
   }   

   public String[][] trafficStat(String day,int nodeId,String moid,boolean isValue)
   {
      String[][] result = null;
      try
      {       	 
        String sql1 = "select entity from topo_interface_data where moid='" + moid + "'"
               + " and node_id=" + nodeId + " and substring(log_time,1,10)='" + day 
               + "' group by entity ";  
        rs = conn.executeQuery("select count(*) from (" + sql1 + ") t");
        int entityTotal = 0;
        if(rs.next()) entityTotal = rs.getInt(1);     	   
        if(entityTotal==0) return null;

        result = new String[entityTotal][25];
        for(int i=0;i<result.length;i++)
           for(int j=0;j<result[0].length;j++)
             result[i][j] = "";  //初始化

        rs = conn.executeQuery(sql1);
        Hashtable tsList = new Hashtable();       
        int i = 0;
        while(rs.next())
        {
            result[i][24] = rs.getString("entity");
            tsList.put(rs.getString("entity"),new Integer(i));
            i++;
        }        
        String temp = null;
        if(isValue)
           temp = "round(avg(value),0) avgvalue";
        else
           temp = "round(avg(percentage),2) avgvalue";	
        String sql2 = "select entity,substring(log_time,11,3) loghour," + temp 
           + " from topo_interface_data where moid='" + moid + "' and node_id=" + nodeId + " and " 
           + " substring(log_time,1,10)='" + day + "' group by node_id,entity,substring(log_time,1,13)";
        
        rs = conn.executeQuery(sql2);
        int row = 0;
        while(rs.next())
        {
            row = ((Integer)tsList.get(rs.getString("entity"))).intValue();
            result[row][rs.getInt("loghour")] = rs.getString("avgvalue");
        }      
     }
     catch (Exception ex)
     {
         SysLogger.error("Error in ReportDao.multiStat()",ex);
         result = null;
     }
     finally
     {
        conn.close();
     }
     return result;
   }   

   public String[][] diskStat(String day,int nodeId)
   {
      String[][] result = null;
//--------------得到一个这样的数组:第一行为entity--------------
//      时间   | 10-01   | 10-02  | 10-03   | ............
//    entity1 | value1  | value2 | value3  | ............
//    entity2 | value4  | value5 | value6  | ............
//-------------------------------------------------------------
      try
      {       	 
        String sql1 = "select entity from topo_node_multi_data where moid='001003'"
               + " and node_id=" + nodeId + " and substring(log_time,1,7)='" + day.substring(0,7) 
               + "' group by entity ";  
        
        rs = conn.executeQuery("select count(*) from (" + sql1 + ") t");
        int entityTotal = 0;
        if(rs.next()) entityTotal = rs.getInt(1);     	   
        if(entityTotal==0) return null;

        int year = Integer.parseInt(day.substring(0,4));
        int month = Integer.parseInt(day.substring(5,7));
        int cols = SysUtil.getDaysOfMonth(year, month);
        result = new String[entityTotal][cols + 1];
        for(int i=0;i<result.length;i++)
           for(int j=0;j<result[0].length;j++)
             result[i][j] = "";  //初始化

        rs = conn.executeQuery(sql1);
        Hashtable tsList = new Hashtable();       
        int i = 0;
        while(rs.next())
        {
            result[i][0] = rs.getString("entity");
            tsList.put(rs.getString("entity"),new Integer(i));
            i++;
        } 
       
        String sql2 = "select entity,substring(log_time,9,3) logday,round(avg(percentage),1) avgvalue"  
           + " from topo_node_multi_data where moid='001003' and node_id=" + nodeId + " and " 
           + " substring(log_time,1,7)='" + day.substring(0,7) + "' group by node_id,entity,substring(log_time,1,10)";       
        rs = conn.executeQuery(sql2);
        
        int row = 0;
        while(rs.next())
        {
            row = ((Integer)tsList.get(rs.getString("entity"))).intValue();
            result[row][rs.getInt("logday")] = rs.getString("avgvalue");
        }      
     }
     catch (Exception ex)
     {
         SysLogger.error("Error in ReportDao.diskStat()",ex);
         result = null;
     }
     finally
     {
        conn.close();
     }
     return result;
   }
   
   /**
    * 统计某个端口的出口入口流量
    */
   public String[][] portTrafficStat(int nodeId,String day,String ifIndex)
   {
	   String[][] result = null;
	   StringBuffer sqlIn = new StringBuffer(200);
   	   sqlIn.append("select round(AVG(value),0) value1,SUBSTRING(log_time,12,2) value2 from topo_interface_data");
   	   sqlIn.append(" where ((node_id=");
   	   sqlIn.append(nodeId);
   	   sqlIn.append(") and (entity='");
   	   sqlIn.append(ifIndex);
   	   sqlIn.append("') and (SUBSTRING(log_time,1,10)='");
   	   sqlIn.append(day);
   	   sqlIn.append("') and (moid='003002')) group by value2 order by log_time");
		
   	   StringBuffer sqlOut = new StringBuffer(200);
       sqlOut.append("select round(AVG(value),0) value1,SUBSTRING(log_time,12,2) value2 from topo_interface_data");
       sqlOut.append(" where ((node_id=");
       sqlOut.append(nodeId);
       sqlOut.append(") and (entity='");
       sqlOut.append(ifIndex);
       sqlOut.append("') and (SUBSTRING(log_time,1,10)='");
       sqlOut.append(day);
       sqlOut.append("') and (moid='003003')) group by value2 order by log_time");

	   try
	   {     	    	
	       result = new String[24][2];
	       for(int i=0;i<result.length;i++)
	          for(int j=0;j<result[0].length;j++)
	             result[i][j] = "0";  //初始化
	        
	       //入口
	       rs = conn.executeQuery(sqlIn.toString());
	       while(rs.next())
	          result[rs.getInt("value2")][0] = rs.getString("value1"); 
	        
	       //出口	       
	       rs = conn.executeQuery(sqlOut.toString());
	       while(rs.next())
	            result[rs.getInt("value2")][1] = rs.getString("value1");
	    }
	    catch (Exception ex)
	    {
	         SysLogger.error("Error in ReportDao.trafficDetail()",ex);
	         result = null;
	    }
	    finally
	    {
	        conn.close();
	    }	     
	    return result;	   
   }
   
   /**
    * 统计某个端口的出口入口利用率
    */
   public String[][] portUtilStat(int nodeId,String day,String index)
   {
	   String[][] result = null;
	      try
	      {      
	    	
	    	String sqlIn = "select round(AVG(percentage),1) value1,SUBSTRING(log_time,12,2) value2 from topo_interface_data" +
		   		" where ((node_id="+nodeId + ") and (entity='" +index + "') and (SUBSTRING(log_time,1,10)='" +
		   		day + "') and (moid='003002'))" +
		   		" group by value2 order by log_time";
	    	String sqlOut = "select round(AVG(percentage),1) value1,SUBSTRING(log_time,12,2) value2 from topo_interface_data" +
	   		" where ((node_id="+nodeId + ") and (entity='" +index + "') and (SUBSTRING(log_time,1,10)='" +
	   		day + "') and (moid='003003'))" +
	   		" group by value2 order by log_time";
	    	
	        result = new String[24][2];
	        for(int i=0;i<result.length;i++)
	           for(int j=0;j<result[0].length;j++)
	             result[i][j] = "0";  //初始化
	        
	        //入口
	        rs = conn.executeQuery(sqlIn);
	        while(rs.next())
	        {
	        	int tmp = (Integer.parseInt(rs.getString("value2")));
	            result[tmp][0] = rs.getString("value1");
	        } 
	        
	        //出口
	        rs = conn.executeQuery(sqlOut);
	        while(rs.next())
	        {
	        	int tmp = (Integer.parseInt(rs.getString("value2")));
	            result[tmp][1] = rs.getString("value1");
	        }      
	     }
	     catch (Exception ex)
	     {
	         SysLogger.error("Error in ReportDao.trafficDetail()",ex);
	         result = null;
	     }
	     finally
	     {
	        conn.close();
	     }	     
	     return result;	   
   }
         
   public BaseVo loadFromRS(ResultSet rs)
   {
	   return null;
   }
}