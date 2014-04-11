package dyno.plugin.metadata

import scala.tools.nsc.plugins.PluginComponent
import scala.language.implicitConversions
import scala.reflect.internal.Flags._

trait DynoInfo {
  this: DynoHelper =>

  import global._
  import treeInfo._
  import definitions._

  /*
   * implicit classes have implicit wrapper functions
   */
  implicit class RichTree(tree: Tree) {
    // todo
  }

  implicit class RichSymbol(sym: Symbol) {
    // todo
  }

  implicit class RichType(tpe: Type) {
    // todo
  }
}