package com.donler.exception
/**
 * Created by jason on 5/31/16.
 */
class AttrNotValidException extends RuntimeException{
    AttrNotValidException() {
    }

    AttrNotValidException(def msg) {
        super("参数验证出错: ${msg}")
    }
}
