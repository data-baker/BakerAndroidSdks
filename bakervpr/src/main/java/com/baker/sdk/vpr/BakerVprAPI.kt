package com.baker.sdk.vpr


/**
 * 声纹识别API
 *@author xujian
 *@date 2021/11/11
 */
class BakerVprAPI {

    companion object {


        private const val host_fat = "http://10.10.50.19:50601/"
        private const val host_uat = "https://openapitest.data-baker.com/"
        private const val vpr_host = host_uat

        private const val token_fat = "http://10.10.50.23:9904/"
        private const val token_uat = "https://oauth2cmstest.data-baker.com:8012/"
        private const val token_host = token_uat

        /**
         * 获取token URL
         */
        internal const val BAKER_GET_TOKEN = "${token_host}oauth/2.0/token"

        /**
         * 创建声纹库，拿到registerid 的URL
         */
        internal const val BAKER_VPR_CREATE_ID = "${vpr_host}vpr/createid"

        /**
         *声纹注册
         */
        internal const val BAKER_VPR_REGISTER = "${vpr_host}vpr/register"

        /**
         * 声纹验证(1:1)
         */
        internal const val BAKER_VPR_MATCH_RATIO_1 = "${vpr_host}vpr/match"

        /**
         * 查询声纹状态码
         */
        internal const val BAKER_VPR_STATUS = "${vpr_host}vpr/status"

        /**
         * 删除声纹
         */
        internal const val BAKER_VPR_DELETE = "${vpr_host}vpr/delete"

        /**
         * 声纹对比（1:N）
         */
        internal const val BAKER_VPR_SEARCH = "${vpr_host}vpr/search"

    }

}