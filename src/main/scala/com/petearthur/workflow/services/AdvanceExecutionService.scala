package com.petearthur.workflow.services

import cats.effect.Effect
import com.petearthur.workflow.data.access.ExecutionAdvancer
import com.petearthur.workflow.data.model.{AdvanceExecutionRequest, Execution}
import io.circe.Json
import org.http4s.{HttpService, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.circe.syntax._

class AdvanceExecutionService [F[_]: Effect](da: ExecutionAdvancer) extends Http4sDsl[F] {
  val service: HttpService[F] = HttpService[F] {
    case req @ POST -> Root / "advance" =>
      req.decode[Json](parseRequest(_).flatMap(da.advance).fold(failure)(success))
  }

  // Attempt to parse an execution request from the entity.
  // I'm cutting corners with the logging here - in a production system, we would want to return this information
  // to the user (e.g. Incorrect format...), and do some logging, rather than just ignoring the error.
  private def parseRequest(json: Json): Option[AdvanceExecutionRequest] = AdvanceExecutionRequest.fromJson(json).fold(_ => None, Some(_))

  // We require our workflow execution encoder in implicit scope
  import io.circe.generic.auto._
  import io.circe.java8.time._

  private def success(execution: Execution): F[Response[F]] = Created(execution.asJson)
  private def failure: F[Response[F]] = {
    BadRequest("Could not create workflow execution")
  }
}
