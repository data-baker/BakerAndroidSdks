package com.baker.vpr.demo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baker.vpr.demo.R
import com.baker.vpr.demo.RegisterActivity
import com.baker.vpr.demo.VprMatchActivity
import com.baker.vpr.demo.bean.Recorder
import com.baker.vpr.demo.databinding.VprRegisteridItemBinding

/**
 *
 *@author xujian
 *@date 2021/11/15
 */
class RegisterIdListAdapter :
    RecyclerView.Adapter<RegisterIdListAdapter.MyViewHolder>() {
    private var dataList: ArrayList<Recorder> = arrayListOf()

    fun setData(dataList: List<Recorder>) {
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    class MyViewHolder(val mBinding: VprRegisteridItemBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val binding=VprRegisteridItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        val recorder = dataList[position]

        viewHolder.mBinding.textView.run {
            text = "姓名:${recorder.name}\n分数:${recorder.score}\n声纹:${recorder.registerid}"
            setOnClickListener {

                RegisterActivity.start(
                    it.context,
                    recorder.name ?: "",
                    recorder.score ?: "",
                    recorder.registerid ?: "",
                    2
                )
                (it.context as VprMatchActivity).finish()
            }
        }
    }

    override fun getItemCount() = dataList.size

}
