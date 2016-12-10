package org.fatp.huephotolampproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.philips.lighting.model.PHLight;
import java.util.List;

public class PowerManageActivity extends Activity {

  boolean toggle = false;
  private HueManager hueManager;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_power_manage);
    hueManager = hueManager.create();
    final ImageView lampView = (ImageView) findViewById(R.id.lampimage);
    Button btn = (Button) findViewById(R.id.powerbutton);
    List<PHLight> allLights = hueManager.getAllLights();
    for (PHLight light : allLights) {
      if(!hueManager.requestPowerState(light))
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
    super.onDestroy();
    hueManager.release();
  }

  private void turnOnLights(boolean isOn) {
    if(isOn)
    {
      hueManager.powerOn();
    }else {
      hueManager.powerOff();
    }
    Toast.makeText(getApplicationContext(),"전원 변경에 성공하였습니다.", Toast.LENGTH_SHORT).show();
  }
}
