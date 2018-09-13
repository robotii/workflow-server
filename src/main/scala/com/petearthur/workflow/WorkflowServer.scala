package com.petearthur.workflow

import cats.effect.{Effect, IO}
import com.petearthur.workflow.data.access.{MemoryExecutionAdvancer, MemoryExecutionCreator, MemoryWorkflowCreator}
import com.petearthur.workflow.data.model.{Execution, Workflow}
import com.petearthur.workflow.services.{AdvanceExecutionService, CreateExecutionService, CreateWorkflowService}
import fs2.StreamApp
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

// The application server - largely unchanged from the auto-generated HelloWorldServer provided by http4s.
// The server back end is "blaze" - the default provided by http4s.
// If you are unfamiliar with http4s, it is a very cool scala HTTP framework,
// which makes heavy use of functional constructs such as higher-kinded types.
// This project follows the architecture used in the quick start guide (see https://http4s.org/v1.0/).
object WorkflowServer extends StreamApp[IO] {

  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) = ServerStream.stream[IO]
}

object ServerStream {
  // These two hashmaps will serve as the in memory storage
  val workflows = collection.mutable.HashMap[Long, Workflow]()
  val executions = collection.mutable.HashMap[Long, Execution]()

  // Injected Data Access instances
  val workflowCreator = new MemoryWorkflowCreator(workflows)
  val executionCreator = new MemoryExecutionCreator(workflows, executions)
  val executionAdvancer = new MemoryExecutionAdvancer(executions)

  // Services
  def createWorkflowService[F[_] : Effect]: HttpService[F] = new CreateWorkflowService[F](workflowCreator).service

  def createExecutionService[F[_] : Effect]: HttpService[F] = new CreateExecutionService[F](executionCreator).service

  def advanceExecutionService[F[_] : Effect]: HttpService[F] = new AdvanceExecutionService[F](executionAdvancer).service

  def stream[F[_] : Effect](implicit ec: ExecutionContext) =
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(createWorkflowService, "/")
      .mountService(createExecutionService, "/")
      .mountService(advanceExecutionService, "/")
      .serve
}
