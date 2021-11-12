package com.baker.vpr.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.baker.sdk.http.CallbackListener
import com.baker.sdk.vpr.BakerVpr
import com.baker.sdk.vpr.bean.response.GetTokenResponse
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.base.Constants
import com.baker.vpr.demo.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        val token = sharedPreferences.getString(Constants.sp_key_access_token, "")
        Log.e(TAG, "token: $token")
        if (token.isNullOrEmpty()) {
            requestToken()
        }

    }


    private fun initView() {
        mBinding.tvGetToken.setOnClickListener {
            requestToken()
        }
        mBinding.btnVprRegister.setOnClickListener {
            startActivity(Intent(this, VprInfoActivity::class.java))
        }
    }

    private fun requestToken() {
        BakerVpr.getToken(
            "513ce6d99633423584a8b8c5a5a0bdd0",
            "6387560836774f4b8b28a125647bf851",
            object : CallbackListener<GetTokenResponse> {
                override fun onSuccess(response: GetTokenResponse?) {
                    Toast.makeText(this@MainActivity, "获取token成功", Toast.LENGTH_SHORT).show()
                    sharedPreferences.edit {
                        Log.e(TAG, "onSuccess: ${response.toString()}")
                        putString(Constants.sp_key_access_token, response?.access_token)
                    }
                }

                override fun onFailure(e: Exception?) {
                    Toast.makeText(this@MainActivity, "获取token失败", Toast.LENGTH_SHORT).show()
                }

            })
    }
}