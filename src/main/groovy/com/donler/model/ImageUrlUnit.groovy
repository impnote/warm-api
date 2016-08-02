package com.donler.model

import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

/**
 * Created by jason on 5/23/16.
 */
@ToString(includeNames = true)
class ImageUrlUnit {
    @ApiModelProperty("下标,用于多张图片修改的情景,单张图片或者已上传的图片无需替换的情况下无须管此参数")
    int index // 下标,用于多张图片修改的情景,单张图片或者已上传的图片无需替换的情况下无须管此参数
    @ApiModelProperty("图片名称")
    String name // 图片名称
    @ApiModelProperty("图片的url")
    String imageUrl // 图片的url
}
