package org.chilternquizleague.domain

import com.googlecode.objectify.annotation._
import com.googlecode.objectify.annotation.Cache
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.googlecode.objectify.annotation.Index
import java.util.{List => JList}
import java.util.{Map => JMap}
import com.googlecode.objectify.Ref
import com.googlecode.objectify.Key
import java.util.ArrayList
import scala.beans.BeanProperty
import java.util.HashMap
import com.googlecode.objectify.annotation.Ignore
import scala.collection.JavaConversions._
import java.util.Calendar
import com.googlecode.objectify.annotation.Stringify
import org.chilternquizleague.domain.util.CompetitionTypeStringifier
import org.chilternquizleague.domain.util.JacksonAnnotations._
import org.chilternquizleague.domain.util.ObjectifyAnnotations._
import org.chilternquizleague.util.DateUtils._
import scala.collection.immutable.HashSet
import java.util.Date
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.googlecode.objectify.annotation.Subclass
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(Array("parent"))
class BaseEntity{
  @Id
  var id:java.lang.Long = null
  
  var retired:Boolean = false
  
  @Ignore
  @JsonProperty
  val refClass = getClass.getSimpleName
  
  @Ignore
  @JsonIgnore
  private var _key:String = null
  
  @JsonGetter("key")
  def key:String = {_key = if(_key == null && id != null) (Key.create(this).getString) else _key;_key}  
  @JsonSetter("key")
  def key_dummy(key:String) = {}
  protected def key_= (key:String){_key = key}
  
  //def same(other:BaseEntity):Boolean = this == other || this.key == other.key
}


@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Entity
class User extends BaseEntity{
  
  @Index
  var name:String = null
  @Index
  var email:String  = null
  
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Entity
class Venue extends BaseEntity{
  var name:String = null
  var address:String = null
  var postcode:String = null
  var website:String = null
  var phone:String = null
  var email:String = null
  var imageURL:String = null
}
@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Entity
class Team extends BaseEntity{

	var name:String = null
  	var shortName:String = null
  	var rubric:Text = new Text
	
  	@JsonProperty("venue")
  	@Load
	private var venueRef:Ref[Venue] = null
	
	@JsonProperty("users")
	@Load
	private var userRefs:JList[Ref[User]] = new ArrayList

	def users:JList[Ref[User]] = userRefs
	def venue:Ref[Venue] = venueRef
	
	lazy val emailName = if(shortName == null) null else shortName.replace(' ', '.').toLowerCase;

	def same(other:Team) = this == other || this.id == other.id
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Entity(name="GlobalText")
@Cache
class CommonText extends BaseEntity{
  
  var name:String = null

  @JsonIgnore
  var text:JMap[String,TextEntry] = new HashMap
  
  @Ignore
  def text(key:String):String = text.getOrElse(key, new TextEntry(key, "No text found for '" + key +"'")).text 
  
  def getEntries:JList[TextEntry] = new ArrayList(text.values)
  def setEntries(entries:JList[TextEntry]):Unit = entries.foreach {t => text put (t.name, t)}
  
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Entity
class GlobalApplicationData extends BaseEntity{

  var frontPageText:String = null
  var leagueName:String = null
  var senderEmail:String = null

  @Load
  var currentSeason:Ref[Season] = null
  @Load
  var globalText:Ref[CommonText] = null

  @Load
  var emailAliases:JList[EmailAlias] = new ArrayList

}


object Season{
  val types = HashSet(CompetitionType.LEAGUE,CompetitionType.BEER, CompetitionType.CUP, CompetitionType.PLATE);
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Entity
class Season extends BaseEntity{
  import org.chilternquizleague.domain.util.RefUtils._
  @Index
  var startYear:Int = Calendar.getInstance.get(Calendar.YEAR)
  @Index
  var endYear:Int = startYear + 1
  @Load
  @Stringify(classOf[CompetitionTypeStringifier])
  var competitions:JMap[CompetitionType, Ref[Competition]] = new HashMap
  
  @Ignore
  lazy val description = s"$startYear / $endYear"
  
  def teamCompetitions:List[TeamCompetition] = competitions.values.filter(c=>Season.types.contains(c.`type`)).map(_.get).asInstanceOf[List[TeamCompetition]]
  def competition[T <: Competition](compType:CompetitionType) = compType.castTo(competitions.get(compType)).asInstanceOf[T]

}

object Results{
  def apply(template:Results) = {
    val copy = new Results()
	copy.key = template.key;
	copy.date = template.date;
	copy.description = template.description;
	copy
  }
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Entity
class Results extends BaseEntity{
  import org.chilternquizleague.domain.util.RefUtils._
  var date:Date = new Date
  var description:String = null
  var results:JList[Result] = new ArrayList
  
  @Parent
  var parent:Ref[BaseEntity] = null
  
  def findRow(fixture:Fixture) = results.toList.find(_.fixture same fixture) 
  
  /**
   * find a row for which either home or away team matches {team}
   */
  def findRow(team:Team) = results.toList.find( t => (t.fixture.home same team) || (t.fixture.away same team))
  def addResult(incoming:Result) = {
    val row = findRow(incoming.fixture)
    row match {
  		case None => results.add(incoming)
  		case a => {
  		  incoming.reports.foreach(r=>a.foreach(_.reports.add(r)))
  		  false
  		}
    }
  }
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class Result{
	var homeScore:Int = 0
	var awayScore:Int = 0
	var fixture:Fixture = null
	var reports:JList[Report] = new ArrayList

	@JsonIgnore
	@Ignore
	var firstSubmitter:Ref[User]= null
  
}

object Fixtures{
  
  def apply(template:Fixtures) = {
    val copy = new Fixtures
    copy.start = template.start 
    copy.end  = template.end
    copy.competitionType  = template.competitionType 
    copy.description  = template.description 
    copy
  }
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Entity
class Fixtures extends BaseEntity{
    import org.chilternquizleague.domain.util.RefUtils._
  var start:Date = null
	var end:Date = null
	var competitionType:CompetitionType = null
	var description:String = null
	var fixtures:JList[Fixture] = new ArrayList
	
	@Parent
	var parent:Ref[BaseEntity] = null
  
  /**
   * find a row for which either home or away team matches {team}
   */
  def findRow(team:Team) = fixtures.toList.find( f => (f.home same team) || (f.away same team))
  
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class Fixture{
  	var start:Date = null
	var end:Date = null
	var home:Ref[Team] = null
	var away:Ref[Team] = null
	
	def same(other:Fixture) = sameDay(start, other.start) && home.getKey().equivalent(other.home.getKey())
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
@Entity
@Cache
abstract class Competition(
    var `type`:CompetitionType,
    var description:String,
    var startTime:String,
    var endTime:String,
    var subsidiary:Boolean = false
    ) extends BaseEntity{
	description = `type`.description
	var text:String = null
	
    @Parent
	var parent:Ref[BaseEntity] = null
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Subclass
class IndividualCompetition extends Competition(CompetitionType.INDIVIDUAL, "","20:00", "22:00")

abstract class TeamCompetition(
    `type`:CompetitionType, 
    subsidiary:Boolean = false)  extends Competition(`type`,"","20:30","22:00",subsidiary){
  import org.chilternquizleague.domain.util.RefUtils._
  
	@Load
  var fixtures:JList[Ref[Fixtures]] = new ArrayList
	@Load
  var results:JList[Ref[Results]] = new ArrayList
    	
	@Load
  var subsidiaryCompetition:Ref[TeamCompetition] = null
  
  def subsidiaryResults(date:Date) = if(subsidiaryCompetition == null) None else subsidiaryCompetition.resultsForDate(date)
  def addResult(result:Result):Results
	def resultsForDate(date:Date):Option[Results] = {
	  
	  val resultSet = results.find(r=>sameDay(date, r.date)) 
	  
	  resultSet match {
	    
	    case None => {
	      val newResults = new Results
	      newResults.description  = description
	      newResults.date = date
	      val fixtures = fixturesForDate(date)
	      for{
	        f <- fixtures
	      }
	      yield{
	        newResults.description = f.description
	        newResults
	      }
	      results.add(newResults)
	      Some(newResults)
	    }
	    case _ => resultSet.map(_.get)
	  }
	  
	}
	
	def fixturesForDate(date:Date):Option[Fixtures] = fixtures.find(r=>sameDay(date, r.start )).map(_.get)
}

abstract class BaseLeagueCompetition(
    `type`:CompetitionType, 
    subsidiary:Boolean = false)  extends TeamCompetition(`type`,subsidiary){
  
  	var win:Int = 2
	var loss:Int = 0;
	var draw:Int = 1;
	
	var leagueTables:JList[LeagueTable] = new ArrayList
	
	override def addResult(result:Result):Results = {
	  val results = resultsForDate(result.fixture.start)
	  for{
	    r <- results
	  }
	  yield{
	    r.addResult(result) match {
	      case true => {
	        for(table <- leagueTables) {
	          for(homeRow <- table.rows if(homeRow.team == result.fixture.home)) updateRow(homeRow, result.homeScore, result.awayScore)
	          for(awayRow <- table.rows if(awayRow.team == result.fixture.away)) updateRow(awayRow, result.awayScore, result.homeScore)
	          table.sort
	        }
	
	      }
	      case false => {}
	    }
	  }

	  results.get
	}
	
	private def updateRow(row:LeagueTableRow, score:Int, oppoScore:Int):Unit={
	  	val points = if(score > oppoScore)  win else if(score == oppoScore) draw else loss;
		
		row.leaguePoints += points
		row.matchPointsFor += score
		row.matchPointsAgainst += oppoScore
		row.drawn += (if(points == draw) 1 else 0)
		row.won += (if(points == win)  1 else 0)
		row.lost += (if(points == loss) 1 else 0)
		row.played += 1

	}

}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Subclass
class LeagueCompetition extends BaseLeagueCompetition(CompetitionType.LEAGUE){
  
  def copyAsInitial:LeagueCompetition = {
    
    val copy = new LeagueCompetition
    val table = new LeagueTable
    
    for{
      t <- leagueTables
    }{
      val table = new LeagueTable
      table.description  = t.description 
      table.rows = for(r <- t.rows)yield {
        val row = new LeagueTableRow
        row.team  = r.team 
        row}
      
      copy.leagueTables.add(table)
    }
    
    copy
  }
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Subclass
class BeerCompetition extends BaseLeagueCompetition(CompetitionType.BEER, true)

abstract class KnockoutCompetition(`type`:CompetitionType) extends TeamCompetition(`type`){
	override def addResult(result:Result):Results = {
	  val results = resultsForDate(result.fixture.start) 
	  results foreach {
	    _.addResult(result)
	  }
	  results.get
	}
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Subclass
class BuzzerCompetition extends Competition(CompetitionType.BUZZER,"","","")

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Subclass
class CupCompetition extends KnockoutCompetition(CompetitionType.CUP)

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Subclass
class PlateCompetition extends KnockoutCompetition(CompetitionType.PLATE)

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class EmailAlias{

  var alias:String = null
  @Load
  var user:Ref[User]= null 
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class TextEntry(var name:String,var text:String){
  def this() = {this(null,null)}
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class Text(var text:String){
  def this() = {this(null)}
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class Report{
  var text:Text = new Text
  @Load
  var team:Ref[Team] = null
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class LeagueTable{
  	
	var description:String = null
	var rows:JList[LeagueTableRow] = new ArrayList
	
	def sort() = {
	  import org.chilternquizleague.domain.util.RefUtils._
	  rows = rows.sortBy(r => (r.leaguePoints, r.matchPointsFor , r.won , r.drawn , r.team.shortName)).reverse
	  
	  var idx = 0
	  for(row <- rows){idx = idx + 1; row.position = idx.toString} 
	}
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class LeagueTableRow{
  	var team:Ref[Team] = null
	var position:String = null
	var played = 0
	var won = 0
	var lost = 0
	var drawn = 0
	var leaguePoints = 0
	var matchPointsFor = 0
	var matchPointsAgainst = 0
}



