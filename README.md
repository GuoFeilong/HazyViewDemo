# HazyViewDemo
刻度表盘雾霾换滤芯提示view
**雾霾在帝都的持久度惊人,很多科技公司也冠冕堂皇的做起来了空气净化器,来麻痹自己,所以Android客户端一个显示空气质量的表盘是必不可少的,昨天在群里看到有人问这个东西怎么做,就闲的稍微写了一下,效果如下!**

###原图
![这里写图片描述](http://img.blog.csdn.net/20170117112037295?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ2l2ZW1lYWNvbmRvbQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

声明一下最外层的渐变以及粗细逐渐增加的圆弧,没有完成,等有时间完善下,先把牛逼吹出去,万一完不成,你来打我啊哈哈哈哈哈......(效果图外面的渐变弧度,从0到进度点线宽是逐渐递增的,只是有个想法可以实现,但是还没写,如果有人看见有合适的思路可以指点下,谢谢)

###看下我们的实现的效果图

![这里写图片描述](http://img.blog.csdn.net/20170117112510104?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ2l2ZW1lYWNvbmRvbQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


###实现思路

 1. view细节拆分
 2. view属性设定
 3. view绘制
 

###细节拆分部分
 效果图如果用Android自定义view来做可以分为如下部分
 

 - 白色表盘
 - 进度指针
 - 表盘上文字
 - 最外层家变弧(未完成)
 
涉及到的canvas API 特别简单

```
canvas.drawCircle
canvas.drawText
canvas.drawLine
```

###属性设定
这一步基本可以忽略了,如果想让自定义和textview一样可以在xml中可以使用属性那么可以做这一步,如果你就想写死,那么基本可以省略100行代码,因为这个雾霾view透传的属性比较多,直接上代码

```
 <declare-styleable name="HazyView">
        <attr name="hazy_value_size" format="dimension" />
        <attr name="hazy_value_color" format="color" />
        <attr name="hazy_value_desc" format="string" />
        <attr name="hazy_title_size" format="dimension" />
        <attr name="hazy_title_color" format="color" />
        <attr name="hazy_title_desc" format="string" />
        <attr name="hazy_desc_size" format="dimension" />
        <attr name="hazy_desc_color" format="color" />
        <attr name="hazy_desc" format="string" />
        <attr name="hazy_view_bg_color" format="color" />
        <attr name="hazy_pb_bg_color" format="color" />
        <attr name="hazy_pb_color" format="color" />
        <attr name="hazy_pb_line_width" format="dimension" />
        <attr name="hazy_view_breach_degree" format="integer" />
    </declare-styleable>

// 下面就是在自定义view中获取部分

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

```

上面将近100行代码是没什么技术含量的,可以忽略;


###重要的最后一步,绘制
我们在确定了view的尺寸之后,根据UI给的设计图我们粗略的估计出各个图层所占的比例,方便适配,尽量不要用写死的尺寸,比例是不会变的,

```
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
....
类似这样的,等view完成后,哪里要调整只需要改变这里的比例即可,
```

####绘制变表盘,一API完成,

```
//这里我用了三句,前两句是给加了个阴影,不想要的可以删掉
 private void drawBgCircle(Canvas canvas) {
        hazyViewBgPaint.setShadowLayer(10, 2, 2, Color.GRAY);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawCircle(viewCenter.x, viewCenter.y, contentCircleRadio, hazyViewBgPaint);
    }
```

####绘制刻度,两句API canvas 旋转 + 保存,注意刻度的计算

```
    private void drawHazyPbBgLine(Canvas canvas) {
        // 注意画布旋转的技巧,以及进度和背景的计算
        canvas.rotate(178 + hazyBreDegreen / 2, viewCenter.x, viewCenter.y);
        canvas.save();
        // 这里是绘制进度刻度和总刻度,不要分开写for循环,尽量写到一个循环里面ondraw中调用循环本来就是 "大胸之罩"了,ondraw频繁调用在加上循环,我擦嘞....
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

```

####绘制文字,drawText即可

```
	//这里我简单的封装了一下文字的坐标计算,这里要根据UI给的文字比例
    private void drawHazyValueAndInfo(Canvas canvas) {
        hazyValuePoint = getTextPointInView(hazyValuePaint, hazyValueDesc, hazyViewWidth, hazyViewHeight);
        hazyTitlePoint = getTextPointInView(hazyTitlePaint, hazyTitleDesc, hazyViewWidth, (int) (hazyViewHeight - hazyViewHeight * VIEW_TEXT_SCALE));
        hazyDescPoint = getTextPointInView(hazyDescPaint, hazyContentDesc, hazyViewWidth, (int) (hazyViewHeight + hazyViewHeight * VIEW_TEXT_SCALE));
        canvas.drawText(hazyValueDesc, hazyValuePoint.x, hazyValuePoint.y, hazyValuePaint);
        canvas.drawText(hazyTitleDesc, hazyTitlePoint.x, hazyTitlePoint.y, hazyTitlePaint);
        canvas.drawText(hazyContentDesc, hazyDescPoint.x, hazyDescPoint.y, hazyDescPaint);
    }

```

####透传接口设置百分比以及雾霾爆表值

```
  /**
     * 设置当前的百分比
     *
     * @param percent 当前的百分比
     */
    public void setCurrentPercent(float percent) {
        this.percent = percent / 100.F;
        // 根据当前爆表值,计算出应该显示的值是多少
        hazyValueDesc = String.valueOf((int) (maxHzV * this.percent));
        invalidate();
    }

    public void setHzValueMax(int maxV) {
        this.maxHzV = maxV;
    }
```

到此这个view就拆分完毕了,没有所谓的怎么让一个圆环绘制两个颜色,以及对不齐的问题,把问题拆分即可,基本核心代码就是这里所有的了
#传送门源代码下载地址:[https://github.com/GuoFeilong/HazyViewDemo记得star谢谢](https://github.com/GuoFeilong/HazyViewDemo)


