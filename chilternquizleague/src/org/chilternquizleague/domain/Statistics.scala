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
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import scala.collection.JavaConversions._
import com.googlecode.objectify.annotation.Stringify
import org.chilternquizleague.domain.util.IntStringifier

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
    stats.cumuPointsFor += pointsFor
    stats.cumuPointsAgainst  += pointsAgainst
    stats.cumuPointsDifference += stats.pointsDifference
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
    val week = cal.getWeekYear + (cal.get(Calendar.YEAR) *100)
    
    if(alwaysNew){
      weekStats put (week, WeekStats(week))
      weekStats(week)
    }
    else weekStats.getOrElseUpdate(week, WeekStats(week))

  }
  

}

object Statistics{
  
  def apply(team:Team, season:Season) = {
    val stats = new Statistics
    stats.team  = team
    stats.season = season
    stats
  }
}

class SeasonStats{
  var currentLeaguePosition = 0
  var runningPointsFor = 0
  var runningPointsAgainst = 0
  var runningPointsDifference = 0
}

class WeekStats{
  var weekNo = 0
  var leaguePosition = 0
  var pointsFor = 0
  var pointsAgainst = 0
  var pointsDifference = 0
  var cumuPointsFor = 0
  var cumuPointsAgainst = 0
  var cumuPointsDifference = 0

}

object WeekStats{
  def apply(weekNo:Int) = {
    val stats = new WeekStats
    stats.weekNo = weekNo
    stats
  }
}


