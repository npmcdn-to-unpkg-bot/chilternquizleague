package scala.org.chilternquizleague.web

import javax.servlet.ServletContextListener
import scala.collection.JavaConversions._
import com.googlecode.objectify.ObjectifyService
import org.chilternquizleague.domain._

class EntityRegistrationListener extends ServletContextListener {

  def contextDestroyed(x$1: javax.servlet.ServletContextEvent): Unit = {}
  def contextInitialized(x$1: javax.servlet.ServletContextEvent): Unit = {

    ObjectifyService.register(classOf[GlobalApplicationData]);
    ObjectifyService.register(classOf[User]);
    ObjectifyService.register(classOf[Team]);
    ObjectifyService.register(classOf[Results]);
    ObjectifyService.register(classOf[Fixtures]);
    ObjectifyService.register(classOf[Venue]);
    ObjectifyService.register(classOf[Competition]);
    ObjectifyService.register(classOf[LeagueCompetition]);
    ObjectifyService.register(classOf[BeerCompetition]);
    ObjectifyService.register(classOf[CupCompetition]);
    ObjectifyService.register(classOf[PlateCompetition]);
    ObjectifyService.register(classOf[BuzzerCompetition]);
    ObjectifyService.register(classOf[GlobalText]);
    ObjectifyService.register(classOf[Season]);

  }
}

object AppStartListener{
  
  var globalApplicationDataId:Option[Long] = None
  
}

class AppStartListener extends ServletContextListener {

  def contextDestroyed(x$1: javax.servlet.ServletContextEvent): Unit = {}
  def contextInitialized(x$1: javax.servlet.ServletContextEvent): Unit = {
		
		val list = ObjectifyService.ofy().load().`type`(classOf[GlobalApplicationData]).list().toList;
			
		AppStartListener.globalApplicationDataId = list match {
		  case Nil => Some(ObjectifyService.ofy.save.entity(new GlobalApplicationData()).now.getId)
		  case _ => Some(list.head.getId)
		}

  }
}