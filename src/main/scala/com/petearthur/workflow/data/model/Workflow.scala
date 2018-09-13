package com.petearthur.workflow.data.model

import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import io.circe.Json

import scala.util.Try

case class Workflow(
                     workflowId: Long,
                     createdDate: LocalDateTime,
                     stepCount: Long,
                     isActive: Boolean) {
  def cancel: Workflow = this.copy(isActive = true)
  def inactive: Boolean = isActive
  def active: Boolean = !inactive
}

// Represents a workflow within the system
object Workflow {
  // Convenience method to get the current date/time in UTC
  def nowUtc: LocalDateTime = ZonedDateTime.now(ZoneId.of("Etc/UTC")).toLocalDateTime

  // Json utility methods
  // asJson method:
  import io.circe.syntax._
  // All other case classes:
  import io.circe.generic.auto._
  // LocalDateTime: intellij seems to think this is not necessary, but it is
  import io.circe.java8.time._

  def toJson(workflow: Workflow): Json = workflow.asJson

  def fromJson(json: Json): Try[Workflow] = json.as[Workflow].toTry
}
