object Test {
	def main(args: Array[String]): Unit = {
		if (sys.props("dyno.crash").toBoolean) {
		  val x = new NoSuchClassFound()
		  x.foo()
		  println("you shouldn't see this!")
		} else {
		  println("okay, we're good")
		}
	}
}
