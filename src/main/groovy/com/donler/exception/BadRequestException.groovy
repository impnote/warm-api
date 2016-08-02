package com.donler.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Created by jason on 5/31/16.
 */

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException extends RuntimeException {
    BadRequestException() {
    }

    BadRequestException(def msg) {
        super("请求失败: ${msg}")
    }
}
