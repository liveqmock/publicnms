/**
 * <p>Description:generate key for discover node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.topology.util;

import java.sql.ResultSet;

import com.afunms.common.util.DBManager;

public class KeyGenerator
{
   private static KeyGenerator keygen = new KeyGenerator();  
   private KeyGenerator() {}

   public static KeyGenerator getInstance()
   {
	   return keygen;
   }

   public synchronized int getNextKey()
   {
	   DBManager db = new DBManager();
	   int id = 0;
	   ResultSet rs = null;
	   try
	   {
		   rs = db.executeQuery("select id from topo_node_id");
		   if(rs.next())
		   {
			   id = rs.getInt(1);
			   db.executeUpdate("update topo_node_id set id=id+1");
		   }
		   else
		   {
			   db.executeUpdate("insert into topo_node_id(id)values(2)");
			   id = 1;
		   } 		   
	   }
	   catch(Exception e){}finally{
		   if(rs != null){
			   try{
				   rs.close();
			   }catch(Exception e){
				   
			   }
		   }
		   db.close();
	   }
	  
	   return id;
   }  
}
