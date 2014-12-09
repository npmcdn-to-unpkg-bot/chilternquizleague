package scala.org.chilternquizleague.results

import org.chilternquizleague.domain.Result
import org.chilternquizleague.domain.CompetitionType
import com.googlecode.objectify.ObjectifyService.ofy
import com.googlecode.objectify.VoidWork
import org.chilternquizleague.domain._
import scala.collection.JavaConversions._
import com.googlecode.objectify.Key

class ResultHandler(result:Result, email:String, seasonId:Long, competitionType:CompetitionType) {

	
	private def commit():Unit = {
		
	  
	  Option(ofy.load.`type`(classOf[User]).filter("email",email).list.head).foreach {

	   result.setFirstSubmitter(_);
	    
	    Option(ofy.load.key(Key.create(classOf[Season],seasonId)).now()).foreach {
	      
	      season => {ofy.transact(new VoidWork() {
	    
	    	  override def vrun:Unit = {
	    	    
	    	  	val competition:Option[TeamCompetition] = Option(season.getCompetition(competitionType))
				
				competition.foreach {
	    	  	 c=> {
	    	  	   c.addResult(result)
	    	  	   season.prePersist
	    	  	   ofy.save.entities(season)
	    	  	 } 
	    	  	}
	    	  	}
	    	  	
	
			})}}
	      
	      
	      return
	  }
		
	}
}


object ResultHandler {
  
  def apply(result:Result, email:String, seasonId:Long, competitionType:CompetitionType):Unit = {
    
    new ResultHandler(result,email,seasonId,competitionType).commit
  }
  
}