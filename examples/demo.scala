package dyno.demo

object Test {

  def main(args: Array[String]): Unit = {
    print("line> ")
    if (io.StdIn.readLine() != "boo") {
      println("okay")
    } else {
      val x = new NoSuchClass
      x.foo()
    }
  }

}

