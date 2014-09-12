package com.afunms.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.util.ReportExport;
import com.afunms.config.dao.AclBaseDao;
import com.afunms.config.dao.CfgBaseInfoDao;
import com.afunms.config.dao.PolicyInterfaceDao;
import com.afunms.config.dao.QueueInfoDao;
import com.afunms.config.model.AclBase;
import com.afunms.config.model.AclDetail;
import com.afunms.config.model.PolicyInterface;
import com.afunms.config.model.QueueInfo;
import com.informix.util.stringUtil;

public class CreateAmLinePic {
	/**
	 * 获取类的分组数据
	 * 
	 * @return
	 */
	public List<List<PolicyInterface>> getSortClassData(
			List<PolicyInterface> list) {
		// Hashtable<String, List<PolicyInterface>> alldata=new
		// Hashtable<String,
		// List<PolicyInterface>>();
		List<List<PolicyInterface>> alldata = new ArrayList<List<PolicyInterface>>();
		if (list != null) {
			List<PolicyInterface> interfaceList = new ArrayList<PolicyInterface>();
			String tempInterface = "";
			String tempPolicy = "";
			String tempClass = "";

			for (int i = 0; i < list.size(); i++) {
				PolicyInterface vo = list.get(i);

				if ((!vo.getInterfaceName().equals(tempInterface)
						|| !vo.getPolicyName().equals(tempPolicy) || !vo
						.getClassName().equals(tempClass))
						&& i != 0) {
					alldata.add(interfaceList);
					interfaceList = new ArrayList<PolicyInterface>();
				}
				interfaceList.add(vo);
				tempInterface = vo.getInterfaceName();
				tempPolicy = vo.getPolicyName();
				tempClass = vo.getClassName();
			}
			if (list.size() != 0)
				alldata.add(interfaceList);
		}
		return alldata;
	}

	public HashMap<Integer, List<?>> getSortDetailData(List<AclDetail> list) {
		List<List<AclDetail>> alldata = new ArrayList<List<AclDetail>>();
		HashMap<Integer, List<?>> map = new HashMap<Integer, List<?>>();
		if (list != null) {
			List<AclDetail> detailList = new ArrayList<AclDetail>();
			int baseId = 0;
			String name = "";

			for (int i = 0; i < list.size(); i++) {
				AclDetail vo = list.get(i);

				if ((vo.getBaseId() != baseId || !vo.getName().equals(name))
						&& i != 0) {
					alldata.add(detailList);
					detailList = new ArrayList<AclDetail>();
					if (vo.getBaseId() != baseId) {
						map.put(baseId, alldata);
						alldata = new ArrayList<List<AclDetail>>();
					}
				}
				detailList.add(vo);
				baseId = vo.getBaseId();
				name = vo.getName();
			}
			if (list.size() != 0) {
				alldata.add(detailList);
				map.put(baseId, alldata);
			}
		}
		return map;
	}

	public List getClassList(String allipstr) {

		CfgBaseInfoDao baseInfoDao = null;

		List classList = null;
		baseInfoDao = new CfgBaseInfoDao(allipstr);
		classList = baseInfoDao.findByCondition(" where type='class'");

		return classList;
	}

	public List getQueueList(String allipstr, String starttime, String totime) {
		List queueList = null;
		QueueInfoDao queueInfoDao = null;

		try {
			queueInfoDao = new QueueInfoDao(allipstr);
			queueList = queueInfoDao.findByCondition(" where collecttime>='"
					+ starttime + "' and collecttime<='" + totime + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			queueInfoDao.close();
		}
		return queueList;

	}

	public List getInterfaceList(String allipstr, String starttime,
			String totime) {
		List policyInterfaceList = null;
		PolicyInterfaceDao interfaceDao = null;
		try {
			interfaceDao = new PolicyInterfaceDao(allipstr);
			policyInterfaceList = interfaceDao
					.findByCondition(" where collecttime>='"
							+ starttime
							+ "' and collecttime<='"
							+ totime
							+ "' group by interfaceName,policyName,className,collecttime");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			interfaceDao.close();
		}
		return policyInterfaceList;

	}

	public List getPolicyList(String allipstr) {
		List policyList = null;
		CfgBaseInfoDao baseInfoDao = null;
		try {
			baseInfoDao = new CfgBaseInfoDao(allipstr);
			policyList = baseInfoDao.findByCondition(" where type='policy' ");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			baseInfoDao.close();
		}
		return policyList;

	}

	public List<String> makeAmChartData(List<List<PolicyInterface>> alldata) {

		StringBuffer sb = new StringBuffer();
		StringBuffer data1 = new StringBuffer();
		String data = "";
		List<String> dataList = new ArrayList<String>();
		if (alldata != null && alldata.size() > 0) {
			PolicyInterface vo = null;
			data1.append("<chart><series>");

			List<PolicyInterface> voList = alldata.get(0);

			if (voList != null && voList.size() > 0) {

				for (int j = 0; j < voList.size(); j++) {
					vo = voList.get(j);
					data1.append("<value xid='");
					data1.append(j);
					data1.append("'>");
					data1.append(vo.getCollecttime());
					data1.append("</value>");
				}
				data1.append("</series><graphs>");
			}
			sb.append(data1.toString());
			for (int i = 0; i < alldata.size(); i++) {
				List<PolicyInterface> interfaceList = alldata.get(i);
				data1 = new StringBuffer();
				if (interfaceList != null && interfaceList.size() > 0) {

					for (int v = 0; v < 2; v++) {
						String title = "";
						if (v == 0) {
							title = "offered rate";
						} else {
							title = "drop rate";
						}
						data1.append("<graph title='" + vo.getInterfaceName()
								+ ":" + vo.getClassName() + title
								+ "' bullet='round_outlined' bullet_size='4'>");
						for (int j = 0; j < interfaceList.size(); j++) {
							vo = interfaceList.get(j);
							data1.append("<value xid='");
							data1.append(j);
							data1.append("'>");
							if (title.equals("offered rate")) {
								data1.append(vo.getOfferedRate());
							} else if (title.equals("drop rate")) {
								data1.append(vo.getDropRate());
							}
							data1.append("</value>");
						}
						data1.append("</graph>");

						sb.append(data1);
						data1 = new StringBuffer();
					}

				}

			}
			sb.append("</graphs></chart>");
			dataList.add(sb.toString());
		}
		return dataList;
	}
	/*
	 * Acl曲线图
	 */
	public HashMap<String, String> makeAclAmChart(HashMap<Integer,List<?>> alldata,String ip) {
		
		
		AclBaseDao baseDao=null;
		List<AclBase> baseList=null;
		HashMap<String, String> amMap=new HashMap<String, String>();
		try {
			baseDao=new AclBaseDao();
			baseList=baseDao.findByCondition(" where ipaddress='"+ip+"'");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			baseDao.close();
		}
		
		List<List<AclDetail>> dataList=null;
		if (alldata != null) {
			if (baseList!=null&&baseList.size()>0) {
				for (int i = 0; i < baseList.size(); i++) {
					AclBase base=baseList.get(i);
					if(base!=null){
						if(alldata.containsKey(base.getId())){
						dataList=(List<List<AclDetail>>) alldata.get(base.getId());
						if (dataList!=null&&dataList.size()>0) {
							String amchart=this.makeSigAmChart(dataList);
							amMap.put(base.getName(), amchart);
						}
						}
					}
				}
			}
		}
		return amMap;
	}
	public String makeSigAmChart(List<List<AclDetail>> alldata) {

		StringBuffer sb = new StringBuffer();
		StringBuffer data1 = new StringBuffer();
		String data = "";
		if (alldata != null && alldata.size() > 0) {
			AclDetail vo = null;
			data1.append("<chart><series>");

			List<AclDetail> voList = alldata.get(0);

			if (voList != null && voList.size() > 0) {

				for (int j = 0; j < voList.size(); j++) {
					vo = voList.get(j);
					data1.append("<value xid='");
					data1.append(vo.getCollecttime());
					data1.append("'>");
					data1.append(vo.getCollecttime());
					data1.append("</value>");
				}
				data1.append("</series><graphs>");
			}
			sb.append(data1.toString());
			for (int i = 0; i < alldata.size(); i++) {
				List<AclDetail> interfaceList = alldata.get(i);
				data1 = new StringBuffer();
				if (interfaceList != null && interfaceList.size() > 0) {
					      vo = interfaceList.get(0);
					
						data1.append("<graph title='" +vo.getName()
								+ "' bullet='round_outlined' bullet_size='4'>");
						for (int j = 0; j < interfaceList.size(); j++) {
							vo = interfaceList.get(j);
							data1.append("<value xid='");
							data1.append(vo.getCollecttime());
							data1.append("'>");
							data1.append(vo.getMatches());
							
							data1.append("</value>");
						}
						data1.append("</graph>");

						sb.append(data1);
						data1 = new StringBuffer();
					}

				}

			
			sb.append("</graphs></chart>");
			
		}
		return sb.toString();
	}
	/*
	 * public List<String> makeAmChartData(List<List<PolicyInterface>>
	 * alldata){ StringBuffer sb=null; StringBuffer data1=null; String data="";
	 * List<String> dataList=new ArrayList<String>(); if(alldata!=null){
	 * PolicyInterface vo=null;
	 * 
	 * for (int i = 0; i <alldata.size(); i++) { List<PolicyInterface>
	 * interfaceList=alldata.get(i); data1=new StringBuffer(); sb=new
	 * StringBuffer(); if (interfaceList!=null&&interfaceList.size()>0) {
	 * data1.append("<chart><series>"); for (int j = 0; j <
	 * interfaceList.size(); j++) { vo=interfaceList.get(j); data1.append("<value
	 * xid='"); data1.append(j); data1.append("'>");
	 * data1.append(vo.getCollecttime()); data1.append("</value>"); }
	 * data1.append("</series><graphs>"); for(int v=0;v<2;v++){ String
	 * title=""; if(v==0){ title="offered rate"; }else { title="drop rate"; }
	 * data1.append("<graph
	 * title='"+vo.getInterfaceName()+":"+vo.getClassName()+title+"'
	 * bullet='round_outlined' bullet_size='4'>"); for (int j = 0; j <
	 * interfaceList.size(); j++) { vo=interfaceList.get(j); data1.append("<value
	 * xid='"); data1.append(j); data1.append("'>"); if(title.equals("offered
	 * rate")){ data1.append(vo.getOfferedRate()); }else if (title.equals("drop
	 * rate")) { data1.append(vo.getDropRate()); } data1.append("</value>"); }
	 * data1.append("</graph>");
	 * 
	 * sb.append(data1); data1=new StringBuffer(); }
	 * 
	 * sb.append("</graphs></chart>"); } dataList.add(sb.toString()); }
	 *  } return dataList; }
	 */
	public List<String> delData(List<List<PolicyInterface>> alldata) {

		StringBuffer sb = null;
		StringBuffer data1 = null;
		String data = "";
		List<String> dataList = new ArrayList<String>();
		if (alldata != null) {
			PolicyInterface vo = null;

			for (int i = 0; i < alldata.size(); i++) {
				List<PolicyInterface> interfaceList = alldata.get(i);
				data1 = new StringBuffer();
				sb = new StringBuffer();
				if (interfaceList != null && interfaceList.size() > 0) {
					data1.append("<chart><series>");
					for (int j = 0; j < interfaceList.size(); j++) {
						vo = interfaceList.get(j);
						data1.append("<value xid='");
						data1.append(j);
						data1.append("'>");
						data1.append(vo.getCollecttime());
						data1.append("</value>");
					}
					data1.append("</series><graphs>");

					String title = "";
					if (vo.getMatchedBytes() != -1) {
						addData(data1, interfaceList, vo, "bytes matched");
					}
					if (vo.getMatchedPkts() != -1) {
						addData(data1, interfaceList, vo, "pkts matched");
					}
					if (vo.getDropsTotal() != -1) {
						addData(data1, interfaceList, vo, "total drops");
					}
					if (vo.getDropsBytes() != -1) {
						addData(data1, interfaceList, vo, "bytes drops");
					}
					if (vo.getDepth() != -1) {
						addData(data1, interfaceList, vo, "depth");
					}
					if (vo.getTotalQueued() != -1) {
						addData(data1, interfaceList, vo, "total queued");
					}
					if (vo.getNoBufferDrop() != -1) {
						addData(data1, interfaceList, vo, "no-buffer drops");
					}

					sb.append(data1);

					sb.append("</graphs></chart>");
				}
				// System.out.println(sb.toString());
				dataList.add(sb.toString());
			}

		}
		return dataList;

	}

	public void addData(StringBuffer data, List<PolicyInterface> interfaceList,
			PolicyInterface vo, String title) {

		data.append("<graph title='" + vo.getInterfaceName() + ":"
				+ vo.getClassName() + ":" + title
				+ "' bullet='round_outlined' bullet_size='4'>");
		for (int j = 0; j < interfaceList.size(); j++) {
			vo = interfaceList.get(j);
			data.append("<value xid='");
			data.append(j);
			data.append("'>");
			if (title.equals("bytes matched")) {
				data.append(vo.getMatchedBytes());
			} else if (title.equals("pkts matched")) {
				data.append(vo.getMatchedPkts());
			} else if (title.equals("total drops")) {
				data.append(vo.getDropsTotal());
			} else if (title.equals("bytes drops")) {
				data.append(vo.getDropsBytes());
			} else if (title.equals("depth")) {
				data.append(vo.getDepth());
			} else if (title.equals("total queued")) {
				data.append(vo.getTotalQueued());
			} else if (title.equals("no-buffer drops")) {
				data.append(vo.getNoBufferDrop());
			}
			data.append("</value>");
		}
		data.append("</graph>");
	}

	public String getQueueData(List<QueueInfo> alldata) {

		StringBuffer sb = new StringBuffer();
		;
		StringBuffer data1 = null;
		String data = "";
		List<String> dataList = new ArrayList<String>();
		if (alldata != null && alldata.size() > 0) {
			QueueInfo vo = null;
			data1 = new StringBuffer();

			data1.append("<chart><series>");

			for (int i = 0; i < alldata.size(); i++) {
				vo = alldata.get(i);
				data1.append("<value xid='");
				data1.append(i);
				data1.append("'>");
				data1.append(vo.getCollecttime());
				data1.append("</value>");
			}
			data1.append("</series><graphs>");

			addQueueData(data1, alldata, "input size");
			addQueueData(data1, alldata, "input max");
			addQueueData(data1, alldata, "input drops");
			addQueueData(data1, alldata, "input flushes");
			addQueueData(data1, alldata, "output size");
			addQueueData(data1, alldata, "output max total");
			addQueueData(data1, alldata, "output threshold");
			addQueueData(data1, alldata, "output drops");
			addQueueData(data1, alldata, "Available Bandwidth(kb/s)");

			sb.append(data1);

			sb.append("</graphs></chart>");
		}

		return sb.toString();

	}

	public void addQueueData(StringBuffer data, List<QueueInfo> interfaceList,
			String title) {

		data.append("<graph title='" + title
				+ "' bullet='round_outlined' bullet_size='4'>");
		for (int j = 0; j < interfaceList.size(); j++) {
			QueueInfo vo = interfaceList.get(j);
			data.append("<value xid='");
			data.append(j);
			data.append("'>");
			if (title.equals("input size")) {
				data.append(vo.getInputSize());
			} else if (title.equals("input max")) {
				data.append(vo.getInputMax());
			} else if (title.equals("input drops")) {
				data.append(vo.getInputDrops());
			} else if (title.equals("input flushes")) {
				data.append(vo.getInputFlushes());
			}
			if (title.equals("output size")) {
				data.append(vo.getOutputSize());
			} else if (title.equals("output max total")) {
				data.append(vo.getOutputMax());
			} else if (title.equals("output threshold")) {
				data.append(vo.getOutputThreshold());
			} else if (title.equals("output drops")) {
				data.append(vo.getOutputDrops());
			}
			if (title.equals("Available Bandwidth(kb/s)")) {
				data.append(vo.getAvailBandwidth());
			}
			data.append("</value>");
		}
		data.append("</graph>");
	}
}
