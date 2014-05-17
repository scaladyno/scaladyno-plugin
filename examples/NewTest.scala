object NewTest {
  def main(args: Array[String]): Unit = {
  	method_which_is_never_called()
  }
  def method_which_is_never_called():Int = {
  	val x = new Foo()
  	x.field1
  	x.field2
  	val y = new Nsc()
  	x.noSuchMethod()
  	val z = x.m()
  	z.aefwdsaf()
  	y.m()
  	y.afdlskz
  	val a = x + y;
  	return 5
  }

}