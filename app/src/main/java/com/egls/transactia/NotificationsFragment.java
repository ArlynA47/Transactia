package com.egls.transactia;

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

public class NotificationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the correct layout for NotificationsFragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Find ImageViews by their IDs
        ImageView exchange = view.findViewById(R.id.exchange);
        ImageView mytransact = view.findViewById(R.id.mytransact);
        ImageView notifs = view.findViewById(R.id.notifs);

        // Set onClick listeners for each ImageView
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with ExchangeFragment
                replaceFragment(new ExchangeFragment());
            }
        });
        notifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with MyTransactionsFragment
                replaceFragment(new NotificationsFragment());
            }
        });

        mytransact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with MyTransactionsFragment
                replaceFragment(new MyTransactFragment());
            }
        });



        return view;
    }

    // Helper method to replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment); // Replace with your fragment container ID
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
