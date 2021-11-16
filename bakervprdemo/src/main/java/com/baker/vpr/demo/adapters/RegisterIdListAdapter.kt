package com.baker.vpr.demo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baker.vpr.demo.R
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
        viewHolder.textView.text = "${recorder.name}:${recorder.registerid}"
    }

    override fun getItemCount() = dataList.size

}
