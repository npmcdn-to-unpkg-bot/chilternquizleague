package org.chilternquizleague.util
import com.googlecode.objectify.Key
import com.googlecode.objectify.ObjectifyService.ofy
import org.chilternquizleague.domain.BaseEntity
import scala.collection.JavaConversions._
import com.googlecode.objectify.cmd.Query

object Storage {
  
  def entity[T](id: Option[Long], clazz: Class[T]): Option[T] = {

    Option[T](id match {

      case Some(idval) => ofy.load.now(Key.create(clazz, idval))
      case None => clazz.newInstance
    })

  }
  
  def entityList[T <: BaseEntity](c:Class[T], filter:(String,Any)*):List[T] = {
    
    (filter match {
      
      case Nil => ofy.load.`type`(c).list
      case _ => {var loader:Query[T] = ofy.load.`type`(c)
        for((a,b) <- filter)loader = loader.filter(a,b)
        loader.list
      }
    }).toList
  }    
  
  def save[T <: BaseEntity](entity:T) = {
    ofy.save.entity(entity).now
  }
  


}