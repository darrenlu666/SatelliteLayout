package com.ldh.android.satellitelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * 卫星式菜单
 * Created by ldh on 2018/2/1.
 */

public class ArcMenuLayout extends ViewGroup {

    private static final String TAG = "ArcMenuLayout";

    //每个item之间的角度
    private int mAngle;

    //圆弧半径
    private float mRadius;

    //圆弧边缘颜色
    private int mArcColor;

    //圆弧中心颜色
    private int mCenterColor = Color.parseColor("#ffffff");

    //圆弧画笔
    private Paint mArcPaint;

    private ImageView mContrlIv;

    private @DrawableRes
    int mExpandRes  = R.mipmap.ic_arc_expand;

    private @DrawableRes
    int mCloseRes = R.mipmap.ic_arc_collapse;

    private @DrawableRes
    int mBackGroundRes = R.mipmap.ic_arc_backgroud;

    //状态
    private enum Status {
        STATUS_COLLAPSE,//关闭
        STATUS_EXPAND//展开
    }

    private Status mCurrentStatus = Status.STATUS_COLLAPSE;

    private OnMenuItemClickListener mOnMenuItemClickListener;

    public ArcMenuLayout setOnMenuItemClickListener(OnMenuItemClickListener listener){
        mOnMenuItemClickListener = listener;
        return this;
    }

    public ArcMenuLayout setContrlIv(ImageView view){
        mContrlIv = view;
        return this;
    }

    public ArcMenuLayout setExpandRes(@DrawableRes int resId){
        mExpandRes = resId;
        return this;
    }

    public ArcMenuLayout setCloseRes(@DrawableRes int resId){
        mCloseRes = resId;
        return this;
    }

    public ArcMenuLayout setBackGroundRes(@DrawableRes int resId){
        mBackGroundRes = resId;
        return this;
    }

    public interface OnMenuItemClickListener{
        void ItemClick(String name);
    }

    public ArcMenuLayout(Context context) {
        this(context, null);
    }

    public ArcMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ArcMenuLayout, defStyleAttr, 0);
        mArcColor = array.getColor(R.styleable.ArcMenuLayout_arcColor, Color.parseColor("#a7b5cd"));

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.FILL);
        mArcPaint.setAlpha(180);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //渐变
        RadialGradient gradient = new RadialGradient((float) (w / 2), (float) h, (float) (w / 2), mCenterColor, mArcColor, Shader.TileMode.CLAMP);
        mArcPaint.setShader(gradient);
        mRadius = w / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            Log.d(TAG, "onLayout()~");
            mAngle = 180 / (getChildCount() - 1);
            View control = getChildAt(0);
            control.layout(getWidth() / 2 - control.getMeasuredWidth() / 2,
                    getHeight() - control.getMeasuredHeight(),
                    getWidth() / 2 + control.getMeasuredWidth() / 2,
                    getHeight());
            control.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleMenu();
                    LinearLayout layout = (LinearLayout)v;
                    rotateButton(mContrlIv, 0f, 360f, 300);
                }
            });

            double length = mRadius * 0.7;

            for (int i = 1; i < getChildCount(); i++) {
                View menu = getChildAt(i);
                menu.setVisibility(GONE);
                menu.layout((int) (mRadius - length * Math.cos(mAngle * i * Math.PI / 180) - menu.getMeasuredWidth() / 2),
                        (int) (mRadius - length * Math.sin(mAngle * i * Math.PI / 180) - menu.getMeasuredHeight() / 2),
                        (int) (mRadius - length * Math.cos(mAngle * i * Math.PI / 180) + menu.getMeasuredWidth() / 2),
                        (int) (mRadius - length * Math.sin(mAngle * i * Math.PI / 180) + menu.getMeasuredHeight() / 2));
                menu.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mOnMenuItemClickListener != null){
                            mOnMenuItemClickListener.ItemClick(v.getTag().toString());
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw()~");

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.d(TAG, "dispatchDraw()~");
        //canvas.drawCircle(getWidth() / 2, getHeight(), getWidth() / 2, mArcPaint);
    }

    private void rotateButton(View v, float start, float end, int duration) {
        RotateAnimation animation = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mCurrentStatus == Status.STATUS_EXPAND){
                    mContrlIv.setImageResource(mExpandRes);
                }else{
                    mContrlIv.setImageResource(mCloseRes);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(animation);
    }

    private void toggleMenu() {
        double length = mRadius * 0.7;
        for (int i = 1; i < getChildCount() ; i++) {
            final View child = getChildAt(i);
            final Animation transAnim;
            if(mCurrentStatus == Status.STATUS_COLLAPSE){
                transAnim = new TranslateAnimation(
                        (float) (length * Math.cos(mAngle * i * Math.PI / 180)),
                        0,
                        (float) (length * Math.sin(mAngle * i * Math.PI / 180)),
                        0);
                child.setClickable(true);
                child.setFocusable(true);

            }else{
                transAnim = new TranslateAnimation(0,
                        (float) (length * Math.cos(mAngle * i * Math.PI / 180)),
                        0,
                        (float) (length * Math.sin(mAngle * i * Math.PI / 180)));
                child.setClickable(false);
                child.setFocusable(false);
            }
            transAnim.setFillAfter(true);
            transAnim.setDuration(200);
            transAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.STATUS_COLLAPSE) {
                        transAnim.setFillAfter(false);
                        child.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            child.startAnimation(transAnim);
        }
        changStatus();
    }

    void changStatus(){
        if(mCurrentStatus == Status.STATUS_COLLAPSE){
            mCurrentStatus = Status.STATUS_EXPAND;
            this.setBackgroundResource(mBackGroundRes);

        }else{
            mCurrentStatus = Status.STATUS_COLLAPSE;
            this.setBackground(null);
        }
    }
}
