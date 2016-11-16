package com.example.xuewenlong.xystudy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xuewenlong on 2016/6/1.
 */
public class DrawView extends View {
    public float currentX = 40;
    public float currentY = 50;
    Paint p = new Paint();

    public DrawView(Context context, AttributeSet set) {
        super(context,set);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置画笔的颜色
        p.setColor(Color.RED);
        //绘制一个小球
        //参数分别是：圆心坐标，半径 ，所使用的画笔
        canvas.drawCircle(currentX, currentY, 15, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                this.currentX = event.getX();
                this.currentY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                this.currentX = 40;
                this.currentY = 50;
                break;
            case MotionEvent.ACTION_MOVE:
                this.currentX = event.getX();
                this.currentY = event.getY();
                break;
        }
        //重绘小球
        this.invalidate();
        Log.i(">>>>", event.getAction() + "currentX,Y: " + this.currentX + "," + this.currentY);

        return true;
    }

}
