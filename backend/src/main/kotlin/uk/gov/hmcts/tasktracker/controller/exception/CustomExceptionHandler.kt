package uk.gov.hmcts.tasktracker.controller.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import uk.gov.hmcts.tasktracker.infrastructure.exception.TaskIdMismatchException
import uk.gov.hmcts.tasktracker.infrastructure.exception.TaskNotFoundException
import uk.gov.hmcts.tasktracker.infrastructure.exception.UnexpectedTaskIdException
import uk.gov.hmcts.tasktracker.model.ErrorDetail

@ControllerAdvice
class CustomExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(TaskNotFoundException::class)
    @ResponseBody
    fun handleTaskNotFoundException(req: HttpServletRequest, e: TaskNotFoundException): ErrorDetail {
        return ErrorDetail(req.requestURL.toString(), e)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnexpectedTaskIdException::class)
    @ResponseBody
    fun handleUnexpectedTaskIdException(req: HttpServletRequest, e: UnexpectedTaskIdException): ErrorDetail {
        return ErrorDetail(req.requestURL.toString(), e)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TaskIdMismatchException::class)
    @ResponseBody
    fun handleTaskIdMismatchException(req: HttpServletRequest, e: TaskIdMismatchException): ErrorDetail {
        return ErrorDetail(req.requestURL.toString(), e)
    }

}