package org.chilternquizleague.web

import org.chilternquizleague.domain._
import java.util.HashMap
import org.chilternquizleague.util.Storage._
import scala.collection.JavaConversions._
import org.chilternquizleague.domain.BaseEntity
import org.chilternquizleague.util.JacksonUtils
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.{List => JList}
import java.util.{Map => JMap}


object DBDumper{
  
  val dumpTypes:List[Class[_ <: BaseEntity]] = List(classOf[Season], classOf[Team], classOf[Venue], classOf[User], classOf[CommonText], classOf[GlobalApplicationData])
  
  def dump() = new DBDumper().dump
  
  def load(entities:JMap[String, JList[_ <: BaseEntity]]) = {
    
  }
  
}

private class DBDumper {
  
  def dump() ={
    
    val dump = new HashMap[String,JList[_]]
    
    for{
      t <- DBDumper.dumpTypes
    }{
      dump.put(t.getName, entityList(t))
    }
    
   dump
  }

}