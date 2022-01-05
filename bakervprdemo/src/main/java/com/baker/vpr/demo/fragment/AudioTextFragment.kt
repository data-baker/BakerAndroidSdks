package com.baker.vpr.demo.fragment

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.baker.vpr.demo.base.BaseFragment
import com.baker.vpr.demo.databinding.FragmentAudioTextBinding
import timber.log.Timber

/**
 * 语音采集，显示文本 Fragment
 */
class AudioTextFragment :
    BaseFragment<FragmentAudioTextBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated")
        val arguments = arguments
        val audioText: String?
        if (arguments != null) {
            audioText = arguments.getString(AUDIO_TEXT)
            getBinding().run {
                tvAudio.text = audioText
                tvAudio.movementMethod = ScrollingMovementMethod.getInstance();
            }
        }
    }

    companion object {
        const val AUDIO_TEXT = "AUDIO_TEXT"

        @JvmStatic
        fun newInstance(
            text: String?,
        ): AudioTextFragment {
            val bundle = Bundle()
            bundle.putString(AUDIO_TEXT, text ?: "")
            val audioTextFragment = AudioTextFragment()
            audioTextFragment.arguments = bundle
            return audioTextFragment
        }
    }
}