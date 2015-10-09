package com.wilddog.android.drawing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.android.wilddog.com.drawing.R;
import com.wilddog.client.Wilddog;

import com.wilddog.android.drawing.util.SystemUiHider;

/**
 * 这是一个从firebase迁移过来的示例
 *
 * @see SystemUiHider
 */
public class DrawingActivity extends Activity {
    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final boolean TOGGLE_ON_CLICK = true;

    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private SystemUiHider mSystemUiHider;

    private DrawingView mDrawingView;

    private final static String DRAWING_URL = "https://drawing.wilddogio.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity context = this;
        super.onCreate(savedInstanceState);
        Wilddog.setAndroidContext(this);

        setContentView(R.layout.activity_fullscreen);

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.x);
        frameLayout.setFocusable(true);


        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        int mBoardWidth = 460 * 2;
        int mBoardHeight = 480 * 2;

        Wilddog mRef = new Wilddog(DRAWING_URL);
        mDrawingView = new DrawingView(DrawingActivity.this, mRef, mBoardWidth, mBoardHeight);
//        mDrawingView.setForegroundGravity(Gravity.CENTER);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mBoardWidth, mBoardHeight, Gravity.CENTER);

        frameLayout.addView(mDrawingView, layoutParams);

        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible) {
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        mDrawingView.setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.color_button).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.dummy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.clear();
                mDrawingView.removeAllPoints();
            }
        });
        findViewById(R.id.color_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorDialog(context).show();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
