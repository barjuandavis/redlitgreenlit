package com.google.location.nearby.apps.redlitgreenlit;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.nearby.connection.Strategy;

/** Activity controlling the Rock Paper Scissors game */
public class MainActivity extends AppCompatActivity {
  /** The Tag */
  static final String TAG = "RockPaperScissors";
  /** The Tag */
  /** Permissions */
  private static final String[] REQUIRED_PERMISSIONS =
          new String[] {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,};
  private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
  /** Permissions */

  GameRoom gameRoom;
  static final Strategy STRATEGY = Strategy.P2P_STAR;


  // Our randomly generated name
  private Button gameRoomButton, joinRoomButton;
  private EditText name;


  // Callbacks for finding other devices (PUT THIS CALLBACK ON ROOMFINDER)
 /* private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
              Log.i(TAG, "onEndpointFound: endpoint found, connecting");
              connectionsClient.requestConnection(codeName, endpointId, connectionLifecycleCallback);
            }

            @Override
            public void onEndpointLost(String endpointId) {}
          };
          */


  @Override
  protected void onCreate(@Nullable Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.activity_main);
    gameRoomButton = findViewById(R.id.create_game_room);
    joinRoomButton = findViewById(R.id.join_room);
    name = findViewById(R.id.name);
    gameRoomButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createGameRoom();
      }
    });
    joinRoomButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        joinGameRoom();
      }
    });
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
    Intent i = new Intent(getApplicationContext(),GameRoom.class);
    checkNames(i);
  }

  public void joinGameRoom () {
    Intent i = new Intent(getApplicationContext(),Player.class);
    checkNames(i);
  }

  private void checkNames(Intent i) {
    if (name.getText().toString().length() == 0) {
      Toast.makeText(getApplicationContext(), "please enter your name",
              Toast.LENGTH_SHORT).show();
    }
    else {
      i.putExtra("guard_name",name.getText().toString());
      startActivity(i);
    }
  }


}
