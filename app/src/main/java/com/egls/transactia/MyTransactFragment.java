package com.egls.transactia;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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

        completedButton = view.findViewById(R.id.completedbt);
        pendingButton = view.findViewById(R.id.pendingbt);

        dispByDefault();

        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change background for needsButton and reset offersButton
                completedButton.setBackgroundResource(R.drawable.srv_gradient_background);
                completedButton.setTextColor(getResources().getColor(R.color.white));
                pendingButton.setBackgroundResource(R.drawable.button);
                pendingButton.setTextColor(Color.parseColor("#33443C"));

                if (getActivity() instanceof mainHome) {
                    ((mainHome) getActivity()).hideViewRequestSent(); // Call the method in the activity

                }

            }
        });

        pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispByDefault();
            }
        });
        return view;
    }

    private void dispByDefault() {
        // Change background for offersButton and reset needsButton
        pendingButton.setBackgroundResource(R.drawable.srv_gradient_background);
        pendingButton.setTextColor(getResources().getColor(R.color.white));
        completedButton.setBackgroundResource(R.drawable.button);
        completedButton.setTextColor(Color.parseColor("#33443C"));
        if (getActivity() instanceof mainHome) {
            ((mainHome) getActivity()).showViewRequestSent(); // Call the method in the activity
            ((mainHome) getActivity()).fetchTransactions();
        }

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
