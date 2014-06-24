import scala.language.implicitConversions

case class Class1(field:Int)

object Test {
   	def main(args: Array[String]) {
   		val c = Class1(3)
	   	val ret = c match {
	   		case Class1(1) => "one"
	   		case new Exception("hello") => "two"
	   		case Class1(3) => "three"
	   	}
	    println(ret)
	}
}