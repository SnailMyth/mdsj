package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.RedPackResultBean;
import com.wwsl.mdsj.glide.ImgLoader;

import java.util.List;

/**
 * Created by cxf on 2018/11/21.
 */

public class RedPackResultAdapter extends RecyclerView.Adapter<RedPackResultAdapter.Vh> {

    private List<RedPackResultBean> mList;
    private LayoutInflater mInflater;

    public RedPackResultAdapter(Context context, List<RedPackResultBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_red_pack_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mTime;
        TextView mWinCoin;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mWinCoin = (TextView) itemView.findViewById(R.id.win_coin);
        }

        void setData(RedPackResultBean bean) {
            ImgLoader.displayAvatar(bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mTime.setText(bean.getTime());
            mWinCoin.setText(bean.getWinCoin());
        }
    }
}
