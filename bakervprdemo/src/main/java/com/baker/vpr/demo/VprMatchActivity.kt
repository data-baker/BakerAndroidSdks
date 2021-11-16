package com.baker.vpr.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.baker.vpr.demo.adapters.RegisterIdListAdapter
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.bean.Recorder
import com.baker.vpr.demo.comm.Constants
import com.baker.vpr.demo.databinding.ActivityVprMatchBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class VprMatchActivity : BaseActivity<ActivityVprMatchBinding>() {

    lateinit var mAdapter: RegisterIdListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        val gson = Gson()
        val recorderListStr = sharedPreferences.getString(Constants.sp_key_recorders, "")
        Log.i(TAG, "recorderListStr:$recorderListStr ")
        val recorderList = mutableListOf<Recorder>()
        if (recorderListStr?.isNotEmpty() == true) {
            val recorders = gson.fromJson<List<Recorder>>(
                recorderListStr,
                object : TypeToken<List<Recorder>>() {}.type
            )
            recorderList.addAll(recorders)
            mAdapter.setData(recorderList)

        }


    }

    private fun initView() {
        mBinding.run {
            toolbar.appToolbar.title = "声纹列表"
            rvVprList.layoutManager = LinearLayoutManager(this@VprMatchActivity)
            mAdapter = RegisterIdListAdapter()
            rvVprList.adapter = mAdapter

        }

    }


    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, VprMatchActivity::class.java))
        }
    }
}