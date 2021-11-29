package com.baker.sdk.vpr.bean.response

/**
 *获取访问令牌 response
 *@author xujian
 *@date 2021/11/11
 */
data class GetTokenResponse(
    val access_token: String?,
    val expires_in: Int?,
    val jti: String?,
    val scope: String?,
    val token_type: String?,
    var error:String?=null,
    var error_description:String?=null
)
