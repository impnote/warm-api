package com.donler.model.response

import com.donler.model.ImageUrlUnit
import groovy.transform.ToString

/**
 * Created by jason on 5/23/16.
 */
@ToString(includeNames = true)
class SimpleUserModel {
    String id // 用户id
    String nickname // 用户昵称
    ImageUrlUnit avatar // 用户头像
}
