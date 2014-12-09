package scala.org.chilternquizleague.domain.util

import scala.annotation.meta.field

import com.googlecode.objectify.annotation



object ObjectifyAnnotations {
  import com.googlecode.objectify.annotation
  
  type Id = annotation.Id @field
  type Parent = annotation.Parent @field
  type Load = annotation.Load @field
  type Ignore = annotation.Ignore @field
  type Index = annotation.Index @field
  type Stringify = annotation.Stringify @field

// Etc. etc.

}

object JacksonAnnotations {
  import com.fasterxml.jackson.annotation
  
  type JsonIgnore = annotation.JsonIgnore @field
}