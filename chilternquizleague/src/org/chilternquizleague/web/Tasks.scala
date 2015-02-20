package org.chilternquizleague.web

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.chilternquizleague.util.HttpUtils.RequestImprovements
import com.fasterxml.jackson.databind.ObjectMapper
import org.chilternquizleague.domain.Result
import org.chilternquizleague.util.Storage._
import com.googlecode.objectify.ObjectifyService.ofy
import scala.collection.JavaConversions._
import org.chilternquizleague.domain.Season
import org.chilternquizleague.domain.Team
import org.chilternquizleague.domain.util.RefUtils._
import org.chilternquizleague.domain.Statistics
import org.chilternquizleague.domain.LeagueCompetition
import org.chilternquizleague.domain.CompetitionType
import org.chilternquizleague.domain.LeagueTable
import scala.collection.mutable.Buffer
import org.chilternquizleague.util.JacksonUtils
import java.util.logging.Logger
import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions.Builder._
import scala.collection.JavaConversions._
import java.util.ArrayList
import com.googlecode.objectify.VoidWork
import org.chilternquizleague.domain.security.LogonToken
import org.chilternquizleague.domain.security.SessionToken

class StatsQueueHandler extends HttpServlet {
  val LOG: Logger = Logger.getLogger(this.getClass.getName)
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {

    LOG.fine(s"task arrived : ${req.getParameter("result")}")

    for {
      resText <- req.parameter("result")
      r = JacksonUtils.safeMapper.readValue(resText, classOf[Result])
      season <- entity(req.id("seasonId"), classOf[Season])
    } {
      StatsWorker.perform(r, season)
    }
  }
}

object StatsWorker {

  def perform(result: Result, season: Season) = new StatsWorker(result, season, season.competition(CompetitionType.LEAGUE)).doIt
}

class StatsWorker(result: Result, season: Season, competition: LeagueCompetition) {

  val LOG: Logger = Logger.getLogger(this.getClass.getName)

  def doIt = {

    LOG.warning(s"Building stats for  ${result.fixture.home.shortName} vs ${result.fixture.away.shortName} on ${result.fixture.start}")
    val homeStats = stats(result.fixture.home, season)
    val awayStats = stats(result.fixture.away, season)

    homeStats.addWeekStats(result.fixture.start, result.homeScore, result.awayScore)
    save(homeStats)
    awayStats.addWeekStats(result.fixture.start, result.awayScore, result.homeScore)
    save(awayStats)

    for (t <- competition.leagueTables; row <- t.rows) {
      val s = stats(row.team, season)
      s.addLeaguePosition(result.fixture.start, leaguePosition(row.team, competition))
      save(s)
    }

  }

  private def stats(team: Team, season: Season): Statistics = {

    Statistics.get(team, season)

  }

  private def leaguePosition(team: Team, competition: LeagueCompetition): Int = {
    import org.chilternquizleague.util.StringUtils.StringImprovements

    val res = for {
      l <- competition.leagueTables
      row <- l.rows if row.team.getKey.getId == team.id
      pos = String.valueOf(row.position).replace("=", "").toIntOpt.getOrElse(l.rows.indexOf(row) + 1)
    } yield {
      pos
    }

    res.head
  }
}

object HistoricalStatsAggregator {

  def perform(season: Season) = {

    val seasonStats = new ArrayList(entityList(classOf[Statistics], ("season", season)))

    for (s <- seasonStats) {

      transaction(() => delete(s))

    }

    val c: LeagueCompetition = season.competition(CompetitionType.LEAGUE)
    val dummyComp = c.copyAsInitial

    for {
      r <- c.results.sortBy(_.date)
      result <- r.results
    } {
      dummyComp.addResult(result)

      new StatsWorker(result, season, dummyComp).doIt
    }
    entityList(classOf[Statistics])
  }

}

class TokenQueueHandler extends HttpServlet {
  val LOG: Logger = Logger.getLogger(this.getClass.getName)
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {

    LOG.fine("Token task arrived")

    LogonToken.cleanUp()
    SessionToken.cleanUp()
    
  }
}