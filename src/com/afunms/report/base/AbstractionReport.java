/**
 * <p>Description:abstraction report,bridge pattern</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

package com.afunms.report.base;

public abstract class AbstractionReport 
{
    protected String fileName;
    protected ImplementorReport impReport;
    
    public AbstractionReport(ImplementorReport impReport)
    {
    	this.impReport = impReport;
    	impReport.createReport();	
    }
    
    public String getFileName()
    {
    	return fileName;
    }
    
    public abstract void createReport();
}