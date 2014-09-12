package com.gatherResulttosql;

import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CMTSaddresscollectdata;
import com.gatherdb.GathersqlListManager;

/**
 * 把cisco的地址结果集存入表中
 * 
 * @author wangzhenlong
 * 
 */
public class NetcmtsaddressResultTosql {

	public void CreateResultTosql(Hashtable ipdata, Host host) {

		String allipstr = SysUtil.doip(host.getIpAddress());// 把ip地址分解
		
		Vector addressVector = (Vector) ipdata.get("cmtsaddress");

		String table="cmts_address"+allipstr;
		
		if (addressVector != null && addressVector.size() > 0) {
			CMTSaddresscollectdata cmtsaddresscollectdata = null;
			for (int i = 0; i < addressVector.size(); i++) {
				cmtsaddresscollectdata = (CMTSaddresscollectdata) addressVector
						.elementAt(i);
				String time = cmtsaddresscollectdata.getCollecttime();
				StringBuffer sBuffer = new StringBuffer(150);
				sBuffer.append("insert into ");
				sBuffer.append(table);
				sBuffer.append("(ipaddress,mac,status,collecttime)");
				sBuffer.append("values('");
				sBuffer.append(cmtsaddresscollectdata.getIpAddress());
				sBuffer.append("','");
				sBuffer.append(cmtsaddresscollectdata.getMacAddress());
				sBuffer.append("','");
				sBuffer.append(cmtsaddresscollectdata.getStatusAddress());
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sBuffer.append("','");
					sBuffer.append(time);
					sBuffer.append("')");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sBuffer.append("',");
					sBuffer.append("to_date('" + time
							+ "','YYYY-MM-DD HH24:MI:SS')");
					sBuffer.append(")");
				}
				GathersqlListManager.Addsql(sBuffer.toString());
				sBuffer = null;

			}
			cmtsaddresscollectdata = null;
		}

		addressVector = null;

	}

}
