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

/** Main miniboxing class */
class Dyno(val global: Global) extends Plugin { plugin =>
  import global._
  
  val errorList:Map[Position, String] = Map.empty
  var dynoPreparePhaseId:Int = 0
  
  val name = "dyno"
  val description = "provides value class functionality"

  val components = List[PluginComponent](
    DynoPreparePhaseObj
  )

  // global reporter hack
  global.reporter = new OurHackedReporter(global.reporter)
  println("Reporter hacked...")
  /*
   * A reporter wrapper which will concert type errors into warnings to avoid stopping the compilation
   */
  class OurHackedReporter(orig: Reporter) extends Reporter {
    val super_info0 = orig.getClass.getMethod("info0", classOf[Position], classOf[String], classOf[Severity], classOf[Boolean])
    
    def info0(pos: Position, msg: String, severity: Severity, force: Boolean): Unit = {
      val (super_severity, super_msg) = severity match {
        case ERROR =>
          if (global.phase.name == global.analyzer.typerFactory.phaseName || global.phase.name == global.analyzer.namerFactory.phaseName) {
            errorList.put(pos, "[suppressed error] " + msg)
            (orig.WARNING, "[suppressed error] " + msg)
          } else
            (orig.ERROR, msg+" in phase:"+global.phase.name)
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
  }
}