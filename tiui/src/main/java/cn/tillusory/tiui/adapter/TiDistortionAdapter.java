package cn.tillusory.tiui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.model.TiDistortion;

/**
 * Created by Anko on 2018/5/13.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiDistortionAdapter extends RecyclerView.Adapter<TiDesViewHolder> {

    private List<TiDistortion> list;

    private int selectedPosition = 0;

    private TiSDKManager tiSDKManager;

    public TiDistortionAdapter(List<TiDistortion> list, TiSDKManager tiSDKManager) {
        this.list = list;
        this.tiSDKManager = tiSDKManager;
    }

    @NonNull
    @Override
    public TiDesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_des, parent, false);
        return new TiDesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TiDesViewHolder holder, int position) {

        holder.tiTextTV.setText(list.get(position).getString(holder.itemView.getContext()));
        holder.tiImageIV.setImageDrawable(list.get(position).getImageDrawable(holder.itemView.getContext()));

        if (selectedPosition == position) {
            holder.tiTextTV.setSelected(true);
            holder.tiImageIV.setSelected(true);
        } else {
            holder.tiTextTV.setSelected(false);
            holder.tiImageIV.setSelected(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = holder.getAdapterPosition();

                tiSDKManager.setDistortionEnum(list.get(holder.getAdapterPosition()).getDistortionEnum());

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}