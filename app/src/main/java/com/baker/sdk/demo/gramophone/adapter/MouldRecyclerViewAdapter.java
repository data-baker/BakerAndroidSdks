package com.baker.sdk.demo.gramophone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baker.engrave.lib.bean.Mould;
import com.baker.sdk.demo.R;

import java.util.List;

public class MouldRecyclerViewAdapter extends RecyclerView.Adapter<MouldRecyclerViewAdapter.MouldHolder> {
    private final List<Mould> moulds;
    private final Context mContext;
    private final RecyclerViewItemOnClickListener mListener;

    public MouldRecyclerViewAdapter(List<Mould> list, Context context, RecyclerViewItemOnClickListener listener) {
        this.moulds = list;
        mContext = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public MouldHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MouldHolder(LayoutInflater.from(mContext).inflate(R.layout.item_experience, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MouldHolder holder,  int position) {
        if (moulds != null && moulds.size() > 0) {
            final Mould mould = moulds.get(position);
            holder.tvIndex.setText((position + 1) + "");
            //TODO 具体项目中应当根据mould的status字段为准来判定mould的状态。
            //TODO 模型状态status 1=默认状态，2=录制中，3=启动训练失败，4=训练中，5=训练失败，6=训练成功。
            holder.tvStatusName.setText(mould.getStatusName());
            holder.tvMouldId.setText(mould.getModelId());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mould.getModelStatus() == 6 && mListener != null) {
                        mListener.onItemClick(position, mould.getModelId());
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return moulds == null ? 0 : moulds.size();
    }

    class MouldHolder extends RecyclerView.ViewHolder {
        private final TextView tvIndex;
        private final TextView tvStatusName;
        private final TextView tvMouldId;

        public MouldHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tv_index);
            tvStatusName = itemView.findViewById(R.id.tv_status_name);
            tvMouldId = itemView.findViewById(R.id.tv_mould_id);
        }
    }

    public interface RecyclerViewItemOnClickListener {
        void onItemClick(int index, String mouldId);
    }
}