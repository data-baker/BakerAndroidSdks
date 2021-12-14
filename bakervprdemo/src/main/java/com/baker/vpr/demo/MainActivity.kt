package com.baker.vpr.demo

import android.content.Intent
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.baker.sdk.http.CallbackListener
import com.baker.sdk.vpr.BakerVpr
import com.baker.sdk.vpr.bean.response.GetTokenResponse
import com.baker.sdk.vpr.bean.response.QueryVprStatusResponse
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.comm.Constants
import com.baker.vpr.demo.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.lang.Exception

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        val key = resources.getStringArray(R.array.vpr_key)
        BakerVpr.initSdk(this, key[0], key[1], object : CallbackListener<GetTokenResponse> {
            override fun onSuccess(response: GetTokenResponse?) {
                Toast.makeText(this@MainActivity, "token获取成功", Toast.LENGTH_SHORT)
                    .apply {
                        setGravity(Gravity.CENTER, 0, 0)
                    }.show()
            }

            override fun onFailure(e: Exception?) {
                Toast.makeText(this@MainActivity, "token获取失败" + e?.message, Toast.LENGTH_SHORT)
                    .apply {
                        setGravity(Gravity.CENTER, 0, 0)
                    }.show()
            }

        })
        getValidSampleRates()
    }


    /**
     * 检测有效采样率
     */
    fun getValidSampleRates() {
        val list =
            listOf<Int>(8000, 11025, 16000, 22050, 44100, 48000, 50000, 96000, 192000, 100000000)
        for (rate in list) { // add the rates you wish to check against
            val bufferSize = AudioTrack.getMinBufferSize(
                rate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            Log.i(TAG, "getValidSampleRates: rate=$rate\tbufferSize=$bufferSize")
            //bufferSize > 0 buffer size is valid, Sample rate supported

        }
    }

    private fun initView() {

        mBinding.run {
            toolbar.appToolbar.title = "声纹识别Demo"

//            btnVprMatch.isEnabled = false
//            btnVprSearch.isEnabled = false
            toolbar.appToolbar.setOnClickListener {
                checkVprStatus()
            }
            btnVprRegister.setOnClickListener {
                startActivity(Intent(this@MainActivity, VprInfoActivity::class.java))
            }

            btnVprMatch.setOnClickListener {
                VprMatchActivity.start(this@MainActivity)
            }
            btnVprSearch.setOnClickListener {
                RegisterActivity.start(this@MainActivity, from = 3)
            }
        }
    }

    fun checkVprStatus() {
        runBlocking {
            val registerid = sharedPreferences.getString(Constants.sp_key_recorder_registerid, "")
            launch(Dispatchers.IO) {
                BakerVpr.queryVprStatus(
                    registerid,
                    object : CallbackListener<QueryVprStatusResponse> {
                        override fun onSuccess(response: QueryVprStatusResponse?) {
                            if (response?.err_no != 90000 || response.status != 3) {
                                Toast.makeText(this@MainActivity, "请先完成声纹注册", Toast.LENGTH_SHORT)
                                    .show()
                                return
                            }
                            mBinding.btnVprMatch.isEnabled = true
                            mBinding.btnVprSearch.isEnabled = true
                        }

                        override fun onFailure(e: java.lang.Exception?) {
                        }

                    })
            }
        }
    }

}