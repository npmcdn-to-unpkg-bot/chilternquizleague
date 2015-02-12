package org.chilternquizleague.domain.security

import com.googlecode.objectify.annotation.Entity
import java.util.Date
import java.util.UUID
import com.googlecode.objectify.annotation.Subclass
import org.chilternquizleague.domain.util.ObjectifyAnnotations._
import org.chilternquizleague.domain.util.RefUtils._
import org.chilternquizleague.util.Storage._
import scala.collection.JavaConversions._
import scala.collection.mutable.Map
import com.googlecode.objectify.annotation.Cache
import com.googlecode.objectify.Ref
import org.chilternquizleague.domain._

//unused at present
trait TokenCache{
  type tokenType <: Token
  
  protected lazy val cache = init()
  def init():Map[String,tokenType]
}


abstract class Token(var uuid:String, var expires:Date) extends BaseEntity

@Entity
@Cache
class LogonToken(uuid:String,expires:Date) extends Token(uuid,expires){
  def this() = this(null, null)  
}

object LogonToken{
  
  def apply() = {
    val token = new LogonToken(UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + (15 * 60 * 1000)))
    save(token)
    token
  }
  def find(id:Long) = {
    val now = new Date
    val token = entity(Some(id),classOf[LogonToken])
    
    for(t <- token if t.expires after now) yield t 
    
  }

}

@Entity
@Cache
class SessionToken(uuid:String,expires:Date, var user:Ref[User]) extends Token(uuid,expires){
  def this() = this(null, null,null)  
}
 
object SessionToken{
  def apply(user:User) = {
    val token = new SessionToken(UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + (60 * 60 * 1000)), user)
    save(token)
    token
  }
  def find(id:Long) = {
    val now = new Date
    val token = entity(Some(id),classOf[SessionToken])
    
    for(t <- token if t.expires after now) yield t 
    
  }

  
  
}