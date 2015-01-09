package org.chilternquizleague.web

import javax.servlet.ServletContextListener
import scala.collection.JavaConversions._
import org.chilternquizleague.domain._
import org.chilternquizleague.util.Storage._
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import java.net.URI
import javax.servlet.ServletContext
import java.util.logging.Logger
import java.util.logging.Level
import javax.servlet.FilterConfig
import javax.servlet.Filter

class EntityRegistrationListener extends ServletContextListener {
  import com.googlecode.objectify.ObjectifyService
  
  override def contextDestroyed(evt: javax.servlet.ServletContextEvent): Unit = {}
  override def contextInitialized(evt: javax.servlet.ServletContextEvent): Unit = {

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
    ObjectifyService.register(classOf[IndividualCompetition]);
    ObjectifyService.register(classOf[CommonText]);
    ObjectifyService.register(classOf[Season]);
    ObjectifyService.register(classOf[Statistics]);

  }
}

object Application{
  
  private var globalApplicationDataId:Option[Long] = None
  
  def globalData = entity(globalApplicationDataId, classOf[GlobalApplicationData])

  def init() = {
    
    val list = entityList(classOf[GlobalApplicationData])
    globalApplicationDataId = list match {
		  case Nil => Some(save(new GlobalApplicationData()).getId)
		  case _ => Some(list.head.id)
		}
  }
}



class ApplicationStartListener extends ServletContextListener {

  override def contextDestroyed(evt: javax.servlet.ServletContextEvent): Unit = {}
  override def contextInitialized(evt: javax.servlet.ServletContextEvent): Unit = Application.init
  
}

object URLRewriteFilter {
  val LOG:Logger = Logger.getLogger(classOf[URLRewriteFilter].getName())
}

class URLRewriteFilter extends Filter{
   import URLRewriteFilter.LOG 
  var context:ServletContext = null	
  
  override def doFilter(arg0:ServletRequest, arg1:ServletResponse,
			 arg2:FilterChain):Unit = {
		 val request = arg0.asInstanceOf[HttpServletRequest]

		try {
			val pathInfo = new URI(request.getRequestURI()).getPath();

			pathInfo match {
			  
			  case a if ((a == null ||(a.startsWith("/tasks")) || (a.contains("/_ah")) && !a.contains("."))) => {arg2.doFilter(arg0, arg1)}
			  case a if a.startsWith("/maintain") => {request.getRequestDispatcher("/maintain.html").forward(
							arg0, arg1)}
			  case a if context.getRealPath(request.getPathInfo()) == null => {request.getRequestDispatcher("/index.html").forward(
								arg0, arg1)}
			
			  case _ => {arg2.doFilter(arg0, arg1)}
			}

		} catch{
			
		case e: Exception => LOG.log(Level.WARNING, "Error in redirect filter",e)
		}

		
	}

	
	override def init(arg0:FilterConfig ):Unit= {
		context = arg0.getServletContext();
		return
	}
	
	override def destroy() = {}
  
  
}