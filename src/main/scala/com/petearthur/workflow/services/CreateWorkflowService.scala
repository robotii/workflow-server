package com.petearthur.workflow.services

import cats.effect.Effect
import com.petearthur.workflow.data.access.WorkflowCreator
import com.petearthur.workflow.data.model.{CreateWorkflowRequest, Workflow}
import io.circe.Json
import org.http4s.{HttpService, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.circe.syntax._

class CreateWorkflowService[F[_]: Effect](da: WorkflowCreator) extends Http4sDsl[F] {
  val service: HttpService[F] = HttpService[F] {
    case req @ POST -> Root / "create" =>
      req.decode[Json](parseRequest(_).flatMap(da.create).fold(failure)(success))
  }

  // Attempt to parse a create workflow request from the entity.
  // I'm cutting corners with the logging here - in a production system, we would want to return this information
  // to the user (e.g. Incorrect format...), and do some logging, rather than just ignoring the error.
  private def parseRequest(json: Json): Option[CreateWorkflowRequest] = CreateWorkflowRequest.fromJson(json).fold(_ => None, Some(_))

  // We require our workflow encoder in implicit scope
  import io.circe.generic.auto._
  import io.circe.java8.time._

  private def success(workflow: Workflow): F[Response[F]] = Created(workflow.asJson)
  private def failure: F[Response[F]] = {
    BadRequest("Could not create workflow")
  }
}
