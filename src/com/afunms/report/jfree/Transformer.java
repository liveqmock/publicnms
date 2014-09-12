/**
 * <p>Description:a artist who draw various chart</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-18
 */

package com.afunms.report.jfree;

import java.util.*;

import com.afunms.monitor.item.base.MonitorResult;

public class Transformer
{
	private String[] xKeys;
	private String[] yKeys;
	private double[][] data;
	
	public Transformer()
	{		
	}
	
    public void item2Array(List list)
    {
    	int len = list.size();
    	xKeys = new String[len];
    	yKeys = new String[]{"“—”√","Œ¥”√"};
    	data = new double[2][len];
    	
    	for(int i=0;i<len;i++)
    	{
    		MonitorResult mr = (MonitorResult)list.get(i);
    		xKeys[i] = mr.getEntity();
    		data[0][i] = mr.getPercentage();
    		data[1][i] = 100 - mr.getPercentage();
    	}
    }
    
    public String[] getXKey()
    {
    	return xKeys;
    }
    public String[] getYKey()
    {
    	return yKeys;
    }
    public double[][] getData()
    {
    	return data;
    }    
}