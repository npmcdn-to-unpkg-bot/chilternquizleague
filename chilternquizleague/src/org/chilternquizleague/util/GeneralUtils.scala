package org.chilternquizleague.util

import org.chilternquizleague.domain._
import org.chilternquizleague.domain.util.RefUtils._
import org.chilternquizleague.util.Storage._
import scala.collection.JavaConversions._
import java.util.logging.Logger


object UserUtils{
  
  def userTeamForEmail(email:Option[String]):Option[(User,Team)] = {
    
      for{
        e <- email.map(_.trim())
        t <- entityList(classOf[Team]).find(_.users.exists(_.email equalsIgnoreCase e))
        u <- t.users.find (_.email equalsIgnoreCase e )
      }yield (u,t)    
  }
}

object LogUtils{
  
  val LOG = Logger.getLogger(this.getClass.getName)
  
  def logTime[T](f: () => T, message: String = "method"): T = {
    val now = System.currentTimeMillis()
    val res = f()
    LOG.fine(s"$message took ${System.currentTimeMillis - now} millis")
    res
  }
}