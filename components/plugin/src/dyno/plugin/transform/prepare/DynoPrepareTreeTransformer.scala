package dyno.plugin
package transform
package prepare
import scala.tools.nsc.transform.InfoTransform
import scala.reflect.internal.Flags._
import scala.reflect.internal.util.Position

trait DynoPrepareTreeTransformer extends InfoTransform {
  this: DynoPreparePhase =>

  import global._
  import definitions._
  import helper._

  override def transformInfo(sym: Symbol, info: Type): Type = {
    if ((sym.isClass || sym.isTrait || sym.isModule) && currentRun.compiles(sym))
      for (mbr <- info.decls if mbr.tpe.isErroneous)
        info.decls.unlink(mbr)

    info
  }

  class TreePreparer(unit: CompilationUnit) extends TypingTransformer(unit) {
    override def transform(tree: Tree): Tree = { //[T <: Tree]
      //debug:
      //println("prep: " + tree.getClass + " tpe: "+ tree.tpe + " tree: " +tree)
      tree match {
        //tpe is a field of all trees => it is a "Type"
        //isErroneous can be applied to any tree through TreeContextApiImpl which is implemented by tree
        //  def isErroneous = (tpe ne null) && tpe.isErroneous
        //  def isErrorTyped = (tpe ne null) && tpe.isError -> not recursive
        
        case DefDef(_, _, _, _, tpt, rhs) if (rhs.isErroneous || tpt.isErroneous) =>
           localTyper.typed(gen.mkAttributedIdent(Predef_???))

        case ValDef(mods, name, tpt, rhs) if (rhs.isErroneous || tpt.isErroneous) =>
           localTyper.typed(gen.mkAttributedIdent(Predef_???))
        case Assign(lhs, rhs) if (lhs.isErroneous || rhs.isErroneous)=>
          EmptyTree
        case Block(stmts, expr) =>
          val expr1 = if (expr.isErroneous) gen.mkAttributedIdent(Predef_???) else expr
          val tree1 = Block(stmts.filterNot(x => x.isErroneous), expr1)
          val tree2 = localTyper.typed(tree1)
          val tree3 = super.transform(tree2)
          tree3
        case Select(qualif, _) if (qualif.isErroneous) =>
          EmptyTree
        case expr if expr.isErroneous =>
          localTyper.typed(gen.mkAttributedIdent(Predef_???))
        //case x if x.isErroneous =>
          //EmptyTree
          //this.
          //treeCopy.Block(tree, transformStats(stats.filter(!_.tpe.isErroneous), currentOwner), transform(expr))
        case x =>
          super.transform(x)
      }
    }
  }

  object ErrorCollector extends Traverser {

    var buffer: List[(Position, String)] = Nil

    override def traverse(tree: Tree) = tree match {
      case _ =>
        // if there's any error for tree.pos, store it in the buffer
        super.traverse(tree)
    }

    def collect(tree: Tree): List[(Position, String)] = {
      buffer = Nil
      traverse(tree)
      buffer
    }
  }
}
