package org.fatp.huephotolampproject;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ml.Rgb;

public class ColorChangeActivity extends Activity {
  private HueManager hueManager;
  private boolean isAreadyClicked = false;
  private final String TAG = "ColorChangeActivity";
  private static final int MAX_HUE = 65535;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_color_change);

    final ImageButton colorbtn01 = (ImageButton) findViewById(R.id.colorbtn01);
    final ImageButton colorbtn02 = (ImageButton) findViewById(R.id.colorbtn02);
    final ImageButton colorbtn03 = (ImageButton) findViewById(R.id.colorbtn03);
    final ImageButton colorbtn04 = (ImageButton) findViewById(R.id.colorbtn04);
    final ImageButton colorbtn05 = (ImageButton) findViewById(R.id.colorbtn05);
    final ImageButton colorbtn06 = (ImageButton) findViewById(R.id.colorbtn06);
    final ImageButton colorbtn07 = (ImageButton) findViewById(R.id.colorbtn07);
    final ImageButton colorbtn08 = (ImageButton) findViewById(R.id.colorbtn08);
    final ImageButton colorbtn09 = (ImageButton) findViewById(R.id.colorbtn09);
    final Button colorbtn10 = (Button) findViewById(R.id.colorbtn10);
    final ImageButton colorbtn11 = (ImageButton) findViewById(R.id.colorbtn11);
    final ImageButton colorbtn12 = (ImageButton) findViewById(R.id.colorbtn12);


    hueManager = hueManager.create();

    colorbtn01.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        colorbtn01.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn01);
        if (isAreadyClicked == false) {
          isAreadyClicked = true;

        } else {

          colorbtn02.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn08.setImageResource(0);
          colorbtn09.setImageResource(0);
          //    colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);
          colorbtn12.setImageResource(0);

        }


      }
    });
    colorbtn02.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn02.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn02);
        if (isAreadyClicked == false) {

          isAreadyClicked = true;

        } else {

          colorbtn01.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn08.setImageResource(0);
          colorbtn09.setImageResource(0);
          //  colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);
          colorbtn12.setImageResource(0);

        }
      }
    });

    colorbtn03.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn03.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn03);
        if (isAreadyClicked == false) {
          isAreadyClicked = true;
        } else {
          colorbtn01.setImageResource(0);
          colorbtn02.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn08.setImageResource(0);
          colorbtn09.setImageResource(0);
          //  colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);
          colorbtn12.setImageResource(0);
        }
      }
    });

    colorbtn04.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn04.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn04);
        if (isAreadyClicked == false) {

          isAreadyClicked = true;

        } else {

          colorbtn01.setImageResource(0);
          colorbtn02.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn08.setImageResource(0);
          colorbtn09.setImageResource(0);
          //    colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);
          colorbtn12.setImageResource(0);


        }
      }
    });

    colorbtn05.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn05.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn05);
        if (isAreadyClicked == false) {

          isAreadyClicked = true;

        } else {
          colorbtn01.setImageResource(0);
          colorbtn02.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn08.setImageResource(0);
          colorbtn09.setImageResource(0);
          //  colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);
          colorbtn12.setImageResource(0);

        }
      }
    });

    colorbtn06.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn06.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn06);
        if (isAreadyClicked == false) {
          isAreadyClicked = true;
        } else {
          colorbtn01.setImageResource(0);
          colorbtn02.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn08.setImageResource(0);
          colorbtn09.setImageResource(0);
          //  colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);
          colorbtn12.setImageResource(0);

        }
      }
    });
    colorbtn07.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn07.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn07);
        if (isAreadyClicked == false) {
          isAreadyClicked = true;
        } else {
          colorbtn01.setImageResource(0);
          colorbtn02.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn08.setImageResource(0);
          colorbtn09.setImageResource(0);
          // colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);
          colorbtn12.setImageResource(0);

        }
      }
    });
    colorbtn08.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn08.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn08);
        if (isAreadyClicked == false) {

          isAreadyClicked = true;

        } else {

          colorbtn01.setImageResource(0);
          colorbtn02.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn09.setImageResource(0);
          // colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);
          colorbtn12.setImageResource(0);


        }
      }
    });
    colorbtn09.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn09.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn09);

        if (isAreadyClicked == false) {

          isAreadyClicked = true;

        } else {

          colorbtn01.setImageResource(0);
          colorbtn02.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn08.setImageResource(0);
          //  colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);
          colorbtn12.setImageResource(0);


        }
      }
    });


    colorbtn10.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //  colorbtn10.setImageResource(R.drawable.ic_done_black_48dp);
        // changeColor(colorbtn10);

        isAreadyClicked = false;
        colorbtn01.setImageResource(0);
        colorbtn02.setImageResource(0);
        colorbtn03.setImageResource(0);
        colorbtn04.setImageResource(0);
        colorbtn05.setImageResource(0);
        colorbtn06.setImageResource(0);
        colorbtn07.setImageResource(0);
        colorbtn08.setImageResource(0);
        colorbtn09.setImageResource(0);
        colorbtn11.setImageResource(0);
        colorbtn12.setImageResource(0);

        int red = new Random().nextInt(256);
        int green = new Random().nextInt(256);
        int blue = new Random().nextInt(256);
        LinkedList<Rgb> rgbs = new LinkedList<Rgb>();
        Rgb rgb = new Rgb(red, green, blue);
        rgbs.add(rgb);
        hueManager.requestColorChange(rgbs);
        Toast.makeText(getApplicationContext(),"색깔 변경에 성공하였습니다.", Toast.LENGTH_SHORT).show();


      }
    });

    colorbtn11.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn11.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn11);
        if (isAreadyClicked == false) {
          isAreadyClicked = true;
        } else {
          colorbtn01.setImageResource(0);
          colorbtn02.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn08.setImageResource(0);
          colorbtn09.setImageResource(0);
          //   colorbtn10.setImageResource(0);
          colorbtn12.setImageResource(0);
        }
      }
    });

    colorbtn12.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorbtn12.setImageResource(R.drawable.ic_done_black_48dp);
        changeColor(colorbtn12);
        if (isAreadyClicked == false) {
          isAreadyClicked = true;
        } else {

          colorbtn01.setImageResource(0);
          colorbtn02.setImageResource(0);
          colorbtn03.setImageResource(0);
          colorbtn04.setImageResource(0);
          colorbtn05.setImageResource(0);
          colorbtn06.setImageResource(0);
          colorbtn07.setImageResource(0);
          colorbtn08.setImageResource(0);
          colorbtn09.setImageResource(0);
          //  colorbtn10.setImageResource(0);
          colorbtn11.setImageResource(0);


        }
      }
    });


  }

  PHLightListener listener = new PHLightListener() {

    @Override
    public void onSuccess() {
    }

    @Override
    public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
      Log.w(TAG, "Light has updated");
    }

    @Override
    public void onError(int arg0, String arg1) {
    }

    @Override
    public void onReceivingLightDetails(PHLight arg0) {
    }

    @Override
    public void onReceivingLights(List<PHBridgeResource> arg0) {
    }

    @Override
    public void onSearchComplete() {
    }
  };

  @Override
  protected void onDestroy() {
    super.onDestroy();
    hueManager.release();
  }

  private void changeColor(ImageButton colorbtn) {
    ColorDrawable buttonColor = (ColorDrawable) colorbtn.getBackground();

    int color = buttonColor.getColor();
    int red = (color >> 16) & 0xFF;
    int green = (color >> 8) & 0xFF;
    int blue = (color >> 0) & 0xFF;
    Rgb rgb = new Rgb(red, green, blue);
    LinkedList<Rgb> rgbs = new LinkedList<>();
    rgbs.add(rgb);
    hueManager.requestColorChange(rgbs);
    Toast.makeText(getApplicationContext(),"색깔 변경에 성공하였습니다.", Toast.LENGTH_SHORT).show();
  }
}
