package com.paddy.chatheads;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HeadLayer extends View {

    private Context mContext;
    private FrameLayout mFrameLayout,mFrameLayout2;
    private WindowManager mWindowManager;
    String TAG ="HEAD LAYER";
    private static final int TOUCH_TIME_THRESHOLD = 150;
    private long lastTouchDown;
    WindowManager.LayoutParams params;
    private boolean magnetismApplied = false;
    private int width;
    private MoveAnimator animator;

    public HeadLayer(Context context) {
        super(context);

        mContext = context;
        mFrameLayout = new FrameLayout(mContext);
        mFrameLayout2 = new FrameLayout(mContext);

        addToWindowManager();
    }

    private void addToWindowManager() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        params.gravity =Gravity.TOP | Gravity.START;
        final WindowManager.LayoutParams params2,params3;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params2 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            params3 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params2 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params3 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        params2.gravity = Gravity.TOP | Gravity.START;


        params.x = params2.x;
        params.y = params2.y;

//        int[] location = new int[2];
//        params2.getLocationOnScreen(location);
//        int x = location[0];
//        int y = location[1];

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mFrameLayout2, params3);
        mWindowManager.addView(mFrameLayout, params);
        final int a = params.width;
        final int b = params.height;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Here is the place where you can inject whatever layout you want.
        layoutInflater.inflate(R.layout.head, mFrameLayout);
        layoutInflater.inflate(R.layout.bubble_remove, mFrameLayout2);

        // Support dragging the image view
        final ImageView imageView2 = (ImageView) mFrameLayout2.findViewById(R.id.closeme);
        final ImageView imageView = (ImageView) mFrameLayout.findViewById(R.id.imageView);
        final LinearLayout xx =  mFrameLayout.findViewById(R.id.xxxx);
        final View v2= mFrameLayout2.findViewById(R.id.hideme);
        mWindowManager.updateViewLayout(mFrameLayout2,params2);
        imageView.setOnTouchListener(new OnTouchListener() {
            private int initX, initY;
            private int initTouchX, initTouchY;
            @Override public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initX = params.x;
                        initY = params.y;
                        initTouchX = x;
                        initTouchY = y;
                        lastTouchDown = System.currentTimeMillis();
                        updateSize();
                        mWindowManager.updateViewLayout(mFrameLayout2,params3);
                        v2.setVisibility(VISIBLE);
                        return true;

                    case MotionEvent.ACTION_UP:
                        goToWall();
                        v2.setVisibility(GONE);
                        Log.e(TAG, "onTouch: "+event.getRawY()+" "+a+" "+b);
                        if (System.currentTimeMillis() - lastTouchDown < TOUCH_TIME_THRESHOLD) {
                            //TODO click event
                            Log.e(TAG, "onClick: " );
                            mWindowManager.removeView(mFrameLayout);
                            mWindowManager.removeView(mFrameLayout2);
                                Intent intent = new Intent(getContext(), ChatHeadActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(intent);
                        }
                        if (checkIfBubbleIsOverTrash()) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(TAG, "run: checking" );
                                    mWindowManager.removeView(mFrameLayout);
                                    mWindowManager.removeView(mFrameLayout2);
                                }
                            });

                        } else {
                        }

                        mWindowManager.updateViewLayout(mFrameLayout2,params2);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initX + (x - initTouchX);
                        params.y = initY + (y - initTouchY);
                        // Invalidate layout
                        mWindowManager.updateViewLayout(mFrameLayout, params);

                        return true;
                }
                return false;
            }
        });
    }


    public void goToWall() {
        animator = new MoveAnimator();
            int middle = width / 2;
            float nearestXWall = params.x >= middle ? width : 0;
            animator.start(nearestXWall, params.y);
        Log.e(TAG, "goToWall: "+params.y);

    }
    private void updateSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = (size.x - this.getWidth());
        Log.e(TAG, "updateSize: "+width);

    }
    private void move(float deltaX, float deltaY) {
        params.x += deltaX;
        params.y += deltaY;
        mWindowManager.updateViewLayout(mFrameLayout, params);
        Log.e(TAG, "move: "+deltaX+" "+deltaY );
    }
    private class MoveAnimator implements Runnable {
        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        private void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
//            if (getRootView() != null && getRootView().getParent() != null) {
                float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
                float deltaX = (destinationX -  params.x) * progress;
                float deltaY = (destinationY -  params.y) * progress;
                move(deltaX, deltaY);
                if (progress < 1) {
                    handler.post(this);
                }
                Log.e(TAG, "run1: "+destinationX);
//            }
            Log.e(TAG, "run: "+destinationX);
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }
    private boolean checkIfBubbleIsOverTrash() {
        View bubble=mFrameLayout;
        boolean result = false;
        if (mFrameLayout.getVisibility() == View.VISIBLE) {
            View trashContentView = mFrameLayout2.findViewById(R.id.closeme);
            int trashWidth = trashContentView.getMeasuredWidth();
            int trashHeight = trashContentView.getMeasuredHeight();
            int trashLeft = (trashContentView.getLeft() - (trashWidth / 2));
            int trashRight = (trashContentView.getLeft() + trashWidth + (trashWidth / 2));
            int trashTop = (trashContentView.getTop() - (trashHeight / 2));
            int trashBottom = (trashContentView.getTop() + trashHeight + (trashHeight / 2));
            int bubbleWidth = bubble.getMeasuredWidth();
            int bubbleHeight = bubble.getMeasuredHeight();
            int bubbleLeft = params.x;
            int bubbleRight = bubbleLeft + bubbleWidth;
            int bubbleTop = params.y;
            int bubbleBottom = bubbleTop + bubbleHeight;
            if (bubbleLeft >= trashLeft && bubbleRight <= trashRight) {
                if (bubbleTop >= trashTop && bubbleBottom <= trashBottom) {
                    result = true;
                }
            }
            Log.e(TAG, "checkIfBubbleIsOverTrash: cl"+trashLeft );
            Log.e(TAG, "checkIfBubbleIsOverTrash: cr"+trashRight );
            Log.e(TAG, "checkIfBubbleIsOverTrash: bl"+bubbleLeft );
            Log.e(TAG, "checkIfBubbleIsOverTrash: br"+bubbleRight );
        }
        return result;
    }
    private View getTrashContent() {
        return mFrameLayout2;
    }

    void applyMagnetism() {
        if (!magnetismApplied) {
            magnetismApplied = true;
            playAnimation(R.animator.bubble_trash_shown_magnetism_animator);
        }
    }

    void releaseMagnetism() {
        if (magnetismApplied) {
            magnetismApplied = false;
            playAnimation(R.animator.bubble_trash_hide_magnetism_animator);
        }
    }
    private void playAnimation(int animationResourceId) {
        if (!isInEditMode()) {
            AnimatorSet animator = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getContext(), animationResourceId);
            animator.setTarget(mFrameLayout2);
            animator.start();
        }
    }
    /**
     * Removes the view from window manager.
     */
    public void destroy() {
            mWindowManager.removeView(mFrameLayout);

    }
}

