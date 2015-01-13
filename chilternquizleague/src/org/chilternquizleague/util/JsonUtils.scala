package org.chilternquizleague.util

import com.fasterxml.jackson.databind.JsonSerializer
import com.googlecode.objectify.Ref
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import org.chilternquizleague.domain.BaseEntity
import com.googlecode.objectify.ObjectifyService.ofy
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import scala.util.control.Exception.catching
import com.fasterxml.jackson.databind.module.SimpleModule
import org.chilternquizleague.domain.Text
import org.chilternquizleague.domain.User
import com.fasterxml.jackson.databind.ObjectMapper



   
object ClassUtils{
    val packages: List[Package] = List(classOf[BaseEntity].getPackage());

  
    def classFromPart[T](part: String) = {

    val className = part.substring(0, 1).toUpperCase() + part.substring(1)
    def fun(c: Option[Class[T]], p: Package): Option[Class[T]] = {
    	import scala.util.control.Exception._

      if (c.isDefined) c else catching(classOf[ClassNotFoundException]) opt Class.forName(p.getName() + "." + className).asInstanceOf[Class[T]]
    }
    packages.foldLeft(Option[Class[T]](null))(fun)
  }
}

object JacksonUtils {
  
  class RefSerializer extends JsonSerializer[Ref[_]] {
  override def serialize(ref: Ref[_], gen: JsonGenerator, prov: SerializerProvider) = gen.writeObject(ref.get)
}

class RefDeserializer extends JsonDeserializer[Ref[_]] {
  override def deserialize(parser: JsonParser, context: DeserializationContext): Ref[_] = {
    val node: JsonNode = parser.getCodec().readTree(parser);

    val className = node.get("refClass").asText
    val opt = ClassUtils.classFromPart[BaseEntity](className)

    val remote = for {
      clazz <- opt
    } yield {
      parser.getCodec().treeToValue(node, clazz)
    }

    Ref.create(ofy.save.entity(remote.get).now())
  }
}

class SafeRefDeserializer extends JsonDeserializer[Ref[_]] {
  override def deserialize(parser: JsonParser, context: DeserializationContext): Ref[_] = {
    val node: JsonNode = parser.getCodec().readTree(parser);

    val className = node.get("refClass").asText
    val opt = ClassUtils.classFromPart[BaseEntity](className)

    val remote = for {
      clazz <- opt
    } yield {
      parser.getCodec().treeToValue(node, clazz)
    }

    remote match{
      case Some(a) => Ref.create(a)
      case _ => null
    }
  }
}

  class UserSerializer extends JsonSerializer[User] {
    override def serialize(user: User, gen: JsonGenerator, prov: SerializerProvider) = {}
  }
  class TextSerializer extends JsonSerializer[Text] {
    override def serialize(text: Text, gen: JsonGenerator, prov: SerializerProvider) = {
      gen writeStartObject;
      gen writeNullField "text"
      gen writeEndObject
    }

  }
 class ScalaIterableSerialiser extends JsonSerializer[Iterable[Any]]{
    override def serialize(list:Iterable[Any], gen: JsonGenerator, prov: SerializerProvider):Unit = {
      gen writeStartArray;
      
      list foreach {gen.writeObject(_)}
      
      gen writeEndArray()
      
    }}
 
 class ScalaMapSerialiser extends JsonSerializer[Map[String,Any]]{
   
   override def serialize(map:Map[String,Any], gen: JsonGenerator, prov: SerializerProvider):Unit = {
     gen writeStartObject()
     
     for((k,v) <- map){gen.writeFieldName(k);gen.writeObject(v)}
     
     gen.writeEndObject()
     
   }
   
 }
  
  lazy val unsafeModule = { 
    new SimpleModule()
    .addSerializer(classOf[Ref[_]], new RefSerializer)
    .addDeserializer(classOf[Ref[_]], new RefDeserializer())
    .addSerializer(classOf[Iterable[Any]], new ScalaIterableSerialiser)
  	}
  
  lazy val safeModule = {    
    new SimpleModule()
    .addSerializer(classOf[User], new UserSerializer)
    .addSerializer(classOf[Text], new TextSerializer)
    .addSerializer(classOf[Map[String,Any]], new ScalaMapSerialiser)
    .addSerializer(classOf[Iterable[Any]], new ScalaIterableSerialiser)
    .addSerializer(classOf[Ref[_]], new RefSerializer)
    .addDeserializer(classOf[Ref[_]], new SafeRefDeserializer)}
  
  def safeMapper = new ObjectMapper registerModule safeModule
      
  }