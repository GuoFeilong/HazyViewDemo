package com.example.jsion.hazyviewdemo.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.jsion.hazyviewdemo.R;

/**
 * Created by Feilong.Guo on 2017/1/16.
 */

public class HazyView extends View {
    /**
     * 默认的角度间隔
     */
    private static final int DEF_INTERVAL_DEGREE = 5;
    /**
     * 内容距离view的padding
     */
    private static final int DEF_CONTENT_PADDING = 10;
    /**
     * 进度距离白点的距离比例
     */
    private static final float DEF_CONTENT_PADDING_F = 5 / 340.F;
    /**
     * 默认view的大小
     */
    private static final int DEF_VIEW_SIZE = 300;
    /**
     * 进度占view的比例
     */
    private static final float VIEW_PB_SCALE = 320 / 340.F;
    /**
     * 中间大白点的边缘比例
     */
    private static final float VIEW_CIRCLE_END_SCALE = 300 / 340.F;
    /**
     * 中间大白点view的比例
     */
    private static final float VIEW_CIRCLE_SCALE = 280 / 340.F;
    /**
     * 线条的进度的高度比例
     */
    private static final float VIEW_LINE_SCALE = 12 / 340.F;
    /**
     * 文字描述比例
     */
    private static final float VIEW_TEXT_SCALE = 100 / 340.F;

    private int hazyViewHeight;
    private int hazyViewWidth;
    private int contentRadio;
    private int contentPbRadio;
    private int contentCircleRadio;
    private int contentPadding;
    private Point viewCenter;
    private Point hazyValuePoint;
    private Point hazyTitlePoint;
    private Point hazyDescPoint;
    private float percent;

    private Paint hazyValuePaint;
    private Paint hazyTitlePaint;
    private Paint hazyDescPaint;
    private Paint hazyViewBgPaint;
    private Paint hazyPbBgPaint;
    private Paint hazyPbPaint;


    private int hazyValueColor;
    private int hazyValueSize;
    private int hazyTitleColor;
    private int hazyTitleSize;
    private int hazyDescColor;
    private int hazyDescSize;
    private int hazyViewBgColor;
    private int hazyPbBgColor;
    private int hazyPbColor;
    private int hazyPbLineWidth;
    private int hazyBreDegreen;
    private int contentSize;
    private String hazyValueDesc;
    private String hazyTitleDesc;
    private String hazyContentDesc;

    public HazyView(Context context) {
        this(context, null);
    }

    public HazyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HazyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initData();
        initPaints();
    }

    private void initPaints() {
        hazyValuePaint = creatPaint(hazyValueColor, hazyValueSize, Paint.Style.FILL, 0);
        hazyTitlePaint = creatPaint(hazyTitleColor, hazyTitleSize, Paint.Style.FILL, 0);
        hazyDescPaint = creatPaint(hazyDescColor, hazyDescSize, Paint.Style.FILL, 0);
        hazyViewBgPaint = creatPaint(hazyViewBgColor, 0, Paint.Style.FILL, 0);
        hazyPbBgPaint = creatPaint(hazyPbBgColor, 0, Paint.Style.FILL, hazyPbLineWidth);
        hazyPbPaint = creatPaint(hazyPbColor, 0, Paint.Style.FILL, hazyPbLineWidth);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HazyView, 0, R.style.def_hazy_view_style);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.HazyView_hazy_value_color:
                    hazyValueColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.HazyView_hazy_value_size:
                    hazyValueSize = typedArray.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.HazyView_hazy_title_color:
                    hazyTitleColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.HazyView_hazy_title_size:
                    hazyTitleSize = typedArray.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.HazyView_hazy_desc_color:
                    hazyDescColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.HazyView_hazy_desc_size:
                    hazyDescSize = typedArray.getDimensionPixelSize(attr, 0);
                    break;
                case R.styleable.HazyView_hazy_view_bg_color:
                    hazyViewBgColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.HazyView_hazy_pb_bg_color:
                    hazyPbBgColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.HazyView_hazy_pb_color:
                    hazyPbColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.HazyView_hazy_pb_line_width:
                    hazyPbLineWidth = typedArray.getDimensionPixelOffset(attr, 0);
                    break;
                case R.styleable.HazyView_hazy_view_breach_degree:
                    hazyBreDegreen = typedArray.getInt(attr, 0);
                    break;
                case R.styleable.HazyView_hazy_value_desc:
                    hazyValueDesc = typedArray.getString(attr);
                    break;
                case R.styleable.HazyView_hazy_title_desc:
                    hazyTitleDesc = typedArray.getString(attr);
                    break;
                case R.styleable.HazyView_hazy_desc:
                    hazyContentDesc = typedArray.getString(attr);
                    break;
            }
        }
        typedArray.recycle();
    }

    private void initData() {
        contentPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_CONTENT_PADDING, getContext().getResources().getDisplayMetrics());
        percent = 65 / 100.F;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        hazyViewHeight = h;
        hazyViewWidth = w;
        viewCenter = new Point(w / 2, h / 2);
        contentSize = w - 2 * contentPadding;
        contentRadio = contentSize / 2;
        contentPbRadio = (int) (contentRadio * VIEW_PB_SCALE);
        contentCircleRadio = (int) (contentRadio * VIEW_CIRCLE_SCALE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize;
        int heightSize;

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_VIEW_SIZE, getResources().getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_VIEW_SIZE, getResources().getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBgCircle(canvas);
        drawHazyValueAndInfo(canvas);
        drawHazyPbBgLine(canvas);
        drawHazyPbLine(canvas);
    }

    private void drawHazyPbLine(Canvas canvas) {

    }


    /**
     * 绘制刻度背景进度
     *
     * @param canvas 画布
     */
    private void drawHazyPbBgLine(Canvas canvas) {
        // 注意画布旋转的技巧,以及进度和背景的计算
        canvas.rotate(178 + hazyBreDegreen / 2, viewCenter.x, viewCenter.y);
        canvas.save();
        for (int i = 0; i < 360 - hazyBreDegreen; i++) {
            if (i % DEF_INTERVAL_DEGREE == 0) {
                canvas.rotate(DEF_INTERVAL_DEGREE, viewCenter.x, viewCenter.y);
                if (i < 360 - hazyBreDegreen * percent - 360 * (1 - percent)) {
                    canvas.drawLine(viewCenter.x, contentPadding + contentSize * DEF_CONTENT_PADDING_F, viewCenter.x, contentPadding + contentSize * (VIEW_LINE_SCALE + DEF_CONTENT_PADDING_F), hazyPbPaint);
                } else {
                    canvas.drawLine(viewCenter.x, contentPadding + contentSize * DEF_CONTENT_PADDING_F, viewCenter.x, contentPadding + contentSize * (VIEW_LINE_SCALE + DEF_CONTENT_PADDING_F), hazyPbBgPaint);
                }
            }
        }
        canvas.restore();
    }

    /**
     * 注意三行文字的计算显示
     *
     * @param canvas 画布
     */
    private void drawHazyValueAndInfo(Canvas canvas) {
        hazyValuePoint = getTextPointInView(hazyValuePaint, hazyValueDesc, hazyViewWidth, hazyViewHeight);
        hazyTitlePoint = getTextPointInView(hazyTitlePaint, hazyTitleDesc, hazyViewWidth, (int) (hazyViewHeight - hazyViewHeight * VIEW_TEXT_SCALE));
        hazyDescPoint = getTextPointInView(hazyDescPaint, hazyContentDesc, hazyViewWidth, (int) (hazyViewHeight + hazyViewHeight * VIEW_TEXT_SCALE));
        canvas.drawText(hazyValueDesc, hazyValuePoint.x, hazyValuePoint.y, hazyValuePaint);
        canvas.drawText(hazyTitleDesc, hazyTitlePoint.x, hazyTitlePoint.y, hazyTitlePaint);
        canvas.drawText(hazyContentDesc, hazyDescPoint.x, hazyDescPoint.y, hazyDescPaint);
    }

    private void drawBgCircle(Canvas canvas) {
        hazyViewBgPaint.setShadowLayer(10, 2, 2, Color.GRAY);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawCircle(viewCenter.x, viewCenter.y, contentCircleRadio, hazyViewBgPaint);
    }


    private Paint creatPaint(int paintColor, int textSize, Paint.Style style, int lineWidth) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);
        paint.setDither(true);
        paint.setTextSize(textSize);
        paint.setStyle(style);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }

    private Point getTextPointInView(Paint textPaint, String textDesc, int w, int h) {
        if (null == textDesc) return null;
        Point point = new Point();
        int textW = (w - (int) textPaint.measureText(textDesc)) / 2;
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        int textH = (int) Math.ceil(fm.descent - fm.top);
        point.set(textW, h / 2 + textH / 2 - textH / 4);
        return point;
    }

    /**
     * 设置当前的百分比
     *
     * @param percent 当前的百分比
     */
    public void setCurrentPercent(float percent) {
        this.percent = percent / 100.F;
        invalidate();
    }


}
