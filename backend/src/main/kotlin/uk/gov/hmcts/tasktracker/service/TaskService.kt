package uk.gov.hmcts.tasktracker.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import uk.gov.hmcts.tasktracker.infrastructure.exception.TaskIdMismatchException
import uk.gov.hmcts.tasktracker.infrastructure.exception.UnexpectedTaskIdException
import uk.gov.hmcts.tasktracker.infrastructure.exception.TaskNotFoundException
import uk.gov.hmcts.tasktracker.model.Task
import uk.gov.hmcts.tasktracker.repository.TaskRepository

@Service
class TaskService(private val taskRepository: TaskRepository) {

    private val logger = KotlinLogging.logger {}

    fun getAllTasks(): List<Task> {
        logger.info { "Retrieving all tasks" }

        return taskRepository.findAll().toList()
    }

    fun getTaskById(taskId: Long): Task {
        logger.info { "Retrieving task: $taskId" }

        return taskRepository.findById(taskId)
            .orElseThrow { TaskNotFoundException(taskId) }
    }

    fun createTask(task: Task): Task {
        if(task.id != null) {
            throw UnexpectedTaskIdException()
        }

        return taskRepository.save(task).also { createdTask ->
            logger.info { "Created task: $createdTask" }
        }
    }

    fun updateTask(taskId: Long, task: Task): Task {
        if(taskId != task.id) {
            throw TaskIdMismatchException(taskId, task.id)
        }

        return taskRepository.save(task).also { updatedTask ->
            logger.info { "Updated task: $updatedTask" }
        }
    }

}