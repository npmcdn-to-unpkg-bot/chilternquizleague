package org.chilternquizleague.web

import javax.servlet.ServletContextListener
import scala.collection.JavaConversions._
import org.chilternquizleague.domain._
import org.chilternquizleague.domain.security._
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
  import com.googlecode.objectify.ObjectifyService.register

  override def contextDestroyed(evt: javax.servlet.ServletContextEvent): Unit = {}
  override def contextInitialized(evt: javax.servlet.ServletContextEvent): Unit = {

    register(classOf[GlobalApplicationData])
    register(classOf[User])
    register(classOf[Team])
    register(classOf[Results])
    register(classOf[Fixtures])
    register(classOf[Venue])
    register(classOf[Competition])
    register(classOf[LeagueCompetition])
    register(classOf[BeerCompetition])
    register(classOf[CupCompetition])
    register(classOf[PlateCompetition])
    register(classOf[BuzzerCompetition])
    register(classOf[IndividualCompetition])
    register(classOf[CommonText])
    register(classOf[Season])
    register(classOf[Statistics])
    register(classOf[LogonToken])
    register(classOf[SessionToken])
    

  }
}

object Application {

  private var globalApplicationDataId: Option[Long] = None

  def globalData = entity(globalApplicationDataId, classOf[GlobalApplicationData])

  def init() = {

    val list = entities[GlobalApplicationData]()
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
  val LOG: Logger = Logger.getLogger(classOf[URLRewriteFilter].getName())
}

class URLRewriteFilter extends Filter {
  import URLRewriteFilter.LOG
  var context: ServletContext = null

  override def doFilter(arg0: ServletRequest, arg1: ServletResponse,
    arg2: FilterChain): Unit = {
    arg0.getRequestDispatcher("/index.html").forward(
      arg0, arg1)

  }

  override def init(arg0: FilterConfig): Unit = {
    context = arg0.getServletContext();
    return
  }

  override def destroy() = {}

}

class MaintainURLRewriteFilter extends Filter {
  override def doFilter(arg0: ServletRequest, arg1: ServletResponse,
    arg2: FilterChain): Unit = {

    arg0.getRequestDispatcher("/maintain/index.html").forward(
      arg0, arg1)

  }

  override def init(arg0: FilterConfig): Unit = {}

  override def destroy() = {}

}

class PassThroughFilter extends Filter {
  override def doFilter(arg0: ServletRequest, arg1: ServletResponse,
    arg2: FilterChain): Unit = {
    val req = arg0.asInstanceOf[HttpServletRequest]
    
    arg0.getRequestDispatcher(req.getRequestURI).forward(arg0, arg1)

  }

  override def init(arg0: FilterConfig): Unit = {}

  override def destroy() = {}
}
