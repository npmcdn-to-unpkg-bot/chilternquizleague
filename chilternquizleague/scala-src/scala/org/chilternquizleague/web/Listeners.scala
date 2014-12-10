package scala.org.chilternquizleague.web

import javax.servlet.ServletContextListener
import scala.collection.JavaConversions._
import org.chilternquizleague.domain._
import scala.org.chilternquizleague.util.Storage._

class EntityRegistrationListener extends ServletContextListener {
  import com.googlecode.objectify.ObjectifyService
  
  def contextDestroyed(evt: javax.servlet.ServletContextEvent): Unit = {}
  def contextInitialized(evt: javax.servlet.ServletContextEvent): Unit = {

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

object Application{
  
  var globalApplicationDataId:Option[Long] = None
  
}

class ApplicationStartListener extends ServletContextListener {

  def contextDestroyed(evt: javax.servlet.ServletContextEvent): Unit = {}
  def contextInitialized(evt: javax.servlet.ServletContextEvent): Unit = {
		
		val list = entityList(classOf[GlobalApplicationData])
			
		Application.globalApplicationDataId = list match {
		  case Nil => Some(save(new GlobalApplicationData()).getId())
		  case _ => Some(list.head.getId)
		}

  }
}