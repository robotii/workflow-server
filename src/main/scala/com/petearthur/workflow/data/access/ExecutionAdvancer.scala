package com.petearthur.workflow.data.access

import com.petearthur.workflow.data.model.{AdvanceExecutionRequest, Execution}

trait ExecutionAdvancer {
  def advance(advanceExecutionRequest: AdvanceExecutionRequest): Option[Execution]
}
