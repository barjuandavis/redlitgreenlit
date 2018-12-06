package com.google.location.nearby.apps.redlitgreenlit;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Player extends AppCompatActivity {
    private String playerName;

    private boolean findingRoom;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        playerName = getIntent().getStringExtra("guard_name");


    }

}