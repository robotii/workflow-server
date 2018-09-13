package com.petearthur.workflow.data.access

import com.petearthur.workflow.data.model.{CreateWorkflowRequest, Workflow}

import scala.util.Random

class MemoryWorkflowCreator(workflows: collection.mutable.HashMap[Long, Workflow]) extends WorkflowCreator {
  override def create(createWorkflowRequest: CreateWorkflowRequest): Option[Workflow] = {
    val id = Random.nextLong()
    workflows.put(id, createWorkflowRequest.submit)
    workflows.get(id)
  }
}
