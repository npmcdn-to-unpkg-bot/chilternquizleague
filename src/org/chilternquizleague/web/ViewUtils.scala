package org.chilternquizleague.web

import org.chilternquizleague.domain._

object ViewUtils{
  
  implicit def compTypeToView(compType:CompetitionType):CompetitionTypeView = new CompetitionTypeView(compType)
  implicit def seasonToView(season:Season):SeasonView = new SeasonView(season)
  implicit def competitionToView(competition:Competition):CompetitionView = new CompetitionView(competition)
  implicit def statisticsToView(statistics:Statistics):StatisticsView = new StatisticsView(statistics)  
}
