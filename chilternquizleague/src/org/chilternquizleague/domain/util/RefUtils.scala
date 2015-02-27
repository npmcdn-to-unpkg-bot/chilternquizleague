package org.chilternquizleague.domain.util

import com.googlecode.objectify.Ref
import com.googlecode.objectify.ObjectifyService.ofy
import org.chilternquizleague.domain.BaseEntity
import java.util.{List => JList}
import scala.collection.JavaConversions._
import com.googlecode.objectify.ObjectifyService
import scala.reflect.ClassTag

object RefUtils {

	implicit def ref2Value[T<:BaseEntity](ref:Ref[T]):T = ref()
	
	implicit def value2Ref[T<:BaseEntity](value:T):Ref[T] = if(value.id == null) Ref.create(ofy.save.entity(value).now) else Ref.create(value)

	implicit def ref2ValueList[T<:BaseEntity](ref:JList[Ref[T]]):JList[T] = ref.map(_())
	implicit def ref2ValueCollection[T<:BaseEntity](ref:java.util.Collection[Ref[T]]):java.util.Collection[T] = ref.map(_())
	implicit def ref2ValueIterable[T<:BaseEntity](ref:Iterable[Ref[T]]):Iterable[T] = ref.map(_())

  implicit class RefImprovements[T<:BaseEntity](val ref: Ref[T]){
    def Id():Long = if (ref != null) ref.getKey.getId else -1
    def idOpt():Option[Long] = Option(ref).map(_.Id())
    def apply():T = if(ref != null) ref.get else null.asInstanceOf[T]
    def ?():Option[T] = Option(ref())
  }
  
 
}
