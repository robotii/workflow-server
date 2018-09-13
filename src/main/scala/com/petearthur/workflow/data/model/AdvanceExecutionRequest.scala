package com.petearthur.workflow.data.model

import io.circe.Json

import scala.util.Try

/**
  * Represents a request to advance a workflow execution
  */
case class AdvanceExecutionRequest(executionId: Long) {
  def submit(execution: Execution) : Execution = execution.advance
}
object AdvanceExecutionRequest {
  // Json utility methods - we just need to keep track of a few implicit converters.
  // Beware, as intelliJ sometimes incorrectly assumes these are not needed!
  // asJson method:
  import io.circe.syntax._
  // All other case classes:
  import io.circe.generic.auto._
  def toJson(createExecution: AdvanceExecutionRequest): Json = createExecution.asJson
  def fromJson(json: Json): Try[AdvanceExecutionRequest] = json.as[AdvanceExecutionRequest].toTry
}
