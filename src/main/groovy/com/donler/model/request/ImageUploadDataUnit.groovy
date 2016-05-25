package com.donler.model.request

import groovy.transform.ToString

import javax.validation.constraints.NotNull
/**
 * Created by jason on 5/25/16.
 */
@ToString(includeNames = true)
class ImageUploadDataUnit {
    String name // 图片名称
    @NotNull(message = "上传数据不能为空")
    String imageData // 图片的base64数据
}
