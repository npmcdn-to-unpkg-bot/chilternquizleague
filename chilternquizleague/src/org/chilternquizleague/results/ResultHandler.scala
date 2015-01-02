package org.chilternquizleague.results

import org.chilternquizleague.domain.Result
import org.chilternquizleague.domain.CompetitionType
import com.googlecode.objectify.ObjectifyService.ofy
import com.googlecode.objectify.VoidWork
import org.chilternquizleague.domain._
import scala.collection.JavaConversions._
import com.googlecode.objectify.Key
import org.chilternquizleague.util.Storage._
import org.chilternquizleague.domain.util.RefUtils._
import com.google.appengine.api.taskqueue.Queue
import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions.Builder._
import com.google.gson.Gson
import com.fasterxml.jackson.databind.ObjectMapper
import org.chilternquizleague.util.JacksonUtils

class ResultHandler(result: Result, email: String, seasonId: Long, competitionType: CompetitionType) {

  private def commit(): Unit = {

    Option(entityList(classOf[User], ("email", email)).head).foreach {user:User =>

      result.firstSubmitter = user;

      entity(Some(seasonId), classOf[Season]).foreach {

        season =>
          {
            ofy.transact(new VoidWork() {

              override def vrun: Unit = {

                Option[TeamCompetition](season.competition(competitionType)).foreach {
                  c => 	val r = c.addResult(result)
                		save(r)  
                    	save(c)
                    
                }
              }

            })
          }
      if(competitionType == CompetitionType.LEAGUE){    
       val queue:Queue = QueueFactory.getQueue("stats");
       queue.add(withUrl("/tasks/stats").param("result", JacksonUtils.safeMapper.writeValueAsString(result)).param("seasonId", seasonId.toString));
      }
      }

      return
    }

  }
}

object ResultHandler {

  def apply(result: Result, email: String, seasonId: Long, competitionType: CompetitionType): Unit = {

    new ResultHandler(result, email, seasonId, competitionType).commit
  }

}