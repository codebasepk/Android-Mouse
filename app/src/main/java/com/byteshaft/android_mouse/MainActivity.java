package com.byteshaft.android_mouse;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;

public class MainActivity extends Activity {

    private View touchBoard;
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

    private Session mWAMPSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mWAMPSession = new Session();
        mWAMPSession.addOnJoinListener((session, details) -> System.out.println("HELLO"));
        Client client = new Client(mWAMPSession, "ws://192.168.100.6:5020/ws", "realm1");
        client.connect().whenComplete((exitInfo, throwable) -> {});
        init();
    }

    private void init() {
        touchBoard = findViewById(R.id.touch_board);
        touchBoard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onScreenTouch(event);
                return true;
            }
        });
    }

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

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        float percentX = ((float) distanceX / point.x) * 100;
        float percentY = ((float) distanceY / point.y) * 100;
        mWAMPSession.call("io.crossbar.move_mouse", percentX, percentY);
        mLastMoveX = mCurMoveX;
        mLastMoveY = mCurMoveY;
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
