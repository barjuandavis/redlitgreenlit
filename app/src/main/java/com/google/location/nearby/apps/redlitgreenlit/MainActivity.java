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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.nearby.Nearby;
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
    private boolean finding;
    private ArrayList<String> playerList;

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo endpointInfo) {
            //oooo u got an endpoint!
            CharSequence c = "Connecting to " + endpointId;
            Toast.makeText(getApplicationContext(),c, Toast.LENGTH_SHORT).show();
            roomConnectionClient.requestConnection(playerName,endpointId,roomCallback);
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            CharSequence c = "Connection to Room " + endpointId + " was lost :(";
            Toast.makeText(getApplicationContext(),c, Toast.LENGTH_SHORT).show();
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
    private ConnectionsClient roomConnectionClient;
    private ConnectionsClient playerConnectionClient;
    private final ConnectionLifecycleCallback roomCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            roomConnectionClient.acceptConnection(endpointId, payloadCallback);
            CharSequence c = "Hello, " + connectionInfo.getEndpointName() + "!";
            Toast.makeText(getApplicationContext(),c, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution result) {
            if (result.getStatus().isSuccess()) {
                addPlayer(endpointId);
            }
        }

        @Override
        public void onDisconnected(String endpointId) { removePlayer(endpointId);}};

    //fragments
    RoomFragment roomFragment;
    JoinRoomFragment joinRoomFragment;
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
        playerName = getIntent().getStringExtra("playerName");
        searching = false;
        finding = false;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        lobbyFragment = new LobbyFragment();
        roomFragment = new RoomFragment();
        joinRoomFragment = new JoinRoomFragment();
        fragmentTransaction.add(R.id.fragment_c,lobbyFragment).commit();
        roomConnectionClient = Nearby.getConnectionsClient(this);
        playerConnectionClient = Nearby.getConnectionsClient(this);
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
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_c, roomFragment).commit();
    }
    public void joinRoom() {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_c, roomFragment).commit();
    }
    public void broadcastRoom() {
        flipSearchSwitch();
        if (isSearching()) {
            //PACKAGE NAME IS THE SERVICEID
            String s = playerName.concat(" - Room");
            roomConnectionClient.startAdvertising(s, getPackageName(), roomCallback,
                    new AdvertisingOptions.Builder().setStrategy(MainActivity.STRATEGY).build());
        } else {
            roomConnectionClient.stopAdvertising();
        }
    }
    public void findRoom() {
        flipSearchSwitch();
        if (isFinding()) playerConnectionClient.startDiscovery(playerName, endpointDiscoveryCallback,
                new DiscoveryOptions.Builder().setStrategy(MainActivity.STRATEGY).build());
        else playerConnectionClient.stopDiscovery();
    }


    public boolean isSearching() {return searching;}
    public boolean isFinding() {return finding;}


    public void flipFindingSwitch() {this.finding = !this.finding;}
    public void flipSearchSwitch() {this.searching = !this.searching;}
    public void addPlayer(String playerId) {if (!isFull()) {playerList.add(playerId); roomFragment.setPlayerSlot(playerList.indexOf(playerId),playerId);}}
    public boolean isFull() {return playerList.size() == MAX_PLAYERS;}
    public void removePlayer(String playerId) {roomFragment.clearPlayerSlot(playerList.indexOf(playerId));playerList.remove(playerId);}
    public void kickPlayer(String playerId) {
        roomConnectionClient.disconnectFromEndpoint(playerId); 
    }
    public ArrayList<String> getPlayerList() {return playerList;}

}
