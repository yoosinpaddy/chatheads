package com.paddy.chatheads;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class HeadLayer extends View {

    private Context mContext;
    private FrameLayout mFrameLayout,mFrameLayout2;
    private WindowManager mWindowManager;
    String TAG ="HEAD LAYER";
    private static final int TOUCH_TIME_THRESHOLD = 150;
    private long lastTouchDown;
    WindowManager.LayoutParams params;
    private boolean magnetismApplied = false;

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
        final WindowManager.LayoutParams params2;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params2 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
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
        }
        params2.gravity = Gravity.TOP | Gravity.START;


        params.x = params2.x;
        params.y = params2.y;

//        int[] location = new int[2];
//        params2.getLocationOnScreen(location);
//        int x = location[0];
//        int y = location[1];

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mFrameLayout2, params2);
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
        final View v2= mFrameLayout2.findViewById(R.id.hideme);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " );
                Intent intent = new Intent(getContext(), MainActivity.class);
                ((Activity)getContext()).startActivity(intent);

            }
        });
        imageView.setOnTouchListener(new OnTouchListener() {
            private int initX, initY;
            private int initTouchX, initTouchY;
            @Override public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v2.setVisibility(VISIBLE);
                        initX = params.x;
                        initY = params.y;
                        initTouchX = x;
                        initTouchY = y;
                        lastTouchDown = System.currentTimeMillis();
                        return true;

                    case MotionEvent.ACTION_UP:
                        v2.setVisibility(GONE);
                        Log.e(TAG, "onTouch: "+event.getRawY()+" "+a+" "+b);
                        if (System.currentTimeMillis() - lastTouchDown < TOUCH_TIME_THRESHOLD) {

                            Log.e(TAG, "onClick: " );
    //                            Intent intent = new Intent(getContext(), MainActivity.class);
    //                            ((Activity)getContext()).startActivity(intent);
                        }
                        if (checkIfBubbleIsOverTrash()) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(TAG, "run: checking" );
//                                    mWindowManager.removeView(mFrameLayout);
//                                    mWindowManager.removeView(mFrameLayout2);
                                }
                            });

                        } else {
                        }
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

    private boolean checkIfBubbleIsOverTrash() {
        View bubble=mFrameLayout;
        boolean result = false;
        if (mFrameLayout.getVisibility() == View.VISIBLE) {
            View trashContentView = mFrameLayout2;
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

