package com.wwsl.mdsj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.LiveWishBean;

import java.util.ArrayList;
import java.util.List;

public class LiveWishAdapter extends BaseAdapter {

    private List<LiveWishBean> list;
    private Context mContext;

    public LiveWishAdapter(Context context) {
        this.list = new ArrayList<>();
        mContext = context;
    }

    public void setList(List<LiveWishBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void updateNum(List<LiveWishBean> data) {
        for (int i = 0; i < data.size(); i++) {
            boolean include = false;
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getId().equals(data.get(i).getId())) {
                    list.get(j).setNum(data.get(i).getNum());
                    include = true;
                    break;
                }
            }
            if (!include) {
                list.add(data.get(i));
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public LiveWishBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_wish, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LiveWishBean item = getItem(position);

        Glide.with(mContext)
                .load(item.getIcon())
                .into(holder.icon);
        holder.text.setText(String.format("%s/%s", item.getNum(), item.getTotal()));
        holder.name.setText(item.getName());
        return convertView;
    }

    private static class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView text;

        public ViewHolder(View view) {
            this.icon = view.findViewById(R.id.iconGift);
            this.name = view.findViewById(R.id.giftName);
            this.text = view.findViewById(R.id.giftNum);
        }
    }
}

