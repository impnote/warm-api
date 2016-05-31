package com.donler.controller

import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.ControllerAdvice
/**
 * Created by jason on 5/26/16.
 */
@ControllerAdvice
@Configuration
class ErrorController {

//    final Logger log = LoggerFactory.getLogger(this.getClass())
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    def handleNotFoundException(Exception ex) {
//        log.error("异常:" + ex)
////        return ResponseMsg.error(ex.getLocalizedMessage(), HttpStatus.NOT_FOUND.value())
//        throw new AttrNotValidException("asdf")
//    }
}
