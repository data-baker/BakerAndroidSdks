package com.baker.vpr.demo.comm

/**
 *
 *@author xujian
 *@date 2021/11/12
 */
interface Constants {
    companion object {
        const val sharedPreference_name = "Vpr_sharedPreferences"
        const val sp_key_access_token = "token"
        const val sp_key_recorder_registerid = "registerid"
        const val sp_key_recorder_name: String = "recorder_name"
        const val sp_key_recorder_score: String = "recorder_score"
        const val sp_key_recorder_status: String = "recorder_status"
        const val sp_key_recorders: String = "recorder_set"
        const val AudioPermissionRequestCode = 10

        /**
         * 1:声纹信息填写
         * 2：声纹信息列表
         */
        const val register_get_from = "register_get_from"

    }
}