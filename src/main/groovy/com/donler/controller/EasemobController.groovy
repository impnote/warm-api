package com.donler.controller

import com.donler.model.persistent.team.Team
import com.donler.model.persistent.user.User
import com.donler.model.response.ResponseMsg
import com.donler.repository.team.TeamRepository
import com.donler.thirdparty.easemob.server.api.ChatGroupAPI
import com.donler.thirdparty.easemob.server.api.ChatMessageAPI
import com.donler.thirdparty.easemob.server.api.ChatRoomAPI
import com.donler.thirdparty.easemob.server.api.FileAPI
import com.donler.thirdparty.easemob.server.api.IMUserAPI
import com.donler.thirdparty.easemob.server.api.SendMessageAPI
import com.donler.thirdparty.easemob.server.comm.ClientContext
import com.donler.thirdparty.easemob.server.comm.EasemobRestAPIFactory
import com.donler.thirdparty.easemob.server.comm.body.ChatGroupBody
import com.donler.thirdparty.easemob.server.comm.body.IMUserBody
import com.donler.thirdparty.easemob.server.comm.wrapper.ResponseWrapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.gson.Gson
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import net.sf.json.JSON
import org.json.simple.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

/**
 * Created by yali-04 on 2017/1/10.
 */
@RestController
@RequestMapping("/easemob")
@Api(value = "easemob", tags = ["环信"])
class EasemobController {


    @Autowired
    private TeamRepository teamRepository
//    @Autowired
//    private EasemobRestAPIFactory easemobRestAPIFactory


    private EasemobRestAPIFactory factory = ClientContext.getInstance().init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();
    private IMUserAPI user = (IMUserAPI)factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
    private ChatMessageAPI chat = (ChatMessageAPI)factory.newInstance(EasemobRestAPIFactory.MESSAGE_CLASS);
    private FileAPI file = (FileAPI)factory.newInstance(EasemobRestAPIFactory.FILE_CLASS);
    private SendMessageAPI message = (SendMessageAPI)factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);
    private ChatGroupAPI chatgroup = (ChatGroupAPI)factory.newInstance(EasemobRestAPIFactory.CHATGROUP_CLASS);
    private ChatRoomAPI chatroom = (ChatRoomAPI)factory.newInstance(EasemobRestAPIFactory.CHATROOM_CLASS);



//    @RequestMapping(value = "/create/user", method = RequestMethod.POST)
//    @ApiOperation(value = "创建环信用户")
//    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
//    def createUser(@RequestParam String name,@RequestParam String password,@RequestParam String nickname) {
    /**
     * 创建环信用户并且返回环信用户id
     * @param name
     * @param password
     * @param nickname
     * @return
     */
    def createUser(String name, String password, String nickname) {
        ResponseWrapper responseWrapper = user.createNewIMUserSingle(new IMUserBody(name, password, nickname)) as ResponseWrapper
        ObjectNode objectNode = responseWrapper.getResponseBody() as ObjectNode
        String uuid = objectNode.get("entities").get(0).get("uuid")
        return uuid
    }

    @RequestMapping(value = "/create/user", method = RequestMethod.POST)
    @ApiOperation(value = "创建环信用户")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def createUsers(@RequestParam String name,@RequestParam String password,@RequestParam String nickname) {
//    def createUser(String name, String password, String nickname) {
        ResponseWrapper responseWrapper = user.createNewIMUserSingle(new IMUserBody(name, password, nickname)) as ResponseWrapper
        ObjectNode objectNode = responseWrapper.getResponseBody() as ObjectNode
        String abc = objectNode.get("entities").get(0).get("uuid")
        println(abc)
    }

    @RequestMapping(value = "/chatgroups", method = RequestMethod.POST)
    @ApiOperation(value = "创建环信用户")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def createChatgroups(@RequestParam(required = true) String teamId,HttpServletRequest req) {
//    def createChatgroups( Team currentTeam,User user) {
        def currentUser = req.getAttribute("user") as User
        def currentTeam = teamRepository.findOne(teamId)
        if (!currentTeam) {
            return ResponseMsg.error("请传入正确的群组Id",200)
        }
        String [] members = new String[currentTeam.members.size()]
        currentTeam.members.toArray(members)
        def jsonStr = chatgroup.createChatGroup(new ChatGroupBody(currentTeam.name,currentTeam.desc,true,200,false,currentTeam.authorId,members))

    }

//    @RequestMapping(value = "/chatgroups", method = RequestMethod.POST)
//    @ApiOperation(value = "创建环信用户")
//    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
//    def createChatgroups(@RequestParam(required = true) String teamId,HttpServletRequest req) {
    /**
     * 创建环信群组,返回环信群组Id
     * @param currentTeam
     * @param user
     * @return
     */
    def createChatgroups( Team currentTeam,User user) {
//        def currentUser = req.getAttribute("user") as User
//        def currentTeam = teamRepository.findOne(teamId)
//        if (!currentTeam) {
//            return ResponseMsg.error("请传入正确的群组Id",200)
//        }
        currentTeam.members.remove(currentTeam.authorId)
        String [] members = new String[currentTeam.members.size()]
        currentTeam.members.toArray(members)
        ResponseWrapper responseWrapper = chatgroup.createChatGroup(new ChatGroupBody(currentTeam.name,currentTeam.desc,true,200,false,currentTeam.authorId,members)) as ResponseWrapper
        ObjectNode objectNode = responseWrapper.getResponseBody() as ObjectNode
        String groupid = objectNode.get("data").get("groupid").toString().replace("\"","").trim()
        return groupid
    }

    @RequestMapping(value = "/chatgroups/users", method = RequestMethod.POST)
    @ApiOperation(value = "批量添加用户")
    @ApiImplicitParam(value = "x-token", required = true, paramType = "header", name = "x-token")
    def addChatGroupsUsers(String groupId,String [] members) {
        chatgroup.addBatchUsersToChatGroup(groupId,members as JSON)
    }

}

