package com.google.location.nearby.apps.redlitgreenlit;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.nearby.connection.*;

/** Activity controlling the Rock Paper Scissors game */
public class MainActivity extends AppCompatActivity {
  private static final String[] REQUIRED_PERMISSIONS = new String[] {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,};
  private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;


  private ConnectionsClient connectionsClient;
  final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
              @Override
              public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                  Log.i(MainActivity.TAG, "onConnectionInitiated: accepting connection");
                  connectionsClient.acceptConnection(endpointId, payloadCallback); //terima 2-2nya
              }

              @Override
              public void onConnectionResult(String endpointId, ConnectionResolution result) {
                  if (result.getStatus().isSuccess()) {
                      addPlayer(endpointId);
                  }
              }
              @Override
              public void onDisconnected(String endpointId) {
                    removePlayer(endpointId);
              }
          };
  final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo endpointInfo) {
            //oooo u got an endpoint!
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            //bikin notice "terputus dari room"
        }
    };
  final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            //  opponentChoice = MainActivity.GameChoice.valueOf(new String(payload.asBytes(), UTF_8));
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                //finishRound();
                //ini kalo payload sukses disini (UNTUK GUARD)
            }
        }
    };
  RoomFragment roomFragment;
  static final Strategy STRATEGY = Strategy.P2P_STAR;




  @Override
  protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);


  }
  @Override
  protected void onStart() {
    super.onStart();

    if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
      requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
    }
  }
  @Override
  protected void onStop() {
    super.onStop();
  }
  /** Returns true if the app was granted all the permissions. Otherwise, returns false. */
  private static boolean hasPermissions(Context context, String... permissions) {
    for (String permission : permissions) {
      if (ContextCompat.checkSelfPermission(context, permission)
              != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }
  /** Handles user acceptance (or denial) of our permission request. */
  @CallSuper
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
  {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
      return;
    }

    for (int grantResult : grantResults) {
      if (grantResult == PackageManager.PERMISSION_DENIED) {
        Toast.makeText(this, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();
        finish();
        return;
      }
    }
    recreate();
  }
  /** Finds an opponent to play the game with using Nearby Connections. */
  public void createGameRoom () {

  }

  public void joinGameRoom () {

  }

  private void checkNames(Intent i) {

  }
}
