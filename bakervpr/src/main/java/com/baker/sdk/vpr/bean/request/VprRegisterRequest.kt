package com.baker.sdk.vpr.bean.request

import com.baker.sdk.vpr.bean.VprBaseRequest

/**
 *声纹注册 request
 *@author xujian
 *@date 2021/11/11
 */
data class VprRegisterRequest(
    override val access_token: String,
    override val audio: ByteArray,

    override val format: String,
    override val scoreThreshold: Double,

    /**
     * 调用创建声纹库接口返回的 id
     */
    val registerId: String,
    /**
     * 自定义名字
     */
    val name: String
) : VprBaseRequest()