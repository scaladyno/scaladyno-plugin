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
  	val inst = new SimpleClass(8, "str")
    val x = inst.field
    val y = inst.fieldNot
    inst.fieldNot
    inst.method()
    inst.methodNot()
    val a = inst.function(2)
    val b:SimpleClass = inst.function(5)
  }
}

