package com.egls.transactia;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ExchangeFragment extends Fragment {
    private TextView needsButton;
    private TextView offersButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the correct layout for ExchangeFragment
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);

        // Find TextViews by their IDs
        needsButton = view.findViewById(R.id.needsbt2);
        offersButton = view.findViewById(R.id.offersbt2);

        // show the needs recyclerview by default
        if (getActivity() instanceof mainHome) {
            ((mainHome) getActivity()).onNeedsButtonClicked(); // Call the method in the activity
        }
        // Change background for needsButton and reset offersButton
        needsButton.setBackgroundResource(R.drawable.srv_gradient_background);
        needsButton.setTextColor(getResources().getColor(R.color.white));
        offersButton.setBackgroundResource(R.drawable.button);
        offersButton.setTextColor(Color.parseColor("#33443C"));

        // Set onClick listeners for the TextViews (needsbt2 and offersbt2) to change backgrounds and replace fragments
        needsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActivity() instanceof mainHome) {
                    ((mainHome) getActivity()).onNeedsButtonClicked(); // Call the method in the activity
                }

                // Change background for needsButton and reset offersButton
                needsButton.setBackgroundResource(R.drawable.srv_gradient_background);
                needsButton.setTextColor(getResources().getColor(R.color.white));
                offersButton.setBackgroundResource(R.drawable.button);
                offersButton.setTextColor(Color.parseColor("#33443C"));

            }
        });

        offersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActivity() instanceof mainHome) {
                    ((mainHome) getActivity()).onOffersButtonClicked(); // Call the method in the activity
                }
                // Change background for offersButton and reset needsButton
                offersButton.setBackgroundResource(R.drawable.srv_gradient_background);
                offersButton.setTextColor(getResources().getColor(R.color.white));
                needsButton.setBackgroundResource(R.drawable.button);
                needsButton.setTextColor(Color.parseColor("#33443C"));

            }
        });

        return view;
    }


}