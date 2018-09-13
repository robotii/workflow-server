package com.petearthur.workflow.data.model

import io.circe.Json

import scala.util.{Random, Try}

/**
  * Represents a request to create a new workflow
  */
case class CreateWorkflowRequest(stepCount: Long) {
  def submit: Workflow = Workflow(Random.nextLong(), Workflow.nowUtc, stepCount, isActive = true)
}
object CreateWorkflowRequest {
  // Json utility methods - we just need to keep track of a few implicit converters.
  // Beware, as intelliJ sometimes incorrectly assumes these are not needed!
  // asJson method:
  import io.circe.syntax._
  // All other case classes:
  import io.circe.generic.auto._
  def toJson(createWorkflow: CreateWorkflowRequest): Json = createWorkflow.asJson
  def fromJson(json: Json): Try[CreateWorkflowRequest] = json.as[CreateWorkflowRequest].toTry
}

