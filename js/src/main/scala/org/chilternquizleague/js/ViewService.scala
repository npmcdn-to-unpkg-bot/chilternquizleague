package org.chilternquizleague.js

import com.greencatsoft.angularjs.injectable
import com.greencatsoft.angularjs.core.HttpService
import com.greencatsoft.angularjs.Service
import com.greencatsoft.angularjs.Factory
import com.greencatsoft.angularjs.core.RootScope
import com.greencatsoft.angularjs.core.HttpConfig
import scala.reflect.ClassTag
import scala.collection.generic.MutableMapFactory
import com.greencatsoft.angularjs.core.Promise
import org.chilternquizleague.js.domain.View
import org.chilternquizleague.js.domain.DOMObject


@injectable("viewService")
class ViewService(http: HttpService, rootScope : RootScope) extends Service {
  import scala.collection.mutable.Map
  
  
  private def config:HttpConfig = {
    val c = HttpConfig.empty
    c.headers.update("responseType","json")
    c
  }
  
  private def config(params:Map[String,String]):HttpConfig = {
    
    val c:HttpConfig = config
    
    for{
      p <- params
    }
    {
      c.headers.update(p._1, p._2)
    }
    
    c
  }
  
  def load[T <: DOMObject](id:Int )(implicit tag: ClassTag[T]) = http.get[T](s"/view/${tag.runtimeClass.getSimpleName}/$id", config)

  def view[T <: View](params:Map[String,String] = Map())(implicit tag: ClassTag[T]) = http.get[T](s"/view/${tag.runtimeClass.getSimpleName}", config(params))
  
  def viewList[T <: View, List[T]](params:Map[String,String] = Map())(implicit tag: ClassTag[T]) = http.get[List[T]](s"/view/${tag.runtimeClass.getSimpleName}", config(params))
  
  def list[T <: DOMObject, List[T]](implicit tag: ClassTag[T]) = http.get[List[T]](s"${tag.runtimeClass.getSimpleName}-list", config)

  def post[T <: DOMObject](entity:T)(implicit tag: ClassTag[T]) = http.post[T](s"/view/${tag.runtimeClass}")

 
}

@injectable("viewService")
class ViewServiceFactory(http: HttpService,rootScope : RootScope) extends Factory[ViewService] {
  override def apply() = new ViewService(http,rootScope)
}
