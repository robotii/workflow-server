package com.petearthur.workflow.services

import cats.effect.IO
import com.petearthur.workflow.data.access._
import com.petearthur.workflow.data.model.{Execution, Workflow}

// Utility methods for building a DA to use in unit tests
class TestDataAccessFactory {
  // Allow access to the underlying memory
  val workflows = collection.mutable.HashMap[Long, Workflow]()
  val executions = collection.mutable.HashMap[Long, Execution]()

  // To mock successful DA operations
  def workflowCreator: WorkflowCreator = new MemoryWorkflowCreator(workflows)

  def executionCreator: ExecutionCreator = new MemoryExecutionCreator(workflows, executions)

  def executionAdvancer: ExecutionAdvancer = new MemoryExecutionAdvancer(workflows, executions)

  // To mock the failures within individual DA classes
  def workflowCreatorFail: WorkflowCreator = { _ => None }

  def executionCreatorFail: ExecutionCreator = { _ => None }

  def executionAdvancerFail: ExecutionAdvancer = { _ => None }
}
