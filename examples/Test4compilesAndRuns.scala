import scala.language.implicitConversions

case class Class1(field:Int)

object Test {
	implicit def unwrap(c1:Class1):Int = {
		c1.field
	}
   	def main(args: Array[String]) {
   		val x = new Class1(2)
   		val y = new Class1(3)

	    println("type correct paths can be executed: "+(x+y))
	}

	def neverCalled() {
		new SimpleClass(8, "str")
	    new SimpleClass(6)
	    new SimpleClass() //not enough parameters
	    new SimpleClass(5, 5) //too many parameters
	    new SimpleClass[Clazz](8) //type parameters not allowed

	    // no such class
	    new NSC()
	    NSC().field
	    NSC().method(4, "str")

	    CaseClass()
	    CaseClass(5)
	    CaseClass(5, 5)
	}
}