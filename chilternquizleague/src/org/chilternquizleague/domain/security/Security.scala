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

object Token{
  
  def token():String = UUID.randomUUID().toString().replace("-", "")
}

@Entity
@Cache
class LogonToken(uuid:String,expires:Date) extends Token(uuid,expires){
  def this() = this(null, null)  
}

object LogonToken{
  
  val duration = 15 * 60 * 1000
  
  def apply() = {
    val token = new LogonToken(Token.token(), new Date(System.currentTimeMillis() + duration))
    save(token)
    token
  }
  def find(id:Long) = {
    val now = new Date
    val token = entity(Some(id),classOf[LogonToken])
    
    for(t <- token if t.expires after now) yield t 
    
  }
  
  def cleanUp() = {
    val now = new Date
    val tokens = entityList(classOf[LogonToken])
    for(t <- tokens if t.expires before now){
      delete(t)
    }
    
  }

}

@Entity
@Cache
class SessionToken(uuid:String,expires:Date, var user:Ref[User]) extends Token(uuid,expires){
  def this() = this(null, null,null)  
}
 
object SessionToken{
  
  val duration = 60 * 60 * 1000
  
  def apply(user:User) = {
    val token = new SessionToken(Token.token(), new Date(System.currentTimeMillis() + duration), user)
    save(token)
    token
  }
  
  def find(id:Long) = {
    val now = new Date
    val token = entity(Some(id),classOf[SessionToken])
    
    for(t <- token if t.expires after now) yield t 
    
  }

  def cleanUp() = {
    val now = new Date
    val tokens = entityList(classOf[SessionToken])
    for(t <- tokens if t.expires before now){
      delete(t)
    }
  }

  
}