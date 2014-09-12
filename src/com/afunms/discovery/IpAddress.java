/**
 * <p>Description:IPAddress</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.discovery;

public class IpAddress
{
   private String ifIndex;  
   private String ipAddress;
   private String physAddress;
   
   public IpAddress()
   {	  
   }
   
   public IpAddress(String ifIndex,String ipAddress)
   {
   	  this.ifIndex = ifIndex;
	  this.ipAddress = ipAddress;
   }
   
   public String getIfIndex() 
   {
   	  return ifIndex;
   }
   
   public void setIfIndex(String ifIndex) 
   {
   	  this.ifIndex = ifIndex;
   }

   public String getIpAddress() 
   {
   	  return ipAddress;
   }

   public void setIpAddress(String ipAddress) 
   {
   	  this.ipAddress = ipAddress;
   }
   
   public String getPhysAddress() 
   {
	   return physAddress;
   }

   public void setPhysAddress(String physAddress) 
   {
	   this.physAddress = physAddress;
   }

   public boolean equals(Object obj)
   {
       if (obj == null)
          return false;
       if (!(obj instanceof IpAddress))
          return false;
       
       IpAddress that = (IpAddress) obj;
       if (this.getIpAddress().equals(that.getIpAddress())
       	&& this.getIfIndex().equals(that.getIfIndex()))                
          return true;
       else 
          return false;
   }

   public int hashCode()
   {
   	   int result = 1;
   	   result = result * 31 + this.getIpAddress().hashCode();
   	   result = result * 31 + this.getIfIndex().hashCode();
   	   return result;
   }   
}
