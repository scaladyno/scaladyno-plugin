package dyno.plugin
package transform
package prepare
import scala.tools.nsc.transform.InfoTransform
import scala.reflect.internal.Flags._
import scala.reflect.internal.util.Position
import collection.mutable.Map


trait DynoPrepareTreeTransformer extends InfoTransform {
  this: DynoPreparePhase =>

  import global._
  import definitions._
  import helper._
  
  def errorList:Map[Position, String]
  
  override def transformInfo(sym: Symbol, info: Type): Type = {
    if ((sym.isClass || sym.isTrait || sym.isModule) && currentRun.compiles(sym))
      for (mbr <- info.decls if mbr.tpe.isErroneous)
        info.decls.unlink(mbr)

    info
  }
  class TreePreparer(unit: CompilationUnit) extends TypingTransformer(unit) {
    def apply(tree:Tree):Tree = { //called on the first iteration of tranform
      println("Collected errors:")
      errorList.foreach(println(_))
      println("end of list")
      transform(tree)
    }
    
    def treeToErrString(tree:Tree):String = {
      val errors = ErrorCollector.collect(tree)
      val result = errors.map{ case (pos, str) => Position.formatMessage(pos, str, false)}.mkString("\n")
      println(result)
      result
    }
    
    def treeToException(tree:Tree):Tree = {
      val str = treeToErrString(tree)
      val tree0 = gen.mkSysErrorCall(str) //factory for creating trees
      localTyper.typed(tree0) //localTyper -> keep track of owner and scope to correctly type new nodes
    }
    
    override def transform(tree: Tree): Tree = { //[T <: Tree]
      //println("prep: " + tree.getClass + " tpe: "+ tree.tpe + " tree: " +tree)
      //transform1 => do the matching
      tree match {
        //tpe is a field of all trees => it is a "Type"
        //isErroneous can be applied to any tree through TreeContextApiImpl which is implemented by tree
        //  def isErroneous = (tpe ne null) && tpe.isErroneous
        //  def isErrorTyped = (tpe ne null) && tpe.isError -> not recursive
        /* 
         * Error need to  bubble up to this level where it is safe to throw an exception
         */
        case DefDef(_, _, _, _, tpt, rhs) if (rhs.isErroneous || tpt.isErroneous) =>
          treeToException(tree)
         // localTyper.typed(gen.mkAttributedIdent(Predef_???))
        case ValDef(mods, name, tpt, rhs) if (rhs.isErroneous || tpt.isErroneous) =>
          treeToException(tree)
        //case Assign(lhs, rhs) if (lhs.isErroneous || rhs.isErroneous)=>
          //EmptyTree
//        case Block(stmts, expr) =>
//          println(ErrorCollector.collect(tree))
//          val expr1 = if (expr.isErroneous) treeToException(tree) else expr
//          val tree1 = Block(stmts.map(transform(_)), expr1)
//          val tree2 = localTyper.typed(tree1)
//          val tree3 = super.transform(tree2)
//          tree
        case x if (x.isErroneous) =>
          treeToException(tree)
        case x =>
          super.transform(x)
      }
    }
  }
  object ErrorCollector extends Traverser {
    var buffer: List[(Position, String)] = Nil

    def add (item:(Position, String)) {
      buffer = item :: buffer
    }
    override def traverse(tree: Tree) = tree match {
      case _ =>
        errorList.get(tree.pos) match {
          case None =>
          case Some(err) => buffer ::= (tree.pos, err)
        }// if there's any error for tree.pos, store it in the buffer
        super.traverse(tree)
    }

    def collect(tree: Tree): List[(Position, String)] = {
      buffer = Nil
      traverse(tree)
      buffer
    }
  }

}
  