package com.donler.config

import groovy.transform.ToString
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
/**
 * Created by jason on 5/25/16.
 */
@Component
@ConfigurationProperties(prefix = "config.oss")
@ToString(includeNames = true)
class OSSConfig {
    String endpoint
    String accessKeyId
    String accessKeySecret
    String bucketName
    String urlPrefix
}
