package org.fatp.huephotolampproject;

import static android.R.id.toggle;

import android.util.Log;
import android.view.View;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;

/**
 * Created by corona10 on 2016. 12. 10..
 */
import ml.Rgb;
public class HueManager {

  private PHHueSDK phHueSDK;
  private static HueManager _instance;

  private HueManager()
  {
    this.phHueSDK = PHHueSDK.create();
  }

  public static HueManager create(){
    return new HueManager();
  }

  public void powerOn()
  {
    PHBridge bridge = phHueSDK.getSelectedBridge();
    List<PHLight> allLights = bridge.getResourceCache().getAllLights();
    PHLightState lightState = new PHLightState();
    lightState.setOn(true);
    bridge.setLightStateForDefaultGroup(lightState);
  }

  public void powerOff()
  {
    PHBridge bridge = phHueSDK.getSelectedBridge();
    List<PHLight> allLights = bridge.getResourceCache().getAllLights();
    PHLightState lightState = new PHLightState();
    lightState.setOn(false);
    bridge.setLightStateForDefaultGroup(lightState);
  }

  public List<PHLight> getLights()
  {
    PHBridge bridge = phHueSDK.getSelectedBridge();
    List<PHLight> allLights = bridge.getResourceCache().getAllLights();
    return allLights;
  }

  public void requestColorChange(List<Rgb> rgbs)
  {
    if(rgbs.size() == 1) {
      PHBridge bridge = phHueSDK.getSelectedBridge();
      List<PHLight> allLights = bridge.getResourceCache().getAllLights();
      Rgb rgb = rgbs.get(0);
      if (allLights.size() > 0) {
        PHLightState lightState = new PHLightState();
        float xy[] = PHUtilities.calculateXYFromRGB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), allLights.get(0).getModelNumber());
        lightState.setOn(true);
        lightState.setX(xy[0]);
        lightState.setY(xy[1]);
        bridge.setLightStateForDefaultGroup(lightState);
      }
    }else {
      PHBridge bridge = phHueSDK.getSelectedBridge();
      List<PHLight> allLights = getAllLights();
      for (int i = 0; i < allLights.size(); i++) {
        boolean isFinish = false;
        PHLight light = allLights.get(i);
        Rgb rgb = rgbs.get(i);
        PHLightState lightState = new PHLightState();
        float xy[] = PHUtilities.calculateXYFromRGB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), light.getModelNumber());
        lightState.setOn(true);
        lightState.setX(xy[0]);
        lightState.setY(xy[1]);

        bridge.updateLightState(light, lightState);
        light = allLights.get(i);
        if(light.getLastKnownLightState().getX()== xy[0] && light.getLastKnownLightState().getY() == xy[1])
        {
          Log.d("light", "okay");
          isFinish = true;
        }
      }
    }
  }

  public List<PHLight> getAllLights()
  {
    PHBridge bridge = phHueSDK.getSelectedBridge();
    List<PHLight> allLights = bridge.getResourceCache().getAllLights();
    return allLights;
  }

  public boolean requestPowerState(PHLight light)
  {
    return light.getLastKnownLightState().isOn();
  }


  public void requestConnect(String ip, String used_id)
  {
    PHAccessPoint lastAccessPoint = new PHAccessPoint();
    lastAccessPoint.setIpAddress(ip);
    lastAccessPoint.setUsername(used_id);
    phHueSDK.connect(lastAccessPoint);
  }

  public void setAppName(String appName)
  {
    phHueSDK.setAppName(appName);
  }

  public void release()
  {
    PHBridge bridge = phHueSDK.getSelectedBridge();
    if (bridge != null) {
      if (phHueSDK.isHeartbeatEnabled(bridge)) {
        phHueSDK.disableHeartbeat(bridge);
      }
      phHueSDK.disconnect(bridge);
    }
  }

  public List<PHAccessPoint> getAccessPointList()
  {
    return this.phHueSDK.getAccessPointsFound();
  }

  public void registerListener(PHSDKListener listener)
  {
    this.phHueSDK.getNotificationManager().registerSDKListener(listener);
  }

  public PHHueSDK getPhHueSDK()
  {
    return this.phHueSDK;
  }

  public  void setPhHueSDK(PHHueSDK sdk)
  {
    this.phHueSDK = sdk;
  }

}
