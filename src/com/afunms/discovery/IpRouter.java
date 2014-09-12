package com.afunms.discovery;

public class IpRouter
{
   private String ifIndex; //ipIndex
   private String dest;    //ipRouterDest
   private String nextHop; //ipRouterNextHop
   private int type;       //ipRouterType
   private int proto;       //ipRouterProto  
   private String mask;    //ipRouterMask
   private String physAddress;
   private int metric;
   
   public void setDest(String dest)
   {
      this.dest = dest;
   }
	/**
	 * @return Returns the metric.
	 */
	public int getMetric() {
		return metric;
	}
	/**
	 * @param metric The metric to set.
	 */
	public void setMetric(int metric) {
		this.metric = metric;
	}
   public String getDest()
   {
      return dest;
   }

   public void setNextHop(String nextHop)
   {
      this.nextHop = nextHop;
   }

   public String getNextHop()
   {
      return nextHop;
   }

   public void setType(int type)
   {
      this.type = type;
   }

   public int getType()
   {
      return type;
   }

   public void setMask(String mask)
   {
      this.mask = mask;
   }

   public String getMask()
   {
      return mask;
   }

   public String getIfIndex()
   {
  	  return ifIndex;
   }

   public void setIfIndex(String ifIndex)
   {
  	  this.ifIndex = ifIndex;
   }

   public int getProto() 
   {
	  return proto;
   }
	
   public void setProto(int proto) 
   {
	  this.proto = proto;
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
       if (!(obj instanceof IpRouter))
          return false;
       
       IpRouter that = (IpRouter) obj;
       if (this.getDest().equals(that.getDest())
       	&& this.getNextHop().equals(that.getNextHop()))                
          return true;
       else 
          return false;
   }

   public int hashCode()
   {
   	   int result = 1;
   	   result = result * 31 + this.getDest().hashCode();
   	   result = result * 31 + this.getNextHop().hashCode();
   	   return result;
   }      
}
