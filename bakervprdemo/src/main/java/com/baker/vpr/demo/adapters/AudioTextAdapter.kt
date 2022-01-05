package com.baker.vpr.demo.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.baker.vpr.demo.fragment.AudioTextFragment
import java.util.*

class AudioTextAdapter(
    fragmentActivity: FragmentActivity,
    private val dataList: MutableList<String?>,
) : FragmentStateAdapter(fragmentActivity) {


    override fun createFragment(position: Int): Fragment {
        return AudioTextFragment.newInstance(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}