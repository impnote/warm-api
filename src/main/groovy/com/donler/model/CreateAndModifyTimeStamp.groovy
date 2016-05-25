package com.donler.model

import groovy.transform.ToString

/**
 * Created by jason on 5/25/16.
 */
@ToString(includeNames = true)
class CreateAndModifyTimeStamp {
    Date createdAt // 创建时间
    Date updatedAt // 更新时间
}
