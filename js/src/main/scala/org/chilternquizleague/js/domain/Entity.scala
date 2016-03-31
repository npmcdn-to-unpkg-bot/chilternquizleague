package org.chilternquizleague.js.domain

import scala.scalajs.js
import js.annotation._

/**
 * @author dgood
 */


@js.native
trait DOMObject extends js.Any

@js.native
class Venue extends js.Any with DOMObject{
  val id:Int = js.native
  val name:String = js.native
  val address:String = js.native
  val postcode:String = js.native
  val website:String = js.native
  val phone:String = js.native
  val email:String = js.native
  val imageURL:String = js.native
  
}



trait View {
  
}

