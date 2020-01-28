package com.paddy.chatheads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;
import com.nex3z.notificationbadge.NotificationBadge;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.paddy.chatheads.PermissionChecker.REQUIRED_PERMISSION_REQUEST_CODE;

public class ChatHeadActivity extends AppCompatActivity {
    CircleImageView imageView;
    ViewPager viewPager;
//    private BubblesManager bubblesManager;
    private NotificationBadge mBadge;

    private com.paddy.chatheads.PermissionChecker mPermissionChecker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_chat_head);


        imageView = findViewById(R.id.avatar2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBubble();
                finish();
//                Intent intent = new Intent(ChatHeadActivity.this, MainActivity.class);
//                intent.putExtra("show", "show");
//                startActivity(intent);
//                finish();
            }
        });
        mPermissionChecker = new PermissionChecker(ChatHeadActivity.this);
        if(!mPermissionChecker.isRequiredPermissionGranted()){
            Intent intent = mPermissionChecker.createRequiredPermissionIntent();
            startActivityForResult(intent, REQUIRED_PERMISSION_REQUEST_CODE);
        } else {
        }
        //Check permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(ChatHeadActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUIRED_PERMISSION_REQUEST_CODE);
            }
        } else {
        }


        // Find the view pager that will allow the user to swipe between fragments
        viewPager = findViewById(R.id.my2viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getApplicationContext(), getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.my2tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void initBubble() {
        startService(new Intent(ChatHeadActivity.this, HeadService.class));
    }

    private void addNewBubble() {
//        final BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(ChatHeadActivity.this)
//                .inflate(R.layout.bubble_layout, null);
//        mBadge = (NotificationBadge) bubbleView.findViewById(R.id.count);
//        mBadge.setNumber(88);
//
//        bubbleView.setShouldStickToWall(true);
//        bubblesManager.addBubble(bubbleView, 60, 20);

    }

    @Override
    public void onBackPressed() {
//        addNewBubble();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
