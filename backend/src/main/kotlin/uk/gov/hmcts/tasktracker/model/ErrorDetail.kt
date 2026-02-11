package uk.gov.hmcts.tasktracker.model

data class ErrorDetail (
    val url: String,
    val exception: Exception
)