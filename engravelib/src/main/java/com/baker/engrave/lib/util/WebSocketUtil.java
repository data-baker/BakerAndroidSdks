package com.baker.engrave.lib.util;

import com.baker.engrave.lib.bean.RecordingSocketBean;
import com.baker.engrave.lib.net.NetConstants;
import com.baker.engrave.lib.net.NetUtil;

import java.util.HashMap;
import java.util.Map;

public class WebSocketUtil {


    public static RecordingSocketBean formatParameters(RecordingSocketBean.ParamBean paramBean, RecordingSocketBean.AudioBean audioBean) {
        String token, userId, nounce, timestamp, signature;
        token = NetUtil.getToken();
        userId = NetUtil.getClientId();
        nounce = String.valueOf(NetUtil.random6num());
        timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("clientId", userId);
        params.put("nounce", nounce);
        params.put("timestamp", timestamp);
        signature = NetUtil.genSignature(NetConstants.VERSION, nounce, params);
        RecordingSocketBean.HeaderBean headerBean = new RecordingSocketBean.HeaderBean(token, userId, nounce, timestamp, signature);
        RecordingSocketBean recordingSocketBean = new RecordingSocketBean(headerBean, paramBean, audioBean);
        return recordingSocketBean;
    }
}
