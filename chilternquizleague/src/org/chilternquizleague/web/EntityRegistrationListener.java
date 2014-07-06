package org.chilternquizleague.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.domain.LeagueCompetition;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.User;
import org.chilternquizleague.domain.Venue;

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

    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	
        ObjectifyService.register(LeagueCompetition.class);
        ObjectifyService.register(Season.class);
        ObjectifyService.register(Team.class);
        ObjectifyService.register(Venue.class);
        ObjectifyService.register(User.class);
        ObjectifyService.register(GlobalApplicationData.class);
        

    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {

    }
	
}
