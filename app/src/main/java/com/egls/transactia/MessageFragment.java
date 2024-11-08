package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends Fragment {
    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ConstraintLayout trytoinbox = view.findViewById(R.id.trytoinbox);

        trytoinbox.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Inbox.class);  // Request is your target activity
            startActivity(intent);
        });


return view;
    }
}