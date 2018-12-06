package com.google.location.nearby.apps.redlitgreenlit;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.*;

public class Player extends AppCompatActivity {
    private String playerName;
    public boolean isFindingRoom() { return findingRoom;}

    public void flipFindingRoomSwitch() {
        this.findingRoom = !this.findingRoom;
    }

    private boolean findingRoom;
    private ConnectionsClient playerClient;
    private ConnectionLifecycleCallback playerClientCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
            playerClient.acceptConnection(s,payloadCallback);
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
            if (connectionResolution.getStatus() == Status.RESULT_SUCCESS) {
                //do something
                playerClient.stopDiscovery();
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            //tell the UI that ur disconnected
        }
    };
    private EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo endpointInfo) {
            //oooo u got an endpoint!
            playerClient.requestConnection(playerName,endpointId,playerClientCallback);
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            //bikin notice "terputus dari room"
        }
    };
    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {

        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };



    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        playerName = getIntent().getStringExtra("guard_name");
        findingRoom = false;
        playerClient = Nearby.getConnectionsClient(this);
    }

    private void findRoom() {
        flipFindingRoomSwitch();
        if (isFindingRoom()) {
            playerClient.startDiscovery(getPackageName(), endpointDiscoveryCallback, new DiscoveryOptions.Builder().setStrategy(MainActivity.STRATEGY).build());
        } else {
            playerClient.stopDiscovery();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
