package org.chilternquizleague;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.chilternquizleague.domain.Competition;
import org.chilternquizleague.domain.Fixture;
import org.chilternquizleague.domain.LeagueResults;
import org.chilternquizleague.domain.LeagueTable;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.Team;
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
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        ObjectifyService.register(LeagueTable.class);
        ObjectifyService.register(Competition.class);
        ObjectifyService.register(LeagueResults.class);
        ObjectifyService.register(Season.class);
        ObjectifyService.register(Team.class);
        ObjectifyService.register(Venue.class);
        ObjectifyService.register(Fixture.class);

    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
