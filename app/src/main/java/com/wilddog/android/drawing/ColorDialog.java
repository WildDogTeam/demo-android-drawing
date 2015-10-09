package com.wilddog.android.drawing;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Map;

import com.android.wilddog.com.drawing.R;
import com.wilddog.android.drawing.util.Colors;

/**
 * 颜色选取
 * Created by jale on 15/10/9.
 */
public class ColorDialog extends Dialog {

    private final static String TAG = ColorDialog.class.getName();

    public ColorDialog(Context context) {
        super(context, R.style.ColorDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ColorView(getContext()));
    }

    private void close() {
        this.cancel();
    }

    private class ColorView extends View {
        private Bitmap mBitmap;
        private Paint p;
        private String[] colorKey = new String[16];

        public ColorView(Context context) {
            super(context);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldW, int oldH) {
            super.onSizeChanged(w, h, oldW, oldH);
            mBitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
            p = new Paint();
            p.setAntiAlias(true);
            p.setDither(true);
            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(800, 800);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.DKGRAY);
            canvas.drawRect(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), p);
            canvas.drawBitmap(mBitmap, 0, 0, p);
            int i = 0, left, top, x, y, pos;
            for (Map.Entry<String, Integer> entry : Colors.all()) {
                x = i % 4;
                y = i / 4;
                pos = x + y * 4;
                left = x * 200;
                top = y * 200;
                colorKey[pos] = entry.getKey();
                Log.d(TAG, "Put color " + entry.getKey() + " at " + pos);
                p.setColor(entry.getValue());
                canvas.drawRect(left, top, left + 200, top + 200, p);
                i++;
            }
        }

        @Override
        public boolean onTouchEvent(@NonNull MotionEvent event) {
            int pos = ((int) (event.getX() / 200)) + ((int) (event.getY() / 200) * 4);
            Log.d(TAG, "User choose color ->" + pos + "," + colorKey[pos]);
            Colors.use(colorKey[pos]);
            close();
            return true;
        }
    }
}
