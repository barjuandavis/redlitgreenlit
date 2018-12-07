package com.google.location.nearby.apps.redlitgreenlit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.android.gms.tasks.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Activity controlling the Rock Paper Scissors game
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "RedLitGreenLit";
    private static final String CLASSTAG = "MainActivity";
    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    private static final int MAX_PLAYERS = 5;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,};
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    static final int ROLE_GUARD = 147;
    static final int ROLE_PLAYER = 123;
    static final int MINTA_JARAK = 573;
    static int ROLE;
    long lastUpdate;
    Commands currentLight;
    boolean gerak;
    private SensorManager sensorManager;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float [] values = event.values;
            float x = values[0];
            float y = values[0];
            float z = values[0];
            float acVect = (x*x + y*y + z*z) / (SensorManager.GRAVITY_EARTH*SensorManager.GRAVITY_EARTH);
            long actualTime = System.currentTimeMillis();
            if (acVect >= 2) {
                if (actualTime - lastUpdate < 200) return;
                Log.d(CLASSTAG,"Checking light = " + currentLight.toString());
                if (currentLight.equals(Commands.RED_LIGHT)) {
                    gerak = true;

                }
                Log.d(CLASSTAG,"Checking gerak = " + gerak);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public enum Commands {
        RED_LIGHT,
        GREEN_LIGHT,
        PLAYER_MOVED,
        KICK_PLAYER,
        PLAYER_WINS
    };



    private String playerName;
    private boolean searching;
    private boolean finding;
    private ArrayList<String> playerList;
    private HashMap<String,String> playerListx;


    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo endpointInfo) {
            //oooo u got an endpoint!
            Log.v(CLASSTAG,endpointInfo.getEndpointName() + ": onEndpointFound! Making Connection request...");
            CharSequence c = "Connecting to " + endpointInfo.getEndpointName();
            Toast.makeText(getApplicationContext(),c, Toast.LENGTH_SHORT).show();
            roomConnectionClient.requestConnection(playerName,endpointId,slaveCallback);
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            Log.v(CLASSTAG,"Connection to Room " + endpointId + " was lost :(");
            CharSequence c = "Connection to Room " + endpointId + " was lost :(";
            Toast.makeText(getApplicationContext(),c, Toast.LENGTH_SHORT).show();
        }
    };

    private final PayloadCallback slavePayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull final String roomId, @NonNull Payload payload) {
            Commands c = Commands.valueOf(new String(payload.asBytes(), UTF_8));
            Log.v(CLASSTAG,c.toString() + " Received!");
            if (c.equals(Commands.RED_LIGHT) || c.equals(Commands.GREEN_LIGHT)) currentLight = c;
            joinRoomFragment.commandResponse(c);
            if (c.equals(Commands.RED_LIGHT)) {
                Log.d(CLASSTAG,"RED_LIGHT Command Received");
                lastUpdate = System.currentTimeMillis();
                if (gerak) slaveConnectionClient.sendPayload(roomId,Payload.fromBytes(Commands.PLAYER_MOVED.name().getBytes()));
            } else if (c.equals(Commands.PLAYER_WINS) || c.equals(Commands.KICK_PLAYER)) {
                //connection cleanup
                 slaveConnectionClient.disconnectFromEndpoint(roomId);
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };
    private final PayloadCallback roomPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String playerId, Payload payload) {
            Commands c = Commands.valueOf(new String(payload.asBytes(), UTF_8));
            if (c.equals(Commands.PLAYER_MOVED)) {
                kickPlayer(playerId);
            }
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                 //FIGURE OUT WHAT PAYLOAD IT IS
                //ini kalo payload sukses disini (UNTUK GUARD)
            }
        }
    };
    private ConnectionsClient roomConnectionClient;
    private ConnectionsClient slaveConnectionClient;
    private final ConnectionLifecycleCallback roomCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            Log.v(CLASSTAG,"Connection Initiated with " + endpointId);
            roomConnectionClient.acceptConnection(endpointId, roomPayloadCallback);
            CharSequence c = "Hello, " + connectionInfo.getEndpointName() + "!";
            Toast.makeText(getApplicationContext(),c, Toast.LENGTH_SHORT).show();
            addPlayer(endpointId, connectionInfo.getEndpointName());
        }
        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution result) {
            if (result.getStatus().isSuccess()) {
                Log.v(CLASSTAG,"player " + endpointId + " added!");

            }
        }

        @Override
        public void onDisconnected(String endpointId) { removePlayer(endpointId);}};
    private final ConnectionLifecycleCallback slaveCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String roomId, @NonNull ConnectionInfo roomInfo) {
            //slave berashil connect ke Master
            Log.v(CLASSTAG,"Initiated Connection with room : " + roomInfo.getEndpointName());
            slaveConnectionClient.acceptConnection(roomId, slavePayloadCallback);
            joinRoomFragment.setRoomName(roomInfo.getEndpointName());
        }

        @Override
        public void onConnectionResult(@NonNull String roomId, @NonNull ConnectionResolution res) {
            if (res.getStatus().isSuccess()) {
                Log.v(CLASSTAG,"Connection to the room success!");
            }
        }

        @Override
        public void onDisconnected(@NonNull String roomId) {

        }
    };
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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        playerName = getIntent().getStringExtra("playerName");
        searching = false;
        finding = false;
        gerak = false;
        currentLight = Commands.GREEN_LIGHT;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        lobbyFragment = new LobbyFragment();
        roomFragment = new RoomFragment();
        joinRoomFragment = new JoinRoomFragment();
        playerList = new ArrayList<String>();
        playerListx = new HashMap<>();
        fragmentTransaction.add(R.id.fragment_c,lobbyFragment).commit();
        roomConnectionClient = Nearby.getConnectionsClient(this);
        slaveConnectionClient = Nearby.getConnectionsClient(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {

        super.onPause();
        sensorManager.unregisterListener(this);
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
        ROLE = ROLE_GUARD;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_c, roomFragment).commit();
    }
    public void joinRoom() {
        ROLE = ROLE_PLAYER;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_c, joinRoomFragment).commit();
    }
    public void broadcastRoom() {
        flipSearchSwitch();
        if (isSearching()) {
            //PACKAGE NAME IS THE SERVICEID
            String s = playerName.concat(" - Room");
            Log.v(CLASSTAG,"Room " + s + " is Broadcasting!");
            roomConnectionClient.startAdvertising(s, getPackageName(), roomCallback,
                    new AdvertisingOptions.Builder().setStrategy(MainActivity.STRATEGY).build());
        } else {
            Log.v(CLASSTAG,"Room broadcasting stopped");
            roomConnectionClient.stopAdvertising();
        }
    }
    public void findRoom() {
        flipFindingSwitch();
        if (isFinding()) {
            Log.v(CLASSTAG,"Finding a room with serviceId: " + getPackageName());
            slaveConnectionClient.startDiscovery(getPackageName(), endpointDiscoveryCallback,
                new DiscoveryOptions.Builder().setStrategy(MainActivity.STRATEGY).build());
        }
        else {
            Log.v(CLASSTAG,"Room finding stopped.");
            slaveConnectionClient.stopDiscovery();
        }
    }

    //PUNYA ROOM
    public boolean isSearching() {return searching;}
    public void flipSearchSwitch() {this.searching = !this.searching;}
    public void addPlayer(String playerId, String playerName) {if (!isFull()) {playerList.add(playerId); roomFragment.setPlayerSlot(playerList.indexOf(playerId),playerName); playerListx.put(playerId,playerName);}}
    public boolean isFull() {return playerList.size() == MAX_PLAYERS;}
    public void removePlayer(String playerId) {roomFragment.clearPlayerSlot(playerList.indexOf(playerId));playerList.remove(playerId); playerListx.remove(playerId);}
    public void kickPlayer(String playerId) {
        sendSpecificCommand(Commands.KICK_PLAYER,playerId);
        roomConnectionClient.disconnectFromEndpoint(playerId); removePlayer(playerId);
        Log.d(CLASSTAG,playerId + "has been kicked");
    }
    public ArrayList<String> getPlayerList() {return playerList;}
    public String getPlayerName(String playerId) {return playerListx.get(playerId);}
    public void sendCommand(Commands c) {
        Log.v(CLASSTAG,c.toString());
        if (c.equals(Commands.RED_LIGHT) || c.equals(Commands.GREEN_LIGHT)) currentLight = c;
        Log.d(CLASSTAG,"currentLight = " + currentLight.toString());
        for (String slaveId : getPlayerList()) {
            roomConnectionClient.sendPayload(
                    slaveId, Payload.fromBytes(c.name().getBytes(UTF_8)));
        }
    }
    public void sendSpecificCommand(Commands c, int id) {
        String playerId = getPlayerList().get(id);
        sendSpecificCommand(c,playerId);
    }

    public void sendSpecificCommand(Commands c, String playerId) {
        roomConnectionClient.sendPayload(
                playerId, Payload.fromBytes(c.name().getBytes(UTF_8)));
        Log.e("Light >>> ", currentLight.toString());
    }




    //PUNYA JOIN ROOM
    public boolean isFinding() {return finding;}
    public void flipFindingSwitch() {this.finding = !this.finding;}

}
