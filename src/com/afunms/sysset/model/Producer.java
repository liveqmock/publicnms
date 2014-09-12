/**
 * <p>Description:mapping table NMS_PRODUCER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-16
 */

package com.afunms.sysset.model;

import com.afunms.common.base.BaseVo;

public class Producer extends BaseVo
{
	private int id;   
	private String producer;
	private String enterpriseOid;
	private String website;
	
	public Producer()
	{		
	}
	
	public Producer(int id)
	{		
		this.id = id;
	}
	
	public String getEnterpriseOid() {
		return enterpriseOid;
	}
	public void setEnterpriseOid(String enterpriseOid) {
		this.enterpriseOid = enterpriseOid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}	
    public boolean equals(Object obj)
    {
       if (obj == null)
          return false;
       if (!(obj instanceof Producer))
          return false;
       
       Producer that = (Producer) obj;
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
