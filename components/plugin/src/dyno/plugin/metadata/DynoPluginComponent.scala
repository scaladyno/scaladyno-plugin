package dyno.plugin.metadata

import scala.language.implicitConversions
import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.transform.TypingTransformers

trait DynoPluginComponent extends PluginComponent with TypingTransformers { self =>
  import global._
  import Flag._

  val helper: DynoHelper { val global: self.global.type }
  import helper._
  def dynolog(msg: => String) = if (settings.log.value.contains(phaseName)) log(msg)
}
