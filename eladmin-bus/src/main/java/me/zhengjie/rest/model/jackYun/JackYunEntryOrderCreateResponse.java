package me.zhengjie.rest.model.jackYun;

import me.zhengjie.support.jackYun.JackYunBasicResponse;

public class JackYunEntryOrderCreateResponse extends JackYunBasicResponse {
    private String entryOrderId;

    public String getEntryOrderId() {
        return entryOrderId;
    }

    public void setEntryOrderId(String entryOrderId) {
        this.entryOrderId = entryOrderId;
    }
}
