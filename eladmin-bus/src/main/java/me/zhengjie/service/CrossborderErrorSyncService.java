package me.zhengjie.service;

import com.taobao.pac.sdk.cp.ReceiveListener;
import com.taobao.pac.sdk.cp.ReceiveSysParams;
import com.taobao.pac.sdk.cp.dataobject.request.CROSSBORDER_ERROR_SYNC.CrossborderErrorSyncRequest;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_ERROR_SYNC.CrossborderErrorSyncResponse;

public interface CrossborderErrorSyncService extends ReceiveListener<CrossborderErrorSyncRequest, CrossborderErrorSyncResponse> {
    CrossborderErrorSyncResponse execute(ReceiveSysParams receiveSysParams, CrossborderErrorSyncRequest crossborderErrorSyncRequest);
}
