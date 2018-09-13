package com.petearthur.workflow.data.access

import com.petearthur.workflow.data.model.{CreateExecutionRequest, Execution, Workflow}

import scala.util.Random

class MemoryExecutionCreator(
                              workflows: collection.mutable.HashMap[Long, Workflow],
                              executions: collection.mutable.HashMap[Long, Execution]
                            ) extends ExecutionCreator {
  override def create(createExecutionRequest: CreateExecutionRequest): Option[Execution] = {
    val id = Random.nextLong()
    val workflow = workflows.get(createExecutionRequest.workflowId)
    workflow match {
      case Some(w) =>
        executions.put(id, createExecutionRequest.submit(id))
        executions.get(id)
      case None => None
    }

  }
}
