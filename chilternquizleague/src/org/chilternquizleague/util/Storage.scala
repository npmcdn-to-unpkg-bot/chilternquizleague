package org.chilternquizleague.util
import com.googlecode.objectify.Key
import com.googlecode.objectify.ObjectifyService.ofy
import org.chilternquizleague.domain.BaseEntity
import scala.collection.JavaConversions._

object Storage {
  
  def entity[T](id: Option[Long], clazz: Class[T]): Option[T] = {

    Option[T](id match {

      case Some(idval) => ofy.load.now(Key.create(clazz, idval))
      case None => clazz.newInstance
    })

  }
  
  def entityList[T <: BaseEntity](c:Class[T], filter:(String,Any) = null):List[T] = {
    
    (filter match {
      
      case (a,b) => ofy.load.`type`(c).filter(a,b).list
      case _ => ofy.load.`type`(c).list
    }).toList
  }    
  
  def save[T <: BaseEntity](entity:T) = {
    ofy.save.entity(entity).now
  }
  


}