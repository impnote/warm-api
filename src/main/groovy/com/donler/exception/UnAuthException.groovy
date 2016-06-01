package com.donler.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Created by jason on 6/1/16.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnAuthException extends RuntimeException {
    UnAuthException() {
    }

    UnAuthException(String msg) {
        super("未授权: ${msg}")
    }
}