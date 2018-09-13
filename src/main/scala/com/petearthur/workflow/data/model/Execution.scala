package com.petearthur.workflow.data.model

import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import io.circe.Json

import scala.util.Try

case class Execution(
                      createExecutionRequest: CreateExecutionRequest,
                      createdDate: LocalDateTime,
                      step: Long,
                      isActive: Boolean) {

  private val workflowId : Long = createExecutionRequest.workflowId
  def cancel: Execution = this.copy(isActive = false)
  def advance: Execution = this.copy(step = step + 1)
  def inactive: Boolean = isActive
  def active: Boolean = !inactive
}

// Represents a workflow within the system
object Execution {
  // Convenience method to get the current date/time in UTC
  def nowUtc: LocalDateTime = ZonedDateTime.now(ZoneId.of("Etc/UTC")).toLocalDateTime

  // Json utility methods
  // asJson method:
  import io.circe.syntax._
  // All other case classes:
  import io.circe.generic.auto._
  // LocalDateTime:
  import io.circe.java8.time._

  def toJson(execution: Execution): Json = execution.asJson

  def fromJson(json: Json): Try[Execution] = json.as[Execution].toTry
}
