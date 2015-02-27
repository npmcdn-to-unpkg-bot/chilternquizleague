package org.chilternquizleague.util
import com.googlecode.objectify.Key
import com.googlecode.objectify.ObjectifyService.ofy
import org.chilternquizleague.domain.BaseEntity
import scala.collection.JavaConversions._
import com.googlecode.objectify.cmd.Query
import com.googlecode.objectify.VoidWork
import scala.reflect.ClassTag

object Storage {

  def entity[T](id: Option[Long], clazz: Class[T]): Option[T] = {

    Option[T](id match {

      case Some(idval) => ofy.load.now(Key.create(clazz, idval))
      case None => clazz.newInstance
    })

  }

  def entity[T](id: Option[Long])(implicit tag: ClassTag[T]): Option[T] = {

    val clazz: Class[T] = tag.runtimeClass.asInstanceOf[Class[T]]

    entity(id, clazz)
  }

  def entityList[T <: BaseEntity](c: Class[T], filter: (String, Any)*): List[T] = loadEntities(c, filter.toList)

  def entities[T <: BaseEntity](filter: (String, Any)*)(implicit tag: ClassTag[T]): List[T] = {
    val clazz: Class[T] = tag.runtimeClass.asInstanceOf[Class[T]]
    loadEntities(clazz, filter.toList)

  }

  private def loadEntities[T](c: Class[T], filter: List[(String, Any)]): List[T] = {

    (filter match {

      case Nil => ofy.load.`type`(c).list
      case _ => {
        val loader: Query[T] = ofy.load.`type`(c)
        for ((a, b) <- filter) loader.filter(a, b)
        loader.list
      }
    }).toList
  }

  def save[T <: BaseEntity](entity: T) = {
    ofy.save.entity(entity).now
  }

  def delete[T <: BaseEntity](entity: T) = ofy.delete.entity(entity).now

  def delete[T <: BaseEntity](entities: java.util.List[T]) = ofy.delete.entities(entities).now

  def transaction(fn: () => Unit) = {

    ofy.transact(new VoidWork() {
      override def vrun = fn()
    })

  }

}