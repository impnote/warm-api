package com.donler.thirdparty.easemob.server

import com.donler.thirdparty.easemob.server.api.*
import com.donler.thirdparty.easemob.server.comm.ClientContext
import com.donler.thirdparty.easemob.server.comm.EasemobRestAPIFactory
import com.donler.thirdparty.easemob.server.comm.body.ModifyNicknameBody
import com.donler.thirdparty.easemob.server.comm.wrapper.BodyWrapper
import com.fasterxml.jackson.databind.node.ContainerNode

/**
 * Created by zhangjiasheng on 7/19/16.
 */
class MainApp {



    static void main(String[] args) {
        EasemobRestAPIFactory factory = ClientContext.getInstance().init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();

        IMUserAPI user = (IMUserAPI)factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
        ChatMessageAPI chat = (ChatMessageAPI)factory.newInstance(EasemobRestAPIFactory.MESSAGE_CLASS);
        FileAPI file = (FileAPI)factory.newInstance(EasemobRestAPIFactory.FILE_CLASS);
        SendMessageAPI message = (SendMessageAPI)factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);
        ChatGroupAPI chatgroup = (ChatGroupAPI)factory.newInstance(EasemobRestAPIFactory.CHATGROUP_CLASS);
        ChatRoomAPI chatroom = (ChatRoomAPI)factory.newInstance(EasemobRestAPIFactory.CHATROOM_CLASS);


//        def res = user.createNewIMUserSingle(new IMUserBody("jason123", "jason", "jason12345"))
//        print("===================create>>>>>>>>>>>>>>>>>>>>>>")
//        println(res)

//        def res1 = user.getIMUsersByUserName("jerry")
//        println("=======get>>>>>${res1}")

        def res = user.modifyIMUserNickNameWithAdminToken("jerry",new ModifyNicknameBody("Riven"))


    }
}
