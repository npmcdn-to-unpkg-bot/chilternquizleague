package scala.org.chilternquizleague.domain.util

import scala.annotation.meta.field

import com.googlecode.objectify.annotation



object Annotations {
  import com.googlecode.objectify.annotation
  import scala.annotation.meta.field
  
  type Id = annotation.Id @field
  type Parent = annotation.Parent @field
  type Load = annotation.Load @field
  type Ignore = annotation.Ignore @field
  type Index = annotation.Index @field
  type Stringify = annotation.Stringify @field

// Etc. etc.

}