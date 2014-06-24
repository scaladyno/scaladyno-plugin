package dyno.plugin

import scala.tools.nsc.Global
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent
import transform._
import metadata._
import scala.tools.nsc.reporters.AbstractReporter
import scala.tools.nsc.reporters.Reporter
import scala.reflect.internal.util.Position
import dyno.plugin.transform.prepare.DynoPrepareTreeTransformer
import collection.mutable.Map
import scala.reflect.internal.Phase

object ErrorList {
}

/** Main scaladyno class */
class Dyno(val global: Global) extends Plugin { plugin =>
  import global._

  val errorList:Map[Position, String] = Map.empty //a maping which collects are erros which have been converted to warnings, their position is used as key
  var dynoPreparePhaseId:Int = 0

  val name = "dyno"
  val description = "provides value class functionality"

  val components = List[PluginComponent](
    DynoPreparePhaseObj
  )

  /*
   *  Through reflection we change the default reporter of the compiler such that it only yields warnings and no errors for typing and naming errors.
   *  Not issuing errors enables us to continue the compilation process and implement special behavior for branches with erroneous types.
   */
  // global.settings.skip.tryToSetColon(List("refchecks"))
  global.reporter = new OurHackedReporter(global.reporter)

  /*
   * A reporter wrapper which will convert type errors into warnings to avoid stopping the compilation
   */
  class OurHackedReporter(val orig: Reporter) extends Reporter {
    val super_info0 = orig.getClass.getMethod("info0", classOf[Position], classOf[String], classOf[Severity], classOf[Boolean])
    def info0(pos: Position, msg: String, severity: Severity, force: Boolean): Unit = {
      val (super_severity, super_msg) = severity match {
        case ERROR =>
          //only mute errors which results from identifier, method or field resolution which happen during the namer or typer phase
          if (global.phase.name == global.analyzer.typerFactory.phaseName || global.phase.name == global.analyzer.namerFactory.phaseName) {
            errorList.put(pos, msg)
            (orig.WARNING, "[suppressed error] " + msg)
          } else
            (orig.ERROR, msg)
        //don't change the behaviour of all other severities
        case WARNING => (orig.WARNING, msg)
        case INFO => (orig.INFO, msg)
        case _ => (orig.INFO, msg)
      }
      super_info0.invoke(orig, pos, super_msg, super_severity, force.asInstanceOf[AnyRef])
    }
  }

  lazy val helper = new { val global: plugin.global.type = plugin.global } with DynoHelper

  override def processOptions(options: List[String], error: String => Unit) {
    for (option <- options) {
      if (option == "passive")
        helper.flag_passive = true
      else
        error("Dyno: option not understood: " + option)
    }
  }

  /*
   * general setting for the compiler plugin
   */
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
      dynoPreparePhaseId = dynoPreparePhase.id
      dynoPreparePhase
    }
    def errorList:Map[Position, String] = Dyno.this.errorList

    def revertReporter(): Unit = {
      global.reporter match {
        case rep: OurHackedReporter =>
          global.reporter = rep.orig
        case _ =>
      }
    }
  }
}