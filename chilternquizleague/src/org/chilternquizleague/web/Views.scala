package org.chilternquizleague.web

import org.chilternquizleague.domain._

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.JsonIgnore
import scala.annotation.meta.param
import scala.collection.JavaConversions._
import org.chilternquizleague.domain.util.RefUtils._


@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class CompetitionTypeView(compType:CompetitionType){
  val name = compType.name
  val description = compType.getDescription
}

object CompetitionTypeView{
  
  def list = CompetitionType.values.map(new CompetitionTypeView(_)).toList
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class CompetitionView(competition:Competition) {
	val description = competition.description
	val `type` = new CompetitionTypeView(competition.`type`)
	val subsidiary = competition.subsidiary
	val startTime = competition.startTime
	val text = competition.text
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class GlobalApplicationDataView(data:GlobalApplicationData ) {
		val frontPageText = data.frontPageText ;
		val leagueName = data.leagueName ;
		val currentSeasonId = if (data.currentSeason == null) null else data.currentSeason.id;
		val textId = if (data.globalText == null) null else data.globalText.id;
}



@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class PreSubmissionView(val team:Team, val fixtures:Fixtures, val results:List[ResultForSubmission])

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class LeagueTableWrapperView(season:Season , compType:CompetitionType ){
		@JsonIgnore
    val comp:BaseLeagueCompetition = season.competition(compType);
    val competition = new CompetitionView(comp)
		val tables = if(comp != null)  comp.leagueTables.map(new LeagueTableView(_)) else List();
		val description = season.description;
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class LeagueTableView(table:LeagueTable){
  val description = table.description
  val rows = table.rows.map(new LeagueTableRowView(_))
  
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class LeagueTableRowView(row:LeagueTableRow){
  var team = new TeamView(row.team)
  var position = row.position
  var played = row.played
  var won = row.won
  var lost = row.lost
  var drawn = row.drawn
  var leaguePoints = row.leaguePoints
  var matchPointsFor = row.matchPointsFor
  var matchPointsAgainst = row.matchPointsAgainst
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class TeamView(team:Team){
  val id = team.id
  val name = team.name
  val shortName = team.shortName
  val emailName = team.emailName
  val venueId = team.venue.Id()
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class TeamExtras(team:Team,  val fixtures:List[Fixtures],  val results:List[Results]) {
	val id = team.id
	val text = team.rubric.text
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class SeasonView(season:Season){
  val id = season.id
  val description = season.description
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class ResultsReportsView(result:Result){
	val reports = result.reports.map(report => new ReportView(report.team, report.text.text)).toList	
}			

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class ReportView(val team:Team, val text:String)

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class ResultForSubmission(val compType:CompetitionTypeView, val result:Result)

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class ResultSubmission(){
  var email:String = null
  var result:Result = null
  var seasonId:Long = 0
  var competitionType:CompetitionType = null
}

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class ContactSubmission(){
  var recipient:String = null
  var sender:String = null
  var text:String = null
}

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
class StatisticsView(statistics:Statistics) {
  val season:SeasonView = new SeasonView(statistics.season)
  val team:Team = statistics.team 
  val seasonStats = statistics.seasonStats 
  val weekStats = statistics.weekStats.values 
}
