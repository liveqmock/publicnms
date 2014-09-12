/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-14
 */

package com.afunms.polling.node;

public class IfEntity implements Comparable<IfEntity>
{
	private int id;
	private String alias;
	private String index;	
	private int type;
	private String descr;  
	private String ipAddress;  
	private String physAddress; 
	private String port;
	
	private long speed;	
	private long inOctets;
	private long outOctets;
	private long errorPkts;
	private long discardPkts;
	private long inspeed;
	private long outspeed;
	
	private float rxUtilization;
	private float txUtilization;
	private long rxTraffic;
	private long txTraffic;	
	private int errors;
	private int discards;
	private int operStatus;
	
	private int chassis;
	private int slot;
	private int uport;
	
	public IfEntity()
	{
	   index = null;
	   descr = null;
	   ipAddress = null;
	   physAddress = null;
	   port = null;
	}
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public int getDiscards() {
		return discards;
	}
	public void setDiscards(int discards) {
		this.discards = discards;
	}
	public int getErrors() {
		return errors;
	}
	public void setErrors(int errors) {
		this.errors = errors;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getPhysAddress() {
		return physAddress;
	}
	public void setPhysAddress(String physAddress) {
		this.physAddress = physAddress;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public float getRxUtilization() {
		return rxUtilization;
	}
	public void setRxUtilization(float rxUtilization) {
		this.rxUtilization = rxUtilization;
	}
	public long getSpeed() {
		return speed;
	}
	public void setSpeed(long speed) {
		this.speed = speed;
	}
	
	public long getInspeed() {
		return inspeed;
	}
	public void setInspeed(long inspeed) {
		this.inspeed = inspeed;
	}
	
	public long getOutspeed() {
		return outspeed;
	}
	public void setOutspeed(long outspeed) {
		this.outspeed = outspeed;
	}
	
	public int getOperStatus() 
	{
		return operStatus;
	}

	public void setOperStatus(int operStatus) 
	{
		this.operStatus = operStatus;
	}
	public float getTxUtilization() {
		return txUtilization;
	}
	public void setTxUtilization(float txUtilization) {
		this.txUtilization = txUtilization;
	}

	public long getInOctets() {
		return inOctets;
	}

	public void setInOctets(long inOctets) {
		this.inOctets = inOctets;
	}

	public long getOutOctets() {
		return outOctets;
	}

	public void setOutOctets(long outOctets) {
		this.outOctets = outOctets;
	}

	public long getDiscardPkts() {
		return discardPkts;
	}

	public void setDiscardPkts(long discardPkts) {
		this.discardPkts = discardPkts;
	}

	public long getErrorPkts() {
		return errorPkts;
	}

	public void setErrorPkts(long errorPkts) {
		this.errorPkts = errorPkts;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the rxTraffic
	 */
	public long getRxTraffic() {
		return rxTraffic;
	}

	/**
	 * @param rxTraffic the rxTraffic to set
	 */
	public void setRxTraffic(long rxTraffic) {
		this.rxTraffic = rxTraffic;
	}

	/**
	 * @return the txTraffic
	 */
	public long getTxTraffic() {
		return txTraffic;
	}

	/**
	 * @param txTraffic the txTraffic to set
	 */
	public void setTxTraffic(long txTraffic) {
		this.txTraffic = txTraffic;
	}
	
	public int getChassis() {
		return chassis;
	}

	public void setChassis(int chassis) {
		this.chassis = chassis;
	}
	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}
	public int getUport() {
		return uport;
	}

	public void setUport(int uport) {
		this.uport = uport;
	}

	public int compareTo(IfEntity ifEntity) {
		if(ifEntity == null){
			return -1;
		}
		int thisIndex = Integer.parseInt(this.getIndex());
		int ifEntityIndex = Integer.parseInt(ifEntity.getIndex());
		if(thisIndex > ifEntityIndex){
			return 1;
		}else if(thisIndex < ifEntityIndex){
			return -1;
		}else{
			return 0;
		}
	}
}