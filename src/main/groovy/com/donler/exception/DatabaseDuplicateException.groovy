package com.donler.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Created by jason on 5/31/16.
 */
@ResponseStatus(HttpStatus.CONFLICT)
class DatabaseDuplicateException extends RuntimeException{
    DatabaseDuplicateException() {
    }

    DatabaseDuplicateException(def msg) {
        super("数据库重复: ${msg}")
    }
}
