package com.example.diagsmartv2.tests.interactive;

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

    /**
     * Initializes the Paint object with anti‑aliasing and fill style,
     * and creates a Random generator for touch point colors.
     */
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        random = new Random();
    }

    /**
     * Draws a colored circle for each active touch point using the
     * stored TouchPoint objects and their associated colors.
     *
     * @param canvas Canvas onto which the touch points are drawn.
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < activePointers.size(); i++) {
            TouchPoint point = activePointers.valueAt(i);
            paint.setColor(point.color);
            canvas.drawCircle(point.x, point.y, 100, paint);
        }
    }

    /**
     * Handles all touch events: adds new touch points on DOWN events,
     * updates positions on MOVE events, removes points on UP/CANCEL events,
     * and notifies the listener of count changes.
     *
     * @param event MotionEvent describing the touch gesture.
     * @return true to indicate all touch events are consumed.
     */
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

    /**
     * Creates a new TouchPoint with a semi‑transparent random color
     * at the given pointer's current position and adds it to activePointers.
     *
     * @param event     MotionEvent containing pointer coordinates.
     * @param pointerId unique ID of the new touch pointer.
     */
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

    /**
     * Notifies the registered OnTouchCountChangeListener of the current
     * number of active touch points.
     */
    private void updateCounter() {
        if (listener != null) {
            listener.onTouchCountChanged(activePointers.size());
        }
    }

    /**
     * Registers a listener to receive callbacks when the number of
     * simultaneous touch points changes.
     *
     * @param listener callback interface for touch count changes.
     */
    public void setOnTouchCountChangeListener(OnTouchCountChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Inner class representing a single touch point with position and color.
     */
    private static class TouchPoint {
        float x;
        float y;
        int color;

        /**
         * Creates a TouchPoint at the given coordinates with the specified color.
         *
         * @param x     X coordinate of the touch point.
         * @param y     Y coordinate of the touch point.
         * @param color ARGB color for the touch point circle.
         */
        TouchPoint(float x, float y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    /**
     * Callback interface for listeners interested in multitouch count changes.
     */
    public interface OnTouchCountChangeListener {
        void onTouchCountChanged(int count);
    }
}

