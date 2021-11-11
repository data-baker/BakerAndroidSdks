package com.baker.sdk.vpr.bean

/**
 *
 *@author xujian
 *@date 2021/11/11
 */
abstract class BakerBaseResponse {
    /**
     * SUCCESS 表示调用成功  ，否则失败
     */
    abstract val err_msg: String?

    /**
     * 90000 表示调用成功
     */
    abstract val err_no: Int?

    /**
     * 日志跟踪 id
     */
    abstract val log_id: String?
}
