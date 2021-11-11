package com.baker.sdk.vpr.bean.response

import com.baker.sdk.vpr.bean.BakerBaseResponse

/**
 * 创建声纹库 response
 *@author xujian
 *@date 2021/11/11
 */
class CreateIdResponse(
    override val err_msg: String?,
    override val err_no: Int?,
    override val log_id: String?,
    val registerid: String?
) : BakerBaseResponse()
