package com.wwsl.mdsj.activity.me;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.bean.PartnerCityBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.im.ImMessageUtil;
import com.wwsl.mdsj.interfaces.ActivityResultCallback;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.interfaces.ImageResultCallback;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.ProcessImageUtil;
import com.wwsl.mdsj.utils.SpUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.dialog.address.ChooseAddressWheel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.hutool.core.date.DateUtil;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by cxf on 2018/9/29.
 * 个人资料
 */
public class UserInfoEditActivity extends AbsActivity {
    private static final int TYPE_AVATAR = 0;
    private static final int TYPE_COVER = 1;
    private int mType = TYPE_AVATAR;
    private ImageView ivCover;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mSign;
    private TextView txAccount;
    private TextView mBirthday;
    private TextView mSex;
    private TextView tvCity;
    private ProcessImageUtil mImageUtil;
    private UserBean mUserBean;
    private LinearLayout rootLayout;
    private ChooseAddressWheel chooseAddressWheel;
    private List<PartnerCityBean> cityBeans;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.edit_profile));
        ivCover = findViewById(R.id.ivCover);
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mSign = findViewById(R.id.sign);
        txAccount = findViewById(R.id.txAccount);
        mBirthday = findViewById(R.id.birthday);
        mSex = findViewById(R.id.sex);
        tvCity = findViewById(R.id.txMainCity);
        rootLayout = findViewById(R.id.rootLayout);
        cityBeans = new ArrayList<>();

        initDialog();

        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    if (mType == TYPE_AVATAR) {
                        ImgLoader.displayAvatar(file, mAvatar);
                        HttpUtil.updateAvatar(file, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0 && info.length > 0) {
                                    ImMessageUtil.getInstance().updateUserAvatar(file, new BasicCallback() {
                                        @Override
                                        public void gotResult(int i, String s) {
                                            if (i == ImMessageUtil.RESULT_SUCCESS) {
                                                ToastUtil.show(R.string.edit_profile_update_avatar_success);
                                                UserBean bean = AppConfig.getInstance().getUserBean();
                                                if (bean != null) {
                                                    JSONObject obj = JSON.parseObject(info[0]);
                                                    bean.setAvatar(obj.getString("avatar"));
                                                    bean.setAvatarThumb(obj.getString("avatarThumb"));
                                                }
                                            } else {
                                                ToastUtil.show("更新失败");
                                            }
                                        }
                                    });
                                } else {
                                    ToastUtil.show(msg);
                                }
                            }
                        });
                    } else {
                        ImgLoader.display(file, ivCover);
                        HttpUtil.updateCover(file, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0 && info.length > 0) {
                                    ToastUtil.show(R.string.edit_profile_update_cover_success);
                                    UserBean bean = AppConfig.getInstance().getUserBean();
                                    if (bean != null) {
                                        JSONObject obj = JSON.parseObject(info[0]);
                                        bean.setLiveThumb(obj.getString("live_thumb"));
                                    }
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure() {

            }
        });
        mUserBean = AppConfig.getInstance().getUserBean();
        if (mUserBean != null) {
            showData(mUserBean);
        } else {
            HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean u) {
                    mUserBean = u;
                    showData(u);
                }
            });
        }
    }


    private void initDialog() {
        cityBeans = AppConfig.getInstance().getCityBeans();

        if (cityBeans == null || cityBeans.size() == 0) {
            cityBeans = new ArrayList<>();
            HttpUtil.getCityConfig("1", "1", new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        List<PartnerCityBean> tempData = JSON.parseArray(Arrays.toString(info), PartnerCityBean.class);
                        SpUtil.getInstance().setStringValue(SpUtil.CITY, JSON.toJSONString(cityBeans));
                        cityBeans.clear();
                        cityBeans.addAll(tempData);
                        AppConfig.getInstance().setCityBeans(cityBeans);
                        initAddressWheel();
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        } else {
            initAddressWheel();
        }
    }

    private void initAddressWheel() {
        chooseAddressWheel = DialogUtil.getCityDetailDialog((Activity) mContext, cityBeans, (province, city, district, id) -> {
            StringBuilder content = new StringBuilder();
            if (!StringUtil.isEmpty(province)) {
                content.append(province);
            }

            if (!StringUtil.isEmpty(city)) {
                content.append("-").append(city);
            }

            if (!StringUtil.isEmpty(district)) {
                content.append("-").append(district);
            }

            showLoad();
            HttpUtil.updateCity(content.toString(), new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            ToastUtil.show(obj.getString("msg"));
                            UserBean u = AppConfig.getInstance().getUserBean();
                            if (u != null) {
                                u.setCity(content.toString());
                            }
                            dismissLoad();
                            tvCity.setText(content.toString());
                        }
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        });
    }

    public void editProfileClick(View v) {
        if (!canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_avatar:
                mType = TYPE_AVATAR;
                editAvatar();
                break;
            case R.id.layoutCover:
                mType = TYPE_COVER;
                editAvatar();
                break;
            case R.id.btn_name:
                forwardName();
                break;
            case R.id.btn_sign:
                forwardSign();
                break;
            case R.id.btn_birthday:
                editBirthday();
                break;
            case R.id.btn_sex:
                forwardSex();
                break;
            case R.id.btn_impression:
                forwardImpress();
                break;
            case R.id.layoutCity:
                if (chooseAddressWheel != null) {
                    chooseAddressWheel.show(rootLayout);
                }
                break;
        }
    }

    private void editAvatar() {
        DialogUtil.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUtil.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }

    private void forwardName() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditNameActivity.class);
        intent.putExtra(Constants.NICK_NAME, mUserBean.getUsername());
        mImageUtil.startActivityForResult(intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String name = intent.getStringExtra(Constants.NICK_NAME);
                    mUserBean.setUsername(name);
                    mName.setText(name);
                }
            }
        });
    }


    private void forwardSign() {
        if (mUserBean == null) {
            return;
        }

        Intent intent = new Intent(mContext, EditSignActivity.class);
        intent.putExtra(Constants.SIGN, mUserBean.getSignature());
        mImageUtil.startActivityForResult(intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String sign = intent.getStringExtra(Constants.SIGN);
                    mUserBean.setSignature(sign);
                    mSign.setText(sign);
                }
            }

        });

    }

    private void editBirthday() {
        if (mUserBean == null) {
            return;
        }

        TimePickerView pickerView = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

                if (DateUtil.compare(date, new Date()) >= 0) {
                    ToastUtil.show("请选择正确的日期");
                    return;
                }

                Calendar chose = Calendar.getInstance();
                chose.setTime(date);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());

                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.CHINA);

                String time = format2.format(chose.getTime());
                String time2 = format2.format(cal.getTime());
                System.out.println("chose 完整的时间和日期： " + time);
                System.out.println("cal 完整的时间和日期： " + time2);


                if (chose.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                        chose.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                        chose.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
                    ToastUtil.show("请选择正确的日期");
                    return;
                }

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String mDate = format.format(chose.getTime());
                HttpUtil.updateFields("{\"birthday\":\"" + mDate + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (info.length > 0) {
                                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                                mUserBean.setBirthday(mDate);
                                mBirthday.setText(mDate);
                                AppConfig.getInstance().getUserBean().setAge(String.valueOf(DateUtil.ageOfNow(date)));
                            }
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("出生日期")//标题文字
                .setTitleColor(Color.WHITE)
                .setOutSideCancelable(true)
                .setSubmitColor(Color.parseColor("#F95921"))//确定按钮文字颜色
                .setCancelColor(Color.WHITE)//取消按钮文字颜色
                .setBgColor(Color.parseColor("#1E222D"))//背景颜色
                .setTextColorOut(Color.WHITE)//字体颜色
                .setTitleBgColor(Color.parseColor("#2C3241"))//标题栏背景色
                .setTextColorCenter(Color.parseColor("#F95921"))
                .isDialog(true)//设置dialog样式
                .build();
        pickerView.show();


    }

    private void forwardSex() {
        if (mUserBean == null) {
            return;
        }

        DialogUtil.showSexPickerDialog(mContext, mUserBean.getSex(), new DialogUtil.IntCallback() {
            @Override
            public void onConfirmClick(final int sex) {
                HttpUtil.updateFields("{\"sex\":\"" + sex + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            ToastUtil.show(obj.getString("msg"));
                            UserBean u = AppConfig.getInstance().getUserBean();
                            if (u != null) {
                                u.setSex(sex);
                            }
                            if (sex == 1) {
                                mSex.setText(R.string.sex_male);
                                mUserBean.setSex(sex);
                            } else if (sex == 2) {
                                mSex.setText(R.string.sex_female);
                                mUserBean.setSex(sex);
                            } else if (sex == 0) {
                                mSex.setText(R.string.sex_default);
                                mUserBean.setSex(sex);
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 我的印象
     */
    private void forwardImpress() {
        startActivity(new Intent(mContext, MyImpressActivity.class));
    }

    @Override
    protected void onDestroy() {
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        HttpUtil.cancel(HttpConst.UPDATE_AVATAR);
        HttpUtil.cancel(HttpConst.UPDATE_FIELDS);
        super.onDestroy();
    }

    private void showData(UserBean u) {
        ImgLoader.displayAvatar(u.getAvatar(), mAvatar);
        ImgLoader.display(u.getLiveThumb(), ivCover);
        mName.setText(u.getUsername());
        mSign.setText(u.getSignature());
        mBirthday.setText(u.getBirthday());

        if (u.getSex() != 0) {
            mSex.setText(u.getSex() == 1 ? R.string.sex_male : R.string.sex_female);
        } else {
            mSex.setText("保密");
        }

        tvCity.setText(u.getCity());
        txAccount.setText(u.getId());
    }

    public static void forward(Context context) {
        context.startActivity(new Intent(context, UserInfoEditActivity.class));
    }


}
