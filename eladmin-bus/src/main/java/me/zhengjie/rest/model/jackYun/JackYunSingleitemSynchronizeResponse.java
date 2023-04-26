package me.zhengjie.rest.model.jackYun;

import me.zhengjie.support.jackYun.JackYunBasicResponse;

public class JackYunSingleitemSynchronizeResponse extends JackYunBasicResponse {
    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
