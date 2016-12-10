package org.fatp.huephotolampproject;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ml.Kmeans;
import ml.Rgb;

import static android.content.Context.ACTIVITY_SERVICE;

public class KmeansTask extends AsyncTask<Void, Void, Void> {
    private Bitmap bitmap;
    private final int KMEANS_ITER = 10;
    private PHHueSDK phHueSDK;
    private ProgressDialog kmeansDialog;
    private Context context;
    private int mode;
    private  ArrayList<Rgb>  rgb_list_paranomal;




    public KmeansTask(Context context, PHHueSDK phHueSDK, Bitmap bitmap, int mode) {
        this.context=context;
        this.phHueSDK=phHueSDK;
        this.bitmap=bitmap;
        this.mode=mode;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Bitmap image_bitmap = this.bitmap;
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Kmeans kmean = new Kmeans(KMEANS_ITER, allLights.size(), image_bitmap);
        kmean.initCLusters();
        kmean.startKmeans();


        if(isNormal()) {
            Rgb[] rgb_list = kmean.getClusters();
            doNormalKmeans(bridge, allLights, rgb_list);
        }
        else{
            rgb_list_paranomal = new ArrayList<Rgb>(Arrays.asList(kmean.getClusters()));
        }
        return null;
    }

    private void doNormalKmeans(PHBridge bridge, List<PHLight> allLights, Rgb[] rgb_list) {
        for (int i = 0; i < allLights.size(); i++) {
            boolean isFinish = false;
            while (true) {
                if (isFinish)
                    break;

                PHLight light = allLights.get(i);
                Rgb rgb = rgb_list[i];
                PHLightState lightState = new PHLightState();
                float xy[] = PHUtilities.calculateXYFromRGB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), light.getModelNumber());
                lightState.setOn(true);
                lightState.setX(xy[0]);
                lightState.setY(xy[1]);
                bridge.updateLightState(light, lightState);

                if (light.getLastKnownLightState().getX() == xy[0] && light.getLastKnownLightState().getY() == xy[1]) {
                    Log.d("light", "okay");
                    isFinish = true;
                }
            }
        }
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(isNormal()) {
            Toast.makeText(context.getApplicationContext(), "처리 완료", Toast.LENGTH_SHORT).show();
            kmeansDialog.dismiss();
            kmeansDialog = null;
        }else {
            super.onPostExecute(aVoid);
            kmeansDialog.dismiss();
            kmeansDialog = null;
            if (isServiceRunning("org.fatp.huephotolampproject.PanoramaBackground")) {
                Intent intent = new Intent(context.getApplicationContext(), PanoramaBackground.class);
                context.stopService(intent);
            }
            Intent intent = new Intent(context.getApplicationContext(), PanoramaBackground.class);
            intent.putExtra("rgbs", rgb_list_paranomal);
            context.startService(intent);
            Toast.makeText(context.getApplicationContext(), "파노라마 모드 시작", Toast.LENGTH_SHORT).show();
        }
    }
    private Boolean isServiceRunning(String serviceName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        kmeansDialog = new ProgressDialog(context.getApplicationContext());
        kmeansDialog.setTitle("k-means 클러스터링");
        kmeansDialog.setMessage("처리 중 입니다");
        kmeansDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        kmeansDialog.show();
    }
    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private boolean isNormal() {
        return this.mode==1;
    }

}
