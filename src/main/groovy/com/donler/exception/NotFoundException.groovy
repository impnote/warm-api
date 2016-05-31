package com.donler.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Created by jason on 5/26/16.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException extends RuntimeException{
    NotFoundException() {
    }


    // TODO
    NotFoundException(def msg) {
        super("未找到: [$msg]")
    }


}
