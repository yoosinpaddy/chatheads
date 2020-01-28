package com.paddy.chatheads;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
    private static final String TAG = "ChatHeadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_head);
        checkPermission();

        imageView = findViewById(R.id.avatar2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBubble();
                finish();
            }
        });

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

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(ChatHeadActivity.this)) {
                final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                Toast.makeText(this, "Please enable permission", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, REQUIRED_PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.d(TAG, "checkPermission: granted");
        }
    }

    public void initBubble() {
        startService(new Intent(ChatHeadActivity.this, HeadService.class));
    }

    @Override
    public void onBackPressed() {
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
