package org.chilternquizleague.web;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.chilternquizleague.domain.GlobalApplicationData;

import com.googlecode.objectify.ObjectifyService;

public class AppStartListener implements ServletContextListener{

	public static long globalApplicationDataId;
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		final long id;
		
		final List<GlobalApplicationData> list = ObjectifyService.ofy().load().type(GlobalApplicationData.class).list();
		
		if(list.isEmpty()){
			
			id = ObjectifyService.ofy().save().entity(new GlobalApplicationData()).now().getId();
			
		}
		else
		{
			id = list.iterator().next().getId();
		}
		
		globalApplicationDataId = id;
	}

}
