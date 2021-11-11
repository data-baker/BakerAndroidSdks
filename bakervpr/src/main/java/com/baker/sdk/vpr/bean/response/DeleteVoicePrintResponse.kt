package com.baker.sdk.vpr.bean.response

import com.baker.sdk.vpr.bean.BakerBaseResponse

/**
 * 删除声纹 response
 * @author xujian
 * @date 2021/11/11
 */
class DeleteVoicePrintResponse(
    override val err_msg: String?,
    override val err_no: Int?,
    override val log_id: String?
) :BakerBaseResponse()