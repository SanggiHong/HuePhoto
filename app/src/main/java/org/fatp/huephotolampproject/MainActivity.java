package org.fatp.huephotolampproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;


/**
 * Created by HamHyunWoong on 2016-06-23.
 */

public class MainActivity extends Activity {
    private PHHueSDK phHueSDK;
    RotateAnimation rotate;
    AlphaAnimation alpha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton enterDetect = (ImageButton) findViewById(R.id.enter_color_detect);
        ImageButton enterColorDetectWithKeyword = (ImageButton) findViewById(R.id.enter_color_detect_with_keyword);
        ImageButton enterColorChange = (ImageButton) findViewById(R.id.enter_color_change);
        ImageButton enterPowerManage = (ImageButton) findViewById(R.id.enter_power_manage);

        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(700);
        rotate.setInterpolator(new DecelerateInterpolator());

        alpha = new AlphaAnimation(1f, 0.1f);
        alpha.setDuration(700);
        alpha.setInterpolator(new DecelerateInterpolator());

        enterDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(ColorDetectActivity.class, R.id.enter_color_detect);
            }
        });
        enterColorDetectWithKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(ColorDetectWithKeywordActivity.class, R.id.enter_color_detect_with_keyword);
            }
        });
        enterColorChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(ColorChangeActivity.class, R.id.enter_color_change);
            }
        });
        enterPowerManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(PowerManageActivity.class, R.id.enter_power_manage);
            }
        });

        Intent intent = new Intent(getApplicationContext(), BridgeSettingActivity.class);
        startActivity(intent);
    }

    private void enterActivity(final Class<? extends Activity> activityClass, int id) {
        phHueSDK = PHHueSDK.create();
        if(phHueSDK.getSelectedBridge() == null) {
            return;
        }
        AnimationSet aniSet = new AnimationSet(true);
        aniSet.addAnimation(rotate);
        aniSet.addAnimation(alpha);
        aniSet.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) { }
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getApplicationContext(), activityClass);
                startActivity(intent);
            }
            public void onAnimationRepeat(Animation animation) { }
        });
        ((ImageButton)findViewById(id)).startAnimation(aniSet);
    }
}