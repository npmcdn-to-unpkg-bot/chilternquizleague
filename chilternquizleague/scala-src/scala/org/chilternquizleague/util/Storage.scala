package scala.org.chilternquizleague.util
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
  
  def entityList[T <: BaseEntity](c:Class[T]):List[T] = ofy.load.`type`(c).list.toList


}