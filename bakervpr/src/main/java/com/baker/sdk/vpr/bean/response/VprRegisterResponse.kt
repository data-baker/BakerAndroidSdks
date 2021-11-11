package com.baker.sdk.vpr.bean.response

import com.baker.sdk.vpr.bean.BakerBaseResponse

/**
 * 声纹注册 response
 *@author xujian
 *@date 2021/11/11
 */
data class VprRegisterResponse(
    override val err_msg: String?,
    override val err_no: Int?,
    override val log_id: String?,
    /**
     * 注册成功次数，为 3 时表示完成注册
     */
    val suc_num: Int?
) : BakerBaseResponse()

/*
 {
      "err_msg": "SUCCESS",
      "log_id": "1632539555089301",
      "err_no": 0,
      "suc_num": 3
  }*/
