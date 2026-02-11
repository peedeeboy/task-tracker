package uk.gov.hmcts.tasktracker.infrastructure.exception

class TaskNotFoundException(taskId: Long) : Exception("Task $taskId not found")

class UnexpectedTaskIdException : Exception("Task ID can not be included in Task creation")

class TaskIdMismatchException(taskIdParam: Long, taskId: Long?) : Exception("Entity Task ID '$taskId' does not match parameter Task ID '$taskIdParam'")