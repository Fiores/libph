package com.cab404.libph.requests;

import com.cab404.moonlight.framework.AccessProfile;
import com.cab404.moonlight.framework.EntrySet;

/**
 * @author cab404
 */
public class CommentEditRequest extends LSRequest {

    private int id;
    private final String text;

    public CommentEditRequest(int id, String text) {
        this.id = id;
        this.text = text;
    }


    @Override
    protected String getURL(AccessProfile profile) {
        return "/ec_ajax/savecomment/";
    }

    @Override
    protected void getData(EntrySet<String, String> data) {
        data.put("commentId", id + "");
        data.put("text", text + "");
    }

}