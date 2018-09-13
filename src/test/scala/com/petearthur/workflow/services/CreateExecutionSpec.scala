package com.petearthur.workflow.services

import cats.effect.IO
import com.petearthur.workflow.data.access.ExecutionCreator
import com.petearthur.workflow.data.model.{CreateExecutionRequest, Execution, Workflow}
import org.scalatest.{BeforeAndAfter, FunSpec}
import org.http4s._
import org.http4s.implicits._

class CreateExecutionSpec extends FunSpec with BeforeAndAfter {
  describe("CreateExecutionService") {
    var factory: TestDataAccessFactory = new TestDataAccessFactory()
    before {
      // Each unit test will use a new instance of the test factory, with a clean hash map each run.
      factory = new TestDataAccessFactory()
      factory.workflows.put(testWorkflow.workflowId, testWorkflow)
    }
    it("Responds with 201 Created upon success") {
      val response: Response[IO] = postAndRespond(factory.executionCreator)
      assert(response.status == Status.Created)
    }
    it("Responds with 400 Bad Request upon failure") {
      val response = postAndRespond(factory.executionCreatorFail)
      assert(response.status == Status.BadRequest)
    }
    it("Responds with 404 Not Found upon receiving an unexpected HTTP request") {
      // Let's use a GET
      val request = Request[IO](Method.GET, Uri.uri("/execute"))
      val response = new CreateExecutionService[IO](factory.executionCreator).service.orNotFound(request).unsafeRunSync()
      assert(response.status == Status.NotFound)
    }
    it("Returns the created Execution") {
      val response: Response[IO] = postAndRespond(factory.executionCreator)
      val createdWF = decodeExecution(response)
      assert(factory.workflows.contains(createdWF.workflowId), "The Workflow Id is not present")
      assert(factory.executions.contains(createdWF.executionId), "The returned execution id is not present")
    }
    it("Saves the posted execution") {
      val response: Response[IO] = postAndRespond(factory.executionCreator)
      val createdWF = decodeExecution(response)
      assert(factory.executions.contains(createdWF.executionId))
    }
  }

  // The workflow and request our tests will use
  val testWorkflow = Workflow(1, Workflow.nowUtc, 3, isActive = true)
  val testRequest = CreateExecutionRequest(1)

  // Utility methods
  // POST to the create service, returning the response
  private def postAndRespond(executionCreator: ExecutionCreator) = getResponse(getPost, executionCreator)

  // Create a POST request to the create service, encoding a request
  private def getPost: Request[IO] = {
    import org.http4s.circe._
    Request[IO](Method.POST, Uri.uri("/execute")).withBody(CreateExecutionRequest.toJson(testRequest)).unsafeRunSync()
  }

  // Run the service
  private def getResponse(postRequest: Request[IO], executionCreator: ExecutionCreator) = {
    new CreateExecutionService[IO](executionCreator).service.orNotFound(postRequest).unsafeRunSync()
  }

  // Extract the Execution from the returned response
  private def decodeExecution(response: Response[IO]): Execution = {
    // Imports required to decode and deserialise the entity
    import org.http4s.circe.CirceEntityDecoder._
    import io.circe.generic.auto._
    import io.circe.java8.time._
    response.as[Execution].unsafeRunSync()
  }
}
