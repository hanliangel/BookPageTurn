package com.reader.hanli.readwidget;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by hanli on 2018/3/23.
 */
public abstract class BasePageAdapter {

    /**
     * 页面宽度
     */
    private int mPageWidth;
    /**
     * 页面高度
     */
    private int mPageHeight;

    protected Context mContext;

    public BasePageAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * 返回翻页的数量
     * @return
     */
    public abstract int getPageCount();

    /**
     * 将页面内容填入canvas
     * @param position
     * @param canvas
     */
    public abstract void onDraw(int position , Canvas canvas);

    /**
     * 当页面layout发生改变的时候回调，初始化也会回调一次
     * @param pageWidth
     * @param pageHeight
     */
    public abstract void onPageLayoutChanged(int pageWidth , int pageHeight);

    /**
     * 改变适配器中标示的page的layout
     * @param pageWidth
     * @param pageHeight
     */
    public final void pageLayoutChange(int pageWidth , int pageHeight){
        this.mPageWidth = pageWidth;
        this.mPageHeight = pageHeight;
        onPageLayoutChanged(pageWidth , pageHeight);
    }

    /**
     * 通知数据发生变化
     */
    public void notifyDataSetChanged(){
        onPageLayoutChanged(mPageWidth , mPageHeight);
    }
}
