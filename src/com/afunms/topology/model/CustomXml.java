/**
 * <p>Description:mapping table NMS_TOPO_XML</p>
 * <p>Company: dhcc.com</p>
 * @author Äô³Éº£
 * @project afunms
 * @date 2006-10-20
 */

package com.afunms.topology.model;

import  com.afunms.common.base.BaseVo;

public class CustomXml extends BaseVo
{
	private int id;
	private String xmlName;
	private String viewName;

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public String getXmlName() 
	{
		return xmlName;
	}

    public void setXmlName(String xmlName) 
    {
		this.xmlName = xmlName;
	}
	
	public String getViewName() 
	{
		return viewName;
	}
	
	public void setViewName(String viewname) 
	{
		this.viewName = viewname;
	}	
}
