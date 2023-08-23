package com.baker.sdk.vpr.bean.response

import com.baker.sdk.vpr.bean.BakerBaseResponse

/**
 *查询声纹状态码 response
 *@author xujian
 *@date 2021/11/11
 */
data class QueryVprStatusResponse(
    override val err_msg: String?,
    override val err_no: Int?,
    override val log_id: String?,
    /**声纹注册次数，3：注册成功，0：未注册*/
    val status: Int?

) : BakerBaseResponse()