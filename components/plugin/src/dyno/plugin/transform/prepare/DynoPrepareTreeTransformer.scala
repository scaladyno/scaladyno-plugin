package dyno.plugin
package transform
package prepare

import scala.reflect.internal.Flags._

trait DynoPrepareTreeTransformer {
  this: DynoPreparePhase =>

  import global._
  import definitions._
  import helper._

  class TreePreparer(unit: CompilationUnit) extends TypingTransformer(unit) {
    override def transform(tree: Tree): Tree = tree match {
//        // remove redundant asInstanceOf-s introduced by tailcalls
//      case AsInstanceOf(expr, tpe) if expr.tpe =:= tpe.tpe && tpe.tpe.typeSymbol.isDynoClass =>
//        dynolog("removed redundant asInstanceOf:")
//        dynolog("  tree: " + tree)
//        dynolog("  expr: " + tree)
//        dynolog("  tree.tpe: " + tree.tpe)
//        dynolog("  expr.tpe: " + expr.tpe)
//        dynolog("  tpe.tpe: " + tpe.tpe)
//        transform(expr)
      case _ =>
        super.transform(tree)
    }
  }
}