/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import org.apache.log4j.Logger;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InitHibernateJNDI  {

	private Context ctx;
private SessionFactory sessionFactory;
	/**
	 * 
	 */
	Logger logger=Logger.getLogger(InitHibernateJNDI.class);
	
	public InitHibernateJNDI() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.apache.struts.action.PlugIn#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		if(ctx!=null){
			try{
				ctx.unbind("HibernateSessionFactory");
			}
			catch(NamingException e){
				throw new RuntimeException("unbind sessionFactory from JNDI Exception "+e.getMessage());
			}
		}
		if(sessionFactory!=null){
				try{
					sessionFactory.close();
				}
				catch(HibernateException e){
					throw new RuntimeException("Close SessionFactory Exception "+ e.getMessage());
				}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.struts.action.PlugIn#init(org.apache.struts.action.ActionServlet, org.apache.struts.config.ModuleConfig)
	 */
	public void init()
		throws Exception {
		// TODO Auto-generated method stub
		try{
			sessionFactory=new Configuration().configure().buildSessionFactory();
			
		}
		catch(HibernateException e){
			e.printStackTrace();
			throw new RuntimeException("Can not build SessionFactory "+e.getMessage());}
		try{
			ctx=new InitialContext();
			ctx.bind("HibernateSessionFactory",sessionFactory);
			
			System.out.println("-----------------JNDI bind success----------");
			
			logger.info("JNDI bind success");
		}catch(NamingException e){
			logger.error("JNDI bind failed"+e);
			e.printStackTrace();
			throw new RuntimeException("bind sessionFactory to JNDI Exception "+e.getMessage());}
			catch(Exception e){
				e.printStackTrace();
			}
	}


}
