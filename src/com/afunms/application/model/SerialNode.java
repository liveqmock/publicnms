package com.afunms.application.model;

//
//这个类是串口设备node的model类 ， 其对应的数据表的名字为 system_serial_node
//

import com.afunms.common.base.BaseVo;

/**
 * 
 * This class is a serial device node of the model class, 
 * its corresponding data table name is system_serial_node
 * 
 * @author nielin
 * create at 2010-01-28</br>
 *
 */
public class SerialNode extends BaseVo {
	
	/**
	 * Default Primary key
	 */
	private int id;
	
	/**
	 * Serial device address
	 */
	private String address;
	
	/**
	 * Serial device name
	 */
	private String name;
	
	/**
	 * Serial device description
	 */
	private String description;
	
	/**
	 * Whether the monitoring.  0 is true , 1 is false
	 */
	private String monflag;
	
	/**
	 * the serialPortId of the serial to be used. COM1, COM2, etc.
	 */
	private String serialPortId;
	
	/**
	 * the number of Baud Rate(For Exemaple: 9600)
	 */
	private String baudRate;
	
	/**
	 * the number of data bit( it must be for the following data : 5 , 6 , 7 or 8)
	 * 
	 * @see javax.comm.SerialPort.DATABITS_5 == 5
	 * @see javax.comm.SerialPort.DATABITS_6 == 6
	 * @see javax.comm.SerialPort.DATABITS_7 == 7
	 * @see javax.comm.SerialPort.DATABITS_8 == 8
	 * 
	 */
	private String databits;
	
	/**
	 * the number of STOP bits (it must be for the following data : 1 , 2 or 3 )
	 * 
	 * @see javax.comm.SerialPort.STOPBITS_1   == 1
	 * @see javax.comm.SerialPort.STOPBITS_2   == 2
	 * @see javax.comm.SerialPort.STOPBITS_1_5 == 3
	 * 
	 */
	private String stopbits;
	
	/**
	 * the number of Parity (it must be for the following data : 0 , 1 , 2 , 3 or 4)
	 * 
	 * @see javax.comm.SerialPort.PARITY_NONE  == 0
	 * @see javax.comm.SerialPort.PARITY_ODD   == 1
	 * @see javax.comm.SerialPort.PARITY_EVEN  == 2
	 * @see javax.comm.SerialPort.PARITY_MARK  == 3
	 * @see javax.comm.SerialPort.PARITY_SPACE == 4
	 *
	 */
	private String parity;
	
	/**
	 * Owned business
	 */
	private String bid;
	
	/**
	 * Send the user's mailbox
	 */
	private String sendMail;

	public SerialNode() {
		// TODO Auto-generated constructor stub
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

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	
	
	/**
	 * @return the monflag
	 */
	public String getMonflag() {
		return monflag;
	}

	/**
	 * @param monfalg the monflag to set
	 */
	public void setMonflag(String monflag) {
		this.monflag = monflag;
	}

	/**
	 * @return the serialPortId
	 */
	public String getSerialPortId() {
		return serialPortId;
	}

	/**
	 * @param serialPortId the serialPortId to set
	 */
	public void setSerialPortId(String serialPortId) {
		this.serialPortId = serialPortId;
	}

	/**
	 * @return the baudRate
	 */
	public String getBaudRate() {
		return baudRate;
	}

	/**
	 * @param baudRate the baudRate to set
	 */
	public void setBaudRate(String baudRate) {
		this.baudRate = baudRate;
	}

	/**
	 * @return the databits
	 */
	public String getDatabits() {
		return databits;
	}

	/**
	 * @param databits the databits to set
	 */
	public void setDatabits(String databits) {
		this.databits = databits;
	}

	/**
	 * @return the stopbits
	 */
	public String getStopbits() {
		return stopbits;
	}

	/**
	 * @param stopbits the stopbits to set
	 */
	public void setStopbits(String stopbits) {
		this.stopbits = stopbits;
	}

	/**
	 * @return the parity
	 */
	public String getParity() {
		return parity;
	}

	/**
	 * @param parity the parity to set
	 */
	public void setParity(String parity) {
		this.parity = parity;
	}

	/**
	 * @return the bid
	 */
	public String getBid() {
		return bid;
	}

	/**
	 * @param bid the bid to set
	 */
	public void setBid(String bid) {
		this.bid = bid;
	}

	/**
	 * @return the sendMail
	 */
	public String getSendMail() {
		return sendMail;
	}

	/**
	 * @param sendMail the sendMail to set
	 */
	public void setSendMail(String sendMail) {
		this.sendMail = sendMail;
	}

}
