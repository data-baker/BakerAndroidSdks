package com.baker.sdk.vpr.bean

import android.util.Base64
import com.baker.sdk.vpr.BakerVpr

/**
 * @author xujian
 * @date 2021/11/11
 */
abstract class VprBaseRequest {

    /**
     * 通过client_id，client_secret调用授权服务获得见获取访问令牌
     */
    internal var access_token: String=BakerVpr.mAccessToken

    /**
     * 注册有效分数，不得低于系统默认值
     */
    abstract val scoreThreshold: Float

    /**
     * 音频数据 base64（采样率 16K，位深 16 位，时长最佳 10 秒，最小 5 秒，最大 30 秒）
     */
    abstract val audio: ByteArray


    /**
     * 声音文件格式(pcm)
     */
    abstract val format: String

    /**
     *把ByteArray转为base64字符串
     */
    fun audioBase64(): String {
        return Base64.encodeToString(audio, Base64.NO_WRAP)
    }
}