package org.chilternquizleague.domain

import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Cache
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.googlecode.objectify.annotation.Index
import java.util.{List => JList}
import java.util.{Map => JMap}
import com.googlecode.objectify.Ref
import java.util.ArrayList
import com.fasterxml.jackson.annotation.JsonIgnore
import scala.beans.BeanProperty
import org.chilternquizleague.domain.Utils._
import java.util.HashMap
import com.googlecode.objectify.annotation.Ignore
import scala.collection.JavaConversions._
import com.googlecode.objectify.annotation.Load



@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Entity
class User extends BaseEntity{
  
  @Index
  var name:String = null
  @Index
  var email:String  = null
  
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@Cache
@Entity
class Venue extends BaseEntity{
  var name:String = null
  var address:String = null
  var postcode:String = null
  var website:String = null
  var phone:String = null
  var email:String = null
  var imageURL:String = null
}

@Cache
@Entity
class Team extends BaseEntity{
	@BeanProperty
	var name:String = null
	@BeanProperty
  	var shortName:String = null
  	@BeanProperty
  	var rubric:Text = new Text
	
  	@JsonIgnore
  	@Load
	private var venueRef:Ref[Venue] = null
	
	@JsonIgnore
	@Load
	private var userRefs:JList[Ref[User]] = new ArrayList
	

	def setVenue(v:Venue):Unit = venueRef = Ref.create(v)
	def getVenue:Venue = if (venueRef == null) null else venueRef.get
	

	def users:JList[User] = refsToEntities(userRefs )
	def setUsers(users:JList[User]):Unit = userRefs = entitiesToRefs(users)
	def getUsers:JList[User] = users
	
	def emailName = if(shortName == null) null else shortName.replace(' ', '.').toLowerCase;
	def getEmailName = emailName
	def setEmailName(dummy:String) = {}
}

@Cache
@Entity
class GlobalText extends BaseEntity{
  
  @BeanProperty
  var name:String = null

  private var text:JMap[String,TextEntry] = new HashMap
  
  def text(key:String):String = text.getOrElse(key, new TextEntry(key, "No text found for '" + key +"'")).text 
  
  def getEntries:JList[TextEntry] = new ArrayList(text.values)
  def setEntries(entries:JList[TextEntry]):Unit = entries.foreach {t => text put (t.name, t)}
  
}

@Entity
class GlobalApplicationData extends BaseEntity{
  @BeanProperty
  var frontPageText:String = null
  @BeanProperty
  var leagueName:String = null

  @Load
  var currentSeason:Ref[Season] = null
  @Load
  var globalText:Ref[GlobalText] = null
  @BeanProperty
  var emailAliases:JList[EmailAlias] = new ArrayList
  
  def setCurrentSeason(s:Season) = currentSeason = Ref.create(s)
  def getCurrentSeason:Season = if(currentSeason==null) null else currentSeason .get

  def setGlobalText(s:GlobalText) = globalText = Ref.create(s)
  def getGlobalText:GlobalText = if(globalText==null) null else globalText .get

}

class EmailAlias{
  @BeanProperty
  var alias:String = null
  @Load
  var user:Ref[User]= null 
  
  def getUser:User = if(user== null) null else user.get()
  def setUser(u:User):Unit = user = Ref.create(u)
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class TextEntry(var name:String,var text:String){
  def this() = {this(null,null)}
}

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
class Text(var text:String){
  def this() = {this(null)}
}




