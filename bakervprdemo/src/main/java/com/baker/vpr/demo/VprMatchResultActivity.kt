package com.baker.vpr.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.comm.Constants
import com.baker.vpr.demo.databinding.ActivityVprMatchResultBinding

class VprMatchResultActivity : BaseActivity<ActivityVprMatchResultBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.toolbar.appToolbar.title = "验证结果"
        initData()

    }

    private fun initData() {
        intent.run {
            val name = getStringExtra(Constants.sp_key_recorder_name)
            val registerId = getStringExtra(Constants.sp_key_recorder_registerid)
            val score = getStringExtra(Constants.sp_key_recorder_score)
            val status = getIntExtra(Constants.sp_key_recorder_status, 0)


            mBinding.run {
                tvMatchResult.text = if (status == 1) "声音匹配" else "声音不匹配"
                tvMatchResult.setTextColor(
                    resources.getColor(
                        if (status == 1) R.color.green else R.color.red
                    )
                )
                tvMatchInfo.text = "$name \n 声纹ID：$registerId \n 匹配分数：$score"
                tvClose.setOnClickListener {
                    finish()
                }
            }

        }
    }

    companion object {
        fun start(context: Context, name: String, registerId: String, score: String, status: Int) {
            context.startActivity(Intent(context, VprMatchResultActivity::class.java).run {
                putExtra(Constants.sp_key_recorder_name, name)
                putExtra(Constants.sp_key_recorder_registerid, registerId)
                putExtra(Constants.sp_key_recorder_score, score)
                putExtra(Constants.sp_key_recorder_status, status)
            })
        }
    }
}