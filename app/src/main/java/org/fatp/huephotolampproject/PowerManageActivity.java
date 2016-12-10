package org.fatp.huephotolampproject;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;


/**
 * Created by HamHyunWoong on 2016-06-23.
 */
public class PowerManageActivity extends Activity {

  boolean toggle = false;
  private PHHueSDK phHueSDK;
  private HueManager hueManager;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_power_manage);
    phHueSDK = PHHueSDK.create();
    final ImageView lampView = (ImageView) findViewById(R.id.lampimage);
    Button btn = (Button) findViewById(R.id.powerbutton);
    PHBridge bridge = phHueSDK.getSelectedBridge();
    List<PHLight> allLights = bridge.getResourceCache().getAllLights();
    for (PHLight light : allLights) {
      if(!light.getLastKnownLightState().isOn())
      {
        toggle = false;
        lampView.setVisibility(View.INVISIBLE);
        break;
      }
      toggle = true;
      lampView.setVisibility(View.VISIBLE);
    }
    btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (toggle == false) {
          toggle = true;
          lampView.setVisibility(View.VISIBLE);
          turnOnLights(toggle);
        } else {
          toggle = false;
          lampView.setVisibility(View.INVISIBLE);
          turnOnLights(toggle);
        }
      }
    });
  }

  protected void onDestroy() {
    PHBridge bridge = phHueSDK.getSelectedBridge();
    if (bridge != null) {
      if (phHueSDK.isHeartbeatEnabled(bridge)) {
        phHueSDK.disableHeartbeat(bridge);
      }
      phHueSDK.disconnect(bridge);
      super.onDestroy();
    }
  }

  private void turnOnLights(boolean isOn) {
    PHBridge bridge = phHueSDK.getSelectedBridge();

    List<PHLight> allLights = bridge.getResourceCache().getAllLights();
    PHLightState lightState = new PHLightState();
    lightState.setOn(isOn);
    bridge.setLightStateForDefaultGroup(lightState);
  }

  private Boolean isServiceRunning(String serviceName) {
    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceName.equals(runningServiceInfo.service.getClassName()))
        return true;
    }
    return false;
  }
}
