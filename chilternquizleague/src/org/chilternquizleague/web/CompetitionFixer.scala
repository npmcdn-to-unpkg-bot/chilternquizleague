package org.chilternquizleague.web

import org.chilternquizleague.domain.Competition
import org.chilternquizleague.domain.util.RefUtils._
import org.chilternquizleague.domain.TeamCompetition
import scala.collection.JavaConversions._
import com.googlecode.objectify.ObjectifyService.ofy
import org.chilternquizleague.domain.Fixtures
import org.chilternquizleague.domain.Results

class CompetitionFixer(target:Option[TeamCompetition], source:Option[TeamCompetition]) {
  
  for{
    t <- target
    s <- source
  }
  yield{
    
    s.fixtures.foreach{ f=> val fix:Fixtures = f; fix.parent = null; t.fixtures.add(fix)}
    s.results.foreach{ r => val res:Results = r; res.parent = null; t.results.add(res)}
    ofy.save().entity(t)
    
    
    
  }
  
  

}