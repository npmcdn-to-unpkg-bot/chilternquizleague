package org.chilternquizleague.util

import org.chilternquizleague.domain._
import org.chilternquizleague.domain.util.RefUtils._
import org.chilternquizleague.util.Storage._
import scala.collection.JavaConversions._


object UserUtils{
  
  def userTeamForEmail(email:Option[String]):Option[(User,Team)] = {
    
      for{
        e <- email.map(_.trim())
        t <- entityList(classOf[Team]).find(_.users.exists(_.email equalsIgnoreCase e))
        u <- t.users.find (_.email equalsIgnoreCase e )
      }yield (u,t)    
  }
}