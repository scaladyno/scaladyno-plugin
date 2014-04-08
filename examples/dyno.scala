package dyno.tests.basic

object Test {
  def main(args: Array[String]): Unit = {
    val y = 123
    println(y.toString)
    val x = new NoSuchClass()
    //println(x.toString)
  }
}
