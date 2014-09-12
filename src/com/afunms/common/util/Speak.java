package com.afunms.common.util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Speak {

	public static String flag = "speak";
	
	/* 私有构造方法，防止被实例化 */
	private Speak() {
	}

	public void testSpeak(String message, int volumeValue, int rateValue) {
		ActiveXComponent sap = new ActiveXComponent("Sapi.SpVoice");
		Dispatch sapo = sap.getObject();
		try {

			sap.setProperty("Volume", new Variant(volumeValue));
			sap.setProperty("Rate", new Variant(rateValue));
			Dispatch.call(sapo, "Speak", new Variant(message));
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sapo.safeRelease();
			sap.safeRelease();
		}
			
	}
	
	public void beginSpeak(String message, int volumeValue, int rateValue, int times) {

		ActiveXComponent sap = new ActiveXComponent("Sapi.SpVoice");
		Dispatch sapo = sap.getObject();
		try {

			sap.setProperty("Volume", new Variant(volumeValue));
			sap.setProperty("Rate", new Variant(rateValue));

			for (int i = 0; i < times; i++) {
				if(!"stop".equals(flag)){
					Dispatch.call(sapo, "Speak", new Variant(message));
				}else{
					flag = "speak";
					break;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sapo.safeRelease();
			sap.safeRelease();
		}
	}

	public void stopSpeak(String stop) {
		flag = stop;
	}

	/* 此处使用一个内部类来维护单例 */
	private static class SpeakFactory {
		private static Speak instance = new Speak();
	}

	/* 获取实例 */
	public static Speak getInstance() {
		return SpeakFactory.instance;
	}
}
