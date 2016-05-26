package com.donler.exception

/**
 * Created by jason on 5/26/16.
 */
class NotFoundException extends RuntimeException{
    NotFoundException() {
    }

    NotFoundException(String var1) {
        super(var1)
    }

    NotFoundException(String var1, Throwable var2) {
        super(var1, var2)
    }

    NotFoundException(Throwable var1) {
        super(var1)
    }

    NotFoundException(String var1, Throwable var2, boolean var3, boolean var4) {
        super(var1, var2, var3, var4)
    }
}
