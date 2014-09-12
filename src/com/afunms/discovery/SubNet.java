/**
 * <p>Description:subnet</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.discovery;

public class SubNet
{     
    private int id;   
    private String ipAddress; //网关的IP
    private String ifIndex;   //所在接口索引
    private String netAddress;
    private String netMask;
      
    public SubNet()
    {    
    }
    
    public SubNet(String netAddress,String netMask)
    {
       this.netAddress = netAddress;
       this.netMask = netMask;      
    }

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}
	
	public String getIpAddress() 
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) 
	{	
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
	
	public String getNetAddress() 
	{
		return netAddress;
	}

	public void setNetAddress(String netAddress) 
	{
		this.netAddress = netAddress;
	}

	public String getNetMask() 
	{
		return netMask;
	}

	public void setNetMask(String netMask) 
	{
		this.netMask = netMask;
	}   
	
	/**
	 * 覆盖equals和hashCode方法
	 * 如果netAddress和netMask一样,认为是同一个对象
	 */
    public boolean equals(Object obj)
    {
        if (obj == null)
           return false;
        if (! (obj instanceof SubNet))
           return false;
        
        SubNet that = (SubNet) obj;
        if (this.getNetAddress().equals(that.getNetAddress())
        	&& this.getNetMask().equals(that.getNetMask()))                
           return true;
        else 
           return false;
    }

    public int hashCode()
    {
    	int result = 1;
    	result = result * 31 + this.getNetAddress().hashCode();
    	result = result * 31 + this.getNetMask().hashCode();
    	return result;
    }
}
