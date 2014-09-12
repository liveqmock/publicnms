/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class Position extends BaseVo
{
	private int id;
	private String name;
	
	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public boolean equals(Object obj)
    {
       if (obj == null)
          return false;
       if (!(obj instanceof Position))
          return false;
       
       Position that = (Position) obj;
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
