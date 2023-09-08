package com.baker.sdk.demo.gramophone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.engrave.lib.bean.Mould;
import com.baker.engrave.lib.callback.MouldCallback;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.gramophone.activity.ExperienceActivity;
import com.baker.sdk.demo.gramophone.adapter.MouldRecyclerViewAdapter;
import com.baker.sdk.demo.gramophone.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class ExperienceFragment extends BaseFragment implements MouldRecyclerViewAdapter.RecyclerViewItemOnClickListener{
    private RecyclerView recyclerView;
    private TextView tvNullTip;
    private MouldRecyclerViewAdapter mAdapter;
    private final List<Mould> mouldList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_experience, null);
        recyclerView = view.findViewById(R.id.recycler_view);
        tvNullTip = view.findViewById(R.id.tv_experience_null);
        tvNullTip.setOnClickListener(view -> BakerVoiceEngraver.getInstance().getMouldList(1, 50, SharedPreferencesUtil.getQueryId(requireContext())));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BakerVoiceEngraver.getInstance().setMouldCallback(mouldCallback);
        mAdapter = new MouldRecyclerViewAdapter(mouldList, getActivity(), this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.addItemDecoration(new RecycleGridDivider(20));
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        BakerVoiceEngraver.getInstance().getMouldList(1, 50, SharedPreferencesUtil.getQueryId(requireContext()));
    }

    private final MouldCallback mouldCallback = new MouldCallback() {
        @Override
        public void onMouldError(int errorCode, String message) {
            Log.e("ExperienceFragment", "errorCode==" + errorCode + ", message=" + message);
            try {
                requireActivity().runOnUiThread(() -> {
                    tvNullTip.setVisibility(View.VISIBLE);
                    tvNullTip.setText("网络请求出错啦\n请点我刷新重试");
                    recyclerView.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 这个方法可自行决定是否重写
         * @param list
         */
        @Override
        public void mouldList(final List<Mould> list) {
            requireActivity().runOnUiThread(() -> {
                if (list != null && list.size() > 0) {
                    mouldList.clear();
                    mouldList.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    tvNullTip.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    tvNullTip.setVisibility(View.VISIBLE);
                    tvNullTip.setText("试听体验\n当前无声音模型。");
                    recyclerView.setVisibility(View.GONE);
                }
            });
        }
    };

    @Override
    public void onItemClick(int index, String mouldId) {
        Intent intent = new Intent(getActivity(), ExperienceActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("mouldId", mouldId);
        startActivity(intent);
    }
}
