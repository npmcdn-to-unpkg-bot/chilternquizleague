package org.chilternquizleague.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.Ref
import java.util.{List => JList}
import java.util.{Map => JMap}
import java.util.ArrayList
import org.chilternquizleague.domain.util.ObjectifyAnnotations._
import org.chilternquizleague.domain.util.RefUtils._
import org.chilternquizleague.util.Storage._
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import scala.collection.JavaConversions._
import com.googlecode.objectify.annotation.Stringify
import org.chilternquizleague.domain.util.IntStringifier
import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.logging.Logger

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Entity
class Statistics extends BaseEntity{
  

  @Index
  var team:Ref[Team] = null

  @Index
  var season:Ref[Season] = null
  
  var seasonStats = new SeasonStats
  
  @Stringify(classOf[IntStringifier])
  var weekStats = new HashMap[Int,WeekStats]
  
  def addWeekStats(date:Date,pointsFor:Int,pointsAgainst:Int):Unit = {
    
   val stats = statsForDate(date)
   
    stats.pointsFor  = pointsFor
    stats.pointsAgainst  = pointsAgainst
    stats.pointsDifference  = pointsFor - pointsAgainst
    stats.cumuPointsFor = seasonStats.runningPointsFor + pointsFor
    stats.cumuPointsAgainst = seasonStats.runningPointsAgainst + pointsAgainst
    stats.cumuPointsDifference = seasonStats.runningPointsDifference + stats.pointsDifference
    seasonStats.runningPointsFor  = stats.cumuPointsFor
    seasonStats.runningPointsAgainst = stats.cumuPointsAgainst 
    seasonStats.runningPointsDifference = stats.cumuPointsDifference 
     
  }
  
  def addLeaguePosition(date:Date, leaguePosition:Int) = {
    val stats = statsForDate(date,false)
    
    stats.leaguePosition = leaguePosition
    seasonStats.currentLeaguePosition = leaguePosition
  }
  
  def statsForDate(date:Date, alwaysNew:Boolean = true) = {
    val cal = Calendar.getInstance
    cal.setTime(date)
    val week = makeWeek(date)
    
    if(alwaysNew){
      weekStats put (week, WeekStats(date))
      weekStats(week)
    }
    else weekStats.getOrElseUpdate(week, WeekStats(date))

  }
  
  def makeWeek(date:Date) = {
     val cal = Calendar.getInstance
    cal.setTime(date)
    (cal.get(Calendar.YEAR) *100) + cal.get(Calendar.WEEK_OF_YEAR)
  }
  

}

object Statistics{
  
  def apply(team:Team, season:Season) = {
    val stats = new Statistics
    stats.team  = team
    stats.season = season
    stats
  }
  
  def get(team:Team,season:Season):Statistics = {
    
    val statSet = entities[Statistics]().filter(s=>s.team.id == team.id && s.season.id == season.id )
    
    statSet match {
      
      case Nil => {val stats = Statistics(team,season)
    		  save(stats)
    		  stats
      }
      case _ => statSet.head
    }
  }
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class SeasonStats{
  var currentLeaguePosition = 0
  var runningPointsFor = 0
  var runningPointsAgainst = 0
  var runningPointsDifference = 0
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class WeekStats{
  var date:Date = null
  var leaguePosition = 0
  var pointsFor = 0
  var pointsAgainst = 0
  var pointsDifference = 0
  var cumuPointsFor = 0
  var cumuPointsAgainst = 0
  var cumuPointsDifference = 0

}

object WeekStats{
  def apply(date:Date) = {
    val stats = new WeekStats
    stats.date = date
    stats
  }
}


