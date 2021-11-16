package com.baker.sdk.vpr.bean.request

import com.baker.sdk.vpr.bean.VprBaseRequest

/**
 * 声纹验证 1:1 request
 * @author xujian
 * @date 2021/11/11
 */
data class VprMatchRequest(
    override val access_token: String,
    override val audio: ByteArray,
    override val format: String,
    override val scoreThreshold: Float,
    /**调用创建声纹库接口返回的 id*/
    val matchId: String
) : VprBaseRequest()

