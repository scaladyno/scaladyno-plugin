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
        //        // remove redundant asInstanceOf-s introduced by tailcalls
        //      case AsInstanceOf(expr, tpe) if expr.tpe =:= tpe.tpe && tpe.tpe.typeSymbol.isDynoClass =>
        //        dynolog("removed redundant asInstanceOf:")
        //        dynolog("  tree: " + tree)
        //        dynolog("  expr: " + tree)
        //        dynolog("  tree.tpe: " + tree.tpe)
        //        dynolog("  expr.tpe: " + expr.tpe)
        //        dynolog("  tpe.tpe: " + tpe.tpe)
        //        transform(expr)

        //Tree
          //trait TermTree extends Tree
            //case class Block(stats: List[Tree], expr: Tree) extends TermTree
            //case class Alternative(trees: List[Tree]) extends TermTree
            //case class Star(elem: Tree) extends TermTree
            //case class UnApply(fun: Tree, args: List[Tree]) extends TermTree
            //case class ArrayValue(elemtpt: Tree, elems: List[Tree]) extends TermTree
            //case class Assign(lhs: Tree, rhs: Tree) extends TermTree
            //case class AssignOrNamedArg(lhs: Tree, rhs: Tree) extends TermTree
            //case class If(cond: Tree, thenp: Tree, elsep: Tree) extends TermTree
            //case class Match(selector: Tree, cases: List[CaseDef]) extends TermTree
            //case class Try(block: Tree, catches: List[CaseDef], finalizer: Tree) extends TermTree
            //case class Throw(expr: Tree) extends TermTree
            //case class Typed(expr: Tree, tpt: Tree) extends TermTree
            //abstract class GenericApply extends TermTree
            //case class Super(qual: Tree, mix: TypeName) extends TermTree
            //case class ReferenceToBoxed(ident: Ident) extends TermTree
            //case class Literal(value: Constant) extends TermTree
            //case object EmptyTree extends TermTree
            //case class LabelDef(name: TermName, params: List[Ident], rhs: Tree) extends DefTree with TermTree
            //many in common with SymTree
          //trait TypTree extends Tree
            //case class SingletonTypeTree(ref: Tree) extends TypTree
            //case class CompoundTypeTree(templ: Template) extends TypTree
            //case class AppliedTypeTree(tpt: Tree, args: List[Tree]) extends TypTree
            //case class TypeBoundsTree(lo: Tree, hi: Tree) extends TypTree
            //case class ExistentialTypeTree(tpt: Tree, whereClauses: List[MemberDef]) extends TypTree
            //case class TypeTree() extends TypTree
          //abstract class SymTree extends Tree
            //trait RefTree extends SymTree with NameTree with RefTreeApi
              //case class Select(qualifier: Tree, name: Name) extends RefTree
              //case class Ident(name: Name) extends RefTree
              //case class SelectFromTypeTree(qualifier: Tree, name: TypeName) extends RefTree with TypTree
            //abstract class DefTree extends SymTree with NameTree with DefTreeApi
              //abstract class MemberDef extends DefTree
                //case class PackageDef(pid: RefTree, stats: List[Tree]) extends MemberDef
                //abstract class ImplDef extends MemberDef
                  //case class ClassDef(mods: Modifiers, name: TypeName, tparams: List[TypeDef], impl: Template) extends ImplDef
                  //case class ModuleDef(mods: Modifiers, name: TermName, impl: Template) extends ImplDef
                //abstract class ValOrDefDef extends MemberDef
                  //case class ValDef(mods: Modifiers, name: TermName, tpt: Tree, rhs: Tree) extends ValOrDefDef
                  //case class DefDef(mods: Modifiers, name: TermName, tparams: List[TypeDef], vparamss: List[List[ValDef]], tpt: Tree, rhs: Tree) extends ValOrDefDef
                //case class TypeDef(mods: Modifiers, name: TypeName, tparams: List[TypeDef], rhs: Tree) extends MemberDef
              //case class LabelDef(name: TermName, params: List[Ident], rhs: Tree) extends DefTree with TermTree
              //case class Bind(name: Name, body: Tree) extends DefTree
            //case class Import(expr: Tree, selectors: List[ImportSelector])
            //case class Template(parents: List[Tree], self: ValDef, body: List[Tree])
            //case class Function(vparams: List[ValDef], body: Tree) extends SymTree with TermTree 
            //case class Return(expr: Tree) extends SymTree with TermTree
            //case class ApplyDynamic(qual: Tree, args: List[Tree]) extends SymTree with TermTree
            //case class This(qual: TypeName) extends SymTree with TermTree
          //trait NameTree extends Tree
          //  => extends RefTree and DefTree
          //case class CaseDef(pat: Tree, guard: Tree, body: Tree)
          //case class Annotated(annot: Tree, arg: Tree)


        /*case b@Block(stats, result) =>
          println("my case")
          val stats2 = stats.filter(_.tpe.isErroneous)
          b
          //localTyper.typed(Block(stats2, result))*/

          // ==> traverser : wa

        case x if x.tpe.isErroneous =>
          //tpe is a field of all trees => it is a Type
          //iserroneous can be applied to any tree through TreeContextApiImpl which is implemented by tree
          x match {
            case _: TermTree => println("erroneous TermTree => replaced by EmptyTree: "+x); EmptyTree
            case _: TypTree => println("isErroneous Typtree: "+x); x
            case _: CaseDef => println("case class CaseDef..."); x
            case _: Annotated => println("case class Annotated..."); x
          }
          println(x+" is isErroneous")
          //TypeTree()
          EmptyTree//.asInstanceOf[T]
        case PackageDef(pid: RefTree, stats: List[Tree]) =>
          PackageDef(pid, stats.map(transform(_)))
        case ModuleDef(mods, name, impl) =>
          //Template(parents: List[Tree], self: ValDef, body: List[Tree])
          ModuleDef(mods, name, Template(impl.parents.map(transform(_)), impl.self, impl.body.map(transform(_))))
        case DefDef(mods, name, tparams, vparamss: List[List[ValDef]], tpt: Tree, rhs: Tree) =>
          if (rhs.isErroneous || rhs.tpe.isErroneous || tpt.isErroneous || tpt.tpe.isErroneous) {
            println("replace DefDef with empty")
            EmptyTree
          }
          DefDef(mods, name, tparams.map(transform(_).asInstanceOf[TypeDef]), vparamss.map(_.map(transform(_).asInstanceOf[ValDef])), transform(tpt), transform(rhs))
        /*case c @ ClassDef(mods, name, tparams, impl) =>//(mods: Modifiers, name: TypeName, tparams: List[TypeDef], impl: Template)
          println("class def:")
          c*/
        case Block(stats, expr) =>
          Block(stats.map(transform(_)), transform(expr))
        case ValDef(mods, name, tpt, rhs) =>//ValdDef(mods: Modifiers, name: TermName, tpt: Tree, rhs: Tree)
          if (rhs.isErroneous || rhs.tpe.isErroneous || tpt.isErroneous || tpt.tpe.isErroneous) {
            println("replace ValDef with empty")
            return Literal(Constant(()))//Block(Nil, Literal(Constant(())))
          }

          val caca = ValDef(mods, name, transform(tpt), transform(rhs))
          println("ValDef done tpt:"+ tpt + " rhs: " + rhs )
          caca
        case x => //catch all
          println("last check: "+x+" of type: "+x.tpe)
          x
          //super.transform(tree)
      }
    }  
  }
}
