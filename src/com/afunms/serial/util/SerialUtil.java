package com.afunms.serial.util;

public class SerialUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SerialUtil simpleSerial = new SerialUtil();
		simpleSerial.getTemperature("");
		//System.out.println(simpleSerial.getResult(new Parameters(), ""));
	}
	
	public String getResult(Parameters parameters , String command){
		String result = "";
		parameters.setBaudRate(9600);
		parameters.setDatabits(7);
		parameters.setStopbits(2);
		parameters.setParity(0);
		parameters.setSerialPortId("COM1");
		command = ":01040080000576" +'\r' + '\n';
		SerialBean serialBean = new SerialBean(parameters);
		serialBean.initialize();
		for(int i = 0; i < 5 ; i++){
			serialBean.writeMsg(command);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String tempResult = serialBean.readMsg();
			if(tempResult!=null&&result.length()<=tempResult.length()){
				result = tempResult;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		serialBean.closePort();
		return result;
	}
	
	public String getTemperature(String result){
//		String temperature = "";
//		String temp1 = result.substring(10 , 14);
//		String temp2 = result.substring(11 , 20);
		String temp3 = "ff";
		System.out.println(Integer.valueOf(temp3,16));
		return null;
	}

}
