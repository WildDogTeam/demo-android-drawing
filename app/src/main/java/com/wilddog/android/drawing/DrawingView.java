package com.wilddog.android.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wilddog.client.ChildEventListener;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Wilddog;
import com.wilddog.client.WilddogError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wilddog.android.drawing.util.Colors;

public class DrawingView extends View {


    public static final String TAG = DrawingView.class.getName();
    public static final int PIXEL_SIZE = 8;

    private Paint mPaint;
    private float mLastX;
    private float mLastY;
    private Canvas mBuffer;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Wilddog mWilddogRef;
    private Path mPath;
    private float mScale = 8.0f;
    private int mCanvasWidth;
    private int mCanvasHeight;
    private final static ConcurrentHashMap<String, Object> unsavedPoints = new ConcurrentHashMap<>();


    public DrawingView(Context context, Wilddog ref) {
        this(context, ref, 8.0f);
    }

    public DrawingView(Context context, Wilddog ref, int width, int height) {
        this(context, ref);
        this.setBackgroundColor(Color.WHITE);
        mCanvasWidth = width;
        mCanvasHeight = height;
    }

    public DrawingView(Context context, Wilddog ref, float scale) {
        super(context);

        mPath = new Path();
        this.mWilddogRef = ref;
        this.mScale = scale;

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                System.out.println("New point->" + dataSnapshot.getKey() + " value->" + dataSnapshot.getValue());
                Map<String, Object> point = new HashMap<>();
                point.put(dataSnapshot.getKey(), dataSnapshot.getValue());
                drawPathForPoints(point);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // No-op
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                unsavedPoints.clear();
                clear();
                // No-op
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // No-op
            }

            @Override
            public void onCancelled(WilddogError WilddogError) {
                // No-op
            }
        });


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void clear() {
        mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mBuffer = new Canvas(mBitmap);
        invalidate();
    }

    public void removeAllPoints() {
        mWilddogRef.removeValue();
    }

    @Override
    public void addChildrenForAccessibility(ArrayList<View> outChildren) {
        super.addChildrenForAccessibility(outChildren);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mScale = Math.min(1.0f * w / mCanvasWidth, 1.0f * h / mCanvasHeight);
        mBitmap = Bitmap.createBitmap(Math.round(mCanvasWidth * mScale), Math.round(mCanvasHeight * mScale), Bitmap.Config.ARGB_8888);
        mBuffer = new Canvas(mBitmap);
        Log.i("AndroidDrawing", "onSizeChanged: created bitmap/buffer of " + mBitmap.getWidth() + "x" + mBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.DKGRAY);
        canvas.drawRect(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), paintFromColor(Color.WHITE, Paint.Style.FILL_AND_STROKE));
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }


    public static Paint paintFromColor(int color, Paint.Style style) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(color);
        p.setStyle(style);
        return p;
    }

    public void drawPathForPoints(Map<String, Object> points) {
        if (points == null) {
            return;
        }
        while (mBuffer == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, Object> p : points.entrySet()) {
            String[] xy = p.getKey().split(":");
            int x = Integer.valueOf(xy[0]) * PIXEL_SIZE;
            int y = Integer.valueOf(xy[1]) * PIXEL_SIZE;

            mPaint.setColor(Colors.use(p.getValue().toString()));
            mBuffer.drawRect(x, y, x + PIXEL_SIZE, y + PIXEL_SIZE, mPaint);
         invalidate();
        }
    }

    private void onTouchStart(float x, float y) {
        mLastX = x;
        mLastY = y;
    }

    private void onTouchMove(float x, float y) {
        lineTo(mLastX, mLastY, x, y);
    }

    private void lineTo(float originX, float originY, float x, float y) {

        float dx = x - originX;
        float dy = y - originY;
        if(dx < PIXEL_SIZE && dy < PIXEL_SIZE) {
            return;
        }
        float adx = Math.abs(dx);
        float ady = Math.abs(dy);
        float x1 = originX, y1 = originY;
        int ax,ay;
        mPaint.setColor(Colors.use(Colors.current()));
        while (adx >= 0 || ady >= 0) {
            if (adx >= 0) {
                x1 = dx >= 0 ? (x1 + PIXEL_SIZE) : (x1 - PIXEL_SIZE);
                adx -= PIXEL_SIZE;
            }
            if (ady >= 0) {
                y1 = dy >= 0 ? (y1 + PIXEL_SIZE) : (y1 - PIXEL_SIZE);
                ady -= PIXEL_SIZE;
            }
            ax = (int) (Math.min(originX, x1) / PIXEL_SIZE);
            ay = (int) (Math.min(originY, y1) / PIXEL_SIZE);
            unsavedPoints.put(ax + ":" + ay, Colors.current());
            mBuffer.drawRect(ax * PIXEL_SIZE, ay * PIXEL_SIZE, (ax + 1) * PIXEL_SIZE, (ay + 1) * PIXEL_SIZE, mPaint);
            invalidate();
            originY = y1;
            originX = x1;
        }
        mLastX = x;
        mLastY = y;
    }

    private void onTouchEnd() {
        mWilddogRef.updateChildren(unsavedPoints, new Wilddog.CompletionListener() {
            @Override
            public void onComplete(WilddogError wilddogError, Wilddog wilddog) {
                if (wilddogError != null) {
                    Log.d(TAG, "Save data failed with error:" + wilddogError.getMessage());
                } else {
                    Log.d(TAG, "All points have been saved.");
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                onTouchEnd();
                invalidate();
                break;
        }
        return true;
    }

}
