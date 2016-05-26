package com.donler.controller

import com.donler.exception.NotFoundException
import com.donler.model.response.ResponseMsg
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
/**
 * Created by jason on 5/26/16.
 */
@ControllerAdvice
@Configuration
class ErrorController {

    final Logger log = LoggerFactory.getLogger(this.getClass())

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    def handleNotFoundException(Exception ex) {
        log.error("未找到异常:" + ex.getLocalizedMessage())
        return ResponseMsg.error(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND.value())
    }
}
