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

        // Find ImageViews by their IDs
        ImageView exchange = view.findViewById(R.id.exchange);
        ImageView mytransact = view.findViewById(R.id.mytransact);
        ImageView notifs = view.findViewById(R.id.notifs);

        // Find TextViews by their IDs
        needsButton = view.findViewById(R.id.needsbt2);
        offersButton = view.findViewById(R.id.offersbt2);

        // Set onClick listeners for each ImageView to replace fragments in fragmentContainerView
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with ExchangeFragment
                replaceFragment(new ExchangeFragment(), R.id.fragmentContainerView);
            }
        });

        notifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with NotificationsFragment
                replaceFragment(new NotificationsFragment(), R.id.fragmentContainerView);
            }
        });

        mytransact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with MyTransactFragment
                replaceFragment(new MyTransactFragment(), R.id.fragmentContainerView);
            }
        });

        // Set onClick listeners for the TextViews (needsbt2 and offersbt2) to change backgrounds and replace fragments
        needsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change background for needsButton and reset offersButton
                needsButton.setBackgroundResource(R.drawable.greenbt);
                needsButton.setText("MY NEEDS");
                needsButton.setTextColor(getResources().getColor(R.color.white));
                offersButton.setBackgroundResource(R.drawable.button);
                offersButton.setText("MY OFFERS");
                offersButton.setTextColor(Color.parseColor("#33443C"));

                // Replace the container with NeedsFragment
                replaceFragment(new NeedsFragment(), R.id.thisshow);
            }
        });

        offersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change background for offersButton and reset needsButton
                offersButton.setBackgroundResource(R.drawable.greenbt);
                offersButton.setText("MY OFFERS");
                offersButton.setTextColor(getResources().getColor(R.color.white));
                needsButton.setBackgroundResource(R.drawable.button);
                needsButton.setText("MY NEEDS");
                needsButton.setTextColor(Color.parseColor("#33443C"));
                // Replace the container with OffersFragment
                replaceFragment(new OffersFragment(), R.id.thisshow);
            }
        });

        return view;
    }

    // Helper method to replace fragments
    private void replaceFragment(Fragment fragment, int containerId) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment); // Use the specified container ID
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}