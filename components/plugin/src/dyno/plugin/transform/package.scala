package dyno.plugin
package transform

import metadata._

import prepare._

/** Removes the known problems in the Scala ASTs that cause the plugin
 *  to malfunction. For example: tailcall introduces .asInstancOf-s that
 *  prevent proper transformation and thus crash in the backend. */
trait DynoPreparePhase extends
    DynoPluginComponent
    with scala.tools.nsc.transform.Transform
    with DynoPrepareTreeTransformer { self =>
  import global._
  def dynoPreparePhase: StdPhase
  def afterPrepare[T](op: => T): T = global.exitingPhase(dynoPreparePhase)(op)
  def beforePrepare[T](op: => T): T = global.enteringPhase(dynoPreparePhase)(op)

  def revertReporter(): Unit

  override def newTransformer(unit: CompilationUnit): Transformer = new Transformer {
    override def transform(tree: Tree) = {
      // [error] /Users/xeno_by/Projects/dyno/tests/correctness/test/dyno/partest/CompileTest.scala:30: [dyno-verify] tree not typed: $anonfun.this.apply$mcV$sp()
      // [error]       Console.withErr(pa) {
      // [error]                           ^
      // [error] one error found
      // TODO: I've no idea why this happens - looks like an invalid tree produced by scalac
      //tree.foreach(tree => if (tree.tpe == null && !tree.toString.contains("apply$mcV$sp")) unit.error(tree.pos, s"[dyno-verify] tree not typed: $tree"))
      //println("new treepreparer")
      new TreePreparer(unit)(tree)
    }
  }
}