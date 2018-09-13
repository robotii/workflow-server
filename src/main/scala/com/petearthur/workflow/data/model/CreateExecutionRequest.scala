package com.petearthur.workflow.data.model

import io.circe.Json
import scala.util.Try

/**
  * Represents a request to create a new workflow execution
  */
case class CreateExecutionRequest(workflowId: Long) {
  def submit(executionId: Long): Execution = Execution(executionId, this.workflowId, Execution.nowUtc, 0, isActive = true)
}

object CreateExecutionRequest {
  // Json utility methods - we just need to keep track of a few implicit converters.
  // Beware, as intelliJ sometimes incorrectly assumes these are not needed!
  // asJson method:
  import io.circe.syntax._
  // All other case classes:
  import io.circe.generic.auto._

  def toJson(createExecution: CreateExecutionRequest): Json = createExecution.asJson

  def fromJson(json: Json): Try[CreateExecutionRequest] = json.as[CreateExecutionRequest].toTry
}
