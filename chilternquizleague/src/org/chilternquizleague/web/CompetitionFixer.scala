package org.chilternquizleague.web

import org.chilternquizleague.domain.Competition
import org.chilternquizleague.domain.util.RefUtils._
import org.chilternquizleague.domain.TeamCompetition
import scala.collection.JavaConversions._
import com.googlecode.objectify.ObjectifyService.ofy
import org.chilternquizleague.domain.Fixtures
import org.chilternquizleague.domain.Results
import org.chilternquizleague.domain.Season
import java.util.ArrayList
import com.googlecode.objectify.Ref
import org.chilternquizleague.domain.LeagueCompetition
import org.chilternquizleague.domain.BeerCompetition
import org.chilternquizleague.domain.BaseLeagueCompetition

class CompetitionFixer(season:Option[Season], competition:Option[BaseLeagueCompetition]) {
  
  
  def doit = {
  for{
    s <- season
    comp <- competition
  }
  yield{
    
      val  c = comp match {
      case a:LeagueCompetition => new LeagueCompetition
      case a:BeerCompetition => new BeerCompetition
      }
      c.description  = "Fixed "  + comp.description 
      c.leagueTables  = comp.leagueTables
    
    c.fixtures = comp.fixtures.map{f=> 
      val fix:Fixtures = new Fixtures
      fix.fixtures  = f.fixtures 
      fix.competitionType = f.competitionType 
      fix.description = f.description 
      fix.start = f.start 
      fix.end = f.end 
      Ref.create(ofy.save.entity(fix).now)
    }
    
    c.results  = comp.results.map{r=>
      val res:Results = new Results
      res.results = r.results 
      res.date = r.date 
      res.description  = r.description 
      Ref.create(ofy.save.entity(res).now)
    }

    val result = ofy.save().entity(c).now.getString
    
    s.competitions.put(c.`type`, c)
    ofy.save().entity(s)
    
    result
    
    
    
  }
  
  
  
  }

}