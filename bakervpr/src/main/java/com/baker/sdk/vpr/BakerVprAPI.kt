package com.baker.sdk.vpr

/**
 * 声纹识别API
 *@author xujian
 *@date 2021/11/11
 */
class BakerVprAPI {

    companion object {
        /**
         * 获取token URL
         */
        internal const val BAKER_GET_TOKEN = "http://10.10.50.23:9904/oauth/2.0/token"

        /**
         * 创建声纹库，拿到registerid 的URL
         */
        internal const val BAKER_VPR_CREATE_ID = "http://10.10.50.19:50601/vpr/createid"

        /**
         *声纹注册
         */
        internal const val BAKER_VPR_REGISTER = "http://10.10.50.19:50601/vpr/register"

        /**
         * 声纹验证(1:1)
         */
        internal const val BAKER_VPR_MATCH_RATIO_1 = "http://10.10.50.19:50601/vpr/match"

        /**
         * 查询声纹状态码
         */
        internal const val BAKER_VPR_STATUS = "http://10.10.50.19:50601/vpr/status"

        /**
         * 删除声纹
         */
        internal const val BAKER_VPR_DELETE = "http://10.10.50.19:50601/vpr/delete"

        /**
         * 声纹对比（1:N）
         */
        internal const val BAKER_VPR_SEARCH = "http://10.10.50.19:50601/vpr/search"

    }

}