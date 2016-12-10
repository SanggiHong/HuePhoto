package org.fatp.huephotolampproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;
import java.util.List;

import ml.Rgb;

public class PanoramaBackground extends Service {

  private PHHueSDK phHueSDK;
  private ArrayList<Rgb> rgb_list;
  private Thread thread;

  public PanoramaBackground() {
  }

  @Override
  public void onCreate() {
    super.onCreate();
    phHueSDK = PHHueSDK.create();
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    rgb_list = (ArrayList<Rgb>) intent.getSerializableExtra("rgbs");
    thread = new Thread() {
      public void run() {
        try {
          PHBridge bridge = phHueSDK.getSelectedBridge();
          List<PHLight> allLights = bridge.getResourceCache().getAllLights();
          int cnt = 0;
          while (true) {

            for (int i = 0; i < allLights.size(); i++) {
              boolean isFinish = false;
                PHLight light = allLights.get(i);
                Rgb rgb = rgb_list.get((i + cnt) % allLights.size());
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
            cnt++;
            if(cnt > 1000)
            {
              cnt = 0;
            }
            Thread.sleep(1000);
          }

        }catch(InterruptedException e)
        {

        }

      }
    };

    thread.start();
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    thread.interrupt();
  }
}
