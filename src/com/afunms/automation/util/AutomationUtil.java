package com.afunms.automation.util;

public class AutomationUtil {
	public static String splitDate(String item, String[] itemCh, String type) {
		String[] idValue = null;
		String value = "";
		idValue = new String[item.split("/").length];
		idValue = item.split("/");

		for (int i = 0; i < idValue.length; i++) {
			if (!idValue[i].equals("")) {
				if (type.equals("week")) {
					value += itemCh[Integer.parseInt(idValue[i])];
				} else if (type.equals("day")) {
					value += (idValue[i] + "ÈÕ ");
				} else if (type.equals("hour")) {
					value += (idValue[i] + "Ê± ");
				} else {
					value += itemCh[Integer.parseInt(idValue[i]) - 1];
				}
			}
		}

		return value;
	}
}
