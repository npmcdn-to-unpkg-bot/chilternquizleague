package org.chilternquizleague.domain.util

import com.googlecode.objectify.Ref

object RefUtils {

	implicit def ref2Value[T](ref:Ref[T]):T = ref.get
	
	implicit def value2Ref[T](value:T):Ref[T] = Ref.create(value)

}
