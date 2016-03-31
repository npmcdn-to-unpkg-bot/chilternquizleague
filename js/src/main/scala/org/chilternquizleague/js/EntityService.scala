package org.chilternquizleague.js

import com.greencatsoft.angularjs.core.HttpService
import com.greencatsoft.angularjs.core.RootScope
import com.greencatsoft.angularjs.Service
import com.greencatsoft.angularjs.injectable
import com.greencatsoft.angularjs.core.HttpConfig
import org.chilternquizleague.js.domain._
import scala.reflect.ClassTag
import com.greencatsoft.angularjs.core.Promise
import com.greencatsoft.angularjs.core.HttpPromise
import org.chilternquizleague.js.domain.DOMObject
import com.greencatsoft.angularjs.Factory

@injectable("entityService")
class EntityService(http: HttpService, rootScope: RootScope) extends Service {

  private def config: HttpConfig = {
    val c = HttpConfig.empty
    c.headers.update("responseType", "json")
    c
  }

  private def config(params: Map[String, String]): HttpConfig = {

    val c = config
    c.headers ++= params
    c

  }
  
  private def progress[T](promise:HttpPromise[T]) = {
    rootScope.$broadcast("progress", true)
    def endIt() = {rootScope.$broadcast("progress", false);val a=0}
    promise.`then`((e:T) => {endIt();e}, (e:T) => {endIt()} )
  }
  
  def load[T <: DOMObject](id: Int)(implicit tag: ClassTag[T]) = progress(http.get[T](s"/entity/${tag.runtimeClass.getSimpleName}/$id", config))

  def save[T <: DOMObject](entity: T)(implicit tag: ClassTag[T]) = progress(http.post(s"/entity/${tag.runtimeClass.getSimpleName}", entity, config))
}

@injectable("entityService")
class EntityServiceFactory(http: HttpService,rootScope : RootScope) extends Factory[EntityService] {
  override def apply() = new EntityService(http,rootScope)
}