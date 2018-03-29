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

import com.reader.hanli.readwidget.computer.DefaultPointComputer;
import com.reader.hanli.readwidget.computer.PointComputer;

/**
 * Created by hanli on 2018/3/22.
 * 模仿书本翻页的view
 * 各个关键点点命名参照：http://blog.csdn.net/hmg25/article/details/6306479
 */
public class MimicPageTurnView extends View {

    private Point a;
    private Paint mPaint;

    private TextPaint mTextPaint;

    /**
     * 绘制页面内容的适配器
     */
    private BasePageAdapter mPageTurnAdapter;

    /**
     * 当前的页面位置
     */
    private int mCurrentPosition;

    /**
     * 当前的关键点计算，根据触摸点和页面宽高来决定页脚a点和f点坐标
     */
    private PointComputer mPointComputer;

    public MimicPageTurnView(Context context) {
        super(context);
        init(context , null);
    }

    public MimicPageTurnView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context , null);
    }

    private void init(Context context, @Nullable AttributeSet attrs){
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(50);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(70);

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

            if(hasNextPage()){
                if(a != null){
                    Point f = null;
//                    a = new Point(0 , getHeight() - getWidth());
                    if(mPointComputer == null){
                        f = new Point(getWidth() , getHeight());
                    } else {
                        f = mPointComputer.getBeforePageTurnedPoint(getWidth() , getHeight() , a);
                        a = mPointComputer.getPageTurnedPoint(getWidth() , getHeight() , a);
                    }

                    Point g = new Point((f.x + a.x)/2 , (f.y + a.y)/2);
                    Point e = new Point((int)(g.x - (f.y - g.y)*(f.y - g.y)/(f.x - g.x)) , f.y);
                    Point h = new Point(f.x , (int)(g.y - (f.x - g.x)*(f.x - g.x)/(f.y - g.y)));
                    Point c = new Point(e.x - (f.x - e.x)/2 , f.y);
                    Point j = new Point(f.x , h.y - (f.y - h.y)/2);
                    Point b = CommonUtil.getIntersectionPoint(c , j , e , a);
                    Point k = CommonUtil.getIntersectionPoint(c , j , a , h);
                    Point d = new Point(((c.x + b.x)/2 + e.x)/2 , ((c.y + b.y)/2 + e.y)/2);
                    Point i = new Point(((k.x + j.x)/2 + h.x)/2 , ((k.y + j.y)/2 + h.y)/2);


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
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        a = new Point((int)event.getX() , (int)event.getY());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                a = null;
                break;
        }
        invalidate();
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
     * 设置当前的坐标计算器
     * @param pointComputer
     */
    public void setPointComputer(PointComputer pointComputer){
        this.mPointComputer = pointComputer;
    }
}
