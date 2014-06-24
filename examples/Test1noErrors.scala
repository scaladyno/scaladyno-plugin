import scala.language.implicitConversions

case class Class1(field:Int)

case class Class2(field:Int) {
	def myMethod():Int = {field}
}


object Test {

	implicit def convert(c1:Class1): Class2 = {
	    Class2(c1.field)
	}
   	def main(args: Array[String]) {
   		val c = Class1(3)
	   	val ret = c.myMethod() match {
	   		case 1 => "one"
	   		case 2 => "two"
	   		case _ => "implicit conversions and pattern matching works normally"
	   	}
	    println(ret)
	}
}