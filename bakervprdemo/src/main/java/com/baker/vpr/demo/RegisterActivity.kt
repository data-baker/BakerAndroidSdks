package com.baker.vpr.demo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import com.baker.sdk.http.CallbackListener
import com.baker.sdk.vpr.BakerVpr
import com.baker.sdk.vpr.bean.request.VprRegisterRequest
import com.baker.sdk.vpr.bean.response.CreateIdResponse
import com.baker.sdk.vpr.bean.response.VprRegisterResponse
import com.baker.vpr.demo.audio.AudioRecorderHelper
import com.baker.vpr.demo.adapters.AudioRecordVPAdapter
import com.baker.vpr.demo.audio.Config
import com.baker.vpr.demo.audio.RecorderCallback
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.bean.Recorder
import com.baker.vpr.demo.comm.Constants
import com.baker.vpr.demo.databinding.ActivityRegisterBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.util.*


class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {


    private lateinit var PCM_PATH: String
    lateinit var mRecordName: String
    lateinit var mRecordScore: String
    private val WAV_PATH =
        Environment.getExternalStorageDirectory().toString() + "/Insane" + "/test.wav"
    private lateinit var recorderHelper: AudioRecorderHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        initView()

        requestRuntimePermissions { Timber.i("获得所需权限") }
    }

    private fun initData() {
        mRecordName = intent.getStringExtra(Constants.sp_key_recorder_name)
        mRecordScore = intent.getStringExtra(Constants.sp_key_recorder_score)
        requestRegisterId()
    }


    private fun initView() {
        mBinding.run {
            toolbar.appToolbar.title = getString(R.string.vpr_register)
            imgRecording.visibility = View.INVISIBLE

            val audioTexts = resources.getStringArray(R.array.register_audio_text)

            vpAudioText.adapter =
                AudioRecordVPAdapter(this@RegisterActivity, audioTexts.toMutableList())
            vpAudioText.isUserInputEnabled = false

            btnAudioControl.setOnClickListener {
                recordAudio()
            }
            tvTextHint.setOnClickListener {
                requestRegisterId()
            }

        }
    }

    private fun recordAudio() {
        recorderHelper = AudioRecorderHelper.getInstance()
        if (recorderHelper.recordStatus) {//正在录制
            Toast.makeText(this@RegisterActivity, "正在提交注册", Toast.LENGTH_SHORT)
                .show()
            mBinding.btnAudioControl.text = getString(R.string.start_speak)
            mBinding.imgRecording.visibility = View.INVISIBLE

            recorderHelper.stopRecorder()
            mBinding.btnAudioControl.postDelayed({
                uploadAudioData()
            }, 100)

        } else {
            Toast.makeText(this@RegisterActivity, "开始录音", Toast.LENGTH_SHORT).show()
            mBinding.btnAudioControl.text = getString(R.string.stop_and_register)
            mBinding.imgRecording.visibility = View.VISIBLE

            val file = getSaveFilePath()
            PCM_PATH = file.absolutePath
            recorderHelper.startRecorder(file, object : RecorderCallback {
                override fun onRecorded(bytes: ByteArray?) {
                    val decibel = recorderHelper.getDecibelForPcm(bytes, Config.ENCODING_FORMAT)
                    runOnUiThread {
                        changeVolumeImg(decibel.toInt())
                    }
                }
            })
        }
    }

    private fun uploadAudioData() {
        val audioTexts = resources.getStringArray(R.array.register_audio_text)
        val currentItem = mBinding.vpAudioText.currentItem

        //getBytesByFile(PCM_PATH)
        val bytes = File(PCM_PATH).readBytes()
        Log.i(TAG, "uploadAudioData: File.size${bytes.size}")
        val accessToken =
            sharedPreferences.getString(Constants.sp_key_access_token, "")
        val registerid =
            sharedPreferences.getString(Constants.sp_key_registerid, "")
        val vprRegisterRequest = VprRegisterRequest(
            access_token = accessToken ?: "",
            format = "pcm",
            audio = bytes ?: byteArrayOf(),
            name = mRecordName,
            scoreThreshold = mRecordScore.toFloat(),
            registerId = registerid ?: ""
        )
        BakerVpr.vprRegister(
            vprRegisterRequest,
            object : CallbackListener<VprRegisterResponse> {
                override fun onSuccess(response: VprRegisterResponse?) {
                    if (response?.err_no != 9000) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "注册失败了！${response?.err_msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    if (currentItem < audioTexts.size) {
                        mBinding.vpAudioText.currentItem = currentItem + 1
                    } else {
                        finish()
                    }
                    mBinding.tvTextPosition.text = "第${currentItem + 1}段 共${audioTexts.size}段"
                }

                override fun onFailure(e: Exception?) {
                    e?.printStackTrace()
                    Toast.makeText(
                        this@RegisterActivity,
                        "第${currentItem + 1}段语音注册失败",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    /**
     * 获取文件保存路径
     */
    private fun getSaveFilePath(): File {
        val file = File(Environment.getExternalStorageDirectory(), "audio")
        if (!file.exists()) {
            file.mkdirs()
        }
        val wavFile = File(file, UUID.randomUUID().toString() + ".pcm")
        if (wavFile.isFile) {
            Timber.d("file = ${wavFile.absolutePath}")
        } else {
            Timber.d("文件不是file ${wavFile.absolutePath}")
        }
        if (wavFile.exists()) {
            Timber.d("文件存在吗 = true")
        } else {
            Timber.d("文件不存在")
        }
        return wavFile
    }

    private fun requestRegisterId() {
        runBlocking {
            launch(
                Dispatchers.IO
            ) {
                val token = sharedPreferences.getString(Constants.sp_key_access_token, "")
                token?.let {
                    BakerVpr.createVprId(token, object : CallbackListener<CreateIdResponse> {
                        override fun onSuccess(response: CreateIdResponse?) {
                            if (response?.err_no == 90000) {
                                mBinding.tvTextHint.text = response.registerid
                                Timber.d("createVprId-${response.err_msg}\t${response.registerid}")

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
                                }
                                recorderList.add(Recorder(mRecordName, mRecordScore,response.registerid))
                                sharedPreferences.edit {
                                    val recorders = gson.toJson(recorderList)
                                    putString(Constants.sp_key_recorders, recorders)
                                }

                                sharedPreferences.edit {
                                    putString(Constants.sp_key_registerid, response.registerid)
                                }
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "数据异常${response?.err_msg}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(e: Exception?) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "创建声纹库失败",
                                Toast.LENGTH_SHORT
                            ).show()
                            e?.printStackTrace()
                        }
                    })
                }
            }
        }
    }

    //将文件转换成Byte数组
    fun getBytesByFile(pathStr: String?): ByteArray? {
        val file = File(pathStr)
        try {
            val fis = FileInputStream(file)
            val bos = ByteArrayOutputStream(1024)
            val b = ByteArray(1024)
            var n: Int
            while (fis.read(b).also { n = it } != -1) {
                bos.write(b, 0, n)
            }
            fis.close()
            val data: ByteArray = bos.toByteArray()
            bos.close()
            return data
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun changeVolumeImg(volume: Int) {
        mBinding.run {
            if (volume < 30) {
                imgRecording.setImageResource(R.mipmap.volume_1)
            } else if (volume < 40) {
                imgRecording.setImageResource(R.mipmap.volume_2)
            } else if (volume < 50) {
                imgRecording.setImageResource(R.mipmap.volume_3)
            } else if (volume < 60) {
                imgRecording.setImageResource(R.mipmap.volume_4)
            } else if (volume < 70) {
                imgRecording.setImageResource(R.mipmap.volume_5)
            } else if (volume < 80) {
                imgRecording.setImageResource(R.mipmap.volume_6)
            } else if (volume < 90) {
                imgRecording.setImageResource(R.mipmap.volume_7)
            } else {
                imgRecording.setImageResource(R.mipmap.volume_8)
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

    companion object {
        fun start(context: Context, name: String, score: String) {
            context.startActivity(Intent(context, RegisterActivity::class.java).run {
                putExtra(Constants.sp_key_recorder_name, name)
                putExtra(Constants.sp_key_recorder_score, score)
            })
        }
    }
}