package com.donler.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Created by jason on 5/31/16.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
class AttrNotValidException extends RuntimeException{
    AttrNotValidException() {
    }

    AttrNotValidException(def msg) {
        super("参数验证出错: ${msg}")
    }

}
