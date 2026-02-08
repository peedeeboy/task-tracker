package uk.gov.hmcts.tasktracker.model

import java.time.LocalDateTime

data class ExampleCase(
    val id: Int,
    val caseNumber: String,
    val title: String,
    val description: String,
    val status: String,
    val createdDate: LocalDateTime
)
