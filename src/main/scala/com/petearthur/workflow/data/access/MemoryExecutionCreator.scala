package com.petearthur.workflow.data.access

import com.petearthur.workflow.data.model.{CreateExecutionRequest, Execution, Workflow}

import scala.util.Random

class MemoryExecutionCreator(
                              workflows: collection.mutable.HashMap[Long, Workflow],
                              executions: collection.mutable.HashMap[Long, Execution]
                            ) extends ExecutionCreator {
  override def create(createWorkflowExecutionRequest: CreateExecutionRequest): Option[Execution] = {
    val id = Random.nextLong()
    executions.put(id, createWorkflowExecutionRequest.submit)
    executions.get(id)
  }
}
