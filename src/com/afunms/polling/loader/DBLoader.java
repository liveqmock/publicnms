/**
 * <p>Description:loading db nodes</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-8
 */

package com.afunms.polling.loader;

import com.afunms.polling.base.Node;
import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.DBNode;

public class DBLoader extends NodeLoader {
	public void loading() {
		List list=null;
		DBDao dao = new DBDao();
		try {
			list = dao.getDbByMonFlag(-1);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally{
			dao.close();
		}
		clearRubbish(list);
		if(list == null)list = new ArrayList();
		ShareData.setDbconfiglist(list);
		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo oracleType = null;
		try {
			oracleType = (DBTypeVo) typedao.findByDbtype("oracle");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
		// SysLogger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		for (int i = 0; i < list.size(); i++) {
			DBVo vo = (DBVo) list.get(i);
//			if (vo.getDbtype() == oracleType.getId()) {
//				OraclePartsDao oracleDao = new OraclePartsDao();
//				List<OracleEntity> oracles = oracleDao.findOracleParts(vo
//						.getId());
//				for (OracleEntity oracle : oracles) {
//					DBVo nvo = new DBVo();
//					nvo.setDbName(vo.getDbName());
//					nvo.setAlias(vo.getAlias());
//					nvo.setBid(oracle.getBid());
//					nvo.setCategory(vo.getCategory());
//					nvo.setCollecttype(vo.getCollecttype());
//					nvo.setDbtype(vo.getDbtype());
//					nvo.setDbuse(vo.getDbuse());
//					nvo.setId(oracle.getId());
//					nvo.setIpAddress(vo.getIpAddress());
//					nvo.setManaged(vo.getManaged());
//					nvo.setPassword(oracle.getPassword());
//					nvo.setPort(vo.getPort());
//					nvo.setSendemail(oracle.getGzerid());
//					nvo.setSendmobiles(vo.getSendmobiles());
//					nvo.setSendphone(vo.getSendphone());
//					nvo.setStatus(vo.getStatus());
//					nvo.setUser(oracle.getUser());
//					// vo.setPassword(oracle.getPassword());
//					// vo.setId(oracle.getId());
//					// vo.setSendemail(oracle.getGzerid());
//					// vo.setUser(oracle.getUser());
//					loadOne(nvo);
//				}
//			} else
				loadOne(vo);
		}

	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getDbList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof DBNode) {
				DBNode node = (DBNode) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						DBVo hostNode = (DBVo) baseVoList.get(j);
						if (node.getId() == hostNode.getId()) {
							flag = true;
						}
					}
					if (!flag) {
						nodeList.remove(node);
					}
				}
			}
		}
	}

	public void loadOne(BaseVo baseVo) {
		DBVo vo = (DBVo) baseVo;
		DBNode dbNode = new DBNode();
		dbNode.setManaged(true);
		dbNode.setId(vo.getId());
		dbNode.setAlias(vo.getAlias());
		dbNode.setIpAddress(vo.getIpAddress());
		dbNode.setCategory(vo.getCategory());
		dbNode.setBid(vo.getBid());
		dbNode.setDbtype(vo.getDbtype());
		dbNode.setUser(vo.getUser());
		dbNode.setPassword(vo.getPassword());
		dbNode.setPort(vo.getPort());
		dbNode.setDbName(vo.getDbName());
		dbNode.setStatus(0);
		dbNode.setCollecttype(vo.getCollecttype());
		dbNode.setType("数据库");
		// SysLogger.info("############"+dbNode.getCollecttype());

		Node node=PollingEngine.getInstance().getDbByID(dbNode.getId());
		if(node!=null){
			PollingEngine.getInstance().getDbList().remove(node);
		}
		PollingEngine.getInstance().addDb(dbNode);
	}
	
	/**
	 * 刷新内存中的数据库列表
	 */
	public void refreshDBConfiglist(){
		//初始化所有数据库
		DBDao dao = new DBDao();
		List list = null;
		try {
			list = dao.getDbByMonFlag(-1);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}
		clearRubbish(list);
		if(list == null)list = new ArrayList();
		ShareData.setDbconfiglist(list);
	}
}