package com.donler.model.persistent.trend

import com.donler.model.Constants
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.Id

/**
 * Created by apple on 16/9/20.
 */
@ToString(includeNames = true)
class TrendItem {
    @Id
    String id // 编号
    @ApiModelProperty(notes = "动态类型")
    Constants.TypeEnum typeEnum //动态类型
    @ApiModelProperty("动态id")
    String trendId //动态id
    @ApiModelProperty("公司id")
    String companyId //公司id
    @ApiModelProperty("创建时间")
    Date createdAt // 创建时间
    @ApiModelProperty("更新时间")
    Date updatedAt // 更新时间


}



