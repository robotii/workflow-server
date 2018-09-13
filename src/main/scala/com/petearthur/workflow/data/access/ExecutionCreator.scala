package com.petearthur.workflow.data.access

import com.petearthur.workflow.data.model.{CreateExecutionRequest, Execution}

trait ExecutionCreator {
  def create(createExecutionRequest: CreateExecutionRequest): Option[Execution]
}
