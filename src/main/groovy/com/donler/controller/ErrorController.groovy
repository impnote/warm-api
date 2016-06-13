package com.donler.controller

import com.donler.exception.DatabaseDuplicateException
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



    @ExceptionHandler(DatabaseDuplicateException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    def handleDatabaseDuplicateException(Exception ex) {
        ex.stackTrace = null
        log.error("异常:${ex}")
//        return ResponseMsg.error(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND.value())
        return ex
    }

}
