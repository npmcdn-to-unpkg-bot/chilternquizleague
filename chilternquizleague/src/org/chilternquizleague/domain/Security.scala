package org.chilternquizleague.domain

import com.googlecode.objectify.annotation.Entity



class Security {
  
}

@Entity
class LogonToken extends BaseEntity{
  val uuid:String
}
