package com.google.location.nearby.apps.redlitgreenlit;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class JoinRoomFragment extends Fragment {

    private Button findRoom;
    private MainActivity mainActivity;
    private TextView roomName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.join_room, container, false);
        findRoom = v.findViewById(R.id.find_room);
        roomName = v.findViewById(R.id.roomName);
        mainActivity = (MainActivity)getActivity();
        findRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.findRoom();
                if (mainActivity.isFinding()) {
                    findRoom.setText(R.string.finding);
                } else {
                    findRoom.setText(R.string.start_finding);
                }
            }
        });
        return v;
    }

    public void setRoomName(String roomName) {
        String s = String.format(roomName, R.string.joined_to_x_room);
        this.roomName.setText("You're in: " + s);
    }
}