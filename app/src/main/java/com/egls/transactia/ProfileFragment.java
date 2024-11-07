
package com.egls.transactia;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find the ImageView by ID
        ImageView accsets = view.findViewById(R.id.accsets);

        // Set a click listener on accsets ImageView
        accsets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Account_Settings activity with transition
                Intent intent = new Intent(getActivity(), Account_Settings.class);
                startActivity(intent);
                // Apply the slide-in and slide-out transitions
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        return view;
    }
}