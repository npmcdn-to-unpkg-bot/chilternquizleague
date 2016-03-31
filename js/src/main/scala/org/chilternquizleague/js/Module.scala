package org.chilternquizleague.js

import com.greencatsoft.angularjs.Angular
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@JSExport
object Module extends JSApp{
  
  @JSExport
  def main() = {
    val module = Angular.module("mainApp", Seq("ngMaterial","ngCookies","ui.router","tc.chartjs","ui.tinymce"))
    
    module
      .factory[EntityServiceFactory]
      
    
    
  }
  
}