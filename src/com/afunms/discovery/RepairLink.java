/**
 * <p>Description:link,which connects two ports</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.discovery;

import com.afunms.common.base.BaseVo;

public class RepairLink extends BaseVo
{   
	private int id;	
    private String startIndex;
    private String endIndex;    
     
    private String startIp;
    private String endIp;    
    
    private String newStartIndex;
    private String newEndIndex;

    
    public RepairLink()
    {	
    }
	public String getEndIndex() 
	{
		return endIndex;
	}

	public void setEndIndex(String endIndex) 
	{
		this.endIndex = endIndex;
	}
	
	public String getNewEndIndex() 
	{
		return newEndIndex;
	}

	public void setNewEndIndex(String newEndIndex) 
	{
		this.newEndIndex = newEndIndex;
	}

	public String getEndIp() 
	{
		return endIp;
	}

	public void setEndIp(String endIp) 
	{
		this.endIp = endIp;
	}

	public String getStartIndex() 
	{
		return startIndex;
	}

	public void setStartIndex(String startIndex) 
	{
		this.startIndex = startIndex;
	}
	
	public String getNewStartIndex() 
	{
		return newStartIndex;
	}

	public void setNewStartIndex(String newStartIndex) 
	{
		this.newStartIndex = newStartIndex;
	}
	
	public String getStartIp() 
	{
		return startIp;
	}

	public void setStartIp(String startIp) 
	{
		this.startIp = startIp;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
    public boolean equals(Object obj)
    {
       if (obj == null)
          return false;
       if (!(obj instanceof Link))
          return false;
      
       RepairLink that = (RepairLink) obj;
       if (startIp == that.startIp && endIp == that.endIp
    	 && startIndex.equals(that.startIndex) && endIndex.equals(that.endIndex))
    	  return true; 
       else if(startIp == that.endIp && endIp == that.startIp
    	 && startIndex.equals(that.endIndex) && endIndex.equals(that.startIndex))	                	   
          return true;
       else 
          return false;
    }

        
    public String toString()
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append(startIp);
    	sb.append("[index=");
    	sb.append(startIndex);
    	sb.append("]--->");
    	sb.append(endIp);
    	sb.append("[index=");
    	sb.append(endIndex);
    	sb.append("]");
    	return sb.toString();
    }
}
