package com.baker.vpr.demo.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baker.vpr.demo.R
import com.baker.vpr.demo.RegisterActivity
import com.baker.vpr.demo.VprMatchActivity
import com.baker.vpr.demo.bean.Recorder

/**
 *
 *@author xujian
 *@date 2021/11/15
 */
class RegisterIdListAdapter() :
    RecyclerView.Adapter<RegisterIdListAdapter.ViewHolder>() {
    private var dataList: ArrayList<Recorder> = arrayListOf()

    public fun setData(dataList: List<Recorder>) {
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.textView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.vpr_registerid_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val recorder = dataList[position]

        viewHolder.textView.run {
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
