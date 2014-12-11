package org.chilternquizleague.results

import org.chilternquizleague.domain.Result
import org.chilternquizleague.domain.CompetitionType
import com.googlecode.objectify.ObjectifyService.ofy
import com.googlecode.objectify.VoidWork
import org.chilternquizleague.domain._
import scala.collection.JavaConversions._
import com.googlecode.objectify.Key
import org.chilternquizleague.util.Storage._

class ResultHandler(result: Result, email: String, seasonId: Long, competitionType: CompetitionType) {

  private def commit(): Unit = {

    Option(entityList(classOf[User], ("email", email)).head).foreach {

      result.setFirstSubmitter(_);

      entity(Some(seasonId), classOf[Season]).foreach {

        season =>
          {
            ofy.transact(new VoidWork() {

              override def vrun: Unit = {

                Option[TeamCompetition](season.getCompetition(competitionType)).foreach {
                  c =>
                    {
                      c.addResult(result)
                      save(season)
                    }
                }
              }

            })
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