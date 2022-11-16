package com.example.purpulse.result;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.purpulse.Note;

public class TaijiView extends View {

    Paint paint;
    private Paint whitePaint;
    private Paint blackPaint;
    private Paint side;
    private float degrees = 0;

    public TaijiView(Context context) {
        super(context);
        init();
    }

    public TaijiView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        init();
    }

    public TaijiView(Context context, AttributeSet attributeSet, int defStyleAttr){
        super(context,attributeSet,defStyleAttr);
        init();
    }

    private void init(){
        whitePaint = new Paint();
        whitePaint.setAntiAlias(true);
        whitePaint.setColor(Color.WHITE);
        blackPaint = new Paint();
        blackPaint.setAntiAlias(true);
        blackPaint.setColor(Color.BLACK);
        side = new Paint();
        side.setColor(Color.BLACK);
        side.setStyle(Paint.Style.STROKE);
        side.setStrokeWidth(3);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Log.d("width",""+width);
        Log.d("height",""+height);

        canvas.translate(width/2,height/2);     //設定圖形起始點

        //canvas.drawColor(Color.GRAY);
        canvas.rotate(degrees);

        //半圆
        int SDNN = Note.sdNN.intValue();    //無條件捨去後面的小數
        int radius = SDNN*3/2;   //半徑，SDNN正常值約等於20~45，於是設直徑為SDNN*10
        if (radius > 50){       //如果半徑大於50則設為50
            radius = 50;
        }
        Log.d("radius",""+radius);
        RectF rectF = new RectF(-radius, -radius, radius, radius);  //定義圓弧大小範圍、形狀(x,y,getwidth()-x,getheight()-y)
        Log.d("rectF",""+rectF);
        canvas.drawArc(rectF,90,180,true,blackPaint); //(定義圓弧大小範圍、形狀,设置圆弧是从哪个角度来顺时针绘画,圆弧在绘画的时候，是否連接圆心,筆畫屬性)
        canvas.drawArc(rectF,-90,180,true,whitePaint);

        //两个小圆
        double LF = Note.LFn;
        double HF = Note.HFn;
        double blackcircle = LF*radius;
        double whitecircle = HF*radius;
        int BC = (int)Math.round(blackcircle);
        int WC = (int)Math.round(whitecircle);
//        int innerRadius = radius / 2;
        canvas.drawCircle(0,-radius+BC,BC,blackPaint);  //(起點X,起點Y,半徑,筆畫)
        canvas.drawCircle(0,radius-WC,WC,whitePaint);

        Path path = new Path();
        path.moveTo(-radius,-radius);
        path.lineTo(radius,-radius);
        path.lineTo(radius,radius);
        path.lineTo(-radius,radius);
        path.close();
        canvas.drawCircle(0,0,radius,side);


//        //鱼眼
//        canvas.drawCircle(0, -innerRadius, innerRadius / 4, whitePaint);
//        canvas.drawCircle(0, innerRadius, innerRadius / 4, blackPaint);

    }
}
