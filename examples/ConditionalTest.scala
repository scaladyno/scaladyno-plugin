
class SimpleClass(arg1: Int, arg2: String) {
  def this(arg1: Int) = this(arg1, "stri")
}

case class ClassWithTypeParams[T](x:Int)
case class ClassTypeConstraint[T <: Int](x:Int, y:Int)

case class CaseClass(i:Int) //extends NSC

object Test {
  def main(args: Array[String]): Unit = {
  	println("Class instantiation test");
    NeverCalled(5)
  }

  def NeverCalled(i:Int) {
	  //NSC = NoSuchClass
	  val x =
      if (i < 5)
        new SimpleClass(3, "str")
      else
        CaseClass(5)

    val y = // this entire block only gets replaced by "class not found" -> not very explicit
      if (i < 5)
        new NSC()
      else
        new CaseClass(5)

  }
}

