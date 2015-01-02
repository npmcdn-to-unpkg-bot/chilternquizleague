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

class StatsWorker extends HttpServlet{
  
  val LOG:Logger = Logger.getLogger(this.getClass.getName)
  
  override def doPost(req:HttpServletRequest, resp:HttpServletResponse){
    
	LOG.fine(s"task arrived : ${req.getParameter("result")}")
    
    for{
      resText <- req.parameter("result")
      r = JacksonUtils.safeMapper.readValue(resText, classOf[Result])
      season <- entity(req.id("seasonId"), classOf[Season])
      homeStats = stats(r.fixture.home,season)
      awayStats = stats(r.fixture.away,season)
    } 
    {
	  homeStats.addWeekStats( r.fixture.start, r.homeScore , r.awayScore )
	  save(homeStats)
	  awayStats.addWeekStats(r.fixture.start, r.awayScore , r.homeScore )
	  save(awayStats)
	  
	  val c:LeagueCompetition = season.competition(CompetitionType.LEAGUE)
	  
	  for(t <- c.leagueTables;row <- t.rows ){
	    val s = stats(row.team, season )
	    s.addLeaguePosition(r.fixture.start, leaguePosition(row.team,season))
	    save(s)
	  }
	  
    }
  }
  
  override def doGet(req:HttpServletRequest, resp:HttpServletResponse) = doPost(req,resp)
  
  private def stats(team:Team,season:Season):Statistics = {
    
    Statistics.get(team,season)
    
  }
  
  private def leaguePosition(team:Team, season:Season):Int = {
    import org.chilternquizleague.util.StringUtils.StringImprovements
    val c:LeagueCompetition = season.competition(CompetitionType.LEAGUE)
    
    val res = for{
      l <- c.leagueTables
      row <- l.rows if row.team.getKey.getId == team.id
      pos = row.position.replace("=", "").toIntOpt.getOrElse(l.rows.indexOf(row)+1)
    }
    yield{
      pos
    }
    
    res.head
  }
}

object HistoricalStatsAggregator{
  
  def perform() = {
	  
    for{
      g <- Application.globalData
      c:LeagueCompetition = g.currentSeason.competition(CompetitionType.LEAGUE)
      r <- c.results.sortBy(_.date)
      result <- r.results
    }
    {
      val queue = QueueFactory.getQueue("stats");
       queue.add(withUrl("/tasks/stats").param("result", JacksonUtils.safeMapper.writeValueAsString(result)).param("seasonId", g.currentSeason.getKey.getId.toString));

    }
    Some("Finished")
  }

}