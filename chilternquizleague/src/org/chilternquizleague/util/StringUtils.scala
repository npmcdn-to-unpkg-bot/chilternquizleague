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
     def toOrdinal(n:Int)=n+{if(n%100/10==1)"th"else(("thstndrd"+"th"*6).sliding(2,2).toSeq(n%10))}
}

object DateUtils{
  private val  format:DateFormat = new SimpleDateFormat("yyyyMMdd");

  def sameDay(date1:Date, date2:Date) = format.format(date1).compareTo(format.format(date2)) == 0	
  
  implicit class DateImprovements(val d:Date){
    
    def sameDay(other:Date) = DateUtils.sameDay(d,other)
    
  }
}