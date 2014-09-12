package com.afunms.schedule.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.afunms.schedule.model.Worker;

/**
 * 四个人一个地点 白班 夜班均衡 周末班次数均衡 不能连班：白+夜
 * 
 * @author Administrator
 * 
 */
public class Scheduling3 {

	private static List<String> workers = new ArrayList<String>();
	private static String[] periods = null;
	private static String position = null;
	private static Map<String, Integer> map = new HashMap<String, Integer>();
	private static Map<String, List<Worker>> workerMap = new HashMap<String, List<Worker>>();
	private static Map<String, Integer> holidayMap = new HashMap<String, Integer>();

	public Scheduling3(String periodids, String positionid) {
		periods = new String[periodids.split(",").length];
		for (int i = 0; i < periodids.split(",").length; i++) {
			periods[i] = periodids.split(",")[i];
		}

		position = positionid;
	}

	public static List<String> init(Calendar c) {
		List<String> list = new ArrayList<String>();
		String period = null;
		String workerStr = null;
		String str = "1234234124134243123124324123214314321324313421421341341243123124324123123423412413423421421341341221431432132431";
		for (int i = 0; i < str.length(); i++) {
			if (i % 2 == 0) {
				period = periods[0];
			} else {
				period = periods[1];
			}
			workerStr = str.charAt(i) + "_" + period + "_" + position;
			list.add(workerStr);
		}

		int week = 0;
		int day = c.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case 1:
			week = 7;
			list.add(list.get(2));
			list.add(list.get(1));
			list.add(list.get(0));
			list.add(list.get(3));
			list.add(list.get(4));
			list.add(list.get(5));
			list.add(list.get(6));
			list.add(list.get(7));
			list.add(list.get(8));
			list.add(list.get(9));
			list.add(list.get(10));
			list.add(list.get(11));
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			break;
		case 2:
			week = 1;
			break;
		case 3:
			week = 2;
			list.add(list.get(1));// 第一个人白班变夜班
			list.add(list.get(0));// 第二个人夜班变白班
			list.remove(0);
			list.remove(0);
			break;
		case 4:
			week = 3;
			list.add(list.get(2));
			list.add(list.get(1));
			list.add(list.get(0));
			list.add(list.get(3));
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			break;
		case 5:
			week = 4;
			list.add(list.get(2));
			list.add(list.get(1));
			list.add(list.get(0));
			list.add(list.get(3));
			list.add(list.get(4));
			list.add(list.get(5));
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			break;
		case 6:
			week = 5;
			list.add(list.get(2));
			list.add(list.get(1));
			list.add(list.get(0));
			list.add(list.get(3));
			list.add(list.get(4));
			list.add(list.get(5));
			list.add(list.get(6));
			list.add(list.get(7));
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			break;
		case 7:
			week = 6;
			Collections.reverse(list);
			list = convert(list, true, true);
			break;
		}

		return list;
	}

	private static List<String> convert(List<String> list, boolean periodFlag, boolean positionFlag) {
		List<String> rList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String str = list.get(i);
			String[] keys = str.split("_");
			if (periodFlag) {
				if (keys[1].equals(periods[0])) {
					keys[1] = periods[1];
				} else {
					keys[1] = periods[0];
				}
			}
			str = String.format("%s_%s_%s", keys[0], keys[1], position);
			rList.add(str);
		}
		return rList;
	}

	public List<String> doSchedule(String userids, Calendar c) {
		List<String> list = new ArrayList<String>();
		String[] users = userids.split(",");
		List<String> userList = new ArrayList<String>();
		if (users.length == 4) {
			userList = Arrays.asList(users);
		}
		Collections.shuffle(userList);

		list = init(c);
		list = replace(list, userList, new String[] { "1", "2", "3", "4" });

		/*for (int i = 0; i < list.size(); i++) {
			System.out.println(i + "=" + list.get(i));
		}*/

		return list;
	}

	private List<String> replace(List<String> list, List<String> subList,
			Object[] strings) {
		List<String> rList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String str = list.get(i);
			String keys[] = str.split("_");
			if (keys[0].equals(strings[0])) {
				keys[0] = subList.get(0);
			} else if (keys[0].equals(strings[1])) {
				keys[0] = subList.get(1);
			} else if (keys[0].equals(strings[2])) {
				keys[0] = subList.get(2);
			} else if (keys[0].equals(strings[3])) {
				keys[0] = subList.get(3);
			}
			str = String.format("%s_%s_%s", keys[0], keys[1], keys[2]);
			rList.add(str);
		}
		return rList;
	}

	/*
	public static void main(String[] args) throws Exception {
		Scheduling3 schedule = new Scheduling3("白班,夜班", "值班地点");
		Calendar c = Calendar.getInstance();
		schedule.doSchedule("a,b,c,d", c);
		// schedule.init(c);

		// init(c);
		if (true) {
			return;
		}

		List<Worker> list = new ArrayList<Worker>();

		label1: while (true) {
			workers.clear();
			workers.add("worker1");
			workers.add("worker2");
			workers.add("worker3");
			workers.add("worker4");

			workerMap.clear();
			workerMap.put("worker1", new ArrayList<Worker>());
			workerMap.put("worker2", new ArrayList<Worker>());
			workerMap.put("worker3", new ArrayList<Worker>());
			workerMap.put("worker4", new ArrayList<Worker>());

			map.clear();
			holidayMap.clear();
			list.clear();

			Calendar rightNow = Calendar.getInstance();
			firstDay(list, rightNow);
			otherDay(list, 28, rightNow);
			rightNow.setTime(new Date());
			int count = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
			for (int i = 0; i < list.size(); i++) {
				Worker worker = list.get(i);
				if (i != 0 && i % 4 == 0) {
					rightNow.add(Calendar.DAY_OF_WEEK, 1);
				}
				String date = sdf.format(rightNow.getTime());
				String week = parseWeek(rightNow);// rightNow.get(Calendar.DAY_OF_WEEK)
													// + "";
				// System.out.println((i+1) + " 日期:" + date + " " + week + ":" +
				// worker.getName()+":"+worker.getPlace().name()+":"+worker.getWorkTime());
				if (worker.getName().equals("worker0")) {
					count++;
				}
			}

			boolean flag = false;
			if (count <= 2) {
				Set<Map.Entry<String, Integer>> set = map.entrySet();
				for (int i = 1; i <= list.size(); i++) {
					Worker worker = list.get(i - 1);
					if (worker.getName().equals("worker0")) {
						String place = worker.getPlace();
						String shift = worker.getWorkTime();
						for (Iterator<Map.Entry<String, Integer>> iterator = set
								.iterator(); iterator.hasNext();) {
							Map.Entry<String, Integer> entry = iterator.next();
							String key = entry.getKey();
							int value = entry.getValue();
							if (value == 7) {
								iterator.remove();
								continue;
							}
							String workername = key.split("_")[0];
							String workerplace = key.split("_")[1];
							String workershift = key.split("_")[2];
							if (place.equalsIgnoreCase(workerplace)
									&& shift.equalsIgnoreCase(workershift)) {
								worker.setName(workername);
								map.put(key, value + 1);
								System.out.println(worker.getName());
								break;
							}
						}
					}
					// System.out.println((i+1) + " 日期:" + date + " " + week +
					// ":" +
					// worker.getName()+":"+worker.getPlace().name()+":"+worker.getWorkTime());
					System.out.println(worker);
					if (i % 4 == 0) {
						List<String> tempList = new ArrayList<String>();
						for (int j = i - 4; j < i; j++) {
							if (!tempList.contains(list.get(j).getName())) {
								tempList.add(list.get(j).getName());
							} else {
								flag = true;
								continue label1;
							}
						}
					}
				}
				if (!flag) {
					break label1;
				}
			}
		}
		System.out.println(map);
		System.out.println(workerMap);
		// System.out.println(holidayMap);

		// System.out.println("fdsfsf");
	}

	private static void firstDay(List<Worker> list, Calendar c) {
		List<Integer> indexs = new ArrayList<Integer>();
		Random random = new Random();
		// boolean isHoliday = isHoliday(c);
		int index0 = random.nextInt(workers.size());
		Worker worker0 = new Worker(workers.get(index0));
		// worker0.setPlace(Place.PLACE1);
		// worker0.setWorkTime(WorkTime.DAYSHIFT);
		worker0.setPlace(position);
		worker0.setWorkTime(periods[0]);
		worker0.setIndex(1);
		indexs.add(index0);
		list.add(worker0);
		map.put(worker0.getName() + "_" + worker0.getPlace() + "_"
				+ worker0.getWorkTime(), 1);
		workerMap.get(worker0.getName()).add(worker0);
		// if(isHoliday){
		// holidayMap.put(worker0.getName(), 1);
		// }

		while (true) {
			int index = random.nextInt(workers.size());
			if (!indexs.contains(index)) {
				Worker worker = new Worker(workers.get(index));
				worker.setIndex(1);
				worker.setPlace(position);
				worker.setWorkTime(periods[1]);
				indexs.add(index);
				list.add(worker);
				map.put(worker.getName() + "_" + worker.getPlace() + "_"
						+ worker.getWorkTime(), 1);
				workerMap.get(worker.getName()).add(worker);
				// if(isHoliday){
				// holidayMap.put(worker.getName(), 1);
				// }
				break;
			}
		}

		while (true) {
			int index = random.nextInt(workers.size());
			if (!indexs.contains(index)) {
				Worker worker = new Worker(workers.get(index));
				worker.setIndex(1);
				worker.setPlace(position);
				worker.setWorkTime(periods[0]);
				indexs.add(index);
				list.add(worker);
				map.put(worker.getName() + "_" + worker.getPlace() + "_"
						+ worker.getWorkTime(), 1);
				workerMap.get(worker.getName()).add(worker);
				// if(isHoliday){
				// holidayMap.put(worker.getName(), 1);
				// }
				break;
			}
		}

		while (true) {
			int index = random.nextInt(workers.size());
			if (!indexs.contains(index)) {
				Worker worker = new Worker(workers.get(index));
				worker.setIndex(1);
				worker.setPlace(position);
				worker.setWorkTime(periods[1]);
				indexs.add(index);
				list.add(worker);
				map.put(worker.getName() + "_" + worker.getPlace() + "_"
						+ worker.getWorkTime(), 1);
				workerMap.get(worker.getName()).add(worker);
				// if(isHoliday){
				// holidayMap.put(worker.getName(), 1);
				// }
				break;
			}
		}

		// System.out.println("==================");
	}

	private static void otherDay(List<Worker> list, int loops, Calendar c) {
		Set<String> indexs = new HashSet<String>();
		Random random = new Random();
		Integer count = null;
		Integer holidayCount = null;
		Worker temp = null;

		for (int i = 2; i <= loops; i++) {
			c.add(Calendar.DAY_OF_WEEK, 1);
			// sortMap(Place.PLACE1.name(),WorkTime.DAYSHIFT.name());
			sortMap(position, periods[0]);
			for (int index = 0; index < workers.size(); index++) {
				// Worker worker = new Worker(workers.get(index));
				int ranindex = random.nextInt(workers.size() / 2);
				Worker worker = new Worker(workers.get(ranindex));
				if (!indexs.contains(worker.getName())) {
					boolean isContinuous = isContinuous(worker, i);
					boolean isNightShift = isNightShift(worker, i);
					// boolean isWorkContinuous = isWorkContinuous(worker,i);
					worker.setPlace(position);
					worker.setWorkTime(periods[0]);

					count = getWorkCount(worker);
					holidayCount = getHolidayWorkCount(worker);

					if (!isContinuous && !isNightShift && count < 7) {
						worker.setIndex(i);
						temp = worker;
						list.add(worker);
						map.put(worker.getName() + "_" + worker.getPlace()
								+ "_" + worker.getWorkTime(), ++count);
						workerMap.get(worker.getName()).add(worker);
						indexs.add(worker.getName());
						break;
					}
				}
			}
			if (null == temp) {
				list.add(new Worker(i, "worker0", position, periods[0]));
			}

			temp = null;
			sortMap(position, periods[1]);
			for (int index = 0; index < workers.size(); index++) {
				int ranindex = random.nextInt(workers.size() / 2);
				Worker worker = new Worker(workers.get(ranindex));
				if (!indexs.contains(worker.getName())) {
					boolean isNightShift = isNightShift(worker, i);
					// boolean isWorkContinuous = isWorkContinuous(worker,i);
					worker.setPlace(position);
					worker.setWorkTime(periods[1]);

					count = getWorkCount(worker);
					holidayCount = getHolidayWorkCount(worker);
					if (!isNightShift && count < 7) {
						worker.setIndex(i);
						temp = worker;
						list.add(worker);
						map.put(worker.getName() + "_" + worker.getPlace()
								+ "_" + worker.getWorkTime(), ++count);
						workerMap.get(worker.getName()).add(worker);
						indexs.add(worker.getName());
						// if(isHoliday){
						// holidayMap.put(worker.getName(), ++holidayCount);
						// }
						break;
					}
				}
			}
			if (null == temp) {
				list.add(new Worker(i, "worker0", position, periods[1]));
			}

			temp = null;
			sortMap(position, periods[0]);
			for (int index = 0; index < workers.size(); index++) {
				// int index = random.nextInt(workers.size());
				// Worker worker = new Worker(workers.get(index));
				int ranindex = random.nextInt(workers.size() / 2);
				Worker worker = new Worker(workers.get(ranindex));
				if (!indexs.contains(worker.getName())) {
					boolean isContinuous = isContinuous(worker, i);
					boolean isNightShift = isNightShift(worker, i);
					// boolean isWorkContinuous = isWorkContinuous(worker,i);
					worker.setPlace(position);
					worker.setWorkTime(periods[0]);

					count = getWorkCount(worker);
					holidayCount = getHolidayWorkCount(worker);
					// if (!isContinuous && !isNightShift && holidayCount < 4) {
					// if (!isContinuous && !isNightShift && !isWorkContinuous
					// && count < 7 && holidayCount < 4) {
					// if (!isContinuous && !isNightShift && !isWorkContinuous
					// && holidayCount < 4) {
					if (!isContinuous && !isNightShift && count < 7) {
						worker.setIndex(i);
						temp = worker;
						list.add(worker);
						map.put(worker.getName() + "_" + worker.getPlace()
								+ "_" + worker.getWorkTime(), ++count);
						workerMap.get(worker.getName()).add(worker);
						indexs.add(worker.getName());
						// if(isHoliday){
						// holidayMap.put(worker.getName(), ++holidayCount);
						// }
						break;
					}
				}
			}
			if (null == temp) {
				list.add(new Worker(i, "worker0", position, periods[0]));
			}

			temp = null;
			sortMap(position, periods[1]);
			for (int index = 0; index < workers.size(); index++) {
				// int index = random.nextInt(workers.size());
				// Worker worker = new Worker(workers.get(index));
				int ranindex = random.nextInt(workers.size() / 2);
				Worker worker = new Worker(workers.get(ranindex));
				if (!indexs.contains(worker.getName())) {
					
//					  List<Worker> tempList = workerMap.get(worker.getName());
//					  if (null != tempList && tempList.size() > 27) {
//					  workers.remove(worker.getName()); continue; }
					 
					boolean isNightShift = isNightShift(worker, i);
					// boolean isWorkContinuous = isWorkContinuous(worker,i);
					worker.setPlace(position);
					worker.setWorkTime(periods[1]);

					count = getWorkCount(worker);
					holidayCount = getHolidayWorkCount(worker);
					// if (!isNightShift && holidayCount < 4) {
					// if (!isNightShift && !isWorkContinuous && count < 7 &&
					// holidayCount < 4) {
					// if (!isNightShift && !isWorkContinuous && holidayCount <
					// 4) {
					if (!isNightShift && count < 7) {
						worker.setIndex(i);
						temp = worker;
						list.add(worker);
						map.put(worker.getName() + "_" + worker.getPlace()
								+ "_" + worker.getWorkTime(), ++count);
						workerMap.get(worker.getName()).add(worker);
						indexs.add(worker.getName());
						// if(isHoliday){
						// holidayMap.put(worker.getName(), ++holidayCount);
						// }
						break;
					}
				}
			}
			if (null == temp) {
				list.add(new Worker(i, "worker0", position, periods[1]));
			}
			temp = null;
			indexs.clear();
		}
	}

	private static Integer getHolidayWorkCount(Worker worker) {
		Integer count = null;
		count = holidayMap.get(worker.getName());
		count = (count == null ? 0 : count.intValue());
		return count;
	}

	private static boolean isContinuous(Worker worker, int index) {
		List<Worker> list = workerMap.get(worker.getName());
		if (list.size() > 0) {
			Worker lastWorker = list.get(list.size() - 1);
			// 首先上次是白班
			if (periods[0].equals(lastWorker.getWorkTime())) {
				boolean isContinuous = lastWorker.isContinuous();
				if (isContinuous && lastWorker.getIndex() == index - 1) {
					return true;
				}
				if (isContinuous && lastWorker.getIndex() < index - 1) {
					lastWorker.setContinuous(false);
					return false;
				}
				if (!isContinuous && lastWorker.getIndex() == index - 1) {
					lastWorker.setContinuous(true);
					return false;
				}
			}
		}

		return false;
	}

	private static boolean isNightShift(Worker worker, int index) {
		List<Worker> list = workerMap.get(worker.getName());
		if (list.size() > 0) {
			Worker workman = list.get(list.size() - 1);
			String workTime = workman.getWorkTime();
			if (workTime.equals(periods[1])) {
				if (workman.getIndex() == index - 1) {
					return true;
				}
			}
		}

		return false;
	}

	private static int getWorkCount(Worker worker) {
		Integer count = null;
		if (null == worker.getPlace()) {
			count = 0;
		} else {
			count = map.get(worker.getName() + "_" + worker.getPlace() + "_"
					+ worker.getWorkTime());
			count = (count == null ? 0 : count.intValue());

		}
		return count;
	}

	private static String parseWeek(Calendar c) {
		int week = c.get(Calendar.DAY_OF_WEEK);
		String str = "";
		switch (week) {
		case 1:
			str = "星期日";
			break;
		case 2:
			str = "星期一";
			break;
		case 3:
			str = "星期二";
			break;
		case 4:
			str = "星期三";
			break;
		case 5:
			str = "星期四";
			break;
		case 6:
			str = "星期五";
			break;
		case 7:
			str = "星期六";
			break;
		}
		return str;
	}

	private static boolean isHoliday(Calendar c) {
		boolean isHoliday = false;
		int day = c.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case 1:
		case 7:
			isHoliday = true;
			break;
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			isHoliday = false;
			break;
		}
		if (isHoliday) {
			System.out.println("holiday");
		}
		return isHoliday;
	}

	private static int getWeekDay(Calendar c) {
		int week = 0;
		int day = c.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case 1:
			week = 7;
			break;
		case 2:
			week = 1;
			break;
		case 3:
			week = 2;
			break;
		case 4:
			week = 3;
			break;
		case 5:
			week = 4;
			break;
		case 6:
			week = 5;
			break;
		case 7:
			week = 6;
			break;
		}

		return week;
	}

	private static List<String> sortMap() {
		List<String> sortList = new ArrayList<String>();
		if (null != map) {
			Set<Map.Entry<String, Integer>> set = map.entrySet();
			for (Map.Entry<String, Integer> entry : set) {
				String key = entry.getKey();
				int value = entry.getValue();
				key = key.split("_")[0];
				if (value > 0) {
					for (int i = 0; i < value; i++) {
						sortList.add(key);
					}
				}
			}

			Map<String, Integer> m = new HashMap<String, Integer>();
			for (int i = 0; i < workers.size(); i++) {
				int count = Collections.frequency(sortList, workers.get(i));
				m.put(workers.get(i), count);
			}

			ArrayList<Map.Entry<String, Integer>> l = new ArrayList<Map.Entry<String, Integer>>(
					m.entrySet());
			Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1,
						Map.Entry<String, Integer> o2) {
					return (o1.getValue() - o2.getValue());
				}
			});

			sortList.clear();
			for (int i = 0; i < l.size(); i++) {
				Map.Entry<String, Integer> entry = l.get(i);
				sortList.add(entry.getKey());
			}
		}
		return sortList;
	}

	private static void sortMap(String... str) {
		List<String> sortList = new ArrayList<String>();
		if (null != map) {
			Set<Map.Entry<String, Integer>> set = map.entrySet();
			for (Map.Entry<String, Integer> entry : set) {
				String key = entry.getKey();
				int value = entry.getValue();
				String workername = key.split("_")[0];
				String place = key.split("_")[1];
				String shift = key.split("_")[2];
				if (str.length == 1) {// 按地点排序
					if (place.equalsIgnoreCase(str[0])) {
						if (value > 0) {
							for (int i = 0; i < value; i++) {
								sortList.add(workername);
							}
						}
					}
				} else if (str.length == 2) {// 按地点、班次排序
					if (place.equalsIgnoreCase(str[0])
							&& shift.equalsIgnoreCase(str[1])) {
						if (value > 0) {
							for (int i = 0; i < value; i++) {
								sortList.add(workername);
							}
						}
					}
				}
			}

			Map<String, Integer> m = new HashMap<String, Integer>();
			for (int i = 0; i < workers.size(); i++) {
				int count = Collections.frequency(sortList, workers.get(i));
				m.put(workers.get(i), count);
			}

			ArrayList<Map.Entry<String, Integer>> l = new ArrayList<Map.Entry<String, Integer>>(
					m.entrySet());
			Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1,
						Map.Entry<String, Integer> o2) {
					return (o1.getValue() - o2.getValue());
				}
			});

			List<String> list2 = new ArrayList<String>();
			for (int i = 0; i < l.size(); i++) {
				Map.Entry<String, Integer> entry = l.get(i);
				list2.add(entry.getKey());
			}

			List<String> sList = sortMap();
			List<String> list3 = new ArrayList<String>();
			for (int i = 4; i < sList.size(); i++) {
				list3.clear();
				list3.addAll(sList.subList(0, i));
				list3.retainAll(list2.subList(0, i));
				if (list3.size() == 4) {
					break;
				}
			}

			workers.clear();
			workers.addAll(list3);
			for (int i = 0; i < sList.size(); i++) {
				if (!workers.contains(sList.get(i))) {
					workers.add(sList.get(i));
				}
			}
		}
	}
*/
	public static void main(String[] args) throws Exception {
		Scheduling3 schedule = new Scheduling3("白班,夜班", "值班地点");
		Calendar c = Calendar.getInstance();
		schedule.doSchedule("a,b,c,d", c);
	}
}
