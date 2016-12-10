package org.fatp.huephotolampproject;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import android.widget.TextView;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;
import ml.Kmeans;
import ml.Rgb;


public class ColorDetectWithKeywordActivity extends Activity {

  private static final String CLOUD_VISION_API_KEY = "AIzaSyDRKw0iBD2rfFic_HqUUQZKY-tBLfdc87E";
  private static final String TAG = "ColorDetectWithKeywordActivity";
  private PHHueSDK phHueSDK;
  private Bitmap selected_bitmap = null;

  private final int REQ_CODE_SELECT_IMAGE = 100;
  private RotateAnimation rotate;
  private ScaleAnimation scale;
  private AlphaAnimation alpha;
  private final int NORMAL=1;
  private final int PARANOMA=2;
  private Button normalmode2;
  private Button panormamode2;
  private String keyword;

  private ProgressDialog dialog;
  private ProgressDialog bingDialog;
  private ProgressDialog cloudDialog;
  private TextView keywordView;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_color_detect_with_keyword);

    rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    rotate.setDuration(700);
    rotate.setInterpolator(new DecelerateInterpolator());

    alpha = new AlphaAnimation(1f, 0.1f);
    alpha.setDuration(700);
    alpha.setInterpolator(new DecelerateInterpolator());


    normalmode2 = (Button) findViewById(R.id.okbutton02);
    panormamode2 = (Button) findViewById(R.id.okbutton02_sec);
    keywordView = (TextView) findViewById(R.id.keyword);
    phHueSDK = PHHueSDK.create();
    normalmode2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        AnimationSet aniSet = new AnimationSet(true);
        aniSet.addAnimation(rotate);
        aniSet.addAnimation(alpha);
        ((Button) findViewById(R.id.okbutton02)).startAnimation(aniSet);
        try {
          callCloudVision(selected_bitmap, NORMAL);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    panormamode2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (selected_bitmap != null) {
          AnimationSet aniSet = new AnimationSet(true);
          aniSet.addAnimation(rotate);
          aniSet.addAnimation(alpha);
          ((Button) findViewById(R.id.okbutton02_sec)).startAnimation(aniSet);
          try {
            callCloudVision(selected_bitmap, PARANOMA);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

      }
    });

    Button btn = (Button) findViewById(R.id.selectimagebtn2);
    btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

      }
    });

  }

  protected void onStop() {
    super.onStop();
    if (isServiceRunning("org.fatp.huephotolampproject.PanoramaBackground")) {
      Intent intent = new Intent(ColorDetectWithKeywordActivity.this, PanoramaBackground.class);
      stopService(intent);
    }
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


  public class KmeansTask extends AsyncTask<Void, Void, Void> {
    private Bitmap bitmap;
    private final int KMEANS_ITER = 10;
    private PHHueSDK phHueSDK;
    private ProgressDialog kmeansDialog;
    private int mode;
    private  ArrayList<Rgb>  rgb_list_paranomal;


    private KmeansTask(PHHueSDK phHueSDK,Bitmap bitmap, int mode) {
      this.phHueSDK=phHueSDK;
      this.bitmap=bitmap;
      this.mode=mode;
    }

    @Override
    protected Void doInBackground(Void... params) {
      Bitmap image_bitmap = this.bitmap;
      PHBridge bridge = phHueSDK.getSelectedBridge();
      List<PHLight> allLights = bridge.getResourceCache().getAllLights();
      colorDetect(image_bitmap, bridge, allLights);
      return null;
    }

    private void colorDetect(Bitmap image_bitmap, PHBridge bridge, List<PHLight> allLights) {
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
        Toast.makeText(ColorDetectWithKeywordActivity.this, "처리 완료", Toast.LENGTH_SHORT).show();
        kmeansDialog.dismiss();
        kmeansDialog = null;
      }else {
        super.onPostExecute(aVoid);
        kmeansDialog.dismiss();
        kmeansDialog = null;
        if (isServiceRunning("org.fatp.huephotolampproject.PanoramaBackground")) {
          Intent intent = new Intent(ColorDetectWithKeywordActivity.this, PanoramaBackground.class);
          stopService(intent);
        }
        Intent intent = new Intent(ColorDetectWithKeywordActivity.this, PanoramaBackground.class);
        intent.putExtra("rgbs", rgb_list_paranomal);
        startService(intent);
        Toast.makeText(ColorDetectWithKeywordActivity.this, "파노라마 모드 시작", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      kmeansDialog = new ProgressDialog(ColorDetectWithKeywordActivity.this);
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //Toast.makeText(getBaseContext(), "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();
    if (requestCode == REQ_CODE_SELECT_IMAGE) {
      if (resultCode == Activity.RESULT_OK) {
        try {
          //Uri에서 이미지 이름을 얻어온다.
          //String name_Str = getImageNameToUri(data.getData());

          //이미지 데이터를 비트맵으로 받아온다.
          Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
          ImageView image = (ImageView) findViewById(R.id.imageview2);

          //배치해놓은 ImageView에 set
          image.setImageBitmap(image_bitmap);
          this.selected_bitmap = scaleBitmapDown(image_bitmap, 1200);
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
  private AsyncTask SearchPhotos(final String keyword, final int mode) throws IOException {
    return new AsyncTask<Void, Void, Bitmap>() {
      private String APILink = "https://api.datamarket.azure.com/Bing/Search/v1/"; //Query=%27girl%27&Market=%27en-us%27&ImageFilters=%27Size%3ASmall%27&format=json&stop=1
      private String API_KEY = "FEqHAQ/dk0+ex9gY0Lk0C76J0aLw6G6OVhUgmI3im1Y";
      private String[] SECTION = {"Image"};

      @Override
      protected Bitmap doInBackground(Void... params) {
        String result = "";
        HttpClient httpClient = new DefaultHttpClient();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("Query", "'" + keyword + "'"));
        nameValuePairs.add(new BasicNameValuePair("Market", "'en-US'"));
        nameValuePairs.add(new BasicNameValuePair("Adult", "'Moderate'"));
        nameValuePairs.add(new BasicNameValuePair("ImageFilters", "'Size:Small'"));

        String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
        HttpGet httpget = new HttpGet(APILink + SECTION[0] + "?" + paramsString + "&$format=json&$top=1");
        String auth = API_KEY + ":" + API_KEY;
        String encodedAuth = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
        Log.e("", encodedAuth);
        httpget.addHeader("Authorization", "Basic " + encodedAuth);

        HttpResponse response = null;
        try {
          response = httpClient.execute(httpget);
        } catch (IOException e1) {
          e1.printStackTrace();
        }

        HttpEntity entity = response.getEntity();
        if (entity != null) {
          InputStream inputStream = null;
          try {
            inputStream = entity.getContent();
          } catch (IllegalStateException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
          BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
          String line;
          try {
            while ((line = bufferedReader.readLine()) != null) {
              result += line;
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        //Extract link from JSON
        //String to Json
        JSONObject jsonObject = null;
        if (JSONValue.isValidJson(result)) {
          jsonObject = (JSONObject) JSONValue.parse(result);
        }

        jsonObject = (JSONObject) jsonObject.get("d");
        jsonObject = (JSONObject) ((JSONArray) jsonObject.get("results")).get(0);
        jsonObject = (JSONObject) jsonObject.get("Thumbnail");
        Log.e(". ", jsonObject.toString() + " . ");
        String url = (String) jsonObject.get("MediaUrl");
        Log.e(". ", url + " . ");

        Bitmap bitmap = null;
        try {
          bitmap = downloadBitmap(url);
        } catch (IOException e) {
          e.printStackTrace();
        }
        return bitmap;
      }


      private Bitmap downloadBitmap(String url) throws IOException {
        HttpUriRequest request = new HttpGet(url.toString());
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);

        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode == 200) {
          HttpEntity entity = response.getEntity();
          byte[] bytes = EntityUtils.toByteArray(entity);

          Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                  bytes.length);
          return bitmap;
        } else {
          throw new IOException("Download failed, HTTP response code "
                  + statusCode + " - " + statusLine.getReasonPhrase());
        }
      }



      @Override
      protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        ImageView image = (ImageView) findViewById(R.id.imageview2);
        image.setImageBitmap(bitmap);
        selected_bitmap = bitmap;
        bingDialog.dismiss();
        bingDialog = null;
        keywordView.setText(keyword);
        keywordView.setTextColor(Color.WHITE);
        KmeansTask kmeansTask =new KmeansTask(phHueSDK, bitmap ,mode);
        kmeansTask.execute();
      }

      protected void onPostExecute(String result) {
        dialog.dismiss();
      }

      protected void onPreExecute() {
        super.onPreExecute();
        bingDialog = new ProgressDialog(ColorDetectWithKeywordActivity.this);
        bingDialog.setTitle("Bing search");
        bingDialog.setMessage(keyword + "를 탐색 중 입니다.");
        bingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        bingDialog.show();
      }
    };
  }
  private void searchKeyword() {
    cloudDialog = new ProgressDialog(ColorDetectWithKeywordActivity.this);
    cloudDialog.setTitle("Cloud Vision");
    cloudDialog.setMessage("사진 속 물체를 탐색 중 입니다.");
    cloudDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    cloudDialog.show();
  }

  private void callCloudVision(final Bitmap bitmap, final int mode) throws IOException {
    new AsyncTask<Object, Void, String>() {
      private String found;

      @Override
      protected String doInBackground(Object... params) {
        try {
          HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
          JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

          Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
          builder.setVisionRequestInitializer(new
              VisionRequestInitializer(CLOUD_VISION_API_KEY));
          Vision vision = builder.build();

          BatchAnnotateImagesRequest batchAnnotateImagesRequest =
              new BatchAnnotateImagesRequest();
          batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
              Feature labelDetection = new Feature();
              labelDetection.setType("LABEL_DETECTION");
              labelDetection.setMaxResults(10);
              add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
          }});

          Vision.Images.Annotate annotateRequest =
              vision.images().annotate(batchAnnotateImagesRequest);
          // Due to a bug: requests to Vision API containing large images fail when GZipped.
          annotateRequest.setDisableGZipContent(true);
          Log.d(TAG, "created Cloud Vision request object, sending request");

          BatchAnnotateImagesResponse response = annotateRequest.execute();
          found = convertResponseToString(response);
          return found;

        } catch (GoogleJsonResponseException e) {
          Log.d(TAG, "failed to make API request because " + e.getContent());
        } catch (IOException e) {
          Log.d(TAG, "failed to make API request because of other IOException " +
              e.getMessage());
        }
        return null;
      }

      protected void onPostExecute(String result) {
        super.onPostExecute(result);
        cloudDialog.dismiss();
        cloudDialog = null;
        try {
          AsyncTask<Void, Void, Bitmap> task = SearchPhotos(found, mode);
          task.execute();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      protected void onPreExecute() {
        super.onPreExecute();
        searchKeyword();
      }
    }.execute();
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

  private String convertResponseToString(BatchAnnotateImagesResponse response) {
    String message = "I found these things:\n\n";
    String result = null;
    List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
    Log.d("ColorDetectWithKeywordActivity", Integer.toString(labels.size()));
    if (labels != null) {
      result = labels.get(0).getDescription();
    } else {
      message += "nothing";
    }
    Log.d("ColorDetectWithKeywordActivity", message);
    return result;
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


