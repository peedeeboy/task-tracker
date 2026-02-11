package uk.gov.hmcts.tasktracker.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tasks")
data class Task (
    @Id
    val id: Long?,
    val title: String,
    val description: String?,
    val status: String,
    val dueDate: LocalDateTime
)