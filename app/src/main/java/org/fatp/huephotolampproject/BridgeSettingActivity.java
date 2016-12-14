package org.fatp.huephotolampproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import java.util.List;

public class BridgeSettingActivity extends Activity implements OnItemClickListener {
  private HueManager hueManager;
  public static final String TAG = "QuickStart";
  private HueSharedPreferences prefs;
  private AccessPointListAdapter adapter;
  private boolean lastSearchWasIPScan = false;
  Dialog dialog = null;

  // Local SDK Listener
  private PHSDKListener listener = new PHSDKListener() {
    @Override
    public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
      Log.w(TAG, "Access Points Found. " + accessPoint.size());

      PHWizardAlertDialog.getInstance().closeProgressDialog();
      if (accessPoint != null && accessPoint.size() > 0) {
        hueManager.getPhHueSDK().getAccessPointsFound().clear();
        hueManager.getPhHueSDK().getAccessPointsFound().addAll(accessPoint);
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            adapter.updateData(hueManager.getPhHueSDK().getAccessPointsFound());
          }
        });
      }
    }

    @Override
    public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {
      Log.w(TAG, "On CacheUpdated");
    }

    @Override
    public void onBridgeConnected(PHBridge b, String username) {
      hueManager.getPhHueSDK().setSelectedBridge(b);
      hueManager.getPhHueSDK().enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
      hueManager.getPhHueSDK().getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
      prefs.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
      prefs.setUsername(username);
      PHWizardAlertDialog.getInstance().closeProgressDialog();
      finish();
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint accessPoint) {
      Log.w(TAG, "Authentication Required.");
      hueManager.getPhHueSDK().startPushlinkAuthentication(accessPoint);
      startActivity(new Intent(BridgeSettingActivity.this, PHPushlinkActivity.class));
      finish();
    }

    @Override
    public void onConnectionResumed(PHBridge bridge) {
      if (BridgeSettingActivity.this.isFinishing()) {
        return;
      }
      Log.v(TAG, "onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
      hueManager.getPhHueSDK().getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
      for (int i = 0; i < hueManager.getPhHueSDK().getDisconnectedAccessPoint().size(); i++) {

        if (hueManager.getPhHueSDK().getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
          hueManager.getPhHueSDK().getDisconnectedAccessPoint().remove(i);
        }
      }
    }

    @Override
    public void onConnectionLost(PHAccessPoint accessPoint) {
      Log.v(TAG, "onConnectionLost : " + accessPoint.getIpAddress());
      if (!hueManager.getPhHueSDK().getDisconnectedAccessPoint().contains(accessPoint)) {
        hueManager.getPhHueSDK().getDisconnectedAccessPoint().add(accessPoint);
      }
    }

    @Override
    public void onError(int code, final String message) {
      Log.e(TAG, "on Error Called : " + code + ":" + message);

      if (code == PHHueError.NO_CONNECTION) {
        Log.w(TAG, "On No Connection");
      }
      else if (code == PHHueError.AUTHENTICATION_FAILED || code== PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
        PHWizardAlertDialog.getInstance().closeProgressDialog();
      }
      else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
        Log.w(TAG, "Bridge Not Responding . . . ");
        PHWizardAlertDialog.getInstance().closeProgressDialog();
        BridgeSettingActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            PHWizardAlertDialog.showErrorDialog(BridgeSettingActivity.this, message, R.string.btn_ok);
          }
        });
      }
      else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
        if (!lastSearchWasIPScan) {  // Perform an IP Scan (backup mechanism) if UPNP and Portal Search fails.
          hueManager.setPhHueSDK( PHHueSDK.getInstance());
          PHBridgeSearchManager sm = (PHBridgeSearchManager) hueManager.getPhHueSDK().getSDKService(PHHueSDK.SEARCH_BRIDGE);
          sm.search(false, false, true);
          lastSearchWasIPScan=true;
        }
        else {
          PHWizardAlertDialog.getInstance().closeProgressDialog();
          BridgeSettingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              PHWizardAlertDialog.showErrorDialog(BridgeSettingActivity.this, message, R.string.btn_ok);
            }
          });
        }
      }
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
      for (PHHueParsingError parsingError: parsingErrorsList) {
        Log.e(TAG, "ParsingError : " + parsingError.getMessage());
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bridge_setting);

    // Try to automatically connect to the last known bridge.  For first time use this will be empty so a bridge search is automatically started.
    prefs = HueSharedPreferences.getInstance(getApplicationContext());
    hueManager = hueManager.create();
    dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_manual_setting);
    dialog.setTitle("UserSetting");
    ((Button)dialog.findViewById(R.id.connectbutton)).setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            // IP랑 아이디를 저장하는 코드
            prefs.setUsername(((EditText) dialog.findViewById(R.id.user_name_editText)).getText().toString());
            prefs.setLastConnectedIPAddress( ((EditText) dialog.findViewById(R.id.ip_editText)).getText().toString() );

            if (prefs.getUsername() != null && !prefs.getLastConnectedIPAddress().equals("")) {
                PHAccessPoint lastAccessPoint = new PHAccessPoint();
                lastAccessPoint.setIpAddress(prefs.getLastConnectedIPAddress());
                lastAccessPoint.setUsername(prefs.getUsername());

                if (!hueManager.getPhHueSDK().isAccessPointConnected(lastAccessPoint)) {
                    PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, BridgeSettingActivity.this);
                  hueManager.getPhHueSDK().connect(lastAccessPoint);
                }
            }
            dialog.dismiss();
        }
    });

    Button enterManualSetting = (Button)findViewById(R.id.enter_manual_setting);
    enterManualSetting.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        dialog.show();
      }
    });

    hueManager.getPhHueSDK().getNotificationManager().registerSDKListener(listener);  // Register the PHSDKListener to receive callbacks from the bridge.
    adapter = new AccessPointListAdapter(getApplicationContext(), hueManager.getPhHueSDK().getAccessPointsFound());

    ListView accessPointList = (ListView) findViewById(R.id.bridge_list);
    accessPointList.setOnItemClickListener(this);
    accessPointList.setAdapter(adapter);

    if (isHistoryExist()) {
      PHAccessPoint lastAccessPoint = new PHAccessPoint();
      lastAccessPoint.setUsername(prefs.getUsername());
      lastAccessPoint.setIpAddress(prefs.getLastConnectedIPAddress());

      if (!hueManager.getPhHueSDK().isAccessPointConnected(lastAccessPoint)) {
        PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, BridgeSettingActivity.this);
        hueManager.getPhHueSDK().connect(lastAccessPoint);
      }
    } else {
      showBridgeList();
    }
  }

  private boolean isHistoryExist() {
    return prefs.getUsername() != null && !prefs.getLastConnectedIPAddress().equals("");
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.w(TAG, "Inflating home menu");
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.home, menu);
    return true;
  }

  /**
   * Called when option is selected.
   * @param item the MenuItem object.
   * @return boolean Return false to allow normal menu processing to proceed,  true to consume it here.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.find_new_bridge:
        showBridgeList();
        break;
    }
    return true;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (listener !=null) {
      hueManager.getPhHueSDK().getNotificationManager().unregisterSDKListener(listener);
    }
    hueManager.getPhHueSDK().disableAllHeartbeat();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    PHAccessPoint accessPoint = (PHAccessPoint) adapter.getItem(position);
    PHBridge connectedBridge = hueManager.getPhHueSDK().getSelectedBridge();

    if (connectedBridge != null) {
      String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
      if (connectedIP != null) {   // We are already connected here:-
        hueManager.getPhHueSDK().disableHeartbeat(connectedBridge);
        hueManager.getPhHueSDK().disconnect(connectedBridge);
      }
    }
    PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, BridgeSettingActivity.this);
    hueManager.getPhHueSDK().connect(accessPoint);
  }

  public void showBridgeList() {
    PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, BridgeSettingActivity.this);
    PHBridgeSearchManager sm = (PHBridgeSearchManager) hueManager.getPhHueSDK().getSDKService(PHHueSDK.SEARCH_BRIDGE);
    // Start the UPNP Searching of local bridges.
    sm.search(true, true);
  }
}