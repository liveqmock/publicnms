package com.afunms.polling.telnet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtilForAS400
{

	public static String replaceAll(String mainString, String oldString,
			String newString)
	{
		if (mainString == null)
			return null;
		if (newString == null)
			return mainString;
		int i = mainString.lastIndexOf(oldString);
		if (i < 0)
			return mainString;
		StringBuffer mainSb = new StringBuffer(mainString);
		while (i >= 0)
		{
			mainSb.replace(i, i + oldString.length(), newString);
			i = mainString.lastIndexOf(oldString, i - 1);
		}
		return mainSb.toString();
	}

	
	public static List<String> find(String input, String regx)
	{
		List<String> values = null;
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(input);
		boolean matchFound = matcher.find();
		while (matchFound)
		{
			if (values == null)
			{
				values = new ArrayList<String>();
			}
			values.add(matcher.group(0));

			if (matcher.end() + 1 <= input.length())
			{
				matchFound = matcher.find(matcher.end());
			} else
			{
				break;
			}
		}

		return values;
	}

	
	
	public static String filterCode(String a)
	{
		if(a != null)
		{
			a = a.replaceAll("\\[\\d+m", "");
			a = a.replaceAll("\\[\\d+[;?]\\d+[Hh]", "");
			a = a.replaceAll("\\[\\?{0,1}\\d+[HhLlJ]{0,1}", "");
			a = a.replaceAll("", "");
			a = a.replaceAll("\t", "\n");
//			a = a.replaceAll("\\[\\d+J", "");
		}
		
		return a;
	}
	
	
	
	
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String a ="[4m[0m [4m                                                                         [0m  F3=Exit   F5=Refresh   F12=Cancel   F24=More keys [22;7H[22;7H[22;7H[4mwrksyssts[12;8H[?3l[?7h[1;1H[2J[0m [1;28H[1m[0m [1mWork with System Status[0m [1;71H ISERIESD  [0m  [0m       [0m  [0m [2;61H 07/15/10  21:53:34  % CPU used . . . . . . . :         .3    System ASP . . . . . . . :    493.9 G  % DB capability  . . . . :         .0    % system ASP used  . . . :    74.4000  Elapsed time . . . . . . :   00:00:01    Total aux stg  . . . . . :    493.9 G  Jobs in system . . . . . :       2223    Current unprotect used . :    16996 M  % perm addresses . . . . :       .036    Maximum unprotect  . . . :    17582 M  % temp addresses . . . . :       .193 [10;1H[1m[0m [1mSys[0m     [1m[0m [1mPool[0m  [1m[0m [1mReserved[0m   [1m[0m [1mMax[0m [1m[0m [1m----DB-----[0m [1m[0m [1m--Non-DB---[0m [1m[0m [1mAct-[0m  [1m[0m [1mWait-[0m [1m[0m [1mAct-[0m  [1m[0m [1mPool[0m   [1m[0m [1mSize M[0m  [1m[0m [1mSize M[0m    [1m[0m [1mAct[0m [1m[0m [1mFault[0m [1mPages[0m [1m[0m [1mFault[0m [1mPages[0m [1m[0m [1mWait[0m  [1m[0m [1mInel[0m  [1m[0m [1mInel[0m     1  [4m[0m [4m 2000.00[0m    543.18  +++++     .0    .0     .0    .0     .0     .0     .0    2   16560.35      4.71 [4m[0m [4m32767[0m     .0    .0     .0    .0  23051     .0     .0    3  [4m[0m [4m 5450.64[0m       .51 [4m[0m [4m32767[0m     .0    .0    1.8   1.8  165.4     .0     .0    4  [4m[0m [4m  500.00[0m       .00 [4m[0m [4m32767[0m     .0    .0     .0    .0     .0     .0     .0 [21;63H[0m[0m [0m [0m [0m [0m[1m[0m [1m      Bottom[0m  ===>[1mInel[0m  [1m[0m [1mInel[0m     1  [4m[0m [4m 2000.00[0m    543.18  +++++     .0    .0     .0    .0     .0     .0     .0    2   16560.35      4.71 [4m[0m [4m32767[0m     .0    .0     .0    .0  23051     .0     .0    3  [4m[0m [4m 5450.64[0m       .51 [4m[0m [4m32767[0m     .0    .0    1.8   1.8  165.4     .0     .0    4  [4m[0m [4m  500.00[0m       .00 [4m[0m [4m32767[0m     .0    .0     .0    .0     .0     .0     .0 [21;63H[0m[0m [0m [0m [0m [0m[1m[0m [1m      Bottom[0m  ===>";
		
		System.out.println(StringUtilForAS400.filterCode(a));
	}

}
