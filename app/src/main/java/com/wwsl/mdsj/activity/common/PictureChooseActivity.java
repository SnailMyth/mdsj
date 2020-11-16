package com.wwsl.mdsj.activity.common;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.PictureChooseAdapter;
import com.wwsl.mdsj.bean.PictureChooseBean;
import com.wwsl.mdsj.custom.ItemDecoration;
import com.wwsl.mdsj.interfaces.OnPictureChooseItemClickListener;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class PictureChooseActivity extends AbsActivity {
    public static final String PICTURE_CHOOSE_NUM = "PICTURE_CHOOSE_NUM";
    private int mMaxChooseNum;
    private ArrayList<PictureChooseBean> mChooseList = new ArrayList<>();

    private TextView tvOther;
    private TextView tvPictureChooseConfirm;
    private RecyclerView mRecyclerView;
    private View mNoData;

    private List<PictureChooseBean> filePath = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_picture_choose;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return false;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.picture_local));

        mMaxChooseNum = getIntent().getIntExtra(PICTURE_CHOOSE_NUM, 0);

        tvPictureChooseConfirm = findViewById(R.id.tvPictureChooseConfirm);
        tvOther = findViewById(R.id.tvOther);
        tvOther.setVisibility(View.VISIBLE);
        mNoData = findViewById(R.id.no_data);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 3, 3);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);

        initListeners();

        serchPhoto();
    }

    /**
     * 查询系统所有图片地址       
     */
    private void serchPhoto() {
        filePath.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?"
                                + " or " + MediaStore.Images.Media.MIME_TYPE
                                + "=?", new String[]{"image/jpeg",
                                "image/png", "image/jpg"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                Log.e("TAG", mCursor.getCount() + "===" + filePath.toString());
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    PictureChooseBean bean = new PictureChooseBean();
                    bean.setPath(path);
                    filePath.add(bean);
                }
                mCursor.close();
                Message msg = Message.obtain();
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<PictureChooseActivity> mActivity;

        public InnerHandler(PictureChooseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PictureChooseActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.arg1 == 1) {
                    activity.showPictures();
                }
            }
        }
    }

    private InnerHandler handler = new InnerHandler(this);

    private void showPictures() {
        if (filePath.size() > 0) {
            final PictureChooseAdapter adapter = new PictureChooseAdapter(mContext, filePath);
            mRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnPictureChooseItemClickListener<PictureChooseBean>() {
                @Override
                public void onItemClick(PictureChooseBean bean, int position) {
//                    ImagePreview.getInstance().setContext(mContext).setImage(bean.getPath()).setShowDownButton(false).start();
                }

                @Override
                public void onItemSelect(PictureChooseBean bean, int position) {
                    if (bean.getNum() == 0) {
                        if (mChooseList.size() < mMaxChooseNum) {
                            bean.setNum(mChooseList.size() + 1);
                            mChooseList.add(bean);
                            adapter.notifyDataSetChanged();
                            tvPictureChooseConfirm.setText(WordUtil.getString(R.string.picture_choose_confirm) + "(" + mChooseList.size() + ")");
                            tvPictureChooseConfirm.setEnabled(true);
                        } else {
                            ToastUtil.show("最多选择" + mMaxChooseNum + "张图片");
                        }
                    } else {
                        bean.setNum(0);
                        mChooseList.remove(bean);
                        for (int i = 0; i < mChooseList.size(); i++) {
                            mChooseList.get(i).setNum(i + 1);
                        }
                        adapter.notifyDataSetChanged();
                        tvPictureChooseConfirm.setText(WordUtil.getString(R.string.picture_choose_confirm) + "(" + mChooseList.size() + ")");
                        if (mChooseList.size() == 0) {
                            tvPictureChooseConfirm.setEnabled(false);
                        }
                    }
                }

                @Override
                public void onItemDelete(PictureChooseBean bean, int position) {

                }
            });
        } else {
            mNoData.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        tvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvPictureChooseConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(Constants.PICTURE_LIST, mChooseList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
