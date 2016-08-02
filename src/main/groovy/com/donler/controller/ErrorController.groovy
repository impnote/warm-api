package com.donler.controller

import com.donler.exception.AttrNotValidException
import com.donler.exception.DatabaseDuplicateException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
/**
 * Created by jason on 5/26/16.
 */
@ControllerAdvice
class ErrorController {

    final Logger log = LoggerFactory.getLogger(this.getClass())


    @ExceptionHandler(DatabaseDuplicateException.class)
    @ResponseBody
    def handleDatabaseDuplicateException(Exception ex) {
        ex.stackTrace = null
        log.error("异常:${ex}")
//        return ResponseMsg.error(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND.value())
        return ex
    }

    @ExceptionHandler(AttrNotValidException.class)
    @ResponseBody
    def handleAttrNotValidException(Exception ex) {
        ex.stackTrace = null
        return ex
    }

}
