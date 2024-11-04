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

public class MyTransactFragment extends Fragment {
    private TextView completedButton;
    private TextView pendingButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_transact, container, false);

        // Find ImageViews by their IDs
        ImageView exchange = view.findViewById(R.id.exchange);
        ImageView mytransact = view.findViewById(R.id.mytransact);
        ImageView notifs = view.findViewById(R.id.notifs);

        completedButton = view.findViewById(R.id.completedbt);
        pendingButton = view.findViewById(R.id.pendingbt);
        // Set onClick listeners for each ImageView
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
                // Replace the current fragment with MyTransactionsFragment
                replaceFragment(new ExchangeFragment(), R.id.fragmentContainerView);
            }
        });
        mytransact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with MyTransactionsFragment
                replaceFragment(new ExchangeFragment(), R.id.fragmentContainerView);
            }
        });
        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change background for needsButton and reset offersButton
                completedButton.setBackgroundResource(R.drawable.greenbt);
                completedButton.setText("COMPLETED");
                completedButton.setTextColor(getResources().getColor(R.color.white));
                pendingButton.setBackgroundResource(R.drawable.button);
                pendingButton.setText("PENDING");
                pendingButton.setTextColor(Color.parseColor("#33443C"));

                // Replace the container with NeedsFragment

            }
        });

        pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change background for offersButton and reset needsButton
                pendingButton.setBackgroundResource(R.drawable.greenbt);
                pendingButton.setText("PENDING");
                pendingButton.setTextColor(getResources().getColor(R.color.white));
                completedButton.setBackgroundResource(R.drawable.button);
                completedButton.setText("COMPLETED");
                completedButton.setTextColor(Color.parseColor("#33443C"));
                // Replace the container with OffersFragment

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
