package uk.gov.hmcts.tasktracker

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import uk.gov.hmcts.tasktracker.model.ErrorDetail
import uk.gov.hmcts.tasktracker.model.Task
import uk.gov.hmcts.tasktracker.repository.TaskRepository
import java.time.LocalDate
import java.time.Month

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class TaskIntegrationTest : FreeSpec() {

    override val extensions = listOf(SpringExtension())

    @Autowired
    lateinit var taskRepository: TaskRepository
    @Autowired
    lateinit var restTestClient: RestTestClient
    @LocalServerPort
     var serverPort: Int = 0

    override suspend fun beforeEach(testCase: TestCase) {
        taskRepository.deleteAll()
    }

    init {

        "should add new task to database" {
            // Given
            val task = Task(
                id = null,
                title = "A Task",
                description = "A Task description",
                status = "IN PROGRESS",
                dueDate = LocalDate.of(2020, 4, 11),
            )

            // When
            val result = restTestClient.post().uri("/api/tasks")
                .body(task)
                .exchange()
                .expectStatus().isCreated
                .expectBody(Task::class.java)
                .returnResult()
            val body = result.responseBody!!
            val persistedTask = taskRepository.findById(body.id!!).get()

            // Then
            body.id shouldNotBe null
            body.title shouldBe task.title
            body.description shouldBe task.description
            body.status shouldBe task.status
            body.dueDate shouldBe task.dueDate

            persistedTask.title shouldBe task.title
            persistedTask.description shouldBe task.description
            persistedTask.status shouldBe task.status
            persistedTask.dueDate shouldBe task.dueDate
        }

        "should retrieve task from database" {
            // Given
            val task = Task(
                id = null,
                title = "A saved Task",
                description = "A saved Task description",
                status = "IN PROGRESS",
                dueDate = LocalDate.of(2020, 4, 11)
            )
            val savedTask = taskRepository.save(task)

            // When
            val result = restTestClient.get().uri("/api/tasks/${savedTask.id}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task::class.java)
                .returnResult()
            val body = result.responseBody!!

            // Then
            body.id shouldBe savedTask.id
            body.title shouldBe task.title
            body.description shouldBe task.description
            body.status shouldBe task.status
            body.dueDate shouldBe task.dueDate
        }

        "should retrieve multiple tasks from database" {
            // Given
            val task1 = Task(
                id = null,
                title = "A saved Task",
                description = "A saved Task description",
                status = "IN PROGRESS",
                dueDate = LocalDate.of(2020, 4, 11),
            )
            val task2 = Task(
                id = null,
                title = "Another saved Task",
                description = "Another saved Task description",
                status = "COMPLETED",
                dueDate = LocalDate.of(2020, 4, 11),
            )
            val savedTask1 = taskRepository.save(task1)
            val savedTask2 = taskRepository.save(task2)

            // When
            val result = restTestClient.get().uri("/api/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBody<List<Task>>()
                .returnResult()
            val body1 = result.responseBody!!.first()
            val body2 = result.responseBody!!.last()

            // Then
            body1.id shouldBe savedTask1.id
            body1.title shouldBe task1.title
            body1.description shouldBe task1.description
            body1.status shouldBe task1.status
            body1.dueDate shouldBe task1.dueDate

            body2.id shouldBe savedTask2.id
            body2.title shouldBe task2.title
            body2.description shouldBe task2.description
            body2.status shouldBe task2.status
            body2.dueDate shouldBe task2.dueDate
        }

        "should update task in database" {
            // Given
            val task = Task(
                id = null,
                title = "A Task",
                description = "A Task description",
                status = "IN PROGRESS",
                dueDate = LocalDate.of(2020, 4, 11),
            )
            val savedTask = taskRepository.save(task)
            val updatedTask = savedTask.copy(status = "COMPLETED")

            // When
            val result = restTestClient.put().uri("/api/tasks/${savedTask.id}")
                .body(updatedTask)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task::class.java)
                .returnResult()
            val body = result.responseBody!!
            val persistedTask = taskRepository.findById(savedTask.id!!).get()

            // Then
            body.id shouldBe updatedTask.id
            body.title shouldBe updatedTask.title
            body.description shouldBe updatedTask.description
            body.status shouldBe updatedTask.status
            body.dueDate shouldBe updatedTask.dueDate

            persistedTask.title shouldBe updatedTask.title
            persistedTask.description shouldBe updatedTask.description
            persistedTask.status shouldBe updatedTask.status
            persistedTask.dueDate shouldBe updatedTask.dueDate
        }

        "should return HTTP404 not found for non-existent task" {
            // Given
            val invalidTaskId = 1L

            // When
            val result = restTestClient.get().uri("/api/tasks/$invalidTaskId")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorDetail::class.java)
                .returnResult()
            val body = result.responseBody!!

            // Then
            body.url shouldBe "http://localhost:$serverPort/api/tasks/$invalidTaskId"
        }

        "should return HTTP400 bad request for invalid task ID" {
            // Given
            val task = Task(
                id = 1,
                title = "A Task",
                description = "A Task description",
                status = "IN PROGRESS",
                dueDate = LocalDate.of(2020, 4, 11),
            )

            // When
            val result = restTestClient.put().uri("/api/tasks/2")
                .body(task)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorDetail::class.java)
                .returnResult()
            val body = result.responseBody!!

            // Then
            body.url shouldBe "http://localhost:$serverPort/api/tasks/2"
        }

        "should return HTTP400 bad request for unexpected task ID" {
            // Given
            val task = Task(
                id = 1,
                title = "A Task",
                description = "A Task description",
                status = "IN PROGRESS",
                dueDate = LocalDate.of(2020, 4, 11),
            )

            // When
            val result = restTestClient.post().uri("/api/tasks")
                .body(task)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorDetail::class.java)
                .returnResult()
            val body = result.responseBody!!

            // Then
            body.url shouldBe "http://localhost:$serverPort/api/tasks"
        }

    }

}