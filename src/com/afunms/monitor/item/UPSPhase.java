/**
 * <p>Description:ups phase,includes input and output</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project 齐鲁石化
 * @date 2007-1-24
 */

package com.afunms.monitor.item;

public class UPSPhase
{
	private int index;
	private int voltage;  //电压
	private int frequency; //频率
	private int current;  //电流
	private int load;    //负载
	private int loadPercent;    //负载%	
	private int io;  //1=输入,0=输出
	
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getIo() {
		return io;
	}
	public void setIo(int io) {
		this.io = io;
	}
	public int getVoltage() {
		return voltage;
	}
	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}
	public int getLoad() {
		return load;
	}
	public void setLoad(int load) {
		this.load = load;
	}
	public int getLoadPercent() {
		return loadPercent;
	}
	public void setLoadPercent(int loadPercent) {
		this.loadPercent = loadPercent;
	}	
}