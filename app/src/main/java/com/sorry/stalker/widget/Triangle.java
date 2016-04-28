package com.sorry.stalker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.view.View;

/**
 * Created by sorry on 2016/4/24.
 */
public class Triangle extends View {
    public Triangle(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

		/*设置背景为白色*/
        canvas.drawColor(Color.TRANSPARENT);
        Paint paint=new Paint();
		/*去锯齿*/
        paint.setAntiAlias(true);
		/*设置paint　的style为　FILL：实心*/
        paint.setStyle(Paint.Style.FILL);
		/*设置paint的颜色*/
        paint.setColor(Color.GRAY);

		/*画一个实心三角形*/
        Path path2=new Path();
        path2.moveTo(0,0);
        path2.lineTo(0,50);
        path2.lineTo(50,0);
        path2.close();
        canvas.drawPath(path2, paint);

    }
}
