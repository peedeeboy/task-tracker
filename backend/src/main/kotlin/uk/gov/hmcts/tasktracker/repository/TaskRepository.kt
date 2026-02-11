package uk.gov.hmcts.tasktracker.repository

import org.springframework.data.repository.CrudRepository
import uk.gov.hmcts.tasktracker.model.Task

interface TaskRepository : CrudRepository<Task, Long>