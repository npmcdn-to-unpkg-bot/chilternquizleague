package scala.org.chilternquizleague.domain

import com.googlecode.objectify.annotation.Cache
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.Ref
import com.googlecode.objectify.annotation.Parent
import java.util.Date
import scala.org.chilternquizleague.domain.util.ObjectifyAnnotations._
import scala.org.chilternquizleague.domain.util.JacksonAnnotations._
import java.util.ArrayList
import com.googlecode.objectify.Key
import com.googlecode.objectify.annotation.Subclass
import scala.org.chilternquizleague.domain.CompetitionTypes._
import java.util.Calendar
import java.util.HashMap
import scala.collection.JavaConversions._
import scala.org.chilternquizleague.domain.util.CompetitionTypeStringifier
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility



trait BasePersistentEntity {
  @Id 
  var id: java.lang.Long = null
  var retired: Boolean = false
  @Ignore
  lazy val key = Key.create(this).getString
}

/**
 * Embedded types
 */
case class Text(var text: String) { private def this() { this(null) } }
case class Report(var team: Ref[Team], var text: Text) { private def this() { this(null, null) } }
case class Fixture(var away: Ref[Team], var home: Ref[Team], var start: Date, var end: Date) { private def this() { this(null, null, null, null) } }
case class Result(var awayScore: Int = 0, var homeScore: Int = 0, var fixture: Ref[Fixture], var reports: java.util.List[Report] = new ArrayList) { private def this() { this(fixture = null) } }
case class TextEntry(var name: String, var text: String) { private def this() { this(null, null) } }
case class LeagueTable(var description: String, var rows: java.util.List[LeagueTableRow] = new ArrayList) { private def this() { this(description = null) } }
case class LeagueTableRow(
  var team: Ref[Team],
  var position: String,
  var played: Int = 0,
  var won: Int = 0,
  var lost: Int = 0,
  var drawn: Int = 0,
  var leaguePoints: Int = 0,
  var matchPointsFor: Int = 0,
  var matchPointsAgainst: Int = 0) { private def this() { this(team = null, position = null) } }
case class EmailAlias(var alias: String, var user: Ref[User]) { private def this() { this(alias = null, user = null) } }

/**
 * Entities
 */

@Cache
@Entity
case class User(@Index var name: String = "", var email: String = "") extends BasePersistentEntity {
  // Only for Objectify creation
  private def this() { this(null, null) }
}

@Cache
@Entity
case class Venue(@Index var name: String, var email: String, var phone: String, var imageUrl: String, var website: String) extends BasePersistentEntity {
  // Only for Objectify creation
  private def this() { this(null, null, null, null, null) }
}

@Cache
@Entity
case class Team(@Index var name: String, @Index var shortName: String, var rubric: Text = Text(null), var venue: Ref[Venue], var users: java.util.List[Ref[User]] = new ArrayList) extends BasePersistentEntity {
  // Only for Objectify creation
  private def this() { this(name = null, shortName = null, venue = null) }
  
}

abstract class CompetitionChild(@Parent var parent: Ref[BasePersistentEntity] = null) extends BasePersistentEntity

@Cache
@Entity
case class Results(var date: Date, var description: String, var results: java.util.List[Result] = new ArrayList) extends CompetitionChild {
  // Only for Objectify creation
  private def this() { this(date = null, description = null) }
}

@Cache
@Entity
case class Fixtures(var date: Date, var description: String, var fixtures: java.util.List[Fixture] = new ArrayList) extends CompetitionChild {
  // Only for Objectify creation
  private def this() { this(date = null, description = null) }
}

@Cache
@Entity
case class GlobalApplicationData(var name: String, var frontPageText: String, var globalText: Ref[GlobalText], var currentSeason: Ref[Season], var emailAliases: java.util.List[EmailAlias] = new ArrayList) extends BasePersistentEntity {
  // Only for Objectify creation
  private def this() { this(null, null, null, null) }
}

@Cache
@Entity
case class GlobalText(var name: String, var entries: java.util.List[TextEntry] = new ArrayList) extends BasePersistentEntity {
  // Only for Objectify creation
  private def this() { this(name = null) }
}

@Cache
@Entity
case class Season(var startYear: Int = Calendar.getInstance.get(Calendar.YEAR),
  var endYear: Int = Calendar.getInstance.get(Calendar.YEAR) + 1,
  @Stringify(classOf[CompetitionTypeStringifier]) var competitions: java.util.Map[CompetitionType, Ref[Competition]] = new HashMap) extends BasePersistentEntity {
  // Only for Objectify creation
  private def this() { this(startYear = Calendar.getInstance.get(Calendar.YEAR)) }
}

object CompetitionTypes {

  sealed abstract class CompetitionType(val name: String, val description: String)
  case object BEER extends CompetitionType("BEER", "Beer Leg")
  case object BUZZER extends CompetitionType("BUZZER", "Team Buzzer")
  case object CUP extends CompetitionType("CUP", "Knockout Cup")
  case object INDIVIDUAL extends CompetitionType("INDIVIDUAL", "Individual")
  case object LEAGUE extends CompetitionType("LEAGUE", "League")
  case object PLATE extends CompetitionType("PLATE", "Plate")

  val types = Seq(LEAGUE, BEER, CUP, PLATE, BUZZER, INDIVIDUAL)

  def byName(name: String) = Option(types.filter(_.name == name).head)

}

@Entity
@Cache
@JsonAutoDetect( fieldVisibility=Visibility.ANY)
abstract class Competition extends BasePersistentEntity {
  var `type`: CompetitionType = null
  var description: String = null
  var startTime: String = null
  var subsidiary: Boolean = false

}

@Subclass
@Cache
case class BuzzerCompetition() extends Competition{ `type` = BUZZER}

abstract class TeamCompetition extends Competition {
  def fixtures: java.util.List[Ref[Fixtures]]
  def results: java.util.List[Ref[Results]]
}

abstract class  BaseLeagueCompetition extends TeamCompetition {
	var draw: Int = 1
	var loss: Int = 0
	var win: Int = 2
	def leagueTables: java.util.List[LeagueTable]
}

@Subclass
@Cache
case class LeagueCompetition(@JsonIgnore var leagueTables: java.util.List[LeagueTable] = new ArrayList, @JsonIgnore var fixtures: java.util.List[Ref[Fixtures]] = new ArrayList,@JsonIgnore var results: java.util.List[Ref[Results]] = new ArrayList) extends BaseLeagueCompetition{`type` = LEAGUE;
// Only for Objectify creation
  private def this(){this(leagueTables = new ArrayList)} 
}

@Subclass
@Cache
case class BeerCompetition(@JsonIgnore var leagueTables: java.util.List[LeagueTable] = new ArrayList,@JsonIgnore var fixtures: java.util.List[Ref[Fixtures]] = new ArrayList, @JsonIgnore var results: java.util.List[Ref[Results]] = new ArrayList) extends BaseLeagueCompetition{`type`=BEER;subsidiary = true
// Only for Objectify creation
  private def this(){this(leagueTables = new ArrayList)} }

abstract class KnockoutCompetition extends TeamCompetition

@Subclass
@Cache
case class CupCompetition(@JsonIgnore var fixtures: java.util.List[Ref[Fixtures]] = new ArrayList,@JsonIgnore var results: java.util.List[Ref[Results]] = new ArrayList) extends KnockoutCompetition{`type` = CUP
  // Only for Objectify creation
  private def this(){this(results = new ArrayList)}
}

@Subclass
@Cache
case class PlateCompetitionvar (@JsonIgnore var fixtures: java.util.List[Ref[Fixtures]] = new ArrayList,@JsonIgnore var results: java.util.List[Ref[Results]] = new ArrayList) extends KnockoutCompetition{`type` = PLATE
  // Only for Objectify creation
  private def this(){this(results = new ArrayList)}
}

