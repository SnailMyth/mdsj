package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.WishBillBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.interfaces.OnWishBillItemClickListener;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.WordUtil;

public class WishBillAddAdapter extends RefreshAdapter<WishBillBean> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private View mHeaderView;
    private OnWishBillItemClickListener onWishBillItemClickListener;
    private View.OnClickListener onClickListener;

    public WishBillAddAdapter(Context context) {
        super(context);
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && onWishBillItemClickListener != null) {
                    int position = (int) tag;
                    switch (v.getId()) {
                        case R.id.layoutWishBillItem:
                            onWishBillItemClickListener.onItemClick(mList.get(position), position);
                            break;
                        case R.id.tvAvatar:
                            if (TextUtils.isEmpty(mList.get(position).getGiftid()) && TextUtils.isEmpty(mList.get(position).getId())) {
                                onWishBillItemClickListener.onAvatarClick(mList.get(position), position);
                            }
                            break;
                        case R.id.ivDelete:
                            onWishBillItemClickListener.onDeleteClick(position);
                            break;
                    }
                }
            }
        };
    }

    public void setOnWishBillItemClickListener(OnWishBillItemClickListener onWishBillItemClickListener) {
        this.onWishBillItemClickListener = onWishBillItemClickListener;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) return new Vh(mHeaderView);
        return new Vh(mInflater.inflate(R.layout.item_wish_bill_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        int realPosition = getRealPosition(vh);
        ((WishBillAddAdapter.Vh) vh).setData(mList.get(realPosition), realPosition);
    }

    private int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
//        return super.getItemCount();
        int size = mList == null ? 0 : mList.size();
        return mHeaderView == null ? size : size + 1;
    }

    class Vh extends RecyclerView.ViewHolder {
        RelativeLayout layoutWishBillItem;
        TextView tvNum;
        ImageView ivDelete;
        TextView tvAvatar;
        ImageView ivAvatar;
        TextView tvWishBillNone;
        LinearLayout layoutWishBillAdd;
        TextView tvAddName;
        TextView tvAddCount;
        LinearLayout layoutDone;
        TextView tvDoneName;
        TextView tvDoneCount;
        TextView tvProgress;

        public Vh(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) return;
            layoutWishBillItem = itemView.findViewById(R.id.layoutWishBillItem);
            tvNum = itemView.findViewById(R.id.tvNum);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvWishBillNone = itemView.findViewById(R.id.tvWishBillNone);
            layoutWishBillAdd = itemView.findViewById(R.id.layoutWishBillAdd);
            tvAddName = itemView.findViewById(R.id.tvAddName);
            tvAddCount = itemView.findViewById(R.id.tvAddCount);
            layoutDone = itemView.findViewById(R.id.layoutDone);
            tvDoneName = itemView.findViewById(R.id.tvDoneName);
            tvDoneCount = itemView.findViewById(R.id.tvDoneCount);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            layoutWishBillItem.setOnClickListener(onClickListener);
            tvAvatar.setOnClickListener(onClickListener);
            ivDelete.setOnClickListener(onClickListener);
        }

        void setData(WishBillBean bean, int position) {
            layoutWishBillItem.setTag(position);
            tvAvatar.setTag(position);
            ivDelete.setTag(position);
            tvNum.setText(WordUtil.getString(R.string.wish_add_num) + (position + 1));
            if (TextUtils.isEmpty(bean.getId())) {
                if (TextUtils.isEmpty(bean.getGiftid())) {
                    //未添加
                    tvWishBillNone.setVisibility(View.VISIBLE);
                    layoutWishBillAdd.setVisibility(View.GONE);
                    layoutDone.setVisibility(View.GONE);
                    ivAvatar.setBackgroundResource(R.drawable.bg_wish_bill_add);
                    ivAvatar.setImageResource(R.mipmap.icon_wish_bill_add);
                } else {
                    //已选取
                    tvWishBillNone.setVisibility(View.GONE);
                    layoutWishBillAdd.setVisibility(View.VISIBLE);
                    layoutDone.setVisibility(View.GONE);
                    ivAvatar.setBackgroundResource(0);
                    ImgLoader.display(bean.getGifticon(), ivAvatar);
                    tvAddName.setText(bean.getGiftname());
                    tvAddCount.setText(bean.getNum());
                }
            } else {
                //已发布
                tvWishBillNone.setVisibility(View.GONE);
                layoutWishBillAdd.setVisibility(View.GONE);
                layoutDone.setVisibility(View.VISIBLE);
                ivAvatar.setBackgroundResource(0);
                ImgLoader.display(bean.getGifticon(), ivAvatar);
                tvDoneName.setText(bean.getGiftname());
                tvDoneCount.setText(WordUtil.strToSpanned(WordUtil.strAddColor(bean.getSendnum(), "#333333") + "/" + bean.getNum()));
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvProgress.getLayoutParams();
                if (bean.getSendnum().equals("0")) {
                    params.width = 0;
                } else if (Integer.parseInt(bean.getSendnum()) >= Integer.parseInt(bean.getNum())) {
                    params.width = DpUtil.dp2px(200);
                } else {
                    params.width = DpUtil.dp2px(200) * Integer.parseInt(bean.getSendnum()) / Integer.parseInt(bean.getNum());
                }
                tvProgress.setLayoutParams(params);
            }
        }
    }
}
