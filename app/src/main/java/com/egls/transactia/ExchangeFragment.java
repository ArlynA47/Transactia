package com.egls.transactia;

import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExchangeFragment extends Fragment {
    private TextView needsButton;
    private TextView offersButton;
    private FloatingActionButton listButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for ExchangeFragment
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);

        ImageView mytransact = view.findViewById(R.id.mytransact);
        ImageView notifs = view.findViewById(R.id.notifs);

        // Find TextViews by their IDs
        needsButton = view.findViewById(R.id.needsbt2);
        offersButton = view.findViewById(R.id.offersbt2);

        // Show the needs RecyclerView by default
        if (getActivity() instanceof mainHome) {
            ((mainHome) getActivity()).onNeedsButtonClicked(); // Call the method in the activity
        }

        // Change background for needsButton and reset offersButton
        needsButton.setBackgroundResource(R.drawable.gradientbt);
        needsButton.setTextColor(getResources().getColor(R.color.white));
        offersButton.setBackgroundResource(R.drawable.button);
        offersButton.setTextColor(Color.parseColor("#33443C"));

        // Set onClick listeners for needsButton and offersButton
        needsButton.setOnClickListener(v -> {
            if (getActivity() instanceof mainHome) {
                ((mainHome) getActivity()).onNeedsButtonClicked();
            }
            // Update button appearance
            needsButton.setBackgroundResource(R.drawable.gradientbt);
            needsButton.setTextColor(getResources().getColor(R.color.white));

            offersButton.setBackgroundResource(R.drawable.button);
            offersButton.setTextColor(Color.parseColor("#33443C"));
        });

        offersButton.setOnClickListener(v -> {
            if (getActivity() instanceof mainHome) {
                ((mainHome) getActivity()).onOffersButtonClicked();
            }
            // Update button appearance
            offersButton.setBackgroundResource(R.drawable.gradientbt);
            offersButton.setTextColor(getResources().getColor(R.color.white));
            needsButton.setBackgroundResource(R.drawable.button);
            needsButton.setTextColor(Color.parseColor("#33443C"));
        });

        // Set onClick listeners for mytransact and notifs ImageViews to navigate to respective fragments
        mytransact.setOnClickListener(v -> loadFragment(new MyTransactFragment())); // Replace with your MyTransactFragment class
        notifs.setOnClickListener(v -> loadFragment(new NotificationsFragment())); // Replace with your NotificationsFragment class

        return view;
    }

    // Method to load the specified fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment); // Use the correct container ID
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
