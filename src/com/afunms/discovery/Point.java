package com.afunms.discovery;

public class Point
{
   private int id;
   private String ifIndex;
   
   public Point(int id,String ifIndex)
   {
	   this.id = id;
	   this.ifIndex = ifIndex;
   }
   
   public int getId() 
   {
		return id;
   }
   
   public void setId(int id) 
   {
		this.id = id;
   }
   
   public String getIfIndex() 
   {
		return ifIndex;
   }
   
   public void setIfIndex(String ifIndex) 
   {
		this.ifIndex = ifIndex;
   }
   
   public boolean equals(Object obj)
   {
       if (obj == null)
          return false;
       if (!(obj instanceof Point))
          return false;
       
       Point that = (Point) obj;
       if (this.getIfIndex().equals(that.getIfIndex())
       	&& this.getId()==that.getId())                
          return true;
       else 
          return false;
   }

   public int hashCode()
   {
   	   int result = 1;
   	   result = result * 31 + this.getId();
   	   result = result * 31 + this.getIfIndex().hashCode();
   	   return result;
   }   

   public String toString()
   {
	   return ifIndex + "@" + id; 
   }
}