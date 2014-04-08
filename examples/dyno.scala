
package dyno.tests.basic

object Test {

  object T {
    def bar = new Wat[T](???)
  }

  class C {
    def foo = new TheresNoSuchClass()
  }

  def main(args: Array[String]): Unit = {
    val y = 123
    println(y.toString)
    println(y.asInstanceOf[List[NoSuchClass]].toString)
    val x = new NoSuchClass()
    println(x.toString)
    val c = new C
    println(c.foo)
    println(T.bar)
  }
}

