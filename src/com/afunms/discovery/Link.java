/**
 * <p>Description:link,which connects two ports</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-10
 */

package com.afunms.discovery;

public class Link
{
    private int startId;
    private int endId;    
    private String startIndex;
    private String endIndex;    
    private String startPort;
    private String endPort;    
    private String startDescr;
    private String endDescr;    
    private String startIp;
    private String endIp;    
    private String startPhysAddress;  
    private String endPhysAddress;      
    private int assistant;
    private int findtype;//连接的发现方式(router,bridge,cdp,ndp,mac,byhand)
    private int linktype;//连接类型(physical,logical)
    private int sublinktype;//混合连接类型(NONEPHYSICALLINK = 1,STARTPHYSICALLINK=2,ENDPHYSICALLINK=3,BOTHPHYSICALLINK = 4)
    private String vlanStartIndex;
    private String vlanEndIndex;
    private String vlanEndIp;
    private String startAlias;
    private String endAlias;
    
    public Link()
    {
    	assistant = 0;	
    }
  
	public String getEndAlias() {
		return endAlias;
	}

	public void setEndAlias(String endAlias) {
		this.endAlias = endAlias;
	}
    
	public String getStartAlias() {
		return startAlias;
	}

	public void setStartAlias(String startAlias) {
		this.startAlias = startAlias;
	}
    
	public int getSublinktype() {
		return sublinktype;
	}

	public void setSublinktype(int sublinktype) {
		this.sublinktype = sublinktype;
	}
	public int getLinktype() {
		return linktype;
	}

	public void setLinktype(int linktype) {
		this.linktype = linktype;
	}
	
	public int getFindtype() {
		return findtype;
	}

	public void setFindtype(int findtype) {
		this.findtype = findtype;
	}
	
	public int getAssistant() {
		return assistant;
	}

	public void setAssistant(int assistant) {
		this.assistant = assistant;
	}

	public String getEndPhysAddress() {
		return endPhysAddress;
	}

	public void setEndPhysAddress(String endPhysAddress) {
		this.endPhysAddress = endPhysAddress;
	}

	public String getStartPhysAddress() {
		return startPhysAddress;
	}

	public void setStartPhysAddress(String startPhysAddress) {
		this.startPhysAddress = startPhysAddress;
	}

	public String getEndDescr() 
	{
		return endDescr;
	}

	public void setEndDescr(String endDescr) 
	{
		this.endDescr = endDescr;
	}

	public int getEndId() 
	{
		return endId;
	}

	public void setEndId(int endId) 
	{
		this.endId = endId;
	}

	public String getEndIndex() 
	{
		return endIndex;
	}

	public void setEndIndex(String endIndex) 
	{
		this.endIndex = endIndex;
	}

	public String getEndIp() 
	{
		return endIp;
	}

	public void setEndIp(String endIp) 
	{
		this.endIp = endIp;
	}

	public String getStartDescr() 
	{
		return startDescr;
	}

	public void setStartDescr(String startDescr) 
	{
		this.startDescr = startDescr;
	}

	public int getStartId() 
	{
		return startId;
	}

	public void setStartId(int startId) 
	{
		this.startId = startId;
	}

	public String getStartIndex() 
	{
		return startIndex;
	}

	public void setStartIndex(String startIndex) 
	{
		this.startIndex = startIndex;
	}
	
	public String getVlanStartIndex() 
	{
		return vlanStartIndex;
	}

	public void setVlanStartIndex(String vlanStartIndex) 
	{
		this.vlanStartIndex = vlanStartIndex;
	}
	
	public String getVlanEndIndex() 
	{
		return vlanEndIndex;
	}

	public void setVlanEndIndex(String vlanEndIndex) 
	{
		this.vlanEndIndex = vlanEndIndex;
	}
	
	public String getVlanEndIp() 
	{
		return vlanEndIp;
	}

	public void setVlanEndIp(String vlanEndIp) 
	{
		this.vlanEndIp = vlanEndIp;
	}

	public String getStartIp() 
	{
		return startIp;
	}

	public void setStartIp(String startIp) 
	{
		this.startIp = startIp;
	}

	public String getEndPort() 
	{
		return endPort;
	}
	
	public void setEndPort(String endPort) 
	{
		this.endPort = endPort;
	}
	
	public String getStartPort() 
	{
		return startPort;
	}
	
	public void setStartPort(String startPort) 
	{
		this.startPort = startPort;
	}
	
    public boolean equals(Object obj)
    {
       if (obj == null)
          return false;
       if (!(obj instanceof Link))
          return false;
      
       Link that = (Link) obj;
       if (startId == that.startId && endId == that.endId
    	 && startIndex.equals(that.startIndex) && endIndex.equals(that.endIndex))
    	  return true; 
       else if(startId == that.endId && endId == that.startId
    	 && startIndex.equals(that.endIndex) && endIndex.equals(that.startIndex))	                	   
          return true;
       else 
          return false;
    }

    public int hashCode()
    {
  	   int result = 1;
  	   result = result * 31 + startId;
  	   result = result * 31 + endId;
  	   return result;
    }
        
    public String toString()
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append(startIp);
    	sb.append("[index=");
    	sb.append(startIndex);
    	sb.append(",port=");
    	sb.append(startPort);
    	sb.append(",descr=");
    	sb.append(startDescr);
    	sb.append("]--->");
    	sb.append(endIp);
    	sb.append("[index=");
    	sb.append(endIndex);
    	sb.append(",port=");
    	sb.append(endPort);
    	sb.append(",descr=");
    	sb.append(endDescr);
    	sb.append("]");
    	return sb.toString();
    }
}
