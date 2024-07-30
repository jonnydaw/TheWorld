package com.example.theworld.ui.notifications;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.strictmode.FragmentStrictMode;
import androidx.lifecycle.ViewModelProvider;

import com.example.theworld.MainActivity;
import com.example.theworld.R;
import com.example.theworld.databinding.FragmentStatsBinding;
import com.example.theworld.flagfinder.FlagFinderActivity;

import java.util.HashMap;
import java.util.Map;

public class StatsFragment extends Fragment {

private FragmentStatsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        StatsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(StatsViewModel.class);

    binding = FragmentStatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RelativeLayout howButton = root.findViewById(R.id.button_how);
        howButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InfoActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        RelativeLayout statsButton = root.findViewById(R.id.button_stat);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StatsActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        return root;
    }


@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}