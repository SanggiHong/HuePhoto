package org.fatp.huephotolampproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;

/**
 * Created by HamHyunWoong on 2016-06-23.
 */

public class MainActivity extends Activity {

    public static final String TAG = "QuickStart";
    private PHHueSDK phHueSDK;
    private boolean lastSearchWasIPScan = false;
    private String ipAddress = "none";
    private String userName = "newdeveloper";
    private PHAccessPoint lastAccessPoint;
    private HueSharedPreferences prefs;
    private AccessPointListAdapter adapter;
    RotateAnimation rotate;
    ScaleAnimation scale;
    AlphaAnimation alpha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ImageButton button1 = (ImageButton) findViewById(R.id.enter_color_detect);
        ImageButton button2 = (ImageButton) findViewById(R.id.enter_color_detect_with_keyword);
        ImageButton button3 = (ImageButton) findViewById(R.id.enter_color_change);
        ImageButton button4 = (ImageButton) findViewById(R.id.enter_power_manage);

        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(700);
        rotate.setInterpolator(new DecelerateInterpolator());

        alpha = new AlphaAnimation(1f, 0.1f);
        alpha.setDuration(700);
        alpha.setInterpolator(new DecelerateInterpolator());


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationSet aniSet = new AnimationSet(true);
                aniSet.addAnimation(rotate);
                aniSet.addAnimation(alpha);
                aniSet.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        // Auto-generated method stub
                    }
                    public void onAnimationEnd(Animation animation) {
                        // Auto-generated method stub
                        Intent intent = new Intent(getApplicationContext(), ColorDetectActivity.class);
                        startActivity(intent);
                    }
                    public void onAnimationRepeat(Animation animation) {
                        // Auto-generated method stub
                    }
                });
                ((ImageButton)findViewById(R.id.enter_color_detect)).startAnimation(aniSet);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationSet aniSet = new AnimationSet(true);
                aniSet.addAnimation(rotate);
                aniSet.addAnimation(alpha);
                aniSet.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        // Auto-generated method stub
                    }
                    public void onAnimationEnd(Animation animation) {
                        // Auto-generated method stub
                        Intent intent = new Intent(getApplicationContext(), ColorDetectWithKeywordActivity.class);
                        startActivity(intent);
                    }
                    public void onAnimationRepeat(Animation animation) {
                        // Auto-generated method stub
                    }
                });
                ((ImageButton)findViewById(R.id.enter_color_detect_with_keyword)).startAnimation(aniSet);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationSet aniSet = new AnimationSet(true);
                aniSet.addAnimation(rotate);
                aniSet.addAnimation(alpha);
                aniSet.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        // Auto-generated method stub
                    }
                    public void onAnimationEnd(Animation animation) {
                        // Auto-generated method stub
                        Intent intent = new Intent(getApplicationContext(), ColorChangeActivity.class);
                        startActivity(intent);
                    }
                    public void onAnimationRepeat(Animation animation) {
                        // Auto-generated method stub
                    }
                });
                ((ImageButton)findViewById(R.id.enter_color_change)).startAnimation(aniSet);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationSet aniSet = new AnimationSet(true);
                aniSet.addAnimation(rotate);
                aniSet.addAnimation(alpha);
                aniSet.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        // Auto-generated method stub
                    }
                    public void onAnimationEnd(Animation animation) {
                        // Auto-generated method stub
                        Intent intent = new Intent(getApplicationContext(), PowerManageActivity.class);
                        startActivity(intent);
                    }
                    public void onAnimationRepeat(Animation animation) {
                        // Auto-generated method stub
                    }
                });
                ((ImageButton)findViewById(R.id.enter_power_manage)).startAnimation(aniSet);
            }
        });



    }

}