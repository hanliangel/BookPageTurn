package com.reader.hanli.readwidget.computer;

import android.graphics.Point;

/**
 * Created by hanli on 2018/3/27.
 * 限制点的机制：保证翻页不能翻起左边竖列，如果可能被翻起，那么另最大翻起点也就是c点为页面左下角点
 */
public class DefaultPointComputer implements PointComputer {
    @Override
    public Point getPageTurnedPoint(int width, int height, Point a) {
        Point f = getBeforePageTurnedPoint(width , height , a);
        float a1 = 1f*(f.y - a.y)/(f.x - a.x);
        float b1 = a.y - (a.x)*a1;
        float a2 = 1f*(a.x - f.x)/(f.y - a.y);
        float b2 = f.y;
        float jx = (b2-b1)/(a1-a2);
        Point j = new Point((int)jx , (int)(a1*jx + b1));


        Point g = new Point((f.x + a.x)/2 , (f.y + a.y)/2);
        Point agHalfPoint = new Point((g.x + a.x) / 2 , (g.y + a.y)/2);

        if(j.x > agHalfPoint.x){
            a = new Point((j.x * 4 - f.x)/3 , (j.y * 4 - f.y)/3);
        }
        return a;
    }

    @Override
    public Point getBeforePageTurnedPoint(int width, int height, Point touchPoint) {
        Point pointf = new Point(width , height);
        return pointf;
    }
}
