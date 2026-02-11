package uk.gov.hmcts.tasktracker.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import uk.gov.hmcts.tasktracker.infrastructure.exception.TaskIdMismatchException
import uk.gov.hmcts.tasktracker.infrastructure.exception.UnexpectedTaskIdException
import uk.gov.hmcts.tasktracker.infrastructure.exception.TaskNotFoundException
import uk.gov.hmcts.tasktracker.model.Task
import uk.gov.hmcts.tasktracker.repository.TaskRepository
import java.time.LocalDateTime
import java.util.Optional

class TaskServiceTest : FreeSpec() {

    init {

        "createTask" - {

            "should return created task with ID on success" {
                // Given
                val task = Task(
                    id = null,
                    title = "A Task",
                    description = null,
                    status = "COMPLETED",
                    dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                )
                val createdTask = task.copy(id = 500)
                val taskRepository = mockk<TaskRepository>()
                every { taskRepository.save(task) } returns createdTask

                val taskService = TaskService(taskRepository)

                // When
                val result = taskService.createTask(task)

                // Then
                result shouldBe createdTask
            }

            "should throw UnexpectedTaskIdException if a Task with ID is provided" {
                // Given
                val task = Task(
                    id = 500,
                    title = "A Task",
                    description = null,
                    status = "COMPLETED",
                    dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                )
                val taskRepository = mockk<TaskRepository>()

                val taskService = TaskService(taskRepository)

                // When
                val result = shouldThrow<UnexpectedTaskIdException> { taskService.createTask(task) }

                // Then
                result.message shouldBe "Task ID can not be included in Task creation"
            }

        }

        "getTaskById" - {

            "should return task when ID exists in database" {
                // Given
                val task = mockk<Task>()
                val taskRepository = mockk<TaskRepository>()
                every { taskRepository.findById(100) } returns Optional.of(task)

                val taskService = TaskService(taskRepository)

                // When
                val result = taskService.getTaskById(100)

                // Then
                result shouldBe task
            }

            "should throw TaskNotFoundException when ID does not exist in database" {
                // Given
                val taskRepository = mockk<TaskRepository>()
                every { taskRepository.findById(100) } returns Optional.empty()

                val taskService = TaskService(taskRepository)

                // When
                val result = shouldThrow<TaskNotFoundException> { taskService.getTaskById(100) }

                // Then
                result.message shouldBe "Task 100 not found"
            }
        }

        "getAllTasks" - {

            "should return all tasks in database" {
                // Given
                val task1 = mockk<Task>()
                val task2 = mockk<Task>()
                val task3 = mockk<Task>()

                val taskRepository = mockk<TaskRepository>()
                every { taskRepository.findAll() } returns listOf(task1, task2, task3)

                val taskService = TaskService(taskRepository)

                // When
                val result = taskService.getAllTasks()

                // Then
                result shouldContainInOrder listOf(task1, task2, task3)
            }

        }

        "updateTask" - {

            "should return updated task on success" {
                // Given
                val task = Task(
                    id = 100,
                    title = "A Task",
                    description = null,
                    status = "COMPLETED",
                    dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                )
                val taskRepository = mockk<TaskRepository>()
                every { taskRepository.save(task) } returns task

                val taskService = TaskService(taskRepository)

                // When
                val result = taskService.updateTask(100, task)

                // Then
                result shouldBe task
            }

            "should throw TaskIdMismatchException when parameter ID does not match entity ID" {
                // Given
                val task = Task(
                    id = null,
                    title = "A Task",
                    description = null,
                    status = "COMPLETED",
                    dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                )
                val taskRepository = mockk<TaskRepository>()
                every { taskRepository.save(task) } returns task

                val taskService = TaskService(taskRepository)

                // When
                val result = shouldThrow<TaskIdMismatchException>{ taskService.updateTask(100, task) }

                // Then
                result.message shouldBe "Entity Task ID 'null' does not match parameter Task ID '100'"
            }
        }
    }

}
