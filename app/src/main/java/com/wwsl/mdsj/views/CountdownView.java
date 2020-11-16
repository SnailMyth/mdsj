package com.wwsl.mdsj.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import com.wwsl.mdsj.R;

/**
 * Created by chenhao on 16/11/3.
 */

public class CountdownView extends View {

    private int mBorderColor;
    private float mBorderWidth;
    private int mTextColor;
    private float mTextSize;
    private Paint mPaint;
    private OnCountdownListener mOnCountdownListener;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private static final int DEFAULT_COUNT = 5;
    private int mCount = DEFAULT_COUNT;
    private int mCurrentCount = mCount;
    private CountDownTimer mCountDownTimer;


    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountdownView, 0, 0);
        try {
            mBorderColor = typedArray.getColor(R.styleable.CountdownView_border_color, 0x000000);
            mBorderWidth = typedArray.getDimension(R.styleable.CountdownView_border_width, 2);
            mTextColor = typedArray.getColor(R.styleable.CountdownView_text_color, 0x000000);
            mTextSize = typedArray.getDimension(R.styleable.CountdownView_text_size, 14);
        }catch (Exception e){

        }finally {
            typedArray.recycle();
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centre = getWidth()/2; //获取圆心的x坐标
        int radius = (int) (centre - mBorderWidth/2); //圆环的半径
        //开始画圆弧
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        //计算弧度
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限
        int angle =(int)(360* (1 - mCurrentCount*1.0/mCount));
        int start = -90 + angle;   //开始的角度
        int sweepAngle = 360 - angle; //扫过的角度
        canvas.drawArc(oval, start, sweepAngle, false, mPaint);  //根据进度画圆弧
        //开始画倒计时文字
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        //计算数字
        String text = "" + mCurrentCount;
        float textWidth = mPaint.measureText(text);
        canvas.drawText(text, centre- textWidth/ 2, centre + textWidth/2,  mPaint); //画出进度百分比
    }

    public void setCountNum(int num){
        if(num > 0) {
            this.mCount = num;
        }
        mCurrentCount = mCount;
    }


    public void start(){
        mCountDownTimer = new CountDownTimer((mCount+1)*COUNT_DOWN_INTERVAL, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCurrentCount--;
                CountdownView.this.postInvalidate();
                if (mCurrentCount == 0 && mOnCountdownListener != null) {
                    mOnCountdownListener.onFinish();
                }
            }

            @Override
            public void onFinish() {
                //最后一次是会回调在这个方法，但是感觉有点延迟，因此判断只要显示成0了就回调完成。
            }
        };
        mCountDownTimer.start();
    }


    public void stop(){
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
    }

    public void setOnCountdownListener(OnCountdownListener listener){
        this.mOnCountdownListener = listener;
    }

    public interface OnCountdownListener{
        public abstract void onFinish();
    }

}
