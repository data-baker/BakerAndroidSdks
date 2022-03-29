package com.baker.sdk.vpr.bean.response

import com.baker.sdk.vpr.bean.BakerBaseResponse

/**
 * 声纹验证 1:1 response
 *@author xujian
 *@date 2021/11/11
 */
data class VprMatchResponse(
    override val err_msg: String?,
    override val err_no: Int?,
    override val log_id: String?,

    /** 1 表示比对成功，0 表示比对失败*/
    val matchStatus: Int?,
    /**比对分数*/
    val score: String
) : BakerBaseResponse()

