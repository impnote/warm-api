package com.donler.thirdparty.easemob.server.api.impl;

import com.donler.thirdparty.easemob.server.api.EasemobRestAPI;
import com.donler.thirdparty.easemob.server.api.FileAPI;
import com.donler.thirdparty.easemob.server.comm.helper.HeaderHelper;
import com.donler.thirdparty.easemob.server.comm.wrapper.HeaderWrapper;

import java.io.File;

public class EasemobFile extends EasemobRestAPI implements FileAPI {
    private static final String ROOT_URI = "/chatfiles";

    @Override
    public String getResourceRootURI() {
        return ROOT_URI;
    }

    public Object uploadFile(Object file) {
        String url = getContext().getSeriveURL() + getResourceRootURI();
        HeaderWrapper header = HeaderHelper.getUploadHeaderWithToken();

        return getInvoker().uploadFile(url, header, (File) file);
    }

    public Object downloadFile(String fileUUID, String shareSecret, Boolean isThumbnail) {
        String url = getContext().getSeriveURL() + getResourceRootURI() + "/" + fileUUID;
        HeaderWrapper header = HeaderHelper.getDownloadHeaderWithToken(shareSecret, isThumbnail);

        return getInvoker().downloadFile(url, header, null);
    }
}
