/**
 * <p>Description:mapping table NMS_TOPO_NODE</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;
import com.ibm.as400.access.ObjectDescription;

public class DiskForAS400 extends BaseVo {
	
	/**
	 * id
	 */
	private int id;
	
	/**
	 * 磁盘所属设备id
	 */
	private String nodeid;
	
	/**
	 * 磁盘所属设备ip
	 */
	private String ipaddress;
	
	/**
	 * 磁盘单元
	 */
	private String unit;
	
	/**
	 * 磁盘类型
	 */
	private String type;
	
	/**
	 * 磁盘大小(M)
	 */
	private String size;
	
	/**
	 * %磁盘利用百分比
	 */
	private String used;
	
	/**
	 * IO Rqs
	 */
	private String ioRqs;
	
	/**
	 * 被请求的大小(K)
	 */
	private String requestSize;
	
	/**
	 * 读 Rqs
	 */
	private String readRqs;
	
	/**
	 * 写 Rqs
	 */
	private String writeRqs;
	
	/**
	 * 读(K)
	 */
	private String read;
	
	/**
	 * 写(K)
	 */
	private String write;
	
	/**
	 * %忙碌百分比
	 */
	private String busy;
	
	/**
	 * 采集时间
	 */
	private String collectTime;

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

	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @param nodeid the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @param ipaddress the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @return the used
	 */
	public String getUsed() {
		return used;
	}

	/**
	 * @param used the used to set
	 */
	public void setUsed(String used) {
		this.used = used;
	}

	/**
	 * @return the ioRqs
	 */
	public String getIoRqs() {
		return ioRqs;
	}

	/**
	 * @param ioRqs the ioRqs to set
	 */
	public void setIoRqs(String ioRqs) {
		this.ioRqs = ioRqs;
	}

	/**
	 * @return the requestSize
	 */
	public String getRequestSize() {
		return requestSize;
	}

	/**
	 * @param requestSize the requestSize to set
	 */
	public void setRequestSize(String requestSize) {
		this.requestSize = requestSize;
	}

	/**
	 * @return the readRqs
	 */
	public String getReadRqs() {
		return readRqs;
	}

	/**
	 * @param readRqs the readRqs to set
	 */
	public void setReadRqs(String readRqs) {
		this.readRqs = readRqs;
	}

	/**
	 * @return the writeRqs
	 */
	public String getWriteRqs() {
		return writeRqs;
	}

	/**
	 * @param writeRqs the writeRqs to set
	 */
	public void setWriteRqs(String writeRqs) {
		this.writeRqs = writeRqs;
	}

	/**
	 * @return the read
	 */
	public String getRead() {
		return read;
	}

	/**
	 * @param read the read to set
	 */
	public void setRead(String read) {
		this.read = read;
	}

	/**
	 * @return the write
	 */
	public String getWrite() {
		return write;
	}

	/**
	 * @param write the write to set
	 */
	public void setWrite(String write) {
		this.write = write;
	}

	/**
	 * @return the busy
	 */
	public String getBusy() {
		return busy;
	}

	/**
	 * @param busy the busy to set
	 */
	public void setBusy(String busy) {
		this.busy = busy;
	}

	/**
	 * @return the collectTime
	 */
	public String getCollectTime() {
		return collectTime;
	}

	/**
	 * @param collectTime the collectTime to set
	 */
	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

	
	
}