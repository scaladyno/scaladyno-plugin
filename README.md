scaladyno-plugin
================

scaladyno is an attempt to make scala a more dynamic language :)

Let's take the following file:
```
$ cat demo.scala 

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
```

And try to compile it:
```
$ scalac demo.scala 
demo.scala:10: error: not found: type NoSuchClass
      val x = new NoSuchClass
                  ^
one error found
```

Of course, it doesn't compile. But what if we use ScalaDyno?

```
$ ../dy-scalac demo.scala 
demo.scala:10: warning: [suppressed error] not found: type NoSuchClass
      val x = new NoSuchClass
                  ^
one warning found
```

Wow, it compiles :). Now let's try to run it:

```
$ ../dy-scala dyno.demo.Test
line> ok
okay

$ ../dy-scala dyno.demo.Test
line> boo
java.lang.RuntimeException: Deferred compile-time error(s):

demo.scala:10: not found: type NoSuchClass
      val x = new NoSuchClass
                  ^
Stacktrace:
	at scala.sys.package$.error(package.scala:27)
	at dyno.demo.Test$.main(demo.scala:7)
	at dyno.demo.Test.main(demo.scala)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at scala.reflect.internal.util.ScalaClassLoader$$anonfun$run$1.apply(ScalaClassLoader.scala:68)
	at scala.reflect.internal.util.ScalaClassLoader$class.asContext(ScalaClassLoader.scala:31)
	at scala.reflect.internal.util.ScalaClassLoader$URLClassLoader.asContext(ScalaClassLoader.scala:99)
	at scala.reflect.internal.util.ScalaClassLoader$class.run(ScalaClassLoader.scala:68)
	at scala.reflect.internal.util.ScalaClassLoader$URLClassLoader.run(ScalaClassLoader.scala:99)
	at scala.tools.nsc.CommonRunner$class.run(ObjectRunner.scala:22)
	at scala.tools.nsc.ObjectRunner$.run(ObjectRunner.scala:39)
	at scala.tools.nsc.CommonRunner$class.runAndCatch(ObjectRunner.scala:29)
	at scala.tools.nsc.ObjectRunner$.runAndCatch(ObjectRunner.scala:39)
	at scala.tools.nsc.MainGenericRunner.runTarget$1(MainGenericRunner.scala:72)
	at scala.tools.nsc.MainGenericRunner.process(MainGenericRunner.scala:94)
	at scala.tools.nsc.MainGenericRunner$.main(MainGenericRunner.scala:103)
	at scala.tools.nsc.MainGenericRunner.main(MainGenericRunner.scala)
```

So, where's the magic? It's in ScalaDyno:
```
$ ../dy-scalac -Xprint:typer,dyno demo.scala 
demo.scala:10: warning: [suppressed error] not found: type NoSuchClass
      val x = new NoSuchClass
                  ^
[[syntax trees at end of                     typer]] // demo.scala
package dyno.demo {
  object Test extends scala.AnyRef {
    def <init>(): dyno.demo.Test.type = {
      Test.super.<init>();
      ()
    };
    def main(args: Array[String]): Unit = {
      scala.this.Predef.print("line> ");
      if (scala.io.StdIn.readLine().!=("boo"))
        scala.this.Predef.println("okay")
      else
        {
          val <x: error>: <error> = new <NoSuchClass: error>();
          <x: error>.<foo: error>()
        }
    }
  }
}

[[syntax trees at end of              dyno-prepare]] // demo.scala
package dyno.demo {
  object Test extends scala.AnyRef {
    def <init>(): dyno.demo.Test.type = {
      Test.super.<init>();
      ()
    };
    def main(args: Array[String]): Unit = {
      scala.this.Predef.print("line> ");
      if (scala.io.StdIn.readLine().!=("boo"))
        scala.this.Predef.println("okay")
      else
        `package`.this.error("Deferred compile-time error(s):\n\ndemo.scala:10: not found: type NoSuchClass\n      val x = new NoSuchClass\n                  ^\nStacktrace:")
    }
  }
}

one warning found
```
