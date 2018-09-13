# workflow-server

A demo http4s server, which allows creation of workflows, workflow executions and execution advance functionality.

The project is built using sbt, and organised as such:
* WorkflowServer - main server class
* services - package containing all available services
* data.access - package containing data access classes
* data.model - packages containing data model classes

This structure is mirrored between main and test folders. Unit tests utilise scalatest, and are run via `sbt test`.

No data is persisted, all data is stored in memory (and therefore lost when the server restarts).

The following services are available:
* CreateWorkflowService
    * POST /create
    * The posted entity should be a JSONified CreateWorkflowRequest instance
    * Responds with either:
        * 201 Content Created, returning JSONified Workflow object, upon success
        * 400 Bad Request upon failure
* CreateExecutionService
    * POST /execute
    * The post entity should be a JSONified CreateExecutionRequest instance 
    * Responds with:
        * 201 Content Created - if the execution object was created, with the Execution object as the body
        * 400 Bad Request upon failure
* AdvanceExecutionService
    * POST /advance
        * The post entity should be 
        * Responds with:
            * 200 Ok, returning the new Execution object, upon success
            * 404 Not Found if the ID does not exist
 
Any requests not covered by the above will receive a 404 Not Found.
 
### About the data model:
* The date time of creation is stored in UTC
* All Step counts must be positive, zero is allowed

A note on unit tests:
* Null pointers are not used (and there is no java interop), so I am not testing for them

### Limitations:
* Corners have been cut with error reporting due to time constraints (indicated by returning None), 
in a production system, the user would require more detailed knowledge of why an operation has failed.

* There is no logging in the system, although it would be possible to add this.

* No effort has been made to provide a scalable solution, as the focus is assumed to be on the code, 
rather than the infrastructure. Likewise, there is no means to delete a workflow, 
an execution, set execution complete etc. as this was not part of the spec for this version.

Start the server with `sbt run`. The server will accept HTTP requests over port 8080.

## Example HTTP requests via curl

Creating a Workflow:

`curl -w "\n" -d '{"stepCount": 0 }' -H "Content-Type: application/json" -X POST http://[0:0:0:0:0:0:0:0]:8080/create`

Output:

`{"workflowId":-4177933973851100877,"createdDate":"2018-09-13T15:19:22.694","stepCount":0,"isActive":true}`

Executing Workflow -4177933973851100877:

`curl -w "\n" -d '{"workflowId": -4177933973851100877 }' -H "Content-Type: application/json" -X POST http://[0:0:0:0:0:0:0:0]:8080/execute`

Output:

`{"executionId":2529019171956494683,"workflowId":-4177933973851100877,"createdDate":"2018-09-13T15:20:31.557","step":0,"isActive":true}`

Advancing Workflow Execution 2529019171956494683:

`curl -w "\n" -d '{"executionId": 2529019171956494683}' -H "Content-Type: application/json" -X POST http://[0:0:0:0:0:0:0:0]:8080/advance`

Output:

`{"executionId":2529019171956494683,"workflowId":-4177933973851100877,"createdDate":"2018-09-13T15:20:31.557","step":0,"isActive":true}`

In the above case the step has not been advanced, because the workflow defined zero steps