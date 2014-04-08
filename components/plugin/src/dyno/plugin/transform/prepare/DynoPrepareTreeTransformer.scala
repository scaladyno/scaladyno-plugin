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
    override def transform(tree: Tree): Tree = { //[T <: Tree]
      //debug:
      println("prep: " + tree.getClass + " tpe: "+ tree.tpe + " tree: " +tree)
      tree match {
        //tpe is a field of all trees => it is a Type
        //isErroneous can be applied to any tree through TreeContextApiImpl which is implemented by tree

        case ValDef(mods, name, tpt, rhs) if (rhs.isErroneous || rhs.tpe.isErroneous || tpt.isErroneous || tpt.tpe.isErroneous) =>//ValdDef(mods: Modifiers, name: TermName, tpt: Tree, rhs: Tree)
           EmptyTree
        case x if (x.isErroneous || x.tpe.isErroneous) =>
          //this.
          //treeCopy.Block(tree, transformStats(stats.filter(!_.tpe.isErroneous), currentOwner), transform(expr))
        case x =>
          super.transform(x)
      }
    }  
  }
}
