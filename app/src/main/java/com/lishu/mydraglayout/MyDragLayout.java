package com.lishu.mydraglayout;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 * ┃　　　┃   神兽保佑
 * ┃　　　┃   代码无BUG！
 * ┃　　　┗━━━┓
 * ┃　　　　　　　┣┓
 * ┃　　　　　　　┏┛
 * ┗┓┓┏━┳┓┏┛
 * ┃┫┫　┃┫┫
 * ┗┻┛　┗┻┛
 *
 * @author 狼
 *         data: 2018/4/25.
 *         QQ 281788747 ;;
 */

public class MyDragLayout extends FrameLayout {

    public  static  final  String TAG = "MyDragLayout";

    //定义帮助类；
    ViewDragHelper viewDragHelper;


    private View layoutLift;
    /**
     * 设置
     */
    private View layoutMain;
    /**
     * 本空间的宽度
     */
    private int mWidth;
    /**
     * 本控件的高度
     */
    private int mHeight;
    /**
     * 菜单能打开的限度
     */
    private int mRange;

    /**
     * 定义一个回调
     */
     private OnDragStatuChangeListener   mlistener;

    /**
     * 设置缓存  ；；；；
     * @param mlistener
     */
    public void  setOnDragStatuChangeListener(OnDragStatuChangeListener mlistener){
         this.mlistener = mlistener;
     }

    /**
     * 默认是关闭状态
     */
    private DragStatu statu = DragStatu.CLOSE;

    /**
     * 第二步定义下回调
     */
    ViewDragHelper.Callback callback = new ViewDragHelper.Callback(){
        /**
         * 第四部，重写方法
         * /
         /**   返回值决定  是否可以拖动
         * @param child
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }
        /**
         * 当view 被捕获的时候调用
         * @param capturedChild
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }
        /**
         * 返回拖动的位置 位置的修正
         * @param child  还没有真正的移动
         * @param left  建议达到的位置  = 当前+ 瞬间变化量  dx
         * @param dx  瞬间变化量
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d(TAG, "clampViewPositionHorizontal: 建议达到的位置:" + left + "::当前位置:" +layoutMain.getLeft()+ "::瞬间变化量:" + dx);
            if(child == layoutMain){
                left = fixLeft(left);
            }
            return left;
        }

        /**
         * 矫正的方法
         * @param left 返回矫正的值
         * @return
         */
        private int fixLeft(int left) {
            if (left < 0) {
                left = 0;
            } else if (left > mRange) {
                left = mRange;
            }
            return left;
        }



        /**
         * 返回/横向拖动的范围
         * 1. 计算伴随动画的时长 2. 校验 最小敏感度   大于0 即可
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        /**
         * 控件的位置 已经 移动
         * 1. 伴随动画  2.  状态更新 3.  添加回调
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //  如果拖动的是左边的面板   右边的面板跟着向左走
            if(changedView == layoutLift){

                //  有一点变化量 就 放回去
                layoutLift.layout(0, 0, 0 + mWidth, 0 + mHeight);

                int oldLeft = layoutMain.getLeft();
                int newLeft = oldLeft + dx;
                //重新修正；；；
                newLeft = fixLeft(newLeft);
                //布局
                layoutMain.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
            }
            dispathEvent(layoutMain.getLeft());
        }
        /**
         * 手指释放的时候调用
         * @param releasedChild   释放在子view
         * @param xvel  水平方向释放时的速度   向右 +  向左  -     0 手指停止后释放
         * @param yvel   垂直方向的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            if (layoutMain.getLeft() > mRange * 0.5f && xvel == 0) {
                open();
            } else if (xvel > 0) {
                // 向右
                open();
            } else {
                close();
            }

        }
    };






    /**
     * 打开
     */
    private void open() {
        open(true);//默认平滑打开
    }

    public void open(boolean isSmooth) {
        if (isSmooth) {
            boolean b = viewDragHelper.smoothSlideViewTo(layoutMain, mRange, 0);
            // 返回值决定是否触发动画
            if (b) {
                //执行动画
                ViewCompat.postInvalidateOnAnimation(this);   // ---->多次调用 computeScroll
            }
        } else {
            layoutMain.layout(mRange, 0, mRange + mWidth, 0 + mHeight);
        }
    }

    public void close(boolean isSmooth) {
        if (isSmooth) {
            boolean b = viewDragHelper.smoothSlideViewTo(layoutMain, 0, 0);
            // 返回值决定是否触发动画
            if (b) {
                //执行动画
                ViewCompat.postInvalidateOnAnimation(this);   // ---->多次调用 computeScroll
            }
        } else {
            // 主面板位置
            layoutMain.layout(0, 0, 0 + mWidth, 0 + mHeight);
        }
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        // 多次调用
        boolean b = viewDragHelper.continueSettling(true);
        // 是否继续触发动画
        if (b) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 关闭
     */
    public void close() {

        close(true);//默认平滑关闭
    }




    /**
     * 1.  伴随动画   主面板的 左边的坐标
     *
     * @param left
     */
    private void dispathEvent(int left){
        //获取百分比 ；
        float percent =  left*1.0f / mRange;
        //设置动画；
        Log.e(TAG,"百分比："+percent);

        setAnimation(percent);
        //设置动画的；；；
        DragStatu preStatu =  statu;
        //获取现在状态 ；；；
        statu = updateStatus(percent);
        //设置回调监听；；
        if (mlistener != null) {
            mlistener.draging(percent);

            if (statu != preStatu) {
                //状态改变了
                // 通知用户状态改变
                if (statu == DragStatu.CLOSE) {
                    mlistener.close();
                } else if (statu == DragStatu.OPEN) {
                    mlistener.open();
                }
            }
        }

    }


    /**
     * 根据百分比设置 状态
     * @param percent
     * @return
     */
    private DragStatu updateStatus(float percent) {
        if (percent == 0) {
            return DragStatu.CLOSE;
        } else if (percent == 1) {
            return DragStatu.OPEN;
        }
        return DragStatu.DRAGING;
    }


    /**
     * 设置动画的进度；；；
     */
    private void setAnimation(float percent) {
        ViewHelper.setScaleX(layoutMain, evaluate(percent, 1.0f, 0.8f));
        ViewHelper.setScaleY(layoutMain, evaluate(percent, 1.0f, 0.8f));

        //侧边面板的动画  缩放 0.7 - 1.0    平移
        ViewHelper.setScaleX(layoutLift, evaluate(percent, 0.7f, 1.0f));
        ViewHelper.setScaleY(layoutLift, evaluate(percent, 0.7f, 1.0f));

        //平移
        ViewHelper.setTranslationX(layoutLift, evaluate(percent, -mWidth * 0.5f, 0));


        //背景 滤镜
        getBackground().setColorFilter((Integer) evaluateColor(percent, 0xFF000000, 0x00000000)
                , PorterDuff.Mode.SRC_OVER);
    }



    /**
     * 颜色的值
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24) |
                (int) ((startR + (int) (fraction * (endR - startR))) << 16) |
                (int) ((startG + (int) (fraction * (endG - startG))) << 8) |
                (int) ((startB + (int) (fraction * (endB - startB))));
    }
    /**
     * 估值器
     *
     * @param fraction
     * @param startValue
     * @param endValue
     * @return
     */
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }


    /**
     * 定义三个 构造函数
     * @param context
     */
    public MyDragLayout(@NonNull Context context) {
        this(context,null);
    }

    public MyDragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    public MyDragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //第一步添加构造函数；
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 第三部，把触摸事件交给ViewDragerHelper 处理；；；
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            viewDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取两个view ;
        layoutLift = findViewById(R.id.layout_lift);
        layoutMain = findViewById(R.id.layout_main);
    }



    //测量 子view
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 只有改变的时候才会调用
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        // 获取拖动的范围   60%
        mRange = (int) (mWidth * 0.6);
    }


    /**
     * 定义一个枚举的状态：
     * 打开，关闭 ， 打开中
     */
    enum DragStatu {
        OPEN, CLOSE, DRAGING
    }

    /**
     * 定义一个监听；；；
     *
     */
    public interface OnDragStatuChangeListener {
        void open();

        void close();

        void draging(float percent);
    }



}
