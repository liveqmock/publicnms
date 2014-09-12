package com.afunms.serial.util;

//3. ReadSerial
//ReadSerial是一个线程，它不断的从指定的串口读取数据并将其存放到缓冲区中。 
//
//public ReadSerial(SerialBuffer SB, InputStream Port)
//本函数构造一个ReadSerial进程，参数SB指定存放传入数据的缓冲区，参数Port指定从串口所接收的数据流。
//
//public void run()
//ReadSerial进程的主函数，它不断的从指定的串口读取数据并将其存放到缓冲区中。 

import java.io.*;

/**
 * 
 * This class reads message from the specific serial port and save the message
 * to the serial buffer.
 * 
 */
public class SerialRead extends Thread {
	private SerialBuffer ComBuffer;
	private InputStream ComPort;

	/**
	 * 
	 * Constructor
	 * 
	 * @param SB
	 *            The buffer to save the incoming messages.
	 * @param Port
	 *            The InputStream from the specific serial port.
	 * 
	 */
	public SerialRead(SerialBuffer SB, InputStream Port) {
		ComBuffer = SB;
		ComPort = Port;
	}

	public void run() {
		int c;
		try {
			while (true) {
				c = ComPort.read();
				if(c==-1){
					break;
				}
				ComBuffer.PutChar(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
