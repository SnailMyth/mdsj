package com.wwsl.mdsj.activity.maodou;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.frame.fire.util.LogUtils;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.bean.net.MdOrderShowBean;
import com.wwsl.mdsj.bean.net.NetMdOrderBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.upload.PictureUploadCallback;
import com.wwsl.mdsj.upload.PictureUploadQnImpl;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.FileUriHelper;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;


public class MdOrderSubFragment extends BaseFragment implements SwipeRecyclerView.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = "MdOrderSubFragment";
    private SwipeRecyclerView recyclerView;
    private SwipeRefreshLayout freshLayout;
    private MdOrderAdapter adapter;
    private int subType;
    private int mType;
    private List<MdOrderShowBean> data;
    private int mPage = 1;

    private PictureUploadQnImpl mUploadStrategy;
    private FileUriHelper fileUriHelper;

    private String curOrderId;

    public MdOrderSubFragment() {

    }

    public static MdOrderSubFragment newInstance(int type, int subType) {
        MdOrderSubFragment fragment = new MdOrderSubFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("subType", subType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_md_order_sub_list;
    }

    @Override
    protected void init() {

        if (getArguments() != null) {
            subType = getArguments().getInt("subType");
            mType = getArguments().getInt("type");
        }

        fileUriHelper = new FileUriHelper(getContext());

        mUploadStrategy = new PictureUploadQnImpl(AppConfig.getInstance().getConfig());

        initView();

        initListener();

    }

    private void initListener() {
        this.openAlbumResultListener = (requestCode, result) -> {
            if (requestCode == REQUEST_UPLOAD_IMG) {
                if (result != null && result.size() > 0) {
                    String path = fileUriHelper.getFilePathByUri(Uri.parse(result.get(0).getPath()));
                    List<File> files = new ArrayList<>();
                    files.add(new File(path));
                    mUploadStrategy.upload(files, new PictureUploadCallback() {
                        @Override
                        public void onSuccess(String url) {
                            HttpUtil.orderUploadCertificate(curOrderId, path, new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    if (code == 200) {
                                        LogUtils.e(TAG, "onSuccess: 上传凭证");
                                        initialData();
                                    }
                                    ToastUtil.show(msg);
                                    curOrderId = null;
                                }

                                @Override
                                public void onError() {
                                    super.onError();
                                    curOrderId = null;
                                }
                            });
                        }

                        @Override
                        public void onFailure() {
                            ToastUtil.show("图片上传失败,请稍后重试!");
                        }
                    });
                }
            }
        };
    }

    private void initView() {
        recyclerView = (SwipeRecyclerView) findViewById(R.id.listRecycler);
        freshLayout = (SwipeRefreshLayout) findViewById(R.id.freshLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        freshLayout.setOnRefreshListener(this);
        data = new ArrayList<>();
        adapter = new MdOrderAdapter(data);

        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                switch (view.getId()) {
                    case R.id.btnAction1:
                        clickAction1(position);
                        break;
                    case R.id.btnAction2:
                        clickAction2(position);
                        break;
                    case R.id.btnAction3:
                        clickAction3(position);
                        break;
                }
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.view_no_data_default);
        recyclerView.useDefaultLoadMore();
        recyclerView.setLoadMoreListener(this);
    }

    private void clickAction1(int position) {
        if (mType == MdOrderFragment.TYPE_SALE) {
            //卖单
            if (subType == ORDER_PROCESSING) {
                //取消交易
                DialogUtil.showSimpleDialog(getContext(), "提示", "是否取消本次交易?", true, new DialogUtil.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        HttpUtil.orderSellerUndo(data.get(position).getId(), new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                ToastUtil.show(msg);
                                if (code == 200) {
                                    initialData();
                                }
                            }
                        });
                    }
                });
            } else {
                //查看凭证
                MdOrderShowBean mdOrderShowBean = data.get(position);
                if (!StrUtil.isEmpty(mdOrderShowBean.getPayImgUrl())) {
                    DialogUtil.getSimpleCenterDialog(mContext, mdOrderShowBean.getPayImgUrl(), 200, 200, 20, 0, 0, 0, "交易凭证").show();
                } else {
                    ToastUtil.show("暂无凭证");
                }
            }
        } else {
            if (subType == ORDER_PROCESSING || subType == ORDER_CONFIRM) {
                //上传凭证-重新上传凭证
                curOrderId = data.get(position).getId();
                openAlbum(1, new ArrayList<>(), REQUEST_UPLOAD_IMG);
            } else {
                //查看凭证
                MdOrderShowBean mdOrderShowBean = data.get(position);
                DialogUtil.getSimpleCenterDialog(mContext, mdOrderShowBean.getPayImgUrl(), 200, 200, 20, 0, 0, 0, "交易凭证").show();
            }
        }
    }

    private void clickAction2(int position) {
        if (mType == MdOrderFragment.TYPE_SALE) {
            if (subType == ORDER_CONFIRM) {
                MdOrderShowBean bean = data.get(position);
                WebViewActivity.forward(getContext(), String.format("%s&uid=%s&token=%s&orderid=", HtmlConfig.WEB_LINK_MD_REPORT_BASE, AppConfig.getInstance().getUid(), AppConfig.getInstance().getToken(), bean.getOrderNum()));
            }
        } else {
            if (subType == ORDER_PROCESSING || subType == ORDER_CONFIRM) {
                //查看二维码收款
                MdOrderShowBean mdOrderShowBean = data.get(position);
                DialogUtil.getSimpleCenterDialog(mContext, mdOrderShowBean.getQrUrl(), 0, 200, 20, 0, 0, 0, "收款二维码").show();
            } else {
                DialogUtil.showContentTipsDialog(getContext(), "提示", "本次交易已完成!", (dialog, content) -> dialog.dismiss());
            }
        }
    }

    private void clickAction3(int position) {
        if (mType == MdOrderFragment.TYPE_SALE) {
            if (subType == ORDER_CONFIRM) {
                //确认收款
                DialogUtil.showSimpleDialog(getContext(), "提示", "您确定您已经收到买家打款?", true, new DialogUtil.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        HttpUtil.orderConfirm(data.get(position).getId(), new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 200) {
                                    initialData();
                                }
                                ToastUtil.show(msg);
                            }
                        });
                    }
                });
            }
        } else {
            if (subType == ORDER_PROCESSING) {
                //买家撤单
                HttpUtil.orderBuyerUndo(data.get(position).getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 200) {
                            initialData();
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        }
    }

    @Override
    protected void initialData() {
        mPage = 1;
        if (mType == MdOrderFragment.TYPE_SALE) {
            HttpUtil.getMdSaleList(mPage, subType, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, true);
                }

                @Override
                public void onError() {
                    super.onError();
                    freshLayout.setRefreshing(false);
                }
            });
        } else {
            HttpUtil.getMdOrderList(mPage, subType, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, true);
                }

                @Override
                public void onError() {
                    super.onError();
                    freshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void loadBack(int code, String msg, String[] info, boolean isFresh) {
        freshLayout.setRefreshing(false);
        if (code == 200) {
            List<NetMdOrderBean> netBeans = JSON.parseArray(Arrays.toString(info), NetMdOrderBean.class);
            List<MdOrderShowBean> parseBeans = MdOrderShowBean.parse(netBeans, mType, subType);
            if (isFresh) {
                data.clear();
                data.addAll(parseBeans);
                adapter.setNewInstance(parseBeans);
            } else {
                data.addAll(parseBeans);
                adapter.addData(parseBeans);
            }
            recyclerView.loadMoreFinish(parseBeans.size() == 0, parseBeans.size() == HttpConst.ITEM_COUNT);
        } else if (info == null) {
            if (isFresh) {
                data.clear();
                adapter.setNewInstance(new ArrayList<>());
            }
            recyclerView.loadMoreFinish(true, false);
        } else {
            recyclerView.loadMoreFinish(true, false);
            ToastUtil.show(msg);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onLoadMore() {
        mPage++;
        if (mType == MdOrderFragment.TYPE_SALE) {
            HttpUtil.getMdSaleList(mPage, subType, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, true);
                }

                @Override
                public void onError() {
                    super.onError();
                    freshLayout.setRefreshing(false);
                }
            });
        } else {
            HttpUtil.getMdOrderList(mPage, subType, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, true);
                }

                @Override
                public void onError() {
                    super.onError();
                    freshLayout.setRefreshing(false);
                }
            });
        }
    }

    static final int ORDER_PROCESSING = 1;
    static final int ORDER_CONFIRM = 2;
    static final int ORDER_FINISH = 3;


    static final int REQUEST_UPLOAD_IMG = 1001;

    @Override
    public void onRefresh() {
        initialData();
    }
}
