package uk.gov.hmcts.tasktracker.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.hmcts.tasktracker.model.Task
import uk.gov.hmcts.tasktracker.service.TaskService

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @GetMapping()
    fun getAllTasks(): List<Task>{
        return taskService.getAllTasks()
    }

    @GetMapping("/{taskId}")
    fun getTaskById(@PathVariable taskId: Long): Task {
        return taskService.getTaskById(taskId)
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@RequestBody task: Task): Task{
        return taskService.createTask(task)
    }

    @PutMapping("/{taskId}")
    fun updateTask(@PathVariable taskId: Long, @RequestBody task: Task): Task {
        return taskService.updateTask(taskId, task)
    }

}