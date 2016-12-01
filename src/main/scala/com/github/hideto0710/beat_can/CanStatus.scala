package com.github.hideto0710.beat_can

sealed abstract class CanStatus(val text: String)
case object NotYet extends CanStatus("having_breakfast")
case object Working extends CanStatus("working")
case object Already extends CanStatus("resting")
case object NilCanStatus$ extends CanStatus("")

object CanStatus {
  def fromStatusId(message: String): CanStatus = {
    message match {
      case text if text == NotYet.text => NotYet
      case text if text == Working.text => Working
      case text if text == Already.text => Already
      case _ => NilCanStatus$
    }
  }
}
