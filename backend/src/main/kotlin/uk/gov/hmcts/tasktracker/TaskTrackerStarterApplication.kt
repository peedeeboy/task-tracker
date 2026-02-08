package uk.gov.hmcts.tasktracker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaskTracker

fun main(args: Array<String>) {
    runApplication<TaskTracker>(*args)
}
