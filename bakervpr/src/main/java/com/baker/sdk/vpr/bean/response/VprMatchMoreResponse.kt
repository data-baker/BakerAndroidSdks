package com.baker.sdk.vpr.bean.response

import com.baker.sdk.vpr.bean.BakerBaseResponse

/**
 * 声纹对比 1：N response
 */
data class VprMatchMoreResponse(
    override val err_msg: String?,
    override val err_no: Int?,
    override val log_id: String?,
    /**
     * 匹配到的声纹特征列表
     */
    val matchList: List<Match?>?

) : BakerBaseResponse() {

    data class Match(
        /**
         * 匹配到的声纹特征 id
         */
        val name: String?,
        /**
         * 比对的分数
         */
        val score: Double?,
        /**
         * 声纹关联名字
         */
        val spkid: String?
    )
}