/**
 * <p>Description:snmp tool</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import com.afunms.discovery.IfEntity;

public class SnmpUtils
{
  private static String proType = "udp";

  private static String proPort = "161";
  public static final int OCTSTRING = 1;
  public static final int INTEGER32 = 2;
  private static TransportMapping transport = null;
  
  public static int default_version = org.snmp4j.mp.SnmpConstants.version1;

  public static final int default_retries = 3;

  private static Integer default_port = new Integer(161);

  private static int default_timeout = 5000;

  private static Snmp snmp = null;
  
//获取FDB专用
  public static String[][] getBulkFc(String ip, String community, String oid, int counter, int version, int securityLevel, String securityName, int v3_ap, String authPassphrase, int v3_privacy, String privacyPassphrase, int retries, int timeout) {
      String[][] retArray = null;
      CommunityTarget target = null;
      UserTarget userTarget = null;
      VariableBinding[] vbArr = null;
      ResponseEvent responseEvent = null;
      List valueList = new ArrayList();

      if (version == 3) {
          // SNMP V3
          USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
          SecurityModels.getInstance().addSecurityModel(usm);
          OID ap_oid = null;// 认证协议[MD5|SHA]
          if (v3_ap == 1) {
              // MD5
              ap_oid = AuthMD5.ID;
          } else if (v3_ap == 2) {
              // SHA
              ap_oid = AuthSHA.ID;
          }
          OID v3_privacy_oid = null;// 加密协议[DES|AES128|AES192|AES256]
          if (v3_privacy == 1) {
              // DES
              v3_privacy_oid = PrivDES.ID;
          } else if (v3_privacy == 2) {
              // AES128
              v3_privacy_oid = PrivAES128.ID;
          } else if (v3_privacy == 3) {
              // AES192
              v3_privacy_oid = PrivAES192.ID;
          } else if (v3_privacy == 4) {
              // AES256
              v3_privacy_oid = PrivAES256.ID;
          }
          // add user to the USM
          snmp.getUSM().addUser(new OctetString(securityName), new UsmUser(new OctetString(securityName), ap_oid, new OctetString(authPassphrase), v3_privacy_oid, new OctetString(privacyPassphrase)));
          userTarget = createUserTarget(ip, securityName, securityLevel, retries, timeout);
      } else {
          target = createCommunityTarget(ip, community, version, retries, timeout);
      }
      PDU requestPdu = new PDU();
      requestPdu.setType(PDU.GETBULK);
      requestPdu.setMaxRepetitions(60);
      requestPdu.setNonRepeaters(0);
      requestPdu.add(new VariableBinding(new OID(oid)));

      boolean isContinue = true;
      try {
          for (int i = 0; i < 5; i++) {
              if (version == 3) {
                  responseEvent = snmp.getBulk(requestPdu, userTarget);
              } else {
                  responseEvent = snmp.getBulk(requestPdu, target);
              }
              if (responseEvent == null) {
//                  System.out.println("[DEBUG] SNMP TimeOut");
              }
              vbArr = responseEvent.getResponse().toArray();
              for (VariableBinding vb : vbArr) {
                  if (!vb.getOid().toString().contains(oid)) {
                      isContinue = false;
                      break;
                  }
                  valueList.add(vb.getVariable().toString().trim());
              }
              if (!isContinue) {
                  break;
              }
              requestPdu.clear();
              requestPdu.add(new VariableBinding(vbArr[vbArr.length - 1].getOid()));
          }
          int rowCounter = (valueList.size()) / counter;// 行数
          retArray = new String[rowCounter][counter];
          int k = -1;
          int saveCounter = 0;
          for (int i = 0; i < rowCounter; i++) {
//              System.out.println(ip + " ------------------->");
              saveCounter = ++k;
              for (int j = 0; j < counter; j++) {
                  retArray[i][j] = (String) valueList.get(k);
                  //System.out.println(i + " @ " + retArray[i][j]);
                  k += rowCounter;
              }
              k = saveCounter;
//              System.out.println("<-------------------");
//              System.out.println("");
          }
          return retArray;
      } catch (IOException e) {
          // e.printStackTrace();
          return null;
      }
  }

  public static String[][] getBulkFc(String ip, String community, String[] oids, int version, int securityLevel, String securityName, int v3_ap, String authPassphrase, int v3_privacy, String privacyPassphrase, int retries, int timeout) {

      String[][] retArray = null;
      CommunityTarget target = null;
      UserTarget userTarget = null;
      VariableBinding[] vbArr = null;
      ResponseEvent responseEvent = null;
      List valueList = new ArrayList();

      if (version == 3) {
          // SNMP V3
          USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
          SecurityModels.getInstance().addSecurityModel(usm);
          OID ap_oid = null;// 认证协议[MD5|SHA]
          if (v3_ap == 1) {
              // MD5
              ap_oid = AuthMD5.ID;
          } else if (v3_ap == 2) {
              // SHA
              ap_oid = AuthSHA.ID;
          }
          OID v3_privacy_oid = null;// 加密协议[DES|AES128|AES192|AES256]
          if (v3_privacy == 1) {
              // DES
              v3_privacy_oid = PrivDES.ID;
          } else if (v3_privacy == 2) {
              // AES128
              v3_privacy_oid = PrivAES128.ID;
          } else if (v3_privacy == 3) {
              // AES192
              v3_privacy_oid = PrivAES192.ID;
          } else if (v3_privacy == 4) {
              // AES256
              v3_privacy_oid = PrivAES256.ID;
          }
          // add user to the USM
          snmp.getUSM().addUser(new OctetString(securityName), new UsmUser(new OctetString(securityName), ap_oid, new OctetString(authPassphrase), v3_privacy_oid, new OctetString(privacyPassphrase)));
          userTarget = createUserTarget(ip, securityName, securityLevel, retries, timeout);
      } else {
          target = createCommunityTarget(ip, community, version, retries, timeout);
      }

      PDU requestPdu = new PDU();
      requestPdu.setType(PDU.GETBULK);
      requestPdu.setMaxRepetitions(60);
      requestPdu.setNonRepeaters(0);
      boolean isContinue = true;

      try {
          for (int j = 0; j < oids.length; j++) {
              requestPdu.clear();
              requestPdu.add(new VariableBinding(new OID(oids[j])));
              for (int i = 0; i < 99; i++) {
                  if (version == 3) {
                      responseEvent = snmp.getBulk(requestPdu, userTarget);
                  } else {
                      responseEvent = snmp.getBulk(requestPdu, target);
                  }
                  vbArr = responseEvent.getResponse().toArray();
                  for (VariableBinding vb : vbArr) {
                      if (!vb.getOid().toString().contains(oids[j])) {
                          isContinue = false;
                          break;
                      }
                      valueList.add(vb.getVariable().toString().trim());
                  }
                  if (!isContinue) {
                      isContinue = true;
                      break;
                  }
                  requestPdu.clear();
                  requestPdu.add(new VariableBinding(vbArr[vbArr.length - 1].getOid()));
              }
          }
          int rowCounter = (valueList.size()) / oids.length;// 行数
          retArray = new String[rowCounter][oids.length];
          int k = -1;
          int saveCounter = 0;
          for (int i = 0; i < rowCounter; i++) {
              saveCounter = ++k;
              for (int j = 0; j < oids.length; j++) {
                  retArray[i][j] = (String) valueList.get(k);
                  k += rowCounter;
              }
              k = saveCounter;
          }
          return retArray;
      } catch (IOException e) {
          e.printStackTrace();
          return null;
      }
  }
  
  public static String[][] getCpuTableData(String ip,String community,String[] oids,int version,int retries,int timeout){
	  String[][] tablevalues = null; 
	  CommunityTarget target = null;
	  List rowvalues = null;
      TableEvent row = null;
      VariableBinding[] columnvalues = null;
      VariableBinding columnvalue = null;
	  try{
		  target = createCommunityTarget(ip, community, version, retries, timeout);
		  PDUFactory pf = new DefaultPDUFactory(PDU.GET);
		  TableUtils tableUtils = new TableUtils(snmp,pf );
	      OID[] columns = new OID[oids.length];
	       for (int i = 0; i < oids.length; i++)
	          columns[i] = new OID(oids[i]);
	      rowvalues = tableUtils.getTable(target,columns,null,null);
	         if(rowvalues == null){
	        	 return tablevalues;
	         }
	         tablevalues = new String[rowvalues.size()][oids.length+1];
	         for (int i = 0; i < rowvalues.size(); i++)
	         {
	            row = (TableEvent) rowvalues.get(i);
	            columnvalues = row.getColumns();
	            if(columnvalues!=null)
	            {
	               for (int j = 0; j < columnvalues.length; j++)
	               {     
	                  columnvalue = columnvalues[j];
	                  if(columnvalue == null)continue;
	                  
	                  String value=columnvalue.toString().substring(columnvalue.toString().indexOf("=")+1,columnvalue.toString().length()).trim();
	                  tablevalues[i][j] = value;
	                  tablevalues[i][j+1] = row.getIndex().toString();
	                  //SysLogger.info(ip+" columnvalue.getOid()=== "+columnvalue.getOid()+" index="+row.getIndex()+" value "+value);
	                  //if(columnvalue!=null) tablevalues[i][j] = columnvalue.getVariable().toString();
	               }
	            }
	         }
	  }catch(Exception e){ 
		  SysLogger.error("SnmpUtils.getCpuTableData() ip:"+ip+" community:"+community);
	  }
	  
	  return tablevalues;
  }
  
  public static String[][] getTableData(String ip,String community,String[] oids,int version,int retries,int timeout) throws Exception
  {
	  String[][] tablevalues = null; 
	  PDU pdu = null;
	  CommunityTarget target = null;
	  List rowvalues = null;
      TableEvent row = null;
      VariableBinding[] columnvalues = null;
      VariableBinding columnvalue = null;
     
     try
     {   	 
		  target = createCommunityTarget(ip, community, version, retries, timeout);
		  PDUFactory pf = new DefaultPDUFactory(PDU.GET);
		  TableUtils tableUtils = new TableUtils(snmp,pf );
	      OID[] columns = new OID[oids.length];
	      for (int i = 0; i < oids.length; i++)
	          columns[i] = new OID(oids[i]);
	      rowvalues = tableUtils.getTable(target,columns,null,null);
	         if(rowvalues == null){
	        	 return tablevalues;
	         }
	      tablevalues = new String[rowvalues.size()][oids.length+1];

        for (int i = 0; i < rowvalues.size(); i++)
        {
           row = (TableEvent) rowvalues.get(i);
           columnvalues = row.getColumns();
           
           if(columnvalues!=null)
           {
              for (int j = 0; j < columnvalues.length; j++)
              {     
           	  //SysLogger.info(ip+" columnvalue==="+columnvalues[j]);
                 columnvalue = columnvalues[j];
                 if(columnvalue == null)continue;
                 
                 String value=columnvalue.toString().substring(columnvalue.toString().indexOf("=")+1,columnvalue.toString().length()).trim();
                        tablevalues[i][j] = formatString(value, "gbk");
                 //SysLogger.info(address+" columnvalue.getOid()=== "+columnvalue.getOid()+" index="+row.getIndex()+" value "+value);
                 //if(columnvalue!=null) tablevalues[i][j] = columnvalue.getVariable().toString();
              }
           }
        }
     }
     catch (Exception e)
     {
   	  e.printStackTrace();
   	  SysLogger.error("Error in getTableData,ip=" + ip + ",community=" + community);
    	  tablevalues = null;
     }
     row = null;
     columnvalues = null;
     columnvalue = null;
     return tablevalues;
  }
  
  public static String[][] walkTable(String ip, String community, String[] oids, int version, int securityLevel, String securityName, int v3_ap, String authPassphrase, int v3_privacy, String privacyPassphrase, int retries, int timeout) {
      String[][] retArray = null;
      OID[] rootOID = new OID[oids.length];
      CommunityTarget target = null;
      UserTarget userTarget = null;
      for (int i = 0; i < oids.length; i++) {
          rootOID[i] = new OID(oids[i].trim());
      }
      try {
          List list = null;
          PDUFactory pf = new DefaultPDUFactory(PDU.GETNEXT);
          TreeUtils treeUtils = new TreeUtils(snmp, pf);
          treeUtils.setIgnoreLexicographicOrder(true);

          if (version == 3) {
              // SNMP V3
              USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
              SecurityModels.getInstance().addSecurityModel(usm);
              OID ap_oid = null;// 认证协议[MD5|SHA]
              if (v3_ap == 1) {
                  // MD5
                  ap_oid = AuthMD5.ID;
              } else if (v3_ap == 2) {
                  // SHA
                  ap_oid = AuthSHA.ID;
              }
              OID v3_privacy_oid = null;// 加密协议[DES|AES128|AES192|AES256]
              if (v3_privacy == 1) {
                  // DES
                  v3_privacy_oid = PrivDES.ID;
              } else if (v3_privacy == 2) {
                  // AES128
                  v3_privacy_oid = PrivAES128.ID;
              } else if (v3_privacy == 3) {
                  // AES192
                  v3_privacy_oid = PrivAES192.ID;
              } else if (v3_privacy == 4) {
                  // AES256
                  v3_privacy_oid = PrivAES256.ID;
              }
              // add user to the USM
              snmp.getUSM().addUser(new OctetString(securityName), new UsmUser(new OctetString(securityName), ap_oid, new OctetString(authPassphrase), v3_privacy_oid, new OctetString(privacyPassphrase)));
              userTarget = createUserTarget(ip, securityName, securityLevel, retries, timeout);
              list = treeUtils.walk(userTarget, rootOID);
          } else {
              // SNMP V1|V2
              target = createCommunityTarget(ip, community, version, retries, timeout);
              list = treeUtils.walk(target, rootOID);
          }
          List VBList = new ArrayList();
          TreeEvent treeEvent = null;
          VariableBinding[] VBs = null;
          for (int i = 0; i < list.size(); i++) {
              treeEvent = (TreeEvent) list.get(i);
              VBs = treeEvent.getVariableBindings();
              if(VBs == null)continue;
              if (VBs.length == 0) {
                  continue;
              } else {
                  for (VariableBinding VB : VBs) {
                      VBList.add(VB.getVariable().toString().trim());
                  }
              }
          }
          int rowCounter = (VBList.size()) / (oids.length);
          int columnCounter = oids.length;
          retArray = new String[rowCounter][columnCounter];
          int k = 0;
          for (int i = 0; i < rowCounter; i++) {
              for (int j = 0; j < columnCounter; j++) {
                  retArray[i][j] = (String) VBList.get(k);
                  ++k;
              }
          }
          return retArray;
      } catch (Exception e) {
          e.printStackTrace();
      }
      return null;
  }
  
  public static String[][] walkTable(String ip, String community, String[] oids) {
      String[][] retArray = null;
      OID[] rootOID = new OID[oids.length];
      CommunityTarget target = null;
      for (int i = 0; i < oids.length; i++) {
          rootOID[i] = new OID(oids[i].trim());
      }
      try {
          List list = null;
          PDUFactory pf = new DefaultPDUFactory(PDU.GETNEXT);
          TreeUtils treeUtils = new TreeUtils(snmp, pf);
          treeUtils.setIgnoreLexicographicOrder(true);
          
          target = createDefaultCommunityTarget(ip, community);
          
          list = treeUtils.walk(target, rootOID);
          List VBList = new ArrayList();
          TreeEvent treeEvent = null;
          VariableBinding[] VBs = null;
          for (int i = 0; i < list.size(); i++) {
              treeEvent = (TreeEvent) list.get(i);
              VBs = treeEvent.getVariableBindings();
              if (VBs.length == 0) {
                  continue;
              } else {
                  for (VariableBinding VB : VBs) {
                      VBList.add(VB.getVariable().toString().trim());
                  }
              }
          }
          int rowCounter = (VBList.size()) / (oids.length);
          int columnCounter = oids.length;
          retArray = new String[rowCounter][columnCounter];
          int k = 0;
          for (int i = 0; i < rowCounter; i++) {
              for (int j = 0; j < columnCounter; j++) {
                  retArray[i][j] = (String) VBList.get(k);
                  ++k;
              }
          }
          return retArray;
      } catch (Exception e) {
          e.printStackTrace();
      }
      return null;
  }
  
    public static String[][] getTableData(String ip, String community, String[] oids, int version, int securityLevel, String securityName, int v3_ap, String authPassphrase, int v3_privacy, String privacyPassphrase, int retries, int timeout) throws Exception {
        String[][] tablevalues = null;
        CommunityTarget target = null;
        UserTarget userTarget = null;
        List tableEventList = null;
        TableEvent perTableEvent = null;
        VariableBinding[] columnValueArray = null;
        VariableBinding columnValue = null;
        try {
            OID[] oidColumns = new OID[oids.length];
            for (int i = 0; i < oids.length; i++) {
                oidColumns[i] = new OID(oids[i]);
            }
            PDUFactory pf = new DefaultPDUFactory(PDU.GET);
            TableUtils tableUtils = new TableUtils(snmp, pf);

            if (version == 3) {
                // SNMP V3
                USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
                SecurityModels.getInstance().addSecurityModel(usm);
                OID ap_oid = null;// 认证协议[MD5|SHA]
                if (v3_ap == 1) {
                    // MD5
                    ap_oid = AuthMD5.ID;
                } else if (v3_ap == 2) {
                    // SHA
                    ap_oid = AuthSHA.ID;
                }
                OID v3_privacy_oid = null;// 加密协议[DES|AES128|AES192|AES256]
                if (v3_privacy == 1) {
                    // DES
                    v3_privacy_oid = PrivDES.ID;
                } else if (v3_privacy == 2) {
                    // AES128
                    v3_privacy_oid = PrivAES128.ID;
                } else if (v3_privacy == 3) {
                    // AES192
                    v3_privacy_oid = PrivAES192.ID;
                } else if (v3_privacy == 4) {
                    // AES256
                    v3_privacy_oid = PrivAES256.ID;
                }
                // add user to the USM
                snmp.getUSM().addUser(new OctetString(securityName), new UsmUser(new OctetString(securityName), ap_oid, new OctetString(authPassphrase), v3_privacy_oid, new OctetString(privacyPassphrase)));
                userTarget = createUserTarget(ip, securityName, securityLevel, retries, timeout);
                tableEventList = tableUtils.getTable(userTarget, oidColumns, null, null);

            } else {
                // SNMP V1|V2
                target = createCommunityTarget(ip, community, version, retries, timeout);
                tableEventList = tableUtils.getTable(target, oidColumns, null, null);
            }
            if (tableEventList == null) {
                return tablevalues;
            } else {
                tablevalues = new String[tableEventList.size()][oids.length + 1];
                for (int i = 0; i < tableEventList.size(); i++) {
                    perTableEvent = (TableEvent) tableEventList.get(i);
                    columnValueArray = perTableEvent.getColumns();
                    if (columnValueArray != null) {
                        for (int j = 0; j < columnValueArray.length; j++) {
                            columnValue = columnValueArray[j];
                            if (columnValue == null)
                                continue;
                            String value = columnValue.toString().substring(columnValue.toString().indexOf("=") + 1, columnValue.toString().length()).trim();
                            tablevalues[i][j] = formatString(value, "gbk");
                            tablevalues[i][oids.length] = perTableEvent.getIndex().toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            tablevalues = null;
        }
        perTableEvent = null;
        columnValueArray = null;
        columnValue = null;
        return tablevalues;
    }
  
//格式化字符串
	public static String formatString(String octetString, String encode) {
		String retString = octetString;
		String[] temps = octetString.split(":");
        if (temps.length > 12) {// 字符串包含的":" 个数大于12，认定此字符串是经过十六进制转化的, 那么进行以下还原操作
			try {
				byte[] bs = new byte[temps.length];
				for (int i = 0; i < temps.length; i++) {
					bs[i] = (byte) Integer.parseInt(temps[i], 16);
				}
				retString = new String(bs, encode);
			} catch (Exception e) {
				e.printStackTrace();
				return retString;
			}
		}
		return retString.trim();
	}
  
  /**
   * 得到所有接口的相关信息(2006.06.30)
   * 增加category,如果是路由器，if_index=if_port(2007.01.16)
   */
  public static List getIfEntityList(String address,int snmpversion,String community,int securityLevel,String securityName,
		  int v3_ap,String authPassPhrase,int v3_privacy,String privacyPassPhrase,int category)
  {
  	 List tableValues = new ArrayList(50);
     String[] oids1 = new String[]
             		  {"1.3.6.1.2.1.2.2.1.1",  //index
		               "1.3.6.1.2.1.2.2.1.2",  //descr
		               "1.3.6.1.2.1.2.2.1.5",  //speed
			           "1.3.6.1.2.1.2.2.1.6"};  //mac
     String[] oids4 = new String[]
                        		  {
    		 				   "1.3.6.1.2.1.2.2.1.1",  //index
           			           "1.3.6.1.2.1.2.2.1.8",  //operstatus
           			           "1.3.6.1.2.1.2.2.1.3"}; //type 
     
     String[] oids2 = new String[]
			          {"1.3.6.1.2.1.17.1.4.1.2",  //1.index
			           "1.3.6.1.2.1.17.1.4.1.1"}; //2.port
     
     String[] oids3 = new String[]
			          {"1.3.6.1.2.1.4.20.1.2",    //1.index
			           "1.3.6.1.2.1.4.20.1.1"};   //2.ip     
     try
	 {
        String[][] ipArray = getTableData(address,community,oids1,snmpversion,
      		  securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,
      		  3, 1000*30);    
     	if(ipArray==null) return null;
     	     	
     	Hashtable ifHash = new Hashtable();
     	IfEntity ifEntity = new IfEntity(); 
        for (int i = 0; i < ipArray.length; i++)
        {   
           //SysLogger.info(address+"-------index:"+ipArray[i][0]);   	
           if(ipArray[i][0]==null) continue; //(2006.08.30)
           
           ifEntity = new IfEntity(); 
           if(ipArray[i][0]==null)
        	  ifEntity.setIndex("");     	      
           else
        	  ifEntity.setIndex(ipArray[i][0]); 
           if(ipArray[i][1].length()<50)        	   
     	      ifEntity.setDescr(ipArray[i][1]);
           else //为防止descr过长，插入数据库时出错
        	  ifEntity.setDescr(ipArray[i][1].substring(0,50)); 
           //依据DESCR处理设备面板
           if(ifEntity.getDescr() != null){
           	String descr = ifEntity.getDescr();            	
           	if(descr.indexOf("GigabitEthernet")>=0){
           		String allchassis = descr.substring(descr.lastIndexOf("t")+1);
           	        String[] chassis = allchassis.split("/");
           		if(chassis.length==3){
           			String str_chassis = chassis[0];
           			String slot = chassis[1];
           			String uport = chassis[2];
           			try{
           				ifEntity.setChassis(Integer.parseInt(str_chassis));
           			}catch(Exception chassex){
           				ifEntity.setChassis(-1);
           			}
           			try{
           				ifEntity.setSlot(Integer.parseInt(slot));
           			}catch(Exception chassex){
           				ifEntity.setSlot(-1);
           			}
           			try{
           				ifEntity.setUport(Integer.parseInt(uport));
           			}catch(Exception chassex){
           				ifEntity.setUport(-1);
           			}
           		} 
           	}else if(descr.indexOf("Ethernet")==0){
           		String allchassis = descr.substring(descr.lastIndexOf("t")+1);
       	        String[] chassis = allchassis.split("/");
       	        if(chassis.length==3){
           			String str_chassis = chassis[0];
           			String slot = chassis[1];
           			String uport = chassis[2];
           			try{
           				ifEntity.setChassis(Integer.parseInt(str_chassis));
           			}catch(Exception chassex){
           				ifEntity.setChassis(-1);
           			}
           			try{
           				ifEntity.setSlot(Integer.parseInt(slot));
           			}catch(Exception chassex){
           				ifEntity.setSlot(-1);
           			}
           			try{
           				ifEntity.setUport(Integer.parseInt(uport));
           			}catch(Exception chassex){
           				ifEntity.setUport(-1);
           			}
       	        }           		
           	}
         }
     	   ifEntity.setSpeed(ipArray[i][2]);
     	   ifEntity.setPhysAddress(ipArray[i][3]);
//     	   if("1".equals(ipArray[i][4]))
//     	      ifEntity.setOperStatus(1);
//     	   else
//     		  ifEntity.setOperStatus(2);     		   
//     	   ifEntity.setIpAddress("");
//     	   ifEntity.setIpList("");
//     	   if(category==1)
//     	      ifEntity.setPort(ifEntity.getIndex());
//     	   else
//     		  ifEntity.setPort(""); 
//     	   ifEntity.setType(Integer.parseInt(ipArray[i][5]));
//     	   tableValues.add(ifEntity);
     	   //SysLogger.info(address+"存在"+ipArray[i][0]+"号端口");
     	   ifHash.put(ipArray[i][0],ifEntity);
        }
        
        //==========端口信息后部分==========
            String[][] ipArray4 = null;
            try{
            	ipArray4 = getTableData(address,community,oids4,snmpversion,
                		  securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,
                  		  3, 1000*30);
            }catch(Exception e){
            	
            }
	        if(ipArray4==null) return null;
        
	        ifEntity = null;
            for(int i = 0; i < ipArray4.length; i++)
            {  
            	if(ipArray4[i][0] == null)continue;
            	//SysLogger.info(address+"-------index:"+ipArray2[i][0]+"---port:"+ipArray2[i][1]);  
            	
        	   if(ifHash.get(ipArray4[i][0])==null) continue;
        	   ifEntity = (IfEntity)ifHash.get(ipArray4[i][0]);   
        	   
         	   if("1".equals(ipArray4[i][1]))
         		   ifEntity.setOperStatus(1);
         	   else
         		   ifEntity.setOperStatus(2);     		   
         	   ifEntity.setIpAddress("");
         	   ifEntity.setIpList("");
	      	   if(category==1)
	      	      ifEntity.setPort(ifEntity.getIndex());
	      	   else
	      		  ifEntity.setPort(""); 
//	      	   if((ipArray4[i][2]).length()<7)
	      		   ifEntity.setType(Integer.parseInt(ipArray4[i][2]));
	      	   tableValues.add(ifEntity);
        	   
        	   //ifEntity.setPort(ipArray4[i][1]);
            }       
        
        
        //2.==========找端口==========
        if(category!=1)
        {
            String[][] ipArray2 = null;
            try{
            	ipArray2 = getTableData(address,community,oids2,snmpversion,
                		  securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,
                  		  3, 1000*30);
            }catch(Exception e){
            	
            }
	        if(ipArray2==null) return null;
	        ifEntity = null;
            for(int i = 0; i < ipArray2.length; i++)
            {  
            	if(ipArray2[i][0] == null)continue;
            	//SysLogger.info(address+"-------index:"+ipArray2[i][0]+"---port:"+ipArray2[i][1]);  
            	
        	   if(ifHash.get(ipArray2[i][0])==null) continue;
        	   ifEntity = (IfEntity)ifHash.get(ipArray2[i][0]);        	         	
        	   ifEntity.setPort(ipArray2[i][1]);
            }
        }
        //3.==========找IP=============
        String[][] ipArray3 = null;
        try{
        	ipArray3 = getTableData(address,community,oids3,snmpversion,
            		  securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,
              		  3, 1000*30);
        }catch(Exception e){
        	
        }
	    if(ipArray3==null) return null;
	    ifEntity = null;
        for (int i = 0; i < ipArray3.length; i++)
        {   
        	if(ipArray3[i][0] == null)continue;
//System.out.println(address+"-------index:"+ipArray3[i][0]+"---ip:"+ipArray3[i][1]);
        	if(ifHash.get(ipArray3[i][0])==null) continue;
        	ifEntity = (IfEntity)ifHash.get(ipArray3[i][0]);
        	if(ipArray3[i][1].startsWith("127"))continue;  //过滤掉本地IP      	
        	if("".equals(ifEntity.getIpAddress()))  //解决一个接口多个IP的问题
        	{	
        	   ifEntity.setIpAddress(ipArray3[i][1]);
        	   ifEntity.setIpList(ipArray3[i][1]);
        	}   
        	else
        	   ifEntity.setIpList(ifEntity.getIpList() + "," + ipArray3[i][1]);	
        }
	 }
     catch(Exception e)
	 {
    	 e.printStackTrace();
    	 SysLogger.error("getIfEntityList(),IP=" + address + ",community=" + community,e);
         tableValues = null;
	 }
     return tableValues;
  } 
  
  /**
   * 博科
   * 得到所有接口的相关信息(2006.06.30)
   * 增加category,如果是路由器，if_index=if_port(2007.01.16)
   */
  public static List getIfEntityList_brocade(String address,int snmpversion,String community,int securityLevel,String securityName,
		  int v3_ap,String authPassPhrase,int v3_privacy,String privacyPassPhrase,int category)
  {
  	 List tableValues = new ArrayList(50);
     String[] oids1 = new String[]
             		  {"1.3.6.1.4.1.1588.2.1.1.1.6.2.1.1",  //index
		               "1.3.6.1.4.1.1588.2.1.1.1.6.2.1.36",  //descr
		               "1.3.6.1.2.1.2.2.1.5",  //speed
			           "1.3.6.1.2.1.2.2.1.6",  //mac
			           "1.3.6.1.4.1.1588.2.1.1.1.6.2.1.4",  //operstatus
			           "1.3.6.1.4.1.1588.2.1.1.1.6.2.1.2"}; //type 
     
     String[] oids2 = new String[]
			          {"1.3.6.1.4.1.1588.2.1.1.1.6.2.1.1",  //1.index
			           "1.3.6.1.2.1.17.1.4.1.1"}; //2.port
     
     String[] oids3 = new String[]
			          {"1.3.6.1.4.1.1588.2.1.1.1.6.2.1.1",    //1.index
			           "1.3.6.1.2.1.4.20.1.1"};   //2.ip     
     try
	 {
        String[][] ipArray = getTableData(address,community,oids1,snmpversion,
        		  securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,
          		  3, 1000*30);   
     	if(ipArray==null) return null;
     	     	
     	Hashtable ifHash = new Hashtable();
        for (int i = 0; i < ipArray.length; i++)
        {   
           //SysLogger.info(address+"-------index:"+ipArray[i][0]);   	
           if(ipArray[i][0]==null) continue; //(2006.08.30)
           
           IfEntity ifEntity = new IfEntity(); 
           if(ipArray[i][0]==null)
        	  ifEntity.setIndex("");     	      
           else
        	  ifEntity.setIndex(ipArray[i][0]); 
           if(ipArray[i][1].length()<50)        	   
     	      ifEntity.setDescr(ipArray[i][1]);
           else //为防止descr过长，插入数据库时出错
        	  ifEntity.setDescr(ipArray[i][1].substring(0,50)); 
           //依据DESCR处理设备面板
           if(ifEntity.getDescr() != null){
           	String descr = ifEntity.getDescr();            	
           	if(descr.indexOf("GigabitEthernet")>=0){
           		String allchassis = descr.substring(descr.lastIndexOf("t")+1);
           	        String[] chassis = allchassis.split("/");
           		if(chassis.length==3){
           			String str_chassis = chassis[0];
           			String slot = chassis[1];
           			String uport = chassis[2];
           			try{
           				ifEntity.setChassis(Integer.parseInt(str_chassis));
           			}catch(Exception chassex){
           				ifEntity.setChassis(-1);
           			}
           			try{
           				ifEntity.setSlot(Integer.parseInt(slot));
           			}catch(Exception chassex){
           				ifEntity.setSlot(-1);
           			}
           			try{
           				ifEntity.setUport(Integer.parseInt(uport));
           			}catch(Exception chassex){
           				ifEntity.setUport(-1);
           			}
           		} 
           	}else if(descr.indexOf("Ethernet")==0){
           		String allchassis = descr.substring(descr.lastIndexOf("t")+1);
       	        String[] chassis = allchassis.split("/");
       	        if(chassis.length==3){
           			String str_chassis = chassis[0];
           			String slot = chassis[1];
           			String uport = chassis[2];
           			try{
           				ifEntity.setChassis(Integer.parseInt(str_chassis));
           			}catch(Exception chassex){
           				ifEntity.setChassis(-1);
           			}
           			try{
           				ifEntity.setSlot(Integer.parseInt(slot));
           			}catch(Exception chassex){
           				ifEntity.setSlot(-1);
           			}
           			try{
           				ifEntity.setUport(Integer.parseInt(uport));
           			}catch(Exception chassex){
           				ifEntity.setUport(-1);
           			}
       	        }           		
           	}
         }
     	   ifEntity.setSpeed(ipArray[i][2]);
     	   ifEntity.setPhysAddress(ipArray[i][3]);
     	   if("1".equals(ipArray[i][4]))
     	      ifEntity.setOperStatus(1);
     	   else
     		  ifEntity.setOperStatus(2);     		   
     	   ifEntity.setIpAddress("");
     	   ifEntity.setIpList("");
     	   if(category==1)
     	      ifEntity.setPort(ifEntity.getIndex());
     	   else
     		  ifEntity.setPort(""); 
     	   ifEntity.setType(Integer.parseInt(ipArray[i][5]));
     	   tableValues.add(ifEntity);
     	   //SysLogger.info(address+"存在"+ipArray[i][0]+"号端口");
     	   ifHash.put(ipArray[i][0],ifEntity);
        }
        
        //2.==========找端口==========
        if(category!=1)
        {
            String[][] ipArray2 = null;
            try{
            	ipArray2 = getTableData(address,community,oids2,snmpversion,
                		  securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,
                  		  3, 1000*30);
            }catch(Exception e){
            	
            }
	        if(ipArray2==null) return null;
        
            for(int i = 0; i < ipArray2.length; i++)
            {  
            	if(ipArray2[i][0] == null)continue;
            	//SysLogger.info(address+"-------index:"+ipArray2[i][0]+"---port:"+ipArray2[i][1]);  
            	
        	   if(ifHash.get(ipArray2[i][0])==null) continue;
        	   IfEntity ifEntity = (IfEntity)ifHash.get(ipArray2[i][0]);        	         	
        	   ifEntity.setPort(ipArray2[i][1]);
            }
        }
        //3.==========找IP=============
        String[][] ipArray3 = null;
        try{
        	ipArray3 = getTableData(address,community,oids3,snmpversion,
            		  securityLevel,securityName,v3_ap,authPassPhrase,v3_privacy,privacyPassPhrase,
              		  3, 1000*30);
        }catch(Exception e){
        	
        }
	    if(ipArray3==null) return null;
        
        for (int i = 0; i < ipArray3.length; i++)
        {   
        	if(ipArray3[i][0] == null)continue;
//System.out.println(address+"-------index:"+ipArray3[i][0]+"---ip:"+ipArray3[i][1]);
        	if(ifHash.get(ipArray3[i][0])==null) continue;
        	IfEntity ifEntity = (IfEntity)ifHash.get(ipArray3[i][0]);
        	if(ipArray3[i][1].startsWith("127"))continue;  //过滤掉本地IP      	
        	if(ifEntity.getIpAddress().equals(""))  //解决一个接口多个IP的问题
        	{	
        	   ifEntity.setIpAddress(ipArray3[i][1]);
        	   ifEntity.setIpList(ipArray3[i][1]);
        	}   
        	else
        	   ifEntity.setIpList(ifEntity.getIpList() + "," + ipArray3[i][1]);	
        }
        for(int i=0;i<tableValues.size();i++)
        {
        	IfEntity tmp = (IfEntity)tableValues.get(i); 
        	//为什么需要把00:00:00:00:00:00去掉????山西森林公安
        	/*
        	if(("00:00:00:00:00:00".equals(tmp.getPhysAddress())&&tmp.getType()!=6&&tmp.getType()!=23&&tmp.getType()!=24)
        			||tmp.getIpAddress().startsWith("127"))
        		tableValues.remove(i);
        	*/
        }
	 }
     catch(Exception e)
	 {
    	 e.printStackTrace();
    	 SysLogger.error("getIfEntityList(),IP=" + address + ",community=" + community,e);
         tableValues = null;
	 }
     return tableValues;
  }
  
  
  public String set(String ip,String community,String oid, String value , char type,int version,int retries,int timeout) throws Exception
	{
	  	PDU pdu = new PDU();
		pdu.setType(PDU.SET);
		Variable var = null;
		pdu.clear();
		CommunityTarget target = null;
		target = createCommunityTarget(ip, community, version, retries, timeout);
		
		if (oid != null && value != null )
		{
			if(type == 'x')
			{	
				byte[] bytes = ip2bytes(value);
				var = new OctetString(bytes);
			}
			else if(type == 's')
			{
				var = new OctetString(value);
			}
			else if(type == 'i')
			{
				var = new Integer32(Integer.parseInt(value));
			}
			

			pdu.add(new VariableBinding(new OID(oid), var));
			try
			{
				ResponseEvent response = snmp.send(pdu, target);
				if (response.getResponse() == null)
				{
					System.out.println("timeout" + " oid = " + oid + " , value = " + value + " , type " + type);
					throw new Exception( "timeout" + " oid = " + oid + " , value = " + value + " , type " + type);
				} else
				{
					if (response.getResponse().getErrorStatus() == 0) // set操作成功
					{
						return "set true" + " oid = " + oid + " , value = " + value + " , type " + type;
					} else
					{
						System.out.println("设置失败" + " oid = " + oid + " , value = " + value + " , type " + type);
						System.out.println("error : "+response.getResponse().getErrorStatusText());
						
						throw new Exception("set failed of " + " oid = " + oid + " , value = " + value + " , type " + type);
					}
				}
			} catch (Exception e)
			{
				throw e;
			}finally{
				if (target != null)
				{
					target = null;
				}
			}

		}

		return " expection has happend -----------  "+ " oid = " + oid + " , value = " + value + " , type " + type;
	}
  
  public static String[][] getSubTreeList(String ip, String community, String[] oids, int version, int securityLevel, String securityName, int v3_ap, String authPassphrase, int v3_privacy, String privacyPassphrase, int retries, int timeout) throws Exception {
      String[][] retArray;
      OID[] rootOID = new OID[oids.length];
      CommunityTarget target = null;
      UserTarget userTarget = null;

      for (int i = 0; i < oids.length; i++) {
          rootOID[i] = new OID(oids[i].trim());
      }
      try {
          List list = null;
          PDUFactory pf = new DefaultPDUFactory(PDU.GETNEXT);
          TreeUtils treeUtils = new TreeUtils(snmp, pf);
          treeUtils.setIgnoreLexicographicOrder(true);

          if (version == 3) {
              // SNMP V3
              USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
              SecurityModels.getInstance().addSecurityModel(usm);
              OID ap_oid = null;// 认证协议[MD5|SHA]
              if (v3_ap == 1) {
                  // MD5
                  ap_oid = AuthMD5.ID;
              } else if (v3_ap == 2) {
                  // SHA
                  ap_oid = AuthSHA.ID;
              }
              OID v3_privacy_oid = null;// 加密协议[DES|AES128|AES192|AES256]
              if (v3_privacy == 1) {
                  // DES
                  v3_privacy_oid = PrivDES.ID;
              } else if (v3_privacy == 2) {
                  // AES128
                  v3_privacy_oid = PrivAES128.ID;
              } else if (v3_privacy == 3) {
                  // AES192
                  v3_privacy_oid = PrivAES192.ID;
              } else if (v3_privacy == 4) {
                  // AES256
                  v3_privacy_oid = PrivAES256.ID;
              }
              // add user to the USM
              snmp.getUSM().addUser(new OctetString(securityName), new UsmUser(new OctetString(securityName), ap_oid, new OctetString(authPassphrase), v3_privacy_oid, new OctetString(privacyPassphrase)));
              userTarget = createUserTarget(ip, securityName, securityLevel, retries, timeout);
              list = treeUtils.walk(userTarget, rootOID);
          } else {
              // SNMP V1|V2
              target = createCommunityTarget(ip, community, version, retries, timeout);
              list = treeUtils.walk(target, rootOID);
          }
          List VBsList = new ArrayList();
          TreeEvent treeEvent = null;
          VariableBinding[] VBs = null;
          for (int i = 0; i < list.size(); i++) {
              treeEvent = (TreeEvent) list.get(i);
              VBs = treeEvent.getVariableBindings();
              if (VBs.length == 0) {
                  continue;
              } else {
                  VBsList.add(VBs);
              }
          }
          retArray = new String[VBsList.size()][oids.length];
          for (int i = 0; i < VBsList.size(); i++) {
              VBs = (VariableBinding[]) VBsList.get(i);
              for (int j = 0; j < VBs.length; j++) {
                  retArray[i][j] = VBs[j].getVariable().toString();
              }
          }
          return retArray;
      } catch (Exception e) {
          e.printStackTrace();
      }
      return null;
  }
  
  public static String[][] walkTable(String ip, String community, String[] oids, int counter, int version, int securityLevel, String securityName, int v3_ap, String authPassphrase, int v3_privacy, String privacyPassphrase, int retries, int timeout) {
      String[][] retArray = null;
      OID[] rootOID = new OID[oids.length];
      CommunityTarget target = null;
      UserTarget userTarget = null;
      for (int i = 0; i < oids.length; i++) {
          rootOID[i] = new OID(oids[i].trim());
      }
      try {
          List list = null;
          PDUFactory pf = new DefaultPDUFactory(PDU.GETNEXT);
          TreeUtils treeUtils = new TreeUtils(snmp, pf);
          treeUtils.setIgnoreLexicographicOrder(true);

          if (version == 3) {
              // SNMP V3
              USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
              SecurityModels.getInstance().addSecurityModel(usm);
              OID ap_oid = null;// 认证协议[MD5|SHA]
              if (v3_ap == 1) {
                  // MD5
                  ap_oid = AuthMD5.ID;
              } else if (v3_ap == 2) {
                  // SHA
                  ap_oid = AuthSHA.ID;
              }
              OID v3_privacy_oid = null;// 加密协议[DES|AES128|AES192|AES256]
              if (v3_privacy == 1) {
                  // DES
                  v3_privacy_oid = PrivDES.ID;
              } else if (v3_privacy == 2) {
                  // AES128
                  v3_privacy_oid = PrivAES128.ID;
              } else if (v3_privacy == 3) {
                  // AES192
                  v3_privacy_oid = PrivAES192.ID;
              } else if (v3_privacy == 4) {
                  // AES256
                  v3_privacy_oid = PrivAES256.ID;
              }
              // add user to the USM
              snmp.getUSM().addUser(new OctetString(securityName), new UsmUser(new OctetString(securityName), ap_oid, new OctetString(authPassphrase), v3_privacy_oid, new OctetString(privacyPassphrase)));
              userTarget = createUserTarget(ip, securityName, securityLevel, retries, timeout);
              list = treeUtils.walk(userTarget, rootOID);
          } else {
              // SNMP V1|V2
              target = createCommunityTarget(ip, community, version, retries, timeout);
              list = treeUtils.walk(target, rootOID);
          }
          List VBList = new ArrayList();
          TreeEvent treeEvent = null;
          VariableBinding[] VBs = null;
          for (int i = 0; i < list.size(); i++) {
              treeEvent = (TreeEvent) list.get(i);
              VBs = treeEvent.getVariableBindings();
              if (VBs.length == 0) {
                  continue;
              } else {
                  for (VariableBinding VB : VBs) {
                      VBList.add(VB.getVariable().toString().trim());
                  }

              }
          }
          int rowCounter = (VBList.size()) / counter;
          int columnCounter = counter;
          retArray = new String[rowCounter][columnCounter];
          int k = -1;
          int saveCounter = 0;
          for (int i = 0; i < rowCounter; i++) {
              saveCounter = ++k;
              for (int j = 0; j < columnCounter; j++) {
                  retArray[i][j] = (String) VBList.get(k);
                  k += rowCounter;
              }
              k = saveCounter;
          }
          return retArray;
      } catch (Exception e) {
          e.printStackTrace();
      }
      return null;
  }
  public static String[][] getSubTreeList(String ip, String community, String[] oids, int version, int retries, int timeout) throws Exception {
		String[][] retArray;
		OID[] rootOID = new OID[oids.length];
		CommunityTarget target = null;
		target = createCommunityTarget(ip, community, version, retries, timeout);
		PDUFactory pf = new DefaultPDUFactory(PDU.GETNEXT);
		TreeUtils treeUtils = new TreeUtils(snmp, pf);
		for (int i = 0; i < oids.length; i++) {
			rootOID[i] = new OID(oids[i].trim());
		}
		try {
			List list = treeUtils.walk(target, rootOID);
			List vbsList=new ArrayList();
			TreeEvent treeEvent;
			VariableBinding[] vbs;
			for (int i=0;i<list.size();i++) {
				treeEvent = (TreeEvent) list.get(i);
				 vbs = treeEvent.getVariableBindings();
				if (vbs.length == 0) {
					continue;
				}else{
					vbsList.add(vbs);
				}
			}
			retArray=new String[vbsList.size()][oids.length];
			for(int i=0;i<vbsList.size();i++){
				vbs=(VariableBinding[])vbsList.get(i);
				for(int j=0;j<vbs.length;j++){
					retArray[i][j]=vbs[j].getVariable().toString();
				}
			}
			return retArray;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String[][] getList(String ip, String community, String[] oids, int version, int retries, int timeout) throws Exception {
		String[][] ret = new String[1][oids.length];
		if (oids == null) {
			return null;
		}
		PDU pdu = new PDU();
		pdu.setType(PDU.GET);
		pdu.clear();

		for (int i = 0; i < oids.length; i++) {
			pdu.add(new VariableBinding(new OID(oids[i].trim())));
		}
		CommunityTarget target = null;
		target = createCommunityTarget(ip, community, version, retries, timeout);
		try {
			ResponseEvent response = snmp.send(pdu, target);
			if (response != null) {
				if (response.getResponse() != null) {
					Vector v = response.getResponse().getVariableBindings();
					if (v != null && v.size() > 0) {
						for (int j = 0; j < v.size(); j++) {
							VariableBinding vb = (VariableBinding) v.elementAt(j);
							if (vb.getOid() != null && vb.getVariable() != null) {
								ret[0][j] = vb.getVariable() + "";
								;
							} else {
								throw new Exception("can not get the value from the snmp device , please make sure your oid is right");
							}
						}
					}
				} else {
					throw new Exception("there has no response from the snmp device");
				}

				return ret;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			if (target != null) {
				target = null;
			}
		}
		return null;
	}
  
    public static String get(String ip, String community, String oid, int version, int retries, int timeout) throws Exception {
		if (oid == null)
		{
			return null;
		}
		PDU pdu = new PDU();
		pdu.setType(PDU.GET);
		pdu.clear();

		pdu.add(new VariableBinding(new OID(oid.trim())));
		CommunityTarget target = null;
		target = createCommunityTarget(ip, community, version, retries, timeout);
		try {
			ResponseEvent response = snmp.send(pdu, target);
			if (response != null)
			{
				if (response.getResponse() != null)
				{
					Vector v = response.getResponse().getVariableBindings();
					if (v != null && v.size() > 0)
					{
						VariableBinding vb = (VariableBinding) v.elementAt(0);
						if(vb.getOid() != null && vb.getVariable() != null)
						{
//							SysLogger.info("###########----------------"+vb.getVariable());
//							SysLogger.info(vb.getVariable()+"");
//							SysLogger.info("###########----------------");
							return vb.getVariable()+"";
						}
						else
						{
							throw new Exception("can not get the value from the snmp device , please make sure your oid is right");
						}
						
					}
				} else
				{
					throw new Exception("there has no response from the snmp device");
				}

			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}finally{
			if (target != null)
			{
				target = null;
			}
		}
		return null;
  	}
  
	public static byte[] ip2bytes(String ip) {
		byte[] result = new byte[0];
		try {
			StringTokenizer st = new StringTokenizer(ip, ".");
			result = new byte[st.countTokens()];
			for (int i = 0; i < result.length; ++i) {
				String token = st.nextToken();
				Integer part = new Integer(token);
				result[i] = part.byteValue();
			}
		} catch (Exception e) {
			result = new byte[0];
		}
		return result;
	}
  
  public static String[][] getTemperatureTableData(String ip,String community,String[] oids,int version,
		  int securityLevel,String securityName,int v3_ap,String authPassphrase,int v3_privacy,String privacyPassphrase,
		  int retries,int timeout) throws Exception
  {
	  //SysLogger.info("snmp version================"+version);
	  String[][] tablevalues = null; 
	  CommunityTarget target = null;
	  UserTarget usertarget = null;
	  List rowvalues = null;
      TableEvent row = null;
      VariableBinding[] columnvalues = null;
      VariableBinding columnvalue = null;    
     try{   	
    	 if(version == 3){
    		//SNMP V3
    		USM usm = new USM(SecurityProtocols.getInstance(),  
    	    		 	new OctetString(MPv3.createLocalEngineID()), 0);  
    	    SecurityModels.getInstance().addSecurityModel(usm);  
    	    OID ap_oid = null;
    	    if(v3_ap == 1){
    	    	//MD5
    	    	ap_oid = AuthMD5.ID;
    	    }else if(v3_ap == 2){
    	    	//SHA
    	    	ap_oid = AuthSHA.ID;
    	    }
    	    OID v3_privacy_oid = null;
    	    if(v3_privacy == 1){
    	    	//DES
    	    	v3_privacy_oid = PrivDES.ID;
    	    }else if(v3_privacy == 2){
    	    	//AES128
    	    	v3_privacy_oid = PrivAES128.ID;
    	    }else if(v3_privacy == 3){
    	    	//AES192
    	    	v3_privacy_oid = PrivAES192.ID;
    	    }else if(v3_privacy == 4){
    	    	//AES256
    	    	v3_privacy_oid = PrivAES256.ID;
    	    }
    	    // add user to the USM   
    	    snmp.getUSM().addUser(new OctetString(securityName),  
    	    				 	new UsmUser(new OctetString(securityName),  
    	    				 	ap_oid, //认证协议  MD5/SHA 
    	    				 	new OctetString(authPassphrase),  
    	    				 	v3_privacy_oid, //加密协议  DES/AES128/AES192/AES256
    	    				 	new OctetString(privacyPassphrase)));  
    		 usertarget = createUserTarget(ip, securityName, securityLevel, retries, timeout);
    		 PDUFactory pdu = new DefaultPDUFactory(PDU.GET);
    		 TableUtils tableUtils = new TableUtils(snmp,pdu );
    		 OID[] columns = new OID[oids.length];
      	      for (int i = 0; i < oids.length; i++)
      	          columns[i] = new OID(oids[i]);
      	      rowvalues = tableUtils.getTable(usertarget,columns,null,null);
      	      
      	         if(rowvalues == null){
      	        	 return tablevalues;
      	         }    		 
    	 }else{
    		 target = createCommunityTarget(ip, community, version, retries, timeout);
    		 PDUFactory pf = new DefaultPDUFactory(PDU.GET);
   		  	 TableUtils tableUtils = new TableUtils(snmp,pf );
   		  	 OID[] columns = new OID[oids.length];
   		  	 for (int i = 0; i < oids.length; i++)
   		  		 columns[i] = new OID(oids[i]);
   		  	 rowvalues = tableUtils.getTable(target,columns,null,null);
   	         if(rowvalues == null){
   	        	 return tablevalues;
   	         }
    	 }
	     tablevalues = new String[rowvalues.size()][oids.length+1];
         for (int i = 0; i < rowvalues.size(); i++){
        	 row = (TableEvent) rowvalues.get(i);
             columnvalues = row.getColumns();
             if(columnvalues!=null){
            	 for (int j = 0; j < columnvalues.length; j++){     
            		 columnvalue = columnvalues[j];
            		 if(columnvalue == null)continue;
                    
            		 String value=columnvalue.toString().substring(columnvalue.toString().indexOf("=")+1,columnvalue.toString().length()).trim();
            		 tablevalues[i][j] = value;
            		 tablevalues[i][oids.length] = row.getIndex().toString();
            		 //SysLogger.info(ip+" columnvalue.getOid()=== "+columnvalue.getOid()+" index="+row.getIndex()+" value "+value);
            		 //if(columnvalue!=null) tablevalues[i][j] = columnvalue.getVariable().toString();
                 }
              }
           }
     }
     catch (Exception e)
     {
   	  e.printStackTrace();
   	  SysLogger.error("Error in getTableData,ip=" + ip + ",community=" + community);
    	  tablevalues = null;
     }
     row = null;
     columnvalues = null;
     columnvalue = null;
     return tablevalues;
  }
  
  /**
   * mac
   */
  public static String getMacAddress(String ip,String community,int version,
		  int securityLevel,String securityName,int v3_ap,String authPassphrase,int v3_privacy,String privacyPassphrase,
		  int retries,int timeout){
	  String bridge = "";
	  String[] oids = new String[]
	                             {"1.3.6.1.2.1.2.2.1.6"};       //ifPhysAddress
	  try{
		  String[][] macArray = getTableData(ip,community,oids,version,
				  securityLevel,securityName,v3_ap,authPassphrase,v3_privacy,privacyPassphrase,
				  retries,timeout);
	      if(macArray==null) return null;
	      String mac = "";
	      List maclist = new ArrayList();
	      for(int i = 0; i < macArray.length; i++){
	    	  mac = macArray[i][0];
	    	  if("00:00:00:00:00:00".equalsIgnoreCase(mac))continue;
	    	  if(maclist.contains(mac))continue;
	    	  if(mac == null || "null".equalsIgnoreCase(mac))continue;
	    	  maclist.add(mac);
	    	  bridge = bridge+"|"+mac;
	      }
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  return bridge;
  }
  
  /**
   * 被监视对象本身mac
   */
  public static String getHostBridgeAddress(String ip,String community,int version,
		  int securityLevel,String securityName,int v3_ap,String authPassphrase,int v3_privacy,String privacyPassphrase,
		  int retries,int timeout)
  {
	  String  value = null;
	  String hostmac = "";
	  try{		  
		  String[] oids =                
			  new String[] {                
				  "1.3.6.1.2.1.2.2.1.6"
				  };
		  String[][] valueArray = null;
			try {
				valueArray = getTableData(ip,community,oids,version,
						  securityLevel,securityName,v3_ap,authPassphrase,v3_privacy,privacyPassphrase,
						  retries,timeout);
			} catch(Exception e){
				valueArray = null;
			}	
			if(valueArray != null){
				for(int i=0;i<valueArray.length;i++){
					value=valueArray[i][0];					
					if (value == null || value.length()==0)continue;
					if(hostmac==null){
						hostmac = value;
					}else{
					hostmac = hostmac +","+value;					
					}					
				}
				hostmac = hostmac.substring(1,hostmac.length());
			}				
		}
		catch(Exception e){
		}
	return hostmac;	
  }
  
  
  
//  /**
//   * 得到system oid
//   */
//  public String getSysOid(String address,String community)
//  {
//  	 return snmp.getMibValue(address,community,"1.3.6.1.2.1.1.2.0");
//  }
  
  public static List getFdbTable(String ip,String community,int version,int retries,int timeout)
  {
     String[] oids1 = new String[]
                    {"1.3.6.1.2.1.17.4.3.1.1",  //1.mac   		       
                     "1.3.6.1.2.1.17.4.3.1.2",  //2.port
                     "1.3.6.1.2.1.17.4.3.1.3"}; //3.type
     
     String[] oids2 = new String[]
           		 {"1.3.6.1.2.1.17.1.4.1.2",  //1.index
           		  "1.3.6.1.2.1.17.1.4.1.1"}; //2.port
                      
     String[][] ipArray1 = null;
     String[][] ipArray2 = null;
     List tableValues = new ArrayList(30);
     try
     {         
   	  HashMap portMap = new HashMap();
   	  
   	  ipArray2 = getTableData(ip,community,oids2,version,retries,timeout);
   	  for(int i=0;i<ipArray2.length;i++){
   		  //SysLogger.info(address+" FDB port:"+ipArray2[i][1]+"   index:"+ipArray2[i][0]);
   	     portMap.put(ipArray2[i][1], ipArray2[i][0]);
   	  }
   	      	  
   	  ipArray1 = getTableData(ip,community,oids1,version,retries,timeout);
   	  for(int i=0;i<ipArray1.length;i++)
   	  {
   		  //SysLogger.info("FDB====>ip : "+address+" type========="+ipArray1[i][2]+" port "+ipArray1[i][1]+" MAC : "+ipArray1[i][0]);
             if(!"3".equals(ipArray1[i][2])) continue; //only type=learned 
   		  if(portMap.get(ipArray1[i][1])==null) continue;
   		  
  		      String ifIndex = (String)portMap.get(ipArray1[i][1]);     	       	      
  		      String[] item = new String[3];
  		      item[0] = ifIndex;
  		      item[1] = ipArray1[i][0];
  		      item[2] = ipArray1[i][1];
  		      //SysLogger.info(address+" FDB "+" ifIndex : "+ifIndex+" MAC : "+ipArray1[i][0]) ;  		      
    	      tableValues.add(item);     	      
   	  }    	  
     }
     catch(Exception e)
     {
   	  e.printStackTrace();
   	  SysLogger.info("getFdbTable(),ip=" + ip + ",community=" + community);         
     }
     return tableValues;
  }
  
  public static String[][] getFdbTable(String ip, String community, int version, int securityLevel, String securityName, int v3_ap, String authPassphrase, int v3_privacy, String privacyPassphrase, int retries, int timeout) {
      try {
          String[][] dot1dTpFdbTableValue = null;
          String dot1dTpFdbTableOID = "1.3.6.1.2.1.17.4.3.1";// dot1dTpFdbTable根OID

          String[][] dot1dBasePortTableValue = null;
          String[] dot1dBasePortTableOID = new String[] { "1.3.6.1.2.1.17.1.4.1.1", //
                  "1.3.6.1.2.1.17.1.4.1.2", //
          };

          String[][] ifTableValue = null;
          String[] ifTableOID = new String[] { "1.3.6.1.2.1.2.2.1.1", //
                  "1.3.6.1.2.1.2.2.1.2", //
          };

          String[][] ipNetToMediaTableValue = null;
          String[] ipNetToMediaTable = new String[] { "1.3.6.1.2.1.4.22.1.1", //
                  "1.3.6.1.2.1.4.22.1.2", //
                  "1.3.6.1.2.1.4.22.1.3", //
                  "1.3.6.1.2.1.4.22.1.4", //
          };
          dot1dTpFdbTableValue = SnmpUtils.getBulkFc(ip, community, dot1dTpFdbTableOID, 3, version, securityLevel, securityName, v3_ap, authPassphrase, v3_privacy, privacyPassphrase, 3, 1000 * 30);
          dot1dBasePortTableValue = SnmpUtils.getBulkFc(ip, community, dot1dBasePortTableOID, version, securityLevel, securityName, v3_ap, authPassphrase, v3_privacy, privacyPassphrase, 3, 1000 * 30);
          ifTableValue = SnmpUtils.getBulkFc(ip, community, ifTableOID, version, securityLevel, securityName, v3_ap, authPassphrase, v3_privacy, privacyPassphrase, 3, 1000 * 30);
          ipNetToMediaTableValue = SnmpUtils.getBulkFc(ip, community, ipNetToMediaTable, version, securityLevel, securityName, v3_ap, authPassphrase, v3_privacy, privacyPassphrase, 3, 1000 * 30);

          String[][] resultArray = new String[dot1dTpFdbTableValue.length][5];
          String physicPort = null;
          String mac = null;
          String ifIndex = null;
          for (int i = 0; i < dot1dTpFdbTableValue.length; i++) {
              // 由于timeout造成的取值错误处理
              if (!dot1dTpFdbTableValue[i][0].contains(":") || dot1dTpFdbTableValue[i][1].contains(":") || (dot1dTpFdbTableValue[i][2].contains(":") || Integer.parseInt(dot1dTpFdbTableValue[i][2]) > 5)) {
                  continue;
              }
              resultArray[i][0] = dot1dTpFdbTableValue[i][0];
              resultArray[i][1] = dot1dTpFdbTableValue[i][1];
              resultArray[i][2] = dot1dTpFdbTableValue[i][2];
              physicPort = dot1dTpFdbTableValue[i][1];
              mac = dot1dTpFdbTableValue[i][0];
              for (int j = 0; j < dot1dBasePortTableValue.length; j++) {
                  if (physicPort.equals(dot1dBasePortTableValue[j][0])) {
                      resultArray[i][3] = dot1dBasePortTableValue[j][1];
                  }
              }
              for (int k = 0; k < ipNetToMediaTableValue.length; k++) {
                  if (mac.equals(ipNetToMediaTableValue[k][1])) {
                      resultArray[i][4] = ipNetToMediaTableValue[k][2];
                  }
              }
          }
          for (int i = 0; i < resultArray.length; i++) {
              ifIndex = resultArray[i][3];
              if (ifIndex == null) {
                  continue;
              }
              for (int j = 0; j < ifTableValue.length; j++) {
                  if (ifIndex.equals(ifTableValue[j][0])) {
                      resultArray[i][3] = ifTableValue[j][1];
                  }
              }
          }
          return resultArray;
      } catch (Exception e) {
          e.printStackTrace();
          SysLogger.error("getFdbTable(),ip=" + ip + ",community=" + community);
          return null;
      }

  }
  
  
  private static CommunityTarget createCommunityTarget(String address,String community,int version,int retries,int timeout)
  {
	  //SysLogger.info(address+"==="+community+"==="+version);
      CommunityTarget target = new CommunityTarget();
      target.setCommunity(new OctetString(community));     
      target.setVersion(version);
      target.setRetries(retries);
      target.setAddress(GenericAddress.parse(address + "/161"));
      target.setTimeout(timeout);
     
      return target;
  }
  
  private static UserTarget createUserTarget(String address,String securityName,int securityLevel,int retries,int timeout)
  {
	  //SysLogger.info(address+"==="+community+"==="+version);
	  UserTarget target = new UserTarget();
	  target.setAddress(GenericAddress.parse(address + "/161"));
	  target.setRetries(retries);  
	  target.setTimeout(timeout);  
	  target.setVersion(SnmpConstants.version3);  
	  if(securityLevel == 1){
		  target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
	  }else if(securityLevel == 2){
		  target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
	  }else if(securityLevel == 1){
		  target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
	  }
	    
	  //target.setSecurityName(new OctetString("dhccsnmpv3"));
	  target.setSecurityName(new OctetString(securityName));
      return target;
  }
  
  public PDU createPDU(Target target)
  {
     PDU request = new PDU();
     request.setType(PDU.GET);
     return request;
  }
  private static PDU createPdu(int version)
  {
    if (version == 0)
      return new PDUv1();
    if (version == 1) {
      return new PDU();
    }
    return new PDU();
  }

  private static CommunityTarget createCommunityTarget(Address targetAddress, String community, int retries, int timeout, int version)
  {
    CommunityTarget target = new CommunityTarget();
    target.setCommunity(new OctetString(community));
    target.setAddress(targetAddress);
    target.setRetries(retries);
    target.setTimeout(timeout);
    target.setVersion(version);
    return target;
  }
  
  private static CommunityTarget createDefaultCommunityTarget(String address, String community) {
      CommunityTarget target = new CommunityTarget();
      target.setCommunity(new OctetString(community));
      target.setVersion(default_version);
      target.setRetries(default_retries);
      target.setAddress(GenericAddress.parse(address + "/" + default_port));
      target.setTimeout(default_timeout);
      return target;
  }



  static
  {
    try
    {
      transport = new DefaultUdpTransportMapping();
      transport.listen();
      snmp = new Snmp(transport);
      //snmp.listen();
      //SysLogger.info("##########################");
      //SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static String[][] getTableDataInterface(String ip,String community,String[] oids,int version,int retries,int timeout) throws Exception
  {
	  String[][] tablevalues = null; 
	  PDU pdu = null;
	  CommunityTarget target = null;
	  List rowvalues = null;
      TableEvent row = null;
      VariableBinding[] columnvalues = null;
      VariableBinding columnvalue = null;
     
     try
     {   	 
		  target = createCommunityTarget(ip, community, version, retries, timeout);
		  PDUFactory pf = new DefaultPDUFactory(PDU.GET);
		  TableUtils tableUtils = new TableUtils(snmp,pf );
	      OID[] columns = new OID[oids.length];
	      for (int i = 0; i < oids.length; i++)
	          columns[i] = new OID(oids[i]);
	      rowvalues = tableUtils.getTable(target,columns,null,null);
	         if(rowvalues == null){
	        	 return tablevalues;
	         }
	      tablevalues = new String[rowvalues.size()][oids.length+1];
       String index="";
        for (int i = 0; i < rowvalues.size(); i++)
        {
           row = (TableEvent) rowvalues.get(i);
           columnvalues = row.getColumns();
           
           if(columnvalues!=null)
           {
              for (int j = 0; j < columnvalues.length; j++)
              {     
                 columnvalue = columnvalues[j];
                 if(columnvalue == null)continue;
                 
                 String value=columnvalue.toString().substring(columnvalue.toString().indexOf("=")+1,columnvalue.toString().length()).trim();
                 tablevalues[i][j] = value;
                
              }
              tablevalues[i][oids.length] =  index=row.getIndex()+"";;
           }
        }
     }
     catch (Exception e)
     {
   	  e.printStackTrace();
   	  SysLogger.error("Error in getTableData,ip=" + ip + ",community=" + community);
    	  tablevalues = null;
     }
     row = null;
     columnvalues = null;
     columnvalue = null;
     return tablevalues;
  }
  public  static void main(String[] args) throws Exception{
	  String[][] valueArray = null;
		String[] oids = new String[] {               
				"1.3.6.1.2.1.2.2.1.1",//cpu利用率
				"1.3.6.1.2.1.2.2.1.2"
		};
		 
	valueArray = SnmpUtils.getTableData("10.37.2.1","",oids,3,3,"v3user",1,"md5-auth",1,"des-priv",3,1000*30);
	for(int i=0;i<valueArray.length;i++){
		System.out.println("====="+valueArray[i][1]);
		System.out.println("====="+valueArray[i][0]);
		System.out.println("====="+valueArray[i][2]);
	}
  }
}