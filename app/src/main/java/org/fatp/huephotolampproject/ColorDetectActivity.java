package org.fatp.huephotolampproject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ml.Kmeans;
import ml.Rgb;

/**
 * Created by HamHyunWoong on 2016-06-23.
 */
public class ColorDetectActivity extends Activity {

  final int REQ_CODE_SELECT_IMAGE = 100;
  final int KMEANS_ITER = 10;
  private PHHueSDK phHueSDK;
  private Bitmap selected_bitmap = null;
  private ProgressDialog dialog;
  private PanoramaBackground background_service;
  private Button normalmode;
  private Button panormamode;
  RotateAnimation rotate;
  ScaleAnimation scale;
  AlphaAnimation alpha;

  @Override
  protected void onStop() {
    super.onStop();
    if(isServiceRunning("org.fatp.huephotolampproject.PanoramaBackground"))
    {
      Intent intent = new Intent(ColorDetectActivity.this, PanoramaBackground.class);
      stopService(intent);
    }
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_color_detect);
    phHueSDK = PHHueSDK.create();

    rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    rotate.setDuration(700);
    rotate.setInterpolator(new DecelerateInterpolator());

    alpha = new AlphaAnimation(1f, 0.1f);
    alpha.setDuration(700);
    alpha.setInterpolator(new DecelerateInterpolator());

    Button btn = (Button) findViewById(R.id.selectimagebtn);

    normalmode = (Button) findViewById(R.id.okbutton01);
    panormamode = (Button) findViewById(R.id.okbutton01_sec);

    normalmode.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        AnimationSet aniSet = new AnimationSet(true);
        aniSet.addAnimation(rotate);
        aniSet.addAnimation(alpha);
        ((Button)findViewById(R.id.okbutton01)).startAnimation(aniSet);

        if (selected_bitmap != null) {
          KnnTask task = new KnnTask();
          task.execute();
        }

      }
    });

    panormamode.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        AnimationSet aniSet = new AnimationSet(true);
        aniSet.addAnimation(rotate);
        aniSet.addAnimation(alpha);
        ((Button)findViewById(R.id.okbutton01_sec)).startAnimation(aniSet);
        if (selected_bitmap != null) {
          KnnTask2 task = new KnnTask2();
          task.execute();
        }

      }
    });

    btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQ_CODE_SELECT_IMAGE) {
      if (resultCode == Activity.RESULT_OK) {
        try {
          //Uri에서 이미지 이름을 얻어온다.
          //String name_Str = getImageNameToUri(data.getData());

          //이미지 데이터를 비트맵으로 받아온다.
          Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
          ImageView image = (ImageView) findViewById(R.id.imageview1);
          //배치해놓은 ImageView에 set
          image.setImageBitmap(image_bitmap);
          this.selected_bitmap = scaleBitmapDown(image_bitmap,1200);
          //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

    int originalWidth = bitmap.getWidth();
    int originalHeight = bitmap.getHeight();
    int resizedWidth = maxDimension;
    int resizedHeight = maxDimension;

    if (originalHeight > originalWidth) {
      resizedHeight = maxDimension;
      resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
    } else if (originalWidth > originalHeight) {
      resizedWidth = maxDimension;
      resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
    } else if (originalHeight == originalWidth) {
      resizedHeight = maxDimension;
      resizedWidth = maxDimension;
    }
    return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
  }

  private class KnnTask extends AsyncTask<Void, Void, Void> {


    @Override
    protected Void doInBackground(Void... voids) {
      Bitmap image_bitmap = selected_bitmap;
      PHBridge bridge = phHueSDK.getSelectedBridge();

      List<PHLight> allLights = bridge.getResourceCache().getAllLights();
      Kmeans kmean = new Kmeans(KMEANS_ITER, allLights.size(), image_bitmap);
      kmean.initCLusters();
      kmean.startKmeans();
      Rgb[] rgb_list = kmean.getClusters();

      for (int i = 0; i < allLights.size(); i++) {
        boolean isFinish = false;
        while(true) {
          if(isFinish)
            break;

          PHLight light = allLights.get(i);
          Rgb rgb = rgb_list[i];
          PHLightState lightState = new PHLightState();
          float xy[] = PHUtilities.calculateXYFromRGB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), light.getModelNumber());
          lightState.setOn(true);
          lightState.setX(xy[0]);
          lightState.setY(xy[1]);
          bridge.updateLightState(light, lightState);

          if(light.getLastKnownLightState().getX()== xy[0] && light.getLastKnownLightState().getY() == xy[1])
          {
            Log.d("light", "okay");
            isFinish = true;
          }
        }
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      dialog.dismiss();
      dialog = null;
      Toast.makeText(ColorDetectActivity.this, "처리 완료", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      dialog = new ProgressDialog(ColorDetectActivity.this);
      dialog.setTitle("k-means 클러스터링");
      dialog.setMessage("처리 중 입니다");
      dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      dialog.show();
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
  }

  private class KnnTask2 extends AsyncTask<Void, Void, Void> {
    private ArrayList<Rgb> rgb_list;
    @Override
    protected Void doInBackground(Void... voids) {
      Bitmap image_bitmap = selected_bitmap;
      PHBridge bridge = phHueSDK.getSelectedBridge();

      List<PHLight> allLights = bridge.getResourceCache().getAllLights();
      Kmeans kmean = new Kmeans(KMEANS_ITER, allLights.size(), image_bitmap);
      kmean.initCLusters();
      kmean.startKmeans();
      rgb_list = new ArrayList<Rgb>(Arrays.asList(kmean.getClusters()));
      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      dialog.dismiss();
      dialog = null;
      if(isServiceRunning("org.fatp.huephotolampproject.PanoramaBackground"))
      {
        Intent intent = new Intent(ColorDetectActivity.this, PanoramaBackground.class);
        stopService(intent);
      }
      Intent intent = new Intent(ColorDetectActivity.this, PanoramaBackground.class);
      intent.putExtra("rgbs", rgb_list);
      startService(intent);
      Toast.makeText(ColorDetectActivity.this, "파노라마 모드 시작", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      dialog = new ProgressDialog(ColorDetectActivity.this);
      dialog.setTitle("k-means 클러스터링");
      dialog.setMessage("처리 중 입니다");
      dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      dialog.show();
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
  }
  private Boolean isServiceRunning(String serviceName) {
    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {

      if (serviceName.equals(runningServiceInfo.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

}
