package com.petearthur.workflow.data.access

import cats.effect.IO
import com.petearthur.workflow.data.model.{AdvanceExecutionRequest, Execution}

class MemoryExecutionAdvancer(executions: collection.mutable.HashMap[Long, Execution])
  extends ExecutionAdvancer {
  override def advance(advanceExecutionRequest: AdvanceExecutionRequest): Option[Execution] = {
    val id = advanceExecutionRequest.executionId
    executions.get(id)
      .flatMap(so => replaceWithUpdated(id,so,executions))
  }

  // If our executionId is valid, we incur a side effect - that of replacing the stored execution with the advanced instance
  def replaceWithUpdated(executionId: Long,
                         execution: Execution,
                         executions: collection.mutable.HashMap[Long, Execution]): Option[Execution] = {
    executions.put(executionId, execution)
  }
}
