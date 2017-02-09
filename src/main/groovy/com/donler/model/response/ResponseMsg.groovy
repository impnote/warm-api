package com.donler.model.response

import io.swagger.annotations.ApiModelProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Created by jason on 5/26/16.
 */
class ResponseMsg {
    @ApiModelProperty(notes = "响应消息")
    String msg


    @ApiModelProperty(notes = "响应内容")
    def data

    @ApiModelProperty(notes = "响应状态码")
    Integer statusCode


    static ok(def data) {
        return new ResponseMsg(msg: "请求成功", statusCode: 200, data: data)
    }

    static errorStatus(def msg, def code) {
        return new ResponseMsg(msg: msg, statusCode: code)
    }
    static error(def msg, int code) {
        return new ResponseEntity(msg,HttpStatus.valueOf(code))
    }

    static ok(String msg, Integer statusCode, def data) {
        return new ResponseMsg(msg: msg, statusCode: statusCode, data: data)

    }
}
