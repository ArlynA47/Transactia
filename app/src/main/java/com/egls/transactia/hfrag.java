package com.egls.transactia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class hfrag extends Fragment {

    ImageView exchange, mytransact, notifs;
    FloatingActionButton addBt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hfrag, container, false);

        // Find ImageViews by their IDs
        exchange = view.findViewById(R.id.exchange);
        mytransact = view.findViewById(R.id.mytransact);
        notifs = view.findViewById(R.id.notifs);

        addBt = getActivity().findViewById(R.id.List);

        // set the exchange tab by default
        defaultDisp();

        // Set onClick listeners for each ImageView
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with ExchangeFragment
                defaultDisp();
                addBt.setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.fragmentContainerHome).setVisibility(View.VISIBLE);
            }
        });

        mytransact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with MyTransactionsFragment
                replaceFragment(new MyTransactFragment());
                exchange.setImageResource(R.drawable.exchangee);
                mytransact.setImageResource(R.drawable.selecttrans);
                notifs.setImageResource(R.drawable.notif);
                addBt.setVisibility(View.GONE);
                getActivity().findViewById(R.id.fragmentContainerHome).setVisibility(View.VISIBLE);
            }
        });

        notifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with NotificationsFragment
                //replaceFragment(new NotificationsFragment());
                exchange.setImageResource(R.drawable.exchangee);
                mytransact.setImageResource(R.drawable.exchange);
                notifs.setImageResource(R.drawable.selectnotif);
                addBt.setVisibility(View.GONE);
                getActivity().findViewById(R.id.fragmentContainerHome).setVisibility(View.GONE);

            }
        });

        return view;
    }

    private void defaultDisp() {
        replaceFragment(new ExchangeFragment());
        exchange.setImageResource(R.drawable.selectexchaneg);
        mytransact.setImageResource(R.drawable.exchange);
        notifs.setImageResource(R.drawable.notif);
    }

    // Helper method to replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerHome, fragment); // Replace with your fragment container ID
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
