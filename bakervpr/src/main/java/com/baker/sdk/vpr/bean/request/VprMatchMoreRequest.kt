package com.baker.sdk.vpr.bean.request

import com.baker.sdk.vpr.bean.VprBaseRequest

/**
 * 声纹对比 1:N request
 *
 * @author xujian
 * @date 2021/11/11
 */
data class VprMatchMoreRequest(
    override val access_token: String,
    override val audio: String,
    override val format: String,
    override val scoreThreshold: Double,
    /**返回匹配列表的数据条数 id*/
    val listNum: Int
) : VprBaseRequest()