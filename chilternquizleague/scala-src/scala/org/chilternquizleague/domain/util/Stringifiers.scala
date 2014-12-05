package scala.org.chilternquizleague.domain.util

import com.googlecode.objectify.stringifier.Stringifier
import scala.org.chilternquizleague.domain.CompetitionTypes.CompetitionType
import scala.org.chilternquizleague.domain.CompetitionTypes

class CompetitionTypeStringifier extends Stringifier[CompetitionType]{
  
  override def fromString(name:String):CompetitionType = CompetitionTypes.byName(name).getOrElse(throw new IllegalArgumentException(name))
  override def toString(compType:CompetitionType) = compType.name
}