package com.qin.face.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.qin.face.R;
import com.qin.face.utils.DisplayUtils;

/**
 * Create by qindl
 * on 2018/12/18
 */
public class SelectView extends View {

    private static final String TAG = "TAG";
    private Paint mPaint;
    private RectF mViewRectF;
    private Context mContext;
    private int[] colors = new int[]{0xFFFF0000, 0xFFFFFF00, 0xFF00FF00};
    private float[] pos = new float[]{0.2f, 0.66f, 1.0f};
    private Shader mShader;
    /*
    指示器路径
     */
    private Path mIndicatorPath;
    /*
    默认titles
     */
    private String[] mTitles = new String[]{"较差", "一般", "很好"};

    private Rect mTextRect;
    /*
    标题资源ID
     */
    private int mTitlesId;
    /*
    标题默认颜色
     */
    private int mTitleColor = Color.GRAY;
    /**
     * 默认竖直线颜色
     */
    private int mVeticallineColor = Color.BLACK;
    /*
    标题字体大小
     */
    private float mTitleSize;
    /*
    标题与颜色选择器间距
     */
    private float mTitleViewSpace = 2;
    /*
    颜色选择器与指示器间距
     */
    private float mViewIndicatorSpace = 2;
    /*
    默认文字paint 大小
     */
    private float mStrokeWidth = 2;
    /*
    默认指示器高度
     */
    private float mIndicatorHeight = 40;
    /*
    默认指示器宽度
     */
    private float mIndicatorWidth = 50;
    /*
    默认颜色选择器高度
     */
    private float mSelectviewHeight = 50;
    /**
     * 默认竖直线宽度
     */
    private int mVeticallineWidth = 5;
    /*
    指示器当前位置
     */
    private float mIndicatorPos;
    /*
    指示器区域偏移量
     */
    private float mIndicatorOffset = 10;
    /*
    指示器区域RECTF
     */
    private RectF mIndicatorRect;
    /*
    颜色选择器最右边距离，及宽度
     */
    private float mSelectViewWidth;

    private float mStartX;
    private float mStartY;
    private float mCurrentX;
    /*
    是否可点击，默认true
     */
    private boolean isClickabled = true;

    /*
    点击事件回调
     */
    private OnClickListener mOnClickListener;
    /*
    拖拽事件回调
     */
    private OnDragListener mOnDragListener;

    /**
     * 获取缩放比系数
     *
     * @return
     */
    public float getFraction() {
        return mFraction;
    }

    /**
     * 设置缩放比系数
     *
     * @param fraction
     */
    public void setFraction(float fraction) {
        mFraction = fraction;
    }

    /*
        缩放比
         */
    private float mFraction;

    /**
     * 是否可点击改变位置
     *
     * @return
     */
    public boolean isClickabled() {
        return isClickabled;
    }

    /**
     * 设置是否可点击改变位置
     *
     * @param clickable
     */
    public void setClickabled(boolean clickable) {
        isClickabled = clickable;
    }

    public boolean isDrag() {
        return isDrag;
    }

    /**
     * 设置是否可拖动
     *
     * @param drag
     */
    public void setDrag(boolean drag) {
        isDrag = drag;
    }

    /*
        是否可点击，默认true
        */
    private boolean isDrag = true;
    /*
    是否在指示器内可拖动
     */
    private boolean enableDrag = false;

    public SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mTitleSize = DisplayUtils.sp2px(mContext, 14);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SelectView);
        mTitlesId = ta.getResourceId(R.styleable.SelectView_titles, -1);
        if (mTitlesId != -1) {
            mTitles = mContext.getResources().getStringArray(mTitlesId);
        }
        mTitleColor = ta.getColor(R.styleable.SelectView_title_color, mTitleColor);
        mTitleSize = ta.getDimension(R.styleable.SelectView_title_size, mTitleSize);
        mStrokeWidth = ta.getDimension(R.styleable.SelectView_title_stroke, mStrokeWidth);
        mTitleViewSpace = ta.getDimension(R.styleable.SelectView_title_selectview_space, mTitleViewSpace);
        mViewIndicatorSpace = ta.getDimension(R.styleable.SelectView_selectview_indicator_space, mViewIndicatorSpace);
        mIndicatorHeight = ta.getDimension(R.styleable.SelectView_indicator_height, mIndicatorHeight);
        mIndicatorWidth = ta.getDimension(R.styleable.SelectView_indicator_width, mIndicatorWidth);
        mSelectviewHeight = ta.getDimension(R.styleable.SelectView_selectview_height, mSelectviewHeight);
        mVeticallineColor = ta.getColor(R.styleable.SelectView_veticalline_color, mVeticallineColor);
        mVeticallineWidth = ta.getColor(R.styleable.SelectView_veticalline_width, mVeticallineWidth);
        isDrag = ta.getBoolean(R.styleable.SelectView_isdrag, isDrag);
        isClickabled = ta.getBoolean(R.styleable.SelectView_isclickabled, isClickabled);
        ta.recycle();
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setColor(mTitleColor);
        // 字体样式
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTitleSize);
        mPaint.setAntiAlias(true);

        // 记录指示器初始位置
        mIndicatorPos = (int) (mIndicatorWidth / 2.0f);

    }

    public void setShader(Shader shader) {
        this.mShader = shader;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas);
        drawView(canvas);
        drawIndicator(canvas);
        drawVeticalLine(canvas);
        // 重置画笔
        mPaint.reset();
    }

    /**
     * 绘制指示器竖线
     *
     * @param canvas
     */
    private void drawVeticalLine(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(mVeticallineColor);
        mPaint.setStrokeWidth(mVeticallineWidth);
        canvas.drawLine(mIndicatorPos, mViewRectF.top, mIndicatorPos, mViewRectF.bottom, mPaint);
    }

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        // 初始哈paint
        mPaint = new Paint();
        mPaint.setColor(mTitleColor);
        // 抗锯齿
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTitleSize);
        // 颜色选择条宽度
        int width = getWidth();
        // 绘制文字
        for (int i = 0; i < mTitles.length; i++) {
            mTextRect = new Rect();
            // 获取文字显示区域
            mPaint.getTextBounds(mTitles[i], 0, mTitles[i].length(), mTextRect);
            canvas.drawText(mTitles[i], width / 6.0f + width / 3.0f * i, mTextRect.height(), mPaint);
        }
    }

    /**
     * 绘制颜色条
     *
     * @param canvas
     */
    private void drawView(Canvas canvas) {

        // 获取当前view Margin数值
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.getLayoutParams();

        // 获取字体高度
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float descent = fontMetrics.descent;
        // 构建颜色选择条区域
        mSelectViewWidth = (float) (getWidth() - mIndicatorWidth / 2.0);
        float top = mTextRect.height() + descent + mTitleViewSpace;
        float left = mIndicatorWidth / 2.0f;
        float bottom = mTextRect.height() + descent + mSelectviewHeight + mTitleViewSpace;
        // 颜色选择器区域
        mViewRectF = new RectF(left, top, mSelectViewWidth, bottom);
        // 设置颜色渐变
        mShader = new LinearGradient(0f, getHeight() / 2, getWidth(), getHeight() / 2, colors, pos, Shader.TileMode.CLAMP);
        // 添加着色器
        mPaint.setShader(mShader);
        canvas.drawRect(mViewRectF, mPaint);
    }

    /**
     * 绘制指示器
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        // 清空着色器
        mPaint.clearShadowLayer();
        // 初始化画笔
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);

        // 初始化指示器位置
        mIndicatorPath = new Path();

        // 绘制三角形指示器顶点
        mIndicatorPath.moveTo(mIndicatorPos, mViewRectF.bottom + mViewIndicatorSpace);
        // 绘制三角形指示器右下角
        mIndicatorPath.lineTo(mIndicatorPos + mIndicatorWidth / 2.0f, mViewRectF.bottom + mIndicatorHeight + mViewIndicatorSpace);
        // 绘制三角形指示器左下角
        mIndicatorPath.lineTo(mIndicatorPos - mIndicatorWidth / 2.0f, mViewRectF.bottom + mIndicatorHeight + mViewIndicatorSpace);
        mIndicatorPath.lineTo(mIndicatorPos, mViewRectF.bottom + mViewIndicatorSpace);

        // 记录指示器区域
        mIndicatorRect = new RectF(mIndicatorPos - mIndicatorPos / 2 + mIndicatorOffset,
                mViewRectF.bottom + mViewIndicatorSpace + mIndicatorOffset,
                mIndicatorPos + mIndicatorPos / 2 + mIndicatorOffset,
                mViewRectF.bottom + mIndicatorHeight + mViewIndicatorSpace + mIndicatorOffset);

        canvas.drawPath(mIndicatorPath, mPaint);

    }

    /**
     * 测量view宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);

//        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
//            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
//        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
//            setMeasuredDimension(sizeW, getMeasuredHeight());
//        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
//            setMeasuredDimension(getMeasuredWidth(), sizeH);
//        }

        int height;
        int minHeight = 144;
        if (modeH == MeasureSpec.EXACTLY) {
            height = sizeH;
        } else if (modeH == MeasureSpec.AT_MOST) {
            height = Math.min(minHeight, sizeH);
        } else {
            height = minHeight;
        }
        setMeasuredDimension(sizeW, height);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public int getDisplayWidth() {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return manager != null ? manager.getDefaultDisplay().getWidth() : 0;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public int getDisplayHeight() {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return manager != null ? manager.getDefaultDisplay().getHeight() : 0;
    }

    /**
     * 触摸事件，处理指示器点击，滑动
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 是否可响应点击
                if (!isClickabled) {
                    return false;
                }
                mStartX = event.getX();
                mStartY = event.getY();
                // 判断点击区域是否在颜色选择条上
                if (mViewRectF.contains(mStartX, mStartY)) {
                    // 判断点击是否在颜色选择条内部
                    if (mStartX >= mViewRectF.left / 2.0f && mStartX <= mViewRectF.right) {
                        // 赋值指示器位置为点击位置
                        mIndicatorPos = mStartX;
                        float selectWidth = mStartX - mViewRectF.left;
                        mFraction = selectWidth / mSelectViewWidth;
                        Log.i(TAG, "fraction:百分比 " + mFraction);
                        if (mOnClickListener != null)
                            mOnClickListener.onClick(mFraction);
                        postInvalidate();
                    }
                }
                // 判断是否点击在指示器内部
                if (mIndicatorRect.contains(mStartX, mStartY)) {
                    enableDrag = true;
                }

                return true;
            case MotionEvent.ACTION_UP:
                // 重置enableDrag，只有点击指示器才能移动
                enableDrag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDrag) {
                    break;
                }
                // 是否点击在指示器内部
                if (enableDrag) {
                    mCurrentX = event.getX();
                    // 判断是否超出左边位置
                    if (mCurrentX <= mIndicatorWidth / 2.0) {
                        mCurrentX = (float) (mIndicatorWidth / 2.0);
                    }
                    // 判断是否超出右边位置
                    if (mCurrentX >= mSelectViewWidth) {
                        mCurrentX = mSelectViewWidth;
                    }
                    // 重新赋值指示器位置
                    mIndicatorPos = mCurrentX;
                    float selectWidth = mCurrentX - mViewRectF.left;
                    mFraction = selectWidth / mSelectViewWidth;
                    Log.i(TAG, "move:百分比 " + mFraction);
                    if (mOnDragListener != null)
                        mOnDragListener.onDrag(mFraction);
                    mStartX = mCurrentX;
                    postInvalidate();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 点击事件
     */
    public interface OnClickListener {
        void onClick(float fraction);
    }

    /**
     * 拖动事件
     */
    public interface OnDragListener {
        void onDrag(float fraction);
    }

    public void setOnclickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        this.mOnDragListener = onDragListener;
    }
}
