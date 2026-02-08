package uk.gov.hmcts.tasktracker.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.hmcts.tasktracker.model.ExampleCase
import java.time.LocalDateTime

@RestController
class CaseController {

    @GetMapping(value = ["/get-example-case"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getExampleCases(): ResponseEntity<ExampleCase> {
        return ResponseEntity.ok(ExampleCase(
            id = 1,
            caseNumber = "ABC12345",
            title = "Case Title",
            description = "Case Description",
            status = "Case Status",
            createdDate = LocalDateTime.now()
        ))
    }
}