package com.afunms.util.connectionPool;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import com.afunms.common.util.SystemConstant;

class ConnectionReaper extends Thread {

    private DBConnectionPool pool;
    private final long delay = 300000;

    ConnectionReaper(DBConnectionPool pool) {
        this.pool = pool;
    }

    public void run() {
        while (true) {
            try {
                sleep(delay);
            } catch (InterruptedException e) {
            }
            pool.reapIdleConnections();
        }
    }
}

/**
 * 两扯?
 */
public class DBConnectionPool {


    private Vector poolConnections;

    /**
     * 两车仔两?
     * min number of connections of current pool
     */
    private int minConn;

    /**
     * 两车状两?
     * max number of connections of current pool
     */
    private int maxConn;

    /**
     * 两车米
     * name of pool
     */
    private String name;

    /**
     * JDBC得?
     * password to connect to database
     */
    private String password;

    /**
     * 窒示康两
     * url to database
     */
    private String URL;

    /**
     * JDBC涤?
     * user to connect to database
     */
    private String user;

    /**
     * 两悼孕始?
     * Maximum number of seconds a Connection can go unused before it is closed
     */
    private int connectUseTimeout;

    /**
     * 两底词哟?
     * number of times a connection can be re-used before connection to database is closed and re-opened (optional parameter)
     */
    private int connectUseCount;

    /**
     * 两笔拥状始Ｈ乖苟始溃踊承糜够两４两持牌?
     * Maximum number of seconds a Thread can checkout a Connection before it is closed and returned to the pool.  This is a protection against the Thread dying and leaving the Connection checked out indefinately
     */
    private int connectCheckOutTimeout;
    private PrintWriter log;

    /**
     * 涤两登鞘?
     */
    private int numRequests;//需要的连接数

    /**
     * 荡视两凳?
     */
    private int numWaits;//等待中的客户程序数

    /**
     * 揖笔拥两誓
     */
    private int numCheckOut;//已经取出的连接数
   private String driverClassName;
    private ConnectionReaper reaper;

    /**
     * 找到所给的连接对象connection在向量(连接池)中的index
     * <p>Find the given connection in the pool
     *
     * @return Index into the pool, or -1 if not found
     */
    private int find(java.sql.Connection con, java.util.Vector vec) {
        int index = -1;
        // Find the matching Connection in the pool
        if ((con != null) &&
                (vec != null)) {
            for (int i = 0; i < vec.size(); i++) {
                ConnectionObject co = (ConnectionObject)
                        vec.elementAt(i);
                if (co.getCon() == con) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * <p>Sets the last access time for the given ConnectionObject
     *
     * @param co 需要设置最后访问时间的ConnectionObject对象
     */
    private void touch(ConnectionObject co) {
        if (co != null) {
            co.setLastAccess(System.currentTimeMillis());
        }
    }

    /**
     * 关闭ConnectionObject中的连接
     * <p>Closes the connection in the given ConnectionObject
     *
     * @param connectObject ConnectionObject
     */
    private void close(ConnectionObject connectionObject) {
        if (connectionObject != null) {
            if (connectionObject.getCon() != null) {
                try {

                    // Close the connection
                    connectionObject.getCon().close();
                } catch (Exception ex) {
                    // Ignore any exceptions during close
                }

                // Clear the connection object reference
                connectionObject.setCon(null);
            }
        }
    }

    /**
     * 将不再使用的连接返回给连接池
     *
     * @param con 客户程序释放的连接
     */
    public synchronized void freeConnection(Connection con) {
        // Find the connection in the pool
        int index = find(con, poolConnections);

        if (index != -1) {
            ConnectionObject co = (ConnectionObject) poolConnections.elementAt(index);

            // If the use count exceeds the max, remove it from
            // the pool.
            if ((connectUseCount > 0) && (co.getUseCount() >= connectUseCount)) {
                removeFromPool(index);
            } else {
                // Clear the use count and reset the time last used
                touch(co);
                co.setInUse(false);
            }
        }
        numCheckOut--;
        notifyAll();
//    System.out.println("how many connections in the pool now? " + poolConnections.size()) ;
    }

    /**
     * 将指定index的ConnectionObject对象从连接池中删除
     * <p>Removes the ConnectionObject from the pool at the
     * given index
     *
     * @param index Index into the pool vector
     */

    private synchronized void removeFromPool(int index) {
        // Make sure the pool and index are valid
        if (poolConnections != null) {
            if (index < poolConnections.size()) {
                // Get the ConnectionObject and close the connection
                ConnectionObject co = (ConnectionObject) poolConnections.elementAt(index);
                close(co);
                // Remove the element from the pool
                poolConnections.removeElementAt(index);
            }
        }
        notifyAll();
    }

    /**
     * 删除无用连接
     *
     * @param co 需要从连接池中删除的ConnectionObject 对象
     */

    private synchronized void removeConnection(ConnectionObject co) {
//    	System.out.println("=删除不可用连接==");
        poolConnections.remove(co);
        try {
            co.getCon().close();
        } catch (SQLException ex) {
            System.out.println("超时连接关闭失败");
        }
    }

    /**
     * 从连接池获得一个可用连接.如没有空闲的连接且当前连接数小于最大连接
     * 数限制,则创建新连接.
     * 如果取得的是不可用的连接，接着尝试连接池里的下一个连接
     */
    public synchronized ConnectionObject getConnection() {
        ConnectionObject con = new ConnectionObject();
        con = null;
        
        
        if (poolConnections.size() > 0) {
            // 获取向量中第一个可用连接
            try {
                int poolSize = poolConnections.size();
               // SysLogger.info("#########   数据库连接池大小:"+poolSize+" ############");
                for (int i = 0; i < poolSize; i++) {
                    ConnectionObject co = (ConnectionObject) poolConnections.elementAt(i);
                    if (co.isAvailable()) {
                        con = co;
                        boolean flg=true;
                       
                        	
//                        	try{
//                        	co.getCon().createStatement();
//                        	}
//                        	catch(Exception e)
//                        	{
//                        		System.out.println("=不可用连接==");
//                            	removeFromPool(i);
//                            	flg=false;
//                        	}
                        Statement st=null;
                        try{
                        	st=co.getCon().createStatement();
                        }catch(Exception e){
                        	System.out.println("=不可用连接==");
                            removeFromPool(i);
                            flg=false;
                        }finally{
                        	st.close();
                        }
                        		
                            	
                        	
                            if(flg)
                            {
                        	break;
                            }
                       
                        
                        //System.out.println("Check out the NO." + (i + 1) + " connection of pool " + name);
                        
                    }
                }
            } catch (SQLException e) {
                System.out.println("从连接池" + name + "删除一个无效连接");
            }
        }
        if ((con == null) && (numCheckOut < maxConn)) {
            con = newConnection();
        }
        if (con != null) {
            con.setLastAccess(System.currentTimeMillis());
            con.setInUse(true);
            int count = con.getUseCount();
            con.setUseCount(count++);
            touch(con);
            numCheckOut++;
        }
        return con;
    }

    /**
     * 从连接池获取可用连接.可以指定客户程序能够等待的最长时间
     * 参见前一个getConnection()方法.
     *
     * @param timeout 以毫秒计的等待时间限制
     */
    public synchronized ConnectionObject getConnection(long timeout) {
        long startTime = new Date().getTime();
        ConnectionObject con;
        while ((con = getConnection()) == null) {
            try {
                wait(timeout);
            } catch (InterruptedException e) {
            }
            if ((new Date().getTime() - startTime) >= timeout) {
                // wait()返回的原因是超时
                return null;
            }
        }
        return con;
    }

    /**
     * 关闭所有连接
     */
    public synchronized void release() {
        Enumeration allConnections = poolConnections.elements();
        while (allConnections.hasMoreElements()) {
            ConnectionObject co = (ConnectionObject) allConnections.nextElement();
            try {
                co.isAvailable();
                co.getCon().close();
                System.out.println("关闭连接池" + name + "中的一个连接");
            } catch (SQLException e) {
                System.out.println("无法关闭连接池" + name + "中的连接");
            }
        }
        poolConnections.removeAllElements();
    }

    /**
     * 将文本信息写入日志文件
     */
    private void log(String msg) {
        log.println(new Date() + ": " + msg);
    }

    /**
     * 将文本信息与异常写入日志文件
     */
    private void log(Throwable e, String msg) {
        log.println(new Date() + ": " + msg);
        e.printStackTrace(log);
    }

    /**
     * 创建新的连接ConnectionObject对象，加入连接池中，并且返回
     */
    private ConnectionObject newConnection() {
        ConnectionObject co = new ConnectionObject();
        try {
            if (user == null) {
                co.setCon(DriverManager.getConnection(URL));
            } else {
//        Class.forName(driverClassName).newInstance();
/*	java.sql.Connection con = DriverManager.getConnection(URL, user, password);
        if  (con.getMetaData()!=null)
        {
          System.out.println("kao");
        }
*/
            	if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
            		//Class.forName(driverClassName).newInstance();
            		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            	}
                co.setCon(DriverManager.getConnection(URL, user, password));
            }
//      log("连接池" + name+"创建一个新的连接");
        } catch (Exception e) {
            System.out.println("无法创建下列URL的连接: " + URL);
            e.printStackTrace();
            return null;
        }
        co.setInUse(false);
        co.setUseCount(0);
        co.setLastAccess(0);
        co.setStartTime(System.currentTimeMillis());
        poolConnections.addElement(co);

        return co;
    }

    /**
     * 创建新的连接池
     *
     * @param name                   连接池名字
     * @param URL                    数据库的JDBC URL
     * @param user                   数据库帐号,或 null
     * @param password               密码,或 null
     * @param minConn                此连接池至少保持的最小连接数
     * @param maxConn                此连接池允许建立的最大连接数
     * @param connectCheckOutTimeout 此连接从连接池取出而且没有返回的最大允许时间，超过时间，认为此连接已无效，从连接池中删除
     * @param connectUseCount        此连接允许使用的最大次数
     * @param connectUseTimeout      此连接允许空运行的最长时间，超过时间，关闭
     * @param logFile                日志文件的路径（如果不写日志文件，可以不需要该参数）
     */
    public DBConnectionPool(String name, String URL, String user, String password, int minConn, int maxConn, int useTimeout, int useCount, int checkOutTimeout, String logFile) {
        this.name = name;
        this.URL = URL;
        this.user = user;
        this.password = password;
        this.minConn = minConn;
        this.maxConn = maxConn;
        this.connectCheckOutTimeout = checkOutTimeout;
        this.connectUseCount = useCount;
        this.connectUseTimeout = useTimeout;
        this.numCheckOut = 0;
        this.numRequests = 0;
        this.numWaits = 0;
//        try {
//            log = new PrintWriter(new FileWriter(logFile, true), true);
//        } catch (IOException e) {
//            System.err.println("无法打开日志文件: " + logFile);
//            log = new PrintWriter(System.err);
//        }

/*      try
      {
//        Class.forName(driverClassName).newInstance();
        Connection con = DriverManager.getConnection(URL,user,password);
        if (con.toString()!=null)
        {
          System.out.println("ok yet");
        }
        else
        {
          System.out.println("not ok yet");
        }
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
*/
        poolConnections = new Vector();
        try {
            fillPool(this.minConn);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("无法创建连接池: " + name);
        }
        reaper = new ConnectionReaper(this);
        reaper.start();

    }

    /**
     * 检查连接池中的连接
     * Check  all connections to make sure they haven't:
     * 1) gone idle for too long<br>
     * 2) been checked out by a thread for too long (cursor leak)<br>
     */

    public synchronized void reapIdleConnections() {
        long now = System.currentTimeMillis();
        long idleTimeout = now - (connectUseTimeout * 1000);
//    long useTimeout = now - (connectUseTimeout * 1000);
        long checkoutTimeout = now - (connectCheckOutTimeout * 1000);
        for (Enumeration e = poolConnections.elements(); e.hasMoreElements();) {
            ConnectionObject co = (ConnectionObject) e.nextElement();
            if (co.isInUse() && (co.getLastAccess() < checkoutTimeout)) {
                System.out.println("ConnectionPool " + name + " Warning : found timeout connection\n");
                removeConnection(co);
                notifyAll();
            } else {
                if (co.getLastAccess() < idleTimeout) {
                    if (co.isInUse()) {
                        removeConnection(co);
                        notifyAll();
                    }
                }
            }
        }
        // Now ensure that the pool is still at it's minimum size
        try {
            if (poolConnections != null) {
                if (poolConnections.size() < minConn) {
                    fillPool(minConn);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 将连接池填充到指定大小
     * <p>Brings the pool to the given size
     */
    private synchronized void fillPool(int size) throws Exception {
        // Loop while we need to create more connections
        int i = 0;
        System.out.println("*********数据连接池大小******************"+poolConnections.size());
        
        
        while (poolConnections.size() < size) {
            ConnectionObject co = newConnection();
            // Do some sanity checking on the first connection in the pool
            if (poolConnections.size() == 1) {
                // Get the maximum number of simultaneous connections
                // as reported by the JDBC driver
                java.sql.DatabaseMetaData md = co.getCon().getMetaData();
//        System.out.println(md.getMaxConnections());
                if ((md.getMaxConnections() != 0) && (maxConn > md.getMaxConnections()))
                    maxConn = md.getMaxConnections();
            }
            // Give a warning if the size of the pool will exceed
            // the maximum number of connections allowed by the
            // JDBC driver
            i++;
            //System.out.println(i);
            if ((maxConn > 0) && (size > maxConn)) {
                System.out.println("WARNING: Size of pool will exceed safe maximum of " + maxConn);
            }
            if (i == size)
                break;
        }
    }

    public Vector getPoolConnections() {
        return poolConnections;
    }
}
