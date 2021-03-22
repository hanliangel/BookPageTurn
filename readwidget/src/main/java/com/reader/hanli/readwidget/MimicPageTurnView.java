package com.reader.hanli.readwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;

import com.reader.hanli.readwidget.computer.DefaultPointComputer;
import com.reader.hanli.readwidget.computer.PointComputer;

/**
 * Created by hanli on 2018/3/22.
 * 模仿书本翻页的view
 * 各个关键点点命名参照：http://blog.csdn.net/hmg25/article/details/6306479
 */
public class MimicPageTurnView extends View {

    /**
     * 翻页动画进行的时间
     */
    private static final int TURN_PAGE_ANIM_DURATION = 500;

    /**
     * 下一页 , 上方按下
     */
    private static final int ACTION_NEXT_PAGE_TOP = 1;

    /**
     * 下一页， 下方按下
     */
    private static final int ACTION_NEXT_PAGE_BOTTOM = 2;

    /**
     * 上一页，上方按下
     */
    private static final int ACTION_PREVIOUS_PAGE_TOP = 3;

    /**
     * 上一页，下方按下
     */
    private static final int ACTION_PREVIOUS_PAGE_BOTTOM = 4;

    /**
     * 前往
     */
    private static final int ACTION_TURN_START = 1;

    /**
     * 复原
     */
    private static final int ACTION_TURN_RECOVER = 2;

    /**
     * 手指拖动的点，有 {@link PointComputer} 对其进行限制从而达到一些效果
     */
    private Point a;

    /**
     * 手势最后抬起的时候的位置，用来辅助处理后续的翻页动画效果
     */
    private Point mLastA;

    /**
     * d 和 i 两个点决定了翻页的右边缘，可以用来进行页面翻动到了哪里的判断
     */
    private Point d;
    private Point i;

    /**
     * 渲染用的画笔
     */
    private Paint mPaint;

    /**
     * 绘制页面内容的适配器
     */
    private BasePageAdapter mPageTurnAdapter;

    /**
     * 当前的页面位置
     */
    private int mCurrentPosition;

    /**
     * 当前翻页要去的页
     */
    private int mTurnToPagePosition;

    /**
     * 当前的关键点计算，根据触摸点和页面宽高来决定页脚a点和f点坐标
     */
    private PointComputer mPointComputer;

    /**
     * 手指放开的页面滑动辅助计算
     */
    private Scroller mScroller;

    /**
     * 当前是否开始了滑动动画
     */
    private boolean mIsStartAnim;

    /**
     * 当前的动作类型，分为：
     * 下一页
     * 上一页
     */
    private int mCurrentPageAction;

    /**
     * 当前的跳转动作类型，分为：
     * 前往
     * 复原
     */
    private int mCurrentTurnAction;

    /**
     * 上一个手势的坐标点
     */
    private Point mLastTouchPoint;

    public MimicPageTurnView(Context context) {
        super(context);
        init(context , null);
    }

    public MimicPageTurnView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context , null);
    }

    private void init(Context context, @Nullable AttributeSet attrs){
        mScroller = new Scroller(context , new AccelerateInterpolator());

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(50);

//        mTextPaint = new TextPaint();
//        mTextPaint.setAntiAlias(true);
//        mTextPaint.setTextSize(70);

        mPointComputer = new DefaultPointComputer();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mPageTurnAdapter != null){
            mPageTurnAdapter.onPageLayoutChanged(getMeasuredWidth() , getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mPageTurnAdapter == null || mPageTurnAdapter.getPageCount() == 0){
            canvas.drawColor(0xff000000);
        }else{
            // 绘制当前页
            canvas.save();
            canvas.drawColor(0xffff0000);
            mPageTurnAdapter.onDraw(mCurrentPosition , canvas);
            canvas.restore();

            // 绘制翻页效果
            if(hasNextPage()){
                if(a != null){
                    Point f = null;
//                    a = new Point(0 , getHeight() - getWidth());
                    if(mPointComputer == null){
                        f = new Point(getWidth() , getHeight());
                    } else {
                        f = mPointComputer.getBeforePageTurnedPoint(getWidth() , getHeight() , a , mIsStartAnim);
                        a = mPointComputer.getPageTurnedPoint(getWidth() , getHeight() , a , mIsStartAnim);
                    }

                    Point g = new Point((f.x + a.x)/2 , (f.y + a.y)/2);
                    Point e = new Point((int)(g.x - (f.y - g.y)^2/(f.x - g.x)) , f.y);
                    Point h = new Point(f.x , (int)(g.y - (f.x - g.x)*(f.x - g.x)/(f.y - g.y)));
                    Point c = new Point(e.x - (f.x - e.x)/2 , f.y);
                    Point j = new Point(f.x , h.y - (f.y - h.y)/2);
                    Point b = CommonUtil.getIntersectionPoint(c , j , e , a);
                    Point k = CommonUtil.getIntersectionPoint(c , j , a , h);
                    d = new Point(((c.x + b.x)/2 + e.x)/2 , ((c.y + b.y)/2 + e.y)/2);
                    i = new Point(((k.x + j.x)/2 + h.x)/2 , ((k.y + j.y)/2 + h.y)/2);


                    Path path1 = new Path();
                    path1.moveTo(j.x , j.y);
                    path1.quadTo(h.x , h.y , k.x , k.y);
                    path1.lineTo(a.x , a.y);
                    path1.lineTo(b.x , b.y);
                    path1.quadTo(e.x , e.y , c.x, c.y);
                    path1.lineTo(f.x , f.y);
                    path1.lineTo(j.x , j.y);
                    path1.close();

                    Path path2 = new Path();
                    path2.moveTo(d.x , d.y);
                    path2.lineTo(a.x , a.y);
                    path2.lineTo(i.x , i.y);
                    path2.lineTo(d.x , d.y);
                    path2.close();
                    canvas.clipPath(path1);

                    // 绘制下一页
                    canvas.save();
                    canvas.drawColor(0xff00ff00);
                    mPageTurnAdapter.onDraw(mCurrentPosition + 1 , canvas);
                    canvas.restore();

                    canvas.clipPath(path2);
                    // 绘制第一页背面，暂时固定一个颜色
                    canvas.drawColor(0xff0000ff);

                    // 绘制阴影
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!hasNextPage()){
            return false;
        }
        a = new Point((int)event.getX() , (int)event.getY());
        Point currentTouchPoint = new Point((int)event.getX() , (int)event.getY());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(currentTouchPoint.x < getWidth() / 2){
                    if(currentTouchPoint.y < getHeight() / 2){
                        changePageAction(ACTION_PREVIOUS_PAGE_TOP);
                    } else {
                        changePageAction(ACTION_PREVIOUS_PAGE_BOTTOM);
                    }
                } else {
                    if(currentTouchPoint.y < getHeight() / 2){
                        changePageAction(ACTION_NEXT_PAGE_TOP);
                    } else {
                        changePageAction(ACTION_NEXT_PAGE_BOTTOM);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int turnAction = -1;
                if(currentTouchPoint.x < mLastTouchPoint.x){
                    // 手势向左
                    turnAction = ACTION_TURN_START;
                } else {
                    // 手势向右
                    turnAction = ACTION_TURN_RECOVER;
                }
                if(turnAction != mCurrentTurnAction){
                    changeTurnAction(turnAction);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastA = a;
                a = null;
                startPageAnim();
                invalidate();
                break;
        }
        mLastTouchPoint = currentTouchPoint;
        return true;
    }


    /**
     * 设置翻页绘制页面使用的adapter
     * @param adapter
     */
    public void setPageTurnAdapter(BasePageAdapter adapter){
        this.mPageTurnAdapter = adapter;
        mCurrentPosition = 0;
        invalidate();
        if(getMeasuredWidth() != 0 && getMeasuredHeight() != 0){
            mPageTurnAdapter.pageLayoutChange(getMeasuredWidth() , getMeasuredHeight());
        }
    }

    /**
     * 是否还有下一页
     * @return
     */
    private boolean hasNextPage(){
        if(mPageTurnAdapter == null || mPageTurnAdapter.getPageCount() == 0 || mCurrentPosition == mPageTurnAdapter.getPageCount() - 1){
            return false;
        }
        return true;
    }

    /**
     * 是否还有上一页
     * @return
     */
    private boolean hasLastPage(){
        if(mPageTurnAdapter == null || mPageTurnAdapter.getPageCount() == 0 || mCurrentPosition == 0){
            return false;
        }
        return true;
    }

    /**
     * 开始翻页动画
     */
    private void startPageAnim(){
        endPageAnim();
        switch (mCurrentPageAction){
            case ACTION_NEXT_PAGE_TOP:
                break;
            case ACTION_NEXT_PAGE_BOTTOM:
                break;
            case ACTION_PREVIOUS_PAGE_TOP :
                break;
            case ACTION_PREVIOUS_PAGE_BOTTOM:
                break;
        }

        int startX = mLastA.x;
        int startY = mLastA.y;
        int dx = 0;
        int dy = 0;
        switch (mCurrentTurnAction){
            case ACTION_TURN_RECOVER:
                dx = getWidth() - startX;
                dy = getHeight() - startY;
                mTurnToPagePosition = mCurrentPosition;
                break;
            case ACTION_TURN_START:
                dx = -getWidth() - startX;
                dy = getHeight() - startY;
                mTurnToPagePosition = mCurrentPosition + 1;
                break;
        }
        mIsStartAnim = true;
        mScroller.startScroll(startX , startY , dx , dy , TURN_PAGE_ANIM_DURATION);
    }

    /**
     * 根据手势改变当前标示的页面动作状态
     * @param pageAction
     */
    private void changePageAction(int pageAction ){
        mCurrentPageAction = pageAction;
    }

    /**
     * 根据手势改变当前标示的跳转动作状态
     * @param turnAction
     */
    private void changeTurnAction(int turnAction){
        mCurrentTurnAction = turnAction;
    }

    /**
     * 结束翻页动画
     */
    private void endPageAnim(){
        if(mIsStartAnim){
            mIsStartAnim = false;
//            mLastA = null;
            a = null;
            mCurrentPosition = mTurnToPagePosition;
            mCurrentPageAction = -1;
            mCurrentTurnAction = -1;
            invalidate();
        }
    }

    /**
     * 设置当前的坐标计算器
     * @param pointComputer
     */
    public void setPointComputer(PointComputer pointComputer){
        this.mPointComputer = pointComputer;
    }

    @Override
    public void computeScroll() {
        if(mLastA != null && mIsStartAnim){
            if(mScroller.computeScrollOffset() && mScroller.getCurrX() != getWidth() && mScroller.getCurrY() != getHeight()){
                int currX = mScroller.getCurrX();
                int currY = mScroller.getCurrY();
                mLastA.x = currX;
                mLastA.y = currY;
                a = mLastA;
                invalidate();
            }else {
                // 动画执行完毕
                endPageAnim();
            }
        } else {
            // 并没有动画开始
        }
    }
}
