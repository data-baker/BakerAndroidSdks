package com.baker.sdk.vpr

/**
 * 声纹识别API
 *@author xujian
 *@date 2021/11/11
 */
interface BakerVprAPI {

    companion object {
        /**
         * 获取token URL
         */
        internal const val BAKER_GET_TOKEN = "https://openapi.data-baker.com/oauth/2.0/token"

        /**
         * 创建声纹库，拿到registerid 的URL
         */
        internal const val BAKER_VPR_CREATE_ID = "https://openapi.data-baker.com/vpr/createid"

        /**
         *声纹注册
         */
        internal const val BAKER_VPR_REGISTER = "https://openapi.data-baker.com/vpr/register"

        /**
         * 声纹验证(1:1)
         */
        internal const val BAKER_VPR_MATCH_RATIO_1 = "https://openapi.data-baker.com/vpr/match"

        /**
         * 查询声纹状态码
         */
        internal const val BAKER_VPR_STATUS = "https://openapi.data-baker.com/vpr/status"

        /**
         * 删除声纹
         */
        internal const val BAKER_VPR_DELETE = "https://openapi.data-baker.com/vpr/delete"

        /**
         * 声纹对比（1:N）
         */
        internal const val BAKER_VPR_SEARCH = "https://openapi.data-baker.com/vpr/search"

    }

}