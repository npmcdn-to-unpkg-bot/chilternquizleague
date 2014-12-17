package org.chilternquizleague.web

import org.chilternquizleague.domain.Team
import org.chilternquizleague.domain.Fixtures
import org.chilternquizleague.domain.Results
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import scala.beans.BeanProperty
import scala.annotation.meta.param
import org.chilternquizleague.domain.Season
import org.chilternquizleague.domain.CompetitionType
import org.chilternquizleague.domain.Competition
import org.chilternquizleague.domain.GlobalApplicationData
import org.chilternquizleague.domain.LeagueCompetition
import scala.collection.JavaConversions._
import org.chilternquizleague.domain.Result
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
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class GlobalApplicationDataView(data:GlobalApplicationData ) {
		val frontPageText = data.frontPageText ;
		val leagueName = data.leagueName ;
		val currentSeasonId = if (data.currentSeason == null) null else data.currentSeason.id;
		val textId = if (data.globalText == null) null else data.globalText.id;
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class PreSubmissionView(val team:Team, val fixtures:List[Fixtures], val results:List[Results])

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class LeagueTableView(season:Season , compType:CompetitionType ){
		val competition:LeagueCompetition = season.competition(compType);
		val tables = if(competition != null)  competition.leagueTables.toList else List();
		val description = season.description;
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
