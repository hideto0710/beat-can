package com.github.hideto0710.beat_can

import scala.beans.BeanProperty

sealed trait BeatRequestStatus
case object Attend extends BeatRequestStatus
case object Leave extends BeatRequestStatus
case object NilBeatRequestStatus extends BeatRequestStatus

object BeatRequestStatus {
  def fromId(id: Int): BeatRequestStatus = {
    id match {
      case 1 => Attend
      case -1 => Leave
      case _ => NilBeatRequestStatus
    }
  }
}

case class BeatRequest(
  @BeanProperty var client: String,
  @BeanProperty var user: String,
  @BeanProperty var password: String,
  @BeanProperty var status: Int
) {
  def this() = this(client = "", user = "", password = "", status = 0)
}
