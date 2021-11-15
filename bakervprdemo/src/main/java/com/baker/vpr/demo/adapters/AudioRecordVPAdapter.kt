package com.baker.vpr.demo.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.baker.vpr.demo.fragment.AudioTextFragment
import java.util.*

/**
 * @param isSupportLeadRead 是否支持领读
 */
class AudioRecordVPAdapter(
    fragmentActivity: FragmentActivity,
    val dataList: MutableList<String?>,
//    private val leadTextListener: (text: String) -> Unit
) :FragmentStateAdapter(fragmentActivity) {


    override fun createFragment(position: Int): Fragment {
        return AudioTextFragment.newInstance(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}