package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.LevelBean;
import com.wwsl.mdsj.bean.LiveUserGiftBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveUserAdapter extends RecyclerView.Adapter<LiveUserAdapter.Vh> {

    private List<LiveUserGiftBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<UserBean> mOnItemClickListener;

    public LiveUserAdapter(Context context) {
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<UserBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mWrap;
        ImageView mAvatar;
        ImageView mIcon;
        ImageView mGuardIcon;

        public Vh(View itemView) {
            super(itemView);
            mWrap = (ImageView) itemView.findViewById(R.id.wrap);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mGuardIcon = (ImageView) itemView.findViewById(R.id.guard_icon);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveUserGiftBean userBean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                ImgLoader.displayAvatar(userBean.getAvatar(), mAvatar);
                LevelBean levelBean = AppConfig.getInstance().getLevel(userBean.getLevel());
                if (levelBean != null) {
                    ImgLoader.display(levelBean.getThumbIcon(), mIcon);
                }
            }
            int guardType = userBean.getGuardType();
            if (guardType == Constants.GUARD_TYPE_NONE) {
                if (mIcon.getVisibility() != View.VISIBLE) {
                    mIcon.setVisibility(View.VISIBLE);
                }
                if (mGuardIcon.getVisibility() == View.VISIBLE) {
                    mGuardIcon.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mIcon.getVisibility() == View.VISIBLE) {
                    mIcon.setVisibility(View.INVISIBLE);
                }
                if (mGuardIcon.getVisibility() != View.VISIBLE) {
                    mGuardIcon.setVisibility(View.VISIBLE);
                }
                if (guardType == Constants.GUARD_TYPE_MONTH) {
                    ImgLoader.display(R.mipmap.icon_guard_type_0, mGuardIcon);
                } else if (guardType == Constants.GUARD_TYPE_YEAR) {
                    ImgLoader.display(R.mipmap.icon_guard_type_1, mGuardIcon);
                }
            }
            if (position == 0) {
                if (userBean.hasContribution()) {
                    mWrap.setImageResource(R.mipmap.icon_live_user_list_1);
                }
            } else if (position == 1) {
                if (userBean.hasContribution()) {
                    mWrap.setImageResource(R.mipmap.icon_live_user_list_2);
                }
            } else if (position == 2) {
                if (userBean.hasContribution()) {
                    mWrap.setImageResource(R.mipmap.icon_live_user_list_3);
                }
            } else {
                mWrap.setImageDrawable(null);
            }
        }

    }

    public void refreshList(List<LiveUserGiftBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    private int findItemPosition(String uid) {
        if (!TextUtils.isEmpty(uid)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                if (uid.equals(mList.get(i).getId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void removeItem(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        int position = findItemPosition(uid);
        if (position >= 0) {
            mList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);
        }
    }

    public void insertItem(LiveUserGiftBean userBean) {
        if (userBean == null) {
            return;
        }
        int position = findItemPosition(userBean.getId());
        if (position >= 0) {
            return;
        }
        int size = mList.size();
        mList.add(userBean);
        notifyItemInserted(size);
    }

    public void insertList(List<LiveUserGiftBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            int position = mList.size();
            mList.addAll(list);
            notifyItemRangeInserted(position, mList.size());
        }
    }

    /**
     * 守护信息发生变化
     */
    public void onGuardChanged(String uid, int guardType) {
        if (!TextUtils.isEmpty(uid)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                LiveUserGiftBean bean = mList.get(i);
                if (uid.equals(bean.getId())) {
                    if (bean.getGuardType() != guardType) {
                        bean.setGuardType(guardType);
                        notifyItemChanged(i, Constants.PAYLOAD);
                    }
                    break;
                }
            }
        }
    }

    public void clear(){
        if(mList!=null){
            mList.clear();
        }
        notifyDataSetChanged();
    }
}
