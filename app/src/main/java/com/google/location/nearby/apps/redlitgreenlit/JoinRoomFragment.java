package com.google.location.nearby.apps.redlitgreenlit;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class JoinRoomFragment extends Fragment {

    private Button findRoom;
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.join_room, container, false);
        findRoom = v.findViewById(R.id.find_room);
        mainActivity = (MainActivity)getActivity();
        findRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.findRoom();
            }
        });


        return v;
    }
}