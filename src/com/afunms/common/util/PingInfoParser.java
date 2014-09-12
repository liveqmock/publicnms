package com.afunms.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingInfoParser
{
	
	public static final String PERCENT_START = "Success rate is ";
	
	public static final String PERCENT_END = " percent";
	
	public static final String AVG_MATCHER = "\\d+/\\d+/\\d+";
	
	
	static Pattern pattern = Pattern.compile(AVG_MATCHER);
	
	
	public static int[] parsePingInfo(String pingResult)
	{
		if(pingResult == null || pingResult.length() == 0)
		{
			return null;
		}
		int[] result = null;
		int percentBeginIndex = pingResult.indexOf(PERCENT_START);
		int endPercentIndex = pingResult.indexOf(PERCENT_END);
		if(percentBeginIndex >= 0 && endPercentIndex > (percentBeginIndex + PERCENT_START.length()))
		{
			String percentString = pingResult.substring(percentBeginIndex + PERCENT_START.length(), endPercentIndex);
			System.out.println(percentString);
			System.out.println(percentString.length());
			
			
			int percent = 0;
			int avg = 0;
			
			try
			{
				percent = Integer.parseInt(percentString);
			} catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			
			String avgStr = findAvgString(pingResult);
			if(avgStr != null && avgStr.length() > 0)
			{
				String[] avgs = avgStr.split("/");
				if(avgs != null && avgs.length == 3)
				{
					String avgOnly = avgs[1];
					try
					{
						avg = Integer.parseInt(avgOnly);
					} catch (NumberFormatException e)
					{
						e.printStackTrace();
					}
				}
			}
			
			return new int[]{percent , avg};
			
			
		}
		
		return result;
	}
	
	
	
	public static String findAvgString(String input)
	{
		String value = null;
		
		Matcher matcher = pattern.matcher(input);
		boolean matchFound = matcher.find();
		while (matchFound)
		{	
			value = matcher.group(0);
			if (matcher.end() + 1 <= input.length())
			{
				matchFound = matcher.find(matcher.end());
			} else
			{
				break;
			}
		}
		
		return value;
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args)
	{
//		String pingInfo= "ping 10.10.152.7\n"+
//"\n"+
//"Type escape sequence to abort.\n"+
//"Sending 5, 100-byte ICMP Echos to 10.10.152.7, timeout is 2 seconds:\n"+
//"!!!!!\n"+
//"Success rate is 100 percent (5/5), round-trip min/avg/max = 1/1/4 ms";
//		
		
		
		
		
		
		
	}
	
	
}
