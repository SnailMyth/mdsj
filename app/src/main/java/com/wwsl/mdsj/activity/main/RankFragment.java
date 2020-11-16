package com.wwsl.mdsj.activity.main;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.RankAdapter;
import com.wwsl.mdsj.bean.ListBean;
import com.wwsl.mdsj.custom.MyLinearLayout1;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author :
 * @date : 2020/5/30 15:23
 * @description : 排行榜列表界面
 */
public class RankFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeRecyclerView.LoadMoreListener, View.OnClickListener {

    private Context mContext;
    private SwipeRecyclerView recycler;
    private MyLinearLayout1 tabLayout;

    private RankAdapter dataAdapter;

    private List<List<ListBean>> allData;
    private String prefix = "";

    private int type;
    private String subType = RANK_SUB_DAY;
    private int subTypeInt = SUB_DAY;
    private int[] mPage = new int[]{1, 1, 1, 1};
    private SwipeRefreshLayout refreshLayout;
    private ImageView imgTop1Frame;
    private RoundedImageView top1Avatar;
    private TextView txTop1Name;
    private TextView txTop1Num;
    private ConstraintLayout top1Layout;

    public static RankFragment getInstance(int type) {
        RankFragment fragment = new RankFragment();
        fragment.setType(type);
        return fragment;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rank_list, null);
        initView(root);
        return root;
    }

    private void initView(View root) {
        recycler = root.findViewById(R.id.dateRecycler);
        tabLayout = root.findViewById(R.id.radio_group_wrap);
        imgTop1Frame = root.findViewById(R.id.imgTop1Frame);
        top1Avatar = root.findViewById(R.id.top1Avatar);
        txTop1Name = root.findViewById(R.id.txTop1Name);
        txTop1Num = root.findViewById(R.id.txTop1Num);
        top1Layout = root.findViewById(R.id.top1Layout);


        switch (type) {
            case RANK_STAR:
                imgTop1Frame.setImageResource(R.mipmap.icon_rank_top1_frame_star);
                prefix = "礼物:";
                break;
            case RANK_CONTRIBUTE:
                imgTop1Frame.setImageResource(R.mipmap.icon_rank_top1_frame_contribute);
                prefix = "礼物:";
                break;
            case RANK_MAGNATE:
                imgTop1Frame.setImageResource(R.mipmap.icon_rank_top1_frame_magnate);
                prefix = "礼物:";
                break;
            case RANK_GAMBLER:
                imgTop1Frame.setImageResource(R.mipmap.icon_rank_top1_frame_gambler);
                prefix = "礼物:";
                break;
        }

        tabLayout.setVisibility(RANK_MAGNATE == type ? View.GONE : View.VISIBLE);

        allData = new ArrayList<>();
        allData.add(new ArrayList<>());
        allData.add(new ArrayList<>());
        allData.add(new ArrayList<>());
        allData.add(new ArrayList<>());

        dataAdapter = new RankAdapter(new ArrayList<>());
        recycler.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        refreshLayout = root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        recycler.useDefaultLoadMore();
        recycler.setLoadMoreListener(this);
        dataAdapter.setEmptyView(R.layout.view_no_data_list);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(dataAdapter);

        root.findViewById(R.id.btn_day).setOnClickListener(this);
        root.findViewById(R.id.btn_week).setOnClickListener(this);
        root.findViewById(R.id.btn_month).setOnClickListener(this);
        root.findViewById(R.id.btn_all).setOnClickListener(this);


        imgTop1Frame.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (allData.get(subTypeInt).size() == 0) {
            loadData();
        }
    }

    public void loadData() {
        refreshLayout.setRefreshing(false);
        recycler.loadMoreFinish(false, true);
        switch (type) {
            case RANK_STAR:
                loadStarData();
                break;
            case RANK_CONTRIBUTE:
                loadContributeData();
                break;
            case RANK_MAGNATE:
                loadMagnateData();
                break;
            case RANK_GAMBLER:
                loadGamblerData();
                break;
        }
    }

    private void loadGamblerData() {
        HttpUtil.getGamblerList(subType, mPage[subTypeInt], new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadComplete(code, msg, info, subTypeInt);
            }
        });
    }

    private void loadMagnateData() {
        HttpUtil.getMagnateList(subType, mPage[subTypeInt], new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadComplete(code, msg, info, subTypeInt);
            }
        });
    }

    public static final int RANK_STAR = 0;
    public static final int RANK_CONTRIBUTE = 1;
    public static final int RANK_MAGNATE = 2;
    public static final int RANK_GAMBLER = 3;

    public static final String RANK_SUB_DAY = "day";
    public static final String RANK_SUB_WEEK = "week";
    public static final String RANK_SUB_MONTH = "month";
    public static final String RANK_SUB_ALL = "total";

    public static final int SUB_DAY = 0;
    public static final int SUB_WEEK = 1;
    public static final int SUB_MONTH = 2;
    public static final int SUB_ALL = 3;


    @Override
    public void onRefresh() {
        mPage[subTypeInt] = 1;
        allData.get(subTypeInt).clear();
        recycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        mPage[subTypeInt]++;
        recycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 1000);
    }


    public void loadStarData() {
        HttpUtil.consumeList(subType, mPage[subTypeInt], new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadComplete(code, msg, info, subTypeInt);
            }
        });
    }

    public void loadContributeData() {
        HttpUtil.profitList(subType, mPage[subTypeInt], new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadComplete(code, msg, info, subTypeInt);
            }
        });
    }

    private void loadComplete(int code, String msg, String[] info, int i) {

        if (code != 0) {
            ToastUtil.show(msg);
            mPage[i]--;
            return;
        }

        if (info != null) {
            List<ListBean> list = JSON.parseArray(Arrays.toString(info), ListBean.class);
            if (list == null) {
                return;
            }
            if (list.size() > 0) {
                for (int j = 0; j <list.size() ; j++) {
                    list.get(j).setType(type);
                }

                if (dataAdapter.getData().size() == 0) {
                    ListBean top1 = (ListBean) list.get(0);
                    txTop1Name.setText(top1.getUserNiceName());
                    txTop1Num.setText(String.format("%s%s", prefix, top1.getTotalCoin()));
                    list.remove(0);
                }
                dataAdapter.addData(list);
            } else {
                ToastUtil.show(WordUtil.getString(R.string.no_more_data));
                mPage[i]--;
            }
        } else {
            ToastUtil.show(WordUtil.getString(R.string.no_more_data));
            mPage[i]--;
        }

        top1Layout.setVisibility(dataAdapter.getData().size() == 0 ? View.GONE : View.VISIBLE);
    }

    public void changeData(int subType) {
        if (subTypeInt == subType) return;
        subTypeInt = subType;
        switch (subType) {
            case SUB_DAY:
                this.subType = RANK_SUB_DAY;
                break;
            case SUB_WEEK:
                this.subType = RANK_SUB_WEEK;
                break;
            case SUB_MONTH:
                this.subType = RANK_SUB_MONTH;
                break;
            case SUB_ALL:
                this.subType = RANK_SUB_ALL;
                break;
        }
        dataAdapter.setNewData(allData.get(subTypeInt));
        if (allData.get(subTypeInt).size() == 0) {
            loadData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_day:
                changeData(SUB_DAY);
                break;
            case R.id.btn_week:
                changeData(SUB_WEEK);
                break;
            case R.id.btn_month:
                changeData(SUB_MONTH);
                break;
            case R.id.btn_all:
                changeData(SUB_ALL);
                break;
        }
    }
}
