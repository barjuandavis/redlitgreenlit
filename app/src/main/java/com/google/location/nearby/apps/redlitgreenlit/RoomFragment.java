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

public class RoomFragment extends AppCompatActivity {
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



}
