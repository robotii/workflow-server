package com.petearthur.workflow.services

import cats.effect.IO
import com.petearthur.workflow.data.access.WorkflowCreator
import com.petearthur.workflow.data.model.{CreateWorkflowRequest, Workflow}
import org.http4s._
import org.http4s.implicits._
import org.scalatest.{BeforeAndAfter, FunSpec}

// Tests the workflow creation service
class CreateWorkflowSpec extends FunSpec with BeforeAndAfter {
  describe("CreateWorkflowService") {
    var factory: TestDataAccessFactory = new TestDataAccessFactory()
    before {
      // Each unit test will use a new instance of the test factory, with a clean hash map each run.
      factory = new TestDataAccessFactory()
    }
    it("Responds with 201 Created upon success") {
      val response: Response[IO] = postAndRespond(factory.workflowCreator)
      assert(response.status == Status.Created)
    }
    it("Responds with 400 Bad Request upon failure") {
      val response = postAndRespond(factory.workflowCreatorFail)
      assert(response.status == Status.BadRequest)
    }
    it("Responds with 404 Not Found upon receiving an unexpected HTTP request") {
      // Let's use a GET
      val request = Request[IO](Method.GET, Uri.uri("/create"))
      val response = new CreateWorkflowService[IO](factory.workflowCreator).service.orNotFound(request).unsafeRunSync()
      assert(response.status == Status.NotFound)
    }
    it("Returns the created Workflow") {
      val response: Response[IO] = postAndRespond(factory.workflowCreator)
      val createdWF = decodeWorkflow(response)
      assert(factory.workflows.contains(createdWF.workflowId), "The returned Workflow Id is not present")
    }
    it("Saves the posted workflow") {
      val response: Response[IO] = postAndRespond(factory.workflowCreator)
      val createdWF = decodeWorkflow(response)
      assert(factory.workflows.contains(createdWF.workflowId))
    }
  }

  // The Request our tests will use
  val testRequest = CreateWorkflowRequest(3)

  // Utility methods
  // POST to the create service, returning the response
  private def postAndRespond(workflowCreator: WorkflowCreator) = getResponse(getPost, workflowCreator)

  // Create a POST request to the create service, encoding a request
  private def getPost: Request[IO] = {
    import org.http4s.circe._
    Request[IO](Method.POST, Uri.uri("/create")).withBody(CreateWorkflowRequest.toJson(testRequest)).unsafeRunSync()
  }

  // Run the service
  private def getResponse(postRequest: Request[IO], workflowCreator: WorkflowCreator) = {
    new CreateWorkflowService[IO](workflowCreator).service.orNotFound(postRequest).unsafeRunSync()
  }

  // Extract the Workflow from the returned response
  private def decodeWorkflow(response: Response[IO]): Workflow = {
    // Imports required to decode and deserialise the entity
    import org.http4s.circe.CirceEntityDecoder._
    import io.circe.generic.auto._
    import io.circe.java8.time._
    response.as[Workflow].unsafeRunSync()
  }
}
