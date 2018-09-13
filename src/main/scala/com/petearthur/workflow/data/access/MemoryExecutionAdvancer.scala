package com.petearthur.workflow.data.access

import cats.effect.IO
import com.petearthur.workflow.data.model.{AdvanceExecutionRequest, Execution, Workflow}

class MemoryExecutionAdvancer(workflows: collection.mutable.HashMap[Long, Workflow],
                              executions: collection.mutable.HashMap[Long, Execution])
  extends ExecutionAdvancer {
  override def advance(advanceExecutionRequest: AdvanceExecutionRequest): Option[Execution] = {
    val id = advanceExecutionRequest.executionId
    executions.get(id)
      .flatMap(so => replaceWithUpdated(id, advanceExecutionRequest.submit(so,workflows.get(so.workflowId)), executions))
  }

  // If our executionId is valid, we incur a side effect - that of replacing the stored execution with the advanced instance
  // Note that the step index may not actually be increased, so we may be replacing with the same object
  def replaceWithUpdated(executionId: Long, execution: Execution,
                         executions: collection.mutable.HashMap[Long, Execution]): Option[Execution] = {
    executions.put(executionId, execution)
  }
}
