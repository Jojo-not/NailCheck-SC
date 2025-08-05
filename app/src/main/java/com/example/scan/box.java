package com.example.scan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class box  extends View {
    private Paint paint;
    private float left, top, right, bottom;

    public box(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFF00FF00);
        paint.setStyle(Paint.Style.STROKE); // Outline only
        paint.setStrokeWidth(5f); // Thickness of the box
    }

    public void setBoxCoordinates(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        invalidate(); // Redraw the view with updated coordinates
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the box
        canvas.drawRect(left, top, right, bottom, paint);
    }
}
