package com.baker.vpr.demo

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.baker.sdk.http.CallbackListener
import com.baker.sdk.vpr.BakerVpr
import com.baker.sdk.vpr.bean.request.VprMatchMoreRequest
import com.baker.sdk.vpr.bean.request.VprMatchRequest
import com.baker.sdk.vpr.bean.request.VprRegisterRequest
import com.baker.sdk.vpr.bean.response.*
import com.baker.vpr.demo.adapters.AudioRecordVPAdapter
import com.baker.vpr.demo.audio.AudioRecorderHelper
import com.baker.vpr.demo.audio.Config
import com.baker.vpr.demo.audio.RecorderCallback
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.bean.Recorder
import com.baker.vpr.demo.comm.Constants
import com.baker.vpr.demo.databinding.ActivityRegisterBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import timber.log.Timber
import java.io.File
import java.util.*


class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {


    private lateinit var PCM_PATH: String
    lateinit var mRecordName: String
    lateinit var mRecordScore: String

    /**
     * 1:来自注册
     * 2：来自验证(1:1)
     * 3:来自验证(1：N)
     */
    var mFrom: Int = 1
    lateinit var registerid: String

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
        mFrom = intent.getIntExtra(Constants.register_get_from, 1)
        if (mFrom == 2) {
            registerid = intent.getStringExtra(Constants.sp_key_recorder_registerid)
            mBinding.tvTextPosition.text = "$mRecordName : $registerid"
        } else {
            requestRegisterId()
        }
    }


    private fun initView() {
        mBinding.run {
            val text = when (mFrom) {
                1 -> R.string.vpr_register
                2 -> R.string.vpr_match_by1
                3 -> R.string.vpr_search
                else -> R.string.vpr_search
            }
            toolbar.appToolbar.title = getString(text)
            imgRecording.visibility = View.INVISIBLE
            tvTextPosition.visibility = if (mFrom != 3) View.VISIBLE else View.GONE
            btnDeleteRegisterId.visibility = if (mFrom == 2) View.VISIBLE else View.GONE
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
            btnDeleteRegisterId.setOnClickListener {
                deleteVprRegisterId()
            }

        }
    }

    private fun deleteVprRegisterId() {
        BakerVpr.deleteVoicePrint(
            registerid,
            object : CallbackListener<DeleteVoicePrintResponse> {
                override fun onSuccess(response: DeleteVoicePrintResponse?) {
                    if (response?.err_no != 90000) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "$registerid 删除失败！",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    Toast.makeText(
                        this@RegisterActivity,
                        "$registerid 删除成功！",
                        Toast.LENGTH_SHORT
                    ).show()
                    val gson = Gson()
                    val recorderListStr =
                        sharedPreferences.getString(Constants.sp_key_recorders, "")
                    val recorderList = mutableListOf<Recorder>()
                    if (recorderListStr?.isNotEmpty() == true) {
                        val recorders = gson.fromJson<List<Recorder>>(
                            recorderListStr,
                            object : TypeToken<List<Recorder>>() {}.type
                        )
                        recorderList.addAll(recorders)
                    }
                    val list = recorderList.filter {
                        it.registerid != registerid
                    }
                    sharedPreferences.edit {
                        putString(Constants.sp_key_recorders, gson.toJson(list))
                    }

                }

                override fun onFailure(e: java.lang.Exception?) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "$registerid 删除失败！",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun recordAudio() {
        recorderHelper = AudioRecorderHelper.getInstance()
        if (recorderHelper.recordStatus) {//正在录制
            Toast.makeText(
                this@RegisterActivity, "正在提交",
                Toast.LENGTH_SHORT
            )
                .show()
            mBinding.btnAudioControl.text = getString(R.string.start_speak)
            mBinding.imgRecording.visibility = View.INVISIBLE

            recorderHelper.stopRecorder()
            mBinding.btnAudioControl.postDelayed({
                when (mFrom) {
                    1 -> uploadAudioDataforRegister()//注册
                    2 -> uploadAudioDataforMatch()//验证1:1
                    3 -> uploadAudioDataforSearch()//对比1：N
                }
            }, 100)

        } else {
            Toast.makeText(this@RegisterActivity, "开始录音", Toast.LENGTH_SHORT).show()
            mBinding.btnAudioControl.text = getString(
                if (mFrom == 1) R.string.stop_and_register else
                    R.string.stop_and_match
            )
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

    private fun uploadAudioDataforSearch() {
        val bytes = File(PCM_PATH).readBytes()
        Log.i(TAG, "uploadAudioData: File.size${bytes.size}")
        val vprMatchMoreRequest = VprMatchMoreRequest(
            format = "pcm",
            audio = bytes,
            scoreThreshold = 30.0f,
            listNum = 5
        )
        BakerVpr.vprMatchMore(
            vprMatchMoreRequest,
            object : CallbackListener<VprMatchMoreResponse> {
                override fun onSuccess(response: VprMatchMoreResponse?) {
                    if (response?.err_no != 90000) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "声音验证失败了！${response?.err_msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val list = response.matchList?.map {
                        Recorder(it?.name, it?.score.toString(), it?.spkid)
                    }
                    val jsonStr = Gson().toJson(list)
                    VprMatchActivity.start(
                        this@RegisterActivity,
                        jsonStr,
                        from = 2
                    )
                    finish()
                }

                override fun onFailure(e: Exception?) {
                    e?.printStackTrace()
                    Toast.makeText(
                        this@RegisterActivity,
                        "声音验证失败了",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun uploadAudioDataforMatch() {
        val bytes = File(PCM_PATH).readBytes()
        Log.i(TAG, "uploadAudioData: File.size${bytes.size}")
        val vprMatchRequest = VprMatchRequest(
            format = "pcm",
            audio = bytes,
            scoreThreshold = mRecordScore.toFloat(),
            matchId = registerid ?: ""
        )
        BakerVpr.vprMatchRatioOne(
            vprMatchRequest,
            object : CallbackListener<VprMatchResponse> {
                override fun onSuccess(response: VprMatchResponse?) {
                    if (response?.err_no != 90000) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "声音验证失败了！${response?.err_msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    VprMatchResultActivity.start(
                        this@RegisterActivity,
                        mRecordName,
                        registerid,
                        response.score,
                        response.matchStatus ?: 0
                    )
                    finish()
                }

                override fun onFailure(e: Exception?) {
                    e?.printStackTrace()
                    Toast.makeText(
                        this@RegisterActivity,
                        "声音验证失败了",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun uploadAudioDataforRegister() {
        val audioTexts = resources.getStringArray(R.array.register_audio_text)
        val currentItem = mBinding.vpAudioText.currentItem

        val bytes = File(PCM_PATH).readBytes()
        Log.i(TAG, "uploadAudioData: File.size${bytes.size}")
        val vprRegisterRequest = VprRegisterRequest(
            format = "pcm",
            audio = bytes,
            name = mRecordName,
            scoreThreshold = mRecordScore.toFloat(),
            registerId = registerid
        )
        BakerVpr.vprRegister(
            vprRegisterRequest,
            object : CallbackListener<VprRegisterResponse> {
                override fun onSuccess(response: VprRegisterResponse?) {
                    if (response?.err_no != 90000) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "注册失败了！${response?.err_msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    if (currentItem + 1 < audioTexts.size) {
                        Toast.makeText(this@RegisterActivity, "验证成功 请继续", Toast.LENGTH_SHORT).show()
                        mBinding.vpAudioText.currentItem = currentItem + 1
                    } else {
                        AlertDialog.Builder(this@RegisterActivity).setMessage("恭喜你注册成功！")
                            .setNegativeButton(
                                "我知道了",
                                DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                    finish()
                                })
                            .show()

                    }
                    mBinding.tvTextPosition.text =
                        "第${mBinding.vpAudioText.currentItem + 1}段 共${audioTexts.size}段"
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
//        val file = File(Environment.getExternalStorageDirectory(), "audio")
        val file = File(this.filesDir.absoluteFile,"audio")
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

                BakerVpr.createVprId(object : CallbackListener<CreateIdResponse> {
                    override fun onSuccess(response: CreateIdResponse?) {
                        if (response?.err_no == 90000) {
                            Timber.d("createVprId-${response.err_msg}\t${response.registerid}")

                            val gson = Gson()
                            val recorderListStr =
                                sharedPreferences.getString(Constants.sp_key_recorders, "")
                            Log.i(TAG, "recorderListStr:$recorderListStr ")
                            val recorderList = mutableListOf<Recorder>()
                            if (recorderListStr?.isNotEmpty() == true) {
                                val recorders = gson.fromJson<List<Recorder>>(
                                    recorderListStr,
                                    object : TypeToken<List<Recorder>>() {}.type
                                )
                                recorderList.addAll(recorders)
                            }

                            val recorder = Recorder(
                                mRecordName,
                                mRecordScore,
                                response.registerid
                            )
                            val tamp = recorderList.filter {
                                it.name == mRecordName
                            }.map {
                                it.registerid = response.registerid
                                it.score = mRecordScore
                            }
                            if (tamp.isEmpty()) {
                                recorderList.add(recorder)
                            }

                            sharedPreferences.edit {
                                val recorders = gson.toJson(recorderList)
                                putString(Constants.sp_key_recorders, recorders)
                            }
                            registerid = response.registerid ?: ""
                            sharedPreferences.edit {
                                putString(
                                    Constants.sp_key_recorder_registerid,
                                    response.registerid
                                )
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

    //    @AfterPermissionGranted(value = Constants.AudioPermissionRequestCode)
    private fun requestRuntimePermissions(block: () -> Unit) {
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
        fun start(
            context: Context,
            name: String = "",
            score: String = "",
            registerid: String = "",
            from: Int = 1
        ) {
            context.startActivity(Intent(context, RegisterActivity::class.java).run {
                putExtra(Constants.sp_key_recorder_name, name)
                putExtra(Constants.sp_key_recorder_score, score)
                putExtra(Constants.sp_key_recorder_registerid, registerid)
                putExtra(Constants.register_get_from, from)
            })
        }
    }
}