/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class A_BaseMap {

	/**
	 * 
	 */
	public A_BaseMap() {
		super();
		// TODO Auto-generated constructor stub
	}

	private Session session;
		// 数据库事务处理器
		private Transaction transaction;

		/**
		 * 初始化数据库连接事务
		 * @return 初始化完成的数据库连接
		 * @throws HibernateException
		 */
		public Session beginTransaction() throws HibernateException {
			session = HibernateUtil.currentSession();
			transaction = session.beginTransaction();
			return session;
		}

		/**
		 * 完成一个数据库事务
		 * @param commit 是否提交事务，true时提交，false时向数据库发起回滚（rollback）
		 * @throws HibernateException
		 */
		public void endTransaction(boolean commit) throws HibernateException {
			try{
			
			if (commit) {
				transaction.commit();
			} else {
				transaction.rollback();
			}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				HibernateUtil.closeSession();
			}
			
		}

}
