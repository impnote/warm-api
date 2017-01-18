package com.donler.model.request.team

import com.donler.thirdparty.easemob.server.comm.wrapper.BodyWrapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ContainerNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import groovy.transform.ToString
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * Created by yali-04 on 2017/1/12.
 */
@ToString(includeNames = true)
class TeamInviteMembersRequestBody implements BodyWrapper{
    @NotNull
    @ApiModelProperty(value = "成员id数组")
    List<String> membersId

    @NotNull
    @ApiModelProperty(value = "群组id")
    String teamId

    @Override
    ContainerNode getBody() {
        ObjectNode body = JsonNodeFactory.instance.objectNode()
        ArrayNode membersNode = body.putArray("usernames")
        for (String member : membersId) {
            membersNode.add(member)
        }
        return body
    }
    @Override
    Boolean validate() {
        return null
    }
}
