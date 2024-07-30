package com.example.theworld.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.theworld.R;
import com.example.theworld.databinding.FragmentGameBinding;
import com.example.theworld.flagfinder.FlagFinderActivity;

import com.example.theworld.mapMaster.MapMasterActivity;

public class GameFragment extends Fragment {

private FragmentGameBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        GameViewModel gameViewModel =
                new ViewModelProvider(this).get(GameViewModel.class);

    binding = FragmentGameBinding.inflate(inflater, container, false);
    View root = binding.getRoot();
   RelativeLayout flagFinderButton = root.findViewById(R.id.button_flag);
   flagFinderButton.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           Intent intent = new Intent(v.getContext(), FlagFinderActivity.class);
           v.getContext().startActivity(intent);
       }
   });

        RelativeLayout  mapMasterButton = root.findViewById(R.id.button_country);
        mapMasterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapMasterActivity.class);
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