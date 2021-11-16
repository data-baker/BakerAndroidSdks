package com.baker.sdk.vpr

import android.util.Log
import com.baker.sdk.http.CallbackListener
import com.baker.sdk.http.CommonOkHttpClient
import com.baker.sdk.http.CommonOkHttpRequest
import com.baker.sdk.vpr.bean.request.VprMatchMoreRequest
import com.baker.sdk.vpr.bean.request.VprMatchRequest
import com.baker.sdk.vpr.bean.request.VprRegisterRequest
import com.baker.sdk.vpr.bean.response.*

/**
 * 标贝易采声纹识别
 * baker vpr:baker voiceprint recognition
 *
 * @author xujian
 * @date 2021/11/11
 */
object BakerVpr : BakerVprAPI {

    /**
     * 获取 access_token  (登录开放平台，点击声纹识别，在授权管理中查看自己的APISecret和APIKey)
     *
     * @param clientSecret 声纹识别授权管理中的 APISecret
     * @param clientId     声纹识别授权管理 APIKey
     */
    fun getToken(
        clientId: String,
        clientSecret: String,
        callbackListener: CallbackListener<GetTokenResponse>
    ) {
        val params: Map<String, String> = mapOf(
            "grant_type" to "client_credentials",
            "client_id" to clientId,
            "client_secret" to clientSecret
        )

        CommonOkHttpClient.sendRequest(
            CommonOkHttpRequest.createGetRequest(
                BakerVprAPI.BAKER_GET_TOKEN,
                params
            ), callbackListener
        )
    }

    /**
     * 创建声纹库，获得获得声纹库id
     *
     * @param accessToken
     * @param callbackListener
     */
    fun createVprId(accessToken: String, callbackListener: CallbackListener<CreateIdResponse>) {
        val params: Map<String, String> = mapOf("access_token" to accessToken)
        CommonOkHttpClient.sendRequest(
            CommonOkHttpRequest.createRequestBodyPostRequest(
                BakerVprAPI.BAKER_VPR_CREATE_ID,
                params
            ), callbackListener
        )
    }

    /**
     * 声纹注册：
     * 少需要调用 3 次该接口完成注册过程，当某次注册返回失败时，需要重新提交注册，直到注册完成
     */
    fun vprRegister(
        vprRegisterRequest: VprRegisterRequest,
        callbackListener: CallbackListener<VprRegisterResponse>
    ) {
        val params: Map<String, Any> =
            mapOf(
                "format" to vprRegisterRequest.format,
                "name" to vprRegisterRequest.name,
                "registerId" to vprRegisterRequest.registerId,
                "scoreThreshold" to vprRegisterRequest.scoreThreshold,
                "access_token" to vprRegisterRequest.access_token,
                "audio" to vprRegisterRequest.audioBase64()
            )

        Log.i("vprRegister", "vprRegister: audioBase64${vprRegisterRequest.audioBase64().length}")
        CommonOkHttpClient.sendRequest(
            CommonOkHttpRequest.createRequestBodyPostRequest(
                BakerVprAPI.BAKER_VPR_REGISTER,
                params
            ), callbackListener
        )
    }

    /**
     * 查询声纹状态码
     *
     * @param accessToken 通过 client_id，client_secret 调用授权服务获得见 获取访问令牌
     * @param registerId 特征库 id
     * @param callbackListener
     */
    fun queryVprStatus(
        accessToken: String?,
        registerId: String?,
        callbackListener: CallbackListener<QueryVprStatusResponse>
    ) {
        val params: Map<String, String?> =
            mapOf("access_token" to accessToken, "registerId" to registerId)
        CommonOkHttpClient.sendRequest(
            CommonOkHttpRequest.createRequestBodyPostRequest(
                BakerVprAPI.BAKER_VPR_STATUS,
                params
            ), callbackListener
        )
    }

    /**
     * 查询声纹状态码
     *
     * @param accessToken 通过 client_id，client_secret 调用授权服务获得见 获取访问令牌
     * @param registerId 特征库 id
     * @param callbackListener
     */
    fun deleteVoicePrint(
        accessToken: String,
        registerId: String,
        callbackListener: CallbackListener<DeleteVoicePrintResponse>
    ) {
        val params: Map<String, String> =
            mapOf("access_token" to accessToken, "registerId" to registerId)
        CommonOkHttpClient.sendRequest(
            CommonOkHttpRequest.createRequestBodyPostRequest(
                BakerVprAPI.BAKER_VPR_DELETE,
                params
            ), callbackListener
        )
    }


    /**
     * 声纹验证(1:1)
     * 上传音频与已存在特征比对，返回是否匹配
     */
    fun vprMatchRatioOne(
        vprMatchRequest: VprMatchRequest,
        callbackListener: CallbackListener<VprMatchResponse>
    ) {
        val params: Map<String, Any> =
            mapOf(
                "access_token" to vprMatchRequest.access_token,
                "audio" to vprMatchRequest.audioBase64(),
                "format" to vprMatchRequest.format,
                "matchId" to vprMatchRequest.matchId,
                "scoreThreshold" to vprMatchRequest.scoreThreshold
            )
        CommonOkHttpClient.sendRequest(
            CommonOkHttpRequest.createRequestBodyPostRequest(
                BakerVprAPI.BAKER_VPR_MATCH_RATIO_1,
                params
            ), callbackListener
        )
    }


    /**
     * 声纹对比(1:N)
     * 用户上传音频，比对库中所有特征，返回匹配的特征列表
     */
    fun vprMatchMore(
        vprMatchMoreRequest: VprMatchMoreRequest,
        callbackListener: CallbackListener<VprMatchMoreResponse>
    ) {
        val params: Map<String, String> =
            mapOf(
                "access_token" to vprMatchMoreRequest.access_token,
                "audio" to vprMatchMoreRequest.audioBase64(),
                "format" to vprMatchMoreRequest.format,
                "listNum" to vprMatchMoreRequest.listNum.toString(),
                "scoreThreshold" to vprMatchMoreRequest.scoreThreshold.toString()
            )
        CommonOkHttpClient.sendRequest(
            CommonOkHttpRequest.createRequestBodyPostRequest(
                BakerVprAPI.BAKER_VPR_SEARCH,
                params
            ), callbackListener
        )
    }


}