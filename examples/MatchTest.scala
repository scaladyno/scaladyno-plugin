class SimpleClass(arg1: Int, arg2: String) {
  def this(arg1: Int) = this(arg1, "stri")
  val field = "my field"
  def method() = { }
  def function(i:Int) = {i+1}
}

object Test {
  def main(args: Array[String]): Unit = {
  	println("Field and method access test.");
  }

  def NeverCalled() {
    val c = new SimpleClass(5, "str")
    
    c match {
      case _:Clazzah => println("error")
      case _ => println("no error")
    }

    c match {
      case ??? =>
        println("???")
      case x: D =>
        println("D")
      case x: String =>
        println("String")
      case x: NoSuchClass2 =>
        println("NoSuchClass2")
      case NoSuchExtractor(x) =>
        println("NoSuchExtractor")
    }

    x match {
      case _ => println("single case")
    }
  }
}

