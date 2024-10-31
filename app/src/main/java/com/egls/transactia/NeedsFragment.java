package com.egls.transactia;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class NeedsFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_needs, container, false);

        FloatingActionButton listButton = view.findViewById(R.id.List);

        // Set an OnClickListener for the floating button
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MyNeeds activity when the button is clicked
                Intent intent = new Intent(getActivity(), MyNeeds.class);
                startActivity(intent);
            }
        });
    return view;
    }
}