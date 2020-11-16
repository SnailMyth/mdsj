package com.wwsl.mdsj.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.wwsl.mdsj.R;


/**
 * https://www.jianshu.com/p/7bd8e078cbe7
 */

public class Music extends View {

    private Path path;
    private Paint paint;
    private float[] pos;
    private float[] tan;
    private float length;
    private float val;
    private PathMeasure pathMeasure;
    private Bitmap scaledBitmap;
    private Bitmap bitmap;
    private ValueAnimator valueAnimator;
    private int resourceId = R.mipmap.music1;

    public Music(Context context) {
        this(context, null);
    }

    public Music(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Music);
        if (typedArray != null) {
            resourceId = typedArray.getResourceId(R.styleable.Music_musicimg, R.mipmap.music1);
        }
        init();
    }

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        valueAnimator = ValueAnimator.ofFloat(0, 2f);
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float temp = (float) animation.getAnimatedValue();
                val = temp / 2;
                if (temp > 1) {
                    Music.this.setAlpha(Math.abs(temp - 2f));
                } else {
                    Music.this.setAlpha(temp);
                }
                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        initPath();
    }

    private void initPath() {
        pos = new float[2];
        tan = new float[2];
        path = new Path();
        path.moveTo(getWidth(), getHeight() - getWidth() / 6);
        path.quadTo(0, getHeight(), getWidth() / 4, 0);
        pathMeasure = new PathMeasure(path, false);
        length = pathMeasure.getLength();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        valueAnimator.start();
    }

    private int measureWidth(int widthMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            return width;
        } else if (mode == MeasureSpec.AT_MOST) {
            return getSuggestedMinimumWidth();
        } else {
            return width;
        }
    }

    private int measureHeight(int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            return height;
        } else if (mode == MeasureSpec.AT_MOST) {
            return getSuggestedMinimumHeight();
        } else {
            return height;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        pathMeasure.getPosTan(length * val, pos, tan);
        scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (getWidth() / 5 * val) + 4, (int) (getWidth() / 5 * val) + 4, true);
//        canvas.drawPath(path,paint);
        canvas.drawBitmap(scaledBitmap, pos[0], pos[1], paint);
    }
}
