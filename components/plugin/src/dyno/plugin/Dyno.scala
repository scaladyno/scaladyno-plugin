package dyno.plugin

import scala.tools.nsc.Global
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent

import transform._
import metadata._

/** Main miniboxing class */
class Dyno(val global: Global) extends Plugin { plugin =>
  // import global._

  val name = "dyno"
  val description = "provides value class functionality"

  val components = List[PluginComponent](
    DynoPreparePhaseObj
  )

  lazy val helper = new { val global: plugin.global.type = plugin.global } with DynoHelper

  override def processOptions(options: List[String], error: String => Unit) {
    for (option <- options) {
      if (option == "passive")
        helper.flag_passive = true
      else
        error("Dyno: option not understood: " + option)
    }
  }

  private object DynoPreparePhaseObj extends DynoPreparePhase { self =>
    val global: Dyno.this.global.type = Dyno.this.global
    val runsAfter = List("typer")
    override val runsRightAfter = Some("typer")
    val phaseName = Dyno.this.name + "-prepare"

    import global._
    val helper: plugin.helper.type = plugin.helper

    var dynoPreparePhase : StdPhase = _
    override def newPhase(prev: scala.tools.nsc.Phase): StdPhase = {
      dynoPreparePhase = new Phase(prev)
      dynoPreparePhase
    }
  }
}
