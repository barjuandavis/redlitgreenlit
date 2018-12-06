package com.google.location.nearby.apps.redlitgreenlit;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.*;
import java.util.ArrayList;

public class GameRoom extends AppCompatActivity {
    //primitives
    private String roomName;
    private boolean searching;
    private final int MAX_PLAYERS = 5;

    //UI elements (especially dynamic ones)
    private LinearLayout playerListLayout;
    // Our handle to Nearby Connections
    private ConnectionsClient roomClient;
    private ArrayList<String> playerList;
    private Button broadcastButton;
    private Button[] playerButton;
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(MainActivity.TAG, "onConnectionInitiated: accepting connection");
                    roomClient.acceptConnection(endpointId, payloadCallback); //terima 2-2nya
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

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_gameroom);
        playerButton = new Button[5]; // 0 is unused
        roomName = getIntent().getStringExtra("guard_name");
        broadcastButton.findViewById(R.id.broadcast);
        playerListLayout.findViewById(R.id.player_list);
        playerButton[0].findViewById(R.id.player1);
        playerButton[1].findViewById(R.id.player2);
        playerButton[2].findViewById(R.id.player3);
        playerButton[3].findViewById(R.id.player4);
        playerButton[4].findViewById(R.id.player5);
        this.searching = false;
        broadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastRoom();
            }
        });
        roomClient = Nearby.getConnectionsClient(this);
    }


    //update this method for every successfully connected OR disconnected player.

    private void broadcastRoom() {
        flipSearchSwitch();
        if (isSearching())  {
            broadcastButton.setText(R.string.searching);
            //PACKAGE NAME IS THE SERVICEID
            roomName = roomName.concat("\'s room");
            roomClient.startAdvertising(roomName, getPackageName(), connectionLifecycleCallback,
                new AdvertisingOptions.Builder().setStrategy(MainActivity.STRATEGY).build());
        } else {
            broadcastButton.setText(R.string.start_searching);
            roomClient.stopAdvertising();
        }
    }

    public boolean isSearching() {
        return searching;
    }
    public void flipSearchSwitch() {
        this.searching = !this.searching;
    }

    public void addPlayer(String playerId) {
        Resources res = getResources();
        if (!isFull()) {
            playerList.add(playerId);
            String s = String.format(res.getString(R.string.filled_slot),playerId);
            playerButton[playerList.indexOf(playerId)].setText(s);
        }
    }

    public boolean isFull() {
        return playerList.size() == MAX_PLAYERS;
    }

    public void removePlayer(String playerId) {
        playerButton[playerList.indexOf(playerId)].setText(R.string.empty_slot);
        playerList.remove(playerId);
    }
}
