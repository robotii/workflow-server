package com.petearthur.workflow.data.access

import com.petearthur.workflow.data.model.{CreateWorkflowRequest, Workflow}

trait WorkflowCreator {
  def create(createWorkflowRequest: CreateWorkflowRequest): Option[Workflow]
}
