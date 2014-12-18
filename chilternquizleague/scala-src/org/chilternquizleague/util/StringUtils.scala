package org.chilternquizleague.util

import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat

object StringUtils {
     implicit class StringImprovements(val s: String) {
         import scala.util.control.Exception._
         def toIntOpt = catching(classOf[NumberFormatException]) opt s.toInt
         def toLongOpt = catching(classOf[NumberFormatException]) opt s.toLong
     }
}

object DateUtils{
  private val  format:DateFormat = new SimpleDateFormat("yyyyMMdd");

  def sameDay(date1:Date, date2:Date) = format.format(date1).compareTo(format.format(date2)) == 0	
  
}