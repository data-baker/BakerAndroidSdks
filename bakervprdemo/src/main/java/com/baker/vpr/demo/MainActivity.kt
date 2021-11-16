package com.baker.vpr.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.baker.sdk.http.CallbackListener
import com.baker.sdk.vpr.BakerVpr
import com.baker.sdk.vpr.bean.response.GetTokenResponse
import com.baker.sdk.vpr.bean.response.QueryVprStatusResponse
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.comm.Constants
import com.baker.vpr.demo.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        val token = sharedPreferences.getString(Constants.sp_key_access_token, "")
        Log.i(TAG, "token: $token")
        if (token.isNullOrEmpty()) {
            requestToken()
        }

    }

    override fun onResume() {
        super.onResume()
        checkVprStatus()
    }


    private fun initView() {

        mBinding.run {
            toolbar.appToolbar.title = "声纹识别Demo"

//            btnVprMatch.isEnabled = false
//            btnVprSearch.isEnabled = false
            toolbar.appToolbar.setOnClickListener {
                requestToken()
            }
            btnVprRegister.setOnClickListener {
                startActivity(Intent(this@MainActivity, VprInfoActivity::class.java))
            }

            btnVprMatch.setOnClickListener {
                VprMatchActivity.start(this@MainActivity)
            }
            btnVprSearch.setOnClickListener {
                RegisterActivity.start(this@MainActivity,from = 3)
            }
        }
    }

    fun checkVprStatus() {
        runBlocking {
            val token = sharedPreferences.getString(Constants.sp_key_access_token, "")
            val registerid = sharedPreferences.getString(Constants.sp_key_recorder_registerid, "")
            launch(Dispatchers.IO) {
                BakerVpr.queryVprStatus(
                    token,
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

    private fun requestToken() {
        runBlocking {
            launch(Dispatchers.IO) {
                BakerVpr.getToken(
                    "513ce6d99633423584a8b8c5a5a0bdd0",
                    "6387560836774f4b8b28a125647bf851",
                    object : CallbackListener<GetTokenResponse> {
                        override fun onSuccess(response: GetTokenResponse?) {
                            Toast.makeText(this@MainActivity, "获取token成功", Toast.LENGTH_SHORT)
                                .show()
                            mBinding.tvTest.text = response?.access_token
                            sharedPreferences.edit {
                                Log.i(TAG, "onSuccess: ${response.toString()}")
                                putString(Constants.sp_key_access_token, response?.access_token)
                            }
                        }

                        override fun onFailure(e: Exception?) {
                            Toast.makeText(this@MainActivity, "获取token失败", Toast.LENGTH_SHORT)
                                .show()
                        }

                    })
            }
        }
    }
}