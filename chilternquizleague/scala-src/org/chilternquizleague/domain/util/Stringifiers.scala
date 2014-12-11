package org.chilternquizleague.domain.util

import com.googlecode.objectify.stringifier.Stringifier
import org.chilternquizleague.domain.CompetitionType

class CompetitionTypeStringifier extends EnumStringifier[CompetitionType](classOf[CompetitionType])

abstract class EnumStringifier[T<:Enum[T]](clazz:Class[T]) extends Stringifier[T]{
   override def fromString(name:String):T = Enum.valueOf(clazz,name)
   override def toString(enum:T) = enum.name
}