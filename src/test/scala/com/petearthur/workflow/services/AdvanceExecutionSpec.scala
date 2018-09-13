package com.petearthur.workflow.services

import cats.effect.IO
import com.petearthur.workflow.data.access.{ExecutionAdvancer}
import com.petearthur.workflow.data.model.{AdvanceExecutionRequest, Execution, Workflow}
import org.scalatest.{BeforeAndAfter, FunSpec}
import org.http4s._
import org.http4s.implicits._

class AdvanceExecutionSpec extends FunSpec with BeforeAndAfter {
  describe("AdvanceExecutionService") {
    var factory: TestDataAccessFactory = new TestDataAccessFactory()
    before {
      // Each unit test will use a new instance of the test factory, with a clean hash map each run.
      factory = new TestDataAccessFactory()
      factory.workflows.put(testWorkflow.workflowId, testWorkflow)
      factory.executions.put(testExecution.executionId, testExecution)
    }
    it("Responds with 200 upon success") {
      val response: Response[IO] = postAndRespond(factory.executionAdvancer)
      assert(response.status == Status.Ok)
    }
    it("Responds with 400 Bad Request upon failure") {
      val response = postAndRespond(factory.executionAdvancerFail)
      assert(response.status == Status.BadRequest)
    }
    it("Responds with 404 Not Found upon receiving an unexpected HTTP request") {
      // Let's use a GET
      val request = Request[IO](Method.GET, Uri.uri("/advance"))
      val response = new AdvanceExecutionService[IO](factory.executionAdvancer).service.orNotFound(request).unsafeRunSync()
      assert(response.status == Status.NotFound)
    }
    it("Returns the updated Execution") {
      val response: Response[IO] = postAndRespond(factory.executionAdvancer)
      val createdWF = decodeExecution(response)
      assert(factory.workflows.contains(createdWF.workflowId), "The Workflow Id is not present")
      assert(factory.executions.contains(createdWF.executionId), "The returned execution id is not present")
    }
    it("Updates the step count") {
      val response: Response[IO] = postAndRespond(factory.executionAdvancer)
      val createdWF = decodeExecution(response)
      assert(factory.workflows.contains(createdWF.workflowId))
      assert(factory.executions(1).step == 1, "The step is not updated to 1")
    }
    it("Does not update the step count beyond the workflow definition") {
      var response: Response[IO] = postAndRespond(factory.executionAdvancer)
      var createdWF = decodeExecution(response)
      assert(factory.workflows.contains(createdWF.workflowId))
      assert(factory.executions(1).step == 1, "The step is not updated to 1")
      response = postAndRespond(factory.executionAdvancer)
      createdWF = decodeExecution(response)
      assert(factory.executions(1).step == 2, "The step is not updated to 2")
      response = postAndRespond(factory.executionAdvancer)
      createdWF = decodeExecution(response)
      assert(factory.executions(1).step == 3, "The step is not updated to 3")
      response = postAndRespond(factory.executionAdvancer)
      createdWF = decodeExecution(response)
      assert(factory.executions(1).step == 3, "The step should remain at 3")
    }
  }


  // The Request our tests will use
  val testWorkflow = Workflow(1, Workflow.nowUtc, 3, isActive = true)
  val testExecution = Execution(1, 1, Execution.nowUtc, 0, isActive = true)
  val testRequest = AdvanceExecutionRequest(1)

  // Utility methods
  // POST to the create service, returning the response
  private def postAndRespond(executionAdvancer: ExecutionAdvancer) = getResponse(getPost, executionAdvancer)

  // Create a POST request to the create service, encoding a request
  private def getPost: Request[IO] = {
    import org.http4s.circe._
    Request[IO](Method.POST, Uri.uri("/advance")).withBody(AdvanceExecutionRequest.toJson(testRequest)).unsafeRunSync()
  }

  // Run the service
  private def getResponse(postRequest: Request[IO], executionAdvancer: ExecutionAdvancer) = {
    new AdvanceExecutionService[IO](executionAdvancer).service.orNotFound(postRequest).unsafeRunSync()
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
