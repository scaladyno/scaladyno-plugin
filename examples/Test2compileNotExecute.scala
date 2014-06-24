
class SimpleClass(arg1: Int, arg2: String) {
  def this(arg1: Int) = this(arg1, "stri")
}

case class ClassWithTypeParams[T](x:Int)

case class CaseClass(i:Int) //extends NSC

object Test {
  def main(args: Array[String]): Unit = {

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
  	println("Class instantiation test");
  }
}
