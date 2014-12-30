package org.chilternquizleague.util

import javax.servlet.http.HttpServletRequest
import org.chilternquizleague.util.StringUtils.StringImprovements

object HttpUtils {

    implicit class RequestImprovements(val s: HttpServletRequest) {
         def parameter(a:String) = Option(s.getParameter(a))
         def id(a:String = "id") = Option(s.getParameter(a)) flatMap { _ toLongOpt } 
    }

}