package com.reader.hanli.readwidget.computer;

import android.graphics.Point;

/**
 * Created by hanli on 2018/3/27.
 */

public interface PointComputer {

    /**
     * 获得页面翻起的页角坐标,即a点
     * @param width
     * @param height
     * @param touchPoint
     * @return
     */
    Point getPageTurnedPoint(int width , int height , Point touchPoint);

    /**
     * 获得页面翻起前的页脚坐标，即b点
     * @param width
     * @param height
     * @param touchPoint
     * @return
     */
    Point getBeforePageTurnedPoint(int width , int height , Point touchPoint);
}
