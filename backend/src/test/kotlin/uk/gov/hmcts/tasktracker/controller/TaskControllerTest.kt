package uk.gov.hmcts.tasktracker.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import uk.gov.hmcts.tasktracker.infrastructure.exception.TaskIdMismatchException
import uk.gov.hmcts.tasktracker.infrastructure.exception.TaskNotFoundException
import uk.gov.hmcts.tasktracker.infrastructure.exception.UnexpectedTaskIdException
import uk.gov.hmcts.tasktracker.model.ErrorDetail
import uk.gov.hmcts.tasktracker.model.Task
import uk.gov.hmcts.tasktracker.service.TaskService
import java.time.LocalDateTime

@WebMvcTest(controllers = [TaskController::class])
@AutoConfigureRestTestClient
class TaskControllerTest() : FreeSpec() {

    override val extensions = listOf(SpringExtension())

    @MockkBean
    private lateinit var taskService: TaskService
    @Autowired
    private lateinit var taskController: TaskController
    @Autowired
    private lateinit var restTestClient: RestTestClient

    override suspend fun beforeEach(testCase: TestCase) {
        clearAllMocks()
    }

    init {

        "getAllTasks()" - {
            "when Task exists, should return Task and HTTP 200 OK " {
                // Given
                val tasks = listOf(
                    Task(
                        id = 100,
                        title = "Task 100",
                        description = "A thing to do",
                        status = "COMPLETED",
                        dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                    ),
                    Task(
                        id = 200,
                        title = "Task 200",
                        description = "Another thing to do",
                        status = "COMPLETED",
                        dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                    )
                )
                every { taskService.getAllTasks() } returns tasks

                // When
                val result = restTestClient.get()
                    .uri("/api/tasks")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<List<Task>>()
                    .returnResult()

                val body = result.responseBody

                // Then
                body shouldBe tasks
            }
        }

        "getTaskById()" - {
            "when Task exists, should return Task and HTTP 200 OK " {
                // Given
                val task = Task(
                    id = 100,
                    title = "Task 100",
                    description = "A thing to do",
                    status = "COMPLETED",
                    dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                )
                every { taskService.getTaskById(100) } returns task

                // When
                val result = restTestClient.get()
                    .uri("/api/tasks/100")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<Task>()
                    .returnResult()

                val body = result.responseBody

                // Then
                body shouldBe task
            }

            "when Task does not exist, should return ErrorDetail and HTTP 404 Not Found" {
                // Given
                every { taskService.getTaskById(100) } throws TaskNotFoundException(100)

                // When
                val result = restTestClient.get()
                    .uri("/api/tasks/100")
                    .exchange()
                    .expectStatus().isNotFound
                    .expectBody<ErrorDetail>()
                    .returnResult()

                val body = result.responseBody

                // Then
                body!!.url shouldBe "http://localhost/api/tasks/100"
                body.exception.message shouldBe "Task 100 not found"
            }
        }

        "createTask()" - {
            "when task is valid should return created Task and HTTP 201 Created" {
                // Given
                val task = Task(
                    id = null,
                    title = "Task 100",
                    description = "A thing to do",
                    status = "COMPLETED",
                    dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                )
                val createdTask = task.copy(id = 500)
                every { taskService.createTask(task) } returns createdTask

                // When
                val result = restTestClient.post()
                    .uri("/api/tasks")
                    .body(task)
                    .exchange()
                    .expectStatus().isCreated
                    .expectBody<Task>()
                    .returnResult()

                val body = result.responseBody

                // Then
                body shouldBe createdTask
            }

            "when task is invalid should return ErrorDetail and HTTP 400 Bad Request" {
                // Given
                val task = Task(
                    id = 100,
                    title = "Task 100",
                    description = "A thing to do",
                    status = "COMPLETED",
                    dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                )
                every { taskService.createTask(task) } throws UnexpectedTaskIdException()

                // When
                val result = restTestClient.post()
                    .uri("/api/tasks")
                    .body(task)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody<ErrorDetail>()
                    .returnResult()

                val body = result.responseBody

                // Then
                body!!.url shouldBe "http://localhost/api/tasks"
                body.exception.message shouldBe "Task ID can not be included in Task creation"
            }
        }

        "updateTask()" - {
            "when task is valid, should return updated Task and HTTP 200 Created" {
                // Given
                val task = Task(
                    id = 100,
                    title = "Task 100",
                    description = "A thing to do",
                    status = "INCOMPLETE",
                    dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                )
                val updatedTask = task.copy(status = "COMPLETED")
                every { taskService.updateTask(100, task) } returns updatedTask

                // When
                val result = restTestClient.put()
                    .uri("/api/tasks/100")
                    .body(task)
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<Task>()
                    .returnResult()

                val body = result.responseBody

                // Then
                body shouldBe updatedTask
            }

            "when task IDs don't match, should return ErrorDetail HTTP 400 Bad Request" {
                // Given
                val task = Task(
                    id = 100,
                    title = "Task 100",
                    description = "A thing to do",
                    status = "INCOMPLETE",
                    dueDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                )
                every { taskService.updateTask(200, task) } throws TaskIdMismatchException(200, 100)

                // When
                val result = restTestClient.put()
                    .uri("/api/tasks/200")
                    .body(task)
                    .exchange()
                    .expectStatus().isBadRequest
                    .expectBody<ErrorDetail>()
                    .returnResult()

                val body = result.responseBody

                // Then
                body!!.url shouldBe "http://localhost/api/tasks/200"
                body.exception.message shouldBe "Entity Task ID '100' does not match parameter Task ID '200'"
            }
        }

    }

}
