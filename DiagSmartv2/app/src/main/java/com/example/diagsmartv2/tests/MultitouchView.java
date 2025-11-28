package com.example.diagsmartv2.tests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Random;

public class MultitouchView extends View {
    private final SparseArray<TouchPoint> activePointers = new SparseArray<>();
    private Paint paint;
    private Random random;
    private OnTouchCountChangeListener listener;

    public MultitouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        random = new Random();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < activePointers.size(); i++) {
            TouchPoint point = activePointers.valueAt(i);
            paint.setColor(point.color);
            canvas.drawCircle(point.x, point.y, 100, paint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                addNewTouchPoint(event, pointerId);
                updateCounter();
                break;

            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int id = event.getPointerId(i);
                    activePointers.get(id).x = event.getX(i);
                    activePointers.get(id).y = event.getY(i);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                // Remove finger
                activePointers.remove(pointerId);
                updateCounter();
                invalidate();
                break;
        }
        return true;
    }

    private void addNewTouchPoint(MotionEvent event, int pointerId) {
        int color = Color.argb(150,
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256));

        TouchPoint point = new TouchPoint(
                event.getX(event.findPointerIndex(pointerId)),
                event.getY(event.findPointerIndex(pointerId)),
                color
        );

        activePointers.put(pointerId, point);
    }

    private void updateCounter() {
        if (listener != null) {
            listener.onTouchCountChanged(activePointers.size());
        }
    }

    public void setOnTouchCountChangeListener(OnTouchCountChangeListener listener) {
        this.listener = listener;
    }

    private static class TouchPoint {
        float x;
        float y;
        int color;

        TouchPoint(float x, float y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    public interface OnTouchCountChangeListener {
        void onTouchCountChanged(int count);
    }
}

