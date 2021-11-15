package com.baker.vpr.demo

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import com.baker.vpr.demo.adapters.AudioRecordVPAdapter
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.comm.AudioRecorder
import com.baker.vpr.demo.comm.Constants
import com.baker.vpr.demo.comm.RecordStreamListener
import com.baker.vpr.demo.databinding.ActivityRegisterBinding
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.util.*


class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {
    val fileName = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AudioRecorder.instance.createDefaultAudio(fileName)

        initView()
        requestRuntimePermissions { Toast.makeText(this, "获取到所有权限", Toast.LENGTH_SHORT).show() }
    }

    private fun initView() {
        mBinding.run {
            toolbar.appToolbar.title = getString(R.string.vpr_register)

            val audioTexts = resources.getStringArray(R.array.register_audio_text)

            vpAudioText.adapter =
                AudioRecordVPAdapter(this@RegisterActivity, audioTexts.toMutableList())
            vpAudioText.isUserInputEnabled = true

            btnAudioControl.setOnClickListener {

//                when (AudioRecorder.instance.status) {
//                    /**未开始*/
//                    AudioRecorder.Status.STATUS_NO_READY -> {
//                        AudioRecorder.instance.createDefaultAudio(
//                            fileName
//                        )
//                    }
//                    /**预备*/
//                    AudioRecorder.Status.STATUS_READY -> {
//                        AudioRecorder.instance.startRecord(object : RecordStreamListener {
//                            override fun recordOfByte(audiodata: ByteArray?, i: Int, length: Int) {
//                            }
//                        })
//                    }
//
//                    /**录音*/
//                    AudioRecorder.Status.STATUS_START -> {
//                    }
//
//                    /**暂停*/
//                    AudioRecorder.Status.STATUS_PAUSE -> {
//                    }
//
//                    /**停止*/
//                    AudioRecorder.Status.STATUS_STOP -> {
//                    }
//                }

                val currentItem = vpAudioText.currentItem
                when(currentItem){
                    0->{
                        Toast.makeText(this@RegisterActivity, "开始录音", Toast.LENGTH_SHORT).show()
                        btnAudioControl.text=getString(R.string.next_page)

                    }
                    1->{
                        btnAudioControl.text=getString(R.string.next_page)

                    }
                    2->{
                        btnAudioControl.text="结束录音并提交"

                    }
                }

                if (currentItem < audioTexts.size) {
                    vpAudioText.currentItem = currentItem + 1
                }
                tvTextPosition.text = "第${currentItem + 1}段 共3段"
            }
        }
    }

    @AfterPermissionGranted(value = Constants.AudioPermissionRequestCode)
    fun requestRuntimePermissions(block: () -> Unit) {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
        if (EasyPermissions.hasPermissions(this, *permissions)
        ) {
            block.invoke()
        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, Constants.AudioPermissionRequestCode, *permissions)
                    .setRationale("录音需要获得语音和存储权限")
                    .setPositiveButtonText(R.string.rationale_ask_ok)
                    .setNegativeButtonText(R.string.rationale_ask_cancel)
                    .build()
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 将结果转发给 EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}