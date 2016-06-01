package com.donler.config

import groovy.transform.ToString
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Created by jason on 6/1/16.
 */
@Component
@ConfigurationProperties(prefix = "config.app")
@ToString(includeNames = true)
class AppConfig {
    String secretKey
    String expiredTime
}
