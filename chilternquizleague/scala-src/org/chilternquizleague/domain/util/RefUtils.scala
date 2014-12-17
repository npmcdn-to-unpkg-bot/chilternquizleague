package org.chilternquizleague.domain.util

import com.googlecode.objectify.Ref
import com.googlecode.objectify.ObjectifyService.ofy
import org.chilternquizleague.domain.BaseEntity
import java.util.{List => JList}
import scala.collection.JavaConversions._

object RefUtils {

	implicit def ref2Value[T<:BaseEntity](ref:Ref[T]):T = ref.get
	
	implicit def value2Ref[T<:BaseEntity](value:T):Ref[T] = if(value.id == null) Ref.create(ofy.save.entity(value).now) else Ref.create(value)

	implicit def ref2ValueList[T<:BaseEntity](ref:JList[Ref[T]]):JList[T] = ref.map(_.get)
}
