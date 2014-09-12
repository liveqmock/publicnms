/**
 * <p>Description:mapping table NMS_ROLE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class Role extends BaseVo
{
   private int id;
   private String role;

   public void setId(int id)
   {
       this.id = id;
   }

   public int getId()
   {
       return id;
   }

   public void setRole(String role)
   {
       this.role = role;
   }

   public String getRole()
   {
       return role;
   }

   public boolean equals(Object obj)
   {
      if (obj == null)
          return false;
      if (!(obj instanceof Role))
          return false;
       
      Role that = (Role) obj;
      if (this.id == that.id)                
         return true;
      else 
         return false;
   }

   public int hashCode()
   {
   	   int result = 1;   	   
   	   result = result * 31 + this.id;
   	   return result;
   }   			
}
