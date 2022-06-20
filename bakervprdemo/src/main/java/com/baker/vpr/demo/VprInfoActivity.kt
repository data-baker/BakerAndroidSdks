package com.baker.vpr.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.comm.Constants
import com.baker.vpr.demo.bean.Recorder
import com.baker.vpr.demo.databinding.ActivityVprInfoBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class VprInfoActivity : BaseActivity<ActivityVprInfoBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        mBinding.run {
            if (BuildConfig.DEBUG) {
                etRegisterName.setText("序俭")
                etScore.setText("15")
            }
            toolbar.appToolbar.title = "声纹信息填写"
            btnVprRegister.setOnClickListener {
                val name = etRegisterName.text?.toString()
                val score = etScore.text?.toString()
                if (name.isNullOrBlank()) {
                    Toast.makeText(this@VprInfoActivity, "请填写姓名", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (score.isNullOrBlank()) {
                    Toast.makeText(this@VprInfoActivity, "请填写合格的分数阈值", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }


                RegisterActivity.start(this@VprInfoActivity, name, score, "", 1)
                finish()
            }


        }
    }
}