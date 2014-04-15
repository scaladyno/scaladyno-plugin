
class SimpleClass(arg1: Int, arg2: String) {
  def this(arg1: Int) = this(arg1, "stri")
}

case class ClassTypeParams[T <: Int](x:Int, y:Int)

case class CaseClass(i:Int) //extends NSC

object Test {
  def main(args: Array[String]): Unit = {
  	println("Class instantiation test");
  }

  def NeverCalled() {
	  //NSC = NoSuchClass
	  NSC()
  	NSC().field
  	NSC().method(4, "str")
  	new SimpleClass(8, "str")
    new SimpleClass(6)
    SimpleClass() //not enough parameters
    new SimpleClass(5, 5)
    new SimpleClass[Clazz](8)
    CaseClass()
    CaseClass(5)
    CaseClass(5, 5)
    ClassTypeParams[Int](5,5)
    ClassTypeParams[CaseClass](5,5)
    ClassTypeParams[NSC](5,5)
  }
}

