package dyno.plugin.metadata

import scala.tools.nsc.Global

trait DynoHelper extends DynoInfo with DynoDefs {
  val global: Global
  var flag_passive: Boolean = false
}
