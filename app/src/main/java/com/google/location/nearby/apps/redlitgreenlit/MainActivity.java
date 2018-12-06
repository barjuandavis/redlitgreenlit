package com.google.location.nearby.apps.redlitgreenlit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.nearby.connection.*;

import java.util.ArrayList;

/**
 * Activity controlling the Rock Paper Scissors game
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RedLitGreenLit";
    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    private static final int MAX_PLAYERS = 5;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,};
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private String playerName;
    private boolean searching;
    private ArrayList<String> playerList;

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo endpointInfo) {
            //oooo u got an endpoint!
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            //bikin notice "terputus dari room"
        }
    };
    private final PayloadCallback payloadCallback = new PayloadCallback() {
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
    private ConnectionsClient connectionsClient;
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
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

    //fragments
    RoomFragment roomFragment;
    LobbyFragment lobbyFragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        playerName = getIntent().getStringExtra("name");
        searching = false;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        lobbyFragment = new LobbyFragment();
        fragmentTransaction.add(R.id.fragment_c,lobbyFragment).commit();
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

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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


    public void createRoom() {
        RoomFragment roomFragment = new RoomFragment();
        fragmentTransaction.replace(R.id.fragment_c, roomFragment).commit();
    }

    public void joinRoom() {
        JoinRoomFragment roomFragment = new JoinRoomFragment();
        fragmentTransaction.replace(R.id.fragment_c, roomFragment).commit();
    }


    public void broadcastRoom() {
        flipSearchSwitch();
        if (isSearching()) {
            //PACKAGE NAME IS THE SERVICEID
            String s = playerName.concat(" - Room");
            connectionsClient.startAdvertising(s, getPackageName(), connectionLifecycleCallback,
                    new AdvertisingOptions.Builder().setStrategy(MainActivity.STRATEGY).build());
        } else {
            connectionsClient.stopAdvertising();
        }
    }

    public ConnectionLifecycleCallback getConnectionLifecycleCallback() {
        return connectionLifecycleCallback;
    }
    public EndpointDiscoveryCallback getEndpointDiscoveryCallback() {
        return endpointDiscoveryCallback;
    }
    public PayloadCallback getPayloadCallback() {
        return payloadCallback;
    }
    public boolean isSearching() {return searching;}
    public void flipSearchSwitch() {this.searching = !this.searching;}
    public void addPlayer(String playerId) {if (!isFull()) {playerList.add(playerId);}}
    public boolean isFull() {return playerList.size() == MAX_PLAYERS;}
    public void removePlayer(String playerId) {playerList.remove(playerId);}

}
