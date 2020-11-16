package com.wwsl.mdsj.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.PartnerWelfareDetailBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author sushi
 * @description
 * @date 2020/7/23.
 */
public class PartnerWelfareDetailDialog extends BasePopupView {

    private List<PartnerWelfareDetailBean> detailBeans;

    public PartnerWelfareDetailDialog(@NonNull Context context, List<PartnerWelfareDetailBean> detailBeans) {
        super(context);
        this.detailBeans = detailBeans;
    }

    public PartnerWelfareDetailDialog(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartnerWelfareDetailDialog(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_partner_welfare;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.ivClose).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        PartnerWelfareDetailAdapter adapter = new PartnerWelfareDetailAdapter(detailBeans);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    static class PartnerWelfareDetailAdapter extends BaseQuickAdapter<PartnerWelfareDetailBean, BaseViewHolder> {

        public PartnerWelfareDetailAdapter(List<PartnerWelfareDetailBean> listData) {
            super(R.layout.item_partner_welfare_detail, listData);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, PartnerWelfareDetailBean partnerWelfareDetailBean) {
            baseViewHolder.setText(R.id.tvNumA, partnerWelfareDetailBean.ad)
                    .setText(R.id.tvNumB, partnerWelfareDetailBean.auth)
                    .setText(R.id.tvNumC, partnerWelfareDetailBean.shop)
                    .setText(R.id.tvNumD, partnerWelfareDetailBean.rare)
                    .setText(R.id.tvNumE, partnerWelfareDetailBean.shopping)
                    .setText(R.id.tvNumF, partnerWelfareDetailBean.live)
                    .setText(R.id.tvNumG, partnerWelfareDetailBean.video)
                    .setText(R.id.tvNumH, partnerWelfareDetailBean.fee);

        }
    }

}
