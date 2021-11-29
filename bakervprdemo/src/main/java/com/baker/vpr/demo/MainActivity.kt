package com.baker.vpr.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.baker.sdk.http.CallbackListener
import com.baker.sdk.vpr.BakerVpr
import com.baker.sdk.vpr.bean.response.QueryVprStatusResponse
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.comm.Constants
import com.baker.vpr.demo.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        BakerVpr.initSdk(
            this,
            "34956ce3e37a43d9a2f3d1a88df37d43",
            "87b78a20702e4604bccbce12b3920075"
        )
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