package org.chilternquizleague;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.chilternquizleague.domain.LeagueTable;

import com.googlecode.objectify.ObjectifyService;

/**
 * Application Lifecycle Listener implementation class EntityRegistrationListener
 *
 */
public class EntityRegistrationListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public EntityRegistrationListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        ObjectifyService.register(LeagueTable.class);
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
