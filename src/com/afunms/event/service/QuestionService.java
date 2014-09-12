package com.afunms.event.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuestionService {
	private Map<String, List<String>> datas = new HashMap<String, List<String>>();
	
	public QuestionService(){
		String[] c1 = {"0A01哪些机器的登录失败次数最多？","0A02网络中发生了多少登录失败的事件？","0A03哪些用户账号被滥用事件比较多？"};
		String[] c2 = {"0B01哪些用户成功修改了他们的密码？","0B02哪些用户修改密码失败？","0B03哪些账号被删除/禁用？","0B04哪些用户修改或者清除了安全审计日志？"};
		String[] c3 = {"0C01哪些机器生成了大量的事件？","0C02哪些程序生成了警报事件？","0C03哪些机器生成了大量的危急事件？"};//,"0C04哪些进程生成了多少个危急事件？"
		String[] c4 = {"0D01最近一周的告警事件有哪些？", "0D02最近一小时出现了多少错误事件？"};
		String[] c5 = {"0E01哪些主机生成的告警最多？"};
		String[] c6 = {"0F01服务器或工作站上多少用户修改了系统时间？","0F02新添的主机中有多少个危急事件？","0F03新添的主机中有多少次对象和文件夹被访问？"};

		datas.put("全部问题", null);
		datas.put("登录/退出", Arrays.asList(c1));
		datas.put("用户账户", Arrays.asList(c2));
		datas.put("事件", Arrays.asList(c3));
		datas.put("基于时间", Arrays.asList(c4));
		datas.put("告警", Arrays.asList(c5));
//		datas.put("系统", Arrays.asList(c6));
	}
	
	public List<String> loadQuestionDetail(String question) {
		return datas.get(question)==null?new ArrayList() : datas.get(question);
	}
	
	public List<String> loadQuestionDetailAll() {
		List list = new ArrayList();
		if (datas.size() > 0) {
			Set set = datas.keySet();
			Iterator iter = set.iterator();
			String key = "";
			while(iter.hasNext()){
				key = (String)iter.next();
				if (!"全部问题".equals(key)) {
					list.addAll((List)datas.get(key));
				}
			}
		}
		return list;
	}

	public List<String> loadQuestionTitle() {
		return new ArrayList<String>(datas.keySet());
	}
}
