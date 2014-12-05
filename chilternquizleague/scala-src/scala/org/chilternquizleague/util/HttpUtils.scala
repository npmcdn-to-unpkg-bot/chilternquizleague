package scala.org.chilternquizleague.util

import javax.servlet.http.HttpServletRequest

object HttpUtils {

    implicit class RequestImprovements(val s: HttpServletRequest) {
         def parameter(a:String) = Option(s.getParameter(a))
    }

}