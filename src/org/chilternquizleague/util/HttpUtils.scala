package org.chilternquizleague.util

import javax.servlet.http.HttpServletRequest
import org.chilternquizleague.util.StringUtils.StringImprovements
import java.net.URL
import org.apache.commons.io.IOUtils
import java.nio.charset.Charset

object HttpUtils {

    implicit class RequestImprovements(val s: HttpServletRequest) {
         def parameter(a:String) = Option(s.getParameter(a))
         def id(a:String = "id") = Option(s.getParameter(a)) flatMap { _ toLongOpt }
         def host = new URL(s.getRequestURL.toString()).getHost.replaceFirst("www.", "")
         def bytes = {
           val buffer = IOUtils.toByteArray(s.getReader)
           buffer
         }
    }

}