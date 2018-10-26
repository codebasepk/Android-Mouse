package com.byteshaft.android_mouse;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {


    private View touchBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        touchBoard = (View) findViewById(R.id.touch_board);
        touchBoard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onScreenTouch(event);
                return true;
            }
        });
    }

    private long mDownTime;
    private float mDownX;
    private float mDownY;

    private long mUpTime;
    private float mUpX;
    private float mUpY;

    private float mLastMoveX = Float.MAX_VALUE;
    private float mLastMoveY = Float.MAX_VALUE;

    private float mCurMoveX;
    private float mCurMoveY;
    private long mLastMoveTime;


    private void onScreenTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onSingClick(event, true);
                break;
            case MotionEvent.ACTION_UP:
                onSingClick(event, false);
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(event);
                break;

        }
    }

    private void onMove(MotionEvent event) {
        int distanceX = 0;
        int distanceY = 0;

        mCurMoveX = event.getX();
        mCurMoveY = event.getY();


        if (mLastMoveX != Float.MAX_VALUE && mLastMoveY != Float.MAX_VALUE) {
            distanceX = (int) (mCurMoveX - mDownX);
            distanceY = (int) (mCurMoveY - mDownY);
        }

        int distance = (int) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        // send a move command per 0.5 s
        if (distance > 100 || System.currentTimeMillis() - mLastMoveTime > 100) {
            Log.i("TAG", "X " + distanceX + "  Y "+ distanceY);
            mLastMoveX = mCurMoveX;
            mLastMoveY = mCurMoveY;
            mLastMoveTime = System.currentTimeMillis();
        }
    }

    private void onSingClick(MotionEvent event, boolean down) {
        if (down) {
            mDownTime = System.currentTimeMillis();
            mDownX = event.getX();
            mDownY = event.getY();
        } else {
            mUpTime = System.currentTimeMillis();
            mUpX = event.getX();
            mUpY = event.getY();
        }


    }
}

