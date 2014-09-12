/**
 * <p>Description:stat nodes data,service for JFreeChart</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-8
 */

package com.afunms.inform.dao;

import java.sql.ResultSet;
import java.util.*;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.*;

public class NewDataDao extends BaseDao 
{
    private String[] rowKeys;
    private String[] colKeys;
    public NewDataDao()
    {
	    super("");
    }
   
    public double[][] multiStat(String day,int nodeId,String moid,boolean isValue,int timeType)
    {
    	return multiStat(1,day,nodeId,moid,isValue,timeType);
    }

    public double[][] interfaceStat(String day,int nodeId,String moid,boolean isValue,int timeType)
    {
    	return multiStat(2,day,nodeId,moid,isValue,timeType);
    }
    
    /**
     * table:表名,topo_interface_data或topo_node_multi_data(这两个表的字段一样)
     * day:日期,nodeId:节点id,moid:指标id,isValue:取字段value或percentage的值
     * timeType:1=按天统计,2=按小时统计，3=按小时和分钟统计 
     */
    private double[][] multiStat(int table,String day,int nodeId,String moid,boolean isValue,int timeType)
    {
        double[][] result = null;
        StringBuffer sql1 = new StringBuffer();
        StringBuffer sql2 = new StringBuffer();
        StringBuffer sql3 = new StringBuffer();
        try
        {       	 		
            //计算有多少个实体   
        	if(table==1)
        	   sql1.append("select -- from topo_node_multi_data where moid='");
        	else
        	   sql1.append("select -- from topo_interface_data where moid='");	
        	sql1.append(moid);
        	sql1.append("' and node_id=");
        	sql1.append(nodeId);        	
        	if(timeType==1) //如果按天，应该计算这个月里有多少个实体
        	{
        		sql1.append(" and substring(log_time,1,7)='");
        		sql1.append(day.substring(0,7));        		
        	}
        	else
        	{
        		sql1.append(" and substring(log_time,1,10)='");
        		sql1.append(day);
        	}
        	sql1.append("'");
        	rs = conn.executeQuery("select count(*) from (" + sql1.toString().replace("--", "entity") + " group by entity) t");
            int entityTotal = 0;
            if(rs.next()) entityTotal = rs.getInt(1);     	   
            rs.close();
            if(entityTotal==0) return null;            
            
            rowKeys = new String[entityTotal];
            rs = conn.executeQuery(sql1.toString().replace("--", "entity") + " group by entity");
            Hashtable rowIndex = new Hashtable();       
            int index = 0;
            while(rs.next())
            {            
            	rowIndex.put(rs.getString("entity"),new Integer(index));
                rowKeys[index] = rs.getString("entity");
                index++;
            }        
            rs.close();
                        
            if(timeType==1) //如果按天
            {
                sql2.append(sql1.toString().replace("--", "substring(log_time,6,5) logtime"));
                sql2.append(" group by substring(log_time,6,5)");
            }
            else if(timeType==2) //如果小时
            {
                sql2.append(sql1.toString().replace("--", "substring(log_time,12,2) logtime"));
                sql2.append(" group by substring(log_time,12,2)");
            }
            else
               sql2.append(sql1.toString().replace("--", "substring(log_time,12,5) logtime"));  
            sql2.append(" order by logtime desc");
            rs = conn.executeQuery("select count(*) from (" + sql2.toString() + ") t");
            int timeTotal = 0;
            if(rs.next()) timeTotal = rs.getInt(1); //计算有多少个时间
            rs.close();
            
            colKeys = new String[timeTotal];
            index = 0;  
            Hashtable colIndex = new Hashtable(); 
            rs = conn.executeQuery(sql2.toString());
            while(rs.next())
            {
            	colIndex.put(rs.getString("logtime"),new Integer(index));
            	colKeys[index] = rs.getString("logtime");
                index++;
            }
            rs.close();
            
            if(timeType==1) //如果按天
            {
                sql3.append(sql1.toString().replace("--", "entity,substring(log_time,6,5) logtime,**"));
                sql3.append(" group by entity,substring(log_time,6,5)");
            }
            else if(timeType==2) //如果小时
            {
                sql3.append(sql1.toString().replace("--", "entity,substring(log_time,12,2) logtime,**"));
                sql3.append(" group by entity,substring(log_time,11,3)");
            }
            else
            {
                sql3.append(sql1.toString().replace("--", "entity,substring(log_time,12,5) logtime,**"));
                sql3.append(" group by entity,substring(log_time,12,5)");
            }
            sql3.append("order by entity,logtime");
            
            String sql = null;
            if(isValue)
               sql = sql3.toString().replace("**", "round(avg(value),0) avgvalue");
            else
               sql = sql3.toString().replace("**", "round(avg(percentage),2) avgvalue");	
         
            rs = conn.executeQuery(sql);
            result = new double[entityTotal][timeTotal];
            int row = 0, col = 0;
            while(rs.next())
            {
                row = ((Integer)rowIndex.get(rs.getString("entity"))).intValue();
                col = ((Integer)colIndex.get(rs.getString("logtime"))).intValue();
                result[row][col] = rs.getDouble("avgvalue");
            }      
        }
        catch (Exception ex)
        {
            SysLogger.error("Error in DataDao.multiStat()",ex);
            result = null;
        }
        finally
        {
            conn.close();
        }
        return result;
    }   
    
    public String[] getRowKeys()
    {
        return rowKeys;	
    }
    
    public String[] getColKeys()
    {
        return colKeys;	
    }
    
    public BaseVo loadFromRS(ResultSet rs)
    {
	   return null;
    }
}