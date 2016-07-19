package com.donler.thirdparty.easemob.server;

import com.donler.thirdparty.easemob.server.api.*;
import com.donler.thirdparty.easemob.server.comm.ClientContext;
import com.donler.thirdparty.easemob.server.comm.EasemobRestAPIFactory;
import com.donler.thirdparty.easemob.server.comm.wrapper.ResponseWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Main {

	public static void main(String[] args) throws Exception {
		EasemobRestAPIFactory factory = ClientContext.getInstance().init(ClientContext.INIT_FROM_PROPERTIES).getAPIFactory();

		IMUserAPI user = (IMUserAPI)factory.newInstance(EasemobRestAPIFactory.USER_CLASS);
		ChatMessageAPI chat = (ChatMessageAPI)factory.newInstance(EasemobRestAPIFactory.MESSAGE_CLASS);
		FileAPI file = (FileAPI)factory.newInstance(EasemobRestAPIFactory.FILE_CLASS);
		SendMessageAPI message = (SendMessageAPI)factory.newInstance(EasemobRestAPIFactory.SEND_MESSAGE_CLASS);
		ChatGroupAPI chatgroup = (ChatGroupAPI)factory.newInstance(EasemobRestAPIFactory.CHATGROUP_CLASS);
		ChatRoomAPI chatroom = (ChatRoomAPI)factory.newInstance(EasemobRestAPIFactory.CHATROOM_CLASS);

        ResponseWrapper fileResponse = (ResponseWrapper) file.uploadFile(new File(Main.class.getClassLoader().getResource("." + File.separator + "static" + File.separator + "dog.jpg").getPath()));
        String uuid = ((ObjectNode) fileResponse.getResponseBody()).get("entities").get(0).get("uuid").asText();
        String shareSecret = ((ObjectNode) fileResponse.getResponseBody()).get("entities").get(0).get("share-secret").asText();
        InputStream in = (InputStream) ((ResponseWrapper) file.downloadFile(uuid, shareSecret, false)).getResponseBody();
        FileOutputStream fos = new FileOutputStream("d:/logo1.png");
        byte[] buffer = new byte[1024];
        int len1 = 0;
        while ((len1 = in.read(buffer)) != -1) {
            fos.write(buffer, 0, len1);
        }
        fos.close();

        /*
		// Create a IM user
		BodyWrapper userBody = new IMUserBody("User101", "123456", "HelloWorld");
		user.createNewIMUserSingle(userBody);

		// Create some IM users
		List<IMUserBody> users = new ArrayList<IMUserBody>();
		users.add(new IMUserBody("User002", "123456", null));
		users.add(new IMUserBody("User003", "123456", null));
		BodyWrapper usersBody = new IMUsersBody(users);
		user.createNewIMUserBatch(usersBody);

		// Get a IM user
		user.getIMUsersByUserName("User001");

		// Get a fake user
		user.getIMUsersByUserName("FakeUser001");

		// Get 12 users
		user.getIMUsersBatch(null, null);
		*/
	}

}
